/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPL
 *
 **********************************************************************/

package org.kapott.hbci.passport;

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
   * @see org.kapott.hbci.passport.HBCIPassportPinTan#create()
   */
  @Override
  protected void create()
  {
    // Ueberschrieben, um das Erstellen der Passport-Datei zu ueberspringen.
  }
  
  /**
   * @see org.kapott.hbci.passport.HBCIPassportPinTan#read()
   */
  @Override
  protected void read()
  {
    // Ueberschrieben, um das Einlesen der Passport-Datei zu ueberspringen.
  }

  /**
   * @see org.kapott.hbci.passport.HBCIPassportPinTan#saveChanges()
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


