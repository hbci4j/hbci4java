/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **********************************************************************/

package org.hbci4java;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Security;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import org.hbci4java.HBCI4JavaAccess.Type;
import org.hbci4java.log.HBCI4JavaLogger;
import org.hbci4java.log.HBCI4JavaLoggerCallback;
import org.kapott.cryptalgs.CryptAlgs4JavaProvider;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.tools.StringUtil;

/**
 * Das ist der neue nicht-statische Einstiegspunkt in die API von HBCI4Java.
 */
public class HBCI4JavaClient implements AutoCloseable
{
  /**
   * Die HBCI-Produktregistrierung von HBCI4Java für Test-Zwecke.
   * !!!!!! ACTHUNG !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
   * Beantrage unter https://www.fints.org/de/hersteller/produktregistrierung bitte eine eigene
   * Registrierung. Die hier genannte ist nur für Test-Zwecke gedacht und kann jederzeit
   * von der Deutschen Kreditwirtschaft gesperrt werden
   */
  public final static String PRODUCT_ID = "36792786FA12F235F04647689";
  private final static String VERSION = HBCIUtils.class.getPackage().getImplementationVersion();

  private final static ThreadLocal<HBCI4JavaClient> THREADLOCAL = new ThreadLocal();
  private final static AtomicInteger CLIENT_COUNT = new AtomicInteger(0);
  
  private HBCI4JavaConfig config = null;
  private HBCICallback callback = null;
  private HBCI4JavaLogger logger = null;
  private List<HBCI4JavaSession> sessions = new LinkedList<>();
  private int clientId = 0;
  
  private Properties blzs = new Properties();
  private Map<String,BankInfo> banks = new HashMap<>();
  private ResourceBundle bundle = null;
  private Locale locale = null;
  
  
  /**
   * ct.
   * Hierbei wird die Default-Konfiguration verwendet.
   * @param callback der Callback.
   */
  public HBCI4JavaClient(HBCICallback callback)
  {
    this(null,callback);
  }

  /**
   * ct.
   * Der Client wird hierbei automatisch per ThreadLocal im aktuellen Thread registriert.
   * @param config die Konfiguration.
   * @param callback der Callback.
   */
  public HBCI4JavaClient(HBCI4JavaConfig config, HBCICallback callback)
  {
    try
    {
      THREADLOCAL.set(this);
      this.clientId = CLIENT_COUNT.getAndIncrement();
      this.config = config != null ? config : HBCI4JavaConfig.createDefault();
      this.callback = callback != null ? callback : new HBCICallbackConsole();
      this.logger = new HBCI4JavaLoggerCallback(this);
      this.logger.info("create hbci4java client [version %s, client-id: %s, thread-id: %s]",version(),this.clientId,Thread.currentThread().getId());
      this.init();
    }
    catch (HBCI_Exception he)
    {
      throw he;
    }
    catch (Exception e)
    {
      throw new HBCI_Exception("error while initializing HBCI4Java", e);
    }
  }
  
  /**
   * Erzeugt eine Default PIN/TAN-Session mit FinTS 3.0.
   * @param file die Passport-Datei.
   * @return die Session.
   */
  public HBCI4JavaSession createSession(File file)
  {
    final HBCI4JavaAccess a = new HBCI4JavaAccess(Type.PINTAN,file);
    final HBCIPassport passport = this.createPassport(a);
    return this.createSession(passport);
  }

  /**
   * Erzeugt eine Session mit dem angegebenen Passport.
   * @param passport der Passport.
   * @return die neue Session.
   */
  public HBCI4JavaSession createSession(HBCIPassport passport)
  {
    final HBCI4JavaSession session = new HBCI4JavaSession(this,passport);
    this.sessions.add(session);
    return session;
  }
  
  /**
   * Verwirft eine Session.
   * @param session die Session.
   */
  void discard(HBCI4JavaSession session)
  {
    this.sessions.remove(session);
  }
  
  /**
   * Erzeugt einen Passport.
   * @param access die Konfiguration des Zugangs.
   * @return der Passport.
   */
  public HBCIPassport createPassport(HBCI4JavaAccess access)
  {
    return AbstractHBCIPassport.getInstance(access.getType().getName(),access.getFile());
  }
  
