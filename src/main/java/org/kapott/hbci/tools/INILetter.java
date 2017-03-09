
/*  $Id: INILetter.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;

/** <p>Tool zum Erzeugen eines INI-Briefes. Diese Klasse enthält kein Programmier-API,
    sondern ist direkt mit 
    <code>java&nbsp;org.kapott.hbci.tools.INILetter&nbsp;[passporttype&nbsp;[passport-file&nbsp;[textfile]]]</code>
    ausführbar.<p/>
    Mit diesem Tool kann zu einem bereits existierenden RDH-Passport ein
    INI-Brief erzeugt werden. Das Passport muss dabei schon initialisiert
    sein, und es müssen bereits Schlüssel erzeugt worden sein. Dieses Tool
    wird also i.d.R. dann benötigt, wenn ein Passport erstmalig erzeugt wird
    und die Programmausführung mit der Meldung <em>"Es muss ein INI-Brief erzeugt
    werden..."</em> abbricht.</p><p>
    Das erste Kommandozeilenargument <code>passporttype</code> gibt an, ob es sich um ein <code>RDH</code>-,
    ein <code>RDHNew</code>-, ein <code>SIZRDHFile</code>- oder ein <code>RDHXFile</code>-Passport-Format handelt. 
    <code>RDH</code>-Passports sollten nicht mehr verwendet werden (siehe dazu auch Datei 
    <code>README.RDHNew</code>). Für <code>SIZRDHFile</code>-Passports wird eine separate
    Bibliothek benötigt (siehe {@link org.kapott.hbci.passport.HBCIPassportSIZRDHFile}).</p>
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
        String rdhtype=getArg(args,0,"Passport-Typ (RDH, RDHNew, SIZRDHFile oder RDHXFile)");
        String pfilename=getArg(args,1,"Dateiname der Passport-Datei");
        String ifilename=getArg(args,2,"Dateiname für INI-Brief (noch nicht existierende Text-Datei)");
        
        String header="client.passport."+rdhtype;
        
        HBCIUtils.init(null,new HBCICallbackConsole());
        HBCIUtils.setParam(header+".filename",pfilename);
        HBCIUtils.setParam(header+".init","1");

        if (rdhtype.equals("SIZRDHFile")) {
            String libname=getArg(args,3,"Dateiname der SIZ-RDH-Bibliothek");
            HBCIUtils.setParam(header+".libname",libname);
        }

        passport=AbstractHBCIPassport.getInstance(rdhtype);
        org.kapott.hbci.passport.INILetter iniletter=new org.kapott.hbci.passport.INILetter(passport,org.kapott.hbci.passport.INILetter.TYPE_USER);
        
        PrintWriter out=new PrintWriter(new FileWriter(ifilename));
        out.print(iniletter.toString());
        out.close();
    }
}
