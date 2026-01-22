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

package org.hbci4java.hbci.GV;

import java.util.Enumeration;
import java.util.Properties;

import org.hbci4java.hbci.GV_Result.GVRTermUeb;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.LogFilter;
import org.hbci4java.hbci.status.HBCIMsgStatus;

/**
 * Job-Implementierung fuer terminierte SEPA-Sammelueberweisungen.
 */
public class GVTermMultiUebSEPA extends GVMultiUebSEPA
{
  /**
   * Liefert den Lowlevel-Namen des Jobs.
   * @return der Lowlevel-Namen des Jobs.
   */
  public static String getLowlevelName()
  {
    return "TermSammelUebSEPA";
  }

  /**
   * ct.
   * @param handler
   */
  public GVTermMultiUebSEPA(HBCIHandler handler)
  {
    this(handler, getLowlevelName());
  }

  /**
   * ct.
   * @param handler
   * @param name
   */
  public GVTermMultiUebSEPA(HBCIHandler handler, String name)
  {
    super(handler, name, new GVRTermUeb());
    addConstraint("date", "sepa.date", null, LogFilter.FILTER_NONE);
  }

  /**
   * @see org.hbci4java.hbci.GV.HBCIJobImpl#extractResults(org.hbci4java.hbci.status.HBCIMsgStatus, java.lang.String, int)
   */
  protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
  {
      Properties result=msgstatus.getData();
      String orderid=result.getProperty(header+".orderid");
      ((GVRTermUeb)(jobResult)).setOrderId(orderid);
      
      if (orderid!=null && orderid.length()!=0) {
          Properties p=getLowlevelParams();
          Properties p2=new Properties();
          
          for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
              String key=(String)e.nextElement();
              p2.setProperty(key.substring(key.indexOf(".")+1),
                             p.getProperty(key));
          }
          
          getMainPassport().setPersistentData("termueb_"+orderid,p2);
      }
  }
}
