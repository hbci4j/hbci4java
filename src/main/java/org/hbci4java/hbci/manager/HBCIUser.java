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

package org.hbci4java.hbci.manager;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hbci4java.hbci.callback.HBCICallback;
import org.hbci4java.hbci.comm.Comm;
import org.hbci4java.hbci.dialog.DialogContext;
import org.hbci4java.hbci.dialog.HBCIDialogEnd;
import org.hbci4java.hbci.dialog.HBCIDialogInit;
import org.hbci4java.hbci.dialog.HBCIDialogLockKeys;
import org.hbci4java.hbci.dialog.HBCIDialogSync;
import org.hbci4java.hbci.dialog.HBCIProcess;
import org.hbci4java.hbci.dialog.HBCIProcessSepaInfo;
import org.hbci4java.hbci.dialog.HBCIProcessTanMedia;
import org.hbci4java.hbci.dialog.HBCIDialogEnd.Flag;
import org.hbci4java.hbci.dialog.HBCIDialogSync.Mode;
import org.hbci4java.hbci.exceptions.HBCI_Exception;
import org.hbci4java.hbci.exceptions.NeedKeyAckException;
import org.hbci4java.hbci.exceptions.ProcessException;
import org.hbci4java.hbci.passport.HBCIPassport;
import org.hbci4java.hbci.passport.HBCIPassportInternal;
import org.hbci4java.hbci.status.HBCIMsgStatus;
import org.hbci4java.hbci.structures.Konto;
import org.hbci4java.hbci.tools.StringUtil;

/**
 * Kapselt die authentifizierten Initialisierungsdialoge. Also im Wesentlichen alles, was mit den UPD zu tun hat.
 */
public final class HBCIUser implements IHandlerData
{
    public final static String UPD_KEY_HBCIVERSION = "_hbciversion";
    
    /**
     * In dem UPD-Property sind die TAN-Medienbezeichnungen gespeichert
     */
    public final static String UPD_KEY_TANMEDIA = "tanmedia.names";
    
    /**
     * In dem UPD-Property ist gespeichert, wann wir die SEPA-Infos (IBAN, BIC) abgerufen haben
     */
    public final static String UPD_KEY_FETCH_SEPAINFO = "_fetchedSepaInfo";
    
    /**
     * In dem UPD-Property ist gespeichert, wann wir die TAN-Medienbezeichnungen abgerufen haben
     */
    public final static String UPD_KEY_FETCH_TANMEDIA = "_fetchedTanMedia";
    
    private final static List<String> UPD_PROTECT_KEYS = Arrays.asList(UPD_KEY_FETCH_TANMEDIA,UPD_KEY_FETCH_SEPAINFO,UPD_KEY_TANMEDIA);

    private HBCIPassportInternal passport;
    private HBCIKernelImpl       kernel;
    private boolean              isAnon;
    private String               anonSuffix;

    /** @brief This constructor initializes a new user instance with the given values */
    public HBCIUser(HBCIKernelImpl kernel,HBCIPassportInternal passport,boolean forceAsParent)
    {
        this.kernel=kernel;
        if (forceAsParent || this.kernel.getParentHandlerData()==null) {
            // Dieser Fall tritt im HBCI4Java-PE ein, wenn ein HBCIInstitute()
            // erzeugt wird, ohne dass es einen HBCIHandler() gäbe
            this.kernel.setParentHandlerData(this);
        }
        
        this.passport=passport;
        if (forceAsParent || this.passport.getParentHandlerData()==null) {
            // Dieser Fall tritt im HBCI4Java-PE ein, wenn ein HBCIInstitute()
            // erzeugt wird, ohne dass es einen HBCIHandler() gäbe
            this.passport.setParentHandlerData(this);
        }

        this.isAnon=passport.isAnonymous();
        this.anonSuffix=isAnon?"Anon":"";
    }

