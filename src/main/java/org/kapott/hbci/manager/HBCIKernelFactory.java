
/*  $Id: HBCIKernelFactory.java,v 1.1 2011/05/04 22:37:46 willuhn Exp $

    This file is part of hbci4java
    Copyright (C) 2001-2008  Stefan Palme

    hbci4java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    hbci4java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.manager;

import org.kapott.hbci.passport.HBCIPassport;

/** Factory-Klasse für das Erzeugen von HBCI-Kernel-Objekten */
public class HBCIKernelFactory
{
    /** Neues HBCI-Kernel-Objekt erzeugen.
     * @param hbciversion 
     * {@link HBCIHandler#HBCIHandler(String,HBCIPassport) HBCI-Versionsnummer},
     * für die ein Kernel-Objekt erzeugt werden soll */
    public static HBCIKernel getKernel(IHandlerData parentHandlerData,String hbciversion) 
    {
        return new HBCIKernelImpl(parentHandlerData,hbciversion);
    }
}
