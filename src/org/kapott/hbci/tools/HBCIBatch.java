
/*  $Id: HBCIBatch.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

    This file is part of hbci4java
    Copyright (C) 2001-2008  Stefan Palme

    hbci4java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    hbci4java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;

/** Tool zum Ausführen von HBCI-Jobs, die in einer Batch-Datei definiert werden 
 *  können.
 *  <pre>
 *  args[0] - configfile für HBCIUtils.init() (Property-File mit Kernel-Parametern
 *            [siehe API-Doc zu org.kapott.hbci.manager.HBCIUtils])
 *    zusätzliche parameter:
 *      client.passport.default=
 *      default.hbciversion=
 * 
 *  args[1] - Dateiname der Antwortdatei für Callbacks
 *    country=
 *    blz=
 *    host=
 *    port=
 *    filter=
 *    userid=
 *    customerid=
 *    sizentry=
 *    passphrase=
 *    softpin=
 *    pin=
 *    tans=
 * 
 *  args[2] - Dateiname der Batch-Datei (jobnamen und parameter siehe
 *            API-Doc zu Paket org.kapott.hbci.GV)
 *    # kommentar
 * 
 *    hljobname:jobid:(props|toString)[:customerid]
 *    hljobparam=paramvalue
 *    hljobparam=&lt;filename
 *    ...
 *    
 *    _lljobname:jobid[:customerid]
 *    _lljobparam=paramvalue
 *    _lljobparam=&lt;filename
 *    ...
 * 
 *    --[:customerid]
 * 
 *  args[3] - Dateiname der Ausgabedatei (mehr dazu siehe unten)
 *    jobid:XXXX
 *    job status:
 *    YYYYYYYYYYY
 *    ZZZZZZZZZZZ
 *    ...
 *    job result:
 *    resultparam=value
 *    resultparam=value
 *  
 *    ...
 *  [args[4]] - Dateiname der Log-Datei
 *  </pre>
 *  
 *  <p>Alle Jobs, bei deren Ausführung ein Fehler auftritt, werden nicht in die
 *  "normale" Ausgabedatei aufgenommen. Statt dessen wird eine zweite Aus-
 *  gabedatei erzeugt, die den gleichen Namen wie die "normale" Ausgabedatei
 *  plus ein Suffix ".err" hat. In dieser Fehlerdatei wird für jeden fehler-
 *  haften Job folgende Struktur geschrieben (String in "<>" wird durch die
 *  jeweiligen werte ersetzt):</p>
 *  <pre>
 *    jobid:JOBID 
 *    global status:
 *    allg. fehlermeldung zur hbci-nachricht, in der der job ausgeführt werden sollte
 *    job status:
 *    fehlermeldung zu dem nachrichten-segment, in welchem der job definiert war
 *  
 *    ...
 *  </pre>
 *  <p>das ist zwar nicht besonders schön, reicht aber vielleicht erst mal (?)
 *  Alternativ dazu könnte ich anbieten, dass eine vollständige Fehlernachricht
 *  über den *kompletten* Batch-Vorgang in eine Fehlerdatei geschrieben wird,
 *  sobald *irgendein* Job nicht sauber ausgeführt wurde (das hätte den Vorteil, 
 *  dass auch Fehler, die nicht direkt mit einem bestimmten Job in Verbindung
 *  stehen [z.B. Fehler bei der Dialog-Initialisierung] ordentlich geloggt
 *  werden).</p> */
