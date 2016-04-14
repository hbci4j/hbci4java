/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.manager;

/**
 * Kapselt die Infos zu einer Bank.
 */
public class BankInfo
{
    private String blz;
    private String bic;
    private String checksumMethod;
    private String location;
    private String name;
    private String pinTanAddress;
    private HBCIVersion pinTanVersion;
    private String rdhAddress;
    private HBCIVersion rdhVersion;
    
    /**
     * ct.
     */
    private BankInfo()
    {
    }
    
    /**
     * Liefert die BLZ.
     * @return die BLZ.
     */
    public String getBlz() {
        return blz;
    }
    
    /**
     * Speichert die BLZ.
     * @param blz die BLZ.
     */
    public void setBlz(String blz) {
        this.blz = blz;
    }
    
    /**
     * Liefert die BIC.
     * @return die BIC.
     */
    public String getBic() {
        return bic;
    }
    
    /**
     * Speichert die BIC.
     * @param bic die BIC.
     */
    public void setBic(String bic) {
        this.bic = bic;
    }
    
    /**
     * Liefert die Nummer des Pruefziffern-Verfahrens.
     * @return die Nummer des Pruefziffern-Verfahrens.
     */
    public String getChecksumMethod() {
        return checksumMethod;
    }
    
    /**
     * Speichert die Nummer des Pruefziffern-Verfahrens.
     * @param checksumMethod die Nummer des Pruefziffern-Verfahrens.
     */
    public void setChecksumMethod(String checksumMethod) {
        this.checksumMethod = checksumMethod;
    }
    
    /**
     * Liefert den Ort der Bank.
     * @return Ort der Bank.
     */
    public String getLocation() {
        return location;
    }
    
    /**
     * Speichert den Ort der Bank.
     * @param location der Ort der Bank.
     */
    public void setLocation(String location) {
        this.location = location;
    }
    
    /**
     * Liefert den Namen der Bank.
     * @return der Name der Bank.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Speichert den Namen der Bank.
     * @param name der Name derBank.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Liefert die HBCI-URL fuer das Verfahren PIN/TAN.
     * @return die HBCI-URL fuer das Verfahren PIN/TAN.
     */
    public String getPinTanAddress() {
        return pinTanAddress;
    }
    
    /**
     * Speichert die HBCI-URL fuer das Verfahren PIN/TAN.
     * @param pinTanAddress die HBCI-URL fuer das Verfahren PIN/TAN.
     */
    public void setPinTanAddress(String pinTanAddress) {
        this.pinTanAddress = pinTanAddress;
    }
    
    /**
     * Liefert die HBCI-Version fuer das Verfahren PIN/TAN.
     * @return die HBCI-Version fuer das Verfahren PIN/TAN.
     */
    public HBCIVersion getPinTanVersion() {
        return pinTanVersion;
    }
    
    /**
     * Speichert die HBCI-Version fuer das Verfahren PIN/TAN.
     * @param pinTanVersion die HBCI-Version fuer das Verfahren PIN/TAN.
     */
    public void setPinTanVersion(HBCIVersion pinTanVersion) {
        this.pinTanVersion = pinTanVersion;
    }
    
    /**
     * Liefert die Server-Adresse fuer das Verfahren Schluesseldatei.
     * @return die Server-Adresse fuer das Verfahren Schluesseldatei.
     */
    public String getRdhAddress() {
        return rdhAddress;
    }
    
    /**
     * Speichert die Server-Adresse fuer das Verfahren Schluesseldatei.
     * @param rdhAddress die Server-Adresse fuer das Verfahren Schluesseldatei.
     */
    public void setRdhAddress(String rdhAddress) {
        this.rdhAddress = rdhAddress;
    }
    
    /**
     * Speichert die HBCI-Version fuer das Verfahren Schluesseldatei.
     * @return die HBCI-Version fuer das Verfahren Schluesseldatei.
     */
    public HBCIVersion getRdhVersion() {
        return rdhVersion;
    }
    
    /**
     * Liefert die HBCI-Version fuer das Verfahren Schluesseldatei.
     * @param rdhVersion die HBCI-Version fuer das Verfahren Schluesseldatei.
     */
    public void setRdhVersion(HBCIVersion rdhVersion) {
        this.rdhVersion = rdhVersion;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.blz);
        sb.append(": ");
        sb.append(this.name);
        return sb.toString();
    }
    
    /**
     * Parst die BankInfo-Daten aus einer Zeile der blz.properties.
     * @param text der Text (Value) aus der blz.properties.
     * @return das BankInfo-Objekt. Niemals NULL sondern hoechstens ein leeres Objekt.
     */
    static BankInfo parse(String text)
    {
        BankInfo info = new BankInfo();
        if (text == null || text.length() == 0)
            return info;
        
        String[] cols = text.split("\\|");
        info.setName(getValue(cols,0));
        info.setLocation(getValue(cols,1));
        info.setBic(getValue(cols,2));
        info.setChecksumMethod(getValue(cols,3));
        info.setRdhAddress(getValue(cols,4));
        info.setPinTanAddress(getValue(cols,5));
        info.setRdhVersion(HBCIVersion.byId(getValue(cols,6)));
        info.setPinTanVersion(HBCIVersion.byId(getValue(cols,7)));

        return info;
    }
    
    /**
     * Liefert den Wert aus der angegebenen Spalte.
     * @param cols die Werte.
     * @param idx die Spalte - beginnend bei 0.
     * @return der Wert der Spalte oder NULL, wenn er nicht existiert.
     * Die Funktion wirft keine {@link ArrayIndexOutOfBoundsException}
     */
    private static String getValue(String[] cols, int idx)
    {
        if (cols == null || idx >= cols.length)
            return null;
        return cols[idx];
    }
}
