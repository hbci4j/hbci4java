
/*  $Id: InfoPointConnector.java,v 1.1 2011/05/04 22:37:46 willuhn Exp $

    This file is part of hbci4java
    Copyright (C) 2001-2008  Stefan Palme

    hbci4java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    hbci4java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.manager;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// TODO: auch rückmeldungen vom InfoPoint-Server entgegennehmen und auswerten
public class InfoPointConnector
{
    private URL url;
    
    public InfoPointConnector()
    {
        String _url=HBCIUtils.getParam("infoPoint.url", "http://hbci4java.kapott.org/infoPoint");
        HBCIUtils.log("configuring InfoPointer-Server with "+_url, HBCIUtils.LOG_INFO);
        
        try {
            this.url=new URL(_url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }
    
    private Element properties2XML(Document doc, String elemName, Properties props)
    {
        Element elem=doc.createElement(elemName);
        for (Enumeration e=props.keys(); e.hasMoreElements(); ) {
            String  key=(String)e.nextElement();
            Element paramElem=doc.createElement("param");
            paramElem.setAttribute("name", key);
            paramElem.setAttribute("value", props.getProperty(key));
            elem.appendChild(paramElem);
        }
        return elem;
    }
    
    private String prepareXMLDocument(String xmlType, Properties passportData, Properties msgData)
    {
        try {
            DocumentBuilderFactory fac=DocumentBuilderFactory.newInstance();
            fac.setIgnoringComments(true);
            fac.setValidating(false);
            DocumentBuilder builder=fac.newDocumentBuilder();
            Document        doc=builder.newDocument();
            
            // xml-dokument zusammenbauen
            Element rootElem=doc.createElement(xmlType);
            doc.appendChild(rootElem);
            
            // passport data
            Element passElem=properties2XML(doc, "passport", passportData);
            rootElem.appendChild(passElem);
            
            // sent message data
            Properties sentData=new Properties();
            for (Enumeration e=msgData.keys(); e.hasMoreElements(); ) {
                String key=(String)e.nextElement();
                if (key.startsWith("orig_")) {
                    if (key.indexOf(".ProcPrep.")!=-1) {
                        String value=msgData.getProperty(key);
                        if (key.endsWith(".lang")) {
                            sentData.setProperty("language", value);
                        } else if (key.endsWith(".prodName")) {
                            sentData.setProperty("productName", value);
                        } else if (key.endsWith(".prodVersion")) {
                            sentData.setProperty("productVersion", value);
                        }
                    }
                }
            }
            Element sentElem=properties2XML(doc, "sent", sentData);
            rootElem.appendChild(sentElem);
            
            // received message data

            // extract BPD from original message
            String msg_st=msgData.getProperty("_msg");
            int    len=msg_st.length();
            int    startpos=msg_st.indexOf("'HIBPA:")+1;
            int    posi=startpos;
            char   prevDelimiter='\'';
            List<String>   bpdSegCodes=Arrays.asList(new String[] {"HIBPA", "HIKOM", "HISHV", "HIKPV"});
            String bpd_st="";
            
            while (true) {
                int delimpos=HBCIUtilsInternal.getPosiOfNextDelimiter(msg_st,posi);
                
                if (prevDelimiter=='\'') {
                    // wir sind gerade beim beginn eines neuen segmentes, also
                    // den segcode ueberpruefen, ob wir tatsaechlich noch innerhalb
                    // der BPD sind
                    
                    String segCode=msg_st.substring(posi,delimpos);
                    if (!bpdSegCodes.contains(segCode)) {
                        // segcode ist kein einfaches bpd-segment, kann also
                        // hoechstens noch ein parameter-segment sein, das ebenfalls
                        // zu den BPD gehoeren wuerde
                        
                        if (!(segCode.length()==6 && segCode.charAt(1)=='I' && segCode.charAt(5)=='S')) {
                            // auch kein parameter-segment, also sind die BPD bereits zu ende
                            bpd_st=msg_st.substring(startpos, posi);
                            break;
                        }
                    }
                }
                
                // jetzt den gerade gefundenen delimiter merken 
                // und posi erhöhen
                if (delimpos<len) {
                    prevDelimiter=msg_st.charAt(delimpos);
                    posi=delimpos+1;
                } else {
                    HBCIUtils.log("reached end of msg without finding the end of BPD - message seems to be broken", HBCIUtils.LOG_ERR);
                    bpd_st=msg_st.substring(startpos);
                    break;
                }
            }
            
            // create BPD element
            Element bpdElem=doc.createElement("bpd");
            bpdElem.setAttribute("encoding","base64");
            rootElem.appendChild(bpdElem);
            
            // store BPD in element
            String  bpd_encoded=HBCIUtils.encodeBase64(bpd_st.getBytes(Comm.ENCODING));
            bpdElem.appendChild(doc.createTextNode(bpd_encoded));
            
            // create XML string
            TransformerFactory    tfac=TransformerFactory.newInstance();
            Transformer           trans=tfac.newTransformer();
            
            trans.setOutputProperty(OutputKeys.METHOD,               "xml");
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            trans.setOutputProperty(OutputKeys.ENCODING,             Comm.ENCODING);
            trans.setOutputProperty(OutputKeys.INDENT,               "yes");

            Source                source=new DOMSource(doc);
            ByteArrayOutputStream xmlStream=new ByteArrayOutputStream();
            Result                target=new StreamResult(xmlStream);
            
            trans.transform(source, target);
            return xmlStream.toString(Comm.ENCODING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void sendDataToServer(HBCIPassportInternal passport, String data)
    {
        StringBuffer retData=new StringBuffer(data);
        HBCIUtilsInternal.getCallback().callback(
                passport,
                HBCICallback.NEED_INFOPOINT_ACK,
                HBCIUtilsInternal.getLocMsg("CALLB_INFOPOINT_ACK"),
                HBCICallback.TYPE_BOOLEAN,
                retData);
        
        if (retData.length()==0) {
            HBCIUtils.log("sending data about successfully received BPD to InfoPoint server", HBCIUtils.LOG_INFO);
            
            HBCIUtilsInternal.getCallback().status(
                    passport, 
                    HBCICallback.STATUS_SEND_INFOPOINT_DATA, 
                    data);

            HttpURLConnection conn=null;
            try {
                conn=(HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "text/xml");
                conn.connect();

                // send data 
                OutputStream out=conn.getOutputStream();
                out.write(data.getBytes("ISO-8859-1"));
                out.flush();

                conn.getResponseCode();
                // TODO: response code und -daten werden ignoriert
                conn.disconnect();
            } catch (Exception e) {
                HBCIUtils.log(e);
            }
        } else {
            HBCIUtils.log("data NOT sent because of missing user confirmation", HBCIUtils.LOG_INFO);
        }
    }
    
    private Properties preparePassportData(HBCIPassportInternal passport)
    {
        Properties passportData=new Properties();
        
        passportData.setProperty("type", passport.getPassportTypeName());
        passportData.setProperty("country", passport.getCountry());
        passportData.setProperty("blz", passport.getBLZ());
        passportData.setProperty("host", passport.getHost());
        passportData.setProperty("filter", passport.getFilterType());
        
        String version=passport.getHBCIVersion();
        passportData.setProperty("hbciVersion", version.equals("plus")?"220":version);
        
        return passportData;
    }
    
    public void sendBPD(HBCIPassportInternal passport, Properties msgData)
    {
        Properties passportData=preparePassportData(passport);
        String     xmlData=prepareXMLDocument("bpdReceived", passportData, msgData);
        sendDataToServer(passport,xmlData);
    }

    public void sendPublicKeys(HBCIPassportInternal passport, Properties msgData)
    {
        /* TODO: activate this later
        Properties passportData=preparePassportData(passport);
        String     xmlData=prepareXMLDocument("keysReceived", passportData, msgData);
        sendDataToServer(passport,xmlData);
        */
    }
}
