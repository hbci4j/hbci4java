package org.kapott.hbci.concurrent;


import java.util.Properties;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassport;

/**
 * Basis-Klasse für Implementierungen von {@link Runnable}, die typische Aufgaben mit einem {@link HBCIPassport}
 * ausführen sollen.
 *
 * <p>Implementierungen müssen die Methode {@link #execute()} ergänzen.</p>
 *
 * <p>Bei Ausführung einer solchen {@link Runnable} passiert folgendes:</p>
 *
 * <ol>
 * <li>{@link HBCIUtils.initThread(properties, callback)} wird mit den Parametern aus dem Constructor aufgerufen.</li>
 * <li>Das Passport wird von der {@link HBCIPassportFactory} abgefragt und darüber wird der {@link HBCIHandler} erzeugt.</li>
 * <li>{@link #execute()} wird aufgerufen.</li>
 *     {@link HBCIPassport} und {@link HBCIHandler} sind über die Variablen <code>passport</code> bzw. <code>handler</code> verfügbar.</li>
 * <li>Abschließend werden Handler und Passport geschlossen, sowie {@link HBCIUtils#doneThread()} aufgerufen.</li>
 * </ol>
 *
 * @author Hendrik Schnepel
 */
public abstract class HBCIRunnable implements Runnable
{

    private final Properties properties;
    private final HBCICallback callback;
    private HBCIPassportFactory passportFactory;

    protected HBCIPassport passport = null;
    protected HBCIHandler handler = null;

    public HBCIRunnable(Properties properties, HBCICallback callback, HBCIPassportFactory passportFactory)
    {
        this.properties = properties;
        this.callback = callback;
        this.passportFactory = passportFactory;
    }

    @Override
    public final void run()
    {
        init();
        try
        {
            prepare();
            execute();
        }
        catch (Exception e)
        {
            HBCIUtils.log(e);
        }
        finally
        {
            done();
        }
    }

    private void init()
    {
        HBCIUtils.initThread(properties, callback);
    }

    private void prepare() throws Exception
    {
        passport = passportFactory.createPassport();
        if (passport != null)
        {
            String version = passport.getHBCIVersion();
            handler = new HBCIHandler((version.length() != 0) ? version : "plus", passport);
        }
    }

    protected abstract void execute() throws Exception;

    private void done()
    {
        if (handler != null)
        {
            handler.close();
        }
        if (passport != null)
        {
            passport.close();
        }
        HBCIUtils.doneThread();
    }

}
