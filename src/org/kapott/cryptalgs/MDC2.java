
/*  $Id: MDC2.java,v 1.1 2011/05/04 22:37:58 willuhn Exp $

    This file is part of CryptAlgs4Java
    Copyright (C) 2001-2010  Stefan Palme

    CryptAlgs4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    CryptAlgs4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.cryptalgs;

import java.security.InvalidKeyException;
import java.security.MessageDigestSpi;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public final class MDC2
    extends MessageDigestSpi
{
    private byte[]           x;
    private byte             writePos;
    private byte[]           h1,h2;
    private SecretKeyFactory fac;
    private Cipher           cipher;
    
    private int[] odd_parity={
        1,  1,  2,  2,  4,  4,  7,  7,  8,  8, 11, 11, 13, 13, 14, 14,
        16, 16, 19, 19, 21, 21, 22, 22, 25, 25, 26, 26, 28, 28, 31, 31,
        32, 32, 35, 35, 37, 37, 38, 38, 41, 41, 42, 42, 44, 44, 47, 47,
        49, 49, 50, 50, 52, 52, 55, 55, 56, 56, 59, 59, 61, 61, 62, 62,
        64, 64, 67, 67, 69, 69, 70, 70, 73, 73, 74, 74, 76, 76, 79, 79,
        81, 81, 82, 82, 84, 84, 87, 87, 88, 88, 91, 91, 93, 93, 94, 94,
        97, 97, 98, 98,100,100,103,103,104,104,107,107,109,109,110,110,
        112,112,115,115,117,117,118,118,121,121,122,122,124,124,127,127,
        128,128,131,131,133,133,134,134,137,137,138,138,140,140,143,143,
        145,145,146,146,148,148,151,151,152,152,155,155,157,157,158,158,
        161,161,162,162,164,164,167,167,168,168,171,171,173,173,174,174,
        176,176,179,179,181,181,182,182,185,185,186,186,188,188,191,191,
        193,193,194,194,196,196,199,199,200,200,203,203,205,205,206,206,
        208,208,211,211,213,213,214,214,217,217,218,218,220,220,223,223,
        224,224,227,227,229,229,230,230,233,233,234,234,236,236,239,239,
        241,241,242,242,244,244,247,247,248,248,251,251,253,253,254,254};

    
    public MDC2()
    {
        this.x=new byte[8];
        this.h1=new byte[8];
        this.h2=new byte[8];
        
        try {
            this.fac=SecretKeyFactory.getInstance("DES");
            this.cipher=Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        
        engineReset();
    }

    @Override
    protected byte[] engineDigest()
    {
        if (this.writePos!=0) {
            this.writePos=8;
            hashIt();
        }
        
        byte[] ret=new byte[16];
        System.arraycopy(this.h1,0,ret,0,8);
        System.arraycopy(this.h2,0,ret,8,8);
        
        engineReset();
        return ret;
    }

    @Override
    protected int engineDigest(byte[] buf, int offset, int len)
    {
        byte[] result=engineDigest();
        System.arraycopy(result,0,buf,offset,engineGetDigestLength());
        return engineGetDigestLength();
    }

    @Override
    protected int engineGetDigestLength()
    {
        return 16;
    }

    @Override
    protected void engineReset()
    {
        Arrays.fill(this.x,(byte)0);
        Arrays.fill(this.h1,(byte)0x52);
        Arrays.fill(this.h2,(byte)0x25);
        this.writePos=0;
    }

    @Override
    protected void engineUpdate(byte[] input, int offset, int len)
    {
        for (int i=0;i<len;i++)
            engineUpdate(input[offset+i]);
    }

    @Override
    protected void engineUpdate(byte input)
    {
        this.x[this.writePos++]=input;
        if ((this.writePos&0x07)==0) {
            // 8 byte boundary reached
            hashIt();
        }
    }

    private void hashIt()
    {
        byte[] k1=g(this.h1,(byte)0x40);
        byte[] k2=g(this.h2,(byte)0x20);

        byte[] c1=xor(des(this.x,k1),this.x);
        byte[] c2=xor(des(this.x,k2),this.x);

        System.arraycopy(c1,0,this.h1,0,4);
        System.arraycopy(c2,4,this.h1,4,4);
        System.arraycopy(c2,0,this.h2,0,4);
        System.arraycopy(c1,4,this.h2,4,4);

        Arrays.fill(this.x,(byte)0);
        this.writePos=0;
    }
    
    private byte[] g(byte[] u,byte modifier)
    {
        byte[] ret=new byte[8];
        System.arraycopy(u,0,ret,0,8);
        
        ret[0]=(byte)((ret[0]&(byte)0x9F) | modifier);
        
        for (int i=0;i<8;i++) {
            int idx=ret[i];
            if (idx<0)
                idx+=256;
            ret[i]=(byte)this.odd_parity[idx];
        }
        return ret;
    }

    private byte[] des(byte[] data,byte[] keydata)
    {
        byte[] ret=null;
        
        try {
            DESKeySpec spec=new DESKeySpec(keydata);
            SecretKey key=this.fac.generateSecret(spec);
            this.cipher.init(Cipher.ENCRYPT_MODE,key);
            ret=this.cipher.doFinal(data);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        
        return ret;
    }
    
    private byte[] xor(byte[] x1,byte[] x2)
    {
        byte[] ret=new byte[8];
        
        for (int i=0;i<8;i++) {
            ret[i]=(byte)(x1[i]^x2[i]);
        }
        
        return ret;
    }
}
