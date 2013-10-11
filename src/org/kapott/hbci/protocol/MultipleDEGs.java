
/*  $Id: MultipleDEGs.java,v 1.1 2011/05/04 22:38:03 willuhn Exp $

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

import org.kapott.hbci.protocol.factory.DEGFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class MultipleDEGs
     extends MultipleSyntaxElements
{
    private char delimiter;

    protected SyntaxElement createAndAppendNewElement(Node ref, String path, int idx, Document syntax)
    {
        SyntaxElement ret=null;

        addElement((ret=DEGFactory.getInstance().createDEG(getType(), getName(), path, idx, syntax)));
        return ret;
    }

    private void initData(Node degref, char delimiter, String path, Document syntax)
    {
        this.delimiter = delimiter;
    }
    
    public MultipleDEGs(Node degref, char delimiter, String path, Document syntax)
    {
        super(degref, path, syntax);
        initData(degref,delimiter,path,syntax);
    }

    public void init(Node degref, char delimiter, String path, Document syntax)
    {
        super.init(degref, path, syntax);
        initData(degref,delimiter,path,syntax);
    }

    public String toString(int zero)
    {
        StringBuffer ret=new StringBuffer(128);
        boolean first = true;

        for (ListIterator<SyntaxElement> i = getElements().listIterator(); i.hasNext(); ) {
            if (!first)
                ret.append(delimiter);
            first=false;

            DEG deg = (DEG)(i.next());
            if (deg != null)
                ret.append(deg.toString(0));
        }

        return ret.toString();
    }

    // --------------------------------------------------------------------------------------------------------------

    protected SyntaxElement parseAndAppendNewElement(Node ref, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        SyntaxElement ret=null;
        addElement((ret=DEGFactory.getInstance().createDEG(getType(), getName(), path, predelim, idx, res, fullResLen, syntax, predefs,valids)));
        return ret;
    }
    
    private void initData(Node degref, char delimiter, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<?, ?> predefs,Hashtable<?, ?> valids)
    {
        this.delimiter = delimiter;
    }

    public MultipleDEGs(Node degref, char delimiter, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        super(degref, path, predelim0, predelim1, res, fullResLen, syntax, predefs,valids);
        initData(degref,delimiter,path,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
    }

    public void init (Node degref, char delimiter, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen,Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        super.init(degref, path, predelim0, predelim1, res, fullResLen, syntax, predefs,valids);
        initData(degref,delimiter,path,predelim0,predelim1,res,fullResLen,syntax,predefs,valids);
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
        if (getElements().size()!=0) {
            for (Iterator<SyntaxElement> i=getElements().iterator();i.hasNext();) {
                SyntaxElement e=i.next();
                if (e!=null) {
                    e.getElementPaths(p,segref,degref,deref);
                }
            }
        } else {
            if (deref==null) {
                p.setProperty(Integer.toString(segref[0])+
                              ":"+Integer.toString(degref[0]),getPath());
                degref[0]++;
            } else {
                p.setProperty(Integer.toString(segref[0])+
                              ":"+
                              Integer.toString(degref[0])+
                              ","+
                              Integer.toString(deref[0]),
                              getPath());
                deref[0]++;
            }
        }
    }
    
    public void destroy()
    {
        List<SyntaxElement> children=getElements();
        for (Iterator<SyntaxElement> i=children.iterator();i.hasNext();) {
            DEGFactory.getInstance().unuseObject(i.next());
        }
        
        super.destroy();
    }
}
