/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/hbci4java/secmech/FlickerTest.java,v $
 * $Revision: 1.6 $
 * $Date: 2011/06/24 16:53:23 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.secmech;

import hbci4java.AbstractTest;
import org.junit.Assert;

import org.junit.Test;
import org.kapott.hbci.manager.FlickerCode;
import org.kapott.hbci.manager.FlickerRenderer;
import org.kapott.hbci.manager.FlickerCode.HHDVersion;

/**
 * Testet die Flicker-Codes.
 */
public class FlickerTest extends AbstractTest
{
  /*
   * Bereits in Flicker-Format umgewandelte Codes gibts
   * 
   * 1. In der Onlinebanking-Demo unter
   *    https://bankingportal.sskduesseldorf.de/portal/portal/StartenIPSTANDARD
   *    
   *    Einfach eine Ueberweisung erstellen und dann auf der Seite, wo der
   *    Flicker-Code angezeigt wird, in den Quelltext schauen.
   *    
   * 2. http://bitinfarkt.de/chiptan/valide_flickercodes.txt
   *    Lange Liste mit vielen Testcodes
   *    
   * Das sind aber alles leider nur HHD 1.2/1.3 Codes. Keine 1.4er. 
   */
  

  /**
   * Testet das Parsen eines Challenge HHDuc, wie er via HBCI uebertragen wird
   * sowie das Umwandeln in den FlickerCode.
   * @throws Exception
   */
  @Test
  public void test1() throws Exception
  {
    // Code von einem User - anonymisiert
    FlickerCode code = new FlickerCode("039870110490631098765432100812345678041,00");

    FlickerCode expected = new FlickerCode();
    expected.lc = 39;
    expected.startCode.lde      = 135;
    expected.startCode.length   = 7;
    expected.startCode.data     = "1049063";
    expected.startCode.encoding = null;
    expected.startCode.controlBytes.add(1);
    expected.de1.lde      = 10;
    expected.de1.length   = 10;
    expected.de1.data     = "9876543210";
    expected.de1.encoding = null;
    expected.de2.lde      = 8;
    expected.de2.length   = 8;
    expected.de2.data     = "12345678";
    expected.de2.encoding = null;
    expected.de3.lde      = 4;
    expected.de3.length   = 4;
    expected.de3.data     = "1,00";
    expected.de3.encoding = null;

    // Code muss dem erwarteten entsprechen
    Assert.assertEquals(expected,code);
    
    // In Flicker-Format wandeln
    String rendered = code.render();
    Assert.assertEquals(rendered,"1784011049063F059876543210041234567844312C303019");
  }

  /**
   * Testet das Parsen eines Challenge HHDuc, wie er via HBCI uebertragen wird
   * sowie das Umwandeln in den FlickerCode.
   * @throws Exception
   */
  @Test
  public void test2() throws Exception
  {
    // Code von einem User - anonymisiert
    FlickerCode code = new FlickerCode("039870110418751012345678900812030000040,20");

    FlickerCode expected = new FlickerCode();
    expected.lc = 39;
    expected.startCode.lde      = 135;
    expected.startCode.length   = 7;
    expected.startCode.data     = "1041875";
    expected.startCode.encoding = null;
    expected.startCode.controlBytes.add(1);
    expected.de1.lde      = 10;
    expected.de1.length   = 10;
    expected.de1.data     = "1234567890";
    expected.de1.encoding = null;
    expected.de2.lde      = 8;
    expected.de2.length   = 8;
    expected.de2.data     = "12030000";
    expected.de2.encoding = null;
    expected.de3.lde      = 4;
    expected.de3.length   = 4;
    expected.de3.data     = "0,20";
    expected.de3.encoding = null;

    // Code muss dem erwarteten entsprechen
    Assert.assertEquals(expected,code);
    
    // In Flicker-Format wandeln
    String rendered = code.render();
    Assert.assertEquals(rendered,"1784011041875F051234567890041203000044302C323015");
  }

  /**
   * Testet das Parsen eines Challenge HHDuc, wie er via HBCI uebertragen wird
   * sowie das Umwandeln in den FlickerCode.
   * @throws Exception
   */
  @Test
  public void test3() throws Exception
  {
    // Beispiel-Code aus der Spec
    // Belegungsrichtlinien TANve1.4  mit Erratum 1-3 final version vom 2010-11-12.pdf
    FlickerCode code = new FlickerCode("0248A0120452019980812345678");
      
    FlickerCode expected = new FlickerCode();
    expected.lc = 24;
    expected.startCode.lde      = 138;
    expected.startCode.length   = 10;
    expected.startCode.data     = "2045201998";
    expected.startCode.encoding = null;
    expected.startCode.controlBytes.add(1);
    expected.de1.lde      = 8;
    expected.de1.length   = 8;
    expected.de1.data     = "12345678";
    expected.de1.encoding = null;
    expected.de2.data     = null;
    expected.de3.data     = null;
    
    // Code muss dem erwarteten entsprechen
    Assert.assertEquals(expected,code);
    
    // In Flicker-Format wandeln
    String rendered = code.render();
    Assert.assertEquals(rendered,"0D85012045201998041234567855");
  }

