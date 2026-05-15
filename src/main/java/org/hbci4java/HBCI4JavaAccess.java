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

/**
 * Die Konfiguration des Bankzugangs.
 */
public class HBCI4JavaAccess
{
  private File file;
  private Type type;
  
  /**
   * ct.
   */
  public HBCI4JavaAccess()
  {
  }
  
  /**
   * ct.
   * @param type der Typ.
   * @param file die Datei.
   */
  public HBCI4JavaAccess(Type type, File file)
  {
    this.type = type;
    this.file = file;
  }
  
  /**
   * Liefert den Typ des Bankzugangs. 
   * @return der Typ des Bankzugangs.
   */
  public Type getType()
  {
    return type;
  }
  
  /**
   * Speichert den Typ des Bankzugangs.
   * @param type der Typ des Bankzugangs.
   */
  public void setType(Type type)
  {
    this.type = type;
  }
  
  /**
   * Liefert die Datei mit den Daten des Bankzugangs. 
   * @return die Datei mit den Daten des Bankzugangs.
   */
  public File getFile()
  {
    return file;
  }
  
  /**
   * Speichert die Datei mit den Daten des Bankzugangs. 
   * @param file die Datei mit den Daten des Bankzugangs.
   */
  public void setFile(File file)
  {
    this.file = file;
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
