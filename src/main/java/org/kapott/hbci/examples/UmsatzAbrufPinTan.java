package org.kapott.hbci.examples;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.GV_Result.GVRSaldoReq;
import org.kapott.hbci.callback.AbstractHBCICallback;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIVersion;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

/**
 * Demo zum Abruf von Umsaetzen per PIN/TAN-Verfahren.
 * 
 * Die folgende Demo zeigt mit dem minimal noetigen Code, wie eine Umsatz-Abfrage
 * fuer ein Konto durchgefuehrt werden kann. Hierzu wird der Einfachheit halber
 * das Verfahren PIN/TAN verwendet, da es von den meisten Banken unterstuetzt wird.
 * 
 * Trage vor dem Ausfuehren des Programms die Zugangsdaten zu deinem Konto ein.
 */
public class UmsatzAbrufPinTan
{
  /**
   * Die BLZ deiner Bank.
   */
  private final static String BLZ = "12345678";
  
  /**
   * Deine Benutzerkennung.
   */
  private final static String USER = "1234567890";
  
  /**
   * Deine PIN.
   */
  private final static String PIN = "12345";
  
  /**
   * Die zu verwendende HBCI-Version.
   */
  private final static HBCIVersion VERSION = HBCIVersion.HBCI_300;
  
  /**
   * Main-Methode.
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {
    // HBCI4Java initialisieren
    // In "props" koennen optional Kernel-Parameter abgelegt werden, die in der Klasse
    // org.kapott.hbci.manager.HBCIUtils (oben im Javadoc) beschrieben sind.
    Properties props = new Properties();
    HBCIUtils.init(props,new MyHBCICallback());

    // In der Passport-Datei speichert HBCI4Java die Daten des Bankzugangs (Bankparameterdaten, Benutzer-Parameter, etc.).
    // Die Datei kann problemlos geloescht werden. Sie wird beim naechsten mal automatisch neu erzeugt,
    // wenn der Parameter "client.passport.PinTan.init" den Wert "1" hat (siehe unten).
    // Wir speichern die Datei der Einfachheit halber im aktuellen Verzeichnis.
    final File passportFile = new File("testpassport.dat");

    // Wir setzen die Kernel-Parameter zur Laufzeit. Wir koennten sie alternativ
    // auch oben in "props" setzen.
    HBCIUtils.setParam("client.passport.default","PinTan"); // Legt als Verfahren PIN/TAN fest.
    HBCIUtils.setParam("client.passport.PinTan.filename",passportFile.getAbsolutePath());
    HBCIUtils.setParam("client.passport.PinTan.init","1");

    // Erzeugen des Passport-Objektes.
    HBCIPassport passport = AbstractHBCIPassport.getInstance();
    
    // Konfigurieren des Passport-Objektes.
    // Das kann alternativ auch alles ueber den Callback unten geschehen
    
    // Das Land.
    passport.setCountry("DE");
    
    // Server-Adresse angeben. Koennen wir entweder manuell eintragen oder direkt von HBCI4Java ermitteln lassen
    BankInfo info = HBCIUtils.getBankInfo(BLZ);
    passport.setHost(info.getPinTanAddress());
    
    // TCP-Port des Servers. Bei PIN/TAN immer 443, da das ja ueber HTTPS laeuft.
    passport.setPort(443);
    
    // Art der Nachrichten-Codierung. Bei Chipkarte/Schluesseldatei wird
    // "None" verwendet. Bei PIN/TAN kommt "Base64" zum Einsatz.
    passport.setFilterType("Base64");
    
    // Das Handle ist die eigentliche HBCI-Verbindung zum Server
    HBCIHandler handle = null;

    try
    {
      // Verbindung zum Server aufbauen
      handle = new HBCIHandler(VERSION.getId(),passport);

      // Wir verwenden einfach das erste Konto, welches wir zur Benutzerkennung finden
      Konto[] konten = passport.getAccounts();
      if (konten == null || konten.length == 0)
        error("Keine Konten ermittelbar");

      log("Anzahl Konten: " + konten.length);
      Konto k = konten[1];

      // 1. Auftrag fuer das Abrufen des Saldos erzeugen
      HBCIJob saldoJob = handle.newJob("SaldoReq");
      saldoJob.setParam("my",k); // festlegen, welches Konto abgefragt werden soll.
      saldoJob.addToQueue(); // Zur Liste der auszufuehrenden Auftraege hinzufuegen

      // 2. Auftrag fuer das Abrufen der Umsaetze erzeugen
      HBCIJob umsatzJob = handle.newJob("KUmsAll");
      umsatzJob.setParam("my",k); // festlegen, welches Konto abgefragt werden soll.
      umsatzJob.addToQueue(); // Zur Liste der auszufuehrenden Auftraege hinzufuegen
      
      // Hier koennen jetzt noch weitere Auftraege fuer diesen Bankzugang hinzugefuegt
      // werden. Z.Bsp. Ueberweisungen.

      // Alle Auftraege aus der Liste ausfuehren.
      HBCIExecStatus status = handle.execute();
      
      // Pruefen, ob die Kommunikation mit der Bank grundsaetzlich geklappt hat
      if (!status.isOK())
        error(status.toString());

      // Auswertung des Saldo-Abrufs.
      GVRSaldoReq saldoResult = (GVRSaldoReq) saldoJob.getJobResult();
      if (!saldoResult.isOK())
        error(saldoResult.toString());
      
      Value s = saldoResult.getEntries()[0].ready.value;
      log("Saldo: " + s.toString());


      // Das Ergebnis des Jobs koennen wir auf "GVRKUms" casten. Jobs des Typs "KUmsAll"
      // liefern immer diesen Typ.
      GVRKUms result = (GVRKUms) umsatzJob.getJobResult();

      // Pruefen, ob der Abruf der Umsaetze geklappt hat
      if (!result.isOK())
        error(result.toString());
      
      // Alle Umsatzbuchungen ausgeben
      List<UmsLine> buchungen = result.getFlatData();
      for (UmsLine buchung:buchungen)
      {
        StringBuilder sb = new StringBuilder();
        sb.append(buchung.valuta);
        
        Value v = buchung.value;
        if (v != null)
        {
          sb.append(": ");
          sb.append(v);
        }
        
        List<String> zweck = buchung.usage;
        if (zweck != null && zweck.size() > 0)
        {
          sb.append(" - ");
          // Die erste Zeile des Verwendungszwecks ausgeben
          sb.append(zweck.get(0));
        }
        
        // Ausgeben der Umsatz-Zeile
        log(sb.toString());
      }
    }
    finally
    {
      // Sicherstellen, dass sowohl Passport als auch Handle nach Beendigung geschlossen werden.
      if (handle !=null)
        handle.close();
      
      if (passport != null)
        passport.close();
    }
    
  }
  
  /**
   * Ueber diesen Callback kommuniziert HBCI4Java mit dem Benutzer und fragt die benoetigten
   * Informationen wie Benutzerkennung, PIN usw. ab.
   */
  private static class MyHBCICallback extends AbstractHBCICallback
  {
    /**
     * @see org.kapott.hbci.callback.HBCICallback#log(java.lang.String, int, java.util.Date, java.lang.StackTraceElement)
     */
    @Override
    public void log(String msg, int level, Date date, StackTraceElement trace)
    {
      // Ausgabe von Log-Meldungen bei Bedarf
      // System.out.println(msg);
    }

