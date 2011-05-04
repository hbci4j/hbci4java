
/*  $Id: XMLCreator2.java,v 1.1 2011/05/04 22:38:04 willuhn Exp $

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

package org.kapott.hbci.xml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/* Schema enthält Typ-Definitionen und Deklarationen von Elementen und Attributen.
 * 
 * Die Typen (simple, complex) bilden eine Hierarchie mit EINER Wurzel. Jede Typ-
 * Definition (mit Ausnahme der Wurzel) ist entweder eine "restriction" oder eine
 * "extension" eines anderen (vor-)definierten Types.  
 * 
 * SimpleTypes beschreiben gültige Attribut- oder Element-Werte, indem sie einen
 * existierenden primitive Datentypen (int, string, date) als Basis verwenden 
 * und ggfs. den möglichen Wertebereich einschränken (restrictions von SimpleTypes).
 * 
 * ComplexTypes sind restrictions/extensions von anderen ComplexTypes oder 
 * extensions von SimpleTypes.
 * 
 * <xs:restriction base="" id="">
 *   min/max-In/Ex-clusive, totalDigits, fractionDigits, length, max/max-Length, enumeration, whiteSpace, pattern
 * </xs:restriction>
 * 
 * <xs:simpleType final="" id="" name="">
 *   <xs:annotation>?
 *   (    restriction
 *      | list
 *      | union
 *   )
 * </xs:simpleType>
 * 
 * <xs:complexType abstract="" block="" final="" id="" mixed="" name="">
 *   <xs:annotation>?
 *           simpleContent 
 *         | complexContent
 *         | ((  group
 *             | all 
 *             | choice
 *             | sequence
 *            )?
 *            ((  attribute
 *              | attributeGroup
 *             )*
 *             anyAttribute?
 *            )
 *           )
 * </xs:complexType>
 * 
 * <xs:element maxOccurs="" minOccurs="" name="" 
 *             abstract="" block="" default="" final="" fixed="" form="" id="" nillable="" ref="" substituionGroup="" type="">
 *   <xs:annotation>?
 *   (<xs:simpleType>|<xs:complexType)?
 *   (<xs:unique>|<xs:key>|<xs:keyref>)*
 * </xs:element>
 * 
 * <xs:attribute name="" type="" 
 *               default="" fixed="" form="" id="" ref="" use="">
 *   <xs:annotation>?
 *   <xs:simpleType>?
 * </xs:attribute>
 *   
 * 
 * Pflicht-Elemente und -Attribute werden IMMER erzeugt. Wird in einem Element
 * ein Wert erwartet, wird dieser aus dem values-Property-Objekt entnommen.
 * Optionale Elemente werden u.U. nicht angelegt. Vor dem Anlegen eines optionalen
 * Elementes wird im values-Objekt nachgesehen, ob dort irgendein Wert mit einem
 * Pfad innerhalb des neu zu erzeugenden optionalen Elementes definiert ist.
 * Ist das der Fall, wird das Element erzeugt (weil es dann ja auch mindestens
 * einen Wert innerhalb dieses Elementes gibt). Wird kein value gefunden, der
 * auf den Sub-Baum des optionalen Elementes passt, wird das Element nicht erzeugt.
 */
public class XMLCreator2
{
    public static String uriSchema = "http://www.w3.org/2001/XMLSchema";

