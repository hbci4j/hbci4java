
/*  $Id: AnalyzeReportOfTransactions.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.kapott.hbci.GV.GVWPDepotUms;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRWPDepotList;
import org.kapott.hbci.GV_Result.GVRWPDepotUms;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.FileSystemClassLoader;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;

/** <p>Tool zum Abholen und Auswerten von Kontoauszügen, gleichzeitig
    Beispielprogramm für die Verwendung von <em>HBCI4Java</em>. Dieses Tool sollte nicht 
    out-of-the-box benutzt werden, da erst einige Anpassungen im Quelltext
    vorgenommen werden müssen. Es dient eher als Vorlage, wie <em>HBCI4Java</em>
    im konkreten Anwendungsfall eingesetzt werden kann.</p>
    <p>Die Methode {@link #main(String[])} zeigt die Verwendung mit einem einzelnen Haupt-
    Thread. die Methode {@link #main_multithreaded(String[])} skizziert die Implementierung
    für Anwendungen mit mehreren Threads.</p>
    <p>Im Quelltext müssen folgende Stellen angepasst werden:</p>
    <ul>
      <li><p>Beim Aufruf der Methode <code>HBCIUtils.init()</code> wird
          der Name eines Property-Files angegeben, in welchem alle benötigten
          Kernel-Parameter aufgelistet sind. Diese Datei muss erst erzeugt
          (Kopieren und Anpassen von <code>hbci.props.template</code>)
          und der Dateiname beim Aufruf angepasst werden.</p></li>
      <li><p>Zum Festlegen des abzufragenden Kontos wird zurzeit automatisch das
          erste Konto benutzt, auf welches über HBCI zugegriffen werden kann. Ist
          diese Information nicht verfügbar (einige Banken senden keine Informationen
          über die verfügbaren Konten), oder soll eine andere Kontoverbindung
          benutzt werden, so sind entsprechende Änderungen bei der Initialisierung
          der Variablen <code>myaccount</code> vorzunehmen.</p></li>
      <li><p>Soll der Kontoauszug nur ab einem bestimmten Zeitpunkt (und nicht alle
          verfügbaren Daten) abgeholt werden, so ist beim Erzeugen des entsprechenden
          Auftrages das Startdatum einzustellen (im Quelltext zur Zeit auskommentiert).</p></li>
      <li><p>Außerdem ist im Quelltext Code zur eigentlichen Auswertung der Auszüge
          zu implementieren. In dieser Vorlage wird nur nach einer fest codierten
          Rechnungsnummer im Verwendungszweck gesucht. Der entsprechende Abschnitt im
          Quelltext ist den eigenen Bedürfnissen anzupassen.</p></li>
    </ul>
    <p>Anschließend kann der Quelltext compiliert und mit
    <pre>java&nbsp;-cp&nbsp;...&nbsp;org.kapott.hbci.tools.AnalyzeReportOfTransactions</pre>
    gestartet werden.</p>
    <p>Der Quellcode dieser Klasse zeigt die prinzipielle Benutzung von <em>HBCI4Java</em>.
       Wurde der HBCI-Zugang, der mit diesem Programm benutzt werden soll, noch nie verwendet,
       so werden alle benötigten Schritte zur Initialisierung der Zugangsdaten und
       Sicherheitsmedien automatisch von <em>HBCI4Java</em> durchgeführt. Es ist nicht
       nötigt, für die Initialisierung von "frischen" Sicherheitsmedien speziellen
       Code in die HBCI-Anwendung einzubauen -- die entsprechenden Aktionen werden
       automatisch und völlig transparent von <em>HBCI4Java</em> durchgeführt. Das hat
       den Vorteil, dass jede beliebige Anwendung, die <em>HBCI4Java</em> als HBCI-Bibliothek
       benutzt, gleichzeitig zum Initialisieren von HBCI-Sicherheitsmedien benutzt
       werden kann, ohne dass dafür spezieller Programmcode nötig wäre. Außerdem wird dadurch
       sichergestellt, dass nur initialisierte und funktionierende HBCI-Sicherheitsmedien
       benutzt werden (weil <em>HBCI4Java</em> beim Laden eines Sicherheitsmediums automatisch
       entsprechende Überprüfungen vornimmt).</p>*/
// Angepasste Version von AnalyzeReportOfTransactions
// Kurzanleitung:
// 1. .properties-Datei erstellen mit folgendem Inhalt:
//
//# -- Beginn
//client.passport.default=PinTan
//default.hbciversion=300
//log.loglevel.default=2
//
//client.passport.PinTan.filename=/home/jonas/java/hbci/pintan_hbci4java.test
//
//# client.passport.PinTan.certfile=hbcicerts.bin
//client.passport.PinTan.checkcert=1
//# client.passport.PinTan.proxy=proxy.intern.domain.com:3128
//client.passport.PinTan.init=1
//# --Ende
//
// Anzupassen ist nur client.passport.PinTan.filename
// Diese Datei muss nicht existieren, sondern wird beim ersten Start angelegt. Dort fragt HBCI4Java dann auch 
// automatisch die ganzen Informationen zur Verbindung ab (BLZ, Benutzername, usw.)
//
// 2. Unten im Source Code "/home/jonas/java/hbci/jw.hbci4java.properties" durch den Pfad zu dieser .properties-Datei ersetzen
public final class DepotAbrufTest 
{
    private static class MyHBCICallback
        extends HBCICallbackConsole
    {
        public void callback(HBCIPassport passport,int reason,String msg,int dataType,StringBuffer retData)
        {
            if (reason == HBCICallback.CLOSE_CONNECTION || reason == HBCICallback.NEED_CONNECTION)
                return;
            
            System.out.println("Callback für folgendes Passport: "+passport.getClientData("init").toString() + ", reason=" + reason);
            super.callback(passport,reason,msg,dataType,retData);
        }
    }
    
