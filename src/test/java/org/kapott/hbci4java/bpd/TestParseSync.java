/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPLv2
 *
 **********************************************************************/

package org.kapott.hbci4java.bpd;

import java.util.Hashtable;

import org.junit.Test;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.kapott.hbci4java.AbstractTest;

/**
 */
public class TestParseSync extends AbstractTest
{
  /**
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    try
    {
      String data = getFile("bpd-psd2-consors.txt");
      HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
      kernel.rawNewMsg("Synch");
      
      MsgGen gen = kernel.getMsgGen();
      MSG msg = MSGFactory.getInstance().createMSG("SynchRes",data,data.length(),gen);
      Hashtable<String,String> ht = new Hashtable<String,String>();
      msg.extractValues(ht);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }
}

