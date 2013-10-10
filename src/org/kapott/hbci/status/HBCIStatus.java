
/*  $Id: HBCIStatus.java,v 1.1 2011/05/04 22:38:02 willuhn Exp $

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
import java.util.Iterator;
import java.util.List;

import org.kapott.hbci.manager.HBCIUtils;

/** <p>Menge zusammengehöriger Status-Informationen. In Objekten dieser
    Klasse kann eine Menge von HBCI-Statuscodes sowie eine Menge von
    Exceptions gespeichert werden. Der Sinn dieser Klasse ist die
    Zusammenfassung von mehreren Status-Informationen, die logisch
    zusammengehören (z.B. alle Status-Informationen, die ein bestimmtes
    Nachrichtensegment betreffen).
    </p><p>
    Objekte dieser Klasse werden beispielsweise in 
    {@link org.kapott.hbci.status.HBCIMsgStatus} verwendet,
    um globale und segmentbezogene Status-Informationen voneinander getrennt
    zu sammeln. </p>*/
public final class HBCIStatus
{
    /** Statuscode für "alle Statusinformationen besagen OK" */
    public static final int STATUS_OK=0;
    /** Statuscode für "Gesamtstatus kann nicht ermittelt werden". (z.B. weil
        gar keine Informationen in diesem Objekt enthalten sind) */
    public static final int STATUS_UNKNOWN=1;
    /** Statuscode für "es ist mindestens ein Fehlercode enthalten" */
    public static final int STATUS_ERR=2;
    
    private List<HBCIRetVal> retVals;
    private List<Exception> exceptions;
    
    public HBCIStatus()
    {
        retVals=new ArrayList<HBCIRetVal>();
        exceptions=new ArrayList<Exception>();
    }
    
    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public void addException(Exception e)
    {
        exceptions.add(e);
        HBCIUtils.log(e);
    }

    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public void addRetVal(HBCIRetVal ret)
    {
        retVals.add(ret);
        if (ret.isError()) {
            HBCIUtils.log("HBCI error code: "+ret.toString(), HBCIUtils.LOG_ERR);
        }
    }

    /** Gibt zurück, ob in diesem Status-Objekt Exceptions gespeichert sind
        @return <code>true</code>, falls Exceptions gespeichert sind,
                sonst <code>false</code>*/
    public boolean hasExceptions()
    {
        return exceptions.size()!=0;
    }
    
    private boolean hasX(char code)
    {
        boolean ret=false;
        
        for (Iterator<HBCIRetVal> i=retVals.iterator(); i.hasNext();) {
            HBCIRetVal retVal= i.next();
            if (retVal.code.charAt(0)==code) {
                ret=true;
                break;
            }
        }
        
        return ret;
    }
    
    /** Gibt zurück, ob in den Rückgabedaten in diesem Objekt Fehlermeldungen
        enthalten sind
        @return <code>true</code>, falls Fehlermeldungen vorhanden sind,
                sonst <code>false</code>*/
    public boolean hasErrors()
    {
        return hasX('9');
    }
    
    /** Gibt zurück, ob in den Rückgabedaten in diesem Objekt Warnungen
        enthalten sind
        @return <code>true</code>, falls Warnungen vorhanden sind,
                sonst <code>false</code>*/
    public boolean hasWarnings()
    {
        return hasX('3');
    }
    
    /** Gibt zurück, ob in den Rückgabedaten in diesem Objekt Erfolgsmeldungen
        enthalten sind
        @return <code>true</code>, falls Erfolgsmeldungen vorhanden sind,
                sonst <code>false</code>*/
    public boolean hasSuccess()
    {
        return hasX('0');
    }
    
    private HBCIRetVal[] getX(char code)
    {
        ArrayList<HBCIRetVal> ret_a=new ArrayList<HBCIRetVal>();
        
        for (Iterator<HBCIRetVal> i=retVals.iterator(); i.hasNext();) {
            HBCIRetVal retVal= i.next();
            
            if (retVal.code.charAt(0)==code) {
                ret_a.add(retVal);
            }
        }
        
        HBCIRetVal[] ret=new HBCIRetVal[0];
        if (ret_a.size()!=0) {
            ret=ret_a.toArray(ret);
        }
        
        return ret;
    }
    
    /** Gibt die in diesem Status-Objekt gespeicherten Exceptions zurück
        @return Array mit Exceptions, die während der HBCI-Kommunikation
        aufgetreten sind. */
    public Exception[] getExceptions()
    {
        return exceptions.toArray(new Exception[exceptions.size()]);
    }
    
    /** Gibt alle in diesem Status-Objekt gespeicherten Rückgabewerte zurück
     @return Array mit <code>HBCIRetVal</code>s, die während der HBCI-Kommunikation
     aufgetreten sind. */
    public HBCIRetVal[] getRetVals()
    {
        return retVals.toArray(new HBCIRetVal[retVals.size()]);
    }
    
