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

package hbci4java.ddv;

import hbci4java.AbstractTest;

import java.io.File;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassportDDVPCSC;

/**
 * Testet den Zugriff auf eine Chipkarte via javax.smartcardio
 */
public class PCSCTest extends AbstractTest
{
  private static File dir = null;
  HBCIPassportDDVPCSC passport = null;
  
  /**
   * List die Daten aus der Karte.
   * @throws Exception
   */
  @Test
  public void testReadCardData() throws Exception
  {
    System.out.println("card id: " + passport.getCardId());
    System.out.println("user id: " + passport.getUserId());
    System.out.println("blz    : " + passport.getBLZ());
    System.out.println("host   : " + passport.getHost());
  }
  
  /**
   * Testet das Abrufen des Saldos.
   * @throws Exception
   */
  @Test
  public void testFetchSaldo() throws Exception
  {
    HBCIHandler handler = new HBCIHandler("210",passport);
    HBCIJob job = handler.newJob("SaldoReq");
    
    // wir nehmen wir die Saldo-Abfrage einfach das erste verfuegbare Konto
    job.setParam("my",passport.getAccounts()[0]);
    job.addToQueue();
    handler.execute();
  }
  
  /**
   * Erzeugt das Passport-Objekt.
   * @throws Exception
   */
  @Before
  public void beforeCard() throws Exception
  {
    Properties props = new Properties();
    props.put("client.passport.DDV.path",dir.getAbsolutePath() + "/");
    props.put("client.passport.DDV.entryidx","1");
    props.put("log.loglevel.default",Integer.toString(HBCIUtils.LOG_DEBUG2));
    HBCIUtils.init(props,new HBCICallbackConsole());
    this.passport = (HBCIPassportDDVPCSC) AbstractHBCIPassport.getInstance("DDVPCSC");
  }
  
  /**
   * Schliesst das Passport-Objekt.
   * @throws Exception
   */
  @After
  public void afterCard() throws Exception
  {
    try
    {
      if (this.passport != null)
        this.passport.close();
    }
    finally
    {
      HBCIUtils.done();
    }
  }
  
  /**
   * Erzeugt das Passport-Verzeichnis.
   * @throws Exception
   */
  @BeforeClass
  public static void beforeCardClass() throws Exception
  {
    String tmpDir = System.getProperty("java.io.tmpdir","/tmp");
    dir = new File(tmpDir,"ddvjava");
    dir.mkdirs();
  }
  
  /**
   * Loescht das Passport-Verzeichnis.
   * @throws Exception
   */
  @AfterClass
  public static void afterCardClass() throws Exception
  {
    dir = null;
    // TODO: Verzeichnis und Inhalt muesste mal noch geloescht werden.
  }
}



/**********************************************************************
 * $Log: PCSCTest.java,v $
 * Revision 1.1  2011/11/24 21:59:37  willuhn
 * @N Patch 33 - erster Code fuer PC/SC-Support via javax.smartcardio - noch nicht funktionsfaehig
 *
 **********************************************************************/