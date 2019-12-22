/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
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

package org.kapott.hbci.passport;

import java.io.File;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.storage.PassportData;
import org.kapott.hbci.passport.storage.PassportStorage;

/** <p>Passport-Klasse für RDH-Zugänge mit Sicherheitsmedium "Datei". Bei dieser Variante
    werden sowohl die HBCI-Zugangsdaten wie auch die kryptografischen Schlüssel für
    die Signierung/Verschlüsselung der HBCI-Nachrichten in einer Datei gespeichert.
    Der Dateiname kann dabei beliebig vorgegeben werden. Da diese Datei vertrauliche
    Informationen enthält, wird der Inhalt verschlüsselt abgespeichert.
    Vor dem Erzeugen bzw. Einlesen wird via Callback-Mechanismus nach einem Passwort
    gefragt, aus dem der Schlüssel zur Verschlüsselung/Entschlüsselung der Schlüsseldatei
    berechnet wird.</p><p>
    Wie auch bei {@link org.kapott.hbci.passport.HBCIPassportDDV} werden in
    der Schlüsseldatei zusätzliche Informationen gespeichert. Dazu gehören u.a. die BPD
    und die UPD sowie die HBCI-Version, die zuletzt mit diesem Passport benutzt wurde.
    Im Gegensatz zu den "Hilfsdateien" bei DDV-Passports darf die Schlüsseldatei bei
    RDH-Passports aber niemals manuell gelöscht werden, da dabei auch die kryptografischen
    Schlüssel des Kunden verlorengehen. Diese können nicht wieder hergestellt werden, so
    dass in einem solchen Fall ein manuelles Zurücksetzes des HBCI-Zuganges bei der Bank
    erfolgen muss!</p>
    <p>Die Schlüsseldateien, die <em>HBCI4Java</em> mit dieser Klasse erzeugt und verwaltet, sind
    <b>nicht kompatibel</b> zu den Schlüsseldateien anderer HBCI-Software (z.B. VR-NetWorld
    o.ä.). Es ist also nicht möglich, durch Auswahl des Sicherheitsverfahrens "RDH" oder "RDHNew" und
    Angabe einer schon existierenden Schlüsseldatei, die mit einer anderen HBCI-Software
    erstellt wurde, diese Schlüsseldatei unter <em>HBCI4Java</em> zu benutzen! Es ist jedoch im
    Prinzip möglich, mit der "anderen" Software die Kundenschlüssel sperren zu lassen und
    anschließend mit <em>HBCI4Java</em> eine völlig neue Schlüsseldatei zu erzeugen. Das hat aber zwei
    Nachteile: Zum einen muss nach dem Neuerzeugen der Schlüsseldatei auch ein neuer
    INI-Brief erzeugt und an die Bank gesandt werden, um die neuen Schlüssel freischalten
    zu lassen. Außerdem lässt sich natürlich die <em>HBCI4Java</em>-Schlüsseldatei nicht mehr
    in der "anderen" HBCI-Software benutzen. Ein Parallel-Betrieb verschiedener HBCI-Softwarelösungen,
    die alle auf dem RDH-Verfahren mit Sicherheitsmedium "Datei" (oder Diskette) basieren,
    ist meines Wissens nicht möglich.</p>
    <p>Ein weiterer Ausweg aus diesem Problem wäre, eine technische Beschreibung des
    Formates der Schlüsseldateien der "anderen" HBCI-Software zu besorgen und diese
    dem <a href="mailto:hbci4java@kapott.org">Autor</a> zukommen zu lassen, damit eine Passport-Variante
    implementiert werden kann, die mit Schlüsseldateien dieser "anderen" Software arbeiten kann.</p>
    @see org.kapott.hbci.tools.INILetter INILetter */
public class HBCIPassportRDHNew extends AbstractRDHSWFileBasedPassport
{
	private String profileVersion;
	
    /**
     * ct.
     * @param init
     * @param dummy
     */
    public HBCIPassportRDHNew(Object init,int dummy)
    {
        super(init);
    }
    
