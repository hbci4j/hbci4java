/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
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

package org.kapott.hbci.structures;

import java.io.Serializable;
import java.util.Date;

import org.kapott.hbci.manager.HBCIUtils;

/** Darstellung eines Saldos. Anders als bei der Darstellung als
    einfacher Wert wird hier der <em>absolute</em> Betrag des Wertes
    gespeichert. Es gibt ein separates Kennzeichen für die
    Unterscheidung zwischen Soll und Haben. */
public final class Saldo
    implements Serializable
{
    /** Betrag des Saldos. */
    public Value  value;
    /** Zeitpunkt der Gültigkeit dieses Saldos. */
    public Date   timestamp;

    /** Anlegen eines neuen Saldo-Objektes */
    public Saldo()
    {
        value=new Value();
    }

    /** Umwandeln des Saldos in eine String-Darstellung. Das Format ist dabei folgendes:
        <pre>&lt;timestamp> ["+"|"-"] &lt;value></pre>
        @return Stringdarstellung des Saldos */
    public String toString()
    {
        return HBCIUtils.datetime2StringLocal(timestamp)+" "+value.toString();
    }
}
