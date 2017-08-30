
/*  $Id: RDHXFile.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.passport.rdhXfile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.kapott.cryptalgs.PBKDF2;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidPassphraseException;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.rdhXfile.HBCIAccount.UserKeys;

public class RDHXFile
{
    private List<TLV>   fields;
    private byte[] passphrase;
    
    public RDHXFile(byte[] passphrase)
    {
        this.fields=new ArrayList<TLV>();
        this.passphrase=passphrase;
    }
    
    public RDHXFile(byte[] data, byte[] passphrase)
    {
        this(passphrase);
        
        // alle felder extrahieren und zu filecontent-content hinzufügen
        int posi=0;
        int len=data.length;
        while (posi<len) {
            TLV  tlv=new TLV(data,posi);
            long tag=tlv.getTag();
            
            if (tag==0x4e56L) {
                //   diskhead (tag=0x564e, len=26|28) (1..1)
                HBCIUtils.log("found diskhead field",HBCIUtils.LOG_INTERN);
                tlv=new FileHeader(tlv);
                
            } else if (tag==0x564bL) {
                //   bankdata (tag=0x4b56, len=..) (0..n)
                HBCIUtils.log("found hbciaccount field",HBCIUtils.LOG_INTERN);
                tlv=new HBCIAccount(tlv);
                
            } else if (tag==0x53d6L) {
                //   bankkeys (tag=0xd653, len=..) (0..n)
                HBCIUtils.log("found bankkeys field",HBCIUtils.LOG_INTERN);
                tlv=new BankKeys(tlv);
                
            } else if (tag==0x4452L) {
                //   datum (tag=0x5244, len=14) (1..1)
                HBCIUtils.log("found date field",HBCIUtils.LOG_INTERN);
                tlv=new DateField(tlv);
                
            } else if (tag==0x4d44L) {
                //   mac (tag=0x444d, len=20) (1..1)
                HBCIUtils.log("found mac field",HBCIUtils.LOG_INTERN);
                tlv=new MACField(tlv);
                
            } else {
                throw new HBCI_Exception("*** invalid field tag found: 0x"+Long.toString(tlv.getTag(),16));
            }
            
            addField(tlv);
            posi+=4+tlv.getLength();
        }
        
        if (getField(FileHeader.class)==null) {
            // ohne header-field geht gar nichts 
            throw new HBCI_Exception("*** RDH-2/10-file does not contain a header field - aborting");
        }
        
        MACField macfield=(MACField)getField(MACField.class);
        if (macfield!=null) {
            byte[]  storedMac=macfield.getMac();
            byte[]  calculatedMac=calculateMAC();
            boolean macOK=Arrays.equals(storedMac,calculatedMac);
            HBCIUtils.log("MAC field ok: "+macOK,HBCIUtils.LOG_DEBUG);
            if (!macOK) {
                throw new InvalidPassphraseException();
            }
        } else {
            HBCIUtils.log("RDH-2/10-file does not contain a MAC field - ignoring this for now",HBCIUtils.LOG_ERR);
        }

        // decrypt private user keys
        try {
        	// calculate decryption key
                FileHeader       fileHeader=(FileHeader)getField(FileHeader.class);
                String           algname=(fileHeader.getProfileVersion()==2)?"HmacSHA1":"HmacSHA256";
        	byte[]           derivedKey=deriveKey(24, algname);
        	String provider = HBCIUtils.getParam("kernel.security.provider");
        	SecretKeyFactory keyfac = provider==null ? SecretKeyFactory.getInstance("DESede") : SecretKeyFactory.getInstance("DESede", provider);
        	DESedeKeySpec    desKeyspec=new DESedeKeySpec(derivedKey);
        	SecretKey        key=keyfac.generateSecret(desKeyspec);

        	// loop through all userkeys to decrypt them
        	TLV[] accounts=getFields(HBCIAccount.class);
        	for (int i=0;i<accounts.length;i++) {
        		HBCIAccount account=(HBCIAccount)accounts[i];
        		List<UserKeys>        userkeys=account.getUserKeys();

        		for (Iterator<UserKeys> j=userkeys.iterator();j.hasNext();) {
        			HBCIAccount.UserKeys userkey= j.next();
        			userkey.decrypt(key);
        			HBCIUtils.log(userkey.toString(),HBCIUtils.LOG_INTERN);
        		}
        	}
        } catch (Exception e) {
        	throw new HBCI_Exception(e);
        }
    }
    
    public byte[] getPassphrase()
    {
    	return this.passphrase;
    }
    
    public void setPassphrase(byte[] passphrase) 
    {
    	this.passphrase = passphrase;
    }
    
    public void addField(TLV field)
    {
        this.fields.add(field);
    }
    
    public TLV getField(Class cl)
    {
        TLV ret=null;
        
        for (Iterator<TLV> i=fields.iterator();i.hasNext();) {
            TLV tlv= i.next();
            if (tlv.getClass().equals(cl)) {
                ret=tlv;
                break;
            }
        }
        
        return ret;
    }

    public TLV[] getFields(Class cl)
    {
        List<TLV> ret=new ArrayList<TLV>();
        
        for (Iterator<TLV> i=fields.iterator();i.hasNext();) {
            TLV tlv= i.next();
            if (tlv.getClass().equals(cl)) {
                ret.add(tlv);
            }
        }
        
        return ret.toArray(new TLV[ret.size()]);
    }

    public byte[] getFileData(int profileVersion)
    {
        // update DateField
        DateField dateField=(DateField)getField(DateField.class);
        if (dateField==null) {
            dateField=new DateField();
            addField(dateField);
        }
        dateField.setDate(new Date());
        
        // check for existance of required fields
        // HeaderField
        FileHeader headerField=(FileHeader)getField(FileHeader.class);
        if (headerField==null) {
            headerField=new FileHeader();
            addField(headerField);
            
            headerField.setNofIterations(10000);
            headerField.setRandomSalt();
            headerField.setVersion(1);
        }
        headerField.setProfileVersion(profileVersion);
        
        // MACField
        MACField macField=(MACField)getField(MACField.class);
        if (macField==null) {
            macField=new MACField();
            addField(macField);
        }
        
        // encrypt private user keys
        try {
                // calculate encryption key
                FileHeader       fileHeader=(FileHeader)getField(FileHeader.class);
                String           algname=(fileHeader.getProfileVersion()==2)?"HmacSHA1":"HmacSHA256";
                byte[]           derivedKey=deriveKey(24, algname);
            	String provider = HBCIUtils.getParam("kernel.security.provider");
            	SecretKeyFactory keyfac = provider==null ? SecretKeyFactory.getInstance("DESede") : SecretKeyFactory.getInstance("DESede", provider);
                DESedeKeySpec    desKeyspec=new DESedeKeySpec(derivedKey);
                SecretKey        key=keyfac.generateSecret(desKeyspec);

                // loop through all userkeys to decrypt them
                TLV[] accounts=getFields(HBCIAccount.class);
                for (int i=0;i<accounts.length;i++) {
                        HBCIAccount account=(HBCIAccount)accounts[i];
                        List<UserKeys>        userkeys=account.getUserKeys();

                        for (Iterator<UserKeys> j=userkeys.iterator();j.hasNext();) {
                                HBCIAccount.UserKeys userkey= j.next();
                                userkey.encrypt(key);
                        }
                }
        } catch (Exception e) {
                throw new HBCI_Exception(e);
        }

        // reorder fields to standard order
        List<TLV> newFields=new ArrayList<TLV>();
        Class[] order=new Class[] {FileHeader.class, HBCIAccount.class, 
                                   BankKeys.class, DateField.class, 
                                   MACField.class};
        for (int i=0; i<order.length; i++) {
            Class c=order[i];
            TLV[] fields=getFields(c);
            newFields.addAll(Arrays.asList(fields));
        }
        this.fields = newFields;

        // update all rawdata fields
        for (Iterator<TLV> i=this.fields.iterator();i.hasNext();) {
            TLV tlv= i.next();
            tlv.updateData();
        }

        // update mac field
        byte[] mac=calculateMAC();
        MACField macfield=(MACField)getField(MACField.class);
        macfield.setMac(mac);
        
        // collect filedata
        try {
            ByteArrayOutputStream os=new ByteArrayOutputStream();
            
            for (Iterator<TLV> i=this.fields.iterator();i.hasNext();) {
                TLV tlv= i.next();
                os.write(tlv.getRawData());
            }
            
            byte[] ret=os.toByteArray();
            os.close();
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public HBCIKey getBankSigKey(HBCIAccount account)
    {
        return getBankKey(account,"S");
    }
    
    public void setBankSigKey(HBCIAccount account, HBCIKey key)
    {
        setBankKey(account,"S",key);
    }
    
    public HBCIKey getBankEncKey(HBCIAccount account)
    {
        return getBankKey(account,"V");
    }
    
    public void setBankEncKey(HBCIAccount account, HBCIKey key)
    {
        setBankKey(account,"V",key);
    }
    
    private HBCIKey getBankKey(HBCIAccount account,String keytype)
    {
        HBCIKey ret=null;
        String  blz=account.getBLZ();
        String  country=account.getCountry();
        
        TLV[] keyfields=getFields(BankKeys.class);
        for (int i=0;i<keyfields.length;i++) {
            BankKeys bankkeys=(BankKeys)keyfields[i];
            
            if (bankkeys.getCountry().equals(country) && 
                    bankkeys.getBLZ().equals(blz) &&
                    bankkeys.getKeyType().equals(keytype)) 
            {
                ret=bankkeys.getHBCIKey();
                break;
            }
        }
        
        return ret;
    }
    
    private void setBankKey(HBCIAccount account,String keytype,HBCIKey key)
    {
        if (key!=null) {
            String  blz=account.getBLZ();
            String  country=account.getCountry();

            TLV[]   keyfields=getFields(BankKeys.class);
            boolean found=false;
            for (int i=0;i<keyfields.length;i++) {
                BankKeys bankkeys=(BankKeys)keyfields[i];

                if (bankkeys.getCountry().equals(country) && 
                        bankkeys.getBLZ().equals(blz) &&
                        bankkeys.getKeyType().equals(keytype)) 
                {
                    bankkeys.setKey(keytype,key);
                    account.setKeyStatus((byte)(account.getKeyStatus()|0x04));
                    found=true;
                    break;
                }
            }

            if (!found) {
                BankKeys bankkeys=new BankKeys();
                addField(bankkeys);

                bankkeys.setCountry(account.getCountry());
                bankkeys.setBLZ(account.getBLZ());
                bankkeys.setKey(keytype,key);
                account.setKeyStatus((byte)(account.getKeyStatus()|0x04));
            }
        }
    }
    
    private byte[] getHashData()
    {
        try {
            ByteArrayOutputStream os=new ByteArrayOutputStream();
            
            for (Iterator<TLV> i=this.fields.iterator();i.hasNext();) {
                TLV tlv= i.next();
                if (!tlv.getClass().equals(MACField.class)) {
                    tlv.updateData();
                    os.write(tlv.getRawData());
                }
            }
            
            byte[] ret=os.toByteArray();
            os.close();
            return ret;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private byte[] deriveKey(int dkLen, String algname)
    {
    	// key ableiten
        FileHeader diskhead=(FileHeader)getField(FileHeader.class);
        HBCIUtils.log(
            "calculating key with alg "+algname+" and length "+dkLen,
            HBCIUtils.LOG_DEBUG);
        
        byte[] derivedKey=PBKDF2.deriveKey(diskhead.getSalt(),
                                           diskhead.getNofIterations(),
                                           getPassphrase(),
                                           dkLen, algname);
        return derivedKey;
    }
    
    private byte[] calculateMAC()
    {
        try {
            FileHeader header=(FileHeader)getField(FileHeader.class);
            int        pversion=header.getProfileVersion();
            
            int keysize=(pversion==2)?20:32;
            HBCIUtils.log("using "+keysize+"-byte-key for MAC calculation", HBCIUtils.LOG_DEBUG);

            // TODO: HmacSHA256 is available only in Java-6++
            String algname=(pversion==2)?"HmacSHA1":"HmacSHA256";
            HBCIUtils.log("MAC algorithm is "+algname, HBCIUtils.LOG_DEBUG);
            
            byte[] derivedKey=deriveKey(keysize, algname);
            
            SecretKeySpec keyspec=new SecretKeySpec(derivedKey, algname);
            Mac           mac=Mac.getInstance(algname);
            mac.init(keyspec);
            
            byte[] hashdata=getHashData();
            byte[] calculatedMac=mac.doFinal(hashdata);

            return calculatedMac;
        } catch (Exception e) {
            throw new HBCI_Exception(e);
        }
    }
}