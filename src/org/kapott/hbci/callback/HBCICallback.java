
/*  $Id: HBCICallback.java,v 1.2 2011/05/09 15:07:02 willuhn Exp $

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

import java.util.Date;
import java.util.Properties;

import org.kapott.hbci.passport.HBCIPassport;

/** <p>Schnittstelle, die eine Callback-Klasse implementieren muss. Beim Initialisieren von <em>HBCI4Java</em> 
    ({@link org.kapott.hbci.manager.HBCIUtils#init(Properties,org.kapott.hbci.callback.HBCICallback)})
    muss ein Callback-Objekt angegeben werden. Die Klasse dieses Objektes muss die HBCICallback-Schnittstelle
    implementieren. Der HBCI-Kernel ruft in bestimmten Situationen Methoden dieser Klasse auf. Das ist
    z.B. dann der Fall, wenn eine bestimmte Aktion (Einlegen der Chipkarte) oder Eingabe (Passwort)
    vom Anwender erwartet wird. Außerdem werden auf diesem Weg Informationen an den Anwender weitergegeben
    (Mitteilungen des Kreditinstitutes bei der Dialoginitialisierung).</p>
    <p>Ein Anwendungsentwickler muss die Methoden dieser Schnittstelle also geeignet implementieren,
    um bei jeder möglichen Ursache für den Aufruf einer der Callback-Methoden sinnvoll zu reagieren.
    Dabei müssen nicht immer tatsächlich alle Anfragen an den Anwender weitergegeben werden. Ist z.B.
    das Passwort für die Schlüsseldatei eines Passports bereits bekannt, so kann die entsprechende
    Methode dieses Passwort direkt zurückgeben, ohne den Anwender erneut danach zu fragen. </p>*/ 
