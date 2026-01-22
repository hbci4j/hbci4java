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

package org.hbci4java.hbci.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.hbci4java.hbci.manager.HBCIUtils;

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
