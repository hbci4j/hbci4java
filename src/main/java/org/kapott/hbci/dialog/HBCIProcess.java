/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.dialog;

import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Kapselt mehrere HBCI-Nachrichten zu einem kompletten Dialog.
 * Eigentlich muesste das Interface "RawHBCIDialog" "RawHBCIMessage" heissen, weil es nur
 * eine einzelne Nachricht kapselt und keinen kompletten Dialog. Ich lass das aber jetzt mal so.
 */
public interface HBCIProcess
{
  /**
   * Fuehrt die Dialoge mit der Bank aus.
   * @param ctx der Dialog-Context.
   * @return der Ausfuehrungsstatus.
   */
  public HBCIMsgStatus execute(final DialogContext ctx);

}


