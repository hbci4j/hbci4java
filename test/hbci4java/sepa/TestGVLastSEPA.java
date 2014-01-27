/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/hbci4java/ddv/PCSCTest.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/11/24 21:59:37 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.sepa;

import hbci4java.AbstractTest;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.structures.Value;

/**
 * Testet das Erstellen von SEPA-Basis-Lastschriften
 
 Folgende Angaben sind für das Nachrichtenformat für SEPA-Lastschriften (pain.008) erforderlich: 
- Art des Verfahrens (Basis- oder Firmen-Lastschrift, <LclInstrm>) 
- Art der Lastschrift (einmalige, erste, wieder-kehrende, letzte Lastschrift, 
<SeqTp>) 
- Name des Zahlungsempfängers (<Cdtr><Nm>) 
- Gläubiger-Identifikationsnummer des Zahlungsempfängers (<CdtrSchmeId>) 
- IBAN des Zahlungskontos des Zahlungsempfängers, auf dem die Gutschrift 
vorgenommen werden soll (<CdtrAcct>) 
- BIC des Kreditinstituts des Zahlungsempfängers (<CdtrAgt>) 
- Name des Zahlungspflichtigen (<Dbtr><Nm>) 
- IBAN des Zahlungskontos des Zahlungspflichtigen (<DbtrAcct>) 
- BIC des Kreditinstituts des Zahlungspflichtigen (<DbtrAgt>) 
- Eindeutige Mandatsreferenz (<MndtId>) 
- Datum der Unterschrift des SEPA-Lastschriftmandats, sofern dieses vom Zahlungspflichtigen erteilt wird, bzw. Datum der Mitteilung über die Weiternutzung einer Einzugsermächtigung (<DtOfSgntr>) 
- Höhe des Einzugsbetrags (<InstdAmt>) 
- Angaben zum Verwendungszweck (<RmtInf>) 
- Name der Referenzpartei des Zahlungspflichtigen (falls im SEPALastschriftmandat vorhanden, <UltmtDbtr>) 
- Identifikationscode der Referenzpartei des Zahlungspflichtigen 
(falls im SEPA-Lastschriftmandat vorhanden, <Dbtr><Id>) 
- Fälligkeitsdatum des Einzugs (<ReqdColltnDt>) 
 
 
 */
public class TestGVLastSEPA extends AbstractTest
{
  private final static int LOGLEVEL = HBCIUtils.LOG_INFO;
  private final static Map<Integer,String> settings = new HashMap<Integer,String>()
  {{
    // Demo-Konto bei der ApoBank
    put(HBCICallback.NEED_COUNTRY,         "DE");
    put(HBCICallback.NEED_BLZ,             "30060601");
    put(HBCICallback.NEED_CUSTOMERID,      "0001956434");
    put(HBCICallback.NEED_FILTER,          "Base64");
    put(HBCICallback.NEED_HOST,            "hbcibanking.apobank.de/fints_pintan/receiver");
    put(HBCICallback.NEED_PASSPHRASE_LOAD, "test");
    put(HBCICallback.NEED_PASSPHRASE_SAVE, "test");
    put(HBCICallback.NEED_PORT,            "443");
    put(HBCICallback.NEED_PT_PIN,          "11111");
    put(HBCICallback.NEED_PT_TAN,          "123456"); // hier geht jede 6-stellige Zahl
    put(HBCICallback.NEED_USERID,          "0001956434");
    put(HBCICallback.NEED_PT_SECMECH,      "900"); // wird IMHO nicht benoetigt, weil es beim Demo-Account eh nur dieses eine Verfahren gibt
    put(HBCICallback.NEED_CONNECTION,      ""); // ignorieren
    put(HBCICallback.CLOSE_CONNECTION,     ""); // ignorieren
  }};
  
  private static File dir             = null;
  
  private HBCIPassportPinTan passport = null;
  private HBCIHandler handler         = null;
  