  /**
   * Testet das Parsen eines Challenge HHDuc, wie er via HBCI uebertragen wird
   * sowie das Umwandeln in den FlickerCode.
   * @throws Exception
   */
  @Test
  public void test4() throws Exception
  {
    // Code von http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=60532
    // Stammt nicht aus dem "Challenge HHDuc" sondern aus dem Freitext
    // des TAN-Dialoges
    // Das zwischen "CHLGUC$4zahlen" und "CHLGTEXT" ist der Flickercode.
    // Das $4zahlen gibt IMHO an, wie lang der danach folgende Flicker-Code ist
    // ...TAN-Nummer: CHLGUC 002624088715131306389726041,00CHLGTEXT0244 Sie haben eine...

    FlickerCode code = new FlickerCode("...TAN-Nummer: CHLGUC 002624088715131306389726041,00CHLGTEXT0244 Sie h...");

    // Das hier soll am Ende rauskommen
    FlickerCode expected = new FlickerCode();
    expected.lc = 24;
    expected.startCode.lde      = 8;
    expected.startCode.length   = 8;
    expected.startCode.data     = "87151313";
    expected.startCode.encoding = null;
    expected.de1.lde      = 6;
    expected.de1.length   = 6;
    expected.de1.data     = "389726";
    expected.de1.encoding = null;
    expected.de2.lde      = 4;
    expected.de2.length   = 4;
    expected.de2.data     = "1,00";
    expected.de2.encoding = null;
    expected.de3.data     = null;

    // Code muss dem erwarteten entsprechen
    Assert.assertEquals(expected,code);
    
    // In Flicker-Format wandeln
    String rendered = code.render();
    Assert.assertEquals(rendered,"0F04871513130338972614312C30303B");
  }

  /**
   * Enthaelt ein DE irgendwas ausser 0-9, muss es ASC-codiert werden.
   * @throws Exception
   */
  @Test
  public void test5() throws Exception
  {
    FlickerCode code = new FlickerCode("0248A01204520199808123F5678");
      
    FlickerCode expected = new FlickerCode();
    expected.lc = 24;
    expected.startCode.lde      = 138;
    expected.startCode.length   = 10;
    expected.startCode.data     = "2045201998";
    expected.startCode.encoding = null;
    expected.startCode.controlBytes.add(1);
    expected.de1.lde      = 8;
    expected.de1.length   = 8;
    expected.de1.data     = "123F5678";
    expected.de1.encoding = null;
    expected.de2.data     = null;
    expected.de3.data     = null;
    
    // Code muss dem erwarteten entsprechen
    Assert.assertEquals(expected,code);
    
    // In Flicker-Format wandeln
    String rendered = code.render();
    Assert.assertEquals(rendered,"118501204520199848313233463536373875");
  }

  /**
   * Testet, dass die Luhn-Checksumme 0 lautet (Sonderbedingung)
   * @throws Exception
   */
  @Test
  public void test6() throws Exception
  {
    FlickerCode code = new FlickerCode();
    code.version = HHDVersion.HHD14;
    code.startCode.data = "1120492";
    code.startCode.controlBytes.add(1);
    code.de1.data = "30084403";
    code.de2.data = "450,00";
    code.de3.data = "2";
    
    String rendered = code.render();
    Assert.assertEquals(rendered,"1584011120492F0430084403463435302C3030012F05");
  }
  
  /**
   * Das ist ein "echter" HHD-1.3-Code, der nicht im Challenge-Freitext sondern
   * tatsaechlich im Challenge HHDuc uebertragen wurde. Erkennbar daran, dass
   * das LC nur 2 Zeichen lang ist. Stammt von der Postbank.
   * @throws Exception
   */
  @Test
  public void test7() throws Exception
  {
    FlickerCode code = new FlickerCode("190277071234567041,00");
    
    FlickerCode expected = new FlickerCode();
    expected.lc = 19;
    expected.startCode.lde      = 2;
    expected.startCode.length   = 2;
    expected.startCode.data     = "77";
    expected.de1.lde      = 7;
    expected.de1.length   = 7;
    expected.de1.data     = "1234567";
    expected.de2.lde      = 4;
    expected.de2.length   = 4;
    expected.de2.data     = "1,00";
    
    // Code muss dem erwarteten entsprechen
    Assert.assertEquals(expected,code);
  }

