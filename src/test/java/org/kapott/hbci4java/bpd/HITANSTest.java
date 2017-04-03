/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPLv2
 *
 **********************************************************************/

package org.kapott.hbci4java.bpd;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet das Parsen der HITANS-Segmente aus den BPD.
 */
public class HITANSTest extends AbstractTest
{
  /**
   * Liefert Pseudo-BPD aus der angegebenen Datei.
   * @param file der Dateiname.
   * @param version die HBCI-Version.
   * @return die Pseudo-BPD.
   * @throws Exception
   */
  private Properties getBPD(String file, String version) throws Exception
  {
    String data = getFile(file);
    HBCIKernelImpl kernel = new HBCIKernelImpl(null,version);
    kernel.rawNewMsg("DialogInitAnon");
    
    MsgGen gen = kernel.getMsgGen();
    MSG msg = MSGFactory.getInstance().createMSG("DialogInitAnonRes",data,data.length(),gen);
    Hashtable<String,String> ht = new Hashtable<String,String>();
    msg.extractValues(ht);
      
    // Prefix abschneiden
    Properties bpd = new Properties();
    for (Enumeration<String> e=ht.keys();e.hasMoreElements();)
    {
      String name = e.nextElement();
      String value = ht.get(name);
        
      if (name.startsWith("DialogInitAnonRes."))
        name = name.replace("DialogInitAnonRes.","");
      if (name.startsWith("BPD."))
        name = name.replace("BPD.","");
      bpd.put(name,value);
    }
    
    return bpd;
  }
  
  /**
   * Testet, dass das HITANS-Segment in Version 5 korrekt geladen wird.
   * @throws Exception
   */
  @Test
  public void testHitans5() throws Exception
  {
    Properties bpd = getBPD("bpd2-formatted.txt","300");
    Enumeration names = bpd.propertyNames();
    
    int version = 0;

    while (names.hasMoreElements())
    {
      String name  = (String) names.nextElement();
      String value = bpd.getProperty(name);
      
      // Das darf kein Template-Parameter sein
      if (value.equals("HITANS"))
        Assert.assertFalse(name.contains("Template"));
      
      // Hoechste Versionsnummer holen. Die muss 5 sein
      if (name.contains("TAN2StepPar") && name.endsWith("SegHead.version"))
      {
        int newVersion = Integer.parseInt(value);
        if (newVersion > version)
          version = newVersion;
      }
    }
    Assert.assertEquals(version,5);
  }
  
  /**
   * Testet das Ermitteln der TAN-Verfahren.
   * @throws Exception
   */
  @Test
  public void testCurrentSecMechInfo() throws Exception
  {
    Properties bpd = getBPD("bpd2-formatted.txt","300");
    HBCIPassportPinTan passport = new HBCIPassportPinTan(null,0);
    passport.setCurrentTANMethod("942");
    passport.setBPD(bpd);
    
    Properties secmech = passport.getCurrentSecMechInfo();
    
    // secmech darf nicht null sein
    Assert.assertNotNull(secmech);
    
    // Das TAN-Verfahren 942 gibts in den BPD drei mal. In HITANS 5, 4 und 2.
    // Der Code muss die Version aus der aktuellsten Segment-Version liefern.
    Assert.assertEquals(secmech.getProperty("segversion"),"5");
  }
}
