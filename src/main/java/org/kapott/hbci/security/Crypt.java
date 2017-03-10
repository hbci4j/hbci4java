
/*  $Id: Crypt.java,v 1.1 2011/05/04 22:38:03 willuhn Exp $

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

package org.kapott.hbci.security;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.IHandlerData;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.MultipleSEGs;
import org.kapott.hbci.protocol.MultipleSyntaxElements;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.protocol.SyntaxElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class Crypt
{
    public final static String SECFUNC_ENC_3DES="4";
    public final static String SECFUNC_ENC_PLAIN="998";
    
    public final static String ENCALG_2K3DES="13";

    public final static String ENCMODE_CBC="2";
    public final static String ENCMODE_PKCS1="18";
    
    public final static String ENC_KEYTYPE_RSA="6";
    public final static String ENC_KEYTYPE_DDV="5";

    private IHandlerData         handlerdata;
    private MSG                  msg;

    private String u_secfunc;    // 4=normal; 998=klartext
    private String u_keytype;    // 5=ddv, 6=rdh
    private String u_blz;        // schluesseldaten
    private String u_country;
    private String u_keyuserid;
    private String u_keynum;
    private String u_keyversion;
    private String u_cid;
    private String u_sysId;
    private String u_role;
    private String u_alg;       // crypthead.cryptalg.alg
    private String u_mode;      // crypthead.cryptalg.mode
    private String u_compfunc;

    public void setParam(String name, String value)
    {
        try {
            Field field=this.getClass().getDeclaredField("u_"+name);
            HBCIUtils.log("setting "+name+" to "+value,HBCIUtils.LOG_DEBUG);
            field.set(this,value);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while setting parameter",ex);
        }
    }

    private void initData(IHandlerData handlerdata, MSG msg)
    {
        this.msg = msg;
        this.handlerdata = handlerdata;
    }
    
    public Crypt(IHandlerData handlerdata, MSG msg)
    {
        initData(handlerdata,msg);
    }

    public void init(IHandlerData handlerdata, MSG msg)
    {
        initData(handlerdata,msg);
    }

    private byte[] getPlainString()
    {
        try {
            // remove msghead and msgtail first
            StringBuffer ret=new StringBuffer(1024);
            List<MultipleSyntaxElements> childs=msg.getChildContainers();
            int len=childs.size();

            /* skip one segment at start and one segment at end of message
               (msghead and msgtail), the rest will be encrypted */
            for (int i=1;i<len-1;i++) {
                ret.append(childs.get(i).toString(0));
            }

            // pad message
            int padLength=8-(ret.length()%8);
            for (int i=0;i<padLength-1;i++) {
                ret.append((char)(0));
            }
            ret.append((char)(padLength));

            return ret.toString().getBytes(Comm.ENCODING);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** error while extracting plain message string",ex);
        }
    }

    public MSG cryptIt(String newName)
    {
        MSG                  newmsg=msg;
        HBCIPassportInternal passport=(HBCIPassportInternal)handlerdata.getPassport();

        if (passport.hasInstEncKey()) {
            String msgName = msg.getName();
            MsgGen gen=handlerdata.getMsgGen();
            Node msgNode = msg.getSyntaxDef(msgName, gen.getSyntax());
            String dontcryptAttr = ((Element)msgNode).getAttribute("dontcrypt");

            if (dontcryptAttr.length() == 0) {
                try {
                    setParam("secfunc",passport.getCryptFunction());
                    setParam("keytype",passport.getCryptKeyType());
                    setParam("blz",passport.getBLZ());
                    setParam("country",passport.getCountry());
                    setParam("keyuserid",passport.getInstEncKeyName());
                    setParam("keynum",passport.getInstEncKeyNum());
                    setParam("keyversion",passport.getInstEncKeyVersion());
                    setParam("cid",passport.getCID());
                    setParam("sysId",passport.getSysId());
                    setParam("role","1");
                    setParam("alg",passport.getCryptAlg());
                    setParam("mode",passport.getCryptMode());
                    setParam("compfunc","0"); // TODO: spaeter kompression implementieren

                    byte[][] crypteds=passport.encrypt(getPlainString());

                    String msgPath=msg.getPath();
                    String dialogid=msg.getValueOfDE(msgPath+".MsgHead.dialogid");
                    String msgnum=msg.getValueOfDE(msgPath+".MsgHead.msgnum");
                    String segnum=msg.getValueOfDE(msgPath+".MsgTail.SegHead.seq");
                    
                    Date d=new Date();

                    gen.set(newName+".CryptData.data","B"+new String(crypteds[1],Comm.ENCODING));
                    gen.set(newName+".CryptHead.CryptAlg.alg",u_alg);
                    gen.set(newName+".CryptHead.CryptAlg.mode",u_mode);
                    gen.set(newName+".CryptHead.CryptAlg.enckey","B"+new String(crypteds[0],Comm.ENCODING));
                    gen.set(newName+".CryptHead.CryptAlg.keytype",u_keytype);
                    gen.set(newName+".CryptHead.SecIdnDetails.func",(newmsg.getName().endsWith("Res")?"2":"1"));
                    gen.set(newName+".CryptHead.KeyName.KIK.blz",u_blz);
                    gen.set(newName+".CryptHead.KeyName.KIK.country",u_country);
                    gen.set(newName+".CryptHead.KeyName.userid",u_keyuserid);
                    gen.set(newName+".CryptHead.KeyName.keynum",u_keynum);
                    gen.set(newName+".CryptHead.KeyName.keyversion",u_keyversion);
                    gen.set(newName+".CryptHead.SecProfile.method",passport.getProfileMethod());
                    gen.set(newName+".CryptHead.SecProfile.version",passport.getProfileVersion());
                    if (passport.getSysStatus().equals("0")) {
                        gen.set(newName+".CryptHead.SecIdnDetails.cid","B"+u_cid);
                    } else {
                        gen.set(newName+".CryptHead.SecIdnDetails.sysid",u_sysId);
                    }
                    gen.set(newName+".CryptHead.SecTimestamp.date",HBCIUtils.date2StringISO(d));
                    gen.set(newName+".CryptHead.SecTimestamp.time",HBCIUtils.time2StringISO(d));
                    gen.set(newName+".CryptHead.role",u_role);
                    gen.set(newName+".CryptHead.secfunc",u_secfunc);
                    gen.set(newName+".CryptHead.compfunc",u_compfunc);
                    gen.set(newName+".MsgHead.dialogid",dialogid);
                    gen.set(newName+".MsgHead.msgnum",msgnum);
                    gen.set(newName+".MsgTail.msgnum",msgnum);
                    
                    if (newName.endsWith("Res")) {
                        gen.set(newName+".MsgHead.MsgRef.dialogid",dialogid);
                        gen.set(newName+".MsgHead.MsgRef.msgnum",msgnum);
                    }

                    newmsg=gen.generate(newName);

                    // renumerate crypto-segments
                    for (int i=1;i<=2;i++) {
                        SEG seg=(SEG)(((MultipleSEGs)((newmsg.getChildContainers()).get(i))).getElements().get(0));
                        seg.setSeq(997+i,SyntaxElement.ALLOW_OVERWRITE);
                    }

                    newmsg.propagateValue(newmsg.getPath()+".MsgTail.SegHead.seq",segnum,
                            SyntaxElement.DONT_TRY_TO_CREATE,
                            SyntaxElement.ALLOW_OVERWRITE);
                    newmsg.autoSetMsgSize(gen);
                } catch (Exception ex) {
                    throw new HBCI_Exception("*** error while encrypting",ex);
                }
            }
            else HBCIUtils.log("did not encrypt - message does not want to be encrypted",HBCIUtils.LOG_DEBUG);
        }
        else HBCIUtils.log("can not encrypt - no encryption key available",HBCIUtils.LOG_WARN);

        return newmsg;
    }

    private boolean isCrypted()
    {
        boolean ret = true;
        MultipleSyntaxElements seglist = (msg.getChildContainers().get(1));

        if (seglist instanceof MultipleSEGs) {
            SEG crypthead = null;

            try {
                crypthead = (SEG)(seglist.getElements().get(0));
            } catch (Exception e) {
                ret = false;
            }

            if (ret) {
                String sigheadCode = "HNVSK";
                MsgGen gen=handlerdata.getMsgGen();

                if (!crypthead.getCode(gen).equals(sigheadCode))
                    ret = false;
            }
        }
        else ret = false;

        return ret;
    }

    public String decryptIt()
    {
        StringBuffer ret=new StringBuffer(msg.toString(0));
        HBCIPassportInternal passport=(HBCIPassportInternal)handlerdata.getPassport();

        if (passport.hasMyEncKey()) {
            if (isCrypted()) {
                try {
                    String msgName=msg.getName();

                    List<MultipleSyntaxElements> childs=msg.getChildContainers();
                    SEG msghead=(SEG)(((MultipleSEGs)(childs.get(0))).getElements().get(0));
                    SEG msgtail=(SEG)(((MultipleSEGs)(childs.get(childs.size()-1))).getElements().get(0));

                    // verschluesselte daten extrahieren
                    SEG cryptdata=(SEG)(((MultipleSEGs)(childs.get(2))).getElements().get(0));
                    byte[] cryptedstring=cryptdata.getValueOfDE(msgName+".CryptData.data").getBytes(Comm.ENCODING);

                    // key extrahieren
                    SEG crypthead=(SEG)(((MultipleSEGs)(childs.get(1))).getElements().get(0));
                    byte[] cryptedkey=crypthead.getValueOfDE(msgName+
                                      ".CryptHead.CryptAlg.enckey").getBytes(Comm.ENCODING);

                    // neues secfunc (klartext/encrypted)
                    String secfunc=crypthead.getValueOfDE(msgName+".CryptHead.secfunc");
                    if (!secfunc.equals(passport.getCryptFunction())) {
                        String errmsg=HBCIUtilsInternal.getLocMsg("EXCMSG_CRYPTSFFAIL",new Object[] {secfunc,
                                                          passport.getCryptFunction()});
                        if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCryptErrors",errmsg))
                            throw new HBCI_Exception(errmsg);
                    }

                    // TODO: diese checks werden vorerst abgeschaltet, damit pin-tan reibungslos geht
                    /*
                                     // constraint checking
                                     String keytype=crypthead.getValueOfDE(msgName+".CryptHead.CryptAlg.keytype");
                         if (!keytype.equals(passport.getSecMethod56()) && !(passport instanceof HBCIPassportPinTan))
                        throw new HBCI_Exception(HBCIUtils.getLocMsg("EXCMSG_CRYPTMETHODFAIL",new Object[] {keytype,passport.getSecMethod56()}));
                                     String mode=crypthead.getValueOfDE(msgName+".CryptHead.CryptAlg.mode");
                                     if (!mode.equals(passport.getCryptMode()))
                         throw new HBCI_Exception(HBCIUtils.getLocMsg("EXCMSG_CRYPTMODEFAIL",new Object[] {keytype,passport.getCryptMode()}));
                     */

                    /* TODO: removed code because no real checks are done here
                    if (passport.getSysStatus().equals("1")) {
                        String sysid=null;
                        try {
                            // falls noch keine system-id ausgehandelt wurde, so sendet der
                            // hbci-server auch keine... deshalb der try-catch-block
                            sysid=crypthead.getValueOfDE(msgName+".CryptHead.SecIdnDetails.sysid");
                        } catch (Exception e) {
                            sysid="0";
                        }
                        
                        // TODO: sysid checken (kann eigentlich auch entfallen, weil
                        // das jeweils auf höherer ebene geschehen sollte!)
                    } else {
                        String cid=crypthead.getValueOfDE(msgName+".CryptHead.SecIdnDetails.cid");
                        if (!cid.equals(passport.getCID())) {
                            String errmsg=HBCIUtilsInternal.getLocMsg("EXCMSG_CRYPTCIDFAIL");
                            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCryptErrors",errmsg))
                                throw new HBCI_Exception(errmsg);
                        }
                        
                        // TODO: cid checken
                    }
                    */

                    // TODO spaeter kompression implementieren
                    String compfunc=crypthead.getValueOfDE(msgName+".CryptHead.compfunc");
                    if (!compfunc.equals("0")) {
                        String errmsg=HBCIUtilsInternal.getLocMsg("EXCMSG_CRYPTCOMPFUNCFAIL",compfunc);
                        if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCryptErrors",errmsg))
                            throw new HBCI_Exception(errmsg);
                    }
                    
                    // TODO: hier auch die DEG SecProfile lesen und überprüfen

                    byte[] plainMsg=passport.decrypt(cryptedkey,cryptedstring);
                    int padLength=plainMsg[plainMsg.length-1];

                    // FileOutputStream fo=new FileOutputStream("decrypt.dat");
                    // fo.write(plainMsg);
                    // fo.close();

                    // neuen nachrichtenstring zusammenbauen
                    ret=new StringBuffer(1024);
                    ret.append(msghead.toString(0)).
                        append(new String(plainMsg,0,plainMsg.length-padLength,Comm.ENCODING)).
                        append(msgtail.toString(0));
                    
                    HBCIUtils.log("decrypted message: "+ret,HBCIUtils.LOG_DEBUG2);
                } catch (Exception ex) {
                    throw new HBCI_Exception("*** error while decrypting",ex);
                }
            }
            else HBCIUtils.log("did not decrypt - message is already cleartext",HBCIUtils.LOG_DEBUG);
        }
        else HBCIUtils.log("can not decrypt - no decryption key available",HBCIUtils.LOG_WARN);
        
        return ret.toString();
    }
    
    public void destroy()
    {
        handlerdata=null;
        msg=null;
        u_alg=null;
        u_blz=null;
        u_cid=null;
        u_compfunc=null;
        u_country=null;
        u_keynum=null;
        u_keyuserid=null;
        u_keyversion=null;
        u_mode=null;
        u_role=null;
        u_secfunc=null;
        u_keytype=null;
        u_sysId=null;
    }
}
