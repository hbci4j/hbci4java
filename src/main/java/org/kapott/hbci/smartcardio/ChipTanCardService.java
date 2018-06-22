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

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * Implementierung des Kartenservice fuer das Verfahren chipTAN-USB.
 */
public class ChipTanCardService extends SmartCardService
{
  /**
   * Sendet das HHDuc an den Kartenleser und liefert die eingegebene TAN zurueck.
   * @param hhduc das HHDuc.
   * @return die TAN. Oder NULL, wenn sie nicht zur Uebertragung ausgewaehlt wurde.
   */
  public String getTan(String hhduc)
  {
    try
    {
      Integer feature = this.getFeatures().get(Feature.FEATURE_MCT_READER_DIRECT);
      if (feature != null)
        return this.getTanScardControl(hhduc);
      else
        return this.getTanScardTransmit(hhduc);
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

  /**
   * Sendet das HHDuc per ScardControl an den Kartenleser und liefert die eingegebene TAN zurueck.
   * @param hhduc das HHDuc.
   * @return die TAN. Oder NULL, wenn sie nicht zur Uebertragung ausgewaehlt wurde.
   * @throws Exception
   */
  public String getTanScardControl(String hhduc) throws Exception
  {
    String tan = null;
    
    //////////////////////////////////////////////////////////////////////////
    // SECODER TRANSMIT HHDuc (Secoder 3G)

    {
      // Flickercode als Byte-Array rendern
      byte[] hhdBytes = this.toBytes(hhduc);
      
      HBCIUtils.log("sending HHDuc to chipcard using ScardControl, length: " + hhdBytes.length,HBCIUtils.LOG_INFO);
      
      int len = 4 + 1 + 1 + 2 + hhdBytes.length; // Laenge von Activation ID, POF, Control Byte, HHD-Laenge, Payload
      ByteArrayOutputStream cmd = new ByteArrayOutputStream();
      cmd.write(new byte[]{(byte)0x20,(byte)0x76,(byte)0x00,(byte)0x00}); // Header
      cmd.write(new byte[]{0x00,0x00,(byte) len});                        // Li Lc Lc
      cmd.write(new byte[]{0x00,0x00,0x00,0x00});                         // Activation ID
      cmd.write(0x01);                                                    // POF
      cmd.write(0x00);                                                    // Control Byte
      cmd.write(new byte[]{0x00,(byte) hhdBytes.length});                 // HHD Laenge - kann eh nie laenger als 255 Zeichen sein - von daher reicht ein Byte aus 
      cmd.write(hhdBytes);                                                // Payload
      cmd.write(new byte[]{0x00,0x00});                                   // Le Le
      
      byte[] request = cmd.toByteArray();

      // "transmitControlCommand" verwendet intern "ScardControl"
      // "getBasicChannel().transmit()" verwendet intern "ScardTransmit"
      byte[] response = this.getCard().transmitControlCommand(this.getFeatures().get(Feature.FEATURE_MCT_READER_DIRECT),request);
      ResponseAPDU apdu = new ResponseAPDU(response);
      this.check(apdu,new byte[]{(byte)0x90,(byte)0x91});
      
      byte[] data = apdu.getData();
      
      // Es ist durchaus moeglich, dass der User bei der Frage "TAN uebertragen" abgebrochen hat.
      // Dann haben wir auch keine TAN, die automatisch eingegeben werden kann.
      // Checken, wie lang die TAN ist
      if (data != null && data.length > 1)
      {
        int tanLength = data[0];
        HBCIUtils.log("received TAN",HBCIUtils.LOG_INFO);
        if (tanLength > 0)
        {
          byte[] tanBytes = new byte[tanLength];
          System.arraycopy(data,1,tanBytes,0,tanLength);
          tan = this.parseTAN(tanBytes);
          HBCIUtils.log("TAN length: " + tan.length() + " chars",HBCIUtils.LOG_INFO);
        }
      }
    }
    //
    //////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////
    // SECODER FINALIZE TRANSACTION (SECODER 3G)
    
    // Transaktion finalisieren
    {
      HBCIUtils.log("finalizing chipcard transaction",HBCIUtils.LOG_INFO);
      ByteArrayOutputStream cmd = new ByteArrayOutputStream();
      cmd.write(new byte[]{0x20,0x77,0x00,0x00});          // Header
      cmd.write(new byte[]{0x00,0x00,0x06});               // Li Lc Lc
      cmd.write(new byte[]{0x00,0x00,0x00,0x00});          // Activation ID
      cmd.write(0x00);                                     // POF
      cmd.write(0x00);                                     // Control Byte
      cmd.write(new byte[]{0x00,0x00});                    // Le Le
      
      byte[] response = this.getCard().transmitControlCommand(this.getFeatures().get(Feature.FEATURE_MCT_READER_DIRECT),cmd.toByteArray());
      ResponseAPDU apdu = new ResponseAPDU(response);
      
      // Wir tolerieren hier auch 0x6D. Das liefert der Kartenleser,
      // wenn er das Kommando nicht unterstuetzt. Die TAN haben wir
      // ja bereits. Wenn das finalize danach nicht unterstuetzt wird,
      // lassen wir das durchgehen.
      this.check(apdu,new byte[]{(byte)0x90,(byte)0x91, (byte) 0x6D});
    }
    
    HBCIUtils.log("returning TAN",HBCIUtils.LOG_INFO);
    return tan;

    //
    //////////////////////////////////////////////////////////////////////////
  }


  /**
   * Sendet das HHDuc per ScardTransmit an den Kartenleser und liefert die eingegebene TAN zurueck.
   * @param hhduc das HHDuc.
   * @return die TAN. Oder NULL, wenn sie nicht zur Uebertragung ausgewaehlt wurde.
   * @throws Exception
   */
  public String getTanScardTransmit(String hhduc) throws Exception
  {
    String tan = null;
    
    //////////////////////////////////////////////////////////////////////////
    // SECODER TRANSMIT HHDuc (Secoder 3G)

    {
      // Flickercode als Byte-Array rendern
      byte[] hhdBytes = this.toBytes(hhduc);
      
      HBCIUtils.log("sending HHDuc to chipcard using ScardTransmit, length: " + hhdBytes.length,HBCIUtils.LOG_INFO);
      
      int len = 4 + 1 + 1 + 2 + hhdBytes.length; // Laenge von Activation ID, POF, Control Byte, HHD-Laenge, Payload
      ByteArrayOutputStream cmd = new ByteArrayOutputStream();
      cmd.write(new byte[]{(byte)0xFF,(byte)0x91,(byte)0x06,(byte)0x00}); // Header
      cmd.write(new byte[]{0x00,0x00,(byte) len});                        // Li Lc Lc
      cmd.write(new byte[]{0x00,0x00,0x00,0x00});                         // Activation ID
      cmd.write(0x01);                                                    // POF
      cmd.write(0x00);                                                    // Control Byte
      cmd.write(new byte[]{0x00,(byte) hhdBytes.length});                 // HHD Laenge - kann eh nie laenger als 255 Zeichen sein - von daher reicht ein Byte aus 
      cmd.write(hhdBytes);                                                // Payload
      cmd.write(new byte[]{0x00,0x00});                                   // Le Le
      
      byte[] request = cmd.toByteArray();

      CommandAPDU apdu = new CommandAPDU(request);
      byte[] data = this.receive(apdu,new byte[]{(byte)0x90,(byte)0x91});
      
      // Es ist durchaus moeglich, dass der User bei der Frage "TAN uebertragen" abgebrochen hat.
      // Dann haben wir auch keine TAN, die automatisch eingegeben werden kann.
      // Checken, wie lang die TAN ist
      if (data != null && data.length > 1)
      {
        int tanLength = data[0];
        HBCIUtils.log("received TAN",HBCIUtils.LOG_INFO);
        if (tanLength > 0)
        {
          byte[] tanBytes = new byte[tanLength];
          System.arraycopy(data,1,tanBytes,0,tanLength);
          tan = this.parseTAN(tanBytes);
          HBCIUtils.log("TAN length: " + tan.length() + " chars",HBCIUtils.LOG_INFO);
        }
      }
    }
    //
    //////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////
    // SECODER FINALIZE TRANSACTION (SECODER 3G)
    
    // Transaktion finalisieren
    {
      HBCIUtils.log("finalizing chipcard transaction",HBCIUtils.LOG_INFO);
      ByteArrayOutputStream cmd = new ByteArrayOutputStream();
      cmd.write(new byte[]{(byte)0xFF,(byte)0x91,0x07,0x00}); // Header
      cmd.write(new byte[]{0x00,0x00,0x06});                  // Li Lc Lc
      cmd.write(new byte[]{0x00,0x00,0x00,0x00});             // Activation ID
      cmd.write(0x00);                                        // POF
      cmd.write(0x00);                                        // Control Byte
      cmd.write(new byte[]{0x00,0x00});                       // Le Le
      
      byte[] request = cmd.toByteArray();

      // Wir tolerieren hier auch 0x6D. Das liefert der Kartenleser,
      // wenn er das Kommando nicht unterstuetzt. Die TAN haben wir
      // ja bereits. Wenn das finalize danach nicht unterstuetzt wird,
      // lassen wir das durchgehen.
      CommandAPDU apdu = new CommandAPDU(request);
      this.receive(apdu,new byte[]{(byte)0x90,(byte)0x91,(byte)0x6D});
    }
    
    HBCIUtils.log("returning TAN",HBCIUtils.LOG_INFO);
    return tan;

    //
    //////////////////////////////////////////////////////////////////////////
  }

  /**
   * Konvertiert die BCD-codierte TAN in einen String.
   * @param bytes die Bytes.
   * @return der String.
   */
  public String parseTAN(byte[] bytes)
  {
    StringBuilder sb = new StringBuilder();
    for(int i=0;i<bytes.length;i++)
    {
      byte b1 = (byte)((bytes[i] & 0xf0)>>4); // mit 0xf0 UNDen, um das linke Halbbyte zu erhalten und dann 4 Bit nach rechts verschieben
      byte b2 = (byte)(bytes[i] & 0x0f);      // mit 0x0f UNDen, um das rechte Halbbyte zu erhalten
      
      sb.append(b1);

      // Wenn die TAN eine ungerade Anzahl Stellen hat, wird das letzte Halbbyte
      // mit "F" belegt.
      if (i+1 == bytes.length && b2 == 0x0f)
        break;
      
      sb.append(b2);      
    }
    
    return sb.toString();
  }
}
