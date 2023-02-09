/**********************************************************************
 *
 * Copyright (c) 2023 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci4java.swift;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci4java.AbstractTest;

/**
 * 
 */
public class TestMT940Parse extends AbstractTest
{
  /**
   * Prüft, dass Tag :61: mit Buchungsdatum gelesen werden kann.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    final String mt940 = new String(this.getBytes("test-mt940-001.sta"),StandardCharsets.ISO_8859_1);
    GVRKUms ums = new GVRKUms();
    ums.appendMT940Data(mt940);
    final List<UmsLine> lines = ums.getFlatData();
    Assert.assertEquals(2,lines.size());
    for (UmsLine line:lines)
    {
      Assert.assertNotNull(line.valuta);
      Assert.assertNotNull(line.bdate);
    }
  }

  /**
   * Prüft, dass Tag :61: ihne Buchungsdatum gelesen werden kann.
   * @throws Exception
   */
  @Test
  public void test002() throws Exception
  {
    final String mt940 = new String(this.getBytes("test-mt940-002.sta"),StandardCharsets.ISO_8859_1);
    GVRKUms ums = new GVRKUms();
    ums.appendMT940Data(mt940);
    final List<UmsLine> lines = ums.getFlatData();
    Assert.assertEquals(2,lines.size());
    for (UmsLine line:lines)
    {
      Assert.assertNotNull(line.valuta);
      Assert.assertNotNull(line.bdate);
    }
  }

}


