
/*
 * $Id: HBCIUtils.java,v 1.2 2011/11/24 21:59:37 willuhn Exp $
 *
 * This file is part of HBCI4Java Copyright (C) 2001-2008 Stefan Palme
 *
 * HBCI4Java is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * HBCI4Java is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.kapott.hbci.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Security;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import org.kapott.cryptalgs.CryptAlgs4JavaProvider;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.swift.Swift;

/**
 * <p>
 * Hilfsklasse für diverse Tools. Diese Klasse definiert nur statische Methoden
 * und Konstanten. Sie kann nicht instanziiert werden.
 * </p>
 * <p>
 * Die wichtigsten Methoden dieser Klasse sind die Methoden zum Initialisieren
 * des HBCI-Kernel
 * ({@link #init(Properties,org.kapott.hbci.callback.HBCICallback)}) sowie zum
 * Setzen von HBCI-Kernel-Parametern ({@link #setParam(String,String)}).
 * </p>
 * <p>
 * Kernel-Parameter können zu jedem beliebigen Zeitpunkt der Laufzeit einer
 * Anwendung gesetzt werden. Das Setzen eines Kernel-Parameters geschieht mit
 * der Methode <code>setParam()</code>. Dieser Methode werden der Name eines
 * Kernel-Parameters sowie der neue Wert für diesen Parameter übergeben.
 * Alternativ bzw. in Verbindung mit dieser Variante können diese Parameter in
 * einer Datei abgelegt werden, die beim Initialiseren des HBCI-Kernels
 * eingelesen wird (via <code>Properties.load()</code>). Folgende
 * Kernel-Parameter werden zur Zeit von verschiedenen Subsystemen des
 * HBCI-Kernels ausgewertet:
 * </p>
 * <ul>
 * <li><code>client.product.name</code> und <code>client.product.version</code>
 * <p>
 * Diese beiden Parameter identifizieren die HBCI-Anwendung. Diese Daten werden
 * von einigen HBCI-Servern ausgewertet, um bestimmte HBCI-Anwendungen besonders
 * zu unterstützen. Werden diese Parameter nicht explizit belegt, so erhalten
 * sie die Standardwerte "HBCI4Java" und "2.5". Es wird empfohlen, diese Werte
 * nicht zu ändern.
 * </p>
 * </li>
 * <li><code>client.passport.DDV.path</code> (für DDV-Passports)
 * <p>
 * Hier wird eingestellt, wo die Datei mit den Zusatzdaten ("Hilfsmedium")
 * gespeichert werden soll. Der Dateiname für das Hilfsmedium setzt sich
 * zusammen aus dem Wert dieses Parameters sowie der Seriennummer der
 * HBCI-Chipkarte. Ein Wert von "<code>/home/hbci/passports/</code>" führt also
 * zur Speicherung der Dateien im Verzeichnis <code>/home/hbci/passports</code>,
 * wobei der Dateiname nur aus der Seriennummer der Chipkarte besteht ("/" am
 * Ende beachten!). Ein Wert von "<code>/home/hbci/passports/card-</code>" führt
 * ebenfalls zur Speicherung der Dateien im Verzeichnis
 * <code>/home/hbci/passports</code>, allerdings bestehen die Dateinamen jetzt
 * aus dem Prefix card- sowie der Seriennummer der Chipkarte.
 * </p>
 * <p>
 * In der Regel wird hier nur eine Pfadangabe verwendet werden, dabei darf aber
 * auf keinen Fall der Slash (oder Backslash unter Windows) vergessen werden, da
 * der Dateiname aus einer simplen Aneinanderkettung von Parameterwert und
 * Seriennummer besteht.
 * </p>
 * </li>
 * <li><code>client.passport.DDV.libname.ddv</code> (für DDV-Passports)
 * <p>
 * Hier wird der vollständige Dateiname (also mit Pfadangabe) der shared library
 * (dynamisch ladbaren Bibliothek) angegeben, welche als Bindeglied zwischen
 * Java und der CTAPI-Bibliothek für den Chipkartenleser fungiert. Diese
 * Bibliothek wird bereits mit dem <em>HBCI4Java</em>-Paket mitgeliefert.
 * </p>
 * </li>
 * <li><code>client.passport.DDV.libname.ctapi</code> (für DDV-Passports)
 * <p>
 * Mit diesem Parameter wird der komplette Dateiname (mit Pfad) der
 * CTAPI-Bibliothek eingestellt, die die CTAPI-Schnittstelle für den zu
 * verwendenden Chipkartenleser implementiert. Diese Bibliothek ist vom
 * Hersteller des Chipkartenterminals zu beziehen.
 * </p>
 * </li>
 * <li><code>client.passport.DDV.port</code> (für DDV-Passports)
 * <p>
 * Die logische Portnummer, an der der Chipkartenleser angeschlossen ist (i.d.R.
 * 0, 1 oder 2, abhängig vom Anschluss (COM1, COM2, USB) und vom Treiber (manche
 * Treiber beginnen mit der Zählung bei 1, andere bei 0)) (am besten
 * ausprobieren). Achtung -- unter UN*X darauf achten, dass für den ausführenden
 * Nutzer Schreib- und Leserechte auf das entsprechende Device (/dev/ttyS0,
 * /dev/ttyUSB0 o.ä.) bestehen.
 * </p>
 * </li>
 * <li><code>client.passport.DDV.ctnumber</code> (für DDV-Passports)
 * <p>
 * Die logische Nummer des Chipkartenterminals, die im weiteren Verlauf
 * verwendet werden soll. Dies ist i.d.R. 0, falls mehrere Chipkartenterminals
 * angeschlossen und in Benutzung sind, sind diese einfach durchzunummerieren.
 * </p>
 * </li>
 * <li><code>client.passport.DDV.usebio</code> (für DDV-Passports)
 * <p>
 * Dieser Parameter kann entweder 0, 1 oder -1 sein und hat nur Bedeutung, wenn
 * die PIN-Eingabe direkt am Chipkartenterminal erfolgt (also wenn
 * <code>client.passport.DDV.softpin</code> ungleich 1 ist und wenn ein Keypad
 * am Chipkartenterminal vorhanden ist).
 * </p>
 * <p>
 * Wenn dieser Wert auf 1 gesetzt wird, so bedeutet das, dass die PIN-Eingabe
 * nicht manuell erfolgt, sondern dass statt dessen biometrische Merkmale des
 * Inhabers ausgewertet werden. Zurzeit ist dieses Feature speziell auf den
 * Chipkartenleser PinPad-Bio von Reiner-SCT angepasst, bei dem einem
 * Fingerabdruck eine PIN zugeordnet werden kann, deren Eingabe beim Auflegen
 * des Fingers simuliert wird. Für andere biometriefähige Chipkartenterminals
 * wird dieser Parameter wahrscheinlich nicht funktionieren, entsprechende
 * Unterstützung ist aber geplant.
 * </p>
 * <p>
 * Durch das Setzen dieses Wertes auf 0 wird das Benutzen der Biometrie-Einheit
 * definitiv abgeschaltet. Bei einem Wert von -1 wird automatisch geprüft, ob
 * eine Biometrie-Einheit verfügbar ist. Wenn ja, so wird diese benutzt,
 * ansonsten erfolgt die PIN-Eingabe über das Keypad des Chipkartenterminals
 * </p>
 * </li>
 * <li><code>client.passport.DDV.softpin</code> (für DDV-Passports)
 * <p>
 * Dieser Parameter kann entweder 0, 1 oder -1 enthalten. Für alle
 * Chipkartenterminals, die über keine eigene Tastatur zur Eingabe der PIN
 * verfügen, ist er auf 1 zu setzen. Damit wird der HBCI-Kernel darüber
 * informiert, dass die PIN vom Anwender über die PC-Tastatur einzugeben ist.
 * Durch Setzen dieses Wertes auf 0 wird die PIN-Eingabe für das Keypad des
 * Chipkartenlesers erzwungen.
 * </p>
 * <p>
 * Setzt man den Parameter auf -1, so wird automatisch erkannt, welches
 * PIN-Eingabeverfahren bei dem jeweils verwendeten Chipkartenterminal zu
 * benutzen ist.
 * </p>
 * </li>
 * <li><code>client.passport.DDV.entryidx</code> (für DDV-Passports)
 * <p>
 * Prinzipiell kann auf einer DDV-Chipkarte mehr als ein Datensatz mit
 * HBCI-Zugangsdaten gespeichert werden (bis zu fünf). Dieser Parameter legt
 * fest, welcher der fünf Datensätze tatsächlich benutzt werden soll. Da in den
 * meisten Fällen aber nur der erste Datensatzu tatsächlich belegt ist, wird
 * dieser Parameter meist den Wert "1" haben (ist auch default, falls dieser
 * Parameter gar nicht gesetzt ist).
 * </p>
 * </li>
 * <li><code>client.passport.DDV.pcsc.name</code> (für DDV-Passports bei
 * Verwendung von HBCIPassportDDVPCSC)
 * <p>
 * Wenn statt dem DDV-Passport der DDVPCSC-Passport (basierend auf
 * javax.smartcardio) verwendet wird, kann hier der Name des Kartenlesers
 * angegeben werden. Andernfalls wird der erste gefundene verwendet.
 * </p>
 * </li>
 * <li><code>client.passport.RDHNew.filename</code> (für RDHNew-Passports)
 * <p>
 * Dieser Parameter legt den Dateinamen der Schlüsseldatei fest. Diese Datei
 * sollte am besten auf einem mobilen Datenträger (Diskette) gespeichert sein.
 * Außerdem sollte ein Backup dieser Datei angefertigt werden, da bei Verlust
 * der Schlüsseldatei keine HBCI-Kommunikation mehr möglich ist.
 * </p>
 * </li>
 * <li><code>client.passport.RDHNew.init</code> (für RDHNew-Passports)
 * <p>
 * Dieser Parameter ist immer auf "1" zu setzen (wird nur intern anders
 * verwendet).
 * </p>
 * </li>
 * <li><code>client.passport.RDHNew.defaultprofile</code> (für RDHNew-Passports)
 * <p>
 * Kann verwendet werden, wenn die RDH-Profilversion beim Erstellen eines
 * Schluessel nicht ermittelbar ist, weil die Bank den anonymen BPD-Abruf nicht
 * unterstuetzt. Per Default wird hier "10" verwendet.
 * </p>
 * </li>
 * <li><code>client.passport.RDH.filename</code> (für RDH-Passports;
 * <b><em>diese Variante der RDH-Passports sollte nicht mehr benutzt werden,
 * sondern <code>RDHNew</code></em></b>; siehe Datei <code>README.RDHNew</code>)
 * <p>
 * analog zu <code>client.passport.RDHNew.filename</code>.
 * </p>
 * </li>
 * <li><code>client.passport.RDH.init</code> (für RDH-Passports; <b><em>diese
 * Variante der RDH-Passports sollte nicht mehr benutzt werden, sondern
 * <code>RDHNew</code></em></b>; siehe Datei <code>README.RDHNew</code>)
 * <p>
 * analog zu <code>client.passport.RDHNew.init</code>.
 * </p>
 * </li>
 * <li><code>client.passport.PinTan.filename</code> (für PIN/TAN-Passports)
 * <p>
 * Dieser Parameter legt den Dateinamen der "Schlüsseldatei" fest. Beim
 * PIN/TAN-Verfahren handelt es sich nicht wirklich um eine Schlüsseldatei, da
 * bei diesem Sicherheitsverfahren keine kryptografischen Schlüssel auf
 * HBCI-Ebene eingesetzt werden. In dieser Datei werden also nur die
 * HBCI-Zugangsdaten abgelegt.
 * </p>
 * </li>
 * <li><code>client.passport.PinTan.certfile</code> (für PIN/TAN-Passports)
 * <p>
 * Dieser Parameter gibt den Dateinamen einer Datei an, die ein Zertifikat für
 * die Kommunikation via HTTPS (SSL-Verschlüsselung) enthält. Diese Datei kann
 * mit dem Tool <code>keytool</code> erzeugt werden, welches zur
 * Java-Laufzeitumgebung gehört. Das Zertifikat (ein bestätigter öffentlicher
 * Schlüssel) kann i.d.R. von der Bank angefordert werden.
 * </p>
 * <p>
 * Dieser Parameter wird nur dann benötigt, wenn das SSL-Zertifikat der Bank
 * nicht mit dem defaultmäßig in die JRE eingebauten TrustStore überprüft werden
 * kann (also am besten erst ohne diesen Parameter ausprobieren - wenn eine
 * entsprechende Fehlermeldung erscheint, muss das jeweilige Zertifikat von der
 * Bank angefordert, mit <code>keytool</code> konvertiert und hier angegeben
 * werden). Wenn ein entsprechendes Root-Zertifikat für die Überprüfung gar
 * nicht zur Verfügung steht, so kann mit dem Parameter
 * <code>client.passport.PinTan.checkcert</code> die Zertifikatsüberprüfung
 * gänzlich deaktiviert werden.
 * </p>
 * </li>
 * <li><code>client.passport.PinTan.checkcert</code> (für PIN/TAN-Passports)
 * <p>
 * Dieser Parameter steht defaultmäßig auf "<code>1</code>". Wird dieser
 * Parameter allerdings auf "<code>0</code>" gesetzt, so wird die Überprüfung
 * des Bank-Zertifikates, welches für die SSL-Kommunikation verwendet wird,
 * deaktiviert. Diese Vorgehensweise wird nicht empfohlen, da dann Angriffe auf
 * das SSL-Protokoll und damit auch auf die HBCI-Kommunikation möglich sind. In
 * einigen Fällen steht aber kein Root-Zertifikat für die Überprüfung des
 * SSL-Zertifikats der Bank zur Verfügung, so dass diese Überprüfung
 * abgeschaltet werden <em>muss</em>, um überhaupt eine Kommunikation mit der
 * Bank zu ermöglichen.
 * </p>
 * </li>
 * <li><code>client.passport.PinTan.proxy</code> (für PIN/TAN-Passports)
 * <p>
 * Falls ausgehende HTTPS-Verbindungen über einen Proxy-Server laufen sollen,
 * kann der zu verwendende Proxy-Server mit diesem Parameter konfiguriert
 * werden. Das Format für den Wert dieses Kernel-Parameters ist "HOST:PORT",
 * also z.B. <code>proxy.intern.domain.com:3128</code>.
 * </p>
 * </li>
 * <li><code>client.passport.PinTan.proxyuser</code> (für PIN/TAN-Passports)
 * <p>
 * Falls für ausgehende HTTPS-Verbindungen (für HBCI-PIN/TAN) ein Proxy-Server
 * verwendet wird, und falls dieser Proxy-Server eine Authentifizierung
 * verlangt, kann mit diesem Parameter der Nutzername festgelegt werden.
 * </p>
 * <p>
 * Wenn dieser Parameter nicht gesetzt wird, wird bei Bedarf über einen Callback
 * (<code>NEED_PROXY_USER</code>) nach dem Nutzernamen gefragt.
 * </p>
 * </li>
 * <li><code>client.passport.PinTan.proxypass</code> (für PIN/TAN-Passports)
 * <p>
 * Falls für ausgehende HTTPS-Verbindungen (für HBCI-PIN/TAN) ein Proxy-Server
 * verwendet wird, und falls dieser Proxy-Server eine Authentifizierung
 * verlangt, kann mit diesem Parameter das Passwort festgelegt werden.
 * </p>
 * <p>
 * Wenn dieser Parameter nicht gesetzt wird, wird bei Bedarf über einen Callback
 * (<code>NEED_PROXY_PASS</code>) nach dem Passwort gefragt.
 * </p>
 * </li>
 * <li><code>client.passport.PinTan.init</code> (für PIN/TAN-Passports)
 * <p>
 * Dieser Parameter ist immer auf "1" zu setzen (wird nur intern anders
 * verwendet).
 * </p>
 * </li>
 * <li><code>client.passport.SIZRDHFile.filename</code> (für
 * SIZRDHFile-Passports)
 * <p>
 * Dieser Parameter legt den Dateinamen der SIZ-Schlüsseldatei fest. Dabei
 * handelt es sich um die Schlüsseldatei, die von anderer HBCI-Software (z.B.
 * <em>StarMoney</em>) erzeugt wurde.
 * </p>
 * <p>
 * Siehe dazu auch <code>README.SIZRDHFile</code>
 * </p>
 * </li>
 * <li><code>client.passport.SIZRDHFile.libname</code> (für
 * SIZRDHFile-Passports)
 * <p>
 * Dieser Parameter gibt den vollständigen Dateinamen der
 * SIZ-RDH-Laufzeitbibliothek an. Diese Bibliothek ist <em>nicht</em> Teil von
 * <em>HBCI4Java</em>, sondern muss separat von <a href=
 * "http://hbci4java.kapott.org#download">http://hbci4java.kapott.org</a>
 * heruntergeladen und installiert werden.
 * </p>
 * <p>
 * Siehe dazu auch <code>README.SIZRDHFile</code>
 * </p>
 * </li>
 * <li><code>client.passport.SIZRDHFile.init</code> (für SIZRDHFile-Passports)
 * <p>
 * Dieser Parameter ist immer auf "1" zu setzen (wird nur intern anders
 * verwendet).
 * </p>
 * <p>
 * Siehe dazu auch <code>README.SIZRDHFile</code>
 * </p>
 * </li>
 * <li><code>client.passport.RDHXFile.filename</code> (für RDHXFile-Passports)
 * <p>
 * Dieser Parameter legt den Dateinamen der RDHXFile-Schlüsseldatei fest. Dabei
 * handelt es sich um die Schlüsseldatei, die von anderer HBCI-Software (z.B.
 * <em>VR-NetWorld</em>, <em>ProfiCash</em>, ...) erzeugt wurde.
 * </p>
 * <li><code>client.passport.RDHXFile.init</code> (für RDHXFile-Passports)
 * <p>
 * Dieser Parameter ist immer auf "1" zu setzen (wird nur intern anders
 * verwendet).
 * </p>
 * </li>
 * <li><code>client.passport.Anonymous.filename</code> (für Anonymous-Passports)
 * <p>
 * Dieser Parameter legt den Dateinamen der Schlüsseldatei fest.
 * </p>
 * </li>
 * <li><code>client.passport.Anonymous.init</code> (für Anonymous-Passports)
 * <p>
 * Dieser Parameter ist immer auf "1" zu setzen (wird nur intern anders
 * verwendet).
 * </p>
 * </li>
 * <li><code>client.passport.default</code>
 * <p>
 * Wird bei der Erzeugung eines Passport-Objektes
 * ({@link org.kapott.hbci.passport.AbstractHBCIPassport#getInstance()}) nicht
 * explizit angegeben, für welches Sicherheitsverfahren ein Passport-Objekt
 * erzeugt werden soll, so wird der Wert dieses Parameters benutzt, um die
 * entsprechende Variante auszuwählen. Gültige Werte sind "<code>DDV</code>",
 * "<code>RDHNew</code>", "<code>RDH</code>" (nicht mehr benutzen!),
 * "<code>PinTan</code>", "<code>SIZRDHFile</code>", "<code>RDHXFile</code>"
 * oder "<code>Anonymous</code>" (Groß-/Kleinschreibung beachten).
 * </p>
 * </li>
 * <li><code>client.retries.passphrase</code>
 * <p>
 * Ist das Passwort für die Entschlüsselung der Passport-Datei falsch, so kann
 * die Eingabe so oft wiederholt werden, wie dieser Parameter angibt, bevor eine
 * Exception geworfen und die weitere Programmausführung unterbrochen wird.
 * </p>
 * </li>
 * <li><code>client.connection.localPort</code>
 * <p>
 * Für Anwendungen, die sich hinter einer Firewall befinden, welche nur
 * ausgehende Verbindungen mit bestimmten lokalen Portnummern zulässt (sowas
 * soll's geben), kann mit diesem Parameter die Portnummer festgelegt werden,
 * die lokal benutzt werden soll. Dieser Parameter hat im Moment nur bei
 * "normalen" HBCI-Verbindungen Auswirkungen. Beim PIN/TAN-Verfahren wird eine
 * HTTPS-Verbindung mit dem HBCI-Server aufgebaut, für diese Verbindung wird der
 * localPort-Parameter im Moment noch nicht ausgewertet.
 * </p>
 * </li>
 * <li><code>comm.standard.socks.server</code>
 * <p>
 * Soll fuer ausgehende Verbindungen ein SOCKS-Server verwendet werden, kann
 * dieser SOCKS-Server im Format <code>hostname:port</code> festgelegt werden.
 * Diese Einstellung wird <em>NICHT</em> fuer HBCI-PIN/TAN verwendet, sondern
 * nur fuer alle "richtigen" HBCI-Verbindungen (alle Passport-Varianten von RDH
 * und DDV).
 * </p>
 * </li>
 * <li><code>sepa.schema.validation</code>
 * <p>
 * Kann auf 1 gesetzt werden, wenn das erzeugte XML gegen das Schema validiert
 * werden soll.
 * </p>
 * </li>
 * <li><code>bpd.maxage.days</code>
 * <p>
 * Maximales Alter der BPD in Tagen nach deren Ablauf die BPD erneut abgerufen
 * werden - auch dann, wenn sich deren Versionsnummer nicht geaendert hat. Das
 * ermoeglicht das automatische Aktualisieren der BPD, wenn die Bank die
 * Versionsnummer nicht erhoeht. Ein Wert von "-1" bedeutet: Jedesmal BPD erneut
 * abrufen. Ein Wert von "0" bedeutet: Niemals BPD ohne Versionsaenderung erneut
 * abrufen. Der Default-Wet ist 7 - also einmal pro Woche.
 * </p>
 * </li>
 * <li><code>kernel.kernel.xmlpath</code>
 * <p>
 * (wird nicht gesetzt, zur Zeit nur intern benutzt)
 * </p>
 * </li>
 * <li><code>kernel.kernel.blzpath</code>
 * <p>
 * (wird nicht gesetzt, zur Zeit nur intern benutzt)
 * </p>
 * </li>
 * <li><code>kernel.kernel.challengedatapath</code>
 * <p>
 * (wird nicht gesetzt, zur Zeit nur intern benutzt)
 * </p>
 * </li>
 * <li><code>log.loglevel.default</code>
 * <p>
 * Mit diesem Parameter kann eingestellt werden, welche vom HBCI-Kernel
 * erzeugten Log-Ausgaben tatsächlich bis zur Anwendung gelangen. Dieser
 * Parameter kann Werte von 1 (nur Fehlermeldungen) bis 5 (einschließlich aller
 * Debug-Ausgaben) annehmen.
 * </p>
 * <p>
 * Bei Problemen mit dem Kernel diesen Level bitte auf 4 oder 5 setzen, alle
 * erzeugten Log-Ausgaben protokollieren und zusammen mit einer Beschreibung des
 * Problems an den <a href="mailto:hbci4java@kapott.org">Autor</a> schicken.
 * </p>
 * </li>
 * <li><code>log.filter</code>
 * <p>
 * Alle Meldungen, die via {@link #log(String,int)} erzeugt werden, durchlaufen
 * einen Log-Filter, um sensible Daten aus den Logs zu entfernen. Mit diesem
 * Kernel-Parameter wird eingestellt, wie stark der Filter filtert. Mögliche
 * Werte für <code>log.filter</code> sind:
 * </p>
 * <ul>
 * <li>0 - Es wird gar nicht gefiltert. Alle Daten erscheinen unbeschnitten im
 * Log.</li>
 * <li>1 - Es werden nur "geheime" Daten gefiltert (Passwörter, PINs, TANs,
 * ...)</li>
 * <li>2 - Es werden zusätzlich alle Daten gefiltert, anhand derer eine
 * Identifikation möglich wäre (Kontonummern, Namen, User-IDs, Kunden-IDs)</li>
 * <li>3 - Es werden auch weniger sensible Daten gefiltert (Bankleitzahlen,
 * Verwendungszweck, Geldbeträge, ...)</li>
 * </ul>
 * <p>
 * Die Standard-Einstellung dieses Wertes ist 2 - es werden also alle
 * "identifizierenden" Daten und alle "geheimen" Daten gefiltert.
 * </p>
 * </li>
 * <li><code>log.ssl.enable</code>
 * <p>
 * Dieser Parameter kann die Werte 0 und 1 annehmen. Ist er auf 1 gesetzt, wird
 * sämtliche Kommunikation, die bei Verwendung von HBCI-PIN/TAN über eine
 * HTTPS-Verbindung geht, im Klartext (also unverschlüsselt!) mitgeschnitten.
 * Das kann nützlich sein, um z.B. Probleme mit diversen HTTP-Request- oder
 * -Response-Headern zu finden. Diese Einstellung funktioniert allerdings
 * <b>NICHT mit Java-1.4.x</b> (Grund dafür ist eine spezielle Einschränkung der
 * JSSE). Der Standard-Wert für diese Einstellung ist 0 (also kein Logging).
 * Siehe dazu auch Kernel-Parameter <code>log.ssl.filename</code>.
 * </p>
 * </li>
 * <li><code>log.ssl.filename</code>
 * <p>
 * Wenn <code>log.ssl.enable=1</code>, so wird sämtliche HTTPS-Kommunikation
 * aller HBCI-PIN/TAN-Verbindungen mitgeschnitten und in die Datei geschrieben,
 * deren Dateiname mit diesem Parameter angegeben wird. Ist die Datei nicht
 * vorhanden, wird sie angelegt. Ist sie bereits vorhanden, werden die Log-Daten
 * angehängt. Wird kein Wert für diesen Parameter angegeben, gibt
 * <em>HBCI4Java</em> eine Warnung aus und erzeugt Log-Meldungen über den
 * <em>HBCI4Java</em>-Log-Mechanismus (Callback-Methode <code>log()</code>) mit
 * Log-Level <code>LOG_DEBUG2</code>.
 * </p>
 * </li>
 * <li><code>kernel.locale.language</code>, <code>kernel.locale.country</code>,
 * <code>kernel.locale.variant</code>
 * <p>
 * Mit diesen Kernel-Parameter kann die von <em>HBCI4Java</em> intern verwendete
 * Locale gesetzt werden. Nach dem Ändern dieser Werte muss die Methode
 * {@link #initLocale()} aufgerufen werden, damit diese Änderungen wirksam
 * werden. Die <em>HBCI4Java</em>-Locale hat Einfluss auf die Sprache der
 * erzeugten Callback-Messages, Exception-Texte sowie die Arbeit von
 * Konvertierungs-Funktionen wie {@link #date2StringLocal(Date)} u.ä.
 * </p>
 * </li>
 * <li><code>kernel.rewriter</code>
 * <p>
 * Einige HBCI-Server-Implementationen bzw. die Backend-Systeme einiger Banken
 * halten sich nicht strikt an die in der HBCI-Spezifikation vorgeschriebenen
 * Formate. Um solche Unzulänglichkeiten nicht direkt im HBCI-Kernel abfangen zu
 * müssen, existieren sogenannte Rewriter-Module. Ein solches Modul ist für
 * jeweils einen bekannten "Bug" zuständig. Kurz vor dem Versand und direkt nach
 * dem Eintreffen von HBCI-Nachrichten werden diese durch alle registrierten
 * Rewriter-Module geschickt. Für ausgehende Nachrichten werden hier u.U. nicht
 * HBCI-konforme Veränderungen vorgenommen, die vom jeweiligen HBCI-Server so
 * erwartet werden. Eingehende Nachrichten, die nicht HBCI-konform sind, werden
 * so umgeformt, dass sie der Spezifikation entsprechen. Auf diese Art und Weise
 * kann der HBCI-Kernel immer mit streng HBCI-konformen Nachrichten arbeiten.
 * Siehe dazu auch die Paketdokumentation zum Paket
 * <code>org.kapott.hbci.rewrite</code>.
 * </p>
 * <p>
 * Der Parameter <code>kernel.rewriter</code> legt die Liste aller
 * Rewriter-Module fest, welche eingehende und ausgehende Nachrichten
 * durchlaufen sollen. Wird dieser Parameter nicht gesetzt, so verwendet
 * <em>HBCI4Java</em> eine default-Liste von aktivierten Rewriter-Modulen (kann
 * mit {@link #getParam(String)} ermittelt werden). Wird dieser Parameter
 * gesetzt, so wird die default-Einstellung überschrieben. Es können mehrere zu
 * durchlaufende Rewriter-Module angegeben werden, indem sie durch Komma
 * voneinander getrennt werden.
 * </p>
 * </li>
 * <li><code>kernel.threaded.maxwaittime</code>
 * <p>
 * Beim Verwenden des threaded-callback-Mechanismus (siehe Datei
 * <code>README.ThreadedCallbacks</code>) wird die eigentliche Ausführung der
 * HBCI-Dialoge und die Interaktion mit der Anwendung auf mehrere Threads
 * verteilt. Es ist jeweils einer der beteiligten Threads "aktiv" - die anderen
 * Threads warten auf eine Nachricht vom gerade aktiven Thread. Um das System
 * nicht mit "unendlich lange wartenden" Threads zu belasten, warten die jeweils
 * inaktiven Threads nur eine bestimmte Zeitspanne auf eine Nachricht vom
 * aktiven Thread. Diese Zeitspanne kann mit diesem Kernel-Parameter
 * konfiguriert werden. Falls nach der hier konfigurierten Zeitspanne keine
 * Nachricht empfangen wurde, beendet sich der jeweils wartende Thread selbst.
 * Falls der aktive Thread nach Ablauf dieser Zeitspanne versucht, eine
 * Nachricht an den wartenden Thread zu senden, wird eine
 * <code>RuntimeException</code> geworfen.
 * </p>
 * <p>
 * Die Zeitspanne wird in Sekunden angegeben. Der default-Wert beträgt 300 (5
 * Minuten).
 * </p>
 * </li>
 * <li>
 * <p>
 * Die folgenden Parameter legen die Größe sog. Object-Pools fest, die intern
 * von <em>HBCI4Java</em> verwendet werden. Object-Pools stellen eine Art Cache
 * dar, um Instanzen häufig benutzter Klassen nicht jedesmal neu zu erzeugen.
 * Statt dessen werden nicht mehr benötigte Objekte in einem Pool verwaltet, aus
 * dem bei Bedarf wieder Objekte entnommen werden. Die Größe der Pools für die
 * einzelnen Objekttypen kann hier festgelegt werden. Falls Speicherprobleme
 * auftreten (<code>OutOfMemory</code>-Exception), so sollten diese Werte
 * verringert werden. Durch Setzen eines Wertes auf "<code>0</code>" wird das
 * Object-Pooling für die entsprechenden Objekte komplett deaktiviert. Zur Zeit
 * werden nur bei der Nachrichtenerzeugung und -analyse Object-Pools verwendet.
 * In der folgenden Auflistung steht in Klammern jeweils der eingebaute
 * default-Wert.
 * </p>
 * <ul>
 * <li>
 * <p>
 * <code>kernel.objpool.MSG</code> -- Pool für Nachrichten-Objekte (3)
 * </p>
 * </li>
 * <li>
 * <p>
 * <code>kernel.objpool.SF</code> -- Pool für SF- (Segmentfolgen-) Objekte (128)
 * </p>
 * </li>
 * <li>
 * <p>
 * <code>kernel.objpool.SEG</code> -- Pool für Segment-Objekte (256)
 * </p>
 * </li>
 * <li>
 * <p>
 * <code>kernel.objpool.DEG</code> -- Pool für DEG- (Datenelementgruppen-)
 * Objekte (256)
 * </p>
 * </li>
 * <li>
 * <p>
 * <code>kernel.objpool.DE</code> -- Pool für Datenelement-Objekte (1024)
 * </p>
 * </li>
 * <li>
 * <p>
 * <code>kernel.objpool.Sig</code> -- Pool für Signatur-Objekte (3)
 * </p>
 * </li>
 * <li>
 * <p>
 * <code>kernel.objpool.Crypt</code> -- Pool für Crypt-Objekte (3)
 * </p>
 * </li>
 * <li>
 * <p>
 * <code>kernel.objpool.Syntax</code> -- Pool für Daten-Objekte (=Werte in
 * Nachrichten) (128 je Datentyp)
 * </p>
 * </li>
 * </ul>
 * </li>
 * <li>
 * <p>
 * Mit den folgenden Parametern kann <em>HBCI4Java</em> veranlasst werden, beim
 * Auftreten bestimmter Fehler keine Exception zu werfen, sondern diesen Fehler
 * zu ignorieren bzw. den Anwender entscheiden zu lassen, ob der Fehler
 * ignoriert werden soll. Bei den Fehlern handelt es sich hauptsächlich um
 * Fehler, die beim überprüfen von Eingabedaten und Institutsnachrichten bzgl.
 * der Einhaltung der HBCI-Spezifikation auftreten.
 * </p>
 * <p>
 * Jeder der folgenden Parameter kann einen der Werte <code>yes</code>,
 * <code>no</code> oder <code>callback</code> annehmen. Ist ein Parameter auf
 * <code>no</code> gesetzt, so wird beim Auftreten des jeweiligen Fehlers eine
 * entsprechende Exception geworfen. Dieses Verhalten ist das Standardverhalten
 * und entspricht dem der Vorgängerversionen von <em>HBCI4Java</em>. Ist ein
 * Parameter auf <code>yes</code> gesetzt, so wird der Fehler komplett
 * ignoriert. Es wird nur eine entsprechende Warnung mit Loglevel
 * <code>LOG_WARN</code> erzeugt. Wird ein Parameter auf <code>callback</code>
 * gesetzt, so wird ein Callback mit dem Callback-Reason <code>HAVE_ERROR</code>
 * erzeugt, bei dem die Callback-Message (Parameter <code>msg</code>) die
 * entsprechende Fehlermeldung enthält. Gibt die Callback-Methode einen leeren
 * String im <code>retData</code>-Objekt zurück, so bedeutet das für
 * <em>HBCI4Java</em>, dass der entsprechende Fehler ignoriert werden soll (so
 * als wäre der Parameter auf <code>yes</code> gesetzt). Ist der Rückgabestring
 * nicht leer, so wird <em>HBCI4Java</em> eine entsprechende Exception werfen,
 * so als wäre der zugehörige Parameter gleich <code>no</code>. Nähere
 * Informationen zu Callbacks befinden sich in der Beschreibung des Interfaces
 * {@link org.kapott.hbci.callback.HBCICallback}.
 * </p>
 * <p>
 * <b>"Normalen" Benutzern von <em>HBCI4Java</em> ist dringend von der
 * Verwendung dieser Parameter abzuraten, weil sie bei falscher Anwendung dazu
 * führen können, dass <em>HBCI4Java</em> gar nicht mehr funktioniert.</b> Diese
 * Parameter sind nur für <em>HBCI4Java</em>-Entwickler (also mich ;-)) gedacht
 * und sind hier nur der Vollständigkeit halber aufgeführt.
 * </p>
 * <p>
 * Eine genauere Beschreibung der einzelnen Parameter befindet sich in der
 * Properties-Template-Datei <code>hbci.props.template</code>.
 * </p>
 * </li>
 * <li><code>client.errors.ignoreJobResultStoreErrors</code></li>
 * <li><code>client.errors.ignoreWrongJobDataErrors</code></li>
 * <li><code>client.errors.ignoreWrongDataLengthErrors</code></li>
 * <li><code>client.errors.ignoreWrongDataSyntaxErrors</code></li>
 * <li><code>client.errors.ignoreAddJobErrors</code></li>
 * <li><code>client.errors.ignoreCreateJobErrors</code></li>
 * <li><code>client.errors.ignoreExtractKeysErrors</code></li>
 * <li><code>client.errors.ignoreDialogEndErrors</code></li>
 * <li><code>client.errors.ignoreSecMechCheckErrors</code></li>
 * <li><code>client.errors.ignoreVersionCheckErrors</code></li>
 * <li><code>client.errors.ignoreSignErrors</code></li>
 * <li><code>client.errors.ignoreMsgSizeErrors</code></li>
 * <li><code>client.errors.ignoreCryptErrors</code></li>
 * <li><code>client.errors.ignoreMsgCheckErrors</code></li>
 * <li><code>client.errors.allowOverwrites</code></li>
 * <li><code>client.errors.ignoreValidValueErrors</code></li>
 * <li><code>client.errors.ignoreSegSeqErrors</code></li>
 * </ul>
 */
