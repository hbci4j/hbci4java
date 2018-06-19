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

import java.io.IOException;

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import org.kapott.hbci.exceptions.HBCI_Exception;

/**
 * Basis-Implementierung fuer spezielle HBCI-Karten.
 */
public abstract class HBCICardService extends SmartCardService
{
  /**
   * Prueft die PIN via Kartenleser.
   * @param pwdId PIN-ID.
   */
  public final void verifyHardPIN(int pwdId)
  {
    try
    {
      byte[] response = this.getCard().transmitControlCommand(this.getFeatures().get(Feature.FEATURE_VERIFY_PIN_DIRECT),this.createPINVerificationDataStructure(pwdId));
      
      ResponseAPDU apdu = new ResponseAPDU(response);

      //////////////////////////////////////////////////////////////////////////
      // Extra Checks
      int sw = apdu.getSW();
      if (sw == 0x63c0) throw new HBCI_Exception("PIN falsch. Noch 1 Versuch");
      if (sw == 0x63c1) throw new HBCI_Exception("PIN falsch. Noch 2 Versuche");
      if (sw == 0x63c2) throw new HBCI_Exception("PIN falsch. Noch 3 Versuche");
      if (sw == 0x6400) throw new HBCI_Exception("PIN-Eingabe aufgrund Timeout abgebrochen");
      if (sw == 0x6401) throw new HBCI_Exception("PIN-Eingabe vom User abgebrochen");
      if (sw == 0x6983) throw new HBCI_Exception("Chipkarte ist gesperrt oder besitzt ein unbekanntes Format");
      //
      //////////////////////////////////////////////////////////////////////////
      
      //////////////////////////////////////////////////////////////////////////
      // Standard-Checks
      this.check(apdu,new byte[]{(byte)0x90});
      //
      //////////////////////////////////////////////////////////////////////////
    }
    catch (HBCI_Exception he)
    {
      throw he;
    }
    catch (Exception e)
    {
      throw new HBCI_Exception(e);
    }
  }
  
  /**
   * Prueft die PIN via Software.
   * @param pwdId die PIN-ID.
   * @param softPin die PIN.
   */
  public void verifySoftPIN(int pwdId, byte[] softPin)
  {
    byte[] body = new byte[] {(byte)0x25, (byte)0xff, (byte)0xff, (byte)0xff, 
                              (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
  
    // pin bcd-kodiert in pin-2-block schreiben
    for (int i=0;i<softPin.length;i++)
    {
      body[1+(i>>1)]&=(byte)(((0x0F)<<(4*(i&1)))&0xFF);
      body[1+(i>>1)]|=(byte)(((softPin[i]-(byte)0x30) << (4-(4*(i&1))))&0xFF);
    }
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_VERIFY,
                                        (byte)0x00, (byte)(SECCOS_PWD_TYPE_DF|pwdId),
                                        body);
    send(command);
  }

  /**
   * Liefert die CID der Karte.
   * @return die CID der Karte.
   */
  public abstract String getCID();
  
  /**
   * Liefert die Card-ID.
   * @return die Card-ID.
   */
  public String getCardId()
  {
    final String cids = this.getCID();
    byte[] cid = cids.getBytes();

    // extract card id
    StringBuffer cardId = new StringBuffer();
    for (int i = 0; i < cid.length; i++)
    {
      cardId.append((char) (((cid[i] >> 4) & 0x0F) + 0x30));
      cardId.append((char) ((cid[i] & 0x0F) + 0x30));
    }
    return cardId.toString();
  }
  
  /**
   * Erzeugt das PIN-Check-Kommando.
   * @return
   * @throws IOException
   */
  protected abstract byte[] createPINVerificationDataStructure(int pwdId) throws IOException;
}


