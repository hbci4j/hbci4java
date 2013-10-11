
/*  $Id: HBCIExecThreadedStatus.java,v 1.1 2011/05/04 22:38:02 willuhn Exp $

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

import java.util.Hashtable;

/** <p>Wird für Status-Informationen bei Verwendung des threaded-callback-Mechanismus'
 * benötigt. Für den threaded-callback-Mechanismus werden die Methoden
 * {@link org.kapott.hbci.manager.HBCIHandler#executeThreaded()} und
 * {@link org.kapott.hbci.manager.HBCIHandler#continueThreaded(String)} 
 * verwendet, die jeweils ein Objekt von <code>HBCIExecThreadedStatus</code>
 * zurückgeben.</p>
 * <p>Objekte dieser Klasse geben zunächst Auskunft darüber, warum
 * <code>executeThreaded()</code> bzw. <code>continueThreaded()</code>
 * terminiert sind. Ursache kann zum einen sein, dass Callback-Daten benötigt
 * werden - in diesem Fall enthält das <code>HBCIExecThreadedStatus</code>-Objekt
 * die Informationen zum aufgetretenen Callback. Andernfalls zeigt das
 * <code>HBCIExecThreadedStatus</code>-Objekt an, dass der HBCI-Dialog beendet
 * ist - in diesem Fall sind die HBCI-Dialog-Status-Informationen als
 * {@link HBCIExecStatus}-Objekt enthalten (analog zum Rückgabewert von
 * {@link org.kapott.hbci.manager.HBCIHandler#execute()}.</p> */ 
public class HBCIExecThreadedStatus 
{
    private Hashtable<String,Object>      callbackData;
    private HBCIExecStatus execStatus;
    
    /** Callback-Daten in diesem Objekt speichern. Wird nur vom HBCI-Kernel
     * aufgerufen. */
    public void setCallbackData(Hashtable<String,Object> callbackData)
    {
        this.callbackData=callbackData;
    }
    
    /** Callback-Daten auslesen. Wenn {@link #isCallback()} <code>true</code>
     * ist, bedeutet das, dass ein Callback aufgetreten ist, der behandelt
     * werden muss. Die zurückgegebene <code>Hashtable</code> enthält folgende
     * Werte:
     * <ul>
     * <li>"<code>method</code>": ist im Moment immer "<code>callback</code>"</li>
     * <li>"<code>passport</code>": enthält das Passport-Objekt, dessen HBCI-Dialog
     * Callback-Daten benötigt.</li>
     * <li>"<code>reason</code>": enthält den Callback-Reason als 
     * <code>Integer</code>-Objekt.</li>
     * <li>"<code>msg</code>": enthält die Callback-Message.</li>
     * <li>"<code>dataType</code>": enthält den erwarteten Datentyp der Antwort
     * als <code>Integer</code>-Objekt.</li>
     * <li>"<code>retData</code>": enthält das <code>retData</code>-Objekt
     * (<code>StringBuffer</code>), in welches die Callback-Daten hineingeschrieben
     * werden müssen.</li>
     * </ul> */
    public Hashtable<String,Object> getCallbackData()
    {
        return this.callbackData;
    }

    /** Speichern des Dialog-Status. Wird nur vom HBCI-Kernel aufgerufen.*/
    public void setExecStatus(HBCIExecStatus status)
    {
        this.execStatus=status;
    }
    
    /** Auslesen des HBCI-Dialog-Status. Falls die Methode {@link #isFinished()}
     * <code>true</code> zurückgibt, bedeutet das, dass der HBCI-Dialog beendet
     * ist. In diesem Fall kann mit <code>getExecStatus</code> das 
     * {@link HBCIExecStatus}-Objekt ausgelesen werden, welches den eigentlichen
     * Status des HBCI-Dialoges anzeigt (analog zu 
     * {@link org.kapott.hbci.manager.HBCIHandler#execute()}). */
    public HBCIExecStatus getExecStatus()
    {
        return this.execStatus;
    }
    
    /** Zeigt an, ob der HBCI-Dialog beendet ist (<code>true</code>). */
    public boolean isFinished()
    {
        return execStatus!=null;
    }
    
    /** Zeigt an, ob Callback-Daten benötigt werden (<code>true</code>), oder
     * ob der HBCI-Dialog beendet ist (<code>false</code>). */
    public boolean isCallback()
    {
        return callbackData!=null;
    }
    
    /** Gibt einen String mit allen gespeicherten Informationen zurück. */
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        String       linesep=System.getProperty("line.separator");
        
        ret.append("isCallback: "+isCallback()+linesep);
        if (isCallback()) {
            ret.append("  method: "+callbackData.get("method")+linesep);
            ret.append("  reason: "+callbackData.get("reason")+linesep);
            ret.append("  msg: "+callbackData.get("msg")+linesep);
        }
        ret.append("isFinished: "+isFinished()+linesep);
        if (isFinished()) {
            ret.append(getExecStatus().toString());
        }
        
        return ret.toString();
    }
}
