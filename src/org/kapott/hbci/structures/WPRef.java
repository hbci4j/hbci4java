
/*  $Id: WPRef.java,v 1.1 2011/05/04 22:37:49 willuhn Exp $

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

/** Wertpapierreferenz (eine mögliche Identifikation für ein Wertpapier */
public class WPRef
{
    /** Code repräsentiert ein ISIN */
    public static final int TYPE_ISIN=1;
    /** Code repräsentier eine WKN */
    public static final int TYPE_WKN=2;
    /** Code repräsentiert eine kreditinstitutsinterne Bezeichnung */
    public static final int TYPE_KIINTERN=3;
    /** Code repräsentiert einen Indexnamen */
    public static final int TYPE_IDXNAME=4;
    
    /** Typ der Bezeichnung. Mögliche Werte sind
        <ul>
          <li>{@link #TYPE_ISIN}</li>
          <li>{@link #TYPE_WKN}</li>
          <li>{@link #TYPE_KIINTERN}</li>
          <li>{@link #TYPE_IDXNAME}</li>
        </ul>*/
    public int type;
    /** Bezeichnung des Wertpapiert, Interpretation abhängig vom
        Wert von {@link #type} */
    public String code;
    
    public WPRef()
    {
    }

    /** Erstellen eines neuen Wertpapierreferenz-Objektes.
        @param type Typ der Referenz
        @param code Wertpapierbezeichnung abhängig von <code>type</code>*/
    public WPRef(int type,String code)
    {
        this.type=type;
        this.code=code;
    }
}