/**********************************************************************
 *
 * Copyright (c) 2022 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci4java.tools;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.tools.StringUtil;

/**
 * Testklasse fuer die String-Utils.
 */
public class TestStringUtil
{
  /**
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    Assert.assertEquals("Foo,Bar,dong",StringUtil.join(Arrays.asList("Foo","Bar","dong"),","));
  }

  /**
   * @throws Exception
   */
  @Test
  public void test002() throws Exception
  {
    Assert.assertNull(StringUtil.join(null,","));
  }

  /**
   * @throws Exception
   */
  @Test
  public void test003() throws Exception
  {
    Assert.assertEquals("",StringUtil.join(Arrays.asList(""),","));
  }

  /**
   * @throws Exception
   */
  @Test
  public void test004() throws Exception
  {
    Assert.assertEquals("foobar",StringUtil.join(Arrays.asList("foo","bar"),null));
  }

}


