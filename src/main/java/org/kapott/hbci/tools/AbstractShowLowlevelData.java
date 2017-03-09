
/*  $Id: AbstractShowLowlevelData.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Basisklasse für die beiden <code>ShowLowlevelGV*</code>-Tools.
    Diese Klasse wird nur intern verwendet. */
public class AbstractShowLowlevelData
{
    protected static void showData(String gvname,Document syntax,int minLevel)
    {
        int len=gvname.length();
        int versionpos=-1;
        
        for (int i=len-1;i>=0;i--) {
            char ch=gvname.charAt(i);
            if (!(ch>='0' && ch<='9')) {
                versionpos=i+1;
                break;
            }
        }
                   
        System.out.println("jobname:"+gvname.substring(0,versionpos)+" version:"+gvname.substring(versionpos));
        
        Element  gvdef=syntax.getElementById(gvname);
        NodeList gvcontent=gvdef.getChildNodes();
        len=gvcontent.getLength();
        
        boolean first=true;
        for (int i=0;i<len;i++) {
            Node contentref=gvcontent.item(i);
            
            if (contentref.getNodeType()==Node.ELEMENT_NODE) {
                if (first) {
                    first=false;
                } else {
                    displayContentRef("",(Element)contentref,syntax,2,0,minLevel);
                }
            }
        }
    }
    
    protected static void displayContentRef(String path,Element ref,Document syntax,int indent,int currentLevel,int minLevel)
    {
        if (ref.getAttribute("type").length()!=0) {
            StringBuffer spaces=new StringBuffer();

            for (int i=0;i<indent;i++)
                spaces.append(" ");

            if (ref.getNodeName().equals("DE")) {
                if (currentLevel>=minLevel) {
                    String name=ref.getAttribute("name");

                    String datatype=ref.getAttribute("type");

                    String minlen=ref.getAttribute("minsize");
                    String maxlen=ref.getAttribute("maxsize");

                    int minnum=1;
                    int maxnum=1;

                    String st=ref.getAttribute("minnum");
                    if (st.length()!=0)
                        minnum=Integer.parseInt(st);

                    st=ref.getAttribute("maxnum");
                    if (st.length()!=0)
                        maxnum=Integer.parseInt(st);

                    System.out.println(spaces+pathWithDot(path)+name+":"+datatype+
                            ((minlen.length()!=0)?(" min:"+minlen):"")+
                            ((maxlen.length()!=0)?(" max:"+maxlen):"")+
                            " {"+minnum+","+maxnum+"}");
                }
            } else {
                String nextPath=path;
                int    nextIndent=indent;

                if (currentLevel>=minLevel) {
                    String name=ref.getAttribute("name");
                    if (name.length()==0)
                        name=ref.getAttribute("type");
                    
                    int minnum=1;
                    int maxnum=1;

                    String st=ref.getAttribute("minnum");
                    if (st.length()!=0)
                        minnum=Integer.parseInt(st);

                    st=ref.getAttribute("maxnum");
                    if (st.length()!=0)
                        maxnum=Integer.parseInt(st);

                    System.out.println(spaces+"GROUP:"+name+" {"+minnum+","+maxnum+"}");
                    
                    nextIndent+=2;
                    nextPath=pathWithDot(path)+name;
                }

                Element  def=syntax.getElementById(ref.getAttribute("type"));
                NodeList defcontent=def.getChildNodes();
                int      len=defcontent.getLength();

                for (int i=0;i<len;i++) {
                    Node content=defcontent.item(i);
                    if (content.getNodeType()==Node.ELEMENT_NODE)
                        displayContentRef(
                                nextPath,(Element)content,syntax,
                                nextIndent,currentLevel+1,minLevel);
                }
            }
        }
    }
    
    protected static void showData(String gvname,Document syntax)
    {
        showData(gvname, syntax, 0);
    }

    protected static String pathWithDot(String path)
    {
        return (path.length()==0)?path:(path+".");
    }
}
