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

package org.hbci4java.hbci.callback;

import java.io.BufferedReader;
import java.io.InputStreamReader;



/** Default-Implementation einer Callback-Klasse für textbasierte Anwendungen. Diese Klasse
    ist eine vollständig funktionsfähige Callback-Klasse und kann direkt in eigenen
    Anwendungen benutzt werden. Sie basiert auf der Klasse {@link HBCICallbackIOStreams} 
    für Stream-basierte Ein-/Ausgabe und verwendet STDIN bzw. STDOUT als
    Input- resp. Output-Streams. */
public class HBCICallbackConsole
    extends HBCICallbackIOStreams
{
    public HBCICallbackConsole() {
        super(System.out, new BufferedReader(new InputStreamReader(System.in)));
    }
}