public final class HBCIUtils
{
  /**
   * Die offizielle HBCI-Produktregistrierung von HBCI4Java - siehe http://hbci-zka.de/register/register_faq.htm
   */
  public final static String PRODUCT_ID = "36792786FA12F235F04647689";
  
  private final static String VERSION = HBCIUtils.class.getPackage().getImplementationVersion();
  
  /** Loglevel für keine Ausgaben */
	public static final int								LOG_NONE	= 0;
	/** Loglevel für Fehlerausgaben */
	public static final int								LOG_ERR		= 1;
	/** Loglevel für Warnungen */
	public static final int								LOG_WARN	= 2;
	/** Loglevel für Informationen */
	public static final int								LOG_INFO	= 3;
	/** Loglevel für Debug-Ausgaben */
	public static final int								LOG_DEBUG	= 4;
	/** Loglevel für Debug-Ausgaben für extreme-Debugging */
	public static final int								LOG_DEBUG2	= 5;
	/** Loglevel für devel-Debugging - nicht benutzen! */
	public static final int								LOG_INTERN	= 6;

	private static Hashtable<ThreadGroup, Properties>	configs;																							// threadgroup->hashtable(paramname->paramvalue)
	private static char[]								base64table	= { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
	static
	{
		initDataStructures();
	}

	private static void initDataStructures ( )
	{
		configs = new Hashtable<ThreadGroup, Properties>();
		HBCIUtilsInternal.callbacks = new Hashtable<ThreadGroup, HBCICallback>();
		HBCIUtilsInternal.blzs = new Properties();
		HBCIUtilsInternal.banks = new HashMap<String, BankInfo>();
		HBCIUtilsInternal.locMsgs = new Hashtable<ThreadGroup, ResourceBundle>();
		HBCIUtilsInternal.locales = new Hashtable<ThreadGroup, Locale>();
	}

	private HBCIUtils ()
	{}

	/**
	 * Lädt ein Properties-File, welches über ClassLoader.getRessourceAsStream()
	 * gefunden wird. Der Name des Property-Files wird durch den Parameter
	 * <code>configfile</code> bestimmt. Wie dieser Name interpretiert wird, um
	 * das Property-File tatsächlich zu finden, hängt von dem zum Laden
	 * benutzten ClassLoader ab. Im Parameter <code>cl</code> kann dazu eine
	 * ClassLoader-Instanz übergeben werden, deren
	 * <code>getRessource</code>-Methode benutzt wird, um das Property-File zu
	 * lokalisieren und zu laden. Wird kein ClassLoader angegeben
	 * (<code>cl==null</code>), so wird zum Laden des Property-Files der
	 * ClassLoader benutzt, der auch zum Laden der aufrufenden Klasse benutzt
	 * wurde.
	 *
	 * @param cl
	 *            ClassLoader, der zum Laden des Property-Files verwendet werden
	 *            soll
	 * @param configfile
	 *            Name des zu ladenden Property-Files (kann <code>null</code>
	 *            sein - in dem Fall gibt diese Methode auch <code>null</code>
	 *            zurück).
	 * @return Properties-Objekt
	 */
	public static Properties loadPropertiesFile ( ClassLoader cl, String configfile )
	{
		Properties props = null;

		if (configfile != null)
		{
			try
			{
				// load kernel params from properties file
				/* determine classloader to be used */
				if (cl == null)
				{
					try
					{
						throw new Exception();
					}
					catch (Exception e)
					{
						StackTraceElement[] stackTrace = e.getStackTrace();

						if (stackTrace.length > 1)
						{
							String classname = stackTrace[1].getClassName();
							cl = Class.forName(classname).getClassLoader();
						}
					}

					if (cl == null)
					{
						cl = ClassLoader.getSystemClassLoader();
					}
				}

				// TODO: im fehlerfall wird hier nur f==null zurueckgegeben,
				// so dass man ueber die eigentliche fehlerursache (file not
				// found,
				// permission denied) nichts erfaehrt

				/* get an input stream */
				InputStream f = null;
				f = cl.getResourceAsStream(configfile);
				if (f == null)
				{
					throw new InvalidUserDataException("*** can not load config file " + configfile);
				}

				props = new Properties();
				props.load(f);
				f.close();
			}
			catch (Exception e)
			{
				throw new HBCI_Exception("*** can not load config file " + configfile, e);
			}
		}

		return props;
	}

	/**
	 * <p>
	 * Initialisieren der <em>HBCI4Java</em>-Umgebung. Diese Methode muss
	 * <em>vor allen anderen</em> HBCI-Methoden aufgerufen werden. Hiermit wird
	 * die <em>HBCI4Java</em>-Laufzeitumgebung initialisiert. Dazu gehören das
	 * Laden verschiedener Dateien aus dem <em>HBCI4Java</em>-Classpath (Dateien
	 * für die Lokalisierung von Nachrichten, Verzeichnis der Banken usw.) sowie
	 * das Initialisieren einiger interner Datenstrukturen.
	 * <p>
	 * Zusätzlich wird in dieser Methode die Methode
	 * {@link #initThread(Properties,HBCICallback)} aufgerufen, um alle
	 * Datenstrukturen, die <code>ThreadGroup</code>-weise verwaltet werden, für
	 * die aktuelle <code>ThreadGroup</code> zu initialisieren. Siehe dazu auch
	 * die Dokumentation zu {@link #initThread(Properties,HBCICallback)} sowie
	 * die Datei <code>README.MultiThread</code>.
	 * </p>
	 *
	 * @param props
	 *            <code>Properties</code>-Objekt mit Initialisierungs-Werten für
	 *            die Kernel-Parameter. Darf <code>null</code> sein.
	 * @param callback
	 *            das zu verwendende Callback-Objekt. Beim Aufruf dieser Methode
	 *            darf <code>callback</code> niemals <code>null</code> sein (im
	 *            Gegensatz zum Aufruf von <code>initThread</code>, um weitere
	 *            <code>ThreadGroups</code> zu initialisieren).
	 */
	public static synchronized void init ( Properties props, HBCICallback callback )
	{
		try
		{
			initThread(props, callback);
			HBCIUtils.log("hbci4java " + version(), HBCIUtils.LOG_INFO);

			refreshBLZList(HBCIUtils.class.getClassLoader());

			if (Security.getProvider("CryptAlgs4Java") == null)
			{
				Security.addProvider(new CryptAlgs4JavaProvider());
			}
		}
		catch (Exception e)
		{
			throw new HBCI_Exception("*** error while initializing HBCI4Java", e);
		}
	}

	/**
	 * Wrapper für {@link #init(Properties,HBCICallback)}. Siehe auch
	 * {@link #initThread(ClassLoader, String, HBCICallback)}.
	 *
	 * @param cl
	 *            der ClassLoader, der zum Laden von <code>configfile</code>
	 *            verwendet werden soll.
	 * @param configfile
	 *            der Name des zu ladenden Property-Files.
	 * @param callback
	 *            das zu verwendende Callback-Objekt. Beim Aufruf dieser Methode
	 *            darf <code>callback</code> niemals <code>null</code> sein (im
	 *            Gegensatz zum Aufruf von <code>initThread</code>, um weitere
	 *            <code>ThreadGroups</code> zu initialisieren).
	 * @deprecated
	 */
	@Deprecated
	public static synchronized void init ( ClassLoader cl, String configfile, HBCICallback callback )
	{
		init(loadPropertiesFile(cl, configfile), callback);
	}

	/**
	 * Entspricht {@link #initThread(ClassLoader,String,HBCICallback)
	 * initThread(cl,configfile,null)}
	 *
	 * @deprecated
	 */
	@Deprecated
	public static synchronized void initThread ( ClassLoader cl, String configfile )
	{
		initThread(cl, configfile, null);
	}

	/**
	 * Initialisieren der <em>HBCI4Java</em>-Umgebung für eine neue
	 * <code>ThreadGroup</code>. Soll <em>HBCI4Java</em> in einer multi-threaded
	 * Anwendung verwendet werden, bei der mehrere Threads gleichzeitig
	 * <em>HBCI4Java</em> benutzen, so muss für jeden solchen Thread eine
	 * separate <code>ThreadGroup</code> angelegt werden. Jede dieser
	 * <code>ThreadGroup</code>s muss mit dieser Methode für die Benutzung von
	 * <em>HBCI4Java</em> initialisiert werden. Alle HBCI-Kernel-Parameter sowie
	 * die HBCI-Callbacks werden für jede <code>ThreadGroup</code> separat
	 * verwaltet, so dass jede <code>ThreadGroup</code> also einen eigenen Satz
	 * dieser Daten benutzt.
	 * <p>
	 * Der Thread, in dem die Methode <code>HBCIUtils.init()</code> aufgerufen
	 * wird, muss <em>nicht</em> zusätzlich mit <code>initThread()</code>
	 * initialisiert werden, das wird automatisch von der Methode
	 * <code>init()</code> übernommen.
	 * </p>
	 * <p>
	 * Siehe dazu auch die Datei <code>README.MultiThreading</code> in den
	 * <em>HBCI4Java</em>-Archiven.
	 * </p>
	 * <p>
	 * Ist der Parameter <code>props</code> ungleich <code>null</code>, so
	 * werden die Kernel-Parameter für die aktuelle <code>ThreadGroup</code> mit
	 * den darin angegebenen Werten initialisiert.
	 * </p>
	 * <p>
	 * Außerdem wird mit dieser Methode ein Callback-Objekt registriert, welches
	 * von <em>HBCI4Java</em> für die Kommunikation mit der Anwendung verwendet
	 * wird.
	 * </p>
	 *
	 * @param props
	 *            <code>Property</code>-Objekt mit initialisierungs-Werten für
	 *            die Kernel-Parameter. Darf auch <code>null</code> sein.
	 * @param callback
	 *            ein Objekt einer <code>HBCICallback</code>-Klasse, das benutzt
	 *            wird, um Anfragen des Kernels (benötigte Daten, benötige
	 *            Chipkarte, wichtige Informationen während der
	 *            Dialog-Ausführung etc.) an die Anwendung weiterzuleiten. Siehe
	 *            dazu {@link org.kapott.hbci.callback.HBCICallback}. Jede
	 *            <code>ThreadGroup</code> kann ein eigenes Callback-Objekt
	 *            registrieren, welches dann für alle HBCI-Prozesse innerhalb
	 *            dieser <code>ThreadGroup</code> verwendet wird. Wird beim
	 *            Initialisieren einer <code>ThreadGroup</code> kein
	 *            <code>callback</code>-Objekt angegeben
	 *            (<code>callback==null</code>), dann wird für diese
	 *            <code>ThreadGroup</code> das Callback-Objekt der
	 *            "Eltern-<code>ThreadGroup</code>" verwendet. Die "initiale"
	 *            <code>ThreadGroup</code>, die mit
	 *            {@link #init(Properties,HBCICallback)} initialisiert wird,
	 *            muss ein <cod>callback!=null</code> spezifizieren.
	 */
	public static synchronized void initThread ( Properties props, HBCICallback callback )
	{
		ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();

		if (HBCIUtilsInternal.callbacks.get(threadgroup) != null)
		{
			HBCIUtils.log("will not initialize this threadgroup because it is already initialized", HBCIUtils.LOG_WARN);
		}
		else
		{
			try
			{
				// initialize kernel params
				Properties config = new Properties();
				if (props != null)
				{
					config.putAll(props);
				}

				synchronized (configs)
				{
					configs.put(threadgroup, config);
				}
				if (getParam("kernel.rewriter") == null)
				{
					setParam(	"kernel.rewriter",
								"InvalidSegment,WrongStatusSegOrder,WrongSequenceNumbers,MissingMsgRef,HBCIVersion,SigIdLeadingZero,InvalidSuppHBCIVersion,SecTypeTAN,KUmsDelimiters,KUmsEmptyBDateSets");
				}

				// initialize callback
				if (callback == null)
				{
					ThreadGroup parent = Thread.currentThread().getThreadGroup().getParent();
					callback = HBCIUtilsInternal.callbacks.get(parent);
					if (callback == null)
					{
						throw new NullPointerException("no callback specified");
					}
				}
				HBCIUtilsInternal.callbacks.put(threadgroup, callback);

				// configure Locale
				initLocale();

				HBCIUtils.log("initialized HBCI4Java for thread group " + threadgroup.getName(), HBCIUtils.LOG_DEBUG);

			}
			catch (Exception ex)
			{
				throw new HBCI_Exception("*** could not init HBCI4Java for thread group " + threadgroup.getName(), ex);
			}
		}
	}

	/**
	 * Wrapper für {@link #initThread(Properties,HBCICallback)}.
	 * <p>
	 * Ist der Parameter <code>configfile</code> ungleich <code>null</code>, so
	 * wird versucht, ein Property-File mit default-Einstellungen für die
	 * HBCI-Kernel-Parameter für die aktuelle <code>ThreadGroup</code> zu laden.
	 * Der Name des Property-Files wird durch den Parameter
	 * <code>configfile</code> bestimmt. Wie dieser Name interpretiert wird, um
	 * das Property-File tatsächlich zu finden, hängt von dem zum Laden
	 * benutzten ClassLoader ab. Im Parameter <code>cl</code> kann dazu eine
	 * ClassLoader-Instanz übergeben werden, deren
	 * <code>getRessource</code>-Methode benutzt wird, um das Property-File zu
	 * lokalisieren und zu laden. Wird kein ClassLoader angegeben
	 * (<code>cl==null</code>), so wird zum Laden des Property-Files der
	 * ClassLoader benutzt, der auch zum Laden der aufrufenden Klasse benutzt
	 * wurde.
	 * </p>
	 * <p>
	 * <b>Achtung</b>: Dieser Default-ClassLoader ist in den meisten Fällen ein
	 * ClassLoader, der in einem JAR-File bzw. im aktuellen CLASSPATH nach
	 * Ressourcen sucht. Soll ein Property-File von einer bestimmten Stelle im
	 * Filesystem geladen werden, so sollte hier statt dessen der ClassLoader
	 * {@link org.kapott.hbci.manager.FileSystemClassLoader} benutzt werden. In
	 * diesem Fall wird der angegebene Dateiname als relativer Pfad von der
	 * <em>Wurzel</em> des Dateisystems aus interpretiert. Eine Demonstration
	 * befindet sich im Tool
	 * {@link org.kapott.hbci.tools.AnalyzeReportOfTransactions}.
	 * </p>
	 *
	 * @param cl
	 *            der ClassLoader, der verwendet werden soll, um das
	 *            Property-File <code>configfile</code> zu laden (mit der
	 *            Methode <code>ClassLoader.getRessource()</code>). Ist dieser
	 *            Parameter <code>null</code>, so wird der ClassLoader
	 *            verwendet, der auch zum Laden <em>der</em> Klasse benutzt
	 *            wurde, die die aufrufende Methode enthält.
	 * @param configfile
	 *            der Name des zu ladenden Property-Files. Ist dieser Parameter
	 *            <code>null</code>, kein Property-File geladen.
	 * @deprecated use {@link #initThread(Properties, HBCICallback)} instead
	 */
	@Deprecated
	public static synchronized void initThread ( ClassLoader cl, String configfile, HBCICallback callback )
	{
		initThread(loadPropertiesFile(cl, configfile), callback);
	}

	/**
	 * Aufräumen der Datenstrukturen für aktuelle <code>ThreadGroup</code>. Alle
	 * <code>ThreadGroups</code>, die via
	 * {@link #initThread(Properties,HBCICallback)} initialisiert wurden,
	 * sollten kurz vor deren Ende mit dieser Methode wieder "aufgeräumt"
	 * werden, damit <em>HBCI4Java</em> die entsprechenden Datenstrukturen für
	 * diese <code>ThreadGroup</code> wieder freigeben kann.
	 */
	public static synchronized void doneThread ( )
	{
		HBCIUtils.log("removing all data for current thread", HBCIUtils.LOG_DEBUG);

		ThreadGroup group = Thread.currentThread().getThreadGroup();
		HBCIUtilsInternal.callbacks.remove(group);
		configs.remove(group);
		HBCIUtilsInternal.locMsgs.remove(group);
		HBCIUtilsInternal.locales.remove(group);
	}

	/**
	 * Bereinigen aller <em>HBCI4Java</em>-Datenstrukturen. Nach Aufruf dieser
	 * Methode kann keine andere <em>HBCI4Java</em>-Funktion mehr benutzt
	 * werden. Durch erneuten Aufruf von {@link #init(Properties,HBCICallback)}
	 * kann <em>HBCI4Java</em> wieder re-initialisiert werden.
	 */
	public static synchronized void done ( )
	{
		HBCIUtils.log("destroying all HBCI4Java resources", HBCIUtils.LOG_DEBUG);
		initDataStructures();
	}

	/**
	 * Aktualisieren der von <em>HBCI4Java</em> verwendeten Locale innerhalb der
	 * aktuellen ThreadGroup. Wenn die Kernel-Parameter
	 * <code>kernel.locale.*</code> geändert wurden, muss anschließend diese
	 * Methode aufgerufen werden, damit die Werte für diese Kernel-Parameter
	 * geprüft und die entsprechende Locale für die aktuelle ThreadGroup
	 * aktiviert wird. Beim Aufruf von {@link #init(Properties, HBCICallback)}
	 * bzw. {@link #initThread(Properties, HBCICallback)} wird diese Methode
	 * automatisch aufgerufen - ein manueller Aufruf ist also nur notwendig,
	 * wenn die Kernel-Parameter <code>kernel.locale.*</code> <em>nach</em> dem
	 * Initialisieren des aktuellen Threads geändert werden.
	 */
	public static void initLocale ( )
	{
		String localeLang = getParam("kernel.locale.language", "");
		String localeCountry = getParam("kernel.locale.country", "");
		String localeVariant = getParam("kernel.locale.variant", "");
		Locale locale;

		if (localeLang.trim().length() == 0)
		{
			locale = Locale.getDefault();
			log("using default system locale " + locale.toString(), HBCIUtils.LOG_DEBUG);

		}
		else
		{
			locale = new Locale(localeLang.trim(), localeCountry.trim(), localeVariant.trim());
			log("using specified locale " + locale.toString(), HBCIUtils.LOG_DEBUG);
		}

		ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();
		synchronized (HBCIUtilsInternal.locales)
		{
			HBCIUtilsInternal.locales.put(threadgroup, locale);
		}
		synchronized (HBCIUtilsInternal.locMsgs)
		{
			HBCIUtilsInternal.locMsgs.put(threadgroup, ResourceBundle.getBundle("hbci4java-messages", locale));
		}
	}

	/**
	 * Gibt die Locale zurück, die von <em>HBCI4Java</em> innerhalb der
	 * aktuellen ThreadGroup verwendet wird. Siehe auch Kernel-Parameter
	 * <code>kernel.locale.*</code> sowie {@link #initLocale()}.
	 */
	public static Locale getLocale ( )
	{
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		return HBCIUtilsInternal.locales.get(group);
	}

	/**
	 * Gibt den aktuellen Wert eines bestimmten HBCI-Parameters zurück. Für jede
	 * {@link java.lang.ThreadGroup} wird ein separater Satz von HBCI-Parametern
	 * verwaltet.
	 *
	 * @param st
	 *            Name des HBCI-Parameters
	 * @param def
	 *            default-Wert, falls dieser Parameter nicht definiert ist
	 * @return den Wert des angegebenen HBCI-Parameters
	 */
	public static String getParam ( String st, String def )
	{
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		Properties config = getParams();
		if (config == null)
		{
			throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_THREAD_NOTINIT", group.getName()));
		}
		return config.getProperty(st, def);
	}

	/**
	 * Gibt eine Map aller in der aktuellen ThreadGroup gesetzten
	 * Kernel-Parameter zurück.
	 */
	public static Properties getParams ( )
	{
		Properties params;
		ThreadGroup threadgroup = Thread.currentThread().getThreadGroup();

		synchronized (configs)
		{
			params = configs.get(threadgroup);
		}

		return params;
	}

	/**
	 * Gibt den aktuellen Wert eines bestimmten HBCI-Parameters zurück. Für jede
	 * {@link java.lang.ThreadGroup} wird ein separater Satz von HBCI-Parametern
	 * verwaltet.
	 *
	 * @param st
	 *            Name des HBCI-Parameters
	 * @return den Wert des angegebenen HBCI-Parameters
	 */
	public static String getParam ( String st )
	{
		return getParam(st, null);
	}

	/**
	 * Ermittelt zu einer gegebenen Bankleitzahl den Namen des Institutes.
	 *
	 * @param blz
	 *            die Bankleitzahl
	 * @return den Namen des dazugehörigen Kreditinstitutes. Falls die
	 *         Bankleitzahl unbekannt ist, so wird ein leerer String
	 *         zurückgegeben
	 */
	public static String getNameForBLZ ( String blz )
	{
		BankInfo info = getBankInfo(blz);
		if (info == null)
		{
			return "";
		}
		return info.getName() != null ? info.getName() : "";
	}

	/**
	 * Liefert die Bank-Informationen zur angegebenen BLZ.
	 *
	 * @param blz
	 *            die BLZ.
	 * @return die Bank-Informationen oder NULL, wenn zu der BLZ keine
	 *         Informationen bekannt sind.
	 */
	public static BankInfo getBankInfo ( String blz )
	{
		return HBCIUtilsInternal.banks.get(blz);
	}

	/**
	 * Liefert eine Liste von Bank-Informationen, die zum angegebenen
	 * Suchbegriff passen.
	 *
	 * @param query
	 *            der Suchbegriff. Der Suchbegriff muss mindestens 3 Zeichen
	 *            enthalten und ist nicht case-sensitive. Der Suchbegriff kann
	 *            im Ort der Bank oder in deren Namen enthalten sein. Oder die
	 *            BLZ oder BIC beginnt mit diesem Text.
	 * @return die Liste der Bank-Informationen. Die Ergebnis-Liste ist nach BLZ
	 *         sortiert. Die Funktion liefert niemals NULL sondern hoechstens
	 *         eine leere Liste.
	 */
	public static List<BankInfo> searchBankInfo ( String query )
	{
		if (query != null)
		{
			query = query.trim();
		}

		List<BankInfo> list = new LinkedList<BankInfo>();
		if (query == null || query.length() < 3)
		{
			return list;
		}

		query = query.toLowerCase();

		for (BankInfo info : HBCIUtilsInternal.banks.values())
		{
			String blz = info.getBlz();
			String bic = info.getBic();
			String name = info.getName();
			String loc = info.getLocation();

			// Anhand der BLZ?
			if (blz != null && blz.startsWith(query))
			{
				list.add(info);
				continue;
			}

			// Anhand der BIC?
			if (bic != null && bic.toLowerCase().startsWith(query))
			{
				list.add(info);
				continue;
			}

			// Anhand des Namens?
			if (name != null && name.toLowerCase().contains(query))
			{
				list.add(info);
				continue;
			}
			// Anhand des Orts?
			if (loc != null && loc.toLowerCase().contains(query))
			{
				list.add(info);
				continue;
			}
		}

		Collections.sort(list, new Comparator<BankInfo>()
		{
			/**
			 * @see java.util.Comparator#compare(java.lang.Object,
			 *      java.lang.Object)
			 */
			@Override
			public int compare ( BankInfo o1, BankInfo o2 )
			{
				if (o1 == null || o1.getBlz() == null)
				{
					return -1;
				}
				if (o2 == null || o2.getBlz() == null)
				{
					return 1;
				}

				return o1.getBlz().compareTo(o2.getBlz());
			}
		});

		return list;
	}

	/**
	 * Gibt zu einer gegebenen Bankleitzahl den BIC-Code zurück.
	 *
	 * @param blz
	 *            Bankleitzahl der Bank
	 * @return BIC-Code dieser Bank. Falls kein BIC-Code bekannt ist, wird ein
	 *         leerer String zurückgegeben.
	 * @deprecated Bitte {@link HBCIUtils#getBankInfo(String)} verwenden.
	 */
	@Deprecated
	public static String getBICForBLZ ( String blz )
	{
		BankInfo info = getBankInfo(blz);
		if (info == null)
		{
			return "";
		}
		return info.getBic() != null ? info.getBic() : "";
	}

	/**
	 * Berechnet die IBAN fuer ein angegebenes deutsches Konto.
	 *
	 * @param k
	 *            das Konto.
	 * @return die berechnete IBAN.
	 */
	public static String getIBANForKonto ( Konto k )
	{
		String konto = k.number;

		// Die Unterkonto-Nummer muss mit eingerechnet werden.
		// Aber nur, wenn sie numerisch ist. Bei irgendeiner Bank wurde
		// "EUR" als Unterkontonummer verwendet. Das geht natuerlich nicht,
		// weil damit nicht gerechnet werden kann
		// Wir machen das auch nur dann, wenn beide Nummern zusammen max.
		// 10 Zeichen ergeben
		if (k.subnumber != null && k.subnumber.length() > 0 && k.subnumber.matches("[0-9]{1,8}") && k.number.length() + k.subnumber.length() <= 10)
		{
			konto += k.subnumber;
		}

		/////////////////
		// Pruefziffer berechnen
		// Siehe http://www.iban.de/iban-pruefsumme.html
		String zeros = "0000000000";
		String filledKonto = zeros.substring(0, 10 - konto.length()) + konto; // 10-stellig
																				// mit
																				// Nullen
																				// fuellen
		StringBuffer sb = new StringBuffer();
		sb.append(k.blz);
		sb.append(filledKonto);
		sb.append("1314"); // hartcodiert fuer "DE
		sb.append("00"); // fest vorgegeben

		BigInteger mod = new BigInteger(sb.toString()).mod(new BigInteger("97")); // "97"
																					// ist
																					// fest
																					// vorgegeben
																					// in
																					// ISO
																					// 7064/Modulo
																					// 97-10
		String checksum = String.valueOf(98 - mod.intValue()); // "98" ist fest
																// vorgegeben in
																// ISO
																// 7064/Modulo
																// 97-10
		if (checksum.length() < 2)
		{
			checksum = "0" + checksum;
			//
			/////////////////
		}

		StringBuffer result = new StringBuffer();
		result.append("DE");
		result.append(checksum);
		result.append(k.blz);
		result.append(filledKonto);

		return result.toString();
	}

	/**
	 * Gibt zu einer gegebenen Bankleitzahl den HBCI-Host (für RDH und DDV)
	 * zurück.
	 *
	 * @param blz
	 *            Bankleitzahl der Bank
	 * @return HBCI-Host (DNS-Name oder IP-Adresse). Falls kein Host bekannt
	 *         ist, wird ein leerer String zurückgegeben.
	 * @deprecated Bitte {@link HBCIUtils#getBankInfo(String)} verwenden.
	 */
	@Deprecated
	public static String getHBCIHostForBLZ ( String blz )
	{
		BankInfo info = getBankInfo(blz);
		if (info == null)
		{
			return "";
		}
		return info.getRdhAddress() != null ? info.getRdhAddress() : "";
	}

	/**
	 * Gibt zu einer gegebenen Bankleitzahl die PIN/TAN-URL zurück.
	 *
	 * @param blz
	 *            Bankleitzahl der Bank
	 * @return PIN/TAN-URL. Falls keine URL bekannt ist, wird ein leerer String
	 *         zurückgegeben.
	 * @deprecated Bitte {@link HBCIUtils#getBankInfo(String)} verwenden.
	 */
	@Deprecated
	public static String getPinTanURLForBLZ ( String blz )
	{
		BankInfo info = getBankInfo(blz);
		if (info == null)
		{
			return "";
		}
		return info.getPinTanAddress() != null ? info.getPinTanAddress() : "";
	}

	/**
	 * Gibt zu einer gegebenen Bankleitzahl zurück, welche HBCI-Version für DDV
	 * bzw. RDH zu verwenden ist. Siehe auch
	 * {@link #getPinTanVersionForBLZ(String)}.
	 *
	 * @param blz
	 * @return HBCI-Version
	 * @deprecated Bitte {@link HBCIUtils#getBankInfo(String)} verwenden.
	 */
	@Deprecated
	public static String getHBCIVersionForBLZ ( String blz )
	{
		BankInfo info = getBankInfo(blz);
		if (info == null)
		{
			return "";
		}
		return info.getRdhVersion() != null ? info.getRdhVersion().getId() : "";
	}

	/**
	 * Gibt zu einer gegebenen Bankleitzahl zurück, welche HBCI-Version für
	 * HBCI-PIN/TAN bzw. RDH zu verwenden ist. Siehe auch
	 * {@link #getHBCIVersionForBLZ(String)}
	 *
	 * @param blz
	 * @return HBCI-Version
	 * @deprecated Bitte {@link HBCIUtils#getBankInfo(String)} verwenden.
	 */
	@Deprecated
	public static String getPinTanVersionForBLZ ( String blz )
	{
		BankInfo info = getBankInfo(blz);
		if (info == null)
		{
			return "";
		}
		return info.getPinTanVersion() != null ? info.getPinTanVersion().getId() : "";
	}

	/**
	 * Setzt den Wert eines HBCI-Parameters. Eine Beschreibung aller vom Kernel
	 * ausgewerteten Parameter befindet sich in der
	 * {@link org.kapott.hbci.manager.HBCIUtils Klassenbeschreibung} zur dieser
	 * Klasse. Für jede {@link java.lang.ThreadGroup} wird ein separater Satz
	 * von HBCI-Parametern verwaltet.
	 *
	 * @param key
	 *            Name des HBCI-Parameters.
	 * @param value
	 *            neuer Wert des zu setzenden HBCI-Parameters
	 */
	public static void setParam ( String key, String value )
	{
		ThreadGroup group = Thread.currentThread().getThreadGroup();
		Properties config = getParams();
		if (config == null)
		{
			throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_THREAD_NOTINIT", group.getName()));
		}

		synchronized (config)
		{
			if (value != null)
			{
				config.setProperty(key, value);
			}
			else
			{
				config.remove(key);
			}
		}
	}

	/**
	 * Ausgabe eines Log-Strings über den Log-Mechanismus des HBCI-Kernels.
	 *
	 * @param st
	 *            der auszugebende String
	 * @param level
	 *            die "Wichtigkeit" dieser Meldung. mögliche Werte:
	 *            <ul>
	 *            <li><code>LOG_ERR</code></li>
	 *            <li><code>LOG_WARN</code></li>
	 *            <li><code>LOG_INFO</code></li>
	 *            <li><code>LOG_DEBUG</code></li>
	 *            <li><code>LOG_CHIPCARD</code> (wird nur intern benutzt)</li>
	 *            </ul>
	 */
	public static synchronized void log ( String st, int level )
	{
		if (level <= Integer.parseInt(getParam("log.loglevel.default", "2")))
		{
			StackTraceElement trace = null;
			try
			{
				throw new Exception("");
			}
			catch (Exception e)
			{
				trace = e.getStackTrace()[1];
			}

			int filterLevel = Integer.parseInt(HBCIUtils.getParam("log.filter", "2"));
			if (filterLevel != 0)
			{
				st = LogFilter.getInstance().filterLine(st, filterLevel);
			}

			HBCIUtilsInternal.getCallback().log(st, level, new Date(), trace);
		}
	}

	/**
	 * Ausgabe der Meldungen einer Exception-Kette mit dem Level
	 * <code>LOG_ERR</code>.
	 *
	 * @param e
	 *            die Exception, deren <code>getMessage()</code>-Meldungen
	 *            geloggt werden sollen
	 */
	public static synchronized void log ( Exception e )
	{
		log(e, LOG_ERR);
	}

	/**
	 * Gibt den StackTrace einer Exception zurück.
	 *
	 * @param e
	 *            Exception
	 * @return kompletter StackTrace als String
	 */
	public static String exception2String ( Exception e )
	{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString().trim();
	}

	/**
	 * Extrahieren der root-Exception aus einer Exception-Chain.
	 *
	 * @param e
	 *            Exception
	 * @return String mit Infos zur root-Exception
	 */
	public static String exception2StringShort ( Exception e )
	{
		StringBuffer st = new StringBuffer();
		Throwable e2 = e;

		while (e2 != null)
		{
			String exClass = e2.getClass().getName();
			String msg = e2.getMessage();

			if (msg != null)
			{
				st.setLength(0);
				st.append(exClass);
				st.append(": ");
				st.append(msg);
			}
			e2 = e2.getCause();
		}

		return st.toString().trim();
	}

	/**
	 * Ausgabe der Meldungen einer Exception-Kette über den Log-Mechanismus des
	 * HBCI-Kernels. Es werden auch alle <code>getCause()</code>-Exceptions
	 * verfolgt und deren Meldung ausgegeben. Enthält keine der Exceptions
	 * dieser Kette eine Message, so wird statt dessen ein Stacktrace
	 * ausgegeben.
	 *
	 * @param e
	 *            die Exception, deren <code>getMessage()</code>-Meldungen
	 *            ausgegeben werden sollen.
	 * @param level
	 *            der Log-Level, mit dem die Meldungen geloggt werden sollen.
	 *            Siehe dazu auch {@link #log(String,int)}
	 */
	public static synchronized void log ( Exception e, int level )
	{
		log(exception2String(e), level);
	}

	/**
	 * Wandelt ein Byte-Array in eine entsprechende hex-Darstellung um.
	 *
	 * @param data
	 *            das Byte-Array, für das eine Hex-Darstellung erzeugt werden
	 *            soll
	 * @return einen String, der für jedes Byte aus <code>data</code> zwei
	 *         Zeichen (0-9,A-F) enthält.
	 */
	public static String data2hex ( byte[] data )
	{
		StringBuffer ret = new StringBuffer();

		for (int i = 0; i < data.length; i++)
		{
			String st = Integer.toHexString(data[i]);
			if (st.length() == 1)
			{
				st = '0' + st;
			}
			st = st.substring(st.length() - 2);
			ret.append(st).append(" ");
		}

		return ret.toString();
	}

	/**
	 * Wandelt ein gegebenes Datumsobjekt in einen String um. Das Format des
	 * erzeugten Strings ist abhängig vom gesetzten <em>HBCI4Java</em>-Locale
	 * (siehe Kernel-Parameter <code>kernel.locale.*</code>)
	 *
	 * @param date
	 *            ein Datum
	 * @return die lokalisierte Darstellung dieses Datums als String
	 */
	public static String date2StringLocal ( Date date )
	{
		String ret;

		try
		{
			ret = DateFormat.getDateInstance(DateFormat.SHORT, HBCIUtils.getLocale()).format(date);
		}
		catch (Exception e)
		{
			throw new InvalidArgumentException(date.toString());
		}

		return ret;
	}

	/**
	 * Wandelt einen String, der ein Datum in der lokalen Darstellung enthält
	 * (abhängig von der <em>HBCI4Java</em>-Locale, siehe Kernel-Parameter
	 * <code>kernel.locale.*</code>), in ein Datumsobjekt um
	 *
	 * @param date
	 *            ein Datum in der lokalen Stringdarstellung
	 * @return ein entsprechendes Datumsobjekt
	 */
	public static Date string2DateLocal ( String date )
	{
		Date ret;

		try
		{
			ret = DateFormat.getDateInstance(DateFormat.SHORT, HBCIUtils.getLocale()).parse(date);
		}
		catch (Exception e)
		{
			throw new InvalidArgumentException(date);
		}

		return ret;
	}

	/**
	 * Wandelt ein gegebenes Datums-Objekt in einen String um, der die Uhrzeit
	 * enthält. Das Format des erzeugten Strings ist abhängig von der gesetzten
	 * <em>HBCI4Java</em>-Locale (siehe Kernel-Parameter
	 * <code>kernel.locale.*</code>).
	 *
	 * @param date
	 *            ein Datumsobjekt
	 * @return die lokalisierte Darstellung der Uhrzeit als String
	 */
	public static String time2StringLocal ( Date date )
	{
		String ret;

		try
		{
			ret = DateFormat.getTimeInstance(DateFormat.SHORT, HBCIUtils.getLocale()).format(date);
		}
		catch (Exception e)
		{
			throw new InvalidArgumentException(date.toString());
		}

		return ret;
	}

	/**
	 * Wandelt einen String, der eine Uhrzeit in der lokalen Darstellung enthält
	 * (abhängig von der <em>HBCI4Java</em>-Locale, siehe Kernel-Parameter
	 * <code>kernel.locale.*</code>), in ein Datumsobjekt um
	 *
	 * @param date
	 *            eine Uhrzeit in der lokalen Stringdarstellung
	 * @return ein entsprechendes Datumsobjekt
	 */
	public static Date string2TimeLocal ( String date )
	{
		Date ret;

		try
		{
			ret = DateFormat.getTimeInstance(DateFormat.SHORT, HBCIUtils.getLocale()).parse(date);
		}
		catch (Exception e)
		{
			throw new InvalidArgumentException(date);
		}

		return ret;
	}

	/**
	 * Wandelt ein gegebenes Datums-Objekt in einen String um, der sowohl Datum
	 * als auch Uhrzeit enthält. Das Format des erzeugten Strings ist abhängig
	 * von der gesetzten <em>HBCI4Java</em>-Locale (siehe Kernel-Parameter
	 * <code>kernel.locale.*</code>).
	 *
	 * @param date
	 *            ein Datumsobjekt
	 * @return die lokalisierte Darstellung des Datums-Objektes
	 */
	public static String datetime2StringLocal ( Date date )
	{
		String ret;

		try
		{
			ret = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, HBCIUtils.getLocale()).format(date);
		}
		catch (Exception e)
		{
			throw new InvalidArgumentException(date.toString());
		}

		return ret;
	}

