
/*  $Id: AbstractRDHSWFileBasedPassport.java,v 1.1 2011/05/04 22:37:43 willuhn Exp $

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

import javax.crypto.SecretKey;

public abstract class AbstractRDHSWFileBasedPassport 
	extends AbstractRDHSWPassport 
{
    private String    filename;
    private SecretKey passportKey;
    
    protected final static byte[] CIPHER_SALT={(byte)0x26,(byte)0x19,(byte)0x38,(byte)0xa7,
                                               (byte)0x99,(byte)0xbc,(byte)0xf1,(byte)0x55};
    protected final static int CIPHER_ITERATIONS=987;

    protected AbstractRDHSWFileBasedPassport(Object init) {
        super(init);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public SecretKey getPassportKey() {
        return passportKey;
    }

    public void setPassportKey(SecretKey passportKey) {
        this.passportKey = passportKey;
    }

    public void resetPassphrase() {
        setPassportKey(null);
    }

    public void close() {
        super.close();
        resetPassphrase();
    }
}
