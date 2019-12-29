/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
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