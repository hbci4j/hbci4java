package org.kapott.hbci.GV.parsers;

import java.util.List;

import org.kapott.hbci.GV_Result.GVRVoP.VoPResultItem;


/**
 * Abstrakte Basis-Klasse der pain.002 Parser.
 */
public abstract class AbstractParsePain002 extends AbstractSepaParser<List<VoPResultItem>>
{
  /**
   * Liefert den korrigierten Namen.
   * @param names die Liste der Namen.
   * @return der korrigerte Name oder einen Leerstring, wenn keiner ermittelt werden konnte.
   */
  protected String getNames(List<String> names)
  {
    final StringBuilder sb = new StringBuilder();
    if (names == null || names.isEmpty())
      return sb.toString();
    
    for (String s:names)
    {
      if (s == null || s.isBlank())
        continue;
      
      if (s.startsWith("“") || s.startsWith("\""))
        s = s.substring(1);
      
      sb.append(s);
    }
    
    return sb.toString();
  }
  
}
