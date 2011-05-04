/*
 * $Id: PinTanSSLSocketFactory.java,v 1.1 2011/05/04 22:37:51 willuhn Exp $
 * 
 * This file is part of HBCI4Java Copyright (C) 2001-2008 Stefan Palme
 * 
 * HBCI4Java is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * HBCI4Java is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.kapott.hbci.comm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractPinTanPassport;

/* This SSLSocketFactory will be used in certain circumstances as a drop-in 
 * replacement for Java's standard SSLSocketFactory:
 *   - when certificate checking should be disabled (because we have to install
 *       an own SSLContext with a modified TrustManager in this case)
 *   - when we want to log the HTTP traffic going through the SSL socket 
 *       (because in this case we have to create "LoggingSockets" instead of
 *       standard Java sockets)
 *       
 * If none of this applies, Java's standard factory will be used.
 *    
 * This socket factory works as a Delegator by creating a java standard socket
 * first and then delegating all relevant API calls to the standard socket
 * while hooking in some special code to log traffic etc...
 * 
 * This socket factory CAN NOT BE USED WITH OPERA<10 because of a bug in operas
 * Java plugin, so when using this class in a Java Applet in Opera, the user
 * has to ENABLE certificate checking and DISABLE ssl-logging */
public class PinTanSSLSocketFactory
    extends SSLSocketFactory
{
    private SSLSocketFactory realSocketFactory;

    public PinTanSSLSocketFactory(AbstractPinTanPassport passport)
    {
        try {
            if (!passport.getCheckCert()) {
                HBCIUtils.log(
                    "creating socket factory with disabled cert checking",
                    HBCIUtils.LOG_WARN);
                
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null,
                    new TrustManager[] {new PinTanSSLTrustManager()},
                    new SecureRandom());
                this.realSocketFactory = sslContext.getSocketFactory();
                
            } else {
                HBCIUtils.log("using system socket factory", HBCIUtils.LOG_DEBUG);
                this.realSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    public boolean debug()
    {
        return HBCIUtils.getParam("log.ssl.enable","0").equals("1");
    }
    
    private OutputStream getLogger()
    {
        OutputStream result;        
        String filename=HBCIUtils.getParam("log.ssl.filename");
        
        if (filename==null || filename.length()==0) {
            HBCIUtils.log("no log.ssl.filename specified - logging to HBCI4Java logger", HBCIUtils.LOG_WARN);
            result=new HBCI4JavaLogOutputStream();
            
        } else {
            try {
                result = new FileOutputStream(filename, true);
                result.write('\n');
                result.write(HBCIUtils.datetime2StringISO(new Date()).getBytes(
                    "ISO-8859-1"));
                result.write('\n');
                result.flush();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        return result;
    }

    
    public Socket createSocket()
        throws IOException
    {
        HBCIUtils.log("createSocket()", HBCIUtils.LOG_DEBUG2);
        Socket sock=this.realSocketFactory.createSocket();
        if (debug()) {
            sock=new LoggingSocket(sock, getLogger());
        }
        return sock;
    }

    
    public Socket createSocket(Socket sock, String host, int port, boolean autoClose)
        throws IOException
    {
        HBCIUtils.log("createSocket(sock,host,port,autoClose)", HBCIUtils.LOG_DEBUG2);
        Socket result=this.realSocketFactory.createSocket(sock, host, port, autoClose);
        if (debug()) {
            result=new LoggingSocket(result, getLogger());
        }
        return result;
    }

    public String[] getDefaultCipherSuites()
    {
        return this.realSocketFactory.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites()
    {
        return this.realSocketFactory.getSupportedCipherSuites();
    }

    public Socket createSocket(String host, int port)
        throws IOException, UnknownHostException
    {
        HBCIUtils.log("createSocket(host,port)", HBCIUtils.LOG_DEBUG2);
        Socket sock=this.realSocketFactory.createSocket(host, port);
        if (debug()) {
            sock=new LoggingSocket(sock, getLogger());
        }
        return sock;
    }

    public Socket createSocket(InetAddress addr, int port)
        throws IOException
    {
        HBCIUtils.log("createSocket(addr,port)", HBCIUtils.LOG_DEBUG2);
        Socket sock=this.realSocketFactory.createSocket(addr, port);
        if (debug()) {
            sock=new LoggingSocket(sock, getLogger());
        }
        return sock;
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
        throws IOException, UnknownHostException
    {
        HBCIUtils.log("createSocket(host,port,localHost,localPort)", HBCIUtils.LOG_DEBUG2);
        Socket sock=this.realSocketFactory.createSocket(host, port, localHost, localPort);
        if (debug()) {
            sock=new LoggingSocket(sock, getLogger());
        }
        return sock;
    }

    public Socket createSocket(InetAddress addr, int port, InetAddress localHost, int localPort)
        throws IOException
    {
        HBCIUtils.log("createSocket(addr,port,localHost,localPort)", HBCIUtils.LOG_DEBUG2);
        Socket sock=this.realSocketFactory.createSocket(addr, port, localHost, localPort);
        if (debug()) {
            sock=new LoggingSocket(sock, getLogger());
        }
        return sock;
    }
}
