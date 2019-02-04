/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.storage.format.PassportFormat;
import org.kapott.hbci.tools.IOUtils;

/**
 * Kapselt das Lesen/Schreiben und Verschluesseln/Entschluesseln der Passport-Dateien.
 */
public class PassportStorage
{
    private static List<PassportFormat> formatsLoad = null;
    private static List<PassportFormat> formatsSave = null;
    
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
            
            for (PassportFormat format:getFormats(true))
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
            throw new HBCI_Exception("unsupported passport file format");
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
        
        try
        {
            for (PassportFormat format:getFormats(false))
            {
                try
                {
                    byte[] bytes = format.save(passport,data);
                    os.write(bytes);
                    os.flush(); // Sicherstellen, dass die Daten geflusht sind.
                    HBCIUtils.log("passport data saved using " + format.getClass().getSimpleName(),HBCIUtils.LOG_DEBUG);
                    return; // Wenn wir fehlerfrei geschrieben haben, sind wir fertig
                }
                catch (UnsupportedOperationException ue)
                {
                    // Passport unterstuetzt das Format nicht - ueberspringen - Fehlermeldung koennen wir ignorieren,
                    // sie wird vom Format bewusst geworfen, um anzuzeigen, dass es das Format nicht unterstuetzt
                }
            }
        }
        catch (IOException e)
        {
            throw new HBCI_Exception("unable to write passport file",e);
        }
    }
    
    /**
     * Liefert die Passport-Formate in der angegebenen Reihenfolge.
     * @param load true, wenn die Passports in der Ladereihenfolge geliefert werden sollen. Sonst in der Speicher-Reihenfolge. 
     * @return die Passports in der angegebenen Reihenfolge.
     */
    private static List<PassportFormat> getFormats(boolean load)
    {
        return load ? formatsLoad : formatsSave;
    }

    /**
     * Initialisiert die Liste der unterstuetzten Dateiformate.
     */
    private static void init()
    {
        if (formatsLoad != null && formatsSave != null)
            return;

        formatsLoad = new LinkedList<PassportFormat>();
        formatsSave = new LinkedList<PassportFormat>();
        
        HBCIUtils.log("searching supported passport formats",HBCIUtils.LOG_DEBUG);
        final ServiceLoader<PassportFormat> loader = ServiceLoader.load(PassportFormat.class);
        for (PassportFormat f:loader)
        {
            formatsLoad.add(f);
            formatsSave.add(f);
        }
        
        if (formatsLoad.size() == 0)
            HBCIUtils.log("No supported passport formats found",HBCIUtils.LOG_ERR);

        sort("load",formatsLoad,new Comparator<PassportFormat>() {
            /**
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            @Override
            public int compare(PassportFormat o1, PassportFormat o2)
            {
                return Integer.compare(o1.getLoadOrder(),o2.getLoadOrder());
            }
        });
        
        // Beim Speichern haengt die Reihenfolge davon ab, ob die Passports automatisch in das neue AESFormat
        // konvertiert werden sollen oder nicht. Wenn hier das AESFormat vorn steht, werden die Passports beim
        // ersten Schreibvorgang - egal aus welchem Format sie gelesen wurden - automatisch in das neue Format
        // konvertiert.
        final List<String> orderSave = getFormatOrder("passportformat.order.save",Arrays.asList("LegacyFormat","AESFormat"));
        sort("save",formatsSave,new Comparator<PassportFormat>() {
            /**
             * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
             */
            @Override
            public int compare(PassportFormat o1, PassportFormat o2)
            {
                int x = orderSave.indexOf(o1.getClass().getSimpleName());
                int y = orderSave.indexOf(o2.getClass().getSimpleName());
                
                // Wenn der Name nicht in der Liste enthalten ist, stellen wir das Format hinten an
                if (x == -1) x = Integer.MAX_VALUE;
                if (y == -1) y = Integer.MAX_VALUE;
                
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        });
    }
    
    /**
     * Liefert die Format-Reihenfolge fuer den Parameter.
     * @param parameter der Parameter.
     * @param def die Default-Reihenfolge.
     * @return die zu verwendende Format-Reihenfolge.
     */
    private static List<String> getFormatOrder(String parameter, List<String> def)
    {
        String value = HBCIUtils.getParam(parameter);
        if (value == null || value.length() == 0)
            return def;
        
        // Whitespaces und Leerzeichen entfernen - auch mittendrin
        value = value.trim().replace(" ","");

        // Nur Leerzeichen?
        if (value.length() == 0)
            return def;

        // Liste enthaelt nur einen Wert
        if (!value.contains(","))
            return Arrays.asList(value);

        return Arrays.asList(value.split(","));
    }

    /**
     * Sortiert die Formate in der angegebenen benannten Reihenfolge.
     * @param name sprechender Name fuer die Sortierung fuer das Logging.
     * @param formats die Formate.
     * @param c der Comparator.
     */
    private static void sort(String name, List<PassportFormat> formats, Comparator<PassportFormat> c)
    {
        Collections.sort(formats,c);
        
        // Logging
        HBCIUtils.log("format order: " + name,HBCIUtils.LOG_DEBUG);
        for (PassportFormat f:formats)
        {
            HBCIUtils.log("  " + f.getClass().getSimpleName(),HBCIUtils.LOG_DEBUG);
        }
    }
}
