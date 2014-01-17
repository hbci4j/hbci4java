
/*  $Id: TransactionsToXML.java,v 1.1 2011/05/04 22:37:44 willuhn Exp $

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

package org.kapott.hbci.tools;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.structures.Konto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// TODO: API-Dok.
public class TransactionsToXML
{
    public void createTransactionElements(Document doc, Element troot, List<GVRKUms.UmsLine> transactions)
    {
        for (Iterator<GVRKUms.UmsLine> i=transactions.iterator(); i.hasNext(); ) {
            GVRKUms.UmsLine transaction= i.next();
            Element         transElem=doc.createElement("transaction");
            troot.appendChild(transElem);
            
            Element vdate=doc.createElement("value_date");
            vdate.appendChild(doc.createTextNode(HBCIUtils.date2StringISO(transaction.valuta)));
            transElem.appendChild(vdate);

            Element bdate=doc.createElement("booking_date");
            bdate.appendChild(doc.createTextNode(HBCIUtils.date2StringISO(transaction.bdate)));
            transElem.appendChild(bdate);
            
            Element amount=doc.createElement("amount");
            amount.setAttribute("curr", transaction.value.getCurr());
            amount.appendChild(doc.createTextNode(HBCIUtils.bigDecimal2String(transaction.value.getBigDecimalValue())));
            transElem.appendChild(amount);
            
            Element saldo=doc.createElement("saldo");
            saldo.setAttribute("curr", transaction.saldo.value.getCurr());
            saldo.appendChild(doc.createTextNode(HBCIUtils.bigDecimal2String(transaction.saldo.value.getBigDecimalValue())));
            transElem.appendChild(saldo);
            
            if (!transaction.gvcode.equals("999")) {
                // structured_details
                Element structured=doc.createElement("structured_details");
                transElem.appendChild(structured);

                // participant
                Element participant=doc.createElement("participant");
                structured.appendChild(participant);

                Konto acc=transaction.other;
                
                Element name=doc.createElement("name");
                name.appendChild(doc.createTextNode(nullAsEmpty(acc!=null?acc.name:"")));
                participant.appendChild(name);

                Element name2=doc.createElement("name2");
                name2.appendChild(doc.createTextNode(nullAsEmpty(acc!=null?acc.name2:"")));
                participant.appendChild(name2);

                Element country=doc.createElement("country");
                country.appendChild(doc.createTextNode(nullAsEmpty(acc!=null?acc.country:"")));
                participant.appendChild(country);

                Element blz=doc.createElement("blz");
                blz.appendChild(doc.createTextNode(nullAsEmpty(acc!=null?acc.blz:"")));
                participant.appendChild(blz);

                Element number=doc.createElement("number");
                number.appendChild(doc.createTextNode(nullAsEmpty(acc!=null?acc.number:"")));
                participant.appendChild(number);

                // description
                Element descr=doc.createElement("description");
                structured.appendChild(descr);

                for (Iterator<String> j=transaction.usage.iterator(); j.hasNext(); ) {
                    Element line=doc.createElement("line");
                    String  usage= j.next();
                    line.appendChild(doc.createTextNode(nullAsEmpty(usage)));
                    descr.appendChild(line);
                }
            } else {
                // unstructured_details
                Element unstructured=doc.createElement("unstructured_details");
                transElem.appendChild(unstructured);
                
                unstructured.appendChild(doc.createTextNode(nullAsEmpty(transaction.additional)));
            }
            
            // booking_type
            Element btype=doc.createElement("booking_type");
            transElem.appendChild(btype);
            
            Element code=doc.createElement("code");
            code.appendChild(doc.createTextNode(nullAsEmpty(transaction.gvcode)));
            btype.appendChild(code);
            
            Element txt=doc.createElement("text");
            String st="";
            if (!transaction.gvcode.equals("999")) {
                st=nullAsEmpty(transaction.text);
            }
            txt.appendChild(doc.createTextNode(st));
            btype.appendChild(txt);
        }
    }
    
    public Document createXMLDocument(List<UmsLine> transactions, String rawMT940)
    {
        // Empfangene Transaktionen als XML-Datei aufbereiten
        DocumentBuilderFactory fac=DocumentBuilderFactory.newInstance();
        fac.setIgnoringComments(true);
        fac.setValidating(false);

        // create document
        DocumentBuilder builder;
        try {
            builder=fac.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc=builder.newDocument();

        Element root=doc.createElement("account_transactions");
        doc.appendChild(root);

        // <transactions>
        if (transactions!=null) {
            Element transElement=doc.createElement("transactions");
            root.appendChild(transElement);
            createTransactionElements(doc, transElement, transactions);
        }
        
        // <raw>
        if (rawMT940!=null) {
            Element rawElem=doc.createElement("raw");
            root.appendChild(rawElem);

            try {
                String mt940_encoded=HBCIUtils.encodeBase64(rawMT940.getBytes("ISO-8859-1"));
                rawElem.appendChild(doc.createCDATASection(mt940_encoded));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return doc;
    }
    
    public void writeXMLString(Document doc, OutputStream out)
    {
        if (doc==null) {
            throw new NullPointerException("document must not be null");
        }
        if (out==null) {
            throw new NullPointerException("output stream must not be null");
        }
        try {
            TransformerFactory    transFac=TransformerFactory.newInstance();
            Transformer           trans=transFac.newTransformer();

            trans.setOutputProperty(OutputKeys.METHOD,"xml");
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
            trans.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            trans.setOutputProperty(OutputKeys.INDENT,"yes");

            Source       source=new DOMSource(doc);
            Result       target=new StreamResult(out);
            trans.transform(source, target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private String nullAsEmpty(String st)
    {
        String ret=st;
        if (ret==null) {
            ret="";
        }
        return ret;
    }
    
}
