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

import org.hbci4java.hbci.GV_Result.HBCIJobResultImpl;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.LogFilter;

/**
 * Job-Implementierung fuer SEPA-Multi-Ueberweisungen.
 */
public class GVMultiUebSEPA extends GVUebSEPA
{
    /**
     * Liefert den Lowlevel-Namen des Jobs.
     * @return der Lowlevel-Namen des Jobs.
     */
    public static String getLowlevelName()
    {
        return "SammelUebSEPA";
    }

    /**
     * @see org.hbci4java.hbci.GV.AbstractSEPAGV#getPainJobName()
     */
    @Override
    public String getPainJobName()
    {
        return "UebSEPA";
    }

    /**
     * ct.
     * @param handler
     */
    public GVMultiUebSEPA(HBCIHandler handler)
    {
        this(handler, getLowlevelName());
    }

    /**
     * ct.
     * @param handler
     * @param name
     */
    public GVMultiUebSEPA(HBCIHandler handler, String name)
    {
        this(handler, name, new HBCIJobResultImpl());
    }

    /**
     * ct.
     * @param handler
     * @param name
     * @param jobResult
     */
    public GVMultiUebSEPA(HBCIHandler handler, String name, HBCIJobResultImpl jobResult)
    {
        super(handler, name, jobResult);

        addConstraint("batchbook", "sepa.batchbook", "", LogFilter.FILTER_NONE);
        addConstraint("Total.value", "Total.value", null, LogFilter.FILTER_MOST);
        addConstraint("Total.curr", "Total.curr", null, LogFilter.FILTER_NONE);
    }

    /**
     * @see org.hbci4java.hbci.GV.HBCIJobImpl#getChallengeParam(java.lang.String)
     */
    @Override
    public String getChallengeParam(String path)
    {
        if (path.equals("sepa.btg.value")) {
            return getLowlevelParam(getName()+".Total.value");
        }
        else if (path.equals("sepa.btg.curr")) {
            return getLowlevelParam(getName()+".Total.curr");
        }
        return null;
    }

    @Override protected void createSEPAFromParams()
    {
        super.createSEPAFromParams();
        setParam("Total", SepaUtil.sumBtgValueObject(sepaParams));
    }
}
