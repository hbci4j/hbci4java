
/*  $Id: MsgGen.java,v 1.1 2011/05/04 22:37:46 willuhn Exp $

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

package org.kapott.hbci.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.factory.MSGFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* Message-Generator-Klasse. Diese Klasse verwaltet die Syntax-Spezifikation
 * für die zu verwendende HBCI-Version. Hiermit wird das Erzeugen von
 * HBCI-Nachrichten gekapselt.
 * Dazu wird eine Hashtable verwaltet, die die Daten enthält, die in die
 * jeweilige Nachricht aufgenommen werden sollen. Die Hashtable enthält als
 * Key den "Pfad" zum Datenelement (DialogInit.MsgHead.hbciversion), als
 * Value den einzustellenden Wert im Klartext.
 * Das Erzeugen einer Nachricht geschieht in drei Schritten:
 *   1) MsgGen.reset() -- vollständiges Leeren der Daten-Hashtable
 *   2) MsgGen.set(key,value) -- speichern eines Datums für die Nachricht
 *      in der Hashtable
 *   3) MsgGen.generate(msgName) -- erzeugen der Nachricht mit dem Namen
 *      <msgName>. Dabei werden auch nur die Daten aus der Hashtable
 *      verwendet, dir mit "<msgName>." beginnen (so dass in der Datenhashtable
 *      auch zusätzliche Daten gespeichert werden können, solange sie nicht
 *      mit "<msgName>." beginnen).*/
public final class MsgGen
{
    private Document syntax;         /**< @internal @brief The representation of the syntax used by this generator */
    private Hashtable<String, String> clientValues;  /**< @internal @brief A table of properties set by the user to specify the message to be generated */
    
    // Wird vom Server-Code benutzt. Wenn ein Dialog reinkommt mit einer HBCI-
    // Version, die schon mal benutzt wurde, dann wird nicht das entsprechende
    // XML-Document nochmal erzeugt, sondern das alte wiederbenutzt.
    public MsgGen(Document syntax)
    {
        this.syntax=syntax;
        this.clientValues=new Hashtable<String, String>();
    }

