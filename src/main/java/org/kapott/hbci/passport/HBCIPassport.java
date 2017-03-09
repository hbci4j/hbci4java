
/*  $Id: HBCIPassport.java,v 1.1 2011/05/04 22:37:43 willuhn Exp $

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

package org.kapott.hbci.passport;

import java.util.Properties;

import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.structures.Konto;

/** <p>Public Interface für HBCI-Passports. Ein HBCI-Passport ist eine Art "Ausweis",
    der individuell für jeden Nutzer eines HBCI-Zugangs und für jeden
    Zugangsmechanismus ist. Ein Passport repräsentiert ein HBCI-Sicherheitsmedium
    und stellt Funktionen bereit, um mit dem jeweiligen Medium zu arbeiten.
    </p><p>
    Für jede Zugangsart gibt es eine konkrete Passport-Implementation, die dieses
    Interface implementiert. Dabei handelt es sich um</p>
    <ul>
      <li><code>HBCIPassportDDV</code> für Zugang über DDV mit Chipkarte</li>
      <li><code>HBCIPassportRDHNew</code> für Zugang über RDH mit Datei</li>
      <li><code>HBCIPassportRDH</code> für Zugang über RDH mit Datei (<b><em>bitte nicht mehr benutzen</em></b>;
          siehe Datei <code>README.RDHNew</code>)</li>
      <li><code>HBCIPassportPinTan</code> für Zugang über das PIN/TAN-Verfahren</li>
      <li><code>HBCIPassportAnonymous</code> für den anonymen Zugang</li>
      <li><code>HBCIPassportSIZRDHFile</code> für den Zugang über RDH mit Datei,
          wobei als Datei eine SIZ-Schlüsseldatei, wie sie z.B. von StarMoney oder GENOlite
          erzeugt wird, verwendet werden kann</li>
      <li><code>HBCIPassportRDHXFile</code> für den Zugang über RDH mit Datei,
          wobei als Datei eine RDH-2- oder RDH-10-Schlüsseldatei verwendet wird, 
          wie sie z.B. von VR-NetWorld erzeugt wird.</li>
    </ul>
    <p>In einem Passport werden alle nutzer- und institutsspezifischen Daten verwaltet.
    Dazu gehören</p>
    <ul>
      <li>die Zugangsdaten für den HBCI-Server der Bank (IP-Adresse, usw.)</li>
      <li>die nutzerspezifischen Zugangsdaten (Nutzerkennung, System-Kennung, usw.)</li>
      <li>die Schlüsselinformationen für die kryptografischen Operationen</li>
      <li>die gecachten BPD und die UPD</li>
    </ul>
    <p>Außerdem sind in einem Passport alle Methoden implementiert, die zur Durchführung
    der kryptografischen Operationen benötigt werden (verschlüsseln, signieren, usw.)</p>*/
public interface HBCIPassport
{
    /** Rolle eines Passport-Objektes: Eigentümer ist Herausgeber der Nachricht.
     *  Wird in {@link org.kapott.hbci.GV.HBCIJob#addSignaturePassport(HBCIPassport, String)}
     *  benötigt. */
    public final static String ROLE_ISS="1";
    
    /** Rolle eines Passport-Objektes: Eigentümer ist Mitunterzeichner für Nachricht.
     *  Wird in {@link org.kapott.hbci.GV.HBCIJob#addSignaturePassport(HBCIPassport, String)}
     *  benötigt. */
    public final static String ROLE_CON="3";
    
    /** Rolle eines Passport-Objektes: Eigentümer ist Zeuge oder Überbringer der Nachricht.
     *  Wird in {@link org.kapott.hbci.GV.HBCIJob#addSignaturePassport(HBCIPassport, String)}
     *  benötigt. */
    public final static String ROLE_WIT="4";
    
    /** Gibt die gespeicherten BPD zurück. Die Auswertung der BPD seitens einer HBCI-Anwendung
        auf direktem Weg wird nicht empfohlen, da es keine Dokumentation über die
        Namensgebung der einzelnen Einträge gibt.
        @return die Bankparamterdaten oder <code>null</code>, falls diese nicht im
                Passport vorhanden sind */
    public Properties getBPD();
    
