
/*  $Id: GVRKontoauszug.java,v 1.1 2011/05/04 22:37:47 willuhn Exp $

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

package org.kapott.hbci.GV_Result;

import java.util.Date;

// TODO: doku fehlt
public class GVRKontoauszug
    extends GVRKUms
{
    private String       format;
    private StringBuffer isodata;
    private StringBuffer pdfdata;
    
    private Date startDate;
    private Date endDate;
    
    private String abschlussInfo;
    private String kundenInfo;
    private String werbetext;
    
    private String iban;
    private String bic;
    
    private String name;
    private String name2;
    private String name3;
    
    private String receipt;
    
    public GVRKontoauszug()
    {
        this.isodata=new StringBuffer();
        this.pdfdata=new StringBuffer();
    }
    
    public void appendISOData(String st)
    {
        this.isodata.append(st);
    }
    
    public void appendPDFData(String st)
    {
        this.pdfdata.append(st);
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public String getAbschlussInfo()
    {
        return abschlussInfo;
    }

    public void setAbschlussInfo(String abschlussInfo)
    {
        this.abschlussInfo = abschlussInfo;
    }

    public String getKundenInfo()
    {
        return kundenInfo;
    }

    public void setKundenInfo(String kundenInfo)
    {
        this.kundenInfo = kundenInfo;
    }

    public String getWerbetext()
    {
        return werbetext;
    }

    public void setWerbetext(String werbetext)
    {
        this.werbetext = werbetext;
    }

    public String getIBAN()
    {
        return iban;
    }

    public void setIBAN(String iban)
    {
        this.iban = iban;
    }

    public String getBIC()
    {
        return bic;
    }

    public void setBIC(String bic)
    {
        this.bic = bic;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName2()
    {
        return name2;
    }

    public void setName2(String name2)
    {
        this.name2 = name2;
    }

    public String getName3()
    {
        return name3;
    }

    public void setName3(String name3)
    {
        this.name3 = name3;
    }

    public String getReceipt()
    {
        return receipt;
    }

    public void setReceipt(String receipt)
    {
        this.receipt = receipt;
    }

    public StringBuffer getISOdata()
    {
        return isodata;
    }

    public StringBuffer getPDFdata()
    {
        return pdfdata;
    }
    
    // TODO: toString() fehlt    
}
