
/*  $Id: MultipleSyntaxElements.java,v 1.2 2012/03/06 23:18:26 willuhn Exp $

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

import org.kapott.hbci.exceptions.NoValueGivenException;
import org.kapott.hbci.exceptions.ParseErrorException;
import org.kapott.hbci.exceptions.PredelimErrorException;
import org.kapott.hbci.exceptions.TooMuchElementsException;
import org.kapott.hbci.protocol.factory.DEFactory;
import org.kapott.hbci.protocol.factory.DEGFactory;
import org.kapott.hbci.protocol.factory.SEGFactory;
import org.kapott.hbci.protocol.factory.SFFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* die child-elemente von strukturierten syntaxelementen (msg, seg, sg, deg)
    werden nicht direkt als listenelemente der uebergeordneten
    syntaxelemente abgelegt. statt dessen wird fuer jedes child-element ein
    syntaxelementarray angelegt, die wiederum das oder die childelement(e) 
    enthaelt. hintergrund ist der, dass ein childelement (z.b. eine
    deg ), in einem uebergeordneten element (z.b. einem segment) mehr
    als einmal auftreten kann (wenn in der syntaxbeschreibung das anzahl feld 
    werte groesser 1 enthaelt). da die in der syntaxbeschreibung angegegeben 
    restriktionen fuer dieses syntaxelement fuer jedes auftreten desselben gelten, 
    wird die tatsache der "beliebigen haeufigkeit" in dem syntaxelementarray 
    gekapselt jedes syntaxelement enthaelt also eine liste seiner child-elemente, 
    wobei jedes listenelement ein syntaxelementarray (mit dem ein- oder mehrmaligen 
    auftreten des eigentlichen syntaxelementes) ist. ein syntaxelementarray 
    enthaelt eine menge (1 oder mehr) von syntaxelementen des gleichen typs */
public abstract class MultipleSyntaxElements
{
    private List<SyntaxElement> elements;
    private String path;
    private String name;
    private String type;
    private int minnum;
    private int maxnum;
    private int syntaxIdx; // die Position dieses Container innerhalb
                           // der Syntax-Definition des Eltern-Elementes
    private Document syntax;
    private Node ref;
    private SyntaxElement parent;

    /** erzeugt einen neuen eintrag in der elements liste; dabei wird ein
        syntaxelement erzeugt, das im xml-node ref referenziert wird;
        idx ist die indexnummer des zu erzeugenden syntaxelementes
        innerhalb der elementlist */
    protected abstract SyntaxElement createAndAppendNewElement(Node ref, String path, int idx, Document syntax);

    /** siehe SyntaxElement::parseElementList() */
    protected abstract SyntaxElement parseAndAppendNewElement(Node ref, String path, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids);

    private void initData(Node ref, String path, Document syntax)
    {
        type=((Element)ref).getAttribute("type");
        name=((Element)ref).getAttribute("name");
        if (name.length()==0) {
            name=type;
        }
        
        this.elements=new ArrayList<SyntaxElement>();
        this.parent=null;
        this.syntaxIdx=-1;
        this.ref=ref;
        this.syntax=syntax;

        StringBuffer temppath=new StringBuffer(128);
        if (path!=null && path.length()!=0)
            temppath.append(path).append(".");
        temppath.append(name);
        this.path=temppath.toString();

        String st;

        minnum = 1;
        st = ((Element)ref).getAttribute("minnum");
        if (st.length() != 0)
            minnum = Integer.parseInt(st);

        maxnum = 1;
        st = ((Element)ref).getAttribute("maxnum");
        if (st.length() != 0)
            maxnum = Integer.parseInt(st);

        try {
            // anlegen mindestens eines syntaxelementes
            // warum? würde es nicht auch reichen, NUR den container anzulegen,
            // wenn minnum=0 ist? -- nein, es gibt zu viele "optionale" Elemente,
            // die in Wirklichkeit gar nicht optional sind, aber mit der Option
            // DONT_TRY_TO_CREATE erzeugt werden, so dass sie also nicht angelegt
            // werden würden und somit fehlerhafte Nachrichten die Folge wären.
            SyntaxElement child=createAndAppendNewElement(ref, path, 0, syntax);
            if (child!=null)
                child.setParent(this);
            
            /* erzeugen sovieler syntaxelemente, bis die mindestanzahl
             aus der syntaxdefinition erreicht ist */
            for (int i = 1; i < minnum; i++) {
                child=createAndAppendNewElement(ref, path, i, syntax);
                if (child!=null)
                    child.setParent(this);
            }
        } catch (RuntimeException e) {
            for (Iterator<SyntaxElement> i=getElements().iterator();i.hasNext();) {
                Object o=i.next();
                if (o instanceof SF) {
                    SFFactory.getInstance().unuseObject(o);
                } else if (o instanceof SEG) {
                    SEGFactory.getInstance().unuseObject(o);
                } else if (o instanceof DEG) {
                    DEGFactory.getInstance().unuseObject(o);
                } else {
                    DEFactory.getInstance().unuseObject(o);
                }
            }
            throw e;
        }
    }
    
