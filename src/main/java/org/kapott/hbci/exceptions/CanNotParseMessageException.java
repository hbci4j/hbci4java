
/*  $Id: CanNotParseMessageException.java,v 1.1 2011/05/04 22:38:01 willuhn Exp $

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


public final class CanNotParseMessageException  extends HBCI_Exception
{
    private final static boolean PRINTABLE = Boolean.getBoolean("hbci4java.cannotparse.printable");
    
    private String message;
    
    public CanNotParseMessageException(String txt,String message,Exception e)
    {
        super(txt,e);
        this.message=applyLogFilter(message);
    }
    
    /**
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage()
    {
        if (!PRINTABLE)
            return this.message;
        
        // Die Nachricht kann u.U. ellenlanges XML enthalten. Das kann
        // die Konsole fluten. Deswegen geben wir nur maximal die ersten
        // 1000 Zeichen an.
        String msg = this.message;
        if (this.message != null && this.message.length() > 1000)
            msg = this.message.substring(0,1000);
        
        // Ausserdem streichen wir alle nicht druckbaren Zeichen
        msg = msg.replaceAll("\\p{C}", "?");
        return msg;
    }
}
