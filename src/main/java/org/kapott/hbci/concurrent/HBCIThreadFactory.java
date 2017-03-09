package org.kapott.hbci.concurrent;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementierung einer {@link ThreadFactory}, die für jeden Thread eine eigene Thread-Gruppe erzeugt.
 * Jede Gruppe ist mit {@link ThreadGroup#setDaemon(true)} konfiguriert, so dass mit Ende des einzelnen
 * Threads auch automatisch die Gruppe geschlossen wird.
 *
 * @see <code>README.MultiThreading</code>
 * @see <a href="https://groups.google.com/forum/#!msg/hbci4java/cOQrbPDC0Jo/rbm6jgMaMfcJ">https://groups.google.com/forum/#!msg/hbci4java/cOQrbPDC0Jo/rbm6jgMaMfcJ</a>
 * @author Hendrik Schnepel
 */
public class HBCIThreadFactory implements ThreadFactory {

    private static final AtomicLong SEQUENCE = new AtomicLong(0L);

    @Override
    public Thread newThread(Runnable runnable) {
        String id = String.valueOf(SEQUENCE.incrementAndGet());
        ThreadGroup threadGroup = new ThreadGroup("HBCI Single-Thread Group #" + id);

        // Wichtig, damit die Gruppe zusammen mit dem Thread geschlossen wird:
        // https://groups.google.com/forum/#!msg/hbci4java/cOQrbPDC0Jo/rbm6jgMaMfcJ
        threadGroup.setDaemon(true);

        return new Thread(threadGroup, runnable, "HBCI Thread #" + id);
    }

}
