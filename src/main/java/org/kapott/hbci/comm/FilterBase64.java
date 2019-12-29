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

package org.kapott.hbci.comm;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;

public final class FilterBase64
    extends Filter
{
    public FilterBase64()
    {
        HBCIUtils.log("using filter: MIM (base64)",HBCIUtils.LOG_DEBUG);
    }
    
    public String decode(String st)
    {
        try {
            return new String(HBCIUtils.decodeBase64(st),Comm.ENCODING);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_B64DECODEERR"),e);
        }
    }
    
    public byte[] encode(String st)
    {
        try {
            return HBCIUtils.encodeBase64(st.getBytes(Comm.ENCODING)).getBytes(Comm.ENCODING);
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_B64ENCODEERR"),ex);
        }
    }
}
