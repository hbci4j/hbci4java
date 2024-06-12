/**********************************************************************
 *
 * Copyright (c) 2024 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci4java.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.protocol.MSG;

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
   * @see org.kapott.hbci.comm.Comm#ping(org.kapott.hbci.protocol.MSG)
   */
  @Override
  protected void ping(MSG msg)
  {
    final String s = msg.toString(0);
    HBCIUtils.log("sending message: " + s,HBCIUtils.LOG_DEBUG2);
  }
  
  /**
   * @see org.kapott.hbci.comm.Comm#pong(org.kapott.hbci.manager.MsgGen)
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
   * @see org.kapott.hbci.comm.Comm#closeConnection()
   */
  @Override
  protected void closeConnection()
  {
  }
}
