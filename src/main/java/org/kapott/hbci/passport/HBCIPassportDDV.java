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

import org.kapott.cryptalgs.CryptAlgs4JavaProvider;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.storage.PassportData;
import org.kapott.hbci.passport.storage.PassportStorage;
import org.kapott.hbci.tools.CryptUtils;
import org.kapott.hbci.tools.IOUtils;

/** <p>Passport-Klasse für Sicherheitsverfahren DDV mit Medium Chipkarte. Bei dieser
    Variante gibt die Bank eine Chipkarte aus, auf der die Zugangsdaten des
    Nutzers für den HBCI-Zugang gespeichert sind. Außerdem befinden sich auf
    der Karte die (symmetrischen) Schlüssel für die Erzeugung der Signaturen
    und für die Verschlüsselung der Nachrichten.</p><p>
    Diese Klasse unterstützt DDV-Chipkarten vom Typ 0 und 1. Auf einer DDV-Chipkarte
    können prinzipiell bis zu fünf HBCI-Zugangsdatensätze (für unterschiedliche
    Banken) gespeichert werden. Diese Klasse ermöglicht die Benutzung eines
    beliebigen dieser Datensätze. Das hat aber in der Praxis kaum Relevanz,
    weil dann alle HBCI-Zugänge die gleichen kryptografischen Schlüssel benutzen
    müssten (es gibt nur ein Schlüsselpaar pro Chipkarte). Für Chipkarten, die
    von Betreibern für HBCI-Testzugängen ausgegeben werden, ist diese Option
    jedoch nützlich, da hier häufig tatsächlich mehrere Zugänge existieren und
    diese Zugangsdaten auf einer einzigen Chipkarte gespeichert werden können.</p><p>
    Prinzipiell benötigt diese Passport-Variante also keine zusätzliche Schlüsseldatei, da
    alle <em>benötigten</em> HBCI-Daten auf der Chipkarte gespeichert sind. Dennoch verwendet
    diese Klasse eine zusätzliche Datei. In dieser werden u.a. die zuletzt empfangenen BPD
    und UPD sowie die zuletzt benutzte HBCI-Version gespeichert, um beim nächsten
    Benutzen dieses HBCI-Zuganges diese Daten nicht erneut abfragen zu müssen. Diese zusätzliche
    Datei wird automatisch angelegt, der Dateiname setzt sich aus einem definierbaren
    Prefix (Pfad) und der Seriennummer der Chipkarte zusammen.</p>*/
public class HBCIPassportDDV extends AbstractDDVPassport
{
    private String      paramHeader;
    
    private String      filename;
    private HBCIKey[]   keys;
    private int         comport;
    private int         ctnumber;
    private String      cardid;
    private boolean     pinEntered;
    private int         useBio;
    private int         useSoftPin;
    private byte[]      softPin;
    private int         entryIdx;

    protected native void initCT();
    protected native void ctReadBankData();
    protected native void ctReadKeyData();
    protected native void ctEnterPIN();
    protected native void ctSaveBankData(); 
    protected native void ctSaveSigId();
    protected native byte[] ctSign(byte[] data);
    protected native byte[][] ctEncrypt();
    protected native byte[] ctDecrypt(byte[] cryptedKey);
    protected native void closeCT();

    /**
     * ct.
     * @param init
     * @param dummy
     */
    public HBCIPassportDDV(Object init,int dummy)
    {
        super(init);
        setParamHeader("client.passport.DDV");
        
        keys=new HBCIKey[2];
        for (int i=0;i<2;i++) {
            keys[i]=null;
        }
    }

