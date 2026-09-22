/**********************************************************************
 *
 * Copyright (c) 2026 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.hbci4java;

import java.io.File;

import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;

/**
 * Die Konfiguration des Bankzugangs.
 */
public class HBCI4JavaBankAccount
{
  private HBCIPassport passport;
  
  /**
   * ct.
   * @param type der Typ.
   * @param file die Datei.
   */
  public HBCI4JavaBankAccount(Type type, File file)
  {
    this.passport = AbstractHBCIPassport.getInstance(type.getName(),file);
  }
  
  /**
   * ct.
   * Der Konstruktor existiert für den Fall, dass Eigen-Implementierungen des Passport verwendet werden.
   * @param passport der Passport.
   */
  public HBCI4JavaBankAccount(HBCIPassport passport)
  {
    this.passport = passport;
  }
  
  /**
   * Liefert den Passport. 
   * @return der Passport.
   */
  HBCIPassport getPassport()
  {
    return passport;
  }
  
  /**
   * Die Art des Bankzugangs.
   */
  public static class Type
  {
    /**
     * Zugangsart "PIN/TAN". Incl. PushTAN mit Direktfreigabe und ChipTAN/ChipTAN USB.
     */
    public final static Type PINTAN = new Type("PinTan");
    
    /**
     * Zugangsart "Chipkarte". NICHT ChipTAN/ChipTAN USB sondern ausschließlich dedizierte DDV-basierte HBCI-Chipkarten.
     */
    public final static Type CHIPCARD = new Type("DDVPCSC");
    
    /**
     * Zugangsart "Schlüsseldatei" mit den HBCI4Java-eigenen Schlüsseldateien.
     */
    public final static Type KEYFILE = new Type("RDHNew");
    
    /**
     * Zugangsart "PIN/TAN". Incl. PushTAN mit Direktfreigabe und ChipTAN/ChipTAN USB.
     */
    public final static Type DEFAULT = PINTAN;
    
    
    private String name = null;
    
    /**
     * ct.
     * @param name der Typ der Zugangsart.
     */
    private Type(String name)
    {
      this.name = name;
    }

    /**
     * Liefert den Namen. 
     * @return der Name.
     */
    public String getName()
    {
      return name;
    }
    
    /**
     * Erzeugt einen benutzerspezifischen Typ.
     * @param type der benutzerspezifische Typ.
     * @return die Zugangsart.
     */
    public static Type custom(String type)
    {
      return new Type(type);
    }
  }
}