    /**
     * @see org.kapott.hbci.callback.HBCICallback#callback(org.kapott.hbci.passport.HBCIPassport, int, java.lang.String, int, java.lang.StringBuffer)
     */
    @Override
    public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData)
    {
      // Diese Funktion ist wichtig. Ueber die fragt HBCI4Java die benoetigten Daten von uns ab.
      switch (reason)
      {
        // Mit dem Passwort verschluesselt HBCI4Java die Passport-Datei.
        // Wir nehmen hier der Einfachheit halber direkt die PIN. In der Praxis
        // sollte hier aber ein staerkeres Passwort genutzt werden.
        // Die Ergebnis-Daten muessen in dem StringBuffer "retData" platziert werden.
        case NEED_PASSPHRASE_LOAD:
        case NEED_PASSPHRASE_SAVE:
          retData.replace(0,retData.length(),PIN);
          break;

        // PIN wird benoetigt
        case NEED_PT_PIN:
          retData.replace(0,retData.length(),PIN);
          break;

        // BLZ wird benoetigt
        case NEED_BLZ:
          retData.replace(0,retData.length(),BLZ);
          break;
          
        // Die Benutzerkennung
        case NEED_USERID:
          retData.replace(0,retData.length(),USER);
          break;
          
        // Die Kundenkennung. Meist identisch mit der Benutzerkennung.
        // Bei manchen Banken kann man die auch leer lassen
        case NEED_CUSTOMERID:
          retData.replace(0,retData.length(),USER);
          break;

        // Manche Fehlermeldungen werden hier ausgegeben
        case HAVE_ERROR:
          UmsatzAbrufPinTan.log(msg);
          break;
      
        default:
          // Wir brauchen nicht alle der Callbacks
          break;
      
      }
    }

    /**
     * @see org.kapott.hbci.callback.HBCICallback#status(org.kapott.hbci.passport.HBCIPassport, int, java.lang.Object[])
     */
    @Override
    public void status(HBCIPassport passport, int statusTag, Object[] o)
    {
      // So aehnlich wie log(String,int,Date,StackTraceElement) jedoch fuer Status-Meldungen.
    }
    
  }

  /**
   * Gibt die angegebene Meldung aus.
   * @param msg die Meldung.
   */
  private static void log(String msg)
  {
    System.out.println(msg);
  }

  /**
   * Beendet das Programm mit der angegebenen Fehler-Meldung.
   * @param msg die Meldung.
   */
  private static void error(String msg)
  {
    System.err.println(msg);
    System.exit(1);
  }

}
