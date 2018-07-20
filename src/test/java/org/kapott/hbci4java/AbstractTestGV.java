package org.kapott.hbci4java;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIVersion;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportPinTanMemory;
import org.kapott.hbci.status.HBCIExecStatus;

/**
 * Abstrakte Basis-Klasse fuer Unit-Tests, bei denen tatsaechlich Geschaeftsvorfaelle ausgefuehrt werden.
 */
public abstract class AbstractTestGV
{
    private final Map<Integer,String> callbackValues = new HashMap<Integer,String>();
    private Properties  params                = null;
    private HBCIPassportPinTanMemory passport = null;
    private HBCIHandler handler               = null;
    
    
//    /**
//     * Beispiel-Code fuer einen Test in einer abgeleiteten Klasse.
//     */
//    public void exampleTest()
//    {
//        this.execute(new Execution() {
//            @Override
//            public String getJobname() {
//                return "GVKUmsAll";
//            }
//        });
//    }

    /**
     * Erzeugt das Passport-Objekt.
     * @throws Exception
     */
    @Before
    public void beforeTest() throws Exception
    {
        this.params = new Properties();
        
        String configfile = System.getProperty("config",System.getProperty("user.dir") + File.separator + this.getClass().getSimpleName() + ".properties");
        File file = new File(configfile);
        
        if (file.exists() && file.canRead())
        {
            InputStream is = null;
            try
            {
                System.out.println("loading " + file);
                is = new BufferedInputStream(new FileInputStream(file));
                this.params.load(is);
            }
            finally
            {
                if (is != null)
                    is.close();
            }
        }
      
        // Presets fuer den Callback
        this.callbackValues.put(HBCICallback.NEED_COUNTRY,          params.getProperty("country",System.getProperty("country","DE")));
        this.callbackValues.put(HBCICallback.NEED_CUSTOMERID,       params.getProperty("customerid",System.getProperty("customerid")));
        this.callbackValues.put(HBCICallback.NEED_USERID,           params.getProperty("userid",System.getProperty("useris")));
        this.callbackValues.put(HBCICallback.NEED_PT_PIN,           params.getProperty("pin",System.getProperty("pin")));
        this.callbackValues.put(HBCICallback.NEED_PT_SECMECH,       params.getProperty("secmech",System.getProperty("secmech")));
        
        final String blz = params.getProperty("blz",System.getProperty("blz"));
        this.callbackValues.put(HBCICallback.NEED_BLZ,              blz);
        this.callbackValues.put(HBCICallback.NEED_PORT,             params.getProperty("port",System.getProperty("port","443")));
        this.callbackValues.put(HBCICallback.NEED_FILTER,           params.getProperty("filter",System.getProperty("filter","Base64")));
        this.callbackValues.put(HBCICallback.NEED_CONNECTION,       "");
        this.callbackValues.put(HBCICallback.CLOSE_CONNECTION,      "");

              
        // Initialisierungsparameter fuer HBCI4Java selbst
        Properties props = new Properties();
        props.put("log.loglevel.default",this.params.getProperty("log.loglevel.default",Integer.toString(HBCIUtils.LOG_DEBUG)));
        props.put("client.passport.PinTan.init","1");
        props.put("client.passport.PinTan.checkcert",this.params.getProperty("client.passport.PinTan.checkcert","1"));
        props.put("client.passport.PinTan.proxy",    this.params.getProperty("client.passport.PinTan.proxy",""));
        props.put("client.passport.PinTan.proxyuser",this.params.getProperty("client.passport.PinTan.proxyuser",""));
        props.put("client.passport.PinTan.proxypass",this.params.getProperty("client.passport.PinTan.proxypass",""));
      
        // Callback initialisieren.
        // Wenn wir passende Antworten in den Presets haben, koennen wir sie direkt
        // beantworten
        final HBCICallback callback = new HBCICallbackConsole()
        {
            /**
             * @see org.kapott.hbci.callback.HBCICallbackIOStreams#callback(org.kapott.hbci.passport.HBCIPassport, int, java.lang.String, int, java.lang.StringBuffer)
             */
            @Override
            public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData) {
                
                // haben wir einen vordefinierten Wert?
                String value = callbackValues.get(reason);
                if (value != null)
                {
                    retData.replace(0,retData.length(),value);
                    return;
                }
                
                // Ne, dann an Super-Klasse delegieren
                super.callback(passport, reason, msg, datatype, retData);
            }
        };
      
