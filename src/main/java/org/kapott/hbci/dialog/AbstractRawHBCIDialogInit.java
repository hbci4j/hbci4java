/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.dialog;

import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

import org.kapott.hbci.dialog.KnownTANProcess.Variant;
import org.kapott.hbci.manager.Feature;
import org.kapott.hbci.manager.HBCIDialog;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.TanMethod;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.tools.NumberUtil;

/**
 * Abstrakte Basis-Klasse fuer "rohe" HBCI-Init-Dialoge.
 */
public abstract class AbstractRawHBCIDialogInit extends AbstractRawHBCIDialog
{
    /**
     * ct.
     * @param template das Template.
     */
    AbstractRawHBCIDialogInit(KnownDialogTemplate template)
    {
        super(template);
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#applyData(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected void applyData(final DialogContext ctx)
    {
        super.applyData(ctx);
        
        final HBCIPassportInternal p = ctx.getPassport();
        final HBCIKernelImpl k = ctx.getKernel();

        k.rawSet("Idn.KIK.blz", p.getBLZ());
        k.rawSet("Idn.KIK.country", p.getCountry());
        k.rawSet("ProcPrep.BPD", p.getBPDVersion());
        k.rawSet("ProcPrep.UPD", p.getUPDVersion());
        k.rawSet("ProcPrep.lang",p.getLang());
        k.rawSet("ProcPrep.prodName",HBCIUtils.getParam("client.product.name",HBCIUtils.PRODUCT_ID));
        k.rawSet("ProcPrep.prodVersion",HBCIUtils.getParam("client.product.version","3"));
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#getSCARequest(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    public SCARequest getSCARequest(DialogContext ctx)
    {
        // Checken, ob es ein Dialog, in dem eine SCA gemacht werden soll
        if (!KnownDialogTemplate.LIST_SEND_SCA.contains(this.getTemplate()))
            return null;

        final HBCIPassportInternal _p = ctx.getPassport();
        if (!(_p instanceof AbstractPinTanPassport))
            return null;
        final AbstractPinTanPassport p = (AbstractPinTanPassport) _p;

        // HKTAN-Version und Prozessvariante ermitteln - kann NULL sein
        final int segversionDefault = 6;
        final Properties secmechInfo = p.getCurrentSecMechInfo();
        
        final int hktanVersion = secmechInfo != null ? NumberUtil.parseInt(secmechInfo.getProperty("segversion"),segversionDefault) : segversionDefault;
        
        // Erst ab HKTAN 6 noetig. Die Bank unterstuetzt es scheinbar noch nicht
        // Siehe B.4.3.1 - Wenn die Bank HITAN < 6 geschickt hat, dann kann sie keine SCA
        if (hktanVersion < 6)
            return null;

        final String segcode = this.getTanReference(ctx);
        if (Feature.PINTAN_FASTSETUP.isEnabled() && !ctx.isAnonymous())
        {
            // Wenn wir ein Einschritt-TAN-Verfahren haben und es die autorisierte Initialisierung ist,
            // dann senden wir das Init ohne HKTAN. Im anonymen Init haben wir ja schon per HKTAN mitgeteilt, dass
            // wir SCA koennen. Jetzt gehts uns nur darum, die TAN-Verfahren per 3920 zu kriegen
            // Ist nach Abstimmung mit einem HBCI-Server-Experten so legitim und wird von allen so gemacht:
            // Bei Dialog-Init Verfahren 999 nehmen und ohne HKTAN senden
            // Ausser wenn wir danach ein HKTAB senden. Dann brauchen wir das HKTAN - obwohl wir noch bei TAN-Verfahren 999 sind. Was fuer ein Bullshit
            //
            //  Dialog                                   HKTAN?
            //  -----------------------------------------------
            //  DialogInitAnon                           ja
            //  DialogInit mit Einschritt-TAN            nein
            //  DialogInit mit Einschritt-TAN für HKTAN  ja
            //  DialogInit mit Zweischritt-TAN           ja
            if (Objects.equals(TanMethod.ONESTEP.getId(),p.getCurrentTANMethod(false)) && !Objects.equals(segcode,"HKTAB"))
            {
                HBCIUtils.log("skipping HKTAN for dialog init, since we are using a one-step tan method",HBCIUtils.LOG_DEBUG);
                return null;
            }
        }

        final SCARequest r = new SCARequest();
        r.setVersion(hktanVersion);
        r.setVariant(Variant.determine(secmechInfo != null ? secmechInfo.getProperty("process") : null));
        r.setTanReference(segcode);

        return r;
    }
    
    /**
     * Liefert den Segmentnamen fuer das Bezugssegment.
     * @param ctx der Context.
     * @return der Segmentname.
     */
    protected String getTanReference(DialogContext ctx)
    {
        // Beim Bezug auf das Segment schicken wir per Default "HKIDN". Gemaess Kapitel B.4.3.1 muss das Bezugssegment aber
        // bei PIN/TAN-Management-Geschaeftsvorfaellen mit dem GV des jeweiligen Geschaeftsvorfalls belegt werden.
        // Daher muessen wir im Payload schauen, ob ein entsprechender Geschaeftsvorfall enthalten ist.
        // Wird muessen nur nach HKPAE, HKTAB schauen - das sind die einzigen beiden, die wir unterstuetzen
        
        String segcode = "HKIDN";
        HBCIDialog payload = ctx.getDialog();
        if (payload != null)
        {
            final HBCIMessageQueue queue = payload.getMessageQueue();
            for (String code:Arrays.asList("HKPAE","HKTAB")) // Das sind GVChangePIN und GVTANMediaList
            {
                if (queue.findTask(code) != null)
                {
                    segcode = code;
                    break;
                }
            }
        }
        
        return segcode;
    }
}