    public static void main(String[] args)
        throws Exception
    {
        // HBCI Objekte
        HBCIPassport passport   = null;
        HBCIHandler  hbciHandle = null;

        // HBCI4Java initialisieren
        HBCIUtils.init(HBCIUtils.loadPropertiesFile(new FileSystemClassLoader(),"/home/jonas/java/hbci/jw.hbci4java.properties"),
                       new MyHBCICallback());

        // Nutzer-Passport initialisieren
        Object passportDescription="Passport für Kontoauszugs-Demo";
        passport=AbstractHBCIPassport.getInstance(passportDescription);
        //passport.clearBPD();

        try {
            // ein HBCI-Handle für einen Nutzer erzeugen
            String version=passport.getHBCIVersion();
            //hbciHandle=new HBCIHandler("300", passport);
            hbciHandle=new HBCIHandler((version.length()!=0)?version:"plus",passport);

            System.out.println("Alle Geschäftsvorfälle in HBCI4Java: " + hbciHandle.getKernel().getAllLowlevelJobs().toString());
            System.out.println("Unterstützte Geschäftsvorfälle der Bank: " + hbciHandle.getSupportedLowlevelJobs().toString());
            
            //"Trockentest" des Umsatzparsers mit vorgebenen Daten
            //test_ums(hbciHandle, "/home/jonas/java/hbci/msg536.txt");
            //test_ums(hbciHandle, "/home/jonas/java/hbci/msg536_hbci-zka.txt");
            
            //Konten ausgeben
            System.out.println("Kontenliste:");
            System.out.println("------------");
            Konto[] konten = passport.getAccounts();
            for (int i=0; i<konten.length; i++) {
                System.out.println("Konto " + i + ":  " + konten[i]);
            }            
            
            BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
            String line;
            int umsatzkto=-1, depotkto=-1;
            
            System.out.print("Bitte Konto für Umsatzliste eingeben (-1, um zu überspringen): ");
            do {
                line = rd.readLine();
                try {
                    umsatzkto = Integer.parseInt(line);
                    if (umsatzkto >= -1 && umsatzkto < konten.length) {
                        break;
                    } else {
                        System.out.println("Ungültiges Konto: " + line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (line != null);
            System.out.print("Bitte Konto für Depotliste eingeben (-1, um zu überspringen): ");
            do {
                line = rd.readLine();
                try {
                    depotkto = Integer.parseInt(line);
                    if (depotkto >= -1 && depotkto < konten.length) {
                        break;
                    } else {
                        System.out.println("Ungültiges Konto: " + line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (line != null);
            
            // Umsätze auflisten (als Demo, dass es grundsätzlich funktioniert)
            if (umsatzkto >= 0)
                analyzeReportOfTransactions(passport, hbciHandle, konten[umsatzkto]);
            
            // Depotinhalt auflisten
            if (depotkto >= 0)
                analyzeDepot(passport, hbciHandle, konten[depotkto]);

        } finally {
            if (hbciHandle!=null) {
                hbciHandle.close();
            } else if (passport!=null) {
                passport.close();
            }
        }
    }

    private static void analyzeReportOfTransactions(HBCIPassport hbciPassport, HBCIHandler hbciHandle, Konto myaccount) {
        // auszuwertendes Konto automatisch ermitteln (das erste verfügbare HBCI-Konto)
        // wenn der obige Aufruf nicht funktioniert, muss die abzufragende
        // Kontoverbindung manuell gesetzt werden:
        // Konto myaccount=new Konto("DE","86055592","1234567890");

        // Job zur Abholung der Kontoauszüge erzeugen
        HBCIJob auszug=hbciHandle.newJob("KUmsAll");
        auszug.setParam("my",myaccount);
        // evtl. Datum setzen, ab welchem die Auszüge geholt werden sollen
        // job.setParam("startdate","21.5.2003");
        auszug.addToQueue();

        // alle Jobs in der Job-Warteschlange ausführen
        HBCIExecStatus ret=hbciHandle.execute();

        GVRKUms result=(GVRKUms)auszug.getJobResult();
        // wenn der Job "Kontoauszüge abholen" erfolgreich ausgeführt wurde
        if (result.isOK()) {
            // kompletten kontoauszug als string ausgeben:
            System.out.println("##############################");
            System.out.println("#####    Umsatzliste   #######");
            System.out.println("##############################");
            System.out.println(result.toString());

//            // kontoauszug durchlaufen, jeden eintrag einmal anfassen:
//
//            List<UmsLine> lines=result.getFlatData();
//            // int  numof_lines=lines.size();
//
//            for (Iterator<UmsLine> j=lines.iterator(); j.hasNext(); ) { // alle Umsatzeinträge durchlaufen
//                UmsLine entry= j.next();
//
//                // für jeden Eintrag ein Feld mit allen Verwendungszweckzeilen extrahieren
//                List<String> usages=entry.usage;
//                // int  numof_usagelines=usages.size();
//
//                for (Iterator<String> k=usages.iterator(); k.hasNext(); ) { // alle Verwendungszweckzeilen durchlaufen
//                    String usageline= k.next();
//
//                    // ist eine bestimmte Rechnungsnummer gefunden (oder welche
//                    // Kriterien hier auch immer anzuwenden sind), ...
//                    if (usageline.equals("Rechnung 12345")) {
//                        // hier diesen Umsatzeintrag (<entry>) auswerten
//
//                        // entry.bdate enthält Buchungsdatum
//                        // entry.value enthält gebuchten Betrag
//                        // entry.usage enthält die Verwendungszweck-zeilen
//                        // mehr Informationen sie Dokumentation zu
//                        //   org.kapott.hbci.GV_Result.GVRKUms
//                    }
//                }
//            }

        } else {
            // Fehlermeldungen ausgeben
            System.out.println("Job-Error");
            System.out.println(result.getJobStatus().getErrorString());
            System.out.println("Global Error");
            System.out.println(ret.getErrorString());
        }
    }
    
    private static void analyzeDepot(HBCIPassport hbciPassport, HBCIHandler hbciHandle, Konto myaccount) {
        myaccount.curr = null;

        // Job zur Abholung des Depotbestands erzeugen
        HBCIJob auszug=hbciHandle.newJob("WPDepotList");
        auszug.setParam("my",myaccount);
        auszug.addToQueue();

        // alle Jobs in der Job-Warteschlange ausführen
        HBCIExecStatus ret=hbciHandle.execute();

        GVRWPDepotList result=(GVRWPDepotList)auszug.getJobResult();
        // wenn der Job "Depotbestand abholen" erfolgreich ausgeführt wurde
        if (result.isOK()) {
            // kompletten Depotbestand als string ausgeben:
            System.out.println("##############################");
            System.out.println("#####    Depotliste    #######");
            System.out.println("##############################");
            System.out.println(result.toString());


        } else {
            // Fehlermeldungen ausgeben
            System.out.println("Job-Error");
            System.out.println(result.getJobStatus().getErrorString());
            System.out.println("Global Error");
            System.out.println(ret.getErrorString());
        }
        
        // Prüfen, ob Depotumsatzabruf unterstützt wird
        if (!hbciHandle.getSupportedLowlevelJobs().containsKey("WPDepotUms")) {
            System.out.println("Abruf der Depotumsätze nicht unterstützt!");
        } else {
            // Job zur Abholung der Depotumsätze erzeugen
            HBCIJob ums=hbciHandle.newJob("WPDepotUms");
            ums.setParam("my",myaccount);
            // evtl. Datum setzen, ab welchem die Umsätze geholt werden sollen
            // job.setParam("startdate","21.5.2003");
            ums.addToQueue();

            // alle Jobs in der Job-Warteschlange ausführen
            ret=hbciHandle.execute();

            GVRWPDepotUms umsRes =(GVRWPDepotUms)ums.getJobResult();
            // wenn der Job "Depotumsätze abholen" erfolgreich ausgeführt wurde
            if (umsRes.isOK()) {
                // komplette Depotumsätze als string ausgeben:
                System.out.println("################################");
                System.out.println("#####    Depotumsätze    #######");
                System.out.println("################################");
                System.out.println(umsRes.toString());


            } else {
                // Fehlermeldungen ausgeben
                System.out.println("Job-Error");
                System.out.println(umsRes.getJobStatus().getErrorString());
                System.out.println("Global Error");
                System.out.println(ret.getErrorString());
            }
        }
    }

    // Testcode für Beispielumsatzdaten, die aus einer Textdatei gelesen werden
    private static class MyGVUms extends GVWPDepotUms {
        public MyGVUms(HBCIHandler handler) {
            super(handler);
            // TODO Auto-generated constructor stub
        }

        public GVRWPDepotUms myExtract(String testdata) {
            HBCIMsgStatus stat = new HBCIMsgStatus();
            stat.getData().put("foo.data536", testdata);
            extractResults(stat, "foo", 0);
            return (GVRWPDepotUms)jobResult;
        }
    }
    
    
    private static void test_ums(HBCIHandler hbciHandle, String fileName) {
        try {
            MyGVUms test = new MyGVUms(hbciHandle);
            FileReader rd=new FileReader(fileName);
            StringBuilder res = new StringBuilder();
            char[] buf = new char[4000];
            int sz;
            while ((sz=rd.read(buf)) >= 0) {
                res.append(buf, 0, sz);
            }
            rd.close();
            
            System.out.println(test.myExtract(res.toString()));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