    /** anlegen eines neuen syntaxelementarrays fuer ein syntaxelement;
        ref ist eine xml-node-referenz auf das syntaxelement */
    protected MultipleSyntaxElements(Node ref, String path, Document syntax)
    {
        initData(ref,path,syntax);
    }

    protected void init(Node ref, String path, Document syntax)
    {
        initData(ref,path,syntax);
    }

    /** siehe SyntaxElement::propagateValue() */
    protected boolean propagateValue(String destPath, String value,boolean tryToCreate,boolean allowOverwrite)
    {
        boolean ret = false;

        if (tryToCreate) {
            int destPathLen=destPath.length();
            int pathLen=path.length();

            if (destPath.startsWith(path) && 
                destPathLen>pathLen &&
                destPath.charAt(pathLen)=='_') {
                
                int nextDot=destPath.indexOf(".",pathLen);
                if (nextDot==-1)
                    nextDot=destPathLen;
                int number=Integer.parseInt(destPath.substring(pathLen+1,nextDot));

                if (number>elements.size()) {
                    String temppath=path.substring(0,path.lastIndexOf("."));

                    for (int i=elements.size();i<number;i++) {
                        SyntaxElement child=createAndAppendNewElement(ref,temppath,i,syntax);
                        if (child!=null)
                            child.setParent(this);
                    }
                }
            }
        }
        
        for (ListIterator<SyntaxElement> i = elements.listIterator(); i.hasNext(); ) {
            SyntaxElement e = i.next();
            String        ePath = e.getPath();
            if (destPath.equals(ePath) || destPath.startsWith(ePath+".")) {
                if (e.propagateValue(destPath, value, tryToCreate,allowOverwrite)) {
                    ret = true;
                    break;
                }
            }
        }

        return ret;
    }

    public void setParent(SyntaxElement parent)
    {
        this.parent=parent;
    }

    public SyntaxElement getParent()
    {
        return parent;
    }
    
    public void setSyntaxIdx(int syntaxIdx)
    {
        this.syntaxIdx=syntaxIdx;
    }
    
    public int getSyntaxIdx()
    {
        return this.syntaxIdx;
    }

    protected boolean storeValidValueInDE(String destPath,String value)
    {
        boolean ret = false;

        for (ListIterator<SyntaxElement> i = elements.listIterator(); i.hasNext(); ) {
            SyntaxElement e= i.next();
            String        ePath=e.getPath();
            if (destPath.equals(ePath) || destPath.startsWith(ePath+".")) {
                if (e.storeValidValueInDE(destPath,value)) {
                    ret=true;
                }
                break;
            }
        }

        return ret;
    }

    /** siehe SyntaxElement::getValue() */
    protected String getValueOfDE(String path)
    {
        String ret = null;

        for (ListIterator<SyntaxElement> i = elements.listIterator(); i.hasNext(); ) {
            SyntaxElement e = i.next();
            String        ePath=e.getPath();
            if (path.equals(ePath) || path.startsWith(ePath+".")) {
                ret=e.getValueOfDE(path);
                break;
            }
        }

        return ret;
    }

    protected String getValueOfDE(String path, int zero)
    {
        String ret = null;

        for (ListIterator<SyntaxElement> i = elements.listIterator(); i.hasNext(); ) {
            SyntaxElement e = i.next();
            String        ePath = e.getPath();
            if (path.equals(ePath) || path.startsWith(ePath+".")) {
                ret=e.getValueOfDE(path,0);
                break;
            }
        }

        return ret;
    }

