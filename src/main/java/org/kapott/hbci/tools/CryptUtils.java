/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.tools;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;

import org.kapott.cryptalgs.CryptAlgs4JavaProvider;
import org.kapott.cryptalgs.SignatureParamSpec;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * Hilfsklasse zum Erzeugen und Pruefen von Signaturen und Hashes sowie zum Ver- und Entschluessen.
 */
public class CryptUtils
{
  /**
   * Hash-Algorithmus RIPEMD160.
   */
  public final static String HASH_ALG_RIPE_MD160 = "RIPEMD160";

  /**
   * Hash-Algorithmus SHA-256.
   */
  public final static String HASH_ALG_SHA256 = "SHA-256";

  /**
   * Hash-Algorithmus SHA1.
   */
  public final static String HASH_ALG_SHA1 = "SHA-1";

  /**
   * Die Liste der Digest-Algorithmen, bei denen der eigene Provider verwendet werden soll.
   */
  private final static List<String> HASH_OWN_PROVIDER = Arrays.asList(HASH_ALG_RIPE_MD160);

  /**
   * Signatur-Alorithmus RSASSA-PSS (PKCS1)
   */
  public final static String SIGN_ALG_RSA = "PKCS1_PSS";

  /**
   * Die Liste der Algorithmen, bei denen der eigene Provider verwendet werden soll.
   */
  private final static List<String> SIGN_OWN_PROVIDER = Arrays.asList(SIGN_ALG_RSA);
  
  /**
   * Verschluesselungsalgorithmus AES.
   */
  public final static String CRYPT_ALG_AES = "AES";

  /**
   * Verschluesselungsalgorithmus AES mit CBC-Padding.
   */
  public final static String CRYPT_ALG_AES_CBC = "AES/CBC/PKCS5Padding";

  /**
   * Hasht die Daten.
   * @param data die zu hashenden Daten.
   * @param alg der zu verwendende Algorithmus.
   * @return der Hash.
   * @throws HBCI_Exception
   */
  public final static String hash(String data, String alg) throws HBCI_Exception
  {
    try
    {
      byte[] hash = hash(data.getBytes(Comm.ENCODING),alg);
      return new String(hash,Comm.ENCODING);
    }
    catch (HBCI_Exception e)
    {
      throw e;
    }
    catch (Exception e2)
    {
        throw new HBCI_Exception(e2);
    }
  }
  
  /**
   * Hasht die Daten.
   * @param data die zu hashenden Daten.
   * @param alg der zu verwendende Algorithmus.
   * @return der Hash.
   * @throws HBCI_Exception
   */
  public final static byte[] hash(byte[] data, String alg) throws HBCI_Exception
  {
    try
    {
      final String provider = HASH_OWN_PROVIDER.contains(alg) ? CryptAlgs4JavaProvider.NAME : null;
      HBCIUtils.log("using " + alg + "/" + provider + " for generating hash of " + data.length + " bytes", HBCIUtils.LOG_DEBUG);
      MessageDigest digest = provider != null ? MessageDigest.getInstance(alg,provider) : MessageDigest.getInstance(alg);
      digest.update(data);
      return digest.digest();
    }
    catch (HBCI_Exception he)
    {
      throw he;
    }
    catch (Exception e)
    {
      throw new HBCI_Exception(e);
    }
  }

