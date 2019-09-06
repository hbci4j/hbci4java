/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIKeyUtil;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.storage.PassportData;
import org.kapott.hbci.passport.storage.PassportStorage;
import org.kapott.hbci.security.Crypt;
import org.kapott.hbci.security.Sig;
import org.kapott.hbci.tools.DigestUtils;
import org.kapott.hbci.tools.SignUtils;

/**
 * Implementierung des Passports fuer RAH10-Schluesseldateien.
 */
public class HBCIPassportRAH10 extends AbstractHBCIPassport
{
    private final static String PROFILE_NAME    = "RAH";
    private final static String PROFILE_VERSION = "10";

    /**
     * Der Prefix fuer die Parametrisierung via HBCIUtils.setParam.
     */
    public final static String PARAM_PREFIX = "client.passport." + PROFILE_NAME + PROFILE_VERSION;
    

    private String filename = null;
    private PassportData data = null;
	
    /**
     * ct.
     * @param initObject
     */
    public HBCIPassportRAH10(Object initObject)
    {
        super(initObject);
        this.setParamHeader(PARAM_PREFIX);
        
        final String prefix = this.getParamHeader(); // Koennte ja in einer abgeleiteten Klasse ueberschrieben worden sein.

        this.filename = HBCIUtils.getParam(prefix+".filename");
        if (this.filename == null)
            throw new NullPointerException(prefix + ".filename must not be null");

        HBCIUtils.log("using passport file " + filename,HBCIUtils.LOG_DEBUG);

        if (!HBCIUtils.getParam(prefix + ".init","1").equals("1"))
            return;

        HBCIUtils.log("loading data from " + filename,HBCIUtils.LOG_DEBUG);

        this.setFilterType("None");
        this.setPort(new Integer(3000));

        // Datei neu erstellen, wenn sie noch nicht existiert
        if (!new File(filename).canRead())
        {
            HBCIUtils.log("have to create new passport file",HBCIUtils.LOG_INFO);
            this.askForMissingData(true,true,true,true,false,true,true);
            this.saveChanges();
        }
        
        this.data = PassportStorage.load(this,new File(filename));
        
        // Wir uebernehmen nur die Daten in die Basis-Klasse, die dort vorgehalten werden.
        // Den Rest nehmen wir direkt aus PassportData
        this.setBLZ(data.blz);
        this.setCountry(data.country);
        this.setHost(data.host);
        this.setPort(data.port);
        this.setUserId(data.userId);
        this.setCustomerId(data.customerId);
        this.setSysId(data.sysId);
        this.setSigId(data.sigId);
        this.setHBCIVersion(data.hbciVersion);
        this.setBPD(data.bpd);
        this.setUPD(data.upd);
        
        // Falls in der existierenden Datei auch noch Daten fehlen
        if (this.askForMissingData(true,true,true,true,false,true,true))
            this.saveChanges();
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#saveChanges()
     */
    public void saveChanges()
    {
        try
        {
            // Vorm Speichern die ggf. in der Basis-Klasse geaenderten Daten uebernehmen
            this.data.country     = this.getCountry();
            this.data.blz         = this.getBLZ();
            this.data.host        = this.getHost();
            this.data.port        = this.getPort();
            this.data.userId      = this.getUserId();
            this.data.customerId  = this.getCustomerId();
            this.data.sysId       = this.getSysId();
            this.data.sigId       = this.getSigId();
            this.data.hbciVersion = this.getHBCIVersion();
            this.data.bpd         = this.getBPD();
            this.data.upd         = this.getUPD();

            PassportStorage.save(this,data,new File(this.filename));
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
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getPassportTypeName()
     */
    @Override
    public String getPassportTypeName()
    {
        return PROFILE_NAME;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getProfileMethod()
     */
    @Override
    public String getProfileMethod()
    {
        return PROFILE_NAME;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getProfileVersion()
     */
    public String getProfileVersion()
    {
        return PROFILE_VERSION;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#resetPassphrase()
     */
    @Override
    public void resetPassphrase()
    {
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getSysStatus()
     */
    @Override
    public String getSysStatus()
    {
        return "1";
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
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setInstSigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setInstSigKey(HBCIKey key)
    {
        this.data.instSigKey = key;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setInstEncKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setInstEncKey(HBCIKey key)
    {
        this.data.instEncKey = key;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPublicSigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPublicSigKey(HBCIKey key)
    {
        this.data.myPublicSigKey = key;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPrivateSigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPrivateSigKey(HBCIKey key)
    {
        this.data.myPrivateSigKey = key;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPublicEncKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPublicEncKey(HBCIKey key)
    {
        this.data.myPublicEncKey = key;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPrivateEncKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPrivateEncKey(HBCIKey key)
    {
        this.data.myPrivateEncKey = key;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPublicDigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPublicDigKey(HBCIKey key)
    {
        // Nicht implementiert
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPrivateDigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPrivateDigKey(HBCIKey key)
    {
        // Nicht implementiert
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstSigKeyName()
     */
    @Override
    public String getInstSigKeyName()
    {
        return HBCIKeyUtil.getUserId(this.getInstSigKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstSigKeyNum()
     */
    @Override
    public String getInstSigKeyNum()
    {
        return HBCIKeyUtil.getNum(this.getInstSigKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstSigKeyVersion()
     */
    @Override
    public String getInstSigKeyVersion()
    {
        return HBCIKeyUtil.getVersion(this.getInstSigKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstEncKeyName()
     */
    @Override
    public String getInstEncKeyName()
    {
        return HBCIKeyUtil.getUserId(this.getInstEncKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstEncKeyNum()
     */
    @Override
    public String getInstEncKeyNum()
    {
        return HBCIKeyUtil.getNum(this.getInstEncKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstEncKeyVersion()
     */
    @Override
    public String getInstEncKeyVersion()
    {
        return HBCIKeyUtil.getVersion(this.getInstEncKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMySigKeyName()
     */
    @Override
    public String getMySigKeyName()
    {
        return HBCIKeyUtil.getUserId(this.getMyPublicSigKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMySigKeyNum()
     */
    @Override
    public String getMySigKeyNum()
    {
        return HBCIKeyUtil.getNum(this.getMyPublicSigKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMySigKeyVersion()
     */
    @Override
    public String getMySigKeyVersion()
    {
        return HBCIKeyUtil.getVersion(this.getMyPublicSigKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMyEncKeyName()
     */
    @Override
    public String getMyEncKeyName()
    {
        return HBCIKeyUtil.getUserId(this.getMyPublicEncKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMyEncKeyNum()
     */
    @Override
    public String getMyEncKeyNum()
    {
        return HBCIKeyUtil.getNum(this.getMyPublicEncKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMyEncKeyVersion()
     */
    @Override
    public String getMyEncKeyVersion()
    {
        return HBCIKeyUtil.getVersion(this.getMyPublicEncKey());
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getCryptKeyType()
     */
    @Override
    public String getCryptKeyType()
    {
        return Crypt.ENC_KEYTYPE_RSA;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getCryptFunction()
     */
    @Override
    public String getCryptFunction()
    {
        return Crypt.SECFUNC_ENC;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getCryptAlg()
     */
    @Override
    public String getCryptAlg()
    {
        return Crypt.ENCALG_AES256;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getCryptMode()
     */
    @Override
    public String getCryptMode()
    {
        return Crypt.ENCMODE_CBC;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getSigFunction()
     */
    @Override
    public String getSigFunction()
    {
        return Sig.SECFUNC_FINTS_SIG_SIG;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getSigAlg()
     */
    @Override
    public String getSigAlg()
    {
        return Sig.SIGALG_RSA;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getSigMode()
     */
    @Override
    public String getSigMode()
    {
        return Sig.SIGMODE_PSS;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getHashAlg()
     */
    @Override
    public String getHashAlg()
    {
        return Sig.HASHALG_SHA256_SHA256;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#hash(byte[])
     */
    @Override
    public byte[] hash(byte[] data)
    {
        // Bei RDH/RAH wird doppelt gehasht.
        // Die Signatur selbst wird bereits auf einem Hash der Daten durchgefuehrt.
        // Die Spec schreibt aber vor, dass nicht die Daten signiert werden sondern
        // der Hash. Also doppelt
        return DigestUtils.hash(data,DigestUtils.ALG_SHA256);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#sign(byte[])
     */
    @Override
    public byte[] sign(byte[] data)
    {
        // Damit ich hier nicht versehentlich einen unnoetigen Fehler einbaue,
        // portiere ich es erstmal 1:1 von RDH-10.
        // Ich weiss nicht, ob die PKCS1-Implementierung in dem eigenen Crypto-Provider
        // ueberhaupt noch notwendig ist oder inzwischen bereits in Java
        // enthalten ist.
        PrivateKey key = (PrivateKey) this.getMyPrivateSigKey().key;
        byte[] sig = SignUtils.sign(data,key,SignUtils.ALG_RSA,DigestUtils.ALG_SHA256);
        
        // Obiges gilt auch fuer das Padding. Eigentlich sollte das ja auch
        // der Crypto-Provider direkt machen.
        return SignUtils.padLeft(sig,(RSAPublicKey) this.getMyPublicSigKey().key);
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#verify(byte[], byte[])
     */
    @Override
    public boolean verify(byte[] data, byte[] sig)
    {
        PublicKey key = (PublicKey) this.getInstSigKey().key;
        return SignUtils.verify(data,sig,key,SignUtils.ALG_RSA,DigestUtils.ALG_SHA256);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#encrypt(byte[])
     */
    @Override
    public byte[][] encrypt(byte[] plainMsg)
    {
        // Einmal-Schluessel fuer die Nachricht erzeugen
        String provider = HBCIUtils.getParam("kernel.security.provider");
        KeyGenerator keygen = provider != null ? KeyGenerator.getInstance("AES",provider) : KeyGenerator.getInstance("AES");
        keygen.init(256);
        SecretKey msgKey = keygen.generateKey();

//        // Nachricht mit dem Einmalschluessel verschluesseln
//        byte[] cryptMsg = encryptMessage(plainMsg,msgKey);
//        
//        // Einmalschluessel mit dem Instituts-Schluessel verschluesseln
//        byte[] cryptKey = encryptKey(msgKey.getEncoded());

        byte[][] ret=new byte[2][];
//        ret[0]=cryptKey;
//        ret[1]=cryptMsg;

        return ret;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#decrypt(byte[], byte[])
     */
    @Override
    public byte[] decrypt(byte[] cryptedKey, byte[] encryptedMsg)
    {
        // TODO Auto-generated
        return null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#isSupported()
     */
    @Override
    public boolean isSupported()
    {
        // Noch keine BPD - wir wissen es noch nicht.
        // Sicherheitshalber sagen wir mal ja.
        if (this.getBPD() == null)
            return true;
        
        for (String[] entry:this.getSuppSecMethods())
        {
            if (PROFILE_NAME.equals(entry[0]) && PROFILE_VERSION.equals(entry[1]))
                return true;
        }
        
        return false;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#needInstKeys()
     */
    @Override
    public boolean needInstKeys()
    {
        return true;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#needUserKeys()
     */
    @Override
    public boolean needUserKeys()
    {
        return true;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasInstSigKey()
     */
    @Override
    public boolean hasInstSigKey()
    {
        return this.getInstSigKey() != null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasInstEncKey()
     */
    @Override
    public boolean hasInstEncKey()
    {
        return this.getInstEncKey() != null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasMySigKey()
     */
    @Override
    public boolean hasMySigKey()
    {
        return this.getMyPublicSigKey() != null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasMyEncKey()
     */
    @Override
    public boolean hasMyEncKey()
    {
        return this.getMyPublicEncKey() != null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPublicSigKey()
     */
    @Override
    public HBCIKey getMyPublicSigKey()
    {
        return this.data.myPublicSigKey;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPublicEncKey()
     */
    @Override
    public HBCIKey getMyPublicEncKey()
    {
        return this.data.myPublicEncKey;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPublicDigKey()
     */
    @Override
    public HBCIKey getMyPublicDigKey()
    {
        // Nicht implementiert
        return null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPrivateSigKey()
     */
    @Override
    public HBCIKey getMyPrivateSigKey()
    {
        return this.data.myPrivateSigKey;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPrivateEncKey()
     */
    @Override
    public HBCIKey getMyPrivateEncKey()
    {
        return this.data.myPrivateEncKey;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPrivateDigKey()
     */
    @Override
    public HBCIKey getMyPrivateDigKey()
    {
        // Nicht implementiert
        return null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getInstSigKey()
     */
    @Override
    public HBCIKey getInstSigKey()
    {
        return this.data.instSigKey;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getInstEncKey()
     */
    @Override
    public HBCIKey getInstEncKey()
    {
        return this.data.instEncKey;
    }

    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#getCommInstance()
     */
    @Override
    public Comm getCommInstance()
    {
        return Comm.getInstance("Standard",this);
    }
}
