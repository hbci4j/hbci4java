/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format;

import java.security.GeneralSecurityException;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.HBCIPassport;

/**
 * Abstrakte Basis-Klasse der Formate.
 */
public abstract class AbstractFormat implements PassportFormat
{
    private final static String CACHE_KEY = "__cached_passphrase__";
    
    /**
     * Liefert einen optionalen Security-Provier. 
     * @return der Security-Provider oder NULL, wenn keiner definiert ist.
     */
    String getSecurityProvider()
    {
        return HBCIUtils.getParam("kernel.security.provider");
    }
    
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
                                         HBCICallback.TYPE_SECRET,
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
