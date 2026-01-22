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

package org.hbci4java.hbci.dialog;

import java.util.Properties;

import org.hbci4java.hbci.status.HBCIMsgStatus;

/**
 * Bei der Ausfuehrung von HBCI-Dialogen kommt es an mehreren Stellen zu Callbacks in Paspports, weil dort
 * abhaengig vom Zugangsverfahren Sonderbehandlungen ergeben (im Wesentlichen PIN/TAN - Stichwort SCA sowie bei Schlüsseldateien).
 * Da diese Callbacks im Laufe der Zeit zu unuebersichtlich geworden sind, gibt es jetzt generische Events und eine Kapselung der rohen HBCI-Dialoge.
 * Interface fuer die rohen HBCI-Dialoge.
 */
public interface RawHBCIDialog
{
    /**
     * Sendet die Dialog-Initialisierung an die Bank.
     * @param ctx der Dialog-Context.
     * @return der Ausfuehrungsstatus. Darf niemals NULL sein. In dem Fall muss die Methode eine Exception werfen.
     */
    public HBCIMsgStatus execute(final DialogContext ctx);
    
    /**
     * Liefert das Template.
     * @return das Template.
     */
    public KnownDialogTemplate getTemplate();
    
    /**
     * Speichert das Template.
     * @param t das Template.
     */
    public void setTemplate(KnownDialogTemplate t);
    
    /**
     * Erzeugt einen SCA-Request.
     * @param secmechInfo die TAN-Verfahren-Parameter.
     * @param hktanVersion die HKTAN-Version.
     * @return der SCA-Request.
     */
    public SCARequest createSCARequest(Properties secmechInfo, int hktanVersion);
}