    protected SyntaxElement getElement(String path)
    {
        SyntaxElement ret=null;

        for (ListIterator<SyntaxElement> i = elements.listIterator(); i.hasNext(); ) {
            SyntaxElement e = i.next();
            String        ePath = e.getPath();
            if (path.equals(ePath) || path.startsWith(ePath+".")) {
                ret=e.getElement(path);
                break;
            }
        }

        return ret;
    }

    protected void validateOneElement(SyntaxElement elem, int idx)
    {
        try {
            elem.validate();
        } catch (NoValueGivenException e) {
            if (idx<minnum || idx+1<elements.size()) {
                throw e;
            }
        }

        if (maxnum>0 && idx>=maxnum)
            throw new TooMuchElementsException(getPath(),idx);
    }

    protected void validate()
    {
        int idx = 0;
        for (ListIterator<SyntaxElement> i = elements.listIterator(); i.hasNext(); ) {
            SyntaxElement e = i.next();
            validateOneElement(e, idx++);
        }
    }

    public void addElement(SyntaxElement x)
    {
        elements.add(x);
    }

    public List<SyntaxElement> getElements()
    {
        return elements;
    }

    public String getPath()
    {
        return path;
    }

    protected String getName()
    {
        return name;
    }

    protected String getType()
    {
        return type;
    }

    protected int enumerateSegs(int startValue,boolean allowOverwrite)
    {
        int idx = startValue;

        for (Iterator<SyntaxElement> i = getElements().iterator(); i.hasNext(); ) {
            SyntaxElement s = i.next();
            if (s != null)
                idx = s.enumerateSegs(idx,allowOverwrite);
        }

        return idx;
    }

    // ---------------------------------------------------------------------------------------------------------------

    private void initData(Node ref, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen,Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        this.ref=null;
        this.syntax=null;
        this.syntaxIdx=-1;
        this.elements = new ArrayList<SyntaxElement>();
        this.type = ((Element)ref).getAttribute("type");
        this.name = ((Element)ref).getAttribute("name");
        if (name.length()==0) {
            this.name=this.type;
        }
        this.parent=null;

        StringBuffer temppath=new StringBuffer(128);
        if (path!=null && path.length()!=0)
            temppath.append(path).append(".");
        temppath.append(name);
        this.path=temppath.toString();

        String st;

        minnum = 1;
        st = ((Element)ref).getAttribute("minnum");
        if (st.length() != 0)
            minnum = Integer.parseInt(st);

        maxnum = 1;
        st = ((Element)ref).getAttribute("maxnum");
        if (st.length() != 0)
            maxnum = Integer.parseInt(st);

        int     idx = 0;
        boolean ready = false;

        try {
            while (!ready) {
                // sichern des reststrings
                StringBuffer save=new StringBuffer(res.toString());
                boolean      emptyElementFound=false;

                try {
                    // versuch, ein weiteres syntaxelement zu erzeugen
                    SyntaxElement child=parseAndAppendNewElement(ref,path, 
                            (idx==0)?predelim0:predelim1, 
                            idx,res,fullResLen,syntax,predefs,valids);
                    if (child!=null)
                        child.setParent(this);
                } catch (ParseErrorException e) {
                  
                    // [willuhn 2012-03-06, BUG 1129] weiterwerfen, wenn sie als fatal eingestuft ist
                    if (e.isFatal())
                      throw e;
                    
                    // wenn das nicht klappt, dann reststring zuruecksetzen, aber nur, 
                    //   wenn naechstes zeichen nicht wieder ein delimiter ist
                    //   dann war naemlich das zu generierende DE leer!!!

                    // charAt(0) ist auf jede fall ein delimiter.
                    // wenn auch charAt(1) ein delimiter ist, dann ist
                    // das dazwischenliegende syntaxelement leer; in diesem
                    // fall muss der vorderste delimiter entfernt werden
                    // (so dass es so aussieht, als wurde das leere syntaxelement
                    // irgendwie richtig geparst)
                    
                    // die exception kann entweder durch einen syntax-fehler oder durch
                    // ein leeres element (was ein spezieller fall eines syntax-fehlers ist)
                    // ausgeloest worden sein.
                    // da das entfernen von leeren elementen optional ist (und manchmal sogar
                    // sinvollerweise gar nicht stattfindet), muessen die exceptions, die wegen
                    // leerer elemente geworfen wurden, als OK akzeptiert werden, solange die
                    // mindestanzahl bereits gefuellter elemente erreicht ist
                    
                    if (save.length()>1) {
                        char secondChar=save.charAt(1);

                        if (secondChar=='+' || secondChar==':' || secondChar=='\'') {
                            // nur wenn der Fehler nicht durch einen predelimiter-error
                            // verursacht wurde, darf der delimiter (der also offensichtlich richtig
                            // und erwartet war) geloescht werden
                            if (!(e instanceof PredelimErrorException)) { 
                                save.deleteCharAt(0);
                            }

                            emptyElementFound=true;
                        }
                    } else {
                        emptyElementFound=true;
                    }
                    
                    res.replace(0,res.length(),save.toString());

                    /* wenn bisher weniger als die mindestanzahl geklappt hat,
                     dann exception werfen */
                    if (idx<minnum)
                        throw new ParseErrorException("reststring in "+getPath()+": "+res.toString(),e);

                    // es wird nur dann aufgehoert, weitere elemente dem aktuellen container hinzu-
                    // zufuegen, wenn ein element gefunden wurde, was offentsichlich nicht mehr dazu-
                    // gehoert (exception, aber nicht leeres element) --> dann stimmt naemlich entweder
                    // der predelimiter nicht, oder die syntax ist falsch
                    if (!emptyElementFound) {
                        ready=true;
                    }
                }

                // anlegen eines neuen synaxelementes hat geklappt, bzw.
                // ein FEHLER beim anlegen kann aufgrund der gegebenen constraints
                // akzeptiert werden (leeres element, aber mindestanzahl erreicht)
                idx++;

                /* wenn die maxanzahl erreicht wurde oder
                 wenn die maxanzahl nicht definiert ist, aber kein neues element
                 erzeugt werden konnte (wenn gesicherter reststring und tatsaechlicher
                 reststring gleich sind; minnum ist aber erreicht),
                 dann diesen container normal beenden */
                if ((maxnum!=0 && idx>=maxnum) ||
                        (maxnum==0 && save.toString().equals(res.toString()) && !emptyElementFound))
                {
                    ready = true;
                }
            }
        } catch (RuntimeException e) {
            for (Iterator<SyntaxElement> i=getElements().iterator();i.hasNext();) {
                SyntaxElement o=i.next();
                if (o instanceof SF) {
                    SFFactory.getInstance().unuseObject(o);
                } else if (o instanceof SEG) {
                    SEGFactory.getInstance().unuseObject(o);
                } else if (o instanceof DEG) {
                    DEGFactory.getInstance().unuseObject(o);
                } else {
                    DEFactory.getInstance().unuseObject(o);
                }
            }
            throw e;
        }
    }

