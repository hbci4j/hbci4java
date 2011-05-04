
/*  $Id: HBCIPassportSIZRDHFile.java,v 1.1 2011/05/04 22:37:43 willuhn Exp $

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

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;

/** <p>Passport-Klasse für die Verwendung von SIZ-RDH-Schlüsseldateien mit <em>HBCI4Java</em>. 
    SIZ-RDH-Schlüsseldateien sind Schlüsseldateien für RDH-Zugänge, die von
    anderer HBCI-Software erzeugt und verwendet werden (z.B. von <em>StarMoney</em>). Soll eine solche
    Schlüsseldatei sowohl mit der anderen Software als auch mit <em>HBCI4Java</em>
    verwendet werden, so kann das mit dieser Passport-Variante geschehen. Soll die Schlüsseldatei
    nur noch mit <em>HBCI4Java</em> benutzt werden, so ist eine Konvertierung der Schlüsseldatei
    in ein anderes Passport-Format (vorzugsweise <code>RDHNew</code>) zu empfehlen. Dazu kann der
    <em>HBCI4Java Passport Editor</em> oder das Tool
    {@link org.kapott.hbci.tools.ConvertSIZRDHPassport} verwendet werden.</p>
    <p><b>Achtung: In der reinen GPL-Version von <em>HBCI4Java</em> ist diese Klasse nicht
    funktionstüchtig.</b> Der Grund dafür ist, dass <code>SIZRDHFile</code> eine zusätzliche
    Bibliothek zur Laufzeit benötigt. Diese Bibliothek steht nur als Binary-Version unter
    <a href="http://hbci4java.kapott.org#download">http://hbci4java.kapott.org</a> zur Verfügung.
    Erst nach Installation dieser Bibliothek können SIZRDHFile-Passports benutzt werden.</p>
    <p><b>Siehe auch die Datei <code>README.SIZRDHFile</code>.</b></p> */    
public class HBCIPassportSIZRDHFile
    extends AbstractRDHSWFileBasedPassport
{
    private native void readData(String filename);
    private native void saveData(String filename);
    
    private String passphrase;
        
    public HBCIPassportSIZRDHFile(Object init,int dummy)
    {
        super(init);
    }
    
    public HBCIPassportSIZRDHFile(Object initObject)
    {
        this(initObject,0);
        setParamHeader("client.passport.SIZRDHFile");

        String  filename=HBCIUtils.getParam(getParamHeader()+".filename");
        if (filename==null) {
            throw new NullPointerException(getParamHeader()+".filename must not be null");
        }
        
        boolean init=HBCIUtils.getParam(getParamHeader()+".init","1").equals("1");
        
        String libname=HBCIUtils.getParam(getParamHeader()+".libname");
        if (libname==null) {
            throw new NullPointerException(getParamHeader()+".libname must not be null");
        }
        System.load(libname);

        HBCIUtils.log("loading passport data from file "+filename,HBCIUtils.LOG_DEBUG);
        setFilename(filename);

        if (init) {
            HBCIUtils.log("loading data from file "+filename,HBCIUtils.LOG_DEBUG);

            setFilterType("None");
            setPort(new Integer(3000));

            if (!new File(filename).canRead()) {
                HBCIUtils.log("have to create new passport file",HBCIUtils.LOG_WARN);
                askForMissingData(false,true,true,false,false,true,false);
                saveChanges();
            }
            
            try {
                if (passphrase==null) {
                    StringBuffer retData=new StringBuffer();
                    HBCIUtilsInternal.getCallback().callback(this,
                                                     HBCICallback.NEED_PASSPHRASE_LOAD,
                                                     HBCIUtilsInternal.getLocMsg("CALLB_NEED_PASS"),
                                                     HBCICallback.TYPE_SECRET,
                                                     retData);
                    LogFilter.getInstance().addSecretData(retData.toString(),"X",LogFilter.FILTER_SECRETS);
                    setPassphrase(retData.toString());
                }

                setCountry("DE");
                setPort(new Integer(3000));
                
                readData(getFilename());
                
                if (askForMissingData(false,true,true,false,false,true,true))
                    saveChanges();
            } catch (Exception e) {
                throw new HBCI_Exception("*** error while reading passport file",e);
            }
        }
    }
    
    private void setPassphrase(String st)
    {
        this.passphrase=st;
    }
    
    public String getPassphrase()
    {
        return passphrase;
    }
    
    public void saveChanges()
    {
        try {
            File passportfile=new File(getFilename());
            File directory=passportfile.getAbsoluteFile().getParentFile();
            String prefix=passportfile.getName()+"_";
            File tempfile=File.createTempFile(prefix,"",directory);

            saveData(tempfile.getAbsolutePath());

            passportfile.delete();
            tempfile.renameTo(passportfile);
        } catch (Exception e) {
            throw new HBCI_Exception("*** saving of passport file failed",e);
        }
    }

    public int askForEntryIdx(String possibilities)
    {
        StringBuffer sb=new StringBuffer(possibilities);
        HBCIUtilsInternal.getCallback().callback(this,
                                         HBCICallback.NEED_SIZENTRY_SELECT,
                                         "*** select one of the following entries",
                                         HBCICallback.TYPE_TEXT,
                                         sb);
        return Integer.parseInt(sb.toString());
    }
    
    public String getProfileVersion()
    {
    	// old SIZRDH format can only be used for RDH-1 keys
    	return "1";
    }
}