    /**
     * @deprecated Stattdessen die Klasse "HBCIDialogEnd" verwenden.
     */
    @Deprecated
    private void doDialogEnd(String dialogid,String msgnum,boolean signIt,boolean cryptIt,boolean needCrypt)
    {
        HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_END,null);

        kernel.rawNewMsg("DialogEnd"+anonSuffix);
        kernel.rawSet("MsgHead.dialogid",dialogid);
        kernel.rawSet("MsgHead.msgnum",msgnum);
        kernel.rawSet("DialogEndS.dialogid",dialogid);
        kernel.rawSet("MsgTail.msgnum",msgnum);
        HBCIMsgStatus status=kernel.rawDoIt(!isAnon && signIt,
                                            !isAnon && cryptIt,
                                            !isAnon && needCrypt);
        HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_END_DONE,status);

        if (!status.isOK()) {
            HBCIUtils.log("dialog end failed: "+status.getErrorString(),HBCIUtils.LOG_ERR);
            
            String msg=HBCIUtilsInternal.getLocMsg("ERR_INST_ENDFAILED");
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreDialogEndErrors",msg+": "+status.getErrorString()))
                throw new ProcessException(msg,status);
        }
    }

    private void sendAndActivateNewUserKeys(HBCIKey[] sigKey,HBCIKey[] encKey)
    {
        try {
            HBCIUtils.log("Sende neue Benutzerschlüssel",HBCIUtils.LOG_INFO);
            
            String country=passport.getCountry();
            String blz=passport.getBLZ();
    
            String[] exponent = new String[2];
            String[] modulus = new String[2];
    
            for (int i=0;i<2;i++) {
                KeyFactory fac = KeyFactory.getInstance("RSA");
                
                RSAPublicKeySpec spec=null;
                if (i==0) {
                    spec=fac.getKeySpec(sigKey[0].key,RSAPublicKeySpec.class);
                } else if (i==1) {
                    spec=fac.getKeySpec(encKey[0].key,RSAPublicKeySpec.class);
                } else {
                }
                
                byte[] ba=spec.getPublicExponent().toByteArray();
                int    len=ba.length;
                int    startpos=0;
                while (startpos<len && ba[startpos]==0) {
                    startpos++;
                }
                exponent[i] = new String(ba,startpos,len-startpos,Comm.ENCODING);

                ba=spec.getModulus().toByteArray();
                len=ba.length;
                startpos=0;
                while (startpos<len && ba[startpos]==0) {
                    startpos++;
                }
                modulus[i] = new String(ba,startpos,len-startpos,Comm.ENCODING);
            }
    
            if (!passport.hasMySigKey()) {
                // es gibt noch gar keine Nutzerschluessel
                
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_SEND_KEYS,null);

                // sigid updated
                passport.setSigId(Long.valueOf(1));
                
                // schluessel senden
                kernel.rawNewMsg("SendKeys");
                kernel.rawSet("Idn.KIK.blz", blz);
                kernel.rawSet("Idn.KIK.country", country);
                kernel.rawSet("Idn.customerid", passport.getCustomerId());
                kernel.rawSet("Idn.sysid",passport.getSysId());
        
                kernel.rawSet("KeyChange.KeyName.KIK.blz", blz);
                kernel.rawSet("KeyChange.KeyName.KIK.country", country);
                kernel.rawSet("KeyChange.KeyName.userid", passport.getUserId());
                kernel.rawSet("KeyChange.KeyName.keynum", sigKey[0].num);
                kernel.rawSet("KeyChange.KeyName.keytype", "S");
                kernel.rawSet("KeyChange.KeyName.keyversion", sigKey[0].version);
                kernel.rawSet("KeyChange.SecProfile.method", passport.getProfileMethod());
                kernel.rawSet("KeyChange.SecProfile.version", passport.getProfileVersion());
                kernel.rawSet("KeyChange.PubKey.mode", "16");
                kernel.rawSet("KeyChange.PubKey.exponent", "B" + exponent[0]);
                kernel.rawSet("KeyChange.PubKey.modulus", "B" + modulus[0]);
                kernel.rawSet("KeyChange.PubKey.usage", "6");
        
                kernel.rawSet("KeyChange_2.KeyName.KIK.blz", blz);
                kernel.rawSet("KeyChange_2.KeyName.KIK.country", country);
                kernel.rawSet("KeyChange_2.KeyName.userid", passport.getUserId());
                kernel.rawSet("KeyChange_2.KeyName.keynum", encKey[0].num);
                kernel.rawSet("KeyChange_2.KeyName.keytype", "V");
                kernel.rawSet("KeyChange_2.KeyName.keyversion", encKey[0].version);
                kernel.rawSet("KeyChange_2.SecProfile.method", passport.getProfileMethod());
                kernel.rawSet("KeyChange_2.SecProfile.version", passport.getProfileVersion());
                kernel.rawSet("KeyChange_2.PubKey.mode", "16");
                kernel.rawSet("KeyChange_2.PubKey.exponent", "B" + exponent[1]);
                kernel.rawSet("KeyChange_2.PubKey.modulus", "B" + modulus[1]);
                kernel.rawSet("KeyChange_2.PubKey.usage", "5");
                
                passport.setMyPublicSigKey(sigKey[0]);
                passport.setMyPrivateSigKey(sigKey[1]);
                passport.setMyPublicEncKey(encKey[0]);
                passport.setMyPrivateEncKey(encKey[1]);
                passport.saveChanges();
        
                HBCIMsgStatus ret=kernel.rawDoIt(HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,HBCIKernelImpl.DONT_NEED_CRYPT);
                
                Properties result=ret.getData();
                
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_SEND_KEYS_DONE,ret);

                if (!ret.isOK()) {
                    if (!ret.hasExceptions()) {
                        HBCIUtils.log("deleting locally generated user keys",HBCIUtils.LOG_WARN);
                        passport.clearMySigKey();
                        passport.clearMyEncKey();
                        passport.clearMyDigKey();
                        passport.saveChanges();
                    } else {
                        HBCIUtils.log(ret.getExceptions());
                        HBCIUtils.log("keys have not been thrown away",HBCIUtils.LOG_WARN);
                    }
        
                    throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDKEYERR"),ret);
                }
        
                try
                {
                    doDialogEnd(result.getProperty("MsgHead.dialogid"),"2",HBCIKernelImpl.DONT_SIGNIT,HBCIKernelImpl.CRYPTIT,HBCIKernelImpl.DONT_NEED_CRYPT);
                }
                catch (Exception e)
                {
                    HBCIUtils.log(e);
                }
                triggerNewKeysEvent();
            }
            else
            {
                // aendern der aktuellen Nutzerschluessel
                
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT,null);

                // Dialog-Context erzeugen
                final DialogContext ctx = DialogContext.create(this.kernel,this.passport);

                // Dialog-Initialisierung senden
                final HBCIDialogInit init = new HBCIDialogInit();
                HBCIMsgStatus ret = init.execute(ctx);

                if (!ret.isOK())
                    throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_GETUPDFAIL"),ret);

                Properties result = ret.getData();

                // evtl. Passport-Daten aktualisieren 
                final HBCIInstitute inst=new HBCIInstitute(kernel,passport,false);
                inst.updateBPD(result);
                this.updateUPD(result);
                passport.saveChanges();
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT_DONE,new Object[] {ret,result.getProperty("MsgHead.dialogid")});

                // neue Schlüssel senden
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_SEND_KEYS,null);
                kernel.rawNewMsg("ChangeKeys");
                kernel.rawSet("MsgHead.dialogid",result.getProperty("MsgHead.dialogid"));
                kernel.rawSet("MsgHead.msgnum","2");
                kernel.rawSet("MsgTail.msgnum","2");
                
                kernel.rawSet("KeyChange.KeyName.KIK.blz", blz);
                kernel.rawSet("KeyChange.KeyName.KIK.country", country);
                kernel.rawSet("KeyChange.KeyName.userid", passport.getUserId());
                kernel.rawSet("KeyChange.KeyName.keynum", sigKey[0].num);
                kernel.rawSet("KeyChange.KeyName.keytype", "S");
                kernel.rawSet("KeyChange.KeyName.keyversion", sigKey[0].version);
                kernel.rawSet("KeyChange.SecProfile.method", passport.getProfileMethod());
                kernel.rawSet("KeyChange.SecProfile.version", passport.getProfileVersion());
                kernel.rawSet("KeyChange.PubKey.mode", "16");
                kernel.rawSet("KeyChange.PubKey.exponent", "B" + exponent[0]);
                kernel.rawSet("KeyChange.PubKey.modulus", "B" + modulus[0]);
                kernel.rawSet("KeyChange.PubKey.usage", "6");
        
                kernel.rawSet("KeyChange_2.KeyName.KIK.blz", blz);
                kernel.rawSet("KeyChange_2.KeyName.KIK.country", country);
                kernel.rawSet("KeyChange_2.KeyName.userid", passport.getUserId());
                kernel.rawSet("KeyChange_2.KeyName.keynum", encKey[0].num);
                kernel.rawSet("KeyChange_2.KeyName.keytype", "V");
                kernel.rawSet("KeyChange_2.KeyName.keyversion", encKey[0].version);
                kernel.rawSet("KeyChange_2.SecProfile.method", passport.getProfileMethod());
                kernel.rawSet("KeyChange_2.SecProfile.version", passport.getProfileVersion());
                kernel.rawSet("KeyChange_2.PubKey.mode", "16");
                kernel.rawSet("KeyChange_2.PubKey.exponent", "B" + exponent[1]);
                kernel.rawSet("KeyChange_2.PubKey.modulus", "B" + modulus[1]);
                kernel.rawSet("KeyChange_2.PubKey.usage", "5");
                
                HBCIKey[] oldEncKeys=new HBCIKey[2];
                oldEncKeys[0]=passport.getMyPublicEncKey();
                oldEncKeys[1]=passport.getMyPrivateEncKey();
                
                passport.setMyPublicEncKey(encKey[0]);
                passport.setMyPrivateEncKey(encKey[1]);
                passport.saveChanges();
                
                ret=kernel.rawDoIt(HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,HBCIKernelImpl.NEED_CRYPT);
                if (!ret.isOK()) {
                    // hier muessen am besten beide schluessel im passport
                    // gesichert werden, damit spaeter ueberprueft werden
                    // kann, welcher der beiden denn nun beim server
                    // gespeichert ist. das ist dann kritisch, wenn eine
                    // eingereichte schlüsseländerung vom server nicht
                    // ausgeführt wird: dann tritt hier eine exception auf,
                    // aber es sind noch die alten schlüssel aktiv
                    if (!ret.hasExceptions()) {
                        HBCIUtils.log("deleting locally generated user keys",HBCIUtils.LOG_WARN);
                        passport.setMyPublicEncKey(oldEncKeys[0]);
                        passport.setMyPrivateEncKey(oldEncKeys[1]);
                        passport.saveChanges();
                    } else {
                        HBCIUtils.log("keys have not been thrown away",HBCIUtils.LOG_WARN);
                    }
        
                    throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDKEYERR"),ret);
                }
        
                passport.setSigId(Long.valueOf(1));
                passport.setMyPublicSigKey(sigKey[0]);
                passport.setMyPrivateSigKey(sigKey[1]);
                passport.saveChanges();
        
                result = ret.getData();
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_SEND_KEYS_DONE,ret);
                doDialogEnd(result.getProperty("MsgHead.dialogid"),"3",HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                                       HBCIKernelImpl.NEED_CRYPT);
            }
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDABORT"),e); 
        } finally {
            passport.closeComm();
        }
    }
    
    private void triggerNewKeysEvent()
    {
        HBCIUtilsInternal.getCallback().callback(passport,
                                         HBCICallback.HAVE_NEW_MY_KEYS,
                                         HBCIUtilsInternal.getLocMsg("CALLB_NEW_USER_KEYS"),
                                         HBCICallback.TYPE_NONE,
                                         new StringBuffer());
        throw new NeedKeyAckException();
    }

    public void generateNewKeys()
    {
        if (passport.needUserKeys()) {
            HBCIKey[][] newUserKeys=passport.generateNewUserKeys();
            sendAndActivateNewUserKeys(newUserKeys[0], newUserKeys[1]);
        } else {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_USRKEYS_UNSUPP"));
        }
    }
    
    public void manuallySetNewKeys(KeyPair sigKey,KeyPair encKey)
    {
        if (passport.needUserKeys()) {
            HBCIKey[] newSigKey=null;
            HBCIKey[] newEncKey=null;

            try {
                HBCIUtils.log("Speichere neue Benutzerschlüssel",HBCIUtils.LOG_INFO);

                String blz=passport.getBLZ();
                String country=passport.getCountry();
                String userid=passport.getUserId();

                newSigKey=new HBCIKey[2];
                newEncKey=new HBCIKey[2];

                for (int i=0;i<2;i++) {
                    if (i==0) {
                        String num=passport.hasMySigKey()?passport.getMyPublicSigKey().num:"0";
                        num=Integer.toString(Integer.parseInt(num)+1);
                        newSigKey[0]=new HBCIKey(country,blz,userid,num,"1",sigKey.getPublic());
                        newSigKey[1]=new HBCIKey(country,blz,userid,num,"1",sigKey.getPrivate());
                    } else {
                        String num=passport.hasMyEncKey()?passport.getMyPublicEncKey().num:"0";
                        num=Integer.toString(Integer.parseInt(num)+1);
                        newEncKey[0]=new HBCIKey(country,blz,userid,num,"1",encKey.getPublic());
                        newEncKey[1]=new HBCIKey(country,blz,userid,num,"1",encKey.getPrivate());
                    }
                }
            } catch (Exception ex) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_GENKEYS_ERR"),ex);
            }

            sendAndActivateNewUserKeys(newSigKey,newEncKey);
        } else {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_USRKEYS_UNSUPP"));
        }
    }

    public void fetchSysId()
    {
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SYSID,null);
            HBCIUtils.log("Rufe neue System-ID ab",HBCIUtils.LOG_INFO);
            
            HBCIUtils.log("checking whether passport is supported (but ignoring result)",HBCIUtils.LOG_DEBUG);
            boolean s=passport.isSupported();
            HBCIUtils.log("passport supported: "+s,HBCIUtils.LOG_DEBUG);

            // 2023-08-24: Ich glaube, es ist falsch, hier pauschal die Sig-ID zurückzusetzen.
            // Das sollte aus meiner Sicht nur gemacht werden, wenn Mode.SIG_ID verwendet wird. Also bei "fetchSigId".
            // passport.setSigId(new Long(1));
            passport.setSysId("0");

            ////////////////////////////////////////
            // Sync
            {
                // Dialog-Synchronisierung senden
                final DialogContext ctx = DialogContext.create(this.kernel,this.passport);
                final HBCIDialogSync sync = new HBCIDialogSync(Mode.SYS_ID);
                final HBCIMsgStatus ret = sync.execute(ctx);
                final Properties result = ret.getData();
        
                HBCIInstitute inst = new HBCIInstitute(kernel,passport,false);
                inst.updateBPD(result);
                updateUPD(result);
                passport.setSysId(result.getProperty("SyncRes.sysid"));
                passport.saveChanges();
        
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SYSID_DONE,new Object[] {ret,passport.getSysId()});
                HBCIUtils.log("new sys-id is "+passport.getSysId(),HBCIUtils.LOG_DEBUG);
                
                final HBCIDialogEnd end = new HBCIDialogEnd();
                end.execute(ctx);

            }
            //
            ////////////////////////////////////////
        }
        catch (Exception e)
        {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSYSIDFAIL"),e);
        }
        finally
        {
            passport.closeComm();
        }
    }
    
    public void fetchSigId()
    {
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SIGID,null);
            HBCIUtils.log("Synchronisiere Signatur-ID",HBCIUtils.LOG_INFO);
            
            // autosecmech
            HBCIUtils.log("checking whether passport is supported (but ignoring result)",HBCIUtils.LOG_DEBUG);
            boolean s=passport.isSupported();
            HBCIUtils.log("passport supported: "+s,HBCIUtils.LOG_DEBUG);

            passport.setSigId(Long.valueOf("9999999999999999"));
    
            // Dialog-Context erzeugen
            final DialogContext ctx = DialogContext.create(this.kernel,this.passport);

            // Dialog-Synchronisierung senden
            final HBCIDialogSync sync = new HBCIDialogSync(Mode.SIG_ID);
            final HBCIMsgStatus ret = sync.execute(ctx);
            final Properties result = ret.getData();

            HBCIInstitute inst=new HBCIInstitute(kernel,passport,false);
            inst.updateBPD(result);
            updateUPD(result);
            passport.setSigId(Long.valueOf(result.getProperty("SyncRes.sigid","1")));
            passport.incSigId();
            passport.saveChanges();
    
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SIGID_DONE,new Object[] {ret,passport.getSigId()});
            HBCIUtils.log("signature id set to "+passport.getSigId(),HBCIUtils.LOG_DEBUG);
            
            final HBCIDialogEnd end = new HBCIDialogEnd(Flag.SIG_ID);
            end.execute(ctx);
        }
        catch (Exception e)
        {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSIGIDFAIL"),e);
        }
        finally
        {
            passport.closeComm();
        }
    }
    
    /**
     * Uebernimmt die aktualisierten UPD in den Passport.
     * @param result die Ergebnis-Daten mit den UPD.
     */
    public void updateUPD(Properties result)
    {
        HBCIUtils.log("extracting UPD from results",HBCIUtils.LOG_DEBUG);

        ///////////////////////////////////////////////////////////////////
        // Wir fischen alle UPD aus den Ergebnisdaten raus
        final Properties p = new Properties();
        for (Enumeration e = result.keys(); e.hasMoreElements(); ) {
            String key = (String)(e.nextElement());
            if (key.startsWith("UPD.")) {
                p.setProperty(key.substring(("UPD.").length()), result.getProperty(key));
            }
        }
        //
        ///////////////////////////////////////////////////////////////////
        
        // Keine UPD enthalten
        if (p.size() == 0)
            return;

        p.setProperty(UPD_KEY_HBCIVERSION,kernel.getHBCIVersion());
        
        ///////////////////////////////////////////////////////////////////
        // Die UPD-Keys sicher, die nicht direkt von der Bank kommen sondern
        // von uns. Eigentlich sollten die nicht direkt in den UPD-Properties
        // gespeichert sondern separat. Dazu muesste ich aber die Datenstruktur
        // von PassportData erweitern und das auch bei allen anderen Passports
        // nachziehen sowie eine Migration einbauen. Dafuer lohnt sich das nicht.
        final Map<String,String> protectedKeys = new HashMap<String,String>();
        final Properties upd = passport.getUPD();
        if (upd != null && upd.size() > 0)
        {
            for (String key:UPD_PROTECT_KEYS)
            {
                String value = upd.getProperty(key);
                if (value != null) // Achtung: In Properties darf es keine NULL-Keys geben
                    protectedKeys.put(key,value);
            }
            
            p.putAll(protectedKeys);
        }

        // Wenn die UPD-Keys keine BIC und IBAN mehr enthalten, verwende
        // die bekannten einfach weiter, solange die Kontonummer identisch ist.
        // Manche Banken schicken in den UPDs scheinbar die Konto-Daten nicht mehr immer mit. Daher merken wird uns die vorherigen
        // Werte, wenn keine neuen uebertragen wurden
        if (upd != null && upd.size() > 0) {
            Konto[] konten = passport.getAccounts();
            final Pattern pattern = Pattern.compile("UPD\\.(KInfo(.*?)\\.KTV)\\.number");
            for (final Object okey : result.keySet()) {
                final String key = okey.toString();
                final Matcher m = pattern.matcher(key);
                if (m.matches()) {
                    final String kinfo = m.group(1);
                    if (!p.contains(kinfo + ".bic") || !p.contains(kinfo + ".iban")) {
                        Optional<Konto> matchingKonto = Arrays.asList(konten).stream()
                                .filter(konto ->
                                    Objects.equals(konto.number, result.get(okey)) &&
                                    Objects.equals(konto.blz, result.get("UPD." + kinfo + ".KIK.blz")) &&
                                    Objects.equals(konto.country, result.get("UPD." + kinfo + ".KIK.country")))
                                .findAny();
                        matchingKonto.ifPresent(konto -> {
                            if (StringUtil.hasText(konto.iban) && StringUtil.hasText(konto.bic)) {
                                HBCIUtils.log(kinfo + ".iban / .bic is missing, using the previous UPD's value",
                                        HBCIUtils.LOG_DEBUG);
                                p.put(kinfo + ".bic", konto.bic);
                                p.put(kinfo + ".iban", konto.iban);
                            }
                        });
                    }
                }
            }
        }
        
        // Wir aktualisieren unabhaengig davon, ob sich die Versionsnummer erhoeht hat oder nicht,
        // da nicht alle Banken die Versionsnummern erhoehen, wenn es Aenderungen gibt. Manche bleiben
        // einfach pauschal immer bei Version 0. Daher aktualisieren wir immer dann, wenn wir neue
        // UPDs erhalten haben.
        final String oldVersion = passport.getUPDVersion();
        passport.setUPD(p);
        final String newVersion = passport.getUPDVersion();

        HBCIUtils.log("Benutzerparameter (UPD) aktualisiert [Bisherige Version: " + oldVersion + ", neue Version: " + newVersion + "]",HBCIUtils.LOG_INFO);
        HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_UPD_DONE,passport.getUPD());
    }

    /**
     * Ruft die UPD von der Bank ab.
     */
    public void fetchUPD()
    {
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_UPD,null);
            HBCIUtils.log("updating UPD (BPD-Version: " + passport.getBPDVersion() + ")",HBCIUtils.LOG_DEBUG);
            HBCIUtils.log("Aktualisiere Benutzerparameter (UPD)",HBCIUtils.LOG_INFO);
            
            // autosecmech
            HBCIUtils.log("checking whether passport is supported (but ignoring result)",HBCIUtils.LOG_DEBUG);
            boolean s=passport.isSupported();
            HBCIUtils.log("passport supported: "+s,HBCIUtils.LOG_DEBUG);
            
            final String version = passport.getUPDVersion();
            if (!version.equals("0"))
            {
                HBCIUtils.log("resetting UPD version from " + version + " to 0",HBCIUtils.LOG_DEBUG);
                passport.getBPD().setProperty("UPA.version","0");
                passport.saveChanges();
            }
            
            // Dialog-Context erzeugen
            final DialogContext ctx = DialogContext.create(this.kernel,this.passport);
            ctx.setAnonymous(this.isAnon);

            // Dialog-Initialisierung senden
            final HBCIDialogInit init = new HBCIDialogInit();
            final HBCIMsgStatus ret = init.execute(ctx);

            if (!ret.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_GETUPDFAIL"),ret);

            Properties result = ret.getData();

            HBCIInstitute inst=new HBCIInstitute(kernel,passport,false);
            inst.updateBPD(result);
            
            this.updateUPD(result);
            passport.saveChanges();
            
            final HBCIDialogEnd end = new HBCIDialogEnd();
            end.execute(ctx);
        }
        catch (Exception e)
        {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_GETUPDFAIL"),e);
        }
        finally
        {
            passport.closeComm();
        }
    }

    /**
     * @see org.hbci4java.hbci.manager.IHandlerData#sync(boolean)
     */
    public void sync(boolean force)
    {
        if (passport.getSysStatus().equals("1"))
        {
            if (passport.getSysId().equals("0"))
                this.fetchSysId();
            if (passport.getSigId().longValue()==-1)
                this.fetchSigId();
        }
        
        Properties upd = passport.getUPD();
        Properties bpd = passport.getBPD();
        String hbciVersion = (upd != null) ? upd.getProperty(UPD_KEY_HBCIVERSION) : null;

        ////////////////////////////////////////
        // TAN-Medienbezeichnung abrufen - machen wir noch vor den UPD. Weil wir dafuer ja ggf. bereits das TAN-Verfahren brauchen (wir rufen dort ja auch KInfo ab)
        {
            final DialogContext ctx = DialogContext.create(this.kernel,this.passport);
            HBCIProcess p = new HBCIProcessTanMedia(force);
            p.execute(ctx);
        }
        //
        ////////////////////////////////////////

        ////////////////////////////////////////
        // UPD abrufen, falls noetig
        if (force || bpd == null || passport.getUPD() == null || hbciVersion==null || !hbciVersion.equals(kernel.getHBCIVersion())) 
        {
            fetchUPD();
        }
        //
        ////////////////////////////////////////

        ////////////////////////////////////////
        // Zum Schluss noch die SEPA-Infos abrufen
        if (Feature.SYNC_SEPAINFO.isEnabled())
        {
          final DialogContext ctx = DialogContext.create(this.kernel,this.passport);
          HBCIProcess p = new HBCIProcessSepaInfo(force);
          p.execute(ctx);
        }
        //
        ////////////////////////////////////////
    }

    /**
     * Registriert den User.
     */
    public void register()
    {
        if (passport.needUserKeys() && !passport.hasMySigKey())
            generateNewKeys();
        
        this.sync(false);
    }
    
    public void lockKeys()
    {
        if (!passport.needUserKeys() ||
            !passport.hasMySigKey()) {
                
            if (!passport.needUserKeys()) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_DONTHAVEUSRKEYS"));
            }
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_NOUSRKEYSAVAIL"));
        }
        
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT,null);
            HBCIUtils.log("Sperre Benutzerschlüssel",HBCIUtils.LOG_INFO);
            
            // Dialog-Context erzeugen
            final DialogContext ctx = DialogContext.create(this.kernel,this.passport);

            // Dialog-Initialisierung senden
            final HBCIDialogInit init = new HBCIDialogInit();
            HBCIMsgStatus ret = init.execute(ctx);

            if (!ret.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),ret);

            final Properties result = ret.getData();
            
            String dialogid=result.getProperty("MsgHead.dialogid");
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT_DONE,new Object[] {ret,dialogid});
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_LOCK_KEYS,null);
            
            final HBCIDialogLockKeys lock = new HBCIDialogLockKeys();
            ret = lock.execute(ctx);

            passport.clearMyDigKey();
            passport.clearMySigKey();
            passport.clearMyEncKey();
            
            passport.setSigId(Long.valueOf(1));
            passport.saveChanges();
            
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_LOCK_KEYS_DONE,ret);
            final HBCIDialogEnd end = new HBCIDialogEnd();
            end.execute(ctx);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),e);
        } finally {
            passport.closeComm();
        }
    }
    
    public MsgGen getMsgGen()
    {
    	return this.kernel.getMsgGen();
    }
    
    public HBCIPassport getPassport()
    {
    	return this.passport;
    }
}
