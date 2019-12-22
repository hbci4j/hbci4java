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

import java.io.File;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.storage.PassportData;
import org.kapott.hbci.passport.storage.PassportStorage;

/** <p>Passport-Implementation für anonyme Zugänge. Bei dieser Passport-Variante
    handelt es sich nicht um einen "echten" HBCI-Zugang. Statt dessen handelt
    es sich hierbei um einen anonymen Zugang, wie er von einigen wenigen
    Banken angeboten wird. Bei einem anonymen Zugang werden die Nachrichten
    nicht kryptographisch gesichert (also keine Signaturen und keine
    Verschlüsselung). Aus diesem Grund können mit diesem Zugang maximal
    die Geschäftsvorfälle abgewickelt werden, die keine Signatur benötigten
    (z.B. Gastmeldung an Bank senden).</p>
    <p>Beim Einrichten eines solchen Passport-Objektes werden also keine
    Nutzer- bzw. Kunden-Kennungen abgefragt (diese sind automatisch auf die Kennungen
    für den anonymen Zugang eingestellt). Außerdem findet keine Synchronisierung
    der Schlüssel zwischen Bank und Kunde statt, da bei dieser Zugangsform
    keine Schlüssel verwendet werden.</p>
    <p>Eine HBCI-Anwendung kann ein Anonymous-Passport genauso verwenden wie
    ein "normales" Passport. Alle Abweichungen, die bei der Durchführung
    anonymer Dialoge zu beachten sind, werden völlig transparent von <em>HBCI4Java</em>
    umgesetzt.</p>
    <p>Gegenwärtig können mit Anonymous-Passports noch keine anonymen Geschäftsvorfälle
    ausgelöst werden. Diese Passport-Variante kann also nur für einen "leeren" 
    HBCI-Dialog verwendet werden, der aus (anonymer) Dialog-Initialisierung und
    (anonymem) Dialog-Ende besteht. Damit kann zumindest die Verfügbarkeit des
    HBCI-Servers bzw. von anonymen Zugängen überprüft werden.</p>*/
public class HBCIPassportAnonymous extends AbstractHBCIPassport
{
    private String filename;
    