        // HBCI4Java initialisieren
        HBCIUtils.init(props,callback);

        ////////////////////////////////////////////////////////////////////////
        // Nach dem Initialisieren von HBCI4Java noch checken, ob wir den
        // Host selbst ermitteln koennen
        String host = params.getProperty("host");
        if (blz != null && host == null)
        {
            BankInfo bank = HBCIUtils.getBankInfo(blz);
            if (bank != null)
                host = bank.getPinTanAddress();
        }
        this.callbackValues.put(HBCICallback.NEED_HOST,host);
        //
        ////////////////////////////////////////////////////////////////////////

        this.passport = (HBCIPassportPinTanMemory) AbstractHBCIPassport.getInstance("PinTanMemory");
      
        // init handler
        this.handler = new HBCIHandler(this.params.getProperty("hbciversion",HBCIVersion.HBCI_300.getId()),this.passport);
    }
    
    /**
     * Fuehrt den Geschaeftsvorfall aus.
     * @param e der Geschaeftsvorfall.
     */
    protected final void execute(Execution e)
    {
        HBCIJob job = this.handler.newJob(e.getJobname());
        e.configure(job,this.passport,this.params);
        
        job.addToQueue();
        
        e.testStatus(this.handler.execute());
        e.testResult(job.getJobResult());
    }
    
    /**
     * Schliesst das Passport-Objekt.
     * @throws Exception
     */
    @After
    public void afterTest() throws Exception
    {
      try
      {
        if (this.passport != null)
          this.passport.close();
      }
      finally
      {
        try
        {
          if (this.handler != null)
            this.handler.close();
        }
        finally
        {
          HBCIUtils.done();
          this.handler = null;
          this.passport = null;
          this.params = null;
        }
      }
    }
    
    /**
     * Kapselt die eigentliche Job-Ausfuehrung.
     */
    public abstract class Execution
    {
        /**
         * Liefert den Namen des auszufuehrenden Geschaeftsvorfalls.
         * Die Basis-Implementierung liefert den Parameter "job" des Test.
         * @return der Name des auszufuehrenden Geschaeftsvorfalls.
         */
        public String getJobname()
        {
            return params.getProperty("job");
        }
        
        /**
         * Konfiguriert den Job.
         * Kann von abgeleiteten Klassen ueberschrieben werden, wenn der Job parametrisiert werden muss.
         * Die Basis-Implementierung ist leer.
         * @param job der auszufuehrende Job.
         * @param passport der Passport.
         * @param params die Parameter des Tests.
         */
        public void configure(HBCIJob job, HBCIPassport passport, Properties params)
        {
        }

        /**
         * Kann von abgeleiteten Klassen implementiert werden, um Tests am Ausfuehrungsstatus durchzufuehren.
         * Die Basis-Implementierung testet per {@link HBCIExecStatus#isOK()}.
         * @param status der Ausfuehrungsstatus.
         */
        public void testStatus(HBCIExecStatus status)
        {
            if (!status.isOK())
                System.err.println(status.getErrorString());
            Assert.assertTrue("Ausfuehrungsstatus nicht OK",status.isOK());
        }

        /**
         * Kann von abgeleiteten Klassen implementiert werden, um Tests Job-Ergebnis durchzufuehren.
         * Die Basis-Implementierung testet per {@link HBCIJobResult#isOK()}.
         * @param result das Job-Ergebnis.
         */
        public void testResult(HBCIJobResult result)
        {
            Assert.assertTrue("Job-Status nicht OK",result.isOK());
        }
    }
    
}
