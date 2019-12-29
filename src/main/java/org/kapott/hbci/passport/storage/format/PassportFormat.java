/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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
    
    /**
     * Testet, ob das Format auf dem System unterstuetzt wird.
     * @return true, wenn es unterstuetzt wird.
     */
    public boolean supported();
}
