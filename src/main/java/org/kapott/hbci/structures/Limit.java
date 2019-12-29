/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **********************************************************************/

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
