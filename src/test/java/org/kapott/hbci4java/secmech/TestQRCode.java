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

package org.kapott.hbci4java.secmech;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.manager.QRCode;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet den Parser fuer die QR-Codes.
 */
public class TestQRCode extends AbstractTest
{
  /**
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    String data = new String(this.getBytes("TestQRCode-001.txt"),Comm.ENCODING);
    String msg = "Sie haben eine \"Einzelüberweisung\" erfasst: Überprüfen Sie die Richtigkeit der \"letzten 10 Zeichen der IBAN des Empfängers\" bei dem Institut \"MUSTER-BANK\" und bestätigen Sie diese mit der Taste \"OK\". Überprüfen Sie die Richtigkeit des \"Betrags\" und bestätigen Sie diesen mit der Taste \"OK\".";
    QRCode code = new QRCode("1234",data);
    Assert.assertEquals("Mime-Type falsch", "image/png", code.getMimetype());
    
    byte[] image = code.getImage();
    
    Assert.assertTrue("Bild zu klein", image.length > 5);
    Assert.assertTrue("Bild kein PNG", (image[0] & 0xFF) == 0x89 && (image[1] & 0xFF) == 0x50 && (image[2] & 0xFF) == 0x4E && (image[3] & 0xFF) == 0x47);
    Assert.assertEquals("Bild-Groesse falsch", 456, image.length);
    Assert.assertNotNull("Text fehlt",code.getMessage());
    Assert.assertEquals("Text falsch",msg,code.getMessage());
  }

  /**
   * @throws Exception
   */
  @Test(expected = Exception.class)
  public void test002() throws Exception
  {
    new QRCode(null,null);
  }
}
