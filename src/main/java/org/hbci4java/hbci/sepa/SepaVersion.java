/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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

package org.hbci4java.hbci.sepa;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hbci4java.hbci.GV.generators.ISEPAGenerator;
import org.hbci4java.hbci.GV.parsers.ISEPAParser;
import org.hbci4java.hbci.comm.Comm;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Basis-Klasse fuer das Parsen und Vergleichen von SEPA Versionen (PAIN und CAMT).
 */
public class SepaVersion implements Comparable<SepaVersion>
{
    private final static Pattern PATTERN = Pattern.compile("([a-z]{2,8})\\.(\\d\\d\\d)\\.(\\d\\d\\d)\\.(\\d\\d)");
    private final static Map<Type,List<SepaVersion>> knownVersions = new HashMap<Type,List<SepaVersion>>();
    
    private final static String DF_MAJOR = "000";
    private final static String DF_MINOR = "00";
    
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_001_001_02 = new SepaVersion(SupportType.GENERATE,1,"urn:sepade:xsd:pain.001.001.02",                "pain.001.001.02.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_001_002_02 = new SepaVersion(SupportType.GENERATE,2,"urn:swift:xsd:$pain.001.002.02",                "pain.001.002.02.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_001_002_03 = new SepaVersion(SupportType.GENERATE,3,"urn:iso:std:iso:20022:tech:xsd:pain.001.002.03","pain.001.002.03.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_001_003_03 = new SepaVersion(SupportType.GENERATE,4,"urn:iso:std:iso:20022:tech:xsd:pain.001.003.03","pain.001.003.03.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_001_001_03 = new SepaVersion(SupportType.GENERATE,5,"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03","pain.001.001.03.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_001_001_09 = new SepaVersion(SupportType.GENERATE,9,"urn:iso:std:iso:20022:tech:xsd:pain.001.001.09","pain.001.001.09.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_001_001_11 = new SepaVersion(SupportType.GENERATE,11,"urn:iso:std:iso:20022:tech:xsd:pain.001.001.11","pain.001.001.11.xsd",true);
    
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_002_001_10 = new SepaVersion(SupportType.PARSE,10,"urn:iso:std:iso:20022:tech:xsd:pain.002.001.10","pain.002.001.10.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_002_001_11 = new SepaVersion(SupportType.PARSE,11,"urn:iso:std:iso:20022:tech:xsd:pain.002.001.11","pain.002.001.11.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_002_001_12 = new SepaVersion(SupportType.PARSE,12,"urn:iso:std:iso:20022:tech:xsd:pain.002.001.12","pain.002.001.12.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_002_001_13 = new SepaVersion(SupportType.PARSE,13,"urn:iso:std:iso:20022:tech:xsd:pain.002.001.13","pain.002.001.13.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_002_001_14 = new SepaVersion(SupportType.PARSE,14,"urn:iso:std:iso:20022:tech:xsd:pain.002.001.14","pain.002.001.14.xsd",true);
    
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_008_001_01 = new SepaVersion(SupportType.GENERATE,1,"urn:sepade:xsd:pain.008.001.01",                "pain.008.001.01.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_008_002_01 = new SepaVersion(SupportType.GENERATE,2,"urn:swift:xsd:$pain.008.002.01",                "pain.008.002.01.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_008_002_02 = new SepaVersion(SupportType.GENERATE,3,"urn:iso:std:iso:20022:tech:xsd:pain.008.002.02","pain.008.002.02.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_008_003_02 = new SepaVersion(SupportType.GENERATE,4,"urn:iso:std:iso:20022:tech:xsd:pain.008.003.02","pain.008.003.02.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_008_001_02 = new SepaVersion(SupportType.GENERATE,5,"urn:iso:std:iso:20022:tech:xsd:pain.008.001.02","pain.008.001.02.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_008_001_08 = new SepaVersion(SupportType.GENERATE,8,"urn:iso:std:iso:20022:tech:xsd:pain.008.001.08","pain.008.001.08.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion PAIN_008_001_10 = new SepaVersion(SupportType.GENERATE,10,"urn:iso:std:iso:20022:tech:xsd:pain.008.001.10","pain.008.001.10.xsd",true);

    @SuppressWarnings("javadoc") public static SepaVersion CAMT_052_001_01 = new SepaVersion(SupportType.PARSE,1,"urn:iso:std:iso:20022:tech:xsd:camt.052.001.01","camt.052.001.01.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion CAMT_052_001_02 = new SepaVersion(SupportType.PARSE,2,"urn:iso:std:iso:20022:tech:xsd:camt.052.001.02","camt.052.001.02.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion CAMT_052_001_03 = new SepaVersion(SupportType.PARSE,3,"urn:iso:std:iso:20022:tech:xsd:camt.052.001.03","camt.052.001.03.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion CAMT_052_001_04 = new SepaVersion(SupportType.PARSE,4,"urn:iso:std:iso:20022:tech:xsd:camt.052.001.04","camt.052.001.04.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion CAMT_052_001_05 = new SepaVersion(SupportType.PARSE,5,"urn:iso:std:iso:20022:tech:xsd:camt.052.001.05","camt.052.001.05.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion CAMT_052_001_06 = new SepaVersion(SupportType.PARSE,6,"urn:iso:std:iso:20022:tech:xsd:camt.052.001.06","camt.052.001.06.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion CAMT_052_001_07 = new SepaVersion(SupportType.PARSE,7,"urn:iso:std:iso:20022:tech:xsd:camt.052.001.07","camt.052.001.07.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion CAMT_052_001_08 = new SepaVersion(SupportType.PARSE,8,"urn:iso:std:iso:20022:tech:xsd:camt.052.001.08","camt.052.001.08.xsd",true);
    @SuppressWarnings("javadoc") public static SepaVersion CAMT_052_001_09 = new SepaVersion(SupportType.PARSE,9,"urn:iso:std:iso:20022:tech:xsd:camt.052.001.09","camt.052.001.09.xsd",true);

    private SupportType support = null;
    private String urn          = null;
    private String file         = null;
    private Type type           = null;
    private int major           = 0;
    private int minor           = 0;
    private int order           = 0;
    
    /**
     * Legt fest, ab wann eine SEPA-Version als unterstuetzt angesehen werden kann.
     */
    private static enum SupportType
    {
        GENERATE,
        PARSE
    }
    
    /**
     * Enum fuer die Gruppierung der verschienden Typen von Geschaeftsvorfaellen.
     */
    public static enum Type
    {
        /**
         * Ueberweisungen.
         */
        PAIN_001("Pain","001","credit transfer"),
    
        /**
         * Kontoauszuege.
         */
        PAIN_002("Pain","002","payment status"),
        
        /**
         * Lastschriften.
         */
        PAIN_008("Pain","008","direct debit"),
      
        /**
         * Umsaetze im CAMT.052-Format.
         */
        CAMT_052("Camt","052","bank to customer cash management"),
        
        ;
        
        private String type  = null;
        private String value = null;
        private String name  = null;
        
        /**
         * ct.
         * @param type
         * @param value
         * @param name
         */
        private Type(String type, String value, String name)
        {
            this.type = type;
            this.value = value;
            this.name  = name;
        }

        /**
         * Liefert den Typ.
         * @return type
         */
        public String getType()
        {
          return type;
        }
        
        /**
         * Liefert den numerischen Wert des Typs.
         * @return der numerischen Wert des Typs.
         */
        public String getValue()
        {
            return this.value;
        }
        
        /**
         * Liefert eine sprechende Bezeichnung des Typs.
         * @return eine sprechende Bezeichnung des Typs.
         */
        public String getName()
        {
            return this.name;
        }
    }

    /**
     * Liefert die SEPA-Version aus dem URN.
     * @param urn URN.
     * In der Form "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03" oder in
     * der alten Form "sepade.pain.001.001.02.xsd".
     * @return die SEPA-Version.
     */
    public static SepaVersion byURN(String urn)
    {
      SepaVersion test = new SepaVersion(null,0,urn,null,false);
        
        if (urn == null || urn.length() == 0)
            return test;
        
        for (List<SepaVersion> types:knownVersions.values())
        {
            for (SepaVersion v:types)
            {
                if (v.equals(test))
                    return v;
            }
        }
        
        // keine passende Version gefunden. Dann erzeugen wir selbst eine
        return test;
    }

    /**
     * Erzeugt eine SEPA-Version aus dem URN bzw dem Dateinamen.
     * @param support der Support-Type.
     * @param order die Reihenfolge bei der Sortierung.
     * @param urn URN.
     * In der Form "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03" oder in
     * der alten Form "sepade.pain.001.001.02.xsd".
     * @param file Dateiname der Schema-Datei.
     * @param add true, wenn die Version zur Liste der bekannten Versionen hinzugefuegt werden soll.
     */
    private SepaVersion(SupportType support, int order, String urn, String file, boolean add)
    {
        Matcher m = PATTERN.matcher(urn);
        if (!m.find() || m.groupCount() != 4)
            throw new IllegalArgumentException("invalid sepa-version: " + urn);
        
        this.support = support;
        this.order   = order;
        this.urn     = urn;
        this.file    = file;
        this.type    = findType(m.group(1),m.group(2));
        this.major   = Integer.parseInt(m.group(3));
        this.minor   = Integer.parseInt(m.group(4));

        // Zur Liste der bekannten Versionen hinzufuegen
        if (add)
        {
            List<SepaVersion> list = knownVersions.get(this.type);
            if (list == null)
            {
              list = new ArrayList<SepaVersion>();
              knownVersions.put(type,list);
            }
            list.add(this);
        }
    }

    /**
     * Liefert den enum-Type fuer den angegebenen Wert.
     * @param type der Type. "pain", "camt".
     * @param value der Wert. 001, 002, 008, ....
     * @return der zugehoerige Enum-Wert.
     * @throws IllegalArgumentException wenn der Typ unbekannt ist.
     */
    private static Type findType(String type, String value) throws IllegalArgumentException
    {
      if (type == null || type.length() == 0)
        throw new IllegalArgumentException("no SEPA type type given");
      
      if (value == null || value.length() == 0)
        throw new IllegalArgumentException("no SEPA version value given");
      
      for (Type t:Type.values())
      {
          if (t.getType().equalsIgnoreCase(type) && t.getValue().equals(value))
              return t;
      }
      throw new IllegalArgumentException("unknown SEPA version type: " + type + "." + value);
    }

    /**
     * Liefert einen String "<URN> <FILE>" zurueck, der im erzeugten XML als
     * "xsi:schemaLocation" verwendet werden kann.
     * @return Schema-Location oder NULL, wenn "file" nicht gesetzt wurde.
     */
    public String getSchemaLocation()
    {
        if (this.file == null)
            return null;
        
        return this.urn + " " + this.file;
    }
    
    /**
     * Erzeugt den Namen der Java-Klasse des zugehoerigen SEPA-Generators.
     * @param jobName der Job-Name. Z.Bsp. "UebSEPA".
     * @return der Name der Java-Klasse des zugehoerigen SEPA-Generators.
     */
    public String getGeneratorClass(String jobName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(ISEPAGenerator.class.getPackage().getName());
        sb.append(".Gen");
        sb.append(jobName);
        sb.append(this.type.getValue());
        sb.append(new DecimalFormat(DF_MAJOR).format(this.major));
        sb.append(new DecimalFormat(DF_MINOR).format(this.minor));
        
        return sb.toString();
    }
    
    /**
     * Erzeugt den Namen der Java-Klasse des zugehoerigen SEPA-Parsers.
     * @return der Name der Java-Klasse des zugehoerigen SEPA-Parsers.
     */
    public String getParserClass()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(ISEPAParser.class.getPackage().getName());
        sb.append(".Parse");
        
        sb.append(this.type.getType());
        sb.append(this.type.getValue());
        sb.append(new DecimalFormat(DF_MAJOR).format(this.major));
        sb.append(new DecimalFormat(DF_MINOR).format(this.minor));
        
        return sb.toString();
    }
    
    /**
     * Prueft, ob fuer die SEPA-Version ein Generator vorhanden ist, der
     * fuer den angegebenen HBCI4Java-Job die SEPA-XML-Dateien erzeugen kann.
     * @param jobName der Job-Name. Z.Bsp. "UebSEPA".
     * @return true, wenn ein Generator vorhanden ist.
     */
    public boolean canGenerate(String jobName)
    {
        try
        {
            Class.forName(this.getGeneratorClass(jobName));
            return true;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }

    /**
     * Prueft, ob fuer die SEPA-Version ein Parser vorhanden ist, der
     * SEPA-XML-Dateien dieser Version lesen kann. 
     * @return true, wenn ein Parser vorhanden ist.
     */
    public boolean canParse()
    {
        try
        {
            Class.forName(this.getParserClass());
            return true;
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }
    
    /**
     * Prueft, ob die SEPA-Version unterstuetzt wird.
     * @param jobName der Job-Name.
     * @return true, wenn die SEPA-Version unterstuetzt wird.
     */
    public boolean isSupported(String jobName)
    {
        return this.support == SupportType.GENERATE ? this.canGenerate(jobName) : this.canParse();
    }

    /**
     * Liefert den Typ der SEPA-Version.
     * @return der Typ der SEPA-Version.
     */
    public Type getType()
    {
        return this.type;
    }
    
    /**
     * Liefert die Major-Versionsnumer.
     * @return die Major-Versionsnumer.
     */
    public int getMajor()
    {
        return this.major;
    }
    
    /**
     * Liefert die Minor-Versionsnumer.
     * @return die Minor-Versionsnumer.
     */
    public int getMinor()
    {
        return this.minor;
    }
    
    /**
     * Liefert die URN der SEPA-Version.
     * @return die URN der SEPA-Version.
     */
    public String getURN()
    {
        return this.urn;
    }
    
    /**
     * Liefert den Dateinamen des Schemas insofern bekannt.
     * @return der Dateiname des Schema oder null.
     */
    public String getFile()
    {
        return this.file;
    }
    
    /**
     * Findet in den der Liste die hoechste SEPA-Version.
     * @param list Liste mit SEPA-Versionen.
     * @return die hoechste Version oder NULL wenn die Liste leer ist.
     */
    public static SepaVersion findGreatest(List<SepaVersion> list)
    {
        if (list == null || list.size() == 0)
            return null;

        // Sortieren, damit die hoechste Version hinten steht
        try
        {
            Collections.sort(list);
        }
        catch (UnsupportedOperationException e)
        {
            // passiert bei unmodifiable Lists. Dann ist es sehr wahrscheinlich
            // die Liste der knownVersions von uns selbst. Das tolerieren wir.
        }
        
        return list.get(list.size() - 1); // letztes Element
    }

    /**
     * Liefert eine Liste der bekannten SEPA-Versionen fuer den angegebenen Typ.
     * @param t der Typ.
     * @return Liste der bekannten SEPA-Versionen fuer den angegebenen Typ.
     */
    public static List<SepaVersion> getKnownVersions(Type t)
    {
        return knownVersions.get(t);
    }
    
    /**
     * Ermittelt die SEPA-Version aus dem uebergebenen XML-Stream.
     * @param xml der XML-Stream.
     * Achtung: Da der Stream hierbei gelesen werden muss, sollte eine Kopie des Streams uebergeben werden.
     * Denn nach dem Lesen des Streams, kann er nicht erneut gelesen werden.
     * Der Stream wird von dieser Methode nicht geschlossen. Das ist Aufgabe des Aufrufers.
     * @return die ermittelte SEPA-Version oder NULL wenn das XML-Document keine entsprechenden Informationen enthielt.
     */
    public static SepaVersion autodetect(InputStream xml)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xml);
            Node root = doc.getFirstChild(); // Das ist das Element mit dem Namen "Document"
            
            if (root == null)
                throw new IllegalArgumentException("XML data did not contain a root element");
            
            String uri = root.getNamespaceURI();
            if (uri == null)
                return null;
            
            return SepaVersion.byURN(uri);
        }
        catch (IllegalArgumentException e)
        {
            throw e;
        }
        catch (Exception e2)
        {
            throw new IllegalArgumentException(e2);
        }
    }
    
    /**
     * Die Bank sendet in ihren Antworten sowohl den SEPA-Deskriptor als auch die SEPA-Daten (die XML-Datei) selbst.
     * Diese Funktion ermittelt sowohl aus dem SEPA-Deskriptor als auch aus den SEPA-Daten die angegebene SEPA-Version
     * und vergleicht beide. Stimmen sie nicht ueberein, wird eine Warnung ausgegeben. Die Funktion liefert anschliessend
     * die zum Parsen passende Version zurueck. Falls sich die angegebenen Versionen unterscheiden, wird die in den
     * XML-Daten angegebene Version zurueckgeliefert.
     * Siehe https://www.willuhn.de/bugzilla/show_bug.cgi?id=1806
     * @param sepadesc die in der HBCI-Nachricht angegebene SEPA-Version.
     * @param sepadata die eigentlichen XML-Daten.
     * @return die zum Parsen zu verwendende SEPA-Version. NULL, wenn keinerlei Daten angegeben wurden.
     */
    public static SepaVersion choose(String sepadesc, String sepadata)
    {
      final boolean haveDesc = sepadesc != null && sepadesc.length() > 0;
      final boolean haveData = sepadata != null && sepadata.length() > 0;
      
      if (!haveDesc && !haveData)
      {
        HBCIUtils.log("neither sepadesr nor sepa data given",HBCIUtils.LOG_WARN);
        return null;
      }
      
      try
      {
        SepaVersion versionDesc = null;
        if (haveDesc)
        {
          try
          {
            versionDesc = SepaVersion.byURN(sepadesc);
          }
          catch (Exception e)
          {
            HBCIUtils.log("invalid sepa version given in sepadescr: " + sepadesc,HBCIUtils.LOG_WARN);
          }
        }
        final SepaVersion versionData = haveData ? SepaVersion.autodetect(new ByteArrayInputStream(sepadata.getBytes(Comm.ENCODING))) : null;
        
        HBCIUtils.log("sepa version given in sepadescr: " + versionDesc,HBCIUtils.LOG_DEBUG);
        HBCIUtils.log("sepa version according to data: " + versionData,HBCIUtils.LOG_DEBUG);
        
        // Wir haben keine Version im Deskriptor, dann bleibt nur die aus den Daten
        if (versionDesc == null)
          return versionData;
        
        // Wir haben keine Version in den Daten, dann bleibt nur die im Deskriptor
        if (versionData == null)
          return versionDesc;
        
        // Wir geben noch eine Warnung aus, wenn unterschiedliche Versionen angegeben sind
        if (!versionDesc.equals(versionData))
          HBCIUtils.log("sepa version mismatch. sepadesc: " + versionDesc + " vs. data: " + versionData,HBCIUtils.LOG_WARN);
        
        // Wir geben priorisiert die Version aus den Daten zurueck, damit ist sicherer, dass die
        // Daten gelesen werden koennen
        return versionData;
      }
      catch (UnsupportedEncodingException e)
      {
        HBCIUtils.log(e,HBCIUtils.LOG_ERR);
      }
      return null;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        
        if (!(obj instanceof SepaVersion)) return false;
        
        SepaVersion other = (SepaVersion) obj;
        if (major != other.major)
            return false;
        if (minor != other.minor)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SepaVersion v)
    {
        if (v.type != this.type)
            throw new IllegalArgumentException("sepa-type incompatible: " + v.type + " != " + this.type);
        
        // Es ist voellig krank!
        // Die Pain-Versionen waren bisher sauber versioniert. Und jetzt ist ploetzlich
        // eine augenscheinlich kleinere Versionsnummer die aktuellste. WTF?!
        // Beispiel Ueberweisungen - in dieser Reihenfolge:
        
        // pain.001.001.02
        // pain.001.002.02
        // pain.001.002.03
        // pain.001.003.03
        // pain.001.001.03
        
        // Nach "001.003.03" kommt jetzt ploetzlich wieder "001.001.03"!
        
        // Daher habe ich jetzt ein extra Flag fuer die Sortierung eingefuehrt.
        // Kriegt ja sonst keiner mehr auf die Reihe, was die aktuellste Version ist.
        int r = this.order - v.order;
        if (r != 0)
          return r;
        
        r = this.major - v.major;
        if (r != 0)
          return r;
      
        return this.minor - v.minor;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.urn;
    }
    
}


