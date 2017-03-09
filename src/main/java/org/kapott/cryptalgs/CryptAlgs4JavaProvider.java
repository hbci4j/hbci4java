
/*  $Id: CryptAlgs4JavaProvider.java,v 1.1 2011/05/04 22:37:58 willuhn Exp $

    This file is part of CryptAlgs4Java
    Copyright (C) 2001-2010  Stefan Palme

    CryptAlgs4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    CryptAlgs4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.cryptalgs;

import java.util.logging.Logger;

public final class CryptAlgs4JavaProvider
     extends java.security.Provider
{
    private static final long serialVersionUID=1;
    
    protected Logger getLogger()
    {
        return Logger.getLogger(this.getClass().getName());
    }
    
    public CryptAlgs4JavaProvider()
    {
        super("CryptAlgs4Java", 1.5, "Some hand-coded algorithms for special use cases");

        put("MessageDigest.RIPEMD160", "org.kapott.cryptalgs.RIPEMD160");
        put("MessageDigest.MDC2", "org.kapott.cryptalgs.MDC2");

        put("Signature.ISO9796p1", "org.kapott.cryptalgs.ISO9796p1");
        put("Signature.ISO9796p2", "org.kapott.cryptalgs.ISO9796p2");
        put("Signature.PKCS1_PSS", "org.kapott.cryptalgs.PKCS1_PSS");
        put("Signature.PKCS1_15",  "org.kapott.cryptalgs.PKCS1_15");
        
        getLogger().fine("initializing CryptAlgs4JavaProvider");
    }
}