  /**
   * Testet das Erstellen einer SEPA-Basis-Lastschrift.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    HBCIJobImpl job = (HBCIJobImpl) handler.newJob("Ueb");
    
    // wir nehmen einfach das erste verfuegbare Konto
    job.setParam("src",passport.getAccounts()[0]);
    job.setParam("dst",passport.getAccounts()[0]);
    job.setParam("btg",new Value(1L,"EUR"));
    job.setParam("usage","test");
    job.setParam("name","test");
    job.setParam("key","51");
    
    job.addToQueue();
   
    SEG seg = job.createJobSegment(0);
    seg.validate();
    String msg = seg.toString(0);
    Assert.assertEquals("HKUEB:0:5+0001956434:EUR:280:30060601+0001956434:EUR:280:30060601+TEST++0,01:EUR+51++TEST'",msg);
  }
  
  /**
   * Erzeugt das Passport-Objekt.
   * @throws Exception
   */
  @Before
  public void beforeTest() throws Exception
  {
    Properties props = new Properties();
    props.put("log.loglevel.default",Integer.toString(LOGLEVEL));
    props.put("infoPoint.enabled",Boolean.FALSE.toString());
    
    props.put("client.passport.PinTan.filename",dir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".pt");
    props.put("client.passport.PinTan.init","1");
    props.put("client.passport.PinTan.checkcert","0"); // Check der SSL-Zertifikate abschalten - brauchen wir nicht fuer den Test
    
    // falls noetig
    props.put("client.passport.PinTan.proxy",""); // host:port
    props.put("client.passport.PinTan.proxyuser","");
    props.put("client.passport.PinTan.proxypass","");
    
    HBCICallback callback = new HBCICallbackConsole()
    {
      public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData)
      {
        // haben wir einen vordefinierten Wert?
        String value = settings.get(reason);
        if (value != null)
        {
          retData.replace(0,retData.length(),value);
          return;
        }

        // Ne, dann an Super-Klasse delegieren
        super.callback(passport, reason, msg, datatype, retData);
      }
    };
    
    HBCIUtils.init(props,callback);
    this.passport = (HBCIPassportPinTan) AbstractHBCIPassport.getInstance("PinTan");
    
    // init handler
    this.handler = new HBCIHandler("300",passport);

    // dump bpd
    // this.dump("BPD",this.passport.getBPD());
    
    // Liste der unterstuetzten Geschaeftsvorfaelle ausgeben
    // this.dump("Supported GV",this.handler.getSupportedLowlevelJobs());
  }
  
  /**
   * Schliesst das Passport-Objekt und loescht die Passport-Datei.
   * @throws Exception
   */
  @After
  public void afterTest() throws Exception
  {
    try
    {
      if (this.passport != null)
        this.passport.close();
      
      File file = new File(this.passport.getFileName());
      if (!file.delete())
        throw new Exception("unable to delete " + file);
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
      }
    }
  }
  
  /**
   * Erzeugt das Passport-Verzeichnis.
   * @throws Exception
   */
  @BeforeClass
  public static void beforeClass() throws Exception
  {
    String tmpDir = System.getProperty("java.io.tmpdir","/tmp");
    dir = new File(tmpDir,"hbci4java-junit-" + System.currentTimeMillis());
    dir.mkdirs();
  }
  
  /**
   * Loescht das Passport-Verzeichnis.
   * @throws Exception
   */
  @AfterClass
  public static void afterClass() throws Exception
  {
    if (!dir.delete())
      throw new Exception("unable to delete " + dir);
  }
  
  private void dump(String name, Properties props)
  {
    System.out.println("--- BEGIN: " + name + " -----");
    Iterator keys = props.keySet().iterator();
    while (keys.hasNext())
    {
      Object key = keys.next();
      System.out.println(key + ": " + props.get(key));
    }
    System.out.println("--- END: " + name + " -----");
  }

}
