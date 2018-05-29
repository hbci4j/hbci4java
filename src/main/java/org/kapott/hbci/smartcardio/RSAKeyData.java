/**
 * 
 */
package org.kapott.hbci.smartcardio;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import org.kapott.hbci.exceptions.HBCI_Exception;

/**
 * @author axel
 *
 */
public class RSAKeyData {
    
    public enum Type {
        
        ENCIPHER(0),
        SIGN(1),
        DECIPHER(2),
        VERIFY(3);
        
        private final int pos;
        
        Type(int pos) {
            this.pos = pos;
        }
        
    }
    
    private final int index;
    private final Type type;
    private final int status;
    private final int keyType;
    private final int keyNum;
    private final int keyVersion;
    private final PublicKey publicKey;
    
    public RSAKeyData(int index, Type type, byte[] keyLogData, byte[] publicKeyData) {
        int offset = type.pos * 8;
        
        if (keyLogData.length < offset + 8)
            throw new HBCI_Exception("keyLogData too short");
        
        this.index = index;
        this.type = type;
        this.status = keyLogData[offset];
        this.keyType = keyLogData[offset + 1];
        this.keyNum = Integer.valueOf(new String(keyLogData, offset + 2, 3, SmartCardService.CHARSET).trim());
        this.keyVersion = Integer.valueOf(new String(keyLogData, offset + 5, 3, SmartCardService.CHARSET).trim());
        
        if (publicKeyData == null || status != 0x10) {
            this.publicKey = null;
        } else {
            if (publicKeyData.length < 0x79)
                throw new HBCI_Exception("publicKeyData too short");
            
            byte algoByte = publicKeyData[6];
            if ((algoByte & 0x01) == 0)
                throw new HBCI_Exception("invalid public key type");
            
            byte modLen = publicKeyData[14];
            byte[] modulus = new byte[modLen];
            byte[] publicExponent = new byte[3];
            if ((algoByte & 0x08) == 0) {
                // MSB
                System.arraycopy(publicKeyData, 20, modulus, 0, modLen);
                System.arraycopy(publicKeyData, 20 + modLen, publicExponent, 0, 3);
            } else {
                // LSB
                for (int n = 0; n < modLen; n++)
                    modulus[n] = publicKeyData[20 + modLen - 1 - n];
                for (int n = 0; n < 3; n++)
                    publicExponent[n] = publicKeyData[20 + modLen + 3 - 1 - n];
            }
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
            
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                this.publicKey = keyFactory.generatePublic(keySpec);
            } catch (NoSuchAlgorithmException e) {
                throw new HBCI_Exception("no support for RSA available", e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public int getIndex() {
        return index;
    }
    
    public Type getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

    public int getKeyType() {
        return keyType;
    }

    public int getKeyNum() {
        return keyNum;
    }

    public int getKeyVersion() {
        return keyVersion;
    }
    
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String toString() {
        return "index=" + index
            + " type=" + type
            + " keyType=0x" + Integer.toHexString(keyType)
            + " status=0x" + Integer.toHexString(status)
            + " keyNum=" + keyNum
            + " keyVersion=" + keyVersion
            + " publicKey=" + publicKey;
    }

}
