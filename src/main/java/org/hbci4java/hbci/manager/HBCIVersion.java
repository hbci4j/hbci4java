/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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

package org.hbci4java.hbci.manager;

/**
 * Kapselt die bekannten HBCI-Versionen.
 */
public enum HBCIVersion {
    
    /**
     * HBCI 2.01
     */
    HBCI_201("201",   "HBCI 2.01"),
    
    /**
     * HBCI 2.1
     */
    HBCI_210("210",   "HBCI 2.10"),
    
    /**
     * HBCI 2.2
     */
    HBCI_220("220",   "HBCI 2.2"),
    
    /**
     * HBCI+ (HBCI 2.2 mit PIN/TAN-Support)
     */
    HBCI_PLUS("plus", "HBCI 2.2 (HBCI+)"),
    
    /**
     * FinTS 3.0
     */
    HBCI_300("300",   "FinTS 3.0"),
    
    /**
     * FinTS 4.0
     */
    HBCI_400("400",   "FinTS 4.0"),

    ;

    private String id = null;
    private String name = null;
    
    /**
     * ct.
     * @param id ID der HBCI-Version.
     * @param name sprechender Name der HBCI-Version.
     */
    private HBCIVersion(String id, String name)
    {
        this.id = id;
        this.name = name;
    }
    
    /**
     * Liefert die ID der HBCI-Version.
     * @return die ID der HBCI-Version.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Liefert den Namen der HBCI-Version.
     * @return der Name der HBCI-Version.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.getId() + ": " + this.getName();
    }
    
    /**
     * Sucht die HBCI-Version anhand der angegebenen ID.
     * @param id die ID der HBCI-Version.
     * @return die gefundene HBCI-Version oder NULL, wenn sie nicht gefunden wurde.
     */
    public static HBCIVersion byId(String id)
    {
        if (id == null || id.length() == 0)
            return null;
        
        for (HBCIVersion v:values())
        {
            if (v.getId().equals(id))
                return v;
        }

        return null;
    }
}


