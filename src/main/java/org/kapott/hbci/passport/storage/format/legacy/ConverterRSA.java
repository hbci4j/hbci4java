/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
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