    private DocumentBuilderFactory fac;
    private Element                schemaroot;
    private Map                    types;

    
    public XMLCreator2(InputStream schemaStream)
    {
        try {
            // schema-beschreibung einlesen
            this.fac = DocumentBuilderFactory.newInstance();
            this.fac.setIgnoringComments(true);
            this.fac.setIgnoringElementContentWhitespace(true);
            this.fac.setNamespaceAware(true);
            this.fac.setValidating(false);

            DocumentBuilder builder = fac.newDocumentBuilder();
            Document schemadoc = builder.parse(schemaStream);

            this.schemaroot = schemadoc.getDocumentElement();

            // erst mal alle Typ-Definitionen in einer Hashtabelle abspeichern
            loadTypes("simpleType");
            loadTypes("complexType");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    private void loadTypes(String elemName)
    {
        if (types == null) {
            types = new Hashtable();
        }

        NodeList elements = schemaroot.getElementsByTagNameNS(uriSchema,
                elemName);
        int l = elements.getLength();
        for (int i = 0; i < l; i++) {
            Element elem = (Element) elements.item(i);
            String typeName = elem.getAttribute("name");
            if (types.get(typeName) != null) {
                throw new RuntimeException("duplicate definition of type "
                        + typeName);
            }
            types.put(typeName, elem);
        }
        // System.out.println("loaded " + l + " types of type " + elemName);
    }

    
    private void warnUnimplemented(Element elem, String[] implAttrs,
            String[] implChilds)
    {
        String elemName = elem.getNodeName();
        List _implAttrs = Arrays.asList(implAttrs);
        List _implChilds = Arrays.asList(implChilds);

        NamedNodeMap attrs = elem.getAttributes();
        int l = attrs.getLength();
        for (int i = 0; i < l; i++) {
            Attr attr = (Attr) attrs.item(i);
            String attrName = attr.getName();
            if (!_implAttrs.contains(attrName)) {
                String attrValue = attr.getValue();
                HBCIUtils.log("warning: attribute " + attrName + "="
                        + attrValue + " of element " + elemName
                        + " not yet implemented",
                        HBCIUtils.LOG_WARN);
            }
        }

        NodeList childs = elem.getChildNodes();
        l = childs.getLength();
        for (int i = 0; i < l; i++) {
            Node node = childs.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) node;
                String childName = child.getLocalName();
                if (!_implChilds.contains(childName)) {
                    String childFullName = child.getNodeName();
                    HBCIUtils.log("warning: child " + childFullName
                            + " of element " + elemName
                            + " not yet implemented",
                            HBCIUtils.LOG_WARN);
                }
            }
        }
    }

    
    /* erzeugt einen pfad parent/elem_idx */
    private String composePath(String parent, String elem, int counter)
    {
        StringBuffer path=new StringBuffer();
        if (parent!=null && parent.length()!=0) {
            path.append(parent);
            path.append("/");
        }
        path.append(HBCIUtilsInternal.withCounter(elem, counter));
        return path.toString();
    }
    
    
    private boolean subTreeHasValues(XMLData xmldata, String prefix)
    {
        boolean found=false;
        
        for (Enumeration e=xmldata.getValueNames(); e.hasMoreElements(); ) {
            String key=(String)e.nextElement();
            if ((key+"/").startsWith(prefix+"/")) {
                found=true;
                break;
            }
        }
        
        return found;
    }
    
    
    private void rememberMissingValue(XMLEntity target, XMLData xmldata)
    {
        Map errors=xmldata.getErrors();
        Set missings=(Set)errors.get("missings");
        
        if (missings==null) {
            missings=new HashSet();
            errors.put("missings", missings);
        }
        
        missings.add(target);
    }
    

    private Text getOrCreateTextNode(Document rootdoc, Element elem)
    {
        Text     ret=null;
        NodeList childs=elem.getChildNodes();
        int      l=childs.getLength();
        for (int i=0; i<l; i++) {
            Node node=childs.item(i);
            if (node.getNodeType()==Node.TEXT_NODE) {
                ret=(Text)node;
                break;
            }
        }

        if (ret==null) {
            ret=rootdoc.createTextNode("");
            elem.appendChild(ret);
        }

        return ret;
    }
    
    
    private String getNodeValue(Node node, Document rootdoc)
    {
        String result="";
        
        if (node instanceof Element) {
            Text text=getOrCreateTextNode(rootdoc, (Element)node);
            result=text.getData();
        } else if (node instanceof Attr) {
            Attr attr=(Attr)node;
            result=attr.getValue();
        } else {
            throw new RuntimeException("node must be an Element or an Attribute");
        }
        
        return result;
    }
    

    private void handleEnumeration(XMLEntity target, Element schemaElem, XMLData xmldata)
    {
        warnUnimplemented(schemaElem, 
            new String[] {"value"}, 
            new String[] {"annotation"});

        String enumvalue=schemaElem.getAttribute("value");

        Map myRestrictions=xmldata.getRestrictions(target.getPath());
        if (myRestrictions==null) {
            myRestrictions=new Hashtable();
            xmldata.setRestrictions(target.getPath(), myRestrictions);
        }

        List valids=(List)myRestrictions.get("valids");
        if (valids==null) {
            valids=new ArrayList();
            myRestrictions.put("valids",valids);
        }

        valids.add(enumvalue);
    }
    

