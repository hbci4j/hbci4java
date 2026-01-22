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
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import org.hbci4java.hbci.AbstractTest;
import org.hbci4java.hbci.manager.HBCIKernelImpl;
import org.hbci4java.hbci.manager.MsgGen;
import org.hbci4java.hbci.protocol.MSG;
import org.hbci4java.hbci.protocol.factory.MSGFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testet, dass das "HICDBS" (DauerSEPAList) korrekt erkannt wird.
 */
public class TestParseBDPDeutscheBank extends AbstractTest
{
  /**
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    try
    {
      final String data = getFile("bpd-deutschebank.txt");
      final HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
      kernel.rawNewMsg("Synch");
      
      final MsgGen gen = kernel.getMsgGen();
      final MSG msg = MSGFactory.getInstance().createMSG("SynchRes",data,data.length(),gen);
      final Hashtable<String,String> ht = new Hashtable<String,String>();
      msg.extractValues(ht);
      
      final List<String> keys = new ArrayList(ht.keySet());
      Collections.sort(keys);
      
      for (String s:keys)
      {
        final String value = ht.get(s);
        if (Objects.equals(value,"HICDBS"))
        {
          Assert.assertTrue(s.contains("DauerSEPAListPar"));
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }
}

