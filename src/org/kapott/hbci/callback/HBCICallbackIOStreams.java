
/*  $Id: HBCICallbackIOStreams.java,v 1.1 2011/05/04 22:37:52 willuhn Exp $

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

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Date;
import java.util.StringTokenizer;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.INILetter;
import org.kapott.hbci.status.HBCIMsgStatus;

/** Callback-Klasse für Ein-/Ausgabe über IO-Streams. Dabei handelt es sich
 * eine Callback-Klasse, die Ausgaben auf einem PrintStream ausgibt und
 * Eingaben über einen BufferedReader liest. Die Klasse 
 * {@link org.kapott.hbci.callback.HBCICallbackConsole HBCICallbackConsole} 
 * ist eine abgeleitete Klasse, welche STDOUT und STDIN für die beiden
 * I/O-Streams verwendet. */
public class HBCICallbackIOStreams 
    extends AbstractHBCICallback 
{
    private PrintStream    outStream;
    private BufferedReader inStream;
    
    /** Instanz mit vorgegebenem OUT- und INPUT-Stream erzeugen.
     * @param outStream Stream, welcher für die Ausgabe verwendet wird.
     * @param inStream Stream, der für das Einlesen von Antworten verwendet wird */
    public HBCICallbackIOStreams(PrintStream outStream, BufferedReader inStream)
    {
        this.outStream=outStream;
        this.inStream=inStream;
    }

    /** TODO: doc */
    protected void setInStream(BufferedReader in)
    {
        this.inStream=in;
    }
    
    /** Gibt des INPUT-Stream zurück. */
    protected BufferedReader getInStream() {
        return inStream;
    }

    /** TODO: doc */
    protected void setOutStream(PrintStream out)
    {
        this.outStream=out;
    }
    
    /** Gibt den verwendeten OUTPUT-Stream zurück. */
    protected PrintStream getOutStream() {
        return outStream;
    }
    
    /** Schreiben von Logging-Ausgaben in einen <code>PrintStream</code>. Diese Methode implementiert die Logging-Schnittstelle
    des {@link org.kapott.hbci.callback.HBCICallback}-Interfaces</a>. Die Log-Informationen,
    die dieser Methode übergeben werden, werden formatiert auf dem jeweiligen <code>outStream</code> ausgegeben. In dem
    ausgegebenen String sind in enthalten das Log-Level der Message, ein Zeitstempel im
    Format "<code>yyyy.MM.dd HH:mm:ss.SSS</code>", die Namen der ThreadGroup und des Threads, aus dem 
    heraus die Log-Message erzeugt wurde, der Klassenname der Klasse, welche die Log-Ausgabe
    erzeugt hat sowie die eigentliche Log-Message */
    public synchronized void log(String msg, int level, Date date, StackTraceElement trace) 
    {
    	String line=createDefaultLogLine(msg,level,date,trace);
        getOutStream().println(line);
    }

    /** Diese Methode reagiert auf alle möglichen Callback-Ursachen. Bei Callbacks, die nur
    Informationen an den Anwender übergeben sollen, werden diese auf dem <code>outStream</code> ausgegeben.
    Bei Callbacks, die Aktionen vom Anwender erwarten (Einlegen der Chipkarte), wird eine
    entsprechende Aufforderung ausgegeben. Bei Callbacks, die eine Eingabe vom
    Nutzer erwarten, wird die entsprechende Eingabeaufforderung ausgegeben und die
    Eingabe vom <code>inStream</code> gelesen.*/
    public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData) 
    {
        getOutStream().println(HBCIUtilsInternal.getLocMsg("CALLB_PASS_IDENT",passport.getClientData("init")));
        
        try {
            INILetter iniletter;
            LogFilter logfilter=LogFilter.getInstance();
            Date      date;
            String    st;
            
            switch (reason) {
                case NEED_PASSPHRASE_LOAD:
                case NEED_PASSPHRASE_SAVE:
                    getOutStream().print(msg+": ");
                    getOutStream().flush();
                    
                    st=getInStream().readLine();
                    if (reason==NEED_PASSPHRASE_SAVE) {
                        getOutStream().print(msg+" (again): ");
                        getOutStream().flush();
                        
                        String st2=getInStream().readLine();
                        if (!st.equals(st2))
                            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_PWDONTMATCH"));
                    }
                    logfilter.addSecretData(st,"X",LogFilter.FILTER_SECRETS);
                    retData.replace(0,retData.length(),st);
                    break;
    
                case NEED_CHIPCARD:
                    getOutStream().println(msg);
                    break;
    
                case NEED_HARDPIN:
                    getOutStream().println(msg);
                    break;
    
                case NEED_SOFTPIN:
                case NEED_PT_PIN:
                case NEED_PT_TAN:
                case NEED_PROXY_PASS:
                    getOutStream().print(msg+": ");
                    getOutStream().flush();
                    String secret=getInStream().readLine();
                    logfilter.addSecretData(secret,"X",LogFilter.FILTER_SECRETS);
                    retData.replace(0,retData.length(),secret);
                    break;
    
                case HAVE_HARDPIN:
                    HBCIUtils.log("end of entering hardpin",HBCIUtils.LOG_DEBUG);
                    break;
    
                case HAVE_CHIPCARD:
                    HBCIUtils.log("end of waiting for chipcard",HBCIUtils.LOG_DEBUG);
                    break;
    
                case NEED_COUNTRY:
                case NEED_BLZ:
                case NEED_HOST:
                case NEED_PORT:
                case NEED_FILTER:
                case NEED_USERID:
                case NEED_CUSTOMERID:
                case NEED_PROXY_USER:
                    getOutStream().print(msg+" ["+retData.toString()+"]: ");
                    getOutStream().flush();
                    st=getInStream().readLine();
                    if (st.length()==0)
                        st=retData.toString();
                    
                    if (reason==NEED_BLZ) {
                    	logfilter.addSecretData(st,"X",LogFilter.FILTER_MOST);
                    } else if (reason==NEED_USERID || reason==NEED_CUSTOMERID || reason==NEED_PROXY_USER) {
                    	logfilter.addSecretData(st,"X",LogFilter.FILTER_IDS);
                    }
                    
                    retData.replace(0,retData.length(),st);
                    break;
    
                case NEED_NEW_INST_KEYS_ACK:
                    getOutStream().println(msg);
                    iniletter=new INILetter(passport,INILetter.TYPE_INST);
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("EXPONENT")+": "+HBCIUtils.data2hex(iniletter.getKeyExponentDisplay()));
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("MODULUS")+": "+HBCIUtils.data2hex(iniletter.getKeyModulusDisplay()));
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("HASH")+": "+HBCIUtils.data2hex(iniletter.getKeyHashDisplay()));
                    getOutStream().print("<ENTER>=OK, \"ERR\"=ERROR: ");
                    getOutStream().flush();
                    retData.replace(0, retData.length(), getInStream().readLine());
                    break;
    
                case HAVE_NEW_MY_KEYS:
                    iniletter=new INILetter(passport,INILetter.TYPE_USER);
                    date=new Date();
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("DATE")+": "+HBCIUtils.date2StringLocal(date));
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("TIME")+": "+HBCIUtils.time2StringLocal(date));
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("BLZ")+": "+passport.getBLZ());
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("USERID")+": "+passport.getUserId());
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("KEYNUM")+": "+passport.getMyPublicSigKey().num);
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("KEYVERSION")+": "+passport.getMyPublicSigKey().version);
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("EXPONENT")+": "+HBCIUtils.data2hex(iniletter.getKeyExponentDisplay()));
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("MODULUS")+": "+HBCIUtils.data2hex(iniletter.getKeyModulusDisplay()));
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("HASH")+": "+HBCIUtils.data2hex(iniletter.getKeyHashDisplay()));
                    getOutStream().println(msg);
                    break;
    
                case HAVE_INST_MSG:
                    getOutStream().println(msg);
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("CONTINUE"));
                    getOutStream().flush();
                    getInStream().readLine();
                    break;
    
                case NEED_REMOVE_CHIPCARD:
                    getOutStream().println(msg);
                    break;
    
                case HAVE_CRC_ERROR:
                    getOutStream().println(msg);
    
                    int idx=retData.indexOf("|");
                    String blz=retData.substring(0,idx);
                    String number=retData.substring(idx+1);
    
                    getOutStream().print(HBCIUtilsInternal.getLocMsg("BLZ")+" ["+blz+"]: ");
                    getOutStream().flush();
                    String s=getInStream().readLine();
                    if (s.length()==0)
                        s=blz;
                    blz=s;
    
                    getOutStream().print(HBCIUtilsInternal.getLocMsg("ACCNUMBER")+" ["+number+"]: ");
                    getOutStream().flush();
                    s=getInStream().readLine();
                    if (s.length()==0)
                        s=number;
                    number=s;
                    
                    logfilter.addSecretData(blz,"X",LogFilter.FILTER_MOST);
                    logfilter.addSecretData(number,"X",LogFilter.FILTER_IDS);
    
                    retData.replace(0,retData.length(),blz+"|"+number);
                    break;
    
                case HAVE_IBAN_ERROR:
                    getOutStream().println(msg);
    
                    String iban=retData.toString();
                    getOutStream().print(HBCIUtilsInternal.getLocMsg("IBAN")+" ["+iban+"]: ");
                    getOutStream().flush();
                    String newiban=getInStream().readLine();
                    if (newiban.length()!=0 && !newiban.equals(iban)) {
                    	retData.replace(0,retData.length(),newiban);
                    	logfilter.addSecretData(newiban,"X",LogFilter.FILTER_IDS);
                    }
                    break;
    
                case HAVE_ERROR:
                    getOutStream().println(msg);
                    getOutStream().print("<ENTER>=OK, \"ERR\"=ERROR: ");
                    getOutStream().flush();
                    retData.replace(0,retData.length(), getInStream().readLine());
                    break;
                    
                case NEED_SIZENTRY_SELECT:
                    StringTokenizer tok=new StringTokenizer(retData.toString(),"|");
                    while (tok.hasMoreTokens()) {
                        String entry=tok.nextToken();
                        StringTokenizer tok2=new StringTokenizer(entry,";");
                        
                        String tempblz;
                        getOutStream().println(tok2.nextToken()+": "+
                                           HBCIUtilsInternal.getLocMsg("BLZ")+"="+(tempblz=tok2.nextToken())+
                                           " ("+HBCIUtils.getNameForBLZ(tempblz)+") "+
                                           HBCIUtilsInternal.getLocMsg("USERID")+"="+tok2.nextToken());
                    }
                    getOutStream().print(HBCIUtilsInternal.getLocMsg("CALLB_SELECT_ENTRY")+": ");
                    getOutStream().flush();
                    retData.replace(0,retData.length(),getInStream().readLine());
                    break;
    
                case NEED_PT_SECMECH:
                    String[] entries=retData.toString().split("\\|");
                    int      len=entries.length;
                    for (int i=0;i<len;i++) {
                        String   entry=entries[i];
                        String[] values=entry.split(":");
                        
                        getOutStream().println(values[0]+": "+values[1]);
                    }
                    getOutStream().print(HBCIUtilsInternal.getLocMsg("CALLB_SELECT_ENTRY")+": ");
                    getOutStream().flush();
                    retData.replace(0,retData.length(),getInStream().readLine());
                    break;
    
                case NEED_INFOPOINT_ACK:
                    getOutStream().println(msg);
                    getOutStream().println(retData);
                    getOutStream().print("Press <RETURN> to send this data; enter \"NO\" to NOT send this data: ");
                    getOutStream().flush();
                    retData.replace(0,retData.length(),getInStream().readLine());
                    break;
    
                case NEED_CONNECTION:
                case CLOSE_CONNECTION:
                    getOutStream().println(msg);
                    getOutStream().println(HBCIUtilsInternal.getLocMsg("CONTINUE"));
                    getOutStream().flush();
                    getInStream().readLine();
                    break;
                    
                case USERID_CHANGED:
                    getOutStream().println(msg);
                    break;
    
                default:
                    throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_UNKNOWN",Integer.toString(reason)));
            }
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_ERR"),e);
        }
    }

    /** Wird diese Methode von <em>HBCI4Java</em> aufgerufen, so wird der aktuelle
    Bearbeitungsschritt (mit evtl. vorhandenen zusätzlichen Informationen)
    auf <code>outStream</code> ausgegeben. */
    public synchronized void status(HBCIPassport passport, int statusTag, Object[] o) {
        switch (statusTag) {
            case STATUS_INST_BPD_INIT:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_DATA"));
                break;
            case STATUS_INST_BPD_INIT_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_DATA_DONE",passport.getBPDVersion()));
                break;
            case STATUS_INST_GET_KEYS:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_KEYS"));
                break;
            case STATUS_INST_GET_KEYS_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_INST_KEYS_DONE"));
                break;
            case STATUS_SEND_KEYS:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_SEND_MY_KEYS"));
                break;
            case STATUS_SEND_KEYS_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_SEND_MY_KEYS_DONE"));
                getOutStream().println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_INIT_SYSID:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_SYSID"));
                break;
            case STATUS_INIT_SYSID_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_SYSID_DONE",o[1].toString()));
                getOutStream().println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_INIT_SIGID:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_SIGID"));
                break;
            case STATUS_INIT_SIGID_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_SIGID_DONE",o[1].toString()));
                getOutStream().println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_INIT_UPD:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_USER_DATA"));
                break;
            case STATUS_INIT_UPD_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_REC_USER_DATA_DONE",passport.getUPDVersion()));
                break;
            case STATUS_LOCK_KEYS:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_USR_LOCK"));
                break;
            case STATUS_LOCK_KEYS_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_USR_LOCK_DONE"));
                getOutStream().println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_DIALOG_INIT:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_INIT"));
                break;
            case STATUS_DIALOG_INIT_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_INIT_DONE",o[1]));
                getOutStream().println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_SEND_TASK:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_NEW_JOB",((HBCIJob)o[0]).getName()));
                break;
            case STATUS_SEND_TASK_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_JOB_DONE",((HBCIJob)o[0]).getName()));
                break;
            case STATUS_DIALOG_END:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_END"));
                break;
            case STATUS_DIALOG_END_DONE:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_DIALOG_END_DONE"));
                getOutStream().println("status: "+((HBCIMsgStatus)o[0]).toString());
                break;
            case STATUS_MSG_CREATE:
                getOutStream().println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_CREATE",o[0].toString()));
                break;
            case STATUS_MSG_SIGN:
                getOutStream().println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_SIGN"));
                break;
            case STATUS_MSG_CRYPT:
                getOutStream().println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_CRYPT"));
                break;
            case STATUS_MSG_SEND:
                getOutStream().println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_SEND"));
                break;
            case STATUS_MSG_RECV:
                getOutStream().println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_RECV"));
                break;
            case STATUS_MSG_PARSE:
                getOutStream().println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_PARSE",o[0].toString()+")"));
                break;
            case STATUS_MSG_DECRYPT:
                getOutStream().println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_DECRYPT"));
                break;
            case STATUS_MSG_VERIFY:
                getOutStream().println("  "+HBCIUtilsInternal.getLocMsg("STATUS_MSG_VERIFY"));
                break;
            case STATUS_SEND_INFOPOINT_DATA:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_SEND_INFOPOINT_DATA"));
                break;
                
            case STATUS_MSG_RAW_SEND:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_MSG_RAW_SEND",o[0].toString()));
                break;

            case STATUS_MSG_RAW_RECV:
                getOutStream().println(HBCIUtilsInternal.getLocMsg("STATUS_MSG_RAW_RECV",o[0].toString()));
                break;

            default:
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("STATUS_INVALID",Integer.toString(statusTag)));
        }
    }
}
