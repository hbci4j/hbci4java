
/*  $Id: HBCICardService.java,v 1.1 2011/11/24 21:59:37 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.smartcardio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * HBCI-Cardservice fuer den DDVPCSC-Passport, basierend auf dem OCF-Code
 * aus HBCI4Java 2.5.8.
 */
public abstract class HBCICardService 
{
  private final static Map<String,String> statusCodes = new HashMap<String,String>();
  static
  {
    // Siehe http://de.wikipedia.org/wiki/Application_Protocol_Data_Unit
    statusCodes.put("6281","Die zurückgegebenen Daten können fehlerhaft sein");
    statusCodes.put("6282","Da das Dateiende vorher erreicht wurde, konnten nur weniger als Le Bytes gelesen werden");
    statusCodes.put("6283","Die ausgewählte Datei ist gesperrt");
    statusCodes.put("6284","Die File Control Information (FCI) ist nicht ISO 7816-4 konform");
    
    statusCodes.put("6381","File filled up by the last write");
    
    statusCodes.put("6581","Speicherfehler");
    
    statusCodes.put("6700","Länge (Lc oder Le) falsch");

    statusCodes.put("6800","Funktionen im Class Byte werden nicht unterstützt");
    statusCodes.put("6881","Logische Kanäle werden nicht unterstützt");
    statusCodes.put("6882","Secure Messaging wird nicht unterstützt");
    
    statusCodes.put("6900","Kommando nicht erlaubt");
    statusCodes.put("6981","Kommando inkompatibel zur Dateistruktur");
    statusCodes.put("6982","Sicherheitszustand nicht erfüllt");
    statusCodes.put("6983","Authentisierungsmethode ist gesperrt");
    statusCodes.put("6984","Referenzierte Daten sind gesperrt");
    statusCodes.put("6985","Nutzungsbedingungen sind nicht erfüllt");
    statusCodes.put("6986","Kommando nicht erlaubt (kein EF selektiert)");
    statusCodes.put("6987","Erwartete Secure Messaging Objekte nicht gefunden");
    statusCodes.put("6988","Secure Messaging Datenobjekte sind inkorrekt");
    
    statusCodes.put("6A00","Falsche Parameter P1/P2");
    statusCodes.put("6A80","Falsche Daten");
    statusCodes.put("6A81","Funktion wird nicht unterstützt");
    statusCodes.put("6A82","Datei wurde nicht gefunden");
    statusCodes.put("6A83","Record der Datei nicht gefunden");
    statusCodes.put("6A84","Nicht genügend Speicherplatz in der Datei");
    statusCodes.put("6A85","Lc nicht konsistent mit der TLV Struktur");
    statusCodes.put("6A86","Inkorrekte Parameter P1/P2");
    statusCodes.put("6A87","Lc inkonsistent mit P1/P2");
    statusCodes.put("6A88","Referenzierte Daten nicht gefunden");

    statusCodes.put("6B00","Parameter P1/P2 falsch");
    statusCodes.put("6D00","Das Kommando (INS) wird nicht unterstützt");
    statusCodes.put("6E00","Die Kommandoklasse (CLA) wird nicht unterstützt");
    statusCodes.put("6F00","Kommando wurde mit unbekanntem Fehler abgebrochen");
  }
  
  final static int HBCI_DDV_EF_ID            = 0x19;
  final static int HBCI_DDV_EF_BNK           = 0x1A;
  final static int HBCI_DDV_EF_MAC           = 0x1B;
  final static int HBCI_DDV_EF_SEQ           = 0x1C;
  
  final static int SECCOS_SELECT_RET_NOTHING = 0x0c;

  final static int SECCOS_CLA_EXT            = 0xb0;
  final static int SECCOS_CLA_SM_PROPR       = 0x04;
  final static int SECCOS_CLA_SM1            = 0x08;
  final static int SECCOS_CLA_STD            = 0x00;
  
  final static int SECCOS_INS_GET_CHALLENGE  = 0x84;
  final static int SECCOS_INS_GET_KEYINFO    = 0xee;
  final static int SECCOS_INS_INT_AUTH       = 0x88;
  final static int SECCOS_INS_PUT_DATA       = 0xda;
  final static int SECCOS_INS_READ_RECORD    = 0xb2;
  final static int SECCOS_INS_SELECT_FILE    = 0xa4;
  final static int SECCOS_INS_VERIFY         = 0x20;
  final static int SECCOS_INS_UPDATE_RECORD  = 0xdc;
  final static int SECCOS_INS_WRITE_RECORD   = 0xd2;
  
