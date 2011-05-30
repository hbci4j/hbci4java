/*  $Id: ChallengeInfo.java,v 1.9 2011/05/30 12:47:56 willuhn Exp $

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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.datatypes.SyntaxDE;
import org.kapott.hbci.datatypes.factory.SyntaxDEFactory;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Diese Klasse ermittelt die noetigen HKTAN-Challenge-Parameter fuer einen
 * Geschaeftsvorfall
 */
public class ChallengeInfo
{
  /**
   * Versionskennung fuer HHD 1.2
   */
  public final static String VERSION_HHD_1_2 = "hhd12";
  
  /**
   * Versionskennung fuer HHD 1.3
   */
  public final static String VERSION_HHD_1_3 = "hhd13";

  /**
   * Versionskennung fuer HHD 1.4
   */
  public final static String VERSION_HHD_1_4 = "hhd14";

  /**
   * Das Singleton.
   */
  private static ChallengeInfo singleton = null;
    private Map<String,Job> data = null; // Die Parameter-Daten aus der XML-Datei.

  /**
   * Erzeugt ein neues Challenge-Info-Objekt.
   * @return das Challenge-Info-Objekt.
   */
  public static synchronized ChallengeInfo getInstance()
  {
    if (singleton == null)
      singleton = new ChallengeInfo();
    return singleton;
  }
  
  /**
   * ct.
   */
  private ChallengeInfo()
  {
    HBCIUtils.log("initializing challenge info engine",HBCIUtils.LOG_DEBUG);

    
    ////////////////////////////////////////////////////////////////////////////
    // XML-Datei lesen
    String xmlpath = HBCIUtils.getParam("kernel.kernel.challengedatapath","");
    InputStream dataStream = null;

    String filename = xmlpath+"challengedata.xml";
    dataStream = ChallengeInfo.class.getClassLoader().getResourceAsStream(filename);
    if (dataStream == null)
      throw new InvalidUserDataException("*** can not load challenge information from "+filename);

    // mit den so gefundenen xml-daten ein xml-dokument bauen
    Document doc = null;
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setIgnoringComments(true);
      dbf.setValidating(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.parse(dataStream);
      dataStream.close();
    }
    catch (Exception e)
    {
      throw new HBCI_Exception("*** can not load challengedata from file "+filename,e);
    }
    //
    ////////////////////////////////////////////////////////////////////////////

    data = new HashMap<String,Job>();

    ////////////////////////////////////////////////////////////////////////////
    // Parsen
    NodeList jobs = doc.getElementsByTagName("job");
    int size      = jobs.getLength();
    
    for (int i=0;i<size;++i)
    {
      Element job = (Element) jobs.item(i);
      String code = job.getAttribute("code");
      data.put(code,new Job(job));
    }
    //
    ////////////////////////////////////////////////////////////////////////////

    HBCIUtils.log("challenge information loaded",HBCIUtils.LOG_DEBUG);
  }
  
  /**
   * Ermittelt die zu verwendende HHD-Version aus den BPD-Informationen des TAN-Verfahrens.
   * @param secmech die BPD-Informationen zum TAN-Verfahren.
   * @return die HHD-Version.
   */
  private String getVersion(Properties secmech)
  {
    // Das ist die "Technische Kennung"
    // Siehe "Belegungsrichtlinien TANve1.4  mit Erratum 1-3 final version vom 2010-11-12.pdf"
    // Der Name ist standardisiert, wenn er mit "HHD1...." beginnt, ist
    // das die HHD-Version
    String id = secmech.getProperty("id","");
    if (id.startsWith("HHD1.4")) return VERSION_HHD_1_4;
    if (id.startsWith("HHD1.3")) return VERSION_HHD_1_3;
    
    // Fallback 1. Wir schauen noch in "ZKA-Version bei HKTAN"
    String version = secmech.getProperty("zkamethod_version");
    if (version != null && version.length() > 0)
    {
      if (version.startsWith("1.4")) return VERSION_HHD_1_4;
      if (version.startsWith("1.3")) return VERSION_HHD_1_3;
    }
    
    // Fallback 2. Wir checken noch die HITAN/HKTAN-Version
    // Bei HKTAN5 kann es HHD 1.3 oder 1.4 sein, bei HKTAN4 bleibt eigentlich nur noch 1.3
    // Ich weiss nicht, ob Fallback 2 ueberhaupt notwendig ist. Denn angeblich
    // ist zkamethod_version seit HHD 1.3.1 Pflicht (siehe
    // FinTS_3.0_Security_Sicherheitsverfahren_PINTAN_Rel_20101027_final_version.pdf,
    // Data dictionary "Version ZKA-TAN-Verfahren"
    String segversion = secmech.getProperty("segversion");
    if (segversion != null && segversion.length() > 0)
    {
      int i = Integer.parseInt(segversion);
      if (i == 5)
        return VERSION_HHD_1_4; // Genau wissen wir es nicht, aber HHD 1.4 ist wahrscheinlich
      if (i == 4)
        return VERSION_HHD_1_3; // 1.4 ist in HKTAN4 noch nicht erlaubt, damit bleibt eigentlich nur 1.3
    }
    
    // Default:
    return VERSION_HHD_1_2;
  }
  
