/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
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

package org.kapott.hbci.smartcardio;

import javax.smartcardio.Card;
import javax.smartcardio.CommandAPDU;

/**
 * DDV-Cardservice fuer Karten des Types 0, basierend auf dem OCF-Code aus HBCI4Java 2.5.8.
 */
public class DDVCardService0 extends DDVCardService
{
  /**
   * @see org.kapott.hbci.smartcardio.SmartCardService#init(javax.smartcardio.Card)
   */
  protected void init(Card card)
  {
    super.init(card);

    // Select AID
    // Kopiert aus org.kapott.hbci.ocf.CardServiceFactory.getCardType
    CommandAPDU command = new CommandAPDU((byte)0x00, (byte)0xa4, (byte)0x04, (byte)0x0c,
                                          new byte[] {(byte)0xd2, (byte)0x76,
                                                      (byte)0x00, (byte)0x00,
                                                      (byte)0x25, (byte)0x48,
                                                      (byte)0x42, (byte)0x01,
                                                      (byte)0x00});
    send(command);
  }


  /**
   * @see org.kapott.hbci.smartcardio.DDVCardService#readKeyData()
   */
  public DDVKeyData[] readKeyData()
  {
    DDVKeyData[] ret=new DDVKeyData[2];
    
    selectSubFile(0x0013);
    byte[] rawData=readRecordBySFI(0x00, 0);
    ret[0]=new DDVKeyData();
    ret[0].num=rawData[0];
    ret[0].version=rawData[4];
    ret[0].len=rawData[1];
    ret[0].alg=rawData[2];

    selectSubFile(0x0014);
    rawData=readRecordBySFI(0x00, 0);
    ret[1]=new DDVKeyData();
    ret[1].num=rawData[0];
    ret[1].version=rawData[3];
    ret[1].len=rawData[1];
    ret[1].alg=rawData[2];
    
    return ret;
  }
  
  /**
   * @see org.kapott.hbci.smartcardio.DDVCardService#calculateSignature(byte[])
   */
  protected byte[] calculateSignature(byte[] data_l)
  {
    putData(0x0100,data_l);
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_SM_PROPR, SECCOS_INS_READ_RECORD,
                                        (byte)0x01, (byte)((0x1B<<3)|0x04),256);
    byte[] data = receive(command);
    byte[] ret = new byte[8];
    System.arraycopy(data,12,ret,0,8);
    return ret;
  }
}
