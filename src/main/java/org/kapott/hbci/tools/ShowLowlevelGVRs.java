
/*  $Id: ShowLowlevelGVRs.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** <p>Dieses Tool dient zum Anzeigen der Struktur von HBCI-Job-Ergebnisdaten im
    Rohformat. Diese Struktur wird benötigt, wenn Job-Ergebnisdaten nicht über
    die Methoden und Felder der entsprechenden Highlevel-Klassen ausgewertet
    werden sollen (Klassen <code>org.kapott.hbci.GV_Result.GVR*</code>), sondern
    wenn die Daten benutzt werden, die durch
    {@link org.kapott.hbci.GV_Result.HBCIJobResult#getResultData()}
    zurückgegeben werden.</p>
    <p>In diesem Property-Objekt werden die Job-Ergebnisdaten nämlich nach der <em>HBCI4Java</em>-internen
    Struktur benannt. Um nun die Bezeichnungen für die einzelnen Datenelemente zu erfahren, 
    kann dieses Tool benutzt werden.</p>
    <p>Der Aufruf erfolgt durch
    <pre>java org.kapott.hbci.tools.ShowLowlevelGVRs [hbciversion]</pre>Ist keine
    <code>hbciversion</code> angegeben, so wird diese über STDIN erfragt.</p>
    <p>Das Tool gibt eine baumartige Struktur aus, welche die Lowlevelnamen der Geschäftsvorfälle
    (plus dem zusätzlichen Suffix "<code>Res</code>") sowie die Bezeichnungen für die
    dazugehörigen Datenfelder enthält. Eine Erklärung der Ausgaben im Detail ist in der Dokumentation
    zum Tool {@link ShowLowlevelGVs} enthalten.</p>
    <p>Innerhalb einer Anwendung kann mit der Methode
    {@link org.kapott.hbci.manager.HBCIHandler#getSupportedLowlevelJobs()}
    eine Liste aller unterstützten Lowlevel-Jobs in Erfahrung gebracht werden. Zusätzlich gibt diese
    Methode zu jedem Jobnamen die Versionsnummer zurück, welche für diesen Job von <em>HBCI4Java</em> benutzt
    werden wird (das hängt von der aktuellen HBCI-Version und dem benutzten Passport ab, kann von
    außen also nicht direkt beeinflusst werden). In der Ausgabe dieses Tool kann nun nach einem
    bestimmten Lowlevelnamen eines Jobs und der von <em>HBCI4Java</em> dafür verwendeten Versionsnummer gesucht werden.
    Ist der entsprechende Eintrag gefunden, so hat man eine Übersicht über alle möglichen
    Job-Ergebnisdaten und wie oft die jeweiligen Datenelemente in einem Antwortsegment auftreten
    können. Die gleiche Übersicht erhält man übrigens, wenn man innerhalb der Anwendung die Methode
    {@link org.kapott.hbci.manager.HBCIHandler#getLowlevelJobResultNames(String)}
    aufruft, allerdings fehlen in der Ausgabe dieser Methode die Informationen über die möglichen
    Häufigkeiten der einzelnen Datenelemente, dafür wird hier automatisch die richtige Versionsnummer
    des Jobs ausgewählt.</p>*/
public class ShowLowlevelGVRs
    extends AbstractShowLowlevelData
{
    public static void main(String[] args)
        throws Exception
    {
        HBCIUtils.init(null,new HBCICallbackConsole());
        
        String hbciversion;
        if (args.length>=1) {
            hbciversion=args[0];
        } else {
            System.out.print("hbciversion: ");
            System.out.flush();
            hbciversion=new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        
        HBCIKernelImpl kernel=new HBCIKernelImpl(null,hbciversion);
        MsgGen         msggen=kernel.getMsgGen();
        Document       syntax=msggen.getSyntax();
        
        Element  gvlist=syntax.getElementById("GVRes");
        NodeList gvnodes=gvlist.getChildNodes();
        int      len=gvnodes.getLength();
        
        for (int i=0;i<len;i++) {
            Node gvrefnode=gvnodes.item(i);
            
            if (gvrefnode.getNodeType()==Node.ELEMENT_NODE) {
                String gvname=((Element)gvrefnode).getAttribute("type");
                showData(gvname,syntax);
            }
        }
    }
}
