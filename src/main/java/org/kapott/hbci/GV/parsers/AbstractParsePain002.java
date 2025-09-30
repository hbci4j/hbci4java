package org.kapott.hbci.GV.parsers;

import java.util.List;

import org.kapott.hbci.GV_Result.GVRVoP.VoPResultItem;


/**
 * Abstrakte Basis-Klasse der pain.002 Parser.
 */
public abstract class AbstractParsePain002 extends AbstractSepaParser<List<VoPResultItem>>
{
  /**
   * Konvertiert die Zeilen in eine.
   * @param lines die Zeilen.
   * @param stripQuotes true, wenn Anführungszeichen zu Beginn einer Zeile entfernt werden sollen. Wird beim Namens-Feld benötigt.
   * @return die zusammengefassten Zeilen.
   */
  protected String toString(List<String> lines, boolean stripQuotes)
  {
    final StringBuilder sb = new StringBuilder();
    if (lines == null || lines.isEmpty())
      return sb.toString();
    
    for (String s:lines)
    {
      if (s == null || s.isBlank())
        continue;
      
      if (stripQuotes && (s.startsWith("“") || s.startsWith("\"")))
        s = s.substring(1);
      
      sb.append(s.trim());
    }
    
    return sb.toString();
  }
  
}
