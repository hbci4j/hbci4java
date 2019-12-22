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

package org.kapott.hbci.status;

import java.io.Serializable;
import java.util.Properties;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtilsInternal;

public final class HBCIInstMessage
    implements Serializable
{
    private String betreff;
    private String text;

    public HBCIInstMessage(Properties result,String header)
    {
        betreff=result.getProperty(header+".betreff");
        if (betreff==null)
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXECMS_IMSGNOSUCHMSG",header));
        text=result.getProperty(header+".text");
    }

    public String toString()
    {
        return betreff+": "+text;
    }
}
