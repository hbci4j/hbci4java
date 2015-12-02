
/*  $Id: HBCIJob.java,v 1.1 2011/05/04 22:37:53 willuhn Exp $

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

package org.kapott.hbci.GV;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/** <p>Schnittstelle für alle Aufträge, die via HBCI ausgeführt werden sollen. Ein
 HBCIJob-Objekt wird nur innerhalb von <em>HBCI4Java</em> verwaltet. Durch Aufruf einer der Methoden
 {@link org.kapott.hbci.manager.HBCIHandler#newJob(String)} oder
 {@link org.kapott.hbci.manager.HBCIHandler#newLowlevelJob(String)} wird
 eine neue Instanz eines HBCIJobs erzeugt. Die konkrete Klasse dieser Instanz ist
 für den Anwendungsentwickler nicht von Bedeutung.</p>
 <p>Die Anwendung muss nur die für diesen Job benötigten Parameter setzen (mit 
 {@link #setParam(String,String)}). Falls dieser Job mehrere digitale
 Signaturen benötigt, können mit der Methode {@link #addSignaturePassport(HBCIPassport,String)} 
 weitere Passport-Objekte zu diesem Job hinzugefügt werden,
 die dann als Zweit-, Dritt-, ...-Signatur bei der Nachrichtenerzeugung verwendet
 werden. Anschließend kann der fertig spezifizierte Job zum aktuellen HBCI-Dialog 
 hinzugefügt werden 
 ({@link #addToQueue()}).</p>
 <p>Nach Ausführung des HBCI-Dialoges können die Rückgabedaten und Statusinformationen für diesen
 Job ermittelt werden. Dazu wird die Methoode {@link #getJobResult()} benötigt, welche
 eine Instanz einer {@link org.kapott.hbci.GV_Result.HBCIJobResult}-Klasse zurückgibt.
 Die konkrete Klasse, um die es sich bei diesem Result-Objekt handelt, ist vom Typ des ausgeführten
 Jobs abhängig (z.B. gibt es eine Klasse, die Ergebnisdaten für Kontoauszüge enthält, eine
 Klasse für Saldenabfragen usw.). Eine Beschreibung der einzelnen Klassen für Result-Objekte findet
 sich im Package <code>org.kapott.hbci.GV_Result</code>. Eine Beschreibung, welcher Job welche Klasse
 zurückgibt, befindet sich in der Package-Dokumentation zu diesem Package (<code>org.kapott.hbci.GV</code>).</p> */
public interface HBCIJob
{
    /** Gibt den internen Namen für diesen Job zurück.
     * @return Job-Name, wie er intern von <em>HBCI4Java</em> verwendet wird. */
    public String getName();
    
    /** Gibt die für diesen Job verwendete Segment-Versionsnummer zurück */
    public String getSegVersion();

    /** <p>Gibt zurück, wieviele Signaturen für diesen Job mindestens benötigt werden.
     *  Diese Information wird den BPD entnommen. In einigen Fällen gibt es
     *  in den UPD aktuellere Informationen zu einem bestimmten Geschäftsvorfall,
     *  zur Zeit werden die UPD von dieser Methode aber nicht ausgewertet.</p>
     *  <p>Wird für einen Job mehr als eine Signatur benötigt, so können mit der
     *  Methode {@link #addSignaturePassport(HBCIPassport, String)}
     *  Passports bestimmt werden, die für die Erzeugung der zusätzlichen
     *  Signaturen verwendet werden sollen.</p>
     *  <p>Es wird außerdem empfohlen, dass Aufträge, die mehrere Signaturen
     *  benötigen, jeweils in einer separaten HBCI-Nachricht versandt werden. Um das
     *  zu erzwingen, kann entweder ein HBCI-Dialog geführt werden, der definitiv
     *  nur diesen einen Auftrag enthält (also nur ein 
     *  {@link #addToQueue()}
     *  für diesen Dialog), oder es wird beim Zusammenstellen der Jobs für einen
     *  Dialog sichergestellt, dass ein bestimmter Job in einer separaten Nachricht
     *  gesandt wird 
     *  ({@link org.kapott.hbci.manager.HBCIHandler#newMsg()}).</p>
     *  @return Mindest-Anzahl der benötigten Signaturen für diesen Job */
    public int getMinSigs();

