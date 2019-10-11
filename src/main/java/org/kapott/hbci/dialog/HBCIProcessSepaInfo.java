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

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Kapselt die HBCI-Nachrichten zum Abruf der SEPA-Infos.
 */
public class HBCIProcessSepaInfo implements HBCIProcess
{
  private boolean force = false;
  
  /**
   * ct.
   * @param force true, wenn der Abruf der TAN-Medien forciert werden soll, auch wenn er eigentlich nicht noetig ist.
   */
  public HBCIProcessSepaInfo(boolean force)
  {
    this.force = force;
  }

  /**
   * @see org.kapott.hbci.dialog.HBCIProcess#execute(org.kapott.hbci.dialog.DialogContext)
   */
  @Override
  public HBCIMsgStatus execute(final DialogContext ctx)
  {
    if (!this.force && !HBCIDialogSepaInfo.required(ctx.getPassport()))
      return null;

    try
    {
      HBCIUtils.log("trying to fetch SEPA infos",HBCIUtils.LOG_INFO);

      final HBCIDialogInit init = new HBCIDialogInit();
      init.execute(ctx);

      final HBCIDialogSepaInfo sepaInfo = new HBCIDialogSepaInfo();
      sepaInfo.execute(ctx);
      final HBCIDialogEnd end = new HBCIDialogEnd();
      return end.execute(ctx);
    }
    catch (Exception e)
    {
      // Wir werfen das nicht hoch. Wenn es fehlschlaegt, dann haben wir halt keine SEPA-Infos. Davon geht die Welt nicht unter
      HBCIUtils.log("failed: " + e.getMessage(),HBCIUtils.LOG_INFO);
      HBCIUtils.log(e,HBCIUtils.LOG_DEBUG);
      
      return null;
    }
  }
}


