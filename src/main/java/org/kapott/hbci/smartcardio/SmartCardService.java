
/*  $Id: SmartCardService.java,v 1.1 2011/11/24 21:59:37 willuhn Exp $

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

import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * Smartcard-Service fuer den DDVPCSC-Passport, basierend auf dem OCF-Code
 * aus HBCI4Java 2.5.8.
 */
public abstract class SmartCardService 
{
  /**
   * Der Zeichensatz.
   */
  final static Charset CHARSET = Charset.forName("ISO-8859-1");
  
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
  final static int SECCOS_INS_READ_BINARY    = 0xb0;
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
  
  private final static int IOCTL_GET_FEATURE_REQUEST = SCARD_CTL_CODE(3400);

  private Map<Feature, Integer> features = new HashMap<Feature,Integer>();
  private Card smartCard = null;
  
  /**
   * Kapselt die Features der Karte.
   */
  protected enum Feature
  {
    FEATURE_VERIFY_PIN_START((byte) 0x01),
    FEATURE_VERIFY_PIN_FINISH((byte) 0x02),
    FEATURE_MODIFY_PIN_START((byte) 0x03),
    FEATURE_MODIFY_PIN_FINISH((byte) 0x04),
    FEATURE_GET_KEY_PRESSED((byte) 0x05),
    FEATURE_VERIFY_PIN_DIRECT((byte) 0x06),
    FEATURE_MODIFY_PIN_DIRECT((byte) 0x07),
    FEATURE_MCT_READER_DIRECT((byte) 0x08),
    FEATURE_MCT_UNIVERSAL((byte) 0x09),
    FEATURE_IFD_PIN_PROPERTIES((byte) 0x0a),
    FEATURE_ABORT((byte) 0x0b),
    FEATURE_SET_SPE_MESSAGE((byte) 0x0c),
    FEATURE_VERIFY_PIN_DIRECT_APP_ID((byte) 0x0d),
    FEATURE_MODIFY_PIN_DIRECT_APP_ID((byte) 0x0e),
    FEATURE_WRITE_DISPLAY((byte) 0x0f),
    FEATURE_GET_KEY((byte) 0x10),
    FEATURE_IFD_DISPLAY_PROPERTIES((byte) 0x11),
    FEATURE_GET_TLV_PROPERTIES((byte) 0x12),
    FEATURE_CCID_ESC_COMMAND((byte) 0x13),
    FEATURE_EXECUTE_PACE((byte) 0x20)
    ;
    
    private byte number;
    
    /**
     * ct.
     * @param number die Feature-Nummer.
     */
    private Feature(byte number)
    {
      this.number = number;
    }
    
    /**
     * Liefert das Feature mit der angegebenen Feature-Nummer.
     * @param b das Byte mit der Feature-Nummer.
     * @return das Feature oder NULL, wenn es nicht existiert.
     */
    private static Feature find(byte b)
    {
      for (Feature f:Feature.values())
      {
        if (b == f.number)
          return f;
      }
      return null;
    }
  }
  