    /** Gibt die HBCI-Version zurück, die zuletzt verwendet wurde. Der hier zurückgegebene
        Wert ist der selbe, der bei der Initialisierung des 
        {@link org.kapott.hbci.manager.HBCIHandler} verwendet werden kann. Um also
        einen HBCIHandler zu erzeugen, der mit der HBCI-Version arbeitet, mit der
        ein Passport-Objekt zuletzt benutzt wurde, so kann das mit
        <code>new HBCIHandler(passport.getHBCIVersion(),passport)</code> erfolgen (vorausgesetzt,
        <code>passport.getHBCIVersion()</code> gibt einen nicht-leeren String zurück. 
        @return Die zuletzt verwendete HBCI-Version. Ist diese Information nicht 
                verfügbar, so wird ein leerer String zurückgegeben. */
    public String getHBCIVersion();
    
    /** Gibt die gespeicherten UPD (User-Parameter-Daten) zurück. Eine direkte
        Auswertung des Inhalts dieses Property-Objektes wird nicht empfohlen, da
        die Benennung der einzelnen Einträge nicht explizit dokumentiert ist.
        @return die Userparameterdaten oder <code>null</code>, falls diese nicht im
                Passport vorhanden sind */
    public Properties getUPD();

    /** <p>Gibt die Bankleitzahl des Kreditinstitutes zurück. Bei Verwendung dieser Methode
        ist Vorsicht geboten, denn hier ist die Bankleitzahl der Bank gemeint, die
        den HBCI-Server betreibt. I.d.R. deckt sich diese BLZ zwar mit der BLZ der
        Konten des Bankkunden, es gibt aber auch Fälle, wo die BLZ, die mit dieser Methode
        ermittelt wird, anders ist als die BLZ bei den Kontoverbindungen des
        Kunden.
        </p><p>
        Für die Ermittlung der BLZ für die Kontodaten sollte statt dessen die Methode
        {@link #getAccounts()} benutzt werden.
        </p>
        @return die BLZ der Bank */
    public String getBLZ();

    /** Gibt den Ländercode der Bank zurück. Für deutsche Banken ist das der String
        "<code>DE</code>".
        @return Ländercode der Bank */
    public String getCountry();
    
    /** Gibt ein Array mit Kontoinformationen zurück. Auf die hier zurückgegebenen Konten kann via
        HBCI zugegriffen werden. Nicht jede Bank unterstützt diese Abfrage, so dass dieses Array
        u.U. auch leer sein kann, obwohl natürlich via HBCI auf bestimmte Konten zugegriffen werden
        kann. In diesem Fall müssen die Kontoinformationen anderweitig ermittelt werden (manuelle
        Eingabe des Anwenders).
        @return Array mit Kontoinformationen über verfügbare HBCI-Konten */ 
    public Konto[] getAccounts();
    
    /** Ausfüllen fehlender Kontoinformationen. In der Liste der verfügbaren Konten (siehe
        {@link #getAccounts()}) wird nach einem Konto gesucht, welches die
        gleiche Kontonummer hat wie das übergebene Konto <code>account</code>. Wird ein solches
        Konto gefunden, so werden die Daten dieses gefundenen Kontos in das <code>account</code>-Objekt
        übertragen.<p/>
        Diese Methode kann benutzt werden, wenn zu einem Konto nicht alle Daten bekannt sind, wenigstens
        aber die Kontonummer.
        @param account unvollständige Konto-Informationen, bei denen die fehlenden Daten nachgetragen
               werden */
    public void fillAccountInfo(Konto account);
    
