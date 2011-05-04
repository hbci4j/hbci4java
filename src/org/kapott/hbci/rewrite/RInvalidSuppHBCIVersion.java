
/*  $Id: RInvalidSuppHBCIVersion.java,v 1.1 2011/05/04 22:37:57 willuhn Exp $

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

package org.kapott.hbci.rewrite;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.SyntaxElement;
import org.kapott.hbci.protocol.factory.MSGFactory;

public class RInvalidSuppHBCIVersion
    extends Rewrite
{
    // TODO: den rewriter umschreiben, so dass er nur string-operationen
    // benutzt, weil nicht sichergestellt werden kann, dass die eingehende
    // nachricht hier tatsächlich schon geparst werden kann
    public String incomingClearText(String st,MsgGen gen) 
    {
        // empfangene Nachricht parsen, dabei die validvalues-Überprüfung weglassen
        String myMsgName=(String)getData("msgName")+"Res";
        MSG    msg=MSGFactory.getInstance().createMSG(myMsgName,st,st.length(),
                gen,
                MSG.DONT_CHECK_SEQ,MSG.DONT_CHECK_VALIDS);
        
        // in einer Schleife durch alle SuppVersions-Datensätze laufen
        for (int i=0;;i++) {
            String        elemName=HBCIUtilsInternal.withCounter(myMsgName+".BPD.BPA.SuppVersions.version",i);
            SyntaxElement elem=msg.getElement(elemName);

            if (elem==null) {
                break;
            } 
            
            // Versionsnummer extrahieren
            String version=elem.toString();
            if (version.equals("2")) { // "2" ist ungültige Versionsnummer
                HBCIUtils.log("there is an invalid hbci version number ('2') in this BPD - replacing it with '210'",HBCIUtils.LOG_WARN);

                // versionsnummer "2" im string durch "210" ersetzen
                int startpos=elem.getPosInMsg()+1;  // +1 wegen überspringen des pre-delimiters
                st=new StringBuffer(st).replace(startpos,startpos+1,"210").toString();
                HBCIUtils.log("new message after replacing: "+st,HBCIUtils.LOG_DEBUG);
                break;
            }
        }
        
        MSGFactory.getInstance().unuseObject(msg);
        return st;
    }
}
