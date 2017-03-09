
/*  $Id: DateField.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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

package org.kapott.hbci.passport.rdhXfile;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.kapott.hbci.exceptions.HBCI_Exception;


public class DateField
    extends TLV
{
    private Date date;
    
    public DateField()
    {
        super(0x4452);
    }
    
    public DateField(TLV tlv)
    {
        super(tlv);
        
        try {
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
            this.date=format.parse(new String(this.getData()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Date getDate()
    {
        return this.date;
    }
    
    public void setDate(Date date)
    {
        this.date=date;
    }
    
    public void updateData()
    {
        try {
            ByteArrayOutputStream os=new ByteArrayOutputStream();

            SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
            String           date_st=format.format(getDate());
            os.write(date_st.getBytes());
            
            setData(os.toByteArray());
        } catch (Exception e) {
            throw new HBCI_Exception(e);
        }
    }
    
    public String toString()
    {
        return this.date.toString();
    }
}