
/*  $Id: HBCIPassportRDH10File.java,v 1.1 2011/05/04 22:37:43 willuhn Exp $

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

package org.kapott.hbci.passport;

import org.kapott.hbci.manager.HBCIUtils;

/** Compatibility and convenience class for applications using the "RDH2File" 
 * passport variant. To avoid user confusion (using RDH-10-keys with passport
 * variant "RDH2File") this class is a kind of alias for RDH2File (with exact 
 * the same behaviour).
 * Both RDH2File and RDH10File are deprecated now - use the more general name
 * RDHXFile instead.
 * @Deprecated Use RDHXFile instead */
public class HBCIPassportRDH10File
    extends HBCIPassportRDHXFile
{
    protected String getCompatName()
    {
        HBCIUtils.log("RDH10File should not be used any longer - use RDHXFile instead!", HBCIUtils.LOG_WARN);
        return "RDH10File";
    }

    public HBCIPassportRDH10File(Object initObject)
    {
        super(initObject);
    }
}
