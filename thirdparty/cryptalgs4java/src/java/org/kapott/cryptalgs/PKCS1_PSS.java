
/*  $Id: PKCS1_PSS.java,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class PKCS1_PSS 
    extends SignatureSpi 
{
    private RSAPublicKey          pubKey;
    private PrivateKey            privKey;
    private SignatureParamSpec    param;
    private ByteArrayOutputStream plainmsg;

    // ----- some interface stuff ---------------------------------------------

    @Override
    @Deprecated
    protected void engineSetParameter(String param1, Object value)
    {
        // do nothing
    }

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec param1)
        throws InvalidAlgorithmParameterException
    {
        if (param1 instanceof SignatureParamSpec)
            this.param=(SignatureParamSpec)(param1);
        else {
            throw new InvalidAlgorithmParameterException();
        }
    }
    
    @Override
    @Deprecated
    protected Object engineGetParameter(String parameter)
    {
        return null;
    }

    
    public static MessageDigest getMessageDigest(SignatureParamSpec spec)
    {
        MessageDigest result;
        
        try {
            String provider=spec.getProvider();
            if (provider!=null) {
                result=MessageDigest.getInstance(spec.getHashAlg(),provider);
            } else {
                result=MessageDigest.getInstance(spec.getHashAlg());
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    
    @Override
    protected void engineInitSign(PrivateKey privateKey)
    {
        this.privKey=privateKey;
        this.plainmsg=new ByteArrayOutputStream();
    }

    
    @Override
    protected void engineInitVerify(PublicKey publicKey)
    {
        this.pubKey=(RSAPublicKey)publicKey;
        this.plainmsg=new ByteArrayOutputStream();
    }

    @Override
    protected void engineUpdate(byte b)
    {
        this.plainmsg.write(b);
    }

    @Override
    protected void engineUpdate(byte[] b,int offset,int length)
    {
        for (int i=0;i<length;i++) {
            engineUpdate(b[offset+i]);
        }
    }

    @Override
    protected int engineSign(byte[] output,int offset,int len)
        throws SignatureException
    {
        byte[] sig=engineSign();

        if (offset+len>output.length)
            throw new SignatureException("output result too large for buffer");
        System.arraycopy(sig,0,output,offset,sig.length);
        return sig.length;
    }

    @Override
    protected byte[] engineSign()
    {
        return pss_sign(this.privKey, this.plainmsg.toByteArray());
    }

    @Override
    protected boolean engineVerify(byte[] sig)
    {
        return pss_verify(this.pubKey, this.plainmsg.toByteArray(), sig);
    }

    // --- stuff from the PKCS#1-PSS specification ---------------------------

    private static byte[] i2os(BigInteger x, int outLen)
    {
        byte[] bytes=x.toByteArray();

        if (bytes.length>outLen) {
            // created output len does not fit into outLen
            // maybe this are only leading zeroes, so we will check this
            for (int i=0; i<(bytes.length-outLen); i++) {
                if (bytes[i]!=0) {
                    throw new RuntimeException("value too large");
                }
            }

            // ok, now remove leading zeroes
            byte[] out=new byte[outLen];
            System.arraycopy(bytes, bytes.length-outLen, out, 0, outLen);
            bytes = out;

        } else if (bytes.length<outLen) {
            // created output is too small, so create leading zeroes
            byte[] out=new byte[outLen];
            System.arraycopy(bytes, 0, out, outLen-bytes.length, bytes.length);
            bytes = out;
        }

        return bytes;
    }

    private static BigInteger os2i(byte[] bytes)
    {
        return new BigInteger(+1, bytes);
    }

    private static BigInteger sp1(PrivateKey key, BigInteger m)
    {
        BigInteger result;

        if (key instanceof RSAPrivateKey) {
            BigInteger d=((RSAPrivateKey)key).getPrivateExponent();
            BigInteger n=((RSAPrivateKey)key).getModulus();
            result = m.modPow(d,n);
        } else {
            RSAPrivateCrtKey2 key2=(RSAPrivateCrtKey2)key;
            BigInteger p=key2.getP();
            BigInteger q=key2.getQ();
            BigInteger dP=key2.getdP();
            BigInteger dQ=key2.getdQ();
            BigInteger qInv=key2.getQInv();

            BigInteger s1 = m.modPow(dP,p);
            BigInteger s2 = m.modPow(dQ,q);
            BigInteger h = s1.subtract(s2).multiply(qInv).mod(p);
            result = s2.add(q.multiply(h));
        }

        return result;
    }

    private static BigInteger vp1(RSAPublicKey key, BigInteger s)
    {
        BigInteger e=key.getPublicExponent();
        BigInteger n=key.getModulus();
        BigInteger m=s.modPow(e,n);
        return m;
    }

    private static byte[] concat(byte[] x1, byte[] x2)
    {
        byte[] result=new byte[x1.length+x2.length];
        System.arraycopy(x1,0, result,0,         x1.length);
        System.arraycopy(x2,0, result,x1.length, x2.length);
        return result;
    }

    private static byte[] hash(SignatureParamSpec spec, byte[] data) 
    {
        MessageDigest dig=getMessageDigest(spec);
        dig.reset();
        return dig.digest(data);
    }

    private static byte[] mgf1(SignatureParamSpec spec, byte[] mgfSeed, int maskLen)
    {
        MessageDigest dig=getMessageDigest(spec);
        int           hLen=dig.getDigestLength();
        byte[]        T=new byte[0];
        for (int i=0; i<Math.ceil(maskLen/(double)hLen); i++) {
            byte[] c=i2os(new BigInteger(Integer.toString(i)), 4);
            T = concat(T, hash(spec, concat(mgfSeed,c)));
        }

        byte[] result=new byte[maskLen];
        System.arraycopy(T,0, result,0, maskLen);
        return result;
    }

    private static byte[] random_os(int len)
    {
        byte[] result=new byte[len];
        for (int i=0; i<len; i++) {
            result[i]=(byte)(256*Math.random());
        }
        return result;
    }

    private static byte[] xor_os(byte[] a1, byte[] a2)
    {
        if (a1.length!=a2.length) {
            throw new RuntimeException("a1.len != a2.len");
        }

        byte[] result=new byte[a1.length];
        for (int i=0; i<result.length; i++) {
            result[i] = (byte)(a1[i] ^ a2[i]);
        }

        return result;
    }

    public static byte[] emsa_pss_encode(SignatureParamSpec spec, byte[] msg, int emBits)
    {
        int emLen=emBits>>3;
        if ((emBits&7) != 0) {
            emLen++;
        }
        
        // System.out.println("message: "+Utils.bytes2String(msg));

        byte[] mHash = hash(spec, msg);
        // System.out.println("mHash: "+Utils.bytes2String(mHash));

        MessageDigest dig=getMessageDigest(spec);
        int           hLen = dig.getDigestLength();
        int           sLen = hLen;
        
        byte[] salt = random_os(sLen);
        byte[] zeroes = new byte[8];
        byte[] m2 = concat(concat(zeroes,mHash),salt);
        // System.out.println("M': "+Utils.bytes2String(m2));
        
        byte[] H = hash(spec, m2);
        // System.out.println("H: "+Utils.bytes2String(H));
        
        byte[] PS = new byte[emLen-sLen-hLen-2];
        byte[] DB = concat(concat(PS, new byte[] {0x01}), salt);
        // System.out.println("DB: "+Utils.bytes2String(DB));
        
        byte[] dbMask = mgf1(spec, H, emLen-hLen-1);
        // System.out.println("dbMask: "+Utils.bytes2String(dbMask));
        
        byte[] maskedDB = xor_os(DB, dbMask);
        // System.out.println("maskedDB: "+Utils.bytes2String(maskedDB));

        // set leftmost X bits in maskedDB to zero
        int  tooMuchBits=(emLen<<3)-emBits;
        byte mask=(byte)(0xFF>>>tooMuchBits);
        maskedDB[0] &= mask;

        byte[] EM = concat(concat(maskedDB,H), new byte[] {(byte)0xBC});
        // System.out.println("EM: "+Utils.bytes2String(EM));
        
        return EM;
    }

    public static boolean emsa_pss_verify(SignatureParamSpec spec, byte[] msg, byte[] EM, int emBits)
    {
        int emLen=emBits>>3;
        if ((emBits&7) != 0) {
            emLen++;
        }

        byte[]        mHash = hash(spec, msg);
        // System.out.println("mHash: "+Utils.bytes2String(mHash));
        
        MessageDigest dig=getMessageDigest(spec);
        int           hLen = dig.getDigestLength();
        // System.out.println("hLen: "+hLen);
        
        int    sLen = hLen;
        if (EM[EM.length-1]!=(byte)0xBC) {
            // System.out.println("no BC at the end");
            return false;
        }

        byte[] maskedDB = new byte[emLen-hLen-1];
        byte[] H = new byte[hLen];
        System.arraycopy(EM,0,            maskedDB,0, emLen-hLen-1);
        System.arraycopy(EM,emLen-hLen-1, H,0,        hLen);

        // TODO: verify if first X bits of maskedDB are zero

        byte[] dbMask = mgf1(spec, H, emLen-hLen-1);
        byte[] DB = xor_os(maskedDB, dbMask);

        // set leftmost X bits of DB to zero
        int  tooMuchBits=(emLen<<3)-emBits;
        byte mask=(byte)(0xFF>>>tooMuchBits);
        DB[0] &= mask;

        // TODO: another consistency check

        byte[] salt = new byte[sLen];
        System.arraycopy(DB,DB.length-sLen, salt,0, sLen);

        byte[] zeroes = new byte[8];
        byte[] m2 = concat(concat(zeroes,mHash),salt);
        byte[] H2 = hash(spec, m2);

        return Arrays.equals(H,H2);
    }
    
    public static int calculateEMBitLen(BigInteger modulus)
    {
        return modulus.bitLength()-1;
    }

    private byte[] pss_sign(PrivateKey key, byte[] msg)
    {
        // Modulus holen, weil dessen Bitlänge benötigt wird
        BigInteger bModulus;
        if (key instanceof RSAPrivateKey) {
            bModulus=((RSAPrivateKey)key).getModulus();
        } else {
            bModulus=((RSAPrivateCrtKey2)key).getP().multiply(((RSAPrivateCrtKey2)key).getQ());
        }        
        int modBits = bModulus.bitLength();

        int k = modBits>>3;
        if ((modBits&7) != 0) {
            k++;
        }

        byte[]     EM = emsa_pss_encode(this.param, msg, modBits-1);
        BigInteger m = os2i(EM);
        BigInteger s = sp1(key, m);
        byte[]     S = i2os(s, k);
        // System.out.println("S: "+Utils.bytes2String(S));
        
        return S;
    }

    private boolean pss_verify(RSAPublicKey key, byte[] msg, byte[] S)
    {
        BigInteger s = os2i(S);
        BigInteger m = vp1(key, s);
        BigInteger n = key.getModulus();

        int emBits = n.bitLength()-1;
        int emLen  = emBits>>3;
        if ((emBits&7)!=0) {
            emLen++;
        }

        byte[] EM = i2os(m, emLen);
        // System.out.println("EM: "+Utils.bytes2String(EM));
        
        return emsa_pss_verify(this.param, msg, EM, emBits);
    }

}
