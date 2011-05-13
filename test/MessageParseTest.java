/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/Attic/MessageParseTest.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/05/13 15:07:58 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;

/**
 * 
 */
public class MessageParseTest
{

  /**
   * Parst die Message aus der genannten Datei.
   * 
   * ACHTUNG: Beim Ausfuehren eine hbci4java-$version.jar in den Classpath tun.
   *          HBCI4Java kriegt sonst seine Locale-Resource-Bundle nicht geladen
   * 
   * @param args die Datei.
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {
    BufferedReader reader = null;
    try
    {
      StringBuffer sb = new StringBuffer();
      reader = new BufferedReader(new InputStreamReader(new FileInputStream("test/bpd2-formatted.txt")));
      String line = null;
      while ((line = reader.readLine()) != null)
        sb.append(line.trim());
      String data = sb.toString();
      
      System.out.println(data);
      
      Locale.setDefault(Locale.GERMANY);

      Properties props = new Properties();
      props.put("log.loglevel.default",""+HBCIUtils.LOG_DEBUG2);
      HBCIUtils.init(props,new HBCICallbackConsole());
      
      HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
      kernel.rawNewMsg("DialogInitAnon");

      
      MsgGen gen = kernel.getMsgGen();
      MSG msg = MSGFactory.getInstance().createMSG("DialogInitAnonRes",data,data.length(),gen);
      Hashtable<String,String> ht = new Hashtable<String,String>();
      msg.extractValues(ht);
      
      // Prefix abschneiden
      Properties bpd = new Properties();
      for (Enumeration e=ht.keys();e.hasMoreElements();)
      {
        String name = (String) e.nextElement();
        bpd.put(name.substring(22),(String) ht.get(name)); // "DialogInitAnonRes.BPD" abschneiden
      }
      
      Set set = bpd.keySet();
      String[] names = (String[]) set.toArray(new String[set.size()]);
      Arrays.sort(names);

      for (String name:names)
      {
        String value = (String) bpd.get(name);
        if (value.toLowerCase().contains("TAN2StepPar"))
//        if (name.contains("TAN2StepParam") && name.endsWith(".id"))
          System.out.println(name + ": " + value);
      }
      
      // Testet das Parsen der BPD
      HBCIPassportPinTan passport = new HBCIPassportPinTan(null,0);
      passport.setBPD(bpd);
    }
    finally
    {
      if (reader != null)
        reader.close();
    }
  }

}



/**********************************************************************
 * $Log: MessageParseTest.java,v $
 * Revision 1.1  2011/05/13 15:07:58  willuhn
 * @N Testcode fuer das Parsen der HITANS-Segmente
 *
 **********************************************************************/