    /** Gibt ein Konto-Objekt zu einer bestimmten Kontonummer zurück. Dazu wird die Liste, die via
        {@link #getAccounts()} erzeugt wird, nach der Kontonummer durchsucht. Es wird in
        jedem Fall ein nicht-leeres Kontoobjekt zurückgegeben. Wird die Kontonummer jedoch nicht in 
        der Liste gefunden, so wird das Konto-Objekt aus den "allgemeinen" Bank-Daten gebildet:
        Kontonummer=<code>number</code>; Länderkennung, BLZ und Kunden-ID aus dem Passport-Objekt;
        Währung des Kontos hart auf "EUR"; Name=Kunden-ID.
        @param number die Kontonummer, für die ein Konto-Objekt erzeugt werden soll
        @return ein Konto-Objekt, welches mindestens die Kontonummer enthält. Wenn 
                verfügbar, so sind auch die restlichen Informationen über dieses Konto (BLZ,
                Inhaber, Währung usw.) ausgefüllt */
    public Konto getAccount(String number);

    /** Gibt den Hostnamen des HBCI-Servers für dieses Passport zurück. Handelt es sich bei
        dem Passport-Objekt um ein PIN/TAN-Passport, so enthält dieser String die URL,
        die für die HTTPS-Kommunikation mit dem HBCI-Server der Bank benutzt wird. 
        @return Hostname oder IP-Adresse des HBCI-Servers */
    public String getHost();
    
    /** Gibt die TCP-Portnummer auf dem HBCI-Server zurück, zu der eine
        HBCI-Verbindung aufgebaut werden soll. In der Regel ist das der Port 3000,
        für PIN/TAN-Passports wird hier 443 (für HTTPS-Port) zurückgegeben.
        Der zu benutzende TCP-Port für die Kommunikation kannn mit 
        {@link #setPort(Integer)} geändert werden.
        @return TCP-Portnummer auf dem HBCI-Server */
    public Integer getPort();
    
    /** Gibt zurück, welcher Datenfilter für die Kommunikation mit dem HBCI-Server
        verwendet wird. Gültige Bezeichner für Filter sind "<code>None</code>" und
        "<code>Base64</code>". */
    public String getFilterType();
    
    /** Gibt die Benutzerkennung zurück, die zur Authentifikation am
        HBCI-Server benutzt wird.
        @return Benutzerkennung für Authentifikation */
    public String getUserId();
    public String getCustomerId(int idx);
    
    /** <p>Gibt die Kunden-ID zurück, die von <em>HBCI4Java</em> für die
        Initialisierung eines Dialoges benutzt wird. Zu einer Benutzerkennung
        ({@link #getUserId()}), welche jeweils an ein bestimmtes Medium
        gebunden ist, kann es mehrere Kunden-IDs geben. Die verschiedenen
        Kunden-IDs entsprechen verschiedenen Rollen, in denen der Benutzer
        auftreten kann.</p>
        <p>In den meisten Fällen gibt es zu einer Benutzerkennung nur eine
        einzige Kunden-ID. Wird von der Bank keine Kunden-ID explizit vergeben,
        so ist die Kunden-ID identisch mit der Benutzerkennung.</p>
        <p>Siehe dazu auch 
        {@link org.kapott.hbci.GV.HBCIJob#addToQueue(String)}.
        </p>
        @return Kunden-ID für die HBCI-Kommunikation */
    public String getCustomerId();
    public boolean isSupported();
    
    public boolean needInstKeys();
    public boolean needUserKeys();
    
    public boolean hasInstSigKey();
    public boolean hasInstEncKey();
    
    public boolean hasMySigKey();
    public boolean hasMyEncKey();
    
    public void clearInstSigKey();
    public void clearInstEncKey();

    public HBCIKey getMyPublicSigKey();
    public HBCIKey getMyPublicEncKey();
    public HBCIKey getMyPublicDigKey();
    public HBCIKey getMyPrivateSigKey();
    public HBCIKey getMyPrivateEncKey();
    public HBCIKey getMyPrivateDigKey();
    public HBCIKey getInstSigKey();
    public HBCIKey getInstEncKey();

