/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **********************************************************************/

package org.hbci4java.hbci.passport.storage.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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

import org.hbci4java.hbci.exceptions.HBCI_Exception;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.passport.HBCIPassport;
import org.hbci4java.hbci.passport.storage.PassportData;
import org.hbci4java.hbci.tools.CryptUtils;
import org.hbci4java.hbci.tools.IOUtils;

/**
 * Implementierung des neuen AES-basierten Formats.
 */
public class AESFormat extends AbstractFormat
{
    private final static String FORMAT_NAME     = "H4JAES"; // Ein paar Bytes am Anfang, anhand derer wir unser eigenes Dateiformat wiedererkennen
    private final static String ENCODING        = "UTF-8";
    
    private final static SecureRandom RAND      = new SecureRandom();
    private final static String KEY_ALG_NAME    = "PBKDF2WithHmacSHA256";
    private final static String KEY_ALG         = "AES";
    private final static String CIPHER_ALG      = "AES/CBC/PKCS5Padding";
    private final static int CIPHER_ITERATIONS  = 64 * 1024;
    private final static int KEY_SIZE           = 256;
    private final static int SALT_SIZE          = 8;

    /**
     * @see org.hbci4java.hbci.passport.storage.format.PassportFormat#load(org.hbci4java.hbci.passport.HBCIPassport, byte[])
     */
    @Override
    public PassportData load(HBCIPassport passport, byte[] data) throws UnsupportedOperationException
    {
        final long started = System.currentTimeMillis();
        
        if (data == null || data.length < 20) // Entweder keine Daten oder einfach zu wenig. Das kann nicht unser Format sein.
          throw new UnsupportedOperationException("not enough data");
        
        int pos = 0;
        int version = 1;
        
        //////////////////////////////////////////////////////////////////
        // Pre-Checks
        try
        {
            // 1. Die ersten Bytes mit dem Formatnamen lesen und checken, ob der Name stimmt
            if (!FORMAT_NAME.equals(new String(Arrays.copyOfRange(data,pos,pos+FORMAT_NAME.length()),ENCODING)))
                throw new UnsupportedOperationException("wrong format identifier, expected: " + FORMAT_NAME);

            pos += FORMAT_NAME.length();
            
            // 2. Versionsnummer checken
            version = data[FORMAT_NAME.length()];
            if (version < 1 || version > 2)
                throw new UnsupportedOperationException("unknown format version (must be 1 or 2)");

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
                final Cipher cipher = this.getCipher();
                final SecretKey key = this.getPassportKey(passport, salt, false);
                cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
                
                is = new ObjectInputStream(new CipherInputStream(new ByteArrayInputStream(Arrays.copyOfRange(data,pos,data.length)),cipher));
                
                PassportData result = null;
                
                if (version == 1)
                {
                  HBCIUtils.log("reading passport format 1, will be migrated to format 2 on next save",HBCIUtils.LOG_INFO);
                  org.kapott.hbci.passport.storage.PassportData old = (org.kapott.hbci.passport.storage.PassportData) is.readObject();
                  result = old.migrate();
                }
                else
                {
                  result = (PassportData) is.readObject();
                }
                
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
     * @see org.hbci4java.hbci.passport.storage.format.PassportFormat#save(org.hbci4java.hbci.passport.HBCIPassport, org.hbci4java.hbci.passport.storage.PassportData)
     */
    @Override
    public byte[] save(HBCIPassport passport, PassportData data) throws UnsupportedOperationException
    {
        final long started = System.currentTimeMillis();
        
        ObjectOutputStream os = null;

        try
        {
            final Cipher cipher = this.getCipher();
            
            // Neues Salt generieren
            final byte[] salt = new byte[SALT_SIZE];
            RAND.nextBytes(salt);

            // Secret Key erzeugen
            final SecretKey key = this.getPassportKey(passport,salt,true);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final AlgorithmParameters params = cipher.getParameters();
            
            // IV erzeugen
            final byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            
            //////////////////////////////////////////////////////////////////
            // Datei-Header schreiben. Der ist immer Klartext, da er vor der Entschluesselung lesbar sein muss
            // 1. Header mit dem Formatnamen
            bos.write(FORMAT_NAME.getBytes(ENCODING));
            
            // 2. Versionsnummer. Wir speichern nur noch im Format 2
            bos.write(2);
            
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
     * @see org.hbci4java.hbci.passport.storage.format.AbstractFormat#getCipherAlg()
     */
    @Override
    protected String getCipherAlg()
    {
        return CIPHER_ALG;
    }
    
    /**
     * @see org.hbci4java.hbci.passport.storage.format.AbstractFormat#supported()
     */
    @Override
    public boolean supported()
    {
        try
        {
            final Cipher cipher = this.getCipher();
            
            // Salt generieren
            final byte[] salt = new byte[SALT_SIZE];
            RAND.nextBytes(salt);
            
            // Zufaelliges Passwort generieren - das Passwort selbst ist egal, wir wollen
            // lediglich testen, ob sich der Cipher initialisieren laesst
            final byte[] b = new byte[10];
            RAND.nextBytes(b);
            final char[] pw = new String(b,StandardCharsets.UTF_8).toCharArray();

            // Secret Key erzeugen
            final SecretKey key = this.getPassportKey(pw,salt);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return super.supported();
        }
        catch (Exception e) {
            HBCIUtils.log("no support for passport format " + this.getClass().getSimpleName() + ": " + e.getMessage(),HBCIUtils.LOG_INFO);
        }
        return false;
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
        try
        {
            char[] pw = this.getPassword(passport,forSaving);
            return this.getPassportKey(pw,salt);
        }
        catch (NoSuchAlgorithmException e)
        {
            HBCIUtils.log("AES-Format not supported in this Java version",HBCIUtils.LOG_DEBUG);
            throw new UnsupportedOperationException("AES-Format not supported in this Java version");
        }
    }

    /**
     * Erzeugt den Secret-Key aus Passwort und Salt.
     * @param password das zu verwendende Passwort.
     * @param salt das zu verwendende Salt.
     * @return der Secret-Key.
     * @throws GeneralSecurityException wenn das Passwort nicht ermittelt werden konnte.
     */
    private SecretKey getPassportKey(final char[] password, final byte[] salt) throws GeneralSecurityException
    {
        try
        {
            final String provider = CryptUtils.getSecurityProvider();
            final SecretKeyFactory fac = provider != null ? SecretKeyFactory.getInstance(KEY_ALG_NAME,provider) : SecretKeyFactory.getInstance(KEY_ALG_NAME);
            final KeySpec spec = new PBEKeySpec(password, salt, CIPHER_ITERATIONS, KEY_SIZE);
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