public interface HBCICallback
{
    /** Ursache des Callback-Aufrufes: Chipkarte benötigt (im Chipkartenterminal). Dieser Callback
        tritt auf, wenn der HBCI-Kernel auf das Einlegen der HBCI-Chipkarte in den Chipkartenleser
        wartet. Als Reaktion auf diesen Callback darf nur eine entsprechende Aufforderung o.ä.
        angezeigt werden, die Callback-Methode muss anschließend sofort beendet werden. Das eigentliche
        "Warten" auf die Chipkarte sowie das Erkennen, dass eine Chipkarte eingelegt wurde,
        wird von <em>HBCI4Java</em> übernommen. Ist das Einlegen der Chipkarte abgeschlossen, so wird ein
        weiterer Callback mit dem Code <code>HAVE_CHIPCARD</code> erzeugt.*/
    public final static int NEED_CHIPCARD=2;
    /** Ursache des Callback-Aufrufes: PIN-Eingabe am Chipkartenterminal erwartet. Dieser Callback
        zeigt an, dass der Anwender jetzt die HBCI-PIN am Chipkartenterminal eingeben muss. Hier
        gilt das gleiche wie beim Code <code>NEED_CHIPCARD</code>: Die Callback-Methode darf hier
        nur eine entsprechende Meldung o.ä. anzeigen und muss dann sofort zurückkehren -- <em>HBCI4Java</em> erledigt die
        eigentliche Entgegennahme der PIN. Wurde die PIN eingegeben (oder die Eingabe abgebrochen),
        so wird ein weiterer Callback-Aufruf mit dem Code <code>HAVE_HARDPIN</code> erzeugt. */
    public final static int NEED_HARDPIN=3;
    /** Ursache des Callback-Aufrufes: PIN-Eingabe über Computer-Tastatur benötigt. Alternativ zum
        Callback <code>NEED_HARDPIN</code> kann dieser Callback auftreten, wenn die direkte PIN-Eingabe
        am Chipkartenterminal nicht möglich oder deaktiviert ist. In diesem Fall muss die PIN
        "softwaremäßig" eingegeben werden, d.h. der Anwender gibt die PIN über die PC-Tastatur
        ein, welche über diesen Callback-Aufruf an den HBCI-Kernel übergeben wird. Der Kernel
        übermittelt die PIN anschließend zur Verifikation an die Chipkarte. In diesem Falle gibt es
        keinen weiteren Callback-Aufruf, wenn die PIN-Verifikation abgeschlossen ist! */
    public final static int NEED_SOFTPIN=4;
    /** Ursache des Callback-Aufrufes: PIN-Eingabe über Chipkartenterminal abgeschlossen. Dieser Callback
        tritt auf, wenn die direkte PIN-Eingabe am Chipkartenleser abgeschlossen (oder abgebrochen) ist.
        Dieser Aufruf kann dazu genutzt werden, evtl. angezeigte Meldungsfenster ("Bitte jetzt PIN eingeben")
        wieder zu schließen. */ 
    public final static int HAVE_HARDPIN=5;
    /** Ursache des Callback-Aufrufes: Chipkarte wurde in Chipkartenterminal eingelegt. Dieser Callback
        tritt auf, wenn das Einlegen der Chipkarte in den Chipkartenleser abgeschlossen (oder abgebrochen) ist.
        Dieser Aufruf kann dazu genutzt werden, evtl. angezeigte Meldungsfenster ("Bitte jetzt Karte einlegen einlegen")
        wieder zu schließen. */
    public final static int HAVE_CHIPCARD=6;
    /** Ursache des Callback-Aufrufes: Länderkennzeichen der Bankverbindung benötigt. Der Kernel benötigt
        für ein neu zu erstellendes Passport-Medium das Länderkennzeichen der Bank, für die dieses
        Passport benutzt werden soll. Da es sich i.d.R. um deutsche Banken handelt, kann die Callback-Routine
        hier immer "DE" zurückgeben, anstatt tatsächlich auf eine Nutzereingabe zu warten. */
    public final static int NEED_COUNTRY=7;
    /** Ursache des Callback-Aufrufes: Bankleitzahl der Bank benötigt. Für ein neu zu erstellendes Passport-Medium
        wird die Bankleitzahl der Bank benötigt, für die dieses Passport verwendet werden soll. */
    public final static int NEED_BLZ=8;
    /** Ursache des Callback-Aufrufes: Netzwerkadresse des HBCI-Servers benötigt. Es wird die Hostadresse
        benötigt, unter welcher der HBCI-Server der Bank zu erreichen ist. Dieses Callback tritt nur auf,
        wenn der Kernel ein neues Passport-Medium erzeugt. Bei RDH- bzw. DDV-Passports wird hier eine
        IP-Adresse oder ein vollständiger Hostname erwartet. Für PIN/TAN-Passports wird hier die URL
        erwartet, unter der der HBCI-PIN/TAN-Handler auf entsprechende HTTPS-Requests reagiert. Dabei
        muss das Prefix "<code>https://</code>" weggelassen werden (also beispielsweise 
        "<code>www.hbci-kernel.de/pintan/PinTanServlet</code>").*/
    public final static int NEED_HOST=9;
    /** Ursache des Callback-Aufrufes: TCP-Port, auf dem der HBCI-Server arbeitet (3000), benötigt. Dieser
        Callback tritt nur auf, wenn ein neues Passport-Medium vom Kernel erzeugt wird. Da die TCP-Portnummer
        für HBCI-Server immer "3000" ist, kann dieser Wert direkt von der Callback-Methode zurückgegeben
        werden, anstatt auf eine Nutzereingabe zu warten. */
    public final static int NEED_PORT=10;
    /** Ursache des Callback-Aufrufes: Nutzerkennung für HBCI-Zugang benötigt. Wird beim Anlegen eines neuen
        Passport-Mediums und manchmal beim erstmaligen Benutzen einer DDV-Chipkarte erzeugt, wenn auf der
        Chipkarte die Benutzerkennung noch nicht gespeichert ist. */
    public final static int NEED_USERID=11;
    /** Ursache des Callback-Aufrufes: Bestätigung für neue Instituts-Schlüssel benötigt (INI-Brief-Vergleich).
        Dieser Callback tritt nur bei Verwendung des RDH-Verfahrens auf. Bei einer Dialoginitialisierung
        versucht <em>HBCI4Java</em>, die öffentlichen Schlüssel des Kreditinstitutes zu aktualisieren. Werden
        tatsächlich neue Schlüsseldaten empfangen (was i.d.R. nur beim erstmaligen Initialisieren eines
        Passport-Mediums auftritt), so müssen diese Schlüsseldaten vom Anwender verifiziert werden. Dazu muss
        er die Schlüsseldaten, die <em>HBCI4Java</em> empfangen hat, mit den Daten vergleichen, die die Bank in
        einem INI-Brief mitgeteilt hat. Erst wenn dieser Vergleich positiv abläuft, wird <em>HBCI4Java</em> diese
        Schlüssel für die Kommunikation mit der Bank benutzen.
        <p>Beim Auftreten dieses Callbacks muss die Anwendung also die gerade empfangenen Schlüsseldaten der
        Bank (öffentlicher Signier-/Chiffrierschlüssel) geeignet anzeigen (Exponent, Modulus, Hash-Wert) und
        den Anwender auffordern, diese Daten mit denen aus dem INI-Brief zu vergleichen. Dieser Callback
        erwartet als Rückgabedaten einen Boolean-Wert (siehe {@link #TYPE_BOOLEAN}). Sind die Daten
        in Ordnung, so muss die Callback-Methode einen leeren String in dem Rückgabedaten-StringBuffer
        zurückgeben, ansonsten füllt sie den StringBuffer mit einem beliebigen nichtleeren String (siehe dazu
        {@link #callback(org.kapott.hbci.passport.HBCIPassport,int,String,int,StringBuffer)} und
        die Beschreibung des Rückgabe-Datentyps {@link #TYPE_BOOLEAN})).</p>
        <p>Da im Moment keine dokumentierten Methoden zur Verfügung stehen, um aus einem Passport die
        entsprechenden Schlüsseldaten zum Anzeigen zu extrahieren, wird folgendes Vorgehen empfohlen:
        die Anwendung erzeugt eine HBCICallback-Klasse, die von einer der bereits vorhandenen 
        Default-Implementationen ({@link org.kapott.hbci.callback.HBCICallbackConsole},
        {@link org.kapott.hbci.callback.HBCICallbackSwing}, ...) abgeleitet ist. Tritt dieser Callback
        auf, so kann die Anwendung mit <code>super.callback(...)</code> die bereits implementierte
        Version des entsprechenden Handlers aufrufen. In diesen Default-Implementationen werden zur Zeit
        nicht dokumentierte Passport-Funktionen benutzt, um die Schlüsseldaten zu extrahieren.</p>*/
    public final static int NEED_NEW_INST_KEYS_ACK=12;
    /** Ursache des Callback-Aufrufes: neue Nutzerschlüssel generiert (INI-Brief erforderlich). Dieser Callback
        tritt nur bei Verwendung von RDH-Passports auf. Wird ein RDH-Passport neu erstellt, so werden für
        den Bankkunden neue Schlüssel für die Signierung und Verschlüsselung der HBCI-Nachrichten erzeugt.
        Die öffentlichen Teile dieser Schlüssel werden von <em>HBCI4Java</em> an die Bank gesandt. Diese schaltet
        die neuen Schlüssel aber erst dann frei, wenn ihre Authentizität durch einen INI-Brief bestätigt
        wird, den der Kunde erzeugen und ebenfalls an die Bank senden muss (per Post oder Fax).
        <p>Nach der Schlüsselerzeugung und dem erfolgreichen Versand der Schlüsseldaten erzeugt <em>HBCI4Java</em>
        also diesen Callback. Die Anwendung muss in diesem Fall den Anwender darüber informieren, dass
        seine neuen Schlüssel erst dann freigeschaltet werden, wenn er einen entsprechenden INI-Brief
        generiert und zur Bank geschickt hat (und diese die Schlüsseldaten auf Übereinstimmung verglichen
        hat). Zum Generieren eines INI-Briefes kann das Tool {@link org.kapott.hbci.tools.INILetter}
        benutzt werden, was Teil von <em>HBCI4Java</em> ist.</p>
        <p>Nachdem dieser Callback abgearbeitet wurde, wirft <em>HBCI4Java</em> eine Exception (<code>NeedKeyAckException</code>)
        und bricht damit die Ausführung des aktuellen HBCI-Dialoges ab. Ein HBCI-Dialog zum Ausführen von
        Geschäftsvorfällen kann erst dann wieder stattfinden, wenn die Bank die Schlüssel freigeschaltet hat.
        Wird ein HBCI-Dialog begonnen, obwohl die Bank die neuen Schlüssel noch nicht aktiviert hat,
        wird der HBCI-Server mit einer entsprechenden Fehlermeldung beim Initialisieren des HBCI-Dialoges
        antworten.</p>*/
    public final static int HAVE_NEW_MY_KEYS=13;
    /** Ursache des Callback-Aufrufes: Institutsnachricht erhalten. Tritt dieser Callback auf, so enthält
        der <code>msg</code>-Parameter der <code>callback</code>-Methode (siehe
        {@link #callback(org.kapott.hbci.passport.HBCIPassport,int,String,int,StringBuffer)} einen
        String, den die Bank als Kreditinstitutsnachricht an den Kunden gesandt hat. Diese Nachricht sollte
        dem Anwender i.d.R. angezeigt werden. <em>HBCI4Java</em> erwartet auf diesen Callback keine Antwortdaten. */
    public final static int HAVE_INST_MSG=14;
    /** Ursache des Callback-Aufrufes: Chipkarte soll aus Chipkartenterminal entfernt werden. Dieser Callback
        wird zur Zeit noch nicht benutzt. */
    public final static int NEED_REMOVE_CHIPCARD=15;
    /** Ursache des Callback-Aufrufes: PIN für PIN/TAN-Verfahren benötigt. Dieser Callback tritt nur bei
        Verwendung von PIN/TAN-Passports auf. Benötigt <em>HBCI4Java</em> die PIN, um die digitale Signatur zu
        erzeugen, wird sie über diesen Callback abgefragt. */
    public final static int NEED_PT_PIN=16;
    /** Ursache des Callback-Aufrufes: eine TAN für PIN/TAN-Verfahren benötigt. Dieser Callback tritt nur bei
        Verwendung von PIN/TAN-Passports auf. Benötigt <em>HBCI4Java</em> eine TAN, um eine digitale Signatur zu
        erzeugen, wird sie über diesen Callback abgefragt. */
    public final static int NEED_PT_TAN=17;
    /** Ursache des Callback-Aufrufes: Kunden-ID für HBCI-Zugang benötigt. Dieser Callback tritt nur beim
        Erzeugen eines neuen Passports auf. <em>HBCI4Java</em> benötigt die Kunden-ID, die das Kreditinstitut
        dem Bankkunden zugewiesen hat (steht meist in dem Brief mit den Zugangsdaten). Hat eine Bank einem
        Kunden keine separate Kunden-ID zugewiesen, so muss an dieser Stelle die Benutzer-Kennung (User-ID)
        zurückgegeben werden. */
    public final static int NEED_CUSTOMERID=18;
    /** <p>Ursache des Callback-Aufrufes: Fehler beim Verifizieren einer Kontonummer mit Hilfe
        des jeweiligen Prüfzifferverfahrens. Tritt dieser Callback auf, so hat <em>HBCI4Java</em>
        festgestellt, dass eine verwendete Kontonummer den Prüfziffercheck der dazugehörigen Bank nicht
        bestanden hat. Der Anwender soll die Möglichkeit erhalten, die Kontonummer und/oder 
        Bankleitzahl zu korrigieren. Dazu wird ein String in der Form "BLZ|KONTONUMMER" im Parameter
        <code>retData</code> der <code>callback</code>-Methode übergeben. Die Anwendung kann dem
        Anwender also BLZ und Kontonummer anzeigen und diese evtl. ändern lassen. Die neue BLZ und
        Kontonummer muss im Ergebnis wieder in der o.g. Form in die Rückgabevariable
        <code>retData</code> eingetragen werden. Wurden BLZ oder Kontonummer geändert,
        so führt <em>HBCI4Java</em> eine erneute Prüfung der Daten durch - schlägt diese
        wieder fehl, so wird der Callback erneut erzeugt, diesmal natürlich mit den neuen
        (vom Anwender eingegebenen) Daten. Werden die Daten innerhalb der Callback-Methode nicht
        geändert (bleibt also der Inhalt von <code>retData</code> unverändert), so übernimmt
        <em>HBCI4Java</em> die Kontodaten trotz des fehlgeschlagenen Prüfziffern-Checks</p>
        <p>Die automatische Überprüfung von Kontonummern findet statt, wenn HBCI-Jobs mit
        Hilfe des Highlevel-Interfaces (siehe dazu Paketbeschreibung von <code>org.kapott.hbci.GV</code>)
        erzeugt werden. Beim Hinzufügen eines so erzeugten Jobs zur Menge der auszuführenden
        Aufträge 
        ({@link org.kapott.hbci.GV.HBCIJob#addToQueue()})
        wird die Überprüfung für alle in diesem Job benutzten Kontonummern durchgeführt. Für jeden
        Prüfzifferfehler, der dabei entdeckt wird, wird dieser Callback erzeugt.<br/>
        Tritt beim Überprüfen einer IBAN ein Fehler auf, wird statt dessen
        {@link #HAVE_IBAN_ERROR} als Callback-Reason verwendet. */
    public final static int HAVE_CRC_ERROR=19;
    /** <p>Ursache des Callback-Aufrufes: Es ist ein Fehler aufgetreten, der auf Wunsch 
        des Anwenders ignoriert werden kann. Durch Setzen bestimmter Kernel-Parameter 
        (siehe {@link org.kapott.hbci.manager.HBCIUtils#setParam(String,String)}) kann
        festgelegt werden, dass beim Auftreten bestimmter Fehler zur Laufzeit nicht sofort eine Exception
        geworfen wird, sondern dass statt dessen erst dieser Callback erzeugt wird, welcher als <code>msg</code>
        eine entsprechende Problembeschreibung enthält. <em>HBCI4Java</em> erwartet einen
        boolschen Rückgabewert, der beschreibt, ob der Fehler ignoriert werden soll oder ob eine
        enstprechende Exception erzeugt werden soll. Der Anwender kann den Fehler ignorieren, indem
        im <code>retData</code> Rückgabedaten-Objekt ein leerer String zurückgegeben wird, oder er kann
        erzwingen, dass <em>HBCI4Java</em> tatsächlich abbricht, indem ein nicht-leerer String im
        <code>retData</code>-Objekt zurückgegen wird. Siehe dazu auch die Beschreibung des
        Rückgabe-Datentyps {@link #TYPE_BOOLEAN}.</p>
        <p>Das Ignorieren eines Fehlers kann dazu führen, dass <em>HBCI4Java</em> später trotzdem eine
        Exception erzeugt, z.B. weil der Fehler in einem bestimmten Submodul doch nicht einfach ignoriert
        werden kann, oder es kann auch dazu führen, dass Aufträge von der Bank nicht angenommen werden usw.
        Es wird aber in jedem Fall eine entsprechende Fehlermeldung erzeugt.</p> */
    public final static int HAVE_ERROR=20;
    
