package org.kapott.hbci.callback;

import java.util.Date;

import org.kapott.hbci.passport.HBCIPassport;

/**
 * Implementierung, die für alle Aufrufe eine {@link UnsupportedOperationException} wirft.
 *
 * <p>Diese Klasse vereinfacht die Initialisierung der <em>HBCI4Java</em>-Umgebung in Multithread-
 * Anwendungen, die im Haupt-Thread keine echte Interaktion mit <em>HBCI4Java</em> erwarten bzw.
 * sogar sicherstellen wollen.
 *
 * <p><code>
 * HBCIUtils.init(new Properties(), new HBCICallbackUnsupported());<br />
 * //...
 * </code></p>
 *
 * @author Hendrik Schnepel
 */
public class HBCICallbackUnsupported implements HBCICallback {

    @Override
    public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData)
    {
        throw new UnsupportedOperationException("Unexpected HBCI callback");
    }

    @Override
    public void status(HBCIPassport passport, int statusTag, Object o)
    {
        throw new UnsupportedOperationException("Unexpected HBCI callback");
    }

    @Override
    public void status(HBCIPassport passport, int statusTag, Object[] o)
    {
        throw new UnsupportedOperationException("Unexpected HBCI callback");
    }

    @Override
    public void log(String msg, int level, Date date, StackTraceElement trace)
    {
        throw new UnsupportedOperationException("Unexpected HBCI callback");
    }

    @Override
    public boolean useThreadedCallback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData)
    {
        throw new UnsupportedOperationException("Unexpected HBCI callback");
    }

}
