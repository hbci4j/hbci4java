package org.kapott.hbci.passport;

/**
 * Gemeinsames Interface fuer chipkarten-basiere Passports.
 * Derzeit sind das HBCIPassportDDV, HBCIPassportDDVPCSC und HBCIPassportRSA.
 */
public interface HBCIPassportChipcard extends HBCIPassport
{
    /**
     * Schreiben der aktuellen Zugangsdaten auf die Chipkarte. Werden Zugangsdaten
     * des Passport verändert (z.B. mit {@link org.kapott.hbci.passport.HBCIPassport#setHost(String)},
     * so werden diese Daten durch die Methode {@link org.kapott.hbci.passport.HBCIPassport#saveChanges()}
     * <em>nicht</em> auf der Chipkarte gespeichert. Durch Aufruf dieser Methode
     * wird das Schreiben der aktuellen Zugangsdaten erzwungen. Zu den hiervon
     * betroffenen Daten zählen der Ländercode der Bank, die Bankleitzahl,
     * die Hostadresse des HBCI-Servers sowie die User-ID zur Anmeldung am
     * HBCI-Server.
     */
    public void saveBankData();
    
    /**
     * Gibt den Dateinamen für die zusätzliche Schlüsseldatei zurück.
     * Diese Datei enthält gecachte Daten, um das Initialisieren eines
     * {@link org.kapott.hbci.manager.HBCIHandler} mit einem DDV-Passport zu
     * beschleunigen. Defaultmäßig setzt sich der Dateiname aus einem
     * definiertbaren Prefix (Pfad) und der Seriennummer der Chipkarte zusammen.
     * Da diese Datei vertrauliche Daten enthält (z.B. die Kontodaten des
     * Bankkunden), wird diese Datei verschlüsselt. Vor dem erstmaligen Lesen
     * bzw. beim Erzeugen dieser Datei wird deshalb via Callback-Mechanismus
     * nach einem Passwort gefragt, das zur Erzeugung des kryptografischen
     * Schlüssels für die Verschlüsselung benutzt wird.
     * @return Dateiname der Cache-Datei
     */
    public String getFileName();

    /**
     * Legt den Dateinamen fuer die zusaetzliche Schluesseldatei fest.
     * @param filename
     */
    public void setFileName(String filename);
}


