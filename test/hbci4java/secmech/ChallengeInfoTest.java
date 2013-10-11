/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/hbci4java/secmech/ChallengeInfoTest.java,v $
 * $Revision: 1.6 $
 * $Date: 2011/05/20 11:03:28 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.secmech;

import hbci4java.AbstractTest;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;

import org.junit.Test;
import org.kapott.hbci.exceptions.InitializingException;
import org.kapott.hbci.manager.ChallengeInfo;
import org.kapott.hbci.manager.ChallengeInfo.HhdVersion;
import org.kapott.hbci.manager.ChallengeInfo.Job;
import org.kapott.hbci.manager.ChallengeInfo.Param;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;

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
   * Testet, ob ein Parameter korrekt als SyntaxWrt erkannt und formatiert wird.
   */
  @Test
  public void testWrt()
  {
    HhdVersion version = null;
    Param p            = null;
    String code        = "HKAOM";
    
    
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_2);
    p = version.getParams().get(1);
    Assert.assertEquals(p.getPath(),"BTG.value");
    Assert.assertEquals(p.getType(),"Wrt");
    Assert.assertEquals(p.format("100"),"100,");
    Assert.assertEquals(p.format("100.50"),"100,5");
    Assert.assertEquals(p.format("100.99"),"100,99");
    Assert.assertNull(p.format(null));
    
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_3);
    p = version.getParams().get(2);
    Assert.assertEquals(p.getPath(),"BTG.value");
    Assert.assertEquals(p.getType(),"Wrt");
    Assert.assertEquals(p.format("100"),"100,");
    Assert.assertEquals(p.format("100.50"),"100,5");
    Assert.assertEquals(p.format("100.99"),"100,99");
    Assert.assertNull(p.format(null));
    

    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_4);
    p = version.getParams().get(0);
    Assert.assertEquals(p.getPath(),"BTG.value");
    Assert.assertEquals(p.getType(),"Wrt");
    Assert.assertEquals(p.format("100"),"100,");
    Assert.assertEquals(p.format("100.50"),"100,5");
    Assert.assertEquals(p.format("100.99"),"100,99");
    Assert.assertNull(p.format(null));
  }

  /**
   * Testet, ob ein Parameter korrekt als SyntaxAN erkannt und formatiert wird.
   */
  @Test
  public void testAN()
  {
    HhdVersion version = null;
    Param p            = null;
    String code        = "HKAOM";
    
    
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_2);
    p = version.getParams().get(0);
    Assert.assertEquals(p.getPath(),"Other.number");
    Assert.assertEquals(p.getType(),"");
    Assert.assertEquals(p.format("AaBb"),"AaBb");
    
    // Hier darf KEIN Escaping stattfinden. Das macht HBCI4Java dann spaeter
    // ohnehin beim Zusammenbauen des Segments, da ChallengeKlassParams#param[1-9]
    // ja in hbci-{version}.xml als Type="AN" deklariert sind.
    Assert.assertEquals(p.format("+:'@"),"+:'@");
    Assert.assertNull(p.format(null));
    
    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_3);
    p = version.getParams().get(1);
    Assert.assertEquals(p.getPath(),"Other.number");
    Assert.assertEquals(p.getType(),"");
    Assert.assertEquals(p.format("AaBb"),"AaBb");
    Assert.assertEquals(p.format("+:'@"),"+:'@");
    Assert.assertNull(p.format(null));
    

    version = getHhdVersion(code,ChallengeInfo.VERSION_HHD_1_4);
    p = version.getParams().get(3);
    Assert.assertEquals(p.getPath(),"Other.number");
    Assert.assertEquals(p.getType(),"");
    Assert.assertEquals(p.format("AaBb"),"AaBb");
    Assert.assertEquals(p.format("+:'@"),"+:'@");
    Assert.assertNull(p.format(null));
  }

  /**
   * Testet, ob ein Parameter korrekt als SyntaxDate erkannt wird.
   * @throws Exception
   */
  @Test
  public void testDate() throws Exception
  {
    HhdVersion version = getHhdVersion("HKTUE",ChallengeInfo.VERSION_HHD_1_4);
    Param p = version.getParams().get(3);
    Assert.assertEquals(p.getPath(),"date");
    Assert.assertEquals(p.getType(),"Date");
    Assert.assertEquals(p.format("2011-05-20"),"20110520");
    Assert.assertNull(p.format(null));

    try
    {
      p.format("invalid-date");
      throw new Exception("hier duerfen wir nicht ankommen");
    }
    catch (InitializingException e)
    {
      Assert.assertEquals(InitializingException.class,e.getClass());
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
  
  /**
   * Testet, dass die Challenge-Parameter korrekt in die DEG eingetragen werden.
   * @throws Exception
   */
  @Test
  public void testDEG() throws Exception
  {
    InputStream is = null;
    try
    {
      is = new BufferedInputStream(new FileInputStream("src/hbci-300.xml"));
      MsgGen gen = new MsgGen(is);
      
      String name = "CustomMsg";
      Hashtable props = new Hashtable();

      props.put("CustomMsg.MsgHead.dialogid","H11051813102140");
      props.put("CustomMsg.MsgHead.msgnum","3");
      props.put("CustomMsg.MsgTail.msgnum","3");
      
      props.put("CustomMsg.GV.TAN2Step5","requested");
      
      props.put("CustomMsg.GV.TAN2Step5.process","1");
      props.put("CustomMsg.GV.TAN2Step5.ordersegcode","HKDAN");
      props.put("CustomMsg.GV.TAN2Step5.OrderAccount.number","12345678");
      props.put("CustomMsg.GV.TAN2Step5.OrderAccount.KIK.country","DE");
      props.put("CustomMsg.GV.TAN2Step5.OrderAccount.KIK.blz","12345678");
      props.put("CustomMsg.GV.TAN2Step5.orderhash","B12345");
      props.put("CustomMsg.GV.TAN2Step5.notlasttan","N");
      props.put("CustomMsg.GV.TAN2Step5.offset","");
      props.put("CustomMsg.GV.TAN2Step5.challengeklass","43");

      // "param1" lassen wir bewusst weg. Wir wollen sehen, dass der Platzhalter trotzdem bleibt
      props.put("CustomMsg.GV.TAN2Step5.ChallengeKlassParams.param2","201,");
      props.put("CustomMsg.GV.TAN2Step5.ChallengeKlassParams.param3","12345");
      // "param4" lassen wir mittendrin ebenfals weg
      props.put("CustomMsg.GV.TAN2Step5.ChallengeKlassParams.param5","Param 5");

      MSG msg = new MSG(name,gen,props);
      
      String generated = msg.toString(0);
      String expected = "HNHBK:1:3+000000000137+300+H11051813102140+3'HKTAN:2:5+1+HKDAN+12345678::280:12345678+@5@12345+++N+++43+:201,:12345::Param 5'HNHBS:3:1+3'";
                                                                                                                              // ^^^^^^^^^^^^^^^^^^^^
      // Das sind die relevanten Params. Die letzten duerfen weggelassen werden, aber nicht am Anfang.
      Assert.assertEquals(generated,expected);
    }
    finally
    {
      if (is != null)
        is.close();
    }
  }
}



/**********************************************************************
 * $Log: ChallengeInfoTest.java,v $
 * Revision 1.6  2011/05/20 11:03:28  willuhn
 * @N 18-hbci4java-challengeparam-no-an-syntax.patch
 *
 * Revision 1.5  2011-05-20 10:49:44  willuhn
 * @N Tests erweitert fuer neue SyntaxDE-basierte Formatierung der Werte
 *
 * Revision 1.4  2011-05-18 17:16:19  willuhn
 * @N Test fuer das Datumsformat
 *
 * Revision 1.3  2011-05-18 13:50:04  willuhn
 * @N Test fuer die korrekte Erzeugung des DEG
 *
 * Revision 1.2  2011-05-18 13:36:23  willuhn
 * @N Test fuer die korrekte Erzeugung des DEG
 *
 * Revision 1.1  2011-05-17 16:39:38  willuhn
 * @N Unit-Tests
 *
 **********************************************************************/