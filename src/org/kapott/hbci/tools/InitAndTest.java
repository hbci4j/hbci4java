
/*  $Id: InitAndTest.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;

/** <p>Tool zum Initialisieren und Testen eines HBCI-Passports.  Dieses Tool dient
    einerseits als Vorlage für die Benutzung von <em>HBCI4Java</em> in eigenen Anwendungen und
    gleichzeitig als Tool, um ein HBCI-Passport einzurichten und zu initialisieren.</p><p>
    Für das Einrichten und Initialisieren eines HBCI-Passports gibt es keine
    speziellen Funktionen, statt dessen wird ein Passport einfach so benutzt, als
    ob es schon vorhanden wäre. Alle fehlenden Daten holt sich <em>HBCI4Java</em> selbstständig
    entweder über den Callback-Mechanismus vom Anwender oder durch spezielle
    HBCI-Dialoge von der Bank.</p><p>
    Aus diesem Grund kann dieses Tool sowohl zum Testen eines schon existierenden Passports
    wie auch zum Einrichten eines neuen Passports verwendet werden, ohne etwas am
    Programmcode zu ändern. In der gleichen Weise wie dieses Tool kann auch eine
    komplexere Anwendung <em>HBCI4Java</em> benutzen, in diesem Tool fehlt nur das Hinzufügen
    von Geschäftsvorfällen zum HBCI-Dialog.</p><p>
    Beim Start des Programmes mit
    <code>java&nbsp;org.kapott.hbci.tools.InitAndTest</code>
    werden verschiedene Parameter über die Standardeingabe abgefragt.
    Konkret handelt es sich dabei um alle HBCI-Parameter, die für die Initialisierung
    eines Passports und dessen Verwendung benötigt werden. Die HBCI-Parameter werden mit
    den eingegeben Werten initialisiert. Anschließend wird ein Passport-Objekt erzeugt
    und ein "leerer" HBCI-Dialog (d.h. einer, der keine Geschäftsvorfälle enthält) ausgeführt.</p><p>
    Bei diesem Vorgang sorgt <em>HBCI4Java</em> selbst dafür, dass alle relevanten Daten vorhanden sind
    bzw. initialisiert diese entspechend. Läuft das Programm fehlerfrei durch, so ist sichergestellt,
    dass das benutzte Passport (=Sicherheitsmedium) korrekt initialisiert und funktionsbereit
    ist.</p> */
public final class InitAndTest
{
    private static class MyCallback
        extends HBCICallbackConsole
    {
        @Override
        public synchronized void status(HBCIPassport passport, int statusTag,
                                        Object[] o)
        {
            // disable status output
        }

    }
	
    private static HBCIPassport passport;
    private static HBCIHandler  hbciHandle;
    
    public static void main(String[] args)
        throws IOException
    {
        try {
            HBCIUtils.init(null,new MyCallback());
            readBasicParams();

            readPassportParams();
            passport=AbstractHBCIPassport.getInstance();

            readHBCIVersion();
            readActions();
            
            if (HBCIUtils.getParam("action.resetBPD").equals("1")) {
            	passport.clearBPD();
            }
            if (HBCIUtils.getParam("action.resetUPD").equals("1")) {
            	passport.clearUPD();
            }
            hbciHandle=new HBCIHandler(HBCIUtils.getParam("client.passport.hbciversion.default"),
                                       passport);

            /* HBCIExecStatus ret=hbciHandle.execute();
            System.out.println("ExecStatus");
            System.out.println(ret.toString());
            System.out.println("ExecStatusEnd");
            System.out.println("ExecStatusError");
            System.out.println(ret.getErrorString());
            System.out.println("ExecStatusErrorEnd"); */
            
            printSupportedGVs(hbciHandle);
            
            System.out.println();
            System.out.println("finished.");
            System.out.println();
        } finally {
            if (hbciHandle!=null) {
                hbciHandle.close();
            } else if (passport!=null) {
                passport.close();
            }
        }
    }
    
    private static void readParam(String paramName,String def,String descr)
        throws IOException
    {
        System.out.println();
        System.out.println(descr);
        System.out.println("press ENTER to accept the default; '-' to set no value for this parameter");
        System.out.print(paramName+" ["+def+"]: ");
        System.out.flush();
        
        String value=new BufferedReader(new InputStreamReader(System.in)).readLine();
        
        if (value.equals("-")) {
            value=null;
        } else if (value.length()==0) {
            value=def;
        }
        
        if (value!=null) {
            System.out.println(paramName+"="+value);
            HBCIUtils.setParam(paramName,value);
        }
    }
    
    private static void readBasicParams()
        throws IOException
    {
        readParam("client.connection.localPort",null,"local tcp-port to be used for outgoing connections");
        readParam("comm.standard.socks.server",null,"SOCKS server to be used for outgoing connections (will be ignored for PIN/TAN)");
        readParam("log.loglevel.default","5","loglevel for HBCI4Java-messages (from 0(no logging) to 5(really heavy)");
        readParam("kernel.rewriter",HBCIUtils.getParam("kernel.rewriter"),"rewriter modules to be activated");
    }
    