    /** <p>Gibt zurück, welche Sicherheitsklasse für diesen Job mindestens benötigt wird.
     *  Diese Information wird den BPD entnommen. Sicherheitsklassen sind erst
     *  ab FinTS-3.0 definiert. Falls keine Sicherheitsklassen unterstützt werden
     *  (weil eine geringere HBCI-Version als FinTS-3.0 verwendet wird), wird 
     *  <code>1</code> zurückgegeben. Die Sicherheitsklasse ist nur die 
     *  Sicherheitsmechanismen DDV und RDH relevant - bei Verwendung von PIN/TAN
     *  hat die Sicherheitsklasse keine Bedeutung. </p>
     *  <p>Folgende Sicherheitsklassen sind definiert:</p>
     *  <ul>
     *  <li><code>0</code>: kein Sicherheitsdienst erforderlich</li>
     *  <li><code>1</code>: Authentication - es wird eine Signatur mit dem
     *  Signaturschlüssel benötigt.</li>
     *  <li><code>2</code>: Authentication mit fortgeschrittener elektronischer
     *  Signatur unter Verwendung des Signaturschlüssels.</li>
     *  <li><code>3</code>: Non-Repudiation mit fortgeschrittener elektronischer
     *  Signatur und optionaler Zertifikatsprüfung unter Verwendung des DigiSig-Schlüssels</li>
     *  <li><code>4</code>: Non-Repudiation mit fortgeschrittener bzw. qualifizierter
     *  elektronischer Signatur und zwingender Zertifikatsüberprüfung mit dem
     *  DigiSig-Schlüssel</li>
     *  </ul>
     *  @return Sicherheitsklasse, die für diese Job benötigt wird */
    public int getSecurityClass();

    /** Gibt alle möglichen Job-Parameter für einen Lowlevel-Job zurück.
     * Die Anwendung dieser Methode ist nur sinnvoll, wenn es sich bei dem 
     * aktuellen Job um einen Lowlevel-Job handelt (erzeugt mit 
     * {@link org.kapott.hbci.manager.HBCIHandler#newLowlevelJob(String)}).
     * Die zurückgegebenen Parameternamen können als erstes Argument der
     * Methode {@link #setParam(String, String)} verwendet werden. 
     * @return Liste aller gültigen Parameternamen (nur für Lowlevel-Jobs) */
    public List getJobParameterNames();

    /** Gibt alle möglichen Property-Namen für die Lowlevel-Rückgabedaten dieses
     * Jobs zurück. Die Lowlevel-Rückgabedaten können mit
     * {@link #getJobResult()} und {@link HBCIJobResult#getResultData()}
     * ermittelt werden. Diese Methode verwendet intern
     * {@link org.kapott.hbci.manager.HBCIHandler#getLowlevelJobResultNames(String)}.
     * @return Liste aller prinzipiell möglichen Property-Keys für die 
     * Lowlevel-Rückgabedaten dieses Jobs */
    public List getJobResultNames();

    /** <p>Gibt für einen Job alle bekannten Einschränkungen zurück, die bei
     der Ausführung des jeweiligen Jobs zu beachten sind. Diese Daten werden aus den
     Bankparameterdaten des aktuellen Passports extrahiert. Sie können von einer HBCI-Anwendung
     benutzt werden, um gleich entsprechende Restriktionen bei der Eingabe von
     Geschäftsvorfalldaten zu erzwingen (z.B. die maximale Anzahl von Verwendungszweckzeilen,
     ob das Ändern von terminierten Überweisungen erlaubt ist usw.).</p>
     <p>Die einzelnen Einträge des zurückgegebenen Properties-Objektes enthalten als Key die
     Bezeichnung einer Restriktion (z.B. "<code>maxusage</code>"), als Value wird der
     entsprechende Wert eingestellt. Die Bedeutung der einzelnen Restriktionen ist zur Zeit
     nur der HBCI-Spezifikation zu entnehmen. In späteren Programmversionen werden entsprechende
     Dokumentationen zur internen HBCI-Beschreibung hinzugefügt, so dass dafür eine Abfrageschnittstelle
     implementiert werden kann.</p>
     <p>Diese Methode verwendet intern 
     {@link org.kapott.hbci.manager.HBCIHandler#getLowlevelJobRestrictions(String)}</p>.
     @return Properties-Objekt mit den einzelnen Restriktionen */
    public Properties getJobRestrictions();

