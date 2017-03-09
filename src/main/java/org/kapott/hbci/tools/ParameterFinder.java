/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/src/org/kapott/hbci/tools/ParameterFinder.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/05/13 15:22:08 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.tools;

import java.util.Enumeration;
import java.util.Properties;

/**
 * Parser zum bequemen Zugriff auf BPD/UPD-Parameter.
 */
public class ParameterFinder
{
  /**
   * Sucht in props nach allen Schluesseln im genannten Pfad und liefert sie zurueck.
   * @param props die Properties, in denen gesucht werden soll.
   * @param path der Pfad.
   * Es koennen Wildcards verwendet werden. Etwa so:
   * Params_*.TAN2StepPar*.ParTAN2Step*.TAN2StepParams*.*secfunc")
   * @return Liefert die gefundenen Properties. Als Schluessel
   * wird jeweils nicht der gesamte Pfad verwendet sondern nur der Teil hinter
   * dem letzten Punkt.
   */
  public static Properties find(Properties props, String path)
  {
    // Kein Pfad angegeben. Also treffen alle.
    if (path == null || path.length() == 0)
      return props;

    // Die neue Map fuer die naechste Runde
    Properties next = new Properties();

    String[] keys = path.split("\\.");
    String key = keys[0];
    
    boolean endsWith   = key.startsWith("*");
    boolean startsWith = key.endsWith("*");
    key = key.replace("*","");
    
    Enumeration e = props.keys();
    while (e.hasMoreElements())
    {
      String name = (String) e.nextElement();
      
      String[] names = name.split("\\.");
      
      if (startsWith && !endsWith && !names[0].startsWith(key)) // Beginnt mit?
        continue;
      else if (!startsWith && endsWith && !names[0].endsWith(key)) // Endet mit?
        continue;
      else if (startsWith && endsWith && !names[0].contains(key)) // Enthaelt?
        continue;
      else if (!startsWith && !endsWith && !names[0].equals(key)) // Ist gleich?
        continue;

      // Wenn wir einen Wert haben, uebernehmen wir ihn in die naechste Runde.
      // Wir schneiden den geprueften Teil ab
      String newName = name.substring(name.indexOf(".")+1);
      next.put(newName,props.getProperty(name));
    }

    // Wir sind hinten angekommen
    if (!path.contains("."))
      return next;
    
    // naechste Runde
    return find(next,path.substring(path.indexOf(".")+1));
  }
  
  /**
   * Test.
   * @param args
   */
  public static void main(String[] args)
  {
    Properties props = new Properties();
    props.put("Params_1.TAN2StepParams3.ParTAN2Step4.TAN2StepParams2.secfunc","Test 1");
    props.put("Params_2.TAN2StepParams3.ParTAN2Step4.TAN2StepParams2.1secfunc","Test 2");
    
    props.put("Params_1.PIN2StepParams3.ParTAN2Step4.TAN2StepParams2.2secfunc","Test 3");
    props.put("Params_1.TANStepParams3.ParTAN2Step4.TAN2StepParams2.3secfunc","Test 4");
    props.put("Params_1.TAN2StepParams3.ParTAN2Step4.TAN2StepParams2.Foo","Test 5");
    props.put("Params_2.TAN2StepPar.ParTAN2Step.TAN2StepParams.5secfunc","Test 5");
    
    Properties result = find(props,"Params_*.TAN2StepPar*.ParTAN2Step*.TAN2StepParams*.*secfunc");
    Enumeration e = result.keys();
    while (e.hasMoreElements())
    {
      String name = (String) e.nextElement();
      String value = (String) result.get(name);
      System.out.println(name + ": " + value);
    }
  }
}



/**********************************************************************
 * $Log: ParameterFinder.java,v $
 * Revision 1.1  2011/05/13 15:22:08  willuhn
 * @N Hilfsklasse zum Suchen von Parametern in BPD/UPD
 *
 **********************************************************************/