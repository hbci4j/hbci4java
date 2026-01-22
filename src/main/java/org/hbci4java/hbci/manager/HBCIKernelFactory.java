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

package org.hbci4java.hbci.manager;

import org.hbci4java.hbci.passport.HBCIPassport;

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
