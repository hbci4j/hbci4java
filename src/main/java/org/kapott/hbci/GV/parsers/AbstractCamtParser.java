/**********************************************************************
 *
 * Copyright (c) 2018 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
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


