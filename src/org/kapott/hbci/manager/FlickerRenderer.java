/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/src/org/kapott/hbci/manager/FlickerRenderer.java,v $
 * $Revision: 1.6 $
 * $Date: 2011/06/07 13:55:08 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uebernimmt das Umwandeln eines Flicker-Codes in die blinkende Darstellung.
 * Da wir hier in HBCI4Java ja keinen GUI-spezifischen Code (Swing, SWT, etc.)
 * haben, uebernimmt die Klasse lediglich das Erzeugen der schwarzen und weissen
 * Balken sowie das Timing. Sie ruft dann im Wiedergabe-Takt die Funktion paint()
 * auf, die ueberschrieben werden muss, um dort dann das eigentliche Zeichnen
 * der 5 Balken durchzufuehren.
 * Die paint-Funktion wird so ca. 10-20 mal pro Sekunde aufgerufen, sollte die
 * Ausgabe auf dem Bildschirm daher flott machen ;)
 */
public class FlickerRenderer
{
  /**
   * Default-Taktfequenz in Hz.
   * Soll laut tan_hhd_uc_v14.pdf, Kapitel C.1 zwischen 2 und 20 Hz liegen.
   */
  public final static int FREQUENCY_DEFAULT = 10;
  
  /**
   * Minimale Taktfrequenz.
   */
  public final static int FREQUENCY_MIN     = 2;
  
  /**
   * Maximale Taktfrequenz.
   * Laut Spec. sind die Geraete bis 20 Hz zugelassen, viele koennen aber schneller.
   */
  public final static int FREQUENCY_MAX = 40;
  
  private int halfbyteid       = 0;
  private int clock            = 0;
  private List<int[]> bitarray = null;
  
  private Thread thread = null;
  private int iterations = 0;
  private int freq       = FREQUENCY_DEFAULT;
  
  /**
   * ct.
   * @param code der zu rendernde Flicker-Code.
   * Er wird von HBCI4Java ueber den Callback NEED_PT_TA uebergeben.
   * 
   * Etwa so:
   * 
   * case HBCICallback.NEED_PT_TAN:
   *   String flicker = retData.toString();
   *   if (flicker != null && flicker.length() > 0)
   *   {
   *     MyFlickerRenderer = new FlickerRenderer(flicker) {
   *       // hier paint() ueberschreiben
   *     };
   *   }
   */
  public FlickerRenderer(String code)
  {
    // Sync-Identifier vorn dran haengen.
    code = "0FFF" + code;

    // Das Bitfeld mit der BCD-Codierung.
    // Koennte man auch in einer For-Schleife und etwas Bit-Schieberei machen.
    // Aber das ist ist besser lesbar ;)
    Map<String,int[]> bcdmap = new HashMap<String,int[]>();
    bcdmap.put("0",new int[]{0, 0, 0, 0, 0});
    bcdmap.put("1",new int[]{0, 1, 0, 0, 0});
    bcdmap.put("2",new int[]{0, 0, 1, 0, 0});
    bcdmap.put("3",new int[]{0, 1, 1, 0, 0});
    bcdmap.put("4",new int[]{0, 0, 0, 1, 0});
    bcdmap.put("5",new int[]{0, 1, 0, 1, 0});
    bcdmap.put("6",new int[]{0, 0, 1, 1, 0});
    bcdmap.put("7",new int[]{0, 1, 1, 1, 0});
    bcdmap.put("8",new int[]{0, 0, 0, 0, 1});
    bcdmap.put("9",new int[]{0, 1, 0, 0, 1});
    bcdmap.put("A",new int[]{0, 0, 1, 0, 1});
    bcdmap.put("B",new int[]{0, 1, 1, 0, 1});
    bcdmap.put("C",new int[]{0, 0, 0, 1, 1});
    bcdmap.put("D",new int[]{0, 1, 0, 1, 1});
    bcdmap.put("E",new int[]{0, 0, 1, 1, 1});
    bcdmap.put("F",new int[]{0, 1, 1, 1, 1});

    // Wir mappen den Code basierend auf dem Bit-Array.
    // Linkes und rechtes Zeichen jedes Bytes wird getauscht.
    this.bitarray = new ArrayList<int[]>();
    for (int i = 0; i < code.length(); i += 2) {
      bitarray.add(bcdmap.get(Character.toString(code.charAt(i+1))));
      bitarray.add(bcdmap.get(Character.toString(code.charAt(i))));
    }
  }
  
  /**
   * Legt die Taktfrequenz in Hz fest.
   * @param hz die zu verwendende Taktfrequenz.
   * Es werden nur Werte zwischen {@link FlickerRenderer#FREQUENCY_MIN} und
   * {@link FlickerRenderer#FREQUENCY_MAX} akzeptiert.
   */
  public void setFrequency(int hz)
  {
    if (hz < FREQUENCY_MIN || hz > FREQUENCY_MAX)
      return;
    this.freq = hz;
  }
  
