
/*  $Id: RetailMAC.java,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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
import java.security.Key;
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
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

// TODO: implement some SPI interface?
public class RetailMAC
{
    private SecretKey       deskey;
    private SecretKey       desedekey;
    private IvParameterSpec ivspec;

    private byte[]          buffer=new byte[16];
    private int             offset;
    private byte[]          c=new byte[8];
    private Cipher          cipher;

    public RetailMAC(Key key,IvParameterSpec iv)
    {
        try {
            SecretKeyFactory fac=SecretKeyFactory.getInstance("DESede");
            DESedeKeySpec    spec=(DESedeKeySpec)fac.getKeySpec((SecretKey)key,
                                                                DESedeKeySpec.class);
            byte[]           desedekeydata=spec.getKey();

            DESKeySpec spec2=new DESKeySpec(desedekeydata);
            fac=SecretKeyFactory.getInstance("DES");
            this.deskey=fac.generateSecret(spec2);

            this.desedekey=(SecretKey)key;
            this.ivspec=iv;
            this.cipher=Cipher.getInstance("DES");
            reset();
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public byte[] doFinal(byte[] data)
    {
        update(data,0,data.length);
        return doFinal();
    }

    public byte[] doFinal()
    {
        for (int i=0;i<8;i++) {
            this.buffer[i]^=this.c[i];
        }

        try {
            Cipher cipher2=Cipher.getInstance("DESede");
            cipher2.init(Cipher.ENCRYPT_MODE, this.desedekey);
            this.c=cipher2.doFinal(this.buffer, 0, 8);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
        
        byte[] ret=new byte[8];
        System.arraycopy(this.c, 0, ret, 0, 8);

        reset();
        return ret;
    }

    public void update(byte data)
    {
        this.buffer[this.offset++]=data;
        if (this.offset>8) {
            hashIt();
        }
    }

    public void reset()
    {
        Arrays.fill(this.buffer,(byte)0);
        System.arraycopy(this.ivspec.getIV(), 0, this.c, 0, 8);
        this.offset=0;
    }

    public int getMacLength()
    {
        return 8;
    }

    public void update(byte[] ibuffer,int offset1,int len)
    {
        for (int i=0;i<len;i++)
            update(ibuffer[offset1+i]);
    }

    private void hashIt()
    {
        for (int i=0;i<8;i++) {
            this.buffer[i]^=this.c[i];
        }

        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.deskey);
            this.c=this.cipher.doFinal(this.buffer, 0, 8);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

        System.arraycopy(this.buffer,8, this.buffer,0, 8);
        Arrays.fill(this.buffer, 8, 16, (byte)0);
        this.offset-=8;
    }
}
