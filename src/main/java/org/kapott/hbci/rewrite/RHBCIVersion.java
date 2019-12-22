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

package org.kapott.hbci.rewrite;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.status.HBCIMsgStatus;

/** <p>Korrektur fehlender HBCI-Versionsnummern. Einige HBCI-Server
    übermitteln in Fehlernachrichten, die "globale" Fehler in der
    Kundennachricht beschreiben, im Datenelement "HBCI-Version"
    des Nachrichtenkopfes den Wert "0". Da <em>HBCI4Java</em> an
    dieser Stelle immer den gleichen Wert wie in der gesendeten
    Nachricht erwartet, wird in diesem Fall der
    erwartete Wert eingetragen.</p>
    <p>Die Überprüfung, ob die HBCI-Versionsnummern in gesendeter und
    empfangender Nachricht übereinstimmen, kann mit dem Kernel-Parameter
    <code>client.errors.ignoreMsgCheckErrors</code> abgeschaltet
    werden (siehe dazu Beschreibung in 
    {@link org.kapott.hbci.manager.HBCIUtils})</p>*/
public class RHBCIVersion 
    extends Rewrite
{
    public String incomingCrypted(String st,MsgGen gen)
    {
        int idx=st.indexOf("+");
        if (idx!=-1) { // + after SegHead found
            idx=st.indexOf("+",idx+1);
            if (idx!=-1) { // + after msgsize found
                int idx2=st.indexOf("+",idx+1);
                if (idx2!=-1) { // + after hbciversion found
                    HBCIMsgStatus msgStatus=(HBCIMsgStatus)getData("msgStatus");
                    String        msgName=(String)getData("msgName");
                    String        version=st.substring(idx+1,idx2);
                    String        origVersion=msgStatus.getData().getProperty("orig_"+msgName+".MsgHead.hbciversion");
                    
                    if (version.length()==0 || version.equals("0")) {
                        HBCIUtils.log("received HBCI version of message ('"+version+"') is incorrect - replacing it with "+origVersion,
                                      HBCIUtils.LOG_WARN);
                        st=new StringBuffer(st).replace(idx+1,idx2,origVersion).toString();
                    }
                }
            }
        }
        return st;
    }
}
