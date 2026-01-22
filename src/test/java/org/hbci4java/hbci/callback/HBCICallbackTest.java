/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2024 Olaf Willuhn
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

package org.hbci4java.hbci.callback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hbci4java.hbci.callback.HBCICallbackIOStreams;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.passport.HBCIPassport;

/**
 * Implementierung eines Callbacks für Tests.
 */
public class HBCICallbackTest extends HBCICallbackIOStreams
{
  /**
   * ct.
   */
  public HBCICallbackTest()
  {
    super(System.out, null);
  }

  private Map<Integer,String> values = new HashMap<>();
  
  /**
   * Speichert einen vordefinierten Wert im Callback.
   * @param callback der Callback-Grund.
   * @param value der zu verwendende Wert.
   */
  public void put(int callback, String value)
  {
    this.values.put(callback,value);
  }
  
  /**
   * @see org.hbci4java.hbci.callback.HBCICallback#callback(org.hbci4java.hbci.passport.HBCIPassport, int, java.lang.String, int, java.lang.StringBuffer)
   */
  @Override
  public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData)
  {
    final String value = this.values.get(reason);
    if (value != null)
    {
      retData.replace(0,retData.length(),value);
      return;
    }
    
    HBCIUtils.log("have no data for callback [reason: " + reason + ", msg: " + msg + "]",HBCIUtils.LOG_WARN);
    super.callback(passport, reason, msg, datatype, retData);
  }
  
  /**
   * @see org.hbci4java.hbci.callback.HBCICallbackIOStreams#status(org.hbci4java.hbci.passport.HBCIPassport, int, java.lang.Object[])
   */
  @Override
  public synchronized void status(HBCIPassport passport, int statusTag, Object[] o)
  {
  }
  
  /**
   * @see org.hbci4java.hbci.callback.HBCICallbackIOStreams#readLine()
   */
  @Override
  protected String readLine() throws IOException
  {
    throw new UnsupportedOperationException("reading from STDIN not supported in unit tests");
  }
}
