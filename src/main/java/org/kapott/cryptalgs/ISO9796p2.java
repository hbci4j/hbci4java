
/*  $Id: ISO9796p2.java,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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
import java.util.logging.Logger;

public class ISO9796p2 
    extends SignatureSpi 
{
    private RSAPublicKey       pubKey;
    private PrivateKey         privKey;
    private MessageDigest      dig;
    private SignatureParamSpec param;

    protected Logger getLogger()
    {
        return Logger.getLogger(this.getClass().getName());
    }

    @Override @Deprecated
    protected Object engineGetParameter(String parameter)
    {
        return null;
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey)
    {
        String provider=this.param.getProvider();
        
        try {
            if (provider!=null) {
                this.dig=MessageDigest.getInstance(this.param.getHashAlg(), provider);
            } else {
                this.dig=MessageDigest.getInstance(this.param.getHashAlg());
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

        this.privKey=privateKey;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey)
    {
        try {
            this.dig=MessageDigest.getInstance(this.param.getHashAlg(), this.param.getProvider());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

        this.pubKey=(RSAPublicKey)publicKey;
    }

    @Override @Deprecated
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
    protected void engineUpdate(byte b)
    {
        this.dig.update(b);
    }

    @Override
    protected void engineUpdate(byte[] b,int offset,int length)
    {
        for (int i=0;i<length;i++) {
            engineUpdate(b[offset+i]);
        }
    }

    // -----------------------------------------------------------------------

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
        BigInteger bModulus;
        if (this.privKey instanceof RSAPrivateKey) {
            bModulus=((RSAPrivateKey)this.privKey).getModulus();
        } else {
            RSAPrivateCrtKey2 key2=(RSAPrivateCrtKey2)this.privKey;
            bModulus=key2.getP().multiply(key2.getQ());
        }
        
        // Hash-Wert holen
        byte[] H=this.dig.digest();
        // System.out.println("H = " + ba2st(H));

        // ein paar "Eckdaten"
        int  k  = bModulus.bitLength();
        int  t  = 1;
        int  T  = 0xBC;
        int  Lh = H.length<<3;
        
        // System.out.println("k (len of modulus) = "+ k +" bits");
        // System.out.println("t (octets in trailer field) = "+t);
        // System.out.println("T (trailer field) = 0xBC");
        // System.out.println("Lh (length of hash) = " + Lh);
        // System.out.println("c (capacity of signature) = k - Lh - 8t - 4 = "+(k-Lh-8*t-4)+" bits");
        // System.out.println("M = M1 = M2 = ''");
        
        // der padded String besteht aus ein paar Indikator-Bits, dem Hashwert,
        // eigentlich noch dem recoverable Teil der Message und Padding-Bits
        int padded_size = k>>3;
        if ((k&0x07)!=0) {
        	padded_size++;
        }
        // System.out.println("length of padded string will be "+padded_size+" bytes");
        
        byte[] paddedM = new byte[padded_size];
        
        // Ende-Zeichen 0xBC anfuegen
        paddedM[padded_size-1] = (byte)T;
        
        // davor kommt der Hashwert
        System.arraycopy(H,0, paddedM,padded_size-1-H.length, H.length);
        
        // davor kommen 8 bytes "message"
        // nichts zu tun, wir schreiben einfach 0-bytes rein
                
        // davor kommt der Indikator, dass die Padding-Bytes hier zu Ende sind
        // laut spez werden 0-nibbled durch 0xb ersetzt, hier aber scheinbar nicht
        // paddedM[padded_size-1-H.length-8-1] = (byte)0xBA;
        paddedM[padded_size-1-H.length-8-1] = (byte)0x01;

        // davor kommen padding-bits und -bytes
        int nof_zero_bits = k-Lh-8*8-8*t-4;
        // System.out.println("number of zero bits for padding: "+nof_zero_bits);
        
        // es werden schon 5 bits im ersten byte und nochmal 7 bits im letzten
        // byte fuers padding gebraucht, also:
        int nof_zero_bytes = (nof_zero_bits-5-7)>>3;
        // System.out.println("number of zero bytes: "+((nof_zero_bits-5-7)/8.0));
        
        byte[] zero_bytes = new byte[nof_zero_bytes];
        // laut spez werden 0-nibbled durch 0xb ersetzt, hier aber scheinbar nicht
        // Arrays.fill(zero_bytes,(byte)0xBB);
        Arrays.fill(zero_bytes,(byte)0x00);
        System.arraycopy(zero_bytes,0, paddedM,padded_size-1-H.length-8-1-nof_zero_bytes, nof_zero_bytes);
        
        // das erste Byte besteht aus den Bits 01, einem Indikator-Bit fuer
        // partielles Recovery und ein paar Padding-Bits
        // laut spez werden 0-nibbled durch 0xb ersetzt, hier aber scheinbar nicht
        // paddedM[padded_size-1-H.length-8-1-nof_zero_bytes-1] = (byte)0x6B;
        paddedM[padded_size-1-H.length-8-1-nof_zero_bytes-1] = (byte)0x60;
        
        // System.out.println("padded string = "+ba2st(paddedM));
        
        // F ist eigentlich paddedM ohne das highest nibble...
        byte[] F = paddedM; 
        
        // hier jetzt die mathematische Operation mit der Integer-Interpretation
        // von F durchfuehren
        BigInteger iSig;
        if (this.privKey instanceof RSAPrivateKey) {
            getLogger().fine("signing with (n,d)-algorithm");
            BigInteger bPrivExponent=((RSAPrivateKey)this.privKey).getPrivateExponent();
            iSig=(new BigInteger(+1,F)).modPow(bPrivExponent,bModulus);
        } else {
            getLogger().fine("signing with (p,q,dP,dQ,qInv)-algorithm");
            RSAPrivateCrtKey2 key2=(RSAPrivateCrtKey2)this.privKey;
            BigInteger        p=key2.getP();
            BigInteger        q=key2.getQ();
            BigInteger        dP=key2.getdP();
            BigInteger        dQ=key2.getdQ();
            BigInteger        qInv=key2.getQInv();
    
            BigInteger encData=new BigInteger(+1,F);
            BigInteger m1=encData.modPow(dP,p);
            BigInteger m2=encData.modPow(dQ,q);
            BigInteger h=m1.subtract(m2).multiply(qInv).mod(p); 
            iSig=m2.add(q.multiply(h));
        }

        // adjust value
        return getSigFromISig(iSig,bModulus);
    }
    
    private static byte[] getSigFromISig(BigInteger iSig, BigInteger modulus)
    {
    	// Es wird der kleinere Wert von sig und n-sig verwendet
    	
        BigInteger iSig2=modulus.subtract(iSig);
        BigInteger sig=null;

        if (iSig.compareTo(iSig2)<0)
            sig=iSig;
        else
            sig=iSig2;

        return sig.toByteArray();
    }

    // -----------------------------------------------------------------------

    @Override
    protected boolean engineVerify(byte[] sig)
    {
    	// System.out.println("verifying signature "+ba2st(sig));
    	
    	BigInteger S = new BigInteger(+1, sig);
        BigInteger exponent = this.pubKey.getPublicExponent();
        BigInteger modulus = this.pubKey.getModulus();

        // es wird sig^exp mod modulus gerechnet
        BigInteger J_=getJfromSig(S,exponent,modulus);
        
        // hier wird als ergebnis entweder x oder n-x verwendet, entsprechend
        // dem letzten Schritt bei der erzeugung der signatur
        BigInteger I_=adjustJ(J_,modulus);
        if (I_==null) {
            getLogger().severe("neither x nor n-x are valid signatures");
            return false;
        }
        
        // message representative F* ist die Bitfolgen-Interpretation von I*
        byte[] F_ = I_.toByteArray();
        // System.out.println("decrypted signature is "+ba2st(F_));

        int last = F_[F_.length-1]&0xFF;
        if (last!=0xBC) {
            getLogger().severe("last nibble is not 0xBC");
        }
        
        byte[] hash  = this.dig.digest();
        // System.out.println("real hash of message is "+ba2st(hash));
        
        byte[] hash_ = new byte[hash.length];
        System.arraycopy(F_,F_.length-1-hash.length, hash_,0, hash.length);
        // System.out.println("recovered hash of message is "+ba2st(hash_));

        return Arrays.equals(hash,hash_);
    }

    private static BigInteger getJfromSig(BigInteger sig,BigInteger exp,BigInteger mod)
    {
        return sig.modPow(exp,mod);
    }
    
    // entweder x oder n-x zurueckgeben
    private static BigInteger adjustJ(BigInteger J, BigInteger modulus)
    {
    	byte[] ba   = J.toByteArray();
    	int    last = ba[ba.length-1];
    	
    	if ((last&0x0F)==0x0C) {
    		return J;
    	}
    	
    	BigInteger twelve=new BigInteger("12");
    	byte[]     modulus2=modulus.subtract(twelve).toByteArray();
    	int        last2=modulus2[modulus2.length-1];
    	
    	if ((last&0x0F) == (last2&0x0F)) {
    		return modulus.subtract(J);
    	}
    	
    	return null;
    }
}