    /** anlegen einer neuen syntaxelementlist beim parsen des strings res;
        - ref ist dabei die referenz auf einen xml-node, der das 
          syntaxelement festlegt, fuer den die syntaxelementlist erzeugt werden soll; 
        - predefs siehe SyntaxElement() 
        - predelim0 ist der delimiter, der vor dem ersten element innerhalb dieser
          syntaxelementlist auftreten muesste; 
        - predelim1 ist der delimiter, der vor dem zweiten, dritten, usw. element in der
          syntaxelementlist auftreten muesste (der unterschied zwischen predelim0 und
          predelim1 ist der, dass predelim0 evtl. von uebergeordneten elementen 
          propagiert wird (z.b. wenn die syntaxelementlist selbst das erste syntaxelement 
          einer msg repraesentiert), predelim1 ist allerdings immer der delimiter, 
          der fuer das aktuell uebergeordnete syntaxelement zu verwenden ist) */
    protected MultipleSyntaxElements(Node ref, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        initData(ref,path,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
    }
    
    protected void init(Node ref, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        initData(ref,path,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
    }
    
    /** siehe SyntaxElement.fillValues() */
    protected void extractValues(Hashtable<String,String> values)
    {
        for (Iterator<SyntaxElement> i = elements.listIterator(); i.hasNext(); ) {
            SyntaxElement e = i.next();
            e.extractValues(values);
        }
    }

    protected int checkSegSeq(int value)
    {
        for (Iterator<SyntaxElement> i=elements.iterator();i.hasNext();) {
            SyntaxElement e= i.next();
            value=e.checkSegSeq(value);
        }

        return value;
    }

    public String toString(int zero)
    {
        return toString();
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
    }
    
    protected void destroy()
    {
        elements.clear();
        elements=null;
        name=null;
        parent=null;
        path=null;
        ref=null;
        syntax=null;
        type=null;
    }
}
