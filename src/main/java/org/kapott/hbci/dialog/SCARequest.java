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
    private String tanMedia;
    
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
    
    /**
     * Liefert die TAN-Medienbezeichnung.
     * @return die TAN-Medienbezeichnung.
     */
    public String getTanMedia()
    {
        return tanMedia;
    }
    
    /**
     * Speichert die TAN-Medienbezeichnung.
     * @param tanMedia die TAN-Medienbezeichnung.
     */
    public void setTanMedia(String tanMedia)
    {
        this.tanMedia = tanMedia;
    }
}