  /**
   * Prueft die Gueltigkeit der Signatur.
   * @param data die zu pruefenden Daten.
   * @param signature die Signatur.
   * @param key der Public-Key zur Signatur.
   * @param signAlg der verwendete Signatur-Algorithmus.
   * @param hashAlg der verwendete Hash-Algorithmus.
   * @return true, wenn die Signatur ok war.
   * @throws HBCI_Exception
   */
  public final static boolean verifySignature(byte[] data, byte[] signature, PublicKey key, String signAlg, String hashAlg) throws HBCI_Exception
  {
    try
    {
      final String signProvider = SIGN_OWN_PROVIDER.contains(signAlg) ? CryptAlgs4JavaProvider.NAME : null;
      final String hashProvider = HASH_OWN_PROVIDER.contains(hashAlg) ? CryptAlgs4JavaProvider.NAME : null;
      HBCIUtils.log("using " + signAlg + "+" + hashAlg + "/" + signProvider + "/" + hashProvider + " for verifying signature of " + data.length + " bytes", HBCIUtils.LOG_DEBUG);

      final Signature sig = signProvider != null ? Signature.getInstance(signAlg, signProvider) : Signature.getInstance(signAlg);
      final SignatureParamSpec spec = new SignatureParamSpec(hashAlg, hashProvider);
      sig.setParameter(spec);

      sig.initVerify(key);
      sig.update(data);
      return sig.verify(signature);
    }
    catch (HBCI_Exception he)
    {
      throw he;
    }
    catch (Exception e)
    {
      throw new HBCI_Exception(e);
    }
  }

  /**
   * Signiert die Daten.
   * @param data die zu hashenden Daten.
   * @param key der fuer die Signatur zu verwendende Private-Key.
   * @param signAlg der zu verwendende Signatur-Algorithmus.
   * @param hashAlg der zu verwendende Hash-Algorithmus.
   * @return die Signatur.
   * @throws HBCI_Exception
   */
  public final static byte[] sign(byte[] data, PrivateKey key, String signAlg, String hashAlg) throws HBCI_Exception
  {
    try
    {
      final String signProvider = SIGN_OWN_PROVIDER.contains(signAlg) ? CryptAlgs4JavaProvider.NAME : null;
      final String hashProvider = HASH_OWN_PROVIDER.contains(hashAlg) ? CryptAlgs4JavaProvider.NAME : null;
      HBCIUtils.log("using " + signAlg + "+" + hashAlg + "/" + signProvider + "/" + hashProvider + " for generating signature of " + data.length + " bytes", HBCIUtils.LOG_DEBUG);

      final Signature sig = signProvider != null ? Signature.getInstance(signAlg, signProvider) : Signature.getInstance(signAlg);
      final SignatureParamSpec spec = new SignatureParamSpec(hashAlg, hashProvider);
      sig.setParameter(spec);

      sig.initSign(key);
      sig.update(data);
      return sig.sign();
    }
    catch (HBCI_Exception he)
    {
      throw he;
    }
    catch (Exception e)
    {
      throw new HBCI_Exception(e);
    }
  }

  /**
   * Fuehrt ein Null-Padding links auf die angegebene Laenge durch.
   * @param buffer der Buffer.
   * @param key der Key.
   * @return der ggf. angepasste Buffer.
   */
  public static byte[] padLeft(byte[] buffer, RSAPublicKey key)
  {
    // Macht nichts anderes als getCryptDataSize in AbstractRDHPassport
    final int bits = key.getModulus().bitLength();
    int size = bits / 8;
    if (size % 8 != 0)
      size++;

    if (buffer.length == size)
      return buffer;

    byte[] result = buffer;

    HBCIUtils.log("left-pad " + buffer.length + " bytes to " + size + " bytes length", HBCIUtils.LOG_DEBUG2);
    if (buffer.length > size)
    {
      int diff = buffer.length - size;
      boolean ok = true;

      for (int i = 0; i < diff; i++)
      {
        if (buffer[i] != 0x00)
        {
          HBCIUtils.log("byte " + i + " in data is not zero, but it should be zero", HBCIUtils.LOG_WARN);
          ok = false;
        }
      }

      if (ok)
      {
        HBCIUtils.log("removing " + diff + " unnecessary null-bytes from data", HBCIUtils.LOG_DEBUG);
        result = new byte[size];
        System.arraycopy(buffer, diff, result, 0, size);
      }
    }
    else if (buffer.length < size)
    {
      int diff = size - buffer.length;
      HBCIUtils.log("prepending " + diff + " null bytes to data", HBCIUtils.LOG_DEBUG2);
      result = new byte[size];
      Arrays.fill(result, (byte) 0);
      System.arraycopy(buffer, 0, result, diff, buffer.length);
    }

    return result;
  }
}
