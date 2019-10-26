/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2019 Olaf Willuhn
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
  private HBCIDialogSepaInfo dialog = null;
  private boolean force = false;
  
  /**
   * ct.
   * @param force true, wenn der Abruf der TAN-Medien forciert werden soll, auch wenn er eigentlich nicht noetig ist.
   */
  public HBCIProcessSepaInfo(boolean force)
  {
    this.dialog = new HBCIDialogSepaInfo();
    this.force = force;
  }

  /**
   * @see org.kapott.hbci.dialog.HBCIProcess#execute(org.kapott.hbci.dialog.DialogContext)
   */
  @Override
  public HBCIMsgStatus execute(final DialogContext ctx)
  {
    if (!this.dialog.supported(ctx))
      return null;
    
    // Weder erzwungen noch noetig
    if (!this.force && !this.dialog.required(ctx))
      return null;

    try
    {
      HBCIUtils.log("trying to fetch SEPA infos",HBCIUtils.LOG_INFO);

      final HBCIDialogInit init = new HBCIDialogInit();
      init.execute(ctx);

      this.dialog.execute(ctx);
      
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


