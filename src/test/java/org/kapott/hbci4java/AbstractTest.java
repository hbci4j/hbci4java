/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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

package org.kapott.hbci4java;

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
public abstract class AbstractTest extends AbstractResourceTest
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
