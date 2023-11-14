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

package org.kapott.hbci.passport.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.storage.format.AESFormat;
import org.kapott.hbci.passport.storage.format.PassportFormat;
import org.kapott.hbci.tools.IOUtils;

/**
 * Kapselt das Lesen/Schreiben und Verschluesseln/Entschluesseln der Passport-Dateien.
 */
public class PassportStorage
{
    private final static List<String> ORDER_DEFAULT = Arrays.asList("AESFormat","LegacyFormat");
    private static Map<String,PassportFormat> formats = null;
    
    static
    {
        init();
    }
    
    /**
     * Liest die Passportdatei ein.
     * @param passport der Passport, zu dem die Daten gelesen werden sollen.
     * @param file die Passport-Datei.
     * @return das Passport-Format.
     */
    public static PassportData load(HBCIPassport passport, File file)
    {
        if (file == null)
            throw new HBCI_Exception("no passport file given");
        
        if (!file.canRead() || !file.isFile())
            throw new HBCI_Exception("passport file " + file + " not readable or no file");

        HBCIUtils.log("loading passport data from " + file,HBCIUtils.LOG_DEBUG);

        InputStream is = null;
        try
        {
            is = new BufferedInputStream(new FileInputStream(file));
            return load(passport, is);
        }
        catch (IOException e)
        {
            throw new HBCI_Exception("unable to read passport file " + file,e);
        }
        finally
        {
            IOUtils.close(is);
        }
    }

    /**
     * Liest die Passportdatei ein.
     * @param passport der Passport, zu dem die Daten gelesen werden sollen.
     * @param is Stream mit der Datei.
     * Die Funktion schliesst den Stream nicht. Das ist Aufgabe des Aufrufers.
     * @return die gelesenen Passport-Daten.
     */
    public static PassportData load(HBCIPassport passport, InputStream is)
    {
        if (passport == null)
            throw new HBCI_Exception("no passport given");
        
        if (is == null)
            throw new HBCI_Exception("no inputstream given");
        try
        {
            // Wir laden die Datei erstmal komplett in den Speicher, damit wir die Formate durchprobieren koennen
            byte[] data = IOUtils.read(is);
            
            // Wenn die Datei leer ist, brauchen wir sie nicht einlesen, sondern liefern einfach ein leeres
            // PassportData-Objekt. Dann werfen wir naemlich keinen Fehler beim Laden, wenn das Schreiben einer
            // Passport-Datei mal fehlschlug und sie als leere Datei erzeugt wurde. Stattdessen erstellen wir
            // einfach einen neuen Passport.
            if (data != null && data.length == 0)
                return new PassportData();
            
            for (PassportFormat format:getLoadFormats())
            {
                try
                {
                    PassportData result = format.load(passport,data);
                    HBCIUtils.log("passport data loaded using " + format.getClass().getSimpleName(),HBCIUtils.LOG_DEBUG);
                    return result;
                }
                catch (UnsupportedOperationException ue)
                {
                    // Passport unterstuetzt das Format nicht - ueberspringen - Fehlermeldung koennen wir ignorieren,
                    // sie wird vom Format bewusst geworfen, um anzuzeigen, dass es das Format nicht unterstuetzt
                }
            }
            
            // Wenn wir hier angekommen sind, unterstuetzen wir das Format nicht.
            throw new HBCI_Exception("unknown passport file format");
        }
        catch (IOException e)
        {
            throw new HBCI_Exception("unable to read passport file",e);
        }
    }
    
    /**
     * Speichert die Passport-Daten.
     * @param passport der Passport.
     * @param data die Daten.
     * @param file die Zieldatei.
     */
    public static void save(HBCIPassport passport, PassportData data, File file)
    {
        if (file == null)
            throw new HBCI_Exception("no passport file given");

        HBCIUtils.log("saving passport data to " + file,HBCIUtils.LOG_DEBUG);

        OutputStream os = null;
        
        try
        {
            // Wir schreiben die Daten erstmal in eine Temp-Datei.
            // Wenn die geschrieben ist, verschieben wir die Datei auf den vorherigen Namen.
            File directory = file.getAbsoluteFile().getParentFile();
            String prefix = file.getName() + "_";
            File tempfile = File.createTempFile(prefix,"",directory);

            os = new BufferedOutputStream(new FileOutputStream(tempfile));
            save(passport,data,os);
            os.close();

            IOUtils.safeReplace(file,tempfile);
        }
        catch (IOException e)
        {
            throw new HBCI_Exception("unable to write passport file " + file,e);
        }
        finally
        {
            IOUtils.close(os);
        }
    }
    
