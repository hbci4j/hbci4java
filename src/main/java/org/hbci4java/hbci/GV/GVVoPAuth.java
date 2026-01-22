/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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

package org.hbci4java.hbci.GV;

import org.hbci4java.hbci.GV_Result.HBCIJobResultImpl;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.LogFilter;
import org.hbci4java.hbci.status.HBCIMsgStatus;

/**
 * Der Geschaeftsvorfall für den VoP-Freigabe.
 */
public class GVVoPAuth extends HBCIJobImpl
{
  private HBCIJobImpl task = null;
  
  /**
   * ct.
   * @param handler
   */
  public GVVoPAuth(HBCIHandler handler)
  {
    super(handler, getLowlevelName(), new HBCIJobResultImpl());
    
    addConstraint("vopid","vopid",null,LogFilter.FILTER_NONE);

  }

  /**
   * Liefert den Lowlevel-Namen.
   * @return der Lowlevel-Name.
   */
  public static String getLowlevelName()
  {
    return "VoPAuth";
  }
  
  /**
   * Speichert eine Referenz auf den eigentlichen Geschaeftsvorfall.
   * @param task
   */
  public void setTask(HBCIJobImpl task)
  {
      this.task = task;
  }
  
  /**
   * @see org.hbci4java.hbci.GV.HBCIJobImpl#setParam(java.lang.String, java.lang.String)
   */
  public void setParam(String paramName, String value)
  {
    if (paramName.equals("vopid"))
      value = "B" + value;
    super.setParam(paramName, value);
  }

  /**
   * @see org.hbci4java.hbci.GV.HBCIJobImpl#saveReturnValues(org.hbci4java.hbci.status.HBCIMsgStatus, int)
   */
  protected void saveReturnValues(HBCIMsgStatus status, int sref)
  {
      super.saveReturnValues(status, sref);
      
      if (this.task != null)
      {
          int orig_segnum=Integer.parseInt(task.getJobResult().getSegNum());
          HBCIUtils.log("storing return values in orig task (segnum="+orig_segnum+")", HBCIUtils.LOG_DEBUG);
          task.saveReturnValues(status,orig_segnum);
      }
  }

  /**
   * @see org.hbci4java.hbci.GV.HBCIJobImpl#skipBPDCheck()
   */
  @Override
  protected boolean skipBPDCheck()
  {
    return true;
  }
}
