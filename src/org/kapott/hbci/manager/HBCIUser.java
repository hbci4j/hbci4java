
/*  $Id: HBCIUser.java,v 1.2 2011/08/31 14:05:21 willuhn Exp $

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

package org.kapott.hbci.manager;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.spec.RSAPublicKeySpec;
import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.NeedKeyAckException;
import org.kapott.hbci.exceptions.ProcessException;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;

/* @brief Instances of this class represent a certain user in combination with
    a certain institute. */
public final class HBCIUser
	implements IHandlerData
{
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
                                            !isAnon && HBCIKernelImpl.NEED_SIG,
                                            !isAnon && needCrypt);
        HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_END_DONE,status);

        if (!status.isOK()) {
            HBCIUtils.log("dialog end failed: "+status.getErrorString(),HBCIUtils.LOG_ERR);
            
            String msg=HBCIUtilsInternal.getLocMsg("ERR_INST_ENDFAILED");
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreDialogEndErrors",msg+": "+status.getErrorString()))
                throw new ProcessException(msg,status);
        }
    }

    // TODO: dig keys unterstützen
    private void sendAndActivateNewUserKeys(HBCIKey[] sigKey,HBCIKey[] encKey)
    {
        try {
            HBCIUtils.log("sending user keys to institute",HBCIUtils.LOG_INFO);
            
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
                    // TODO: dig key senden
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
                passport.setSigId(new Long(1));
                
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
                kernel.rawSet("KeyChange.KeyName.keytype", "S"); // TODO: keytype "D"
                kernel.rawSet("KeyChange.KeyName.keyversion", sigKey[0].version);
                kernel.rawSet("KeyChange.SecProfile.method", passport.getProfileMethod());
                kernel.rawSet("KeyChange.SecProfile.version", passport.getProfileVersion());
                kernel.rawSet("KeyChange.PubKey.mode", "16"); // TODO: later real mode
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
                kernel.rawSet("KeyChange_2.PubKey.mode", "16"); // TODO: later real mode
                kernel.rawSet("KeyChange_2.PubKey.exponent", "B" + exponent[1]);
                kernel.rawSet("KeyChange_2.PubKey.modulus", "B" + modulus[1]);
                kernel.rawSet("KeyChange_2.PubKey.usage", "5");
                
                // TODO: KeyChange_3
                
                passport.setMyPublicSigKey(sigKey[0]);
                passport.setMyPrivateSigKey(sigKey[1]);
                passport.setMyPublicEncKey(encKey[0]);
                passport.setMyPrivateEncKey(encKey[1]);
                // TODO: setMyDigKey
                passport.saveChanges();
        
                HBCIMsgStatus ret=kernel.rawDoIt(HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                 HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.DONT_NEED_CRYPT);
                
                passport.postInitResponseHook(ret, passport.isAnonymous());
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
                        HBCIUtils.log("keys have not been thrown away",HBCIUtils.LOG_WARN);
                    }
        
                    throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_SENDKEYERR"),ret);
                }
        
                try {
                    doDialogEnd(result.getProperty("MsgHead.dialogid"),"2",HBCIKernelImpl.DONT_SIGNIT,HBCIKernelImpl.CRYPTIT,
                                HBCIKernelImpl.DONT_NEED_CRYPT);
                } catch (Exception e) {
                    HBCIUtils.log(e);
                }
                triggerNewKeysEvent();
            } else {
                // aendern der aktuellen Nutzerschluessel
                
                HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT,null);

                // als erstes Dialog-Initialisierung
                HBCIMsgStatus ret=null;
                boolean       restarted=false;
                while (true) {
                    kernel.rawNewMsg("DialogInit");
                    kernel.rawSet("Idn.KIK.blz", blz);
                    kernel.rawSet("Idn.KIK.country", country);
                    kernel.rawSet("Idn.customerid", passport.getCustomerId());
                    kernel.rawSet("Idn.sysid", passport.getSysId());
                    String sysstatus=passport.getSysStatus();
                    kernel.rawSet("Idn.sysStatus",sysstatus);
                    kernel.rawSet("ProcPrep.BPD",passport.getBPDVersion());
                    kernel.rawSet("ProcPrep.UPD",passport.getUPDVersion());
                    kernel.rawSet("ProcPrep.lang",passport.getLang());
                    kernel.rawSet("ProcPrep.prodName",HBCIUtils.getParam("client.product.name","HBCI4Java"));
                    kernel.rawSet("ProcPrep.prodVersion",HBCIUtils.getParam("client.product.version","2.5"));
                    ret=kernel.rawDoIt(HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                            HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.NEED_CRYPT);

                    boolean need_restart=passport.postInitResponseHook(ret, passport.isAnonymous());
                    if (need_restart) {
                        HBCIUtils.log("for some reason we have to restart this dialog", HBCIUtils.LOG_INFO);
                        if (restarted) {
                            HBCIUtils.log("this dialog already has been restarted once - to avoid endless loops we stop here", HBCIUtils.LOG_WARN);
                            throw new HBCI_Exception("*** restart loop - aborting");
                        }
                        restarted=true;
                    } else {
                        break;
                    }
                }
                
                Properties result=ret.getData();
                
                if (!ret.isOK())
                    throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_GETUPDFAIL"),ret);
    
                // evtl. Passport-Daten aktualisieren 
                HBCIInstitute inst=new HBCIInstitute(kernel,passport,false);
                inst.updateBPD(result);
                updateUPD(result);
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
                kernel.rawSet("KeyChange.KeyName.keytype", "S"); // TODO: keytype "D"
                kernel.rawSet("KeyChange.KeyName.keyversion", sigKey[0].version);
                kernel.rawSet("KeyChange.SecProfile.method", passport.getProfileMethod());
                kernel.rawSet("KeyChange.SecProfile.version", passport.getProfileVersion());
                kernel.rawSet("KeyChange.PubKey.mode", "16"); // TODO: later real mode
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
                kernel.rawSet("KeyChange_2.PubKey.mode", "16"); // TODO: later real mode
                kernel.rawSet("KeyChange_2.PubKey.exponent", "B" + exponent[1]);
                kernel.rawSet("KeyChange_2.PubKey.modulus", "B" + modulus[1]);
                kernel.rawSet("KeyChange_2.PubKey.usage", "5");
                
                // TODO: KeyChange_3
                
                HBCIKey[] oldEncKeys=new HBCIKey[2];
                oldEncKeys[0]=passport.getMyPublicEncKey();
                oldEncKeys[1]=passport.getMyPrivateEncKey();
                
                passport.setMyPublicEncKey(encKey[0]);
                passport.setMyPrivateEncKey(encKey[1]);
                passport.saveChanges();
                
                ret=kernel.rawDoIt(HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                   HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.NEED_CRYPT);
                if (!ret.isOK()) {
                    // TODO: hier muessen am besten beide schluessel im passport
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
        
                passport.setSigId(new Long(1));
                passport.setMyPublicSigKey(sigKey[0]);
                passport.setMyPrivateSigKey(sigKey[1]);
                // TODO: setDigKey()
                passport.saveChanges();
        
                result=ret.getData();
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
        // TODO: hier überprüfen, ob tatsächlich ein INI-brief benötigt wird
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
    
    // TODO: auch neuen dig-key setzen lassen?
    public void manuallySetNewKeys(KeyPair sigKey,KeyPair encKey)
    {
        if (passport.needUserKeys()) {
            HBCIKey[] newSigKey=null;
            HBCIKey[] newEncKey=null;

            try {
                HBCIUtils.log("manually setting new user keys",HBCIUtils.LOG_INFO);

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
            HBCIUtils.log("fetching new sys-id from institute",HBCIUtils.LOG_INFO);
            
            // autosecmech
            HBCIUtils.log("checking whether passport is supported (but ignoring result)",HBCIUtils.LOG_DEBUG);
            boolean s=passport.isSupported();
            HBCIUtils.log("passport supported: "+s,HBCIUtils.LOG_DEBUG);

            String blz=passport.getBLZ();
            String country=passport.getCountry();
    
            passport.setSigId(new Long(1));
            passport.setSysId("0");
    
            HBCIMsgStatus ret=null;
            boolean       restarted=false;
            while (true) {
                kernel.rawNewMsg("Synch");
                kernel.rawSet("Idn.KIK.blz", blz);
                kernel.rawSet("Idn.KIK.country", country);
                kernel.rawSet("Idn.customerid", passport.getCustomerId());
                kernel.rawSet("Idn.sysid", "0");
                kernel.rawSet("Idn.sysStatus", "1");
                kernel.rawSet("MsgHead.dialogid", "0");
                kernel.rawSet("MsgHead.msgnum", "1");
                kernel.rawSet("MsgTail.msgnum", "1");
                kernel.rawSet("ProcPrep.BPD", passport.getBPDVersion());
                kernel.rawSet("ProcPrep.UPD", passport.getUPDVersion());
                kernel.rawSet("ProcPrep.lang", "0");
                kernel.rawSet("ProcPrep.prodName", HBCIUtils.getParam("client.product.name","HBCI4Java"));
                kernel.rawSet("ProcPrep.prodVersion", HBCIUtils.getParam("client.product.version","2.5"));
                kernel.rawSet("Sync.mode", "0");
                ret=kernel.rawDoIt(HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                        HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.NEED_CRYPT);

                boolean need_restart=passport.postInitResponseHook(ret, passport.isAnonymous());
                if (need_restart) {
                    HBCIUtils.log("for some reason we have to restart this dialog", HBCIUtils.LOG_INFO);
                    if (restarted) {
                        HBCIUtils.log("this dialog already has been restarted once - to avoid endless loops we stop here", HBCIUtils.LOG_WARN);
                        throw new HBCI_Exception("*** restart loop - aborting");
                    }
                    restarted=true;
                } else {
                    break;
                }
            } 
            
            Properties result=ret.getData();
            
            if (!ret.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSYSIDFAIL"),ret);
    
            HBCIInstitute inst=new HBCIInstitute(kernel,passport,false);
            inst.updateBPD(result);
            updateUPD(result);
            passport.setSysId(result.getProperty("SyncRes.sysid"));
            passport.saveChanges();
    
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SYSID_DONE,new Object[] {ret,passport.getSysId()});
            HBCIUtils.log("new sys-id is "+passport.getSysId(),HBCIUtils.LOG_DEBUG);
            doDialogEnd(result.getProperty("MsgHead.dialogid"),"2",HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                                   HBCIKernelImpl.NEED_CRYPT);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSYSIDFAIL"),e);
        } finally {
            passport.closeComm();
        }
    }

    public void fetchSigId()
    {
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SIGID,null);
            HBCIUtils.log("syncing signature id",HBCIUtils.LOG_INFO);
            
            // autosecmech
            HBCIUtils.log("checking whether passport is supported (but ignoring result)",HBCIUtils.LOG_DEBUG);
            boolean s=passport.isSupported();
            HBCIUtils.log("passport supported: "+s,HBCIUtils.LOG_DEBUG);

            String blz=passport.getBLZ();
            String country=passport.getCountry();
    
            passport.setSigId(new Long("9999999999999999"));
    
            HBCIMsgStatus ret=null;
            boolean       restarted=false;
            while (true) {
                kernel.rawNewMsg("Synch");
                kernel.rawSet("Idn.KIK.blz", blz);
                kernel.rawSet("Idn.KIK.country", country);
                kernel.rawSet("Idn.customerid", passport.getCustomerId());
                kernel.rawSet("Idn.sysid", passport.getSysId());
                kernel.rawSet("Idn.sysStatus", passport.getSysStatus());
                kernel.rawSet("MsgHead.dialogid", "0");
                kernel.rawSet("MsgHead.msgnum", "1");
                kernel.rawSet("MsgTail.msgnum", "1");
                kernel.rawSet("ProcPrep.BPD", passport.getBPDVersion());
                kernel.rawSet("ProcPrep.UPD", passport.getUPDVersion());
                kernel.rawSet("ProcPrep.lang", "0");
                kernel.rawSet("ProcPrep.prodName", HBCIUtils.getParam("client.product.name","HBCI4Java"));
                kernel.rawSet("ProcPrep.prodVersion", HBCIUtils.getParam("client.product.version","2.5"));
                kernel.rawSet("Sync.mode", "2");
                ret=kernel.rawDoIt(passport.hasMySigKey(),HBCIKernelImpl.CRYPTIT,
                        HBCIKernelImpl.NEED_SIG,passport.hasMyEncKey());

                boolean need_restart=passport.postInitResponseHook(ret, passport.isAnonymous());
                if (need_restart) {
                    HBCIUtils.log("for some reason we have to restart this dialog", HBCIUtils.LOG_INFO);
                    if (restarted) {
                        HBCIUtils.log("this dialog already has been restarted once - to avoid endless loops we stop here", HBCIUtils.LOG_WARN);
                        throw new HBCI_Exception("*** restart loop - aborting");
                    }
                    restarted=true;
                } else {
                    break;
                }
            }
            
            Properties result=ret.getData();

            if (!ret.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSIGIDFAIL"),ret);
    
            HBCIInstitute inst=new HBCIInstitute(kernel,passport,false);
            inst.updateBPD(result);
            updateUPD(result);
            passport.setSigId(new Long(result.getProperty("SyncRes.sigid","1")));
            passport.incSigId();
            passport.saveChanges();
    
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_SIGID_DONE,new Object[] {ret,passport.getSigId()});
            HBCIUtils.log("signature id set to "+passport.getSigId(),HBCIUtils.LOG_DEBUG);
            doDialogEnd(result.getProperty("MsgHead.dialogid"),"2",passport.hasMySigKey(),HBCIKernelImpl.CRYPTIT,
                                                                   passport.hasMyEncKey());
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_SYNCSIGIDFAIL"),e);
        } finally {
            passport.closeComm();
        }
    }

    public void updateUPD(Properties result)
    {
        HBCIUtils.log("extracting UPD from results",HBCIUtils.LOG_DEBUG);

        Properties p = new Properties();
        
        for (Enumeration e = result.keys(); e.hasMoreElements(); ) {
            String key = (String)(e.nextElement());
            if (key.startsWith("UPD.")) {
                p.setProperty(key.substring(("UPD.").length()), result.getProperty(key));
            }
        }

        if (p.size()!=0) {
            p.setProperty("_hbciversion",kernel.getHBCIVersion());
            
            // Wir sichern wenigstens noch die TAN-Media-Infos, die vom HBCIHandler vorher abgerufen wurden
            // Das ist etwas unschoen. Sinnvollerweise sollten die SEPA-Infos und TAN-Medien nicht in den
            // UPD gespeichert werden. Dann gehen die auch nicht jedesmal wieder verloren und muessen nicht
            // dauernd neu abgerufen werden. Das wuerde aber einen groesseren Umbau erfordern
            Properties upd = passport.getUPD();
            if (upd != null)
            {
                String mediaInfo = upd.getProperty("tanmedia.names");
                if (mediaInfo != null)
                {
                    HBCIUtils.log("rescued TAN media info to new UPD: " + mediaInfo,HBCIUtils.LOG_INFO);
                    p.setProperty("tanmedia.names",mediaInfo);
                }
            }
            
            String oldVersion = passport.getUPDVersion();
            passport.setUPD(p);
            
            HBCIUtils.log("installed new UPD [old version: " + oldVersion + ", new version: " + passport.getUPDVersion() + "]",HBCIUtils.LOG_INFO);
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_UPD_DONE,passport.getUPD());
        }
    }

    public void fetchUPD()
    {
        try {
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_INIT_UPD,null);
            HBCIUtils.log("fetching UPD (BPD-Version: " + passport.getBPDVersion() + ")",HBCIUtils.LOG_INFO);
            
            // autosecmech
            HBCIUtils.log("checking whether passport is supported (but ignoring result)",HBCIUtils.LOG_DEBUG);
            boolean s=passport.isSupported();
            HBCIUtils.log("passport supported: "+s,HBCIUtils.LOG_DEBUG);

            String blz=passport.getBLZ();
            String country=passport.getCountry();
    
            HBCIMsgStatus ret=null;
            boolean       restarted=false;
            while (true) {
                kernel.rawNewMsg("DialogInit"+anonSuffix);
                kernel.rawSet("Idn.KIK.blz", blz);
                kernel.rawSet("Idn.KIK.country", country);
                if (!isAnon) {
                    kernel.rawSet("Idn.customerid", passport.getCustomerId());
                    kernel.rawSet("Idn.sysid", passport.getSysId());
                    String sysstatus=passport.getSysStatus();
                    kernel.rawSet("Idn.sysStatus",sysstatus);
                }
                kernel.rawSet("ProcPrep.BPD",passport.getBPDVersion());
                kernel.rawSet("ProcPrep.UPD","0");
                kernel.rawSet("ProcPrep.lang",passport.getLang());
                kernel.rawSet("ProcPrep.prodName",HBCIUtils.getParam("client.product.name","HBCI4Java"));
                kernel.rawSet("ProcPrep.prodVersion",HBCIUtils.getParam("client.product.version","2.5"));
                ret=kernel.rawDoIt(!isAnon && HBCIKernelImpl.SIGNIT,
                        !isAnon && HBCIKernelImpl.CRYPTIT,
                        !isAnon && HBCIKernelImpl.NEED_SIG,
                        !isAnon && HBCIKernelImpl.NEED_CRYPT);

                boolean need_restart=passport.postInitResponseHook(ret, passport.isAnonymous());
                if (need_restart) {
                    HBCIUtils.log("for some reason we have to restart this dialog", HBCIUtils.LOG_INFO);
                    if (restarted) {
                        HBCIUtils.log("this dialog already has been restarted once - to avoid endless loops we stop here", HBCIUtils.LOG_WARN);
                        throw new HBCI_Exception("*** restart loop - aborting");
                    }
                    restarted=true;
                } else {
                    break;
                }
            }
            
            Properties result=ret.getData();
            
            if (!ret.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_GETUPDFAIL"),ret);
    
            HBCIInstitute inst=new HBCIInstitute(kernel,passport,false);
            inst.updateBPD(result);
            
            updateUPD(result);
            passport.saveChanges();
    
            doDialogEnd(result.getProperty("MsgHead.dialogid"),"2",HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                                                   HBCIKernelImpl.NEED_CRYPT);
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_GETUPDFAIL"),e);
        } finally {
            passport.closeComm();
        }
    }

    private void updateUserData()
    {
        if (passport.getSysStatus().equals("1")) {
            if (passport.getSysId().equals("0"))
                fetchSysId();
            if (passport.getSigId().longValue()==-1)
                fetchSigId();
        }
        
        Properties upd=passport.getUPD();
        Properties bpd=passport.getBPD();
        String     hbciVersionOfUPD=(upd!=null)?upd.getProperty("_hbciversion"):null;

        // Wir haben noch keine BPD. Offensichtlich unterstuetzt die Bank
        // das Abrufen von BPDs ueber einen anonymen Dialog nicht. Also machen
        // wir das jetzt hier mit einem nicht-anonymen Dialog gleich mit
        if (bpd == null || passport.getUPD() == null ||
            hbciVersionOfUPD==null ||
            !hbciVersionOfUPD.equals(kernel.getHBCIVersion())) 
        {
            fetchUPD();
        }
    }

    public void register()
    {
        if (passport.needUserKeys() && !passport.hasMySigKey()) {
            generateNewKeys();
        }
        updateUserData();
        passport.setPersistentData("_registered_user", Boolean.TRUE);
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
            HBCIUtils.log("locking user keys",HBCIUtils.LOG_INFO);
            
            String blz=passport.getBLZ();
            String country=passport.getCountry();
    
            HBCIMsgStatus status=null;
            boolean       restarted=false;
            while (true) {
                kernel.rawNewMsg("DialogInit");
                kernel.rawSet("Idn.KIK.blz", blz);
                kernel.rawSet("Idn.KIK.country", country);
                kernel.rawSet("Idn.customerid", passport.getCustomerId());
                kernel.rawSet("Idn.sysid", passport.getSysId());
                kernel.rawSet("Idn.sysStatus",passport.getSysStatus());
                kernel.rawSet("ProcPrep.BPD",passport.getBPDVersion());
                kernel.rawSet("ProcPrep.UPD",passport.getUPDVersion());
                kernel.rawSet("ProcPrep.lang",passport.getLang());
                kernel.rawSet("ProcPrep.prodName",HBCIUtils.getParam("client.product.name","HBCI4Java"));
                kernel.rawSet("ProcPrep.prodVersion",HBCIUtils.getParam("client.product.version","2.5"));

                status=kernel.rawDoIt(HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                        HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.NEED_CRYPT);

                boolean need_restart=passport.postInitResponseHook(status, passport.isAnonymous());
                if (need_restart) {
                    HBCIUtils.log("for some reason we have to restart this dialog", HBCIUtils.LOG_INFO);
                    if (restarted) {
                        HBCIUtils.log("this dialog already has been restarted once - to avoid endless loops we stop here", HBCIUtils.LOG_WARN);
                        throw new HBCI_Exception("*** restart loop - aborting");
                    }
                    restarted=true;
                } else {
                    break;
                }
            }
            
            Properties result=status.getData();
            
            if (!status.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),status);
            
            String dialogid=result.getProperty("MsgHead.dialogid");
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_DIALOG_INIT_DONE,new Object[] {status,dialogid});

            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_LOCK_KEYS,null);
            kernel.rawNewMsg("LockKeys");
            kernel.rawSet("MsgHead.dialogid",dialogid);
            kernel.rawSet("MsgHead.msgnum","2");
            kernel.rawSet("MsgTail.msgnum","2");
            kernel.rawSet("KeyLock.KeyName.KIK.country",country);
            kernel.rawSet("KeyLock.KeyName.KIK.blz",blz);
            kernel.rawSet("KeyLock.KeyName.userid",passport.getMySigKeyName());
            kernel.rawSet("KeyLock.KeyName.keynum",passport.getMySigKeyNum());
            kernel.rawSet("KeyLock.KeyName.keyversion",passport.getMySigKeyVersion());
            kernel.rawSet("KeyLock.SecProfile.method", passport.getProfileMethod());
            kernel.rawSet("KeyLock.SecProfile.version", passport.getProfileVersion());
            kernel.rawSet("KeyLock.locktype","999");

            status=kernel.rawDoIt(HBCIKernelImpl.SIGNIT,HBCIKernelImpl.CRYPTIT,
                                  HBCIKernelImpl.NEED_SIG,HBCIKernelImpl.DONT_NEED_CRYPT);
            if (!status.isOK())
                throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),status);

            passport.clearMyDigKey();
            passport.clearMySigKey();
            passport.clearMyEncKey();
            
            passport.setSigId(new Long(1));
            passport.saveChanges();
                
            HBCIUtilsInternal.getCallback().status(passport,HBCICallback.STATUS_LOCK_KEYS_DONE,status);
            doDialogEnd(dialogid,"3",HBCIKernelImpl.DONT_SIGNIT,HBCIKernelImpl.CRYPTIT,HBCIKernelImpl.DONT_NEED_CRYPT);
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
