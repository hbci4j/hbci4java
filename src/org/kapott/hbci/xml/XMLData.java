
/*  $Id: XMLData.java,v 1.1 2011/05/04 22:38:04 willuhn Exp $

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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* contains data information for an xml tree */
public class XMLData
{
    private Document   rootdoc;
    private Map        nodes;   // path -> node
    private Properties values;
    private Map        restrictions;
    private Map        errors;
    
    private boolean createOptionalElements;
    
    public XMLData()
    {
        this.nodes=new Hashtable();
        this.values=new Properties();
        this.restrictions=new Hashtable();
        this.errors=new Hashtable();
    }
    

    public Document getRootDoc()
    {
        return rootdoc;
    }

    public void setRootDoc(Document rootdoc)
    {
        this.rootdoc = rootdoc;
    }

    public void storeNode(String path, Node node)
    {
        this.nodes.put(path, node);
    }
    
    public Node getNodeByPath(String path)
    {
        return (Node)nodes.get(path);
    }

    public void setValue(String key, String value)
    {
        if (value!=null) {
            this.values.setProperty(key, value);
        }
    }
    
    public String getValue(String key)
    {
        return this.values.getProperty(key);
    }
    
    public Enumeration getValueNames()
    {
        return this.values.propertyNames();
    }
    
    public Map getErrors()
    {
        return this.errors;
    }
    
    public Iterator getRestrictionPaths()
    {
        return this.restrictions.keySet().iterator();
    }
    
    public Map getRestrictions(String path)
    {
        return (Map)this.restrictions.get(path);
    }
    
    public void setRestrictions(String path, Map restrictions)
    {
        this.restrictions.put(path, restrictions);
    }
    
    public void setCreateOptionalElements(boolean x)
    {
        this.createOptionalElements=x;
    }
    
    public boolean getCreateOptionalElements()
    {
        return this.createOptionalElements;
    }
}
