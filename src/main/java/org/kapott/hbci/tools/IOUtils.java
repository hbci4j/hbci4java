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

package org.kapott.hbci.tools;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * Hilfsfunktionen fuer IO-Operationen.
 */
public class IOUtils
{
    /**
     * Ersetzt die Datei origFile gegen tmpFile.
     * Nach dem Loeschen der Datei origFile wartet die Methode jedoch maximal 20 Sekunden,
     * um sicherzustellen, dass z.Bsp. Virenscanner die Datei wieder losgelassen haben und
     * sie wirklich verschwunden ist, bevor tmpFile auf den Namen von origFile umbenannt wird.
     * Wichtig ist, dass zum Zeitpunkt des Aufrufes dieser Methode alle Streams auf die
     * Dateien bereits geschlossen wurden. Die Schreibvorgaenge auf die Dateien muessen also
     * abgeschlossen sein. Heisst: "os.close()" nicht erst im finally-Block machen sondern
     * VOR dem Aufruf dieser Methode.
     * @param origFile die originale zu ersetzende Datei.
     * @param tmpFile die neue Datei, welche die originale ersetzen soll.
     */
    public static void safeReplace(File origFile, File tmpFile)
    {
        HBCIUtils.log("saving file " + origFile, HBCIUtils.LOG_DEBUG);
        
        if (origFile.exists()) // Nur loeschen, wenn es ueberhaupt existiert
        {
            HBCIUtils.log("deleting old file " + origFile, HBCIUtils.LOG_DEBUG);
            if (!origFile.delete())
                HBCIUtils.log("file " + origFile + " not yet deleted, waiting...", HBCIUtils.LOG_WARN);
        }

        // Wenn die Datei noch existiert, warten wir noch etwas
        int retry = 0;
        while (origFile.exists() && retry++ < 20)
        {
            try
            {
                HBCIUtils.log("wait a little bit, maybe another thread (antivirus scanner) holds a lock, file still exists", HBCIUtils.LOG_WARN);
                Thread.sleep(1000L);
            }
            catch (InterruptedException e)
            {
                HBCIUtils.log("interrupted", HBCIUtils.LOG_WARN);
                break;
            }
            if (!origFile.exists())
            {
                HBCIUtils.log("file now gone: " + origFile, HBCIUtils.LOG_DEBUG);
                break;
            }
        }
        
        // Datei existiert immer noch, dann brauchen wir das Rename gar nicht erst versuchen
        if (origFile.exists())
            throw new HBCI_Exception("could not delete " + origFile);

        // Das Rename versuchen wir jetzt auch wiederholt mehrfach
        retry = 0;
        HBCIUtils.log("renaming " + tmpFile.getName() + " to " + origFile.getName(), HBCIUtils.LOG_DEBUG);
        while (!origFile.exists() && retry++ < 20)
        {
            if (!tmpFile.renameTo(origFile))
                HBCIUtils.log("file " + tmpFile + " not yet renamed to " + origFile + ", waiting ", HBCIUtils.LOG_WARN);
            
            if (origFile.exists())
            {
                HBCIUtils.log("new file now exists: " + origFile, HBCIUtils.LOG_DEBUG);
                break;
            }
            
            try
            {
                HBCIUtils.log("wait a little bit, maybe another thread (antivirus scanner) holds a lock, file still not renamed", HBCIUtils.LOG_WARN);
                Thread.sleep(1000L);
            }
            catch (InterruptedException e)
            {
                HBCIUtils.log("interrupted", HBCIUtils.LOG_WARN);
                break;
            }
        }
        
        if (!origFile.exists())
            throw new HBCI_Exception("could not rename " + tmpFile.getName() + " to " + origFile.getName());
    }

    /**
     * Kopiert die Daten aus dem InputStream in den OutputStream.
     * @param is der InputStream.
     * Der InputStream wird nicht geschlossen. Das ist Aufgabe des Aufrufers.
     * @param os der OutputStream.
     * @return die Anzahl geschriebener Bytes.
     * @throws IOException
     */
    public static long copy(InputStream is, OutputStream os) throws IOException
    {
        byte[] buf = new byte[4096];
        long size = 0;
        int read;
        while ((read = is.read(buf)) != -1)
        {
            os.write(buf, 0, read);
            size += read;
        }
        return size;
    }
    
    /**
     * Liest die Datei komplett in das Byte-Array.
     * @param is der InputStream.
     * Der InputStream wird nicht geschlossen. Das ist Aufgabe des Aufrufers.
     * @return das Byte-Array.
     * @throws IOException
     */
    public static byte[] read(InputStream is) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        copy(is,bos);
        return bos.toByteArray();
    }
    
    /**
     * Schliesst das Closeable, ohne eine Exception zu werfen.
     * Auch dann nicht, wenn c NULL ist.
     * @param list ein oder mehrere Closeables.
     */
    public static void close(Closeable... list)
    {
        if (list == null || list.length == 0)
            return;
        
        for (Closeable c:list)
        {
          try
          {
              c.close();
          }
          catch (Exception e)
          {
              HBCIUtils.log(e, HBCIUtils.LOG_DEBUG);
          }
        }
    }
    
    /**
     * Prueft, ob ein Dateipfad ungueltige Zeichen enthaelt oder zu lang ist und kuerzt ihn automatisch.
     * Siehe https://homebanking-hilfe.de/forum/topic.php?p=138325#real138325
     * Die Funktion laesst in Dateinamen ausschliesslich Buchstaben, Zahlen, Unterstrich, Bindestrich und Punkt zu.
     * Wenn der Dateiname laenger als 25 Zeichen ist, wuerde er auf 25 Zeichen abgeschnitten.
     * @param filename der zu pruefende Dateiname inclusive Pfad.
     * @return der ggf korrigierte Dateiname.
     */
    public static String safeFilename(String filename)
    {
        if (filename == null || filename.length() == 0)
            return filename;
        
        File f = new File(filename).getAbsoluteFile();
        String name = f.getName();
        
        // Zeichen, die nicht enthalten sein sollten, entfernen wir.
        name = name.replaceAll("[^a-zA-Z0-9_\\.-]","");
        
        // Wenn die Datei laenger als 25 Zeichen ist, schneiden wir die ueberzaehligen ab
        if (name.length() > 25)
          name = name.substring(0,25);
        
        f = new File(f.getParentFile(),name);
        String newName = f.getAbsolutePath();
        
        // Dateiname ist unveraendert. Dann wurden keine Korrekturen vorgenommen.
        if (newName.equals(filename))
            return filename;
        
        HBCIUtils.log("auto-fixed filename from " + f + " to " + newName,HBCIUtils.LOG_DEBUG);
        return newName;
    }
}


