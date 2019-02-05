/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.storage.PassportData;
import org.kapott.hbci.passport.storage.format.legacy.Converter;
import org.kapott.hbci.tools.IOUtils;

/**
 * Liest Dateien im alten HBCI4Java-Format.
 */
public class LegacyFormat extends AbstractFormat
{
    private static List<Converter> converters   = null;

    private final static String CIPHER_ALG      = "PBEWithMD5AndDES";
    private final static int CIPHER_ITERATIONS  = 987;

    static
    {
        init();
    }

    /**
     * @see org.kapott.hbci.passport.storage.format.PassportFormat#load(org.kapott.hbci.passport.HBCIPassport, byte[])
     */
    @Override
    public PassportData load(final HBCIPassport passport, final byte[] data) throws UnsupportedOperationException
    {
        // Passenden Converter ermitteln
        final Converter converter = this.getConverter(passport);
        int retries = this.getRetries();
        

        for (int i=0;i<10;++i) // Mehr als 10 mal brauchen wir es nicht versuchen
        {
            InputStream is = null;
            
            try
            {
                final Cipher cipher = this.getCipher();
                
                final SecretKey key = this.getPassportKey(passport, false);
                final PBEParameterSpec paramspec = new PBEParameterSpec(converter.getSalt(), CIPHER_ITERATIONS);

                cipher.init(Cipher.DECRYPT_MODE, key, paramspec);
                
                is = new CipherInputStream(new ByteArrayInputStream(data),cipher);
                return converter.load(is);
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
        // Passenden Converter ermitteln
        final Converter converter = this.getConverter(passport);
        
        CipherOutputStream os = null;
        
        try
        {
            final Cipher cipher = this.getCipher();
            
            final SecretKey key = this.getPassportKey(passport, false);
            final PBEParameterSpec paramspec = new PBEParameterSpec(converter.getSalt(), CIPHER_ITERATIONS);

            cipher.init(Cipher.ENCRYPT_MODE, key, paramspec);

            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            os = new CipherOutputStream(bos,cipher);
            converter.save(data,os);
            os.close(); // Stellt sicher, dass die Verschluesselung abgeschlossen ist
            
            return bos.toByteArray();
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
     * @see org.kapott.hbci.passport.storage.format.AbstractFormat#getCipherAlg()
     */
    @Override
    protected String getCipherAlg()
    {
        return CIPHER_ALG;
    }
    
    /**
     * Fragt den User per Callback nach dem Passwort fuer die Passport-Datei.
     * @param passport der Passport.
     * @param forSaving true, wenn das Passwort zum Speichern erfragt werden soll.
     * @return der Secret-Key.
     * @throws GeneralSecurityException wenn das Passwort nicht ermittelt werden konnte.
     */
    private SecretKey getPassportKey(final HBCIPassport passport, final boolean forSaving) throws GeneralSecurityException
    {
        char[] pw = this.getPassword(passport,forSaving);
        final String provider = this.getSecurityProvider();
        final SecretKeyFactory fac = provider != null ? SecretKeyFactory.getInstance(CIPHER_ALG,provider) : SecretKeyFactory.getInstance(CIPHER_ALG);
        final PBEKeySpec keyspec = new PBEKeySpec(pw);
        final SecretKey passportKey = fac.generateSecret(keyspec);
        keyspec.clearPassword();
        
        return passportKey;
    }

    /**
     * Liefert den passenden Converter.
     * @param p der Passport.
     * @return der Converter.
     */
    private Converter getConverter(HBCIPassport p)
    {
        for (Converter c:converters)
        {
            if (c.supports(p))
                return c;
        }
        
        throw new UnsupportedOperationException("found no matching converter for passport type " + p.getClass().getSimpleName());
    }

    /**
     * Initialisiert die Liste der unterstuetzten Converter.
     */
    private static void init()
    {
        if (converters != null)
            return;

        converters = new LinkedList<Converter>();
        HBCIUtils.log("searching supported converters",HBCIUtils.LOG_DEBUG);
        final ServiceLoader<Converter> loader = ServiceLoader.load(Converter.class);
        for (Converter c:loader)
        {
            HBCIUtils.log("  " + c.getClass().getSimpleName(),HBCIUtils.LOG_DEBUG);
            converters.add(c);
        }
        if (converters.size() == 0)
            HBCIUtils.log("no supported legacy converters found",HBCIUtils.LOG_ERR);
    }
}