    /** Ursache des Callback-Aufrufes: Passwort für das Einlesen der Schlüsseldatei
        benötigt. Dieser Callback tritt beim Laden eines Passport-Files auf, um nach dem 
        Passwort für die Entschlüsselung zu fragen. 
        ACHTUNG: Die folgenden Zeichen duerfen NICHT im Passwort enthalten sein: ß´°§üÜöäÖÄ
        */
    public final static int NEED_PASSPHRASE_LOAD=21;
    /** Ursache des Callback-Aufrufes: Passwort für das Erzeugen der Schlüsseldatei
        benötigt. Dieser Callback tritt beim Erzeugen eines neuen Passport-Files bzw. beim
        Ändern der Passphrase für eine Schlüsseldatei auf, um nach dem 
        Passwort für die Verschlüsselung zu fragen.
        ACHTUNG: Die folgenden Zeichen duerfen NICHT im Passwort enthalten sein: ß´°§üÜöäÖÄ
         */
    public final static int NEED_PASSPHRASE_SAVE=22;
    /** <p>Ursache des Callback-Aufrufes: Auswahl eines Eintrages aus einer SIZ-RDH-Datei
        benötigt. Dieser Callback tritt nur bei Verwendung der Passport-Variante
        SIZRDHFile auf. In einer SIZ-RDH-Schlüsseldatei können mehrere HBCI-Zugangsdatensätze
        gespeichert sein. Wird eine solche Datei mit mehreren Datensätzen geladen,
        so wird dieser Callback erzeugt, um den zu benutzenden Datensatz aus der Datei
        auswählen zu können.</p>
        <p>Dazu wird beim Aufruf der Callback-Routine im Parameter <code>retData</code>
        ein String übergeben, der aus Informationen über alle in der Datei vorhandenen
        Zugangsdatensätze besteht. Das Format dieses Strings ist
        <code>&lt;ID&gt;;&lt;BLZ&gt;;&lt;USERID&gt;[|&lt;ID&gt;;&lt;BLZ&gt;;&lt;USERID&gt;...]</code>
        Es werden also die verschiedenen Datensätze durch "|" getrennt dargestellt,
        wobei jeder einzelne Datensatz durch eine ID, die Bankleitzahl und die UserID
        dieses Datensatzes repräsentiert wird.</p>
        <p>Dem Anwender müssen diese Daten in geeigneter Weise zur Auswahl angezeigt
        werden. Die Callback-Routine muss schließlich die ID des vom Anwender ausgewählten
        Eintrages im <code>retData</code>-Rückgabedatenobjekt zurückgeben.</p>
        <p>Beim Aufruf der Callback-Routine könnte <code>retData</code> also folgendes
        enthalten: <code>0;09950003;Kunde-001|1;01234567;Kunde8|4;8765432;7364634564564</code>.
        Der Anwender muss sich also zwischen den Datensätzen "09950003;Kunde-001",
        "01234567;Kunde8" und "8765432;7364634564564" entscheiden. Je nach Auswahl
        muss in <code>retData</code> dann jeweils "0", "1" oder "4" zurückgegeben werden.</p>*/
    public final static int NEED_SIZENTRY_SELECT=23;
    /** <p>Ursache des Callback-Aufrufes: es wird eine Netz-Verbindung zum HBCI-Server benötigt.
        Dieser Callback wird erzeugt, bevor <em>HBCI4Java</em> eine Verbindung zum HBCI-Server
        aufbaut. Bei Client-Anwendungen, die mit einer Dialup-Verbindung zum Internet arbeiten,
        kann dieser Callback benutzt werden, um den Anwender zum Aktivieren der Internet-Verbindung
        aufzufordern. Es werden keine Rückgabedaten erwartet. Sobald die Internet-Verbindung 
        nicht mehr benötigt wird, wird ein anderer Callback ({@link #CLOSE_CONNECTION}) erzeugt.</p>
        <p>Dieses Callback-Paar wird immer dann erzeugt, wenn von der aktuellen 
        <em>HBCI4Java</em>-Verarbeitungsstufe tatsächlich eine Verbindung zum Internet benötigt 
        wird bzw. nicht mehr ({@link #CLOSE_CONNECTION}) benötigt wird. U.U. werden allerdings 
        mehrere solcher Verarbeitungsstufen direkt hintereinander ausgeführt - das kann zur Folge 
        haben, dass auch diese Callback-Paare mehrmals direkt hintereinander auftreten. Das tritt
        vor allem beim erstmaligen Initialiseren eines Passports auf. Beim Aufruf von
        <code>new&nbsp;HBCIHandler(...)</code> werden verschiedene Passport-Daten mit
        der Bank abgeglichen, dabei wird u.U. mehrmals 
        <code>NEED_CONNECTION</code>/<code>CLOSE_CONNECTION</code> aufgerufen. Evtl.
        sollte der Callback-Handler der Anwendung in diesem Fall also entsprechende
        Maßnahmen treffen.</p> */
    public final static int NEED_CONNECTION=24;
    /** Ursache des Callback-Aufrufes: die Netzwerk-Verbindung zum HBCI-Server wird nicht länger
        benötigt. Dieser Callback wird aufgerufen, sobald <em>HBCI4Java</em> die Kommunikation
        mit dem HBCI-Server vorläufig beendet hat. Dieser Callback kann zusammen mit dem
        Callback {@link #NEED_CONNECTION} benutzt werden, um für Clients mit Dialup-Verbindungen
        die Online-Zeiten zu optimieren. Bei diesem Callback werden keine Rückgabedaten
        erwartet */
    public final static int CLOSE_CONNECTION=25;
    /** <p>Ursache des Callback-Aufrufes: es wird die Bezeichnung des zu verwendenden
        Datenfilters benötigt. Mögliche Filterbezeichnungen sind "<code>None</code>"
        (kein Filter) und "<code>Base64</code>" (Daten BASE64-kodieren). Die
        jeweilige Filterbezeichnung ist in <code>retData</code> zurückzugeben.
        Dieser Callback tritt zur Zeit nur bei Verwendung von PIN/TAN-Passports 
        auf, weil hier nicht alle Banken einheitlich mit der gleichen Art der 
        Filterung arbeiten.</p>
        <p>Normalweise wird bei PIN/TAN der <code>Base64</code>-Filter benutzt.
        Wenn bei dessen Verwendung aber keine Antwortdaten von der Bank empfangen
        werden, dann sollte die andere Variante (<code>None</code>) ausprobiert 
        werden.</p> */
    public final static int NEED_FILTER=26;
    
