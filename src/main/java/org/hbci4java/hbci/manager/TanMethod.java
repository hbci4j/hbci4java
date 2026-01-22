/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2019 Olaf Willuhn
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
 * Kapselt die Informationen zu einem TAN-Verfahren.
 */
public class TanMethod
{
    /**
     * Das Einschritt-Verfahren.
     */
    public final static TanMethod ONESTEP = new TanMethod("999","Einschritt-Verfahren");
    
    private String id = null;
    private String name = null;
        
    /**
     * ct.
     * @param id ID des TAN-Verfahrens.
     * @param name Name des TAN-Verfahrens.
     */
    public TanMethod(String id, String name)
    {
        this.id = id;
        this.name = name;
    }
    
    /**
     * Liefert die dreistellige numerische ID.
     * @return id die dreistellige numerische ID.
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * Liefert die sprechende Bezeichnung.
     * @return name die sprechende Bezeichnung.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.id + ": " + this.name;
    }

}


