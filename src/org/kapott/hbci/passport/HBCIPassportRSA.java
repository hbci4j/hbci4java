/**
 * 
 */
package org.kapott.hbci.passport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.smartcardio.Card;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactory;

import org.kapott.cryptalgs.SignatureParamSpec;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.datatypes.SyntaxCtr;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidPassphraseException;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.smartcardio.RSABankData;
import org.kapott.hbci.smartcardio.RSACardService;
import org.kapott.hbci.smartcardio.RSAKeyData;

/**
 * HBCI-Passport fuer RDH-Chipkarten.
 */
public class HBCIPassportRSA extends AbstractRDHPassport implements HBCIPassportChipcard
{

    protected final static byte[] CIPHER_SALT={(byte) 0x56, (byte) 0xbc, (byte) 0x1c, (byte) 0x88,
                                               (byte) 0x1f, (byte) 0xe3, (byte) 0x73, (byte) 0xcc};
    protected final static int CIPHER_ITERATIONS=987;
    
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
    private SecretKey passportKey;
    private int entryIdx;
    private String forcedProfileVersion;
    private String bankId;
    private String defaultCustomerId;
    
    private Card smartCard;
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
        
        ObjectInputStream is = null;
        
        try {
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
            HBCIUtilsInternal.getCallback().callback(this, HBCICallback.NEED_CHIPCARD, HBCIUtilsInternal.getLocMsg("CALLB_NEED_CHIPCARD"), HBCICallback.TYPE_NONE, null);
            
            initCT();
            HBCIUtilsInternal.getCallback().callback(this, HBCICallback.HAVE_CHIPCARD, "", HBCICallback.TYPE_NONE, null);
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
            // read passport file
            String path = HBCIUtils.getParam(getParamHeader() + ".path", "./");
            setFileName(HBCIUtilsInternal.withCounter(path + getCardId(), getEntryIdx() - 1));
            HBCIUtils.log("loading passport data from file " + getFileName(), HBCIUtils.LOG_DEBUG);
            
            File file = new File(getFileName());
            
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                HBCIUtils.log("have to create new passport file", HBCIUtils.LOG_WARN);
                askForMissingData(true, true, true, false, false, true, true);
                saveChanges();
            }
            
            int retries = Integer.parseInt(HBCIUtils.getParam("client.retries.passphrase", "3"));
            
            while (true) { // loop for entering the correct passphrase
                if (getPassportKey() == null)
                    setPassportKey(calculatePassportKey(FOR_LOAD));
                
                PBEParameterSpec paramspec = new PBEParameterSpec(CIPHER_SALT, CIPHER_ITERATIONS);
                Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
                cipher.init(Cipher.DECRYPT_MODE, getPassportKey(), paramspec);
                
                try {
                    is = new ObjectInputStream(new CipherInputStream(new FileInputStream(file), cipher));
                } catch (StreamCorruptedException e) {
                    setPassportKey(null); // Passwort resetten
                    retries--;
                    if (retries<=0)
                        throw new InvalidPassphraseException();
                } catch (Exception e) {
                    throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_READERR"), e);
                }
                
                // wir habens
                if (is != null) {
                    setBPD((Properties) is.readObject());
                    setUPD((Properties) is.readObject());
                    setHBCIVersion((String) is.readObject());
                    // this could be stored on the chipcard, but we use the file
                    setSysId((String) is.readObject());
                    setCustomerId((String) is.readObject());
                    break;
                }
            }
            //
            ////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            // Im Fehlerfall wieder schliessen
            try {
                closeCT();
            } catch (Exception ex) {
                HBCIUtils.log(ex);
            }
            
