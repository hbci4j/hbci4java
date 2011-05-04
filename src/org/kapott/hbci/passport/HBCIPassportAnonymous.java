
/*  $Id: HBCIPassportAnonymous.java,v 1.1 2011/05/04 22:37:43 willuhn Exp $

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

package org.kapott.hbci.passport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidPassphraseException;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** <p>Passport-Implementation für anonyme Zugänge. Bei dieser Passport-Variante
    handelt es sich nicht um einen "echten" HBCI-Zugang. Statt dessen handelt
    es sich hierbei um einen anonymen Zugang, wie er von einigen wenigen
    Banken angeboten wird. Bei einem anonymen Zugang werden die Nachrichten
    nicht kryptographisch gesichert (also keine Signaturen und keine
    Verschlüsselung). Aus diesem Grund können mit diesem Zugang maximal
    die Geschäftsvorfälle abgewickelt werden, die keine Signatur benötigten
    (z.B. Gastmeldung an Bank senden).</p>
    <p>Beim Einrichten eines solchen Passport-Objektes werden also keine
    Nutzer- bzw. Kunden-Kennungen abgefragt (diese sind automatisch auf die Kennungen
    für den anonymen Zugang eingestellt). Außerdem findet keine Synchronisierung
    der Schlüssel zwischen Bank und Kunde statt, da bei dieser Zugangsform
    keine Schlüssel verwendet werden.</p>
    <p>Eine HBCI-Anwendung kann ein Anonymous-Passport genauso verwenden wie
    ein "normales" Passport. Alle Abweichungen, die bei der Durchführung
    anonymer Dialoge zu beachten sind, werden völlig transparent von <em>HBCI4Java</em>
    umgesetzt.</p>
    <p>Gegenwärtig können mit Anonymous-Passports noch keine anonymen Geschäftsvorfälle
    ausgelöst werden. Diese Passport-Variante kann also nur für einen "leeren" 
    HBCI-Dialog verwendet werden, der aus (anonymer) Dialog-Initialisierung und
    (anonymem) Dialog-Ende besteht. Damit kann zumindest die Verfügbarkeit des
    HBCI-Servers bzw. von anonymen Zugängen überprüft werden.</p>*/
