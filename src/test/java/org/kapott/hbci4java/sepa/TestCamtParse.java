/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPLv2
 *
 **********************************************************************/

package org.kapott.hbci4java.sepa;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.BTag;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet das Parsen von CAMT XML-Dateien - ohne HBCI-Context.
 */
public class TestCamtParse extends AbstractTest
{
  /**
   * Testet das korrekte Ermitteln der PAIN-Version aus dem XML-Dokument.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    Map<String, SepaVersion> files = new HashMap<String, SepaVersion>() {
      {
        put("test-camt-parse-05200102.xml", SepaVersion.CAMT_052_001_02);
      }
    };

    for (Entry<String, SepaVersion> entry : files.entrySet())
    {
      InputStream is = null;
      try
      {
        is = this.getStream(entry.getKey());
        SepaVersion version = SepaVersion.autodetect(is);
        Assert.assertEquals(version, entry.getValue());
      }
      finally
      {
        if (is != null)
          is.close();
      }
    }
  }

  /**
   * Testet die Autodetection fuer eine XML-Datei ohne Namespace.
   * @throws Exception
   */
  @Test
  public void test002() throws Exception
  {
    InputStream is = null;
    try
    {
      is = this.getStream("test-camt-parse-none.xml");
      SepaVersion version = SepaVersion.autodetect(is);
      Assert.assertNull(version);
    }
    finally
    {
      if (is != null)
        is.close();
    }
  }

  /**
   * Testet das Fehlschlagen der Autodetection fuer eine XML-Datei mit falschem
   * Namespace.
   * @throws Exception
   */
  @Test
  public void test003() throws Exception
  {
      InputStream is = null;
      try
      {
        is = this.getStream("test-camt-parse-invalid.xml");
        SepaVersion.autodetect(is);
        Assert.fail();
      }
      catch (IllegalArgumentException iae)
      {
        Assert.assertEquals(iae.getClass(), IllegalArgumentException.class);
      }
      finally
      {
        if (is != null)
          is.close();
      }
  }
  
  /**
   * Testet das Lesen einer CAMT-Datei.
   * @throws Exception
   */
  @Test
  public void test004() throws Exception
  {
      final String file = "test-camt-parse-05200102.xml";
      InputStream is1 = null;
      InputStream is2 = null;
      try
      {
        is1 = this.getStream(file);
        SepaVersion version = SepaVersion.autodetect(is1);
        ISEPAParser<GVRKUms> parser = SEPAParserFactory.get(version);
        
        is2 = this.getStream(file);
        GVRKUms ums = new GVRKUms();
        parser.parse(is2,ums);
        
        List<BTag> days = ums.getDataPerDay();
        Assert.assertEquals("Anzahl Tage falsch",1,days.size());
        
        BTag day = days.get(0);
        List<UmsLine> lines = day.lines;
        Assert.assertEquals("Anzahl Buchungen falsch",2,lines.size());
        
        Assert.assertTrue("Startsaldo falsch",new BigDecimal("100").compareTo(day.start.value.getBigDecimalValue()) == 0);
        Assert.assertTrue("Endsaldo falsch",new BigDecimal("110.50").compareTo(day.end.value.getBigDecimalValue()) == 0);
        
        // TODO: Hier noch weitere Tests
      }
      finally
      {
        if (is1 != null) is1.close();
        if (is2 != null) is2.close();
      }
  }

}
