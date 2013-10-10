
/*  $Id: SyntaxElement.java,v 1.1 2011/05/04 22:38:03 willuhn Exp $

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

package org.kapott.hbci.protocol;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.NoSuchPathException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.protocol.factory.MultipleDEGsFactory;
import org.kapott.hbci.protocol.factory.MultipleDEsFactory;
import org.kapott.hbci.protocol.factory.MultipleSEGsFactory;
import org.kapott.hbci.protocol.factory.MultipleSFsFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* ein syntaxelement ist ein strukturelement einer hbci-nachricht (die nachricht
    selbst, eine segmentfolge, ein einzelnes segment, eine deg oder 
    ein einzelnes de) */
public abstract class SyntaxElement
{
    private List<MultipleSyntaxElements> childContainers;  /**< @internal @brief alle in diesem element enthaltenen unterelemente */
    private String name;   /**< @internal @brief bezeichner fuer dieses element */
    private String type;
    private String path;   /**< @internal @brief pfadname dieses elementes innerhalb einer MSG */
    private char predelim;  /**< @internal @brief nur beim parsen: zeichen, das vor diesem element stehen muesste */
    private boolean valid; /**< @internal @brief indicates if this element is really valid, i.e. will appear in 
                                an outgoing hbci message resp. in returned results from an incoming message*/
    
    private MultipleSyntaxElements parent;
    
    // Wird von einigen Rewriter-Modules beim Parsen verwendet, um im Antwort-String
    // an der richtigen Stelle Daten auszuschneiden oder einzufügen.
    // TODO: Problem dabei ist nur: sobald auch nur *ein* Rewriter die Antwort-
    // Message verändert, stimmen von allen anderen SyntaxElementen die
    // Werte für posInMsg nicht mehr (es sei denn, es wird nach dem 
    // Verändern ein neues MSG-Objekt erzeugt).
    private int posInMsg;
    
    private Document syntax;
    private Node     def;
    
    public final static boolean TRY_TO_CREATE=true;
    public final static boolean DONT_TRY_TO_CREATE=false;
    public final static boolean ALLOW_OVERWRITE=true;
    public final static boolean DONT_ALLOW_OVERWRITE=false;

    /** gibt einen string mit den typnamen (msg,seg,deg,de,...) des 
     elementes zurueck */
    protected abstract String getElementTypeName();
    
    /** liefert das delimiter-zeichen zurueck, dass innerhalb dieses
     syntaxelementes benutzt wird, um die einzelnen child-elemente voneinander
     zu trennen */
    protected abstract char getInDelim();
    
    /** erzeugt einen neuen Child-Container, welcher durch den
    xml-knoten 'ref' identifiziert wird; wird beim erzeugen von elementen
    benutzt */
    protected abstract MultipleSyntaxElements createNewChildContainer(Node ref, Document syntax);
    
