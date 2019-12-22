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

package org.kapott.hbci.passport;

/**
 * 
 */
public abstract class AbstractRDHSWFileBasedPassport  extends AbstractRDHSWPassport implements FileBasedPassport 
{
    private String    filename;
    
    protected AbstractRDHSWFileBasedPassport(Object init) {
        super(init);
    }

    /**
     * @see org.kapott.hbci.passport.FileBasedPassport#getFilename()
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#close()
     */
    public void close() {
        super.close();
        resetPassphrase();
    }
}
