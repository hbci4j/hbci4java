/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPLv2
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
    QRCode code = new QRCode(null,data);
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