    // TODO: aus konsistenz-gründen auch in MultipleSyntaxElements create und
    // createAndAdd trennen
    /** beim parsen: haengt an die 'childElements' ein neues Element an. der
     xml-knoten 'ref' gibt an, um welches element es sich dabei handelt; aus
     'res' (der zu parsende String) wird der wert fuer das element ermittelt
     (falls es sich um ein de handelt); in 'predefined' ist der wert des
     elementes zu finden, der laut syntaxdefinition ('syntax') an dieser stelle
     auftauchen mueste (optional; z.b. fuer segmentcodes); 'predelim*' geben
     die delimiter an, die direkt vor dem zu erzeugenden syntaxelement
     auftauchen muessten */
    protected abstract MultipleSyntaxElements parseNewChildContainer(Node ref, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids);
    
    
    /** wird fuer datenelemente benoetigt, die sonst unbeabsichtigt generiert werden koennten.
        das problem ist, dass es datenelemente (bisher nur bei segmenten bekannt) gibt,
        die aus einigen "required" unterelementen bestehen und aus einigen optionalen
        unterelementen. wenn *alle* "required" elemente bereits durch predefined values
        bzw. durch automatisch generierte werte vorgegeben sind, dann wird das entsprechende
        element erzeugt, da es auch ohne angabe der optionalen unterelemente gueltig ist.
        
        es ist aber u.U. gar nicht beabsichtigt, dass dieses element erzeugt wird (beispiel
        segment "KIOffer", wo nur die DEG SegHead required ist, alle anderen elemente sind
        optional). es kann also vorkommen, dass ein element *unbeabsichtigt* nur aus den
        vorgabedaten erzeugt wird.

        bei den elementen, bei denen das passieren kann, wird in der xml-spezifikation
        deshalb zusaetzlich das attribut "needsRequestTag" angegeben. der wert dieses
        attributes wird hier in der variablen @p needsRequestTag gespeichert.

        beim ueberpruefen, ob das aktuelle element gueltig ist (mittels @c validate() ),
        wird neben der gueltigkeit aller unterelemente zusaetzlich ueberprueft, ob dieses
        element ein request-tag benoetigt, und wenn ja, ob es vorhanden ist. wenn die
        @p needsRequestTag -bedingung nicht erfuellt ist, ist auch das element ungueltig,
        und es wird nicht erzeugt.

        das vorhandensein eines request-tags wird in der variablen @haveRequestTag
        gespeichert. dieses flag kann fuer ein bestimmtes element gesetzt werden, indem
        ihm der wert "requested" zugewiesen wird. normalerweise kann nur DE-elementen
        ein wert zugewiesen werden, diese benoetigen aber kein request-tag. wird also einem
        gruppierenden element der wert "requested" zugewiesen, dann wird das durch die
        methode @c propagateValue() als explizites setzen des @p haveRequestTag
        interpretiert.

        alle klassen und methoden, die also daten fuer die erzeugung von nachrichten
        generieren, muessen u.U. fuer bestimmte syntaxelemente diesen "requested"-wert
        setzen. 

        needsRequestTag kann komplett weg, oder? -- nein. Für GV-Segmente
        gilt das schon. Die Überprüfung des requested-Werted findet aber
        in der *allgemeinen* SyntaxElement-Klasse statt, wo auch andere
        Segmente (z.b. MsgHead) erzeugt werden. Wenn als "allgemeiner"
        Check der Check "if SEG.isRequested" eingeführt werden würde, dann
        würde der nur bei tatsächlich gewünschten GV-Segmenten true ergeben.
        Bei MsgHead-Segmenten z.B. würde er false ergeben (weil diese
        Segmente niemals auf "requested" gesetzt werden). Deshalb darf diese
        "requested"-Überprüfung nur bei den Syntaxelementen stattfinden,
        bei denen das explizit gewünscht ist (needsRequestTag). */
    private boolean needsRequestTag;
    private boolean haveRequestTag;
    