    /**
     * ct.
     * @param init
     */
    public HBCIPassportDDV(Object init)
    {
        this(init,0);

        final int comport  = Integer.parseInt(HBCIUtils.getParam(paramHeader+".port","0"));
        final int ctnumber = Integer.parseInt(HBCIUtils.getParam(paramHeader+".ctnumber","0"));
        
        final String ddvLib = HBCIUtils.getParam(paramHeader+".libname.ddv");
        if (ddvLib == null)
            throw new NullPointerException(paramHeader+".libname.ddv must not be null");

        this.setComPort(comport);
        this.setCTNumber(ctnumber);
        this.setUseBio(Integer.parseInt(HBCIUtils.getParam(paramHeader+".usebio","-1")));
        this.setUseSoftPin(Integer.parseInt(HBCIUtils.getParam(paramHeader+".softpin","-1")));
        this.setSoftPin(new byte[0]);
        this.setPINEntered(false);
        this.setEntryIdx(Integer.parseInt(HBCIUtils.getParam(paramHeader+".entryidx","1")));
        this.setPort(new Integer(3000));
        this.setFilterType("None");

        HBCIUtils.log("trying to load native DDV library "+ddvLib,HBCIUtils.LOG_DEBUG);
        System.load(ddvLib);

        HBCIUtils.log("using chipcard terminal with port "+comport+" and terminal number "+ctnumber,HBCIUtils.LOG_DEBUG);
        try
        {
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_CHIPCARD,HBCIUtilsInternal.getLocMsg("CALLB_NEED_CHIPCARD"),HBCICallback.ResponseType.TYPE_NONE,null);
            initCT();
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.HAVE_CHIPCARD,"",HBCICallback.ResponseType.TYPE_NONE,null);
            
            this.ctReadBankData();

            // Wenn wir neue Daten haben, speichern wir die gleich auf der Karte
            if (askForMissingData(true,true,true,false,false,true,false))
                this.saveBankData();

            // Schluesseldaten von der Karte laden.
            ctReadKeyData();
            
