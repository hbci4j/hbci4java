
/*  $Id: RInvalidSuppHBCIVersion.java,v 1.1 2011/05/04 22:37:57 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.rewrite;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.HBCIVersion;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.SyntaxElement;
import org.kapott.hbci.protocol.factory.MSGFactory;

/**
 * Korrigiert falsche HBCI-Versionen in den BPD.
 */
public class RInvalidSuppHBCIVersion extends Rewrite
{
  /**
   * @see org.kapott.hbci.rewrite.Rewrite#incomingClearText(java.lang.String, org.kapott.hbci.manager.MsgGen)
   */
  public String incomingClearText(String st, MsgGen gen)
  {
    // Wir packen das Rewrite in ein try/catch, weil wir hier keine reinen String-Operationen
    // verwenden und nicht 100%ig sichergestellt ist, ob die Nachricht zu diesem Zeitpunkt schon
    // geparst werden kann (eventuell wird sie ja erst nach der Bearbeitung durch die Folge-Rewriter lesbar)
    // Falls das Rewrite fehschlaegt, dann tolerieren wir es halt.
    
    MSG msg = null;

    try
    {
      // empfangene Nachricht parsen, dabei die validvalues-Überprüfung weglassen
      String myMsgName = (String) getData("msgName") + "Res";
      msg = MSGFactory.getInstance().createMSG(myMsgName, st, st.length(), gen, MSG.DONT_CHECK_SEQ, MSG.DONT_CHECK_VALIDS);

      // in einer Schleife durch alle SuppVersions-Datensätze laufen
      // Limiter bei 1000 setzen. "msg.getElement" kann u.U. "this" (=msg) zurueckliefern.
      // Die Funktion koennte dann in einer Endlosschleife landen.
      for (int i=0;i<1000;i++)
      {
        String elemName = HBCIUtilsInternal.withCounter(myMsgName + ".BPD.BPA.SuppVersions.version", i);
        SyntaxElement elem = msg.getElement(elemName);

        if (elem == null)
          break;
        
        StringBuffer sb = new StringBuffer(st);
        
        if (this.replace(sb,elem,"2",HBCIVersion.HBCI_210))
        {
          st = sb.toString();
          break;
        }
        
        if (this.replace(sb,elem,"3",HBCIVersion.HBCI_300))
        {
          st = sb.toString();
          break;
        }
      }
    }
    catch (Exception e)
    {
      HBCIUtils.log("unable to apply rewriter " + this.getClass().getSimpleName() + " - leaving messag unchanged", HBCIUtils.LOG_INFO);
      HBCIUtils.log(e,HBCIUtils.LOG_DEBUG);
    }
    finally
    {
      if (msg != null)
      {
        try
        {
          MSGFactory.getInstance().unuseObject(msg);
        }
        catch (Exception e)
        {
          HBCIUtils.log(e,HBCIUtils.LOG_WARN);
        }
      }
    }

    return st;
  }
  
  /**
   * Ersetzt eine ungueltige HBCI-Version in der Nachricht.
   * @param msg die Nachricht.
   * @param elem Das Element mit der HBCI-Version.
   * @param search die zu suchende HBCI-Version.
   * @param replace die HBCI-Version, die stattdessen eingetragen werden soll.
   * @return true, wenn eine Ersetzung vorgenommen wurde.
   */
  private boolean replace(StringBuffer msg, SyntaxElement elem, String search, HBCIVersion replace)
  {
    final String version = elem.toString();
    if (version == null || search == null || !version.equals(search))
      return false;
      
    final String s = replace.getId();
    HBCIUtils.log("there is an invalid hbci version number ('" + version + "') in this BPD - replacing it with '" + s + " '",HBCIUtils.LOG_WARN);

    int startpos = elem.getPosInMsg() + 1; // +1 wegen überspringen des pre-delimiters
    msg.replace(startpos, startpos + 1,s);
    HBCIUtils.log("new message after replacing: " + msg,HBCIUtils.LOG_DEBUG);
    return true;
  }
}
