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
  private final static boolean DEBUG = false;
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
        String blz      = values[0];
        String current  = values[1];

        try
        {
          String url = lookup.get(blz);
          if (url == null) continue; // wir haben keine URL fuer die BLZ
          
          List<String> oldValues = Arrays.asList(current.split("\\|"));
          if (oldValues.size() <= 5)
          {
            if (DEBUG) System.err.println(blz + ": " + url + " URL fehlt in HBCI4Java");
            continue; // wir haben in HBCI4Java keine URL - TODO: Die neue URL sollte dann in HBCI4Java uebernommen werden
          }
            
          String oldUrl = oldValues.get(5).trim();
          if (oldUrl.length() == 0)
          {
            if (DEBUG) System.err.println(blz + ": " + url + " URL leer in HBCI4Java");
            continue; // wir haben in HBCI4Java keine URL - TODO: Die sollte dann in HBCI4Java uebernommen werden  
          }
          
          // checken, ob wir in der CSV-Datei eine andere URL haben. Falls ja -> aktualisieren
          if (url.equals(oldUrl))
            continue; // unveraendert
          
          System.out.println(blz + ": Update " + oldUrl + "-> " + url);
          current = current.replace(oldUrl,url);
        }
        finally
        {
          f3.write(blz + "=" + current);
          f3.newLine();
        }
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
}



/**********************************************************************
 * $Log: UpdateBLZProperties.java,v $
 * Revision 1.1  2012/04/16 22:24:40  willuhn
 * @N BLZ-Updater Tool
 * @N Aktualisierte BLZ-Liste (Stand 16.10.2011)
 *
 **********************************************************************/