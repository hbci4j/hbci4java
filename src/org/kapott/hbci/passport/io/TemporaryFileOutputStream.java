package org.kapott.hbci.passport.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TemporaryFileOutputStream extends FileOutputStream {
    
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
        destinationFile.delete();
        temporaryFile.renameTo(destinationFile);
    }

}
