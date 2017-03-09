
/*  $Id: HBCI_Exception.java,v 1.2 2012/03/06 23:18:26 willuhn Exp $

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

package org.kapott.hbci.exceptions;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;


/** Diese Klasse ist die Super-Klasse aller Exceptions, die
    durch den HBCI-Kernel erzeugt werden. Beim Auftreten einer
    solchen Exception sollten die Messages der gesamten(!)
    Exception-Kette angezeigt werden, um die Fehlerursache
    bestmöglich bestimmen zu können.
    <pre>
try {
    // hier HBCI-Zeugs machen
} catch (HBCI_Exception e) {
    Throwable e2=e;
    String msg;
    
    System.out.println("HBCI-Exception:");
    while (e2!=null) {
        if ((msg=e2.getMessage())!=null) {
            System.out.println(msg);
        }
        e2=e2.getCause();
    }
}
    </pre> */
public class HBCI_Exception
    extends RuntimeException
{
    private boolean fatal = false;
  
    protected static String applyLogFilter(String st) 
    {
        try {
            int filterLevel=Integer.parseInt(HBCIUtils.getParam("log.filter","2"));
            if (filterLevel!=0) {
                st=LogFilter.getInstance().filterLine(st,filterLevel);
            }
        } catch (Exception e) {
            System.out.println("strange exception: "+e);
        }
        return st;
    }
    
    /** Erzeugen einer neuen HBCI_Exception ohne Message und
        ohne Cause */
    public HBCI_Exception()
    {
        super();
    }
    
    /** Erzeugen einer neuen HBCI_Exception mit bestimmter
        Message 
        @param s Message, die bei <code>getMessage()</code> zurückgegeben werden soll*/
    public HBCI_Exception(String s)
    {
        super(applyLogFilter(s));
    }
    
    /** Erzeugen einer neuen HBCI_Exception mit bestimmtem Cause.
        Die Message, die in dieser Exception gespeichert wird, ist
        auf jeden Fall leer 
        @param e "Ursache" dieser Exception, die in der Exception-Kette als
               <code>getCause()</code> zurückgegeben werden soll */
    public HBCI_Exception(Throwable e)
    {
        super(null,e);
    }
    
    /** Erzeugen einer neuen HBCI_Exception mit gegebener Message und Cause 
        @param st Message, die bei <code>getMessage()</code> zurückgegeben werden soll 
        @param e "Ursache" dieser Exception, die in der Exception-Kette als
               <code>getCause()</code> zurückgegeben werden soll */
    public HBCI_Exception(String st,Throwable e)
    {
        super(applyLogFilter(st),e);
    }
    
    /**
     * Markiert eine Exception als fatal.
     * @param b true, wenn sie fatal ist.
     */
    public void setFatal(boolean b)
    {
      this.fatal = b;
    }
    
    /**
     * Liefert true, wenn die Exception oder ihr Cause als fatal eingestuft wurde.
     * @return true, wenn die Exception oder ihr Cause als fatal eingestuft wurde.
     */
    public boolean isFatal()
    {
      if (this.fatal) // dann brauchen wir den Cause nicht mehr checken
        return true;
      
      Throwable t = this.getCause();
      if (t == this)
        return false; // sind wir selbst
      if (t instanceof HBCI_Exception)
        return ((HBCI_Exception)t).isFatal();
      
      return false;
    }
}