  /**
   * Liefert den Callback. 
   * @return der Callback.
   */
  public HBCICallback getCallback()
  {
    return callback;
  }
  
  /**
   * Liefert die Konfiguration. 
   * @return die Konfiguration.
   */
  public HBCI4JavaConfig getConfig()
  {
    return config;
  }
  
  /**
   * Liefert den Logger. 
   * @return der Logger.
   */
  public HBCI4JavaLogger getLogger()
  {
    return logger;
  }
  
  /**
   * Liefert den Client des aktuellen Thread.
   * @return der Client des aktuellen Thead oder NULL, wenn keiner existiert.
   */
  public static HBCI4JavaClient getCurrent()
  {
    return THREADLOCAL.get();
  }
  
  /**
   * Schliesst die Instanz aus dem Thread.
   */
  public static void discard()
  {
    final HBCI4JavaClient client = getCurrent();
    if (client == null)
      return; // Kein Client zum Verwerfen da.
    
    client.close();
  }
  
  /**
   * @see java.lang.AutoCloseable#close()
   */
  public void close()
  {
    try
    {
      // Checken, ob wir noch offene Sessions haben. Wir iterieren ueber eine Kopie
      // der Liste, weil HBCI4JavaSession#close wiederrum HBCI4JavaClient#discard(HBCI4JavaSession) aufruft
      for (HBCI4JavaSession session:new ArrayList<>(this.sessions))
      {
        try
        {
          session.close();
        }
        catch (Exception e)
        {
          this.logger.error("unable to close session",e);
        }
      }
    }
    finally
    {
      THREADLOCAL.remove();
      this.blzs.clear();
      this.banks.clear();
      this.config.clear();
      this.sessions.clear();
      this.logger.info("closed hbci4java client [version %s, client-id: %s, thread-id: %s]",version(),this.clientId,Thread.currentThread().getId());
    }
  }
  
  /**
   * Liefert die Bank-Informationen zur angegebenen BLZ.
   * @param blz die BLZ.
   * @return die Bank-Informationen oder NULL, wenn zu der BLZ keine Informationen bekannt sind.
   */
  public BankInfo getBankInfo(String blz)
  {
    return this.banks.get(blz);
  }
  
  /**
   * Liefert eine Liste von Bank-Informationen, die zum angegebenen
   * Suchbegriff passen.
   *
   * @param query der Suchbegriff. Der Suchbegriff muss mindestens 3 Zeichen enthalten und ist nicht case-sensitive.
   * Der Suchbegriff kann im Ort der Bank oder in deren Namen enthalten sein. Oder die BLZ oder BIC beginnt mit diesem Text.
   * @return die Liste der Bank-Informationen. Die Ergebnis-Liste ist nach BLZ sortiert.
   * Die Funktion liefert niemals NULL sondern hoechstens eine leere Liste.
   */
  public List<BankInfo> searchBankInfo(String query)
  {
    if (query != null)
      query = query.trim();

    final List<BankInfo> list = new LinkedList<BankInfo>();
    if (query == null || query.length() < 3)
      return list;

    query = query.toLowerCase();

    for (BankInfo info:this.banks.values())
    {
      String blz = info.getBlz();
      String bic = info.getBic();
      String name = info.getName();
      String loc = info.getLocation();

      // Anhand der BLZ?
      if (blz != null && blz.startsWith(query))
      {
        list.add(info);
        continue;
      }

      // Anhand der BIC?
      if (bic != null && bic.toLowerCase().startsWith(query))
      {
        list.add(info);
        continue;
      }

      // Anhand des Namens?
      if (name != null && name.toLowerCase().contains(query))
      {
        list.add(info);
        continue;
      }
      // Anhand des Orts?
      if (loc != null && loc.toLowerCase().contains(query))
      {
        list.add(info);
        continue;
      }
    }

    Collections.sort(list, new Comparator<BankInfo>()
    {
      /**
       * @see java.util.Comparator#compare(java.lang.Object,java.lang.Object)
       */
      @Override
      public int compare ( BankInfo o1, BankInfo o2 )
      {
        if (o1 == null || o1.getBlz() == null)
          return -1;
        
        if (o2 == null || o2.getBlz() == null)
          return 1;

        return o1.getBlz().compareTo(o2.getBlz());
      }
    });

    return list;
  }
  
