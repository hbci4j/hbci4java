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

package org.hbci4java.hbci.passport;

/**
 * Implementierung eines PIN/TAN-Passport, welcher keine Daten im Dateisystem ablegt
 * sondern alle Daten im Speicher haelt.
 */
public class HBCIPassportPinTanMemory extends HBCIPassportPinTan
{

  /**
   * ct.
   * @param init Generische Init-Daten.
   */
  public HBCIPassportPinTanMemory(Object init)
  {
    super(init);
  }

  /**
   * @see org.hbci4java.hbci.passport.HBCIPassportPinTan#create()
   */
  @Override
  protected void create()
  {
    // Ueberschrieben, um das Erstellen der Passport-Datei zu ueberspringen.
  }
  
  /**
   * @see org.hbci4java.hbci.passport.HBCIPassportPinTan#read()
   */
  @Override
  protected void read()
  {
    // Ueberschrieben, um das Einlesen der Passport-Datei zu ueberspringen.
  }

  /**
   * @see org.hbci4java.hbci.passport.HBCIPassportPinTan#saveChanges()
   */
  @Override
  public void saveChanges()
  {
    // Ueberschrieben, um das Schreiben zu ueberspringen.
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return this.getFileName();
  }
  
}


