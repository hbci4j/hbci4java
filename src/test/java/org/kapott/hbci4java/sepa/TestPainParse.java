/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPLv2
 *
 **********************************************************************/

package org.kapott.hbci4java.sepa;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet das Parsen von Pain XML-Dateien - ohne HBCI-Context.
 */
public class TestPainParse extends AbstractTest
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
        put("test-pain-parse-00100102.xml", SepaVersion.PAIN_001_001_02);
        put("test-pain-parse-00100202.xml", SepaVersion.PAIN_001_002_02);
        put("test-pain-parse-00100203.xml", SepaVersion.PAIN_001_002_03);
        put("test-pain-parse-00100303.xml", SepaVersion.PAIN_001_003_03);
        put("test-pain-parse-00100103.xml", SepaVersion.PAIN_001_001_03);
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
   * 
   * @throws Exception
   */
  @Test
  public void test002() throws Exception
  {
    InputStream is = null;
    try
    {
      is = this.getStream("test-pain-parse-none.xml");
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
   * 
   * @throws Exception
   */
  @Test
  public void test003() throws Exception
  {
    InputStream is = null;
    try
    {
      is = this.getStream("test-pain-parse-invalid.xml");
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
}
