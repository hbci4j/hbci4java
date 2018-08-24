/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPLv2
 *
 **********************************************************************/

package org.kapott.hbci4java;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * Abstrakte Basis-Klasse fuer HBCI4Java-Tests.
 */
public abstract class AbstractTest
{
  private final static AtomicBoolean initialized = new AtomicBoolean(false);
  
  /**
   * System-Property, mit dem festgelegt werden kann, ob die Tests ausgefuehrt
   * werden sollen, die eine Onlineverbindung (z.Bsp. zur Bank) erfordern.
   * Default-Wert ist false.
   */
  public final static String SYSPROP_ONLINE = "test.online";

  /**
   * System-Property, mit dem festgelegt werden kann, ob die Tests ausgefuehrt
   * werden sollen, die Kartenleser und Chipkarte erfordern.
   * Default-Wert ist false.
   */
  public final static String SYSPROP_CHIPCARD = "test.chipcard";

  /**
   * System-Property, mit dem festgelegt werden kann, ob Performance-Tests durchgefuehrt werden sollen.
   * Default-Wert ist true.
   */
  public final static String SYSPROP_PERFORMANCE = "test.performance";

  /**
   * Liefert den Inhalt der angegebenen Datei.
   * @param name Dateiname.
   * @return der Inhalt der Datei.
   * @throws Exception
   */
  public String getFile(String name) throws Exception
  {
    BufferedReader reader = null;
    try
    {
      StringBuffer sb = new StringBuffer();
      reader = new BufferedReader(new InputStreamReader(this.getStream(name)));
      String line = null;
      while ((line = reader.readLine()) != null)
        sb.append(line.trim());
      return sb.toString();
    } finally
    {
      if (reader != null)
        reader.close();
    }
  }

  /**
   * Liefert einen Inputstream fuer die angegebene Datei.
   * @param name der Dateiname.
   * @return der Stream.
   * @throws Exception
   */
  public InputStream getStream(String name) throws Exception
  {
    InputStream is = this.getClass().getResourceAsStream(name);
    if (is != null)
      return is;

    URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
    String path = this.getClass().getPackage().getName().replace('.',File.separatorChar);
    String msg = "Datei \"" + name + "\" nicht gefunden in: " + new File(url.getPath() + path + File.separator + name).getCanonicalPath();
    System.err.println(msg);
    throw new IOException(msg);
  }

  /**
   * Liest die angegebene Datei und liefert den Inhalt zurueck.
   * @param name der Dateiname.
   * @return die Binaer-Daten aus der Datei.
   * @throws Exception
   */
  public byte[] getBytes(String name) throws Exception
  {
    InputStream is = null;
    try
    {
      is = this.getStream(name);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      int len = 0;
      byte[] buf = new byte[1024];
      while ((len = is.read(buf)) != -1)
      {
        bos.write(buf, 0, len);
      }
      return bos.toByteArray();
    } finally
    {
      if (is != null)
        is.close();
    }
  }

  /**
   * Initialisiert HBCI4Java.
   * @throws Exception
   */
  @BeforeClass
  public static void beforeClassAbstractTest() throws Exception
  {
    Locale.setDefault(Locale.GERMANY);
    Properties props = new Properties();
    props.put("log.loglevel.default", "" + HBCIUtils.LOG_DEBUG2);
    props.putAll(System.getProperties());
    HBCIUtils.init(props, new HBCICallbackConsole());
    initialized.set(true);
  }
  
  /**
   * Beendet HBCI4Java
   * @throws Exception
   */
  @AfterClass
  public static void afterClassAbstractTest() throws Exception
  {
    if (initialized.get())
      HBCIUtils.done();
  }
}
