/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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

package org.kapott.hbci.passport.storage.format.legacy;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kapott.cryptalgs.RSAPrivateCrtKey2;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.storage.PassportData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Abstrakte Basis-Implementierung des Converter fuer Passports, die intern XML-basiert speichern.
 */
public abstract class AbstractConverterXML extends AbstractConverter
{
    /**
     * Parst die XML-Datei und liefert das Root-Element.
     * @param is der InputStream.
     * @return das Root-Element.
     * @throws Exception
     */
    protected Element read(InputStream is) throws Exception
    {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        
        final DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(is).getDocumentElement();
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
        
        this.fill(doc,root,data);

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
     * Schreibt die Daten in die XML-Struktur.
     * @param doc das Dokument.
     * @param root das Root-Element.
     * @param data die zu schreibenden Daten.
     */
    protected abstract void fill(Document doc, Element root, PassportData data);

    /**
     * Liefert einen einzelnen Wert.
     * @param root das Element.
     * @param name der Name des Elements.
     * @return
     */
    protected String getElementValue(Element root, String name)
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
    protected Properties getElementProps(Element root, String name)
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
     * Parst den Schluessel aus der XML-Struktur.
     * @param root das Root-Element.
     * @param owner Owner.
     * @param type Typ.
     * @param part Teil.
     * @return der Schluessel.
     * @throws Exception
     */
    protected HBCIKey getElementKey(Element root, String owner, String type, String part) throws Exception
    {
        HBCIKey ret = null;

        NodeList keys = root.getElementsByTagName("key");
        int len = keys.getLength();

        for (int i = 0; i < len; i++)
        {
            Node n = keys.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            Element keynode = (Element) n;
            if (keynode.getAttribute("owner").equals(owner) && keynode.getAttribute("type").equals(type) && keynode.getAttribute("part").equals(part))
            {

                Key key;

                if (part.equals("public"))
                {
                    RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(getElementValue(keynode, "modulus")),
                                    new BigInteger(getElementValue(keynode, "exponent")));
                    key = KeyFactory.getInstance("RSA").generatePublic(spec);
                }
                else
                {
                    String modulus = getElementValue(keynode, "modulus");
                    String privexponent = getElementValue(keynode, "exponent");
                    String pubexponent = getElementValue(keynode, "pubexponent");
                    String p = getElementValue(keynode, "p");
                    String q = getElementValue(keynode, "q");
                    String dP = getElementValue(keynode, "dP");
                    String dQ = getElementValue(keynode, "dQ");
                    String qInv = getElementValue(keynode, "qInv");

                    if (privexponent == null)
                    {
                        // only CRT
                        HBCIUtils.log("private " + type + " key is CRT-only", HBCIUtils.LOG_DEBUG);
                        key = new RSAPrivateCrtKey2(new BigInteger(p), new BigInteger(q), new BigInteger(dP), new BigInteger(dQ), new BigInteger(qInv));
                    }
                    else if (p == null)
                    {
                        // only exponent
                        HBCIUtils.log("private " + type + " key is exponent-only", HBCIUtils.LOG_DEBUG);
                        RSAPrivateKeySpec spec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privexponent));
                        key = KeyFactory.getInstance("RSA").generatePrivate(spec);
                    }
                    else
                    {
                        // complete data
                        HBCIUtils.log("private " + type + " key is fully specified", HBCIUtils.LOG_DEBUG);
                        RSAPrivateCrtKeySpec spec = new RSAPrivateCrtKeySpec(new BigInteger(modulus), new BigInteger(pubexponent),
                                        new BigInteger(privexponent), new BigInteger(p), new BigInteger(q), new BigInteger(dP), new BigInteger(dQ),
                                        new BigInteger(qInv));
                        key = KeyFactory.getInstance("RSA").generatePrivate(spec);
                    }
                }

                ret = new HBCIKey(getElementValue(keynode, "country"), getElementValue(keynode, "blz"), getElementValue(keynode, "userid"),
                                getElementValue(keynode, "keynum"), getElementValue(keynode, "keyversion"), key);

                break;
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
    protected void createElement(Document doc, Element root, String elemName, String elemValue)
    {
        Node elem = doc.createElement(elemName);
        root.appendChild(elem);
        Node data = doc.createTextNode(this.notNull(elemValue));
        elem.appendChild(data);
    }

    /**
     * Erzeugt ein Properties-Element.
     * @param doc das Dokument.
     * @param root das Root-Element.
     * @param elemName der Name des Element.s
     * @param p die Properties.
     */
    protected void createPropsElement(Document doc, Element root, String elemName, Properties p)
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
            data.setAttribute("value",this.notNull(value));
            base.appendChild(data);
        }
    }
    
    /**
     * Erzeugt die XML-Elemente fuer den Schluessel.
     * @param doc das Dokument.
     * @param root das Root-Element.
     * @param owner Owner.
     * @param type Typ.
     * @param part Teil.
     * @param key Schluessel.
     */
    protected void createKeyElement(Document doc, Element root, String owner, String type, String part, HBCIKey key)
    {
        if (key == null)
            return;
        
        Element base = doc.createElement("key");
        base.setAttribute("owner", this.notNull(owner));
        base.setAttribute("type", this.notNull(type));
        base.setAttribute("part", this.notNull(part));
        root.appendChild(base);

        createElement(doc, base, "country", notNull(key.country));
        createElement(doc, base, "blz", notNull(key.blz));
        createElement(doc, base, "userid", notNull(key.userid));
        createElement(doc, base, "keynum", notNull(key.num));
        createElement(doc, base, "keyversion", notNull(key.version));

        Element keydata = doc.createElement("keydata");
        base.appendChild(keydata);

        byte[] e = key.key.getEncoded();
        String encoded = (e != null) ? HBCIUtils.encodeBase64(e) : null;
        String format = key.key.getFormat();

        if (encoded != null)
        {
            Element data = doc.createElement("rawdata");
            data.setAttribute("format", format);
            data.setAttribute("encoding", "base64");
            keydata.appendChild(data);
            Node content = doc.createTextNode(encoded);
            data.appendChild(content);
        }

        if (part.equals("public") && key.key != null)
        {
            createElement(doc, keydata, "modulus", ((RSAPublicKey) key.key).getModulus().toString());
            createElement(doc, keydata, "exponent", ((RSAPublicKey) key.key).getPublicExponent().toString());
        }
        else
        {
            if (key.key instanceof RSAPrivateCrtKey)
            {
                HBCIUtils.log("saving " + type + " key as fully specified", HBCIUtils.LOG_DEBUG);
                createElement(doc, keydata, "modulus", ((RSAPrivateCrtKey) key.key).getModulus().toString());
                createElement(doc, keydata, "exponent", ((RSAPrivateCrtKey) key.key).getPrivateExponent().toString());
                createElement(doc, keydata, "pubexponent", ((RSAPrivateCrtKey) key.key).getPublicExponent().toString());
                createElement(doc, keydata, "p", ((RSAPrivateCrtKey) key.key).getPrimeP().toString());
                createElement(doc, keydata, "q", ((RSAPrivateCrtKey) key.key).getPrimeQ().toString());
                createElement(doc, keydata, "dP", ((RSAPrivateCrtKey) key.key).getPrimeExponentP().toString());
                createElement(doc, keydata, "dQ", ((RSAPrivateCrtKey) key.key).getPrimeExponentQ().toString());
                createElement(doc, keydata, "qInv", ((RSAPrivateCrtKey) key.key).getCrtCoefficient().toString());
            }
            else if (key.key instanceof RSAPrivateKey)
            {
                HBCIUtils.log("saving " + type + " key as exponent-only", HBCIUtils.LOG_DEBUG);
                createElement(doc, keydata, "modulus", ((RSAPrivateKey) key.key).getModulus().toString());
                createElement(doc, keydata, "exponent", ((RSAPrivateKey) key.key).getPrivateExponent().toString());
            }
            else if (key.key instanceof RSAPrivateCrtKey2)
            {
                HBCIUtils.log("saving " + type + " key as crt-only", HBCIUtils.LOG_DEBUG);
                createElement(doc, keydata, "p", ((RSAPrivateCrtKey2) key.key).getP().toString());
                createElement(doc, keydata, "q", ((RSAPrivateCrtKey2) key.key).getQ().toString());
                createElement(doc, keydata, "dP", ((RSAPrivateCrtKey2) key.key).getdP().toString());
                createElement(doc, keydata, "dQ", ((RSAPrivateCrtKey2) key.key).getdQ().toString());
                createElement(doc, keydata, "qInv", ((RSAPrivateCrtKey2) key.key).getQInv().toString());
            }
            else
            {
                HBCIUtils.log("key has none of the known types - please contact the author!", HBCIUtils.LOG_WARN);
            }
        }
    }

    /**
     * Liefert den Wert oder einen Leerstring, wenn "value" NULL ist.
     * @param value der Wert.
     * @return Leerstring oder der Wert, aber niemals NULL.
     */
    private String notNull(String value)
    {
        return value != null ? value : "";
    }
}
