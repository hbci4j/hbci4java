
/*  $Id: Limit.java,v 1.1 2011/05/04 22:37:49 willuhn Exp $

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

package org.kapott.hbci.structures;

public class Limit 
{
    public static final char TYPE_SINGLE='E';
    public static final char TYPE_DAILY='T';
    public static final char TYPE_WEEKLY='W';
    public static final char TYPE_MONTHLY='M';
    public static final char TYPE_TIME='Z';

    public int   type;
    public Value value;
    public int   days;
    
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        switch (type) {
            case TYPE_SINGLE:
                ret.append("Einzellimit");
                break;
            case TYPE_DAILY:
                ret.append("Tageslimit");
                break;
            case TYPE_WEEKLY:
                ret.append("Wochenlimit");
                break;
            case TYPE_MONTHLY:
                ret.append("Monatslimit");
                break;
            case TYPE_TIME:
                ret.append("Zeitliches Limit (").append(days).append(" Tage)");
                break;
        }
        
        ret.append(": ").append(value.toString());
        return ret.toString();
    }
}
