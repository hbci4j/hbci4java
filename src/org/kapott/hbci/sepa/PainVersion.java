/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.sepa;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kapott.hbci.GV.generators.ISEPAGenerator;

/**
 * Kapselt das Parsen und Vergleichen von SEPA Pain-Versionen.
 */
public class PainVersion implements Comparable<PainVersion>
{
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
        PAIN_002("002","paiment status"),
        
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
    
    private final static DecimalFormat DF_MAJOR = new DecimalFormat("000");
    private final static DecimalFormat DF_MINOR = new DecimalFormat("00");
    
    private final static Pattern PATTERN = Pattern.compile("(\\d\\d\\d)\\.(\\d\\d\\d)\\.(\\d\\d)");

    private String urn = null;
    private Type type  = null;
    private int major  = 0;
    private int minor  = 0;
    
    /**
     * Erzeugt eine PAIN-Version aus dem URN bzw dem Dateinamen.
     * @param urn URN.
     * In der Form "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03" oder in
     * der alten Form "sepade.pain.001.001.02.xsd".
     */
    public PainVersion(String urn)
    {
        Matcher m = PATTERN.matcher(urn);
        if (!m.find() || m.groupCount() != 3)
            throw new IllegalArgumentException("invalid pain-version: " + urn);
        
        this.urn   = urn;
        this.type  = Type.getType(m.group(1));
        this.major = Integer.parseInt(m.group(2));
        this.minor = Integer.parseInt(m.group(3));
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
        sb.append(DF_MAJOR.format(this.major));
        sb.append(DF_MINOR.format(this.minor));
        
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
     * Findet in den der Liste die hoechste Pain-Version.
     * @param list Liste mit PAIN-Versionen.
     * @return die hoechste Version oder NULL wenn die Liste leer ist.
     */
    public static PainVersion findGreatest(List<PainVersion> list)
    {
        if (list == null || list.size() == 0)
            return null;

        // Sortieren, damit die hoechste Version hinten steht
        Collections.sort(list);
        
        return list.get(list.size() - 1); // letztes Element
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
    public int compareTo(PainVersion v)
    {
        if (v.type != this.type)
            throw new IllegalArgumentException("pain-type incompatible: " + v.type + " != " + this.type);
        
      int r = this.major - v.major;
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


