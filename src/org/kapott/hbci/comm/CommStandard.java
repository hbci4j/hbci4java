
/*  $Id: CommStandard.java,v 1.1 2011/05/04 22:37:50 willuhn Exp $

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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.protocol.MSG;

public final class CommStandard
    extends Comm
{
    Socket s;                /**< @internal @brief The socket for communicating with the server. */
    OutputStream o;          /**< @internal @brief The outputstream to write HBCI-messages to. */
    InputStream i;           /**< @internal @brief The inputstream to read HBCI-messages from. */

    public CommStandard(HBCIPassportInternal parentPassport)
    {
        super(parentPassport);
        
        HBCIUtils.log("opening connection to "+
                parentPassport.getHost()+":"+
                parentPassport.getPort().toString(),
                HBCIUtils.LOG_DEBUG);
        
        try {
            String socksServer=HBCIUtils.getParam("comm.standard.socks.server");
            if (socksServer!=null && socksServer.trim().length()!=0) {
                // use SOCKS server
                String[] ss=socksServer.split(":");
                String socksHost=ss[0].trim();
                String socksPort=ss[1].trim();
                HBCIUtils.log(
                    "using SOCKS server at "+socksHost+":"+socksPort,
                    HBCIUtils.LOG_DEBUG);
                
                Proxy proxy=new Proxy(
                    Proxy.Type.SOCKS, 
                    new InetSocketAddress(socksHost, Integer.parseInt(socksPort)));
                this.s=new Socket(proxy);
                
            } else {
                // no SOCKS server
                s=new Socket();
            }

            int localPort=Integer.parseInt(HBCIUtils.getParam("client.connection.localPort","0"));
            if (localPort!=0) {
                s.setReuseAddress(true);
                s.bind(new InetSocketAddress(localPort));
            }

            s.connect(new InetSocketAddress(parentPassport.getHost(),
                                            parentPassport.getPort().intValue()));
            i=s.getInputStream();
            o=s.getOutputStream();
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CONNERR"),e);
        }
    }

    protected void ping(MSG msg)
    {
        try {
            byte[] b=filter.encode(msg.toString(0));

            o.write(b);
            o.flush();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDERR"),ex);
        }
    }

    protected StringBuffer pong(MsgGen gen)
    {
        int          num;
        byte[]       b = new byte[1024];
        StringBuffer ret = new StringBuffer();
        boolean      sizeknown = false;
        int          msgsize=-1;

        HBCIUtils.log("waiting for response",HBCIUtils.LOG_INFO);

        try {
            StringBuffer res=new StringBuffer();
            
            while ((!sizeknown || msgsize>0) && (num=i.read(b))!=-1) {
                HBCIUtils.log("received "+num+" bytes",HBCIUtils.LOG_DEBUG2);

                String st=new String(b,0,num,ENCODING);

                ret.append(st);

                if (!sizeknown) {
                    res.setLength(0);
                    res.append(filter.decode(ret.toString()));
                        
                    msgsize=extractMessageSize(res);
                    if (msgsize!=-1) {
                        HBCIUtils.log("found message size: "+msgsize,HBCIUtils.LOG_DEBUG);
                        // jetzt ist die msgsize bekannt
                        // davon die anzahl der schon gelesenen zeichen abziehen
                        msgsize-=ret.length();
                        sizeknown=true;
                    }
                } else {
                    msgsize-=num;

                }
                HBCIUtils.log("we still need "+msgsize+" bytes",HBCIUtils.LOG_DEBUG2);
            }

            // FileOutputStream fo=new FileOutputStream("pong.dat");
            // fo.write(ret.toString().getBytes(ENCODING));
            // fo.close();

            return new StringBuffer(filter.decode(ret.toString()));
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_RECVERR"),ex);
        }
    }
    
    private int extractMessageSize(StringBuffer st)
    {
        int ret=-1;
        
        if (st!=null) {
            int firstPlus=st.indexOf("+");
            if (firstPlus!=-1) {
                int secondPlus=st.indexOf("+",firstPlus+1);
                if (secondPlus!=-1) {
                    ret=Integer.parseInt(st.substring(firstPlus+1,secondPlus));
                }
            }
        }
        
        return ret;
    }

    protected void closeConnection()
    {
        try {
            HBCIUtils.log("closing communication line",HBCIUtils.LOG_DEBUG);
            s.close();
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CLOSEERR"),ex);
        }
    }
}