  /**
   * Liefert die Challenge-Daten fuer einen Geschaeftsvorfall.
   * @param code die Segmentkennung des Geschaeftsvorfalls.
   * @return die Challenge-Daten.
   */
  public Job getData(String code)
  {
    return data.get(code);
  }

  /**
   * Uebernimmt die Challenge-Parameter in den HKTAN-Geschaeftsvorfall.
   * @param task der Job, zu dem die Challenge-Parameter ermittelt werden sollen.
   * @param hktan der HKTAN-Geschaeftsvorfall, in dem die Parameter gesetzt werden sollen.
   * @param secmech die BPD-Informationen zum TAN-Verfahren.
   */
  public void applyParams(HBCIJobImpl task, HBCIJob hktan, Properties secmech)
  {
    String code = task.getHBCICode(); // Code des Geschaeftsvorfalls

    // Job-Parameter holen
    Job job = this.getData(code);
    
    // Den Geschaeftsvorfall kennen wir nicht. Dann brauchen wir
    // auch keine Challenge-Parameter setzen
    if (job == null)
    {
      HBCIUtils.log("have no challenge data for " + code + ", will not apply challenge params", HBCIUtils.LOG_INFO);
      return;
    }
    
    String version = this.getVersion(secmech); // HHD-Version
    HBCIUtils.log("using hhd version " + version, HBCIUtils.LOG_DEBUG2);

    // Parameter fuer die passende HHD-Version holen
    HhdVersion hhd = job.getVersion(version);
    
    // Wir haben keine Parameter fuer diese HHD-Version
    if (hhd == null)
    {
      HBCIUtils.log("have no challenge data for " + code + " in " + version + ", will not apply challenge params", HBCIUtils.LOG_INFO);
      return;
    }


    // Schritt 1: Challenge-Klasse uebernehmen
    String klass = hhd.getKlass();
    HBCIUtils.log("using challenge klass " + klass, HBCIUtils.LOG_DEBUG2);
    hktan.setParam("challengeklass", klass);

    
    // Schritt 2: Challenge-Parameter uebernehmen
    List<Param> params = hhd.getParams();
    for (int i=0;i<params.size();++i)
    {
      int num = i+1; // Die Job-Parameter beginnen bei 1
      Param param = params.get(i);
      
      // Checken, ob der Parameter angewendet werden soll.
      if (!param.isComplied(secmech))
      {
        HBCIUtils.log("skipping challenge parameter " + num + " (" + param.path + "), condition " + param.conditionName + "=" + param.conditionValue + " not complied",HBCIUtils.LOG_DEBUG2);
        continue;
      }
      
      // Parameter uebernehmen. Aber nur wenn er auch einen Wert hat.
      // Seit HHD 1.4 duerfen Parameter mittendrin optional sein, sie
      // werden dann freigelassen
      String value = param.getValue(task);
      if (value == null || value.length() == 0)
      {
        HBCIUtils.log("challenge parameter " + num + " (" + param.path + ") is empty",HBCIUtils.LOG_DEBUG2);
        continue;
      }
      
      HBCIUtils.log("adding challenge parameter " + num + " " + param.path + "=" + value, HBCIUtils.LOG_DEBUG2);
      hktan.setParam("ChallengeKlassParam" + num, value);
    }
  }
  
  /**
   * Eine Bean fuer die Parameter-Saetze eines Geschaeftsvorfalles fuer die HHD-Versionen.
   */
  public static class Job
  {
    /**
     * Die Parameter fuer die jeweilige HHD-Version.
     */
    private Map<String,HhdVersion> versions = new HashMap<String,HhdVersion>();
    
    /**
     * ct.
     * @param job der XML-Knoten, in dem die Daten stehen.
     */
    private Job(Element job)
    {
      NodeList specs = job.getElementsByTagName("challengeinfo");
      int size       = specs.getLength();
      
      for (int i=0;i<size;++i)
      {
        Element spec    = (Element) specs.item(i);
        String  version = spec.getAttribute("spec");
        
        this.versions.put(version,new HhdVersion(spec));
      }
    }
    
    /**
     * Liefert die Challenge-Parameter fuer die angegeben HHD-Version.
     * @param version die HHD-Version.
     * @return die Challenge-Parameter fuer die HHD-Version.
     */
    public HhdVersion getVersion(String version)
    {
      return this.versions.get(version);
    }
  }
  
  /**
   * Eine Bean fuer den Parameter-Satz eines Geschaeftvorfalles innerhalb einer HHD-Version.
   */
  public static class HhdVersion
  {
    /**
     * Die Challenge-Klasse.
     */
    private String klass = null;
    
    /**
     * Liste der Challenge-Parameter.
     */
    private List<Param> params = new ArrayList<Param>();
    
