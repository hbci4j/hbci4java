
/*  $Id: SF.java,v 1.1 2011/05/04 22:38:03 willuhn Exp $

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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.protocol.factory.MultipleSEGsFactory;
import org.kapott.hbci.protocol.factory.MultipleSFsFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class SF
     extends SyntaxElement
{
    protected MultipleSyntaxElements createNewChildContainer(Node ref, Document syntax)
    {
        MultipleSyntaxElements ret=null;

        if ((ref.getNodeName()).equals("SEG"))
            ret=MultipleSEGsFactory.getInstance().createMultipleSEGs(ref, getPath(), syntax);
        else if ((ref.getNodeName()).equals("SF"))
            ret=MultipleSFsFactory.getInstance().createMultipleSFs(ref, getPath(), syntax);

        return ret;
    }

    // die optimierte Variante dieser Methode sorgt dafür, dass SegmentFolgen-Container
    // nicht erzeugt werden, wenn die Segmentfolge selbst optional ist. Das ist praktisch
    // nur bei den SFs GV, GVRes und GVParams der Fall (und funktioniert auch nur bei
    // diesen).
    protected MultipleSyntaxElements createAndAppendNewChildContainer(Node ref, Document syntax) 
    {
        MultipleSyntaxElements ret=null;
        
        if (((Element)ref).getAttribute("minnum").equals("0")) {
            HBCIUtils.log("will not create container "+getPath()+" -> "+((Element)ref).getAttribute("type")+" with minnum=0",
                    HBCIUtils.LOG_INTERN);
        } else {
            ret=super.createAndAppendNewChildContainer(ref, syntax);
        }
        
        return ret;
    }

    protected String getElementTypeName()
    {
        return "SF";
    }

    public SF(String type, String name, String path, int idx, Document syntax)
    {
        super(type, name, path, idx, syntax);
    }

    public void init(String type, String name, String path, int idx, Document syntax)
    {
        super.init(type,name,path,idx,syntax);
    }

    public String toString(int zero)
    {
        StringBuffer ret = new StringBuffer(256);

        if (isValid())
            for (ListIterator<MultipleSyntaxElements> i = getChildContainers().listIterator(); i.hasNext(); ) {
                MultipleSyntaxElements list = (i.next());

                if (list != null)
                    ret.append(list.toString(0));
            }

        return ret.toString();
    }

    // -------------------------------------------------------------------------------------------

    public SF(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        super(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }

    public void init(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        super.init(type,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }

    protected char getInDelim()
    {
        return '\'';
    }
    
    // Diese Methode wird von einem dirty hack beim Parsen verwendet. Sie extrahiert
    // den Segment-Code des nächsten zu parsenden Segments aus dem Antwort-String.
    // Stimmt dieser Segment-Code nicht mit dem nächsten eigentlich zu parsenden
    // <SEG type="..." minnum="0"> überein, wird gar nicht erst *versucht*, das
    // dieses <SEG> anzuwenden
    private String[] extractSegId(StringBuffer sb)
    {
        String[] ret=new String[] {"",""};
        
        if (sb.length()>1) {
            int  startpos=0;
            char ch=sb.charAt(0);
            if (ch=='+' || ch==':' || ch=='\'')
                startpos++;
            
            // erste DEG extrahieren
            int endpos=sb.indexOf("+",startpos);
            if (endpos==-1) {
            	endpos=sb.length();
            }
            // code und version aus der ersten DEG extrahieren
            String[] des=sb.substring(startpos,endpos).split(":");
            ret[0] = des[0]; // segcode
            ret[1] = des[2]; // segversion
        }
        
        return ret;
    }
    
    // siehe extractSegCode(). Diese Methode holt sich den SegCode des nächsten
    // mit <SEG ...> referenzierten Segments aus der Syntax-Spez. Der gefundene
    // SegCode wird in HBCIUtils.params gecacht, so dass diese Suche nur einmal
    // erfolgen muss.
    private String[] getRefSegId(Node segref,Document syntax)
    {    	
        String segname=((Element)segref).getAttribute("type");
        String segnameCacheParam="segid_"+segname;
        
        // versuch, daten aus dem cache zu lesen
    	String[] ret=new String[] {"",""};
        ret[0]=HBCIUtils.getParam(segnameCacheParam+"_code","");
        ret[1]=HBCIUtils.getParam(segnameCacheParam+"_version","");
        
        if (ret[0].equals("")) {
        	// segid noch nicht im cache
            Element segdef=syntax.getElementById(segname);
            NodeList valueElems=segdef.getElementsByTagName("value");
            int len=valueElems.getLength();
            for (int i=0;i<len;i++) {
            	// alle value-elemente durchlaufen und seghead.code und 
            	// seghead.version ermitteln
                Node valueNode=valueElems.item(i);
                if (valueNode.getNodeType()==Node.ELEMENT_NODE) {
                    String pathAttr=((Element)valueNode).getAttribute("path");
                    if (pathAttr.equals("SegHead.code")) {
                    	// code gefunden
                        ret[0]=valueNode.getFirstChild().getNodeValue();
                        HBCIUtils.setParam(segnameCacheParam+"_code",ret[0]);
                    } else if (pathAttr.equals("SegHead.version")) {
                    	// version gefunden
                        ret[1]=valueNode.getFirstChild().getNodeValue();
                        HBCIUtils.setParam(segnameCacheParam+"_version",ret[1]);
                    }
                }
            }
        }
        
        return ret;
    }

    protected MultipleSyntaxElements parseNewChildContainer(Node segref, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        MultipleSyntaxElements ret=null;

        if ((segref.getNodeName()).equals("SEG")) {
            ret=MultipleSEGsFactory.getInstance().createMultipleSEGs(segref, getPath(), predelim0, predelim1, res, fullResLen, syntax, predefs,valids);
        } else if ((segref.getNodeName()).equals("SF")) {
            ret=MultipleSFsFactory.getInstance().createMultipleSFs(segref, getPath(), predelim0, predelim1, res, fullResLen, syntax, predefs,valids);
        }

        return ret;
    }

    protected MultipleSyntaxElements parseAndAppendNewChildContainer(Node segref, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        MultipleSyntaxElements ret=null;

        if ((segref.getNodeName()).equals("SEG")) {
            // TODO: this is a hack to speed up parsing of segments
            // (params, customres); das funktioniert so, dass zunächst aus dem zu parsenden
            // string der nächste seghead.code extrahiert wird (string-operationen); außerdem
            // wird ermittelt, wie der seghead.code *des* segmentes ist (segref), als welches das
            // nächste response-token *eigentlich* geparst werden soll. stimmen die beiden codes
            // nicht überein, so kann das nächste response-token mit sicherheit nicht als
            // segref-segment geparst werden, und es wird erst gar nicht versucht.
            // die zuordnung "segref"-->"seghead.code" wird nicht jedesmal neu durch nachsehen
            // in der syntax-spez aufgelöst, sondern es ist ein entsprechender cache
            // implementiert (hashtable:segname-->seghead.code).
            
            String[] nextSegId=extractSegId(res);
            String[] segRefId=getRefSegId(segref,syntax);
            
            if (segRefId[0].equals(nextSegId[0]) && segRefId[1].equals(nextSegId[1])
            		|| segRefId[0].equals("") 
            		|| segRefId[1].equals("")) 
            {
            	// das Segment wird nur geparst, wenn entweder segcode und segversion
            	// mit dem aus der syntax-spez übereinstimmen oder wenn in der syntax-
            	// spez. keine konkreten werte dafür gefunden wurden
                
                /* this is a very ugly hack for the ugly parser code: in certain
                 * cases it may happen that hbci4java takes a HIUPA segment as
                 * a BPD-Params-Template segment. the following code tries to
                 * avoid this, but the solution is not "general". 
                 * TODO: we really should replace the ugly message engine soon! */
                
                boolean parseNext=true;
                if (getName().startsWith("Params")) {
                    /* we are in the BPD-Params SF. only child SEGs with a segcode
                     * of the form ".....S" are allowed here */
                    if ((nextSegId[0].length()!=6) || !nextSegId[0].endsWith("S")) {
                        // this is not a BPD-Param segment
                        parseNext=false;
                    }
                }
                
                if (parseNext) {
                    ret=super.parseAndAppendNewChildContainer(segref,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
                }
            }
        } else if ((segref.getNodeName()).equals("SF")) {
            ret=super.parseAndAppendNewChildContainer(segref,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
        }

        return ret;
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
        if (isValid()) {
            for (Iterator<MultipleSyntaxElements> i=getChildContainers().iterator();i.hasNext();) {
                MultipleSyntaxElements l= i.next();
                if (l!=null) {
                    l.getElementPaths(p,segref,null,null);
                }
            }
        }
    }
    
    public void destroy()
    {
        List<MultipleSyntaxElements> childContainers=getChildContainers();
        for (Iterator<MultipleSyntaxElements> i=childContainers.iterator();i.hasNext();) {
            MultipleSyntaxElements child=i.next();
            if (child instanceof MultipleSFs) {
                MultipleSFsFactory.getInstance().unuseObject(child);
            } else {
                MultipleSEGsFactory.getInstance().unuseObject(child);
            }
        }
        
        super.destroy();
    }
}
