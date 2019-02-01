/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.storage.PassportData;
import org.kapott.hbci.tools.IOUtils;

/**
 * Implementierung des neuen AES-basierten Formats.
 */
public class AESFormat extends AbstractFormat
{
    private final static String FORMAT_NAME     = "H4JAES"; // Ein paar Bytes am Anfang, anhand derer wir unser eigenes Dateiformat wiedererkennen
    private final static int FORMAT_VERSION     = 1; // Versionsnummer des Formats
    private final static String ENCODING        = "UTF-8";
    
    private final static SecureRandom RAND      = new SecureRandom();
    private final static String KEY_ALG_NAME    = "PBKDF2WithHmacSHA256";
    private final static String KEY_ALG         = "AES";
    private final static String CIPHER_ALG      = "AES/CBC/PKCS5Padding";
    private final static int CIPHER_ITERATIONS  = 64 * 1024;
    private final static int KEY_SIZE           = 256;
    private final static int SALT_SIZE          = 8;

    /**
     * @see org.kapott.hbci.passport.storage.format.PassportFormat#load(org.kapott.hbci.passport.HBCIPassport, byte[])
     */
    @Override
    public PassportData load(HBCIPassport passport, byte[] data) throws UnsupportedOperationException
    {
        final long started = System.currentTimeMillis();
        
        if (data == null || data.length < 20) // Entweder keine Daten oder einfach zu wenig. Das kann nicht unser Format sein.
          throw new UnsupportedOperationException("not enough data");
        
        int pos = 0;
        
        //////////////////////////////////////////////////////////////////
        // Pre-Checks
        try
        {
            // 1. Die ersten Bytes mit dem Formatnamen lesen und checken, ob der Name stimmt
            if (!FORMAT_NAME.equals(new String(Arrays.copyOfRange(data,pos,pos+FORMAT_NAME.length()),ENCODING)))
                throw new UnsupportedOperationException("wrong format identifier, expected: " + FORMAT_NAME);

            pos += FORMAT_NAME.length();
            
            // 2. Versionsnummer checken
            if (FORMAT_VERSION != data[FORMAT_NAME.length()])
                throw new UnsupportedOperationException("wrong format version, expected: " + FORMAT_VERSION);

            pos += 1; // Fuer die Versionsnummer haben wir 1 Byte vorgesehen
        }
        catch (UnsupportedEncodingException e)
        {
            HBCIUtils.log(e);
            throw new UnsupportedOperationException();
        }
        //
        //////////////////////////////////////////////////////////////////
        
        // Wenn wir hier angekommen sind, ist es auf jeden Fall das richtige Dateiformat.
        // Wenn wir es jetzt nicht lesen koennen, dann kann es sonst auch niemand
        
        
        //////////////////////////////////////////////////////////////////
        // Salt
        int saltLen = data[pos];
        pos += 1;
        
        // Wir checken noch, ob ueberhaupt genuegend Daten im Byte-Array vorhanden sind
        if (data.length < (pos + saltLen))
            throw new HBCI_Exception("passport file corrupted, not enough data");
        
        byte[] salt = Arrays.copyOfRange(data,pos,pos+saltLen);
        pos += saltLen;
        //
        //////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////
        // IV
        int ivLen = data[pos];
        pos += 1;
        
        // Wir checken noch, ob ueberhaupt genuegend Daten im Byte-Array vorhanden sind
        if (data.length < (pos + ivLen))
            throw new HBCI_Exception("passport file corrupted, not enough data");
        
        byte[] iv = Arrays.copyOfRange(data,pos,pos+ivLen);
        pos += ivLen;
        //
        //////////////////////////////////////////////////////////////////
        
        int retries = this.getRetries();

        for (int i=0;i<10;++i) // Mehr als 10 mal brauchen wir es nicht versuchen
        {
            ObjectInputStream is = null;
            
            try
            {
                final String provider = this.getSecurityProvider();
                final SecretKey key = this.getPassportKey(passport, salt, false);
                final Cipher cipher = provider != null ? Cipher.getInstance(CIPHER_ALG,provider) : Cipher.getInstance(CIPHER_ALG);
                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
                is = new ObjectInputStream(new CipherInputStream(new ByteArrayInputStream(Arrays.copyOfRange(data,pos,data.length)),cipher));
                PassportData result = (PassportData) is.readObject();
                
                HBCIUtils.log("used time for decrypting " + data.length + " bytes: " + (System.currentTimeMillis() - started) + " millis",HBCIUtils.LOG_DEBUG);
                return result;
            }
            catch (UnsupportedOperationException uoe)
            {
                throw uoe;
            }
            catch (HBCI_Exception e)
            {
                if (retries-- <= 0)
                    throw e;
            }
            catch (Exception ex)
            {
                if (retries-- <= 0)
                    throw new HBCI_Exception("unable to load passport data",ex);
            }
            finally
            {
                IOUtils.close(is);
            }
        }
        
        throw new HBCI_Exception("unable to load passport data");
    }
    