    private void initData(String type, String name, String ppath, int idx, Document syntax)
    {
        if (getElementTypeName().equals("SEG"))
            HBCIUtils.log("creating segment "+ppath+" -> "+name+"("+idx+")", HBCIUtils.LOG_INTERN);
        
        this.type = type;
        this.name = name;
        this.parent=null;
        this.needsRequestTag=false;
        this.haveRequestTag=false;
        this.childContainers = new ArrayList<MultipleSyntaxElements>();
        this.predelim=0;
        this.syntax=syntax;
        this.def=null;
        
        /* der pfad wird gebildet aus bisherigem pfad
         plus name des elementes
         plus indexnummer, falls diese groesser 0 ist */
        StringBuffer temppath=new StringBuffer(128);
        if (ppath!=null && ppath.length()!=0)
            temppath.append(ppath).append(".");
        temppath.append(HBCIUtilsInternal.withCounter(name,idx));
        this.path=temppath.toString();

        setValid(false);

        if (syntax != null) {
            this.def=getSyntaxDef(type,syntax);
            
            // erzeugen der child-elemente
            String requestTag=((Element)def).getAttribute("needsRequestTag");
            if (requestTag!=null && requestTag.equals("1"))
                needsRequestTag=true;

            try {
                int syntaxIdx=0;
                for (Node ref=def.getFirstChild(); ref!=null; ref=ref.getNextSibling()) {
                    if (ref.getNodeType()==Node.ELEMENT_NODE) {
                        MultipleSyntaxElements child=createAndAppendNewChildContainer(ref, syntax);
                        if (child!=null) {
                            child.setParent(this);
                            // TODO: überprüfen, ob noch an anderen Stellen Container
                            // erzeugt werden - diese müssten dann auch die richtige
                            // syntaxIdx bekommen
                            child.setSyntaxIdx(syntaxIdx);
                            
                            if (getElementTypeName().equals("MSG"))
                                HBCIUtils.log("child container "+child.getPath()+" has syntaxIdx="+child.getSyntaxIdx(), HBCIUtils.LOG_INTERN);
                        }
                        syntaxIdx++;
                    }
                }

                /* durchlaufen aller "value"-knoten und setzen der
                 werte der entsprechenden de */
                // TODO: effizienter: das nicht hier machen, sondern später,
                // Wenn wir das *hier* machen, dann werden ja DOCH wieder
                // alle "minnum=0"-Segmente
                // erzeugt, weil für jedes Segment code und version gesetzt
                // werden müssten. Am besten das immer in dem Moment machen,
                // wo ein entsprechendes SyntaxDE erzeugt wird.
                // --> nein, das geht hier. Grund: die optimierte Message-Engine
                // wird nur für Segmentfolgen angewendet. Und in Segmentfolgen-
                // Definitionen sind keine values oder valids angegeben, so dass
                // dieser Code hier gar keine Relevanz für Segmentfolgen hat
                NodeList valueNodes = ((Element)def).getElementsByTagName("value");
                int      len=valueNodes.getLength();
                String   dottedPath = this.path+".";
                for (int i=0; i<len; i++) {
                    Node   valueNode = valueNodes.item(i);
                    String valuePath = ((Element)valueNode).getAttribute("path");
                    String value     = (valueNode.getFirstChild()).getNodeValue();
                    String destpath  = dottedPath+valuePath;
                    
                    if (!propagateValue(destpath,value,TRY_TO_CREATE,DONT_ALLOW_OVERWRITE))
                        throw new NoSuchPathException(destpath);
                }

                /* durchlaufen aller "valids"-knoten und speichern der valid-values */
                // TODO: das hier ebenfalls später machen, siehe "values"
                NodeList validNodes=((Element)def).getElementsByTagName("valids");
                len = validNodes.getLength();
                dottedPath = getPath()+".";
                for (int i=0;i<len;i++) {
                    Node validNode=validNodes.item(i);
                    String valuePath=((Element)(validNode)).getAttribute("path");
                    String absPath=dottedPath+valuePath;

                    NodeList validvalueNodes=((Element)(validNode)).getElementsByTagName("validvalue");
                    int len2=validvalueNodes.getLength();
                    for (int j=0;j<len2;j++) {
                        Node validvalue=validvalueNodes.item(j);
                        String value=(validvalue.getFirstChild()).getNodeValue();

                        storeValidValueInDE(absPath,value);
                    }
                }
            } catch (RuntimeException e) {
                for (Iterator<MultipleSyntaxElements> i=getChildContainers().iterator();i.hasNext();) {
                    MultipleSyntaxElements o=i.next();
                    if (o instanceof MultipleSFs) {
                        MultipleSFsFactory.getInstance().unuseObject(o);
                    } else if (o instanceof MultipleSEGs) {
                        MultipleSEGsFactory.getInstance().unuseObject(o);
                    } else if (o instanceof MultipleDEGs) {
                        MultipleDEGsFactory.getInstance().unuseObject(o);
                    } else {
                        MultipleDEsFactory.getInstance().unuseObject(o);
                    }
                }
                throw e;
            }
        }
    }

    /** es wird ein syntaxelement mit der id 'name' initialisiert; der pfad bis zu
        diesem element wird in 'path' uebergeben; 'idx' ist die nummer dieses
        elementes innerhalb der syntaxelementliste fuer dieses element (falls ein
        bestimmtes syntaxelement mehr als einmal auftreten kann) */
    protected SyntaxElement(String type, String name, String path, int idx, Document syntax)
    {
        initData(type,name,path,idx,syntax);
    }

    protected void init(String type, String name, String path, int idx, Document syntax)
    {
        initData(type,name,path,idx,syntax);
    }
    
    protected MultipleSyntaxElements createAndAppendNewChildContainer(Node ref, Document syntax)
    {
        MultipleSyntaxElements ret=createNewChildContainer(ref,syntax);
        if (ret!=null)
            addChildContainer(ret);
        return ret;
    }

    protected boolean storeValidValueInDE(String destPath,String value)
    {
        boolean ret=false;
        
        for (Iterator<MultipleSyntaxElements> i=childContainers.listIterator(); i.hasNext(); ) {
            MultipleSyntaxElements l = i.next();
            if (l.storeValidValueInDE(destPath, value)) {
                ret=true;
                break;
            }
        }

        return ret;
    }

