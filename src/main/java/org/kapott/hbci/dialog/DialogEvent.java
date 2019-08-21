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

/**
 * Diese Klasse enthaelt die verschiedenen Event-Arten.
 */
public enum DialogEvent
{
    /**
     * Wird gesendet, bevor HBCI4Java die Nachricht erzeugt aber noch nicht an die Bank gesendet hat.
     * Kann mehrfach auftreten, wenn z.Bsp. die Dialog-Initialisierung nach einer Neuwahl des TAN-Verfahrens (aus Code 3920) wiederholt werden muss.
     */
    MSG_CREATE,
    
    /**
     * Wird versendet, bevor HBCI4Java die Nachricht an die Bank gesendet hat und das Response vorliegt.
     * Kann mehrfach auftreten, wenn z.Bsp. die Dialog-Initialisierung nach einer Neuwahl des TAN-Verfahrens (aus Code 3920) wiederholt werden muss.
     */
    MSG_CREATED,
    
    /**
     * Wird versendet, nachdem HBCI4Java die Nachricht an die Bank gesendet hat und das Response vorliegt.
     * Kann mehrfach auftreten, wenn z.Bsp. die Dialog-Initialisierung nach einer Neuwahl des TAN-Verfahrens (aus Code 3920) wiederholt werden muss.
     */
    MSG_SENT,
    
    /**
     * Wird gesendet, nachdem die Dialog-Initialisierung abgeschlossen ist und bevor die eigentlichen Geschaeftsvorfaelle an die Bank gesendet werden.
     */
    JOBS_CREATED,
}


