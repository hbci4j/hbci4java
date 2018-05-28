/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPL
 *
 **********************************************************************/

package org.kapott.hbci.GV;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRKontoauszug;
import org.kapott.hbci.GV_Result.GVRKontoauszug.Format;
import org.kapott.hbci.GV_Result.GVRKontoauszug.GVRKontoauszugEntry;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Implementierung des Geschaeftsvorfalls fuer den elektronischen Kontoauszug
 * (HKEKP) im PDF-Format
 */
public class GVKontoauszugPdf extends HBCIJobImpl
{
  /**
   * Liefert den Lowlevel-Namen des Auftrags.
   * @return der Lowlevel-Name des Auftrags.
   */
  public static String getLowlevelName()
  {
    return "KontoauszugPdf";
  }

  /**
   * ct.
   * @param handler
   * @param name
   */
  public GVKontoauszugPdf(HBCIHandler handler, String name)
  {
    super(handler, name, new GVRKontoauszug());
  }

  /**
   * ct.
   * @param handler
   */
  public GVKontoauszugPdf(HBCIHandler handler)
  {
    this(handler, getLowlevelName());

    addConstraint("my.bic",  "My.bic",  null, LogFilter.FILTER_MOST);
    addConstraint("my.iban", "My.iban", null, LogFilter.FILTER_IDS);

    if (this.canNationalAcc(handler))
    {
      addConstraint("my.country",  "My.KIK.country", "DE", LogFilter.FILTER_NONE);
      addConstraint("my.blz",      "My.KIK.blz",     "", LogFilter.FILTER_MOST);
      addConstraint("my.number",   "My.number",      "", LogFilter.FILTER_IDS);
      addConstraint("my.subnumber","My.subnumber",   "", LogFilter.FILTER_MOST);
    }

    addConstraint("idx", "idx", "", LogFilter.FILTER_NONE);
    addConstraint("year", "year", "", LogFilter.FILTER_NONE);
    addConstraint("maxentries", "maxentries", "", LogFilter.FILTER_NONE);
    addConstraint( "offset", "offset", "", LogFilter.FILTER_NONE );
  }

  /**
   * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus,java.lang.String, int)
   */
  protected void extractResults(HBCIMsgStatus msgstatus, String header, int idx)
  {
    Properties result   = msgstatus.getData();
    GVRKontoauszug list = (GVRKontoauszug) jobResult;
    
    GVRKontoauszugEntry auszug = new GVRKontoauszugEntry();
    list.getEntries().add(auszug);
    
    // Das Format setzen wir hier pauschal auf PDF, weil HKEKP immer PDF liefert
    auszug.setFormat(Format.PDF);

    ////////////////////////////////////////////////////////////////////////
    // Die folgenden Parameter existieren in Segment-Version 1 noch
    // nicht - daher die Null-Checks. Auch dann, wenn die Properties
    // in Segment-Version u.U. Pflicht sind.

    String start  = result.getProperty(header + ".TimeRange.startdate");
    String end    = result.getProperty(header + ".TimeRange.enddate");
    String date   = result.getProperty(header + ".date");
    String year   = result.getProperty(header + ".year");
    String number = result.getProperty(header + ".number");

    if (start != null && start.length() > 0)
      auszug.setStartDate(HBCIUtils.string2DateISO(start));

    if (end != null && end.length() > 0)
      auszug.setEndDate(HBCIUtils.string2DateISO(end));

    if (date != null && date.length() > 0)
      auszug.setDate(HBCIUtils.string2DateISO(date));

    if (year != null && year.length() > 0)
      auszug.setYear(Integer.parseInt(year));

    if (number != null && number.length() > 0)
      auszug.setNumber(Integer.parseInt(number));

    // Wenn hier NULL drin steht, ist es nicht weiter schlimm
    auszug.setIBAN(result.getProperty(header + ".iban"));
    auszug.setBIC(result.getProperty(header + ".bic"));
    auszug.setName(result.getProperty(header + ".name"));
    auszug.setName2(result.getProperty(header + ".name2"));
    auszug.setName3(result.getProperty(header + ".name3"));
    auszug.setFilename(result.getProperty(header + ".filename"));
    //
    ////////////////////////////////////////////////////////////////////////

    // Den Rest gibts auch in Segment-Version 1
    
    // In Segment-Version sind die PDF-Daten Base64-codiert, obwohl sie als
    // Typ "bin" angegeben sind. Das ist ein Fehler in der Spec. In Segment-
    // Version 2 wurde das korrigiert. Allerdings gibts es jetzt einen BPD-
    // Parameter anhand dem erkannt werden kann, ob es Base64-codiert ist
    // oder nicht. Das sind mir zuviele Variablen. Zumal mich nicht wundern
    // wuerde, wenn es Banken gibt, die in den BPD nicht reinschreiben, dass
    // sie Base64 senden und es dann trotzdem tun. Also checken wir einfach
    // selbst. Wenn "data" nur ASCII-Zeichen enthaelt, kann es nur Base64
    // sein. Ansonsten ist es binaer.
    String data = result.getProperty(header + ".booked");
    
    if (data != null && data.length() > 0)
    {
      if (data.startsWith("%PDF-"))
      {
        // Ist Bin
        try
        {
          auszug.setData(data.getBytes(Comm.ENCODING));
        }
        catch (UnsupportedEncodingException e)
        {
          // Kann eigentlich nicht passieren
          HBCIUtils.log(e,HBCIUtils.LOG_WARN);
        }
      }
      else
      {
        // Ist Base64
        auszug.setData(HBCIUtils.decodeBase64(data));
      }
    }
    
    String receipt = result.getProperty(header+".receipt");
    if (receipt != null)
    {
      try
      {
        auszug.setReceipt(receipt.getBytes(Comm.ENCODING));
      }
      catch (UnsupportedEncodingException e)
      {
        HBCIUtils.log(e,HBCIUtils.LOG_WARN);
        
        // Wir versuchen es als Fallback ohne explizites Encoding
        auszug.setReceipt(receipt.getBytes());
      }
    }
  }

  /**
   * @see org.kapott.hbci.GV.HBCIJobImpl#verifyConstraints()
   */
  public void verifyConstraints()
  {
    super.verifyConstraints();
    checkAccountCRC("my");
  }
}