    /** loop through all child-elements; the segments found there
        will be sequentially enumerated starting with num startValue;
        if startValue is zero, the segments will not be enumerated,
        but all given the number 0

        @param startValue value to be used for the first segment found
        @return next sequence number usable for enumeration */
    public int enumerateSegs(int startValue,boolean allowOverwrite)
    {
        int idx = startValue;

        for (Iterator<MultipleSyntaxElements> i = getChildContainers().iterator(); i.hasNext(); ) {
            MultipleSyntaxElements s = i.next();
            if (s != null)
                idx = s.enumerateSegs(idx,allowOverwrite);
        }

        return idx;
    }

    // -------------------------------------------------------------------------------------------
    
    private void initData(String type, String name, String ppath, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        this.type=type;
        this.name=name;
        this.parent=null;
        this.childContainers = new ArrayList<MultipleSyntaxElements>();
        this.predelim = predelim;
        this.needsRequestTag=false;
        this.haveRequestTag=false;
        this.syntax=syntax;
        this.def=null;
        /* position des aktuellen datenelementes berechnet sich aus der
         * gesamtlänge des ursprünglichen msg-strings minus der länge des
         * reststrings, der jetzt zu parsen ist, und der mit dem aktuellen
         * datenelement beginnt */
        this.posInMsg=fullResLen-res.length(); 

        StringBuffer temppath=new StringBuffer(128);
        if (ppath!=null && ppath.length()!=0)
            temppath.append(ppath).append(".");
        temppath.append(HBCIUtilsInternal.withCounter(name,idx));
        this.path=temppath.toString();

    	setValid(false);

        if (syntax != null) {
            this.def=getSyntaxDef(type,syntax);
            
            /* fuellen der 'predefs'-tabelle mit den in der
             syntaxbeschreibung vorgegebenen werten */
            NodeList valueNodes = ((Element)def).getElementsByTagName("value");
            String dottedPath = getPath() + ".";
            int len=valueNodes.getLength();
            for (int i = 0; i < len; i++) {
                Node valueNode = valueNodes.item(i);
                String valuePath = ((Element)valueNode).getAttribute("path");
                String value = (valueNode.getFirstChild()).getNodeValue();

                predefs.put(dottedPath + valuePath, value);
            }

            if (valids!=null) {
                /* durchlaufen aller "valids"-knoten und speichern der valid-values */
                NodeList validNodes=((Element)def).getElementsByTagName("valids");
                len=validNodes.getLength();
                for (int i=0;i<len;i++) {
                    Node validNode=validNodes.item(i);
                    String valuePath=((Element)(validNode)).getAttribute("path");
                    String absPath=dottedPath+valuePath;
                    
                    NodeList validvalueNodes=((Element)(validNode)).getElementsByTagName("validvalue");
                    int len2=validvalueNodes.getLength();
                    for (int j=0;j<len2;j++) {
                        Node validvalue=validvalueNodes.item(j);
                        String value=(validvalue.getFirstChild()).getNodeValue();
                        valids.put(HBCIUtilsInternal.withCounter(absPath+".value",j),value);
                    }
                }
            }

            try {
                // anlegen der child-elemente
                int counter=0;
                for (Node ref=def.getFirstChild();ref!=null;ref=ref.getNextSibling()) {
                	if (ref.getNodeType()==Node.ELEMENT_NODE) {
                		MultipleSyntaxElements child=parseAndAppendNewChildContainer(ref,
                				((counter++)==0)?predelim:getInDelim(),
                						getInDelim(),
                						res,fullResLen,syntax,predefs,valids);

                		if (child!=null) {
                			child.setParent(this);

                			// TODO: this is a very very dirty hack to fix the problem with the params-template;
                			// bei der SF "Params", die mit <SF type="Params" maxnum="0"/> referenziert wird, 
                			// soll nach jedem erfolgreich in die SF aufgenommenen Param-Segment eine neue
                			// SF begonnen werden, damit das Problem mit dem am Ende der SF stehenden Template-
                			// Param-Segment nicht mehr auftritt
                			// dazu wird beim hinzufuegen von segmenten zur sf ueberprueft, ob diese evtl. bereits
                			// segmente enthaelt (hasValidChilds()). falls das der fall ist, so wird
                			// kein neues segment hinzugefuegt
                			// analoges gilt für die SF "GVRes" - hier muss dafür gesorgt werden, dass jede
                			// antwort in ein eigenes GVRes kommt, damit die zuordnung reihenfolge-erkennung
                			// der empfangenen GVRes-segmente funktioniert (in HBCIJobImpl.fillJobResult())
                			if ((this instanceof SF) && 
                					(getName().equals("Params") || getName().equals("GVRes")) &&
                					((MultipleSEGs)child).hasValidChilds()) {
                				break;
                			}
                		}
                	}
                }
            } catch (RuntimeException e) {
                for (Iterator<MultipleSyntaxElements> i=getChildContainers().iterator();i.hasNext();) {
                    MultipleSyntaxElements o=i.next();
                    if (o instanceof MultipleSFs) {
                        MultipleSFsFactory.getInstance().unuseObject(o);
                    } else if (o instanceof MultipleSEGs) {
                        MultipleSEGsFactory.getInstance().unuseObject(o);
                    } else if (o instanceof MultipleDEGs) {
                        MultipleDEGsFactory.getInstance().unuseObject(o);
                    } else {
                        MultipleDEsFactory.getInstance().unuseObject(o);
                    }
                }
                throw e;
            }
        }

        // if there was no error until here, this syntaxelement is valid
        setValid(true);
    }

