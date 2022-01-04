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

import java.util.List;

/**
 * Verschiedene String-Hilfsklassen.
 */
public class StringUtil
{
    /**
     * Liefert zu einem HBCI-Code vom Client den zugehoerigen HBCI-Code des Instituts.
     * @param hbciCode der HBCI-Code des Clients.
     * @return der HBCI-Code des Instituts.
     */
    public static String toInsCode(String hbciCode)
    {
        if (hbciCode == null || hbciCode.length() < 3)
            return hbciCode;
        return new StringBuffer(hbciCode).replace(1,2,"I").toString();
    }

    /**
     * Liefert zu einem HBCI-Code vom Client den zugehoerigen HBCI-Code der BPD/UPD.
     * @param hbciCode der HBCI-Code des Clients.
     * @return der HBCI-Code der BPD/UPD.
     */
    public static String toParameterCode(String hbciCode)
    {
        if (hbciCode == null)
            return hbciCode;
        
        return toInsCode(hbciCode) + "S";
    }
    
    /**
     * Prueft, ob im String Text enthalten ist, der kein Whitespace ist.
     * @param s der zu pruefende Text.
     * @return true, wenn Text enthalten ist.
     * Wenn nur Leerzeichen oder andere Whitespaces enthalten sind, liefert die Funktion false.
     * Ebenso wenn der String NULL ist.
     */
    public static boolean hasText(String s)
    {
        if (s == null || s.length() == 0)
            return false;
        
        return s.trim().length() > 0;
    }
    
    /**
     * Verbindet die Strings in der Liste mit dem Trennzeichen.
     * @param values die Werte.
     * @param sep das Trennzeichen.
     * @return der verbundene String.
     */
    public static String join(List<String> values, String sep)
    {
      if (values == null)
        return null;
      
      final StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (String s:values)
      {
        if (!first && sep != null)
        {
          sb.append(sep);
        }
        first = false;
        sb.append(s);
      }
      return sb.toString();
    }
}


