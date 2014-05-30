
/*  $Id: FilterBase64.java,v 1.1 2011/05/04 22:37:51 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

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
