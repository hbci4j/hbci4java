/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/hbci4java/secmech/ChallengeInfoTest.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/05/17 16:39:38 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.secmech;

import hbci4java.AbstractTest;

import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.kapott.hbci.manager.ChallengeInfo;
import org.kapott.hbci.manager.ChallengeInfo.HhdVersion;
import org.kapott.hbci.manager.ChallengeInfo.Job;
import org.kapott.hbci.manager.ChallengeInfo.Param;

/**
 * Tests fuer die ChallengeInfo-Klasse.
 */
public class ChallengeInfoTest extends AbstractTest
{
  /**
   * Liefert die Challenge-Daten fuer einen Geschaeftsvorfall in einer HHD-Version.
   * @param code der Geschaeftsvorfall-Code.
   * @param version die HHD-Version.
   * @return die Challenge-Daten.
   */
  private HhdVersion getHhdVersion(String code, String version)
  {
    ChallengeInfo info = ChallengeInfo.getInstance();
    Job job = info.getData(code);
    return job != null ? job.getVersion(version) : null;
  }
  
  
  /**
   * Testet, dass fuer eine unbekannte HHD-Version oder einen unbekannten
   * Geschaeftsvorfall auch wirklich nichts geliefert wird.
   */
  @Test
  public void testInvalid()
  {
    Assert.assertNull(getHhdVersion("UNDEF",ChallengeInfo.VERSION_HHD_1_4));
    Assert.assertNull(getHhdVersion("HKAOM","hhd15"));
  }
  
  /**
   * Testet, wenn fuer einen Geschaeftsvorfall in der HHD-Version keine Parameter vorhanden sind
   */
  @Test
  public void testMissing()
  {
    HhdVersion version = getHhdVersion("HKDTE",ChallengeInfo.VERSION_HHD_1_4);
    Assert.assertEquals(version.getParams().size(),0);
  }
  
  /**
   * Testet die korrekten Challenge-Klassen.
   */
  @Test
  public void testKlass()
  {
    HhdVersion version = null;
    String code        = null;
    
    code = "HKAOM";
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_2);
    Assert.assertEquals(version.getKlass(),"20");

    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_3);
    Assert.assertEquals(version.getKlass(),"20");
  
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_4);
    Assert.assertEquals(version.getKlass(),"10");


    code = "HKCCS";
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_2);
    Assert.assertEquals(version.getKlass(),"22");

    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_3);
    Assert.assertEquals(version.getKlass(),"22");
  
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_4);
    Assert.assertEquals(version.getKlass(),"09");
  }

  /**
   * Testet, ob ein Parameter als Value korrekt erkannt wird.
   */
  @Test
  public void testValue()
  {
    HhdVersion version = null;
    String code        = "HKAOM";
    List<Param> params = null;
    
    
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_2);
    params = version.getParams();
    Assert.assertEquals(params.size(),3);
    for (Param p:params)
    {
      if (p.getPath().equals("BTG.value"))
        Assert.assertEquals(p.getType(),"value");
    }

    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_3);
    params = version.getParams();
    Assert.assertEquals(params.size(),4);
    for (Param p:params)
    {
      if (p.getPath().equals("BTG.value"))
        Assert.assertEquals(p.getType(),"value");
    }

    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_4);
    Assert.assertEquals(params.size(),4);
    params = version.getParams();
    for (Param p:params)
    {
      if (p.getPath().equals("BTG.value"))
        Assert.assertEquals(p.getType(),"value");
    }
  }
  
  /**
   * Testet Parameter mit Bedingung.
   */
  @Test
  public void testCondition()
  {
    HhdVersion version = null;
    String code        = "HKAOM";
    List<Param> params = null;
    

    Properties secmech = new Properties();
    secmech.setProperty("needchallengevalue","N");
    
    // Darf nicht enthalten sein
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_2);
    params = version.getParams();
    for (Param p:params)
    {
      if (p.getPath().equals("BTG.value"))
        Assert.assertFalse(p.isComplied(secmech));
    }

    // Darf nicht enthalten sein
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_3);
    params = version.getParams();
    for (Param p:params)
    {
      if (p.getPath().equals("BTG.value"))
        Assert.assertFalse(p.isComplied(secmech));
    }

    // Hier ist er enthalten - auch wenn in den BPD etwas anderes steht
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_4);
    params = version.getParams();
    for (Param p:params)
    {
      if (p.getPath().equals("BTG.value"))
        Assert.assertTrue(p.isComplied(secmech));
    }
    
  }
  
  /**
   * Testet Parameter mit Bedingung.
   */
  @Test
  public void testCondition2()
  {
    HhdVersion version = null;
    String code        = "HKCCS";
    List<Param> params = null;
    

    Properties secmech = new Properties();
    secmech.setProperty("needchallengevalue","J");
    
    // Jetzt muss er enthalten sein
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_2);
    params = version.getParams();
    for (Param p:params)
    {
      if (p.getPath().equals("sepa.btg.value"))
        Assert.assertTrue(p.isComplied(secmech));
    }

    // Jetzt muss er enthalten sein
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_3);
    params = version.getParams();
    for (Param p:params)
    {
      if (p.getPath().equals("sepa.btg.value"))
        Assert.assertTrue(p.isComplied(secmech));
    }

    // Und hier bleibt er weiterhin enthalten
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_4);
    params = version.getParams();
    for (Param p:params)
    {
      if (p.getPath().equals("sepa.btg.value"))
        Assert.assertTrue(p.isComplied(secmech));
    }
    
  }
  
}



/**********************************************************************
 * $Log: ChallengeInfoTest.java,v $
 * Revision 1.1  2011/05/17 16:39:38  willuhn
 * @N Unit-Tests
 *
 **********************************************************************/