    /**
     * @see org.kapott.hbci.passport.storage.format.PassportFormat#save(org.kapott.hbci.passport.HBCIPassport, org.kapott.hbci.passport.storage.PassportData)
     */
    @Override
    public byte[] save(HBCIPassport passport, PassportData data) throws UnsupportedOperationException
    {
        final long started = System.currentTimeMillis();
        
        ObjectOutputStream os = null;

        try
        {
            // Neues Salt generieren
            final byte[] salt = new byte[SALT_SIZE];
            RAND.nextBytes(salt);

            // Secret Key erzeugen
            final SecretKey key = this.getPassportKey(passport,salt,true);
            
            final Cipher cipher = Cipher.getInstance(CIPHER_ALG);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final AlgorithmParameters params = cipher.getParameters();
            final byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            //////////////////////////////////////////////////////////////////
            // Datei-Header schreiben. Der ist immer Klartext, da er vor der Entschluesselung lesbar sein muss
            // 1. Header mit dem Formatnamen
            bos.write(FORMAT_NAME.getBytes(ENCODING));
            
            // 2. Versiosnsnummer
            bos.write(FORMAT_VERSION);
            
            // 3. Salt
            bos.write(salt.length);
            bos.write(salt);

            // 4. IV
            bos.write(iv.length);
            bos.write(iv);
            //
            //////////////////////////////////////////////////////////////////

            //////////////////////////////////////////////////////////////////
            // Eigentlichen Datei-Inhalt verschluesselt schreiben
            os = new ObjectOutputStream(new CipherOutputStream(bos,cipher));
            os.writeObject(data);
            //
            //////////////////////////////////////////////////////////////////
            
            os.close(); // Stellt sicher, dass die Verschluesselung abgeschlossen ist
            
            final byte[] result = bos.toByteArray();
            HBCIUtils.log("used time for encrypting passort into " + result.length + " bytes: " + (System.currentTimeMillis() - started) + " millis",HBCIUtils.LOG_DEBUG);
            return result;
        }
        catch (UnsupportedOperationException uoe)
        {
            throw uoe;
        }
        catch (HBCI_Exception e)
        {
            throw e;
        }
        catch (Exception ex)
        {
            throw new HBCI_Exception("unable to load passport data",ex);
        }
        finally
        {
            IOUtils.close(os);
        }
    }
    
    /**
     * Fragt den User per Callback nach dem Passwort fuer die Passport-Datei.
     * @param passport der Passport.
     * @param salt das zu verwendende Salt.
     * @param forSaving true, wenn das Passwort zum Speichern erfragt werden soll.
     * @return der Secret-Key.
     * @throws GeneralSecurityException wenn das Passwort nicht ermittelt werden konnte.
     */
    private SecretKey getPassportKey(final HBCIPassport passport, final byte[] salt, final boolean forSaving) throws GeneralSecurityException
    {
        StringBuffer passphrase = new StringBuffer();
        HBCIUtilsInternal.getCallback().callback(passport,
                                         forSaving ? HBCICallback.NEED_PASSPHRASE_SAVE : HBCICallback.NEED_PASSPHRASE_LOAD,
                                         forSaving ? HBCIUtilsInternal.getLocMsg("CALLB_NEED_PASS_NEW") : HBCIUtilsInternal.getLocMsg("CALLB_NEED_PASS"),
                                         HBCICallback.TYPE_SECRET,
                                         passphrase);
        if (passphrase.length() == 0)
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSZERO"));
        
        LogFilter.getInstance().addSecretData(passphrase.toString(),"X",LogFilter.FILTER_SECRETS);

        try
        {
            final String provider = this.getSecurityProvider();
            final SecretKeyFactory fac = provider != null ? SecretKeyFactory.getInstance(KEY_ALG_NAME,provider) : SecretKeyFactory.getInstance(KEY_ALG_NAME);
            final KeySpec spec = new PBEKeySpec(passphrase.toString().toCharArray(), salt, CIPHER_ITERATIONS, KEY_SIZE);
            final SecretKey tmp = fac.generateSecret(spec);
            final SecretKey secret = new SecretKeySpec(tmp.getEncoded(),KEY_ALG);
            return secret;
        }
        catch (NoSuchAlgorithmException e)
        {
            HBCIUtils.log("AES-Format not supported in this Java version",HBCIUtils.LOG_DEBUG);
            throw new UnsupportedOperationException("AES-Format not supported in this Java version");
        }
    }
}