    /** <p>Ursache des Callbacks: bei Verwendung von HBCI-PIN/TAN muss eines der
     * unterstützten Verfahren ausgewählt werden. Seit FinTS-3.0 gibt es mehrere
     * Verfahren für PIN/TAN - das "normale" Einschrittverfahren sowie mehrere
     * Zweischritt-Verfahren. Unterstützt eine Bank mehr als ein Verfahren, so
     * wird dieser Callback erzeugt, damit der Anwender das zu verwendende 
     * Verfahren auswählen kann.</p>
     * <p>Dazu wird in <code>retData</code> ein String mit folgendem Format
     * an die Callback-Methode übergeben: 
     * "<code>ID1:Beschreibung1|ID2:Beschreibung2...</code>". Jedes Token 
     * "<code>ID:Beschreibung</code>" steht dabei für ein unterstütztes
     * PIN/TAN-Verfahren. Die Callback-Methode muss die ID des vom Anwender
     * ausgewählten PIN/TAN-Verfahrens anschließend in <code>retData</code>
     * zurückgeben.</p> */
    public final static int NEED_PT_SECMECH=27;
    
    /** Ursache des Callbacks: es wird ein Nutzername für die Authentifizierung
     * am Proxy-Server benötigt. Wird für die HTTPS-Verbindungen bei HBCI-PIN/TAN 
     * ein Proxy-Server verwendet, und verlangt dieser Proxy-Server eine
     * Authentifizierung, so wird über diesen Callback nach dem Nutzernamen
     * gefragt, falls dieser nicht schon durch den Kernel-Parameter
     * <code>client.passport.PinTan.proxyuser</code> gesetzt wurde */
    public final static int NEED_PROXY_USER=28;