  /**
   * Das ist ein Code von einem User, der zwar korrekt als HHD 1.3 erkannt
   * wurde - aber der Fehler beim Versuch, es als HHD 1.4 zu parsen, trat
   * zu einem Zeitpunkt auf, wo schon ein Controlbyte falsch gelesen wurde.
   * Daraufhin habe ich die neue Funktion "rest()" eingebaut, die sicherstellt,
   * dass von dem 1.4er Versuch keine Fragmente mehr uebrig sind, wenn im
   * zweiten Versuch als 1.3 gelesen wird.
   * @throws Exception
   */
  @Test
  public void test8() throws Exception
  {
    FlickerCode code = new FlickerCode("250891715637071234567041,00");
    Assert.assertEquals(code.version,HHDVersion.HHD13);
    Assert.assertEquals(0,code.startCode.controlBytes.size());
  }


  /**
   * Testet das korrekte Rendern.
   * @throws Exception
   */
  @Test
  public void testRender1() throws Exception
  {
    // Beispiel-Code aus der Spec
    // Belegungsrichtlinien TANve1.4  mit Erratum 1-3 final version vom 2010-11-12.pdf
    FlickerCode code = new FlickerCode("0248A0120452019980812345678");
    String flicker = code.render();
    
    // Die gesammelten Daten
    final StringBuffer collected = new StringBuffer();

    // Wir loggen mit, was der Renderer ausgibt und vergleichen es mit unseren Daten.
    FlickerRenderer renderer = new FlickerRenderer(flicker)
    {
      /**
       * @see org.kapott.hbci.manager.FlickerRenderer#paint(boolean, boolean, boolean, boolean, boolean)
       */
      public void paint(boolean b1, boolean b2, boolean b3, boolean b4,boolean b5)
      {
        collected.append(b1 ? "1" : "0");
        collected.append(b2 ? "1" : "0");
        collected.append(b3 ? "1" : "0");
        collected.append(b4 ? "1" : "0");
        collected.append(b5 ? "1" : "0");
        collected.append(" ");
      }

      /**
       * @see org.kapott.hbci.manager.FlickerRenderer#done(int)
       */
      public void done(int iterations)
      {
        // nach 2 Durchlaeufen brechen wir ab.
        if (iterations >= 2)
          stop();
      }
    };
    
    renderer.setFrequency(FlickerRenderer.FREQUENCY_MAX);
    renderer.start();
    renderer.waitFor();
    
    // Die erwarteten Daten. Das sind genau 2 Durchlaeufe. (Das Leerzeichen am Ende ist wichtig)
    StringBuffer expected = new StringBuffer();
    expected.append("11111 01111 10000 00000 11111 01111 11111 01111 11011 01011 10000 00000 11010 01010 10001 00001 11000 01000 10000 00000 10000 00000 10100 00100 11010 01010 10010 00010 10000 00000 10100 00100 11001 01001 11000 01000 10001 00001 11001 01001 10010 00010 10000 00000 10100 00100 11000 01000 10010 00010 11100 01100 10110 00110 11010 01010 10001 00001 11110 01110 11010 01010 11010 01010 ");
    expected.append("11111 01111 10000 00000 11111 01111 11111 01111 11011 01011 10000 00000 11010 01010 10001 00001 11000 01000 10000 00000 10000 00000 10100 00100 11010 01010 10010 00010 10000 00000 10100 00100 11001 01001 11000 01000 10001 00001 11001 01001 10010 00010 10000 00000 10100 00100 11000 01000 10010 00010 11100 01100 10110 00110 11010 01010 10001 00001 11110 01110 11010 01010 11010 01010 ");

    Assert.assertEquals(collected.toString(),expected.toString());
  }
  
}



/**********************************************************************
 * $Log: FlickerTest.java,v $
 * Revision 1.6  2011/06/24 16:53:23  willuhn
 * @N 30-hbci4java-chiptan-reset.patch
 *
 * Revision 1.5  2011-06-09 08:06:49  willuhn
 * @N 29-hbci4java-chiptan-opt-hhd13.patch
 *
 * Revision 1.4  2011-06-07 13:45:50  willuhn
 * @N 27-hbci4java-flickercode-luhnsum.patch
 *
 * Revision 1.3  2011-05-27 15:46:13  willuhn
 * @N 23-hbci4java-chiptan-opt2.patch - Kleinere Nacharbeiten
 *
 * Revision 1.2  2011-05-27 11:15:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2011-05-27 10:28:38  willuhn
 * @N 22-hbci4java-chiptan-opt.patch
 *
 **********************************************************************/