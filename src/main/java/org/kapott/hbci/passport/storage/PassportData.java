/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.manager.HBCIKey;

/**
 * Kapselt die Daten des Passport zum Lesen und Schreiben.
 */
public class PassportData implements Serializable
{
    // Stellt sicher, dass das Objekt auch dann noch deserialisierbar ist, wenn Attribute hinzugekommen sind.
    private static final long serialVersionUID = 1L;
    
    /**
     * Die BPD.
     */
    public Properties bpd = null;
    
    /**
     * Die UPD.
     */
    public Properties upd = null;
    
    /**
     * Die HBCI-Version.
     */
    public String hbciVersion = null;
    
    /**
     * Der Laendercode.
     */
    public String country = null;
    
    /**
     * Die BLZ.
     */
    public String blz = null;
    
    /**
     * Der Host.
     */
    public String host = null;
    
    /**
     * Der TCP-Port.
     */
    public Integer port = null;
    
    /**
     * Die User-ID.
     */
    public String userId = null;
    
    /**
     * Die System-ID.
     */
    public String sysId = null;
    
    /**
     * Die Signatur-ID.
     */
    public Long sigId = null;
    
    /**
     * Die Profil-Version.
     */
    public String profileVersion = null;
    
    /**
     * Die Kunden-ID.
     */
    public String customerId = null;
    
    /**
     * Der Transport-Filter.
     */
    public String filter = null;
    
    /**
     * Die Liste der Zweischritt-Verfahren.
     */
    public List<String> twostepMechs = new ArrayList<String>();
    
    /**
     * Das TAN-Verfahren.
     */
    public String tanMethod = null;

    /**
     * Der Signatur-Key der Bank.
     */
    public HBCIKey instSigKey = null;
    
    /**
     * Der Verschluesselungskey der Bank.
     */
    public HBCIKey instEncKey = null;
    
    /**
     * Der eigene Public-Signatur-Key.
     */
    public HBCIKey myPublicSigKey = null;
    
    /**
     * Der eigene Private-Signatur-Key.
     */
    public HBCIKey myPrivateSigKey = null;
    
    /**
     * Der eigene Public-Verschluesselungskey.
     */
    public HBCIKey myPublicEncKey = null;
    
    /**
     * Der eigene Private-Verschluesselungskey.
     */
    public HBCIKey myPrivateEncKey = null;
}