    /** Ursache des Callbacks: es wird ein Passwort für die Authentifizierung
     * am Proxy-Server benötigt. Wird für die HTTPS-Verbindungen bei HBCI-PIN/TAN 
     * ein Proxy-Server verwendet, und verlangt dieser Proxy-Server eine
     * Authentifizierung, so wird über diesen Callback nach dem Passwort
     * gefragt, falls dieses nicht schon durch den Kernel-Parameter
     * <code>client.passport.PinTan.proxypass</code> gesetzt wurde */
    public final static int NEED_PROXY_PASS=29;
    
    /** Ursache des Callbacks: beim Überprüfen einer IBAN ist ein Fehler aufgetreten.
     * in <code>retData</code> wird die fehlerhafte IBAN übergeben. Der Nutzer
     * sollte die IBAN korrieren. Die korrigierte IBAN sollte wieder in <code>retData</code>
     * zurückgegeben werden. Wird die IBAN nicht verändert, wird diese IBAN trotz
     * des Fehlers verwendet. Wird eine korrigierte IBAN zum Nutzer zurückgegeben,
     * wird für diese erneut ein Prüfsummencheck ausgeführt. Schlägt der wieder fehl,
     * wird der Callback erneut erzeugt. Das geht so lange, bis entweder der
     * Prüfsummencheck erfolgreich war oder bis die IBAN vom Nutzer nicht verändert
     * wird. Siehe dazu auch {@link #HAVE_CRC_ERROR}. */
    public final static int HAVE_IBAN_ERROR=30;
    
    /** Ursache des Callbacks: Kernel fragt um Erlaubnis, Daten an den InfoPoint-Server
     * zu senden. An bestimmten Punkten der HBCI-Kommunikation sendet der HBCI-Kernel
     * Daten über erfolgreich gelaufene Verbindungen an den InfoPoint-Server (siehe
     * Kernel-Parameter "<code>infoPoint.enabled</code>" und Datei <em>README.InfoPoint</em>).
     * Bei diesem Callback wird im StringBuffer <code>retData</code> das XML-Document
     * übergeben, welches an den InfoPoint-Server gesendet werden soll. Als Antwort
     * wird ein Boolean-Wert erwartet (siehe {@link #TYPE_BOOLEAN}). Dürfen die
     * Daten gesendet werden, ist von der Anwendung also ein leerer String in
     * <code>retData</code> zurückzugeben, ansonsten ein beliebiger nicht-leerer String. */
    public final static int NEED_INFOPOINT_ACK=31;
    
    /** <p>Ursache des Callbacks: bei Verwendung von HBCI-PIN/TAN muss
     * die Bezeichnung des TAN-Mediums eingegeben werden. Bei smsTAN ist
     * das z.Bsp. der Alias-Name des Mobiltelefons, wie er bei der Bank
     * hinterlegt wurde. Dieser Name wird verwendet, damit die SMS mit
     * der TAN an mehrere Mobiltelefone schicken kann. */
    public final static int NEED_PT_TANMEDIA=32;

    /**
     * Ursache des Callback-Aufrufes: eine Photo-TAN für PIN/TAN-Verfahren benötigt. Dieser
     * Callback tritt nur bei Verwendung von PIN/TAN-Passports mit dem photoTAN-Verfahren auf.
     * Im Callback wird im StringBuffer der Wert aus dem HHDuc uebergeben. Das sind die Roh-Daten
     * des Bildes inclusive Angaben zum Bildformat. HBCI4Java enthaelt eine Klasse "MatrixCode",
     * mit dem diese Daten dann gelesen werden koennen.
     **/
    public final static int NEED_PT_PHOTOTAN=33;

    /** <p>Ursache des Callbacks: falsche PIN eingegeben */
    public final static int WRONG_PIN=40;
    
    /** <p>Ursache des Callbacks: Dialogantwort 3072 der GAD - UserID und CustomerID werden ausgetauscht */
    /** <p>im Parameter retData stehen die neuen Daten im Format UserID|CustomerID drin */
    public final static int USERID_CHANGED=41;

    /** erwarteter Datentyp der Antwort: keiner (keine Antwortdaten erwartet) */
    public final static int TYPE_NONE=0;
    /** erwarteter Datentyp der Antwort: geheimer Text (bei Eingabe nicht anzeigen) */
    public final static int TYPE_SECRET=1;
    /** erwarteter Datentyp der Antwort: "normaler" Text */
    public final static int TYPE_TEXT=2;
    /** <p>erwarteter Datentyp der Antwort: ja/nein, true/false, weiter/abbrechen
        oder ähnlich. Da das 
        Rückgabedatenobjekt immer ein <code>StringBuffer</code> ist, wird hier
        folgende Kodierung verwendet: die beiden möglichen Werte für die
        Antwort (true/false, ja/nein, weiter/abbrechen, usw.) werden dadurch
        unterschieden, dass für den einen Wert ein <em>leerer</em> String 
        zurückgegeben wird, für den anderen Wert ein <em>nicht leerer</em>
        beliebiger String. Einige Callback-Reasons können auch den Inhalt
        des nicht-leeren Strings auswerten. Eine genaue Beschreibung der jeweilis
        möglichen Rückgabedaten befinden sich in der Beschreibung der 
        Callback-Reasons (<code>HAVE_*</code> bzw. <code>NEED_*</code>), bei 
        denen Boolean-Daten als Rückgabewerte benötigt werden.</p>
        <p>Siehe dazu auch die Hinweise in der Paketbeschreibung zum Paket
        <code>org.kapott.hbci.callback</code>.</p> */     
    public final static int TYPE_BOOLEAN=3;
    