  /**
   * Übersetzt den angegebenen String.
   * @param s der String.
   * @param params optionale Platzhalter.
   * @return die Übersetzung.
   */
  public String tr(String s, Object... params)
  {
    try
    {
      s = this.bundle.getString(s);
    }
    catch (MissingResourceException re)
    {
      // tolerieren wir
      this.logger.error(null,re);
    }
    
    if (params == null || params.length == 0)
      return s;
    
    return MessageFormat.format(s,params);
  }
  
  /**
   * Liefert das konfigurierte Locale. 
   * @return das konfigurierte Locale.
   */
  public Locale getLocale()
  {
    return locale;
  }
  
  /**
   * Liefert die Versionsnummer.
   * @return die Version
   */
  public String version()
  {
    return VERSION != null ? VERSION : "DEV";
  }
  
  /**
   * Initialisiert den Client.
   * @throws IOException
   */
  private void init() throws IOException
  {
    if (Security.getProvider(CryptAlgs4JavaProvider.NAME) == null)
      Security.addProvider(new CryptAlgs4JavaProvider());
    
    final String product = this.config.getString("client.product.name",HBCIUtils.PRODUCT_ID);
    if (this.clientId == 0 && Objects.equals(product,HBCIUtils.PRODUCT_ID))
    {
      this.logger.warn("***********************************************************************************");
      this.logger.warn("** WARNING                                                                       **");
      this.logger.warn("**                                                                               **");
      this.logger.warn("** HBCI4Java is currently using a product registration that should               **");
      this.logger.warn("** ONLY be used for internal testing, not for production purpose!!               **");
      this.logger.warn("**                                                                               **");
      this.logger.warn("** Please go to https://www.fints.org/de/hersteller/produktregistrierung         **");
      this.logger.warn("** and create your own registration (it's free)                                  **");
      this.logger.warn("**                                                                               **");
      this.logger.warn("** After receiving your registration, add this line to your code:                **");
      this.logger.warn("** HBCIUtils.setParam(\"client.product.name\",\"<your registration>\");              **");
      this.logger.warn("**                                                                               **");
      this.logger.warn("** This test registration can be invalidated at any time!!                       **");
      this.logger.warn("**                                                                               **");
      this.logger.warn("***********************************************************************************");
    }
    
    this.initLocale();
    this.initBanks();
  }

  /**
   * Initialisiert die Locale-Daten.
   */
  private void initLocale()
  {
    final String lang    = this.config.getString("kernel.locale.language");
    final String country = this.config.getString("kernel.locale.country");
    final String variant = this.config.getString("kernel.locale.variant");

    this.locale = StringUtil.hasText(lang) ? new Locale(lang, country, variant) : Locale.getDefault();
    this.logger.debug("init locale [locale: %s]",locale.toString());
    this.bundle = ResourceBundle.getBundle("hbci4java-messages", locale);
  }

  /**
   * Lädt die Bankenliste.
   * @throws IOException
   */
  private void initBanks() throws IOException
  {
    final ClassLoader cl = this.getClass().getClassLoader();
    final String file = "blz.properties";
    final InputStream is = cl.getResourceAsStream(file);

    if (is == null)
      throw new InvalidUserDataException(this.tr("EXCMSG_BLZLOAD", file));

    try (is)
    {
      this.logger.debug("trying to load BLZ data [file: %s]",file);
      final InputStreamReader isr = new InputStreamReader(is, "UTF-8");
      this.blzs.load(isr);

      for (Entry<Object, Object> e:this.blzs.entrySet())
      {
        final String blz = (String) e.getKey();
        final String value = (String) e.getValue();

        final BankInfo info = BankInfo.parse(value);
        info.setBlz(blz);
        this.banks.put(blz, info);
      }
    }
  }
  
}
