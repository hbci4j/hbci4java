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

package org.kapott.hbci4java.msg;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet den Abruf der Dauerauftraege.
 */
public class TestDauerSEPAList extends AbstractTest
{
  /**
   * Versucht, die Datei mit dem Response zu parsen.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    final String data = this.getFile("TestDauerSEPAList.txt");
    final HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
    kernel.rawNewMsg("DauerSEPAList2");
    
    final MsgGen gen = kernel.getMsgGen();
    final MSG msg = MSGFactory.getInstance().createMSG("CustomMsgRes",data,data.length(),gen);

    final Hashtable<String,String> ht = new Hashtable<String,String>();
    msg.extractValues(ht);
    
    boolean found = false;
    for (Entry<String,String> e:ht.entrySet())
    {
      if (Objects.equals(e.getKey(),"CustomMsgRes.GVRes.DauerSEPAListRes2.DauerDetails.firstdate"))
      {
        Assert.assertEquals("2025-11-01",e.getValue());
        found = true;
        break;
      }
    }
    
    Assert.assertTrue(found);
  }
}
