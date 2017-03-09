
/*  $Id: HBCICallbackConsole.java,v 1.1 2011/05/04 22:37:51 willuhn Exp $

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

package org.kapott.hbci.callback;

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
