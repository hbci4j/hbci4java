/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPL
 *
 **********************************************************************/

package org.kapott.hbci.GV_Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Response-Klasse fuer den Abruf von Kontoauszuegen.
 */
public class GVRKontoauszug extends HBCIJobResultImpl
{
  /**
   * Enum mit den moeglichen Formaten.
   */
  public enum Format
  {
    /**
     * Datenformat SWIFT MT940.
     */
    MT940("1","sta"),
    
    /**
     * Datenformat ISO-8583.
     */
    ISO8583("2","iso"),
    
    /**
     * Datenformat PDF.
     */
    PDF("3","pdf"),
    
    ;
    
    private String code = null;
    private String ext = null;
    
    /**
     * ct.
     * @param code
     * @param ext
     */
    private Format(String code, String ext)
    {
      this.code = code;
      this.ext = ext;
    }
    
    /**
     * Liefert den Code des Formats.
     * @return der Code des Formats.
     */
    public String getCode()
    {
      return this.code;
    }
    
    /**
     * Liefert die Dateiendung fuer dieses Format.
     * @return die Dateiendung - ohne fuehrenden Punkt.
     */
    public String getExtention()
    {
      return this.ext;
    }
    
    /**
     * Liefert das passende Format oder NULL, wenn es nicht bekannt ist. 
     * @param code der Code des Formats.
     * @return das Format oder NULL, wenn es nicht bekannt ist.
     */
    public static Format find(String code)
    {
      if (code == null || code.length() == 0)
        return null;
      
      for (Format f:Format.values())
      {
        if (f.code.equals(code))
          return f;
      }
      
      return null;
    }
  }
  
  private List<GVRKontoauszugEntry> entries = new ArrayList<GVRKontoauszugEntry>();
  
  /**
   * Liefert die Liste der Kontoauszuege.
   * @return die Liste der Kontoauszuege.
   */
  public List<GVRKontoauszugEntry> getEntries()
  {
    return this.entries;
  }
  
  /**
   * Kapselt einen einzelnen Kontoauszug.
   */
  public static class GVRKontoauszugEntry
  {
    private Format format;
    private byte[] data;

    private Date date;
    private Date startDate;
    private Date endDate;

    private int year;
    private int number;

    private String abschlussInfo;
    private String kundenInfo;
    private String werbetext;

    private String iban;
    private String bic;

    private String name;
    private String name2;
    private String name3;

    private byte[] receipt;

    private String filename;
    
    /**
     * Liefert das Startdatum des Berichtszeitraums.
     * @return das Startdatum des Berichtszeitraums.
     */
    public Date getStartDate()
    {
        return startDate;
    }

    /**
     * Speichert das Startdatum des Berichtszeitraums.
     * @param startDate das Startdatum des Berichtszeitraums.
     */
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    /**
     * Liefert das Enddatum des Berichtszeitraums.
     * @return das Enddatum des Berichtszeitraums.
     */
    public Date getEndDate()
    {
        return endDate;
    }

    /**
     * Speichert das das Enddatum des Berichtszeitraums.
     * @param endDate das Enddatum des Berichtszeitraums.
     */
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    /**
     * Liefert die IBAN.
     * @return die IBAN.
     */
    public String getIBAN()
    {
        return iban;
    }

    /**
     * Speichert die IBAN.
     * @param iban die IBAN.
     */
    public void setIBAN(String iban)
    {
        this.iban = iban;
    }

    /**
     * Speichert die BIC.
     * @return die BIC.
     */
    public String getBIC()
    {
        return bic;
    }

    /**
     * Liefert die BIC.
     * @param bic die BIC.
     */
    public void setBIC(String bic)
    {
        this.bic = bic;
    }

    /**
     * Liefert Auszugsname 1.
     * @return Auszugsname 1.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Speichert Auszugsname 1.
     * @param name Auszugsname 1.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Liefert Auszugsname 2.
     * @return Auszugsname 2.
     */
    public String getName2()
    {
        return name2;
    }

    /**
     * Speichert Auszugsname 2.
     * @param name2 Auszugsname 2.
     */
    public void setName2(String name2)
    {
        this.name2 = name2;
    }

    /**
     * Liefert Auszugsname 3.
     * @return Auszugsname 3.
     */
    public String getName3()
    {
        return name3;
    }

