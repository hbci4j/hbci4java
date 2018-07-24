/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/hbci4java/msg/TestBug1129.java,v $
 * $Revision: 1.1 $
 * $Date: 2012/03/06 23:18:26 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci4java.msg;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci4java.AbstractTest;

/**
 * Tests fuer BUGZILLA 1806.
 */
public class TestBug1806 extends AbstractTest
{
  /**
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    String data = getFile("bugzilla-1806.txt");
    HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
    kernel.rawNewMsg("SepaDauerList");
    
    MsgGen gen = kernel.getMsgGen();
    MSG msg = MSGFactory.getInstance().createMSG("CustomMsgRes",data,data.length(),gen);

    Hashtable<String,String> ht = new Hashtable<String,String>();
    msg.extractValues(ht);
    
    List<String> keys = new ArrayList<String>(ht.keySet());
    Collections.sort(keys);
    for (String key:keys)
    {
      if (!key.endsWith(".sepapain"))
        continue;
      
      ByteArrayInputStream bis = new ByteArrayInputStream(ht.get(key).getBytes(Comm.ENCODING));
      SepaVersion version = SepaVersion.autodetect(bis);
      Assert.assertNotNull(version);
      ISEPAParser<List<Properties>> parser = SEPAParserFactory.get(version);
      
      bis.reset();
      
      List<Properties> sepaResults = new ArrayList<Properties>();
      parser.parse(bis,sepaResults);
      Assert.assertTrue(sepaResults.size() > 0);
      for (int i=0;i<sepaResults.size();++i)
      {
        System.out.println("\nDatensatz: " + (i+1));
        
        Properties props = sepaResults.get(i);
        for (Entry e:props.entrySet())
        {
          System.out.println(e.getKey() + ": " + e.getValue());
        }
      }
    }
  }
}