    /**
     * Speichert die Passport-Daten.
     * @param passport der Passport.
     * @param data die Daten.
     * @param os der Stream, in den die Daten geschrieben werden.
     */
    public static void save(HBCIPassport passport, PassportData data, OutputStream os)
    {
        if (passport == null)
            throw new HBCI_Exception("no passport given");

        if (data == null)
            throw new HBCI_Exception("no passport data given");
        
        // Ermitteln, welches Dateiformat verwendet werden soll.
        final PassportFormat format = getSaveFormat(passport);
        
        try
        {
            byte[] bytes = format.save(passport,data);
            os.write(bytes);
            os.flush(); // Sicherstellen, dass die Daten geflusht sind.
            HBCIUtils.log("passport data saved using " + format.getClass().getSimpleName(),HBCIUtils.LOG_DEBUG);
            return; // Wenn wir fehlerfrei geschrieben haben, sind wir fertig
        }
        catch (IOException e)
        {
            throw new HBCI_Exception("unable to write passport file",e);
        }
    }
    
    /**
     * Initialisiert die Liste der unterstuetzten Dateiformate.
     */
    private static void init()
    {
        if (formats != null)
            return;

        formats = new HashMap<String,PassportFormat>();
        
        HBCIUtils.log("searching supported passport formats",HBCIUtils.LOG_DEBUG);
        final ServiceLoader<PassportFormat> loader = ServiceLoader.load(PassportFormat.class);
        for (PassportFormat f:loader)
        {
            final String name = f.getClass().getSimpleName();
            
            if (!f.supported())
            {
                HBCIUtils.log("passport format " + name + " not supported on this plattform",HBCIUtils.LOG_INFO);
                continue;
            }
            formats.put(name,f);
        }
        
        if (formats.size() == 0)
            HBCIUtils.log("No supported passport formats found",HBCIUtils.LOG_ERR);
    }

    /**
     * Liefert die Passport-Formate in der angegebenen Reihenfolge zum Laden.
     * @return die Passports in der konfigurierten Reihenfolge.
     */
    private static List<PassportFormat> getLoadFormats()
    {
        List<PassportFormat> result = new LinkedList<PassportFormat>();
        for (String name:getFormatOrder())
        {
            PassportFormat f = formats.get(name);
            if (f == null)
            {
                HBCIUtils.log("passport format unknown or not supported: " + name,HBCIUtils.LOG_DEBUG);
                continue;
            }
            
            result.add(f);
        }
        
        return result;
    }


    /**
     * Liefert das fuer die Speicherung zu verwendende Dateiformat.
     * @param passport der Passport.
     * @return das zu verwendende Format. Nie NULL.
     * Wenn keines per Konfiguration ermittelbar ist, wird das Default-Format {@link AESFormat} verwendet.
     */
    private static PassportFormat getSaveFormat(HBCIPassport passport)
    {
        final String type = passport.getClass().getSimpleName();
        
        // Checkt erst, ob es ein passport-spezifisches Format-Parameter gibt. Z.Bsp. "passport.format.HBCIPassportPinTan"
        // Wenn icht, dann ob es einen generischen Format-Parameter "passport.format"
        final String name = HBCIUtils.getParam("passport.format." + type,HBCIUtils.getParam("passport.format"));


        // Es ist ein Format konfiguriert. Checken, ob wir es unterstuetzen
        if (name != null)
        {
            PassportFormat format = formats.get(name);
            if (format != null)
                return format;
        }
        
        // Entweder wir kennen es nicht oder wir unterstuetzen es nicht. Dann nehmen wir das
        // erste Format, das wir kennen
        for (String s:getFormatOrder())
        {
            PassportFormat format = formats.get(s);
            if (format != null)
                return format;
        }
        
        // Wenn auch hier nichts da ist, nehmen wir das erste, das ueberhaupt existiert
        if (formats.size() == 0)
            throw new HBCI_Exception("No supported passport formats found");
        
        return formats.values().iterator().next();
    }
    
    /**
     * Liefert die Format-Reihenfolge.
     * @return die zu verwendende Format-Reihenfolge.
     */
    private static List<String> getFormatOrder()
    {
        String value = HBCIUtils.getParam("passport.order");
        if (value == null || value.length() == 0)
            return ORDER_DEFAULT;
        
        // Whitespaces und Leerzeichen entfernen - auch mittendrin
        value = value.trim().replace(" ","");

        // Nur Leerzeichen?
        if (value.length() == 0)
            return ORDER_DEFAULT;

        
        // Liste enthaelt nur einen Wert
        if (!value.contains(","))
            return Arrays.asList(value);

        return Arrays.asList(value.split(","));
    }
}
