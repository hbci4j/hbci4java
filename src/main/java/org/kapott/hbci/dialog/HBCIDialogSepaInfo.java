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
    /**
     * ct.
     */
    public HBCIDialogSepaInfo()
    {
        super(KnownDialogTemplate.SEPAINFO);
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
        final Properties upd = p.getUPD();
        
        // Wenn wir noch keine UPD haben, koennen wir uns die Auswertung schenken. Wir
        // wissen dann eh nicht, wo wir die Daten einsortieren sollen.
        if (upd == null)
        {
          HBCIUtils.log("suspect, got SEPAInfo result but have no UPD",HBCIUtils.LOG_INFO);
          return;
        }

        for (int i=0;i<500;i++)
        {
          final String header = HBCIUtilsInternal.withCounter("SEPAInfoRes1.Acc", i);
          String cansepa = result.getProperty(header + ".sepa");
          
          // Ende
          if (cansepa == null)
            break;
          
          if (cansepa.equals("N"))
            continue;
          
          final String iban = result.getProperty(header + ".iban");
          final String bic = result.getProperty(header + ".bic");
          
          // normale konto-informationen extrahieren, um dieses konto
          // in den upd suchen zu koennen
          final String country = result.getProperty(header+".KIK.country");
          final String blz = result.getProperty(header+".KIK.blz");
          final String number = result.getProperty(header+".number");
          
          HBCIUtils.log("found BIC/IBAN = " + bic + "/" + iban + " for account " + country + "/" + blz + "/" + number,HBCIUtils.LOG_DEBUG);
          
          // konto in den UPD suchen und UPD-Informationen aktualisieren
          for (int j=0;j<500;j++)
          {
            String h = HBCIUtilsInternal.withCounter("KInfo",j);
            String n = upd.getProperty(h+".KTV.number");
            
            // Ende
            if (n == null)
              break;
            
            String temp_country=upd.getProperty(h+".KTV.KIK.country");
            String temp_blz=upd.getProperty(h+".KTV.KIK.blz");
            
            if (temp_country.equals(country) && temp_blz.equals(blz) && n.equals(number))
            {
                if (StringUtil.hasText(iban))
                    upd.setProperty(h+".KTV.iban", iban);
                
                if (StringUtil.hasText(bic))
                    upd.setProperty(h+".KTV.bic", bic);
            }
          }
        }

        
        HBCIUtils.log("SEPA-Konto-Informationen empfangen", HBCIUtils.LOG_INFO);
        if (upd != null)
          p.setUPD(upd);
///        upd.setProperty(HBCIUser.UPD_KEY_TANMEDIA,names);
        upd.setProperty(HBCIUser.UPD_KEY_METAINFO,new Date().toString()); // Wir vermerken auch gleich noch, dass der Abruf damit schon erledigt ist
    }
}
