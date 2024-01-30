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

package org.kapott.hbci.manager;

import java.lang.reflect.Constructor;
import java.security.KeyPair;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kapott.hbci.GV.GVTemplate;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIDialogStatus;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.status.HBCIExecThreadedStatus;

/** <p>Ein Handle für genau einen HBCI-Zugang. Diese Klasse stellt das Verbindungsglied
    zwischen der Anwendung und dem HBCI-Kernel dar. Für jeden HBCI-Zugang, den
    die Anwendung benutzt, muss ein entsprechender HBCI-Handler angelegt werden.
    Darin sind folgende Daten zusammengefasst:</p>
    <ul>
      <li>Ein {@link org.kapott.hbci.passport.HBCIPassport}, welches die Nutzeridentifikationsdaten
          sowie die Zugangsdaten zum entsprechenden HBCI-Server enthält</li>
      <li>Die zu benutzende HBCI-Versionsnummer</li>
      <li>interne Daten zur Verwaltung der Dialoge bei der Kommunikation
          mit dem HBCI-Server</li>
    </ul>
    <p>Alle Anfragen der Anwendung an den HBCI-Kernel laufen über einen solchen
    Handler, womit gleichzeit eindeutig festgelegt ist, welche HBCI-Verbindung
    diese Anfrage betrifft.</p>
    <p>Die prinzipielle Benutzung eines Handlers sieht in etwa wiefolgt aus:
    <pre>
// ...
HBCIPassport passport=AbstractHBCIPassport.getInstance();
HBCIHandler handle=new HBCIHandler(passport.getHBCIVersion(),passport);

HBCIJob jobSaldo=handle.newJob("SaldoReq");       // nächster Auftrag ist Saldenabfrage
jobSaldo.setParam("number","1234567890");         // Kontonummer für Saldenabfrage
jobSaldo.addToQueue();

HBCIJob jobUeb=handle.newJob("Ueb");
jobUeb.setParam("src.number","1234567890");
jobUeb.setParam("dst.number","9876543210");
// ...
jobUeb.addToQueue();

// ...

HBCIExecStatus status=handle.execute();

// Auswerten von status
// Auswerten der einzelnen job-Ergebnisse

handle.close();
</pre> */
public final class HBCIHandler
	implements IHandlerData, AutoCloseable
{
    public final static int REFRESH_BPD=1;
    public final static int REFRESH_UPD=2;
    
    private HBCIKernelImpl       kernel;
    private HBCIPassportInternal passport;
    private Map<String, HBCIDialog>                  dialogs;
    
    /** Anlegen eines neuen HBCI-Handler-Objektes. Beim Anlegen wird
        überprüft, ob für die angegebene HBCI-Version eine entsprechende
        Spezifikation verfügbar ist. Außerdem wird das übergebene
        Passport überprüft. Dabei werden - falls nicht vorhanden - die BPD und die UPD
        vom Kreditinstitut geholt. Bei Passports, die asymmetrische Verschlüsselungsverfahren
        benutzen (RDH), wird zusätzlich überprüft, ob alle benötigten Schlüssel vorhanden
        sind. Gegebenenfalls werden diese aktualisiert.
        @param hbciversion zu benutzende HBCI-Version. gültige Werte sind:
               <ul>
                 <li><code>null</code> - es wird <em>die</em> HBCI-Version benutzt, die bei der
                     letzten Verwendung dieses Passports benutzt wurde</li>
                 <li>"<code>201</code>" für HBCI 2.01</li>
                 <li>"<code>210</code>" für HBCI 2.1</li>
                 <li>"<code>220</code>" für HBCI 2.2</li>
                 <li>"<code>plus</code>" für HBCI+</li>
                 <li>"<code>300</code>" für FinTS 3.0</li>
               </ul>
        @param passport das zu benutzende Passport. Dieses muss vorher mit
               {@link org.kapott.hbci.passport.AbstractHBCIPassport#getInstance()}
               erzeugt worden sein */
    public HBCIHandler(String hbciversion,HBCIPassport passport)
    {
        this(hbciversion,passport,false);
    }

    /** Anlegen eines neuen HBCI-Handler-Objektes. Beim Anlegen wird
        überprüft, ob für die angegebene HBCI-Version eine entsprechende
        Spezifikation verfügbar ist. Außerdem wird das übergebene
        Passport überprüft. Dabei werden - falls nicht vorhanden und falls
        @param lazyInit nicht auf true gesetzt ist - die BPD und die UPD
        vom Kreditinstitut geholt. Bei Passports, die asymmetrische 
        Verschlüsselungsverfahren benutzen (RDH), wird zusätzlich überprüft, 
        ob alle benötigten Schlüssel vorhanden sind. Gegebenenfalls werden 
        diese aktualisiert.
        @param hbciversion zu benutzende HBCI-Version. gültige Werte sind:
            <ul>
              <li><code>null</code> - es wird <em>die</em> HBCI-Version benutzt, die bei der
                  letzten Verwendung dieses Passports benutzt wurde</li>
              <li>"<code>201</code>" für HBCI 2.01</li>
              <li>"<code>210</code>" für HBCI 2.1</li>
              <li>"<code>220</code>" für HBCI 2.2</li>
              <li>"<code>plus</code>" für HBCI+</li>
              <li>"<code>300</code>" für FinTS 3.0</li>
            </ul>
        @param passport das zu benutzende Passport. Dieses muss vorher mit
               {@link org.kapott.hbci.passport.AbstractHBCIPassport#getInstance()}
               erzeugt worden sein
        @param lazyInit auf true setzen, um den UPD nachgelagert per
               {@link #initThreaded()} zu laden (zum Handling der eventuellen
               TAN-Abfrage) */
    public HBCIHandler(String hbciversion,HBCIPassport passport,boolean lazyInit)
    {
        try {
            if (passport==null)
                throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_NULL"));

            if (hbciversion==null) {
                hbciversion=passport.getHBCIVersion();
            }
            if (hbciversion.length()==0)
                throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_NO_HBCIVERSION"));

            this.kernel=new HBCIKernelImpl(this,hbciversion);

            this.passport=(HBCIPassportInternal)passport;
            this.passport.setParentHandlerData(this);

            if (!lazyInit) {
              
              // Das macht nur bei PIN/TAN Sinn
              if ((passport instanceof AbstractPinTanPassport) && Feature.INIT_FLIP_USER_INST.isEnabled())
              {
                registerUser();
                registerInstitute();
              }
              else
              {
                registerInstitute();
                registerUser();
              }
            }

            if (!passport.getHBCIVersion().equals(hbciversion)) {
                this.passport.setHBCIVersion(hbciversion);
                this.passport.saveChanges();
            }

            dialogs=new Hashtable<String, HBCIDialog>();
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANT_CREATE_HANDLE"),e);
        }
    }

    /** <p>Führt die Abfrage von BPD und UPD aus, die normalerweise in
        {@link #HBCIHandler(String, HBCIPassport)} bzw.
        {@link #HBCIHandler(String, HBCIPassport, boolean)} mit lazyInit=false
        durchgeführt wird, allerdings können Callbacks hier auch synchron
        behandelt werden. Bei einem Aufruf von <code>initThreaded()</code>
        wird der eigentliche HBCI-Dialog in einem separaten Thread geführt.
        Bei evtl. auftretenden Callbacks wird geprüft, ob diese synchron oder
        asynchron zu behandeln sind. Im asynchronen Fall wird der Callback wie
        gewohnt durch Aufruf der <code>callback()</code>-Methode des 
        registrierten "normalen" Callback-Objektes behandelt. Soll ein Callback
        synchron behandelt werden, terminiert diese Methode.</p>
        <p>Das zurückgegebene Status-Objekt zeigt an, ob diese Methode terminierte,
        weil ein synchron zu behandelnder Callback aufgetreten ist oder weil die
        Ausführung aller HBCI-Dialoge abgeschlossen ist.</p>
        <p>Mehr Informationen dazu in der Datei 
        <code>README.ThreadedCallbacks</code>.</p>*/
    public HBCIExecThreadedStatus initThreaded()
    {
        HBCIUtils.log("main thread: starting new threaded init",HBCIUtils.LOG_DEBUG);

        final ThreadSyncer sync_main=new ThreadSyncer("sync_main");
        passport.setPersistentData("thread_syncer_main",sync_main);

        new Thread() { public void run() {
            try {
                HBCIUtils.log("hbci thread: starting init()",HBCIUtils.LOG_DEBUG);

                if (Feature.INIT_FLIP_USER_INST.isEnabled())
                {
                  registerUser();
                  registerInstitute();
                }
                else
                {
                  registerInstitute();
                  registerUser();
                }
                
                sync_main.setData("execStatus",null);
            } catch (Exception e) {
                // im fehlerfall muss sicherheitshalber ein noch
                // im sync-objekt enthaltenes altes execStatus-objekt entfernt
                // werden
                sync_main.setData("execStatus",null);
            } finally {
                // die existenz von "thread_syncer" im passport entscheidet
                // in CallbackThreaded darüber, ob der threaded callback mechanimus
                // verwendet werden soll oder das standard-callback.
                // der threaded mechanismus wird allerdings *nur* für hbci.init() und
                // hbci.execute() verwendet, deshalb muss das thread_syncer-Objekt
                // wieder entfernt werden, wenn hbci.init() beendet ist.
                passport.setPersistentData("thread_syncer_main",null);

                // egal, wie der hbci-thread beendet wird (fehlerhaft oder nicht),
                // am ende muss auf jeden fall ein evtl. noch wartender main-thread
                // wieder aufgeweckt werden (das kann entweder executeThreaded()
                // oder continueThreaded() sein)
                HBCIUtils.log("hbci thread: awaking main thread with hbci result data",HBCIUtils.LOG_DEBUG);
                sync_main.setData("callbackData",null);
                sync_main.stopWaiting();

                HBCIUtils.log("hbci thread: thread finished",HBCIUtils.LOG_DEBUG);
            }
        }}.start();

        // für dieses wait() brauche ich kein timeout, weil der hbci-thread auf
        // jeden fall ein notify() macht, sobald er beendet wird oder sobald der
        // hbci-thread callback-daten braucht. die sichere beendigung des
        // hbci-threads wiederum wird dadurch abgesichert, dass die waits() aus
        // dem hbci-thread (warten auf callback-daten) mit timeouts versehen sind
        HBCIUtils.log("main thread: waiting for hbci result or callback data from hbci thread",HBCIUtils.LOG_DEBUG);
        sync_main.startWaiting(Integer.parseInt(HBCIUtils.getParam("kernel.threaded.maxwaittime","300")), "no response from hbci thread - timeout");

        HBCIExecThreadedStatus threadStatus=new HBCIExecThreadedStatus();
        threadStatus.setCallbackData((Hashtable<String, Object>)sync_main.getData("callbackData"));
        threadStatus.setExecStatus((HBCIExecStatus)sync_main.getData("execStatus"));

        HBCIUtils.log(
                "main thread: received answer from hbci thread, returning status "+
                        "(isCallback="+threadStatus.isCallback()+
                        ", isFinished="+threadStatus.isFinished()+")",
                HBCIUtils.LOG_DEBUG);

        return threadStatus;
    }


    /**
     * Macht die anonyme Initialisierung und ruft die BPD ab.
     */
    private void registerInstitute()
    {
        try {
            HBCIUtils.log("registering institute",HBCIUtils.LOG_DEBUG);
            HBCIInstitute inst=new HBCIInstitute(kernel,passport,false);
            inst.register();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANT_REG_INST"),ex);
        }
    }

    /**
     * Ruft die UPD ab.
     */
    private void registerUser()
    {
        try {
            HBCIUtils.log("registering user",HBCIUtils.LOG_DEBUG);
            HBCIUser user=new HBCIUser(kernel,passport,false);
            user.register();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CANT_REG_USER"),ex);
        }
    }

    /**
     * @see org.kapott.hbci.manager.IHandlerData#sync(boolean)
     */
    public void sync(boolean force)
    {
      HBCIInstitute inst = new HBCIInstitute(kernel,passport,false);
      inst.sync(force);
      
      HBCIUser user = new HBCIUser(kernel,passport,false);
      user.sync(force);
    }
    
    /** <p>Schließen des Handlers. Diese Methode sollte immer dann aufgerufen werden,
        wenn die entsprechende HBCI-Verbindung nicht mehr benötigt wird. </p><p>
        Beim Schließen des Handlers wird das Passport ebenfalls geschlossen.
        Sowohl das Passport-Objekt als auch das Handler-Objekt können anschließend
        nicht mehr benutzt werden.</p> */
    public void close()
    {
        if (passport!=null) {
            try {
                passport.close();
            } catch (Exception e) {
                HBCIUtils.log(e);
            }
        }
        
        passport=null;
        kernel=null;
        dialogs=null;
    }
    
    /* gibt die zu verwendende Customer-Id zurück. Wenn keine angegeben wurde
     * (customerId==null), dann wird die derzeitige passport-customerid 
     * verwendet */
    private String fixUnspecifiedCustomerId(String customerId)
    {
        if (customerId==null) {
            customerId=passport.getCustomerId();
            HBCIUtils.log("using default customerid "+customerId,HBCIUtils.LOG_DEBUG);
        }
        return customerId;
    }
    
    /* gibt ein Dialog-Objekt für eine bestimmte Kunden-ID zurück. Existiert für
     * die Kunden-ID noch kein Dialog-Objekt, so wird eines erzeugt */
    private HBCIDialog getDialogFor(String customerId)
    {
        HBCIDialog dialog=dialogs.get(customerId);
        if (dialog==null) {
            HBCIUtils.log("have to create new dialog for customerid "+customerId,HBCIUtils.LOG_DEBUG);
            dialog=new HBCIDialog(this);
            dialogs.put(customerId,dialog);
        }
        
        return dialog;
    }

    /** <p>Beginn einer neuen HBCI-Nachricht innerhalb eines Dialoges festlegen.
        Normalerweise muss diese Methode niemals manuell aufgerufen zu werden!</p>
        <p>Mit dieser Methode wird der HBCI-Kernel gezwungen, eine neue HBCI-Nachricht 
        anzulegen, in die alle nachfolgenden Geschäftsvorfälle aufgenommen werden. 
        Die <code>customerId</code> legt fest, für welchen Dialog die neue Nachricht
        erzeugt werden soll. Für eine genauere Beschreibung von Dialogen und
        <code>customerid</code>s siehe {@link org.kapott.hbci.GV.HBCIJob#addToQueue(String)}. </p>
        @param customerId die Kunden-ID, für deren Dialog eine neue Nachricht
        begonnen werden soll */
    public void newMsg(String customerId)
    {
        HBCIUtils.log("have to create new message for dialog for customer "+customerId,HBCIUtils.LOG_DEBUG);
        getDialogFor(fixUnspecifiedCustomerId(customerId)).newMsg();
    }
    
    /** Erzwingen einer neuen Nachricht im Dialog für die aktuelle Kunden-ID.
        Diese Methode arbeitet analog zu {@link #newMsg(String)}, nur dass hier
        die <code>customerid</code> mit der Kunden-ID vorbelegt ist, wie sie
        im aktuellen Passport gespeichert ist. Siehe dazu auch
        {@link org.kapott.hbci.GV.HBCIJob#addToQueue(String)}.*/
    public void newMsg()
    {
        newMsg(null);
    }
    
    /** <p>Erzeugen eines neuen Highlevel-HBCI-Jobs. Diese Methode gibt ein neues Job-Objekt zurück. Dieses
        Objekt wird allerdings noch <em>nicht</em> zum HBCI-Dialog hinzugefügt. Statt dessen
        müssen erst alle zur Beschreibung des jeweiligen Jobs benötigten Parameter mit
        {@link org.kapott.hbci.GV.HBCIJob#setParam(String,String)} gesetzt werden.
        Anschließend kann der Job mit {@link org.kapott.hbci.GV.HBCIJob#addToQueue(String)} zum
        HBCI-Dialog hinzugefügt werden.</p>
        <p>Eine Beschreibung aller unterstützten Geschäftsvorfälle befindet sich
        im Package <code>org.kapott.hbci.GV</code>.</p>
        @param jobname der Name des Jobs, der erzeugt werden soll. Gültige
               Job-Namen sowie die benötigten Parameter sind in der Beschreibung des Packages
               <code>org.kapott.hbci.GV</code> zu finden.
        @return ein Job-Objekt, für das die entsprechenden Job-Parameter gesetzt werden müssen und
                welches anschließend zum HBCI-Dialog hinzugefügt werden kann. */
    public HBCIJob newJob(String jobname)
    {
        HBCIUtils.log("creating new job "+jobname,HBCIUtils.LOG_DEBUG);
        
        if (jobname==null || jobname.length()==0)
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_JOBNAME"));
        
        HBCIJobImpl ret=null;
        String      className="org.kapott.hbci.GV.GV"+jobname;

        try {
            Class cl=Class.forName(className);
            Constructor cons=cl.getConstructor(new Class[] {HBCIHandler.class});
            ret=(HBCIJobImpl)cons.newInstance(new Object[] {this});
        } catch (ClassNotFoundException e) {
            throw new InvalidUserDataException("*** there is no highlevel job named "+jobname+" - need class "+className);
        } catch (Exception e) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_JOB_CREATE_ERR",jobname);
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCreateJobErrors",msg))
                throw new HBCI_Exception(msg,e);
        }
        
        return ret;
    }
    
    /** Erzeugt ein neues Lowlevel-Job-Objekt. Für eine Beschreibung des Unterschiedes
        zwischen High- und Lowlevel-Definition von Jobs siehe Package <code>org.kapott.hbci.GV</code>.
        @param gvname der Lowlevel-Name des zu erzeugenden Jobs
        @return ein neues Job-Objekt, für das erst alle benötigten Lowlevel-Parameter gesetzt
                werden müssen und das anschließend zum HBCI-Dialog hinzugefügt werden kann */
    public HBCIJob newLowlevelJob(String gvname)
    {
        HBCIUtils.log("generating new lowlevel-job "+gvname,HBCIUtils.LOG_DEBUG);

        if (gvname==null || gvname.length()==0)
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_JOBNAME"));

        HBCIJobImpl ret=new GVTemplate(gvname,this);
        return ret;
    }
    
    /** Do NOT use! Use {@link org.kapott.hbci.GV.HBCIJob#addToQueue(String)} instead */
    public void addJobToDialog(String customerId,HBCIJob job)
    {
        // TODO: nach dem neuen Objekt-Graph kennt der HBCIJob bereits "seinen"
        // HBCIHandler, so dass ein HBCIHandler.addJob(job) eigentlich
        // redundant ist und durch HBCIJob.addToQueue() ersetzt werden
        // könnte. Deswegen muss es hier einen Überprüfung geben, ob
        // (job.getHBCIHandler() === this) ist.
        
        customerId=fixUnspecifiedCustomerId(customerId);
        
        HBCIDialog dialog = null;
        try {
            dialog = getDialogFor(customerId);
            dialog.addTask((HBCIJobImpl)job);
        } finally {
            // wenn beim hinzufügen des jobs ein fehler auftrat, und wenn der
            // entsprechende dialog extra für diesen fehlerhaften job erzeugt
            // wurde, dann kann der (leere) dialog auch wieder aus der liste
            // auszuführender dialoge entfernt werden
            
            if (dialog!=null) {
                if (dialog.getMessageQueue().getTaskCount() == 0)
                {
                    HBCIUtils.log("removing empty dialog for customerid "+customerId+" from list of dialogs",HBCIUtils.LOG_DEBUG);
                    dialogs.remove(customerId);
                }
            }
        }
    }

    /** @deprecated use {@link org.kapott.hbci.GV.HBCIJob#addToQueue(String) HBCIJob.addToQueue(String)} instead */
    public void addJob(String customerId,HBCIJob job)
    {
        addJobToDialog(customerId,job);
    }
    
    /** @deprecated use {@link org.kapott.hbci.GV.HBCIJob#addToQueue() HBCIJob.addToQueue()} instead */
    public void addJob(HBCIJob job)
    {
        addJob(null,job);
    }

    /** Erzeugen eines leeren HBCI-Dialoges. <p>Im Normalfall werden HBCI-Dialoge
     * automatisch erzeugt, wenn Geschäftsvorfälle mit der Methode {@link org.kapott.hbci.GV.HBCIJob#addToQueue(String)}
     * zur Liste der auszuführenden Jobs hinzugefügt werden. <code>createEmptyDialog()</code>
     * kann explizit aufgerufen werden, wenn ein Dialog erzeugt werden soll,
     * der keine Geschäftsvorfälle enthält, also nur aus Dialog-Initialisierung
     * und Dialog-Ende besteht.</p>
     * <p>Ist die angegebene <code>customerId=null</code>, so wird der Dialog
     * für die aktuell im Passport gespeicherte Customer-ID erzeugt.</p>
     * 
     * @param customerId die Kunden-ID, für die der Dialog erzeugt werden soll.
     */
    public void createEmptyDialog(String customerId)
    {
        customerId=fixUnspecifiedCustomerId(customerId);
        HBCIUtils.log("creating empty dialog for customerid "+customerId,HBCIUtils.LOG_DEBUG);
        getDialogFor(customerId);
    }
    
    /** Entspricht {@link #createEmptyDialog(String) createEmptyDialog(null)} */
    public void createEmptyDialog()
    {
        createEmptyDialog(null);
    }
    
    /** <p>Ausführen aller bisher erzeugten Aufträge. Diese Methode veranlasst den HBCI-Kernel,
        die Aufträge, die durch die Aufrufe der Methode 
        {@link org.kapott.hbci.GV.HBCIJob#addToQueue(String)}
        zur Auftragsliste hinzugefügt wurden, auszuführen. </p>
        <p>Beim Hinzufügen der Aufträge zur Auftragsqueue (mit {@link org.kapott.hbci.GV.HBCIJob#addToQueue()}
        oder {@link org.kapott.hbci.GV.HBCIJob#addToQueue(String)}) wird implizit oder explizit
        eine Kunden-ID mit angegeben, unter der der jeweilige Auftrag ausgeführt werden soll.
        In den meisten Fällen hat ein Benutzer nur eine einzige Kunden-ID, so dass die
        Angabe entfallen kann, es wird dann automatisch die richtige verwendet. Werden aber
        mehrere Aufträge via <code>addToQueue()</code> zur Auftragsqueue hinzugefügt, und sind
        diese Aufträge unter teilweise unterschiedlichen Kunden-IDs auszuführen, dann wird
        für jede verwendete Kunden-ID ein separater HBCI-Dialog erzeugt und ausgeführt.
        Das äußert sich dann also darin, dass beim Aufrufen der Methode {@link #execute()}
        u.U. mehrere HBCI-Dialog mit der Bank geführt werden, und zwar je einer für jede Kunden-ID,
        für die wenigstens ein Auftrag existiert. Innerhalb eines HBCI-Dialoges werden alle
        auszuführenden Aufträge in möglichst wenige HBCI-Nachrichten verpackt.</p>
        <p>Dazu wird eine Reihe von HBCI-Nachrichten mit dem HBCI-Server der Bank ausgetauscht. Die
        Menge der dazu verwendeten HBCI-Nachrichten kann dabei nur bedingt beeinflusst werden, da <em>HBCI4Java</em> 
        u.U. selbstständig Nachrichten erzeugt, u.a. wenn ein Auftrag nicht mehr mit in eine Nachricht
        aufgenommen werden konnte, oder wenn eine Antwortnachricht nicht alle verfügbaren Daten
        zurückgegeben hat, so dass <em>HBCI4Java</em> mit einer oder mehreren weiteren Nachrichten den Rest
        der Daten abholt. </p>
        <p>Nach dem Nachrichtenaustausch wird ein Status-Objekt zurückgegeben,
        welches zur Auswertung aller ausgeführten Dialoge benutzt werden kann.</p>
        @return ein Status-Objekt, anhand dessen der Erfolg oder das Fehlschlagen
                der Dialoge festgestellt werden kann. */
    public HBCIExecStatus execute()
    {
        String origCustomerId=passport.getCustomerId();
        try {
            HBCIExecStatus ret=new HBCIExecStatus();
            
            while (!dialogs.isEmpty()) {
                String customerid=dialogs.keySet().iterator().next();
                HBCIUtils.log("executing dialog for customerid "+customerid,HBCIUtils.LOG_DEBUG);
                passport.setCustomerId(customerid);
                
                try {
                    HBCIDialog dialog=getDialogFor(customerid);
                    HBCIDialogStatus dialogStatus=dialog.doIt();
                    ret.addDialogStatus(customerid,dialogStatus);
                } catch (Exception e) {
                    ret.addException(customerid,e);
                } finally {
                    dialogs.remove(customerid);
                }
            }
            return ret;
        } finally {
            reset();
            passport.setCustomerId(origCustomerId);
            try {
                passport.closeComm();
            } catch (Exception e) {
                HBCIUtils.log("nested exception while closing passport: ", HBCIUtils.LOG_ERR);
                HBCIUtils.log(e);
            }
        }
    }
    
    /** <p>Entspricht {@link #execute()}, allerdings können Callbacks hier auch synchron
     * behandelt werden. Bei einem Aufruf von <code>executeThreaded()</code>
     * anstelle von <code>execute()</code> wird der eigentliche HBCI-Dialog in einem
     * separaten Thread geführt. Bei evtl. auftretenden Callbacks wird geprüft,
     * ob diese synchron oder asynchron zu behandeln sind. Im asynchronen Fall
     * wird der Callback wie gewohnt durch Aufruf der <code>callback()</code>-Methode
     * des registrierten "normalen" Callback-Objektes behandelt. Soll ein Callback
     * synchron behandelt werden, terminiert diese Methode.</p>
     * <p>Das zurückgegebene Status-Objekt zeigt an, ob diese Methode terminierte,
     * weil ein synchron zu behandelnder Callback aufgetreten ist oder weil die
     * Ausführung aller HBCI-Dialoge abgeschlossen ist.</p>
     * <p>Mehr Informationen dazu in der Datei <code>README.ThreadedCallbacks</code>.</p>*/
    public HBCIExecThreadedStatus executeThreaded()
    {
        HBCIUtils.log("main thread: starting new threaded execute",HBCIUtils.LOG_DEBUG);
        
        final ThreadSyncer sync_main=new ThreadSyncer("sync_main");
        passport.setPersistentData("thread_syncer_main",sync_main);
        
        new Thread() { public void run() {
            try {
                HBCIUtils.log("hbci thread: starting execute()",HBCIUtils.LOG_DEBUG);
                
                HBCIExecStatus execStatus=execute();
                sync_main.setData("execStatus",execStatus);
            } catch (Exception e) {
                // im fehlerfall (der eigentlich nie auftreten sollte, weil execute()
                // selbst alle exceptions catcht) muss sicherheitshalber ein noch
                // im sync-objekt enthaltenes altes execStatus-objekt entfernt
                // werden
                sync_main.setData("execStatus",null);
            } finally {
                // die existenz von "thread_syncer" im passport entscheidet
                // in CallbackThreaded darüber, ob der threaded callback mechanimus
                // verwendet werden soll oder das standard-callback.
                // der threaded mechanismus wird allerdings *nur* für hbci.execute()
                // verwendet, deshalb muss das thread_syncer-Objekt wieder entfernt
                // werden, wenn hbci.execute() beendet ist.
                passport.setPersistentData("thread_syncer_main",null);
                
                // egal, wie der hbci-thread beendet wird (fehlerhaft oder nicht),
                // am ende muss auf jeden fall ein evtl. noch wartender main-thread
                // wieder aufgeweckt werden (das kann entweder executeThreaded()
                // oder continueThreaded() sein)
                HBCIUtils.log("hbci thread: awaking main thread with hbci result data",HBCIUtils.LOG_DEBUG);
                sync_main.setData("callbackData",null);
                sync_main.stopWaiting();
                
                HBCIUtils.log("hbci thread: thread finished",HBCIUtils.LOG_DEBUG);
            }
        }}.start();
        
        // für dieses wait() brauche ich kein timeout, weil der hbci-thread auf
        // jeden fall ein notify() macht, sobald er beendet wird oder sobald der
        // hbci-thread callback-daten braucht. die sichere beendigung des 
        // hbci-threads wiederum wird dadurch abgesichert, dass die waits() aus
        // dem hbci-thread (warten auf callback-daten) mit timeouts versehen sind
        HBCIUtils.log("main thread: waiting for hbci result or callback data from hbci thread",HBCIUtils.LOG_DEBUG);
        sync_main.startWaiting(Integer.parseInt(HBCIUtils.getParam("kernel.threaded.maxwaittime","300")), "no response from hbci thread - timeout");
        
        HBCIExecThreadedStatus threadStatus=new HBCIExecThreadedStatus();
        threadStatus.setCallbackData((Hashtable<String, Object>)sync_main.getData("callbackData"));
        threadStatus.setExecStatus((HBCIExecStatus)sync_main.getData("execStatus"));
        
        HBCIUtils.log(
            "main thread: received answer from hbci thread, returning status "+
            "(isCallback="+threadStatus.isCallback()+
            ", isFinished="+threadStatus.isFinished()+")",
            HBCIUtils.LOG_DEBUG);

        return threadStatus;
    }
    
    /** <p>Setzt bei Verwendung des threaded-callback-Mechanismus einen noch 
     * aktiven HBCI-Dialog fort. Trat bei der Ausführung eines HBCI-Dialoges
     * via {@link #executeThreaded()} ein synchroner Callback auf, so dass
     * <code>executeThreaded()</code> terminierte und der Rückgabewert anzeigte,
     * dass Callback-Daten benötigt werden 
     * ({@link HBCIExecThreadedStatus#isCallback()}<code>==true</code>), dann
     * müssen die benötigten Callback-Daten mit 
     * <code>continueThreaded(String)</code> an den HBCI-Kernel übergeben 
     * werden.</p>
     * <p>Das führt dazu, dass der HBCI-Kernel die übergebenen Callback-Daten
     * an den wartenden HBCI-Thread übergibt (der immer noch mit der Ausführung
     * des HBCI-Dialoges beschäftigt ist und auf Daten von der Anwendung 
     * wartet).</p>
     * <p>Der Rückgabewert von <code>continueThreaded(String)</code> ist wieder
     * ein <code>HBCIExecThreadedStatus</code>-Objekt (analog zu
     * <code>executeThreaded()</code>), welches anzeigt, ob weitere Callback-
     * Daten benötigt werden oder ob der HBCI-Dialog nun beendet ist. Falls
     * weitere Callback-Daten benötigt werden, sind diese wiederum via
     * <code>continueThreaded(String)</code> an den HBCI-Kernel zu übergeben,
     * und zwar so lange, bis der HBCI-Dialog tatsächlich beendet ist.</p>
     * <p>Mehr Informationen zu threaded callbacks in der Datei
     * <code>README.ThreadedCallbacks</code>. */
    public HBCIExecThreadedStatus continueThreaded(String retData)
    {
        HBCIUtils.log("main thread: continuing hbci dialog with callback retData",HBCIUtils.LOG_DEBUG);
        
        // diese sync-objekte gibt es immer (bei richtiger verwendung des API),
        // weil continueThreaded() nur nach einem initialen executeThreaded()
        // ausgeführt werden darf und auch nur dann, wenn bei beiden methoden
        // noch kein endgültiges hbci-exec-status zurückgegeben wurde

        // damit wird das wait() im threaded callback wieder aufgeweckt
        ThreadSyncer sync_hbci=(ThreadSyncer)passport.getPersistentData("thread_syncer_hbci");
        sync_hbci.setData("retData",retData);
        
        HBCIUtils.log("main thread: awaking hbci thread with callback data from application",HBCIUtils.LOG_DEBUG);
        sync_hbci.stopWaiting();
        
        // für dieses wait() brauche ich kein timeout, weil der hbci-thread auf
        // jeden fall ein notify() macht, sobald er beendet wird oder sobald der
        // hbci-thread callback-daten braucht. die sichere beendigung des 
        // hbci-threads wiederum wird dadurch abgesichert, dass die waits() aus
        // dem hbci-thread (warten auf callback-daten) mit timeouts versehen sind
        ThreadSyncer sync_main=(ThreadSyncer)passport.getPersistentData("thread_syncer_main");
        HBCIUtils.log("main thread: waiting for hbci result or new callback data from hbci thread",HBCIUtils.LOG_DEBUG);
        sync_main.startWaiting(Integer.parseInt(HBCIUtils.getParam("kernel.threaded.maxwaittime","300")), "no response from hbci thread - timeout");
        
        HBCIExecThreadedStatus threadStatus=new HBCIExecThreadedStatus();
        threadStatus.setCallbackData((Hashtable<String, Object>)sync_main.getData("callbackData"));
        threadStatus.setExecStatus((HBCIExecStatus)sync_main.getData("execStatus"));
        
        HBCIUtils.log(
            "main thread: received answer from hbci thread, returning status "+
            "(isCallback="+threadStatus.isCallback()+
            ", isFinished="+threadStatus.isFinished()+")",
            HBCIUtils.LOG_DEBUG);

        return threadStatus;
    }
    
    /** <p>Sperren der Nutzerschlüssel. Das ist nur dann sinnvoll, wenn zwei Bedinungen erfüllt sind:</p>
        <ol>
          <li>Das verwendete Passport erlaubt die Sperrung der Schlüssel des Nutzers (nur RDH)</li>
          <li>Im verwendeten Passport sind auch tatsächlich bereits Nutzerschlüssel hinterlegt.</li>
        </ol>
        <p>Ist mindestens eine der beiden Bedingungen nicht erfüllt, so wird diese Methode mit einer
        Exception abgebrochen.</p>
        <p>Nach dem erfolgreichen Aufruf dieser Methode muss dieses HBCIHandler-Objekt mit
        {@link #close()} geschlossen werden. Anschließend muss mit dem gleichen Passport-Objekt
        ein neues HBCIHandler-Objekt angelegt werden, damit das Passport neu initialisiert wird. Bei
        dieser Neu-Initialisierung werden neue Nutzerschlüssel für das Passport generiert.
<pre>
// ...
hbciHandle.lockKeys();
hbciHandle.close();

hbciHandle=new HBCIHandle(hbciversion,passport);
// ...
</pre>
        Um die Nutzerschlüssel eines Passport nur zu <em>ändern</em>, kann die Methode
        {@link #setKeys(java.security.KeyPair,java.security.KeyPair)} 
        oder {@link #newKeys()} aufgerufen werden.</p>
        <p>Ab Version 2.4.0 von <em>HBCI4Java</em> muss der HBCIHandler nach dem
        Schlüsselsperren nicht mehr geschlossen werden. Statt dessen können direkt
        nach der Schlüsselsperrung neue Schlüssel erzeugt oder manuell gesetzt
        werden (mit den Methoden {@link #setKeys(java.security.KeyPair,java.security.KeyPair)}
        bzw. {@link #newKeys()}. </p>
        <p>In jedem Fall muss für die neuen Schlüssel, die nach einer Schlüsselsperrung
        erzeugt werden, ein neuer INI-Brief generiert und an die Bank versandt werden.</p>*/
    public void lockKeys()
    {
        // TODO: die methode hat hier eigentlich nichts zu suchen
        try {
            new HBCIUser(kernel,passport,false).lockKeys();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),ex);
        }
    }
    
    /** <p>Erzeugen neuer kryptografischer Schlüssel für den Nutzer.
        Mit dieser Methode wird für den Nutzer sowohl ein neues Signier- als auch ein
        neues Chiffrierschlüsselpaar erzeugt. Die neuen Schlüsseldaten werden anschließend
        automatisch an die Bank übermittelt. Sofern diese Aktion erfolgreich verläuft,
        werden die neuen Schlüssel in der Passport-Datei (Schlüsseldatei) gespeichert.</p>
        <p><b>ACHTUNG!</b> Vor dieser Aktion sollte unbedingt ein Backup der aktuellen Schlüsseldatei
        durchgeführt werden. Bei ungünstigen Konstellationen von Fehlermeldungen seitens
        des Kreditinstitutes kann es nämlich passieren, dass die neuen Schlüssel trotz
        eingegangener Fehlermeldung gespeichert werden, dann wären aber die alten (noch gültigen)
        Schlüssel überschrieben.</p>
        <p><b>ACHTUNG!</b> In noch ungünstigeren Fällen kann es auch vorkommen, dass neue Schlüssel
        generiert und erfolgreich an die Bank übermittelt werden, die neuen Schlüssel aber nicht
        in der Schlüsseldatei gespeichert werden. Das ist insofern der ungünstigste Fall, da
        die Bank dann schon die neuen Schlüssel kennt, in der Passport-Datei aber noch die
        alten Schlüssel enthalten sind und die soeben generierten neuen Schlüssel "aus Versehen"
        weggeworfen wurden.</p> */
    public void newKeys()
    {
        // TODO: diese methode verschieben
        try {
            new HBCIUser(kernel,passport,false).generateNewKeys();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_GENKEYS_ERR"),ex);
        }
    }
    
    /** <p>Setzen der Nutzerschlüssel auf vorgegebene Daten.
        Mit dieser Methode wird für den Nutzer sowohl ein neues Signier- als auch ein
        neues Chiffrierschlüsselpaar gesetzt. Die neuen Schlüsseldaten werden anschließend
        automatisch an die Bank übermittelt. Sofern diese Aktion erfolgreich verläuft,
        werden die neuen Schlüssel in der Passport-Datei (Schlüsseldatei) gespeichert.</p>
        <p><b>ACHTUNG!</b> Vor dieser Aktion sollte unbedingt ein Backup der aktuellen Schlüsseldatei
        durchgeführt werden. Bei ungünstigen Konstellationen von Fehlermeldungen seitens
        des Kreditinstitutes kann es nämlich passieren, dass die neuen Schlüssel trotz
        eingegangener Fehlermeldung gespeichert werden, dann wären aber die alten (noch gültigen)
        Schlüssel überschrieben.</p> */
    // TODO: hier digisig keys mit unterstützen
    public void setKeys(KeyPair sigKey,KeyPair encKey)
    {
        // TODO: diese methode verschieben
        try {
            new HBCIUser(kernel,passport,false).manuallySetNewKeys(sigKey,encKey);
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SETKEYS_ERR"),ex);
        }
    }
    
    /** Key-Management: Überprüfen einer TAN (nur für PinTan-Passports!). Durch 
     * den Aufruf dieser Methode wird ein "leerer" HBCI-Dialog (also ein 
     * HBCI-Dialog, der nur aus Dialog-Initialisierung und Dialog-Ende besteht) 
     * gestartet. Im Verlauf dieses Dialoges wird über den Callback-Mechanismus 
     * nach einer TAN gefragt. Diese TAN wird serverseitig auf Gültigkeit 
     * überprüft, die Status-Information im Rückgabewert dieser Methode 
     * enthalten entsprechende Infos über das Ergebnis dieser Überprüfung.
     * @param customerId Kunden-ID, für die der Dialog ausgeführt werden soll
     *                   (<code>null</code> für aktuelle Kunden-ID)
     * @return ein Status-Objekt, anhand dessen der Erfolg oder das Fehlschlagen
     *         der TAN-Überprüfung festgestellt werden kann. */
    public HBCIExecStatus verifyTAN(String customerId)
    {
        // TODO diese methode ist eine key-management-methode, muss also später
        // ins passport-objekt verschoben werden
        reset();
        createEmptyDialog(customerId);
        ((AbstractPinTanPassport)passport).activateTANVerifyMode();
        return execute();
    }
    
    /** Entspricht {@link #verifyTAN(String) verifyTAN(null)}. */
    public HBCIExecStatus verifyTAN()
    {
        return verifyTAN(null);
    }

    /** Zurücksetzen des Handlers auf den Ausgangszustand. Diese Methode kann
     aufgerufen werden, wenn alle bisher hinzugefügten Nachrichten und
     Aufträge wieder entfernt werden sollen. Nach dem Ausführen eines
     Dialoges mit {@link #execute()} wird diese Methode 
     automatisch aufgerufen. */
    public void reset()
    {
        dialogs.clear();
    }
    
    /** Gibt das Passport zurück, welches in diesem Handle benutzt wird.
        @return Passport-Objekt, mit dem dieses Handle erzeugt wurde */
    public HBCIPassport getPassport()
    {
        return passport;
    }
    
    /** Gibt das HBCI-Kernel-Objekt zurück, welches von diesem HBCI-Handler
     * benutzt wird. Das HBCI-Kernel-Objekt kann u.a. benutzt werden, um
     * alle für die aktuellen HBCI-Version (siehe {@link #getHBCIVersion()}) 
     * implementierten Geschäftsvorfälle abzufragen. 
     * @return HBCI-Kernel-Objekt, mit dem der HBCI-Handler arbeitet */
    public HBCIKernel getKernel()
    {
        return kernel;
    }
    
    public MsgGen getMsgGen()
    {
        return kernel.getMsgGen();
    }
    
    /** Gibt die HBCI-Versionsnummer zurück, für die der aktuelle HBCIHandler
     * konfiguriert ist.
     * @return HBCI-Versionsnummer, mit welcher dieses Handler-Objekt arbeitet */
    public String getHBCIVersion()
    {
        return kernel.getHBCIVersion();
    }
    
    /** <p>Gibt die Namen aller vom aktuellen HBCI-Zugang (d.h. Passport) 
     * unterstützten Lowlevel-Jobs zurück. Alle hier zurückgegebenen Job-Namen 
     * können als Argument beim Aufruf der Methode 
     * {@link #newLowlevelJob(String)} benutzt werden.</p>
     * <p>In dem zurückgegebenen Properties-Objekt enthält jeder Eintrag als
     * Key den Lowlevel-Job-Namen; als Value wird die Versionsnummer des
     * jeweiligen Geschäftsvorfalls angegeben, die von <em>HBCI4Java</em> mit dem
     * aktuellen Passport und der aktuell eingestellten HBCI-Version
     * benutzt werden wird.</p>
     * <p><em>(Prinzipiell unterstützt <em>HBCI4Java</em> für jeden
     * Geschäftsvorfall mehrere GV-Versionen. Auch eine Bank bietet i.d.R. für
     * jeden GV mehrere Versionen an. Wird mit <em>HBCI4Java</em> ein HBCI-Job
     * erzeugt, so verwendet <em>HBCI4Java</em> immer automatisch die höchste
     * von der Bank unterstützte GV-Versionsnummer. Diese Information ist
     * für den Anwendungsentwickler kaum von Bedeutung und dient hauptsächlich 
     * zu Debugging-Zwecken.)</em></p>
     * <p>Zum Unterschied zwischen High- und Lowlevel-Jobs siehe die
     * Beschreibung im Package <code>org.kapott.hbci.GV</code>.</p>
     * @return Sammlung aller vom aktuellen Passport unterstützten HBCI-
     * Geschäftsvorfallnamen (Lowlevel) mit der jeweils von <em>HBCI4Java</em>
     * verwendeten GV-Versionsnummer.*/
    public Properties getSupportedLowlevelJobs()
    {
        Hashtable<String, List<String>>  allValidJobNames=kernel.getAllLowlevelJobs();
        Properties paramSegments=passport.getParamSegmentNames();
        Properties result=new Properties();
        
        for (Enumeration e=paramSegments.propertyNames();e.hasMoreElements();) {
            String segName=(String)e.nextElement();
            
            // überprüfen, ob parameter-segment tatsächlich zu einem GV gehört
            // gilt z.b. für "PinTan" nicht
            if (allValidJobNames.containsKey(segName))
                result.put(segName,paramSegments.getProperty(segName));
        }
        
        return result;
    }
    
    /** <p>Gibt alle Parameter zurück, die für einen Lowlevel-Job gesetzt
        werden können. Wird ein Job mit {@link #newLowlevelJob(String)}
        erzeugt, so kann der gleiche <code>gvname</code> als Argument dieser
        Methode verwendet werden, um eine Liste aller Parameter zu erhalten, die
        für diesen Job durch Aufrufe der Methode 
        {@link org.kapott.hbci.GV.HBCIJob#setParam(String,String)}
        gesetzt werden können bzw. müssen.</p>
        <p>Aus der zurückgegebenen Liste ist nicht ersichtlich, ob ein bestimmter
        Parameter optional ist oder gesetzt werden <em>muss</em>. Das kann aber
        durch Benutzen des Tools {@link org.kapott.hbci.tools.ShowLowlevelGVs}
        ermittelt werden.</p>
        <p>Jeder Eintrag der zurückgegebenen Liste enthält einen String, welcher als
        erster Parameter für den Aufruf von <code>HBCIJob.setParam()</code> benutzt
        werden kann - vorausgesetzt, der entsprechende Job wurde mit
        {@link #newLowlevelJob(String)} erzeugt. </p>
        <p>Diese Methode verwendet intern die Methode 
        {@link HBCIKernel#getLowlevelJobParameterNames(String, String)}. 
        Unterschied ist, dass diese Methode zum einen überprüft, ob  der 
        angegebene Lowlevel-Job überhaupt vom aktuellen Passport unterstützt wird.
        Außerdem wird automatisch die richtige Versionsnummer an
        {@link HBCIKernel#getLowlevelJobParameterNames(String, String)} übergeben
        (nämlich die Versionsnummer, die <em>HBCI4Java</em> auch beim Anlegen
        eines Jobs via {@link #newLowlevelJob(String)} verwenden wird).</p>
        <p>Zur Beschreibung von High- und Lowlevel-Jobs siehe auch die Dokumentation
        im Package <code>org.kapott.hbci.GV</code>.</p>
        @param gvname der Lowlevel-Jobname, für den eine Liste der Job-Parameter
        ermittelt werden soll
        @return eine Liste aller Parameter-Bezeichnungen, die in der Methode
        {@link org.kapott.hbci.GV.HBCIJob#setParam(String,String)}
        benutzt werden können */
    public List<String> getLowlevelJobParameterNames(String gvname)
    {
        if (gvname==null || gvname.length()==0)
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_JOBNAME"));
        
        String version=getSupportedLowlevelJobs().getProperty(gvname);
        if (version==null)
            throw new HBCI_Exception("*** lowlevel job "+gvname+" not supported");
        
        return kernel.getLowlevelJobParameterNames(gvname,version);
    }
    
    /** <p>Gibt eine Liste mit Strings zurück, welche Bezeichnungen für die einzelnen Rückgabedaten
        eines Lowlevel-Jobs darstellen. Jedem {@link org.kapott.hbci.GV.HBCIJob} ist ein
        Result-Objekt zugeordnet, welches die Rückgabedaten und Statusinformationen zu dem jeweiligen
        Job enthält (kann mit {@link org.kapott.hbci.GV.HBCIJob#getJobResult()}
        ermittelt werden). Bei den meisten Highlevel-Jobs handelt es sich dabei um bereits aufbereitete
        Daten (Kontoauszüge werden z.B. nicht in dem ursprünglichen SWIFT-Format zurückgegeben, sondern
        bereits als fertig geparste Buchungseinträge).</p>
        <p>Bei Lowlevel-Jobs gibt es diese Aufbereitung der Daten nicht. Statt dessen müssen die Daten
        manuell aus der Antwortnachricht extrahiert und interpretiert werden. Die einzelnen Datenelemente
        der Antwortnachricht werden in einem Properties-Objekt bereitgestellt 
        ({@link org.kapott.hbci.GV_Result.HBCIJobResult#getResultData()}). Jeder Eintrag
        darin enthält den Namen und den Wert eines Datenelementes aus der Antwortnachricht.</p>
        <p>Die Methode <code>getLowlevelJobResultNames()</code> gibt nun alle gültigen Namen zurück,
        für welche in dem Result-Objekt Daten gespeichert sein können. Ob für ein Datenelement tatsächlich
        ein Wert in dem Result-Objekt existiert, wird damit nicht bestimmt, da einzelne Datenelemente
        optional sind.</p>
        <p>Diese Methode verwendet intern die Methode
        {@link HBCIKernel#getLowlevelJobResultNames(String, String)}. 
        Unterschied ist, dass diese Methode zum einen überprüft, ob  der 
        angegebene Lowlevel-Job überhaupt vom aktuellen Passport unterstützt wird.
        Außerdem wird automatisch die richtige Versionsnummer an
        {@link HBCIKernel#getLowlevelJobResultNames(String, String)} übergeben
        (nämlich die Versionsnummer, die <em>HBCI4Java</em> auch beim Anlegen
        eines Jobs via {@link #newLowlevelJob(String)} verwenden wird).</p>
        <p>Mit dem Tool {@link org.kapott.hbci.tools.ShowLowlevelGVRs} kann offline eine
        Liste aller Job-Result-Datenelemente erzeugt werden.</p>
        <p>Zur Beschreibung von High- und Lowlevel-Jobs siehe auch die Dokumentation
        im Package <code>org.kapott.hbci.GV</code>.</p>
        @param gvname Lowlevelname des Geschäftsvorfalls, für den die Namen der Rückgabedaten benötigt werden.
        @return Liste aller möglichen Property-Keys, für die im Result-Objekt eines Lowlevel-Jobs
        Werte vorhanden sein könnten */
    public List<String> getLowlevelJobResultNames(String gvname)
    {
        if (gvname==null || gvname.length()==0)
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_JOBNAME"));
        
        String version=getSupportedLowlevelJobs().getProperty(gvname);
        if (version==null)
            throw new HBCI_Exception("*** lowlevel job "+gvname+" not supported");
        
        return kernel.getLowlevelJobResultNames(gvname,version);
    }
    
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
     <p>I.d.R. werden mehrere Versionen eines Geschäftsvorfalles von der Bank
     angeboten. Diese Methode ermittelt automatisch die "richtige" Versionsnummer
     für die Ermittlung der GV-Restriktionen aus den BPD (und zwar die selbe,
     die <em>HBCI4Java</em> beim Erzeugen eines Jobs benutzt). </p>
     <p>Siehe dazu auch {@link HBCIJob#getJobRestrictions()}.</p>
     @param gvname Lowlevel-Name des Geschäftsvorfalles, für den die Restriktionen
     ermittelt werden sollen
     @return Properties-Objekt mit den einzelnen Restriktionen */
    public Properties getLowlevelJobRestrictions(String gvname)
    {
        if (gvname==null || gvname.length()==0)
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_JOBNAME"));
        
        String version=getSupportedLowlevelJobs().getProperty(gvname);
        if (version==null)
            throw new HBCI_Exception("*** lowlevel job "+gvname+" not supported");
        
        return passport.getJobRestrictions(gvname,version);
    }

    /** <p>Überprüfen, ein bestimmter Highlevel-Job von der Bank angeboten
        wird. Diese Methode kann benutzt werden, um <em>vor</em> dem Erzeugen eines
        {@link org.kapott.hbci.GV.HBCIJob}-Objektes zu überprüfen, ob
        der gewünschte Job überhaupt von der Bank angeboten wird. Ist das 
        nicht der Fall, so würde der Aufruf von 
        {@link org.kapott.hbci.manager.HBCIHandler#newJob(String)} 
        zu einer Exception führen.</p>
        <p>Eine Liste aller zur Zeit verfügbaren Highlevel-Jobnamen ist in der Paketbeschreibung
        des Packages <code>org.kapott.hbci.GV</code> zu finden. Wird hier nach einem Highlevel-Jobnamen
        gefragt, der nicht in dieser Liste enthalten ist, so wird eine Exception geworfen.</p>
        <p>Mit dieser Methode können nur Highlevel-Jobs überprüft werden. Zum Überprüfen,
        ob ein bestimmter Lowlevel-Job unterstützt wird, ist die Methode
        {@link HBCIHandler#getSupportedLowlevelJobs()}
        zu verwenden.</p>
        @param jobnameHL der Highlevel-Name des Jobs, dessen Unterstützung überprüft werden soll
        @return <code>true</code>, wenn dieser Job von der Bank unterstützt wird und
        mit <em>HBCI4Java</em> verwendet werden kann; ansonsten <code>false</code> */ 
    public boolean isSupported(String jobnameHL)
    {
        if (jobnameHL==null || jobnameHL.length()==0)
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_JOBNAME"));
        
        try {
            Class cl=Class.forName("org.kapott.hbci.GV.GV"+jobnameHL);
            String lowlevelName=(String)cl.getMethod("getLowlevelName",(Class[])null).invoke(null,(Object[])null);
            return getSupportedLowlevelJobs().keySet().contains(lowlevelName);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_HANDLER_HLCHECKERR",jobnameHL),e);
        }
    }
    
    /** Abholen der BPD bzw. UPD erzwingen. Beim Aufruf dieser Methode wird
     * automatisch ein HBCI-Dialog ausgeführt, der je nach Wert von <code>selectX</code>
     * die BPD und/oder UPD erneut abholt. Alle bis zu diesem Zeitpunkt erzeugten
     * ({@link org.kapott.hbci.GV.HBCIJob#addToQueue()}) und noch nicht ausgeführten Jobs werden dabei 
     * wieder aus der Job-Schlange entfernt. 
     * @param selectX kann aus einer Kombination (Addition) der Werte
     * {@link #REFRESH_BPD} und {@link #REFRESH_UPD} bestehen
     * @return Status-Objekt, welches Informationen über den ausgeführten 
     * HBCI-Dialog enthält */ 
    public HBCIDialogStatus refreshXPD(int selectX) 
    {
        if ((selectX & REFRESH_BPD)!=0) {
            passport.clearBPD();
        }
        if ((selectX & REFRESH_UPD)!=0) {
            passport.clearUPD();
        }

        reset();
        
        String customerId=passport.getCustomerId();
        getDialogFor(customerId);
        HBCIDialogStatus result=execute().getDialogStatus(customerId);
        return result;
    }
}
