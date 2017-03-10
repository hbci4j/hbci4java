
/*  $Id: HBCIAccount.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.kapott.cryptalgs.RSAPrivateCrtKey2;
import org.kapott.hbci.datatypes.SyntaxCtr;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;


public class HBCIAccount
    extends TLV
{
    // bankdaten
    public static class BankData
        extends TLV
    {
        private String countrycode;
        private String blz;
        private String name;
        private String userid;
        private String customerid;
        private String sysid;
        private int    commtype;
        private String commaddr;
        private long   sigid;
        
        // bit 0: need_first_send_keys
        // bit 1: use iso-9796+Ann.A
        // bit 2: inst-key validiert
        // bit 3: need_send_changed_key (enckey)
        // bit 4: need_send_changed_key (sigkey)
        // bit 5: key-locked
        // bit 6: error while send_keys
        // bit 7: 0
        private byte   keystatus;
        
        public BankData()
        {
            super(0x444b);
        }
        
        public BankData(TLV tlv)
        {
            super(tlv);
            byte[] data=this.getData();
            
            this.countrycode=new String(data,0,3).trim();
            this.blz=new String(data,3,30).trim();
            this.name=new String(data,33,60).trim();
            this.userid=new String(data,93,30).trim();
            this.customerid=new String(data,123,30).trim();
            this.sysid=new String(data,153,30).trim();
            this.commtype=data[183];
            this.commaddr=new String(data,184,50).trim();
            this.sigid=((data[235]&0xFFL)<<8) | (data[234]&0xFFL);
            this.keystatus=data[236];
        }
        
        public String getCountry()
        {
            return new SyntaxCtr(new StringBuffer(countrycode),1,0).toString();
        }
        
        public void setCountry(String country)
        {
            this.countrycode=new SyntaxCtr(country,1,0).toString(0);
        }
        
        public String getBLZ()
        {
            return this.blz;
        }
        
        public void setBLZ(String blz)
        {
            this.blz=blz;
        }
        
        public String getUserId()
        {
            return this.userid;
        }
        
        public void setUserId(String userid)
        {
            this.userid=userid;
        }
        
        public String getCustomerId()
        {
            return this.customerid;
        }
        
        public void setCustomerId(String customerid)
        {
            this.customerid=customerid;
        }
        
        public String getHost()
        {
            return this.commaddr;
        }
        
        public void setHost(String host)
        {
            this.commaddr=host;
        }
        
        public String getSysId()
        {
            return this.sysid;
        }
        
        public void setSysId(String sysid)
        {
                this.sysid=sysid;
        }
        
        public long getSigId()
        {
            return this.sigid;
        }
        
        public void setSigId(long sigid)
        {
                this.sigid=sigid;
        }
        
        public byte getKeyStatus()
        {
            return this.keystatus;
        }
        
        public void setKeyStatus(byte keystatus)
        {
            this.keystatus=keystatus;
        }
        
        public void updateData()
        {
            try {
                ByteArrayOutputStream os=new ByteArrayOutputStream();

                os.write(expand(this.countrycode,3).getBytes());
                os.write(expand(getBLZ(),30).getBytes());
                os.write(expand(this.name,60).getBytes("ISO-8859-1"));
                os.write(expand(getUserId(),30).getBytes("ISO-8859-1"));
                os.write(expand(getCustomerId(),30).getBytes("ISO-8859-1"));
                os.write(expand(getSysId(),30).getBytes());
                os.write(new byte[] {0x02});
                os.write(expand(getHost(),50).getBytes());
                os.write(int2ba((int)getSigId()));
                
                // TODO: wenn nutzerschlüssel vorhanden, aber noch nicht 
                // übermittelt sind: +0x01
                // TODO: das kann in HBCI4Java eigentlich nicht passieren, weil
                // Nutzerschlüssel immer erst dann im Passport gespeichert werden,
                // wenn sie erfolgreich an die Bank übermittelt werden konnten. Umgekehrt
                // kann es aber passieren, dass mit HBCI4Java eine RDH-2-Datei gelesen
                // wird, bei der dieses Flag gesetzt ist. Wenn das der Fall ist,
                // müssten eigentlich die gespeicherten Nutzerschlüssel erst mal
                // übertragen werden (oder wir ignorieren die Nutzerschlüssel einfach
                // und erzeugen neue - in jedem Fall ein BUG).
                os.write(new byte[] {(byte)(this.keystatus&0xFF)});
                
                setData(os.toByteArray());
            } catch (Exception e) {
                throw new HBCI_Exception(e);
            }
        }
        
        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            
            ret.append("bankdata: country="+this.countrycode);
            ret.append("; blz="+this.blz);
            ret.append("; name="+this.name);
            ret.append("; userid="+this.userid);
            ret.append("; customerid="+this.customerid);
            ret.append("; sysid="+this.sysid);
            ret.append("; commtype="+this.commtype);
            ret.append("; commaddr="+this.commaddr);
            ret.append("; sigid="+this.sigid);
            ret.append("; keystatus=0x"+Integer.toString(this.keystatus,16));
            
            return ret.toString();
        }
    }
    
    // userkeys
    public static class UserKeys
        extends TLV
    {
        private int    keytype;
        private int    keynum;
        private int    keyversion;
        private byte[] exponent;
        private byte[] modulus;
        
        private byte[] encPrivateKey;
        private byte[] p;
        private byte[] q;
        private byte[] dP;
        private byte[] dQ;
        private byte[] Ap;
        private byte[] Aq;
        
        public UserKeys()
        {
            super(0x4553);
        }
        
        public UserKeys(TLV tlv)
        {
            super(tlv);
            byte[] data=this.getData();
            
            this.keytype=data[1];
            this.keynum=((data[3]&0xFF)<<8) | (data[2]&0xFF);
            this.keyversion=((data[5]&0xFF)<<8) | (data[4]&0xFF);
            
            int len=((data[7]&0xFF)<<8) | (data[6]&0xFF);
            this.exponent=new byte[len];
            for (int i=0;i<len;i++) {
                this.exponent[len-i-1]=data[8+i];
            }
            int offset=8+len;
            
            len=((data[offset+1]&0xFF)<<8) | (data[offset]&0xFF);
            offset+=2;
            this.modulus=new byte[len];
            for (int i=0;i<len;i++) {
                this.modulus[len-i-1]=data[offset+i];
            }
            offset+=len;
            
            len=((data[offset+1]&0xFF)<<8) | (data[offset]&0xFF);
            offset+=2;
            this.encPrivateKey=new byte[len];
            for (int i=0;i<len;i++) {
                this.encPrivateKey[len-i-1]=data[offset+i];
            }
            offset+=len;
            
            HBCIUtils.log("found userkey with keynum="+this.keynum, HBCIUtils.LOG_DEBUG);
        }
        
        public void decrypt(SecretKey key)
            throws Exception
        {
            // TODO: exception
            
            // decrypt encrypted data
            Cipher cipher=Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[8]));
            byte[] plaindata=cipher.doFinal(this.encPrivateKey);
            int    offset=0;
            
            // modulus
            int len=((plaindata[offset+1]&0xFF)<<8) | (plaindata[offset]&0xFF);
            offset+=2;
            byte[] modulus2=new byte[len];
            for (int i=0;i<len;i++) {
                modulus2[len-i-1]=plaindata[offset+i];
            }
            offset+=len;
            // System.out.println("public and private modulus equal: "+Arrays.equals(this.modulus,modulus2));
            
            // p
            len=((plaindata[offset+1]&0xFF)<<8) | (plaindata[offset]&0xFF);
            offset+=2;
            this.p=new byte[len];
            for (int i=0;i<len;i++) {
                p[len-i-1]=plaindata[offset+i];
            }
            offset+=len;
            
            // q
            len=((plaindata[offset+1]&0xFF)<<8) | (plaindata[offset]&0xFF);
            offset+=2;
            this.q=new byte[len];
            for (int i=0;i<len;i++) {
                q[len-i-1]=plaindata[offset+i];
            }
            offset+=len;
            
            // dP
            len=((plaindata[offset+1]&0xFF)<<8) | (plaindata[offset]&0xFF);
            offset+=2;
            this.dP=new byte[len];
            for (int i=0;i<len;i++) {
                dP[len-i-1]=plaindata[offset+i];
            }
            offset+=len;
            
            // dQ
            len=((plaindata[offset+1]&0xFF)<<8) | (plaindata[offset]&0xFF);
            offset+=2;
            this.dQ=new byte[len];
            for (int i=0;i<len;i++) {
                dQ[len-i-1]=plaindata[offset+i];
            }
            offset+=len;
            
            // Ap
            len=((plaindata[offset+1]&0xFF)<<8) | (plaindata[offset]&0xFF);
            offset+=2;
            this.Ap=new byte[len];
            for (int i=0;i<len;i++) {
                Ap[len-i-1]=plaindata[offset+i];
            }
            offset+=len;
            
            // Aq
            len=((plaindata[offset+1]&0xFF)<<8) | (plaindata[offset]&0xFF);
            offset+=2;
            this.Aq=new byte[len];
            for (int i=0;i<len;i++) {
                Aq[len-i-1]=plaindata[offset+i];
            }
            offset+=len;
        }
        
        public String getKeyType()
        {
            return (keytype==0)?"S":"V";
        }
        
        public void setKeyType(String t)
        {
            if (t.equals("S"))
                this.keytype=0;
            else
                this.keytype=1;
        }
        
        public int getKeyNum()
        {
            return keynum;
        }
        
        public void setKeyNum(int num)
        {
            this.keynum=num;
        }
        
        public int getKeyVersion()
        {
            return keyversion;
        }
        
        public void setKeyVersion(int version)
        {
            this.keyversion=version;
        }
        
        public Key getPublicKey()
        {
            try {
                KeySpec spec=new RSAPublicKeySpec(
                    new BigInteger(+1,modulus),
                    new BigInteger(+1,exponent));
                KeyFactory fac=KeyFactory.getInstance("RSA");
                return fac.generatePublic(spec);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        public void setPublicKey(Key key)
        {
            RSAPublicKey pubkey=(RSAPublicKey)key;
            this.exponent=trimba(pubkey.getPublicExponent().toByteArray());
            this.modulus=trimba(pubkey.getModulus().toByteArray());
        }
        
        public Key getPrivateKey()
        {
            try {
                RSAPrivateCrtKey2 k=new RSAPrivateCrtKey2(
                    new BigInteger(+1,p),
                    new BigInteger(+1,q),
                    new BigInteger(+1,dP),
                    new BigInteger(+1,dQ),
                    new BigInteger(+1,q).modInverse(new BigInteger(+1,p)));
                k.setAp(new BigInteger(+1,Ap));
                k.setAq(new BigInteger(+1,Aq));
                return k;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        public void setPrivateKey(Key key)
        {
            if (key instanceof RSAPrivateCrtKey2) {
                // gesetzter key wurde ursprünglich auch aus einem SIZ-file gelesen
                
                RSAPrivateCrtKey2 privkey=(RSAPrivateCrtKey2)key;
                this.p=trimba(privkey.getP().toByteArray());
                this.q=trimba(privkey.getQ().toByteArray());
                this.dP=trimba(privkey.getdP().toByteArray());
                this.dQ=trimba(privkey.getdQ().toByteArray());
                this.Ap=trimba(privkey.getAp().toByteArray());
                this.Aq=trimba(privkey.getAq().toByteArray());
            } else {
                // key wurde mit Java erzeugt, es müssen noch ein paar Parameter,
                // die fürs SIZ-file benötigt werden, errechnet werden
                
                RSAPrivateCrtKey privkey=(RSAPrivateCrtKey)key;
                this.p=trimba(privkey.getPrimeP().toByteArray());
                this.q=trimba(privkey.getPrimeQ().toByteArray());
                this.dP=trimba(privkey.getPrimeExponentP().toByteArray());
                this.dQ=trimba(privkey.getPrimeExponentQ().toByteArray());
                
                BigInteger one=new BigInteger("1");
                BigInteger modulus=new BigInteger(+1,this.p).multiply(new BigInteger(+1,this.q));
                
                this.Ap=trimba(new BigInteger(+1,this.q).modPow(new BigInteger(+1,this.p).subtract(one),modulus).toByteArray());
                this.Aq=trimba(modulus.add(one).subtract(new BigInteger(+1,this.Ap)).toByteArray());
            }
        }
        
        public void encrypt(SecretKey key)
            throws Exception
        {
            // TODO: exception
            ByteArrayOutputStream plaindata = new ByteArrayOutputStream();
            
            plaindata.write(int2ba(this.modulus.length));
            plaindata.write(reverseba(this.modulus));
            plaindata.write(int2ba(this.p.length));
            plaindata.write(reverseba(this.p));
            plaindata.write(int2ba(this.q.length));
            plaindata.write(reverseba(this.q));
            plaindata.write(int2ba(this.dP.length));
            plaindata.write(reverseba(this.dP));
            plaindata.write(int2ba(this.dQ.length));
            plaindata.write(reverseba(this.dQ));
            plaindata.write(int2ba(this.Ap.length));
            plaindata.write(reverseba(this.Ap));
            plaindata.write(int2ba(this.Aq.length));
            plaindata.write(reverseba(this.Aq));

            // encrypt encrypted data
            Cipher cipher=Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[8]));
            this.encPrivateKey = cipher.doFinal(plaindata.toByteArray());
        }
        
        public void updateData() 
        {
            try {
                ByteArrayOutputStream os=new ByteArrayOutputStream();

                os.write(new byte[] {0x02});
                os.write(new byte[] {(byte)this.keytype});
                os.write(int2ba(this.keynum));
                os.write(int2ba(this.keyversion));
                os.write(int2ba(this.exponent.length));
                os.write(reverseba(this.exponent));
                os.write(int2ba(this.modulus.length));
                os.write(reverseba(this.modulus));
                os.write(int2ba(this.encPrivateKey.length));
                os.write(reverseba(this.encPrivateKey));
                
                setData(os.toByteArray());
            } catch (Exception e) {
                throw new HBCI_Exception(e);
            }
        }

        public String toString()
        {
            StringBuffer ret=new StringBuffer();
            ret.append("userkeys: keytype="+keytype);
            ret.append("; keynum="+keynum);
            ret.append("; keyversion="+keyversion);
            
            ret.append("; exponent=");
            for (int i=0;i<exponent.length;i++) {
                int x=exponent[i]&0xFF;
                ret.append(Integer.toString(x,16)+" ");
            }
            
            ret.append("; modulus=");
            for (int i=0;i<modulus.length;i++) {
                int x=modulus[i]&0xFF;
                ret.append(Integer.toString(x,16)+" ");
            }
            
            if (p!=null) {
                ret.append("; p=");
                for (int i=0;i<p.length;i++) {
                    int x=p[i]&0xFF;
                    ret.append(Integer.toString(x,16)+" ");
                }
            }
            
            if (q!=null) {
                ret.append("; q=");
                for (int i=0;i<q.length;i++) {
                    int x=q[i]&0xFF;
                    ret.append(Integer.toString(x,16)+" ");
                }
            }
            
            if (dP!=null) {
                ret.append("; dP=");
                for (int i=0;i<dP.length;i++) {
                    int x=dP[i]&0xFF;
                    ret.append(Integer.toString(x,16)+" ");
                }
            }
            
            if (dQ!=null) {
                ret.append("; dQ=");
                for (int i=0;i<dQ.length;i++) {
                    int x=dQ[i]&0xFF;
                    ret.append(Integer.toString(x,16)+" ");
                }
            }
            
            if (Ap!=null) {
                ret.append("; Ap=");
                for (int i=0;i<Ap.length;i++) {
                    int x=Ap[i]&0xFF;
                    ret.append(Integer.toString(x,16)+" ");
                }
            }
            
            if (Aq!=null) {
                ret.append("; Aq=");
                for (int i=0;i<Aq.length;i++) {
                    int x=Aq[i]&0xFF;
                    ret.append(Integer.toString(x,16)+" ");
                }
            }
            
            return ret.toString();
        }
    }
    
    private BankData bankdata;
    private List<UserKeys>     userkeys;
    
    public HBCIAccount()
    {
        super(0x564b);
        this.bankdata=new BankData();
        this.userkeys=new ArrayList<UserKeys>();
    }
    
    public HBCIAccount(TLV tlv)
    {
        super(tlv);
        this.bankdata=new BankData(new TLV(tlv.getData(),0));
        
        this.userkeys=new ArrayList<UserKeys>();
        int size=this.getData().length;
        int posi=bankdata.getRawData().length;
        
        while (posi<size) {
            HBCIAccount.UserKeys userkey=new UserKeys(new TLV(this.getData(),posi));
            this.userkeys.add(userkey);
            posi+=userkey.getRawData().length;
        }
    }
    
    public String getCountry()
    {
        return this.bankdata.getCountry();
    }
    
    public void setCountry(String country)
    {
        this.bankdata.setCountry(country);
    }
    
    public String getBLZ()
    {
        return this.bankdata.getBLZ();
    }
    
    public void setBLZ(String blz)
    {
        this.bankdata.setBLZ(blz);
    }
    
    public String getUserId()
    {
        return this.bankdata.getUserId();
    }
    
    public void setUserId(String userid)
    {
        this.bankdata.setUserId(userid);
    }
    
    public String getCustomerId()
    {
        return this.bankdata.getCustomerId();
    }
    
    public void setCustomerId(String customerid)
    {
        this.bankdata.setCustomerId(customerid);
    }
    
    public String getHost()
    {
        return this.bankdata.getHost();
    }
    
    public void setHost(String host)
    {
        this.bankdata.setHost(host);
    }
    
    public String getSysId()
    {
        return this.bankdata.getSysId();
    }
    
    public void setSysId(String sysid)
    {
        this.bankdata.setSysId(sysid);
    }
    
    public long getSigId()
    {
        return this.bankdata.getSigId();
    }
    
    public void setSigId(long sigid)
    {
        this.bankdata.setSigId(sigid);
    }
    
    public byte getKeyStatus()
    {
        return this.bankdata.getKeyStatus();
    }
    
    public void setKeyStatus(byte keystatus)
    {
        this.bankdata.setKeyStatus(keystatus);
    }
    
    public List<UserKeys> getUserKeys()
    {
        return this.userkeys;
    }
    
    public HBCIKey[] getUserSigKeys()
    {
        return getUserKeys("S");
    }
    
    public void setUserSigKeys(HBCIKey[] keys)
    {
        setUserKeys("S", keys);
        
        if (keys==null || keys.length==0 || keys[0]==null) {
            // wenn keine keys vorhanden sind, dann das flag setzen, welches
            // markiert, dass noch nutzerschlüssel übertragen werden müssen
            setKeyStatus((byte)((getKeyStatus()|0x01)&0xFF));
        } else {
            setKeyStatus((byte)(getKeyStatus()&0xFE));
        }
    }
    
    public HBCIKey[] getUserEncKeys()
    {
        return getUserKeys("V");
    }
    
    public void setUserEncKeys(HBCIKey[] keys)
    {
        setUserKeys("V", keys);
    }
    
    private HBCIKey[] getUserKeys(String keytype)
    {
        HBCIKey[] ret=null;
        
        for (Iterator<UserKeys> i=userkeys.iterator();i.hasNext();) {
            UserKeys key= i.next();
            if (key.getKeyType().equals(keytype)) {
                ret=new HBCIKey[2];
                
                ret[0]=new HBCIKey(
                    getCountry(),getBLZ(),getUserId(),
                    Integer.toString(key.getKeyNum()), Integer.toString(key.getKeyVersion()),
                    key.getPublicKey());
                
                ret[1]=new HBCIKey(
                    getCountry(),getBLZ(),getUserId(),
                    Integer.toString(key.getKeyNum()), Integer.toString(key.getKeyVersion()),
                    key.getPrivateKey());
            }
        }
        
        return ret;
    }
    
    private void setUserKeys(String keytype,HBCIKey[] keys)
    {
        if (keys!=null && keys.length==2 && keys[0]!=null && keys[1]!=null) {
            boolean found=false;
            for (Iterator<UserKeys> i=userkeys.iterator();i.hasNext();) {
                UserKeys userkey= i.next();
                if (userkey.getKeyType().equals(keytype)) {
                    userkey.setKeyNum(Integer.parseInt(keys[0].num));
                    userkey.setKeyVersion(Integer.parseInt(keys[0].version));
                    userkey.setPublicKey(keys[0].key);
                    userkey.setPrivateKey(keys[1].key);
                    found=true;
                }
            }

            if (!found) {
                UserKeys userkey=new UserKeys();
                userkeys.add(userkey);

                userkey.setKeyType(keytype);
                userkey.setKeyNum(Integer.parseInt(keys[0].num));
                userkey.setKeyVersion(Integer.parseInt(keys[0].version));
                userkey.setPublicKey(keys[0].key);
                userkey.setPrivateKey(keys[1].key);
            }
        }
    }
    
    public void updateData()
    {
        try {
            ByteArrayOutputStream os=new ByteArrayOutputStream();

            bankdata.updateData();
            os.write(bankdata.getRawData());
            
            for (Iterator<UserKeys> i=userkeys.iterator();i.hasNext();) {
                UserKeys userkeys= i.next();
                userkeys.updateData();
                os.write(userkeys.getRawData());
            }
            
            setData(os.toByteArray());
        } catch (Exception e) {
            throw new HBCI_Exception(e);
        }
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        ret.append("hbciaccount: "+bankdata);
        
        for (Iterator<UserKeys> i=userkeys.iterator();i.hasNext();) {
            ret.append("; "+i.next());
        }
        
        return ret.toString();
    }
}