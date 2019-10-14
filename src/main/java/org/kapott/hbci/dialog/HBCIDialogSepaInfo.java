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

import org.kapott.hbci.manager.HBCIUser;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.tools.StringUtil;

/**
 * Diese Klasse enthaelt den Dialog fuer den Abruf der SEPA-Informationen der Konten.
 */
public class HBCIDialogSepaInfo extends AbstractRawHBCIDialog
{
  private final static String GVNAME = "SEPAInfo";

    /**
     * ct.
     */
    public HBCIDialogSepaInfo()
    {
        super(KnownDialogTemplate.SEPAINFO);
    }
    
    /**
     * Prueft, ob der Dialog noetig ist.
     * @param ctx der Context.
     * @return true, wenn er noetig ist.
     */
    public boolean required(DialogContext ctx)
    {
      HBCIPassportInternal p = ctx.getPassport();
      final Properties upd = p.getUPD();
      if (upd == null)
        return true;
      
      return !upd.containsKey(HBCIUser.UPD_KEY_FETCH_SEPAINFO);
    }
      
    /**
     * Prueft, ob der Dialog moeglich ist.
     * @param ctx der Context.
     * @return true, wenn er moeglich ist.
     */
    public boolean supported(DialogContext ctx)
    {
      return getSegmentVersion(ctx,GVNAME,null) != null;
    }

    /**
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#checkResult(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected void checkResult(DialogContext ctx)
    {
      final HBCIPassportInternal p = ctx.getPassport();
      final Properties upd = p.getUPD();
      
      try
      {
        super.checkResult(ctx);

        final HBCIMsgStatus ret = ctx.getMsgStatus();
        if (!ret.isOK())
            return;
        
        final Properties result = ret.getData();
        if (result == null)
            return;

        // Wenn wir noch keine UPD haben, koennen wir uns die Auswertung schenken. Wir
        // wissen dann eh nicht, wo wir die Daten einsortieren sollen.
        if (upd == null)
        {
          HBCIUtils.log("suspect, got SEPAInfo result but have no UPD",HBCIUtils.LOG_INFO);
          return;
        }

        int count = 0;
        
        for (int i=0;i<500;i++)
        {
          final String header  = HBCIUtilsInternal.withCounter("SEPAInfoRes1.Acc", i);
          final String cansepa = result.getProperty(header + ".sepa");
          
          // Ende
          if (!StringUtil.hasText(cansepa))
            break;

          // Konto kann kein Sepa
          if (cansepa.equals("N"))
            continue;
          
          final String iban    = result.getProperty(header + ".iban");
          final String bic     = result.getProperty(header + ".bic");
          final String country = result.getProperty(header + ".KIK.country");
          final String blz     = result.getProperty(header + ".KIK.blz");
          final String number  = result.getProperty(header + ".number");

          // keine IBAN erhalten
          if (!StringUtil.hasText(iban))
            continue;

          HBCIUtils.log("found BIC/IBAN = " + bic + "/" + iban + " for account " + country + "/" + blz + "/" + number,HBCIUtils.LOG_DEBUG);
          
          // Konto in den UPD suchen und UPD-Informationen aktualisieren
          for (int j=0;j<500;j++)
          {
            final String h = HBCIUtilsInternal.withCounter("KInfo",j);
            final String n = upd.getProperty(h + ".KTV.number");
            final String c = upd.getProperty(h + ".KTV.KIK.country");
            final String b = upd.getProperty(h + ".KTV.KIK.blz");

            // Ende
            if (!StringUtil.hasText(n))
              break;

            // Land, BLZ und Konto stimmen ueberein - wir uebernehmen IBAN und BIC in das Konto
            if (Objects.equals(country,c) && Objects.equals(blz,b) && Objects.equals(number,n))
            {
              count++;

              HBCIUtils.log("updating BIC/IBAN = " + bic + "/" + iban + " for account " + country + "/" + blz + "/" + number,HBCIUtils.LOG_DEBUG);

              // uebernehmen wir nur, wenn wir eine haben
              if (StringUtil.hasText(iban))
              upd.setProperty(h + ".KTV.iban", iban);
              
              // uebernehmen wir nur, wenn wir eine haben
              if (StringUtil.hasText(bic))
                upd.setProperty(h + ".KTV.bic", bic);
              
              break;
            }
          }
        }
        final String name = (count == 1 ? "Konto" : "Konten");
        HBCIUtils.log("IBAN/BIC für " + count + " " + name + " empfangen", HBCIUtils.LOG_INFO);
      }
      finally
      {
        // Egal, wie das Abrufen der SEPA-Infos ausgegangen ist, wir vermerken es als erledigt,
        // damit es nicht immer wieder wiederholt wird.
        if (p != null && upd != null)
        {
          upd.setProperty(HBCIUser.UPD_KEY_FETCH_SEPAINFO,new Date().toString());
          p.saveChanges(); // Sicherstellen, dass die Aenderungen sofort gespeichert sind
        }
      }
    }
}