    private void handleMinLength(XMLEntity target, Element schemaElem, XMLData xmldata)
    {
        warnUnimplemented(schemaElem, 
            new String[] {"value"}, 
            new String[] {"annotation"});

        String minsize=schemaElem.getAttribute("value");

        Map myRestrictions=xmldata.getRestrictions(target.getPath());
        if (myRestrictions==null) {
            // TODO: extract to class XMLData
            myRestrictions=new Hashtable();
            xmldata.setRestrictions(target.getPath(), myRestrictions);
        }

        myRestrictions.put("minsize", minsize);
    }
    

    private void handleMaxLength(XMLEntity target, Element schemaElem, XMLData xmldata)
    {
        warnUnimplemented(schemaElem, 
            new String[] {"value"}, 
            new String[] {"annotation"});

        String maxsize=schemaElem.getAttribute("value");

        Map myRestrictions=xmldata.getRestrictions(target.getPath());
        if (myRestrictions==null) {
            // TODO: extract to class XMLData
            myRestrictions=new Hashtable();
            xmldata.setRestrictions(target.getPath(), myRestrictions);
        }

        myRestrictions.put("maxsize", maxsize);
    }
    

    private void handleMinInclusive(XMLEntity target, Element schemaElem, XMLData xmldata)
    {
        warnUnimplemented(schemaElem, 
            new String[] {"value"}, 
            new String[] {"annotation"});

        String minvalue=schemaElem.getAttribute("value");

        Map myRestrictions=xmldata.getRestrictions(target.getPath());
        if (myRestrictions==null) {
            // TODO: extract to class XMLData
            myRestrictions=new Hashtable();
            xmldata.setRestrictions(target.getPath(), myRestrictions);
        }

        myRestrictions.put("minvalue", minvalue);
    }
    

    private void handleMaxInclusive(XMLEntity target, Element schemaElem, XMLData xmldata)
    {
        warnUnimplemented(schemaElem, 
            new String[] {"value"}, 
            new String[] {"annotation"});

        String maxvalue=schemaElem.getAttribute("value");

        Map myRestrictions=xmldata.getRestrictions(target.getPath());
        if (myRestrictions==null) {
            // TODO: extract to class XMLData
            myRestrictions=new Hashtable();
            xmldata.setRestrictions(target.getPath(), myRestrictions);
        }

        myRestrictions.put("maxvalue", maxvalue);
    }
    

    private void handlePattern(XMLEntity target, Element schemaElem, XMLData xmldata)
    {
        warnUnimplemented(schemaElem, 
            new String[] {"value"}, 
            new String[] {"annotation"});

        String pattern=schemaElem.getAttribute("value");

        Map myRestrictions=xmldata.getRestrictions(target.getPath());
        if (myRestrictions==null) {
            // TODO: extract to XMLData
            myRestrictions=new Hashtable();
            xmldata.setRestrictions(target.getPath(), myRestrictions);
        }

        List patterns=(List)myRestrictions.get("patterns");
        if (patterns==null) {
            patterns=new ArrayList();
            myRestrictions.put("patterns",patterns);
        }

        patterns.add(pattern);
    }
    

    /* schemaRest ist ein restriction-Element, welches das aktuelle Element (target)
     * näher spezifiziert */
    private void handleRestriction(XMLEntity target, Element schemaRest, XMLData xmldata)
    {
        warnUnimplemented(schemaRest, 
            new String[] {"base"}, 
            new String[] {"annotation","enumeration","fractionDigits","maxInclusive","maxLength","minInclusive","minLength","pattern","totalDigits"});
        
        String baseType=schemaRest.getAttribute("base");
        handleType(target,baseType,xmldata);

        NodeList childs=schemaRest.getChildNodes();
        int      l=childs.getLength();
        for (int i=0; i<l; i++) {
            Node node=childs.item(i);
            if (node.getNodeType()==Node.ELEMENT_NODE) {
                Element child=(Element)node;
                String  childName=child.getLocalName();
                
                // TODO: handle fractionDigits and totalDigits
                if (childName.equals("enumeration")) {
                    handleEnumeration(target,child,xmldata);
                } else if (childName.equals("minLength")) {
                    handleMinLength(target,child,xmldata);
                } else if (childName.equals("maxLength")) {
                    handleMaxLength(target,child,xmldata);
                } else if (childName.equals("pattern")) {
                    handlePattern(target,child,xmldata);
                } else if (childName.equals("minInclusive")) {
                    handleMinInclusive(target,child,xmldata);
                } else if (childName.equals("maxInclusive")) {
                    handleMaxInclusive(target,child,xmldata);
                }
            }
        }
    }
    

