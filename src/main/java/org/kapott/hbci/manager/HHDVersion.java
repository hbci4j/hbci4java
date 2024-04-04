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

package org.kapott.hbci.manager;

import java.util.Properties;

/**
 * Kapselt die Erkennung der verschiedenen HHD-Versionen.
 */
public enum HHDVersion
{
    /**
     * QR-Code in HHD-Version 1.3 - die Sparkasse verwendet das so.
     * Muss als erstes hier stehen, weil es sonst falsch als "HHD_1_3" erkannt wird (ID beginnt genauso).
     */
    QR_1_3(Type.QRCODE,"HHD1\\.3\\..*?QR",null,-1,"hhd13"),

    /**
     * QR-Code.
     */
    QR_1_4(Type.QRCODE,"Q1S.*",null,-1,"hhd14"),

    /**
     * HHD-Version 1.4
     * Zur HKTAN-Segment-Version: Genau wissen wir es nicht, aber HHD 1.4 ist wahrscheinlich.
     */
    HHD_1_4(Type.CHIPTAN,"HHD1\\.4.*","1.4",5,"hhd14"),
    
    /**
     * HHD-Version 1.3
     * Zur HKTAN-Segment-Version: 1.4 ist in HKTAN4 noch nicht erlaubt, damit bleibt eigentlich nur 1.3
     */
    HHD_1_3(Type.CHIPTAN,"HHD1\\.3.*","1.3",4,"hhd13"),

    /**
     * Server-seitig generierter Matrix-Code (photoTAN)
     * ZKA-Version und HKTAN-Version bleiben hier frei, weil wir anhand diesen
     * Merkmalen das Matrix-Code-Verfahren nicht eindeutig erkennen koennen.
     * Und da chipTAN/smsTAN deutlich gebrauechlicher ist, ist es erheblich wahrscheinlicher,
     * dass dann nicht Matrix-Code ist.
     * Generell unterstuetzen wir nur server-seitig generierte Matrix-Codes.
     */
    MS_1(Type.PHOTOTAN,"MS1.*|photoTAN.*",null,-1,"hhd14"),

    /**
     * HHD-Version 1.2.
     * Fallback.
     */
    HHD_1_2(Type.CHIPTAN,null,null,-1,"hhd12"),

    /**
     * Push-TAN 2.0 (Decoupled)
     */
    DECOUPLED(Type.DECOUPLED,"Decouple.*"),

    ;
  
    /**
     * Die Default-Version.
     */
    public final static HHDVersion DEFAULT = HHD_1_2;
    
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
        
        /**
         * QR-Code.
         */
        QRCODE,
        
        /**
         * Push-TAN 2.0 (Decoupled)
         */
        DECOUPLED

    }

    private Type type = null;
    private String nameMatch = null;
    private String idMatch = null;
    private String versionStart = null;
    private int segVersion = 0;
    private String challengeVersion = null;

    /**
     * ct.
     * @param type die Art des TAN-Verfahrens.
     * @param nameMatch Pattern für DK-Verfahrensbezeichnung.
     */
    private HHDVersion(Type type, String nameMatch)
    {
      this.type = type;
      this.nameMatch = nameMatch;
    }

    /**
     * ct.
     * @param type die Art des TAN-Verfahrens.
     * @param idMatch Pattern fuer die Technische Kennung.
     * Siehe "Belegungsrichtlinien TANve1.4  mit Erratum 1-3 final version vom 2010-11-12.pdf"
     * Der Name ist standardisiert, wenn er mit "HHD1...." beginnt, ist das die HHD-Version
     * @param versionStart ZKA-Version bei HKTAN.
     * @param segVersion Segment-Version des HKTAN-Elements.
     * @param challengeVersion die Kennung fuer das Lookup in den ChallengeInfo-Daten.
     */
    private HHDVersion(Type type, String idMatch, String versionStart, int segVersion, String challengeVersion)
    {
        this.type = type;
        this.idMatch = idMatch;
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

      if (secmech == null)
      {
        HBCIUtils.log("have no secmech data, fallback to default: " + DEFAULT,HBCIUtils.LOG_WARN);
        return DEFAULT;
      }
      
      // DK-TAN-Verfahren
      String name = secmech.getProperty("zkamethod_name","");
      if (name != null && name.length() > 0)
      {
        HBCIUtils.log("  DK name: " + name,HBCIUtils.LOG_DEBUG);
        for (HHDVersion v:values())
        {
            String s = v.nameMatch;
            if (s == null)
                continue;
            if (name.matches(s))
            {
                HBCIUtils.log("  identified as " + v,HBCIUtils.LOG_DEBUG);
                return v;
            }
        }
      }
      
      // Das ist die "Technische Kennung"
      // Siehe "Belegungsrichtlinien TANve1.4  mit Erratum 1-3 final version vom 2010-11-12.pdf"
      // Der Name ist standardisiert, wenn er mit "HHD1...." beginnt, ist
      // das die HHD-Version
      String id = secmech.getProperty("id","");
      HBCIUtils.log("  technical HHD id: " + id,HBCIUtils.LOG_DEBUG);
      for (HHDVersion v:values())
      {
          String s = v.idMatch;
          if (s == null || id == null)
              continue;
          if (id.matches(s))
          {
              HBCIUtils.log("  identified as " + v,HBCIUtils.LOG_DEBUG);
              return v;
          }
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
              {
                  HBCIUtils.log("  identified as " + v,HBCIUtils.LOG_DEBUG);
                  return v;
              }
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
              {
                  HBCIUtils.log("  identified as " + v,HBCIUtils.LOG_DEBUG);
                  return v;
              }
          }
      }
      
      // Default:
      HBCIUtils.log("  no HHD version detected, default to " + DEFAULT,HBCIUtils.LOG_DEBUG);
      return DEFAULT;
    }

}