    /** Kernel-Status: Erzeuge Auftrag zum Versenden. Als Zusatzinformation 
        wird bei diesem Callback das <code>HBCIJob</code>-Objekt des 
        Auftrages übergeben, dessen Auftragsdaten gerade erzeugt werden. */
    public final static int STATUS_SEND_TASK=1;
    /** Kernel-Status: Auftrag gesendet. Tritt auf, wenn zu einem bestimmten Job
        Auftragsdaten empfangen und ausgewertet wurden. Als Zusatzinformation wird
        das <code>HBCIJob</code>-Objekt des jeweiligen Auftrages übergeben. */
    public final static int STATUS_SEND_TASK_DONE=2;
    /** Kernel-Status: hole BPD. Kann nur während der Passport-Initialisierung
        ({@link org.kapott.hbci.manager.HBCIHandler#HBCIHandler(String,org.kapott.hbci.passport.HBCIPassport)})
        auftreten und zeigt an, dass die BPD von der Bank abgeholt werden müssen,
        weil sie noch nicht lokal vorhanden sind. Es werden keine zusätzlichen
        Informationen übergeben. */
    public final static int STATUS_INST_BPD_INIT=3;
    /** Kernel-Status: BPD aktualisiert. Dieser Status-Callback tritt nach dem expliziten
        Abholen der BPD ({@link #STATUS_INST_BPD_INIT}) auf und kann auch nach einer
        Dialog-Initialisierung auftreten, wenn dabei eine neue BPD vom Kreditinstitut
        empfangen wurde. Als Zusatzinformation wird ein <code>Properties</code>-Objekt
        mit den neuen BPD übergeben.*/
    public final static int STATUS_INST_BPD_INIT_DONE=4;
    /** Kernel-Status: hole Institutsschlüssel. Dieser Status-Callback zeigt an, dass
        <em>HBCI4Java</em> die öffentlichen Schlüssel des Kreditinstitutes abholt.
        Dieser Callback kann nur beim Initialisieren eines Passportes (siehe 
        {@link org.kapott.hbci.manager.HBCIHandler#HBCIHandler(String,org.kapott.hbci.passport.HBCIPassport)})
        und bei Verwendung von RDH als Sicherheitsverfahren auftreten. Es werden keine
        zusätzlichen Informationen übergeben. */
    public final static int STATUS_INST_GET_KEYS=5;
    /** Kernel-Status: Institutsschlüssel aktualisiert. Dieser Callback tritt
        auf, wenn <em>HBCI4Java</em> neue öffentliche Schlüssel der Bank
        empfangen hat. Dieser Callback kann nach dem expliziten Anfordern der
        neuen Schlüssel ({@link #STATUS_INST_GET_KEYS}) oder nach einer Dialog-Initialisierung
        auftreten, wenn das Kreditinstitut neue Schlüssel übermittelt hat. Es
        werden keine zusätzlichen Informationen übergeben. */
    public final static int STATUS_INST_GET_KEYS_DONE=6;
    /** Kernel-Status: Sende Nutzerschlüssel. Wird erzeugt, wenn <em>HBCI4Java</em>
        neue Schlüssel des Anwenders an die Bank versendet. Das tritt beim erstmaligen
        Einrichten eines RDH-Passportes bzw. nach dem manuellen Erzeugen neuer
        RDH-Schlüssel auf. Es werden keine zusätzlichen Informationen übergeben. */
    public final static int STATUS_SEND_KEYS=7;
    /** Kernel-Status: Nutzerschlüssel gesendet. Dieser Callback zeigt an, dass die RDH-Schlüssel
        des Anwenders an die Bank versandt wurden. Der Erfolg dieser Aktion kann nicht
        allein durch das Auftreten dieses Callbacks angenommen werden! Es wird der Status
        des Nachrichtenaustauschs ({@link org.kapott.hbci.status.HBCIMsgStatus})
        als Zusatzinformation übergeben. */
    public final static int STATUS_SEND_KEYS_DONE=8;
    /** Kernel-Status: aktualisiere System-ID. Dieser Status-Callback wird erzeugt, wenn
        <em>HBCI4Java</em> die System-ID, die für das RDH-Verfahren benötigt
        wird, synchronisiert. Der Callback kann nur beim Initialisieren eines Passports
        (siehe {@link org.kapott.hbci.manager.HBCIHandler#HBCIHandler(String,org.kapott.hbci.passport.HBCIPassport)})
        auftreten. Es werden keine Zusatzinformationen übergeben. */
    public final static int STATUS_INIT_SYSID=9;
    /** Kernel-Status: System-ID aktualisiert. Dieser Callback tritt auf, wenn im Zuge der
        Synchronisierung ({@link #STATUS_INIT_SYSID}) eine System-ID empfangen wurde. Als
        Zusatzinformation wird ein Array übergeben, dessen erstes Element die Statusinformation
        zu diesem Nachrichtenaustausch darstellt ({@link org.kapott.hbci.status.HBCIMsgStatus}) 
        und dessen zweites Element die neue System-ID ist. */
    public final static int STATUS_INIT_SYSID_DONE=10;
    /** Kernel-Status: hole UPD. Kann nur während der Passport-Initialisierung
        ({@link org.kapott.hbci.manager.HBCIHandler#HBCIHandler(String,org.kapott.hbci.passport.HBCIPassport)})
        auftreten und zeigt an, dass die UPD von der Bank abgeholt werden müssen,
        weil sie noch nicht lokal vorhanden sind. Es werden keine zusätzlichen
        Informationen übergeben.  */
    public final static int STATUS_INIT_UPD=11;
    /** Kernel-Status: UPD aktualisiert. Dieser Status-Callback tritt nach dem expliziten
        Abholen der UPD ({@link #STATUS_INIT_UPD}) auf und kann auch nach einer
        Dialog-Initialisierung auftreten, wenn dabei eine neue UPD vom Kreditinstitut
        empfangen wurde. Als Zusatzinformation wird ein <code>Properties</code>-Objekt
        mit den neuen UPD übergeben. */
    public final static int STATUS_INIT_UPD_DONE=12;
    /** Kernel-Status: sperre Nutzerschlüssel. Dieser Status-Callback wird erzeugt, wenn
        <em>HBCI4Java</em> einen Auftrag zur Sperrung der aktuellen Nutzerschlüssel
        generiert. Es werden keine Zusatzinformationen übergeben. */
    public final static int STATUS_LOCK_KEYS=13;
    /** Kernel-Status: Nutzerschlüssel gesperrt. Dieser Callback tritt auf, nachdem die
        Antwort auf die Nachricht "Sperren der Nutzerschlüssel" eingetroffen ist. Ein
        Auftreten dieses Callbacks ist keine Garantie dafür, dass die Schlüsselsperrung
        erfolgreich abgelaufen ist. Es wird der Status
        des Nachrichtenaustauschs ({@link org.kapott.hbci.status.HBCIMsgStatus})
        als Zusatzinformation übergeben. */
    public final static int STATUS_LOCK_KEYS_DONE=14;
    /** Kernel-Status: aktualisiere Signatur-ID. Dieser Status-Callback wird erzeugt, wenn
        <em>HBCI4Java</em> die Signatur-ID, die für das RDH-Verfahren benötigt
        wird, synchronisiert. Der Callback kann nur beim Initialisieren eines Passports
        (siehe {@link org.kapott.hbci.manager.HBCIHandler#HBCIHandler(String,org.kapott.hbci.passport.HBCIPassport)})
        auftreten. Es werden keine Zusatzinformationen übergeben. */
    public final static int STATUS_INIT_SIGID=15;
    /** Kernel-Status: Signatur-ID aktualisiert. Dieser Callback tritt auf, wenn im Zuge der
        Synchronisierung ({@link #STATUS_INIT_SIGID}) eine Signatur-ID empfangen wurde. Als
        Zusatzinformation wird ein Array übergeben, dessen erstes Element die Statusinformation
        zu diesem Nachrichtenaustausch darstellt ({@link org.kapott.hbci.status.HBCIMsgStatus}) 
        und dessen zweites Element die neue Signatur-ID (ein Long-Objekt) ist.*/
    public final static int STATUS_INIT_SIGID_DONE=16;
    /** Kernel-Status: Starte Dialog-Initialisierung. Dieser Status-Callback zeigt an, dass
        <em>HBCI4Java</em> eine Dialog-Initialisierung startet. Es werden keine
        zusätzlichen Informationen übergeben. */
    public final static int STATUS_DIALOG_INIT=17;
    /** Kernel-Status: Dialog-Initialisierung ausgeführt. Dieser Callback tritt nach dem
        Durchführen der Dialog-Initialisierung auf. Als
        Zusatzinformation wird ein Array übergeben, dessen erstes Element die Statusinformation
        zu diesem Nachrichtenaustausch darstellt ({@link org.kapott.hbci.status.HBCIMsgStatus}) 
        und dessen zweites Element die neue Dialog-ID ist. */
    public final static int STATUS_DIALOG_INIT_DONE=18;
    /** Kernel-Status: Beende Dialog. Wird ausgelöst, wenn <em>HBCI4Java</em> den
        aktuellen Dialog beendet. Es werden keine zusätzlichen Daten übergeben. */
    public final static int STATUS_DIALOG_END=19;
    /** Kernel-Status: Dialog beendet. Wird ausgeführt, wenn der HBCI-Dialog tatsächlich
        beendet ist. Es wird der Status
        des Nachrichtenaustauschs ({@link org.kapott.hbci.status.HBCIMsgStatus})
        als Zusatzinformation übergeben.*/
    public final static int STATUS_DIALOG_END_DONE=20;
    /** Kernel-Status: Erzeuge HBCI-Nachricht. Dieser Callback zeigt an, dass <em>HBCI4Java</em>
        gerade eine HBCI-Nachricht erzeugt. Es wird der Name der Nachricht als zusätzliches
        Objekt übergeben. */
    public final static int STATUS_MSG_CREATE=21;
    /** Kernel-Status: Signiere HBCI-Nachricht. Dieser Callback wird aufgerufen, wenn
        <em>HBCI4Java</em> die ausgehende HBCI-Nachricht signiert. Es werden keine
        zusätzlichen Informationen übergeben. */
    public final static int STATUS_MSG_SIGN=22;
    /** Kernel-Status: Verschlüssele HBCI-Nachricht. Wird aufgerufen, wenn <em>HBCI4Java</em>
        die ausgehende HBCI-Nachricht verschlüsselt. Es werden keine zusätzlichen
        Informationen übergeben. */
    public final static int STATUS_MSG_CRYPT=23;
    /** Kernel-Status: Sende HBCI-Nachricht (bei diesem Callback ist das
        <code>passport</code>-Objekt immer <code>null</code>). Wird aufgerufen,
        wenn die erzeugte HBCI-Nachricht an den HBCI-Server versandt wird. Es werden
        keine zusätzlichen Informationen übergeben. */
    public final static int STATUS_MSG_SEND=24;
    /** Kernel-Status: Entschlüssele HBCI-Nachricht. Wird aufgerufen, wenn die empfangene
        HBCI-Nachricht von <em>HBCI4Java</em> entschlüsselt wird. Es werden keine
        zusätzlichen Informationen übergeben. */
    public final static int STATUS_MSG_DECRYPT=25;
    /** Kernel-Status: Überprüfe digitale Signatur der Nachricht. Wird aufgerufen, wenn
        <em>HBCI4Java</em> die digitale Signatur der empfangenen Antwortnachricht
        überprüft. Es werden keine zusätzlichen Informationen übergeben. */
    public final static int STATUS_MSG_VERIFY=26;
    /** Kernel-Status: Empfange HBCI-Antwort-Nachricht (bei diesem Callback ist das
        <code>passport</code>-Objekt immer <code>null</code>). Wird aufgerufen, wenn
        die Antwort-HBCI-Nachricht vom HBCI-Server empfangen wird. Es werden keine
        zusätzlichen Informationen übergeben. */
    public final static int STATUS_MSG_RECV=27;
    /** Kernel-Status: Parse HBCI-Antwort-Nachricht (bei diesem Callback ist das
        <code>passport</code>-Objekt immer <code>null</code>). Wird aufgerufen, wenn 
        <em>HBCI4Java</em> versucht, die empfangene Nachricht zu parsen. Es wird
        der Name der erwarteten Nachricht als zusätzliche Information übergeben. */
    public final static int STATUS_MSG_PARSE=28;    
    /** Kernel-Status: Der Kernel sendet Informationen über eine erfolgreiche
     * Dialog-Initialisierung an den InfoPoint-Server (siehe auch <em>README.InfoPoint</em>).
     * Als zusätzlicher Parameter wird das XML-Dokument (als String) übergeben,
     * welches an den InfoPoint-Server gesendet wird. */
    public final static int STATUS_SEND_INFOPOINT_DATA=29;

