
/*  $Id: LoggingInputStream.java,v 1.1 2011/05/04 22:37:50 willuhn Exp $

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LoggingInputStream
    extends InputStream
{
    private InputStream  targetInputStream;
    private OutputStream logger;
    
    public LoggingInputStream(InputStream target, OutputStream logger)
    {
        this.targetInputStream=target;
        this.logger=logger;
    }
    
    public int read() 
        throws IOException
    {
        int c=targetInputStream.read();
        logger.write(c);
        return c;
    }

    public void close()
        throws IOException
    {
        logger.flush();
        targetInputStream.close();
    }

    public int available()
        throws IOException
    {
        return targetInputStream.available();
    }

    public int read(byte[] b, int off, int len)
        throws IOException
    {
        int result=targetInputStream.read(b, off, len);
        logger.write(b, off, result);
        return result;
    }

    public int read(byte[] b)
        throws IOException
    {
        int result=targetInputStream.read(b);
        logger.write(b, 0, result);
        return result;
    }
}
