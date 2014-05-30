
/*  $Id: HBCI4JavaLogOutputStream.java,v 1.1 2011/05/04 22:37:50 willuhn Exp $

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

package org.kapott.hbci.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.kapott.hbci.manager.HBCIUtils;

public class HBCI4JavaLogOutputStream
    extends OutputStream
{
    private ByteArrayOutputStream logdata;
    
    public HBCI4JavaLogOutputStream()
    {
        this.logdata=new ByteArrayOutputStream();
    }
    
    public void write(int b)
    {
        this.logdata.write(b);
    }

    public void write(byte[] b, int off, int len)
    {
        this.logdata.write(b, off, len);
    }

    public void write(byte[] b)
        throws IOException
    {
        this.logdata.write(b);
    }

    public void close()
        throws IOException
    {
        this.logdata.flush();
        this.logdata.close();
    }

    public void flush()
        throws IOException
    {
        if (this.logdata.size()!=0) {
            HBCIUtils.log("socket log: "+this.logdata.toString(Comm.ENCODING), HBCIUtils.LOG_DEBUG2);
        }
        this.logdata.reset();
    }
}