    private static void readPassportParams()
        throws IOException
    {
        readParam("client.passport.default",null,"enter type of media you have (Anonymous, DDV, RDHNew, RDH (deprecated), PinTan, SIZRDHFile or RDHXFile)");
        String type=HBCIUtils.getParam("client.passport.default","");
        
        if (type.equals("Anonymous")) {
            readParam("client.passport.Anonymous.filename","passport_anon.dat","filename to be used for your HBCI4Java keyfile.");
            readParam("client.passport.Anonymous.init","1","never change this value!");
        } else if (type.equals("DDV")) {
            readParam("client.passport.DDV.path","./","the path where to store a file to cash information about your HBCI account");
            readParam("client.passport.DDV.libname.ddv","/home/kleiner/projects/hbci2/chipcard/lib/libhbci4java-card-linux.so","the name of the library needed to use the CTAPI interface of your chipcard terminal");
            readParam("client.passport.DDV.libname.ctapi","/usr/lib/libctapi-cyberjack.so","the name of the library containing the CTAPI interface to your chipcard terminal");
            readParam("client.passport.DDV.port","1","the port to which your chipcard terminal is connected (in most cases 1, 0 or 2)");
            readParam("client.passport.DDV.ctnumber","0","the logical number for your chipcard terminal, can be 0 in most cases");
            readParam("client.passport.DDV.usebio","0","use the biometric interface of Reiner-SCT chipcard terminals (0 or 1)");
            readParam("client.passport.DDV.softpin","0","use the keypad of your chipcard terminal (0) or your PC-keyboard (1) to enter the PIN for your HBCI chipcard");
            readParam("client.passport.DDV.entryidx","1","enter the index, which HBCI account stored on the card should be used");
        } else if (type.equals("RDH")) {
            readParam("client.passport.RDH.filename","my_passport.dat","filename to be used for your HBCI4Java keyfile. DONT LOOSE THIS FILE!");
            readParam("client.passport.RDH.init","1","never change this value!");
        } else if (type.equals("RDHNew")) {
            readParam("client.passport.RDHNew.filename","my_passport.dat","filename to be used for your HBCI4Java keyfile. DONT LOOSE THIS FILE!");
            readParam("client.passport.RDHNew.init","1","never change this value!");
        } else if (type.equals("PinTan")) {
            readParam("client.passport.PinTan.filename","my_passport_pintan.dat","filename to be used for your PIN/TAN keyfile");
            readParam("client.passport.PinTan.checkcert","1","whether to check the HTTPS-certificate of the server (1) or not (0)");
            readParam("client.passport.PinTan.certfile",null,"filename with a SSL-certificate for HTTPS-communication; leave blank when you don't want to check the certificate or when the certificate can be checked with the java-builtin CA database");
            readParam("client.passport.PinTan.proxy",null,"host:port for proxy server to be used; leave blank when you want direct connections to be made");
            readParam("client.passport.PinTan.proxyuser",null,"when you need proxy authentication enter the username here; leave empty to be queried on demand");
            readParam("client.passport.PinTan.proxypass",null,"when you need proxy authentication enter the passphrase here; leave empty to be queried on demand");
            readParam("client.passport.PinTan.init","1","never change this value!");
        } else if (type.equals("SIZRDHFile")) {
            readParam("client.passport.SIZRDHFile.filename","secret.key","filename of SIZ-RDH-keyfile to be used. MAKE A BACKUP OF THIS FILE BEFORE USE!");
            readParam("client.passport.SIZRDHFile.libname","/usr/lib/libhbci4java-sizrdh.so","filename of native library for accessing SIZ RDH files");
            readParam("client.passport.SIZRDHFile.init","1","never change this value!");
        } else if (type.equals("RDHXFile")) {
            readParam("client.passport.RDHXFile.filename","secret.key","filename of SIZ-RDH2-keyfile to be used. MAKE A BACKUP OF THIS FILE BEFORE USE!");
            readParam("client.passport.RDHXFile.init","1","never change this value!");
        }
    }
    
    private static void readHBCIVersion()
        throws IOException
    {
        String pversion=passport.getHBCIVersion();
        readParam("client.passport.hbciversion.default",((pversion.length()!=0)?pversion:"210"),"the hbci-version to be used; may be '201', '210', '220', '300' or 'plus'");
    }
    
    private static void readActions()
    	throws IOException
    {
    	readParam("action.resetBPD","1","reset and refetch BPD (1/0)");
    	readParam("action.resetUPD","1","reset and refetch UPD (1/0)");
    }
    
    private static void printSupportedGVs(HBCIHandler handle)
    {
        Properties gvcodes=getGVCodes();
        
        String[] codes=gvcodes.keySet().toArray(new String[0]);
        Arrays.sort(codes);
        
        System.out.println();
        System.out.println("lowlevel GVs supported by institute and HBCI4Java:");
        for (int i=0; i<codes.length; i++) {
            String gvcode=codes[i];
            String name=gvcodes.getProperty(gvcode);
            if (!name.startsWith("Template")) {
                System.out.println("  "+gvcode+" ("+name+")");
            }
        }
        System.out.println();
        System.out.println("lowlevel GVs supported by institute but unknown to HBCI4Java:");
        for (int i=0; i<codes.length; i++) {
            String gvcode=codes[i];
            String name=gvcodes.getProperty(gvcode);
            if (name.startsWith("Template")) {
                System.out.println("  "+gvcode);
            }
        }
    }
    
    private static Properties getGVCodes()
    {
        Properties ret=new Properties();
        Properties bpd=passport.getBPD();
        
        for (Enumeration e=bpd.propertyNames();e.hasMoreElements();) {
            String key=(String)e.nextElement();
            
            if (key.startsWith("Params") &&
                key.endsWith(".SegHead.code")) 
            {
                String gvcode=bpd.getProperty(key);
                    
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
                }
                ret.setProperty(gvcode,gvname);
            }
        }
        
        return ret;
    }
}