  final static int SECCOS_KEY_TYPE_DF = 0x80;
  final static int SECCOS_PWD_TYPE_DF = 0x80;
  
  final static byte SECCOS_SM_CRT_CC        = (byte) 0xb4;
  final static byte SECCOS_SM_REF_INIT_DATA = (byte) 0x87;
  final static byte SECCOS_SM_RESP_DESCR    = (byte) 0xba;
  final static byte SECCOS_SM_VALUE_LE      = (byte) 0x96;
  
  private final static String[] FEATURES = new String[]
  {
    "NO_FEATURE",
    "FEATURE_VERIFY_PIN_START",
    "FEATURE_VERIFY_PIN_FINISH",
    "FEATURE_MODIFY_PIN_START",
    "FEATURE_MODIFY_PIN_FINISH",
    "FEATURE_GET_KEY_PRESSED",
    "FEATURE_VERIFY_PIN_DIRECT",
    "FEATURE_MODIFY_PIN_DIRECT",
    "FEATURE_MCT_READER_DIRECT",
    "FEATURE_MCT_UNIVERSAL",
    "FEATURE_IFD_PIN_PROPERTIES",
    "FEATURE_ABORT",
    "FEATURE_SET_SPE_MESSAGE",
    "FEATURE_VERIFY_PIN_DIRECT_APP_ID",
    "FEATURE_MODIFY_PIN_DIRECT_APP_ID",
    "FEATURE_WRITE_DISPLAY",
    "FEATURE_GET_KEY",
    "FEATURE_IFD_DISPLAY_PROPERTIES",
    "FEATURE_GET_TLV_PROPERTIES", // NEU
    "FEATURE_CCID_ESC_COMMAND" //NEU
  };

  final static Byte FEATURE_VERIFY_PIN_START   = new Byte((byte) 0x01);
  final static Byte FEATURE_VERIFY_PIN_FINISH  = new Byte((byte) 0x02);
  final static Byte FEATURE_GET_KEY_PRESSED    = new Byte((byte) 0x05);
  final static Byte FEATURE_VERIFY_PIN_DIRECT  = new Byte((byte) 0x06);
  final static Byte FEATURE_MCT_READER_DIRECT  = new Byte((byte) 0x08);
  final static Byte FEATURE_MCT_UNIVERSAL      = new Byte((byte) 0x09);
  final static Byte FEATURE_IFD_PIN_PROPERTIES = new Byte((byte) 0x0a);

  private final static int IOCTL_GET_FEATURE_REQUEST = SCARD_CTL_CODE(3400);

  private Map<Byte, Integer> features = new HashMap<Byte,Integer>();
  private Card smartCard = null;
  
