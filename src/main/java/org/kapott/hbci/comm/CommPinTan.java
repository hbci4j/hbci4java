
/*  $Id: CommPinTan.java,v 1.1 2011/05/04 22:37:50 willuhn Exp $

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
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.protocol.MSG;

public final class CommPinTan
    extends Comm
{
    private URL               url;
    private HttpURLConnection conn;
    private boolean           checkCert;
    
    // die socket factory, die in jedem fall benutzt wird.
    private SSLSocketFactory   mySocketFactory;
    
    // der hostname-verifier, der nur dann benutzt wird, wenn zertifikate
    // nicht verifiziert werden sollen
    private HostnameVerifier   myHostnameVerifier;
    
    /**
     * Timeout fuer HTTP connect in Millisekunden.
     */
    private final static int HTTP_CONNECT_TIMEOUT = 60 * 1000;
    
    /**
     * Timeout fuer HTTP Read in Millisekunden.
     */
    private final static int HTTP_READ_TIMEOUT    = 5 * HTTP_CONNECT_TIMEOUT;

    
    public CommPinTan(HBCIPassportInternal parentPassport)
    {
        super(parentPassport);
        checkCert=((AbstractPinTanPassport)parentPassport).getCheckCert();
        
        String trustStore=((AbstractPinTanPassport)parentPassport).getCertFile();
        if (checkCert && trustStore!=null && trustStore.length()!=0) {
            System.setProperty("javax.net.ssl.trustStore",trustStore);
        }
        
        try {
            String fullpath=parentPassport.getHost();
            int    slashIdx=fullpath.indexOf("/");
            if (slashIdx==-1)
                slashIdx=fullpath.length();
            String host=fullpath.substring(0,slashIdx);
            String path=fullpath.substring(slashIdx);
            
            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("LOG_CONNECT",new Object[]{host,parentPassport.getPort(),path}),HBCIUtils.LOG_INFO);
            this.url=new URL("https",host,parentPassport.getPort().intValue(),path);

            // creating instances of modified socket factories etc.
            this.mySocketFactory=new PinTanSSLSocketFactory((AbstractPinTanPassport)parentPassport);
            this.myHostnameVerifier=new PinTanSSLHostnameVerifier();
            
            String[] proxyData=((AbstractPinTanPassport)parentPassport).getProxy().split(":");
            if (proxyData.length==2) {
                HBCIUtils.log(
                    "HTTPS connections will be made using proxy "+
                    proxyData[0]+ "(Port "+proxyData[1]+")",
                    HBCIUtils.LOG_INFO);
                
                Properties sysProps = System.getProperties();
                sysProps.put("https.proxyHost",proxyData[0]);
                sysProps.put("https.proxyPort",proxyData[1]);
                
                HBCIUtils.log("initializing HBCI4Java proxy authentication callback", HBCIUtils.LOG_DEBUG);
                Authenticator.setDefault(new PinTanProxyAuthenticator(parentPassport));
            }
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CONNERR"),e);
        }
    }

    protected void ping(MSG msg)
    {
        try {
            byte[] b=filter.encode(msg.toString(0));

            HBCIUtils.log("connecting to server",HBCIUtils.LOG_DEBUG);
            conn=(HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
            conn.setReadTimeout(HTTP_READ_TIMEOUT);
            
            boolean checkCert=((AbstractPinTanPassport)getParentPassport()).getCheckCert();
            boolean debugging=((PinTanSSLSocketFactory)this.mySocketFactory).debug();
            if (!checkCert || debugging) {
                // if we have to disable cert checking or enable ssl logging,
                // we have to set some special SSL stuff on the connection object
                HttpsURLConnection connSSL=(HttpsURLConnection)conn;
                
                HBCIUtils.log("activating modified socket factory for"
                    +" checkCert="+checkCert+" and debugging="+debugging, 
                    HBCIUtils.LOG_DEBUG);
                connSSL.setSSLSocketFactory(this.mySocketFactory);
                
                if (!checkCert) {
                    // checkcert=0 --> use dummy hostname verifier that always succeeds
                    HBCIUtils.log("activating modified hostname verifier because cert checking is disabled", 
                        HBCIUtils.LOG_DEBUG);
                    connSSL.setHostnameVerifier(this.myHostnameVerifier);
                }
            }
            
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setFixedLengthStreamingMode(b.length);

            conn.connect();                        
            OutputStream out=conn.getOutputStream();
            
            HBCIUtils.log("writing data to output stream", HBCIUtils.LOG_DEBUG);
            out.write(b);
            out.flush();
            
            HBCIUtils.log("closing output stream", HBCIUtils.LOG_DEBUG);
            out.close();
        } catch (Exception e) {
            HBCI_Exception he = new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDERR"),e);
            he.setFatal(true); // Abbruch. Auch dann, wenn es ein anonymer BPD-Abruf war
            throw he;
        }
    }

    protected StringBuffer pong(MsgGen gen)
    {
        try {
            byte[] b=new byte[1024];
            StringBuffer ret=new StringBuffer();

            HBCIUtils.log(HBCIUtilsInternal.getLocMsg("STATUS_MSG_RECV"),HBCIUtils.LOG_INFO);

            int msgsize=conn.getContentLength();
            int num;

            if (msgsize!=-1) {
                HBCIUtils.log("found messagesize: "+msgsize,HBCIUtils.LOG_DEBUG);
            } else {
                HBCIUtils.log("can not determine message size, trying to detect automatically",HBCIUtils.LOG_DEBUG);
            }
            InputStream i=conn.getInputStream();

            while (msgsize!=0 && (num=i.read(b))>0) {
                HBCIUtils.log("received "+num+" bytes",HBCIUtils.LOG_DEBUG2);
                ret.append(new String(b,0,num,ENCODING));
                msgsize-=num;
                if (msgsize>=0) {
                    HBCIUtils.log("we still need "+msgsize+" bytes",HBCIUtils.LOG_DEBUG2);
                } else {
                    HBCIUtils.log("read "+num+" bytes, looking for more",HBCIUtils.LOG_DEBUG2);
                }
            }

            HBCIUtils.log("closing communication line",HBCIUtils.LOG_DEBUG);
            conn.disconnect();
            return new StringBuffer(filter.decode(ret.toString()));
        } catch (Exception e) {
            // Die hier marieren wir nicht als fatal - ich meine mich zu erinnern,
            // dass es Banken gibt, die einen anonymen BPD-Abruf mit einem HTTP-Fehlercode quittieren
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_RECVERR"),e);
        }
    }

    protected void closeConnection()
    {
    }
}
