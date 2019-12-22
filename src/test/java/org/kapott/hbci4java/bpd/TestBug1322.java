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

package org.kapott.hbci4java.bpd;

import java.util.Hashtable;
import java.util.Iterator;

import org.junit.Test;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.kapott.hbci4java.AbstractTest;

/**
 * Test fuer die neuen grossen BPD bei der GAD.
 * BUGZILLA 1322
 */
public class TestBug1322 extends AbstractTest
{
  /**
   * Versucht, die BPD mit dem ueberlangen (mehr als 999 Zeichen) HIVISS Segment in der HBCI-Version
   * "FinTS3" zu parsen.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    try
    {
      String data = getFile("bugzilla-1322.txt");
      HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
      kernel.rawNewMsg("DialogInitAnon");
      
      MsgGen gen = kernel.getMsgGen();
      MSG msg = MSGFactory.getInstance().createMSG("DialogInitAnonRes",data,data.length(),gen);
      Hashtable<String,String> ht = new Hashtable<String,String>();
      msg.extractValues(ht);

      // Wir checken noch, ob in der Testdatei tatsaechlich ein Segment mit
      // mehr als 999 Zeichen drin war. Wenn nicht, deckt die Testdatei
      // den Testfall gar nicht ab.
      
      Iterator<String> it = ht.values().iterator();
      while (it.hasNext())
      {
        String value = it.next();
        if (value.length() > 999)
          return;
      }

      throw new Exception("no BPD segment > 999 chars found");
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }
}

