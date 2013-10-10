
/*  $Id: HBCIDialogStatus.java,v 1.1 2011/05/04 22:38:02 willuhn Exp $

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

package org.kapott.hbci.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtilsInternal;

/** <p>Status-Informationen für einen kompletten HBCI-Dialog. Objekte
    dieser Klasse werden von {@link HBCIExecStatus}-Objekten verwaltet. 
    In einem <code>HBCIDialogStatus</code> werden alle Status-Informationen
    gespeichert, die während der Ausführung eines HBCI-Dialoges anfallen.
    </p><p>
    Die direkte Auswertung der Felder dieser Klasse ist i.d.R. nicht zu empfehlen.
    Statt dessen sollten die bereitgestellten Methoden benutzt werden, um alle
    relevanten Informationen zu extrahieren. </p>*/
public final class HBCIDialogStatus
{
    /** <p>Status-Informationen zu den einzelnen Nachrichten zwischen
        Dialog-Initialisierung und Dialog-Abschluss ("Nutzdaten"). Ein Element
        dieses Arrays enthält dabei die Status-Informationen über
        genau einen HBCI-Nachrichtenaustausch.
        </p><p>
        Die direkte Auswertung dieses Feldes ist aus folgendem Grund
        in den meisten Fällen nicht zu empfehlen: Sollen mehrere Geschäftsvorfälle
        innerhalb eines einzigen HBCI-Dialoges ausgeführt werden, so weiß
        die HBCI-Anwendung i.d.R. nicht, in welcher Nachricht sich ein bestimmter
        GV befindet bzw. wie viele Nachrichten überhaupt erzeugt werden,
        weil der HBCI-Kernel beim Hinzufügen von Geschäftsvorfällen u.U.
        selbstständig die Erzeugung einer zusätzlichen Nachricht auslöst. Es
        ist deshalb nicht ohne weiteres möglich, die zu einem bestimmten
        Geschäftsvorfall passende Nachrichtennummer zu ermitteln, um damit
        das entsprechende Element aus diesem Array zu extrahieren. </p> */
    public HBCIMsgStatus[] msgStatus;
    /** Statusinformationen zur Dialog-Initialisierungs-Nachricht. In diesem
        Feld werden alle Status-Informationen gespeichert, die die
        Dialog-Initialisierung betreffen. */
    public HBCIMsgStatus   initStatus;
    /** Statusinformationen zur Dialog-Abschluss-Nachricht. In diesem Feld
        werden alle Status-Informationen gespeichert, die die Nachrichten zur
        Beendigung des Dialoges betreffen. */
    public HBCIMsgStatus   endStatus;
    
    public HBCIDialogStatus()
    {
        msgStatus=null;
        initStatus=null;
        endStatus=null;
    }
    
    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public void setInitStatus(HBCIMsgStatus status)
    {
        this.initStatus=status;
    }
    
    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public void setMsgStatus(HBCIMsgStatus[] status)
    {
        this.msgStatus=status;
    }
    
    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public void setEndStatus(HBCIMsgStatus status)
    {
        this.endStatus=status;
    }
    
    /** <p>Gibt zurück, ob der Dialog als ganzes erfolgreich abgelaufen ist.
        Ein Dialog gilt dann als erfolgreich abgelaufen, wenn die Dialog-Initialisierung,
        alle Nachrichten mit Geschäftsvorfällen sowie der Dialog-Abschluss ohne
        Fehlermeldungen abgelaufen sind.
        </p><p>
        Sobald auch nur eine dieser Nachrichten einen Fehler erzeugt hat, gibt diese
        Methode <code>false</code> zurück. Es handelt sich also um einen sehr
        "strengen" Test. Falls diese Methode <code>true</code> zurückgibt,
        so könnte eine Überprüfung der einzelnen Geschäftsvorfälle auf eventuell
        aufgetretene Fehler entfallen (siehe jedoch unten). Beim Rückgabewert 
        <code>false</code> müssen alle ausgeführten Geschäftsvorfälle überprüft 
        werden, ob einer (oder mehrere) davon den (oder die) Fehler ausgelöst haben.</p>
        <p><b>Achtung:</b> Wenn diese Methode <code>true</code> zurückgibt, heißt
        das nicht zwangsläufig, dass auch alle geplanten <code>HBCIJobs</code>
        tatsächlich erfolgreich durchgeführt wurden. Diese Methode zeigt nur an,
        dass die <code>HBCIJobs</code>, die auch tatsächlich in einer der 
        Auftragsnachrichten enthalten gewesen sind, erfolgreich durchgeführt wurden.
        Trat beim Hinzufügen eines <code>HBCIJobs</code> zu einer Azuftragsnachricht
        ein Fehler auf, so dass dieser <code>HBCIJob</code> gar nicht erst 
        versandt wurde, so zeigt diese Methode u.U. trotzdem <code>true</code>,
        obwohl gar nicht alle geplanten Aufträge ausgeführt wurden (eben weil diese
        Methode nur anzeigt, ob bei der eigentlichen <em>Ausführung</em> von Aufträgen
        Fehler aufgetreten sind oder nicht).</p>
        <p>Um also sicher zu gehen, dass alle gewünschten Aufträge auch wirklich 
        erfolgreich ausgeführt wurden, sollte von jedem ursprünglich erzeugten 
        <code>HBCIJob</code> der Status mit {@link org.kapott.hbci.GV.HBCIJob#getJobResult()} und
        {@link org.kapott.hbci.GV_Result.HBCIJobResult#isOK()} geprüft werden.</p>
        @return <code>true</code>, wenn keine Nachricht des Dialoges einen Fehler
                erzeugt hat; <code>false</code>, wenn wenigstens ein Nachrichtenaustausch
                nicht fehlerfrei abgelaufen ist.*/
    public boolean isOK()
    {
        boolean ret;
        
        ret=(initStatus!=null && initStatus.isOK());
        if (msgStatus!=null) {
            for (int i=0;i<msgStatus.length;i++) {
                ret&=msgStatus[i].isOK();
            }
        }
        ret&=(endStatus!=null && endStatus.isOK());
        
        return ret;
    }
    
