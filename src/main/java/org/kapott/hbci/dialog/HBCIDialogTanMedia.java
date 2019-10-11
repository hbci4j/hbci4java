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

import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUser;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.tools.StringUtil;

/**
 * Diese Klasse enthaelt den Dialog fuer den Abruf der TAN-Medien.
 */
public class HBCIDialogTanMedia extends AbstractRawHBCIDialog
{
    /**
     * ct.
     */
    public HBCIDialogTanMedia()
    {
        super(KnownDialogTemplate.TANMEDIA);
    }

    /**
     * Prueft, ob der Dialog noetig ist.
     * @param p der Passport.
     * @return true, wenn er noetig ist.
     */
    public static boolean required(HBCIPassportInternal p)
    {
        // Checken, ob er ueberhaupt moeglich ist
        if (!supported(p))
            return false;
        
        final Properties upd = p.getUPD();
        if (upd == null)
            return true;
        
        return !upd.containsKey(HBCIUser.UPD_KEY_FETCH_TANMEDIA);
    }
    
    /**
     * Prueft, ob der Dialog moeglich ist.
     * @param p der Passport.
     * @return true, wenn er moeglich ist.
     */
    public static boolean supported(HBCIPassportInternal p)
    {
        if (p == null || !(p instanceof AbstractPinTanPassport))
            return false;
        
        return getSegmentVersion(p,null) != null;
    }
    
    /**
     * Liefert die hoechste bei der Bank verfuegbare Segment-Version fuer das HKTAB.
     * @param p der Passport.
     * @param defaultVersion die Default-Version, wenn keine gefunden wurde.
     * @return die Segment-Version oder NULL, wenn keine brauchbare Version unterstuetzt wird
     */
    private static Integer getSegmentVersion(HBCIPassportInternal p,Integer defaultVersion)
    {
        final Properties props = p.getParamSegmentNames();
        final String version = props.getProperty("TANMediaList");
        if (!StringUtil.hasText(version))
            return defaultVersion;
        
        try
        {
            final Integer i = Integer.valueOf(version);
            if (i == null)
                return defaultVersion;
            
            if (i.intValue() < 2 || i.intValue() > 5)
                return defaultVersion;
            
            return i;
        }
        catch (Exception e)
        {
            HBCIUtils.log("invalid segment version: " + version,HBCIUtils.LOG_WARN);
            return defaultVersion;
        }
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractHBCIDialogInit#applyData(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected void applyData(DialogContext ctx)
    {
        super.applyData(ctx);

        final HBCIPassportInternal p = ctx.getPassport();
        final HBCIKernelImpl k = ctx.getKernel();
        
        final Integer version = getSegmentVersion(p,5);
        k.rawSet("TANMediaList" + version+ ".mediatype","0"); // Eigentlich wollen wir nur 1 (also nur die aktiven). Aber die SPK akzeptiert das nicht
        k.rawSet("TANMediaList" + version+ ".mediacategory","A"); // Wir wollen alle Medien-Arten
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#checkResult(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected void checkResult(DialogContext ctx)
    {
        super.checkResult(ctx);

        final HBCIMsgStatus ret = ctx.getMsgStatus();
        if (!ret.isOK())
            return;
        
        final Properties result = ret.getData();
        if (result == null)
            return;

        final HBCIPassportInternal p = ctx.getPassport();
        final Integer version = getSegmentVersion(p,5);

        final StringBuilder sb = new StringBuilder();
        for (int i=0;i<100;i++)
        {
            final String header = HBCIUtilsInternal.withCounter("TANMediaListRes" + version + ".MediaInfo",i);
            
            if (result.getProperty(header + ".mediacategory") == null)
                break;

            // Nur aktive
            final String status = result.getProperty(header + ".status");
            if (!Objects.equals(status,"1"))
                continue;

            final String name = result.getProperty(header + ".medianame");
            if (!StringUtil.hasText(name))
                continue;

            if (sb.length() != 0)
                sb.append("|");

            sb.append(name);
        }
        
        final String names = sb.toString();
        if (!StringUtil.hasText(names))
            return;
        
        HBCIUtils.log("TAN-Medienbezeichnungen empfangen: " + names, HBCIUtils.LOG_INFO);
        Properties upd = p.getUPD();
        if (upd == null)
        {
            // Fuer den Fall, dass wir das zu einem Zeitpunkt aufgerufen haben, wo wir noch gar keine UPD haben
            upd = new Properties();
            p.setUPD(upd);
        }
        upd.setProperty(HBCIUser.UPD_KEY_TANMEDIA,names);
        upd.setProperty(HBCIUser.UPD_KEY_FETCH_TANMEDIA,new Date().toString()); // Wir vermerken auch gleich noch, dass der Abruf damit schon erledigt ist
        p.saveChanges(); // Sicherstellen, dass die Aenderungen sofort gespeichert sind
    }
}
