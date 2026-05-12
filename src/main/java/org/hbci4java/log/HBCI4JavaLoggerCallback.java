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

package org.hbci4java.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Objects;

import org.hbci4java.HBCI4JavaClient;
import org.hbci4java.HBCI4JavaConfig;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.tools.StringUtil;

/**
 * Logger für HBCI4Java, der über den Callback-Mechanismus loggt.
 */
public class HBCI4JavaLoggerCallback implements HBCI4JavaLogger
{
  private HBCI4JavaClient client = null;
  
  /**
   * ct.
   * @param client der Client.
   */
  public HBCI4JavaLoggerCallback(HBCI4JavaClient client)
  {
    this.client = client;
  }
  
  /**
   * @see org.hbci4java.log.HBCI4JavaLogger#debug(java.lang.String, java.lang.Object[])
   */
  public void debug(String msg, Object... params)
  {
    this.log(msg,Level.DEBUG.getLevel(),null,params);
  }

  /**
   * @see org.hbci4java.log.HBCI4JavaLogger#info(java.lang.String, java.lang.Object[])
   */
  public void info(String msg, Object... params)
  {
    this.log(msg,Level.INFO.getLevel(),null,params);
  }
  
  /**
   * @see org.hbci4java.log.HBCI4JavaLogger#warn(java.lang.String, java.lang.Object[])
   */
  public void warn(String msg, Object... params)
  {
    this.log(msg,Level.WARN.getLevel(),null,params);
  }
  
  /**
   * @see org.hbci4java.log.HBCI4JavaLogger#error(java.lang.String, java.lang.Throwable, java.lang.Object[])
   */
  public void error(String msg, Throwable t, Object... params)
  {
    this.log(msg,Level.ERROR.getLevel(),t,params);
  }
  
  /**
   * @see org.hbci4java.log.HBCI4JavaLogger#log(java.lang.String, int, java.lang.Throwable, java.lang.Object[])
   */
  public void log(String msg, int l, Throwable t, Object... params)
  {
    final HBCI4JavaConfig conf = this.client.getConfig();
    final Level level = Level.find(conf.getInteger("log.loglevel.default",Level.INFO.getLevel()));
    if (!level.log(l))
      return;
    
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw);
    
    if (StringUtil.hasText(msg))
    {
      if (params != null && params.length > 0)
        msg = String.format(msg,params);
      
      pw.print(msg);
    }

    if (t != null)
      t.printStackTrace(pw);

    msg = sw.toString();
    
    final Integer fl = conf.getInteger("log.filter",2);
    if (fl != null && fl != 0)
      msg = LogFilter.getInstance().filterLine(msg, fl);

    final StackTraceElement trace = this.getTrace();
    this.client.getCallback().log(msg, level.getLevel(), new Date(), trace);
  }

  /**
   * Liefert das Stacktrace-Element.
   * @return das Stacktrace-Element.
   */
  private StackTraceElement getTrace()
  {
    final Exception e = new Exception();
    for (StackTraceElement s:e.getStackTrace())
    {
      final String c = s.getClassName();
      final String m = s.getMethodName();
      
      if (Objects.equals(c,this.getClass().getName()))
        continue;
      
      if (Objects.equals(c,HBCIUtils.class.getName()) && Objects.equals(m,"log"))
        continue;
      
      return s;
    }
    
    return null;
  }
  
}