    /* Initialisieren eines Message-Generators. Der <syntaFileStream> ist ein
     * Stream, mit dem eine XML-Datei mit einer HBCI-Syntaxspezifikation
     * eingelesen wird */
    public MsgGen(InputStream syntaxFileStream)
    {
        try {
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();

            dbf.setIgnoringComments(true);
            dbf.setValidating(true);

            DocumentBuilder db=dbf.newDocumentBuilder();
            syntax=db.parse(syntaxFileStream);
            syntaxFileStream.close();

            clientValues=new Hashtable<String, String>();
        } catch (FactoryConfigurationError e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_MSGGEN_DBFAC"),e);
        } catch (ParserConfigurationException e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_MSGGEN_DB"),e);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_MSGGEN_STXFILE"),e);
        }
    }

    /** @internal 
        @brief Generates the HBCI message @p msgName.

        The syntax description for the message to be generated is taken from an
        XML node @c MSGdef where the attribute @c id equals @p msgName.

        To build the message the values stored in @c clientValues will be used.

        @param msgName The name (i.e. XML-identifier for a MSGdef-node) of the message to be generated.
        @return A new MSG object representing the generated message.
    */
    public MSG generate(String msgName)
    {
        return MSGFactory.getInstance().createMSG(msgName,this,clientValues);
    }

    /** @internal 
        @brief Sets a certain property that is later used in message generation.

        @param path The path to the syntax element for which the value is to be set. For
                    more information about paths, see 
                    SyntaxElement::SyntaxElement()
        @param value The new value for the specified element.
    */
    public void set(String path, String value)
    {
        clientValues.put(path,value);
    }

    /** @internal @brief Clears the list of already set properties */
    public void reset()
    {
        clientValues.clear();
    }

    /** @internal @brief Returns the representation of the HBCI syntax used by this generator 

        @return The internally used representation of a HBCI syntax description.
     */
    public Document getSyntax()
    {
        return syntax;
    }
    
    public Hashtable<String, List<String>> getLowlevelGVs()
    {
        Hashtable<String, List<String>> result=new Hashtable<String, List<String>>();
        
        Element      gvlist=syntax.getElementById("GV");
        NodeList     gvs=gvlist.getChildNodes();
        int          len=gvs.getLength();
        StringBuffer type=new StringBuffer();
        
        for (int i=0;i<len;i++) {
            Node gvref=gvs.item(i);
            if (gvref.getNodeType()==Node.ELEMENT_NODE) {
                type.setLength(0);
                type.append(((Element)gvref).getAttribute("type"));
                
                int  pos=type.length()-1;
                char ch;
                
                while ((ch=type.charAt(pos))>='0' && ch<='9') {
                    pos--;
                }
                
                String gvname=type.substring(0,pos+1);
                List<String>   entry= result.get(gvname);
                
                if (entry==null) {
                    entry=new ArrayList<String>();
                    result.put(gvname,entry);
                }
                entry.add(type.substring(pos+1));
            }
        }
        
        return result;
    }
    
    /* gibt für einen hbci-gv ("saldo3") die liste aller ll-job-parameter
     * zurück */
    public List<String> getGVParameterNames(String specname)
    {
        int  versionPos=specname.length()-1;
        char ch;
        
        while ((ch=specname.charAt(versionPos))>='0' && ch<='9') {
            versionPos--;
        }
        
        return getGVParameterNames(
            specname.substring(0,versionPos+1),
            specname.substring(versionPos+1));
    }
    
    /* gibt für einen hbci-gv ("saldo3") die liste aller ll-job-parameter
     * zurück */
    public List<String> getGVParameterNames(String gvname,String version)
    {
        ArrayList<String> ret=new ArrayList<String>();
        Element   gvdef=syntax.getElementById(gvname+version);
        NodeList  gvcontent=gvdef.getChildNodes();
        int       len=gvcontent.getLength();

        boolean first=true;
        for (int i=0;i<len;i++) {
            Node contentref=gvcontent.item(i);

            if (contentref.getNodeType()==Node.ELEMENT_NODE) {
                // skip seghead
                if (first) {
                    first=false;
                } else {
                    addLowlevelProperties(ret,"",(Element)contentref);
                }
            }
        }

        return ret;
    }

    /* gibt für einen hbci-gv ("saldo3") die liste aller ll-job-result-parameter
     * zurück */
    public List<String> getGVResultNames(String specname)
    {
        int  versionPos=specname.length()-1;
        char ch;
        
        while ((ch=specname.charAt(versionPos))>='0' && ch<='9') {
            versionPos--;
        }
        
        return getGVResultNames(
            specname.substring(0,versionPos+1),
            specname.substring(versionPos+1));
    }
    
    /* gibt für einen hbci-gv ("saldo3") die liste aller ll-job-result-parameter
     * zurück */
    public List<String> getGVResultNames(String gvname,String version)
    {
        ArrayList<String> ret=new ArrayList<String>();
        Element   gvdef=syntax.getElementById(gvname+"Res"+version);
        
        if (gvdef!=null) {
            NodeList gvcontent=gvdef.getChildNodes();
            int len=gvcontent.getLength();

            boolean first=true;
            for (int i=0;i<len;i++) {
                Node contentref=gvcontent.item(i);

                if (contentref.getNodeType()==Node.ELEMENT_NODE) {
                    if (first) {
                        first=false;
                    } else {
                        addLowlevelProperties(ret,"",(Element)contentref);
                    }
                }
            }
        }

        return ret;
    }

    /* gibt für einen hbci-gv ("saldo3") die liste aller ll-job-restriction-
     * parameter zurück */
    public List<String> getGVRestrictionNames(String specname)
    {
        int  versionPos=specname.length()-1;
        char ch;
        
        while ((ch=specname.charAt(versionPos))>='0' && ch<='9') {
            versionPos--;
        }
        
        return getGVRestrictionNames(
            specname.substring(0,versionPos+1),
            specname.substring(versionPos+1));
    }
    
    /* gibt für einen hbci-gv ("saldo3") die liste aller ll-job-restriction-
     * parameter zurück */
    public List<String> getGVRestrictionNames(String gvname,String version)
    {
        ArrayList<String> ret=new ArrayList<String>();
        
        // SEGdef id="TermUebPar1" finden
        Element   gvdef=syntax.getElementById(gvname+"Par"+version);
        
        if (gvdef!=null) {
            // alle darin enthaltenen elemente durchlaufen, bis ein element
            // DEG type="ParTermUeb1" gefunden ist
            NodeList gvcontent=gvdef.getChildNodes();
            int len=gvcontent.getLength();

            for (int i=0;i<len;i++) {
                Node contentref=gvcontent.item(i);

                if (contentref.getNodeType()==Node.ELEMENT_NODE) {
                    String type=((Element)contentref).getAttribute("type");
                    if (type.startsWith("Par")) {
                        // wenn ein DEG type="ParTermUeb" gefunden ist, können
                        // alle umgebenenden schleifenvariablen wiederverwendet
                        // werden, weil es nur *ein* solches element geben kann
                        // und die umgebende schleife demzufolge abgebrochen werden
                        // kann, nachdem das gefundenen element bearbeitet wurde
                        
                        // DEGdef id="ParTermUeb1" finden
                        gvdef=syntax.getElementById(type);
                        gvcontent=gvdef.getChildNodes();
                        len=gvcontent.getLength();
                        
                        // darin alle elemente durchlaufen und deren namen
                        // zur ergebnisliste hinzufügen
                        for (i=0;i<len;i++) {
                            contentref=gvcontent.item(i);

                            if (contentref.getNodeType()==Node.ELEMENT_NODE) {
                                addLowlevelProperties(ret,"",(Element)contentref);
                            }
                        }
                        break;
                    }
                }
            }
        }

        return ret;
    }

    private void addLowlevelProperties(ArrayList<String> result,String path,Element ref)
    {
        if (ref.getAttribute("type").length()!=0) {
            if (ref.getNodeName().equals("DE")) {
                String name=ref.getAttribute("name");
                result.add(pathWithDot(path)+name);
            } else {
                String name=ref.getAttribute("name");
                if (name.length()==0)
                    name=ref.getAttribute("type");

                Element  def=syntax.getElementById(ref.getAttribute("type"));
                NodeList defcontent=def.getChildNodes();
                int len=defcontent.getLength();

                for (int i=0;i<len;i++) {
                    Node content=defcontent.item(i);
                    if (content.getNodeType()==Node.ELEMENT_NODE)
                        addLowlevelProperties(result,pathWithDot(path)+name,(Element)content);
                }
            }
        }
    }

    private static String pathWithDot(String path)
    {
        return (path.length()==0)?path:(path+".");
    }
    
    public String get(String key)
    {
        return clientValues.get(key);
    }
}
