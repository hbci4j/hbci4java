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

package org.hbci4java;

import java.util.function.BiFunction;

import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIVersion;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.tools.StringUtil;

/**
 * Eine HBCI-Session innerhalb der neuen API.
 */
public class HBCI4JavaSession implements AutoCloseable
{
  private HBCI4JavaClient client = null;
  private HBCIPassport passport = null;
  
  /**
   * ct.
   * @param client der Client.
   * @param passport der Passport.
   */
  HBCI4JavaSession(HBCI4JavaClient client, HBCIPassport passport)
  {
    this.client = client;
    this.passport = passport;
    this.client.getLogger().info("open new session [passport-type: %s]",this.getPassportType());
  }
  
  /**
   * Führt ein oder mehrere Aktionen auf der Session durch und schliesst
   * die Verbindung danach automatisch wieder. Die Session kann anschließend
   * weiterverwendet werden. Es können also beliebig viele execute-Aufrufe durchgeführt werden.
   * @param <T> der Typ der Funktion.
   * @param f die aufzurufende Funktion.
   * @return das Ergebnis der Funktion.
   */
  public <T> T execute(BiFunction<HBCI4JavaSession,HBCIHandler,T> f)
  {
    try (HBCIHandler handler = new HBCIHandler(HBCIVersion.HBCI_300.getId(),this.passport))
    {
      return f.apply(this,handler);
    }
  }
  
  /**
   * Schließt die Session und den internen Passport.
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close() throws Exception
  {
    if (this.passport == null || this.client == null)
      return;
    
    try
    {
      this.client.getLogger().info("closing session [passport-type: %s]",this.getPassportType());
      this.passport.close();
    }
    finally
    {
      this.passport = null;
      
      this.client.discard(this);
      this.client = null;
    }
  }

  
  /**
   * Liefert den Typ des Passports.
   * @return der Typ des Passports.
   */
  private String getPassportType()
  {
    if (this.passport == null)
      return null;
    
    String s = this.passport.getClass().getSimpleName();
    return StringUtil.hasText(s) ? s : this.passport.getClass().getName();
  }
}
