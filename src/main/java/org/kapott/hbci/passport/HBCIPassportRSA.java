/**
 * 
 */
package org.kapott.hbci.passport;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import org.kapott.cryptalgs.SignatureParamSpec;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.datatypes.SyntaxCtr;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.storage.PassportData;
import org.kapott.hbci.passport.storage.PassportStorage;
import org.kapott.hbci.smartcardio.RSABankData;
import org.kapott.hbci.smartcardio.RSACardService;
import org.kapott.hbci.smartcardio.RSAKeyData;
import org.kapott.hbci.smartcardio.SmartCardService;
import org.kapott.hbci.tools.CryptUtils;
import org.kapott.hbci.tools.IOUtils;

/**
 * HBCI-Passport fuer RDH-Chipkarten.
 */
public class HBCIPassportRSA extends AbstractRDHPassport implements HBCIPassportChipcard
{
    private final static int KEY_INST_SIG = 0;
    private final static int KEY_INST_ENC = 1;
    private final static int KEY_MY_PUBLIC_SIG = 2;
    private final static int KEY_MY_PUBLIC_ENC = 3;
    
    private String filename;
    private HBCIKey[] keys;
    private String cardid;
    private boolean pinEntered;
    private int useSoftPin;
    private byte[] softPin;
    private int entryIdx;
    private String forcedProfileVersion;
    private String bankId;
    private String defaultCustomerId;
    
    private RSACardService cardService;
    
    /**
     * ct.
     * @param init
     * @param dummy
     */
    public HBCIPassportRSA(Object init, int dummy)
    {
        super(init);
        
        setParamHeader("client.passport.RSA");
        
        this.forcedProfileVersion = null;
        
        keys = new HBCIKey[4];
        for (int n = 0; n < 4; n++) {
            keys[n] = null;
        }
    }
    