    private void handleAttribute(XMLEntity parent, Element schemaAttr, XMLData xmldata) 
    {
        warnUnimplemented(schemaAttr, 
            new String[] {"name","type","use"}, 
            new String[] {"annotation"});
        
        // TODO: handle value of "use"

        // name of the element
        String    attrName=schemaAttr.getAttribute("name");
        String    attrPath=parent.getPath()+":"+attrName;
        Attr      attr=xmldata.getRootDoc().createAttribute(attrName);
        parent.getElement().setAttributeNode(attr);
        xmldata.storeNode(attrPath, attr);

        XMLEntity entity=new XMLEntity(attr, attrPath);
        String    type=schemaAttr.getAttribute("type");
        handleType(entity, type, xmldata);
    }

    
    /* schemaExt ist ein extension-Element, welches das aktuelle Element (target)
     * näher spezifiziert */
    private void handleExtension(XMLEntity target, Element schemaExt, XMLData xmldata)
    {
        warnUnimplemented(schemaExt, 
            new String[] {"base"}, 
            new String[] {"annotation","attribute"});

        String baseType=schemaExt.getAttribute("base");
        handleType(target,baseType,xmldata);

        NodeList childs=schemaExt.getChildNodes();
        int      l=childs.getLength();
        for (int i=0; i<l; i++) {
            Node node=childs.item(i);
            if (node.getNodeType()==Node.ELEMENT_NODE) {
                Element child=(Element)node;
                String  childName=child.getLocalName();
                
                if (childName.equals("attribute")) {
                    handleAttribute(target,child,xmldata);
                }
            }
        }
    }
    

    /* Element/Attribut "target" existiert bereits. Die
     * Typ-Beschreibung des Objektes steht im Element "schemaType" und muss nun
     * abgearbeitet werden. Es handelt sich um einen simpleType */
    private void handleSimpleType(XMLEntity target, Element schemaType, XMLData xmldata)
    {
        warnUnimplemented(schemaType, 
            new String[] {"name"}, 
            new String[] {"restriction"});

        // TODO: handle restrictions
        NodeList simpleChilds=schemaType.getChildNodes();
        int      l=simpleChilds.getLength();
        for (int i=0; i<l; i++) {
            Node node=simpleChilds.item(i);

            if (node.getNodeType()==Node.ELEMENT_NODE) {
                Element simpleChild=(Element)node;
                String  simpleChildType=simpleChild.getLocalName();

                if (simpleChildType.equals("restriction")) {
                    handleRestriction(target,simpleChild,xmldata);
                }
            }
        }
    }
    

    /* Unterhalb vom Element "target" muss genau eines der in der choice aufgeführten
     * elemente erzeugt werden. Dafür werden alle Kinder der Choice durchlaufen.
     * sobald ein Objekt erzeugt wurde, können wir abbrechen. */
    private void handleChoice(XMLEntity target, Element schemaChoice, XMLData xmldata)
    {
        warnUnimplemented(schemaChoice, 
            new String[] {}, 
            new String[] {"element"});
        
        Element createdChild=null;

        NodeList choiceChilds=schemaChoice.getChildNodes();
        int      l=choiceChilds.getLength();
        for (int i=0; i<l; i++) {
            Node node=choiceChilds.item(i);
            
            if (node.getNodeType()==Node.ELEMENT_NODE) {
                Element choiceChild=(Element)node;
                String  choiceChildType=choiceChild.getLocalName();

                if (choiceChildType.equals("element")) {
                    String subName=choiceChild.getAttribute("name");
                    String subPath=composePath(target.getPath(), subName, 0);
                    if (subTreeHasValues(xmldata, subPath)) {
                        // wenn es für die gerade betrachtete choice einen user-value
                        // gibt, erzeugen wir das betreffende child und brechen dann ab
                        createdChild=handleElement(target,choiceChild,xmldata);
                        break;
                    }
                }
            }
        }
        
        if (createdChild==null) {
            HBCIUtils.log(
                    "no choice for children of "+target.getPath()+" choosen", 
                    HBCIUtils.LOG_WARN);
        }
    }
    

