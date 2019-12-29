/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2010 Stefan Palme
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

package org.kapott.cryptalgs;

import java.util.logging.Logger;

public final class CryptAlgs4JavaProvider extends java.security.Provider
{
    private static final long serialVersionUID=1;
    
    /**
     * Der Provider-Name.
     */
    public final static String NAME = "CryptAlgs4Java";
    
    protected Logger getLogger()
    {
        return Logger.getLogger(this.getClass().getName());
    }
    
    public CryptAlgs4JavaProvider()
    {
        super(NAME, 1.5, "Some hand-coded algorithms for special use cases");

        put("MessageDigest.RIPEMD160", "org.kapott.cryptalgs.RIPEMD160");
        put("MessageDigest.MDC2", "org.kapott.cryptalgs.MDC2");

        put("Signature.ISO9796p1", "org.kapott.cryptalgs.ISO9796p1");
        put("Signature.ISO9796p2", "org.kapott.cryptalgs.ISO9796p2");
        put("Signature.PKCS1_PSS", "org.kapott.cryptalgs.PKCS1_PSS");
        put("Signature.PKCS1_15",  "org.kapott.cryptalgs.PKCS1_15");
        
        getLogger().fine("initializing CryptAlgs4JavaProvider");
    }
}
