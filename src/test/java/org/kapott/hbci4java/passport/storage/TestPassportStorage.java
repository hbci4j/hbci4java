/**********************************************************************
 *
 * Copyright (c) 2026 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci4java.passport.storage;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci4java.AbstractResourceTest;
import org.kapott.hbci4java.callback.HBCICallbackTest;

/**
 * Testet den Passport-Storage.
 */
public class TestPassportStorage extends AbstractResourceTest
{
  /**
   * Erzeugt das Passport-Objekt.
   * @throws Exception
   */
  @Before
  public void before() throws Exception
  {
    final HBCICallbackTest callback = new HBCICallbackTest();
    callback.put(HBCICallback.NEED_BLZ,"12345678");
    callback.put(HBCICallback.NEED_COUNTRY,"DE");
    callback.put(HBCICallback.NEED_HOST,"https://fints-demobank.local/fints/test123");
    callback.put(HBCICallback.NEED_PORT,"443");
    callback.put(HBCICallback.NEED_FILTER,"Base64");
    callback.put(HBCICallback.NEED_USERID,"1234567890");
    callback.put(HBCICallback.NEED_CUSTOMERID,"1234567890");
    callback.put(HBCICallback.NEED_PT_PIN,"12345");
    callback.put(HBCICallback.NEED_CONNECTION,"");
    callback.put(HBCICallback.CLOSE_CONNECTION,"");
    callback.put(HBCICallback.NEED_PT_SECMECH,"921");
    callback.put(HBCICallback.NEED_PT_TANMEDIA,"foo");
    
    callback.put(HBCICallback.NEED_PASSPHRASE_LOAD,"test1234");
    callback.put(HBCICallback.NEED_PASSPHRASE_SAVE,"test1234");

    final Properties props = new Properties();
    props.put("log.loglevel.default",Integer.toString(HBCIUtils.LOG_INFO));
    
    HBCIUtils.init(props,callback);
  }
  
  /**
   * Testet das Speichern und Laden eines Passport.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    final File file = File.createTempFile("hbci4java-testpassport", ".pt");
    file.deleteOnExit();

    // Passport erzeugen
    final HBCIPassport save = AbstractHBCIPassport.getInstance("PinTan",file);
    save.setBLZ("12345678");
    save.setUserId("testuser");
    save.setCustomerId("testcustomer");
    save.saveChanges();
    
    final HBCIPassport read = AbstractHBCIPassport.getInstance("PinTan",file);
    Assert.assertNotNull(read);
    Assert.assertEquals("12345678", read.getBLZ());
    Assert.assertEquals("testuser", read.getUserId());
    Assert.assertEquals("testcustomer", read.getCustomerId());

    // Cleanup
    HBCIUtils.done();
  }

  /**
   * Testet Laden eines gespeicherten Passport im AES v1-Format.
   * @throws Exception
   */
  @Test
  public void test002() throws Exception
  {
    final byte[] data = this.getBytes("hbci4java-testpassport.pt");

    final File file = File.createTempFile("hbci4java-testpassport", ".pt");
    file.deleteOnExit();
    
    Files.write(file.toPath(),data);

    final HBCIPassport read = AbstractHBCIPassport.getInstance("PinTan",file);
    Assert.assertNotNull(read);
    Assert.assertEquals("12345678", read.getBLZ());
    Assert.assertEquals("testuser", read.getUserId());
    Assert.assertEquals("testcustomer", read.getCustomerId());

    // Cleanup
    HBCIUtils.done();
  }

}
