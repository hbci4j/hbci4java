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

package org.hbci4java;

import java.util.Date;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.passport.HBCIPassport;

/**
 * Implementierung eines Callbacks mit vordefinierten Werten.
 */
public class HBCI4JavaCallback implements HBCICallback
{
  private HBCI4JavaClient client = null;
  private HBCICallback parent = null;
  
  /**
   * ct.
   * @param client der Client.
   * @param parent das Parent.
   */
  HBCI4JavaCallback(HBCI4JavaClient client, HBCICallback parent)
  {
    this.client = client;
    this.parent = parent != null ? parent : new HBCICallbackConsole();
  }

  /**
   * @see org.kapott.hbci.callback.HBCICallback#log(java.lang.String, int, java.util.Date, java.lang.StackTraceElement)
   */
  @Override
  public void log(String msg, int level, Date date, StackTraceElement trace)
  {
    this.parent.log(msg,level,date,trace);
  }

  /**
   * @see org.kapott.hbci.callback.HBCICallback#callback(org.kapott.hbci.passport.HBCIPassport, int, java.lang.String, int, java.lang.StringBuffer)
   */
  @Override
  public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData)
  {
    // Checken, ob wir für die Antwort einen vordefinierten Wert aus der Config haben.
    // Wenn ja, liefern wir ihn direkt. Andernfalls delegieren wir an das Parent.
    this.parent.callback(passport,reason,msg,datatype,retData);
  }

  /**
   * @see org.kapott.hbci.callback.HBCICallback#status(org.kapott.hbci.passport.HBCIPassport, int, java.lang.Object[])
   */
  @Override
  public void status(HBCIPassport passport, int statusTag, Object[] o)
  {
    this.parent.status(passport,statusTag,o);
  }

  /**
   * @see org.kapott.hbci.callback.HBCICallback#status(org.kapott.hbci.passport.HBCIPassport, int, java.lang.Object)
   */
  @Override
  public void status(HBCIPassport passport, int statusTag, Object o)
  {
    this.parent.status(passport,statusTag,o);
  }

  /**
   * @see org.kapott.hbci.callback.HBCICallback#useThreadedCallback(org.kapott.hbci.passport.HBCIPassport, int, java.lang.String, int, java.lang.StringBuffer)
   */
  @Override
  public boolean useThreadedCallback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData)
  {
    return this.useThreadedCallback(passport,reason,msg,datatype,retData);
  }
}
