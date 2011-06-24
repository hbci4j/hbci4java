/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/src/org/kapott/hbci/manager/FlickerCode.java,v $
 * $Revision: 1.9 $
 * $Date: 2011/06/24 16:53:23 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.manager;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementierung des Flicker-Codes fuer optisches ChipTAN.
 * Basiert auf der Javascript-Implementierung von
 * http://6xq.net/media/00/20/flickercode.html
 * 
 * Die Javascript-Implementierung war jedoch nicht mehr aktuell (basiert auf HHD 1.3).
 */
public class FlickerCode
{
  /**
   * Versionskennung.
   */
  public static enum HHDVersion
  {
    /**
     * HHD-Version 1.4
     */
    HHD14,
    
    /**
     * HHD-Version 1.3
     */
    HHD13
  }
  
  /**
   * Das Encoding der Nutzdaten.
   */
  public static enum Encoding
  {
    /**
     * ASC-Encoding.
     */
    ASC,
    
    /**
     * BCD-Encoding.
     */
    BCD,
  }
  

  /**
   * Die Anzahl der Bytes, in der die Laenge des Challenge bei HHD 1.4 steht.
   * Bei HHD 1.3 war das noch 2 Zeichen lang.
   * Wenn der Flicker-Code nicht in "Challenge HHDuc" uebertragen wurde
   * sondern direkt im Freitext-Challenge, koennen wir das Problem umgehen,
   * indem wir in clean() einfach eine "0" vorn anhaengen.
   * Wenn er aber tatsaechlich im "Challenge HHDuc" steht, kann man dem
   * Code nicht ansehen, ob es ein HHD 1.3-Code ist. In dem Fall hilft nur
   * Try&Error. Also mit HHD 1.4 parsen. Und wenn das fehlschlaegt, dann
   * HHD 1.3 versuchen.
   */
  private final static int LC_LENGTH_HHD14 = 3;
  
  /**
   * Die Anzahl der Bytes, in der die Laenge des Challenge bei HHD 1.3 steht.
   */
  private final static int LC_LENGTH_HHD13 = 2;

  /**
   * Die Position des Bits, welches das Encoding enthaelt.
   */
  private final static int BIT_ENCODING = 6;
  
  /**
   * Die Position des Bits, welches festlegt, ob ein Controlbyte folgt.
   */
  private final static int BIT_CONTROLBYTE = 7;
  
  /**
   * Die HHD-Version.
   */
  public HHDVersion version = null;
  
  /**
   * Laenge des gesamten Codes.
   */
  public int lc           = 0;
  
  /**
   * Der Startcode.
   */
  public Startcode startCode = new Startcode();

  /**
   * Datenelement 1.
   */
  public DE de1           = new DE();
  
  /**
   * Datenelement 2.
   */
  public DE de2           = new DE();

  /**
   * Datenelement 3.
   */
  public DE de3           = new DE();
  
  /**
   * Der Rest des Codes. Mit dem koennen wir nichts anfangen
   */
  public String rest      = null;

  /**
   * ct.
   * Parameterloser Konstruktor zum manuellen Zusammenstecken eines Codes.
   */
  public FlickerCode()
  {
  }
  
  /**
   * ct.
   * Parst den HHDuc-Code aus dem uebergebenen Code.
   * @param code der zu parsende Code.
   */
  public FlickerCode(String code)
  {
    // Wir versuchen es erstmal als HHD 1.4
    try
    {
      parse(code,HHDVersion.HHD14);
    }
    catch (Exception e)
    {
      // OK, dann HHD 1.3
      parse(code,HHDVersion.HHD13);
    }
  }
  
  /**
   * Parst den Code mit der angegebenen HHD-Version.
   * @param code der zu parsende Code.
   * @param version die HHD-Version.
   */
  private void parse(String code, HHDVersion version)
  {
    reset();
    code = clean(code);
    
    // 1. LC ermitteln. Banales ASCII
    {
      int len = version == HHDVersion.HHD14 ? LC_LENGTH_HHD14 : LC_LENGTH_HHD13;
      this.lc = Integer.parseInt(code.substring(0,len));
      code = code.substring(len); // und abschneiden
    }

    // 2. Startcode/Control-Bytes
    code = this.startCode.parse(code);
    
    // 3. LDE/DE 1-3
    code = this.de1.parse(code);
    code = this.de2.parse(code);
    code = this.de3.parse(code);

    // 4. Den Rest speichern wir hier.
    this.rest = code.length() > 0 ? code : null;
  }
  
