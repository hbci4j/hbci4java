/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format.legacy;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportAnonymous;
import org.kapott.hbci.passport.storage.PassportData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementierung des Converter fuer Anonymous-Passports.
 */
public class ConverterAnonymous extends AbstractConverter
{
    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#load(java.io.InputStream)
     */
    @Override
    public PassportData load(InputStream is) throws Exception
    {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        
        final DocumentBuilder db = dbf.newDocumentBuilder();
        Element root = db.parse(is).getDocumentElement();

        final PassportData data = new PassportData();
        data.blz         = this.getElementValue(root,"blz");
        data.country     = this.getElementValue(root,"country");
        data.host        = this.getElementValue(root,"host");
        data.hbciVersion = this.getElementValue(root,"hbciversion");
        data.bpd         = this.getElementProps(root,"bpd");
        data.upd         = this.getElementProps(root,"upd");

        String port = this.getElementValue(root,"port");
        if (port != null)
            data.port = Integer.parseInt(port);
        
        return data;
    }
    
    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#save(org.kapott.hbci.passport.storage.PassportData, java.io.OutputStream)
     */
    @Override
    public void save(PassportData data, OutputStream os) throws Exception
    {
        final DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        fac.setValidating(false);
        final DocumentBuilder db = fac.newDocumentBuilder();

        final Document doc = db.newDocument();
        final Element root = doc.createElement("HBCIPassportRDHNew");

        this.createElement(doc,root,"country",data.country);
        this.createElement(doc,root,"blz",data.blz);
        this.createElement(doc,root,"host",data.host);
        if (data.port != null)
            this.createElement(doc,root,"port",Integer.toString(data.port));
        
        this.createElement(doc,root,"hbciversion",data.hbciVersion);

        this.createPropsElement(doc,root,"bpd",data.bpd);
        this.createPropsElement(doc,root,"upd",data.upd);

        final TransformerFactory tfac = TransformerFactory.newInstance();
        final Transformer tform = tfac.newTransformer();

        tform.setOutputProperty(OutputKeys.METHOD,"xml");
        tform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
        tform.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
        tform.setOutputProperty(OutputKeys.INDENT,"yes");

        tform.transform(new DOMSource(root),new StreamResult(os));
        os.flush();
    }

    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#supports(org.kapott.hbci.passport.HBCIPassport)
     */
    @Override
    public boolean supports(HBCIPassport passport)
    {
        return passport != null && (passport instanceof HBCIPassportAnonymous);
    }
    
    /**
     * Liefert einen einzelnen Wert.
     * @param root das Element.
     * @param name der Name des Elements.
     * @return
     */
    private String getElementValue(Element root, String name)
    {
        NodeList list = root.getElementsByTagName(name);
        if (list != null && list.getLength() != 0)
        {
            Node content = list.item(0).getFirstChild();
            if (content != null)
                return content.getNodeValue();
        }
        return null;
    }
    
    /**
     * Liefert die Werte aus dem XML-Teil als Properties.
     * @param root Das Basis-Element.
     * @param name der Name des Elements.
     * @return die Properties.
     */
    private Properties getElementProps(Element root, String name)
    {
        Node base=root.getElementsByTagName(name).item(0);
        if (base == null)
            return null;
        
        Properties ret = new Properties();
        NodeList entries=base.getChildNodes();
        int len=entries.getLength();

        for (int i=0;i<len;i++)
        {
            Node n = entries.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE)
            {
                Element e = (Element) n;
                ret.setProperty(e.getAttribute("name"),e.getAttribute("value"));
            }
        }

        return ret;
    }
    
    /**
     * Erzeugt ein XML-Element.
     * @param doc das Dokument.
     * @param root das Root-Element.
     * @param elemName der Name des Elements.
     * @param elemValue der Wert des Elements.
     */
    private void createElement(Document doc, Element root, String elemName, String elemValue)
    {
        Node elem = doc.createElement(elemName);
        root.appendChild(elem);
        Node data = doc.createTextNode(elemValue);
        elem.appendChild(data);
    }

    /**
     * Erzeugt ein Properties-Element.
     * @param doc das Dokument.
     * @param root das Root-Element.
     * @param elemName der Name des Element.s
     * @param p die Properties.
     */
    private void createPropsElement(Document doc, Element root, String elemName, Properties p)
    {
        if (p == null)
            return;
        
        Node base = doc.createElement(elemName);
        root.appendChild(base);

        for (Enumeration e = p.propertyNames();e.hasMoreElements();)
        {
            String key = (String) e.nextElement();
            String value = p.getProperty(key);

            Element data = doc.createElement("entry");
            data.setAttribute("name",key);
            data.setAttribute("value",value);
            base.appendChild(data);
        }
    }
}
