
/*  $Id: ShowCumulatedLowlevelParams.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Dieses Tool ist nur für interne Verwendung bei der Entwicklung von HBCI4Java
 * gedacht. Damit lässt sich prüfen, ob die Highlevel-Klassen für die einzelnen
 * GVs tatsächlich alle möglichen Lowlevel-Parameter eines bestimmten GV
 * kennen und mit richtigen Werten füllen (jeweils abhängig von den bankseitig
 * unterstützten GV-Versionen) */
public class ShowCumulatedLowlevelParams 
{
    private static void extractParams(Document doc, Element node, String path, List<String> params)
    {
        NodeList childs=node.getChildNodes();
        int      l=childs.getLength();
        for (int i=0; i<l; i++) {
            Node child=childs.item(i);
            if (child.getNodeType()==Node.ELEMENT_NODE) {
                Element ref=(Element)child;
                String  refnode=ref.getNodeName();
                String  reftype=ref.getAttribute("type");
                String  refname=ref.getAttribute("name");
                String  refbez=(refname!=null && refname.length()!=0)?refname:reftype;
                
                String localPath=path;
                if (localPath.length()!=0) {
                    localPath+=".";
                }
                localPath+=refbez;

                if (refnode.equals("DE")) {
                    if (!params.contains(localPath)) {
                        params.add(localPath);
                    }
                } else if (refnode.equals("DEG")) {
                    Element def=doc.getElementById(reftype);
                    if (def==null) {
                        // System.out.println("warning: type '"+reftype+"' referenced in '"+localPath+"' not found");
                        String p=localPath+" (+)";
                        if (!params.contains(p)) {
                            params.add(p);
                        }
                    } else {
                        extractParams(doc,def,localPath,params);
                    }
                }
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
        Map<String, List<String>>      paramsByJob=new Hashtable<String, List<String>>();
        for (int i=0; i<l; i++) {
            Element  segdef=(Element)segdefs.item(i);
            String   segdefid=segdef.getAttribute("id");
            
            String   segcode=null;
            String   segversion=null;
            
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
                System.out.println("warning: SEGdef with id "+segdefid+" has no segcode or segversion");
                continue;
            }
            
            String plainJobName=segdefid.substring(0,segdefid.length()-segversion.length());
            List<String>   params= paramsByJob.get(plainJobName);
            if (params==null) {
                params=new ArrayList<String>();
                paramsByJob.put(plainJobName,params);
            }
            extractParams(doc, segdef,"",params);
        }
        
        String[] jobnames=paramsByJob.keySet().toArray(new String[0]);
        Arrays.sort(jobnames);
        l=jobnames.length;
        for (int i=0; i<l; i++) {
            String jobname=jobnames[i];
            System.out.println(jobname+":");
            
            List<String>     params=paramsByJob.get(jobname);
            String[] _params= params.toArray(new String[0]);
            Arrays.sort(_params);
            int l2=_params.length;
            for (int j=0; j<l2; j++) {
                System.out.println("  "+_params[j]);
            }
        }
    }
}
