/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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

package org.hbci4java.hbci.passport.storage.format.legacy;

/**
 * Basis-Klasse der Converter.
 */
public abstract class AbstractConverter implements Converter
{
    private final static byte[] CIPHER_SALT = {(byte)0x26,(byte)0x19,(byte)0x38,(byte)0xa7,(byte)0x99,(byte)0xbc,(byte)0xf1,(byte)0x55};

    /**
     * @see org.hbci4java.hbci.passport.storage.format.legacy.Converter#getSalt()
     */
    @Override
    public byte[] getSalt()
    {
        return CIPHER_SALT;
    }
}
