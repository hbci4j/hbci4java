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

}