    /* Unterhalb vom Element "target" muss eine Sequenz weiterer Elemente erzeugt
     * werden. "schemaSeq" ist das Schema-Element, welches die Sequenz beschreibt */
    private void handleSequence(XMLEntity target, Element schemaSeq, XMLData xmldata)
    {
        warnUnimplemented(schemaSeq, 
            new String[] {"name"}, 
            new String[] {"choice","element"});

        NodeList seqChilds=schemaSeq.getChildNodes();
        int      l=seqChilds.getLength();
        for (int i=0; i<l; i++) {
            Node node=seqChilds.item(i);
            
            if (node.getNodeType()==Node.ELEMENT_NODE) {
                Element seqChild=(Element)node;
                String  seqChildType=seqChild.getLocalName();

                if (seqChildType.equals("element")) {
                    handleElement(target,seqChild,xmldata);
                } else if (seqChildType.equals("choice")) {
                    handleChoice(target,seqChild,xmldata);
                }
            }
        }
    }
    

    /* Element/Attribut "target" existiert bereits. Die
     * Typ-Beschreibung des Objektes steht im Element "schemaType" und muss nun
     * abgearbeitet werden.  */
    private void handleSimpleContent(XMLEntity target, Element schemaType, XMLData xmldata)
    {
        warnUnimplemented(schemaType, 
            new String[] {}, 
            new String[] {"extension", "restriction"});

        NodeList children=schemaType.getChildNodes();
        int      l=children.getLength();
        for (int i=0; i<l; i++) {
            Node node=children.item(i);
            
            if (node.getNodeType()==Node.ELEMENT_NODE) {
                Element child=(Element)node;
                String  childType=child.getLocalName();

                if (childType.equals("restriction")) {
                    handleRestriction(target, child, xmldata);
                } else if (childType.equals("extension")) {
                    handleExtension(target, child, xmldata);
                }
            }
        }
    }


    /* Element/Attribut "target" existiert bereits. Die
     * Typ-Beschreibung des Objektes steht im Element "schemaType" und muss nun
     * abgearbeitet werden. Es handelt sich um einen complexType */
    private void handleComplexType(XMLEntity target, Element schemaType, XMLData xmldata)
    {
        warnUnimplemented(schemaType, 
            new String[] {"name"}, 
            new String[] {"sequence","simpleContent"});

        NodeList complexChilds=schemaType.getChildNodes();
        int      l=complexChilds.getLength();
        for (int i=0; i<l; i++) {
            Node node=complexChilds.item(i);
            
            if (node.getNodeType()==Node.ELEMENT_NODE) {
                Element complexChild=(Element)node;
                String  complexChildType=complexChild.getLocalName();

                if (complexChildType.equals("sequence")) {
                    handleSequence(target, complexChild, xmldata);
                } else if (complexChildType.equals("simpleContent")) {
                    handleSimpleContent(target, complexChild, xmldata);
                }
            }
        }
    }


    /* ein Element/Attribut "target" existiert schon.
     * Der Typ dieses Elements/Attributes ist "typeName". Hier wird jetzt die
     * entsprechende Typ-Definition gesucht und das Element/Attribut entsprechend
     * modifiziert (Werte setzen, childs erzeugen, ...) */
    private void handleType(XMLEntity target, String typeName, XMLData xmldata)
    {
        if (typeName==null || typeName.length()==0) {
            throw new RuntimeException("Element description for '"+target+"' has no type attribute");
        }

        Element typeDescr=(Element)types.get(typeName);
        if (typeDescr==null) {
            // es handelt sich um einen typen, der nicht explizit definiert wird
            // evtl. ist es ein vordefinierter typ...

            String[] nameParts=typeName.split(":",2);
            String   localName=nameParts[nameParts.length-1];
            if (localName.equals("string") || localName.equals("dateTime") || 
                    localName.equals("date") || localName.equals("decimal") ||
                    localName.equals("boolean")) 
            {
                // hier den richtigen wert anhand von targetPath aus der wertemenge holen und eintragen
                String value=xmldata.getValue(target.getPath());
                if (value==null) {
                    /* wenn kein User-Value vorhanden ist, merken wir uns erst mal, dass hier
                     * noch was fehlt. evtl. kann der fehlende wert ja aus den restrictions
                     * ermittelt werden (z.b. bei einer enumeration mit nur einem element) */
                    rememberMissingValue(target, xmldata);
                    value="TODO: "+target.getPath()+":"+localName;
                }
                
                Node node=target.getNode();
                if (node instanceof Attr) {
                    ((Attr)node).setValue(value);
                } else if (node instanceof Element) {
                    Node txt=xmldata.getRootDoc().createTextNode(value);
                    ((Element)node).appendChild(txt);
                } else {
                    throw new RuntimeException("target must be of type Attr or Element");
                }
            } else {
                throw new RuntimeException("type '"+typeName+"' used for element "+target+" not in table of types");
            }

        } else {
            // es handelt sich um einen datentypen, der im schema definiert wird
            String typeType=typeDescr.getLocalName();

            if (typeType.equals("simpleType")) {
                handleSimpleType(target, typeDescr, xmldata);
            } else {
                handleComplexType(target, typeDescr, xmldata);
            }
        }
    }
    

