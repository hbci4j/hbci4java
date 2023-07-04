/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2022 Olaf Willuhn
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


