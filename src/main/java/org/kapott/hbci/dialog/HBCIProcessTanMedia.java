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

import java.util.Objects;
import java.util.Properties;

import org.kapott.hbci.manager.Feature;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Kapselt die HBCI-Nachrichten zum Abruf der TAN-Medien.
 */
public class HBCIProcessTanMedia implements HBCIProcess
{
  /**
   * @see org.kapott.hbci.dialog.HBCIProcess#execute(org.kapott.hbci.dialog.DialogContext)
   */
  @Override
  public HBCIMsgStatus execute(final DialogContext ctx)
  {
    if (!HBCIDialogTanMedia.supported(ctx.getPassport()))
      return null;
    
    boolean skip = Feature.PINTAN_INIT_SKIPONESTEPSCA.isEnabled();
    // Wir versuchen es erstmal mit dem Einschritt-TAN-Verfahren und ohne SCA - wenn es per Feature erlaubt ist
    // Das unterstuetzen aber nicht alle Banken. Die Deutsche Bank z.Bsp. hier ein konkretes Zweischritt-Verfahren,
    // was voellig absurd ist. Denn fuer das Zweischritt-Verfahren brauche ich ja die Medienbezeichnung.
    // Und die will ich ja gerade erst abrufen.
    try
    {
      return this.execute(ctx,skip);
    }
    catch (Exception e)
    {
      HBCIUtils.log("failed: " + e.getMessage(),HBCIUtils.LOG_INFO);
      HBCIUtils.log(e,HBCIUtils.LOG_DEBUG);

      // Den erneuten Versuch mit SCA brauchen wir natuerlich machen, wenn wir ihn beim ersten weggelassen haben
      if (skip)
      {
        try
        {
          return this.execute(ctx,false);
        }
        catch (Exception e2)
        {
          HBCIUtils.log("fetching of TAN media names failed: " + e.getMessage(),HBCIUtils.LOG_INFO);
          HBCIUtils.log(e,HBCIUtils.LOG_DEBUG);
        }
      }
      
      return null;
    }
  }

  /**
   * Fuehrt die HBCI-Dialoge aus.
   * @param ctx der Context.
   * @param skipSCA true, wenn die SCA weggelassen werden soll.
   * @return der Status der Dialoge.
   */
  private HBCIMsgStatus execute(final DialogContext ctx, boolean skipSCA)
  {
    HBCIUtils.log("trying to fetch TAN media names [skip sca: " + skipSCA + "]",HBCIUtils.LOG_INFO);

    final HBCIDialogInit init = new HBCIDialogInit()
    {
      /**
       * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#createSCARequest(java.util.Properties, int)
       */
      @Override
      public SCARequest createSCARequest(Properties secmechInfo, int hktanVersion)
      {
        if (!skipSCA)
          return null;
        
        // Anpassen des SCA-Requests fuer das Abfragen der TAN-Medien per HKTAB
        SCARequest r = super.createSCARequest(secmechInfo, hktanVersion);
        r.setTanReference("HKTAB");
        final String needed = secmechInfo != null ? secmechInfo.getProperty("needtanmedia","") : "";
        r.setTanMedia(Objects.equals(needed,"2") ? "noref" : "");
        return r;
      }
    };
    init.execute(ctx);

    final HBCIDialogTanMedia tanMedia = new HBCIDialogTanMedia();
    tanMedia.execute(ctx);
    final HBCIDialogEnd end = new HBCIDialogEnd();
    return end.execute(ctx);
  }
}