    /** Zeigt an, ob während der Dialogausführung Exceptions ausgetreten sind 
        @return <code>true</code>, wenn während der Dialogausführung Exceptions
                 aufgetreten sind, sonst <code>false</code> 
        @deprecated wird nicht benutzt */
    public boolean hasExceptions()
    {
        boolean ret=false;
        
        if (initStatus!=null) {
            ret|=initStatus.hasExceptions();
        }
        
        if (msgStatus!=null) {
            for (int i=0;i<msgStatus.length;i++) {
                ret|=msgStatus[i].hasExceptions();
            }
        }
        
        if (endStatus!=null) {
            ret|=endStatus.hasExceptions();
        }
        
        return ret;
    }
    
    /** Gibt alle während der Dialogausführung aufgetretenen Exceptions zurück.
        Die hier erzeugte Menge von Exceptions schließt alle Exceptions ein, die
        während der Dialog-Initialisierung, der Ausführung der einzelnen 
        Auftragsnachrichten bzw. während der Ausführung der Dialog-Ende-Nachricht
        aufgetreten sind 
        @return Array mit Exceptions, die während der Dialogausführung aufgetreten
                 sind
        @deprecated wird nicht benutzt */
    public Exception[] getExceptions()
    {
        List<Exception> ret=new ArrayList<Exception>();
        
        if (initStatus!=null) {
            ret.addAll(Arrays.asList(initStatus.getExceptions()));
        }
        
        if (msgStatus!=null) {
            for (int i=0;i<msgStatus.length;i++) {
                ret.addAll(Arrays.asList(msgStatus[i].getExceptions()));
            }
        }
        
        if (endStatus!=null) {
            ret.addAll(Arrays.asList(endStatus.getExceptions()));
        }

        return ret.toArray(new Exception[ret.size()]);
    }
    
    /** Gibt für einen Dialog alle Fehlermeldungen zurück. Für jede Nachricht
        des kompletten HBCI-Dialoges (Dialog-Initialisierung, Nutzdaten,
        Dialog-Abschluss) werden die jeweils aufgetretenen Fehlermeldungen
        gesammelt und zu dem Rückgabewert dieser Methode hinzugefügt. Trat bei
        einer Nachricht kein Fehler auf, so wird auch nichts zum Rückgabewert
        dieser Methode hinzugefügt.
        @return einen String, der alle im Dialog aufgetretenen Fehler beschreibt */
    public String getErrorString()
    {
        StringBuffer ret=new StringBuffer();
        
        if (initStatus!=null) {
            String s=initStatus.getErrorString();
            if (s.length()!=0) {
                // ret.append(HBCIUtilsInternal.getLocMsg("STAT_INIT")).append(":");
                // ret.append(System.getProperty("line.separator"));
                ret.append(s);
                ret.append(System.getProperty("line.separator"));
            }
        }
        
        if (msgStatus!=null) {
            for (int i=0;i<msgStatus.length;i++) {
                String s=msgStatus[i].getErrorString();
                if (s.length()!=0) {
                    // ret.append(HBCIUtilsInternal.getLocMsg("STAT_MSG")).append(" ");
                    // ret.append(Integer.toString(i+1));
                    // ret.append(":");
                    // ret.append(System.getProperty("line.separator"));
                    ret.append(s);
                    ret.append(System.getProperty("line.separator"));
                }
            }
        }
        
        if (endStatus!=null) {
            String s=endStatus.getErrorString();
            if (s.length()!=0) {
                // ret.append(HBCIUtilsInternal.getLocMsg("STAT_END")).append(":");
                // ret.append(System.getProperty("line.separator"));
                ret.append(s);
                ret.append(System.getProperty("line.separator"));
            }
        }
        
        return ret.toString().trim();
    }
    
    /** Wandelt alle Statusinformationen zu einem Dialog in einen
        einzigen String um. Zu jeder einzelnen Nachricht des Dialoges
        werden alle Status-Informationen (aufgetretene Exceptions, Fehlermeldungen,
        Warnungen, Erfolgsmeldungen) gesammelt und aneinander gehängt.
        @return einen String, der die kompletten Status-Informationen für einen 
                Dialog enthält */
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        ret.append(HBCIUtilsInternal.getLocMsg("STAT_INIT")).append(":").append(System.getProperty("line.separator"));
        if (initStatus!=null) {
            ret.append(initStatus.toString());
        } else {
            ret.append("(not status information available)");
        }
        ret.append(System.getProperty("line.separator"));
        
        if (msgStatus!=null) {
            for (int i=0;i<msgStatus.length;i++) {
                ret.append(HBCIUtilsInternal.getLocMsg("STAT_MSG")).append(" #").append(i+1).append(":").append(System.getProperty("line.separator"));
                ret.append(msgStatus[i].toString());
                ret.append(System.getProperty("line.separator"));
            }
        }
        
        ret.append(HBCIUtilsInternal.getLocMsg("STAT_END")).append(":").append(System.getProperty("line.separator"));
        if (endStatus!=null) {
            ret.append(endStatus.toString());
        } else {
            ret.append("(not status information available)");
        }
        ret.append(System.getProperty("line.separator"));
        
        return ret.toString().trim();
    }
}