  /**
   * Startet das Rendering des Flicker-Codes.
   * Die Funktion startet einen neuen Thread, kehrt also sofort zurueck.
   * 
   * Da diese Funktion einen neuen Thread startet und somit sofort
   * zurueckkehrt, kann es ggf. noetig sein, eine Warteschleife zu
   * implementieren. Hierzu kann einfach die Funktion "waitFor()" aufgerufen
   * werden. Sie pausiert solange, bis "stop()" augerufen wurde.
   * 
   * Beispiel:
   * 
   * FlickerRenderer renderer = new FlickerRenderer(meinCode) {
   *   public void paint(boolean b1,boolean b2,boolean b3,boolean b4,boolean b5)
   *   {
   *     // hier eigenen Code zum Rendern einbauen
   *   }
   *   
   *   public void done(int iterations)
   *   {
   *     // Nach 20 Uebertragungen hoeren wir auf.
   *     if (iterations > 20)
   *       stop();
   *   }
   * };
   * 
   * renderer.start();
   * renderer.waitFor();
   * System.out.println("Code uebertragen");
   * 
   */
  public final synchronized void start()
  {
    // ggf. laufenden Thread beenden
    stop();
    
    this.thread = new Thread("Flicker Update-Thread")
    {
      public void run()
      {
        // Wir fangen beim ersten Halbbyte an.
        halfbyteid = 0;
        
        // Die Clock, die immer hin und her kippt. Wir beginnen bei 1.
        // Sonst wuerde das allererste Zeichen nur einmal uebertragen
        // werden, was bewirkt, dass der Code erst einmal komplett
        // durchlaufen muesste, bevor wir einen kompletten gesendet haetten
        clock      = 1;

        try
        {
          // Die Endlos-Schleife mit der Uebertragung
          while (true)
          {
            int[] bits = bitarray.get(halfbyteid);
            
            bits[0] = clock;
            paint(bits[0] == 1,bits[1] == 1,bits[2] == 1,bits[3] == 1,bits[4] == 1);
            
            clock--;
            if (clock < 0)
            {
              clock = 1;
              
              // Jedes Zeichen muss doppelt uebertragen werden. Einmal mit clock 0
              // und einmal mit clock 1.
              halfbyteid++;
              if (halfbyteid >= bitarray.size())
              {
                halfbyteid = 0;

                // Wir sind einmal mit dem Code komplett durch
                iterations++;
                done(iterations);
              }
            }
            
            // Warten
            // Wir errechnen die Wartezeit in jedem Durchlauf.
            // Dann kann die Frequenz auch waehrend des Blinkens geaendert werden.
            long sleep = 1000L / freq;
            sleep(sleep);
          }
        }
        catch (InterruptedException e)
        {
          // Ende der Anzeige
        }
      }
    };
    thread.start();
  }
  
  /**
   * Stoppt das Rendern.
   */
  public final void stop()
  {
    if (this.thread != null)
    {
      try
      {
        if (this.thread != null)
        {
          this.thread.interrupt();
          synchronized (this.thread)
          {
            this.thread.notifyAll();
          }
        }
      }
      finally
      {
        this.thread = null;
      }
    }
  }
  
  /**
   * Wird immer dann aufgerufen, wenn die 5 Balken der Flicker-Grafik aktualisiert
   * werden sollen. Die 5 Boolean-Parameter legen die aktuell anzuzeigende Codierung
   * fest.
   * 
   * Die Default-Implementierung ist leer.
   * 
   * Diese Funktion muss auf jeden Fall ueberschrieben werden. Sonst kriegt man
   * keinen Flicker-Code.
   * 
   * @param b1 Balken 1. true=weiss, false=schwarz
   * @param b2 Balken 2. true=weiss, false=schwarz
   * @param b3 Balken 3. true=weiss, false=schwarz
   * @param b4 Balken 4. true=weiss, false=schwarz
   * @param b5 Balken 5. true=weiss, false=schwarz
   */
  public void paint(boolean b1,boolean b2,boolean b3,boolean b4,boolean b5)
  {
  }
  
  /**
   * Wird immer dann aufgerufen, nachdem der Flicker-Code einmal komplett
   * an den TAN-Generator uebertragen wurde.
   * Die Funktion wird zum ersten Mal NACH der ersten Uebertragung aufgerufen
   * und anschliessend nach jeder weiteren.
   * 
   * Die Default-Implementierung ist leer.
   * 
   * Fuer gewoehnlich wird die Funktion zur Darstellung nicht benoetigt.
   * Sie kann aber zu Debugging-Zwecken verwendet werden oder zum automatischen
   * Abbruch nach einer definierten Anzahl von Uebertragungen.
   * Sie muss also nicht ueberschrieben werden.
   * 
   * Die Funktion wird direkt im Flicker-Thread aufgerufen. Sie sollte daher
   * auf keinen Fall irgendwas aufwaendiges machen, da das zum Ausbremsen
   * der Flicker-Uebertragung fuehren wuerde.
   * 
   * @param iterations Anzahl der bisherigen Uebertragungen (beginnend bei 1 -
   * da die Funktion ja erst nach der ersten Uebertragung aufgerufen wird)
   */
  public void done(int iterations)
  {
  }
  
  /**
   * Kann verwendet werden, um den Aufrufer-Thread solange zu pausieren,
   * bis "stop()" aufgerufen wurde. Damit kann warten, bis die Uebertragung
   * abgeschlossen ist.
   */
  public final synchronized void waitFor()
  {
    if (this.thread == null)
      return;
    
    synchronized (this.thread)
    {
      if (this.thread == null)
        return;
      
      try
      {
        this.thread.wait();
      }
      catch (InterruptedException e)
      {
        // Wir sind raus.
      }
    }
  }
}



/**********************************************************************
 * $Log: FlickerRenderer.java,v $
 * Revision 1.6  2011/06/07 13:55:08  willuhn
 * @N 28-hbci4java-flicker-speed2.patch
 *
 * Revision 1.5  2011-06-06 15:32:51  willuhn
 * @N 26-hbci4java-flicker-speed.patch
 *
 * Revision 1.3  2011-06-06 15:25:12  willuhn
 * @N 26-hbci4java-flicker-speed.patch
 *
 * Revision 1.2  2011-05-27 15:46:13  willuhn
 * @N 23-hbci4java-chiptan-opt2.patch - Kleinere Nacharbeiten
 *
 * Revision 1.1  2011-05-27 10:28:38  willuhn
 * @N 22-hbci4java-chiptan-opt.patch
 *
 **********************************************************************/