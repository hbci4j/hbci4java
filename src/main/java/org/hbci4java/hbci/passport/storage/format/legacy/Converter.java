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

package org.hbci4java.hbci.passport.storage.format.legacy;

import java.io.InputStream;
import java.io.OutputStream;

import org.hbci4java.hbci.passport.HBCIPassport;
import org.hbci4java.hbci.passport.storage.PassportData;

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
