/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format.legacy;

import java.io.InputStream;
import java.io.OutputStream;

import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.storage.PassportData;

/**
 * Das Interface bietet eine gemeinsame Schnittstelle fuer die unterschiedlichen Daten in den Passport-Dateien.
 */
public interface Converter
{
    /**
     * Liefert den zu verwendenden Salt.
     * @return das zu verwendende Salt.
     */
    public byte[] getSalt();
    
    /**
     * Laedt die Passport-Daten.
     * @param is der Stream, von dem die Daten gelesen werden.
     * @return die gelesenen Daten.
     * @throws Exception wenn es beim Lesen der Daten zu einem Fehler kam.
     */
    public PassportData load(InputStream is) throws Exception;
    
    /**
     * Speichert die Passport-Daten.
     * @param data die Daten.
     * @param os der Stream, in den die Daten geschrieben werden.
     * @throws Exception wenn es beim Schreiben der Daten zu einem Fehler kam.
     */
    public void save(PassportData data, OutputStream os) throws Exception;
    
    /**
     * Prueft, ob der Loader dieses Format lesen kann.
     * @param passport der Passport.
     * @return true, wenn er es lesen kann.
     */
    public boolean supports(HBCIPassport passport);
}
