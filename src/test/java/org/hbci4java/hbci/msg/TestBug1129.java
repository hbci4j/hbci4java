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

package org.hbci4java.hbci.msg;

import java.util.Hashtable;

import org.hbci4java.hbci.exceptions.ParseErrorException;
import org.hbci4java.hbci.AbstractTest;
import org.hbci4java.hbci.manager.HBCIKernelImpl;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.MsgGen;
import org.hbci4java.hbci.protocol.MSG;
import org.hbci4java.hbci.protocol.factory.MSGFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests fuer BUGZILLA 1129.
 */
public class TestBug1129 extends AbstractTest
{
  /**
   * Versucht, die Datei mit dem Response zu parsen.
   * @return die geparsten Daten.
   * @throws Exception
   */
  private Hashtable<String, String> parse() throws Exception
  {
    String data = this.getFile("bugzilla-1129.txt");
    HBCIKernelImpl kernel = new HBCIKernelImpl(null,"plus");
    kernel.rawNewMsg("DauerList");
    
    MsgGen gen = kernel.getMsgGen();
    MSG msg = MSGFactory.getInstance().createMSG("CustomMsgRes",data,data.length(),gen);

    Hashtable<String,String> ht = new Hashtable<String,String>();
    msg.extractValues(ht);
    return ht;
  }
  
  /**
   * Testet das Parsen eines Responses mit ungueltigem DTAUS ohne Fehlertoleranz.
   * Code muss einen Fehler werfen.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    try
    {
      parse();
      throw new Exception("Test-Code haette eine Exception werfen muessen");
    }
    catch (Exception e)
    {
      Assert.assertEquals(ParseErrorException.class,e.getClass());
      Assert.assertTrue(((ParseErrorException)e).isFatal());
    }
  }

  /**
   * Testet das Parsen eines Responses mit ungueltigem DTAUS MIT Fehlertoleranz.
   * @throws Exception
   */
  @Test
  public void test002() throws Exception
  {
    HBCIUtils.setParam("client.errors.ignoreWrongDataSyntaxErrors","yes");
    parse();
  }

  /**
   * Testet das Decodieren der DIN-66003 Umlaute.
   * @throws Exception
   */
  @Test
  public void test003() throws Exception
  {
    HBCIUtils.setParam("client.errors.ignoreWrongDataSyntaxErrors","yes");
    Hashtable<String, String> ht = parse();
    Assert.assertEquals("EBÜHREN Z.T. IM VORAUS",ht.get("CustomMsgRes.GVRes_6.DauerListRes4.usage.usage_3"));
  }

}
