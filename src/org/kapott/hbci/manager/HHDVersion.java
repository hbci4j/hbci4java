/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.manager;

import java.util.Properties;

/**
 * Kapselt die Erkennung der verschiedenen HHD-Versionen.
 */
public enum HHDVersion
{
    /**
     * HHD-Version 1.4
     * Zur HKTAN-Segment-Version: Genau wissen wir es nicht, aber HHD 1.4 ist wahrscheinlich.
     */
    HHD_1_4(Type.CHIPTAN,"HHD1.4","1.4",5,"hhd14"),
    
    /**
     * HHD-Version 1.3
     * Zur HKTAN-Segment-Version: 1.4 ist in HKTAN4 noch nicht erlaubt, damit bleibt eigentlich nur 1.3
     */
    HHD_1_3(Type.CHIPTAN,"HHD1.3","1.3",4,"hhd13"),

    /**
     * Server-seitig generierter Matrix-Code (photoTAN)
     * ZKA-Version und HKTAN-Version bleiben hier frei, weil wir anhand diesen
     * Merkmalen das Matrix-Code-Verfahren nicht eindeutig erkennen koennen.
     * Und da chipTAN/smsTAN deutlich gebrauechlicher ist, ist es erheblich wahrscheinlicher,
     * dass dann nicht Matrix-Code ist.
     * Generell unterstuetzen wir nur server-seitig generierte Matrix-Codes.
     */
    MS_1(Type.PHOTOTAN,"MS1",null,-1,"hhd14"),

    /**
     * HHD-Version 1.2.
     * Fallback.
     */
    HHD_1_2(Type.CHIPTAN,null,null,-1,"hhd12"),
    
    ;
    
    /**
     * Definiert die Art des TAN-Verfahrens.
     */
    public static enum Type
    {
        /**
         * chipTAN oder smsTAN.
         */
        CHIPTAN,
        
        /**
         * photoTAN.
         */
        PHOTOTAN,
    }
    
    private Type type = null;
    private String idStart = null;
    private String versionStart = null;
    private int segVersion = 0;
    private String challengeVersion = null;
    
    /**
     * ct.
     * @param type die Art des TAN-Verfahrens.
     * @param idStart Technische Kennung beginnt mit diesem Text.
     * Siehe "Belegungsrichtlinien TANve1.4  mit Erratum 1-3 final version vom 2010-11-12.pdf"
     * Der Name ist standardisiert, wenn er mit "HHD1...." beginnt, ist das die HHD-Version
     * @param versionStart ZKA-Version bei HKTAN.
     * @param segVersion Segment-Version des HKTAN-Elements.
     * @param challengeVersion die Kennung fuer das Lookup in den ChallengeInfo-Daten.
     */
    private HHDVersion(Type type, String idStart, String versionStart, int segVersion, String challengeVersion)
    {
        this.type = type;
        this.idStart = idStart;
        this.versionStart = versionStart;
        this.segVersion = segVersion;
        this.challengeVersion = challengeVersion;
    }
    
    /**
     * Liefert die Kennung fuer das Lookup in den ChallengeInfo-Daten.
     * @return die Kennung fuer das Lookup in den ChallengeInfo-Daten.
     */
    public String getChallengeVersion()
    {
        return this.challengeVersion;
    }
    
    /**
     * Liefert die Art des TAN-Verfahrens.
     * @return die Art des TAN-Verfahrens.
     */
    public Type getType()
    {
        return this.type;
    }

    /**
     * Ermittelt die zu verwendende HHD-Version aus den BPD-Informationen des TAN-Verfahrens.
     * @param secmech die BPD-Informationen zum TAN-Verfahren.
     * @return die HHD-Version.
     */
    public static HHDVersion find(Properties secmech)
    {
      HBCIUtils.log("trying to determine HHD version for secmech: " + secmech,HBCIUtils.LOG_DEBUG);
      // Das ist die "Technische Kennung"
      // Siehe "Belegungsrichtlinien TANve1.4  mit Erratum 1-3 final version vom 2010-11-12.pdf"
      // Der Name ist standardisiert, wenn er mit "HHD1...." beginnt, ist
      // das die HHD-Version
      String id = secmech.getProperty("id","");
      HBCIUtils.log("  technical HHD id: " + id,HBCIUtils.LOG_DEBUG);
      for (HHDVersion v:values())
      {
          String s = v.idStart;
          if (s == null)
              continue;
          if (id.startsWith(s))
              return v;
      }
      
      // Fallback 1. Wir schauen noch in "ZKA-Version bei HKTAN"
      String version = secmech.getProperty("zkamethod_version");
      HBCIUtils.log("  ZKA version: " + version,HBCIUtils.LOG_DEBUG);
      if (version != null && version.length() > 0)
      {
          for (HHDVersion v:values())
          {
              String s = v.versionStart;
              if (s == null)
                  continue;
              if (version.startsWith(s))
                  return v;
          }
      }
      
      // Fallback 2. Wir checken noch die HITAN/HKTAN-Version
      // Bei HKTAN5 kann es HHD 1.3 oder 1.4 sein, bei HKTAN4 bleibt eigentlich nur noch 1.3
      // Ich weiss nicht, ob Fallback 2 ueberhaupt notwendig ist. Denn angeblich
      // ist zkamethod_version seit HHD 1.3.1 Pflicht (siehe
      // FinTS_3.0_Security_Sicherheitsverfahren_PINTAN_Rel_20101027_final_version.pdf,
      // Data dictionary "Version ZKA-TAN-Verfahren"
      String segversion = secmech.getProperty("segversion");
      HBCIUtils.log("  segment version: " + segversion,HBCIUtils.LOG_DEBUG);
      if (segversion != null && segversion.length() > 0)
      {
          int i = Integer.parseInt(segversion);
          for (HHDVersion v:values())
          {
              int i2 = v.segVersion;
              if (i2 <= 0)
                  continue;
              
              if (i == i2)
                  return v;
          }
      }
      
      // Default:
      return HHD_1_2;
    }

}


