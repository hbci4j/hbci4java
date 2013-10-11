
/*  $Id: SyntaxDEFactory.java,v 1.1 2011/05/04 22:38:01 willuhn Exp $

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

package org.kapott.hbci.datatypes.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.kapott.hbci.datatypes.SyntaxDE;
import org.kapott.hbci.exceptions.InitializingException;
import org.kapott.hbci.exceptions.NoSuchConstructorException;
import org.kapott.hbci.exceptions.NoSuchSyntaxException;
import org.kapott.hbci.exceptions.ParseErrorException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.tools.ObjectFactory;

public class SyntaxDEFactory 
{
    private static SyntaxDEFactory instance;
    
    private Hashtable<String, ObjectFactory> factories;
    
    public static synchronized SyntaxDEFactory getInstance()
    {
        if (instance==null) {
            instance=new SyntaxDEFactory();
        }
        return instance;
    }
    
    private SyntaxDEFactory()
    {
        factories=new Hashtable<String, ObjectFactory>();
    }
    
    public SyntaxDE createSyntaxDE(String dataType,String path,String value,int minsize,int maxsize)
    {
        SyntaxDE ret=null;
        ObjectFactory factory;
        
        synchronized(this) {
        	factory=factories.get(dataType);

        	if (factory==null) {
        		factory=new ObjectFactory(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.Syntax","1024")));
        		factories.put(dataType,factory);
        	}
        }
        
        ret=(SyntaxDE)factory.getFreeObject();
        if (ret==null) {
            // laden der klasse, die die syntax des de enthaelt
            Class c;
            try {
                c=Class.forName("org.kapott.hbci.datatypes.Syntax"+dataType,false,this.getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new NoSuchSyntaxException(dataType,path);
            }

            // holen des constructors fuer diese klasse
            Constructor con;
            try {
                con=c.getConstructor(new Class[]{String.class, int.class, int.class});
            } catch (NoSuchMethodException e) {
                throw new NoSuchConstructorException(dataType);
            }

            /* anlegen einer neuen instanz der syntaxklasse und initialisieren
             mit dem uebergebenen wert */
            try {
                ret=(SyntaxDE)(con.newInstance(new Object[]{value, new Integer(minsize), new Integer(maxsize)}));
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
                throw new InitializingException((Exception)e.getCause(),path);
            }
            
            if (ret!=null) {
                factory.addToUsedPool(ret);
            }
        } else {
            try {
                ret.init(value,minsize,maxsize);
                factory.addToUsedPool(ret);
            } catch (RuntimeException e) {
                factory.addToFreePool(ret);
                throw new InitializingException(e,path);
            }
        }
        
        return ret;
    }

    public SyntaxDE createSyntaxDE(String dataType,String path,StringBuffer res,int minsize,int maxsize)
    {
        SyntaxDE      ret=null;
        ObjectFactory factory;
        
        synchronized(this) {
        	factory=factories.get(dataType);

        	if (factory==null) {
        		factory=new ObjectFactory(Integer.parseInt(HBCIUtils.getParam("kernel.objpool.Syntax","1024")));
        		factories.put(dataType,factory);
        	}
        }
        
        ret=(SyntaxDE)factory.getFreeObject();
        if (ret==null) {
            // laden der klasse, die die syntax des de enthaelt
            Class c;
            try {
                c=Class.forName("org.kapott.hbci.datatypes.Syntax"+dataType,false,this.getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new NoSuchSyntaxException(dataType,path);
            }

            // holen des constructors fuer diese klasse
            Constructor con;
            try {
                con=c.getConstructor(new Class[]{StringBuffer.class, int.class, int.class});
            } catch (NoSuchMethodException e) {
                throw new NoSuchConstructorException(dataType);
            }

            /* anlegen einer neuen instanz der syntaxklasse und initialisieren
             mit dem uebergebenen wert */
            try {
                ret=(SyntaxDE)(con.newInstance(new Object[]{res, new Integer(minsize), new Integer(maxsize)}));
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
                throw new ParseErrorException(HBCIUtilsInternal.getLocMsg("EXCMSG_PROT_ERRSYNDE",path),(Exception)e.getCause());
            }
            
            if (ret!=null) {
                factory.addToUsedPool(ret);
            }
        } else {
            try {
                ret.init(res,minsize,maxsize);
                factory.addToUsedPool(ret);
            } catch (RuntimeException e) {
                factory.addToFreePool(ret);
                throw new ParseErrorException(HBCIUtilsInternal.getLocMsg("EXCMSG_PROT_ERRSYNDE",path),(Exception)e.getCause());
            }
        }
        
        return ret;
    }
    
    public void unuseObject(SyntaxDE sde,String type)
    {
        if (sde!=null) {
            sde.destroy();
            ObjectFactory fac=factories.get(type);
            fac.unuseObject(sde);
        }
    }
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        for (Enumeration<String> e=factories.keys();e.hasMoreElements();) {
            String        type=e.nextElement();
            ObjectFactory fac=factories.get(type);
            
            ret.append(type).append(": ").append(fac.toString()).append(System.getProperty("line.separator"));
        }
        
        return ret.toString().trim();
    }
}
