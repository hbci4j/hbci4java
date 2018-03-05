
/*  $Id: AbstractHBCIPassport.java,v 1.4 2012/03/13 22:07:43 willuhn Exp $

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

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.comm.Filter;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIDialog;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.IHandlerData;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Limit;
import org.kapott.hbci.structures.Value;

/** <p>Diese Klasse stellt die Basisklasse für alle "echten" Passport-Implementationen
    dar. Hier werden bereits einige Methoden implementiert sowie einige 
    zusätzliche Hilfsmethoden zur Verfügung gestellt.</p><p>
    Aus einer HBCI-Anwendung heraus ist hier nur eine einzige Methode interessant,
    um eine Instanz eines bestimmtes Passports zu erzeugen</p> */
public abstract class AbstractHBCIPassport
    implements HBCIPassportInternal,Serializable
{
    private String     paramHeader;

    private Properties bpd;     
    private Properties upd;     
    private String     hbciversion;
    private String     country; 
    private String     blz;     
    private String     host;    
    private Integer    port;
    private String     filterType;
    private String     userid;
    private String     customerid;
    private String     sysid;   
    private Long       sigid;
    private String     cid;
    private Comm       comm;
    private Hashtable<String, Object>  persistentData;

    private IHandlerData parentHandlerData;
    
    protected static final boolean FOR_SAVE=true;
    protected static final boolean FOR_LOAD=false;
    
    public AbstractHBCIPassport(Object init)
    {
        persistentData=new Hashtable<String, Object>();
        setClientData("init",init);
    }

    protected boolean askForMissingData(boolean needCountry,boolean needBLZ,
                                        boolean needHost,boolean needPort,
                                        boolean needFilter,
                                        boolean needUserId,boolean needCustomerId)
    {
        boolean dataChanged=false;

        if (needBLZ && 
            (getBLZ()==null || getBLZ().length()==0)) {
            StringBuffer sb=new StringBuffer();
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_BLZ,HBCIUtilsInternal.getLocMsg("BLZ"),HBCICallback.TYPE_TEXT,sb);
            if (sb.length()==0)
                throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_X",HBCIUtilsInternal.getLocMsg("BLZ")));
            setBLZ(sb.toString());
            dataChanged=true;
        }
        
        if (needCountry &&
            (getCountry()==null || getCountry().length()==0)) {
            StringBuffer sb=new StringBuffer("DE");
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_COUNTRY,HBCIUtilsInternal.getLocMsg("COUNTRY"),HBCICallback.TYPE_TEXT,sb);
            if (sb.length()==0)
                throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_X",HBCIUtilsInternal.getLocMsg("COUNTRY")));
            setCountry(sb.toString());
            dataChanged=true;
        }

        if (needHost &&
            (getHost()==null || getHost().length()==0)) 
        {
            StringBuffer sb;
            
            if (this instanceof AbstractPinTanPassport) {
                sb=new StringBuffer(HBCIUtils.getPinTanURLForBLZ(getBLZ()));
                if (sb.indexOf("https://")==0) {
                    sb.delete(0,8);
                }
            } else {
                sb=new StringBuffer(HBCIUtils.getHBCIHostForBLZ(getBLZ()));
            }
            
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_HOST,HBCIUtilsInternal.getLocMsg("HOST"),HBCICallback.TYPE_TEXT,sb);
            if (sb.length()==0)
                throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_X",HBCIUtilsInternal.getLocMsg("HOST")));
            setHost(sb.toString());
            dataChanged=true;
        }

        if (needPort && 
            (getPort()==null || getPort().intValue()==0)) {
            StringBuffer sb=new StringBuffer((this instanceof AbstractPinTanPassport) ? "443" : "3000");
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_PORT,HBCIUtilsInternal.getLocMsg("PORT"),HBCICallback.TYPE_TEXT,sb);
            if (sb.length()==0)
                throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_X",HBCIUtilsInternal.getLocMsg("PORT")));
            setPort(new Integer(sb.toString()));
            dataChanged=true;
        }

        if (needFilter &&
                (getFilterType()==null || getFilterType().length()==0)) {
            StringBuffer sb=new StringBuffer("Base64");
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_FILTER,HBCIUtilsInternal.getLocMsg("FILTER"),HBCICallback.TYPE_TEXT,sb);
            if (sb.length()==0)
                throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_X",HBCIUtilsInternal.getLocMsg("FILTER")));
            setFilterType(sb.toString());
            dataChanged=true;
        }

        if (needUserId &&
            (getUserId()==null || getUserId().length()==0)) {
            StringBuffer sb=new StringBuffer();
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_USERID,HBCIUtilsInternal.getLocMsg("USERID"),HBCICallback.TYPE_TEXT,sb);
            if (sb.length()==0)
                throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_EMPTY_X",HBCIUtilsInternal.getLocMsg("USERID")));
            setUserId(sb.toString());
            dataChanged=true;
        }

        if (needCustomerId &&
            (getStoredCustomerId()==null || getStoredCustomerId().length()==0)) {
            StringBuffer sb=new StringBuffer(getCustomerId());
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_CUSTOMERID,HBCIUtilsInternal.getLocMsg("CUSTOMERID"),HBCICallback.TYPE_TEXT,sb);
            setCustomerId(sb.toString());
            dataChanged=true;
        }

        return dataChanged;
    }

    public final Comm getComm()
    {
        if (comm==null) {
            comm=getCommInstance();
        }
        
        return comm;
    }
    
    protected abstract Comm getCommInstance();
    
    public final Filter getCommFilter()
    {
        return Filter.getInstance(getFilterType());
    }
    
    public final void closeComm()
    {
        if (comm!=null) {
            comm.close();
            comm=null;
        }
    }

    public final Properties getBPD()
    {
        return bpd;
    }
    
    public final void setHBCIVersion(String hbciversion)
    {
        this.hbciversion=hbciversion;
    }
    
    public final String getHBCIVersion()
    {
        return (hbciversion!=null)?hbciversion:"";
    }
    
    public final Properties getUPD()
    {
        return upd;
    }

    public final String getBLZ()
    {
        return blz;
    }

    public final String getCountry()
    {
        return country;
    }
    
    public final Konto[] getAccounts()
    {
        ArrayList<Konto> ret=new ArrayList<Konto>();
        
        if (upd!=null) {
            for (int i=0;;i++) {
                String header=HBCIUtilsInternal.withCounter("KInfo",i);
                String number=upd.getProperty(header+".KTV.number");

                if (number==null)
                    break;

                Konto entry=new Konto();
                entry.blz=upd.getProperty(header+".KTV.KIK.blz");
                entry.country=upd.getProperty(header+".KTV.KIK.country");
                entry.number=number;
                entry.subnumber=upd.getProperty(header+".KTV.subnumber");
                entry.curr=upd.getProperty(header+".cur");
                entry.type=upd.getProperty(header+".konto");
                entry.customerid=upd.getProperty(header+".customerid");
                entry.name=upd.getProperty(header+".name1");
                entry.name2=upd.getProperty(header+".name2");
                entry.bic=upd.getProperty(header+".KTV.bic");
                entry.iban=upd.getProperty(header+".KTV.iban");
                entry.acctype=upd.getProperty(header+".acctype");
                
                String st;
                if ((st=upd.getProperty(header+".KLimit.limittype"))!=null) {
                    Limit limit=new Limit();
                    limit.type=st.charAt(0);
                    limit.value=new Value(upd.getProperty(header+".KLimit.BTG.value"),
                                          upd.getProperty(header+".KLimit.BTG.curr"));
                    if ((st=upd.getProperty(header+".KLimit.limitdays"))!=null)
                        limit.days=Integer.parseInt(st);
                }
                
                // allowedGVs
                ArrayList<String> codes = new ArrayList<String>();
                for (int j=0;;j++) {
                	String gvHeader = HBCIUtilsInternal.withCounter(header+".AllowedGV", j);
                	String code = upd.getProperty(gvHeader+".code");
                	if (code == null) break;
                	codes.add(code);
                }
                if (!codes.isEmpty()) entry.allowedGVs = codes;
                
                ret.add(entry);
            }
        }
        
        return ret.toArray(new Konto[ret.size()]);
    }
    
    public final void fillAccountInfo(Konto account)
    {
        String  number=HBCIUtilsInternal.stripLeadingZeroes(account.number);
        String  iban=HBCIUtilsInternal.stripLeadingZeroes(account.iban);
        boolean haveNumber=(number!=null && number.length()!=0);
        boolean haveIBAN=(iban!=null && iban.length()!=0);
        
        Konto[] accounts=getAccounts();
        
        for (int i=0;i<accounts.length;i++) {
            String temp_number=HBCIUtilsInternal.stripLeadingZeroes(accounts[i].number);
            String temp_iban=HBCIUtilsInternal.stripLeadingZeroes(accounts[i].iban);
            
            if (haveNumber && number.equals(temp_number) ||
                    haveIBAN && iban.equals(temp_iban)) 
            {
                account.blz=accounts[i].blz;
                account.country=accounts[i].country;
                account.number=accounts[i].number;
                account.subnumber=accounts[i].subnumber;
                account.type=accounts[i].type;
                account.curr=accounts[i].curr;
                account.customerid=accounts[i].customerid;
                account.name=accounts[i].name;
                account.bic=accounts[i].bic;
                account.iban=accounts[i].iban;
                account.acctype=accounts[i].acctype;
                break;
            }
        }
    }
    
    public final Konto getAccount(String number)
    {
        Konto ret=new Konto();
        ret.number=number;
        fillAccountInfo(ret);
        
        if (ret.blz==null) {
        	// es wurde kein Konto-Objekt in getAccounts() gefunden
        	ret.blz=getBLZ();
        	ret.country=getCountry();
        	ret.customerid=getCustomerId();
        	// TODO: very dirty!
        	ret.name=getCustomerId();
        	
        	// an dieser Stelle sind jetzt alle Werte gefüllt, die teilweise
        	// zwingend benötigt werden
        }
        
        return ret;
    }

    public String getHost()
    {
        return host;
    }

    public final Integer getPort()
    {
        return (port!=null)?port:new Integer(0);
    }
    
    public final String getFilterType()
    {
        return filterType;
    }
    
    public String getUserId()
    {
        return userid;
    }

    public final String getCustomerId(int idx)
    {
        String header=HBCIUtilsInternal.withCounter("KInfo",idx)+".customerid";
        String c=(upd!=null)?upd.getProperty(header):customerid;
        return (c!=null)?c:getUserId();
    }
    
    public String getCustomerId()
    {
        return (customerid!=null && customerid.length()!=0)?customerid:getUserId();
    }
    
    public String getStoredCustomerId()
    {
        return customerid;
    }
    
    public String getSysId()
    {
        return (sysid!=null && sysid.length()!=0)?sysid:"0";
    }

    public final String getCID()
    {
        return cid!=null?cid:"";
    }

    public final void clearInstSigKey()
    {
        setInstSigKey(null);
    }

    public final void clearInstEncKey()
    {
        setInstEncKey(null);
    }

    public final void clearMySigKey()
    {
        setMyPublicSigKey(null);
        setMyPrivateSigKey(null);
    }

    public final void clearMyEncKey()
    {
        setMyPublicEncKey(null);
        setMyPrivateEncKey(null);
    }

    public final void clearMyDigKey()
    {
        setMyPublicDigKey(null);
        setMyPrivateDigKey(null);
    }
    
    public final String getBPDVersion()
    {
        String version=((bpd!=null)?bpd.getProperty("BPA.version"):null);
        return ((version!=null)?version:"0");
    }

    public final String getUPDVersion()
    {
        String version=((upd!=null)?upd.getProperty("UPA.version"):null);
        return ((version!=null)?version:"0");
    }

    public final String getInstName()
    {
        return (bpd!=null)?bpd.getProperty("BPA.kiname"):null;
    }

    public int getMaxGVperMsg()
    {
        return (bpd!=null)?Integer.parseInt(bpd.getProperty("BPA.numgva")):-1;
    }

    public final int getMaxMsgSizeKB()
    {
        return (bpd!=null)?Integer.parseInt(bpd.getProperty("BPA.maxmsgsize","0")):0;
    }

    public final String[] getSuppLangs()
    {
        String[] ret=new String[0];

        if (bpd!=null) {
            ArrayList<String> temp=new ArrayList<String>();
            String header;
            String value;
            int i=0;

            while ((header=HBCIUtilsInternal.withCounter("BPA.SuppLangs.lang",i))!=null &&
                   (value=bpd.getProperty(header))!=null) {
                temp.add(value);
                i++;
            }

            if (temp.size()!=0) 
                ret=(temp.toArray(ret));
        }
    
        return ret;
    }

    public final String[] getSuppVersions()
    {
        String[] ret=new String[0];

        if (bpd!=null) {
            ArrayList<String> temp=new ArrayList<String>();
            String header;
            String value;
            int i=0;

            while ((header=HBCIUtilsInternal.withCounter("BPA.SuppVersions.version",i))!=null &&
                   (value=bpd.getProperty(header))!=null) {
                temp.add(value);
                i++;
            }

            if (temp.size()!=0)
                ret=(temp.toArray(ret));
        }
    
        return ret;
    }

    public final String getDefaultLang()
    {
        String value=(bpd!=null)?bpd.getProperty("CommListRes.deflang"):null;
        return (value!=null)?value:"0";
    }

    public final boolean canMixSecMethods()
    {
        boolean ret=false;

        if (bpd!=null) {
            String value=bpd.getProperty("SecMethod.mixing");

            if (value!=null && value.equals("J"))
                ret=true;
        }

        return ret;
    }

    public final String[][] getSuppSecMethods()
    {
        String[][] ret=new String[0][];

        if (bpd!=null) {
            ArrayList<String[]> temp=new ArrayList<String[]>();
            String header;
            String method;
            int i=0;

            while ((header=HBCIUtilsInternal.withCounter("SecMethod.SuppSecMethods",i))!=null &&
                   (method=bpd.getProperty(header+".method"))!=null) {

                String header2;
                String version;
                int    j=0;
                
                while ((header2=HBCIUtilsInternal.withCounter(header+".version",j))!=null &&
                        (version=bpd.getProperty(header2))!=null) 
                {
                    String[] entry=new String[2];
                    entry[0]=method;
                    entry[1]=version;
                    temp.add(entry);
                    j++;
                }
                
                i++;
            }

            if (temp.size()!=0)
                ret=(temp.toArray(ret));
        }
            
        return ret;
    }

    public final String[][] getSuppCompMethods()
    {
        String[][] ret=new String[0][];

        if (bpd!=null) {
            ArrayList<String[]> temp=new ArrayList<String[]>();
            String header;
            String method;
            int i=0;

            while ((header=HBCIUtilsInternal.withCounter("CompMethod.SuppCompMethods",i))!=null &&
                   (method=bpd.getProperty(header+".func"))!=null) {

                String version=bpd.getProperty(header+".version");
                String[] entry=new String[2];
                entry[0]=method;
                entry[1]=version;
                temp.add(entry);
                i++;
            }

            if (temp.size()!=0)
                ret=(temp.toArray(ret));
        }
            
        return ret;
    }

    public final String getLang()
    {
        String value=(bpd!=null)?bpd.getProperty("CommListRes.deflang"):null;
        return (value!=null)?value:"0";
    }

    public final Long getSigId()
    {
        return sigid!=null?sigid:new Long(1);
    }

    public final void clearBPD()
    {
        setBPD(null);
    }

    public void setBPD(Properties bpd)
    {
        this.bpd=bpd;
    }
    
    public final void clearUPD()
    {
        setUPD(null);
    }

    public final void setUPD(Properties upd)
    {
        this.upd=upd;
    }

    public final void setCountry(String country)
    {
        this.country=country;
    }

    public final void setBLZ(String blz)
    {
      	LogFilter.getInstance().addSecretData(blz,"X",LogFilter.FILTER_MOST);
        this.blz=blz;
    }

    public final void setHost(String host)
    {
        this.host=host;
    }

    public final void setPort(Integer port)
    {
        this.port=port;
    }
    
    public final void setFilterType(String filter)
    {
        this.filterType=filter;
    }
    
    public final void setUserId(String userid)
    {
      	LogFilter.getInstance().addSecretData(userid,"X",LogFilter.FILTER_IDS);
        this.userid=userid;
    }

    public final void setCustomerId(String customerid)
    {
      	LogFilter.getInstance().addSecretData(customerid,"X",LogFilter.FILTER_IDS);
        this.customerid=customerid;
    }    

    public final void setSigId(Long sigid)
    {
        this.sigid=sigid;
    }

    public final void setSysId(String sysid)
    {
        this.sysid=sysid;
    }

    public final void setCID(String cid)
    {
    	LogFilter.getInstance().addSecretData(cid,"X",LogFilter.FILTER_IDS);
        this.cid=cid;
    }

    public void incSigId()
    {
        setSigId(new Long(getSigId().longValue()+1));
    }

    public final boolean onlyBPDGVs()
    {
        return getUPD().getProperty("UPA.usage").equals("0");
    }

    /** <p>Erzeugt eine Instanz eines HBCIPassports und gibt diese zurück. Der
        Typ der erzeugten Passport-Instanz wird durch den Parameter <code>name</code>
        bestimmt. Gültige Werte sind zur Zeit
        <ul>
          <li>DDV</li>
          <li>RDHNew</li>
          <li>RDH (nicht mehr benutzen!)</li>
          <li>PinTan</li>
          <li>SIZRDHFile</li>
          <li>RDHXFile</li>
          <li>Anonymous</li>
        </ul></p>
        <p>Der zusätzliche Parameter <code>init</code> gibt ein Objekt an, welches
        bereits während der Instanziierung des Passport-Objektes in dessen internen
        <code>clientData</code>-Datenstrukturen gespeichert wird
        (siehe {@link org.kapott.hbci.passport.HBCIPassport#setClientData(String,Object)}).
        Auf dieses Objekt kann dann mit 
        {@link org.kapott.hbci.passport.HBCIPassport#getClientData(String) getClientData("init")}
        zugegriffen werden. Ist <code>init==null</code>), wo wird <code>init=name</code>
        gesetzt.</p>
        <p>Beim Erzeugen eines Passport-Objektes tritt i.d.R. der 
        {@link org.kapott.hbci.callback.HBCICallback Callback} <code>NEED_PASSPHRASE</code>
        auf, um nach dem Passwort für das Einlesen der Schlüsseldatei zu fragen. 
        Von der Callback-Methode eventuell zusätzlich benötigte Daten zu diesem Passport
        konnten bis zu dieser Stelle noch nicht via <code>setClientData(...)</code>
        gesetzt werden, weil das Passport-Objekt noch gar nicht existierte. Für diesen
        Zweck gibt es das <code>init</code>-Objekt, welches bereits beim Erzeugen
        des Passport-Objektes (und <em>vor</em> dem Aufrufen eines Callbacks) zu den
        zusätzlichen Passport-Daten hinzugefügt wird (mit der id "<code>init</code>").</p>
        <p>Eine beispielhafte (wenn auch nicht sehr praxisnahe) Anwendung dieses 
        Features wird im Quelltext des Tools 
        {@link org.kapott.hbci.tools.AnalyzeReportOfTransactions}
        gezeigt. Zumindest das Prinzip sollte damit jedoch klar werden.</p>
        @param name Typ der zu erzeugenden Passport-Instanz
        @param init Objekt, welches schon während der Passport-Erzeugung via
        <code>setClientData("init",init)</code> zu den Passport-Daten hinzugefügt wird.
        @return Instanz eines HBCIPassports */
    public static HBCIPassport getInstance(String name,Object init)
    {
        if (name==null) {
            throw new NullPointerException("name of passport implementation must not be null");
        }
            
        String className = "org.kapott.hbci.passport.HBCIPassport"+name;
        try {
            if (init==null)
                init=name;
                
            HBCIUtils.log("creating new instance of a "+name+" passport",HBCIUtils.LOG_DEBUG);
            Class cl=Class.forName(className);
            Constructor con=cl.getConstructor(new Class[] {Object.class});
            HBCIPassport p=(HBCIPassport)(con.newInstance(new Object[] {init}));
            return p;
        }
        catch (ClassNotFoundException e)
        {
          throw new InvalidUserDataException("*** No passport implementation '"+name+"' found - there must be a class "+className);
        }
        catch (InvocationTargetException ite)
        {
          Throwable cause = ite.getCause();
          if (cause instanceof HBCI_Exception)
            throw (HBCI_Exception) cause;
          
          throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_INST",name),ite);
        }
        catch (HBCI_Exception he)
        {
          throw he;
        }
        catch (Exception e)
        {
          throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_INST",name),e); 
        }
    }

    /** Erzeugt eine Instanz eines HBCI-Passports. Der Typ der erzeugten
        Passport-Instanz wird hierbei dem Wert des HBCI-Parameters
        <code>client.passport.default</code> entnommen. Gültige Werte für diesen
        HBCI-Parameter sind die gleichen wie beim Aufruf der Methode
        {@link #getInstance(String)}.
        @param init (siehe {@link #getInstance(String,Object)})
        @return Instanz eines HBCI-Passports */
    public static HBCIPassport getInstance(Object init)
    {
        String passportName=HBCIUtils.getParam("client.passport.default");
        if (passportName==null)
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_NODEFPASS"));

        return getInstance(passportName,init);
    }
    
    /** Entspricht {@link #getInstance(String,Object) getInstance(name,null)} */
    public static HBCIPassport getInstance(String name)
    {
        return getInstance(name,null);
    }
    
    /** Entspricht {@link #getInstance(Object) getInstance((Object)null)} */
    public static HBCIPassport getInstance()
    {
        return getInstance((Object)null);
    }

    public void close()
    {
        closeComm();
    }
    
    /**
     * Fragt den User per Callback nach dem Passwort fuer die Passport-Datei.
     * @param forSaving true, wenn das Passwort zum Speichern erfragt werden soll.
     * @return der Secret-Key.
     */
    protected SecretKey calculatePassportKey(boolean forSaving)
    {
        try {
            StringBuffer passphrase=new StringBuffer();
            HBCIUtilsInternal.getCallback().callback(this,
                                             forSaving?HBCICallback.NEED_PASSPHRASE_SAVE
                                                      :HBCICallback.NEED_PASSPHRASE_LOAD,
                                             forSaving?HBCIUtilsInternal.getLocMsg("CALLB_NEED_PASS_NEW")
                                                      :HBCIUtilsInternal.getLocMsg("CALLB_NEED_PASS"),
                                             HBCICallback.TYPE_SECRET,
                                             passphrase);
            if (passphrase.length()==0) {
                throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSZERO"));
            }
            LogFilter.getInstance().addSecretData(passphrase.toString(),"X",LogFilter.FILTER_SECRETS);

            String provider = HBCIUtils.getParam("kernel.security.provider");
            SecretKeyFactory fac = provider==null ? SecretKeyFactory.getInstance("PBEWithMD5AndDES") : SecretKeyFactory.getInstance("PBEWithMD5AndDES", provider);
            PBEKeySpec keyspec=new PBEKeySpec(passphrase.toString().toCharArray());
            SecretKey passportKey=fac.generateSecret(keyspec);
            keyspec.clearPassword();
            passphrase=null;

            return passportKey;
        } catch (Exception ex) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_PASSPORT_KEYCALCERR"),ex);
        }
    }
    
    public Properties getParamSegmentNames()
    {
        Properties ret=new Properties();
        
        for (Enumeration e=bpd.propertyNames();e.hasMoreElements();) {
            String key=(String)e.nextElement();
            
            if (key.startsWith("Params") &&
                key.endsWith(".SegHead.code")) {
                int dotPos=key.indexOf('.');
                int dotPos2=key.indexOf('.',dotPos+1);
                
                String gvname=key.substring(dotPos+1,dotPos2);
                int    len=gvname.length();
                int    versionPos=-1;
                
                for (int i=len-1;i>=0;i--) {
                    char ch=gvname.charAt(i);
                    if (!(ch>='0' && ch<='9')) {
                        versionPos=i+1;
                        break;
                    }
                }
                
                String version=gvname.substring(versionPos);
                if (version.length()!=0) {
                    gvname=gvname.substring(0,versionPos-3); // remove version and "Par"

                    String knownVersion=(String)ret.get(gvname);

                    if (knownVersion==null ||
                        Integer.parseInt(version)>Integer.parseInt(knownVersion)) {
                        ret.setProperty(gvname,version);
                    }
                }
            }
        }
        
        return ret;
    }

    public Properties getJobRestrictions(String specname)
    {
        int  versionPos=specname.length()-1;
        char ch;
        
        while ((ch=specname.charAt(versionPos))>='0' && ch<='9') {
            versionPos--;
        }
        
        return getJobRestrictions(
            specname.substring(0,versionPos+1),
            specname.substring(versionPos+1));
    }
    
    public Properties getJobRestrictions(String gvname,String version)
    {
        Properties result=new Properties();

        String searchstring=gvname+"Par"+version;
        for (Enumeration e=bpd.propertyNames();e.hasMoreElements();) {
            String key=(String)e.nextElement();

            if (key.startsWith("Params")&&
                key.indexOf("."+searchstring+".Par")!=-1) {
                int searchIdx=key.indexOf(searchstring);
                result.setProperty(key.substring(key.indexOf(".",
                                                             searchIdx+searchstring.length()+4)+1),
                                   bpd.getProperty(key));
            }
        }

        return result;
    }
    
    public void setPersistentData(String id,Object o)
    {
        if (o!=null)
            persistentData.put(id,o);
        else
            persistentData.remove(id);
    }
    
    public Object getPersistentData(String id)
    {
        return persistentData.get(id);
    }
    
    public void syncSigId()
    {
        setSigId(new Long("-1"));
    }
    
    public void syncSysId()
    {
        setSysId("0");
    }
    
    public void changePassphrase()
    {
        resetPassphrase();
        saveChanges();
    }
    
    public final void setClientData(String id,Object o)
    {
        setPersistentData("client_"+id,o);
    }
    
    public final Object getClientData(String id)
    {
        return getPersistentData("client_"+id);
    }
    
    public boolean isAnonymous()
    {
        return false;
    }

    protected void setParamHeader(String paramHeader)
    {
        this.paramHeader=paramHeader;
    }
    
    protected String getParamHeader()
    {
        return paramHeader;
    }
    
    public void setParentHandlerData(IHandlerData handler)
    {
        this.parentHandlerData=handler;
    }
    
    public IHandlerData getParentHandlerData()
    {
        return this.parentHandlerData;
    }
    
    public HBCIKey[][] generateNewUserKeys()
    {
    	throw new HBCI_Exception("*** current passport does not know how to generate user keys");
    }

    public void setProfileMethod(String method) 
    {
        throw new HBCI_Exception("*** overriding security profiles in passports not possible");
    }

    public void setProfileVersion(String version) 
    {
        throw new HBCI_Exception("*** overriding security profiles in passports not possible");
    }
    
    public static byte[] checkForCryptDataSize(byte[] buffer, int size)
    {
        byte[] result=buffer;
        
        if (buffer.length!=size) {
            HBCIUtils.log("checking for crypted_data_length=="+size+"; current length is "+buffer.length,HBCIUtils.LOG_DEBUG);
            if (buffer.length>size) {
                int diff=buffer.length-size;
                boolean ok=true;

                for (int i=0;i<diff;i++) {
                    if (buffer[i]!=0x00) {
                        HBCIUtils.log("byte "+i+" in crypted_data is not zero, but it should be zero - please contact the author",HBCIUtils.LOG_WARN);
                        ok=false;
                    }
                }

                if (ok) {
                    HBCIUtils.log("removing "+diff+" unnecessary null-bytes from crypted_data",HBCIUtils.LOG_DEBUG);
                    result=new byte[size];
                    System.arraycopy(buffer,diff,result,0,size);
                }
            } else if (buffer.length<size) {
                int diff=size-buffer.length;
                HBCIUtils.log("prepending "+diff+" null bytes to crypted_data",HBCIUtils.LOG_WARN);
                result=new byte[size];
                Arrays.fill(result,(byte)0);
                System.arraycopy(buffer,0,result,diff,buffer.length);
            }
        }
        
        return result;
    }

    public boolean postInitResponseHook(HBCIMsgStatus msgStatus, boolean anonDialog)
    {
        // die default-Implementierung tut nichts und verlangt auch keine
        // erneute Dialog-Initialisierung
        return false;
    }

    public void beforeCustomDialogHook(HBCIDialog dialog)
    {
        // default implementation does nothing
    }
    
    public void afterCustomDialogInitHook(HBCIDialog dialog)
    {
        // default implementation does nothing - only PinTan variant will override this
    }
    
    public int getMaxGVSegsPerMsg()
    {
        return 0;
    }
    
    /**
     * Ersetzt die Datei origFile gegen tmpFile.
     * Nach dem Loeschen der Datei origFile wartet die Methode jedoch maximal 20 Sekunden,
     * um sicherzustellen, dass z.Bsp. Virenscanner die Datei wieder losgelassen haben und
     * sie wirklich verschwunden ist, bevor tmpFile auf den Namen von origFile umbenannt wird.
     * Wichtig ist, dass zum Zeitpunkt des Aufrufes dieser Methode alle Streams auf die
     * Dateien bereits geschlossen wurden. Die Schreibvorgaenge auf die Dateien muessen also
     * abgeschlossen sein. Heisst: "os.close()" nicht erst im finally-Block machen sondern
     * VOR dem Aufruf dieser Methode.
     * @param origFile die originale zu ersetzende Datei.
     * @param tmpFile die neue Datei, welche die originale ersetzen soll.
     */
    protected void safeReplace(File origFile, File tmpFile)
    {
        HBCIUtils.log("saving passport file " + origFile, HBCIUtils.LOG_DEBUG);
        
        if (origFile.exists()) // Nur loeschen, wenn es ueberhaupt existiert
        {
            HBCIUtils.log("deleting old passport file " + origFile, HBCIUtils.LOG_DEBUG);
            if (!origFile.delete())
                HBCIUtils.log("delete method for " + origFile + " returned false", HBCIUtils.LOG_ERR);
        }

        // Wenn die Datei noch existiert, warten wir noch etwas
        int retry = 0;
        while (origFile.exists() && retry++ < 20)
        {
            try
            {
                HBCIUtils.log("wait a little bit, maybe another thread (antivirus scanner) holds a lock, file still exists", HBCIUtils.LOG_WARN);
                Thread.sleep(1000L);
            }
            catch (InterruptedException e)
            {
                HBCIUtils.log("interrupted", HBCIUtils.LOG_WARN);
                break;
            }
            if (!origFile.exists())
            {
                HBCIUtils.log("passport file now gone: " + origFile, HBCIUtils.LOG_INFO);
                break;
            }
        }
        
        // Datei existiert immer noch, dann brauchen wir das Rename gar nicht erst versuchen
        if (origFile.exists())
            throw new HBCI_Exception("could not delete " + origFile);

        // Das Rename versuchen wir jetzt auch wiederholt mehrfach
        retry = 0;
        HBCIUtils.log("renaming " + tmpFile.getName() + " to " + origFile.getName(), HBCIUtils.LOG_DEBUG);
        while (!origFile.exists() && retry++ < 20)
        {
            if (!tmpFile.renameTo(origFile))
                HBCIUtils.log("rename method for " + tmpFile + " to " + origFile + " returned false", HBCIUtils.LOG_ERR);
            
            if (origFile.exists())
            {
                HBCIUtils.log("new passport file now exists: " + origFile, HBCIUtils.LOG_DEBUG);
                break;
            }
            
            try
            {
                HBCIUtils.log("wait a little bit, maybe another thread (antivirus scanner) holds a lock, file still not renamed", HBCIUtils.LOG_WARN);
                Thread.sleep(1000L);
            }
            catch (InterruptedException e)
            {
                HBCIUtils.log("interrupted", HBCIUtils.LOG_WARN);
                break;
            }
        }
        
        if (!origFile.exists())
            throw new HBCI_Exception("could not rename " + tmpFile.getName() + " to " + origFile.getName());
    }
}
