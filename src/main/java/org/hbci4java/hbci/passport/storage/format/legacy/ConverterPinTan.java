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

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.passport.HBCIPassport;
import org.hbci4java.hbci.passport.HBCIPassportPinTan;
import org.hbci4java.hbci.passport.storage.PassportData;

/**
 * Implementierung des Converter fuer PIN/TAN.
 */
public class ConverterPinTan extends AbstractConverter
{
    /**
     * @see org.hbci4java.hbci.passport.storage.format.legacy.Converter#load(java.io.InputStream)
     */
    @Override
    public PassportData load(InputStream is) throws Exception
    {
        final PassportData data = new PassportData();
        
        final ObjectInputStream o = new ObjectInputStream(is);
        
        data.country     = (String) o.readObject();
        data.blz         = (String) o.readObject();
        data.host        = (String) o.readObject();
        data.port        = (Integer) o.readObject();
        data.userId      = (String) o.readObject();
        data.sysId       = (String) o.readObject();
        data.bpd         = (Properties) o.readObject();
        data.upd         = (Properties) o.readObject();
        data.hbciVersion = (String) o.readObject();
        data.customerId  = (String) o.readObject();
        data.filter      = (String) o.readObject();
        
        // Wir tolerieren, wenn das fehlschlaegt
        try
        {
            data.twostepMechs = (List<String>) o.readObject();
        }
        catch (Exception e)
        {
            HBCIUtils.log("no list of allowed secmechs found in passport file", HBCIUtils.LOG_WARN);
        }
        try
        {
            data.tanMethod = (String) o.readObject();
        }
        catch (Exception e)
        {
            HBCIUtils.log("no current secmech found in passport file", HBCIUtils.LOG_WARN);
        }
        
        return data;
    }
    
    /**
     * @see org.hbci4java.hbci.passport.storage.format.legacy.Converter#save(org.hbci4java.hbci.passport.storage.PassportData, java.io.OutputStream)
     */
    @Override
    public void save(PassportData data, OutputStream os) throws Exception
    {
        final ObjectOutputStream o = new ObjectOutputStream(os);
        o.writeObject(data.country);
        o.writeObject(data.blz);
        o.writeObject(data.host);
        o.writeObject(data.port);
        o.writeObject(data.userId);
        o.writeObject(data.sysId);
        o.writeObject(data.bpd);
        o.writeObject(data.upd);
        o.writeObject(data.hbciVersion);
        o.writeObject(data.customerId);
        o.writeObject(data.filter);
        o.writeObject(data.twostepMechs);
        o.writeObject(data.tanMethod);
        os.flush();
    }

    /**
     * @see org.hbci4java.hbci.passport.storage.format.legacy.Converter#supports(org.hbci4java.hbci.passport.HBCIPassport)
     */
    @Override
    public boolean supports(HBCIPassport passport)
    {
        return passport != null && (passport instanceof HBCIPassportPinTan);
    }

}