    /** Gibt alle für diesen Job gesetzten Parameter zurück. In dem
     zurückgegebenen <code>Properties</code>-Objekt sind werden die
     Parameter als <em>Lowlevel</em>-Parameter abgelegt. Außerdem hat
     jeder Lowlevel-Parametername zusätzlich ein Prefix, welches den
     Lowlevel-Job angibt, für den der Parameter gilt (also z.B.
     <code>Ueb3.BTG.value</code>
     @return aktuelle gesetzte Lowlevel-Parameter für diesen Job */
    public Properties getLowlevelParams();

    /** Setzen eines komplexen Job-Parameters (Kontodaten). Einige Jobs benötigten Kontodaten
     als Parameter. Diese müssten auf "normalem" Wege durch mehrere Aufrufe von 
     {@link #setParam(String,String)} erzeugt werden (Länderkennung, Bankleitzahl, 
     Kontonummer, Unterkontomerkmal, Währung, IBAN, BIC).
     Durch Verwendung dieser Methode wird dieser Weg abgekürzt. Es wird ein Kontoobjekt 
     übergeben, für welches die entsprechenden <code>setParam(String,String)</code>-Aufrufe 
     automatisch erzeugt werden. 
     @param paramname die Basis der Parameter für die Kontodaten (für <code>my.country</code>,
     <code>my.blz</code>, <code>my.number</code>, <code>my.subnumber</code>, <code>my.bic</code>, 
     <code>my.iban</code>, <code>my.curr</code> wäre das also "<code>my</code>")
     @param acc ein Konto-Objekt, aus welchem die zu setzenden Parameterdaten entnommen werden */
    public void setParam(String paramname,Konto acc);

    /**
     * @see HBCIJob#setParam(String, Konto) - jedoch mit Index.
     * @param paramname
     * @param index
     * @param acc
     */
    public void setParam(String paramname,Integer index,Konto acc);

    /** Setzen eines komplexen Job-Parameters (Geldbetrag). Einige Jobs benötigten Geldbeträge
     als Parameter. Diese müssten auf "normalem" Wege durch zwei Aufrufe von 
     {@link #setParam(String,String)} erzeugt werden (je einer für
     den Wert und die Währung). Durch Verwendung dieser
     Methode wird dieser Weg abgekürzt. Es wird ein Value-Objekt übergeben, für welches
     die entsprechenden zwei <code>setParam(String,String)</code>-Aufrufe automatisch
     erzeugt werden.
     @param paramname die Basis der Parameter für die Geldbetragsdaten (für "<code>btg.value</code>" und
     "<code>btg.curr</code>" wäre das also "<code>btg</code>")
     @param v ein Value-Objekt, aus welchem die zu setzenden Parameterdaten entnommen werden */
    public void setParam(String paramname,Value v);

    /** Setzen eines Job-Parameters, bei dem ein Datums als Wert erwartet wird. Diese Methode
     dient als Wrapper für {@link #setParam(String,String)}, um das Datum in einen korrekt
     formatierten String umzuwandeln. Das "richtige" Datumsformat ist dabei abhängig vom
     aktuellen Locale.
     @param paramName Name des zu setzenden Job-Parameters
     @param date Datum, welches als Wert für den Job-Parameter benutzt werden soll */
    public void setParam(String paramName,Date date);

    public void setParam(String paramName, Integer index, Date date);

    /** Setzen eines Job-Parameters, bei dem ein Integer-Wert Da als Wert erwartet wird. Diese Methode
     dient nur als Wrapper für {@link #setParam(String,String)}.
     @param paramName Name des zu setzenden Job-Parameters
     @param i Integer-Wert, der als Wert gesetzt werden soll */
    public void setParam(String paramName,int i);

