
/*  $Id: LoggingSocket.java,v 1.1 2011/05/04 22:37:51 willuhn Exp $

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
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/* original idea for how to integrate "logging sockets"
 * by Thomas Kruse <tkruse@sforce.org> */
public class LoggingSocket
    extends SSLSocket
{
    private SSLSocket    targetSocket;
    private OutputStream logger;
    
    public LoggingSocket(Socket targetSocket, OutputStream logger)
    {
        this.targetSocket = (SSLSocket)targetSocket;
        this.logger = logger;
    }
    
    public InputStream getInputStream()
        throws IOException
    {
        LoggingInputStream logInputStream = new LoggingInputStream(targetSocket.getInputStream(), logger);
        return logInputStream;
    }

    public OutputStream getOutputStream()
        throws IOException
    {
        LoggingOutputStream outputStream = new LoggingOutputStream(targetSocket.getOutputStream(), logger);
        return outputStream;
    }

    public void addHandshakeCompletedListener(HandshakeCompletedListener arg0)
    {
        targetSocket.addHandshakeCompletedListener(arg0);
    }

    public void bind(SocketAddress bindpoint)
        throws IOException
    {
        targetSocket.bind(bindpoint);
    }

    public void close()
        throws IOException
    {
        targetSocket.close();
    }

    public void connect(SocketAddress endpoint, int timeout)
        throws IOException
    {
        targetSocket.connect(endpoint, timeout);
    }

    public void connect(SocketAddress endpoint)
        throws IOException
    {
        targetSocket.connect(endpoint);
    }

    public boolean equals(Object obj)
    {
        return targetSocket.equals(obj);
    }

    public SocketChannel getChannel()
    {
        return targetSocket.getChannel();
    }

    public String[] getEnabledCipherSuites()
    {
        return targetSocket.getEnabledCipherSuites();
    }

    public String[] getEnabledProtocols()
    {
        return targetSocket.getEnabledProtocols();
    }

    public boolean getEnableSessionCreation()
    {
        return targetSocket.getEnableSessionCreation();
    }

    public InetAddress getInetAddress()
    {
        return targetSocket.getInetAddress();
    }

    public boolean getKeepAlive()
        throws SocketException
    {
        return targetSocket.getKeepAlive();
    }

    public InetAddress getLocalAddress()
    {
        return targetSocket.getLocalAddress();
    }

    public int getLocalPort()
    {
        return targetSocket.getLocalPort();
    }

    public SocketAddress getLocalSocketAddress()
    {
        return targetSocket.getLocalSocketAddress();
    }

    public boolean getNeedClientAuth()
    {
        return targetSocket.getNeedClientAuth();
    }

    public boolean getOOBInline()
        throws SocketException
    {
        return targetSocket.getOOBInline();
    }

    public int getPort()
    {
        return targetSocket.getPort();
    }

    public int getReceiveBufferSize()
        throws SocketException
    {
        return targetSocket.getReceiveBufferSize();
    }

    public SocketAddress getRemoteSocketAddress()
    {
        return targetSocket.getRemoteSocketAddress();
    }

    public boolean getReuseAddress()
        throws SocketException
    {
        return targetSocket.getReuseAddress();
    }

    public int getSendBufferSize()
        throws SocketException
    {
        return targetSocket.getSendBufferSize();
    }

    public SSLSession getSession()
    {
        return targetSocket.getSession();
    }

    public int getSoLinger()
        throws SocketException
    {
        return targetSocket.getSoLinger();
    }

    public int getSoTimeout()
        throws SocketException
    {
        return targetSocket.getSoTimeout();
    }

    public String[] getSupportedCipherSuites()
    {
        return targetSocket.getSupportedCipherSuites();
    }

    public String[] getSupportedProtocols()
    {
        return targetSocket.getSupportedProtocols();
    }

    public boolean getTcpNoDelay()
        throws SocketException
    {
        return targetSocket.getTcpNoDelay();
    }

    public int getTrafficClass()
        throws SocketException
    {
        return targetSocket.getTrafficClass();
    }

    public boolean getUseClientMode()
    {
        return targetSocket.getUseClientMode();
    }

    public boolean getWantClientAuth()
    {
        return targetSocket.getWantClientAuth();
    }

    public int hashCode()
    {
        return targetSocket.hashCode();
    }

    public boolean isBound()
    {
        return targetSocket.isBound();
    }

    public boolean isClosed()
    {
        return targetSocket.isClosed();
    }

    public boolean isConnected()
    {
        return targetSocket.isConnected();
    }

    public boolean isInputShutdown()
    {
        return targetSocket.isInputShutdown();
    }

    public boolean isOutputShutdown()
    {
        return targetSocket.isOutputShutdown();
    }

    public void removeHandshakeCompletedListener(HandshakeCompletedListener arg0)
    {
        targetSocket.removeHandshakeCompletedListener(arg0);
    }

    public void sendUrgentData(int data)
        throws IOException
    {
        targetSocket.sendUrgentData(data);
    }

    public void setEnabledCipherSuites(String[] arg0)
    {
        targetSocket.setEnabledCipherSuites(arg0);
    }

    public void setEnabledProtocols(String[] arg0)
    {
        targetSocket.setEnabledProtocols(arg0);
    }

    public void setEnableSessionCreation(boolean arg0)
    {
        targetSocket.setEnableSessionCreation(arg0);
    }

    public void setKeepAlive(boolean on)
        throws SocketException
    {
        targetSocket.setKeepAlive(on);
    }

    public void setNeedClientAuth(boolean arg0)
    {
        targetSocket.setNeedClientAuth(arg0);
    }

    public void setOOBInline(boolean on)
        throws SocketException
    {
        targetSocket.setOOBInline(on);
    }

    public void setReceiveBufferSize(int size)
        throws SocketException
    {
        targetSocket.setReceiveBufferSize(size);
    }

    public void setReuseAddress(boolean on)
        throws SocketException
    {
        targetSocket.setReuseAddress(on);
    }

    public void setSendBufferSize(int size)
        throws SocketException
    {
        targetSocket.setSendBufferSize(size);
    }

    public void setSoLinger(boolean on, int linger)
        throws SocketException
    {
        targetSocket.setSoLinger(on, linger);
    }

    public void setSoTimeout(int timeout)
        throws SocketException
    {
        targetSocket.setSoTimeout(timeout);
    }

    public void setTcpNoDelay(boolean on)
        throws SocketException
    {
        targetSocket.setTcpNoDelay(on);
    }

    public void setTrafficClass(int tc)
        throws SocketException
    {
        targetSocket.setTrafficClass(tc);
    }

    public void setUseClientMode(boolean arg0)
    {
        targetSocket.setUseClientMode(arg0);
    }

    public void setWantClientAuth(boolean arg0)
    {
        targetSocket.setWantClientAuth(arg0);
    }

    public void shutdownInput()
        throws IOException
    {
        targetSocket.shutdownInput();
    }

    public void shutdownOutput()
        throws IOException
    {
        targetSocket.shutdownOutput();
    }

    public void startHandshake()
        throws IOException
    {
        targetSocket.startHandshake();
    }

    public String toString()
    {
        return targetSocket.toString();
    }
}
