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

package org.kapott.hbci.passport.storage.format;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.tools.CryptUtils;

/**
 * Abstrakte Basis-Klasse der Formate.
 */
public abstract class AbstractFormat implements PassportFormat
{
    private final static String CACHE_KEY = "__cached_passphrase__";
    
    /**
     * @see org.kapott.hbci.passport.storage.format.PassportFormat#supported()
     */
    @Override
    public boolean supported()
    {
        try
        {
            this.getCipher();
            return true;
        }
        catch (Exception e) {
            HBCIUtils.log("no support for passport format " + this.getClass().getSimpleName() + ": " + e.getMessage(),HBCIUtils.LOG_INFO);
        }
        return false;
    }
    
    /**
     * Liefert den zu verwendenden Cipher.
     * @return der zu verwendende Cipher.
     * @throws GeneralSecurityException
     */
    protected Cipher getCipher() throws GeneralSecurityException
    {
        final String provider = CryptUtils.getSecurityProvider();
        final String alg      = this.getCipherAlg();
        return provider != null ? Cipher.getInstance(alg,provider) : Cipher.getInstance(alg);
    }
    
    /**
     * Liefert den zu verwendenden Cipher-Algorithmus.
     * @return der zu verwendende Cipher-Algorithmus.
     */
    protected abstract String getCipherAlg();
    
    /**
     * Liefert die Anzahl der Versuche beim Entschluesseln.
     * @return die Anzahl der Versuche beim Entschluesseln.
     */
    int getRetries()
    {
        return Integer.parseInt(HBCIUtils.getParam("client.retries.passphrase","3"));
    }
    
    /**
     * Fragt den User per Callback nach dem Passwort fuer die Passport-Datei.
     * @param passport der Passport.
     * @param forSaving true, wenn das Passwort zum Speichern erfragt werden soll.
     * @return das Passwort.
     * @throws GeneralSecurityException wenn das Passwort nicht ermittelt werden konnte.
     */
    protected char[] getPassword(final HBCIPassport passport, final boolean forSaving) throws GeneralSecurityException
    {
        // Wir cachen das Passwort in der Instanz des Passport. Dann haben wir das selbe Verhalten wir vor der Umstellung
        // auf das neue PassportStorage - dort wurde der SecretKey als Member-Variable im Passport gehalten.
        // Direkt den SecretKey koennen wir aber nicht zwischenspeichern, weil je nach Format unterschiedliche Algorithmen
        // zum Einsatz kommen. Das Passwort ist aber der gemeinsame Nenner.
        char[] pw = (char[]) passport.getClientData(CACHE_KEY);
        if (pw != null && pw.length > 0)
            return pw;
        
        StringBuffer passphrase = new StringBuffer();
        HBCIUtilsInternal.getCallback().callback(passport,
                                         forSaving ? HBCICallback.NEED_PASSPHRASE_SAVE : HBCICallback.NEED_PASSPHRASE_LOAD,
                                         forSaving ? HBCIUtilsInternal.getLocMsg("CALLB_NEED_PASS_NEW") : HBCIUtilsInternal.getLocMsg("CALLB_NEED_PASS"),
                                         HBCICallback.ResponseType.TYPE_SECRET,
                                         passphrase);
        if (passphrase.length() == 0)
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSZERO"));

        String s = passphrase.toString();
        LogFilter.getInstance().addSecretData(s,"X",LogFilter.FILTER_SECRETS);
        
        pw = s.toCharArray();
        passport.setClientData(CACHE_KEY,pw);
        return pw;
    }

}
