
/*  $Id: Value.java,v 1.1 2011/05/04 22:37:49 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.structures;

import java.io.Serializable;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;

/** Darstellung eines Geldbetrages. */
public final class Value
    implements Serializable
{
    /** Numerischer Wert des Betrages mal 100*/
    private long   value;
    
    /** Währung. Für EURO ist hier <code>EUR</code> zu benutzen. */
    private String curr;
    
    /** Anlegen eines neuen Objektes zur Aufnahme eines Geldbetrages. Vorbelegung
     * ist der Wert "0 EUR" */
    public Value()
    {
        this(0,"EUR");
    }
    
    /** Anlegen eines Geldbetrag-Objektes. Die Währung wird mit <code>EUR</code> vorbelegt.
        @param value der Geldbetrag (1.23)
        @deprecated */
    public Value(double value)
    {
        this(Math.round(100.0*value),"EUR");
    }
    
    /** Anlegen eines Geldbetrag-Objektes. Die Währung wird mit <code>EUR</code> vorbelegt.
        @param value der Geldbetrag mal 100 (123) */
    public Value(long value)
    {
        this(value,"EUR");
    }

    /** Anlegen eines Geldbetrag-Objektes. Die Währung wird mit <code>EUR</code> vorbelegt.
        @param value der Geldbetrag als String ("1.23") */
    public Value(String value)
    {
        this(value,"EUR");
    }
    
    /** Anlegen eines Geldbetrag-Objektes.
        @param value der Geldbetrag als String ("1.23")
        @param curr die Währung des Geldbetrages */
    public Value(String value,String curr)
    {
        this(HBCIUtilsInternal.string2Long(value,100),curr);
    }

    /** Anlegen eines Geldbetrag-Objektes.
        @param value der Geldbetrag (1.23)
        @param curr die Währung des Geldbetrages 
        @deprecated */
    public Value(double value,String curr)
    {
        this(Math.round(100.0*value),curr);
    }
    
    /** Anlegen eines Geldbetrag-Objektes.
        @param value der Geldbetrag mal 100 (123)
        @param curr die Währung des Geldbetrages */
    public Value(long value,String curr)
    {
        this.value=value;
        this.curr=curr;
    }
    
    /** Erstellt eine neue Instanz eines Geldbetrag-Objektes als Kopie
        eines bestehenden Objektes.
        @param v ein Objekt, welches geklont werden soll */
    public Value(Value v)
    {
        this(v.value,v.curr);
    }

    /** Umwandeln in einen String. Die Rückgabe erfolgt im Format 
        <pre>&lt;value> " " &lt;curr></pre>
        @return Stringdarstellung des Geldbetrages */
    public String toString()
    {
        return HBCIUtils.value2String(value/100.0)+" "+curr;
    }
    
    /** Gibt den Betrag mal 100 als Ganzzahl zurück */
    public long getLongValue()
    {
        return value;
    }
    
    /** Gibt den Betrag als Fließkommazahl zurück */
    public double getDoubleValue()
    {
        return value/100.0;
    }
    
    /** Gibt die Währung zurück */
    public String getCurr()
    {
        return curr;
    }
    
    /** Setzt den Betrag neu.
     * @param value Betrag (1.23)
     * @deprecated */
    public void setValue(double value)
    {
        setValue(Math.round(100.0*value));
    }
    
    /** Setzt den Betrag neu. Der hier angegebene Wert entspricht dem 
     * eigentlichen Betrag mal 100.
     * @param value Der Betrag mal 100 */
    public void setValue(long value)
    {
        this.value=value;
    }
    
    /** Setzt die Währung neu.
     * @param curr die Währung */
    public void setCurr(String curr) 
    {
        this.curr=curr;
    }
}
