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

package org.kapott.hbci.passport.storage.format.legacy;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportDDV;
import org.kapott.hbci.passport.storage.PassportData;

/**
 * Implementierung des Converter fuer DDV.
 */
public class ConverterDDV extends AbstractConverter
{
    private final static byte[] CIPHER_SALT_DDV = {(byte)0x56,(byte)0xbc,(byte)0x1c,(byte)0x88,(byte)0x1f,(byte)0xe3,(byte)0x73,(byte)0xcc};

    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.AbstractConverter#getSalt()
     */
    @Override
    public byte[] getSalt()
    {
        // Ueberschrieben, weil hier ein anderes Salt verwendet wird.
        return CIPHER_SALT_DDV;
    }

    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#load(java.io.InputStream)
     */
    @Override
    public PassportData load(InputStream is) throws Exception
    {
        final PassportData data = new PassportData();
        
        final ObjectInputStream o = new ObjectInputStream(is);
        data.bpd         = (Properties) o.readObject();
        data.upd         = (Properties) o.readObject();
        data.hbciVersion = (String) o.readObject();
        
        return data;
    }
    
    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#save(org.kapott.hbci.passport.storage.PassportData, java.io.OutputStream)
     */
    @Override
    public void save(PassportData data, OutputStream os) throws Exception
    {
        final ObjectOutputStream o = new ObjectOutputStream(os);
        o.writeObject(data.bpd);
        o.writeObject(data.upd);
        o.writeObject(data.hbciVersion);
        os.flush();
    }

    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#supports(org.kapott.hbci.passport.HBCIPassport)
     */
    @Override
    public boolean supports(HBCIPassport passport)
    {
        // Wir unterstuetzen nur den DDV-Passport.
        return passport != null && (passport instanceof HBCIPassportDDV);
    }

}


