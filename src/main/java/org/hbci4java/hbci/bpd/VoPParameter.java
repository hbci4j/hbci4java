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

package org.hbci4java.hbci.bpd;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.hbci4java.hbci.sepa.SepaVersion;
import org.hbci4java.hbci.tools.ParameterFinder;

/**
 * Übernimmt das Parsen der VoP-Informationen in den BPD.
 */
public class VoPParameter
{
  private List<String> gvCodes = new ArrayList<>();
  private boolean infoTextFormatted = false;
  private List<SepaVersion> formats = new ArrayList<>();
  
  /**
   * ct.
   */
  private VoPParameter()
  {
  }
  
  /**
   * Liefert die Liste der Geschäftsvorfälle, für die das gilt. 
   * @return die Liste der Geschäftsvorfälle, für die das gilt.
   */
  public List<String> getGvCodes()
  {
    return gvCodes;
  }
  
  /**
   * Liefert true, wenn der Informationstext im HIVPP formatiert ist. 
   * @return true, wenn der Informationstext im HIVPP formatiert ist.
   */
  public boolean isInfoTextFormatted()
  {
    return infoTextFormatted;
  }
  
  /**
   * Liefert die PAIN-Formate, welche für die VoP-Rückmeldungen bei Sammelaufträgen verwendet werden.
   * @return die PAIN-Formate, welche für die VoP-Rückmeldungen bei Sammelaufträgen verwendet werden.
   */
  public List<SepaVersion> getFormats()
  {
    return formats;
  }
  
  /**
   * Parst die VoP-Parameter aus den BPD.
   * @param bpd die BPD.
   * @return die VoP-Parameter oder NULL, wenn in den BPD keine passenden Informationen enthalten waren.
   */
  public static VoPParameter parse(Properties bpd)
  {
    if (bpd == null)
      return null;

    final VoPParameter result = new VoPParameter();
    
    // Es kann sein, dass bei den unterstützten Formaten mehrere mit Semikolon angegeben sind
    final String formats = ParameterFinder.getValue(bpd,"Params*.VoPCheckPar1.ParVoPCheck.suppreports",null);
    if (formats != null)
    {
      for (String s:formats.split(";"))
      {
        final SepaVersion v = SepaVersion.byURN(s);
        if (!v.canParse())
          continue; // konnen wir nicht lesen
        result.formats.add(v);
      }
    }
    
    result.infoTextFormatted = Objects.equals("j",ParameterFinder.getValue(bpd,"Params*.VoPCheckPar1.ParVoPCheck.infotextformatted","n").toLowerCase());

    final Properties gvList = ParameterFinder.find(bpd,"Params*.VoPCheckPar1.ParVoPCheck.segcode*");
    // Wir brauchen nur die Werte
    for (Object o:gvList.values())
    {
      result.gvCodes.add(o.toString());
    }
    
    return result;
  }
}
