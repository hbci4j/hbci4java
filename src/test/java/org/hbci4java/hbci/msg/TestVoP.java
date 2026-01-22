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

import org.hbci4java.hbci.AbstractTest;
import org.hbci4java.hbci.manager.HBCIKernelImpl;
import org.hbci4java.hbci.manager.MsgGen;
import org.hbci4java.hbci.protocol.MSG;
import org.hbci4java.hbci.protocol.factory.MSGFactory;
import org.junit.Test;

/**
 * Die DKB Bank sendet als VOP-ID einen 364 Zeichen langen Base64-Code. Offensichtlich verwenden
 * die die ID als Payload zum verschlüsselten Versand von Status-Informationen, damit die das
 * auf ihrer Seite nicht speichern müssen. Wir haben hier bisher nur maximal 256 Zeichen erwartet. 
 */
public class TestVoP extends AbstractTest
{
  /**
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    String data = getFile("testvop.txt");
    HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
    kernel.rawNewMsg("UebSEPA");
    
    MsgGen gen = kernel.getMsgGen();
    MSG msg = MSGFactory.getInstance().createMSG("CustomMsgRes",data,data.length(),gen);

    Hashtable<String,String> ht = new Hashtable<String,String>();
    msg.extractValues(ht);
  }
}
