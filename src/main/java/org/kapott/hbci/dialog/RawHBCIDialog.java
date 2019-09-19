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

import org.kapott.hbci.status.HBCIMsgStatus;

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
     * Liefert den fuer die Dialog-Initialisierung zu verwendenden SCA-Request fuer das HKTAN.
     * @param ctx der Dialog-Context.
     * @return der SCA-Request oder NULL, wenn der Init-Dialog kein HKTAN enthalten soll.
     */
    public SCARequest getSCARequest(final DialogContext ctx);
}