  /**
   * Ermittelt den Namen der Funktion zum Abrufen der Features aus der Karte.
   * @param code der Code.
   * @return der Funktions-Code.
   */
  private static int SCARD_CTL_CODE(int code)
  {
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1)
      return (0x31 << 16 | (code) << 2);
    return 0x42000000 + code;
  }
  
  /**
   * Initialisiert den Service mit der angegebenen Karte.
   * @param card die Karte.
   */
  public void init(Card card)
  {
    this.smartCard = card;
    
    // Liste der Features abrufen
    try
    {
      HBCIUtils.log("querying features",HBCIUtils.LOG_INFO);
      byte[] response = this.smartCard.transmitControlCommand(IOCTL_GET_FEATURE_REQUEST, new byte[0]);
      for (int i = 0; i < response.length; i += 6)
      {
        Byte feature = new Byte(response[i]);
        Integer ioctl = new Integer((0xff & response[i + 2]) << 24)
                                 | ((0xff & response[i + 3]) << 16)
                                 | ((0xff & response[i + 4]) << 8)
                                 | (0xff & response[i + 5]);
        
        String name = null;
        try
        {
          name = FEATURES[feature.intValue()];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
          name = "FEATURE_UNKNOWN";
        }
        HBCIUtils.log("  " +  name + ": " + Integer.toHexString(ioctl.intValue()),HBCIUtils.LOG_INFO);
        features.put(feature, ioctl);
      }      
    }
    catch (Exception e)
    {
      throw new HBCI_Exception(e);
    }
  }
  
  public Map<Byte, Integer> getFeatures() {
      return Collections.unmodifiableMap(features);
  }
  
  /**
   * Prueft die PIN via Kartenleser.
   * @param pwdId PIN-ID.
   */
  public void verifyHardPIN(int pwdId)
  {
    try
    {
      byte[] response = this.smartCard.transmitControlCommand(features.get(FEATURE_VERIFY_PIN_DIRECT),this.createPINVerificationDataStructure(pwdId));
      
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
  
  protected void writeRecordBySFI(int sfi, int idx, byte[] data)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_WRITE_RECORD,
                                        (byte)(idx+1), (byte)((sfi<<3)|0x04),
                                        data);
    send(command);
  }
  
  protected void updateRecordBySFI(int sfi, int idx, byte[] data)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_UPDATE_RECORD,
                                        (idx+1),((sfi<<3)|0x04),data);
    send(command);
  }
  
  protected byte[] readRecordBySFI(int sfi, int idx)
  {
    CommandAPDU command = new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_READ_RECORD,
                                         (idx+1),((sfi<<3)|0x04),256);
    return receive(command);
  }
  
  protected byte[] readRecord(int idx)
  {
      return readRecordBySFI(0x00, idx);
  }
  
  protected void selectSubFile(int id)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_SELECT_FILE,
                                        0x02, SECCOS_SELECT_RET_NOTHING,
                                        new byte[] {(byte)((id>>8)&0xFF), (byte)(id&0xFF)});
    send(command);
  }
  
  protected byte[] getKeyInfo(int idx)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_EXT, SECCOS_INS_GET_KEYINFO,
                                        SECCOS_KEY_TYPE_DF,(idx+1),256);
    return receive(command);
  }
  
  protected void putData(int tag,byte[] data)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_PUT_DATA,
                                        (byte)((tag>>8)&0xFF), (byte)(tag&0xFF),
                                        data);
    send(command);
  }
  
  protected byte[] getChallenge()
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_GET_CHALLENGE,
                                        0x00,0x00,8);
    return receive(command);
  }
  
  protected byte[] internalAuthenticate(int keynum, byte[] challenge)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_INT_AUTH,
                                        0x00,(SECCOS_KEY_TYPE_DF|keynum),
                                        challenge,8);
    return receive(command);
  }
  
  /**
   * Sendet ein Kommando an den Kartenleser und prueft, ob es erfolgreich ausgefuehrt wurde.
   * @param command das Kommando.
   */
  void send(CommandAPDU command)
  {
    // 0x90: Alles OK
    // 0x61: Warnung - im Response sind noch Bytes verfuegbar. Da wir das Response
    //       hier aber eh nicht brauchen, koennen wir das tolerieren
    _receive(command,new byte[]{(byte)0x90,(byte)0x61});
  }

  /**
   * Sendet ein Kommando an den Kartenleser, prueft ob es erfolgreich
   * ausgefuehrt wurde und liefert die Antwort zurueck.
   * @param command das Kommando.
   * @return die Antwort.
   */
  byte[] receive(CommandAPDU command)
  {
    return _receive(command,new byte[]{(byte)0x90});
  }

  /**
   * Sendet ein Kommando an den Kartenleser, prueft ob es erfolgreich
   * ausgefuehrt wurde und liefert die Antwort zurueck.
   * @param command das Kommando.
   * @param returncodes zulaessige Return-Codes.
   * @return die Antwort.
   */
  private byte[] _receive(CommandAPDU command, byte[] returncodes)
  {
    try
    {
      //////////////////////////////////////////////////////////////////////////
      // Aufrufer ermitteln
      String caller = "";
      try
      {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        caller = stack[3].getMethodName();
      } catch (Exception e) {} // ignore
      //////////////////////////////////////////////////////////////////////////
      

      CardChannel channel = this.smartCard.getBasicChannel();
      ResponseAPDU response = channel.transmit(command);
      
      // Command und Response loggen
      HBCIUtils.log(caller + " command : " + toHex(command.getBytes()),HBCIUtils.LOG_DEBUG);
      HBCIUtils.log(caller + " response: " + toHex(response.getBytes()),HBCIUtils.LOG_DEBUG);

      this.check(response,returncodes);
      return response.getData();
    }
    catch (HBCI_Exception e1)
    {
      throw e1;
    }
    catch (Exception e2)
    {
      throw new HBCI_Exception(e2);
    }
  }
  
  /**
   * Prueft das Response auf die angegebenen Return-Codes.
   * @param response das Response.
   * @param returncodes zulaessige Return-Codes.
   */
  private void check(ResponseAPDU response, byte[] returncodes)
  {
    // Return-Code pruefen
    byte sw1 = (byte) response.getSW1();
    for (byte b:returncodes)
    {
      if (sw1 == b) // Statuscode gefunden
        return;
    }

    // Checken, ob wir einen Fehlertext haben
    String code = Integer.toHexString(response.getSW()).toUpperCase();
    String msg = statusCodes.get(code);
    if (msg != null)
      throw new HBCI_Exception("Fehler " + code + ": " + msg);
    
    // Ne, dann halt so
    throw new HBCI_Exception("Fehler " + code + " bei Kartenleser-Zugriff");
  }

  /**
   * Konvertiert die Bytes in HEX-Darstellung.
   * @param bytes
   * @return String-Repraesentation.
   */
  private String toHex(byte[] bytes)
  {
    StringBuffer sb = new StringBuffer();
    for (byte b:bytes)
    {
      String s = Integer.toHexString(b & 0xff).toUpperCase();
      if (s.length() == 1)
        sb.append("0");
      sb.append(s);
      sb.append(" ");
    }
    return sb.toString();
  }

  /**
   * Fuellt den String rechtsbuendig mit Leerzeichen auf die angegebene Laenge.
   * @param st der String.
   * @param len die Gesamtlaenge.
   * @return der codierte String mit Leerzeichen auf der rechten Seite.
   */
  protected byte[] expand(String st,int len)
  {
    try {
      StringBuffer st_new=new StringBuffer(st);
      for (int i=st.length();i<len;i++) {
          st_new.append(" ");
      }
      return st_new.toString().getBytes("ISO-8859-1");
    }
    catch (HBCI_Exception e1)
    {
      throw e1;
    }
    catch (Exception e2)
    {
      throw new HBCI_Exception(e2);
    }
  }
  
  /**
   * Erzeugt das PIN-Check-Kommando.
   * @return
   * @throws IOException
   */
  protected byte[] createPINVerificationDataStructure(int pwdId) throws IOException
  {
    ByteArrayOutputStream verifyCommand = new ByteArrayOutputStream();
    verifyCommand.write(0x0f); // bTimeOut
    verifyCommand.write(0x05); // bTimeOut2
    verifyCommand.write(0x89); // bmFormatString
    verifyCommand.write(0x07); // bmPINBlockString
    verifyCommand.write(0x10); // bmPINLengthFormat
    verifyCommand.write(new byte[] {(byte) 8,(byte) 4}); // PIN size (max/min), volker: 12,4=>8,4
    verifyCommand.write(0x02); // bEntryValidationCondition
    verifyCommand.write(0x01); // bNumberMessage
    verifyCommand.write(new byte[] { 0x04, 0x09 }); // wLangId, volker: 13,8=>4,9
    verifyCommand.write(0x00); // bMsgIndex
    verifyCommand.write(new byte[] { 0x00, 0x00, 0x00 }); // bTeoPrologue
    byte[] verifyApdu = new byte[] {
        SECCOS_CLA_STD, // CLA
        SECCOS_INS_VERIFY, // INS
        0x00, // P1
        (byte) (SECCOS_PWD_TYPE_DF|pwdId), // P2 volker: 01=>81
        0x08, // Lc = 8 bytes in command data
        (byte) 0x25, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,//volker:0x20=>0x25
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
    verifyCommand.write(verifyApdu.length & 0xff); // ulDataLength[0]
    verifyCommand.write(0x00); // ulDataLength[1]
    verifyCommand.write(0x00); // ulDataLength[2]
    verifyCommand.write(0x00); // ulDataLength[3]
    verifyCommand.write(verifyApdu); // abData
    return verifyCommand.toByteArray();
  }
}
