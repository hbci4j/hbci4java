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

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util-Klasse, welche die FinTS-Bankenliste im CSV-Format (kann von http://www.hbci-zka.de/institute/institut_hersteller.htm bezogen werden)
 * parst, un die aktualisierten Daten in die blz.properties von HBCI4Java uebernimmt.
 */
public class UpdateBLZProperties
{
  private final static Charset CHARSET_INPUT = StandardCharsets.ISO_8859_1;
  private final static Charset CHARSET_OUTPUT = StandardCharsets.UTF_8;

  /**
   * @param args
   * 1. Pfad/Dateiname zu "fints_institute.csv".
   * 2. Pfad/Dateiname zu "blz.properties".
   * 3. Pfad/Dateiname zur neuen "blz.properties".
   * 4. optional: Pfad/Dateiname zur BLZ-Datei der Bundesbank. Falls angegeben, werden die eventuell vorhandene BIC-Updates übernommen
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {
    if (args == null || args.length < 3 || args.length > 4)
    {
      System.err.println("benoetigte Parameter: 1) fints_institute.csv, 2) blz.properties, 3) zu schreibende blz.properties");
      System.err.println("optionaler Parameter: 4) BLZ_20160606.txt (BLZ-Update der Bundesbank)");
      System.exit(1);
    }

    BufferedReader f1 = null;
    BufferedReader f2 = null;
    BufferedWriter f3 = null;
    BufferedReader f4 = null;
    String line = null;
    try
    {
      final Map<String, BLZEntry> blzData = new HashMap<String, BLZEntry>();
      final Map<String, BICEntry> bicData = new HashMap<String, BICEntry>();

      //////////////////////////////////////////////////////////////////////////
      // fints_institute.csv lesen
      f1 = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), CHARSET_INPUT));
      int count = 0;
      while ((line = f1.readLine()) != null)
      {
        if (++count <= 1)
          continue; // erste Zeile ueberspringen
        
        if (line.trim().length() == 0)
          continue; // leere Zeile
        
        final BLZEntry e = new BLZEntry(line);
        if (e.blz == null)
          continue;
        
        blzData.put(e.blz,e);
      }
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // BLZ-Datei der Bundesbank einlesen, wenn vorhanden
      if (args.length >= 4)
      {
        final File f = new File(args[3]);
        if (f.exists() && f.isFile() && f.canRead())
        {
          f4 = new BufferedReader(new InputStreamReader(new FileInputStream(f), CHARSET_INPUT));
          while ((line = f4.readLine()) != null)
          {
            final BICEntry e = new BICEntry(line);
            if (e.blz == null || e.bic == null)
              continue;

            bicData.put(e.blz, e);
          }
        }
      }
      //
      //////////////////////////////////////////////////////////////////////////

      final List<Line> newLines = new ArrayList<Line>();

      //////////////////////////////////////////////////////////////////////////
      // blz.properties lesen und abgleichen
      f2 = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), CHARSET_OUTPUT));
      f3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2]), CHARSET_OUTPUT));
      
      while ((line = f2.readLine()) != null)
      {
        final String[] values = line.split("=");
        final Line current = new Line(values[0], values[1]);

        // Daten aktualisieren
        current.updateBLZ(blzData.remove(current.blz));
        current.updateBIC(bicData.get(current.blz));
        
        newLines.add(current);
      }
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // Checken, ob wir Daten für neue BLZ haben, die wir noch nicht kennen
      for (BLZEntry e:blzData.values())
      {
        newLines.add(new Line(e,bicData.get(e.blz)));
      }

      //////////////////////////////////////////////////////////////////////////
      // Geänderte blz.properties schreiben
      f3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2]), CHARSET_OUTPUT));
      for (Line nl:newLines)
      {
        f3.write(nl.toString());
        f3.newLine();
      }
      //
      //////////////////////////////////////////////////////////////////////////
      
    }
    catch (Exception e)
    {
      System.out.println("Fehler in Zeile: " + line);
      throw e;
    }
    finally
    {
      IOUtils.close(f1,f2,f3,f4);
    }
  }

  /**
   * Implementiert eine einzelne Zeile der blz.properties
   */
  private static class Line
  {
    private String blz = null;
    private String[] values = new String[9];

    /**
     * ct.
     * @param blz die BLZ.
     * @param line die Zeile aus der blz.properties
     */
    private Line(String blz, String line)
    {
      this.blz = blz;
      final String[] s = line.split("\\|");
      System.arraycopy(s, 0, this.values, 0, s.length);
    }
    
    /**
     * ct.
     * @param blz die BLZ-Daten.
     * @param bic die BIC-Daten.
     */
    private Line(BLZEntry blz, BICEntry bic)
    {
      this.blz = blz.blz;
      this.values[0] = blz.name;
      this.values[1] = blz.ort;
      this.values[2] = blz.bic;
      this.values[3] = bic != null ? bic.checksumMethod : null;
      this.values[4] = blz.rdh;
      this.values[5] = blz.url;
      
      // Wir nehmen als Version für neue Einträge pauschal nur noch FinTS 3.0
      this.values[6] = "300";
      this.values[7] = "300";
      
      System.out.println("neue Bank: " + this.blz + ": " + blz.name + " in " + blz.ort + ", URL \"" + blz.url);
    }
    
    /**
     * Aktualisiert die Daten aus der BLZ-Datei.
     * @param e die Daten aus der BLZ-Datei.
     */
    private void updateBLZ(BLZEntry e)
    {
      if (e == null)
        return;
      
      this.updateUrl(e.url);
      this.updateVersion(e.version);
      this.updateName(e.name);
    }
    
    /**
     * Aktualisiert die Daten aus der BIC-Datei.
     * @param e die Daten aus der BIC-Datei.
     */
    private void updateBIC(BICEntry e)
    {
      if (e == null)
        return;
      
      this.updateBic(e.bic);
    }

    /**
     * Speichert die neue URL, wenn vorher keine da war oder eine andere.
     * @param url die neue URL.
     */
    private void updateUrl(String url)
    {
      // Keine neue URL
      if (url == null || url.isBlank())
        return;

      String current = this.values[5];
      current = current != null ? current.trim() : "";
      if (!current.equals(url))
      {
        System.out.println(blz + ": URL \"" + current + "\" -> \"" + url + "\"");
        this.values[5] = url;
      }
    }
    
    /**
     * Aktualisiert den Namen der Bank.
     * @param name der neue Name.
     */
    private void updateName(String name)
    {
      if (name == null || name.isBlank())
        return;
      
      String current = this.values[0];
      current = current != null ? current.trim() : "";
      if (!current.equals(name))
      {
        System.out.println(blz + ": Name \"" + current + "\" -> \"" + name + "\"");
        this.values[0] = name;
      }
    }

    /**
     * Speichert die neue BIC, wenn vorher keine da war oder eine andere.
     * @param bic die neue BIC.
     */
    private void updateBic(String bic)
    {
      // Keine neue BIC
      if (bic == null || bic.isBlank())
        return;

      String current = this.values[2];
      current = current != null ? current.trim() : "";
      if (!current.equals(bic))
      {
        // Wenn sich die BIC nur in den letzten 3 Stellen unterscheiden, koennen wir nicht mit Sicherheit sagen,
        // ob unsere Aenderung korrekt waere. In dem Fall kann es sein, dass eine der BIC auf "XXX" endet und
        // keine konkrete Filiale meint. Oder - und das ist noch fataler: Es gibt bei einigen Banken zur selben BLZ
        // UNTERSCHIEDLICHE BIC. Beispiel Deutsche Bank Kiel und Rendsburg. Beide die BLZ "21070020", jedoch
        // unterschiedliche BIC (DEUTDEHH210 und DEUTDEHH214). Da wir das Lookup anhand der BLZ machen und der
        // Name der Filiale aus meiner Sicht kein hinreichend eindeutiges Erkennungsmerkmal ist, machen wir das
        // BIC-Update nur dann, wenn sich die BIC auf den ersten 8 Zeichen aendert. Die letzten 3 Stellen fuer die
        // Filialen lassen wir erstmal aussen vor.
        if (bic.length() < 8 || current.length() < 8)
          return;

        String s1 = bic.substring(0, 8);
        String s2 = current.substring(0, 8);
        if (!s1.equals(s2))
        {
          System.out.println(blz + ": BIC \"" + current + "\" -> \"" + bic + "\"");
          this.values[2] = bic;
        }
      }
    }

    /**
     * Speichert die neue HBCI-Version, wenn vorher keine da war oder eine andere.
     * @param die neue HBCI-Version.
     */
    private void updateVersion(String version)
    {
      // Keine neue Version
      if (version == null || version.length() == 0)
        return;
      
      version = version.toLowerCase();
      if (version.contains("3.0"))
        version = "300";
      else if (version.contains("2.2"))
        version = "plus";

      String current = this.values[7];
      current = current != null ? current.trim() : "";
      if (!current.equals(version))
      {
        System.out.println(blz + ": Version \"" + current + "\" -> \"" + version + "\"");
        this.values[7] = version;
      }
    }

    /**
     * Wandelt die Zeile wieder zurueck in einen String.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      final StringBuffer sb = new StringBuffer();
      sb.append(this.blz);
      sb.append("=");
      for (int i=0;i<this.values.length;++i)
      {
        if (i > 0)
          sb.append("|");

        String s = this.values[i];
        if (s != null)
          sb.append(s);
      }

      return sb.toString();
    }
  }

  /**
   * Implementiert eine einzelne Zeile aus der BLZ-Datei der Bundesbank.
   */
  private static class BICEntry
  {
    private String line = null;
    private String  blz = null;
    private String  bic = null;
    private String checksumMethod = null;

    /**
     * ct.
     * @param line die Zeile aus der BLZ-Datei.
     */
    private BICEntry(String line)
    {
      this.line = line;
      
      if (this.line == null || this.line.length() < 152)
        return;

      this.blz = this.get(0,8);
      this.bic = this.get(139,150);
      this.checksumMethod = this.get(150,152);
    }
    
    /**
     * Liefert den Wert der angegebenen Position aus der Zeike.
     * @param from Start-Index.
     * @param to End-Index.
     * @return der Wert oder NULL.
     */
    private String get(int from, int to)
    {
      try
      {
        if (this.line == null)
          return null;
        
        String s = this.line.substring(from,to);
        if (s == null)
          return null;
        
        s = s.trim();
        return s.length() > 0 ? s : null;
      }
      catch (Exception e)
      {
        return null;
      }
    }
  }

  /**
   * Kapselt die Datei aus einer Zeile der "fints_institute NEU mit BIC Master.csv".
   */
  private static class BLZEntry
  {
    private List<String> values = null;
    private String blz = null;
    private String bic = null;
    private String url = null;
    private String version = null;
    private String name = null;
    private String ort = null;
    private String rdh = null;
    
    /**
     * ct.
     * @param line die Zeile aus der Datei.
     */
    private BLZEntry(String line)
    {
      line = line.trim();
      if (line.length() == 0)
        return;
      
      this.values = Arrays.asList(line.split(";"));
      this.blz = this.get(1);
      this.bic = this.get(2);
      this.name = this.get(3);
      this.ort = this.get(4);
      this.url = this.get(24);
      this.rdh = this.get(7);
      this.version = this.get(25);
      
      // Bei der 30010444 ist Leerzeichen in der URL. Das fixen wir bei der Gelegenheit
      if (this.url != null)
        this.url = this.url.replace(" ","");
    }
    
    /**
     * Liefert den Wert der angegebenen Position aus der Liste.
     * @param pos die Position.
     * @return der Wert oder NULL.
     */
    private String get(int pos)
    {
      try
      {
        if (this.values == null || this.values.size() == 0 || this.values.size() <= pos)
          return null;
        
        String s = this.values.get(pos);
        if (s == null)
          return null;
        
        s = s.trim();
        return s.length() > 0 ? s : null;
      }
      catch (Exception e)
      {
        return null;
      }
    }
  }
}