    /**
     * Speichert Auszugsname 3.
     * @param name3 Auszugsname 3.
     */
    public void setName3(String name3)
    {
        this.name3 = name3;
    }

    /**
     * Liefert den Quittungscode.
     * Der kann durchaus binaer sein. Daher Byte-Array.
     * @return der Quittungscode.
     */
    public byte[] getReceipt()
    {
        return receipt;
    }

    /**
     * Speichert den Quittungscode.
     * @param receipt der Quittungscode.
     */
    public void setReceipt(byte[] receipt)
    {
        this.receipt = receipt;
    }

    /**
     * Liefert die Daten.
     * @return die Daten.
     */
    public byte[] getData()
    {
      return this.data;
    }

    /**
     * Speichert die Daten.
     * @param data die Daten.
     */
    public void setData(byte[] data)
    {
      this.data = data;
    }
    
    /**
     * Liefert das Erstelldatum des Kontoauszuges.
     * @return das Erstelldatum des Kontoauszuges.
     */
    public Date getDate()
    {
      return date;
    }

    /**
     * Speichert das Erstelldatum des Kontoauszuges.
     * @param date das Erstelldatum des Kontoauszuges.
     */
    public void setDate(Date date)
    {
      this.date = date;
    }

    /**
     * Liefert das Jahr des Kontoauszuges.
     * @return das Jahr des Kontoauszuges.
     */
    public int getYear()
    {
      return year;
    }

    /**
     * Speichert das Jahr des Kontoauszuges.
     * @param year das Jahr des Kontoauszuges.
     */
    public void setYear(int year)
    {
      this.year = year;
    }

    /**
     * Liefert die fortlaufende Nummer des Kontoauszuges.
     * @return die fortlaufende Nummer des Kontoauszuges.
     */
    public int getNumber()
    {
      return number;
    }

    /**
     * Speichert die fortlaufende Nummer des Kontoauszuges.
     * @param number die fortlaufende Nummer des Kontoauszuges.
     */
    public void setNumber(int number)
    {
      this.number = number;
    }
    
    /**
     * Liefert den Dateinamen. Optional.
     * @return der Dateiname.
     */
    public String getFilename()
    {
      return filename;
    }
    
    /**
     * Speichert den Dateinamen.
     * @param filename der Dateiname.
     */
    public void setFilename(String filename)
    {
      this.filename = filename;
    }
    

    /**
     * Liefert das Format des Kontoauszuges.
     * @return das Format des Kontoauszuges.
     */
    public Format getFormat()
    {
      return format;
    }

    /**
     * Speichert das Format des Kontoauszuges.
     * @param format das Format des Kontoauszuges.
     */
    public void setFormat(Format format)
    {
      this.format = format;
    }

    /**
     * Liefert einen optionalen Text mit Informationen zum Rechnungsabschluss.
     * @return ein optionaler Text mit Informationen zum Rechnungsabschluss.
     */
    public String getAbschlussInfo()
    {
      return abschlussInfo;
    }

    /**
     * Speichert einen optionalen Text mit Informationen zum Rechnungsabschluss.
     * @param abschlussInfo ein optionaler Text mit Informationen zum Rechnungsabschluss.
     */
    public void setAbschlussInfo(String abschlussInfo)
    {
      this.abschlussInfo = abschlussInfo;
    }

    /**
     * Liefert einen optionalen Text mit Informationen zu den Kundenbedingungen.
     * @return ein optionaler Text mit Informationen zu den Kundenbedingungen.
     */
    public String getKundenInfo()
    {
      return kundenInfo;
    }

    /**
     * Speichert einen optionalen Text mit Informationen zu den Kundenbedingungen.
     * @param kundenInfo ein optionaler Text mit Informationen zu den Kundenbedingungen.
     */
    public void setKundenInfo(String kundenInfo)
    {
      this.kundenInfo = kundenInfo;
    }

    /**
     * Liefert optionalen Werbetext.
     * @return optionaler Werbetext.
     */
    public String getWerbetext()
    {
      return werbetext;
    }

    /**
     * Speichert optionalen Werbetext.
     * @param werbetext optionaler Werbetext.
     */
    public void setWerbetext(String werbetext)
    {
      this.werbetext = werbetext;
    }
  }
}
