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

package org.hbci4java.hbci.GV;


import java.util.Properties;

import org.hbci4java.hbci.GV_Result.GVRInfoList;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.HBCIUtilsInternal;
import org.hbci4java.hbci.manager.LogFilter;
import org.hbci4java.hbci.status.HBCIMsgStatus;

public final class GVInfoList
    extends HBCIJobImpl
{
    public static String getLowlevelName()
    {
        return "InfoList";
    }
    
    public GVInfoList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRInfoList());

        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
    }

    /**
     * @see org.hbci4java.hbci.GV.HBCIJobImpl#redoAllowed()
     */
    @Override
    protected boolean redoAllowed()
    {
        return true;
    }
    
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        for (int i=0;;i++) {
            GVRInfoList.Info entry=new GVRInfoList.Info();
            String           header2=HBCIUtilsInternal.withCounter(header+".InfoInfo",i);

            if (result.getProperty(header2+".code")==null)
                break;
            
            entry.code=result.getProperty(header2+".code");
            entry.date=HBCIUtils.string2DateISO(result.getProperty(header2+".version"));
            entry.description=result.getProperty(header2+".descr");
            entry.format=result.getProperty(header2+".format");
            entry.type=result.getProperty(header2+".type");

            for (int j=0;;j++) {
                String hint=result.getProperty(header2+HBCIUtilsInternal.withCounter(".comment",j));
                if (hint==null)
                    break;
                entry.addComment(hint);
            }

            ((GVRInfoList)(jobResult)).addEntry(entry);
        }
    }
}