  /**
   * Erzeut eine neue Instanz des Card-Service fuer die angegebene Karte.
   * @param type der Typ des zu erzeugenden Service.
   * @param name optionale Angabe des Kartenlesernamens - noetig, wenn am Rechner mehrere Kartenleser angeschlossen sind.
   * @return die neue Instanz des Card-Service.
   */
  public static <T extends SmartCardService> T createInstance(Class<? extends SmartCardService> type, String name)
  {
    if (type == null)
      throw new HBCI_Exception("Kein Karten-Typ angegeben");

    try
    {
      HBCIUtils.log("creating smartcard-service, using type " + type.getSimpleName(), HBCIUtils.LOG_INFO);
  
      TerminalFactory terminalFactory = TerminalFactory.getDefault();
      CardTerminals terminals = terminalFactory.terminals();
      if (terminals == null)
        throw new HBCI_Exception("Kein Kartenleser gefunden");
      
      List<CardTerminal> list = terminals.list();
      if (list == null || list.size() == 0)
        throw new HBCI_Exception("Kein Kartenleser gefunden");
      
      HBCIUtils.log("found card terminals:",HBCIUtils.LOG_INFO);
      for (CardTerminal t : list)
      {
        HBCIUtils.log("  " + t.getName(), HBCIUtils.LOG_INFO);
      }
  
      CardTerminal terminal = null;
  
      // Checken, ob der User einen konkreten Kartenleser vorgegeben hat
      if (name != null)
      {
        HBCIUtils.log("explicit terminal name given, trying to open terminal: " + name,HBCIUtils.LOG_DEBUG);
        terminal = terminals.getTerminal(name);
        if (terminal == null)
          throw new HBCI_Exception("Kartenleser \"" + name + "\" nicht gefunden");
      }
      else
      {
        HBCIUtils.log("open first available card terminal",HBCIUtils.LOG_DEBUG);
        terminal = list.get(0);
      }
      HBCIUtils.log("using card terminal " + terminal.getName(),HBCIUtils.LOG_DEBUG);
  
      // wait for card
      if (!terminal.waitForCardPresent(60 * 1000L))
        throw new HBCI_Exception("Keine Chipkarte in Kartenleser " + terminal.getName() + " gefunden");
  
      // Hier kann man gemaess
      // http://download.oracle.com/javase/6/docs/jre/api/security/smartcardio/spec/javax/smartcardio/CardTerminal.html#connect%28java.lang.String%29
      // auch "T=0" oder "T=1" angeben. Wir wissen allerdings noch nicht, von welchem
      // Typ die Karte ist. Daher nehmen wir "*" fuer jedes verfuegbare. Wenn wir die
      // Karte geoeffnet haben, kriegen wir dann auch das Protokoll raus.
      Card smartCard = terminal.connect("*");
  
      if (smartCard == null)
        throw new HBCI_Exception("Keine Karte angegeben");
    
      String proto = smartCard.getProtocol();
      HBCIUtils.log("card type: " + proto,HBCIUtils.LOG_INFO);
      
      // Card-Service basierend auf dem Kartentyp erzeugen
      if (proto == null || proto.indexOf("=") == -1)
        throw new HBCI_Exception("Unbekannter Kartentyp");

      // Checken, ob wir einen abstrakten Service-Typ haben. Wenn ja, haengen wir
      // die Protokoll-Kennung an und pruefen, ob die existiert
      if (Modifier.isAbstract(type.getModifiers()))
      {
        String id = proto.substring(proto.indexOf("=")+1);
        String serviceName = type.getName() + id;
        HBCIUtils.log(" trying to load: " + serviceName,HBCIUtils.LOG_DEBUG);
        type = (Class<SmartCardService>) Class.forName(serviceName);
      }
      else
      {
        HBCIUtils.log(" trying to load: " + type.getName(),HBCIUtils.LOG_DEBUG);
      }
      
      SmartCardService cardService = type.newInstance();
      HBCIUtils.log(" using: " + cardService.getClass().getName(),HBCIUtils.LOG_INFO);
      cardService.init(smartCard);
      return (T) cardService;
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
   * Schliesst die Karte und resettet den Service.
   */
  public void close()
  {
    this.features.clear();
    
    if (this.smartCard == null)
      return;
    
    try
    {
      this.smartCard.disconnect(false);
    }
    catch (Exception e2)
    {
      throw new HBCI_Exception(e2);
    }
  }
  
  /**
   * Initialisiert den Service mit der angegebenen Karte.
   * @param card die Karte.
   */
  protected void init(Card card)
  {
    this.smartCard = card;
    
    // Liste der Features abrufen
    try
    {
      HBCIUtils.log("querying features",HBCIUtils.LOG_INFO);
      byte[] response = this.smartCard.transmitControlCommand(IOCTL_GET_FEATURE_REQUEST, new byte[0]);
      for (int i = 0; i < response.length; i += 6)
      {
        Integer ioctl = new Integer((0xff & response[i + 2]) << 24)
                                 | ((0xff & response[i + 3]) << 16)
                                 | ((0xff & response[i + 4]) << 8)
                                 | (0xff & response[i + 5]);
        
        Feature feature = Feature.find(response[i]);
        if (feature == null)
        {
          HBCIUtils.log("  unknown feature: " + Integer.toHexString(ioctl.intValue()),HBCIUtils.LOG_INFO);
          continue;
        }
        
        HBCIUtils.log("  " +  feature.name() + ": " + Integer.toHexString(ioctl.intValue()),HBCIUtils.LOG_INFO);
        features.put(feature, ioctl);
      }      
    }
    catch (Exception e)
    {
      HBCIUtils.log("unable to query features, continuing without having a feature set, error: " + e.getMessage(),HBCIUtils.LOG_WARN);
      HBCIUtils.log(e,HBCIUtils.LOG_DEBUG);
    }
  }
  
  /**
   * Liefert die Map mit den verfuegbaren Features.
   * @return die Map mit den verfuegbaren Features.
   */
  protected final Map<Feature, Integer> getFeatures()
  {
    return Collections.unmodifiableMap(features);
  }
  
  /**
   * Liefert die Instanz der Smartcard.
   * @return die Instanz der Smartcard.
   */
  protected final Card getCard()
  {
    return this.smartCard;
  }
  
  /**
   * @param sfi
   * @param idx
   * @param data
   */
  protected final void writeRecordBySFI(int sfi, int idx, byte[] data)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_WRITE_RECORD,
                                        (byte)(idx+1), (byte)((sfi<<3)|0x04),
                                        data);
    send(command);
  }
  
