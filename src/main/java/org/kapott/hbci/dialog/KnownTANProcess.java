/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.dialog;

/**
 * Enthaelt die Liste der bekannten TAN-Prozesse.
 */
public enum KnownTANProcess
{
    /**
     * Prozess-Variante 1.
     */
    PROCESS1("1"),
    
    /**
     * Prozess-Variante 2, Schritt 1.
     */
    PROCESS2_STEP1("4"),
    
    /**
     * Prozess-Variante 2, Schritt 2.
     */
    PROCESS2_STEP2("2"),
    
    ;
    
    private String code = null;
    
    /**
     * ct.
     * @param code
     */
    private KnownTANProcess(String code)
    {
        this.code = code;
    }

    /**
     * Prueft der angegebene Code identisch ist.
     * @param code der zu pruefende Code.
     * @return true, wenn der Code identisch ist.
     */
    public boolean is(String code)
    {
        return code != null && this.code.equals(code);
    }
    
    /**
     * Liefert den Code des TAN-Prozess-Schrittes.
     * @return der Code des TAN-Prozess-Schrittes.
     */
    public String getCode()
    {
        return this.code;
    }
    
    /**
     * Ermittelt den passenden TAN-Prozess fuer den angegebenen Code.
     * @param code der Code.
     * @return der TAN-Prozess oder NULL, wenn er nicht gefunden wurde.
     */
    public static KnownTANProcess determine(String code)
    {
        if (code == null || code.length() == 0)
            return null;
        
        for (KnownTANProcess t:values())
        {
            if (t.is(code))
                return t;
        }
        
        return null;
    }
}
