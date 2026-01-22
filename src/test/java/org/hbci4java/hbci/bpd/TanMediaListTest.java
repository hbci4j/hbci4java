package org.hbci4java.hbci.bpd;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.hbci4java.hbci.AbstractTest;
import org.hbci4java.hbci.manager.HBCIKernelImpl;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.MsgGen;
import org.hbci4java.hbci.protocol.MSG;
import org.hbci4java.hbci.protocol.factory.MSGFactory;
import org.hbci4java.hbci.rewrite.Rewrite;
import org.junit.Test;

/**
 * Testet das Empfangen der TAN-Medienliste.
 */
public class TanMediaListTest extends AbstractTest
{

  /**
   * @throws Exception
   */
  @Test
  public void test() throws Exception
  {
    String data = getFile("bpd-tanmedialist.txt");
    HBCIKernelImpl kernel = new HBCIKernelImpl(null, "300");

    Rewrite.setData("msgName", "CustomMsg");
    // liste der rewriter erzeugen
    String rewriters_st = HBCIUtils.getParam("kernel.rewriter");
    ArrayList<Rewrite> al = new ArrayList<Rewrite>();
    StringTokenizer tok = new StringTokenizer(rewriters_st, ",");
    while (tok.hasMoreTokens())
    {
      String rewriterName = tok.nextToken().trim();
      if (rewriterName.length() != 0)
      {
        Class cl = this.getClass().getClassLoader().loadClass("org.hbci4java.hbci.rewrite.R" + rewriterName);
        Constructor con = cl.getConstructor((Class[]) null);
        Rewrite rewriter = (Rewrite) (con.newInstance((Object[]) null));
        al.add(rewriter);
      }
    }
    Rewrite[] rewriters = al.toArray(new Rewrite[al.size()]);

    kernel.rawNewMsg("CustomMsg");

    MsgGen gen = kernel.getMsgGen();

    // alle patches für die unverschlüsselte nachricht durchlaufen
    String newmsgstring = data;

    for (int i = 0; i < rewriters.length; i++)
    {
      newmsgstring = rewriters[i].incomingClearText(newmsgstring, gen);
    }

    MSG msg = MSGFactory.getInstance().createMSG("CustomMsgRes", newmsgstring, newmsgstring.length(), gen);
    Hashtable<String, String> ht = new Hashtable<String, String>();
    msg.extractValues(ht);
  }

}