    /**
     * ct.
     * @param spec der XML-Knoten mit den Daten.
     */
    private HhdVersion(Element spec)
    {
      this.klass = ((Element)spec.getElementsByTagName("klass").item(0)).getFirstChild().getNodeValue();

      NodeList list = spec.getElementsByTagName("param");
      int size      = list.getLength();
      for (int i=0;i<size;++i)
      {
        Element param = (Element) list.item(i);
        this.params.add(new Param(param));
      }
    }
    
    /**
     * Liefert die Challenge-Klasse.
     * @return die Challenge-Klasse.
     */
    public String getKlass()
    {
      return this.klass;
    }
    
    /**
     * Liefert die Challenge-Parameter fuer den Geschaeftsvorfall in dieser HHD-Version.
     * @return die Challenge-Parameter fuer den Geschaeftsvorfall in dieser HHD-Version.
     */
    public List<Param> getParams()
    {
      return this.params;
    }
  }
  
  /**
   * Eine Bean fuer einen einzelnen Challenge-Parameter.
   */
  public static class Param
  {
    /**
     * Der Typ des Parameters.
     */
    private String type = null;
    
    /**
     * Der Pfad in den Geschaeftsvorfall-Parametern, unter dem der Wert steht.
     */
    private String path = null;
    
    /**
     * Optional: Der Name einer Bedingung, die erfuellt sein muss, damit
     * der Parameter verwendet wird. Konkret ist hier der Name eines Property
     * aus secmechInfo gemeint. Also ein BPD-Parameter.
     */
    private String conditionName = null;
    
    /**
     * Optional: Der Wert, den der BPD-Parameter haben muss, damit der Challenge-Parameter
     * verwendet wird.
     */
    private String conditionValue = null;
    
    /**
     * ct.
     * @param param der XML-Knoten mit den Daten.
     */
    private Param(Element param)
    {
      Node content = param.getFirstChild();
      this.path           = content != null ? content.getNodeValue() : null;
      this.type           = param.getAttribute("type");
      this.conditionName  = param.getAttribute("condition-name");
      this.conditionValue = param.getAttribute("condition-value");
    }
    
    /**
     * Liefert true, wenn entweder keine Bedingung angegeben ist oder
     * die Bedingung erfuellt ist und der Parameter verwendet werden kann.
     * @param secmech die BPD-Informationen zum TAN-Verfahren.
     * @return true, wenn der Parameter verwendet werden kann.
     */
    public boolean isComplied(Properties secmech)
    {
      if (this.conditionName == null || this.conditionName.length() == 0)
        return true;
      
      // Wir haben eine Bedingung. Mal schauen, ob sie erfuellt ist.
      String value = secmech.getProperty(this.conditionName,"");
      return value.equals(this.conditionValue);
    }
    
    /**
     * Liefert den Typ des Parameters.
     * @return der Typ des Parameters.
     */
    public String getType()
    {
      return this.type;
    }
    
    /**
     * Liefert den Pfad zum Wert.
     * @return der Pfad zum Wert.
     */
    public String getPath()
    {
      return this.path;
    }
    
    /**
     * Liefert den Wert des Parameters.
     * @param job der Geschaeftsvorfall.
     * @return der Wert des Parameters.
     */
    private String getValue(HBCIJobImpl job)
    {
      // Leerer Parameter
      if (this.path == null || this.path.length() == 0)
        return null;
      
      String value = job.getChallengeParam(this.path);
      
      // Wert im passenden Format zurueckliefern
      return format(value);
    }
    
    /**
     * Formatiert den Text abhaengig vom Typ.
     * Wenn kein Typ angegeben ist, wird der Wert unformatiert zurueckgegeben.
     * @param value der zu formatierende Wert.
     * @return der formatierte Wert.
     */
    public String format(String value)
    {
      // Bei leeren Werten lieferen wir generell NULL.
      // Die Parameter werden dann uebersprungen.
      if (value == null || value.trim().length() == 0)
        return null;

      // Wenn kein Typ angegeben ist, gibts auch nichts zu formatieren.
      // Nein, wir duerfen NICHT SyntaxAN verwenden. Denn die Parameter
      // in ChallengeKlassParams#param[1-9] sind ja bereits als Type AN
      // deklariert. Wuerden wir hier SyntaxAN verwenden, wuerden die
      // Werte dann doppelt codiert werden (das zweite Codieren macht ja
      // anschliessend HBCI4Java intern beim Zusammenbauen des Segments).
      // Was zum Beispiel dazu fuehren wuerde, dass ein Sonderzeichen wie
      // "+" oder "?" doppelt escaped werden wuerde.
      if (this.type == null || this.type.trim().length() == 0)
        return value;

      SyntaxDEFactory factory = SyntaxDEFactory.getInstance();
      SyntaxDE syntax = null;
      try
      {
        syntax = factory.createSyntaxDE(this.type,this.path,value,0,0);
        return syntax.toString(0);
      }
      finally
      {
        // Objekt wieder freigeben
        if (syntax != null)
          factory.unuseObject(syntax,this.type);
      }
    }
  }
}
