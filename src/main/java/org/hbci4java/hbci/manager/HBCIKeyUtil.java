/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2019 Olaf Willuhn
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

package org.hbci4java.hbci.manager;

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