    /** Gibt die Versionsnummer der lokal gespeicherten BPD zurück. Sind keine
        BPD vorhanden, so wird "0" zurückgegeben. Leider benutzen einige Banken
        "0" auch als Versionsnummer für die tatsächlich vorhandenen BPD, so 
        dass bei diesen Banken auch dann "0" zurückgegeben wird, wenn in Wirklichkeit
        BPD vorhanden sind.
        @return Versionsnummer der lokalen BPD */
    public String getBPDVersion();

    /** Gibt die Versionsnummer der lokal gespeicherten UPD zurück. Sind keine UPD
        lokal vorhanden, so wird "0" zurückgegeben. Siehe dazu auch
        {@link #getBPDVersion()}.
        @return Versionsnummer der lokalen UPD */
    public String getUPDVersion();

    /** Gibt den Namen des Kreditinstitutes zurück. Diese Information wird aus
        den BPD ermittelt. Sind keine BPD vorhanden bzw. steht da kein Name drin,
        so wird <code>null</code> zurückgegeben.
        @return Name des Kreditinstitutes */
    public String getInstName();
    public int getMaxGVperMsg();
    public int getMaxMsgSizeKB();

    /** Gibt eine Liste aller unterstützten Sprachcodes zurück. Die einzelnen
        Codes stehen dabei für folgende Sprachen:
        <ul>
          <li>1 - deutsch</li>
          <li>2 - englisch</li>
          <li>3 - französisch</li>
        </ul>
        @return Liste aller unterstützten Sprachen (1,2,3) */
    public String[] getSuppLangs();

    /** <p>Gibt eine Liste aller unterstützten HBCI-Versionen zurück.
        Die einzelnen Strings für die Versionen sind die gleichen, wie sie in der Methode
        {@link org.kapott.hbci.manager.HBCIHandler#HBCIHandler(String,org.kapott.hbci.passport.HBCIPassport)}
         verwendet werden können.
        </p><p>
        Zusätzlich zu den hier zurückgegebenen HBCI-Versions-Codes gibt es einige 
        spezielle Codes. Siehe dazu die Dokumentation zu
        {@link org.kapott.hbci.manager.HBCIHandler#HBCIHandler(String,org.kapott.hbci.passport.HBCIPassport)}
        </p>
        @return eine Liste aller von der Bank unterstützten HBCI-Versionen */
    public String[] getSuppVersions();

    /** Gibt die Standardsprache des HBCI-Servers zurück. Zu den Bedeutungen der
        Sprachcodes siehe {@link #getSuppLangs()}.
        @return Standardsprache (1,2 oder 3) */
    public String getDefaultLang();

    /** <p>Gibt eine Liste der vom HBCI-Server unterstützten Sicherheitsmechanismen
        zurück. Gültige Werte für jeden einzelnen String sind <code>RDH</code> bzw.
        <code>DDV</code>.<p/><p>
        Die Unterstützung des PIN/TAN-Verfahrens kann mit dieser Methode nicht
        ermittelt werden.</p>
        @return eine Liste der unterstützten Sicherheitsmechanismen. Jeder Listeneintrag
                ist wieder ein Stringarray mit zwei Elementen: dem Namen des
                Mechanismus und der Versionsnummer dieses Mechanismus */
    public String[][] getSuppSecMethods();
    public String[][] getSuppCompMethods();
    
    /** Löschen der lokal gespeicherten BPD. Damit kann erzwungen werden, dass
        die BPD beim nächsten HBCI-Dialog erneut abgeholt werden. */
    public void clearBPD();
    /** Löschen der lokal gespeicherten UPD. Damit kann erzwungen werden, dass
        die UPD beim nächsten HBCI-Dialog erneut abgeholt werden. */
    public void clearUPD();

    public void setCountry(String country);
    public void setBLZ(String blz);

