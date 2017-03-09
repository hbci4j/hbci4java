
/*  $Id: AbstractRDHPassport.java,v 1.1 2011/05/04 22:37:43 willuhn Exp $

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

package org.kapott.hbci.passport;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.kapott.cryptalgs.SignatureParamSpec;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.security.Crypt;
import org.kapott.hbci.security.Sig;


public abstract class AbstractRDHPassport 
	extends AbstractHBCIPassport 
{
    protected AbstractRDHPassport(Object init)
    {
    	super(init);
    }
    
    public String getPassportTypeName()
    {
        return "RDH";
    }

    public boolean isSupported()
    {
        boolean ret=false;
        
        if (getBPD()!=null) {
            String[][] methods=getSuppSecMethods();
            String     passportVersion=getProfileVersion();
            boolean    noPassportVersion=(passportVersion.length()==0);
            
            for (int i=0;i<methods.length;i++) {
                String method=methods[i][0];
                String version=methods[i][1];

                if (method.equals("RDH") && 
                        (noPassportVersion || passportVersion.equals(version))) 
                {
                    // ein RDH-Passport gilt dann als "unterstützt", wenn in den
                    // BPD der SecMech "RDH" auftaucht, und dabei die RDH-Profilnummer
                    // aus den BPD mit der gewünschten RDH-Profilnummer des 
                    // Passports übereinstimmt. 
                    // Es reicht auch nur die Übereinstimmung auf "RDH", wenn
                    // das Passport seine Profil-Nummer noch nicht weiß ("").
                    ret=true;
                    break;
                }
            }
        } else {
            ret=true;
        }
        
        return ret;
    }

    public Comm getCommInstance()
    {
        return Comm.getInstance("Standard",this);
    }
    
    public String getSysStatus()
    {
        return "1";
    }

    public boolean needInstKeys()
    {
        return true;
    }
    
    public boolean needUserKeys()
    {
        return true;
    }
    
    public boolean needUserSig()
    {
        return false;
    }
    
    public String getProfileMethod()
    {
    	return "RDH";
    }
    
    public String getCryptKeyType()
    {
        return Crypt.ENC_KEYTYPE_RSA;
    }

    public String getSigFunction()
    {
        String ret;
        
        // TODO: hier auch höhere hbci-versionen berücksichtigen
        if (getHBCIVersion().equals("300")) {
            // TODO: bei verwendung der digisig hier anderen wert zurückgeben
            ret=Sig.SECFUNC_FINTS_SIG_SIG;
        } else {
            ret=Sig.SECFUNC_HBCI_SIG_RDH;
        }
        
        HBCIUtils.log("using sig function "+ret,HBCIUtils.LOG_DEBUG2);
        return ret;
    }

    public String getSigAlg()
    {
        return Sig.SIGALG_RSA;
    }

    public String getSigMode()
    {
        int    profile=Integer.parseInt(getProfileVersion());
        String ret=null;
        
        switch (profile) {
        case 1:
            ret=Sig.SIGMODE_ISO9796_1;
            break;
        case 2:
            ret=Sig.SIGMODE_ISO9796_2;
            break;
        case 10:
            ret=Sig.SIGMODE_PSS;
            break;
        default:
            // TODO das später vom security profile abhängig machen
            // RDH3: ISO9796-(1|2)
            // RDH4: PKCS1
            // RDH5: PKCS1
            throw new HBCI_Exception("*** dont know which sigmode to use for profile rdh-"+profile);
        }
        
        HBCIUtils.log("using sig mode "+ret,HBCIUtils.LOG_DEBUG2);
        return ret;
    }

    public String getCryptFunction()
    {
        return Crypt.SECFUNC_ENC_3DES;
    }

    public String getCryptAlg()
    {
        return Crypt.ENCALG_2K3DES;
    }

    public String getCryptMode()
    {
        int    profile=Integer.parseInt(getProfileVersion());
        String ret=null;
        
        switch (profile) {
        case 1:
            ret=Crypt.ENCMODE_CBC;
            break;
        case 2:
            ret=Crypt.ENCMODE_CBC;
            break;
        case 10:
            ret=Crypt.ENCMODE_CBC;
            break;
        default:
            // TODO das später vom security profile abhängig machen
            // RDH3: PKCS1
            // RDH4: PKCS1
            // RDH5: PKCS1
            throw new HBCI_Exception("*** dont know which cryptmode to use for profile rdh-"+profile);
        }

        HBCIUtils.log("using crypt mode "+ret,HBCIUtils.LOG_DEBUG2);
        return ret;
    }
    
    protected int getCryptDataSize(Key key)
    {
        int profile=Integer.parseInt(getProfileVersion());
        int ret=-1;
        
        switch (profile) {
        case 1:
        case 2:
        case 10:
            int bits=((RSAPublicKey)key).getModulus().bitLength();
            int bytes=bits>>3;
            if ((bits&0x07)!=0) {
                bytes++;
            }
            ret=bytes;
            break;
        default:
            // TODO das später vom security profile abhängig machen
            throw new HBCI_Exception("*** dont know which crypt data size to use for profile rdh-"+profile);
        }

        HBCIUtils.log("using crypt data size "+ret,HBCIUtils.LOG_DEBUG2);
        return ret;
    }

    public String getHashAlg()
    {
        int    profile=Integer.parseInt(getProfileVersion());
        String ret=null;
        
        switch (profile) {
        case 1:
            ret=Sig.HASHALG_RIPEMD160;
            break;
        case 2:
            ret=Sig.HASHALG_RIPEMD160;
            break;
        case 10:
            ret=Sig.HASHALG_SHA256_SHA256;
            break;
        default:
            // TODO das später vom security profile abhängig machen
            // RDH3: RIPE/SHA
            // RDH4: SHA
            // RDH5: SHA
            throw new HBCI_Exception("*** dont know which hashalg to use for profile rdh-"+profile);
        }

        HBCIUtils.log("using hash alg "+ret,HBCIUtils.LOG_DEBUG2);
        return ret;
    }
    
    public SignatureParamSpec getSignatureParamSpec()
    {
        int    profile=Integer.parseInt(getProfileVersion());
        String hashalg=null;
        String hashprovider=null;
        
        switch (profile) {
        case 1:
            hashalg="RIPEMD160";
            hashprovider="CryptAlgs4Java";
            break;
        case 2:
            hashalg="RIPEMD160";
            hashprovider="CryptAlgs4Java";
            break;
        case 10:
            hashalg="SHA-256";
            // hashprovider=null;
            break;
        default:
            // TODO das später vom security profile abhängig machen
            throw new HBCI_Exception("*** dont know which hash instance to use for profile rdh-"+profile);
        }

        HBCIUtils.log("using hash instance "+hashalg+"/"+hashprovider,HBCIUtils.LOG_DEBUG2);
        
        return new SignatureParamSpec(hashalg, hashprovider);
    }

    protected Signature getSignatureInstance()
    {
        int    profile=Integer.parseInt(getProfileVersion());
        String sigalg=null;
        String sigprovider=null;
        
        switch (profile) {
        case 1:
            sigalg="ISO9796p1";
            sigprovider="CryptAlgs4Java";
            break;
        case 2:
            sigalg="ISO9796p2";
            sigprovider="CryptAlgs4Java";
            break;
        case 10:
            sigalg="PKCS1_PSS";
            sigprovider="CryptAlgs4Java";
            break;
        default:
            // TODO das später vom security profile abhängig machen
            throw new HBCI_Exception("*** dont know which signature instance to use for profile rdh-"+profile);
        }

        HBCIUtils.log("using sig instance "+sigalg+"/"+sigprovider,HBCIUtils.LOG_DEBUG2);
        try {
            Signature sig=Signature.getInstance(sigalg, sigprovider);
            sig.setParameter(getSignatureParamSpec());
            return sig;
        } catch (Exception ex) {
            throw new HBCI_Exception("*** signing of message failed",ex);
        }
    }
    
    protected SecretKey createMsgKey()
    {
        try {
            KeyGenerator generator=KeyGenerator.getInstance("DESede");
            SecretKey key=generator.generateKey();
            SecretKeyFactory factory=SecretKeyFactory.getInstance("DESede");
            DESedeKeySpec spec=(DESedeKeySpec)(factory.getKeySpec(key,DESedeKeySpec.class));
            byte[] bytes=spec.getKey();

            System.arraycopy(bytes,0,bytes,16,8);

            spec=new DESedeKeySpec(bytes);
            key=factory.generateSecret(spec);

            return key;
        } catch (Exception ex) {
            throw new HBCI_Exception("*** can not create message key",ex);
        }
    }
    
    public byte[] hash(byte[] data)
    {
        /* data is the plaintext message to be signed. In most cases, we do NOT
         * have to execute an explicit hashing here, because most signature algorithms
         * already INCLUDE their own hashing step. The only exception is RDH-10,
         * where we have to sign the HASH value, not the HBCI message itself, so
         * in this case we have to execute one round of hashing here */
        
        byte[] result=data;
        
        if (getHashAlg().equals(Sig.HASHALG_SHA256_SHA256)) {
            // only if we need the double-round-hash thing
            
            MessageDigest dig;
            try {
                dig = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            result=dig.digest(data);
        }
        
        return result;
    }

}