  /**
   * Entfernt das CHLGUC0026....CHLGTEXT aus dem Code, falls vorhanden.
   * Das sind HHD 1.3-Codes, die nicht im "Challenge HHDuc" uebertragen
   * wurden sondern direkt im Challenge-Freitext,
   * @param code
   * @return
   */
  private String clean(String code)
  {
    code = code.replaceAll(" ",""); // Alle Leerzeichen entfernen
    code = code.trim();             // Whitespaces entfernen
    
    // Jetzt checken, ob die beiden Tokens enthalten sind
    int t1Start = code.indexOf("CHLGUC");
    int t2Start = code.indexOf("CHLGTEXT");
    if (t1Start == -1 || t2Start == -1 || t2Start <= t1Start)
      return code; // Ne, nicht enthalten
    
    // Erstmal den 2. Token abschneiden
    code = code.substring(0,t2Start);
    
    // Dann alles abschneiden bis zum Beginn von "CHLGUC"
    code = code.substring(t1Start);

    // Wir haben eigentlich nicht nur "CHLGUC" sondern "CHLGUC0026"
    // Wobei die 4 Zahlen sicher variieren koennen. Wir schneiden einfach alles ab.
    code = code.substring(10);
    
    // Jetzt vorn noch ne "0" dran haengen, damit LC wieder 3-stellig ist - wie bei HHD 1.4
    return "0" + code;
  }

  /**
   * Rendert den flickerfaehigen Code aus dem Challenge im HHD-Format.
   * @return der neu generierte Flicker-Code.
   */
  public String render()
  {
    // 1. Payload ermitteln
    String s = createPayload();

    // 2. Luhn-Checksumme neu berechnen
    String luhn = createLuhnChecksum();
    
    // 3. XOR-Checksumme neu berechnen
    String xor = createXORChecksum(s);
    
    // 4. Alles zusammenbauen und zurueckliefern
    return s + luhn + xor;
  }
  
  /**
   * Generiert den Payload neu.
   * Das ist der komplette Code, jedoch ohne Pruefziffern am Ende.
   * @return der neu generierte Payload.
   */
  private String createPayload()
  {
    StringBuffer sb  = new StringBuffer();
    
    // 1. Laenge Startcode
    sb.append(this.startCode.renderLength());
    
    // 2. Die Control-Bytes
    for (Integer i:this.startCode.controlBytes)
    {
      sb.append(toHex(i,2));
    }

    // 3. Der Startcode
    sb.append(this.startCode.renderData());

    // 4. DEs anhaengen.
    DE[] deList = new DE[]{this.de1,this.de2,this.de3};
    for (int i=0;i<deList.length;++i)
    {
      DE de = deList[i];
      sb.append(de.renderLength());
      sb.append(de.renderData());
    }
    
    String s = sb.toString();
    
    // 5. Laenge neu berechnen und vorn dran haengen
    int len = s.length();
    len += 2; // die zwei Zeichen am Ende mit den Pruefsummen muessen wir noch mit reinrechnen. 
    len = len / 2; // Anzahl der Bytes. Jedes Byte sind 2 Zeichen.
    String lc = toHex(len,2);
    
    return (lc + s);
  }
  
  /**
   * Berechnet die XOR-Checksumme fuer den Code neu.
   * @param der Payload.
   * @return die XOR-Checksumme im Hex-Format.
   */
  private String createXORChecksum(String payload)
  {
    int xorsum = 0;
    for (int i=0; i<payload.length(); ++i)
    {
      xorsum ^= Integer.parseInt(Character.toString(payload.charAt(i)),16);
    }
    return toHex(xorsum,1);
  }

