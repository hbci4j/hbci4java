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

package org.hbci4java.hbci.tools;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.hbci4java.hbci.callback.HBCICallbackConsole;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.passport.AbstractHBCIPassport;
import org.hbci4java.hbci.passport.HBCIPassport;

/** <p>Tool zum Erzeugen eines INI-Briefes. Diese Klasse enthält kein Programmier-API,
    sondern ist direkt mit 
    <code>java&nbsp;org.hbci4java.hbci.tools.INILetter&nbsp;[passporttype&nbsp;[passport-file&nbsp;[textfile]]]</code>
    ausführbar.<p/>
    Mit diesem Tool kann zu einem bereits existierenden RDH-Passport ein
    INI-Brief erzeugt werden. Das Passport muss dabei schon initialisiert
    sein, und es müssen bereits Schlüssel erzeugt worden sein. Dieses Tool
    wird also i.d.R. dann benötigt, wenn ein Passport erstmalig erzeugt wird
    und die Programmausführung mit der Meldung <em>"Es muss ein INI-Brief erzeugt
    werden..."</em> abbricht.</p><p>
    Das erste Kommandozeilenargument <code>passporttype</code> gibt an, ob es sich um ein <code>RDH</code>-,
    ein <code>RDHNew</code>-, ein <code>RDHXFile</code>-Passport-Format handelt. 
    <code>RDH</code>-Passports sollten nicht mehr verwendet werden (siehe dazu auch Datei 
    <code>README.RDHNew</code>).</p>
    <p>Das zweite Kommandozeilenargument <code>passport-file</code> ist dabei die
    Schlüsseldatei, in der die Passport-Daten gespeichert sind (entspricht
    also dem Inhalt von <code>client.passport.*.filename</code>).</p><p>
    Das Argument <code>textfile</code> gibt den Dateinamen einer Datei an,
    in die der INI-Brief geschrieben werden soll. Der INI-Brief wird als
    reine ASCII-Ausgabe erzeugt, welche ausgedruckt, unterschrieben und an die
    Bank versandt werden kann.</p><p>
    Falls ein oder beide Parameter nicht angegeben sind, so fragt das Tool
    interaktiv nach den entsprechenden Daten. </p>*/
public final class INILetter
{
    private static HBCIPassport passport;
    
    private static String getArg(String[] args,int idx,String st)
        throws IOException
    {
        String ret=null;
        
        System.out.print(st+": ");
        System.out.flush();

        if (args!=null && idx<args.length) {
            ret=args[idx];
            System.out.println(ret);
        } else {
            ret=new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        
        return ret;
    }
    
    public static void main(String[] args)
        throws IOException
    {
        String rdhtype=getArg(args,0,"Passport-Typ (RDH, RDHNew oder RDHXFile)");
        String pfilename=getArg(args,1,"Dateiname der Passport-Datei");
        String ifilename=getArg(args,2,"Dateiname für INI-Brief (noch nicht existierende Text-Datei)");
        
        String header="client.passport."+rdhtype;
        
        HBCIUtils.init(null,new HBCICallbackConsole());
        HBCIUtils.setParam(header+".filename",pfilename);
        HBCIUtils.setParam(header+".init","1");

        passport=AbstractHBCIPassport.getInstance(rdhtype);
        org.hbci4java.hbci.passport.INILetter iniletter=new org.hbci4java.hbci.passport.INILetter(passport,org.hbci4java.hbci.passport.INILetter.TYPE_USER);
        
        PrintWriter out=new PrintWriter(new FileWriter(ifilename));
        out.print(iniletter.toString());
        out.close();
    }
}
