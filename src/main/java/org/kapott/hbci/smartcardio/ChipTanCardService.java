/**********************************************************************
 *
 * Copyright (c) 2018 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.smartcardio;

import java.io.ByteArrayOutputStream;

import javax.smartcardio.ResponseAPDU;

import org.kapott.hbci.exceptions.HBCI_Exception;

/**
 * Implementierung des Kartenservice fuer das Verfahren chipTAN-USB.
 */
public class ChipTanCardService extends SmartCardService
{
  /**
   * Sendet das HHDuc an den Kartenleser und liefert die eingegebene TAN zurueck.
   * @param hhduc das HHDuc.
   * @return die TAN.
   */
  public String getTan(String hhduc)
  {
    try
    {
      //////////////////////////////////////////////////////////////////////////
      // SECODER TRANSMIT HHDuc (Secoder 3G)
      // "transmitControlCommand" verwendet intern "ScardControl"
      // "getBasicChannel().transmit()" verwendet intern "ScardTransmit"

      {
        int len = 4 + 1 + 1 + 1 + hhduc.length(); // Laenge von Activation ID, POF, Control Byte, HHD-Laenge, Payload
        ByteArrayOutputStream cmd = new ByteArrayOutputStream();
        cmd.write(new byte[]{0x20,0x76,0x00,0x00});          // Header
        cmd.write(new byte[]{0x00,0x00,(byte) len});         // Li Lc Lc
        cmd.write(new byte[]{0x00,0x00,0x00,0x00});          // Activation ID
        cmd.write(0x01);                                     // POF
        cmd.write(0x00);                                     // Control Byte
        cmd.write((byte) hhduc.length());                    // HHD Laenge
        cmd.write(hhduc.getBytes(SmartCardService.CHARSET)); // Payload
        cmd.write(new byte[]{0x00,0x00});                    // Le Le
        
        byte[] response = this.getCard().transmitControlCommand(this.getFeatures().get(FEATURE_MCT_READER_DIRECT),cmd.toByteArray());
        ResponseAPDU apdu = new ResponseAPDU(response);
        this.check(apdu,new byte[]{(byte)0x90,(byte)0x91});
      }
      //
      //////////////////////////////////////////////////////////////////////////

      //////////////////////////////////////////////////////////////////////////
      // SECODER FINALIZE TRANSACTION (SECODER 3G)
      // this.selectFile(0x3F00);

      {
        ByteArrayOutputStream cmd = new ByteArrayOutputStream();
        cmd.write(new byte[]{0x20,0x77,0x00,0x00});          // Header
        cmd.write(new byte[]{0x00,0x00,0x06});               // Li Lc Lc
        cmd.write(new byte[]{0x00,0x00,0x00,0x00});          // Activation ID
        cmd.write(0x00);                                     // POF
        cmd.write(0x00);                                     // Control Byte
        cmd.write(hhduc.getBytes(SmartCardService.CHARSET)); // Payload
        cmd.write(new byte[]{0x00,0x00});                    // Le Le
        
        byte[] response = this.getCard().transmitControlCommand(this.getFeatures().get(FEATURE_MCT_READER_DIRECT),cmd.toByteArray());
        ResponseAPDU apdu = new ResponseAPDU(response);
        this.check(apdu,new byte[]{(byte)0x90,(byte)0x91});
        
        return new String(apdu.getData(),SmartCardService.CHARSET);
      }

      //
      //////////////////////////////////////////////////////////////////////////
    }
    catch (HBCI_Exception e)
    {
      throw e;
    }
    catch (Exception e2)
    {
      throw new HBCI_Exception(e2);
    }
  }
}


