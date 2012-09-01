/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/src/org/kapott/hbci/tools/UpdateBLZProperties.java,v $
 * $Revision: 1.1 $
 * $Date: 2012/04/16 22:24:40 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util-Klasse, welche die FinTS-Bankenliste im CSV-Format (kann von
 * http://www.hbci-zka.de/institute/institut_hersteller.htm bezogen werden)
 * parst, un die aktualisierten Daten in die blz.properties von HBCI4Java
 * uebernimmt.
 */
public class UpdateBLZProperties
{
  private final static String ENCODING = "iso-8859-1";
  
  /**
   * @param args
   *   1. Pfad/Dateiname zu "fints_institute.csv".
   *   2. Pfad/Dateiname zu "blz.properties".
   *   3. Pfad/Dateiname zur neuen "blz.properties".
   * @throws Exception 
   */
  public static void main(String[] args) throws Exception
  {
    if (args == null || args.length != 3)
    {
      System.err.println("benoetigte Parameter: 1) fints_institute.csv, 2) blz.properties, 3) zu schreibende blz.properties");
      System.exit(1);
    }
    
    BufferedReader f1 = null;
    BufferedReader f2 = null;
    BufferedWriter f3 = null;
    String line       = null;
    try
    {
      Map<String,String> lookup = new HashMap<String,String>();

      //////////////////////////////////////////////////////////////////////////
      // fints_institute.csv lesen
      f1 = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]),ENCODING));
      int count = 0;
      while ((line = f1.readLine()) != null)
      {
        if (++count <= 1) continue; // erste Zeile ueberspringen
        if (line.trim().length() == 0) continue; // leere Zeile
          
        List<String> values = Arrays.asList(line.split(";"));
        
        if (values.size() <= 20) continue; // Bank hat keine PIN/TAN-URL
        
        String blz = values.get(1).trim();
        if (blz.length() == 0) continue; // Die Zeile enthaelt keine BLZ
        if (lookup.get(blz) != null) continue; // die Zeile haben wir schon
        
        String url = values.get(20).trim();
        if (url.length() == 0) continue; // keine URL gefunden

        lookup.put(blz,url);
      }
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // blz.properties lesen und abgleichen
      f2 = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]),ENCODING));
      f3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2]),ENCODING));
      while ((line = f2.readLine()) != null)
      {
        String[] values = line.split("=");
        Line current    = new Line(values[0],values[1]);

        String url = lookup.get(current.blz);
        
        // URL uebernehmen
        current.updateUrl(url);
        
        // Neue Zeile schreiben
        f3.write(current.toString());
        f3.newLine();
      }
      //
      //////////////////////////////////////////////////////////////////////////
    }
    finally
    {
      if (f1 != null) f1.close();
      if (f2 != null) f2.close();
      if (f3 != null) f3.close();
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
    private Line(String blz,String line)
    {
      this.blz = blz;
      String[] s = line.split("\\|");
      System.arraycopy(s,0,this.values,0,s.length);
    }
    
    /**
     * Speichert die neue URL, wenn vorher keine da war oder eine andere.
     * @param url die neue URL.
     */
    private void updateUrl(String url)
    {
      // Keine neue URL
      if (url == null || url.length() == 0)
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
     * Wandelt die Zeile wieder zurueck in einen String.
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
      StringBuffer sb = new StringBuffer();
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
}
