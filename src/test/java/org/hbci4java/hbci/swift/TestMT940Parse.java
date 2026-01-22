/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2023 Olaf Willuhn
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

package org.hbci4java.hbci.swift;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.hbci4java.hbci.GV_Result.GVRKUms;
import org.hbci4java.hbci.GV_Result.GVRKUms.UmsLine;
import org.hbci4java.hbci.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

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


