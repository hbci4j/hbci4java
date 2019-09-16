/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.manager;

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


