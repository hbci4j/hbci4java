/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
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

package org.kapott.hbci.GV;


import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRVoP;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Die Geschaeftsvorfall-Implementierung fuer VoP.
 */
public class GVVoP extends HBCIJobImpl
{
    private HBCIJobImpl task;
    private HBCIJobImpl redo;

    /**
     * Liefert den Lowlevel-Namen.
     * @return der Lowlevel-Name.
     */
    public static String getLowlevelName()
    {
        return "VoPCheck";
    }
    
    /**
     * ct.
     * @param handler
     */
    public GVVoP(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRVoP());
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#setParam(java.lang.String, java.lang.String)
     */
    public void setParam(String paramName, String value)
    {
        if (paramName.equals("pollingid"))
            value="B"+value;
        super.setParam(paramName,value);
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
     * @see org.kapott.hbci.GV.HBCIJobImpl#saveReturnValues(org.kapott.hbci.status.HBCIMsgStatus, int)
     */
    protected void saveReturnValues(HBCIMsgStatus status, int sref)
    {
        super.saveReturnValues(status, sref);
        
        // Rueckgabecode an den eigentlichen Auftrag weiterreichen
        if (this.task != null)
        {
            int orig_segnum=Integer.parseInt(task.getJobResult().getSegNum());
            HBCIUtils.log("storing return values in orig task (segnum="+orig_segnum+")", HBCIUtils.LOG_DEBUG);
            task.saveReturnValues(status,orig_segnum);
        }
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#redo()
     */
    @Override
    public HBCIJobImpl redo()
    {
      // TODO: redo noch klären
      return this.redo;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#haveTan()
     */
    @Override
    public boolean haveTan()
    {
        // VoP kann nie ein HKTAN benoetigen - die wird nur fuer den eigentlichen Auftrag gebraucht.
        return true;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        final Properties result = msgstatus.getData();
        final String segCode = result.getProperty(header+".SegHead.code"); // HIVPP oder das HI** des GV
        HBCIUtils.log("found HKTAN response with segcode " + segCode,HBCIUtils.LOG_DEBUG);
    }
}