  /**
   * @param sfi
   * @param idx
   * @param data
   */
  protected final void updateRecordBySFI(int sfi, int idx, byte[] data)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_UPDATE_RECORD,
                                        (idx+1),((sfi<<3)|0x04),data);
    send(command);
  }
  
  /**
   * @param sfi
   * @param idx
   * @return
   */
  protected final byte[] readRecordBySFI(int sfi, int idx)
  {
    CommandAPDU command = new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_READ_RECORD,
                                          (idx+1),((sfi<<3)|0x04),256);
    return receive(command);
  }
  
  /**
   * @param offset
   * @param length
   * @return
   */
  protected byte[] readBinary(int offset, int length)
  {
    CommandAPDU command = new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_READ_BINARY,
                                          (offset >> 8) & 0x7F,
                                          offset & 0xFF,
                                          length == 0 ? 256 : length);
    return receive(command);
  }

  /**
   * @param id
   */
  protected final void selectFile(int id)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_SELECT_FILE,
                                        0x00, SECCOS_SELECT_RET_NOTHING,
                                        new byte[] {(byte)((id>>8)&0xFF), (byte)(id&0xFF)});
    send(command);
  }

  /**
   * @param id
   */
  protected final void selectSubFile(int id)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_SELECT_FILE,
                                        0x02, SECCOS_SELECT_RET_NOTHING,
                                        new byte[] {(byte)((id>>8)&0xFF), (byte)(id&0xFF)});
    send(command);
  }
  
  /**
   * @param idx
   * @return
   */
  protected final byte[] getKeyInfo(int idx)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_EXT, SECCOS_INS_GET_KEYINFO,
                                        SECCOS_KEY_TYPE_DF,(idx+1),256);
    return receive(command);
  }
  
  /**
   * @param tag
   * @param data
   */
  protected final void putData(int tag,byte[] data)
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_PUT_DATA,
                                        (byte)((tag>>8)&0xFF), (byte)(tag&0xFF),
                                        data);
    send(command);
  }
  
  /**
   * @return
   */
  protected final byte[] getChallenge()
  {
    CommandAPDU command=new CommandAPDU(SECCOS_CLA_STD, SECCOS_INS_GET_CHALLENGE,
                                        0x00,0x00,8);
    return receive(command);
  }
  
  /**
   * @param keynum
   * @param challenge
   * @return
   */
  protected final byte[] internalAuthenticate(int keynum, byte[] challenge)
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
  protected final void send(CommandAPDU command)
  {
    // 0x90: Alles OK
    // 0x61: Warnung - im Response sind noch Bytes verfuegbar. Da wir das Response
    //       hier aber eh nicht brauchen, koennen wir das tolerieren
    receive(command,new byte[]{(byte)0x90,(byte)0x61});
  }

  /**
   * Sendet ein Kommando an den Kartenleser, prueft ob es erfolgreich
   * ausgefuehrt wurde und liefert die Antwort zurueck.
   * @param command das Kommando.
   * @return die Antwort.
   */
  protected final byte[] receive(CommandAPDU command)
  {
    return receive(command,new byte[]{(byte)0x90});
  }

  /**
   * Sendet ein Kommando an den Kartenleser, prueft ob es erfolgreich
   * ausgefuehrt wurde und liefert die Antwort zurueck.
   * @param command das Kommando.
   * @param returncodes zulaessige Return-Codes.
   * @return die Antwort.
   */
  protected byte[] receive(CommandAPDU command, byte[] returncodes)
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
  protected final void check(ResponseAPDU response, byte[] returncodes)
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
  protected final String toHex(byte[] bytes)
  {
    return this.toHex(bytes," ");
  }
  
  /**
   * Konvertiert die Bytes in HEX-Darstellung.
   * @param bytes
   * @param sep Separator-Zeichen.
   * @return String-Repraesentation.
   */
  protected final String toHex(byte[] bytes, String sep)
  {
    StringBuffer sb = new StringBuffer();
    for (byte b:bytes)
    {
      String s = Integer.toHexString(b & 0xff).toUpperCase();
      if (s.length() == 1)
        sb.append("0");
      sb.append(s);
      
      if (sep != null)
        sb.append(sep);
    }
    return sb.toString();
  }
  
  /**
   * Konvertiert den HEX-String zurueck in ein Byte-Array.
   * @param hex der Text in HEX-Schreibweise.
   * @return das Byte-Array.
   */
  public byte[] toBytes(String hex)
  {
    byte[] result = new byte[hex.length() / 2];
    for (int i=0;i<result.length;i++)
    {
      int index = i * 2;
      result[i] = (byte) Integer.parseInt(hex.substring(index,index+2),16);
    }
    return result;
  }

  /**
   * Fuellt den String rechtsbuendig mit Leerzeichen auf die angegebene Laenge.
   * @param st der String.
   * @param len die Gesamtlaenge.
   * @return der codierte String mit Leerzeichen auf der rechten Seite.
   */
  protected final byte[] expand(String st,int len)
  {
    StringBuffer st_new=new StringBuffer(st);
    for (int i=st.length();i<len;i++) {
        st_new.append(" ");
    }
    return st_new.toString().getBytes(CHARSET);
  }
}
