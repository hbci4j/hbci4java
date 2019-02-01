/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format;

import org.kapott.hbci.manager.HBCIUtils;

/**
 * Abstrakte Basis-Klasse der Formate.
 */
public abstract class AbstractFormat implements PassportFormat
{
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
}
