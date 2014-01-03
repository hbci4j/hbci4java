package org.kapott.hbci.passport.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Diese Implementierung hält die Daten im Speicher.
 *
 * @author Hendrik Schnepel
 */
public class ByteArrayStreamFactory implements ResourceStreamFactory {

    private ByteArrayOutputStream outputStream = null;

    @Override
    public OutputStream newOutputStream() throws IOException {
        outputStream = new ByteArrayOutputStream();
        return outputStream;
    }

    @Override
    public InputStream newInputStream() throws IOException {
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
