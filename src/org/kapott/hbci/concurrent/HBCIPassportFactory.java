package org.kapott.hbci.concurrent;

import org.kapott.hbci.passport.HBCIPassport;

/**
 * Implementierungen dieser Schnittstelle werden verwendet, um bei der Initialisierung
 * einer {@link HBCIRunnable} das Passport zu erzeugen.
 *
 * @author Hendrik Schnepel
 */
public interface HBCIPassportFactory {

    /**
     * Gibt ein neues Passport entsprechend der Implementierung zurück.
     */
    HBCIPassport createPassport() throws Exception;

}