            // Lokale Passport-Datei laden, wenn sie existiert
            this.setFileName(HBCIUtilsInternal.withCounter(HBCIUtils.getParam(paramHeader+".path","./") + this.getCardId(),getEntryIdx()-1));
            File file = new File(this.getFileName());
            if (file.exists() && file.isFile() && file.canRead())
            {
                PassportData data = PassportStorage.load(this,new File(this.getFileName()));
                this.setBPD(data.bpd);
                this.setUPD(data.upd);
                this.setHBCIVersion(data.hbciVersion);
            }
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
     * @see org.kapott.hbci.passport.HBCIPassportChipcard#getFileName()
     */
    @Override
    public String getFileName() 
    {
        return filename;
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportChipcard#setFileName(java.lang.String)
     */
    @Override
    public void setFileName(String filename) 
    { 
        this.filename = IOUtils.safeFilename(filename);
    }

    public void setComPort(int comport)
    {
        this.comport=comport;
    }

    public void setCTNumber(int ctnumber)
    {
        this.ctnumber=ctnumber;
    }

    /** Gibt zurück, welcher logische Port für die Kommunikation mit der Chipkarte benutzt
        wird. Dieser Wert wird vom CTAPI-Treiber des jeweils verwendeten Chipkartenterminals
        interpretiert. 
        @return Schnittstellennummer, an der der Chipkartenleser angeschlossen ist*/ 
    public int getComPort()
    {
        return comport;
    }

    /** Gibt die logische Nummer zurück, unter der der Chipkartenleser zu verwenden
        ist. Wird nur ein Chipkartenleser verwendet, so kann dieser Wert immer 0 sein.
        Bei gleichzeitiger Verwendung mehrerer Chipkartenleser sollten die einzelnen
        Leser hier unterschiedliche Werte zugewiesen bekommen. Dieser Wert wird vom
        CTAPI-Treiber benutzt, um die Chipkartenleser intern auseinander zu halten.
        @return logische Nummer des Chipkartenlesers */
    public int getCTNumber()
    {
        return ctnumber;
    }

    /** Gibt zurück, ob zur PIN-Eingabe am Chipkartenterminal das Biometric-Interface
        verwendet werden soll. Diese Funktion steht zur Zeit nur für Reiner-SCT-
        Chipkartenterminals zur Verfügung.
        @return <code>1</code>, wenn die Biometrie-Einheit des Chipkartenterminals
        für die PIN-Eingabe benutzt werden soll; <code>0</code>, wenn die Biometrie-Einheit
        nicht benutzt werden soll, oder <code>-1</code>, wenn die Verwendung
        der Biometrie-Einheit automatisch erkannt werden soll.*/
    public int getUseBio()
    {
        return useBio;
    }

    public void setUseBio(int useBio)
    {
        this.useBio=useBio;
    }

    /** Gibt zurück, ob die PIN-Eingabe für die Chipkarte über das Keypad des Chipkartenterminals
        oder über die PC-Tastatur erfolgen soll. Dieser Wert wird benutzt, um die
        PIN-Eingabe sowohl bei Klasse-2-Lesern mit eigener Tastatur wir auch für
        Klasse-1-Leser ohne separate Tastatur zu ermöglichen.
        @return PIN-Eingabe über welche Tastatur
        <ul>
          <li>=0 PIN-Eingabe zwingend über Terminal-Keypad</li>
          <li>=1 PIN-Eingabe zwingend über PC-Tastatur</li>
          <li>=-1 automatische Erkennung, ob bevorzugtes Chipkarten-Terminal-Keypad
              verfügbar ist</li>
        </ul>*/
    public int getUseSoftPin()
    {
        return useSoftPin;
    }

    public void setUseSoftPin(int useSoftPin)
    {
        this.useSoftPin=useSoftPin;
    }

    public byte[] getSoftPin()
    {
        return softPin;
    }

    public void setSoftPin(byte[] softPin)
    {
    	LogFilter.getInstance().addSecretData(new String(softPin),"X",LogFilter.FILTER_SECRETS);
        this.softPin=softPin;
    }
    
    public void setEntryIdx(int idx)
    {
        this.entryIdx=idx;
    }
    
    /** Gibt die Indexnummer des Datensatzes zurück, dessen Inhalt als HBCI-Account-Informationen
        benutzt werden sollen. Auf einer Chipkarte können bis zu fünf Zugangsdatensätze
        gespeichert sein, dieser Wert enthält die Nummer des benutzten Eintrages
        (von 1-5). Normalerweise wird der Eintrag Nummer 1 (welcher auch meist der
        einzige Eintrag ist) verwendet.
        @return Indexnummer des verwendeten Account-Datensatzes */
    public int getEntryIdx()
    {
        return entryIdx;
    }

    public void setCardId(String cardid)
    {
        this.cardid=cardid;
    }

    /** Gibt eine 16-stellige Identifikationsnummer für die verwendete Chipkarte
        zurück
        @return Chipkarten-Identifikationsnummer */
    public String getCardId()
    {
        return cardid;
    }
    
    public boolean isSupported()
    {
        boolean ret=false;
        
        if (getBPD()!=null) {
            String[][] methods=getSuppSecMethods();
            
            for (int i=0;i<methods.length;i++) {
                if (methods[i][0].equals("DDV")) {
                    ret=true;
                    break;
                }
            }
        } else {
            ret=true;
        }
        
        return ret;
    }

    private HBCIKey getKey(int i)
    {
        return keys[i];
    }
    
    public HBCIKey getInstSigKey()
    {
        return getKey(0);
    }

    public String getInstSigKeyName()
    {
        return getInstSigKey()!=null?getInstSigKey().userid:null;
    }

    public String getInstSigKeyNum()
    {
        return getInstSigKey()!=null?getInstSigKey().num:null;
    }

    public String getInstSigKeyVersion()
    {
        return getInstSigKey()!=null?getInstSigKey().version:null;
    }
    
    public HBCIKey getInstEncKey()
    {
        return getKey(1);
    }

    public String getInstEncKeyName()
    {
        return getInstEncKey()!=null?getInstEncKey().userid:null;
    }

    public String getInstEncKeyNum()
    {
        return getInstEncKey()!=null?getInstEncKey().num:null;
    }

    public String getInstEncKeyVersion()
    {
        return getInstEncKey()!=null?getInstEncKey().version:null;
    }
    
    public HBCIKey getMyPublicSigKey()
    {
        return getInstSigKey();
    }
    
    public HBCIKey getMyPublicEncKey()
    {
        return getInstEncKey();
    }

    public HBCIKey getMyPublicDigKey()
    {
        return null;
    }

    public HBCIKey getMyPrivateSigKey()
    {
        return getMyPublicSigKey();
    }
    
    public HBCIKey getMyPrivateEncKey()
    {
        return getMyPublicEncKey();
    }

    public HBCIKey getMyPrivateDigKey()
    {
        return getMyPublicDigKey();
    }

    public String getMySigKeyName()
    {
        return getInstSigKeyName();
    }

    public String getMySigKeyNum()
    {
        return getInstSigKeyNum();
    }

    public String getMySigKeyVersion()
    {
        return getInstSigKeyVersion();
    }

    public String getMyEncKeyName()
    {
        return getInstEncKeyName();
    }

    public String getMyEncKeyNum()
    {
        return getInstEncKeyNum();
    }

    public String getMyEncKeyVersion()
    {
        return getInstEncKeyVersion();
    }

    private void setKey(int i,HBCIKey key)
    {
        // System.out.println("passportDDV: setting key "+i+" to "+(key==null?"null":key.country+":"+key.blz+":"+key.cid+":"+key.num+":"+key.version));
        keys[i]=key;
    }
    
    public void setInstSigKey(HBCIKey key)
    {
        setKey(0,key);
    }

    public void setInstEncKey(HBCIKey key)
    {
        setKey(1,key);
    }

    public void setMyPublicDigKey(HBCIKey key)
    {
    }

    public void setMyPrivateDigKey(HBCIKey key)
    {
    }

    public void setMyPublicSigKey(HBCIKey key)
    {
    }

    public void setMyPrivateSigKey(HBCIKey key)
    {
    }

    public void setMyPublicEncKey(HBCIKey key)
    {
    }

    public void setMyPrivateEncKey(HBCIKey key)
    {
    }

    private void checkPIN()
    {
        try {
            if (!pinEntered) {
                if (useSoftPin==1) {
                    String pin=HBCIUtils.getParam(paramHeader+".pin");

                    if (pin==null || pin.length()==0) {
                        StringBuffer temppin=new StringBuffer();
                        HBCIUtilsInternal.getCallback().callback(this,
                                                         HBCICallback.NEED_SOFTPIN,
                                                         HBCIUtilsInternal.getLocMsg("CALLB_NEED_SOFTPIN"),
                                                         HBCICallback.ResponseType.TYPE_SECRET,
                                                         temppin);
                        if (temppin.length()==0)
                            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PINZERO"));
                        pin=temppin.toString();
                        LogFilter.getInstance().addSecretData(pin,"X",LogFilter.FILTER_SECRETS);
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
                    pinEntered=true;
                } catch (Exception e) {
                    HBCIUtils.setParam(paramHeader+".pin",null);
                    setSoftPin(new byte[0]);
                } finally {
                    if (useSoftPin!=1) {
                        HBCIUtilsInternal.getCallback().callback(this,
                                                         HBCICallback.HAVE_HARDPIN,
                                                         null,
                                                         HBCICallback.ResponseType.TYPE_NONE,
                                                         null);
                    }
                }
            }
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PINERR"),e);
        }
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
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#resetPassphrase()
     */
    public void resetPassphrase()
    {
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#saveChanges()
     */
    public void saveChanges()
    {
        try {
            this.checkPIN();
            this.ctSaveSigId();

            final PassportData data = new PassportData();
            data.bpd          = this.getBPD();
            data.upd          = this.getUPD();
            data.hbciVersion  = this.getHBCIVersion();
            
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
     * @see org.kapott.hbci.passport.HBCIPassportInternal#hash(byte[])
     */
    public byte[] hash(byte[] data)
    {
        /* hash-before-sign creates a RMD-160 hash, which will encrypted by
         * the sign() method later */
        MessageDigest dig;
        try {
            dig = MessageDigest.getInstance(CryptUtils.HASH_ALG_RIPE_MD160,CryptAlgs4JavaProvider.NAME);
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
    public byte[] sign(byte[] data)
    {
        /* data is hash value calculated by the hash() method */
        checkPIN();
        return ctSign(data);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#verify(byte[], byte[])
     */
    public boolean verify(byte[] data,byte[] sig)
    {
        /* data is the hash value calculated by the hash() method */
        checkPIN();
        byte[] correctSig=ctSign(data);
        return Arrays.equals(sig,correctSig);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#encrypt(byte[])
     */
    public byte[][] encrypt(byte[] plainMsg)
    {
        try {
            checkPIN();

            byte[][] msgkeys=ctEncrypt(); // 0=plain,1=encrypted
            byte[] longKey=new byte[24];

            int posi=msgkeys[0].length-16;
            System.arraycopy(msgkeys[0],posi,longKey,0,16);
            System.arraycopy(msgkeys[0],posi,longKey,16,8);

            DESedeKeySpec spec=new DESedeKeySpec(longKey);
        	final String provider = CryptUtils.getSecurityProvider();
        	SecretKeyFactory fac = provider==null ? SecretKeyFactory.getInstance("DESede") : SecretKeyFactory.getInstance("DESede", provider);
            SecretKey key=fac.generateSecret(spec);

            // nachricht verschluesseln
        	Cipher cipher = provider == null ? Cipher.getInstance("DESede/CBC/NoPadding") : Cipher.getInstance("DESede/CBC/NoPadding", provider);
            byte[] ivarray=new byte[8];
            Arrays.fill(ivarray,(byte)(0));
            IvParameterSpec iv=new IvParameterSpec(ivarray);
            cipher.init(Cipher.ENCRYPT_MODE,key,iv);
            byte[] cryptedMsg=cipher.doFinal(plainMsg);

            byte[][] ret=new byte[2][];
            ret[0]=msgkeys[1];
            ret[1]=cryptedMsg;
            return ret;
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANTCRYPT"),ex);
        }
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#decrypt(byte[], byte[])
     */
    public byte[] decrypt(byte[] cryptedKey,byte[] cryptedMsg)
    {
        try {
            checkPIN();

            byte[] plainKey=ctDecrypt(cryptedKey);
            byte[] longKey=new byte[24];

            int posi=plainKey.length-16;
            System.arraycopy(plainKey,posi,longKey,0,16);
            System.arraycopy(plainKey,posi,longKey,16,8);

            DESedeKeySpec spec=new DESedeKeySpec(longKey);
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
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_DECRYPTERR"),ex);
        }
    }

    /**
     * @param pinEntered
     */
    public void setPINEntered(boolean pinEntered)
    {
        this.pinEntered=pinEntered;
    }

    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#close()
     */
    public void close()
    {
        super.close();
        
        setPINEntered(false);
        closeCT();
    }

    /** Gibt den Dateinamen der verwendeten CTAPI-Treiberbibliothek zurück.
        @return Dateiname der CTAPI-Bibliothek */
    public String getLibName()
    {
        return HBCIUtils.getParam(paramHeader+".libname.ctapi");
    }
    
    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#setParamHeader(java.lang.String)
     */
    protected void setParamHeader(String p)
    {
        this.paramHeader=p;
    }
    
    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#getParamHeader()
     */
    protected String getParamHeader()
    {
        return this.paramHeader;
    }
}
