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

package org.hbci4java.hbci.manager;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

/** <p>ClassLoader, der für das Laden eines Property-Files aus dem Filesystem benutzt
    werden kann. Dieser ClassLoader kann überall da benutzt werden, wenn eine
    Ressource über einen ClassLoader geladen wird, und wenn die Ressource ein
    File im aktuellen Filesystem ist. Es ist zur Zeit nicht möglich, relative
    Pfadangaben zu benutzen. Das heißt, dass der Pfad zu einer Ressource (Datei)
    immer als vollständiger Pfad von der Wurzel des Dateisystems an spezifiziert
    werden muss.</p>
    <p>Es gibt im Moment nur eine konkrete Anwendung für diese Klasse (siehe dazu
    Initialisierung des HBCI-Kernels mit
    {@link org.hbci4java.hbci.manager.HBCIUtils#init(Properties,org.hbci4java.hbci.callback.HBCICallback)}).</p>*/
public class FileSystemClassLoader 
    extends URLClassLoader
{
    /** Erzeugen einer neuen Instanz dieser Klasse */
    public FileSystemClassLoader()
        throws MalformedURLException
    {
        super(new URL[] {new URL("file:///")});
    }
}
