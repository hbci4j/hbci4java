/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.manager;

/**
 * Hilfsklasse mit verschiedenen statischen Methoden fuer den Zugriff auf HBCI-Schluessel.
 */
public class HBCIKeyUtil
{
    /**
     * Liefert die User-ID aus dem Schluessel.
     * @param key der Schluessel.
     * @return die User-ID oder NULL, wenn sie nicht existiert.
     */
    public static String getUserId(HBCIKey key)
    {
      return key !=null ? key.userid : null;
    }

    /**
     * Liefert die Schluesselnummer aus dem Schluessel.
     * @param key der Schluessel.
     * @return die Schluesselnummer oder NULL, wenn sie nicht existiert.
     */
    public static String getNum(HBCIKey key)
    {
        return key !=null ? key.num : null;
    }

    /**
     * Liefert die Version des Schluessels.
     * @param key der Schluessel.
     * @return die Version des Schluessels oder NULL, wenn sie nicht existiert.
     */
    public static String getVersion(HBCIKey key)
    {
        return key != null ? key.version : null;
    }

}


