
/*  $Id: HBCIPassportRDHNew.java,v 1.2 2011/05/25 10:07:20 willuhn Exp $

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

import java.io.CharConversionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kapott.cryptalgs.RSAPrivateCrtKey2;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidPassphraseException;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** <p>Passport-Klasse für RDH-Zugänge mit Sicherheitsmedium "Datei". Bei dieser Variante
    werden sowohl die HBCI-Zugangsdaten wie auch die kryptografischen Schlüssel für
    die Signierung/Verschlüsselung der HBCI-Nachrichten in einer Datei gespeichert.
    Der Dateiname kann dabei beliebig vorgegeben werden. Da diese Datei vertrauliche
    Informationen enthält, wird der Inhalt verschlüsselt abgespeichert.
    Vor dem Erzeugen bzw. Einlesen wird via Callback-Mechanismus nach einem Passwort
    gefragt, aus dem der Schlüssel zur Verschlüsselung/Entschlüsselung der Schlüsseldatei
    berechnet wird.</p><p>
    Wie auch bei {@link org.kapott.hbci.passport.HBCIPassportDDV} werden in
    der Schlüsseldatei zusätzliche Informationen gespeichert. Dazu gehören u.a. die BPD
    und die UPD sowie die HBCI-Version, die zuletzt mit diesem Passport benutzt wurde.
    Im Gegensatz zu den "Hilfsdateien" bei DDV-Passports darf die Schlüsseldatei bei
    RDH-Passports aber niemals manuell gelöscht werden, da dabei auch die kryptografischen
    Schlüssel des Kunden verlorengehen. Diese können nicht wieder hergestellt werden, so
    dass in einem solchen Fall ein manuelles Zurücksetzes des HBCI-Zuganges bei der Bank
    erfolgen muss!</p>
    <p>Die Schlüsseldateien, die <em>HBCI4Java</em> mit dieser Klasse erzeugt und verwaltet, sind
    <b>nicht kompatibel</b> zu den Schlüsseldateien anderer HBCI-Software (z.B. VR-NetWorld
    o.ä.). Es ist also nicht möglich, durch Auswahl des Sicherheitsverfahrens "RDH" oder "RDHNew" und
    Angabe einer schon existierenden Schlüsseldatei, die mit einer anderen HBCI-Software
    erstellt wurde, diese Schlüsseldatei unter <em>HBCI4Java</em> zu benutzen! Es ist jedoch im
    Prinzip möglich, mit der "anderen" Software die Kundenschlüssel sperren zu lassen und
    anschließend mit <em>HBCI4Java</em> eine völlig neue Schlüsseldatei zu erzeugen. Das hat aber zwei
    Nachteile: Zum einen muss nach dem Neuerzeugen der Schlüsseldatei auch ein neuer
    INI-Brief erzeugt und an die Bank gesandt werden, um die neuen Schlüssel freischalten
    zu lassen. Außerdem lässt sich natürlich die <em>HBCI4Java</em>-Schlüsseldatei nicht mehr
    in der "anderen" HBCI-Software benutzen. Ein Parallel-Betrieb verschiedener HBCI-Softwarelösungen,
    die alle auf dem RDH-Verfahren mit Sicherheitsmedium "Datei" (oder Diskette) basieren,
    ist meines Wissens nicht möglich.</p>
    <p>Ein weiterer Ausweg aus diesem Problem wäre, eine technische Beschreibung des
    Formates der Schlüsseldateien der "anderen" HBCI-Software zu besorgen und diese
    dem <a href="mailto:hbci4java@kapott.org">Autor</a> zukommen zu lassen, damit eine Passport-Variante
    implementiert werden kann, die mit Schlüsseldateien dieser "anderen" Software arbeiten kann.</p>
    @see org.kapott.hbci.tools.INILetter INILetter */
