
/*  $Id: PKCS1_15.java,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureSpi;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class PKCS1_15
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
            throws InvalidParameterException
    {
        // do nothing

    }
    

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec params)
            throws InvalidAlgorithmParameterException
    {
        if (params instanceof SignatureParamSpec)
            this.param=(SignatureParamSpec)(params);
        else {
            throw new InvalidAlgorithmParameterException();
        }
    }


    @Override
    @Deprecated
    protected Object engineGetParameter(String param1)
            throws InvalidParameterException
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
    protected void engineUpdate(byte[] b, int off, int len)
    {
        for (int i=0;i<len;i++) {
            engineUpdate(b[off+i]);
        }
    }
   
    
    @Override
    protected byte[] engineSign()
    {
        return sign(this.param, this.privKey, this.plainmsg.toByteArray());
    }

    
    @Override
    protected boolean engineVerify(byte[] sigBytes) 
    {
        return verify(this.param, this.pubKey, this.plainmsg.toByteArray(), sigBytes);
    }
    
    
    // --------------------------------------------------------------------
    
    
    private static byte[] i2osp(BigInteger x, int len)
    {
        byte[] bytes=x.toByteArray();

        if (bytes.length>len) {
            // created output len does not fit into outLen
            // maybe this are only leading zeroes, so we will check this
            for (int i=0; i<(bytes.length-len); i++) {
                if (bytes[i]!=0) {
                    throw new RuntimeException("value too large");
                }
            }

            // ok, now remove leading zeroes
            byte[] out=new byte[len];
            System.arraycopy(bytes, bytes.length-len, out, 0, len);
            bytes = out;

        } else if (bytes.length<len) {
            // created output is too small, so create leading zeroes
            byte[] out=new byte[len];
            System.arraycopy(bytes, 0, out, len-bytes.length, bytes.length);
            bytes = out;
        }

        return bytes;
    }
    
    
    private static BigInteger os2ip(byte[] bytes)
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


    private static byte[] sign(SignatureParamSpec spec, PrivateKey privKey, byte[] msg)
    {
        BigInteger bModulus;
        if (privKey instanceof RSAPrivateKey) {
            bModulus=((RSAPrivateKey)privKey).getModulus();
        } else {
            bModulus=((RSAPrivateCrtKey2)privKey).getP().multiply(((RSAPrivateCrtKey2)privKey).getQ());
        }        
        int modBits = bModulus.bitLength();
        int k = modBits>>3;
        if ((modBits&7)!=0) {
            k++;
        }
        
        byte[] EM=emsa_encode(spec, msg, k);
        BigInteger m=os2ip(EM);
        BigInteger s=sp1(privKey, m);
        byte[]     S=i2osp(s, k);
        return S;
    }
    
    
    private static boolean verify(SignatureParamSpec spec, PublicKey pubKey, byte[] msg, byte[] signature)
    {
        // TODO: check if signature.len == pubKey.getModulus().len
        
        int modBits=((RSAPublicKey)pubKey).getModulus().bitLength();
        int k=modBits>>3;
        if ((modBits&0x07)!=0) {
            k++;
        }
        // System.out.println("modBits: "+modBits+", k="+k);        
        // System.out.println("signature: "+Utils.bytes2String(signature));
        
        BigInteger s=os2ip(signature);
        BigInteger m=vp1((RSAPublicKey)pubKey, s);
        byte[]     EM2=i2osp(m, k);
        // System.out.println("decoded EM2: "+Utils.bytes2String(EM2));
        
        byte[]     EM=emsa_encode(spec, msg, k);
        // System.out.println("encoded EM: "+Utils.bytes2String(EM));
        
        return Arrays.equals(EM, EM2);
    }
    
    
    private static byte[] hash(SignatureParamSpec spec, byte[] msg)
    {
        MessageDigest dig=getMessageDigest(spec);
        return dig.digest(msg);
    }
    
    
    private static byte[] getHashAlgOID(SignatureParamSpec spec)
    {
        byte[] result;
        
        if (spec.getHashAlg().equals("SHA-1")) {
            result=new byte[] {(byte)0x2b, (byte)0x0e, (byte)0x03, (byte)0x02, (byte)0x1a};
            
        } else if (spec.getHashAlg().equals("SHA-256")) {
                result=new byte[] {
                        (byte)0x60, (byte)0x86, (byte)0x48, (byte)0x01, 
                        (byte)0x65, (byte)0x03, (byte)0x04, (byte)0x02, 
                        (byte)0x01};
                
        } else {
            throw new IllegalArgumentException("dont know OID for "+spec.getHashAlg());
        }
        
        return result;
    }
    
    
    public static byte[] createDigestInfo(SignatureParamSpec spec, byte[] hash)
    {
        // write digest info
        ByteArrayOutputStream digestInfoS=new ByteArrayOutputStream();
        byte[]                digestInfo_hashAlg=getHashAlgOID(spec);
        try {
            // sequence: outer wrapper
            digestInfoS.write(0x30);
            digestInfoS.write(8+digestInfo_hashAlg.length+hash.length);
            //   sequence hash info
            digestInfoS.write(0x30);
            digestInfoS.write(4+digestInfo_hashAlg.length);
            //     oid hashalg
            digestInfoS.write(0x06);
            digestInfoS.write(digestInfo_hashAlg.length);
            digestInfoS.write(digestInfo_hashAlg);
            //     zero (no hashalg params)
            digestInfoS.write(0x05);
            digestInfoS.write(0x00);
            //   hash value
            digestInfoS.write(0x04);
            digestInfoS.write(hash.length);
            digestInfoS.write(hash);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }
        byte[] digestInfo=digestInfoS.toByteArray();
        return digestInfo;
    }
    
    
    private static byte[] emsa_encode(SignatureParamSpec spec, byte[] msg, int emLen)
    {
        byte[] H=hash(spec, msg);
        byte[] T=createDigestInfo(spec, H);
        int    tLen=T.length;
        
        byte[] PS=new byte[emLen-tLen-3];
        Arrays.fill(PS, (byte)0xFF);
        
        ByteArrayOutputStream EMs=new ByteArrayOutputStream();
        try {
            EMs.write(0x00);
            EMs.write(0x01);
            EMs.write(PS);
            EMs.write(0x00);
            EMs.write(T);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] EM=EMs.toByteArray();
        
        return EM;
    }
}
