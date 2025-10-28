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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.bpd.VoPParameter;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet das Parsen der BPD mit VoP.
 */
public class TestParseVoP extends AbstractTest
{
  /**
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    try
    {
      String data = getFile("bpd-vop.txt");
      HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
      kernel.rawNewMsg("Synch");
      
      MsgGen gen = kernel.getMsgGen();
      MSG msg = MSGFactory.getInstance().createMSG("SynchRes",data,data.length(),gen);
      Hashtable<String,String> ht = new Hashtable<String,String>();
      msg.extractValues(ht);
      
      final List<String> keys = new ArrayList(ht.keySet());
      Collections.sort(keys);

      final Properties bpd = new Properties();

      for (String s:keys)
      {
        final String value = ht.get(s);
        if (Objects.equals(value,"urn:iso:std:iso:20022:tech:xsd:pain.002.001.10"))
        {
          // Das TAN-Verfahren muss korrekt erkannt worden sein und in "ParVoPCheck" landen.
          // Es darf nicht in "Template2DPar" stehen.
          Assert.assertTrue(s.contains(".ParVoPCheck."));
          Assert.assertTrue(s.endsWith(".suppreports"));
        }
        bpd.setProperty(s.replace("SynchRes.BPD.",""),value);
        
        if (Objects.equals(value,"HICDBS"))
        {
          // Die BPD müssen für beide Segment-Versionen korrekt erkannt werden
          Assert.assertTrue(s.contains("DauerSEPAListPar"));
        }
      }
      
      
      final VoPParameter params = VoPParameter.parse(bpd);
      Assert.assertNotNull(params);
      
      final List<SepaVersion> vopVersions = params.getFormats();
      Assert.assertNotNull(vopVersions);
      Assert.assertEquals(1,vopVersions.size());
      Assert.assertEquals(SepaVersion.PAIN_002_001_10,vopVersions.get(0));
      Assert.assertFalse(params.isInfoTextFormatted());
      
      final List<String> codes = params.getGvCodes();
      Assert.assertEquals(17,codes.size());
      Assert.assertTrue(codes.contains("HKIPZ"));
      
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }
}

