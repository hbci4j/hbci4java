package org.kapott.hbci.passport.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Eine Erweiterung von {@link FileOutputStream}.
 *
 * <p>Anstatt mit der eigentlich übergebenen Zieldatei initialisiert diese Implementierung den Stream aber mit einer temporären Datei.
 * Alle Schreiboperationen auf dem Stream werden also auf diese temporäre Datei angewendet.</p>
 *
 * <p>Erst wenn der Stream geschlossen wird, wird die temporäre Datei in die gewünschte Zieldatei umbenannt. Sollte die Zieldatei bereits
 * existieren, so wird sie erst in diesem Moment ersetzt.</p>
 *
 * <p>Dadurch lässt sich insbesondere in Fehlerfällen verhindern, dass die Zieldatei in einem unvollständigen Zustand verbleibt.</p> 
 *
 * @author Hendrik Schnepel
 */
public class TemporaryFileOutputStream extends FileOutputStream {
    
    /**
     * Erzeugt einen neuen {@link TemporaryFileOutputStream} für die Zieldatei.
     */
    public static FileOutputStream create(File file) throws IOException {
        return new TemporaryFileOutputStream(getTemporaryFile(file), file);
    }
    
    private static File getTemporaryFile(File file) throws IOException {
        File directory = file.getAbsoluteFile().getParentFile();
        String prefix = file.getName() + "_";
        return File.createTempFile(prefix, "", directory);
    }

    private final File temporaryFile;
    private final File destinationFile;

    private TemporaryFileOutputStream(File temporaryFile, File destinationFile) throws IOException {
        super(temporaryFile);
        this.temporaryFile = temporaryFile;
        this.destinationFile = destinationFile;
    }

    @Override
    public void close() throws IOException {
        super.close();
        /*
         * Sobald der Stream geschlossen ist, wird die (evtl. bestehende) Zieldatei vorsichtshalber gelöscht.
         * Danach wird die temporäre Datei, die die jetzt neuen Daten enthält, in die Zieldatei umbenannt.
         */
        destinationFile.delete();
        temporaryFile.renameTo(destinationFile);
    }

}
