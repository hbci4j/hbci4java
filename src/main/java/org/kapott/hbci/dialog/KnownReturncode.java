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

import java.util.ArrayList;
import java.util.List;

import org.kapott.hbci.status.HBCIRetVal;

/**
 * Liste von bekannten Returncodes.
 */
public enum KnownReturncode
{
    /**
     * Es liegen weitere Informationen vor - mit Aufsetzpunkt.
     */
    W3040("3040"),
    
    /**
     * Geaenderte Benutzerdaten.
     */
    W3072("3072"),
    
    /**
     * SCA-Ausnahme.
     */
    W3076("3076"),

    /**
     * Die Liste der zugelassenen Zweischritt-Verfahren.
     */
    W3920("3920"),
    
    /**
     * Signatur falsch (generisch)
     */
    E9340("9340"),
    
    /**
     * PIN ist gesperrt
     */
    E9930("9930"),
    
    /**
     * Konto gesperrt wegen falscher PIN
     */
    E9931("9931"),
    
    /**
     * PIN falsch (konkret)
     */
    E9942("9942"),
    
    ;
    
    /**
     * Die Liste der Return-Codes, die als "PIN falsch" interpretiert werden sollen.
     */
    public final static KnownReturncode[] LIST_AUTH_FAIL = new KnownReturncode[]{E9340,E9930,E9931,E9942};
    
    private String code = null;
    
    /**
     * ct.
     * @param code der Code.
     */
    private KnownReturncode(String code)
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
     * Prueft, ob der angegebene Code in der Liste enthalten ist.
     * @param code der zu pruefende Code.
     * @param codes die Liste der Codes.
     * @return true, wenn er in der Liste enthalten ist.
     */
    public static boolean contains(String code, KnownReturncode... codes)
    {
        return find(code,codes) != null;
    }

    /**
     * Prueft, ob der angegebene Code in der Liste enthalten ist.
     * @param code der zu pruefende Code.
     * @param codes die Liste der Codes.
     * @return true, wenn er in der Liste enthalten ist.
     */
    public static KnownReturncode find(String code, KnownReturncode... codes)
    {
        if (code == null || code.length() == 0 || codes == null || codes.length == 0)
            return null;
        
        for (KnownReturncode c:codes)
        {
            if (c.is(code))
                return c;
        }
        
        return null;
    }
    
    /**
     * Sucht nach dem angegebenen Status-Code in den Rueckmeldungen und liefert den Code zurueck.
     * @param rets die Rueckmeldungen.
     * @return der gesuchte Rueckmeldecode oder NULL, wenn er nicht existiert.
     * Es wird der erste gefundene verwendet.
     */
    public HBCIRetVal searchReturnValue(HBCIRetVal[] rets)
    {
        if (rets == null || rets.length == 0)
            return null;
        
        for (HBCIRetVal ret:rets)
        {
            if (this.is(ret.code))
                return ret;
        }
        return null;
    }

    public boolean isIn(HBCIRetVal[] rets)
    {
        return null != searchReturnValue(rets);
    }

    /**
     * Sucht nach dem angegebenen Status-Code in den Rueckmeldungen und liefert den Code zurueck.
     * @param rets die Rueckmeldungen.
     * @return die gesuchten Rueckmeldecodes oder NULL, wenn sie nicht existieren.
     * Es werden alle gefundenen geliefert.
     */
    public List<HBCIRetVal> searchReturnValues(HBCIRetVal[] rets)
    {
        if (rets == null || rets.length == 0)
            return null;

        List<HBCIRetVal> result = new ArrayList<HBCIRetVal>();
        for (HBCIRetVal ret:rets)
        {
            if (this.is(ret.code))
                result.add(ret);
        }
        return result;
    }
}
