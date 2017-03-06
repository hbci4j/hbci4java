
/*  $Id: ISO9796p1.java,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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

public class ISO9796p1
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
        try {
            this.dig=MessageDigest.getInstance(this.param.getHashAlg(), this.param.getProvider());
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
    protected byte[] engineSign()
        throws SignatureException
    {
        BigInteger bModulus;
        if (this.privKey instanceof RSAPrivateKey) {
            bModulus=((RSAPrivateKey)this.privKey).getModulus();
        } else {
            RSAPrivateCrtKey2 key2=(RSAPrivateCrtKey2)this.privKey;
            bModulus=key2.getP().multiply(key2.getQ());
        }
        byte[] modulus=bModulus.toByteArray();
        
        byte[] buffer=this.dig.digest();
        byte[] rr=prepareForSig(buffer,bModulus);
        
        byte[] is;
        if (this.privKey instanceof RSAPrivateKey) {
            getLogger().fine("signing with (n,d)-algorithm");
            BigInteger bPrivExponent=((RSAPrivateKey)this.privKey).getPrivateExponent();
            BigInteger bIS=(new BigInteger(+1,rr)).modPow(bPrivExponent,bModulus);
            is=bIS.toByteArray();
        } else {
            getLogger().fine("signing with (p,q,dP,dQ,qInv)-algorithm");
            RSAPrivateCrtKey2 key2=(RSAPrivateCrtKey2)this.privKey;
            BigInteger        p=key2.getP();
            BigInteger        q=key2.getQ();
            BigInteger        dP=key2.getdP();
            BigInteger        dQ=key2.getdQ();
            BigInteger        qInv=key2.getQInv();
    
            BigInteger encData=new BigInteger(+1,rr);
            BigInteger m1=encData.modPow(dP,p);
            BigInteger m2=encData.modPow(dQ,q);
            BigInteger h=m1.subtract(m2).multiply(qInv).mod(p); 
            is=m2.add(q.multiply(h)).toByteArray();
        }

        // adjust value
        byte[] sig=getSigFromIS(is,modulus);

        return sig;
    }
    
    public static byte[] prepareForSig(byte[] buffer,BigInteger bModulus)
        throws SignatureException
    {
        /* padding; 'cause my buffer is already byte-aligned, there
           are no padding bits to be prepended */
        byte[] mp=new byte[buffer.length];
        System.arraycopy(buffer,0,mp,0,buffer.length);

        int k=bModulus.bitLength()-1;
        int z=mp.length;
        int r=1;

        if (!((z<<4)<=(k+3))) {
            throw new SignatureException("16*z is greater than k");
        }

        // extension (concatenate MP multiple times)
        /*double t2=((double)(k))/8;
        if ((k&0x07) != 0)
            t2 += 1.0;
        int t = ((int)(t2)) >> 1;*/
        int t=(k-1)>>4;
        if (((k-1)&0x0F)!=0)
            t++;
        byte[] me=getMEfromMP(mp, t);

        /* creating redundancy by interleaving the extended message bytes
           with redundancy bytes */
        byte[] mr=getMRfromME(me,t,z,r);

        // get intermediate integer
        byte[] ir=getIRfromMR(mr,k);

        // calculate signature
        byte[] rr=ir; // getRRfromIR(ir, k, modulus, privExponent);
        
        return rr;
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

    @Override
    protected boolean engineVerify(byte[] sig)
        throws SignatureException
    {
        BigInteger bExponent = this.pubKey.getPublicExponent();
        byte[] exponent = bExponent.toByteArray();
        BigInteger bModulus = this.pubKey.getModulus();
        byte[] modulus = bModulus.toByteArray();

        byte[] is=getISfromSig(sig,exponent,modulus);

        int[] ks=new int[1];
        byte[] ir=getIRfromIS(is,exponent,modulus,ks);
        int k=ks[0];

        int[] ts=new int[1];
        byte[] mr=getMRfromIR(ir,k,ts);
        int t=ts[0];

        int[] zs=new int[1];
        int[] rs=new int[1];
        byte[] mp=getMPfromMR(mr,t,zs,rs);
        int z=zs[0];
        int r=rs[0];
        
        int datalen=(z<<3)+1-r;
        int databytes=(datalen>>3);
        if ((datalen&0x07)!=0) {
            databytes++;
        }
        byte[] recHash=new byte[databytes];
        System.arraycopy(mp,mp.length-databytes,recHash,0,databytes);
        if ((datalen&0x07)!=0) {
            recHash[0]&=(1<<(datalen&0x07))-1;
        }
        BigInteger hash=new BigInteger(+1, recHash);
        BigInteger hash2=new BigInteger(+1, this.dig.digest());
        
        byte[] me2=getMEfromMP(mp,t);
        byte[] mr2=getMRfromME(me2,t,z,r);

        mr[0] &=(1<<(7-((mr.length <<3)-k)))-1;
        mr2[0]&=(1<<(7-((mr2.length<<3)-k)))-1;

        return hash.equals(hash2) && Arrays.equals(mr,mr2);
    }

    // ---------------------------------------------------------------------------------

    private static byte[] getISfromSig(byte[] sig,byte[] exp,byte[] mod)
    {
        return (new BigInteger(+1,sig)).modPow(new BigInteger(+1,exp),new BigInteger(+1,mod)).toByteArray();
    }

    private static byte[] getIRfromIS(byte[] is,byte[] exp,byte[] mod,int[] ks)
        throws SignatureException
    {
        BigInteger is_b=new BigInteger(+1,is);
        BigInteger mod_b=new BigInteger(+1,mod);
        BigInteger exp_b=new BigInteger(+1,exp);

        BigInteger ret=null;

        if (is_b.mod(new BigInteger("16")).equals(new BigInteger("6")))
            ret=is_b;
        else if (mod_b.subtract(is_b).mod(new BigInteger("16")).equals(new BigInteger("6")))
            ret=mod_b.subtract(is_b);

        if (ret==null && exp_b.mod(new BigInteger("2")).compareTo(new BigInteger("0"))==0)
            if (is_b.mod(new BigInteger("8")).equals(new BigInteger("3")))
                ret=is_b.multiply(new BigInteger("2"));
            else if (mod_b.subtract(is_b).mod(new BigInteger("8")).equals(new BigInteger("3")))
                ret=mod_b.subtract(is_b).multiply(new BigInteger("2"));

        if (ret==null) {
            throw new SignatureException("can not convert IS to IR");
        }

        byte[] reta=ret.toByteArray();
        int k=reta.length<<3;

        for (int idx=0,pos=0;;) {
            if ((reta[idx]&(1<<(7-pos)))!=0)
                break;

            k--;

            if (++pos==8) {
                pos=0;
                idx++;
            }
        }
        ks[0]=k;

        if ((reta[reta.length-1]&0x0F)!=0x06)
            throw new SignatureException("last nibble is not 0x06");

        return reta;
    }

    private static byte Perm(int b)
    {
        return (new byte[]{
                0x0E, 0x03, 0x05, 0x08,
                0x09, 0x04, 0x02, 0x0F,
                0x00, 0x0D, 0x0B, 0x06,
                0x07, 0x0A, 0x0C, 0x01})[b];
    }

    private static byte Perm1(int b)
    {
        return (new byte[]{
                0x08, 0x0F, 0x06, 0x01,
                0x05, 0x02, 0x0B, 0x0C,
                0x03, 0x04, 0x0D, 0x0A,
                0x0E, 0x09, 0x00, 0x07})[b];
    }

    private static byte[] getMRfromIR(byte[] ir,int k,int[] ts)
    {
        /*
        double t2 = ((double)(k)) / 8;
        if ((k & 0x07) != 0)
            t2 += 1.0;
        int t = ((int)(t2)) >> 1;*/
        
        int t=(k-1)>>4;
        if (((k-1)&0x0F)!=0)
            t++;
        ts[0]=t;

        byte[] mr=new byte[2*t];

        int bitsum=0;
        for (int i=0;i<2*t;i++) {
            mr[2*t-1-i]=ir[2*t-1-i];
            bitsum+=8;

            if (bitsum>=k)
                mr[2*t-1-i]&=(1<<(7-(bitsum-k)))-1;
        }

        mr[2*t-1]=(byte)((Perm1((mr[2*t-2]>>4)&0x0F)<<4) | ((mr[2*t-1]>>4)&0x0F));

        return mr;
    }

    private static byte S(int x)
    {
        return (byte)((Perm((x>>4)&0x0F)<<4) | Perm(x&0x0F));
    }

    private static byte[] getMPfromMR(byte[] mr,int t,int[] zs,int[] rs)
        throws SignatureException
    {
        int i;
        for (i=0;i<t;i++) {
            byte sum=(byte)(S(mr[2*t-1 - (2*i)]) ^ mr[2*t-1 - (2*i+1)]);

            if (sum!=0) {
                zs[0]=i+1;
                rs[0]=sum&0x0F;
                break;
            }
        }

        if (i==t) {
            throw new SignatureException("all sums are 0");
        }

        if (rs[0]<1 || rs[0]>8)
            throw new SignatureException("r is not in range 1..8");

        int z=zs[0];
        byte[] mp=new byte[z];
        for (i=0;i<z;i++) {
            mp[z-1-i]=mr[2*t-1-(2*i)];
        }

        return mp;
    }

    private static byte[] getMEfromMP(byte[] mp,int t)
    {
        byte[] ret=new byte[t];
        int sum=0;

        while (sum<t) {
            if (sum+mp.length<=t) {
                System.arraycopy(mp,0,ret,ret.length-sum-mp.length,mp.length);
                sum+=mp.length;
            } else {
                int diff=t-sum;
                System.arraycopy(mp,mp.length-diff,ret,0,diff);
                sum+=diff;
            }
        }

        return ret;
    }

    private static byte[] getMRfromME(byte[] me,int t,int z,int r)
    {
        byte[] mr=new byte[2*t];

        for (int i=0;i<t;i++) {
            mr[2*t-1 - (2*i)]  =me[t-1-i];
            mr[2*t-1 - (2*i+1)]=S(me[t-1-i]);
        }

        mr[(t-z)<<1]^=r;

        return mr;
    }

    private static byte[] getIRfromMR(byte[] mr,int k)
    {
        int    len=mr.length;
        byte[] ir=new byte[len];
        System.arraycopy(mr,0,ir,0,len);

        ir[0]&=(1<<(7-((len<<3)-k)))-1;
        ir[0]|=(1<<(7-((len<<3)-k)));
        ir[len-1]=(byte)(((ir[len-1]&0x0F)<<4) | 0x06);
        return ir;
    }

    /*
    private int jacobi(BigInteger a,BigInteger n)
    {
        int j=1;

        while (a.compareTo(new BigInteger("0"))!=0) {
            while (a.mod(new BigInteger("2")).compareTo(new BigInteger("0"))==0) {
                a=a.divide(new BigInteger("2"));
                
                BigInteger mod8=n.mod(new BigInteger("8"));
                if (mod8.compareTo(new BigInteger("3"))==0 ||
                    mod8.compareTo(new BigInteger("5"))==0) {
                    j=-j;
                }
            }

            BigInteger temp=a;
            a=n;
            n=temp;

            if (a.mod(new BigInteger("4")).compareTo(new BigInteger("3"))==0 &&
                n.mod(new BigInteger("4")).compareTo(new BigInteger("3"))==0) {
                j=-j;
            }

            a=a.mod(n);
        }

        int ret;
        
        if (n.compareTo(new BigInteger("1"))==0)
            ret=j;
        else
            ret=0;
        
        return ret;
    }*/

    /*private byte[] getRRfromIR(byte[] ir, int k, byte[] modulus, byte[] privExponent)
    {
        byte[] ir2 = new byte[ir.length];
        System.arraycopy(ir, 0, ir2, 0, ir.length);
        ir2[0] &= ((1 << (8 - ((ir.length << 3) - k))) - 1);

        BigInteger bIR = new BigInteger(+1, ir2);
        BigInteger bModulus = new BigInteger(+1, modulus);
        BigInteger bRR = null;

        bRR = bIR;

        return bRR.toByteArray();
    }*/

    private static byte[] getSigFromIS(byte[] is,byte[] modulus)
    {
        BigInteger bIS=new BigInteger(+1,is);
        BigInteger bModulus=new BigInteger(+1,modulus);
        BigInteger bIS2=bModulus.subtract(bIS);
        BigInteger bSig=null;

        if (bIS.compareTo(bIS2)<0)
            bSig=bIS;
        else
            bSig=bIS2;

        return bSig.toByteArray();
    }
}