public class HBCIPassportRDHNew 
    extends AbstractRDHSWFileBasedPassport
{
	private String profileVersion;
	
    public HBCIPassportRDHNew(Object init,int dummy)
    {
        super(init);
    }
    
    public HBCIPassportRDHNew(Object initObject)
    {
        this(initObject,0);
        setParamHeader("client.passport.RDHNew");

        String  filename=HBCIUtils.getParam(getParamHeader()+".filename");
        boolean init=HBCIUtils.getParam(getParamHeader()+".init","1").equals("1");
        
        if (filename==null) {
            throw new NullPointerException(getParamHeader()+".filename must not be null");
        }

        HBCIUtils.log("loading passport data from file "+filename,HBCIUtils.LOG_DEBUG);
        setFilename(filename);

        if (init) {
            HBCIUtils.log("loading data from file "+filename,HBCIUtils.LOG_DEBUG);

            setFilterType("None");
            setPort(new Integer(3000));

            if (!new File(filename).canRead()) {
                HBCIUtils.log("have to create new passport file",HBCIUtils.LOG_WARN);
                askForMissingData(true,true,true,true,false,true,true);
                saveChanges();
            }
            
            try {
                DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
                dbf.setValidating(false);
                DocumentBuilder db=dbf.newDocumentBuilder();
                Element root=null;

                int retries=Integer.parseInt(HBCIUtils.getParam("client.retries.passphrase","3"));

                while (true) {          // loop for entering the correct passphrase
                    if (getPassportKey()==null)
                    	setPassportKey(calculatePassportKey(FOR_LOAD));

                    PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
                    Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
                    cipher.init(Cipher.DECRYPT_MODE,getPassportKey(),paramspec);

                    root=null;
                    CipherInputStream ci=null;
                    
                    try {
                        ci=new CipherInputStream(new FileInputStream(getFilename()),cipher);
                        root=db.parse(ci).getDocumentElement();
                    } catch (Exception e) {
                      
                        // willuhn 2011-05-25 Wir lassen einen erneuten Versuch nur bei einer der beiden
                        // folgenden Exceptions zu. 
                        // Die "CharConversionException" ist in der Praxis eine
                        // " com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException: Invalid byte 2 of 2-byte UTF-8 sequence."
                        // Sie fliegt in "db.parse()". Sprich: Der CipherInputStream kann nicht erkennen,
                        // ob das Passwort falsch ist. Stattdessen decodiert er einfach Muell, der
                        // anschliessend nicht als XML-Dokument gelesen werden kann
                        if (!(e instanceof SAXException) && !(e instanceof CharConversionException))
                          throw e;
                        
                        resetPassphrase();

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
                setUserId(getElementValue(root,"userid"));
                setCustomerId(getElementValue(root,"customerid"));
                setSysId(getElementValue(root,"sysid"));
                setSigId(new Long(getElementValue(root,"sigid")));
                
                String rdhprofile=getElementValue(root,"rdhprofile");
                setProfileVersion(rdhprofile!=null?rdhprofile:"");
                
                setHBCIVersion(getElementValue(root,"hbciversion"));
                
                setBPD(getElementProps(root,"bpd"));
                setUPD(getElementProps(root,"upd"));
                
                setInstSigKey(getElementKey(root,"inst","S","public"));
                setInstEncKey(getElementKey(root,"inst","V","public"));
                setMyPublicSigKey(getElementKey(root,"user","S","public"));
                setMyPrivateSigKey(getElementKey(root,"user","S","private"));
                setMyPublicEncKey(getElementKey(root,"user","V","public"));
                setMyPrivateEncKey(getElementKey(root,"user","V","private"));
                
                if (askForMissingData(true,true,true,true,false,true,true))
                    saveChanges();
            } catch (Exception e) {
                throw new HBCI_Exception("*** error while reading passport file",e);
            }
        }
    }
    
    protected String getElementValue(Element root,String name)
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
    
    protected Properties getElementProps(Element root,String name)
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
    
    protected HBCIKey getElementKey(Element root,String owner,String type,String part)
        throws Exception
    {
        HBCIKey ret=null;
        
        NodeList keys=root.getElementsByTagName("key");
        int len=keys.getLength();
        
        for (int i=0;i<len;i++) {
            Node n=keys.item(i);
            if (n.getNodeType()==Node.ELEMENT_NODE) {
                Element keynode=(Element)n;
                if (keynode.getAttribute("owner").equals(owner) &&
                    keynode.getAttribute("type").equals(type) &&
                    keynode.getAttribute("part").equals(part)) {
                
                    Key key;
                    
                    if (part.equals("public")) {
                        RSAPublicKeySpec spec=new RSAPublicKeySpec(new BigInteger(getElementValue(keynode,"modulus")),
                                                                   new BigInteger(getElementValue(keynode,"exponent")));
                        key=KeyFactory.getInstance("RSA").generatePublic(spec);
                    } else {
                        String modulus=getElementValue(keynode,"modulus");
                        String privexponent=getElementValue(keynode,"exponent");
                        String pubexponent=getElementValue(keynode,"pubexponent");
                        String p=getElementValue(keynode,"p");
                        String q=getElementValue(keynode,"q");
                        String dP=getElementValue(keynode,"dP");
                        String dQ=getElementValue(keynode,"dQ");
                        String qInv=getElementValue(keynode,"qInv");
                        
                        if (privexponent==null) {
                            // only CRT
                            HBCIUtils.log("private "+type+" key is CRT-only",HBCIUtils.LOG_DEBUG);
                            key=new RSAPrivateCrtKey2(new BigInteger(p),
                                                      new BigInteger(q),
                                                      new BigInteger(dP),
                                                      new BigInteger(dQ),
                                                      new BigInteger(qInv));
                        } else if (p==null) {
                            // only exponent
                            HBCIUtils.log("private "+type+" key is exponent-only",HBCIUtils.LOG_DEBUG);
                            RSAPrivateKeySpec spec=new RSAPrivateKeySpec(new BigInteger(modulus),
                                                                         new BigInteger(privexponent));
                            key=KeyFactory.getInstance("RSA").generatePrivate(spec);
                        } else {
                            // complete data
                            HBCIUtils.log("private "+type+" key is fully specified",HBCIUtils.LOG_DEBUG);
                            RSAPrivateCrtKeySpec spec=new RSAPrivateCrtKeySpec(new BigInteger(modulus),
                                                                               new BigInteger(pubexponent),
                                                                               new BigInteger(privexponent),
                                                                               new BigInteger(p),
                                                                               new BigInteger(q),
                                                                               new BigInteger(dP),
                                                                               new BigInteger(dQ),
                                                                               new BigInteger(qInv));
                            key=KeyFactory.getInstance("RSA").generatePrivate(spec);
                        }
                    }
                    
                    ret=new HBCIKey(getElementValue(keynode,"country"),
                                    getElementValue(keynode,"blz"),
                                    getElementValue(keynode,"userid"),
                                    getElementValue(keynode,"keynum"),
                                    getElementValue(keynode,"keyversion"),
                                    key);
                    
                    break;
                }
            }
        }
        
        return ret;
    }

    public void saveChanges()
    {
        try {
            if (getPassportKey()==null)
                setPassportKey(calculatePassportKey(FOR_SAVE));

            PBEParameterSpec paramspec=new PBEParameterSpec(CIPHER_SALT,CIPHER_ITERATIONS);
            Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
            cipher.init(Cipher.ENCRYPT_MODE,getPassportKey(),paramspec);

            DocumentBuilderFactory fac=DocumentBuilderFactory.newInstance();
            fac.setValidating(false);
            DocumentBuilder db=fac.newDocumentBuilder();
            
            Document doc=db.newDocument();
            Element root=doc.createElement("HBCIPassportRDHNew");
            
            createElement(doc,root,"country",getCountry());
            createElement(doc,root,"blz",getBLZ());
            createElement(doc,root,"host",getHost());
            createElement(doc,root,"port",getPort().toString());
            createElement(doc,root,"userid",getUserId());
            createElement(doc,root,"customerid",getCustomerId());
            createElement(doc,root,"sysid",getSysId());
            createElement(doc,root,"sigid",getSigId().toString());
            createElement(doc,root,"rdhprofile",getProfileVersion());
            createElement(doc,root,"hbciversion",getHBCIVersion());
            
            createPropsElement(doc,root,"bpd",getBPD());
            createPropsElement(doc,root,"upd",getUPD());
            
            createKeyElement(doc,root,"inst","S","public",getInstSigKey());
            createKeyElement(doc,root,"inst","V","public",getInstEncKey());
            createKeyElement(doc,root,"user","S","public",getMyPublicSigKey());
            createKeyElement(doc,root,"user","S","private",getMyPrivateSigKey());
            createKeyElement(doc,root,"user","V","public",getMyPublicEncKey());
            createKeyElement(doc,root,"user","V","private",getMyPrivateEncKey());
            
            TransformerFactory tfac=TransformerFactory.newInstance();
            Transformer tform=tfac.newTransformer();
            
            tform.setOutputProperty(OutputKeys.METHOD,"xml");
            tform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"no");
            tform.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
            tform.setOutputProperty(OutputKeys.INDENT,"yes");
            
            File passportfile=new File(getFilename());
            File directory=passportfile.getAbsoluteFile().getParentFile();
            String prefix=passportfile.getName()+"_";
            File tempfile=File.createTempFile(prefix,"",directory);
            
            CipherOutputStream co=new CipherOutputStream(new FileOutputStream(tempfile),cipher);
            tform.transform(new DOMSource(root),new StreamResult(co));
            
            co.close();
            passportfile.delete();
            tempfile.renameTo(passportfile);
        } catch (Exception e) {
            throw new HBCI_Exception("*** saving of passport file failed",e);
        }
    }

    protected void createElement(Document doc,Element root,String elemName,String elemValue)
    {
        Node elem=doc.createElement(elemName);
        root.appendChild(elem);
        Node data=doc.createTextNode(elemValue);
        elem.appendChild(data);
    }
    
    protected void createPropsElement(Document doc,Element root,String elemName,Properties p)
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
    
    protected void createKeyElement(Document doc,Element root,String owner,String type,String part,HBCIKey key)
    {
        if (key!=null) {
            Element base=doc.createElement("key");
            base.setAttribute("owner",owner);
            base.setAttribute("type",type);
            base.setAttribute("part",part);
            root.appendChild(base);
            
            createElement(doc,base,"country",key.country);
            createElement(doc,base,"blz",key.blz);
            createElement(doc,base,"userid",key.userid);
            createElement(doc,base,"keynum",key.num);
            createElement(doc,base,"keyversion",key.version);
            
            Element keydata=doc.createElement("keydata");
            base.appendChild(keydata);

            byte[] e=key.key.getEncoded();
            String encoded=(e!=null)?HBCIUtils.encodeBase64(e):null;
            String format=key.key.getFormat();

            if (encoded!=null) {
                Element data=doc.createElement("rawdata");
                data.setAttribute("format",format);
                data.setAttribute("encoding","base64");
                keydata.appendChild(data);
                Node content=doc.createTextNode(encoded);
                data.appendChild(content);
            }
            
            if (part.equals("public")) {
                createElement(doc,keydata,"modulus",((RSAPublicKey)key.key).getModulus().toString());
                createElement(doc,keydata,"exponent",((RSAPublicKey)key.key).getPublicExponent().toString());
            } else {
                if (key.key instanceof RSAPrivateCrtKey) {
                    HBCIUtils.log("saving "+type+" key as fully specified",HBCIUtils.LOG_DEBUG);
                    createElement(doc,keydata,"modulus",((RSAPrivateCrtKey)key.key).getModulus().toString());
                    createElement(doc,keydata,"exponent",((RSAPrivateCrtKey)key.key).getPrivateExponent().toString());
                    createElement(doc,keydata,"pubexponent",((RSAPrivateCrtKey)key.key).getPublicExponent().toString());
                    createElement(doc,keydata,"p",((RSAPrivateCrtKey)key.key).getPrimeP().toString());
                    createElement(doc,keydata,"q",((RSAPrivateCrtKey)key.key).getPrimeQ().toString());
                    createElement(doc,keydata,"dP",((RSAPrivateCrtKey)key.key).getPrimeExponentP().toString());
                    createElement(doc,keydata,"dQ",((RSAPrivateCrtKey)key.key).getPrimeExponentQ().toString());
                    createElement(doc,keydata,"qInv",((RSAPrivateCrtKey)key.key).getCrtCoefficient().toString());
                } else if (key.key instanceof RSAPrivateKey) {
                    HBCIUtils.log("saving "+type+" key as exponent-only",HBCIUtils.LOG_DEBUG);
                    createElement(doc,keydata,"modulus",((RSAPrivateKey)key.key).getModulus().toString());
                    createElement(doc,keydata,"exponent",((RSAPrivateKey)key.key).getPrivateExponent().toString());
                } else if (key.key instanceof RSAPrivateCrtKey2) {
                    HBCIUtils.log("saving "+type+" key as crt-only",HBCIUtils.LOG_DEBUG);
                    createElement(doc,keydata,"p",((RSAPrivateCrtKey2)key.key).getP().toString());
                    createElement(doc,keydata,"q",((RSAPrivateCrtKey2)key.key).getQ().toString());
                    createElement(doc,keydata,"dP",((RSAPrivateCrtKey2)key.key).getdP().toString());
                    createElement(doc,keydata,"dQ",((RSAPrivateCrtKey2)key.key).getdQ().toString());
                    createElement(doc,keydata,"qInv",((RSAPrivateCrtKey2)key.key).getQInv().toString());
                } else {
                    HBCIUtils.log("key has none of the known types - please contact the author!",HBCIUtils.LOG_WARN);
                }
            }
        }         
    }
    
    public void setProfileVersion(String version)
    {
    	this.profileVersion=version;
    }
    
    public String getProfileVersion()
    {
        String ret=this.profileVersion;
        if (ret==null) {
            ret="";
        }

        if (ret.length()==0) {
            HBCIUtils.log("have to determine my rdh-profile-version, but have no information about it yet", HBCIUtils.LOG_DEBUG);

            // es ist noch keine profilnummer bekannt, d.h. im passport-file
            // stand keine drin

            // das kann entweder daran liegen, dass es sich um ein "altes"
            // rdhnew-file handelte (in diesem fall ist die nummer "1"),
            // oder weil noch gar keine schlüssel im file gespeichert sind
            // und damit auch kein profil feststeht - in diesem fall verwenden
            // wir die höchste profil-nummer aus den BPD

            if (hasMySigKey()) {
                HBCIUtils.log("found user sig key in passport file, but no profile version, "+
                        "so I guess it is an old RDHnew file, which always stored RDH-1 keys",
                        HBCIUtils.LOG_DEBUG);
                // es gibt Schlüssel, aber keine profilVersion, also haben wir
                // gerade ein altes file gelesen, in dem diese Info noch nicht
                // drinstand
                ret="1";
            } else {
                HBCIUtils.log("no user keys found in passport - so we use the highest available profile",
                        HBCIUtils.LOG_DEBUG);

                // es gibt noch gar keine schlüssel - also nehmen wir die
                // höchste unterstützte profil-nummer

                String[][] methods=getSuppSecMethods();
                int        maxVersion=0;
                for (int i=0;i<methods.length;i++) {
                    String method=methods[i][0];
                    int    version=Integer.parseInt(methods[i][1]);

                    if (method.equals("RDH") && 
                            (version==1 || version==2 || version==10)) 
                    {
                        // es werden nur RDH-1, RDH-2 und RDH-10 betrachtet, weil
                        // alle anderen rdh-profile nicht für software-lösungen
                        // zugelassen sind
                        if (version>maxVersion) {
                            maxVersion=version;
                        }
                    }
                }

                if (maxVersion!=0) {
                    ret=Integer.toString(maxVersion);
                }
                HBCIUtils.log("using RDH profile "+ret+" taken from supported profiles (BPD)",
                        HBCIUtils.LOG_DEBUG);
            }
        }

        // in jedem fall merken wir uns die gerade ermittelte profil-nummer
        setProfileVersion(ret);

        return ret;
    }
}