    /** <p>Setzen eines Job-Parameters. Für alle Highlevel-Jobs ist in der Package-Beschreibung zum
     Package <code>org.kapott.hbci.GV</code> eine Auflistung aller Jobs und deren Parameter zu finden.
     Für alle Lowlevel-Jobs kann eine Liste aller Parameter entweder mit dem Tool
     {@link org.kapott.hbci.tools.ShowLowlevelGVs} oder zur Laufzeit durch Aufruf
     der Methode {@link org.kapott.hbci.manager.HBCIHandler#getLowlevelJobParameterNames(String)} 
     ermittelt werden.</p>
     <p>Bei Verwendung dieser oder einer der anderen <code>setParam()</code>-Methoden werden zusätzlich
     einige der Job-Restriktionen (siehe {@link #getJobRestrictions()}) analysiert. Beim Verletzen einer
     der überprüften Einschränkungen wird eine Exception mit einer entsprechenden Meldung erzeugt.
     Diese Überprüfung findet allerdings nur bei Highlevel-Jobs statt.</p>
     @param paramName der Name des zu setzenden Parameters.
     @param value Wert, auf den der Parameter gesetzt werden soll */
    public void setParam(String paramName,String value);

    /** <p>Setzen eines Job-Parameters. Für alle Highlevel-Jobs ist in der Package-Beschreibung zum
     Package <code>org.kapott.hbci.GV</code> eine Auflistung aller Jobs und deren Parameter zu finden.
     Für alle Lowlevel-Jobs kann eine Liste aller Parameter entweder mit dem Tool
     {@link org.kapott.hbci.tools.ShowLowlevelGVs} oder zur Laufzeit durch Aufruf
     der Methode {@link org.kapott.hbci.manager.HBCIHandler#getLowlevelJobParameterNames(String)} 
     ermittelt werden.</p>
     <p>Bei Verwendung dieser oder einer der anderen <code>setParam()</code>-Methoden werden zusätzlich
     einige der Job-Restriktionen (siehe {@link #getJobRestrictions()}) analysiert. Beim Verletzen einer
     der überprüften Einschränkungen wird eine Exception mit einer entsprechenden Meldung erzeugt.
     Diese Überprüfung findet allerdings nur bei Highlevel-Jobs statt.</p>
     @param paramName der Name des zu setzenden Parameters.
     @param index der Index bei Index-Parametern, sonst <code>null</code>
     @param value Wert, auf den der Parameter gesetzt werden soll */
    public void setParam(String paramName,Integer index,String value);

    public void setParam(String paramname, Integer index, Value v);

    /** <p>Hinzufügen dieses Jobs zu einem HBCI-Dialog. Diese Methode arbeitet analog zu 
        {@link #addToQueue(String)}, nur dass hier
        die <code>customerid</code> mit der Kunden-ID vorbelegt ist, wie sie
        im aktuellen Passport gespeichert ist.</p> */
    public void addToQueue();
    
