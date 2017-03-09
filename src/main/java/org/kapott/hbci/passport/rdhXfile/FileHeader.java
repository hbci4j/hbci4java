
/*  $Id: FileHeader.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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
import java.util.Random;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;


public class FileHeader
    extends TLV
{
    private int    profileversion;
    private int    version;
    private byte[] salt;
    private long   nof_iterations;
    
    public FileHeader()
    {
        super(0x4e56);
    }
    
    public FileHeader(TLV tlv)
    {
        super(tlv);
        byte[] data=this.getData();
        
        int offset;
        if (getLength()==26) {
            // RDH-2
            this.profileversion=2;
            offset=0;
        } else {
            // RDH-10
            this.profileversion=(data[1]<<8) | (data[0]&0xFF);
            offset=2;
        }
        
        this.version=(data[offset+1]<<8) | (data[offset+0]&0xFF);
        this.nof_iterations=((data[offset+25]&0xFFL)<<24) | ((data[offset+24]&0xFFL)<<16) | 
                             ((data[offset+23]&0xFFL)<<8) | ((data[offset+22]&0xFFL)<<0);
        
        this.salt=new byte[20];
        System.arraycopy(data,offset+2, this.salt,0, 20);
        HBCIUtils.log(
            "file is a RDH-"+this.profileversion+"-file in version "+this.version,
            HBCIUtils.LOG_DEBUG);
    }
    
    public int getProfileVersion()
    {
        return this.profileversion;
    }
    
    public void setProfileVersion(int pversion)
    {
        this.profileversion=pversion;
    }
    
    public int getVersion()
    {
        return this.version;
    }
    
    public void setVersion(int version)
    {
        this.version=version;
    }
    
    public byte[] getSalt()
    {
        return this.salt;
    }
    
    public void setSalt(byte[] salt)
    {
        this.salt=salt;
    }
    
    public void setRandomSalt()
    {
        byte[] s=new byte[20];
        Random r=new Random();
        r.nextBytes(s);
        setSalt(s);
    }
    
    public long getNofIterations()
    {
        return this.nof_iterations;
    }
    
    public void setNofIterations(int nof_iterations)
    {
        this.nof_iterations=nof_iterations;
    }
    
    public void updateData()
    {
        try {
            ByteArrayOutputStream os=new ByteArrayOutputStream();

            if (getProfileVersion()==10) {
                // RDH-10: write profile-version
                os.write(int2ba(getProfileVersion()));
            }
            os.write(int2ba(getVersion()));
            os.write(getSalt());
            os.write(long2ba(getNofIterations()));
            
            setData(os.toByteArray());
        } catch (Exception e) {
            throw new HBCI_Exception(e);
        }
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        ret.append("diskhead: profileversion="+this.profileversion);
        ret.append("; version="+this.version);
        ret.append("; nof_iterations="+this.nof_iterations);
        ret.append("; salt=");
        
        for (int i=0;i<this.salt.length;i++) {
            int x=salt[i]&0xFF;
            if (x<0) {
                x+=256;
            }
            ret.append(Integer.toString(x,16)+" ");
        }
        
        return ret.toString();
    }
}