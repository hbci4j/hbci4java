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

package org.hbci4java.hbci.msg;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.hbci4java.hbci.GV.parsers.ISEPAParser;
import org.hbci4java.hbci.GV.parsers.SEPAParserFactory;
import org.hbci4java.hbci.comm.Comm;
import org.hbci4java.hbci.AbstractTest;
import org.hbci4java.hbci.manager.HBCIKernelImpl;
import org.hbci4java.hbci.manager.MsgGen;
import org.hbci4java.hbci.protocol.MSG;
import org.hbci4java.hbci.protocol.factory.MSGFactory;
import org.hbci4java.hbci.sepa.SepaVersion;
import org.junit.Assert;
import org.junit.Test;

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