    /** <p>Manuelles Setzen der Adresse des HBCI-Servers. Das kann evtl. nötig
        sein, wenn sich die Zugangsdaten des Server geändert haben. Die Änderungen
        werden permanent gespeichert, nachdem die neuen Werte wenigstens einmal in
        einem HBCI-Dialog benutzt wurden oder mit 
        {@link #saveChanges()} explizit gespeichert
        werden. Diese permanente Speicherung wird allerdings
        nur bei RDH- oder PIN/TAN-Passports durchgeführt. Um die Daten bei DDV-Passports
        permanent auf der Chipkarte zu speichern, ist der HBCI-PassportEditor
        nötig</p><p>(es wäre kein Problem, diese Daten sofort auf der Chipkarte zu speichern,
        allerdings besteht dann die Gefahr, dass man "aus Versehen" falsche
        Daten auf der Chipkarte ablegt und die richtigen Daten nicht wieder restaurieren
        kann, da es bei DDV-Zugängen i.d.R. keine Begleitbriefe von der Bank gibt,
        in denen die korrekten Zugangsdaten aufgelistet sind).</p>
        <p>Für das HBCI-PIN/TAN-Verfahren wird als <code>host</code> die URL angegeben,
        welche für die Behandlung der HBCI-PIN/TAN-Nachrichten zu benutzen ist
        (z.B. <code><em>www.meinebank.de/pintan/PinTanServlet</em></code>). Soll ein
        anderer Port als der normale HTTPS-Port 443 benutzt werden, so darf die neue
        Portnummer <em>nicht</em> in der URL kodiert werden. Statt dessen muss die
        Methode {@link #setPort(Integer)} benutzt werden, um die Portnummer zu
        setzen.</p>
        @param host die neue Adresse, unter der der HBCI-Server zu erreichen ist */
    public void setHost(String host);
    
    /** Setzen des TCP-Ports, der für HBCI-Verbindungen benutzt wird. Bei HBCI-PIN/TAN-
     *  Passports wird der Port mit <code>443</code> vorinitialisiert, für alle anderen
     *  "normalen" HBCI-Verbindungstypen mit <code>3000</code>. Diese Methode kann
     *  benutzt werden, wenn eine andere Portnummer als die default-Nummer benutzt
     *  werden soll. Die Portnummer für ein Passport kann auch mit dem 
     *  <em>HBCI4Java Passport Editor</em> geändert werden.
     *  @param port neue TCP-Portnummer, die für ausgehende Verbindungen benutzt 
     *         werden soll */
    public void setPort(Integer port);
    
    public void setFilterType(String filter);
    public void setUserId(String userid);
    
    /** Setzen der zu verwendenden Kunden-ID. Durch Aufruf dieser Methode wird die
        Kunden-ID gesetzt, die beim nächsten Ausführen eines HBCI-Dialoges
        ({@link org.kapott.hbci.manager.HBCIHandler#execute()})
        benutzt wird. Diese neue Kunden-ID wird dann außerdem permanent im
        jeweiligen Sicherheitsmedium gespeichert (sofern das von dem Medium 
        unterstützt wird). 
        @param customerid die zu verwendende Kunden-ID; wird keine customerid
        angegeben (<code>null</code> oder ""), so wird automatisch die 
        User-ID verwendet. 
        
        @see #getCustomerId() */
    public void setCustomerId(String customerid);
    public boolean onlyBPDGVs();
    
    /** Speichern der Änderungen an den Passport-Daten. Diese Methode sollte eigentlich
        niemals manuell aus einer Anwendung heraus aufgerufen werden, sondern wird 
        vom HBCI-Kernel benutzt. Das manuelle Aufrufen von <code>saveChanges</code>
        ist nur dann sinnvoll, wenn irgendwelche Passport-Daten manuell verändert
        werden ({@link #setHost(String)},
        {@link #clearBPD()} usw.) und diese Änderungen
        explizit gespeichert werden sollen. */
    public void saveChanges();

