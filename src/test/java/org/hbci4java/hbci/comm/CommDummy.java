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

package org.hbci4java.hbci.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.hbci4java.hbci.comm.Comm;
import org.hbci4java.hbci.exceptions.HBCI_Exception;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.MsgGen;
import org.hbci4java.hbci.passport.HBCIPassportInternal;
import org.hbci4java.hbci.protocol.MSG;

/**
 * Ermöglicht Dummy-Kommunikation.
 */
public final class CommDummy extends Comm
{
  private List<String> responses = new ArrayList();
  private AtomicInteger pos = new AtomicInteger(0);
  
  /**
   * ct.
   * @param parentPassport
   */
  public CommDummy(HBCIPassportInternal parentPassport)
  {
    super(parentPassport);
  }
  
  /**
   * Fügt ein neues Response hinzu.
   * @param s das Response.
   */
  public void addResponse(String s)
  {
    this.responses.add(s);
  }

  /**
   * @see org.hbci4java.hbci.comm.Comm#ping(org.hbci4java.hbci.protocol.MSG)
   */
  @Override
  protected void ping(MSG msg)
  {
    final String s = msg.toString(0);
    HBCIUtils.log("sending message: " + s,HBCIUtils.LOG_DEBUG2);
  }
  
  /**
   * @see org.hbci4java.hbci.comm.Comm#pong(org.hbci4java.hbci.manager.MsgGen)
   */
  @Override
  protected synchronized StringBuffer pong(MsgGen gen)
  {
    final int i = this.pos.getAndIncrement();
    if (i+1 > this.responses.size())
    {
      HBCI_Exception e = new HBCI_Exception("have no more predefined responses [idx: " + i + "]");
      e.setFatal(true);
      throw e;
    }

    final String s = this.responses.get(i);
    HBCIUtils.log("dummy response " + i + ": " + s,HBCIUtils.LOG_DEBUG2);
    return new StringBuffer(s);
  }

  /**
   * @see org.hbci4java.hbci.comm.Comm#closeConnection()
   */
  @Override
  protected void closeConnection()
  {
  }
}