public class HBCIBatch
{
    // speziell callback-klasse, um die ausgaben zu reduzieren und um die
    // nutzer-interaktion zu unterbinden, indem alle abgefragten daten auto-
    // tisch übergeben werden (aus args[1])
    private static class MyCallback
        extends HBCICallbackConsole
    {
        private Properties answers;   // alle spezifizierten antwortdaten

        public MyCallback(String[] args)
            throws FileNotFoundException,IOException
        {
            // einlesen der answers-datei
            answers=new Properties();
            FileInputStream answerFile=new FileInputStream(args[1]);
            answers.load(answerFile);
            answerFile.close();
            
            // wenn ein logfile angegeben wurde, dann dieses als ausgabemedium
            // für stdout und stderr verwenden
            if (args.length>=5) {
                PrintStream outStream=new PrintStream(new FileOutputStream(args[4]));
                System.setOut(outStream);
                System.setErr(outStream);
                this.setOutStream(outStream);
            }
        }
        
        // modifizierte callback-methode, die daten-anfragen "automatisch"
        // beantwortet
        public synchronized void callback(HBCIPassport passport,int reason,String msg,int datatype,StringBuffer retData)
        {
            switch (reason) {
                case NEED_CHIPCARD:
                    System.out.println(HBCIUtilsInternal.getLocMsg("CALLB_NEED_CHIPCARD"));
                    break;
                case NEED_HARDPIN:
                    System.out.println(HBCIUtilsInternal.getLocMsg("CALLB_NEED_HARDPIN"));
                    break;
                case NEED_SOFTPIN:
                    retData.replace(0,retData.length(),answers.getProperty("softpin"));
                    break;
                
                case NEED_PASSPHRASE_LOAD:
                case NEED_PASSPHRASE_SAVE:
                    retData.replace(0,retData.length(),answers.getProperty("passphrase"));
                    break;

                case NEED_PT_SECMECH:
                    retData.replace(0,retData.length(),answers.getProperty("secmech"));
                    break;

                case NEED_PT_PIN:
                    retData.replace(0,retData.length(),answers.getProperty("pin"));
                    break;
                case NEED_PT_TAN:
                    // TODO tan-liste aktivieren
                    retData.replace(0,retData.length(),answers.getProperty("tan"));
                    break;

                case NEED_COUNTRY:
                    retData.replace(0,retData.length(),answers.getProperty("country"));
                    break;
                case NEED_BLZ:
                    retData.replace(0,retData.length(),answers.getProperty("blz"));
                    break;
                case NEED_HOST:
                    retData.replace(0,retData.length(),answers.getProperty("host"));
                    break;
                case NEED_PORT:
                    retData.replace(0,retData.length(),answers.getProperty("port"));
                    break;
                case NEED_FILTER:
                    retData.replace(0,retData.length(),answers.getProperty("filter"));
                    break;
                case NEED_USERID:
                    retData.replace(0,retData.length(),answers.getProperty("userid"));
                    break;
                case NEED_CUSTOMERID:
                    retData.replace(0,retData.length(),answers.getProperty("customerid"));
                    break;
                    
                case NEED_SIZENTRY_SELECT:
                    retData.replace(0,retData.length(),answers.getProperty("sizentry"));
                    break;
                    
                case NEED_NEW_INST_KEYS_ACK:
                    retData.replace(0,retData.length(),"");
                    break;
                case HAVE_NEW_MY_KEYS:
                    System.out.println("please restart batch process");
                    break;
                    
                case HAVE_INST_MSG:
                    HBCIUtils.log(msg,HBCIUtils.LOG_INFO);
                    break;
                    
                case NEED_CONNECTION:
                case CLOSE_CONNECTION:
                    break;
            }
        }
        
        // ausgabe der status-meldungen komplett unterbinden
        public synchronized void status(HBCIPassport passport,int statusTag,
                                        Object[] objs)
        {
        }
    }
    
    private final static int STATE_NEED_JOBNAME=1;   // state-flags für 
    private final static int STATE_NEED_JOBPARAMS=2; //   batch-file-parser
    