    /**
     * Wird aufgerufen unmittelbar bevor die HBCI-Nachricht an den Server gesendet wird.
     * Als zusaetzliche Information wird die zu sendende Nachricht als String uebergeben.
     * Sie kann dann z.Bsp. in einem Log gesammelt werden, welches ausschliesslich
     * (zusammen mit {@link HBCICallback#STATUS_MSG_RAW_RECV}) die gesendeten und
     * empfangenen rohen HBCI-Nachrichten enthaelt. Sinnvoll zum Debuggen der Kommunikation
     * mit der Bank.
     */
    public final static int STATUS_MSG_RAW_SEND=30;
    
    /**
     * Wird aufgerufen unmittelbar nachdem die HBCI-Nachricht vom Server empfangen wurde.
     * Als zusaetzliche Information wird die empfangene Nachricht als String uebergeben.
     * Sie kann dann z.Bsp. in einem Log gesammelt werden, welches ausschliesslich
     * (zusammen mit {@link HBCICallback#STATUS_MSG_RAW_SEND}) die gesendeten und
     * empfangenen rohen HBCI-Nachrichten enthaelt. Sinnvoll zum Debuggen der Kommunikation
     * mit der Bank.
     */
    public final static int STATUS_MSG_RAW_RECV=31;

    /** Wird aufgerufen, wenn der HBCI-Kernel eine Log-Ausgabe
        erzeugt. <em>HBCI4Java</em> gibt Logging-Ausgaben nicht selbst auf
        irgendeinem Device aus, sondern sendet diese mit Hilfe der
        Methode <code>log(...)</code> an die Anwendung. Diese muss selbst
        entscheiden, was mit der Information geschehen soll (einfach ignorieren,
        abspeichern, dem Nutzer anzeigen, ...).
        @param msg die eigentliche Text-Meldung des HBCI-Kernels
        @param level Loglevel, welcher die "Wichtigkeit" dieser Meldung angibt. Die
        möglichen Werte dafür sind in {@link org.kapott.hbci.manager.HBCIUtils}
        definiert und lauten
        <ul>
          <li><code>LOG_CHIPCARD</code></li>
          <li><code>LOG_DEBUG</code></li>
          <li><code>LOG_INFO</code></li>
          <li><code>LOG_WARN</code></li>
          <li><code>LOG_ERR</code></li>
        </ul>
        @param date Zeitpunkt, zu dem die Logausgabe generiert wurde
        @param trace ein <code>StackTrace</code>-Element, welches die Stelle
        im Code beschreibt, an der die Logausgabe erzeugt wurde
        (kann benutzt werden, um die Klasse, Methode, Zeilennummer etc.
        des Aufrufes zu ermitteln) */
    public void log(String msg,int level,Date date,StackTraceElement trace);
    
