/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPL
 *
 **********************************************************************/

package org.kapott.hbci.GV_Result;

import java.util.Date;

/**
 * Response-Klasse fuer den Abruf von Kontoauszuegen im PDF-Format.
 */
public class GVRKontoauszugPdf extends GVRKUms
{
    private byte[] pdfdata;
    
    private Date date;
    private Date startDate;
    private Date endDate;
    
    private int year;
    private int number;
        
    private String iban;
    private String bic;
    
    private String name;
    private String name2;
    private String name3;
    private String filename;
    
    private String receipt;
    
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
     * @return der Quittungscode.
     */
    public String getReceipt()
    {
        return receipt;
    }

    /**
     * Speichert den Quittungscode.
     * @param receipt der Quittungscode.
     */
    public void setReceipt(String receipt)
    {
        this.receipt = receipt;
    }
    
    /**
     * Speichert die PDF-Daten.
     * @param pdf die PDF-Daten.
     */
    public void setPDFData(byte[] pdf)
    {
      this.pdfdata = pdf;
    }
    
    /**
     * Liefert die PDF-Daten.
     * @return die PDF_Daten.
     */
    public byte[] getPDFData()
    {
      return this.pdfdata;
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
    
}