  /**
   * Berechnet die Luhn-Pruefziffer neu.
   * @return die Pruefziffer im Hex-Format.
   */
  private String createLuhnChecksum()
  {
    ////////////////////////////////////////////////////////////////////////////
    // Schritt 1: Payload ermitteln
    StringBuffer sb = new StringBuffer();
    
    // a) Controlbytes
    for (Integer i:this.startCode.controlBytes)
      sb.append(toHex(i,2));
    
    // b) Startcode
    sb.append(this.startCode.renderData());
    
    // c) DEs
    if (this.de1.data != null) sb.append(this.de1.renderData());
    if (this.de2.data != null) sb.append(this.de2.renderData());
    if (this.de3.data != null) sb.append(this.de3.renderData());
    
    String payload = sb.toString();
    
    //
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // Schritt 2: Pruefziffer berechnen
    int luhnsum = 0;
    int i = 0;
    for (i=0; i<payload.length(); i+=2)
    {
      luhnsum += (1*Integer.parseInt(Character.toString(payload.charAt(i)),16)) + 
                 quersumme(2*Integer.parseInt(Character.toString(payload.charAt(i+1)),16));
    }
    
    // Ermittelt, wieviel zu "luhnsum" addiert werden muss, um auf die
    // naechste Zahl zu kommen, die durch 10 teilbar ist
    // Beispiel:
    // luhnsum = 129 modulo 10 -> 9
    // 10 - 9 = 1
    // also 129 + 1 = 130
    int mod = luhnsum % 10;
    if (mod == 0)
      return "0"; // Siehe "Schritt 3" in tan_hhd_uc_v14.pdf, Seite 17
    
    int rest = 10 - mod;
    int sum = luhnsum + rest;
    
    // Von dieser Summe ziehen wir die berechnete Summe ab
    // Beispiel:
    // 130 - 129 = 1
    // 1 -> ist die Luhn-Checksumme.
    int luhn = sum - luhnsum;
    return toHex(luhn,1);
    //
    ////////////////////////////////////////////////////////////////////////////
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("VERSION:\n" + this.version + "\n");
    sb.append("LC: " + this.lc + "\n");
    sb.append("Startcode:\n" + this.startCode + "\n");
    sb.append("DE1:\n" + this.de1 + "\n");
    sb.append("DE2:\n" + this.de2 + "\n");
    sb.append("DE3:\n" + this.de3 + "\n");
    sb.append("CB : " + this.rest + "\n");
    return sb.toString();
  }
  