    /**
     * ct.
     * @param initObject
     */
    public HBCIPassportRDHNew(Object initObject)
    {
        this(initObject,0);
        setParamHeader("client.passport.RDHNew");

        String  filename=HBCIUtils.getParam(getParamHeader()+".filename");
        boolean init=HBCIUtils.getParam(getParamHeader()+".init","1").equals("1");
        
        if (filename==null)
            throw new NullPointerException(getParamHeader()+".filename must not be null");

        HBCIUtils.log("loading passport data from file "+filename,HBCIUtils.LOG_DEBUG);
        setFilename(filename);

        if (init) {
            HBCIUtils.log("loading data from file "+filename,HBCIUtils.LOG_DEBUG);

            setFilterType("None");
            setPort(new Integer(3000));

            if (!new File(filename).canRead())
            {
                HBCIUtils.log("have to create new passport file",HBCIUtils.LOG_WARN);
                askForMissingData(true,true,true,true,false,true,true);
                saveChanges();
            }
            
            PassportData data = PassportStorage.load(this,new File(filename));
            this.setBLZ(data.blz);
            this.setCountry(data.country);
            this.setHost(data.host);
            this.setPort(data.port);
            this.setUserId(data.userId);
            this.setCustomerId(data.customerId);
            this.setSysId(data.sysId);
            this.setSigId(data.sigId);
            this.setProfileVersion(data.profileVersion != null ? data.profileVersion : "");
            this.setHBCIVersion(data.hbciVersion);

            this.setBPD(data.bpd);
            this.setUPD(data.upd);
            this.setInstSigKey(data.instSigKey);
            this.setInstEncKey(data.instEncKey);
            this.setMyPublicSigKey(data.myPublicSigKey);
            this.setMyPrivateSigKey(data.myPrivateSigKey);
            this.setMyPublicEncKey(data.myPublicEncKey);
            this.setMyPrivateEncKey(data.myPrivateEncKey);
                
            if (askForMissingData(true,true,true,true,false,true,true))
                saveChanges();
        }
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#saveChanges()
     */
    public void saveChanges()
    {
        try
        {
            final PassportData data = new PassportData();
            data.country         = this.getCountry();
            data.blz             = this.getBLZ();
            data.host            = this.getHost();
            data.port            = this.getPort();
            data.userId          = this.getUserId();
            data.customerId      = this.getCustomerId();
            data.sysId           = this.getSysId();
            data.sigId           = this.getSigId();
            data.profileVersion  = this.getProfileVersion();
            data.hbciVersion     = this.getHBCIVersion();
            data.bpd             = this.getBPD();
            data.upd             = this.getUPD();
            data.instSigKey      = this.getInstSigKey();
            data.instEncKey      = this.getInstEncKey();
            data.myPublicSigKey  = this.getMyPublicSigKey();
            data.myPrivateSigKey = this.getMyPrivateSigKey();
            data.myPublicEncKey  = this.getMyPublicEncKey();
            data.myPrivateEncKey = this.getMyPrivateEncKey();

            PassportStorage.save(this,data,new File(this.getFilename()));
        }
        catch (HBCI_Exception he)
        {
            throw he;
        }
        catch (Exception e)
        {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_WRITEERR"),e);
        }
    }

    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#setProfileVersion(java.lang.String)
     */
    public void setProfileVersion(String version)
    {
    	this.profileVersion=version;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getProfileVersion()
     */
    public String getProfileVersion()
    {
        String ret=this.profileVersion;
        if (ret==null) {
            ret="";
        }

        if (ret.length()==0) {
            HBCIUtils.log("have to determine my rdh-profile-version, but have no information about it yet", HBCIUtils.LOG_DEBUG);

            // es ist noch keine profilnummer bekannt, d.h. im passport-file
            // stand keine drin

            // das kann entweder daran liegen, dass es sich um ein "altes"
            // rdhnew-file handelte (in diesem fall ist die nummer "1"),
            // oder weil noch gar keine schlüssel im file gespeichert sind
            // und damit auch kein profil feststeht - in diesem fall verwenden
            // wir die höchste profil-nummer aus den BPD

            if (hasMySigKey()) {
                HBCIUtils.log("found user sig key in passport file, but no profile version, "+
                        "so I guess it is an old RDHnew file, which always stored RDH-1 keys",
                        HBCIUtils.LOG_DEBUG);
                // es gibt Schlüssel, aber keine profilVersion, also haben wir
                // gerade ein altes file gelesen, in dem diese Info noch nicht
                // drinstand
                ret="1";
            } else {
                HBCIUtils.log("no user keys found in passport - so we use the highest available profile",
                        HBCIUtils.LOG_DEBUG);

                // es gibt noch gar keine schlüssel - also nehmen wir die
                // höchste unterstützte profil-nummer

                String[][] methods=getSuppSecMethods();
                int        maxVersion=0;
                for (int i=0;i<methods.length;i++) {
                    String method=methods[i][0];
                    int    version=Integer.parseInt(methods[i][1]);

                    if (method.equals("RDH") && 
                            (version==1 || version==2 || version==10)) 
                    {
                        // es werden nur RDH-1, RDH-2 und RDH-10 betrachtet, weil
                        // alle anderen rdh-profile nicht für software-lösungen
                        // zugelassen sind
                        if (version>maxVersion) {
                            maxVersion=version;
                        }
                    }
                }

                if (maxVersion!=0) {
                    ret=Integer.toString(maxVersion);
                }
                HBCIUtils.log("using RDH profile "+ret+" taken from supported profiles (BPD)",
                        HBCIUtils.LOG_DEBUG);
            }
        }
        
        if (ret == null || ret.length() == 0)
        {
            ret = HBCIUtils.getParam(getParamHeader()+".defaultprofile",null);
            HBCIUtils.log("unable to determine rdh-profile-version using BPD, using default version " + ret, HBCIUtils.LOG_WARN);
        }

        // in jedem fall merken wir uns die gerade ermittelte profil-nummer
        setProfileVersion(ret);

        return ret;
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#resetPassphrase()
     */
    @Override
    public void resetPassphrase()
    {
    }
}
