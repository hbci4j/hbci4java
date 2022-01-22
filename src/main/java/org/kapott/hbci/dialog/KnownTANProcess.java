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

package org.kapott.hbci.dialog;

import java.util.Objects;

import org.kapott.hbci.tools.StringUtil;

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
    
    /**
     * Prozess-Variante.
     */
    public enum Variant
    {
        /**
         * Prozess-Variante 1.
         */
        V1("1"),
        
        /**
         * Prozess-Variante 2.
         */
        V2("2"),
        
        ;
        
        private final static Variant DEFAULT = V2;
        
        private String code = null;
        
        /**
         * ct.
         * @param code der Code der Prozess-Variante.
         */
        private Variant(String code)
        {
            this.code = code;
        }
        
        /**
         * Liefert die zu verwendende Prozessvariante.
         * @param code der Code der Variante. Nie NULL, sondern hoechstens die Default-Variante.
         * @return die Prozess-Variante.
         */
        public final static Variant determine(String code)
        {
            if (!StringUtil.hasText(code))
                return DEFAULT;
            
            for (Variant v:values())
            {
                if (Objects.equals(code,v.code))
                    return v;
            }
            
            return DEFAULT;
        }
    }
    
    private String code = null;
    
    /**
     * ct.
     * @param code der Prozess-Schritt.
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
     * Ermittelt den passenden TAN-Prozess fuer die Variante und die Schritt-Nummer.
     * @param v die Prozess-Variante.
     * @param step die Schritt-Nummer.
     * @return der TAN-Prozess. Nie NULL, sondern im Zweifel {@link KnownTANProcess#PROCESS2_STEP1}.
     */
    public static KnownTANProcess get(Variant v, int step)
    {
        // Hier gibts nur einen
        if (v == Variant.V1)
            return PROCESS1;

        return step == 2 ? PROCESS2_STEP2 : PROCESS2_STEP1;
    }
}
