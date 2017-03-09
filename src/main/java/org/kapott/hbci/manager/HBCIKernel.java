
/*  $Id: HBCIKernel.java,v 1.1 2011/05/04 22:37:46 willuhn Exp $

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

package org.kapott.hbci.manager;

import java.util.Hashtable;
import java.util.List;

/** HBCI-Kernel für eine bestimmte HBCI-Version. Objekte dieser Klasse 
 * werden intern für die Nachrichtenerzeugung und -analyse verwendet. */
public interface HBCIKernel
{
    /** Gibt die HBCI-Versionsnummer zurück, für die dieses Kernel-Objekt 
     * Nachrichten erzeugen und analysieren kann.
     * @return HBCI-Versionsnummer */
    public String getHBCIVersion();

    /** <p>Gibt die Namen und Versionen aller von <em>HBCI4Java</em> für die
     * aktuelle HBCI-Version (siehe {@link #getHBCIVersion()}) unterstützten 
     * Lowlevel-Geschäftsvorfälle zurück. Es ist zu beachten, dass ein konkreter
     * HBCI-Zugang i.d.R. nicht alle in dieser Liste aufgeführten 
     * Geschäftsvorfälle auch tatsächlich anbietet (siehe dafür
     * {@link HBCIHandler#getSupportedLowlevelJobs()}).</p>
     * <p>Die zurückgegebene Hashtable enthält als Key jeweils einen String mit 
     * dem Bezeichner eines Lowlevel-Jobs, welcher für die Erzeugung eines
     * Lowlevel-Jobs mit {@link HBCIHandler#newLowlevelJob(String)} verwendet
     * werden kann. Der dazugehörige Wert ist ein List-Objekt (bestehend aus 
     * Strings), welches alle GV-Versionsnummern enthält, die von 
     * <em>HBCI4Java</em> für diesen GV unterstützt werden.</p>
     * @return Hashtable aller Lowlevel-Jobs, die prinzipiell vom aktuellen
     * Handler-Objekt unterstützt werden. */
    public Hashtable<String, List<String>> getAllLowlevelJobs();

    /** <p>Gibt alle für einen bestimmten Lowlevel-Job möglichen Job-Parameter-Namen
     * zurück. Der übergebene Job-Name ist einer der von <em>HBCI4Java</em>
     * unterstützten Jobnamen, die Versionsnummer muss eine der für diesen GV
     * unterstützten Versionsnummern sein (siehe {@link #getAllLowlevelJobs()}).
     * Als Ergebnis erhält man eine Liste aller Parameter-Namen, die für einen
     * Lowlevel-Job (siehe {@link HBCIHandler#newLowlevelJob(String)}) gesetzt
     * werden können (siehe 
     * {@link org.kapott.hbci.GV.HBCIJob#setParam(String, String)}).</p>
     * <p>Aus der Liste der möglichen Parameternamen ist nicht ersichtlich, 
     * welche Parameter zwingend und welche optional sind, bzw. wie oft ein
     * Parameter mindestens oder höchstens auftreten darf. Für diese Art der
     * Informationen stehen zur Zeit noch keine Methoden bereit.</p>
     * <p>Siehe dazu auch {@link HBCIHandler#getLowlevelJobParameterNames(String)}.</p>
     * @param gvname Name des Lowlevel-Jobs
     * @param version Version des Lowlevel-jobs
     * @return Liste aller Job-Parameter, die beim Erzeugen des angegebenen
     * Lowlevel-Jobs gesetzt werden können */
    public List getLowlevelJobParameterNames(String gvname,String version);

    /** <p>Gibt für einen bestimmten Lowlevel-Job die Namen aller
     * möglichen Lowlevel-Result-Properties zurück 
     * (siehe {@link org.kapott.hbci.GV_Result.HBCIJobResult#getResultData()}).
     * Der übergebene Job-Name ist einer der von <em>HBCI4Java</em>
     * unterstützten Jobnamen, die Versionsnummer muss eine der für diesen GV
     * unterstützten Versionsnummern sein (siehe {@link #getAllLowlevelJobs()}).
     * Als Ergebnis erhält man eine Liste aller Property-Namen, die in den
     * Lowlevel-Ergebnisdaten eines Jobs auftreten können.</p>
     * <p>Aus der resultierenden Liste ist nicht ersichtlich, 
     * welche Properties immer zurückgeben werden und welche optional sind, bzw. 
     * wie oft ein bestimmter Wert mindestens oder höchstens auftreten kann. 
     * Für diese Art der Informationen stehen zur Zeit noch keine Methoden 
     * bereit.</p>
     * <p>Siehe dazu auch {@link HBCIHandler#getLowlevelJobResultNames(String)}.</p>
     * @param gvname Name des Lowlevel-Jobs
     * @param version Version des Lowlevel-jobs
     * @return Liste aller Property-Namen, die in den Lowlevel-Antwortdaten
     * eines Jobs auftreten können */
    public List getLowlevelJobResultNames(String gvname,String version);

    /** <p>Gibt für einen bestimmten Lowlevel-Job die Namen aller
     * möglichen Job-Restriction-Parameter zurück 
     * (siehe auch {@link org.kapott.hbci.GV.HBCIJob#getJobRestrictions()} und
     * {@link HBCIHandler#getLowlevelJobRestrictions(String)}).
     * Der übergebene Job-Name ist einer der von <em>HBCI4Java</em>
     * unterstützten Jobnamen, die Versionsnummer muss eine der für diesen GV
     * unterstützten Versionsnummern sein (siehe {@link #getAllLowlevelJobs()}).
     * Als Ergebnis erhält man eine Liste aller Property-Namen, die in den
     * Job-Restrictions-Daten eines Jobs auftreten können.</p>
     * @param gvname Name des Lowlevel-Jobs
     * @param version Version des Lowlevel-jobs
     * @return Liste aller Property-Namen, die in den Job-Restriction-Daten
     * eines Jobs auftreten können */
    public List getLowlevelJobRestrictionNames(String gvname,String version);
}