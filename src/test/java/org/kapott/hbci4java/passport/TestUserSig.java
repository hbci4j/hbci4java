/**********************************************************************
 *
 * Copyright (c) 2026 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci4java.passport;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.passport.UserSig;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet die Klasse "UserSig".
 */
public class TestUserSig extends AbstractTest
{
  /**
   * Test mit PIN und TAN.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    final String[] pintan = UserSig.decode(UserSig.encode("foo1","bar"));
    Assert.assertEquals("foo1",pintan[0]);
    Assert.assertEquals("bar",pintan[1]);
  }

  /**
   * Test mit PIN und leerer TAN.
   * @throws Exception
   */
  @Test
  public void test002() throws Exception
  {
    final String[] pintan = UserSig.decode(UserSig.encode("foo12",""));
    Assert.assertEquals("foo12",pintan[0]);
    Assert.assertEquals("",pintan[1]);
  }
  
  /**
   * Test mit leerer PIN und TAN.
   * @throws Exception
   */
  @Test
  public void test003() throws Exception
  {
    final String[] pintan = UserSig.decode(UserSig.encode("","bar"));
    Assert.assertEquals("",pintan[0]);
    Assert.assertEquals("bar",pintan[1]);
  }
  
  /**
   * Test mit PIN und NULL-TAN.
   * @throws Exception
   */
  @Test
  public void test004() throws Exception
  {
    final String[] pintan = UserSig.decode(UserSig.encode("foo123",null));
    Assert.assertEquals("foo123",pintan[0]);
    Assert.assertEquals("",pintan[1]);
  }
  /**
   * Test mit NULL-PIN und TAN.
   * @throws Exception
   */
  @Test
  public void test005() throws Exception
  {
    final String[] pintan = UserSig.decode(UserSig.encode(null,"bar1234"));
    Assert.assertEquals("",pintan[0]);
    Assert.assertEquals("bar1234",pintan[1]);
  }
  
  /**
   * Test mit NULL-PIN und NULL-TAN.
   * @throws Exception
   */
  @Test
  public void test006() throws Exception
  {
    final String[] pintan = UserSig.decode(UserSig.encode(null,null));
    Assert.assertEquals("",pintan[0]);
    Assert.assertEquals("",pintan[1]);
  }
  
  /**
   * Test mit NULL-PIN und leerer TAN.
   * @throws Exception
   */
  @Test
  public void test007() throws Exception
  {
    final String[] pintan = UserSig.decode(UserSig.encode(null,""));
    Assert.assertEquals("",pintan[0]);
    Assert.assertEquals("",pintan[1]);
  }
  
  /**
   * Test mit leerer PIN und NULL-TAN.
   * @throws Exception
   */
  @Test
  public void test008() throws Exception
  {
    final String[] pintan = UserSig.decode(UserSig.encode("",null));
    Assert.assertEquals("",pintan[0]);
    Assert.assertEquals("",pintan[1]);
  }

  /**
   * Test mit leerer PIN und leerer TAN.
   * @throws Exception
   */
  @Test
  public void test009() throws Exception
  {
    final String[] pintan = UserSig.decode(UserSig.encode("",""));
    Assert.assertEquals("",pintan[0]);
    Assert.assertEquals("",pintan[1]);
  }

  /**
   * Test mit langem Passwort mit dem ursprünglichen Trennzeichen.
   * @throws Exception
   */
  @Test
  public void test010() throws Exception
  {
    final String pin = "lKJZGoi8765t8965/)&%/&%$4||.>+:<";
    final String[] pintan = UserSig.decode(UserSig.encode(pin,null));
    Assert.assertEquals(pin,pintan[0]);
    Assert.assertEquals("",pintan[1]);
  }

  /**
   * Test mit langem Passwort und TAN mit dem ursprünglichen Trennzeichen.
   * @throws Exception
   */
  @Test
  public void test011() throws Exception
  {
    final String pin = "lKJZGoi8765t8965/)&%/&%$4||.>+:<";
    final String tan = "MJHGF7&%$||%?:Kjh87";
    final String[] pintan = UserSig.decode(UserSig.encode(pin,tan));
    Assert.assertEquals(pin,pintan[0]);
    Assert.assertEquals(tan,pintan[1]);
  }
}
