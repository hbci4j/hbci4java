/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/hbci4java/secmech/FlickerTest.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/05/27 10:28:38 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.secmech;

import hbci4java.AbstractTest;
import junit.framework.Assert;

import org.junit.Test;
import org.kapott.hbci.manager.FlickerCode;
import org.kapott.hbci.manager.FlickerRenderer;

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
    // Das zwischen "CHLGUC" und "CHLGTEXT" ist der Flickercode. Die fuehrende
    // "0" muss aber auch noch weg. Keine Ahnung, warum die da steht.
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
 * Revision 1.1  2011/05/27 10:28:38  willuhn
 * @N 22-hbci4java-chiptan-opt.patch
 *
 **********************************************************************/