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

package org.kapott.hbci.GV_Result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Kapselt die Antwort-Daten des VoP.
 */
public class GVRVoP extends HBCIJobResultImpl
{
  private VoPResult result = null;
  
  /**
   * Liefert das Ergebnis der Prüfung.
   * @return das Ergebnis der Prüfung.
   */
  public VoPResult getResult()
  {
    return this.result;
  }
  
  /**
   * Speichert das Ergebnis der Prüfung.
   * @param result das Ergebnis der Prüfung.
   */
  public void setResult(VoPResult result)
  {
    this.result = result;
  }
  
  /**
   * Das Gesamt-Ergebnis der Prüfung.
   */
  public static class VoPResult
  {
    private String text = null;
    private List<VoPResultItem> items = new ArrayList<>();
    
    /**
     * Liefert den von der Bank gemeldeten Info-Text.
     * @return der von der Bank gemeldete Info-Text.
     */
    public String getText()
    {
      return text;
    }
    
    /**
     * Speichert den von der Bank gemeldeten Info-Text.
     * @param text der von der Bank gemeldete Info-Text. 
     */
    public void setText(String text)
    {
      this.text = text;
    }
    
    /**
     * Liefert die Ergebnis-Liste.
     * @return items die Ergebnis-Liste.
     */
    public List<VoPResultItem> getItems()
    {
      return items;
    }
  }

  
  /**
   * Prüfergebnis einer einzelnen VOP-Prüfung.
   */
  public static class VoPResultItem
  {
    private VoPStatus status;
    private String original;
    private String name;
    private String iban;
    private String usage;
    private BigDecimal amount;
    
    private String text;
    
    /**
     * Liefert den Status.
     * @return der Status.
     */
    public VoPStatus getStatus()
    {
      return status;
    }
    
    /**
     * Speichert den Status.
     * @param status der Status.
     */
    public void setStatus(VoPStatus status)
    {
      this.status = status;
    }
    
    /**
     * Liefert den im Auftrag angegebenen Namen.
     * @return der im Auftrag angegebene Name.
     */
    public String getOriginal()
    {
      return original;
    }
    
    /**
     * Speichert den im Auftrag angegebenen Namen.
     * @param original der im Auftrag angegebene Name.
     */
    public void setOriginal(String original)
    {
      this.original = original;
    }
    
    /**
     * Liefert den korrigierten Namen, falls vorhanden.
     * @return der korrigierte Name, falls vorhanden.
     */
    public String getName()
    {
      return name;
    }
    
    /**
     * Speichert den korrigierten Namen.
     * @param name der korrigierte Name.
     */
    public void setName(String name)
    {
      this.name = name;
    }
    
    /**
     * Liefert die IBAN.
     * @return die IBAN.
     */
    public String getIban()
    {
      return iban;
    }
    
    /**
     * Speichert die IBAN.
     * @param iban die IBAN.
     */
    public void setIban(String iban)
    {
      this.iban = iban;
    }
    
    /**
     * Ein optionaler erläuternder Hinweis-Text - u.a. nötig bei Status "Not applicable".
     * @return der Hinweis-Text.
     */
    public String getText()
    {
      return text;
    }
    
    /**
     * Ein optionaler erläuternder Hinweis-Text.
     * @param text der Hinweis-Text.
     */
    public void setText(String text)
    {
      this.text = text;
    }
    
    /**
     * Liefert den Betrag.
     * @return der Betrag.
     */
    public BigDecimal getAmount()
    {
      return amount;
    }
    
    /**
     * Speichert den Betrag.
     * @param amount der Betrag.
     */
    public void setAmount(BigDecimal amount)
    {
      this.amount = amount;
    }
    
    /**
     * Liefert den Verwendungszweck.
     * @return der Verwendungszweck.
     */
    public String getUsage()
    {
      return usage;
    }
    
    /**
     * Speichert den Verwendungszweck.
     * @param usage der Verwendungszweck.
     */
    public void setUsage(String usage)
    {
      this.usage = usage;
    }
  }

  /**
   * Enum mit den möglichen Rückgabe-Status.
   */
  public static enum VoPStatus
  {
    /**
     * Übereinstimmung.
     */
    MATCH("RCVC","Übereinstimmung"),
    
    /**
     * Keine Übereinstimmung.
     */
    NO_MATCH("RVNM","Keine Übereinstimmung"),
    
    /**
     * Beinahe Übereinstimmung.
     */
    CLOSE_MATCH("RVMC","Beinahe Übereinstimmung"),
    
    /**
     * Nicht anwendbar.
     */
    NOT_APPLICABLE("RVNA","Nicht anwendbar"),
    
    /**
     * Wartet auf Rückmeldung.
     */
    PENDING("PDNG","Wartet auf Rückmeldung"),
    
    ;
    
    private String code;
    private String description;
    
    /**
     * ct.
     * @param code der HBCI-Code.
     * @param description der Beschreibungstext.
     */
    private VoPStatus(String code, String description)
    {
      this.code = code;
      this.description = description;
    }
    
    /**
     * Liefert den HBCI-Code.
     * @return code der HBCI-Code.
     */
    public String getCode()
    {
      return code;
    }
    
    /**
     * Liefert die Beschreibung.
     * @return description die Beschreibung.
     */
    public String getDescription()
    {
      return description;
    }
    
    /**
     * Versucht den Enum-Wert basierend auf dem Code zu ermitteln.
     * @param code der Text mit dem Code.
     * @return die Enum oder NULL, wenn sie nicht ermittelbar ist.
     */
    public static VoPStatus byCode(String code)
    {
      if (code == null || code.isBlank())
        return null;

      for (VoPStatus s:values())
      {
        if (code.equalsIgnoreCase(s.code))
          return s;
      }
      
      return null;
    }

    /**
     * Versucht den Enum-Wert basierend auf dem Namen zu ermitteln.
     * @param name der Text mit dem Namen.
     * @return die Enum oder NULL, wenn sie nicht ermittelbar ist.
     */
    public static VoPStatus byName(String name)
    {
      if (name == null || name.isBlank())
        return null;

      for (VoPStatus s:values())
      {
        if (name.equalsIgnoreCase(s.name()))
          return s;
      }
      
      return null;
    }
  }
}
