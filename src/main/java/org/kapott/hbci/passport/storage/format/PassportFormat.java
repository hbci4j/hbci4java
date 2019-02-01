/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format;

import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.storage.PassportData;

/**
 * Dieses Interface kapselt die verschiedenen Dateiformate von Passport-Dateien.
 */
public interface PassportFormat
{
    /**
     * Liest die Passport-Datei.
     * @param passport der Passport, fuer den die Daten gelesen werden sollen.
     * @param data das Byte-Array mit dem Datei-Inhalt. Wir uebergeben hier keinen Stream, damit wir
     * mehrere Formate mit den selben Daten durchprobieren koennen, ohne jedesmal den Stream neu oeffnen zu muessen
     * (mark/reset unterstuetzen viele InputStream-Implementierungen nicht).
     * Und da wir die Daten zur Deserialisierung ohnehin komplett lesen muessen, koennen wir sie auch gleich in
     * ein Byte-Array lesen.
     * @return die gelesenen Daten des Passport. 
     * @throws UnsupportedOperationException wenn die Implementierung dieses Dateiformat nicht unterstuetzt.
     */
    public PassportData load(HBCIPassport passport, byte[] data) throws UnsupportedOperationException;
    
    /**
     * Speichert die Passport-Daten-
     * @param passport der Passport, fuer den die Daten gespeichert werden sollen.
     * @param data die zu speichernden Daten.
     * @return die gespeicherten Daten als Byte-Array.
     * @throws UnsupportedOperationException
     */
    public byte[] save(HBCIPassport passport, PassportData data) throws UnsupportedOperationException;
}
