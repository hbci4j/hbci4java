
/*  $Id: ValidateXMLGVTemplate.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** Dieses Tool ist nur für die interne Verwendung für die Entwicklung von
 * HBCI4Java gedacht.
 * Die XML-Dateien mit den HBCI-Syntax-Beschreibungen werden zum Teil automatisch
 * erzeugt. Mit diesem Tool können die dafür benötigten Templates auf Konsistenz
 * geprüft werden. */
public class ValidateXMLGVTemplate 
{
    private static void validateRefs(Element sfelem, List<String> deflist)
    {
        String   sfname=sfelem.getAttribute("id");
        NodeList refs=sfelem.getElementsByTagName("SEG");
        int      l=refs.getLength();
        for (int i=0; i<l; i++) {
            Element ref=(Element)refs.item(i);
            String  refid=ref.getAttribute("type");
            if (!deflist.remove(refid)) {
                System.out.println("warning: in SF '"+sfname+"': referenced SEG '"+refid+"' does not exist");
            }
        }
        if (deflist.size()!=0) {
            for (Iterator<String> i=deflist.iterator(); i.hasNext();) {
                System.out.println("warning: defined "+sfname+" '"+i.next()+"' not referenced in SF '"+sfname+"'");
            }
        }
    }
    
    public static void main(String[] args)
        throws Exception
    {
        DocumentBuilderFactory fac=DocumentBuilderFactory.newInstance();
        fac.setIgnoringComments(true);
        fac.setIgnoringElementContentWhitespace(true);
        fac.setNamespaceAware(false);
        fac.setValidating(false);
        
        DocumentBuilder builder=fac.newDocumentBuilder();
        File            f=new File(args[0]);
        Document        doc=builder.parse(f);
        
        Element  root=doc.getDocumentElement();
        NodeList segdefs=root.getElementsByTagName("SEGdef");
        int      l=segdefs.getLength();
        List<String>     seen=new ArrayList<String>();
        List<String>     gvs=new ArrayList<String>();
        List<String>     gvrs=new ArrayList<String>();
        List<String>     params=new ArrayList<String>();
        for (int i=0; i<l; i++) {
            Element  segdef=(Element)segdefs.item(i);
            String   segdefid=segdef.getAttribute("id");
            
            String segcode=null;
            String segversion=null;
            
            NodeList values=segdef.getElementsByTagName("value");
            int      l2=values.getLength();
            for (int j=0; j<l2; j++) {
                Element value=(Element)values.item(j);
                String  path=value.getAttribute("path");
                if (path.equals("SegHead.code")) {
                    segcode=value.getFirstChild().getNodeValue();
                } else if (path.equals("SegHead.version")) {
                    segversion=value.getFirstChild().getNodeValue();
                }
            }
            
            if (segcode==null || segversion==null) {
                System.out.println("warning: SEGdef with id "+segdefid+" has no segcode or no segversion");
                
                segcode="";
                segversion="";
            } else {
                String key=segcode+":"+segversion;
                if (seen.contains(key)) {
                    System.out.println("warning: segment "+key+" appears more than once");
                } else {
                    seen.add(key);
                }
            }
            
            if (segdefid.endsWith("Res"+segversion)) {
                gvrs.add(segdefid);
            } else if (segdefid.endsWith("Par"+segversion)) {
                params.add(segdefid);
            } else {
                gvs.add(segdefid);
            }
        }
        
        validateRefs(doc.getElementById("GV"), gvs);
        validateRefs(doc.getElementById("GVRes"), gvrs);
        validateRefs(doc.getElementById("Params"), params);
    }
}