    /** <p>Hinzufügen dieses Jobs zu einem HBCI-Dialog. Nachdem alle
        Jobparameter mit 
        {@link #setParam(String,String)}
        gesetzt wurden, kann der komplett spezifizierte Job mit dieser Methode
        zur Auftragsliste eines Dialoges hinzugefügt werden.</p>
        <p>Die <code>customerId</code> gibt an, unter welcher Kunden-ID dieser Job
        ausgeführt werden soll. Existiert für eine HBCI-Nutzerkennung (ein Passport)
        nur genau eine Kunden-ID (wie das i.d.R. der Fall ist), so kann der
        <code>customerId</code>-Parameter weggelassen werden - <em>HBCI4Java</em>
        verwendet dann automatisch die richtige Kunden-ID (als Kunden-ID wird in diesem
        Fall der Wert von {@link HBCIPassport#getCustomerId()} verwendet). Gibt es aber mehrere
        gültige Kunden-IDs für einen HBCI-Zugang, so muss die Kunden-ID,
        die für diesen Job verwendet werden soll, mit angegeben werden.</p>
        <p>Jeder Auftrag (=Job) ist i.d.R. an ein bestimmtes Konto des Auftraggebers
        gebunden (Überweisung: das Belastungskonto; Saldenabfrage: das abzufragende
        Konto usw.). Als Kunden-ID für einen Auftrag muss <em>die</em> Kunden-ID
        angegeben werden, die für dieses Konto verfügungsberechtigt ist.</p>
        <p>I.d.R. liefert eine Bank Informationen über alle Konten, auf die
        via HBCI zugegriffen werden kann. Ist das der Fall, so kann die Menge
        dieser Konten mit {@link HBCIPassport#getAccounts()} ermittelt werden.
        In jedem zurückgemeldeten {@link org.kapott.hbci.structures.Konto}-Objekt 
        ist im Feld <code>customerid</code> vermerkt, welche Kunden-ID für 
        dieses Konto verfügungsberechtigt ist. Diese Kunden-ID müsste dann also 
        beim Hinzufügen eines Auftrages angegeben werden, welcher das jeweilige 
        Konto betrifft.</p>
        <p>Liefert eine Bank diese Informationen nicht, so hat die Anwendung selbst
        eine Kontenverwaltung zu implementieren, bei der jedem Nutzerkonto eine
        zu verwendende Kunden-ID zugeordnet ist.</p>
        <p>Ein HBCI-Dialog kann aus beliebig vielen HBCI-Nachrichten bestehen. <em>HBCI4Java</em> versucht zunächst,
        alle Jobs in einer einzigen Nachricht unterzubringen. Kann ein Job nicht mehr zur aktuellen
        Nachricht hinzugefügt werden (weil sonst bestimmte vorgegebene Bedingungen nicht eingehalten
        werden), so legt <em>HBCI4Java</em> automatisch eine neue Nachricht an, zu der der Job schließlich
        hinzugefügt wird. Beim Ausführen des HBCI-Dialoges (siehe {@link org.kapott.hbci.manager.HBCIHandler#execute()}) werden dann
        natürlich <em>alle</em> erzeugten Nachrichten zum HBCI-Server gesandt.</p> 
        <p>Der HBCI-Kernel bestimmt also automatisch, ob ein Auftrag noch mit in die aktuelle Nachricht
        aufgenommen werden kann, oder ob eine separate Nachricht erzeugt werden muss. Der manuelle
        Aufruf von {@link org.kapott.hbci.manager.HBCIHandler#newMsg() HBCIHandler.newMsg()} ist 
        deshalb im Prinzip niemals notwendig, es sei denn,
        es soll aus anderen Gründen eine neue Nachricht begonnen werden.</p>
        @param customerId die Kunden-ID, zu deren Dialog der Auftrag hinzugefügt werden soll */
    public void addToQueue(String customerId);

    /** Gibt ein Objekt mit den Rückgabedaten für diesen Job zurück. Das zurückgegebene Objekt enthält
     erst <em>nach</em> der Ausführung des Jobs gültige Daten.
     @return ein Objekt mit den Rückgabedaten und Statusinformationen zu diesem Job */
    public HBCIJobResult getJobResult();

    /** Hinzufügen eines Passports, welches für eine zusätzliche Signatur für
     *  diesen Auftrag benutzt wird. <code>role</code> gibt dabei die Rolle an,
     *  die der Eigentümer des zusätzlichen Passports in Bezug auf diesen
     *  Job (bzw. die aktuelle Nachricht) einnimmt. Gültige Werte sind in
     *  {@link org.kapott.hbci.passport.HBCIPassport} beschrieben.
     *  Mit der Methode {@link #getMinSigs()} kann ermittelt werden, wieviele
     *  Signaturen für einen Job mindestens benötigt werden. 
     *  @param passport das hinzuzufügende Passport-Objekt, welches für eine 
     *         zusätzliche Signatur benutzt werden soll
     *  @param role die Rolle, in der sich der Eigentümer des zusätzlichen 
     *         Passport-Objektes bezüglich dieses Jobs befindet */
    public void addSignaturePassport(HBCIPassport passport,String role);
    
    /**
     * Kann von der Banking-Anwendung genutzt werden, um einen eigenen Identifier im Job zu speichern, um im spaeteren
     * Verlauf des HBCI-Dialoges (z.Bsp. bei der TAN-Eingabe) einen Bezug zum urspruenglichen Auftrag wiederherstellen zu
     * koennen.
     * @param id optionale ID.
     */
    public void setExternalId(String id);

    /**
     * Liefert eine optionalen Identifier, der von der Banking-Anwendung genutzt werden kann, um einen Bezug zum urspruenglichen
     * Auftrag herstellen zu koennen.
     * @return der Identifier.
     */
    public String getExternalId();
    
}
