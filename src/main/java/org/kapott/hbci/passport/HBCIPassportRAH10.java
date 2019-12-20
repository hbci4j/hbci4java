/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport;

import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.kapott.cryptalgs.SignatureParamSpec;
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
import org.kapott.hbci.tools.CryptUtils;

/**
 * Implementierung des Passports fuer RAH10-Schluesseldateien.
 */
public class HBCIPassportRAH10 extends AbstractHBCIPassport implements InitLetterPassport, FileBasedPassport
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
     * @see org.kapott.hbci.passport.FileBasedPassport#getFilename()
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassport#saveChanges()
     */
    public void saveChanges()
    {
        try
        {
          if (this.data == null)
            this.data = new PassportData();
          
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
        return CryptUtils.hash(data,CryptUtils.HASH_ALG_SHA256);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#sign(byte[])
     */
    @Override
    public byte[] sign(byte[] data)
    {
        PrivateKey key = (PrivateKey) this.getMyPrivateSigKey().key;
        byte[] sig = CryptUtils.sign(data,key,CryptUtils.SIGN_ALG_RSA,CryptUtils.HASH_ALG_SHA256);
        
        // Padding sollte bei SHA-256 nicht noetig sein, schadet aber auch nicht  
        return CryptUtils.padLeft(sig,(RSAPublicKey) this.getMyPublicSigKey().key);
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#verify(byte[], byte[])
     */
    @Override
    public boolean verify(byte[] data, byte[] sig)
    {
        PublicKey key = (PublicKey) this.getInstSigKey().key;
        return CryptUtils.verifySignature(data,sig,key,CryptUtils.SIGN_ALG_RSA,CryptUtils.HASH_ALG_SHA256);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#encrypt(byte[])
     */
    @Override
    public byte[][] encrypt(byte[] plainMsg)
    {
      try
      {
        // Einmal-Schluessel fuer die Nachricht erzeugen
        final String provider = CryptUtils.getSecurityProvider();
        KeyGenerator keygen = provider != null ? KeyGenerator.getInstance(CryptUtils.CRYPT_ALG_AES,provider) : KeyGenerator.getInstance(CryptUtils.CRYPT_ALG_AES);
        keygen.init(32);
        final SecretKey msgKey = keygen.generateKey();

        final byte[][] ret = new byte[2][];

        // Nachricht mit dem Einmalschluessel verschluesseln
        ret[0] = encryptKey(msgKey.getEncoded());
        
        // Einmalschluessel mit dem Instituts-Schluessel verschluesseln
        ret[1] = encryptMessage(plainMsg,msgKey);
        
        return ret;
      }
      catch (HBCI_Exception e)
      {
        throw e;
      }
      catch (Exception e2)
      {
        throw new HBCI_Exception(e2);
      }
    }

  /**
   * Verschluesselt die Nachricht.
   * @param plainMsg die zu verschluesselnde Nachricht.
   * @param msgkey der Secret-Key.
   * @return die verschluesselte Nachricht.
   */
  private byte[] encryptMessage(byte[] plainMsg, SecretKey msgkey)
  {
    try
    {
      final String provider = CryptUtils.getSecurityProvider();
      Cipher cipher = provider == null ? Cipher.getInstance(CryptUtils.CRYPT_ALG_AES_CBC) : Cipher.getInstance(CryptUtils.CRYPT_ALG_AES_CBC, provider);
      
      // IV muss 0 sein, weil wir den ja sonst mit senden muessten
      final byte[] iv = new byte[16];
      Arrays.fill(iv,(byte)(0));
      final IvParameterSpec spec = new IvParameterSpec(iv);
      cipher.init(Cipher.ENCRYPT_MODE,msgkey,spec);
      byte[] result = cipher.doFinal(plainMsg);
      return result;
    }
    catch (HBCI_Exception e)
    {
      throw e;
    }
    catch (Exception ex) 
    {
      throw new HBCI_Exception(ex);
    }
  }
  
  /**
   * Verschluesselt den Message-Key mit dem Institutsschluessel.
   * @param plainKey der Message-Key.
   * @return der verschluesselte Message-Key.
   */
  private byte[] encryptKey(byte[] plainKey)
  {
    try
    {
//      final RSAPublicKey key = (RSAPublicKey) getInstEncKey().key;
//      final String provider = CryptUtils.getSecurityProvider();
//      final Cipher cipher = provider == null ? Cipher.getInstance(CryptUtils.CRYPT_ALG_RSA) : Cipher.getInstance(CryptUtils.CRYPT_ALG_RSA, provider);
//      cipher.init(Cipher.ENCRYPT_MODE,key);
//      
//      final byte[] plainText = CryptUtils.padLeft(plainKey,(RSAPublicKey) getInstEncKey().key);
//      return cipher.doFinal(plainText);
      
      final RSAPublicKey key = (RSAPublicKey) getInstEncKey().key;

      int tSize = CryptUtils.getCryptDataSize(key);
      final byte[] plainText = CryptUtils.padLeft(plainKey,tSize);
      
      final BigInteger m = new BigInteger(+1,plainText);
      final BigInteger c = m.modPow(key.getPublicExponent(),key.getModulus());

      byte[] result = c.toByteArray();
      return checkForCryptDataSize(result, tSize);
    }
    catch (HBCI_Exception e)
    {
      throw e;
    }
    catch (Exception ex) 
    {
      throw new HBCI_Exception(ex);
    }
  }
  
  /**
   * @see org.kapott.hbci.passport.HBCIPassportInternal#decrypt(byte[], byte[])
   */
  @Override
  public byte[] decrypt(byte[] cryptedKey, byte[] encryptedMsg)
  {
    try
    {
      final String provider = CryptUtils.getSecurityProvider();
      
      HBCIUtils.log("decrypting message key", HBCIUtils.LOG_DEBUG);
      final RSAPrivateKey key = (RSAPrivateKey) this.getMyPrivateEncKey().key;
      
      BigInteger e = key.getPrivateExponent();
      BigInteger m = key.getModulus();
      BigInteger c = new BigInteger(+1,cryptedKey);
      final byte[] plainKey = c.modPow(e,m).toByteArray();

      HBCIUtils.log("decrypting message", HBCIUtils.LOG_DEBUG);
      final SecretKey msgKey = new SecretKeySpec(plainKey,CryptUtils.CRYPT_ALG_AES);
      Cipher cm = provider == null ? Cipher.getInstance(CryptUtils.CRYPT_ALG_AES_CBC) : Cipher.getInstance(CryptUtils.CRYPT_ALG_AES_CBC, provider);
      
      // IV muss 0 sein, weil wir den ja sonst mit senden muessten
      byte[] iv = new byte[16];
      Arrays.fill(iv,(byte)(0));
      final IvParameterSpec spec = new IvParameterSpec(iv);
      
      cm.init(Cipher.DECRYPT_MODE,msgKey,spec);
      
      return cm.doFinal(encryptedMsg);
    }
    catch (HBCI_Exception e)
    {
      throw e;
    }
    catch (Exception ex)
    {
      throw new HBCI_Exception(ex);
    }
  }
  
  /**
   * @see org.kapott.hbci.passport.AbstractHBCIPassport#generateNewUserKeys()
   */
  @Override
  public HBCIKey[][] generateNewUserKeys()
  {
    // Kopiert von AbstractRDHSWPassport
    HBCIKey[] newSigKey = new HBCIKey[2];
    HBCIKey[] newEncKey = new HBCIKey[2];
    try
    {
      HBCIUtils.log("Erzeuge neue Benutzerschlüssel", HBCIUtils.LOG_INFO);

      final String profileVersion = this.getProfileVersion();

      final String num = this.hasMySigKey() ? this.getMyPublicSigKey().num : profileVersion;
      String version = hasMySigKey() ? getMyPublicSigKey().version : "0";
      version = Integer.toString(Integer.parseInt(version) + 1);

      int keySize = 4096;

      HBCIKey k = this.getInstSigKey();
      if (k == null)
        k = getInstEncKey();
      if (k != null)
      {
        RSAPublicKey pkey = (RSAPublicKey) k.key;
        keySize = pkey.getModulus().bitLength();
      }

      final String blz = this.getBLZ();
      final String country = this.getCountry();
      final String userid = this.getUserId();
      
      for (int i=0;i<2;++i)
      {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(CryptUtils.CRYPT_ALG_RSA);
        keygen.initialize(keySize);
        KeyPair pair = keygen.generateKeyPair();

        if (i == 0)
        {
          newSigKey[0] = new HBCIKey(country, blz, userid, num, version, pair.getPublic());
          newSigKey[1] = new HBCIKey(country, blz, userid, num, version, pair.getPrivate());
        } else
        {
          newEncKey[0] = new HBCIKey(country, blz, userid, num, version, pair.getPublic());
          newEncKey[1] = new HBCIKey(country, blz, userid, num, version, pair.getPrivate());
        }
      }
    } catch (Exception ex)
    {
      throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_GENKEYS_ERR"), ex);
    }
    
    final HBCIKey[][] ret = new HBCIKey[2][];
    ret[0]=newSigKey;
    ret[1]=newEncKey;
    
    return ret;
  }
  
  /**
   * @see org.kapott.hbci.passport.InitLetterPassport#getSignatureParamSpec()
   */
  @Override
  public SignatureParamSpec getSignatureParamSpec()
  {
    try
    {
      return new SignatureParamSpec(CryptUtils.HASH_ALG_SHA256,null);
    }
    catch (Exception e)
    {
      throw new HBCI_Exception(e);
    }
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
