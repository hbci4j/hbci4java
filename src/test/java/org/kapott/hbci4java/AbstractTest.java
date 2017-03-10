/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/hbci4java/AbstractTest.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/05/17 12:48:05 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci4java;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * Abstrakte Basis-Klasse fuer HBCI4Java-Tests.
 */
public abstract class AbstractTest
{
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
    }
    finally
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
      return new FileInputStream("test/hbci4java/" + name);
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
              bos.write(buf,0,len);
          }
          return bos.toByteArray();
      }
      finally
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
  public static void beforeClass() throws Exception
  {
    Locale.setDefault(Locale.GERMANY);
    Properties props = new Properties();
    props.put("log.loglevel.default",""+HBCIUtils.LOG_DEBUG2);
    HBCIUtils.init(props,new HBCICallbackConsole());
  }
  
  /**
   * Beendet HBCI4Java
   * @throws Exception
   */
  @AfterClass
  public static void afterClass() throws Exception
  {
    HBCIUtils.done();
  }
}



/**********************************************************************
 * $Log: AbstractTest.java,v $
 * Revision 1.1  2011/05/17 12:48:05  willuhn
 * @N Unit-Tests
 *
 **********************************************************************/