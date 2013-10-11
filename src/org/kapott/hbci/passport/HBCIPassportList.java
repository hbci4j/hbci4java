
/*  $Id: HBCIPassportList.java,v 1.1 2011/05/04 22:37:43 willuhn Exp $

    This file is part of hbci4java
    Copyright (C) 2001-2008  Stefan Palme

    hbci4java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    hbci4java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.passport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* This class represents a collection of passports that will be used for
 * multi-signatures. Each entry of this collection consists of a passport
 * object and a string describing the role for that passport. When adding
 * entries to this list, the add() method automatically removes any duplicates.*/
public class HBCIPassportList
{
    /* one entry of the passport-collection consists of passport and role */
    private static class Entry 
    {
        private HBCIPassportInternal passport;
        private String               role;
        
        public Entry(HBCIPassportInternal passport,String role)
        {
            this.passport=passport;
            this.role=role;
        }
        
        public HBCIPassportInternal getPassport()
        {
            return passport;
        }
        
        public String getRole()
        {
            return role;
        }
    }
    
    private List<Entry> passports;
    
    public HBCIPassportList()
    {
        this.passports=new ArrayList<Entry>();
    }
    
    /* check whether a certain passport object is already in list */
    private boolean contains(HBCIPassportInternal passport)
    {
        boolean ret=false;
        
        for (Iterator<Entry> i=passports.iterator();i.hasNext();) {
            if (i.next().getPassport()==passport) {
                ret=true;
                break;
            }
        }
        
        return ret;
    }
    
    private void addPassport(Entry entry)
    {
        addPassport(entry.getPassport(),entry.getRole());
    }
    
    /* add a new entry to this list */
    public void addPassport(HBCIPassportInternal passport,String role)
    {
        if (!contains(passport)) {
            Entry entry=new Entry(passport,role);
            passports.add(entry);
        }
    }
    
    /* add all entries from another passportlist to this list */
    public void addAll(HBCIPassportList passportList) 
    {
        for (Iterator<Entry> i=passportList.iterator();i.hasNext();) {
            addPassport(i.next());
        }
    }
    
    /* return the main-passports, which is always the first one */
    public HBCIPassportInternal getMainPassport()
    {
        return getPassport(0);
    }
    
    public HBCIPassportInternal getPassport(int idx)
    {
        return passports.get(idx).getPassport();
    }
    
    public String getRole(int idx)
    {
        return passports.get(idx).getRole();
    }
    
    private Iterator<Entry> iterator()
    {
        return passports.iterator();
    }
    
    public int size()
    {
        return passports.size();
    }
    
    public void clear()
    {
        passports.clear();
    }
}
