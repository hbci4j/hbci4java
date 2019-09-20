/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.dialog;

import org.kapott.hbci.dialog.KnownTANProcess.Variant;

/**
 * Kapselt die Eckdaten des SCA-Requests in der Dialog-Initialisierung.
 */
public class SCARequest
{
    private int version;
    private String tanReference;
    private Variant variant;
    
    /**
     * Liefert die Prozess-Variante.
     * @return variant die Prozess-Variante.
     */
    public Variant getVariant()
    {
        return variant;
    }
    
    /**
     * Speichert die Prozess-Variante.
     * @param variant die Prozess-Variante.
     */
    public void setVariant(Variant variant)
    {
        this.variant = variant;
    }
    
    /**
     * Liefert die Segment-Version fuer das HKTAN.
     * @return die Segment-Version fuer das HKTAN.
     */
    public int getVersion()
    {
        return version;
    }
    
    /**
     * Speichert die Segment-Version fuer das HKTAN.
     * @param version die Segment-Version fuer das HKTAN.
     */
    public void setVersion(int version)
    {
        this.version = version;
    }

    /**
     * Liefert den Geschaeftsvorfall-Code, der im HKTAN als Referenz verwendet werden soll.
     * @return de Geschaeftsvorfall-Code, der im HKTAN als Referenz verwendet werden soll.
     */
    public String getTanReference()
    {
        return tanReference;
    }
    
    /**
     * Speichert den Geschaeftsvorfall-Code, der im HKTAN als Referenz verwendet werden soll.
     * @param tanReference der Geschaeftsvorfall-Code, der im HKTAN als Referenz verwendet werden soll.
     */
    public void setTanReference(String tanReference)
    {
        this.tanReference = tanReference;
    }
}
