package org.kapott.hbci.passport.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Implementierungen dieser Schnittstelle kapseln den schreibenden und lesenden Zugriff auf einzelne Resourcen.
 *
 * @author Hendrik Schnepel
 */
public interface ResourceStreamFactory {

    /**
     * Erzeugt einen neuen {@link OutputStream}, mit dem auf die Resource geschrieben werden kann.
     *
     * <p>Sollte die Resource bereits existieren, so könnte sie (abhängig von der jeweiligen Implementierung) mit Aufruf
     * dieser Methode überschrieben werden.</p>
     */
    OutputStream newOutputStream() throws IOException;

    /**
     * Erzeugt einen neuen {@link InputStream}, mit dem aus der Resource gelesen werden kann. Dabei sind jene Daten zu erwarten,
     * die mit dem letzten Aufruf von {@link #newOutputStream()} geschrieben wurden.
     */
    InputStream newInputStream() throws IOException;

    /**
     * Gibt an, wo die Resource hinterlegt ist. Kann <code>null</code> sein, wenn die Angabe im Rahmen der
     * Implementierung nicht sinnvoll ist (z.B. in-memory Resourcen).
     */
    URI getURI();

}