    /**
     * ct.
     * @param initObject
     */
    public HBCIPassportAnonymous(Object initObject)
    {
        super(initObject);

        String  header="client.passport.Anonymous.";
        String  filename=HBCIUtils.getParam(header+"filename");
        boolean init=HBCIUtils.getParam(header+"init","1").equals("1");
        
        if (filename==null)
            throw new NullPointerException("*** client.passport.Anonymous.filename must not be null");

        HBCIUtils.log("loading passport data from file "+filename,HBCIUtils.LOG_DEBUG);
        setFileName(filename);
        setFilterType("None");
        setPort(Integer.valueOf(3000));

        if (init)
        {
            HBCIUtils.log("loading data from file "+filename,HBCIUtils.LOG_DEBUG);

            if (!new File(filename).canRead())
            {
                HBCIUtils.log("have to create new passport file",HBCIUtils.LOG_WARN);
                askForMissingData(true,true,true,true,false,false,false);
                saveChanges();
            }

            PassportData data = PassportStorage.load(this,new File(filename));
            this.setBLZ(data.blz);
            this.setCountry(data.country);
            this.setHost(data.host);
            this.setPort(data.port);
            this.setHBCIVersion(data.hbciVersion);

            this.setBPD(data.bpd);
            this.setUPD(data.upd);
                
            if (askForMissingData(true,true,true,true,false,false,false))
                saveChanges();
        }
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getPassportTypeName()
     */
    @Override
    public String getPassportTypeName()
    {
        return "Anonymous";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPublicDigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPublicDigKey(HBCIKey key)
    {
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getSigFunction()
     */
    @Override
    public String getSigFunction()
    {
         return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getProfileMethod()
     */
    @Override
    public String getProfileMethod()
    {
        return "";
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#needUserKeys()
     */
    @Override
    public boolean needUserKeys()
    {
        return false;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getInstEncKey()
     */
    @Override
    public HBCIKey getInstEncKey()
    {
        return null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMyEncKeyVersion()
     */
    @Override
    public String getMyEncKeyVersion()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMySigKeyNum()
     */
    @Override
    public String getMySigKeyNum()
    {
        return "";
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getCryptMode()
     */
    @Override
    public String getCryptMode()
    {
        return "";
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#needInstKeys()
     */
    @Override
    public boolean needInstKeys()
    {
        return false;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getSigAlg()
     */
    @Override
    public String getSigAlg()
    {
        return "";
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getSigMode()
     */
    @Override
    public String getSigMode()
    {
        return "";
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#encrypt(byte[])
     */
    @Override
    public byte[][] encrypt(byte[] parm1)
    {
        return new byte[][] {null,parm1};
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstSigKeyVersion()
     */
    @Override
    public String getInstSigKeyVersion()
    {
        return "";
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setInstSigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setInstSigKey(HBCIKey key)
    {
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getCryptKeyType()
     */
    @Override
    public String getCryptKeyType()
    {
        return "";
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMySigKeyName()
     */
    @Override
    public String getMySigKeyName()
    {
        return "";
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMySigKeyVersion()
     */
    @Override
    public String getMySigKeyVersion()
    {
        return "";
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPublicEncKey()
     */
    @Override
    public HBCIKey getMyPublicEncKey()
    {
        return null;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#needUserSig()
     */
    @Override
    public boolean needUserSig()
    {
        return false;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPublicDigKey()
     */
    @Override
    public HBCIKey getMyPublicDigKey()
    {
        return null;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPrivateEncKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPrivateEncKey(HBCIKey key)
    {
    }
    
    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#getCommInstance()
     */
    @Override
    public Comm getCommInstance()
    {
        return Comm.getInstance("Standard",this);
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getProfileVersion()
     */
    @Override
    public String getProfileVersion()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPrivateSigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPrivateSigKey(HBCIKey key)
    {
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPrivateSigKey()
     */
    @Override
    public HBCIKey getMyPrivateSigKey()
    {
        return null;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPublicSigKey()
     */
    @Override
    public HBCIKey getMyPublicSigKey()
    {
        return null;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getCryptAlg()
     */
    @Override
    public String getCryptAlg()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPublicSigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPublicSigKey(HBCIKey key)
    {
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMyEncKeyNum()
     */
    @Override
    public String getMyEncKeyNum()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasMyEncKey()
     */
    @Override
    public boolean hasMyEncKey()
    {
        return false;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#hash(byte[])
     */
    @Override
    public byte[] hash(byte[] data)
    {
        /* the function hash-before-sign has nothing to do here, so we simply
         * return the original message */
        return data;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#sign(byte[])
     */
    @Override
    public byte[] sign(byte[] data)
    {
        /* no signature at all */
        return new byte[0];
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPrivateDigKey()
     */
    @Override
    public HBCIKey getMyPrivateDigKey()
    {
        return null;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#isSupported()
     */
    @Override
    public boolean isSupported()
    {
        return true;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasMySigKey()
     */
    @Override
    public boolean hasMySigKey()
    {
        return false;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#resetPassphrase()
     */
    @Override
    public void resetPassphrase()
    {
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMyEncKeyName()
     */
    @Override
    public String getMyEncKeyName()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getCryptFunction()
     */
    @Override
    public String getCryptFunction()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstSigKeyName()
     */
    @Override
    public String getInstSigKeyName()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPrivateDigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPrivateDigKey(HBCIKey key)
    {
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPrivateEncKey()
     */
    @Override
    public HBCIKey getMyPrivateEncKey()
    {
        return null;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPublicEncKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPublicEncKey(HBCIKey key)
    {
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstEncKeyVersion()
     */
    @Override
    public String getInstEncKeyVersion()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getHashAlg()
     */
    @Override
    public String getHashAlg()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#decrypt(byte[], byte[])
     */
    @Override
    public byte[] decrypt(byte[] parm1, byte[] parm2)
    {
        return parm2;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setInstEncKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setInstEncKey(HBCIKey key)
    {
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasInstEncKey()
     */
    @Override
    public boolean hasInstEncKey()
    {
        return false;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstSigKeyNum()
     */
    @Override
    public String getInstSigKeyNum()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getInstSigKey()
     */
    @Override
    public HBCIKey getInstSigKey()
    {
        return null;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#verify(byte[], byte[])
     */
    @Override
    public boolean verify(byte[] parm1, byte[] parm2)
    {
        return true;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstEncKeyName()
     */
    @Override
    public String getInstEncKeyName()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstEncKeyNum()
     */
    @Override
    public String getInstEncKeyNum()
    {
        return "";
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasInstSigKey()
     */
    @Override
    public boolean hasInstSigKey()
    {
        return false;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getSysStatus()
     */
    @Override
    public String getSysStatus()
    {
        return "0";
    }
    
    /**
     * Speichert den Dateinamen.
     * @param filename der Dateiname.
     */
    private void setFileName(String filename)
    {
        this.filename=filename;
    }
    
    /**
     * Liefert den Dateinamen.
     * @return der Dateiname.
     */
    public String getFileName()
    {
        return filename;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#saveChanges()
     */
    @Override
    public void saveChanges()
    {
        try
        {
            final PassportData data = new PassportData();
            data.country     = this.getCountry();
            data.blz         = this.getBLZ();
            data.host        = this.getHost();
            data.port        = this.getPort();
            data.hbciVersion = this.getHBCIVersion();
            data.bpd         = this.getBPD();
            data.upd         = this.getUPD();

            PassportStorage.save(this,data,new File(this.getFileName()));
        }
        catch (HBCI_Exception he)
        {
            throw he;
        }
        catch (Exception e)
        {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_WRITEERR"),e);
        }
    }

    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#isAnonymous()
     */
    @Override
    public boolean isAnonymous()
    {
        return true;
    }
}