    /** <p>Schließen eines Passport-Objektes. Diese Methode wird normalerweise
        nicht manuell aufgerufen, da das bereits von 
        {@link org.kapott.hbci.manager.HBCIHandler#close()} erledigt
        wird. Wurde jedoch ein Passport-Objekt erzeugt, und das anschließende
        Erzeugen eines HBCIHandler-Objektes schlägt fehlt, dann ist das Passport
        immer noch geöffnet und sollte mit dieser Methode geschlossen werden, falls
        es nicht weiterbenutzt werden soll.</p><p>
        Am Ende eines Programmes sollte also in jedem Fall entweder ein erfolgreiches
        {@link org.kapott.hbci.manager.HBCIHandler#close()} oder
        wenigstens ein {@link org.kapott.hbci.passport.HBCIPassport#close()}
        für jedes erzeugte Passport-Objekt stehen. Das ist vor allem für
        Passport-Varianten wichtig, die auf einer Chipkarte basieren, da mit dieser
        Methode die entsprechenden Ressourcen wieder freigegeben werden. */
    public void close();
    
    /** Synchronisation der Signatur-ID erzwingen (nur für RDH-Passports sinnvoll).
        Diese Methode kann
        aufgerufen werden, <em>nachdem</em> ein Passport erzeugt wurde,
        aber <em>bevor</em> damit ein neues <code>HBCIHandler</code>-Objekt
        erzeugt wird. Durch den Aufruf dieser Methode wird veranlasst, dass
        beim Erzeugen eines <code>HBCIHandler</code>-Objektes mit diesem
        Passport die Signatur-ID des Passports synchronisiert wird.*/
    public void syncSigId();

    /** Synchronisation der System-ID (nur für RDH-Passports sinnvoll).
        Diese Methode kann
        aufgerufen werden, <em>nachdem</em> ein Passport erzeugt wurde,
        aber <em>bevor</em> damit ein neues <code>HBCIHandler</code>-Objekt
        erzeugt wird. Durch den Aufruf dieser Methode wird veranlasst, dass
        beim Erzeugen eines <code>HBCIHandler</code>-Objektes mit diesem
        Passport die System-ID des Passports synchronisiert wird. */
    public void syncSysId();
    
    /** Ändern des Passwortes für die Schlüsseldatei. Der Aufruf dieser
        Methode bewirkt, dass <em>HBCI4Java</em> via Callback-Mechanismus 
        (<code>NEED_PASSPHRASE_SAVE</code>) nach dem neuen Passwort für die 
        Schlüsseldatei fragt. Anschließend wird das Medium unter Verwendung des
        neuen Passwortes automatisch neu gespeichert. */
    public void changePassphrase();
    
    /** Speichern zusätzlicher Daten im Passport-Objekt. Diese Methode ermöglicht
        das Speichern zusätzlicher Informationen (Objekte), die diesem Passport
        zugeordnet sind. Die Funktionsweise ist analog zur Verwendung einer
        Hashtable, es wird also ein Objekt <code>o</code> unter dem Identifikations-String
        <code>id</code> gespeichert. Mit {@link #getClientData(String)}
        kann das entsprechende Objekt wieder ausgelesen werden. Die mit dieser Methode
        gesetzten Daten werden <em>nicht</em> mit in der Schlüsseldatei (Passport-Datei)
        abgelegt, d.h. die Lebensdauer dieser Daten entspricht nur der Lebensdauer
        des Passport-Objektes.
        @param id Identifikationsstring für das zu speichernde Objekt
        @param o zu speicherndes Objekt */
    public void setClientData(String id,Object o);
    
    /** Holen von clientseitig gespeicherten zusätzlichen Daten. Mit dieser Methode
        können die zusätzlichen Daten, die via {@link #setClientData(String,Object)}
        im Passport gespeichert wurden, wieder ausgelesen werden. Auch das Objekt,
        das beim Erzeugen eines Passport-Objektes als <code>init</code>-Parameter übergeben wurde 
        (siehe {@link org.kapott.hbci.passport.AbstractHBCIPassport#getInstance(String,Object)}),
        kann damit ausgelesen werden (mit <code>id="init"</code>).
        @param id  Identifikationsstring des auszulesenden Objektes
        @return Objekt, welches mit {@link #setClientData(String,Object)}
        im Passport gespeichert wurde.*/ 
    public Object getClientData(String id);
}