    /** Gibt die in diesem Objekt gespeicherten Fehlermeldungen zurück
        @return Array mit HBCI-Returncodes, die allesamt Fehlermeldungen beschreiben */
    public HBCIRetVal[] getErrors()
    {
        return getX('9');
    }

    /** Gibt die in diesem Objekt gespeicherten Warnungen zurück
        @return Array mit HBCI-Returncodes, die allesamt Warnmeldungen beschreiben */
    public HBCIRetVal[] getWarnings()
    {
        return getX('3');
    }

    /** Gibt die in diesem Objekt gespeicherten Erfolgsmeldungen zurück
        @return Array mit HBCI-Returncodes, die allesamt Erfolgsmeldungen beschreiben */
    public HBCIRetVal[] getSuccess()
    {
        return getX('0');
    }

    /** Gibt einen Code zurück, der den zusammengefassten Status aller in diesem
        Objekt gespeicherten Rückgabewerte beschreibt. Dafür gibt es folgende
        Möglichkeiten:
        <ul>
          <li><code>STATUS_OK</code> wird zurückgegeben, wenn es keine Fehlermeldungen
              oder Exceptions gegeben hat und mindestens eine Erfolgsmeldung oder
              Warnung enthalten ist</li>
         <li><code>STATUS_ERR</code> wird zurückgegeben, wenn wenigstens eine
             Exception aufgetreten ist oder wenigstens eine Fehlermeldung enthalten
             ist.</li>
         <li><code>STATUS_UNKNOWN</code> wird zurückgegeben, wenn keine der beiden
             o.g. Bedingungen zutrifft.</li>
        </ul> 
        @return einen Code, der den zusammengefassten Status aller Rückgabewerte
                beschreibt. */
    public int getStatusCode()
    {
        int code;
        
        /* TODO: eine Exception als Fehler einzustufen ist gefaehrlich: wenn
         * ein GV bei einer Bank eingereicht wird und von der Bank erfolgreich
         * verarbeitet wird, beim Entgegennehmen der Antwort-Nachricht jedoch
         * eine Exception auftritt, sieht der Job aus wie "fehlgeschlagen" - 
         * dabei ist nur das Parsen der Erfolgsnachricht fehlgeschlagen */
        if (hasExceptions() || hasErrors()) {
            code=STATUS_ERR;
        } else if (hasSuccess() || hasWarnings()) {
            code=STATUS_OK;
        } else {
            code=STATUS_UNKNOWN;
        }
        
        return code;
    }
    
    /** Gibt <code>true</code> zurück, wenn keine Fehlermeldungen bzw. Exceptions
     * aufgetreten sind und wenigstens eine Successmeldung oder Warnung enthalten
     * ist */
    public boolean isOK() 
    {
        return getStatusCode()==STATUS_OK;
    }
    
    /** Gibt einen String zurück, der alle Fehlermeldungen der hier enthaltenen
        Rückgabewerte im Klartext enthält. Für evtl. enthaltene Exception wird
        die entsprechende Beschreibung in Kurz (siehe
        {@link org.kapott.hbci.manager.HBCIUtils#exception2StringShort(Exception)})
        benutzt.
        @return String mit allen Fehlermeldungen */
    public String getErrorString()
    {
        StringBuffer ret=new StringBuffer();
        
        if (hasExceptions()) {
            for (Iterator<Exception> i = exceptions.iterator(); i.hasNext();) {
                Exception ex = i.next();
                ret.append(HBCIUtils.exception2StringShort(ex));
                ret.append(System.getProperty("line.separator"));
            }
        }
        
        if (hasErrors()) {
            HBCIRetVal[] errList=getErrors();
            for (int i=0;i<errList.length;i++) {
                ret.append(errList[i].toString());
                ret.append(System.getProperty("line.separator"));
            }
        }
        
        return ret.toString().trim();
    }
    
    /** Gibt die Status-Informationen aller enthaltenen Exceptions und
        HBCI-Rückgabewerte als ein String zurück.
        @return String mit allen gespeicherten Status-Informationen */
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        for (Iterator<Exception> i = exceptions.iterator(); i.hasNext();) {
            Exception ex = i.next();
            ret.append(HBCIUtils.exception2StringShort(ex));
            ret.append(System.getProperty("line.separator"));
        }
        
        HBCIRetVal[] errList=getErrors();
        for (int i=0;i<errList.length;i++) {
            ret.append(errList[i].toString());
            ret.append(System.getProperty("line.separator"));
        }

        HBCIRetVal[] warnList=getWarnings();
        for (int i=0;i<warnList.length;i++) {
            ret.append(warnList[i].toString());
            ret.append(System.getProperty("line.separator"));
        }

        HBCIRetVal[] succList=getSuccess();
        for (int i=0;i<succList.length;i++) {
            ret.append(succList[i].toString());
            ret.append(System.getProperty("line.separator"));
        }
        
        return ret.toString().trim();
    }
}