    /** Wird vom HBCI-Kernel aufgerufen, wenn die Interaktion mit der
        Anwendung erforderlich ist. In bestimmten Situationen benötigt der
        HBCI-Kernel zusätzliche Daten bzw. muss auf die Ausführung einer
        Aktion des Nutzers warten. Dann wird diese Methode aufgerufen. Dabei wird
        ein Code (<code>reason</code>) übergeben, der anzeigt, welche Ursache
        dieser Callbackaufruf hat, d.h. welche Daten oder Aktionen erwartet werden.
        Falls Daten erwartet werden (z.B. ein Passwort, eine Benutzerkennung, ...),
        so ist legt der Parameter <code>datatype</code> fest, wie diese Daten erwartet
        werden. Die eigentlichen Daten muss die Anwendung im Objekt <code>retData</code>
        ablegen (keinen neuen StringBuffer erzeugen, sondern den Inhalt von <code>retData</code>
        überschreiben!). Bei einigen Callbacks übergibt <em>HBCI4Java</em> einen vorgeschlagenen
        default-Wert für die Nutzereingabe im <em>retData</em>-Objekt. Diese Tatsache ist
        besonders bei der Auswertung des Callbacks {@link #HAVE_CRC_ERROR} zu beachten!
        @param passport enthält das Passport-Objekt, bei dessen Benutzung der
        Callback erzeugt wurde. Falls also in einer Anwendung mehrere
        Passport-Objekte gleichzeitig benutzt werden, so kann anhand
        dieses Parameters festgestellt werden, welches Passport
        (und damit welches HBCIHandle) <em>HBCI4Java</em> gerade benutzt.
        @param reason gibt den Grund für diesen Aufruf an. Dieser Parameter kann
        alle Werte annehmen, die als "Ursache des Callback-Aufrufes" in der Dokumentation
        aufgeführt sind. Je nach Wert dieses Parameters werden vom Nutzer
        Aktionen oder Eingaben erwartet.
        @param msg ein Hinweistext, der den Grund des Callbacks näher beschreibt.
        Dieser Parameter muss nicht ausgewertet werden, der Parameter
        <code>reason</code> ist bereits eindeutig. Er dient nur dazu,
        bei Anwendungen, die nicht für jeden Ursache des Callback-Aufrufes einen eigenen
        Hinweistext bereitstellen wollen, eine Art default-Wert für den
        anzuzeigenden Text bereitzustellen.
        @param datatype legt fest, welchen Datentyp die vom HBCI-Kernel erwarteten
        Antwortdaten haben müssen. Ist dieser Wert gleich
        <code>TYPE_NONE</code>, so werden keine Antwortdaten (also keine
        Nutzereingabe) erwartet, bei <code>TYPE_SECRET</code> und
        <code>TYPE_TEXT</code> wird ein normaler String erwartet.<br/>
        Der Unterschied zwischen beiden ist der, dass bei
        <code>TYPE_SECRET</code> sensible Daten (Passwörter usw.) eingegeben
        werden sollen, so dass die Eingaberoutine evtl. anders arbeiten
        muss (z.B. Sternchen anstatt dem eingegebenen Text darstellen).
        @param retData In diesem StringBuffer-Objekt müssen die Antwortdaten
        abgelegt werden. Beim Aufruf der Callback-Methode von <em>HBCI4Java</em> wird dieser
        StringBuffer u.U. mit einem vorgeschlagenen default-Wert für die Nutzereingabe
        gefüllt. */
    public void callback(HBCIPassport passport,int reason,String msg,int datatype,StringBuffer retData);
    
    /** Wird vom HBCI-Kernel aufgerufen, um einen bestimmten Status der
        Abarbeitung bekanntzugeben.
        @param passport gibt an, welches Passport (und damit welches HBCIHandle)
        benutzt wurde, als der Callback erzeugt wurde (siehe auch
        {@link #callback(org.kapott.hbci.passport.HBCIPassport,int,String,int,StringBuffer)}).
        @param statusTag gibt an, welche Stufe der Abarbeitung gerade erreicht
        wurde (alle oben beschriebenen Konstanten, die mit <code>STATUS_</code>
        beginnen)
        @param o ein Array aus Objekten, das zusätzliche Informationen zum jeweiligen
        Status enthält. In den meisten Fällen handelt es sich um einen
        String, der zusätzliche Informationen im Klartext enthält. Welche Informationen
        das jeweils sind, ist der Beschreibung zu den einzelnen <code>STATUS_*</code>-Tag-Konstanten
        zu entnehmen. */
    public void status(HBCIPassport passport,int statusTag,Object[] o);
    
    /** Kurzform für {@link #status(HBCIPassport, int, Object[])} für den Fall,
     *  dass das <code>Object[]</code> nur ein einziges Objekt enthält */
    public void status(HBCIPassport passport,int statusTag,Object o);
    
    /** <p>Legt fest, ob ein Callback asynchron oder über den threaded-callback-Mechanismus
     * behandelt werden soll. Im "Normalfall" gibt diese Methode <code>false</code>
     * zurück, womit die asynchrone Callback-Behandlung aktiviert wird. Für 
     * bestimmte Anwendungsfälle ist jedoch eine synchrone Callback-Behandlung 
     * sinnvoll. Dazu muss zunächst das zu verwendende Callback-Objekt in einer 
     * Instanz der Klasse {@link org.kapott.hbci.callback.HBCICallbackThreaded HBCICallbackThreaded} 
     * gekapselt werden. Außerdem muss diese Methode so überschrieben werden,
     * dass sie für alle Callbacks, die synchron behandelt werden sollen,
     * <code>true</code> zurückgibt.</p>
     * <p>Die übergebenen Parameter entsprechen denen der Methode
     * {@link #callback(HBCIPassport, int, String, int, StringBuffer)}. Der 
     * Rückgabewert gibt ab, ob dieser Callback synchron (<code>true</code>) oder
     * asynchron (<code>false</code>) behandelt werden soll.</p>
     * <p>Mehr Informationen dazu in der Datei <code>README.ThreadedCallbacks</code>.</p> */
    public boolean useThreadedCallback(HBCIPassport passport,int reason,
                                       String msg,int datatype,
                                       StringBuffer retData);
}

