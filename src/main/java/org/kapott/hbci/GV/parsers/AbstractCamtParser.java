/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2018 Olaf Willuhn
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

package org.kapott.hbci.GV.parsers;

import java.util.ArrayList;
import java.util.List;

import org.kapott.hbci.GV_Result.GVRKUms.BTag;

/**
 * Abstrakte Basis-Implementierung der CAMT-Parser.
 */
public abstract class AbstractCamtParser implements ISEPAParser<List<BTag>>
{
    /**
     * Entfernt die Whitespaces des Textes.
     * Manche Banken fuellen den Gegenkontoinhaber rechts auf 70 Zeichen mit Leerzeichen auf.
     * @param s der Text. NPE-Sicher.
     * @return der getrimmte Text.
     */
    protected String trim(String s)
    {
        if (s == null || s.length() == 0)
            return s;
        
        return s.trim();
    }
    
    /**
     * Entfernt die Whitespaces in der Liste der Texte.
     * @param list Liste der Texte. NPE-Sicher. Leere Zeilen werden uebersprungen.
     * @return die getrimmte Liste.
     */
    protected List<String> trim(List<String> list)
    {
        if (list == null || list.size() == 0)
            return list;
        
        List<String> result = new ArrayList<String>();
        for (String s:list)
        {
            s = trim(s);
            
            if (s == null || s.length() == 0)
                continue;
            
            result.add(s);
        }
        
        return result;
    }

}


