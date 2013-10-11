
/*  $Id: MultipleSEGs.java,v 1.1 2011/05/04 22:38:02 willuhn Exp $

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

import org.kapott.hbci.protocol.factory.SEGFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class MultipleSEGs
     extends MultipleSyntaxElements
{
    protected SyntaxElement createAndAppendNewElement(Node ref, String path, int idx, Document syntax)
    {
        SyntaxElement ret=null;
        addElement((ret=SEGFactory.getInstance().createSEG(getType(), getName(), path, idx, syntax)));
        return ret;
    }

    public MultipleSEGs(Node segref, String path, Document syntax)
    {
        super(segref, path, syntax);
    }

    public void init(Node segref, String path, Document syntax)
    {
        super.init(segref, path, syntax);
    }

    public String toString(int zero)
    {
        StringBuffer ret = new StringBuffer(256);

        for (ListIterator<SyntaxElement> i = getElements().listIterator(); i.hasNext(); ) {
            SEG seg = (SEG)(i.next());
            if (seg != null)
                ret.append(seg.toString(0));
        }

        return ret.toString();
    }

    // ---------------------------------------------------------------------------------------------------------------

    public MultipleSEGs(Node segref, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        super(segref, path, predelim0, predelim1, res, fullResLen, syntax, predefs,valids);
    }

    public void init(Node segref, String path, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        super.init(segref, path, predelim0, predelim1, res, fullResLen, syntax, predefs,valids);
    }

    protected SyntaxElement parseAndAppendNewElement(Node ref, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        SyntaxElement ret=null;
        addElement((ret=SEGFactory.getInstance().createSEG(getType(), getName(), path, predelim, idx, res, fullResLen, syntax, predefs,valids)));
        return ret;
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
        for (Iterator<SyntaxElement> i=getElements().iterator();i.hasNext();) {
            SyntaxElement e= i.next();
            if (e!=null) {
                e.getElementPaths(p,segref,degref,deref);
            }
        }
    }
    
    // TODO: diese Methode gehört zu einem dirty hack (der aber gut funktioniert)
    // Diese Methode wird von SyntaxElement.parse() verwendet, um bei den
    // SFs "Params" und "GVRes" dafür zu sorgen, dass nach jedem gefunden Segment
    // eine neue SF begonnen wird:
    //   Params.LastPar1
    //   Params_2.UebPar1
    //   Params_3.KUmsPar1
    // anstatt
    //   Params.LastPar1
    //   Params.UebPar1
    //   Params_2.KUmsPar1
    public boolean hasValidChilds()
    {
        return (getElements().size()!=0);
    }
    
    public void destroy()
    {
        List<SyntaxElement> children=getElements();
        for (Iterator<SyntaxElement> i=children.iterator();i.hasNext();) {
            SEGFactory.getInstance().unuseObject(i.next());
        }
        
        super.destroy();
    }
}
