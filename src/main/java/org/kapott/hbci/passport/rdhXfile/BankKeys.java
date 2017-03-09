
/*  $Id: BankKeys.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

import org.kapott.hbci.datatypes.SyntaxCtr;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;


public class BankKeys
    extends TLV
{
    private String countrycode;
    private String blz;
    private int    keytype;
    private int    keyversion;
    private int    keynum;
    private String keyname;
    private byte[] modulus;
    private byte[] exponent;
    
    private PublicKey key;
    
    public BankKeys()
    {
        super(0x53d6);
    }
    
    public BankKeys(TLV tlv)
    {
        super(tlv);
        byte[] data=this.getData();
        
        this.countrycode=new String(data,0,3).trim();
        this.blz=new String(data,3,30).trim();
        this.keytype=data[34];
        this.keyversion=((data[36]&0xFF)<<8) | (data[35]&0xFF);
        this.keynum=((data[38]&0xFF)<<8) | (data[37]&0xFF);
        this.keyname=new String(data,39,30).trim();
        
        int len=((data[70]&0xFF)<<8) | (data[69]&0xFF);
        this.modulus=new byte[len];
        for (int i=0;i<len;i++) {
            this.modulus[len-i-1]=data[71+i];
        }
        int offset=71+len;
        
        len=((data[offset+1]&0xFF)<<8) | (data[offset]&0xFF);
        offset+=2;
        this.exponent=new byte[len];
        for (int i=0;i<len;i++) {
            this.exponent[len-i-1]=data[offset+i];
        }
        offset+=len;
        
        try {
            RSAPublicKeySpec spec=new RSAPublicKeySpec(
                new BigInteger(+1,modulus),
                new BigInteger(+1,exponent));
            KeyFactory fac=KeyFactory.getInstance("RSA");
            this.key=fac.generatePublic(spec);
        } catch (Exception e) {
            throw new HBCI_Exception(e);
        }
        
        HBCIUtils.log("found bank keys with keynum="+this.keynum, HBCIUtils.LOG_DEBUG);
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
    
    public String getKeyType()
    {
        return (keytype==0)?"S":"V";
    }
    
    public HBCIKey getHBCIKey()
    {
        return new HBCIKey(
            countrycode,blz,keyname,
            Integer.toString(keynum),Integer.toString(keyversion),
            key);
    }
    
    public void setKey(String keytype, HBCIKey key)
    {
        if (keytype.equals("S"))
            this.keytype=0;
        else
            this.keytype=1;
        this.keyname=key.userid;
        this.keynum=Integer.parseInt(key.num);
        this.keyversion=Integer.parseInt(key.version);
        
        RSAPublicKey pubkey=(RSAPublicKey)key.key;
        this.key=pubkey;
        this.modulus=trimba(pubkey.getModulus().toByteArray());
        this.exponent=trimba(pubkey.getPublicExponent().toByteArray());
    }
    
    public void updateData()
    {
        try {
            ByteArrayOutputStream os=new ByteArrayOutputStream();

            os.write(this.countrycode.getBytes());
            os.write(expand(getBLZ(),30).getBytes());
            os.write(new byte[] {0x02});
            os.write(new byte[] {(byte)this.keytype});
            os.write(int2ba(this.keyversion));
            os.write(int2ba(this.keynum));
            os.write(expand(this.keyname,30).getBytes("ISO-8859-1"));
            os.write(int2ba(this.modulus.length));
            os.write(reverseba(this.modulus));
            os.write(int2ba(this.exponent.length));
            os.write(reverseba(this.exponent));

            setData(os.toByteArray());
        } catch (Exception e) {
            throw new HBCI_Exception(e);
        }
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        ret.append("bankkeys: countrycode="+this.countrycode);
        ret.append("; blz="+this.blz);
        ret.append("; keytype="+this.keytype);
        ret.append("; keynum="+this.keynum);
        ret.append("; keyversion="+this.keyversion);
        ret.append("; keyname="+this.keyname);
        
        ret.append("; modulus=");
        for (int i=0;i<this.modulus.length;i++) {
            int x=this.modulus[i]&0xFF;
            ret.append(Integer.toString(x,16)+" ");
        }

        ret.append("; exponent=");
        for (int i=0;i<this.exponent.length;i++) {
            int x=this.exponent[i]&0xFF;
            ret.append(Integer.toString(x,16)+" ");
        }
        
        return ret.toString();
    }
}