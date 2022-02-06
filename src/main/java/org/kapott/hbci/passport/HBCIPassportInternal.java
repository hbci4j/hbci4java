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

import java.util.Properties;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.comm.Filter;
import org.kapott.hbci.dialog.DialogContext;
import org.kapott.hbci.dialog.DialogEvent;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.IHandlerData;

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

    /*
      Besondere Behandlung für Postbank
     */
    default boolean isSpecialTreatmentForPostbank() { return false; }

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
    
    /**
     * Wird bei einem Dialog-Event ausgefuehrt.
     * @param event das Event.
     * @param ctx der Dialog-Kontext.
     */
    public void onDialogEvent(DialogEvent event, DialogContext ctx);
    
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
