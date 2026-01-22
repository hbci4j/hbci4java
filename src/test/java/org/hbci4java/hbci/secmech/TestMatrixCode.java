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

package org.hbci4java.hbci.secmech;

import java.util.Properties;

import org.hbci4java.hbci.AbstractTest;
import org.hbci4java.hbci.manager.HHDVersion;
import org.hbci4java.hbci.manager.MatrixCode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testet den Parser fuer die Matrix-Codes.
 */
public class TestMatrixCode extends AbstractTest
{
  /**
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    byte[] data = this.getBytes("TestMatrixCode-001.txt");
    MatrixCode code = new MatrixCode(data);
    Assert.assertEquals("Mime-Type falsch", "image/png", code.getMimetype());
    Assert.assertEquals("Bild-Groesse falsch", 4556, code.getImage().length);
  }

  /**
   * @throws Exception
   */
  @Test
  public void test002() throws Exception
  {
    byte[] data = this.getBytes("TestMatrixCode-002.txt");
    MatrixCode code = new MatrixCode(data);
    Assert.assertEquals("Mime-Type falsch", "image/png", code.getMimetype());
    Assert.assertEquals("Bild-Groesse falsch", 4980, code.getImage().length);
  }

  /**
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public void test003() throws Exception
  {
    new MatrixCode((byte[]) null);
  }

  /**
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public void test004() throws Exception
  {
    new MatrixCode("zu kurz");
  }

  /**
   * @throws Exception
   */
  @Test
  public void test005() throws Exception
  {
    // Testet die Erkennung des Matrix-Code-Verfahrens aus dem Secmech.
    Properties props = new Properties();
    props.put("id", "MS1.0.0");
    props.put("segversion", "5");

    HHDVersion version = HHDVersion.find(props);
    Assert.assertEquals("Matrix-Code-Verfahren nicht erkannt", HHDVersion.MS_1, version);
  }


  /**
   * @throws Exception
   */
  @Test
  public void test006() throws Exception
  {
    // Testet die Erkennung des Matrix-Code-Verfahrens aus dem Secmech.
    Properties props = new Properties();
    props.put("id", "photoTAN");
    props.put("name", "photoTAN QRcode");
    props.put("segversion", "6");

    HHDVersion version = HHDVersion.find(props);
    Assert.assertEquals("Matrix-Code-Verfahren nicht erkannt", HHDVersion.MS_1, version);
  }

}
