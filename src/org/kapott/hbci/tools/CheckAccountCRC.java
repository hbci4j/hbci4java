
/*  $Id: CheckAccountCRC.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;

// TODO: doku anpassen
/** <p>Tool zum Verifizieren der Gültigkeit von BLZ/Kontonummer.
    Alle Kontonummern in Deutschland enthalten eine Prüfziffer, anhand
    welcher überprüft werden kann, ob die Kontonummer an sich gültig ist.
    Mit diesem Tool kann für eine gegebene Bankleitzahl und Kontonummer
    deren Gültigkeit überprüft werden.</p>
    <p>In <em>HBCI4Java</em> sind noch nicht alle von den Banken verwendeten
    Prüfzifferverfahren implementiert. Deshalb können bis jetzt nur
    die Kontonummern von einigen bestimmten Banken überprüft werden. Anhand
    der Ausgabe des Programmes ist ersichtlich, ob <em>HBCI4Java</em> tatsächlich
    die Kontonummer überprüfen konnte und wenn ja, ob die Prüfung erfolgreich
    verlaufen ist oder nicht.</p>
    <p>Der Aufruf erfolgt mit 
    <code>java&nbsp;-cp&nbsp;...&nbsp;org.kapott.hbci.tools.CheckAccountCRC&nbsp;&lt;blz&gt;&nbsp;&lt;kontonummer&gt;</code> */  
public class CheckAccountCRC
{
    public static void main(String[] args)
    {
        if (args.length<1 || args.length>2) {
            System.out.println("usage:");
            System.out.println("  CheckAccountCRC <blz> <accnumber>");
            System.out.println("  CheckAccountCRC <iban>");
            System.exit(1);
        }
        
        HBCIUtils.init(null,new HBCICallbackConsole());
        
        if (args.length==2) {
        	String blz=args[0];
        	String number=args[1];
        	String data=HBCIUtilsInternal.getBLZData(blz);
        	String kiname=data.substring(0,data.indexOf("|"));
        	String alg=HBCIUtilsInternal.getAlgForBLZ(blz);

        	if (kiname.length()!=0) {
        		System.out.println("institute name: "+kiname);
        		System.out.println("algorithmus: "+alg);
        		System.out.println("blz: "+blz);
        		System.out.println("account number: "+number);
        		System.out.println(HBCIUtils.checkAccountCRC(blz,number)?"OK":"not OK");
        	} else {
        		System.out.println("no information about "+blz+" in database");
        	}
        } else {
        	String iban=args[0];
        	System.out.println("IBAN: "+iban);
        	System.out.println(HBCIUtils.checkIBANCRC(iban)?"OK":"not OK");
        }
    }
}