  /**
   * Resettet den Code.
   */
  private void reset()
  {
    this.version   = null;
    this.lc        = 0;
    this.startCode = new Startcode();
    this.de1       = new DE();
    this.de2       = new DE();
    this.de3       = new DE();
    this.rest      = null;
  }
  

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FlickerCode))
      return false;
    FlickerCode other = (FlickerCode) obj;
    
    if (this.lc != other.lc)                     return false;
    if (!this.startCode.equals(other.startCode)) return false;
    if (!this.de1.equals(other.de1))             return false;
    if (!this.de2.equals(other.de2))             return false;
    if (!this.de3.equals(other.de3))             return false;
    
    if (this.rest == null)
      return (other.rest == null);
    return this.rest.equals(other.rest);
  }

  
  
  /**
   * Bean fuer die Eigenschaften eines einzelnen DE.
   */
  public class DE
  {
    /**
     * Die tatsaechliche Laenge des DE.
     * Bereinigt um ggf. vorhandene Control-Bits.
     */
    public int length = 0;
    
    /**
     * Die Laengen-Angabe des DE im Roh-Format.
     * Sie kann noch Control-Bits enthalten, sollte daher
     * also NICHT fuer Laengenberechnungen verwendet werden.
     * In dem Fall stattdessen <code>length</code> verwenden.
     */
    public int lde    = 0;
    
    /**
     * Das Encoding der Nutzdaten.
     * Per Definition ist im Challenge HHDuc dieses Bit noch NICHT gesetzt.
     * Das Encoding passiert erst beim Rendering.
     */
    public Encoding encoding = null;
    
    /**
     * Die eigentlichen Nutzdaten des DE.
     */
    public String data  = null;
   
    /**
     * Parst das DE am Beginn des uebergebenen Strings.
     * @param s der String, dessen Anfang das DE enthaelt.
     * @return der Reststring.
     */
    String parse(String s)
    {
      // Nichts mehr zum Parsen da
      if (s == null || s.length() == 0)
        return s;

      // LDE ermitteln (dezimal)
      this.lde = Integer.parseInt(s.substring(0,2));
      s = s.substring(2); // und abschneiden

      // Control-Bits abschneiden. Die Laengen-Angabe steht nur in den Bits 0-5.
      // In den Bits 6 und 7 stehen Steuer-Informationen
      this.length  = getBitSum(this.lde,5); // Bit 0-5
      
      // Encoding gibts hier noch nicht. Das passiert erst beim Rendern
      
      // Nutzdaten ermitteln
      this.data = s.substring(0,this.length);
      s = s.substring(this.length); // und abschneiden
    
      return s;
    }

    /**
     * Rendert die Laengenangabe fuer die Uebertragung via Flickercode.
     * @return die codierten Nutzdaten.
     * Wenn das DE keine Nutzdaten enthaelt, wird "" zurueck gegeben.
     */
    String renderLength()
    {
      // Keine Daten enthalten. Dann muessen wir auch nichts weiter
      // beruecksichtigen.
      // Laut Belegungsrichtlinien TANve1.4  mit Erratum 1-3 final version vom 2010-11-12.pdf
      // duerfen im "ChallengeHHDuc" eigentlich keine leeren DEs enthalten
      // sein. Daher geben wir in dem Fall "" zurueck und nicht "00" wie in
      // tan_hhd_uc_v14.pdf angegeben. Denn mit "00" wollte es mein TAN-Generator nicht
      // lesen. Kann aber auch sein, dass der einfach nicht HHD 1.4 tauglich ist
      if (this.data == null)
        return "";

      Encoding enc = this.getEncoding();

      // Die wollen die Anzahl der Bytes, nicht die Laenge der Zeichen
      int len = renderData().length() / 2;
      
      // A) BCD -> Muss nichts weiter codiert werden.
      if (enc == Encoding.BCD)
        return toHex(len,2);

      // B) ASC -> Encoding-Bit reincodieren
      // HHD 1.4 -> in das Bit-Feld codieren
      if (FlickerCode.this.version == HHDVersion.HHD14)
      {
        len = len + (1 << BIT_ENCODING);
        return toHex(len,2);
      }

      // HHD 1.3 -> nur ne 1 im linken Halbbyte schicken
      return "1" + toHex(len,1);
    }
    
    /**
     * Liefert das zu verwendende Encoding fuer die Uebertragung via Flickercode.
     * Im Normalfall (also nach dem Parsen eines HHDuc) ist kein Encoding angegeben
     * (im Challenge HHDuc ist das per Definition nie gesetzt) machen wir ASC.
     * Es sei denn, das Encoding wurde explizit auf BCD gesetzt.
     * @return das fuer das Rendering zu verwendende Encoding.
     */
    Encoding getEncoding()
    {
      if (this.data == null)
        return Encoding.BCD;
      
      // Explizit angegeben
      if (this.encoding != null)
        return this.encoding;
      
      // Siehe tan_hhd_uc_v14.pdf, letzter Absatz in B.2.3
      // Bei SEPA-Auftraegen koennen auch Buchstaben in BIC/IBAN vorkommen.
      // In dem Fall muss auch ASC-codiert werden. Also machen wir BCD nur
      // noch dann, wenn ausschliesslich Zahlen drin stehen.
      // Das macht subsembly auch so
      // http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=75602#75602
      if (this.data.matches("[0-9]{1,}"))
        return Encoding.BCD;
      
      return Encoding.ASC;
    }
    
    /**
     * Rendert die Nutzdaten fuer die Uebertragung via Flickercode.
     * @return die codierten Nutzdaten.
     * Wenn das DE keine Nutzdaten enthaelt, wird "" zurueck gegeben.
     */
    String renderData()
    {
      if (this.data == null)
        return "";

      Encoding enc = this.getEncoding();
      if (enc == Encoding.ASC)
        return toHex(this.data);

      // Bei BCD-Encoding noch mit "F" auf Byte-Grenze ergenzen
      String s = this.data;
      if (s.length() % 2 == 1)
        s += "F";
      
      return s;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
      StringBuffer sb = new StringBuffer();
      sb.append("  Length  : " + this.length + "\n");
      sb.append("  LDE     : " + this.lde + "\n");
      sb.append("  Data    : " + this.data + "\n");
      sb.append("  Encoding: " + this.encoding + "\n");
      return sb.toString();
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
      if (!(obj instanceof DE))
        return false;
      return this.toString().equals(obj.toString());
    }
  }
  
  
  /**
   * Bean fuer die Eigenschaften des Startcodes.
   * Selbstverstaendlich sind hier so einige Sachen anders codiert als im DE.
   * Waer ja auch zu einfach sonst.
   * Die Laengen-Angabe ist anders codiert (hex statt dec). Und nach der
   * Laenge kommen nicht sofort die Nutzdaten sondern erst noch die Control-Bytes.
   */
  public class Startcode extends DE
  {
    /**
     * Die Control-Bytes.
     * In der Regel sollte das nur eines sein.
     */
    public List<Integer> controlBytes = new ArrayList<Integer>();
    
    /**
     * Parst das DE am Beginn des uebergebenen Strings.
     * @param s der String, dessen Anfang das DE enthaelt.
     * @return der Reststring.
     */
    String parse(String s)
    {
      // 1. LDE ermitteln (hex)
      this.lde = Integer.parseInt(s.substring(0,2),16);
      s = s.substring(2); // und abschneiden
      
      // 2. tatsaechliche Laenge ermitteln
      this.length = getBitSum(this.lde,5); // Bit 0-5
      
      // Encoding gibts hier noch nicht.
      // Das passiert erst beim Rendern

      // Wenn kein Control-Byte vorhanden ist, muss es HHD 1.3 sein
      FlickerCode.this.version = HHDVersion.HHD13;

      // 3. Control-Byte ermitteln, falls vorhanden
      if (isBitSet(this.lde,BIT_CONTROLBYTE))
      {
        FlickerCode.this.version = HHDVersion.HHD14;
        
        // Es darf maximal 9 Controlbytes geben
        for (int i=0;i<10;++i)
        {
          // 2 Zeichen, Hex
          int controlByte = Integer.parseInt(s.substring(0,2),16);
          this.controlBytes.add(controlByte);
          s = s.substring(2); // und abschneiden
          
          // Solange beim Controlbyte das groesste Bit gesetzt ist,
          // folgen weitere
          if (!isBitSet(controlByte,BIT_CONTROLBYTE))
            break;
        }
      }
      
      // 4. Startcode ermitteln
      this.data = s.substring(0,this.length);
      s = s.substring(this.length); // und abschneiden
      
      return s;
    }
    
    
    /**
     * @see org.kapott.hbci.manager.FlickerCode.DE#renderLength()
     * Ueberschrieben, weil wir hier noch reincodieren muessen, ob ein Controlbyte folgt.
     */
    String renderLength()
    {
      String s = super.renderLength();
      
      // HHD 1.3 -> gibt keine Controlbytes
      if (FlickerCode.this.version == HHDVersion.HHD13)
        return s;

      // HHD 1.4 -> aber keine Controlbytes vorhanden
      if (this.controlBytes.size() == 0)
        return s;

      // Controlbytes reincodieren
      int len = Integer.parseInt(s,16);

      if (this.controlBytes.size() > 0)
        len += (1 << BIT_CONTROLBYTE);

      return toHex(len,2);
    }


    /**
     * @see org.kapott.hbci.manager.FlickerCode.DE#toString()
     */
    public String toString()
    {
      StringBuffer sb = new StringBuffer(super.toString());
      sb.append("  Controlbytes: " + this.controlBytes + "\n");
      return sb.toString();
    }

    /**
     * @see org.kapott.hbci.manager.FlickerCode.DE#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
      if (!(obj instanceof Startcode))
        return false;
       return this.toString().equals(obj.toString());
    }
  }



  //////////////////////////////////////////////////////////////////////////////
  // Hilfsfunktionen fuer die Berechnungen

  /**
   * Wandelt die Zahl in Hex-Schreibweise um und fuellt links mit Nullen auf, bis die Laenge "len" erreicht ist.
   * @param n die Zahl.
   * @param len die zu erreichende Laenge.
   * @return die links mit Nullen aufgefuellte Zahl in HEX-Schreibweise.
   */
  private static String toHex(int n,int len)
  {
    String s = Integer.toString(n,16).toUpperCase();
    while (s.length() < len)
      s = "0" + s;
    return s;
  }
  
  /**
   * Wandelt alle Zeichen des String gemaess des jeweiligen ASCII-Wertes in HEX-Codierung um.
   * Beispiel: Das Zeichen "0" hat den ASCII-Wert "30" in Hexadezimal-Schreibweise.
   * @param s der umzuwandelnde String.
   * @return der codierte String.
   */
  private static String toHex(String s)
  {
    StringBuffer sb = new StringBuffer();
    char[] chars = s.toCharArray();
    for (char c:chars)
    {
      sb.append(toHex(c,2));
    }
    return sb.toString();
  }

  /**
   * Berechnet die Quersumme.
   * @param n die Zahl, deren Quersumme errechnet werden soll.
   * @return die Quersumme.
   */
  private static int quersumme(int n)
  {
    int q = 0;
    while (n != 0)
    {
      q += n % 10;
      n = (int) Math.floor(n / 10);
    }
    return q;
  }
  
  /**
   * Liefert die Summe der Bit-Wertigkeiten fuer die genannten Bits
   * (beginndend bei 0 und beim kleinsten Bit, angegebens inclusive).
   * 
   * Beispiel:
   * num  = 156 (-> 10011100)
   * bits = 5
   * 
   * Es wird die Summe der Bitwertigkeiten 2^0 bis 2^5 errechnet.
   * Also der Wert von **011100 = 2^4+2^3+s^2 = 28
   * 
   * @param num Zahl, aus der die Summe berechnet werden soll.
   * @param bits Anzahl der Bits (beginnend bei 0 und beim kleinsten Bit, angegebenes inclusive), deren Wertigkeit addiert werden soll.
   * @return der errechnete Wert.
   */
  private static int getBitSum(int num,int bits)
  {
    int sum = 0;
    for (int i=0;i<=bits;++i)
      sum += (num & (1 << i));
    return sum;
  }
  
  
  /**
   * Prueft, ob in der genannten Zahl das angegebene Bit gesetzt ist.
   * @param num die zu pruefende Zahl.
   * @param bit die Nummer des zu pruefenden Bits.
   * Wobei "0" das kleinste (rechts) und "7" das groesste (links) Bit ist.
   * @return true, wenn das Bit gesetzt ist.
   */
  private static boolean isBitSet(int num,int bit)
  {
    return (num & (1 << bit)) != 0;
  }
  //////////////////////////////////////////////////////////////////////////////
}



/**********************************************************************
 * $Log: FlickerCode.java,v $
 * Revision 1.9  2011/06/24 16:53:23  willuhn
 * @N 30-hbci4java-chiptan-reset.patch
 *
 * Revision 1.8  2011-06-09 08:06:49  willuhn
 * @N 29-hbci4java-chiptan-opt-hhd13.patch
 *
 * Revision 1.7  2011-06-07 13:45:50  willuhn
 * @N 27-hbci4java-flickercode-luhnsum.patch
 *
 * Revision 1.6  2011-05-27 15:46:13  willuhn
 * @N 23-hbci4java-chiptan-opt2.patch - Kleinere Nacharbeiten
 *
 * Revision 1.4  2011-05-27 11:21:38  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2011-05-27 11:20:36  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2011-05-27 11:19:44  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2011-05-27 10:28:38  willuhn
 * @N 22-hbci4java-chiptan-opt.patch
 *
 **********************************************************************/