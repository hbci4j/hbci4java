/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format.legacy;

/**
 * Basis-Klasse der Converter.
 */
public abstract class AbstractConverter implements Converter
{
    private final static byte[] CIPHER_SALT = {(byte)0x26,(byte)0x19,(byte)0x38,(byte)0xa7,(byte)0x99,(byte)0xbc,(byte)0xf1,(byte)0x55};

    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#getSalt()
     */
    @Override
    public byte[] getSalt()
    {
        return CIPHER_SALT;
    }
}
