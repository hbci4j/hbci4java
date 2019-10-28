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

import java.util.Arrays;
import java.util.List;

/**
 * Die Namen der bekannten Dialog-Templates.
 */
public enum KnownDialogTemplate
{
    /**
     * Dialog-Initialisierung.
     */
    INIT("DialogInit"),

    /**
     * Dialog-Initialisierung.
     */
    INIT_SCA("DialogInitSCA"),

    /**
     * Synchronisierung.
     */
    SYNC("Synch"),
    
    /**
     * Abruf der TAN-Medien.
     */
    TANMEDIA("TanMedia"),
    
    /**
     * Abruf der SEPA-Infos.
     */
    SEPAINFO("SepaInfo"),
    
    /**
     * Abfrage der Bankensignatur.
     */
    FIRSTKEYREQUEST("FirstKeyReq"),
    
    /**
     * Sperren von Schluesseln.
     */
    LOCKKEYS("LockKeys"),
    
    /**
     * Dialog-Ende.
     */
    END("DialogEnd"),
    
    ;
    
    private String name = null;
    
    /**
     * Liste der Dialoge, bei denen ein SCA-Request per HKTAN gesendet werden soll.
     * Auch bei der anonymen - Siehe 'FinTS_3.0_Security_Sicherheitsverfahren_PINTAN_2018-02-23_final_version.pdf', letzter Hinweis in B.4.3.1
     * FIRSTKEYREQUEST ist zwar auch eine Dialog-Initialisierung. Da es die aber nur bei Schluesseldateien
     * und nicht bei PIN/TAN gibt, wuerde ein HKTAN hier gar keinen Sinn machen.
     */
    public static List<KnownDialogTemplate> LIST_SEND_SCA = Arrays.asList(INIT,INIT_SCA,SYNC);
    
    /**
     * ct.
     * @param name der Name des Template.
     */
    private KnownDialogTemplate(String name)
    {
        this.name = name;
    }
    
    /**
     * Liefert den Namen des Dialogs.
     * @return der Name des Dialogs.
     */
    public String getName()
    {
        return this.name;
    }
}