            if (e instanceof HBCI_Exception)
                throw (HBCI_Exception) e;
            
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CTERR"), e);
        } finally {
            // Close Passport-File
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    HBCIUtils.log(e);
                }
            }
        }
    }
    
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
    
    public String getDefaultCustomerId() {
        return defaultCustomerId;
    }

    public void setDefaultCustomerId(String defaultCustomerId) {
        this.defaultCustomerId = defaultCustomerId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
    
    public String getBankId() {
        return bankId;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportChipcard#setFileName(java.lang.String)
     */
    @Override
    public void setFileName(String filename)
    {
        this.filename = filename;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportChipcard#getFileName()
     */
    @Override
    public String getFileName()
    {
        return filename;
    }
    
    public void setCardId(String cardid) {
        this.cardid = cardid;
    }
    
    public String getCardId() {
        return cardid;
    }
    
    public void setPINEntered(boolean pinEntered) {
        this.pinEntered = pinEntered;
    }
    
    public boolean isPINEntered() {
        return pinEntered;
    }
    
    public void setUseSoftPin(int useSoftPin) {
        this.useSoftPin = useSoftPin;
    }
    
    public int getUseSoftPin() {
        return useSoftPin;
    }
    
    public void setSoftPin(byte[] softPin) {
        LogFilter.getInstance().addSecretData(new String(softPin), "X", LogFilter.FILTER_SECRETS);
        this.softPin = softPin;
    }
    
    public byte[] getSoftPin() {
        return softPin;
    }
    
    protected void setPassportKey(SecretKey passportKey) {
        this.passportKey = passportKey;
    }
    
    protected SecretKey getPassportKey() {
        return passportKey;
    }
    
    public void setEntryIdx(int entryIdx) {
        this.entryIdx = entryIdx;
    }
    
    public int getEntryIdx() {
        return entryIdx;
    }
    
    @Override
    public void setProfileVersion(String version) {
        if (version != null) {
            Integer.parseInt(version);   // check for valid integer value
        }
        this.forcedProfileVersion = version;
    }
    
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
                        HBCIUtils.log(
                            "no keys found in passport - so we use the highest available profile",
                            HBCIUtils.LOG_DEBUG);

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

    private void setKey(int i, HBCIKey key) {
        // System.out.println("passportDDV: setting key "+i+" to "+(key==null?"null":key.country+":"+key.blz+":"+key.cid+":"+key.num+":"+key.version));
        keys[i] = key;
    }

    private HBCIKey getKey(int i) {
        return keys[i];
    }

    @Override
    public void setInstSigKey(HBCIKey key) {
        setKey(KEY_INST_SIG, key);
    }

    @Override
    public void setInstEncKey(HBCIKey key) {
        setKey(KEY_INST_ENC, key);
    }

    @Override
    public void setMyPublicSigKey(HBCIKey key) {
        setKey(KEY_MY_PUBLIC_SIG, key);
    }

    @Override
    public void setMyPrivateSigKey(HBCIKey key) {
    }

    @Override
    public void setMyPublicEncKey(HBCIKey key) {
        setKey(KEY_MY_PUBLIC_ENC, key);
    }

    @Override
    public void setMyPrivateEncKey(HBCIKey key) {
    }

    @Override
    public void setMyPublicDigKey(HBCIKey key) {
    }

    @Override
    public void setMyPrivateDigKey(HBCIKey key) {
    }

    @Override
    public String getInstSigKeyName() {
        return getInstSigKey() != null ? getInstSigKey().userid : null;
    }

    @Override
    public String getInstSigKeyNum() {
        return getInstSigKey() != null ? getInstSigKey().num : null;
    }

    @Override
    public String getInstSigKeyVersion() {
        return getInstSigKey() != null ? getInstSigKey().version : null;
    }

    @Override
    public String getInstEncKeyName() {
        return getInstEncKey() != null ? getInstEncKey().userid : null;
    }

    @Override
    public String getInstEncKeyNum() {
        return getInstEncKey() != null ? getInstEncKey().num : null;
    }

    @Override
    public String getInstEncKeyVersion() {
        return getInstEncKey() != null ? getInstEncKey().version : null;
    }

    @Override
    public String getMySigKeyName() {
        return getMyPublicSigKey() != null ? getMyPublicSigKey().userid : null;
    }

    @Override
    public String getMySigKeyNum() {
        return getMyPublicSigKey() != null ? getMyPublicSigKey().num : null;
    }

    @Override
    public String getMySigKeyVersion() {
        return getMyPublicSigKey() != null ? getMyPublicSigKey().version : null;
    }

    @Override
    public String getMyEncKeyName() {
        return getMyPublicEncKey() != null ? getMyPublicEncKey().userid : null;
    }

    @Override
    public String getMyEncKeyNum() {
        return getMyPublicEncKey() != null ? getMyPublicEncKey().num : null;
    }

    @Override
    public String getMyEncKeyVersion() {
        return getMyPublicEncKey() != null ? getMyPublicEncKey().version : null;
    }

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

    @Override
    public byte[] sign(byte[] data) {
        checkPIN();
        return ctSign(data);
    }

    @Override
    public boolean verify(byte[] data, byte[] sig) {
        checkPIN();
        return ctVerify(data, sig);
    }

    private byte[] encryptMessage(byte[] plainMsg, SecretKey msgkey) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
            byte[] iv = new byte[8];
            Arrays.fill(iv, (byte) 0);
            IvParameterSpec spec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, msgkey, spec);

            return cipher.doFinal(plainMsg);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** can not encrypt message", ex);
        }
    }
    
    private byte[] encryptKey(SecretKey msgkey) {
        try {
            // schluessel als byte-array abspeichern
            SecretKeyFactory factory=SecretKeyFactory.getInstance("DESede");
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

    @Override
    public byte[] decrypt(byte[] cryptedKey, byte[] cryptedMsg) {
        try {
            // key entschluesseln
            byte[] plainKey = ctDecipher(cryptedKey);

            byte[] realPlainKey=new byte[24];
            System.arraycopy(plainKey,plainKey.length-16,realPlainKey,0,16);
            System.arraycopy(plainKey,plainKey.length-16,realPlainKey,16,8);

            DESedeKeySpec spec=new DESedeKeySpec(realPlainKey);
            SecretKeyFactory fac=SecretKeyFactory.getInstance("DESede");
            SecretKey key=fac.generateSecret(spec);

            // nachricht entschluesseln
            Cipher cipher=Cipher.getInstance("DESede/CBC/NoPadding");
            byte[] ivarray=new byte[8];
            Arrays.fill(ivarray,(byte)(0));
            IvParameterSpec iv=new IvParameterSpec(ivarray);
            cipher.init(Cipher.DECRYPT_MODE,key,iv);
            return cipher.doFinal(cryptedMsg);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while decrypting message",ex);
        }
    }
    
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
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_INSTSAVEERR"),e);
        }
    }

    
    @Override
    public void resetPassphrase() {
        passportKey = null;
    }

    @Override
    public boolean hasInstSigKey() {
        return getInstSigKey() != null;
    }

    @Override
    public boolean hasInstEncKey() {
        return getInstEncKey() != null;
    }

    @Override
    public boolean hasMySigKey() {
        return getMyPublicSigKey() != null;
    }

    @Override
    public boolean hasMyEncKey() {
        return getMyPublicEncKey() != null;
    }

    @Override
    public HBCIKey getMyPublicSigKey() {
        return getKey(KEY_MY_PUBLIC_SIG);
    }

    @Override
    public HBCIKey getMyPublicEncKey() {
        return getKey(KEY_MY_PUBLIC_ENC);
    }

    @Override
    public HBCIKey getMyPublicDigKey() {
        return null;
    }

    @Override
    public HBCIKey getMyPrivateSigKey() {
        return getMyPublicSigKey();
    }

    @Override
    public HBCIKey getMyPrivateEncKey() {
        return getMyPublicEncKey();
    }

    @Override
    public HBCIKey getMyPrivateDigKey() {
        return getMyPublicDigKey();
    }

    @Override
    public HBCIKey getInstSigKey() {
        return getKey(KEY_INST_SIG);
    }

    @Override
    public HBCIKey getInstEncKey() {
        return getKey(KEY_INST_ENC);
    }

    @Override
    public void saveChanges() {
        try {
            checkPIN();
            ctSaveBankData();
            ctSaveSigId();

            if (passportKey == null)
                passportKey = calculatePassportKey(FOR_SAVE);
            
            File passportfile = new File(getFileName());
            File directory = passportfile.getAbsoluteFile().getParentFile();
            String prefix = passportfile.getName() + "_";
            File tempfile = File.createTempFile(prefix, "", directory);

            PBEParameterSpec paramspec = new PBEParameterSpec(CIPHER_SALT, CIPHER_ITERATIONS);
            Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(Cipher.ENCRYPT_MODE, passportKey, paramspec);
            ObjectOutputStream o = new ObjectOutputStream(new CipherOutputStream(new FileOutputStream(tempfile), cipher));
            
            o.writeObject(getBPD());
            o.writeObject(getUPD());
            o.writeObject(getHBCIVersion());
            // this could be stored on the chipcard, but we use the file
            o.writeObject(getSysId());
            o.writeObject(getCustomerId());
            
            o.close();
            passportfile.delete();
            tempfile.renameTo(passportfile);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_WRITEERR"), e);
        }
    }
    
    public void readBankData() {
        try {
            checkPIN();
            ctReadBankData();
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_INSTSAVEERR"),e);
        }
    }
    
    public void readKeyData() {
        try {
            checkPIN();
            ctReadKeyData();
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_INSTSAVEERR"),e);
        }
    }
    
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
                                                         HBCICallback.TYPE_SECRET,
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
                                                     HBCICallback.TYPE_NONE,
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
                                                         HBCICallback.TYPE_NONE,
                                                         null);
                    }
                }
            }
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PINERR"), e);
        }
    }
    
    protected void initCT() {
        try {
            TerminalFactory terminalFactory = TerminalFactory.getDefault();
            CardTerminals terminals = terminalFactory.terminals();
            if (terminals == null)
                throw new HBCI_Exception("Kein Kartenleser gefunden");
            
            List<CardTerminal> list = terminals.list();
            if (list == null || list.size() == 0)
                throw new HBCI_Exception("Kein Kartenleser gefunden");
            
            HBCIUtils.log("found card terminals:", HBCIUtils.LOG_INFO);
            for (CardTerminal t : list) {
                HBCIUtils.log("  " + t.getName(), HBCIUtils.LOG_INFO);
            }

            CardTerminal terminal = null;

            // Checken, ob der User einen konkreten Kartenleser vorgegeben hat
            String name = HBCIUtils.getParam(getParamHeader() + ".pcsc.name", null);
            if (name != null) {
                HBCIUtils.log("explicit terminal name given, trying to open terminal: " + name, HBCIUtils.LOG_DEBUG);
                terminal = terminals.getTerminal(name);
                if (terminal == null)
                    throw new HBCI_Exception("Kartenleser \"" + name + "\" nicht gefunden");
            } else {
                HBCIUtils.log("open first available card terminal", HBCIUtils.LOG_DEBUG);
                terminal = list.get(0);
            }
            HBCIUtils.log("using card terminal " + terminal.getName(), HBCIUtils.LOG_DEBUG);

            // wait for card
            if (!terminal.waitForCardPresent(60 * 1000L))
              throw new HBCI_Exception("Keine Chipkarte in Kartenleser " + terminal.getName() + " gefunden");

            this.smartCard = terminal.connect("T=1");
            
            this.cardService = new RSACardService();
            HBCIUtils.log(" using: " + this.cardService.getClass().getName(),HBCIUtils.LOG_INFO);
            this.cardService.init(this.smartCard);
            
            // getCID
            byte[] cid = this.cardService.getCID();
            this.setCID(new String(cid, "ISO-8859-1"));
            
            // extract card id
            StringBuffer cardId = new StringBuffer();
            for (int i = 0; i < cid.length; i++) {
                cardId.append((char) (((cid[i] >> 4) & 0x0F) + 0x30));
                cardId.append((char) ((cid[i] & 0x0F) + 0x30));
            }
            this.setCardId(cardId.toString());
        } catch (HBCI_Exception he) {
            throw he;
        } catch (Exception e) {
            throw new HBCI_Exception(e);
        }
    }
    
    protected void ctEnterPIN() {
        if (getUseSoftPin() == 1)
            cardService.verifySoftPIN(0x10, this.getSoftPin());
        else
            cardService.verifyHardPIN(0x10);
    }
    
    protected void ctReadBankData() {
        int idx = getEntryIdx() - 1;
        
        RSABankData bankData = cardService.readBankData(idx);
        
        setCountry(SyntaxCtr.getName(bankData.getCountry()));
        setBLZ(bankData.getBankCode());
        setHost(bankData.getComAddress());
        setUserId(bankData.getUserId());
        setBankId(bankData.getBankId());
        // this could be stored on the chipcard - use it for first initialization
        setSysId(bankData.getSystemId());
        setDefaultCustomerId(bankData.getCustomerId());
    }
    
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
    
    protected void ctSaveSigId() {
        int idx = getEntryIdx() - 1;
        
        cardService.writeSigId(idx, getSigId().intValue());
    }
    
    protected byte[] ctSign(byte[] data) {
        int idx = getEntryIdx() - 1;
        
        return cardService.sign(idx, data);
    }
    
    protected boolean ctVerify(byte[] data, byte[] sig) {
        int idx = getEntryIdx() - 1;
        
        return cardService.verify(idx, data, sig);
    }
    
    protected byte[] ctEncipher(byte[] data) {
        int idx = getEntryIdx() - 1;
        
        return cardService.encipher(idx, data);
    }
    
    protected byte[] ctDecipher(byte[] data) {
        int idx = getEntryIdx() - 1;
        
        return cardService.decipher(idx, data);
    }
    
    protected void closeCT() {
        try {
            if (smartCard!=null)
                smartCard.disconnect(false);
        } catch (HBCI_Exception e1) {
            throw e1;
        } catch (Exception e2) {
            throw new HBCI_Exception(e2);
        }
    }

}
