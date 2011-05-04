
/*  $Id: TLV.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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

public class TLV
{
    private int    tag;
    private int    len;
    private byte[] data;
    private byte[] rawdata;
    
    public TLV(byte[] buffer,int offset)
    {
        this.tag=((buffer[offset+1])<<8) | (buffer[offset+0]&0xFF);
        this.len=((buffer[offset+3])<<8) | (buffer[offset+2]&0xFF);
        
        this.data=new byte[this.len];
        System.arraycopy(buffer,offset+4, this.data,0, this.len);
        
        this.rawdata=new byte[4+this.len];
        System.arraycopy(buffer,offset, this.rawdata,0, 4+this.len);
    }
    
    protected TLV(TLV tlv)
    {
        this.tag=(int)(tlv.getTag()&0xFFFFL);
        this.len=tlv.getLength();
        this.data=tlv.getData();
        this.rawdata=tlv.getRawData();
    }
    
    public TLV(int tag)
    {
        this.tag=tag;
    }
    
    public long getTag()
    {
        return this.tag&0xFFFFL;
    }
    
    public int getLength()
    {
        return this.len;
    }
    
    public void setData(byte[] data)
    {
    	this.data=data;
    	this.len=data.length;
    	
    	this.rawdata=new byte[2+2+this.len];
    	this.rawdata[0]=(byte)((this.tag>>0)&0xFF);
    	this.rawdata[1]=(byte)((this.tag>>8)&0xFF);
    	this.rawdata[2]=(byte)((this.len>>0)&0xFF);
    	this.rawdata[3]=(byte)((this.len>>8)&0xFF);
    	System.arraycopy(this.data,0, this.rawdata,4, this.len);
    }
    
    public byte[] getData()
    {
        return this.data;
    }
    
    public byte[] getRawData()
    {
        return this.rawdata;
    }
    
    public void updateData()
    {
    }

    protected String expand(String st, int size)
    {
        StringBuffer ret;
        if (st!=null) {
            ret=new StringBuffer(st);
        } else {
            ret=new StringBuffer();
        }
        for (int i=ret.length();i<size;i++)
            ret.append(' ');
        return ret.toString();
    }
    
    protected byte[] int2ba(int x)
    {
        return new byte[] {(byte)(x&0xFF), (byte)((x>>8)&0xFF)};
    }
    
    protected byte[] long2ba(long x)
    {
        return new byte[] {(byte)(x&0xFF), (byte)((x>>8)&0xFF), 
                           (byte)((x>>16)&0xFF), (byte)((x>>24)&0xFF)};
    }
    
    protected byte[] reverseba(byte[] ba)
    {
        int    l=ba.length;
        byte[] ret=new byte[l];
        for (int i=0;i<l;i++) {
            ret[i] = ba[l-i-1];
        }
        return ret;
    }
    
    protected byte[] trimba(byte[] ba)
    {
        int posi=0;
        int l=ba.length;
        while (posi<ba.length && ba[posi]==0x00) {
            posi++;
        }
        byte[] newba=new byte[l-posi];
        System.arraycopy(ba,posi, newba,0, l-posi);
        return newba;
    }
    
}