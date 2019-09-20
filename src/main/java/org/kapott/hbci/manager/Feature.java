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
 * Kapselt verschiedene Feature-Flags.
 */
public enum Feature
{
    /**
     * Feature, mit dem die Neueinrichtung eines PIN/TAN-Bankzugangs optimierter ablaeuft. 
     */
    PINTAN_FASTSETUP(true),
    
    ;
    
    private boolean enabled = false;
    
    /**
     * ct.
     * @param enabled Legt fest, ob das Feature per Default aktiviert sein soll.
     */
    private Feature(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    /**
     * Liefert true, wenn das Feature per Default aktiv sein soll.
     * @return true, wenn das Feature per Default aktiv sein soll.
     */
    public boolean getDefault()
    {
        return this.enabled;
    }
    
    /**
     * Liefert den aktuellen Zustand des Features.
     * @return true, wenn das Feature aktiv ist.
     */
    public boolean isEnabled()
    {
        return Boolean.parseBoolean(HBCIUtils.getParam("feature." + this,Boolean.toString(this.getDefault())));
    }
    
    /**
     * Setzt den Status des Features zur Laufzeit.
     * @param b true, wenn das Feature aktiv sein soll.
     */
    public void setEnabled(boolean b)
    {
        HBCIUtils.setParam("feature." + this,Boolean.toString(b)); // Hier nicht "NULL" bei false, weil das den Default-Zustand wieder herstellen koennte. Und der kann true sein
    }

    /**
     * Setzt den Status des Features zur Laufzeit auf die Werksvorgabe zurueck.
     */
    public void reset()
    {
        HBCIUtils.setParam("feature." + this,null);
    }
}