    public static void main(String[] args)
        throws Exception
    {
        // initialisieren von hbci4java
        Properties  props=new Properties();
        InputStream istream=new FileInputStream(args[0]);
        props.load(istream);
        istream.close();
        HBCIUtils.init(props, new MyCallback(args));
        
        // erzeugen des passport-objektes
        HBCIPassport passport=AbstractHBCIPassport.getInstance();
        
        try {
            // initialisieren des hbci-handlers für das passport
            String version=passport.getHBCIVersion();
            HBCIHandler handler=new HBCIHandler(version.length()!=0?version:HBCIUtils.getParam("default.hbciversion"),passport);
            
            try {
                // batch-datei öffnen
                BufferedReader reader=new BufferedReader(new FileReader(args[2]));
                String line;
                
                try {
                    int       state=STATE_NEED_JOBNAME;
                    boolean   lljob=false;           // low- oder high-level-job?
                    HBCIJob   job=null;              // job-objekt
                    String    jobid=null;            // job-bezeichner
                    String    customerId=null;       // customer-id für job
                    Hashtable<String, Object> jobs=new Hashtable<String, Object>();  // liste aller jobs
                        
                    // batch-datei zeilenweise einlesen und auswerten
                    while ((line=reader.readLine())!=null) {
                        line=line.trim();
                        // kommentare ignorieren
                        if (line.startsWith("#")) {
                            continue;
                        }
                        
                        if (state==STATE_NEED_JOBNAME && line.length()!=0) {
                            // es wird der beginn einer job-definition erwartet
                            
                            StringTokenizer tok=new StringTokenizer(line,":");
                            // jobnamen extrahieren
                            String          jobname=tok.nextToken().trim();
                            
                            if (jobname.equals("--")) {
                                // wenn jobname="--", dann neue hbci-message erzeugen
                                customerId=(tok.hasMoreTokens()?tok.nextToken().trim():null);
                                handler.newMsg(customerId);
                            } else {
                                // ansonsten handelt es sich um einen "richtigen" job
                                String resultMode;
                                
                                if (jobname.startsWith("_")) {
                                    // wenn jobname mit "_" beginnt, handelt es
                                    // sich um einen low-level-jobnamen
                                    
                                    job=handler.newLowlevelJob(jobname.substring(1));
                                    lljob=true;
                                    
                                    // zu einem low-level-job müssen zusätzlich noch
                                    // eine ID (zum späteren wiederfinden des jobs)
                                    // und optional eine kunden-id festgelegt werden
                                    jobid=tok.nextToken().trim();
                                    resultMode="toString";
                                    customerId=(tok.hasMoreTokens()?tok.nextToken().trim():null);
                                } else {
                                    // wenn jobname nicht mit "_" beginnt, ist es
                                    // ein high-level-job
                                    
                                    job=handler.newJob(jobname);
                                    lljob=false;
                                    
                                    // zu einem high-level-job müssen zusätzlich noch
                                    // eine ID (zum späteren wiederfinden des jobs),
                                    // ein modus für die ausgabe der ergebisdaten
                                    // und optional eine kunden-id festgelegt werden
                                    jobid=tok.nextToken().trim();
                                    resultMode=tok.nextToken().trim();
                                    customerId=(tok.hasMoreTokens()?tok.nextToken().trim():null);
                                }
                                
                                // job in menge der jobs speichern 
                                jobs.put(jobid,job);
                                // ... und ausgabemodus für diesen job merken
                                jobs.put(jobid+"_resultMode",resultMode);
                                state=STATE_NEED_JOBPARAMS;
                            }
                        } else if (state==STATE_NEED_JOBPARAMS) {
                            // bis zur nächsten leerzeile oder dem dateienende
                            // werden jetzt alle zeilen als job-parameter
                            // interpretiert
                            
                            if (line.length()!=0) {
                                StringTokenizer tok=new StringTokenizer(line,"=");
                                
                                // parameternamen und -wert holen
                                String paramName=tok.nextToken().trim();
                                if (!tok.hasMoreTokens()) {
                                    continue;
                                }
                                String paramValue=tok.nextToken().trim();
                                
                                // für low-level-jobs müssen die parameter mit
                                // einem "_" beginnen, bei high-level-jobs
                                // dürfen sie *nicht* mit einem "_" beginnen
                                if (paramName.startsWith("_")!=lljob) {
                                    if (lljob) {
                                        throw new HBCI_Exception("*** "+jobid+" is a lowlevel job, so parameter names have to start with '_'");
                                    }
                                    throw new HBCI_Exception("*** "+jobid+" is a highlevel job, so parameter names must not start with '_'");
                                }
                                
                                // wenn es sich um einen low-level-job, den
                                // führenden "_" beim parameter-namen entfernen 
                                if (lljob) {
                                    paramName=paramName.substring(1);
                                }
                                
                                // wenn der parameter-wert mit einem "<" beginnt,
                                // so soll der wert des parameter aus der datei
                                // gelesen werden, die nach dem "<" spezifiziert
                                // ist
                                if (paramValue.startsWith("<")) {
                                    // öffnen der datei
                                    String          filename=paramValue.substring(1);
                                    FileInputStream fin=new FileInputStream(filename);
                                    
                                    // puffer für einlesen der datei
                                    byte[]       buffer=new byte[2048];
                                    int          len;
                                    StringBuffer content=new StringBuffer();
                                    
                                    // datei in stringbuffer einlesen
                                    while ((len=fin.read(buffer))>0) {
                                        content.append(new String(buffer,0,len,"ISO-8859-1"));
                                    }
                                    
                                    // datei schließen
                                    fin.close();
                                    // parameterwert ist inhalt des stringbuffers
                                    paramValue=content.toString();
                                }
                                
                                // parameter für aktuellen job setzen
                                job.setParam(paramName,paramValue);
                            } else {
                                // leerzeile gefunden - damit ist die parameter-
                                // spez. für den aktuellen job beendet
                                
                                // aktuellen job zur job-queue hinzufügen
                                job.addToQueue(customerId);
                                state=STATE_NEED_JOBNAME;
                            }
                        }
                    }
                    
                    // wenn noch ein job "in bearbeitung" ist, der noch nicht
                    // zur job-queue hinzugefügt wurde, dann das jetzt nachholen
                    if (state==STATE_NEED_JOBPARAMS) {
                        job.addToQueue(customerId);
                    }
                    
                    // alle batch-jobs ausführen
                    handler.execute();
                    
                    // ergebnis-writer für ok-jobs und für fehlerhafte jobs
                    // erzeugen
                    PrintWriter writer=new PrintWriter(new FileWriter(args[3]));
                    PrintWriter errWriter=new PrintWriter(new FileWriter(args[3]+".err"));
                    
                    try {
                        // alle bekannten job-bezeichner (IDs) durchlaufen
                        for (Enumeration<String> jobIds=jobs.keys();jobIds.hasMoreElements();) {
                            jobid=jobIds.nextElement();
                            if (jobid.endsWith("_resultMode")) {
                                continue;
                            }
                            // den dazugehörigen job holen
                            job=(HBCIJob)jobs.get(jobid);
                            
                            if (job.getJobResult().isOK()) {
                                // wenn der job erfolgreich gelaufen ist
                                
                                // ausgabe von jobid
                                writer.println("jobid:"+jobid);
                                // ausgabe der hbci-status-meldungen zu diesem job
                                writer.println("job status:");
                                writer.println(job.getJobResult().getJobStatus());
                                
                                // ausgabe der job-ergebnisse
                                writer.println("job result:");
                                
                                String resultMode=(String)jobs.get(jobid+"_resultMode");
                                if (resultMode.equals("props")) {
                                    // ausgabemodus="props": alle ergebnisdaten
                                    // als lowlevel-properties ausgeben
                                    
                                    Properties result=job.getJobResult().getResultData();
                                    if (result!=null) {
                                        // array mit result-properties holen und
                                        // sortieren
                                        String[] keys=(String[])new ArrayList(result.keySet()).toArray(new String[0]);
                                        Arrays.sort(keys);
                                        
                                        // ausgabe aller result-properties
                                        for (int i=0;i<keys.length;i++) {
                                            String name=keys[i];
                                            String value=result.getProperty(name);
                                            writer.println(name+"="+value);
                                        }
                                    }
                                } else {
                                    // ausgabemodus="toString": job-spezifische
                                    // toString()-methode für formatierung der
                                    // ergebnisdaten aufrufen
                                    writer.println(job.getJobResult());
                                }
                                
                                // leerzeile einfügen
                                writer.println();
                            } else {
                                // wenn ein job fehler erzeugt hatte, die fehlermeldungen
                                // an die err-datei anhängen
                                
                                errWriter.println("jobid:"+jobid);
                                errWriter.println("global status:");
                                errWriter.println(job.getJobResult().getGlobStatus().getErrorString());
                                errWriter.println("job status:");
                                errWriter.println(job.getJobResult().getJobStatus().getErrorString());
                                errWriter.println();
                            }
                        }
                    } finally {
                        writer.close();
                        errWriter.close();
                    }
                } finally {
                    reader.close();
                }
            } finally {
                handler.close();
                passport=null;
            }
        } finally {
            if (passport!=null) {
                passport.close();
            }
        }
    }
}
