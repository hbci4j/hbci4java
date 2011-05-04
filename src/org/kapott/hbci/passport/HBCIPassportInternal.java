
/*  $Id: HBCIPassportInternal.java,v 1.1 2011/05/04 22:37:43 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.passport;

import java.util.Properties;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.comm.Filter;
import org.kapott.hbci.manager.HBCIDialog;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.IHandlerData;
import org.kapott.hbci.status.HBCIMsgStatus;

/** Interface, welches alle Passport-Varianten implementieren müssen.
    Diese Schnittstelle wird nur intern verwendet. Sie beschreibt alle
    Methoden, die ein Passport zur Verfügung stellen muss, um von
    <em>HBCI4Java</em> benutzt werden zu können. Dieses Interface ist
    nicht zur Anwendung hin sichtbar (deshalb auch "<code>Internal</code>").*/
public interface HBCIPassportInternal
    extends HBCIPassport
{
    public String getPassportTypeName();
    
    public Comm getComm();
    public Filter getCommFilter();
    public void closeComm();
    public void setHBCIVersion(String hbciversion);
    public String getSysId();
    public String getCID();
    public String getSysStatus();
    public String getProfileMethod();
    public String getProfileVersion();

    public boolean needUserSig();
    public void setInstSigKey(HBCIKey key);
    public void setInstEncKey(HBCIKey key);
    public void clearMySigKey();
    public void clearMyEncKey();
    public void clearMyDigKey();
    public void setMyPublicSigKey(HBCIKey key);
    public void setMyPrivateSigKey(HBCIKey key);
    public void setMyPublicEncKey(HBCIKey key);
    public void setMyPrivateEncKey(HBCIKey key);
    public void setMyPublicDigKey(HBCIKey key);
    public void setMyPrivateDigKey(HBCIKey key);

    public String getInstSigKeyName();
    public String getInstSigKeyNum();
    public String getInstSigKeyVersion();
    public String getInstEncKeyName();
    public String getInstEncKeyNum();
    public String getInstEncKeyVersion();

    public String getMySigKeyName();
    public String getMySigKeyNum();
    public String getMySigKeyVersion();
    public String getMyEncKeyName();
    public String getMyEncKeyNum();
    public String getMyEncKeyVersion();

    public boolean canMixSecMethods();
    public String getLang();
    public Long getSigId();
    public String getCryptKeyType();
    public String getCryptFunction();
    public String getCryptAlg();
    public String getCryptMode();
    public String getSigFunction();
    public String getSigAlg();
    public String getSigMode();
    public String getHashAlg();

    public void setBPD(Properties bpd);
    public void setUPD(Properties upd);
    public void setSigId(Long sigid);
    public void setSysId(String sysid);
    public void setCID(String cid);
    public void incSigId();
    public void setProfileMethod(String method);
    public void setProfileVersion(String version);

    public HBCIKey[][] generateNewUserKeys();
    public byte[] hash(byte[] data);
    public byte[] sign(byte[] data);
    public boolean verify(byte[] data,byte[] sig);
    public byte[][] encrypt(byte[] plainMsg);
    public byte[] decrypt(byte[] cryptedKey,byte[] encryptedMsg);
    
    public Properties getParamSegmentNames();
    public Properties getJobRestrictions(String specname);
    public Properties getJobRestrictions(String gvname,String version);
    public void setPersistentData(String id,Object o);
    public Object getPersistentData(String id);
    
    public void resetPassphrase();
    public boolean isAnonymous();
    
    public void setParentHandlerData(IHandlerData handler);
    public IHandlerData getParentHandlerData();
    
    /* Diese Methode wird nach jeder Dialog-Initialisierung aufgerufen. Ein
     * Passport-Objekt kann den Status der Response mit Hilfe von msgStatus
     * auswerten. Durch Zurückgeben von "true" wird angezeigt, dass eine
     * erneute Dialog-Initialisierung stattfinden sollte (z.B. weil sich grund-
     * legende Zugangsdaten geändert haben, secMechs neu festgelegt wurden o.ä.) */
    public boolean postInitResponseHook(HBCIMsgStatus msgStatus, boolean anonDialog);
    
    /* Diese Methode wird aufgerufen, bevor ein "normaler" Dialog (also mit GVs)
     * geführt wird. */
    public void beforeCustomDialogHook(HBCIDialog dialog);
    
    /* Diese Methode wird aufgerufen, nachdem bei einem normalen Dialog die
     * Dialog-Initialisierung abgeschlossen ist. 
     * Wird im Moment nur von PinTan-Passports benutzt, um bei
     * Verwendung des Zweischritt-Verfahrens die Message-Liste zu patchen */
    public void afterCustomDialogInitHook(HBCIDialog dialog);
    
    /* Gibt zurück, wieviele GV-Segmente in einer Nachricht enthalten sein dürfen.
     * Normalerweise wird das schon durch die BPD bzw. die Job-Params festgelegt,
     * deswegen geben die meisten Passport-Implementierungen hier 0 zurück (also
     * keine weiteren Einschränkungen neben den BPD-Daten). Im Fall von PIN/TAN 
     * muss jedoch dafür gesorgt werden, dass tatsächlich nur ein einziges 
     * Auftragssegment in einer HBCI-Nachricht steht (weil sonst das "Signieren" 
     * mit einer TAN schwierig wird). Deswegen gibt die PIN/TAN-Implementierung 
     * dieser Methode 1 zurück.
     * In HBCIDialog.addTask() wird diese Methode aufgerufen, um festzustellen,
     * ob für den hinzuzufügenden Task eine neue Nachricht erzeugt werden muss
     * oder nicht.
     */
    public int getMaxGVSegsPerMsg();
}