public class HBCIPassportAnonymous 
    extends AbstractHBCIPassport
{
    private String    filename;
    private SecretKey passportKey;
    
    protected final static byte[] CIPHER_SALT={(byte)0x26,(byte)0x19,(byte)0x38,(byte)0xa7,
                                               (byte)0x99,(byte)0xbc,(byte)0xf1,(byte)0x55};
    protected final static int CIPHER_ITERATIONS=987;

    public HBCIPassportAnonymous(Object initObject)
    {
        super(initObject);

        String  header="client.passport.Anonymous.";
        String  filename=HBCIUtils.getParam(header+"filename");
        boolean init=HBCIUtils.getParam(header+"init","1").equals("1");
        
        if (filename==null) {
            throw new NullPointerException("*** client.passport.Anonymous.filename must not be null");
        }

        HBCIUtils.log("loading passport data from file "+filename,HBCIUtils.LOG_DEBUG);
        setFileName(filename);
        setFilterType("None");
        setPort(new Integer(3000));

        if (init) {
            HBCIUtils.log("loading data from file "+filename,HBCIUtils.LOG_DEBUG);

            if (!new File(filename).canRead()) {
                HBCIUtils.log("have to create new passport file",HBCIUtils.LOG_WARN);
                askForMissingData(true,true,true,true,false,false,false);
                saveChanges();
            }

            try {
                DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
                dbf.setValidating(false);
                DocumentBuilder db=dbf.newDocumentBuilder();
                Element root=null;

                int retries=Integer.parseInt(HBCIUtils.getParam("client.retries.passphrase","3"));

                while (true) {          // loop for entering the correct passphrase
                    if (passportKey==null)
                        passportKey=calculatePassportKey(FOR_LOAD);

                    PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
                    Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
                    cipher.init(Cipher.DECRYPT_MODE,passportKey,paramspec);

                    root=null;
                    CipherInputStream ci=null;

                    try {
                        ci=new CipherInputStream(new FileInputStream(getFileName()),cipher);
                        root=db.parse(ci).getDocumentElement();
                    } catch (SAXException e) {
                        passportKey=null;

                        retries--;
                        if (retries<=0)
                            throw new InvalidPassphraseException();
                    } finally {
                        if (ci!=null)
                            ci.close();
                    }

                    if (root!=null)
                        break;
                }

                setCountry(getElementValue(root,"country"));
                setBLZ(getElementValue(root,"blz"));
                setHost(getElementValue(root,"host"));
                setPort(new Integer(getElementValue(root,"port")));
                setHBCIVersion(getElementValue(root,"hbciversion"));

                setBPD(getElementProps(root,"bpd"));
                setUPD(getElementProps(root,"upd"));
                
                if (askForMissingData(true,true,true,true,false,false,false))
                    saveChanges();
            } catch (Exception e) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_READERR"),e);
            }
        }
    }
    
    public String getPassportTypeName()
    {
        return "Anonymous";
    }
    
    private String getElementValue(Element root,String name)
    {
        String ret=null;

        NodeList list=root.getElementsByTagName(name);
        if (list!=null && list.getLength()!=0) {
            Node content=list.item(0).getFirstChild();
            if (content!=null)
                ret=content.getNodeValue();
        }

        return ret;
    }

    private Properties getElementProps(Element root,String name)
    {
        Properties ret=null;

        Node base=root.getElementsByTagName(name).item(0);
        if (base!=null) {
            ret=new Properties();
            NodeList entries=base.getChildNodes();
            int len=entries.getLength();

            for (int i=0;i<len;i++) {
                Node n=entries.item(i);
                if (n.getNodeType()==Node.ELEMENT_NODE) {
                    ret.setProperty(((Element)n).getAttribute("name"),
                                    ((Element)n).getAttribute("value"));
                }
            }
        }

        return ret;
    }
    
    public void setMyPublicDigKey(HBCIKey key)
    {
    }
    
    public String getSigFunction()
    {
         return "";
    }
    
    public String getProfileMethod()
    {
        return "";
    }

    public boolean needUserKeys()
    {
        return false;
    }
    
    public HBCIKey getInstEncKey()
    {
        return null;
    }

    public String getMyEncKeyVersion()
    {
        return "";
    }
    
    public String getMySigKeyNum()
    {
        return "";
    }

    public String getCryptMode()
    {
        return "";
    }

    public boolean needInstKeys()
    {
        return false;
    }
    
    public String getSigAlg()
    {
        return "";
    }

    public String getSigMode()
    {
        return "";
    }

    public byte[][] encrypt(byte[] parm1)
    {
        return new byte[][] {null,parm1};
    }
    
    public String getInstSigKeyVersion()
    {
        return "";
    }

    public void setInstSigKey(HBCIKey key)
    {
    }
    
    public String getCryptKeyType()
    {
        return "";
    }

    public String getMySigKeyName()
    {
        return "";
    }

    public String getMySigKeyVersion()
    {
        return "";
    }

    public HBCIKey getMyPublicEncKey()
    {
        return null;
    }
    
    public boolean needUserSig()
    {
        return false;
    }
    
    public HBCIKey getMyPublicDigKey()
    {
        return null;
    }
    
    public void setMyPrivateEncKey(HBCIKey key)
    {
    }
    
    protected Comm getCommInstance()
    {
        return Comm.getInstance("Standard",this);
    }
    
    public String getProfileVersion()
    {
        return "";
    }
    
    public void setMyPrivateSigKey(HBCIKey key)
    {
    }
    
    public HBCIKey getMyPrivateSigKey()
    {
        return null;
    }
    
    public HBCIKey getMyPublicSigKey()
    {
        return null;
    }
    
    public String getCryptAlg()
    {
        return "";
    }
    
    public void setMyPublicSigKey(HBCIKey key)
    {
    }

    public String getMyEncKeyNum()
    {
        return "";
    }
    
    public boolean hasMyEncKey()
    {
        return false;
    }
    
    public byte[] hash(byte[] data)
    {
        /* the function hash-before-sign has nothing to do here, so we simply
         * return the original message */
        return data;
    }
    
    public byte[] sign(byte[] data)
    {
        /* no signature at all */
        return new byte[0];
    }
    
    public HBCIKey getMyPrivateDigKey()
    {
        return null;
    }
    
    public boolean isSupported()
    {
        return true;
    }
    
    public boolean hasMySigKey()
    {
        return false;
    }
    
    public void resetPassphrase()
    {
    }
    
    public String getMyEncKeyName()
    {
        return "";
    }
    
    public String getCryptFunction()
    {
        return "";
    }
    
    public String getInstSigKeyName()
    {
        return "";
    }
    
    public void setMyPrivateDigKey(HBCIKey key)
    {
    }
    
    public HBCIKey getMyPrivateEncKey()
    {
        return null;
    }
    
    public void setMyPublicEncKey(HBCIKey key)
    {
    }

    public String getInstEncKeyVersion()
    {
        return "";
    }
    
    public String getHashAlg()
    {
        return "";
    }
    
    public byte[] decrypt(byte[] parm1, byte[] parm2)
    {
        return parm2;
    }
    
    public void setInstEncKey(HBCIKey key)
    {
    }
    
    public boolean hasInstEncKey()
    {
        return false;
    }
    
    public String getInstSigKeyNum()
    {
        return "";
    }
    
    public HBCIKey getInstSigKey()
    {
        return null;
    }
    
    public boolean verify(byte[] parm1, byte[] parm2)
    {
        return true;
    }
    
    public String getInstEncKeyName()
    {
        return "";
    }
    
    public String getInstEncKeyNum()
    {
        return "";
    }
    
    public boolean hasInstSigKey()
    {
        return false;
    }
    
    public String getSysStatus()
    {
        return "0";
    }
    
    private void setFileName(String filename)
    {
        this.filename=filename;
    }
    
    public String getFileName()
    {
        return filename;
    }

    public void saveChanges()
    {
        try {
            if (passportKey==null)
                passportKey=calculatePassportKey(FOR_SAVE);

            PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
            Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(Cipher.ENCRYPT_MODE,passportKey,paramspec);

            DocumentBuilderFactory fac=DocumentBuilderFactory.newInstance();
            fac.setValidating(false);
            DocumentBuilder db=fac.newDocumentBuilder();

            Document doc=db.newDocument();
            Element root=doc.createElement("HBCIPassportRDHNew");

            createElement(doc,root,"country",getCountry());
            createElement(doc,root,"blz",getBLZ());
            createElement(doc,root,"host",getHost());
            createElement(doc,root,"port",getPort().toString());
            createElement(doc,root,"hbciversion",getHBCIVersion());

            createPropsElement(doc,root,"bpd",getBPD());
            createPropsElement(doc,root,"upd",getUPD());

            TransformerFactory tfac=TransformerFactory.newInstance();
            Transformer tform=tfac.newTransformer();

            tform.setOutputProperty(OutputKeys.METHOD,"xml");
            tform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
            tform.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
            tform.setOutputProperty(OutputKeys.INDENT,"yes");

            File passportfile=new File(getFileName());
            File directory=passportfile.getAbsoluteFile().getParentFile();
            String prefix=passportfile.getName()+"_";
            File tempfile=File.createTempFile(prefix,"",directory);

            CipherOutputStream co=new CipherOutputStream(new FileOutputStream(tempfile),cipher);
            tform.transform(new DOMSource(root),new StreamResult(co));

            co.close();
            passportfile.delete();
            tempfile.renameTo(passportfile);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_WRITEERR"),e);
        }
    }

    private void createElement(Document doc,Element root,String elemName,String elemValue)
    {
        Node elem=doc.createElement(elemName);
        root.appendChild(elem);
        Node data=doc.createTextNode(elemValue);
        elem.appendChild(data);
    }

    private void createPropsElement(Document doc,Element root,String elemName,Properties p)
    {
        if (p!=null) {
            Node base=doc.createElement(elemName);
            root.appendChild(base);

            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                String value=p.getProperty(key);

                Element data=doc.createElement("entry");
                data.setAttribute("name",key);
                data.setAttribute("value",value);
                base.appendChild(data);
            }
        }
    }
    
    public boolean isAnonymous()
    {
        return true;
    }
}
