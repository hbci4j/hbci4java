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

/**
 * Hilfsmethoden fuer das Handling mit Zahlen.
 */
public class NumberUtil
{
    /**
     * Parst den Text als Zahl.
     * Die Funktion wirft keine Exception. Weder bei NULL noch bei einer nicht zu parsenden Zahl. In dem Fall wird der Default-Wert geliefert.
     * @param s der zu parsende String.
     * @param defaultValue der Default-Wert.
     * @return der geparste Wert oder der Default-Wert.
     */
    public static int parseInt(String s, int defaultValue)
    {
        if (s == null)
            return defaultValue;
        
        s = s.trim();
        if (s.length() == 0)
            return defaultValue;
        
        try
        {
            return Integer.parseInt(s);
        }
        catch (Exception e)
        {
        }
        return defaultValue;
    }

}


