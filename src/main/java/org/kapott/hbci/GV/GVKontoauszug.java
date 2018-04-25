
/*  $Id: GVKontoauszug.java,v 1.1 2011/05/04 22:37:54 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

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
import org.kapott.hbci.swift.Swift;

/**
 * Implementierung des Geschaeftsvorfalls fuer den elektronischen Kontoauszug (HKEKA)
 */
public class GVKontoauszug extends HBCIJobImpl
{
    /**
     * Liefert den Lowlevel-Namen.
     * @return der Lowlevel-Name.
     */
    public static String getLowlevelName()
    {
        return "Kontoauszug";
    }
    
    /**
     * ct.
     * @param handler
     * @param name
     */
    public GVKontoauszug(HBCIHandler handler,String name)
    {
        super(handler, name, new GVRKontoauszug());
    }

    /**
     * ct.
     * @param handler
     */
    public GVKontoauszug(HBCIHandler handler)
    {
        this(handler,getLowlevelName());

        
        boolean sepa = false;
        try
        {
          sepa = Integer.parseInt(this.getSegVersion()) >= 4; 
        }
        catch (Exception e)
        {
          HBCIUtils.log(e);
        }
        
        boolean nat = this.canNationalAcc(handler);

        if (sepa)
        {
          addConstraint("my.bic",  "My.bic",  null, LogFilter.FILTER_MOST);
          addConstraint("my.iban", "My.iban", null, LogFilter.FILTER_IDS);
        }

        if (nat || !sepa)
        {
          addConstraint("my.country",  "My.KIK.country", "DE", LogFilter.FILTER_NONE);
          addConstraint("my.blz",      "My.KIK.blz",     "", LogFilter.FILTER_MOST);
          addConstraint("my.number",   "My.number",      "", LogFilter.FILTER_IDS);
          addConstraint("my.subnumber","My.subnumber",   "", LogFilter.FILTER_MOST);
        }

        addConstraint("format", "format", "", LogFilter.FILTER_NONE);
        addConstraint("idx", "idx", "", LogFilter.FILTER_NONE);
        addConstraint("year", "year", "", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
        addConstraint( "offset", "offset", "", LogFilter.FILTER_NONE );
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result   = msgstatus.getData();
        GVRKontoauszug list = (GVRKontoauszug) jobResult;
        
        GVRKontoauszugEntry auszug = new GVRKontoauszugEntry();
        list.getEntries().add(auszug);
        
        Format format = Format.find(result.getProperty(header+".format"));
        auszug.setFormat(format);
        
        String data = result.getProperty(header+".booked");
        
        if (data != null && data.length() > 0)
        {
          if (format != null && format == Format.MT940)
            data = Swift.decodeUmlauts(data);

          try
          {
            auszug.setData(data.getBytes(Comm.ENCODING));
          }
          catch (UnsupportedEncodingException e)
          {
            HBCIUtils.log(e,HBCIUtils.LOG_WARN);
            
            // Wir versuchen es als Fallback ohne explizites Encoding
            auszug.setData(data.getBytes());
          }
        }

        String date = result.getProperty(header+".date");
        if (date != null && date.length() > 0)
          auszug.setDate(HBCIUtils.string2DateISO(date));
        
        String year   = result.getProperty(header+".year");
        String number = result.getProperty(header+".number");
        if (year != null && year.length() > 0)
          auszug.setYear(Integer.parseInt(year));
        if (number != null && number.length() > 0)
          auszug.setNumber(Integer.parseInt(number));

        auszug.setStartDate(HBCIUtils.string2DateISO(result.getProperty(header+".TimeRange.startdate")));
        auszug.setEndDate(HBCIUtils.string2DateISO(result.getProperty(header+".TimeRange.enddate")));
        auszug.setAbschlussInfo(result.getProperty(header+".abschlussinfo"));
        auszug.setKundenInfo(result.getProperty(header+".kondinfo"));
        auszug.setWerbetext(result.getProperty(header+".ads"));
        auszug.setIBAN(result.getProperty(header+".iban"));
        auszug.setBIC(result.getProperty(header+".bic"));
        auszug.setName(result.getProperty(header+".name"));
        auszug.setName2(result.getProperty(header+".name2"));
        auszug.setName3(result.getProperty(header+".name3"));
        
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
