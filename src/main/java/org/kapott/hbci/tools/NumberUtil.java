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


