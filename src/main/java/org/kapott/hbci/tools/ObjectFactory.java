
/*  $Id: ObjectFactory.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

import java.util.ArrayList;
import java.util.Collection;

public class ObjectFactory 
{
    protected final Collection<Object> freeObjects;
    protected final Collection<Object> usedObjects;
    private   final int        maxPoolSize;
    protected int              currentPoolSize;
    
    public ObjectFactory()
    {
    	this(10);
    }
    
    public ObjectFactory(int maxPoolSize)
    {
        this.freeObjects=new ArrayList<Object>();
        this.usedObjects=new ArrayList<Object>();
        this.maxPoolSize=maxPoolSize;
        this.currentPoolSize=0;
    }
    
    public synchronized Object getFreeObject()
    {
    	if (freeObjects.isEmpty()) {
    	    return null;
        }
        
    	Object ret=freeObjects.iterator().next();
    	freeObjects.remove(ret);
    	currentPoolSize--;
    	// HBCIUtils.log("returning unused object, new: "+toString(),HBCIUtils.LOG_DEBUG);
    	return ret;
    }
    
    public synchronized void addToUsedPool(Object o)
    {
    	// HBCIUtils.log("trying to add object to used pool",HBCIUtils.LOG_DEBUG);
    	if (o!=null && currentPoolSize<maxPoolSize) {
    		usedObjects.add(o);
    		currentPoolSize++;
    		// HBCIUtils.log("ok; new: "+toString(),HBCIUtils.LOG_DEBUG);
    	}
    }
    
    public synchronized void addToFreePool(Object o)
    {
    	// HBCIUtils.log("trying to add object to free pool",HBCIUtils.LOG_DEBUG);
    	if (o!=null && currentPoolSize<maxPoolSize) {
    		freeObjects.add(o);
    		currentPoolSize++;
    		// HBCIUtils.log("ok; new: "+toString(),HBCIUtils.LOG_DEBUG);
    	}
    }
    
    public synchronized void unuseObject(Object o)
    {
    	if (usedObjects.remove(o)) {
    		freeObjects.add(o);
    		// HBCIUtils.log("removed an object from used pool; new: "+toString(),HBCIUtils.LOG_DEBUG);
    	}
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        ret.append("used:").append(usedObjects.size()).append(" free:").append(freeObjects.size()).append(" maxsize:").append(maxPoolSize);
        
        /*
        for (Iterator i=usedObjects.iterator();i.hasNext();) {
            ret.append(System.getProperty("line.separator"));
            
            Object o=i.next();
            if (o instanceof SyntaxElement)
                ret.append("used: "+((SyntaxElement)o).toString(0));
            else if (o instanceof MultipleSyntaxElements)
                ret.append("used: "+((MultipleSyntaxElements)o).toString(0));
            else 
                ret.append("used: "+o.toString());
        }
        */
        
        return ret.toString();
    }
    
    public synchronized Collection<Object> getUsedObjects() 
    {
        return usedObjects;
    }
    
    public synchronized Collection<Object> getFreeObjects()
    {
        return freeObjects;
    }
}
