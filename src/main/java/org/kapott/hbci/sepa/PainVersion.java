/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.sepa;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kapott.hbci.GV.generators.ISEPAGenerator;
import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Kapselt das Parsen und Vergleichen von SEPA Pain-Versionen.
 */
public class PainVersion implements Comparable<PainVersion>
{
    private final static String DF_MAJOR = "000";
    private final static String DF_MINOR = "00";
    
    private final static Pattern PATTERN = Pattern.compile("(\\d\\d\\d)\\.(\\d\\d\\d)\\.(\\d\\d)");

    @SuppressWarnings("javadoc") public static PainVersion PAIN_001_001_02 = new PainVersion(1,"urn:sepade:xsd:pain.001.001.02",                "pain.001.001.02.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_001_002_02 = new PainVersion(2,"urn:swift:xsd:$pain.001.002.02",                "pain.001.002.02.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_001_002_03 = new PainVersion(3,"urn:iso:std:iso:20022:tech:xsd:pain.001.002.03","pain.001.002.03.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_001_003_03 = new PainVersion(4,"urn:iso:std:iso:20022:tech:xsd:pain.001.003.03","pain.001.003.03.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_001_001_03 = new PainVersion(5,"urn:iso:std:iso:20022:tech:xsd:pain.001.001.03","pain.001.001.03.xsd");
    
    @SuppressWarnings("javadoc") public static PainVersion PAIN_002_002_02 = new PainVersion(1,"urn:swift:xsd:$pain.002.002.02",                "pain.002.002.02.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_002_003_03 = new PainVersion(2,"urn:iso:std:iso:20022:tech:xsd:pain.002.003.03","pain.002.003.03.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_002_001_03 = new PainVersion(3,"urn:iso:std:iso:20022:tech:xsd:pain.002.001.03","pain.002.001.03.xsd");
    
    @SuppressWarnings("javadoc") public static PainVersion PAIN_008_001_01 = new PainVersion(1,"urn:sepade:xsd:pain.008.001.01",                "pain.008.001.01.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_008_002_01 = new PainVersion(2,"urn:swift:xsd:$pain.008.002.01",                "pain.008.002.01.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_008_002_02 = new PainVersion(3,"urn:iso:std:iso:20022:tech:xsd:pain.008.002.02","pain.008.002.02.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_008_003_02 = new PainVersion(4,"urn:iso:std:iso:20022:tech:xsd:pain.008.003.02","pain.008.003.02.xsd");
    @SuppressWarnings("javadoc") public static PainVersion PAIN_008_001_02 = new PainVersion(5,"urn:iso:std:iso:20022:tech:xsd:pain.008.001.02","pain.008.001.02.xsd");
    
    private final static Map<Type,List<PainVersion>> knownVersion = new HashMap<Type,List<PainVersion>>()
    {{
        put(Type.PAIN_001,Collections.unmodifiableList(Arrays.asList(PAIN_001_001_02,PAIN_001_002_02,PAIN_001_002_03,PAIN_001_003_03,PAIN_001_001_03)));
        put(Type.PAIN_002,Collections.unmodifiableList(Arrays.asList(PAIN_002_002_02,PAIN_002_003_03,PAIN_002_001_03)));
        put(Type.PAIN_008,Collections.unmodifiableList(Arrays.asList(PAIN_008_001_01,PAIN_008_002_01,PAIN_008_002_02,PAIN_008_003_02,PAIN_008_001_02)));
    }};
    
    /**
     * Enum fuer die Gruppierung der verschienden Typen von Geschaeftsvorfaellen.
     */
    public static enum Type
    {
        /**
         * Ueberweisungen.
         */
        PAIN_001("001","credit transfer"),
    
        /**
         * Kontoauszuege.
         */
        PAIN_002("002","payment status"),
        
        /**
         * Lastschriften.
         */
        PAIN_008("008","direct debit");
        
        private String value = null;
        private String name  = null;
        
        /**
         * ct.
         * @param value
         * @param name
         */
        private Type(String value, String name)
        {
            this.value = value;
            this.name  = name;
        }
        
        /**
         * Liefert den numerischen Wert des PAIN-Typs.
         * @return der numerischen Wert des PAIN-Typs.
         */
        public String getValue()
        {
            return this.value;
        }
        
        /**
         * Liefert eine sprechende Bezeichnung des PAIN-Typs.
         * @return eine sprechende Bezeichnung des PAIN-Typs.
         */
        public String getName()
        {
            return this.name;
        }
        
        /**
         * Liefert den enum-Type fuer den angegebenen Wert.
         * @param value der Wert. 001, 002 oder 008.
         * @return der zugehoerige Enum-Wert.
         * @throws IllegalArgumentException wenn der Typ unbekannt ist.
         */
        public static Type getType(String value) throws IllegalArgumentException
        {
            if (value != null && value.length() > 0)
            {
                for (Type t:Type.values())
                {
                    if (t.value.equals(value))
                        return t;
                }
            }
            
            throw new IllegalArgumentException("unknown PAIN type: " + value);
        }
    }
    
    private String urn  = null;
    private String file = null;
    private Type type   = null;
    private int major   = 0;
    private int minor   = 0;
    private int order   = 0;
    
    
    /**
     * Liefert die PAIN-Version aus dem URN.
     * @param urn URN.
     * In der Form "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03" oder in
     * der alten Form "sepade.pain.001.001.02.xsd".
     * @return die PAIN-Version.
     */
    public static PainVersion byURN(String urn)
    {
        PainVersion test = new PainVersion(0,urn,null);
        
        if (urn == null || urn.length() == 0)
            return test;
        
        for (List<PainVersion> types:knownVersion.values())
        {
            for (PainVersion v:types)
            {
                if (v.equals(test))
                    return v;
            }
        }
        
        // keine passende Version gefunden. Dann erzeugen wir selbst eine
        return test;
    }
    
    /**
     * ct.
     * Erzeugt eine neue PAIN-Version.
     * @deprecated Bitte stattdessen {@link PainVersion#byURN(String)} verwenden.
     * @param urn der URN.
     */
    @Deprecated
    public PainVersion(String urn)
    {
        this(0,urn,null);
    }

    
    /**
     * ct.
     * Erzeugt eine neue PAIN-Version.
     * @deprecated Bitte stattdessen {@link PainVersion#byURN(String)} verwenden.
     * @param urn der URN.
     * @param file Dateiname der Schema-Datei.
     */
    @Deprecated
    public PainVersion(String urn, String file)
    {
        this(0,urn,file);
    }
    
    /**
     * Erzeugt eine PAIN-Version aus dem URN bzw dem Dateinamen.
     * @param order die Reihenfolge bei der Sortierung.
     * @param urn URN.
     * In der Form "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03" oder in
     * der alten Form "sepade.pain.001.001.02.xsd".
     * @param file Dateiname der Schema-Datei.
     */
    private PainVersion(int order, String urn, String file)
    {
        Matcher m = PATTERN.matcher(urn);
        if (!m.find() || m.groupCount() != 3)
            throw new IllegalArgumentException("invalid pain-version: " + urn);
        
        this.order = order;
        this.urn   = urn;
        this.file  = file;
        this.type  = Type.getType(m.group(1));
        this.major = Integer.parseInt(m.group(2));
        this.minor = Integer.parseInt(m.group(3));
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
        sb.append(".ParsePain");
        sb.append(this.type.getValue());
        sb.append(new DecimalFormat(DF_MAJOR).format(this.major));
        sb.append(new DecimalFormat(DF_MINOR).format(this.minor));
        
        return sb.toString();
    }
    
    /**
     * Prueft, ob die angegebene PAIN-Version fuer den angegebenen Job von HBCI4Java unterstuetzt wird.
     * @param jobName der Job-Name. Z.Bsp. "UebSEPA".
     * @return true, wenn sie unterstuetzt wird.
     */
    public boolean isSupported(String jobName)
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
     * Liefert den Typ der PAIN-Version.
     * @return der Typ der PAIN-Version.
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
     * Liefert die URN der PAIN-Version.
     * @return die URN der PAIN-Version.
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
     * Findet in den der Liste die hoechste Pain-Version.
     * @param list Liste mit PAIN-Versionen.
     * @return die hoechste Version oder NULL wenn die Liste leer ist.
     */
    public static PainVersion findGreatest(List<PainVersion> list)
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
     * Liefert eine Liste der bekannten PAIN-Versionen fuer den angegebenen Typ.
     * @param t der Typ.
     * @return Liste der bekannten PAIN-Versionen fuer den angegebenen Typ.
     */
    public static List<PainVersion> getKnownVersions(Type t)
    {
        return knownVersion.get(t);
    }
    
    /**
     * Ermittelt die PAIN-Version aus dem uebergebenen XML-Stream.
     * @param xml der XML-Stream.
     * Achtung: Da der Stream hierbei gelesen werden muss, sollte eine Kopie des Streams uebergeben werden.
     * Denn nach dem Lesen des Streams, kann er nicht erneut gelesen werden.
     * Der Stream wird von dieser Methode nicht geschlossen. Das ist Aufgabe des Aufrufers.
     * @return die ermittelte PAIN-Version oder NULL wenn das XML-Document keine entsprechenden Informationen enthielt.
     */
    public static PainVersion autodetect(InputStream xml)
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
            
            return PainVersion.byURN(uri);
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
        
        if (!(obj instanceof PainVersion)) return false;
        
        PainVersion other = (PainVersion) obj;
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
    public int compareTo(PainVersion v)
    {
        if (v.type != this.type)
            throw new IllegalArgumentException("pain-type incompatible: " + v.type + " != " + this.type);
        
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


