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

package org.kapott.hbci4java.sepa;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRVoP.VoPResult;
import org.kapott.hbci.GV_Result.GVRVoP.VoPResultItem;
import org.kapott.hbci.GV_Result.GVRVoP.VoPStatus;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet das Parsen von Pain 002 Nachrichten mit dem VoP-Ergebnis.
 */
public class TestParseVoPResult extends AbstractTest
{
  /**
   * Testet das Lesen einer PAIN.002 Datei, bei der der Namespace fehlt.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    this.doTest("test-pain-parse-0020110-01.xml");
  }
  
  /**
   * Testet das Lesen einer PAIN.002 Datei mit Namespace.
   * @throws Exception
   */
  @Test
  public void test002() throws Exception
  {
    this.doTest("test-pain-parse-0020110-02.xml");
  }

  /**
   * Testet das Lesen einer PAIN.002 Datei mit Namespace.
   * @throws Exception
   */
  @Test
  public void test003() throws Exception
  {
    try (InputStream is = this.getStream("test-pain-parse-0020110-03.xml"))
    {
      ISEPAParser<List<VoPResultItem>> parser = SEPAParserFactory.get(SepaVersion.PAIN_002_001_10);
      final VoPResult result = new VoPResult();
      parser.parse(is,result.getItems());
      Assert.assertEquals(1,result.getItems().size());
      
      final VoPResultItem item = result.getItems().get(0);
      Assert.assertEquals("Status falsch",VoPStatus.MATCH,item.getStatus());
      Assert.assertNull("IBAN falsch",item.getIban());
      Assert.assertNull("Name falsch",item.getName());
      Assert.assertNull("Originaler Name falsch",item.getOriginal());
      Assert.assertNull("Text falsch",item.getText());
      Assert.assertNull("Usage falsch",item.getUsage());
      Assert.assertNull("Betrag falsch",item.getAmount());

      final boolean needCallback = result.getItems().stream().filter(r -> !Objects.equals(r.getStatus(),VoPStatus.MATCH)).count() > 0;
      Assert.assertFalse(needCallback);
    }
  }

  /**
   * Führt den Test durch.
   * @param file die Testdatei.
   * @throws Exception
   */
  private void doTest(String file) throws Exception
  {
    InputStream is = null;
    try
    {
      is = this.getStream(file);
      ISEPAParser<List<VoPResultItem>> parser = SEPAParserFactory.get(SepaVersion.PAIN_002_001_10);
      final VoPResult result = new VoPResult();
      parser.parse(is,result.getItems());
      Assert.assertEquals(1,result.getItems().size());
      
      final VoPResultItem item = result.getItems().get(0);
      Assert.assertEquals("Status falsch",VoPStatus.NO_MATCH,item.getStatus());
      Assert.assertEquals("IBAN falsch","DE12345678901234567890",item.getIban());
      Assert.assertEquals("Name falsch","",item.getName());
      Assert.assertEquals("Originaler Name falsch","Max Mustermann",item.getOriginal());
      Assert.assertNull("Text falsch",item.getText());
      Assert.assertNull("Usage falsch",item.getUsage());
      Assert.assertNull("Betrag falsch",item.getAmount());

      final boolean needCallback = result.getItems().stream().filter(r -> !Objects.equals(r.getStatus(),VoPStatus.MATCH)).count() > 0;
      Assert.assertTrue(needCallback);
    }
    finally
    {
      if (is != null)
        is.close();
    }
  }
}