    /**
     * ct.
     * @param init
     */
    public HBCIPassportRSA(Object init)
    {
        this(init, 0);
        
        try
        {
            ////////////////////////////////////////////////////////////////////////
            // set parameters for initializing card
            //setUseBio(Integer.parseInt(HBCIUtils.getParam(getParamHeader() + ".usebio", "-1")));
            setUseSoftPin(Integer.parseInt(HBCIUtils.getParam(getParamHeader() + ".softpin", "-1")));
            setSoftPin(new byte[0]);
            setPINEntered(false);
            setEntryIdx(Integer.parseInt(HBCIUtils.getParam(getParamHeader() + ".entryidx", "1")));
            //
            ////////////////////////////////////////////////////////////////////////
            
            ////////////////////////////////////////////////////////////////////////
            // init card
            HBCIUtils.log("initializing javax.smartcardio", HBCIUtils.LOG_DEBUG);
            HBCIUtilsInternal.getCallback().callback(this, HBCICallback.NEED_CHIPCARD, HBCIUtilsInternal.getLocMsg("CALLB_NEED_CHIPCARD"), HBCICallback.ResponseType.TYPE_NONE, null);
            
            initCT();
            HBCIUtilsInternal.getCallback().callback(this, HBCICallback.HAVE_CHIPCARD, "", HBCICallback.ResponseType.TYPE_NONE, null);
            //
            ////////////////////////////////////////////////////////////////////////
            
            ////////////////////////////////////////////////////////////////////////
            // init basic bank data
            setPort(new Integer(3000));
            setFilterType("None");
            try {
                readBankData();
                readKeyData();
            } catch (HBCI_Exception e) {
                throw e;
            } catch (Exception e) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_INSTDATAERR"), e);
            }
            //
            ////////////////////////////////////////////////////////////////////////
            
            ////////////////////////////////////////////////////////////////////////
            // Lokale Passport-Datei laden, wenn sie existiert
            final String path = HBCIUtils.getParam(getParamHeader() + ".path", "./");
            this.setFileName(HBCIUtilsInternal.withCounter(path + getCardId(), getEntryIdx() - 1));
            HBCIUtils.log("loading passport data from file " + getFileName(), HBCIUtils.LOG_DEBUG);
            
            File file = new File(getFileName());
            
            if (!file.exists() || !file.isFile() || !file.canRead())
            {
                HBCIUtils.log("have to create new passport file", HBCIUtils.LOG_WARN);
                askForMissingData(true, true, true, false, false, true, true);
                saveChanges();
            }

            PassportData data = PassportStorage.load(this,file);
            this.setBPD(data.bpd);
            this.setUPD(data.upd);
            this.setHBCIVersion(data.hbciVersion);
            this.setSysId(data.sysId);
            this.setCustomerId(data.customerId);
            //
            ////////////////////////////////////////////////////////////////////////
        }
        catch (Exception e)
        {
            // Verbindung zum Kartenleser nur im Fehlerfall schliessen
            try
            {
                closeCT();
            }
            catch (Exception e2)
            {
                HBCIUtils.log(e2);
            }
            
            if (e instanceof HBCI_Exception)
                throw (HBCI_Exception) e;
                
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CTERR"),e);
        }
    }
    
    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#getCustomerId()
     */
    @Override
    public String getCustomerId() {
        if (getStoredCustomerId() == null || getStoredCustomerId().length() == 0) {
            if (getDefaultCustomerId() == null || getDefaultCustomerId().length() == 0) {
                return getUserId();
            } else {
                return getDefaultCustomerId();
            }
        } else {
            return getStoredCustomerId();
        }
    }
    
    /**
     * Liefert die Default-Kundenkennung.
     * @return die Default-Kundenkennung.
     */
    public String getDefaultCustomerId() {
        return defaultCustomerId;
    }

    /**
     * Speichert die Default-Kundenkennung.
     * @param defaultCustomerId die Default-Kundenkennung.
     */
    public void setDefaultCustomerId(String defaultCustomerId) {
        this.defaultCustomerId = defaultCustomerId;
    }

    /**
     * Speichert die Bank-ID.
     * @param bankId die Bank-ID.
     */
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
    
    /**
     * Liefert die Bank-ID.
     * @return die Bank-ID.
     */
    public String getBankId() {
        return bankId;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportChipcard#setFileName(java.lang.String)
     */
    @Override
    public void setFileName(String filename)
    {
        this.filename = IOUtils.safeFilename(filename);
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportChipcard#getFileName()
     */
    @Override
    public String getFileName()
    {
        return filename;
    }
    
    /**
     * Speichert die Karten-ID.
     * @param cardid
     */
    public void setCardId(String cardid) {
        this.cardid = cardid;
    }
    
    /**
     * Liefert die Karten-ID.
     * @return die Karten-ID.
     */
    public String getCardId() {
        return cardid;
    }
    
    /**
     * Speichert, ob die PIN eingegeben wurde.
     * @param pinEntered true, wenn die PIN eingegeben wurde.
     */
    public void setPINEntered(boolean pinEntered) {
        this.pinEntered = pinEntered;
    }
    
    /**
     * Liefert true, wenn die PIN eingegeben wurde.
     * @return true, wenn die PIN eingegeben wurde.
     */
    public boolean isPINEntered() {
        return pinEntered;
    }
    
    /**
     * Legt fest, ob die PIN per Tastatur eingegeben werden soll.
     * @param useSoftPin true, wenn die PIN per Tastatur eingegeben werden soll.
     */
    public void setUseSoftPin(int useSoftPin) {
        this.useSoftPin = useSoftPin;
    }
    
    /**
     * Prueft, ob die PIN per Tastatur eingegeben werden soll.
     * @return true, wenn die PIN per Tastatur eingegeben werden soll.
     */
    public int getUseSoftPin()
    {
        return useSoftPin;
    }
    
    /**
     * Speichert die ueber die Tastatur eingegebene PIN.
     * @param softPin die ueber die Tastatur eingegebene PIN.
     */
    public void setSoftPin(byte[] softPin) {
        LogFilter.getInstance().addSecretData(new String(softPin), "X", LogFilter.FILTER_SECRETS);
        this.softPin = softPin;
    }
    
    /**
     * Liefert die ueber die Tastatur eingegebene PIN.
     * @return die ueber die Tastatur eingegebene PIN.
     */
    public byte[] getSoftPin() {
        return softPin;
    }
    
    /**
     * Speichert den Index des Bankzugangs.
     * @param entryIdx der Index des Bankzugangs.
     */
    public void setEntryIdx(int entryIdx) {
        this.entryIdx = entryIdx;
    }
    
    /**
     * Liefert den Index des Bankzugangs.
     * @return der Index des Bankzugangs.
     */
    public int getEntryIdx() {
        return entryIdx;
    }
    
    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#setProfileVersion(java.lang.String)
     */
    @Override
    public void setProfileVersion(String version) {
        if (version != null) {
            Integer.parseInt(version);   // check for valid integer value
        }
        this.forcedProfileVersion = version;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getProfileVersion()
     */
    @Override
    public String getProfileVersion() {
        String result = this.forcedProfileVersion;
        
        if (result == null) {
            HBCIUtils.log("no RDH profile version explicity specified - starting autodetection", HBCIUtils.LOG_DEBUG);
            
            /* TODO: do not use the hbci-version stored in the passport, but the
             * hbci version of the current HBCIHandler associated with this passport.
             * This will be easy in HBCI4Java-3, but in HBCI4Java-2 this is an
             * ugly problem - broken by design */
            if (getHBCIVersion().length() != 0 && !getHBCIVersion().startsWith("3")) { // TODO: support FinTS-4, too
                result = "1";
                setProfileVersion(result);
                HBCIUtils.log("this is HBCI version '" + getHBCIVersion() + "', which only supports RDH-1", HBCIUtils.LOG_DEBUG);
            } else {
                HBCIKey key = getMyPublicSigKey();
                if (key != null) {
                    // profil-erkennung anhand schluesselnummer
                    result = key.num;
                    setProfileVersion(result);
                    HBCIUtils.log("using user sig key num '" + result + "' as profile version", HBCIUtils.LOG_DEBUG);
                } else {
                    key = getInstEncKey();
                    if (key != null && (key.num.equals("1") || key.num.equals("2") || key.num.equals("10"))) {
                        // found a sig key with a valid key num - so we use this as the profile version
                        result = key.num;
                        HBCIUtils.log("using inst enc key num '" + result + "' as RDH profile version", HBCIUtils.LOG_DEBUG);
                    } else {
                        // neither user keys nor inst keys present - using highest available profile
                        HBCIUtils.log("no keys found in passport - so we use the highest available profile",HBCIUtils.LOG_DEBUG);

                        // es gibt noch gar keine schlüssel - also nehmen wir die
                        // höchste unterstützte profil-nummer

                        String[][] methods = getSuppSecMethods();
                        int        maxVersion = 0;
                        for (int i = 0; i < methods.length; i++) {
                            String method = methods[i][0];
                            int version = Integer.parseInt(methods[i][1]);
    
                            if (method.equals("RDH") && 
                                    (version == 1 || version == 2 || version == 10)) {
                                // es werden nur RDH-1, RDH-2 und RDH-10 betrachtet, weil
                                // alle anderen rdh-profile nicht für software-lösungen
                                // zugelassen sind
                                if (version > maxVersion) {
                                    maxVersion = version;
                                }
                            }
                        }
    
                        if (maxVersion != 0) {
                            result = Integer.toString(maxVersion);
                            setProfileVersion(result);
                        }
                        HBCIUtils.log(
                            "using RDH profile '" + result + "' taken from supported profiles (BPD)",
                            HBCIUtils.LOG_DEBUG);
                    }
                }
            }
        } else {
            HBCIUtils.log("using forced RDH profile version '" + result + "'", HBCIUtils.LOG_DEBUG);
        }
        
        return result;
    }

    /**
     * Speichert den Schluesel.
     * @param i der Index.
     * @param key der Schluessel.
     */
    private void setKey(int i, HBCIKey key) {
        keys[i] = key;
    }

    /**
     * Liefert den Schluessel.
     * @param i der Index.
     * @return der Schluessel.
     */
    private HBCIKey getKey(int i) {
        return keys[i];
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setInstSigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setInstSigKey(HBCIKey key) {
        setKey(KEY_INST_SIG, key);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setInstEncKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setInstEncKey(HBCIKey key) {
        setKey(KEY_INST_ENC, key);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPublicSigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPublicSigKey(HBCIKey key) {
        setKey(KEY_MY_PUBLIC_SIG, key);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPrivateSigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPrivateSigKey(HBCIKey key) {
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPublicEncKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPublicEncKey(HBCIKey key) {
        setKey(KEY_MY_PUBLIC_ENC, key);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPrivateEncKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPrivateEncKey(HBCIKey key) {
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPublicDigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPublicDigKey(HBCIKey key) {
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#setMyPrivateDigKey(org.kapott.hbci.manager.HBCIKey)
     */
    @Override
    public void setMyPrivateDigKey(HBCIKey key) {
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstSigKeyName()
     */
    @Override
    public String getInstSigKeyName() {
        return getInstSigKey() != null ? getInstSigKey().userid : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstSigKeyNum()
     */
    @Override
    public String getInstSigKeyNum() {
        return getInstSigKey() != null ? getInstSigKey().num : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstSigKeyVersion()
     */
    @Override
    public String getInstSigKeyVersion() {
        return getInstSigKey() != null ? getInstSigKey().version : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstEncKeyName()
     */
    @Override
    public String getInstEncKeyName() {
        return getInstEncKey() != null ? getInstEncKey().userid : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstEncKeyNum()
     */
    @Override
    public String getInstEncKeyNum() {
        return getInstEncKey() != null ? getInstEncKey().num : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getInstEncKeyVersion()
     */
    @Override
    public String getInstEncKeyVersion() {
        return getInstEncKey() != null ? getInstEncKey().version : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMySigKeyName()
     */
    @Override
    public String getMySigKeyName() {
        return getMyPublicSigKey() != null ? getMyPublicSigKey().userid : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMySigKeyNum()
     */
    @Override
    public String getMySigKeyNum() {
        return getMyPublicSigKey() != null ? getMyPublicSigKey().num : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMySigKeyVersion()
     */
    @Override
    public String getMySigKeyVersion() {
        return getMyPublicSigKey() != null ? getMyPublicSigKey().version : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMyEncKeyName()
     */
    @Override
    public String getMyEncKeyName() {
        return getMyPublicEncKey() != null ? getMyPublicEncKey().userid : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMyEncKeyNum()
     */
    @Override
    public String getMyEncKeyNum() {
        return getMyPublicEncKey() != null ? getMyPublicEncKey().num : null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getMyEncKeyVersion()
     */
    @Override
    public String getMyEncKeyVersion() {
        return getMyPublicEncKey() != null ? getMyPublicEncKey().version : null;
    }

    /**
     * @see org.kapott.hbci.passport.AbstractRDHPassport#hash(byte[])
     */
    @Override
    public byte[] hash(byte[] data) {
        data = super.hash(data);
        
        SignatureParamSpec sps = getSignatureParamSpec();
        MessageDigest dig;
        try {
            dig = MessageDigest.getInstance(sps.getHashAlg(), sps.getProvider());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        return dig.digest(data);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#sign(byte[])
     */
    @Override
    public byte[] sign(byte[] data) {
        checkPIN();
        return ctSign(data);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#verify(byte[], byte[])
     */
    @Override
    public boolean verify(byte[] data, byte[] sig) {
        checkPIN();
        return ctVerify(data, sig);
    }

    /**
     * Verschluesselt die Nachricht.
     * @param plainMsg die Nachricht.
     * @param msgkey der Schluessel.
     * @return die verschluesselte Nachricht.
     */
    private byte[] encryptMessage(byte[] plainMsg, SecretKey msgkey) {
        try {
        	final String provider = CryptUtils.getSecurityProvider();
        	Cipher cipher = provider == null ? Cipher.getInstance("DESede/CBC/NoPadding") : Cipher.getInstance("DESede/CBC/NoPadding", provider);
            byte[] iv = new byte[8];
            Arrays.fill(iv, (byte) 0);
            IvParameterSpec spec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, msgkey, spec);

            return cipher.doFinal(plainMsg);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** can not encrypt message", ex);
        }
    }
    
    /**
     * Verschluesselt den Key.
     * @param msgkey der Key.
     * @return der verschluesselte Key.
     */
    private byte[] encryptKey(SecretKey msgkey) {
        try {
            // schluessel als byte-array abspeichern
        	final String provider = CryptUtils.getSecurityProvider();
        	SecretKeyFactory factory = provider==null ? SecretKeyFactory.getInstance("DESede") : SecretKeyFactory.getInstance("DESede", provider);
            DESedeKeySpec spec=(DESedeKeySpec)(factory.getKeySpec(msgkey,DESedeKeySpec.class));
            byte[] plainKey=spec.getKey(); // plainKey ist der DESede-Key

            // abhängig von der Länge des inst-enc-keys
            int    cryptDataSize=getCryptDataSize(getInstEncKey().key);
            byte[] plainText=new byte[cryptDataSize];
            Arrays.fill(plainText,(byte)(0));
            System.arraycopy(plainKey,0,plainText,plainText.length-16,16);
            
            byte[] result = ctEncipher(plainText);
            
            result=checkForCryptDataSize(result, cryptDataSize);
            return result;
        } catch (Exception ex) {
            throw new HBCI_Exception("*** can not encrypt message key", ex);
        }
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#encrypt(byte[])
     */
    @Override
    public byte[][] encrypt(byte[] plainMsg) {
        try {
            SecretKey msgkey = createMsgKey();
            byte[] cryptMsg = encryptMessage(plainMsg, msgkey);
            byte[] cryptKey = encryptKey(msgkey);

            byte[][] ret = new byte[2][];
            ret[0] = cryptKey;
            ret[1] = cryptMsg;

            return ret;
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while encrypting", ex);
        }
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#decrypt(byte[], byte[])
     */
    @Override
    public byte[] decrypt(byte[] cryptedKey, byte[] cryptedMsg) {
        try {
            // key entschluesseln
            byte[] plainKey = ctDecipher(cryptedKey);

            byte[] realPlainKey=new byte[24];
            System.arraycopy(plainKey,plainKey.length-16,realPlainKey,0,16);
            System.arraycopy(plainKey,plainKey.length-16,realPlainKey,16,8);

            DESedeKeySpec spec=new DESedeKeySpec(realPlainKey);
        	final String provider = CryptUtils.getSecurityProvider();
        	SecretKeyFactory fac = provider==null ? SecretKeyFactory.getInstance("DESede") : SecretKeyFactory.getInstance("DESede", provider);
            SecretKey key=fac.generateSecret(spec);

            // nachricht entschluesseln
        	Cipher cipher = provider == null ? Cipher.getInstance("DESede/CBC/NoPadding") : Cipher.getInstance("DESede/CBC/NoPadding", provider);
            byte[] ivarray=new byte[8];
            Arrays.fill(ivarray,(byte)(0));
            IvParameterSpec iv=new IvParameterSpec(ivarray);
            cipher.init(Cipher.DECRYPT_MODE,key,iv);
            return cipher.doFinal(cryptedMsg);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while decrypting message",ex);
        }
    }
    
    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#close()
     */
    @Override
    public void close() {
        super.close();
        
        resetPassphrase();
        setPINEntered(false);
        closeCT();
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportChipcard#saveBankData()
     */
    @Override
    public void saveBankData()
    {
        try {
            checkPIN();
            ctSaveBankData();
        }
        catch (Exception e)
        {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_INSTSAVEERR"),e);
        }
    }

    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasInstSigKey()
     */
    @Override
    public void resetPassphrase() {
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasInstSigKey()
     */
    @Override
    public boolean hasInstSigKey() {
        return getInstSigKey() != null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasInstEncKey()
     */
    @Override
    public boolean hasInstEncKey() {
        return getInstEncKey() != null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasMySigKey()
     */
    @Override
    public boolean hasMySigKey() {
        return getMyPublicSigKey() != null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#hasMyEncKey()
     */
    @Override
    public boolean hasMyEncKey() {
        return getMyPublicEncKey() != null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPublicSigKey()
     */
    @Override
    public HBCIKey getMyPublicSigKey() {
        return getKey(KEY_MY_PUBLIC_SIG);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPublicEncKey()
     */
    @Override
    public HBCIKey getMyPublicEncKey() {
        return getKey(KEY_MY_PUBLIC_ENC);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPublicDigKey()
     */
    @Override
    public HBCIKey getMyPublicDigKey() {
        return null;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPrivateSigKey()
     */
    @Override
    public HBCIKey getMyPrivateSigKey() {
        return getMyPublicSigKey();
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPrivateEncKey()
     */
    @Override
    public HBCIKey getMyPrivateEncKey() {
        return getMyPublicEncKey();
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getMyPrivateDigKey()
     */
    @Override
    public HBCIKey getMyPrivateDigKey() {
        return getMyPublicDigKey();
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getInstSigKey()
     */
    @Override
    public HBCIKey getInstSigKey() {
        return getKey(KEY_INST_SIG);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#getInstEncKey()
     */
    @Override
    public HBCIKey getInstEncKey() {
        return getKey(KEY_INST_ENC);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#saveChanges()
     */
    @Override
    public void saveChanges() {
        try {
            this.checkPIN();
            this.ctSaveBankData();
            this.ctSaveSigId();

            final PassportData data = new PassportData();
            data.bpd          = this.getBPD();
            data.upd          = this.getUPD();
            data.hbciVersion  = this.getHBCIVersion();
            data.sysId        = this.getSysId();
            data.customerId   = this.getCustomerId();
            
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
     * Liest die Bank-Daten.
     */
    public void readBankData() {
        try {
            checkPIN();
            ctReadBankData();
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_INSTSAVEERR"),e);
        }
    }
    
    /**
     * Liest die Schluesseldaten.
     */
    public void readKeyData() {
        try {
            checkPIN();
            ctReadKeyData();
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_INSTSAVEERR"),e);
        }
    }
    
    /**
     * Prueft die PIN.
     */
    private void checkPIN() {
        try {
            if (!isPINEntered()) {
                if (getUseSoftPin() == 1) {
                    String pin = HBCIUtils.getParam(getParamHeader() + ".pin");
                    
                    if (pin == null || pin.length() == 0) {
                        StringBuffer temppin = new StringBuffer();
                        HBCIUtilsInternal.getCallback().callback(this,
                                                         HBCICallback.NEED_SOFTPIN,
                                                         HBCIUtilsInternal.getLocMsg("CALLB_NEED_SOFTPIN"),
                                                         HBCICallback.ResponseType.TYPE_SECRET,
                                                         temppin);
                        if (temppin.length() == 0)
                            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PINZERO"));
                        pin = temppin.toString();
                        LogFilter.getInstance().addSecretData(pin, "X", LogFilter.FILTER_SECRETS);
                    }
                    
                    setSoftPin(pin.getBytes("ISO-8859-1"));
                } else {
                    HBCIUtilsInternal.getCallback().callback(this,
                                                     HBCICallback.NEED_HARDPIN,
                                                     HBCIUtilsInternal.getLocMsg("CALLB_NEED_HARDPIN"),
                                                     HBCICallback.ResponseType.TYPE_NONE,
                                                     null);
                }

                try {
                    ctEnterPIN();
                    setPINEntered(true);
                } catch (Exception e) {
                    HBCIUtils.setParam(getParamHeader() + ".pin", null);
                    setSoftPin(new byte[0]);
                } finally {
                    if (getUseSoftPin() != 1) {
                        HBCIUtilsInternal.getCallback().callback(this,
                                                         HBCICallback.HAVE_HARDPIN,
                                                         null,
                                                         HBCICallback.ResponseType.TYPE_NONE,
                                                         null);
                    }
                }
            }
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PINERR"), e);
        }
    }
    
    /**
     * Initialisiert die Karte.
     */
    protected void initCT()
    {
      this.cardService = SmartCardService.createInstance(RSACardService.class,HBCIUtils.getParam(getParamHeader() + ".pcsc.name", null));
      this.setCID(this.cardService.getCID());
      this.setCardId(this.cardService.getCardId());
    }
    
    /**
     * Fuehrt die PIN-Eingabe durch.
     */
    protected void ctEnterPIN()
    {
        if (getUseSoftPin() == 1)
            cardService.verifySoftPIN(0x10, this.getSoftPin());
        else
            cardService.verifyHardPIN(0x10);
    }
    
    /**
     * Liest die Bankdaten.
     */
    protected void ctReadBankData()
    {
        int idx = getEntryIdx() - 1;
        
        RSABankData bankData = cardService.readBankData(idx);
        
        setBLZ(bankData.getBankCode());
        setCountry(SyntaxCtr.getName(bankData.getCountry()));
        setHost(bankData.getComAddress());
        setUserId(bankData.getUserId());
        setBankId(bankData.getBankId());
        // this could be stored on the chipcard - use it for first initialization
        setSysId(bankData.getSystemId());
        setDefaultCustomerId(bankData.getCustomerId());
    }
    
    /**
     * Speichert die Bankdaten.
     */
    protected void ctSaveBankData() {
        int idx = getEntryIdx() - 1;
        
        RSABankData bankData = cardService.readBankData(idx);
        
        bankData.setCountry(SyntaxCtr.getCode(getCountry()));
        bankData.setBankCode(getBLZ());
        bankData.setComAddress(getHost());
        bankData.setUserId(getUserId());
        bankData.setBankId(getBankId());
        // this could be stored on the chipcard, but we use the file
        // bankData.setSystemId("0".equals(getSysId()) ? "" : getSysId());
        
        cardService.writeBankData(idx, bankData);
    }
    
    /**
     * Liest die Schluesseldaten.
     */
    protected void ctReadKeyData() {
        int idx = getEntryIdx() - 1;
        
        setSigId(new Long(cardService.readSigId(idx)));
        
        // readKeyData
        RSAKeyData[] keyData = cardService.readKeyData(idx);
        
        if (keyData[0].getStatus() == 0x10 && keyData[0].getKeyType() == 0x53) {
            HBCIUtils.log("found valid instSigKey ", HBCIUtils.LOG_DEBUG);
            setInstSigKey(new HBCIKey(
                            getCountry(), getBLZ(), getBankId(), 
                            Integer.toString(keyData[0].getKeyNum()), Integer.toString(keyData[0].getKeyVersion()), 
                            keyData[0].getPublicKey()));
        }
        
        if (keyData[1].getStatus() == 0x10 && keyData[1].getKeyType() == 0x56) {
            HBCIUtils.log("found valid instEncKey ", HBCIUtils.LOG_DEBUG);
            setInstEncKey(new HBCIKey(
                            getCountry(), getBLZ(), getBankId(), 
                            Integer.toString(keyData[1].getKeyNum()), Integer.toString(keyData[1].getKeyVersion()),
                            keyData[1].getPublicKey()));
        }
        
        if (keyData[2].getStatus() == 0x10 && keyData[2].getKeyType() == 0x53) {
            HBCIUtils.log("found valid myPublicSigKey ", HBCIUtils.LOG_DEBUG);
            setMyPublicSigKey(new HBCIKey(
                            getCountry(), getBLZ(), getUserId(), 
                            Integer.toString(keyData[2].getKeyNum()), Integer.toString(keyData[2].getKeyVersion()),
                            keyData[2].getPublicKey()));
        }
        
        if (keyData[3].getStatus() == 0x10 && keyData[3].getKeyType() == 0x56) {
            HBCIUtils.log("found valid myPublicEncKey ", HBCIUtils.LOG_DEBUG);
            setMyPublicEncKey(new HBCIKey(
                            getCountry(), getBLZ(), getUserId(), 
                            Integer.toString(keyData[3].getKeyNum()), Integer.toString(keyData[3].getKeyVersion()),
                            keyData[3].getPublicKey()));
        }
    }
    
    /**
     * Speichert die Signatur-ID.
     */
    protected void ctSaveSigId() {
        int idx = getEntryIdx() - 1;
        
        cardService.writeSigId(idx, getSigId().intValue());
    }
    
    /**
     * Signiert die Daten.
     * @param data die zu signierenden Daten.
     * @return
     */
    protected byte[] ctSign(byte[] data) {
        int idx = getEntryIdx() - 1;
        
        return cardService.sign(idx, data);
    }
    
    /**
     * Prueft die Signatur.
     * @param data die Daten.
     * @param sig die Signatur.
     * @return true, wenn die Signatur ok ist.
     */
    protected boolean ctVerify(byte[] data, byte[] sig) {
        int idx = getEntryIdx() - 1;
        
        return cardService.verify(idx, data, sig);
    }
    
    /**
     * Verschluesselt die Daten.
     * @param data die Daten.
     * @return die verschluesselten Daten.
     */
    protected byte[] ctEncipher(byte[] data) {
        int idx = getEntryIdx() - 1;
        
        return cardService.encipher(idx, data);
    }
    
    /**
     * Entschluesselt die Daten.
     * @param data die verschluesselten Daten.
     * @return die entschluesselten Daten.
     */
    protected byte[] ctDecipher(byte[] data) {
        int idx = getEntryIdx() - 1;
        
        return cardService.decipher(idx, data);
    }
    
    /**
     * Schliesst den Kartenleser.
     */
    protected void closeCT()
    {
      try
      {
        if (this.cardService != null)
          this.cardService.close();
      }
      finally
      {
        this.cardService = null;
      }
    }

}
