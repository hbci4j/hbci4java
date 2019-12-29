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
import org.kapott.hbci.passport.HBCIPassportRSA;
import org.kapott.hbci.passport.storage.PassportData;

/**
 * Implementierung des Converter fuer RSA.
 * Von ConverterDDV abgeleitet, weil es den selben Salt verwendet.
 */
public class ConverterRSA extends ConverterDDV
{
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
        data.sysId       = (String) o.readObject();
        data.customerId  = (String) o.readObject();
        
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
        o.writeObject(data.sysId);
        o.writeObject(data.customerId);
        os.flush();
    }

    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#supports(org.kapott.hbci.passport.HBCIPassport)
     */
    @Override
    public boolean supports(HBCIPassport passport)
    {
        // Wir unterstuetzen nur den RSA-Passport.
        return passport != null && (passport instanceof HBCIPassportRSA);
    }

}