	/**
	 * Erzeugt ein Datums-Objekt aus Datum und Uhrzeit in der
	 * String-Darstellung. Die String-Darstellung von Datum und Uhrzeit müssen
	 * dabei der aktuellen <em>HBCI4Java</em>-Locale entsprechen (siehe
	 * Kernel-Parameter <code>kernel.locale.*</code>)).
	 *
	 * @param date
	 *            ein Datum in der lokalen Stringdarstellung
	 * @param time
	 *            eine Uhrzeit in der lokalen Stringdarstellung (darf
	 *            <code>null</code> sein)
	 * @return ein entsprechendes Datumsobjekt
	 */
	public static Date strings2DateTimeLocal ( String date, String time )
	{
		Date ret;

		try
		{
			if (time != null)
			{
				ret = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, HBCIUtils.getLocale()).parse(date + " " + time);
			}
			else
			{
				ret = DateFormat.getDateInstance(DateFormat.SHORT, HBCIUtils.getLocale()).parse(date);
			}
		}
		catch (Exception e)
		{
			throw new InvalidArgumentException(date + " / " + time);
		}

		return ret;
	}

	/** Erzeugt einen String im Format YYYY-MM-DD */
	public static String date2StringISO ( Date date )
	{
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	/**
	 * Wandelt einen String der Form YYYY-MM-DD in ein <code>Date</code>-Objekt
	 * um.
	 */
	public static Date string2DateISO ( String st )
	{
		try
		{
			return new SimpleDateFormat("yyyy-MM-dd").parse(st);
		}
		catch (ParseException e)
		{
			throw new InvalidArgumentException(st);
		}
	}

	/** Erzeugt einen String der Form HH:MM:SS */
	public static String time2StringISO ( Date date )
	{
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}

	/**
	 * Wandelt einen String der Form HH:MM:SS in ein <code>Date</code>-Objekt um
	 */
	public static Date string2TimeISO ( String st )
	{
		try
		{
			return new SimpleDateFormat("HH:mm:ss").parse(st);
		}
		catch (ParseException e)
		{
			throw new InvalidArgumentException(st);
		}
	}

	/** Erzeugt einen String im Format YYYY-MM-DD HH:MM:SS */
	public static String datetime2StringISO ( Date date )
	{
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	/**
	 * Erzeugt ein Datums-Objekt aus Datum und Uhrzeit in der
	 * String-Darstellung. Die String-Darstellung von Datum und Uhrzeit müssen
	 * dabei im ISO-Format vorlegen (Datum als yyyy-mm-dd, Zeit als hh:mm:ss).
	 * Der Parameter <code>time</code> darf auch <code>null</code> sein,
	 * <code>date</code> jedoch nicht.
	 *
	 * @param date
	 *            ein Datum in der ISO-Darstellung
	 * @param time
	 *            eine Uhrzeit in der ISO-Darstellung (darf auch
	 *            <code>null</code> sein)
	 * @return ein entsprechendes Datumsobjekt
	 */
	public static Date strings2DateTimeISO ( String date, String time )
	{
		if (date == null)
		{
			throw new InvalidArgumentException("*** date must not be null");
		}

		Date result;
		try
		{
			if (time != null)
			{
				result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " " + time);
			}
			else
			{
				result = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			}
		}
		catch (ParseException e)
		{
			throw new InvalidArgumentException(date + " / " + time);
		}

		return result;
	}

	private static void errDeprecated ( String method )
	{
		HBCIUtils.log("programming error: the method " + method + "() has been deprecated, is very dangerous and will be removed soon.", HBCIUtils.LOG_ERR);
		HBCIUtils.log("programming error: please check your application to replace calls to " + method + "() with calls to either " + method + "Local() or "
				+ method + "ISO()", HBCIUtils.LOG_ERR);
	}

	/**
	 * Wrapper für {@link #date2StringLocal(Date)}.
	 *
	 * @deprecated
	 */
	@Deprecated
	public static String date2String ( Date date )
	{
		errDeprecated("date2String");
		return date2StringLocal(date);
	}

	/**
	 * Wrapper für {@link #string2DateLocal(String)}
	 *
	 * @deprecated
	 */
	@Deprecated
	public static Date string2Date ( String st )
	{
		errDeprecated("string2Date");
		return string2DateLocal(st);
	}

	/**
	 * Wrapper für {@link #time2StringLocal(Date)}
	 *
	 * @deprecated
	 */
	@Deprecated
	public static String time2String ( Date date )
	{
		errDeprecated("time2String");
		return time2StringLocal(date);
	}

	/**
	 * Wrapper für {@link #string2TimeLocal(String)}
	 *
	 * @deprecated
	 */
	@Deprecated
	public static Date string2Time ( String st )
	{
		errDeprecated("string2Time");
		return string2TimeLocal(st);
	}

	/**
	 * Wrapper für {@link #datetime2StringLocal(Date)}
	 *
	 * @deprecated
	 */
	@Deprecated
	public static String datetime2String ( Date date )
	{
		errDeprecated("datetime2String");
		return datetime2StringLocal(date);
	}

	/**
	 * Wrapper für {@link #string2DateLocal(String)}
	 *
	 * @deprecated
	 */
	@Deprecated
	public static Date strings2DateTime ( String date, String time )
	{
		errDeprecated("strings2DateTime");
		return strings2DateTimeLocal(date, time);
	}

	/**
	 * Gibt Daten Base64-encoded zurück. Die zu kodierenden Daten müssen als
	 * Byte-Array übergeben werden, als Resultat erhält man einen String mit der
	 * entsprechenden Base64-Kodierung.
	 *
	 * @param x
	 *            zu kodierende Daten
	 * @return String mit Base64-kodierten Daten
	 */
	public static String encodeBase64 ( byte[] x )
	{
		try
		{
			int origSize = x.length;

			if ( ( origSize % 3 ) != 0)
			{
				byte[] temp = new byte[ ( ( origSize / 3 ) + 1 ) * 3];
				System.arraycopy(x, 0, temp, 0, origSize);
				x = temp;
			}

			StringBuffer ret = new StringBuffer();
			int readPos = 0;

			while (readPos < ( x.length << 3 ))
			{
				int modulus = readPos & 7;
				int value;

				if ( ( readPos >> 3 ) < origSize)
				{
					if (modulus <= 2)
					{
						// six bits in one byte
						value = ( x[readPos >> 3] >> ( 2 - modulus ) ) & 0x3F;
					}
					else
					{
						// six bits in two bytes
						value = ( ( x[readPos >> 3] << ( modulus - 2 ) ) & 0x3F )
								| ( ( x[ ( readPos >> 3 ) + 1] >> ( 10 - modulus ) ) & ( ( 1 << ( modulus - 2 ) ) - 1 ) );
					}

					ret.append(base64table[value]);
				}
				else
				{
					ret.append('=');

				}
				readPos += 6;
			}

			return ret.toString();
		}
		catch (Exception ex)
		{
			throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_UTIL_ENCB64"), ex);
		}
	}

	/**
	 * Dekodieren eines Base64-Datenstroms. Es wird zu einem gegebenen
	 * Base64-Datenstrom der dazugehörige "Klartext" zurückgegeben.
	 *
	 * @param st
	 *            Base64-kodierten Daten
	 * @return dekodierter Datenstrom als Byte-Array
	 */
	public static byte[] decodeBase64 ( String st )
	{
		try
		{
			byte[] source = st.getBytes(Comm.ENCODING);
			byte[] ret = new byte[st.length()];
			int retlen = 0;

			int needFromFirst = 6;
			int needFromSecond = 2;
			boolean abort = false;
			int byteCounter = 0;
			int[] values = new int[2];

			for (int readPos = 0; readPos < source.length; readPos++)
			{
				values[0] = 0;
				values[1] = 0;

				for (int step = 0; step < 2; step++)
				{
					int value = 0;

					while ( ( readPos + step ) < source.length)
					{
						value = source[readPos + step];

						if (value >= '0' && value <= '9' || value >= 'A' && value <= 'Z' || value >= 'a' && value <= 'z' || value == '+' || value == '/'
								|| value == '=')
						{
							break;
						}

						readPos++;
					}

					if (! ( value >= '0' && value <= '9' || value >= 'A' && value <= 'Z' || value >= 'a' && value <= 'z' || value == '+' || value == '/' ))
					{

						abort = true;
						break;
					}

					if ((char) value == '/')
					{
						value = 63;
					}
					else if ((char) value == '+')
					{
						value = 62;
					}
					else if ((char) value <= '9')
					{
						value = 52 + value - (byte) '0';
					}
					else if ((char) value <= 'Z')
					{
						value = value - (byte) 'A';
					}
					else
					{
						value = 26 + value - (byte) 'a';

					}
					if (step == 0)
					{
						values[0] = ( value << ( 8 - needFromFirst ) ) & 0xFF;
					}
					else
					{
						values[1] = ( value >> ( 6 - needFromSecond ) ) & 0xFF;
					}
				}

				if (abort)
				{
					break;
				}

				ret[retlen++] = (byte) ( values[0] | values[1] );

				if ( ( byteCounter & 3 ) == 2)
				{
					readPos++;
					byteCounter++;
					needFromFirst = 6;
					needFromSecond = 2;
				}
				else
				{
					needFromFirst = 6 - needFromSecond;
					needFromSecond = 8 - needFromFirst;
				}

				byteCounter++;
			}

			byte[] ret2 = new byte[retlen];
			System.arraycopy(ret, 0, ret2, 0, retlen);
			return ret2;
		}
		catch (Exception ex)
		{
			throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_UTIL_DECB64"), ex);
		}
	}

	private static Method getAccountCRCMethodByAlg ( String alg )
	{
		Class<AccountCRCAlgs> cl = null;
		Method method = null;

		try
		{
			cl = AccountCRCAlgs.class;
			method = cl.getMethod("alg_" + alg, new Class[] { int[].class, int[].class });
		}
		catch (Exception e)
		{
			log("CRC algorithm " + alg + " not yet implemented", LOG_WARN);
		}

		return method;
	}

	/**
	 * Ermittelt, ob die Kontonummern für eine bestimmte BLZ mit
	 * <em>HBCI4Java</em> überprüft werden können oder nicht. Je nach Bank
	 * werden unterschiedliche Prüf-Algorithmen verwendet. Es sind noch nicht
	 * alle Prüf-Algorithmen in <em>HBCI4Java</em> implementiert - für manche
	 * Banken existiert auch keine Information darüber, welche Prüf-Algorithmen
	 * diese verwenden.
	 * <p>
	 * Mit dieser Methode kann nun ermittelt werden, ob für eine bestimmte Bank
	 * eine Prüfung möglich ist oder nicht.
	 * </p>
	 *
	 * @param blz
	 *            Die BLZ der Bank
	 * @return <code>true</code>, wenn die Kontonummern für diese Bank mit
	 *         <em>HBCI4Java</em> validiert werden können, sonst
	 *         <code>false</code>
	 */
	public static boolean canCheckAccountCRC ( String blz )
	{
		BankInfo info = getBankInfo(blz);
		if (info == null)
		{
			return false;
		}

		String alg = info.getChecksumMethod();
		if (alg == null || alg.length() != 2)
		{
			return false;
		}

		Method method = getAccountCRCMethodByAlg(alg);
		return method != null;
	}

	/**
	 * <p>
	 * Überprüft, ob gegebene BLZ und Kontonummer zueinander passen. Bei diesem
	 * Test wird wird die in die Kontonummer "eingebaute" Prüziffer verifiziert.
	 * Anhand der BLZ wird ermittelt, welches Prüfzifferverfahren zur
	 * Überprüfung eingesetzt werden muss.
	 * </p>
	 * <p>
	 * Ein positives Ergebnis dieser Routine bedeutet <em>nicht</em>, dass das
	 * entsprechende Konto bei der Bank <em>existiert</em>, sondern nur, dass
	 * die Kontonummer bei der entsprechenden Bank prinzipiell gültig ist.
	 * </p>
	 *
	 * @param blz
	 *            die Bankleitzahl der Bank, bei der das Konto geführt wird
	 * @param number
	 *            die zu überprüfende Kontonummer
	 * @return <code>true</code> wenn die Kontonummer nicht verifiziert werden
	 *         kann (z.B. weil das jeweilige Prüfzifferverfahren noch nicht in
	 *         <em>HBCI4Java</em> implementiert ist) oder wenn die Prüfung
	 *         erfolgreich verläuft; <code>false</code> wird immer nur dann
	 *         zurückgegeben, wenn tatsächlich ein Prüfzifferverfahren zum
	 *         Überprüfen verwendet wurde und die Prüfung einen Fehler ergab
	 */
	public static boolean checkAccountCRC ( String blz, String number )
	{
		BankInfo info = getBankInfo(blz);
		String alg = info != null ? info.getChecksumMethod() : null;

		// Im Zweifel lassen wir die Bankverbindung lieber durch
		if (alg == null || alg.length() != 2)
		{
			HBCIUtils.log("no crc information about " + blz + " in database", HBCIUtils.LOG_WARN);
			return true;
		}

		HBCIUtils.log("crc-checking " + blz + "/" + number, HBCIUtils.LOG_DEBUG);
		return checkAccountCRCByAlg(alg, blz, number);
	}

	/**
	 * Used to convert a blz or an account number to an array of ints, one array
	 * element per digit.
	 */
	private static int[] string2Ints ( String st, int target_length )
	{
		int[] numbers = new int[target_length];
		int st_len = st.length();
		char ch;

		for (int i = 0; i < st_len; i++)
		{
			ch = st.charAt(i);
			numbers[target_length - st_len + i] = ch - '0';
		}

		return numbers;
	}

	/**
	 * Überprüfen einer Kontonummer mit einem gegebenen CRC-Algorithmus. Diese
	 * Methode wird intern von {@link HBCIUtils#checkAccountCRC(String,String)}
	 * aufgerufen und kann für Debugging-Zwecke auch direkt benutzt werden.
	 *
	 * @param alg
	 *            Nummer des zu verwendenden Prüfziffer-Algorithmus (siehe Datei
	 *            <code>blz.properties</code>).
	 * @param blz
	 *            zu überprüfende Bankleitzahl
	 * @param number
	 *            zu überprüfende Kontonummer
	 * @return <code>false</code>, wenn der Prüfzifferalgorithmus für die
	 *         angegebene Kontonummer einen Fehler meldet, sonst
	 *         <code>true</code> (siehe dazu auch
	 *         {@link #checkAccountCRC(String, String)})
	 */
	public static boolean checkAccountCRCByAlg ( String alg, String blz, String number )
	{
		boolean ret = true;

		if (blz == null || number == null)
		{
			throw new NullPointerException("blz and number must not be null");
		}

		if (number.length() <= 10)
		{
			Method method = getAccountCRCMethodByAlg(alg);

			if (method != null)
			{
				try
				{
					int[] blz_digits = string2Ints(blz, 8);
					int[] number_digits = string2Ints(number, 10);

					Object[] args = new Object[] { blz_digits, number_digits };
					ret = ( (Boolean) method.invoke(null, args) ).booleanValue();

					HBCIUtils.log("CRC check for " + blz + "/" + number + " with alg " + alg + ": " + ret, HBCIUtils.LOG_DEBUG);
				}
				catch (Exception e)
				{
					throw new HBCI_Exception(e);
				}
			}
		}
		else
		{
			HBCIUtils.log("can not check account numbers with more than 10 digits (" + number + ")- skipping CRC check", HBCIUtils.LOG_WARN);
		}

		return ret;
	}

	/**
	 * Use {@link #checkAccountCRCByAlg(String, String, String)} instead!
	 *
	 * @deprecated
	 */
	@Deprecated
	public static boolean checkAccountCRCByAlg ( String alg, String number )
	{
		return checkAccountCRCByAlg(alg, "", number);
	}

	/**
	 * Überprüfen der Gültigkeit einer IBAN. Diese Methode prüft anhand eines
	 * Prüfziffer-Algorithmus, ob die übergebene IBAN prinzipiell gültig ist.
	 *
	 * @return <code>false</code> wenn der Prüfzifferntest fehlschlägt, sonst
	 *         <code>true</code>
	 */
	public static boolean checkIBANCRC ( String iban )
	{
		return AccountCRCAlgs.checkIBAN(iban);
	}

	/**
	 * Überprüfen der Gültigkeit einer Gläubiger-ID. Diese Methode prüft anhand
	 * eines Prüfziffer-Algorithmus, ob die übergebene ID prinzipiell gültig
	 * ist.
	 *
	 * @param creditorId
	 *            die zu pruefende Creditor-ID.
	 * @return <code>false</code> wenn der Prüfzifferntest fehlschlägt, sonst
	 *         <code>true</code>
	 */
	public static boolean checkCredtitorIdCRC ( String creditorId )
	{
		return AccountCRCAlgs.checkCreditorId(creditorId);
	}

	private static void refreshBLZList ( ClassLoader cl ) throws IOException
	{
		String blzpath = HBCIUtils.getParam("kernel.kernel.blzpath");
		if (blzpath == null)
		{
			blzpath = "";
		}
		blzpath += "blz.properties";
		InputStream f = cl.getResourceAsStream(blzpath);

		if (f == null)
		{
			throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_BLZLOAD", blzpath));
		}

		refreshBLZList(f);
		f.close();
	}

	/**
	 * Aktivieren einer neuen Bankenliste. Diese Methode kann aufgerufen werden,
	 * um während der Laufzeit einer <em>HBCI4Java</em>-Anwendung eine neue
	 * Bankenliste zu aktivieren. Die Bankenliste wird aus dem übergebenen
	 * InputStream gelesen, welcher Daten im Format eines Java-Properties-Files
	 * liefern muss. Das konkrete Format der Property-Einträge der Bankenliste
	 * ist am Beispiel der bereits mitgelieferten Datei
	 * <code>blz.properties</code> ersichtlich.
	 *
	 * @param in
	 *            Eingabe-Stream, der für das Laden der Bankleitzahlen-Daten
	 *            verwendet werden soll
	 * @throws IOException
	 **/
	public static synchronized void refreshBLZList ( InputStream in ) throws IOException
	{
		HBCIUtils.log("trying to load BLZ data", HBCIUtils.LOG_DEBUG);
		InputStreamReader isr = new InputStreamReader(in, "UTF-8");
		HBCIUtilsInternal.blzs.clear();
		HBCIUtilsInternal.blzs.load(isr);

		HBCIUtilsInternal.banks.clear();
		for (Entry<Object, Object> e : HBCIUtilsInternal.blzs.entrySet())
		{
			String blz = (String) e.getKey();
			String value = (String) e.getValue();

			BankInfo info = BankInfo.parse(value);
			info.setBlz(blz);
			HBCIUtilsInternal.banks.put(blz, info);
		}
	}

	/**
	 * Konvertiert einen String in einen BigDecimal-Wert mit zwei
	 * Nachkommastellen.
	 *
	 * @param st
	 *            String, der konvertiert werden soll (Format
	 *            "<code>1234.56</code>");
	 * @return BigDecimal-Wert
	 */
	public static BigDecimal string2BigDecimal ( String st )
	{
		BigDecimal result = new BigDecimal(st);
		result.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return result;
	}

	/**
	 * Wandelt einen BigDecimal-Wert in einen String im Format
	 * "<code>1234.56</code>" um (also ohne Tausender-Trennzeichen und mit "."
	 * als Dezimaltrennzeichen).
	 *
	 * @param value
	 *            zu konvertierender BigDecimal-Wert
	 * @return String-Darstellung dieses Wertes
	 */
	public static String bigDecimal2String ( BigDecimal value )
	{
		DecimalFormat format = new DecimalFormat("0.00");
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		format.setDecimalFormatSymbols(symbols);
		format.setDecimalSeparatorAlwaysShown(true);
		return format.format(value);
	}

	/**
	 * Konvertiert einen String in einen double-Wert (entspricht
	 * <code>Double.parseDouble(st)</code>).
	 *
	 * @param st
	 *            String, der konvertiert werden soll (Format
	 *            "<code>1234.56</code>");
	 * @return double-Wert
	 * @deprecated use {@link #string2BigDecimal(String)}
	 */
	@Deprecated
	public static double string2Value ( String st )
	{
		return Double.parseDouble(st);
	}

	/**
	 * Wandelt einen Double-Wert in einen String im Format
	 * "<code>1234.56</code>" um (also ohne Tausender-Trennzeichen und mit "."
	 * als Dezimaltrennzeichen).
	 *
	 * @param value
	 *            zu konvertierender Double-Wert
	 * @return String-Darstellung dieses Wertes
	 * @deprecated use {@link #bigDecimal2String(BigDecimal)}
	 */
	@Deprecated
	public static String value2String ( double value )
	{
		DecimalFormat format = new DecimalFormat("0.00");
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		format.setDecimalFormatSymbols(symbols);
		format.setDecimalSeparatorAlwaysShown(true);
		return format.format(value);
	}

	/**
	 * Gibt die Versionsnummer der <em>HBCI4Java</em>-Bibliothek zurück.
	 *
	 * @return verwendete <em>HBCI4Java</em>-Version
	 */
	public static String version ( )
	{
		return VERSION;
	}

	/**
	 * Parsen eines MT940-Datenstroms (Kontoauszüge). Kontoauszüge können von
	 * vielen Software-Produkten im MT940-Format exportiert werden. Diese
	 * Methode nimmt einen solchen MT940-String entgegen, parst ihn und stellt
	 * ein {@link org.kapott.hbci.GV_Result.GVRKUms GVRKUms}-Objekt mit den
	 * geparsten Daten zur Verfügung.
	 *
	 * @param mt940
	 *            Der zu parsende MT940-String
	 * @return {@link org.kapott.hbci.GV_Result.GVRKUms GVRKUms}-Objekt für den
	 *         einfachen Zugriff auf die Umsatzinformationen.
	 */
	public static GVRKUms parseMT940 ( String mt940 )
	{
		GVRKUms result = new GVRKUms();
		result.appendMT940Data(Swift.decodeUmlauts(mt940));
		return result;
	}
}