    /* in schemaElement wird das zu erzeugende Element benannt ("name") und beschrieben ("type").
     * Außerdem ist dort festgelegt, wie OFT das Element vorkommen kann (min/max-Occurs).
     * Das Element wird also erzeugt (0..n mal) und als child von parent hinzugefügt.
     * Anschließend wird der Typ des Elementes gesucht und gehandelt, was dazu führt,
     * dass am Element die entsprechenden Attribute gesetzt und/oder child-Elemente
     * erzeugt werden. */
    private Element handleElement(XMLEntity parent, Element schemaElem, XMLData xmldata) 
    {
        Element result=null;
        
        warnUnimplemented(schemaElem, 
            new String[] {"minOccurs","maxOccurs","name","type"}, 
            new String[] {"annotation"});

        // min/max bestimmen
        String min_s=schemaElem.getAttribute("minOccurs");
        int    min=1;
        if (min_s.length()!=0) {
            min=Integer.parseInt(min_s);
        }

        String max_s=schemaElem.getAttribute("maxOccurs");
        int    max=0;
        if (max_s.length()==0) {
            max=1;
        } else if (!max_s.equals("unbounded")) {
            max=Integer.parseInt(max_s);
        }

        // name of the element
        String elemName=schemaElem.getAttribute("name");
        int    counter=0;
        
        while (true) {
            // calculate path for new element
            String elemPath=composePath(parent!=null?parent.getPath():null, elemName, counter);
            
            if (counter>=min && !subTreeHasValues(xmldata, elemPath)) {
                /* element muss nur erzeugt werden, wenn entweder min noch nicht erreicht
                 * ist oder es einen wert für das optionale element gibt. in letzterem
                 * fall muss ein fehler erzeugt werden, wenn der counter schon größer
                 * als max ist. 
                 * wir brechen also die schleife ab, wenn wir schon mindestens min
                 * elemente erzeugt haben und fuer den aktuellen counter-wert keine
                 * daten finden. */
                
                /* noch eine spezieller debug-fall: wenn xmldata.getCreateOptionalElements()
                 * gesetzt ist, dann erzeugen wir zumindest EIN optionales element */
                if (counter!=0 || !xmldata.getCreateOptionalElements()) {
                    /* wir brechen also nur dann ab, wenn wir entweder schon beim
                     * nicht-ersten optionalen element sind. wenn wir beim
                     * ersten optionalen element sind, brechen wir nur dann ab,
                     * wenn wir keine optionalen elemente erzeugen wollen */
                    break;
                }
            }
            
            if (max!=0 && counter>=max) {
                // TODO: too many values
                HBCIUtils.log(
                        "found values for "+elemPath+" in data, but max is "+max,
                        HBCIUtils.LOG_ERR);
                break;
            }
            
            // create element and add it to parent element
            Element elem=xmldata.getRootDoc().createElement(elemName);
            xmldata.storeNode(elemPath, elem);
            if (parent!=null) {
                parent.getElement().appendChild(elem);
            }
            if (result==null) {
                // das erste erzeugte Element wird der Return-Wert der Methode
                result=elem;
            }

            // den Daten-Typen des frischen Elementes "target" holen (ist entweder
            // ein simpleType oder ein complexType)
            String typeName=schemaElem.getAttribute("type");
            handleType(new XMLEntity(elem,elemPath), typeName, xmldata);
            
            counter++;
        }
        
        return result;
    }

    
    public void createXMLFromSchemaAndData(XMLData xmldata, OutputStream outStream)
    {
        try {
            // dieses Element ist ein xsd:element-Knoten, der das erste target-element beschreibt
            NodeList elements=schemaroot.getElementsByTagNameNS(uriSchema,"element");
            if (elements.getLength()==0) {
                throw new RuntimeException("there is no top level 'element' declaration in the schema");
            }
            Element rootDescr=(Element)elements.item(0);

            // targetdoc ist das target-document
            DocumentBuilder builder2=this.fac.newDocumentBuilder();
            Document        targetdoc=builder2.newDocument();
            xmldata.setRootDoc(targetdoc);
            
            // beginnend beim wurzel-elemente alle childs erzeugen
            Element targetroot=handleElement(null, rootDescr, xmldata);
            
            targetroot.setAttribute("xmlns",              "urn:sepade:xsd:pain.001.001.02");
            targetroot.setAttribute("xmlns:xsi",          "http://www.w3.org/2001/XMLSchema-instance");
            targetroot.setAttribute("xsi:schemaLocation", "urn:sepade:xsd:pain.001.001.02 pain.001.001.02.xsd");

            // restrictions validieren

            // alle "valids" prüfen - wenn es nur einen valid gibt, diesen
            // automatisch setzen
            for (Iterator i=xmldata.getRestrictionPaths(); i.hasNext(); ) {
                String path=(String)i.next();
                Node   node=xmldata.getNodeByPath(path);
                if (node==null) {
                    // node does not exist - so restrictions are irrelevant
                    continue;
                }
                
                Map myRestrictions=xmldata.getRestrictions(path);

                // check for valids
                List   valids=(List)myRestrictions.get("valids");
                if (valids!=null) {
                    // TODO: wenn schon ein wert existiert, diesen gegen valids checken

                    // wenn noch kein wert existiert und len(valids)==1,
                    // dann diesen einen wert automatisch setzen;
                    // das ganze für "element" und "attribute"
                    if (valids.size()==1) {
                        // TODO: hier noch überprüfen, ob nicht schon
                        // ein evtl. falscher Wert drin steht
                        
                        String value=(String)valids.get(0);
                        // TODO: das nach setNodeValue() auslagern 
                        if (node instanceof Element) {
                            Text txt=getOrCreateTextNode(targetdoc,(Element)node);
                            txt.setData(value);
                        } else if (node instanceof Attr) {
                            ((Attr)node).setValue(value);
                        } else {
                            throw new RuntimeException("node must be an Element or an Attr");
                        }
                    }
                }
                
                // TODO: validate minsize
                String minsize_st=(String)myRestrictions.get("minsize");
                if (minsize_st!=null) {
                    String value=getNodeValue(node, targetdoc);
                    if (value.length()<Integer.parseInt(minsize_st)) {
                        HBCIUtils.log(
                                "value of "+path+" is too short (minsize="+minsize_st+")",
                                HBCIUtils.LOG_ERR);
                    }
                }

                // TODO: validate maxsize
                String maxsize_st=(String)myRestrictions.get("maxsize");
                if (maxsize_st!=null) {
                    String value=getNodeValue(node, targetdoc);
                    if (value.length()>Integer.parseInt(maxsize_st)) {
                        HBCIUtils.log(
                                "value of "+path+" is too long (maxsize="+maxsize_st+")",
                                HBCIUtils.LOG_ERR);
                    }
                }
                
                // TODO: validate minvalue
                String minvalue_st=(String)myRestrictions.get("minvalue");
                if (minvalue_st!=null) {
                    Double minvalue=new Double(minvalue_st);
                    String value_st=getNodeValue(node, targetdoc);
                    if (value_st!=null && value_st.length()!=0) {
                        try {
                            Double value=new Double(value_st);
                            if (value.doubleValue() < minvalue.doubleValue()) {
                                HBCIUtils.log(
                                        "value of "+path+" is too small (minvalue="+minvalue_st+")",
                                        HBCIUtils.LOG_ERR);
                            }
                        } catch (Exception e) {
                            HBCIUtils.log(
                                    value_st+" is not a numeric value",
                                    HBCIUtils.LOG_ERR);
                        }
                    }
                }

                // TODO: validate maxvalue
                String maxvalue_st=(String)myRestrictions.get("maxvalue");
                if (maxvalue_st!=null) {
                    Double maxvalue=new Double(maxvalue_st);
                    String value_st=getNodeValue(node, targetdoc);
                    if (value_st!=null && value_st.length()!=0) {
                        try {
                            Double value=new Double(value_st);
                            if (value.doubleValue() > maxvalue.doubleValue()) {
                                HBCIUtils.log(
                                        "value of "+path+" is too large (maxvalue="+maxvalue_st+")",
                                        HBCIUtils.LOG_ERR);
                            }
                        } catch (Exception e) {
                            HBCIUtils.log(
                                    value_st+" is not a numeric value",
                                    HBCIUtils.LOG_ERR);
                        }
                    }
                }

                // TOOD: validate patterns
                List   patterns=(List)myRestrictions.get("patterns");
                if (patterns!=null) {
                    String  value=getNodeValue(node, targetdoc);
                    boolean ok=false;
                    for (Iterator j=patterns.iterator(); j.hasNext(); ) {
                        String pattern=(String)j.next();
                        if (value.matches(pattern)) {
                            ok=true;
                            break;
                        }
                    }
                    if (!ok) {
                        HBCIUtils.log(
                                "value of "+path+" does not match any pattern",
                                HBCIUtils.LOG_ERR);
                    }
                }
            }
            
            // alle errors durchlaufen
            
            // missings durchsehen - evtl. wurde ja einige inzwischen durch eindeutige "valids" repariert
            Set missings=(Set)xmldata.getErrors().get("missings");
            for (Iterator i=missings.iterator(); i.hasNext(); ) {
                XMLEntity e=(XMLEntity)i.next();
                Node      node=e.getNode();
                boolean   empty=true;
                
                // check whether the value of this element is still empty
                if (node instanceof Element) {
                    Text text=getOrCreateTextNode(targetdoc, (Element)node);
                    if (text.getLength()!=0) {
                        empty=false;
                    }
                } else if (node instanceof Attr) {
                    Attr attr=(Attr)node;
                    if (attr.getValue().length()!=0) {
                        empty=false;
                    }
                }
                
                if (empty) {
                    // TODO: hard error
                    HBCIUtils.log("missing value for "+e.getPath(), HBCIUtils.LOG_ERR);
                }
            }

            // targetdocument rausschreiben
            TransformerFactory tfac=TransformerFactory.newInstance();
            Transformer        trans=tfac.newTransformer();

            trans.setOutputProperty("encoding", "UTF-8");
            trans.setOutputProperty("indent", "yes");
            // trans.setOutputProperty("standalone", "yes");
            trans.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

            /*
            System.out.println("transformer properties:");
            Properties p=trans.getOutputProperties();
            for (Enumeration e=p.propertyNames(); e.hasMoreElements(); ) {
                String key=(String)e.nextElement();
                String value=p.getProperty(key);
                System.out.println("  "+key+"="+value);
            }
            */

            Source tsrc=new DOMSource(targetroot);
            Result tdst=new StreamResult(outStream);
            trans.transform(tsrc,tdst);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws Exception
    {
        FileInputStream f = new FileInputStream(args[0]);
        XMLCreator2 creator = new XMLCreator2(f);
        
        XMLData xmldata=new XMLData();
        /*
        xmldata.setValue("Document/pain.001.001.02/GrpHdr/MsgId",                              "Message-ID-4711");
        xmldata.setValue("Document/pain.001.001.02/GrpHdr/CreDtTm",                            "2008-05-11T09:30:47.000Z");
        xmldata.setValue("Document/pain.001.001.02/GrpHdr/NbOfTxs",                            "1");
        xmldata.setValue("Document/pain.001.001.02/GrpHdr/InitgPty/Nm",                        "Stefan Palme");
        
        xmldata.setValue("Document/pain.001.001.02/PmtInf/Dbtr/Nm",                            "Stefan Palme");
        xmldata.setValue("Document/pain.001.001.02/PmtInf/DbtrAgt/FinInstnId/BIC",             "MY-BIC");
        xmldata.setValue("Document/pain.001.001.02/PmtInf/DbtrAcct/Id/IBAN",                   "MY-IBAN");
        
        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/Cdtr/Nm",                "Der Empfaenger");
        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/CdtrAgt/FinInstnId/BIC", "OTHER-BIC");
        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/CdtrAcct/Id/IBAN",       "OTHER-IBAN");
        
        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/Amt/InstdAmt",           "1.23");
        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/Amt/InstdAmt:Ccy",       "EUR");
        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/RmtInf/Ustrd",           "Verwendungszweck");
        xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/PmtId/EndToEndId",       "NOTPROVIDED");
        
        xmldata.setValue("Document/pain.001.001.02/PmtInf/ReqdExctnDt",                        "1999-01-01"); // hart kodiert
        */

        // TODO: remove this in production code
        xmldata.setCreateOptionalElements(true);
        
        creator.createXMLFromSchemaAndData(xmldata, System.out);
    }
}