    /** beim parsen: initialisiert ein neues syntaxelement mit der id 'name'; in
        'path' wird der pfad bis zu dieser stelle uebergeben 'predelim' gibt das
        delimiter-zeichen an, das beim parsen vor diesem syntax- element stehen
        muesste 'idx' ist die nummer des syntaxelementes innerhalb der
        uebergeordneten liste (die liste repraesentiert das evtl. mehrmalige
        auftreten eines syntaxelementes, siehe class syntaxelementlist) 'res' ist
        der zu parsende String 'predefs' soll eine menge von pfad-wert-paaren
        enthalten, die fuer einige syntaxelemente den wert angeben, den diese
        elemente zwingend haben muessen (z.b. ein bestimmter segmentcode o.ae.) */
    protected SyntaxElement(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        initData(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }
    
    protected void init(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        initData(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }

    protected MultipleSyntaxElements parseAndAppendNewChildContainer(Node ref, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        MultipleSyntaxElements ret=parseNewChildContainer(ref,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
        if (ret!=null)
            addChildContainer(ret);
        return ret;
    }

    /** fuellt die hashtable 'values' mit den werten der de-syntaxelemente; dazu
     wird in allen anderen typen von syntaxelementen die liste der
     child-elemente durchlaufen und deren 'fillValues' methode aufgerufen */
    public void extractValues(Hashtable<String,String> values)
    {
        for (Iterator<MultipleSyntaxElements> i = childContainers.listIterator(); i.hasNext(); ) {
            MultipleSyntaxElements l = i.next();
            l.extractValues(values);
        }
    }
    
    
    // -------------------------------------------------------------------------------------------
    
    protected void addChildContainer(MultipleSyntaxElements x)
    {
        childContainers.add(x);
    }

    /** @return the ArrayList containing all child-elements (the elements
        of the ArrayList are instances of the SyntaxElementArray class */
    public List<MultipleSyntaxElements> getChildContainers()
    {
        return childContainers;
    }

    /** setzt den wert eines de; in allen syntaxelementen ausser DE wird dazu die
     liste der child-elemente durchlaufen; jedem dieser child-elemente wird der
     wert zum setzen uebergeben; genau _eines_ dieser elemente wird sich dafuer
     zustaendig fuehlen (das DE mit 'path'='destPath') und den wert uebernehmen */
    // TODO: code splitten
    public boolean propagateValue(String destPath, String value, boolean tryToCreate,boolean allowOverwrite)
    {
        boolean ret = false;
        
        if (destPath.equals(getPath())) {
            if (value!=null && value.equals("requested"))
                this.haveRequestTag=true;
            else
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_INVVALUE",new Object[] {destPath,value}));
            ret=true;
        } else {
            // damit überspringen wir gleich elemente, bei denen es mit 
            // sicherheit nicht funktionieren kann
            if (destPath.startsWith(getPath())) {                
                for (Iterator<MultipleSyntaxElements> i = childContainers.listIterator(); i.hasNext(); ) {
                    MultipleSyntaxElements l = i.next();
                    if (l.propagateValue(destPath,value,tryToCreate,allowOverwrite)) {
                        ret = true;
                        break;
                    }
                }
                
                if (!ret && tryToCreate) {
                    // der Wert konnte nicht gesetzt werden -> möglicherweise
                    // existiert ja nur der entsprechende child-container noch
                    // nicht
                    HBCIUtils.log(getPath()+": could not set value for "+destPath, HBCIUtils.LOG_INTERN);
                    
                    // Namen des fehlenden Elementes ermitteln
                    String subPath=destPath.substring(getPath().length()+1);
                    HBCIUtils.log("  subpath is "+subPath, HBCIUtils.LOG_INTERN);
                    int dotPos=subPath.indexOf('.');
                    if (dotPos==-1) {
                        dotPos=subPath.length();
                    }
                    String subType=subPath.substring(0,dotPos);
                    HBCIUtils.log("  subname is "+subType, HBCIUtils.LOG_INTERN);
                    int counterPos=subType.indexOf('_');
                    if (counterPos!=-1) {
                        subType=subType.substring(0,counterPos);
                    }
                    HBCIUtils.log("  subType is "+subType, HBCIUtils.LOG_INTERN);
                    
                    // hier überprüfen, ob es wirklich noch keinen child-container
                    // mit diesem Namen gibt. Wenn z.B. der pfad msg.gv.ueb.kik.blz
                    // gesucht wird und msg.gv schon existiert, wird diese methode
                    // hier in msg.gv ausgeführt. wenn sie fehlschlägt (z.b. weil
                    // tatsächlich kein .ueb.kik.blz angelegt werden kann), wird false
                    // ("can not propagate") zurückgegeben. im übergeordneten modul
                    // (msg) soll dann nicht versucht werden, das nächste sub-element
                    // (gv) anzulegen - dieser test merkt, dass es "gv" schon gibt 
                    boolean found=false;
                    for (Iterator<MultipleSyntaxElements> i=childContainers.iterator();i.hasNext();) {
                        MultipleSyntaxElements c= i.next();
                        if (c.getName().equals(subType)) {
                            found=true;
                            break;
                        }
                    }
                    
                    if (!found) {
                        // jetzt durch alle child-elemente des definierenden XML-Knotens
                        // loopen und den ref-Knoten suchen, der das fehlende Element
                        // beschreibt
                        int     newChildIdx=0;
                        Node    ref=null;
                        found=false;
                        for (ref=def.getFirstChild(); ref!=null; ref=ref.getNextSibling()) {
                            if (ref.getNodeType()==Node.ELEMENT_NODE) {
                                String type=((Element)ref).getAttribute("type");
                                String name=((Element)ref).getAttribute("name");
                                if (name.length()==0) {
                                    name=type;
                                }
                                if (name.equals(subType)) {
                                    found=true;
                                    break;
                                }
                                newChildIdx++;
                            }
                        }

                        if (found) {
                            // entsprechenden child-container erzeugen
                            MultipleSyntaxElements child=createNewChildContainer(ref,syntax);
                            child.setParent(this);
                            child.setSyntaxIdx(newChildIdx);

                            if (getElementTypeName().equals("MSG"))
                                HBCIUtils.log("child container "+child.getPath()+" has syntaxIdx="+child.getSyntaxIdx(),
                                        HBCIUtils.LOG_INTERN);

                            // aktuelle child-container-liste durchlaufen und den neu
                            // erzeugten child-container dort richtig einsortieren
                            int newPosi=0;
                            for (Iterator<MultipleSyntaxElements> i=childContainers.iterator(); i.hasNext(); ) {
                                MultipleSyntaxElements c= i.next();
                                if (c.getSyntaxIdx()>newChildIdx) {
                                    // der gerade betrachtete child-container hat einen idx
                                    // größer als den des einzufügenden elementes, also wird
                                    // sich diese position gemerkt und das element hier eingefügt
                                    break;
                                }
                                newPosi++;
                            }
                            HBCIUtils.log("  inserting child container with syntaxIdx "+newChildIdx+" at position "+newPosi,
                                    HBCIUtils.LOG_INTERN);
                            childContainers.add(newPosi,child);
                            
                            // now try to propagate the value to the newly created child
                            ret=child.propagateValue(destPath,value,tryToCreate,allowOverwrite);
                        }
                    } else {
                        HBCIUtils.log("  subtype "+subType+" already existing - will not try to create",
                                HBCIUtils.LOG_INTERN);
                    }
                }
            }
        }
        
        return ret;
    }
    
    /** @return den wert eines bestimmten DE; 
        funktioniert analog zu 'propagateValue' */
    public String getValueOfDE(String path)
    {
        String ret = null;
        
        for (Iterator<MultipleSyntaxElements> i = childContainers.listIterator(); i.hasNext(); ) {
            MultipleSyntaxElements l = i.next();
            ret=l.getValueOfDE(path);
            if (ret!=null) {
                break;
            }
        }

        return ret;
    }

    public String getValueOfDE(String path, int zero)
    {
        String ret = null;
        
        for (ListIterator<MultipleSyntaxElements> i = childContainers.listIterator(); i.hasNext(); ) {
            MultipleSyntaxElements l = i.next();
            ret=l.getValueOfDE(path,0);
            if (ret!=null) {
                break;
            }
        }

        return ret;
    }

    /** @param path path to the element to be returned
        @return the element identified by path */
    public SyntaxElement getElement(String path)
    {        
        SyntaxElement ret = null;

        if (getPath().equals(path)) {
            ret=this;
        } else {
            for (ListIterator<MultipleSyntaxElements> i = childContainers.listIterator(); i.hasNext(); ) {
                MultipleSyntaxElements l = i.next();
                ret=l.getElement(path);
                if (ret!=null) {
                    break;
                }
            }
        }
        
        return ret;
    }

    protected void setPath(String path)
    {
        this.path = path;
    }

    /** @return the path to this element */
    public final String getPath()
    {
        return path;
    }

    protected void setName(String name)
    {
        this.name = name;
    }

    /** @return the name of this element (i.e. the last component of path) */
    public String getName()
    {
        return name;
    }

    protected void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    /** @return the delimiter that must be in front of this element */
    protected char getPreDelim()
    {
        return predelim;
    }
    
    /** @param type the name of the syntaxelement to be returned
        @param syntax the structure containing the current syntaxdefinition
        @return a XML-node with the definition of the requested syntaxelement */
    public final Node getSyntaxDef(String type, Document syntax)
    {
        Node ret = syntax.getElementById(type);
        if (ret == null)
            throw new org.kapott.hbci.exceptions.NoSuchElementException(getElementTypeName(), type);
        return ret;
    }

    /** diese toString() methode wird benutzt, um den wert eines
        de-syntaxelementes in human-readable-form zurueckzugeben. innerhalb eines
        de-elementes wird der wert in der hbci-form gespeichert */
    public String toString(int zero)
    {
        return toString();
    }

    protected final void setValid(boolean valid)
    {
        this.valid = valid;
    }

    public boolean isValid()
    {
        return valid;
    }

    public int checkSegSeq(int value)
    {
        for (Iterator<MultipleSyntaxElements> i=childContainers.iterator();i.hasNext();) {
            MultipleSyntaxElements a= i.next();
            value=a.checkSegSeq(value);
        }

        return value;
    }

    /** ueberpreuft, ob das syntaxelement alle restriktionen einhaelt; ist das
        nicht der fall, so wird eine Exception ausgeloest. die meisten
        syntaxelemente koennen sich nicht selbst ueberpruefen, sondern rufen statt
        dessen die validate-funktion der child-elemente auf */
    public void validate()
    {
        if (!needsRequestTag || haveRequestTag) {
            for (ListIterator<MultipleSyntaxElements> i = childContainers.listIterator(); i.hasNext(); ) {
                MultipleSyntaxElements l = i.next();
                l.validate();
            }

            /* wenn keine exception geworfen wurde, dann ist das aktuelle element
               offensichtlich valid */
            setValid(true);
        }
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
    }
    
    public void setParent(MultipleSyntaxElements parent)
    {
        this.parent=parent;
    }

    public MultipleSyntaxElements getParent()
    {
        return parent;
    }
    
    public int getPosInMsg()
    {
        return posInMsg;
    }
    
    protected void destroy()
    {
        childContainers.clear();
        childContainers=null;
        name=null;
        parent=null;
        path=null;
        type=null;
        syntax=null;
        def=null;
    }
}

