
/*  $Id: RSecTypeTAN.java,v 1.1 2011/05/04 22:37:57 willuhn Exp $

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

import java.util.Iterator;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.MultipleSyntaxElements;
import org.kapott.hbci.protocol.SyntaxElement;
import org.kapott.hbci.protocol.factory.MSGFactory;

/** <p>Rewriter-Modul für falsche Informationen über TAN-Verfahren. Einige Banken
 * mit HBCI+ (HBCI-PIN/TAN) -Unterstützung stellen fälschlicherweise in die BPD 
 * die Information ein, dass das Sicherheitsverfahren "TAN" unterstützt wird.
 * "TAN" ist aber kein gültiger Code für Sicherheitsmechanismen, so dass dieses
 * Rewriter-Modul die "TAN"-Information aus den BPD entfernt.</p>
 * <p>Ist dieses Modul aktiv, so muss auch das Modul "<code>Olly</code>" aktiv
 * sein, weil einige hier vorgenommene Änderungen wiederum fehlerhafte Nachrichten
 * erzeugen, die aber durch "<code>Olly</code>" wieder korrigiert werden.</p> */
public class RSecTypeTAN 
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
                MSG.CHECK_SEQ,MSG.DONT_CHECK_VALIDS);
        
        // in einer Schleife durch alle SuppSecMethods-Datensätze laufen
        for (int i=0;;i++) {
            String        elemBaseName=HBCIUtilsInternal.withCounter(myMsgName+".BPD.SecMethod.SuppSecMethods",i);
            SyntaxElement elem=msg.getElement(elemBaseName+".method");

            if (elem==null) {
                break;
            } 
            
            // Methodenbezeichner extrahieren
            String method=elem.toString();
            if (method.equals("TAN")) { // "TAN" ist ungültiger Bezeichner
                HBCIUtils.log("there is an invalid sec type (TAN) in this BPD - removing it",HBCIUtils.LOG_WARN);

                // Elternelement finden (Segment "SecMethods")
                SyntaxElement parent=elem.getParent().getParent().getParent().getParent();
                String        parentPath=parent.getPath();
                int           number=0;
                
                // durch alle Elemente dieses Segmentes laufen, bis die Multiple-DEG
                // mit den unterstützten SecMethods gefunden wurde
                for (Iterator<MultipleSyntaxElements> it=parent.getChildContainers().iterator();it.hasNext();) {
                    MultipleSyntaxElements childContainer= it.next();
                    if (childContainer.getPath().equals(parentPath+".SuppSecMethods")) {
                        // die Anzahl der eingestellten unterstützten SecMethods herausholen
                        number=childContainer.getElements().size();
                        break;
                    }
                }
                
                int startpos;
                int endpos;
                
                /* wenn mehr als eine SecMethod im Segment stand, dann braucht nur
                 * das eine der multiplen DEGs entfernt werden. Wenn aber nur die eine
                 * fehlerhafte Info enthalten war, dann muss das gesamte Segment
                 * entfernt werden, weil ein SecMethods-Segment ohne tatsächliche Daten
                 * über unterstützte SecMethods ungültig ist. */
                
                if (number>1) { // nur das eine fehlerhafte TAN:1 löschen
                    startpos=elem.getPosInMsg();
                    endpos=startpos
                        +1
                        +elem.toString(0).length()
                        +1
                        +msg.getElement(elemBaseName+".version").toString(0).length();
                } else { // komplettes segment "SecMethod" löschen
                    startpos=parent.getPosInMsg()+1;
                    endpos=startpos+parent.toString(0).length();
                    /* der Fehler, der hier gemacht wird (nachfolgende Segment-
                     * Sequenznummern sind falsch), wird durch ein nachgeschaltetes
                     * Olly-Modul korrigiert */
                }
                
                st=new StringBuffer(st).delete(startpos,endpos).toString();
                HBCIUtils.log("new message after removing: "+st,HBCIUtils.LOG_DEBUG);
                break;
            }
        }
        
        MSGFactory.getInstance().unuseObject(msg);
        return st;
    }
}
