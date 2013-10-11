
/*  $Id: HBCIRetVal.java,v 1.1 2011/05/04 22:38:02 willuhn Exp $

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtilsInternal;

/** <p>Repräsentation eines HBCI-Statuscodes. Objekte dieser Klasse
    stellen einen einzigen HBCI-Returncode dar, welcher aus einer
    Antwortnachricht von der Bank extrahiert wurde.
    </p><p>
    Zu den hier bereitgestellten Informationen zählen neben den eigentlichen
    Status-Daten (Status-Code, Textmeldung) auch eine numerische Darstellung
    <em>des</em> Teiles der ursprünglich gesendeten Nachricht, auf den sich
    diese Statusmeldung bezieht. Sofern das möglich ist, wird diese numerische
    Darstellung zusätzlich in den Lowlevel-Namen des betreffenden Nachrichtenteils
    umgewandelt, so dass für den Anwender eine bessere Lokalisierung des
    Problems möglich ist.</p> */
public final class HBCIRetVal
    implements Serializable
{
    /** <p>HBCI-Fehlercode. Diese Codes bestehen immer aus vier Ziffern. Die erste
        Ziffer kennzeichnet dabei die Art:</p>
        <ul>
          <li><p>0 - Erfolgsmeldung</p></li>
          <li><p>3 - Warnung</p></li>
          <li><p>9 - Fehlermeldung</p></li>
        </ul> */
    public String   code;
    /** Segmentnummer in der gesendeten Nachricht, auf das sich dieser 
        Rückgabewert bezieht. Falls es sich um einen globalen Rückgabewert
        handelt (d.h. einen, der sich auf die komplette Nachricht bezieht),
        so ist dieser Wert <code>null</code>*/
    public String   segref;
    /** Nummer des Datenelementes oder der Datenelementgruppe, auf das sich 
        dieser Rückgabewert bezieht. Diese Information ist nicht in jedem
        Fall vorhanden (z.B. wenn es sich um einen globalen Fehlercode handelt
        oder wenn sich der Rückgabewert auf ein komplettes Segment bezieht).
        In einem solchen Fall ist dieser Wert <code>null</code>.*/
    public String   deref;
    /** Beschreibender Text. Dieser Text wird vom HBCI-Server der Bank generiert. */
    public String   text;
    /** Optionale Parameter, die im Zusammenhang mit <code>text</code> zu interpretieren sind. */
    public String[] params;
    /** Lowlevel-Name des Nachrichtenelementes, auf das sich dieser Rückgabewert bezieht.
        Dieser Name kann nicht in jedem Fall bestimmt werden, der Wert dieses Feldes
        kann dann auch <code>null</code> sein. */
    public String   element;
    
    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public HBCIRetVal(String segref,String deref,String element,String code,String text,String[] params)
    {
        if (params==null)
            params=new String[0];
        
        this.code=code;
        this.segref=segref;
        this.deref=deref;
        this.text=text;
        this.params=params;
        this.element=element;
    }

    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public HBCIRetVal(Properties result,String header)
    {
        this(result,header,null);
    }

    /** Wird von der <em>HBCI4Java</em>-Dialog-Engine aufgerufen */
    public HBCIRetVal(Properties result,String header,String segref)
    {
        this.segref=segref;

        code=result.getProperty(header+".code");
        if (code==null)
            throw new HBCI_Exception("*** no valid error");
        deref=result.getProperty(header+".ref");
        text=result.getProperty(header+".text");

        element=null;
        if (segref!=null) {
            String path=result.getProperty(segref+((deref!=null)?":"+deref:""));
            String value=(path!=null)?result.getProperty("orig_"+path):null;
            element=path+((value!=null)?("="+value):"");
        }
                
        ArrayList<String> a=new ArrayList<String>();
        int i=0;
        String parm;

        while ((parm=result.getProperty(HBCIUtilsInternal.withCounter(header+".parm",i)))!=null) {
            a.add(parm);
            i++;
        }

        params=new String[0];
        if (a.size()!=0)
            params=(a.toArray(params));
    }

    /** Gibt diesen Rückgabewert in einer lesbaren Darstellung zurück.
        @return einen String, der alle Informationen dieses Objektes kurz
                zusammenfasst. */
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        ret.append(code).append(":").append(text);

        for (int i=0;i<params.length;i++) {
            ret.append(" p:").append(params[i]);
        }

        if (segref!=null) {
            ret.append(" (");
            
            ret.append(segref);
            if (deref!=null) {
                ret.append(":");
                ret.append(deref);
            }
            
            if (element!=null) {
                ret.append(": ");
                ret.append(element);
            }

            ret.append(")");
        }

        return ret.toString().trim();
    }
    
    /** Gibt zurück, ob dieser Rückgabewert eine Erfolgsmeldung vom 
        HBCI-Server darstellt */
    public boolean isSuccess()
    {
        return (code!=null) && (code.charAt(0)=='0');
    }
    
    /** Gibt zurück, ob dieser Rückgabewert eine Warnung vom 
        HBCI-Server darstellt */
    public boolean isWarning()
    {
        return (code!=null) && (code.charAt(0)=='3');
    }
    
    /** Gibt zurück, ob dieser Rückgabewert eine HBCI-Fehlermeldung darstellt */
    public boolean isError()
    {
        return (code!=null) && (code.charAt(0)=='9');
    }
    
    public boolean equals(Object o)
    {
        boolean ret;
        
        if (o instanceof HBCIRetVal) {
            boolean equal=true;
            HBCIRetVal other=(HBCIRetVal)o;
            
            equal&=(code  ==null && other.code  ==null) || (code  !=null && other.code  !=null && code.equals(other.code));
            equal&=(text  ==null && other.text  ==null) || (text  !=null && other.text  !=null && text.equals(other.text));
            equal&=(segref==null && other.segref==null) || (segref!=null && other.segref!=null && segref.equals(other.segref));
            equal&=(deref ==null && other.deref ==null) || (deref !=null && other.deref !=null && deref.equals(other.deref));
            
            ret=equal;
        } else {
            ret=super.equals(o);
        }
        return ret;
    }
}
