
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
import java.math.BigDecimal;

import org.kapott.hbci.manager.HBCIUtils;

/** Darstellung eines Geldbetrages. */
public final class Value
    implements Serializable
{
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");
    
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
    @Deprecated
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

    /** Anlegen eines Geldbetrag-Objektes. Die Währung wird mit <code>EUR</code> vorbelegt.
    @param value der Geldbetrag als String ("1.23") */
    public Value(BigDecimal value)
    {
        this(value,"EUR");
    }

    /** Anlegen eines Geldbetrag-Objektes.
        @param value der Geldbetrag als String ("1.23")
        @param curr die Währung des Geldbetrages */
    public Value(String value,String curr)
    {
        this(new BigDecimal(value.replace(" ","")),curr);
    }

    /** Anlegen eines Geldbetrag-Objektes.
        @param value der Geldbetrag (1.23)
        @param curr die Währung des Geldbetrages 
        @deprecated */
    @Deprecated
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

    /** Anlegen eines Geldbetrag-Objektes.
    @param value der Geldbetrag (1.23).
    @param curr die Währung des Geldbetrages */
    public Value(BigDecimal value,String curr)
    {
        this.value=value.multiply(ONE_HUNDRED).longValueExact();
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
    @Override
    public String toString()
    {
        return HBCIUtils.bigDecimal2String(new BigDecimal(value).divide(ONE_HUNDRED))+" "+curr;
    }
    
    /** Gibt den Betrag mal 100 als Ganzzahl zurück */
    public long getLongValue()
    {
        return value;
    }
    
    /** Gibt den Betrag als Fließkommazahl zurück */
    @Deprecated
    public double getDoubleValue()
    {
        return value/100.0;
    }
    
    public BigDecimal getBigDecimalValue() {
        BigDecimal result = new BigDecimal(value).divide(ONE_HUNDRED);
        result.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return result;
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
    
    /**
     * Setzt den Betrag neu. Der hier angegebene Wert entspricht dem Betrag mal 100. Wenn der
     * Wert Centbruchteile enthält, welche wegfallen würden, wird eine Exception geworfen.
     * 
     * @param value Der Betrag mal 100
     */
    public void setValue(BigDecimal value)
    {
        this.value = value.multiply(ONE_HUNDRED).longValueExact();
    }
    
    /** Setzt die Währung neu.
     * @param curr die Währung */
    public void setCurr(String curr) 
    {
        this.curr=curr;
    }
}
