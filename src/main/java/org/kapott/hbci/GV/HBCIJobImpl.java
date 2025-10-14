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

package org.kapott.hbci.GV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.dialog.KnownReturncode;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.exceptions.JobNotSupportedException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.passport.HBCIPassportList;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.protocol.SyntaxElement;
import org.kapott.hbci.protocol.factory.SEGFactory;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.status.HBCIRetVal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;

public abstract class HBCIJobImpl<T extends HBCIJobResultImpl>
    implements HBCIJob<T>
{
    private String name;              /* Job-Name mit Versionsnummer */
    private String jobName;           /* Job-Name ohne Versionsnummer */
    public String getJobName() {
		return jobName;
	}

	private String segVersion;        /* Segment-Version */
    private Properties llParams;       /* Eingabeparameter für diesen GV (Saldo.KTV.number) */
    private HBCIPassportList passports;
    protected T jobResult;         /* Objekt mit Rückgabedaten für diesen GV */
    private HBCIHandler parentHandler;
    private int idx;                  /* idx gibt an, der wievielte task innerhalb der aktuellen message
                                         dieser GV ist */
    private int contentCounter;       /* Zähler, wie viele Rückgabedaten bereits in outStore eingetragen wurden 
                                           (entspricht der anzahl der antwort-segmente!)*/
    private Hashtable<String, String[][]> constraints;    /* Festlegungen, welche Parameter eine Anwendung setzen muss, wie diese im
                                         HBCI-Kernel umgesetzt werden und welche default-Werte vorgesehen sind; 
                                         die Hashtable hat als Schlüssel einen String, der angibt, wie ein Wert aus einer
                                         Anwendung heraus zu setzen ist. Der dazugehörige Value ist ein Array. Jedes Element
                                         dieses Arrays ist ein String[2], wobei das erste Element angibt, wie der Pfadname heisst,
                                         unter dem der anwendungs-definierte Wert abzulegen ist, das zweite Element gibt den
                                         default-Wert an, falls für diesen Namen *kein* Wert angebeben wurde. Ist der default-
                                         Wert="", so kann das Syntaxelement weggelassen werden. Ist der default-Wert=null,
                                         so *muss* die Anwendung einen Wert spezifizieren */
    private Hashtable<String, Integer> logFilterLevels; /* hier wird für jeden hl-param-name gespeichert, ob der dazugehörige wert
                                          über den logfilter-Mechanimus geschützt werden soll */
    
    private String externalId;
    private int loopCount = 0;
    private boolean haveTan = false;
    private boolean skip = false;
    private boolean useResult = true;
    private boolean haveVop = false;
    
    private HashSet<String> indexedConstraints;
    
    protected HBCIJobImpl(HBCIHandler parentHandler,String jobnameLL,T jobResult)
    {
        findSpecNameForGV(jobnameLL,parentHandler);
        this.llParams=new Properties();
        
        this.passports=new HBCIPassportList();
        this.passports.addPassport((HBCIPassportInternal)parentHandler.getPassport(),HBCIPassport.ROLE_ISS);
        
        this.jobResult=jobResult;
        this.jobResult.setParentJob(this);
        
        this.contentCounter=0;
        this.constraints=new Hashtable<String, String[][]>();
        this.logFilterLevels=new Hashtable<String, Integer>();
        this.indexedConstraints=new HashSet<String>();
        
        this.parentHandler=parentHandler;

        /* offensichtlich soll ein GV mit dem Namen name in die nachricht
           aufgenommen werden. da GV durch segmente definiert sind, und einige
           dieser segmente ein request-tag benoetigen (siehe klasse
           SyntaxElement), wird hier auf jeden fall das request-tag gesetzt.
           wenn es *nicht* benoetigt wird, schadet es auch nichts. und es ist
           auf keinen fall "zu viel" gesetzt, da dieser code nur ausgefuehrt wird,
           wenn das jeweilige segment tatsaechlich erzeugt werden soll */
        llParams.setProperty(this.name,"requested");
    }
    
    /**
     * Liefert die Segment-Kennung des Jobs.
     * @return die Segment-Kennung oder NULL, wenn sie nicht ermittelbar war.
     */
    public String getHBCICode()
    {
        StringBuffer ret=null;
        
        // Macht aus z.Bsp. "KUmsZeit5" -> "KUmsZeitPar5.SegHead.code"
        StringBuffer searchString=new StringBuffer(name);
        for (int i=searchString.length()-1;i>=0;i--) {
            if (!(searchString.charAt(i)>='0' && searchString.charAt(i)<='9')) {
                searchString.insert(i+1,"Par");
                searchString.append(".SegHead.code");
                break;
            }
        }
        
        HBCIPassportInternal passport=getMainPassport();
        StringBuffer         tempkey=new StringBuffer();
        
        // durchsuchen aller param-segmente nach einem job mit dem jobnamen des
        // aktuellen jobs
        for (Enumeration i=passport.getBPD().propertyNames();i.hasMoreElements();) {
            String key=(String)i.nextElement();
            
            if (key.indexOf("Params")==0) {
                tempkey.setLength(0);
                tempkey.append(key);
                tempkey.delete(0,tempkey.indexOf(".")+1);
                
                if (tempkey.toString().equals(searchString.toString())) {
                    ret=new StringBuffer(passport.getBPD().getProperty(key));
                    ret.replace(1,2,"K");
                    ret.deleteCharAt(ret.length()-1);
                    break;
                }
            }
        }
        
        return ret != null ? ret.toString() : null;
    }
    
    /**
     * Liefert true, wenn die Prüfung der BPD übersprungen werden soll.
     * @return true, wenn die Prüfung der BPD übersprungen werden soll.
     */
    protected boolean skipBPDCheck()
    {
      return false;
    }
    
    /* gibt zu einem gegebenen jobnamen des namen dieses jobs in der syntax-spez.
     * zurück (also mit angehängter versionsnummer)
     */
    private void findSpecNameForGV(String jobnameLL,HBCIHandler handler)
    {
      if (this.skipBPDCheck())
      {
        this.jobName    = jobnameLL;
        this.segVersion = "1";
        this.name       = jobnameLL + this.segVersion;
        return;
      }
      
        int          maxVersion=0;
        StringBuffer key=new StringBuffer();
        
        // alle param-segmente durchlaufen
        Properties bpd = handler.getPassport().getBPD();
        for (Enumeration i=bpd.propertyNames();i.hasMoreElements();) {
            String path = (String)i.nextElement();
            key.setLength(0);
            key.append(path);
            
            if (key.indexOf("Params")==0) {
                key.delete(0,key.indexOf(".")+1);
                // wenn segment mit namen des aktuellen jobs gefunden wurde
                
                if (key.indexOf(jobnameLL+"Par")==0 &&
                    key.toString().endsWith(".SegHead.code")) 
                {
                  // willuhn 2011-06-06 Maximal zulaessige Segment-Version ermitteln
                  // Hintergrund: Es gibt Szenarien, in denen nicht die hoechste verfuegbare
                  // Versionsnummer verwendet werden kann, weil die Voraussetzungen impliziert,
                  // die beim User nicht gegeben sind. Mit diesem Parameter kann die maximale
                  // Version nach oben begrenzt werden. In AbstractPinTanPassport#setBPD() ist
                  // ein konkretes Beispiel enthalten (Bank macht HITANS5 und damit HHD 1.4, der
                  // User hat aber nur ein HHD-1.3-tauglichen TAN-Generator)
                  int maxAllowedVersion = Integer.parseInt(HBCIUtils.getParam("kernel.gv." + bpd.getProperty(path,"default") + ".segversion.max","0"));
                  
                  key.delete(0,jobnameLL.length()+("Par").length());
                    
                    // extrahieren der versionsnummer aus dem spez-namen
                    String st=key.substring(0,key.indexOf("."));
                    int    version=0;
                    
                    try {
                        version=Integer.parseInt(st);
                    } catch (Exception e) {
                        HBCIUtils.log("found invalid job version: key="+key+", jobnameLL="+jobnameLL+" (this is a known, but harmless bug)", HBCIUtils.LOG_WARN);
                    }
                    
                    // willuhn 2011-06-06 Segment-Versionen ueberspringen, die groesser als die max. zulaessige sind
                    if (maxAllowedVersion > 0 && version > maxAllowedVersion)
                    {
                      HBCIUtils.log("skipping segment version " + version + " for task " + jobnameLL + ", larger than allowed version " + maxAllowedVersion, HBCIUtils.LOG_DEBUG);
                      continue;
                    }
                    // merken der größten jemals aufgetretenen versionsnummer
                    if (version!=0) {
                        HBCIUtils.log("task "+jobnameLL+" is supported with segment version "+st,HBCIUtils.LOG_DEBUG2);
                        if (version>maxVersion) {
                            maxVersion=version;
                        }
                    }
                }
            }
        }
        
        if (maxVersion==0)
        {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_GVNOTSUPP",jobnameLL);
            if (!HBCIUtilsInternal.ignoreError(handler.getPassport(),"client.errors.ignoreJobNotSupported",msg))
                throw new JobNotSupportedException(jobnameLL);
            
            maxVersion = 1;
            HBCIUtils.log("Using segment version " + maxVersion + " for job " + jobnameLL + ", although not found in BPD. This may fail", HBCIUtils.LOG_WARN);
        }
        
        // namen+versionsnummer speichern
        this.jobName    = jobnameLL;
        this.segVersion = Integer.toString(maxVersion);
        this.name       = jobnameLL + this.segVersion;
    }
    
    /**
     * Legt die Versionsnummer des Segments manuell fest.
     * Ist u.a. noetig, um HKTAN-Segmente in genau der Version zu senden, in der
     * auch die HITANS empfangen wurden. Andernfalls koennte es passieren, dass
     * wir ein HKTAN mit einem TAN-Verfahren senden, welches in dieser HKTAN-Version
     * gar nicht von der Bank unterstuetzt wird. Das ist ein Dirty-Hack, ich weiss ;)
     * Falls das noch IRGENDWO anders verwendet wird, muss man hoellisch aufpassen,
     * dass alle Stellen, wo "this.name" bzw. "this.segVersion" direkt oder indirekt
     * verwendet wurde, ebenfalls beruecksichtigt werden.
     * @param version die neue Versionsnummer.
     */
    public synchronized void setSegVersion(String version)
    {
      if (version == null || version.length() == 0)
      {
        HBCIUtils.log("tried to change segment version for task " + this.jobName + " explicit, but no version given",HBCIUtils.LOG_WARN);
        return;
      }
      
      // Wenn sich die Versionsnummer nicht geaendert hat, muessen wir die
      // Huehner ja nicht verrueckt machen ;)
      if (version.equals(this.segVersion))
        return;
      
      HBCIUtils.log("changing segment version for task " + this.jobName + " explicitly from " + this.segVersion + " to " + version,HBCIUtils.LOG_DEBUG);

      // Der alte Name
      String oldName = this.name;
      
      // Neuer Name und neue Versionsnummer
      this.segVersion = version;
      this.name       = this.jobName + version;

      // Bereits gesetzte llParams fixen
      String[] names = this.llParams.keySet().toArray(new String[this.llParams.size()]);
      for (String s:names)
      {
        if (!s.startsWith(oldName))
          continue; // nicht betroffen

        // Alten Schluessel entfernen und neuen einfuegen
        String value = this.llParams.getProperty(s);
        String newName = s.replaceFirst(oldName,this.name);
        this.llParams.remove(s);
        this.llParams.setProperty(newName,value);
      }

      // Destination-Namen in den LowLevel-Parameter auf den neuen Namen umbiegen
      Enumeration<String> e = constraints.keys();
      while (e.hasMoreElements())
      {
        String frontendName = e.nextElement();
        String[][] values = constraints.get(frontendName);
        for (int i=0;i<values.length;++i)
        {
          String[] value = values[i];
          // value[0] ist das Target
          if (!value[0].startsWith(oldName))
            continue;

          // Hier ersetzen wir z.Bsp. "TAN2Step5.process" gegen "TAN2Step3.process"
          value[0] = value[0].replaceFirst(oldName,this.name);
        }
      }
    }
    
    public int getMaxNumberPerMsg()
    {
        int ret=1;
        
        StringBuffer searchString=new StringBuffer(name);
        for (int i=searchString.length()-1;i>=0;i--) {
            if (!(searchString.charAt(i)>='0' && searchString.charAt(i)<='9')) {
                searchString.insert(i+1,"Par");
                searchString.append(".maxnum");
                break;
            }
        }
        
        HBCIPassportInternal passport=getMainPassport();
        StringBuffer         tempkey=new StringBuffer();
        
        for (Enumeration i=passport.getBPD().propertyNames();i.hasMoreElements();) {
            String key=(String)i.nextElement();
            
            if (key.indexOf("Params")==0) {
                tempkey.setLength(0);
                tempkey.append(key);
                tempkey.delete(0,tempkey.indexOf(".")+1);
                
                if (tempkey.toString().equals(searchString.toString())) {
                    ret=Integer.parseInt(passport.getBPD().getProperty(key));
                    break;
                }
            }
        }
        
        return ret;
    }

    public int getMinSigs()
    {
        int ret=0;
        
        StringBuffer searchString=new StringBuffer(name);
        for (int i=searchString.length()-1;i>=0;i--) {
            if (!(searchString.charAt(i)>='0' && searchString.charAt(i)<='9')) {
                searchString.insert(i+1,"Par");
                searchString.append(".minsigs");
                break;
            }
        }
        
        HBCIPassportInternal passport=getMainPassport();
        StringBuffer         tempkey=new StringBuffer();
        
        for (Enumeration i=passport.getBPD().propertyNames();i.hasMoreElements();) {
            String key=(String)i.nextElement();
            
            if (key.indexOf("Params")==0) {
                tempkey.setLength(0);
                tempkey.append(key);
                tempkey.delete(0,tempkey.indexOf(".")+1);
                
                if (tempkey.toString().equals(searchString.toString())) {
                    ret=Integer.parseInt(passport.getBPD().getProperty(key));
                    break;
                }
            }
        }
        
        return ret;
    }
    
    public int getSecurityClass()
    {
        int ret=1;
        
        StringBuffer searchString=new StringBuffer(name);
        for (int i=searchString.length()-1;i>=0;i--) {
            if (!(searchString.charAt(i)>='0' && searchString.charAt(i)<='9')) {
                searchString.insert(i+1,"Par");
                searchString.append(".secclass");
                break;
            }
        }
        
        HBCIPassportInternal passport=getMainPassport();
        StringBuffer         tempkey=new StringBuffer();
        
        for (Enumeration i=passport.getBPD().propertyNames();i.hasMoreElements();) {
            String key=(String)i.nextElement();
            
            if (key.indexOf("Params")==0) {
                tempkey.setLength(0);
                tempkey.append(key);
                tempkey.delete(0,tempkey.indexOf(".")+1);
                
                if (tempkey.toString().equals(searchString.toString())) {
                    ret=Integer.parseInt(passport.getBPD().getProperty(key));
                    break;
                }
            }
        }
        
        return ret;
    }

    protected void addConstraint(String frontendName,String destinationName,String defValue,int logFilterLevel)
    {
        addConstraint(frontendName, destinationName, defValue, logFilterLevel, false);
    }

    protected void addConstraint(String frontendName,String destinationName,String defValue,int logFilterLevel,boolean indexed)
    {
        // value ist array:(lowlevelparamname, defaultvalue)
        String[] value=new String[2];
        value[0]=getName()+"."+destinationName;
        value[1]=defValue;

        // alle schon gespeicherten "ziel-lowlevelparameternamen" für den gewünschten
        // frontend-namen suchen
        String[][] values=(constraints.get(frontendName));

        if (values==null) {
            // wenn es noch keine gibt, ein neues frontend-ding anlegen
            values=new String[1][];
            values[0]=value;
        } else {
            ArrayList<String[]> a=new ArrayList<String[]>(Arrays.asList(values));
            a.add(value);
            values=(a.toArray(values));
        }

        constraints.put(frontendName,values);
        
        if (indexed) {
            indexedConstraints.add(frontendName);
        }
        
        if (logFilterLevel>0) {
        	logFilterLevels.put(frontendName,Integer.valueOf(logFilterLevel));
        }
    }
    
    public void verifyConstraints()
    {
        HBCIPassportInternal passport=getMainPassport();
        
        // durch alle gespeicherten constraints durchlaufen
        for (Iterator<String> i=constraints.keySet().iterator();i.hasNext();) {
            // den frontendnamen für das constraint ermitteln
            String     frontendName=(i.next());
            
            // dazu alle ziel-lowlevelparameter mit default-wert extrahieren
            String[][] values=(constraints.get(frontendName));

            // durch alle ziel-lowlevel-parameternamen durchlaufen, die gesetzt werden müssen
            for (int j=0;j<values.length;j++) {
            	//Array mit Pfadname und default Wert 
                String[] value=values[j];
                // lowlevel-name (Pfadname) des parameters (z.B. wird Frontendname src.bic zum Pfad My.bic
                String   destination=value[0];
                // default-wert des parameters, wenn keiner angegeben wurde
                String   defValue=value[1];

                String   givenContent=getLowlevelParam(destination);
                if (givenContent==null && indexedConstraints.contains(frontendName)) {
                    givenContent = getLowlevelParam(insertIndex(destination, 0));
                }

                String   content=null;

                content=defValue;
                if (givenContent!=null && givenContent.length()!=0)
                    content=givenContent;

                if (content==null) {
                    String msg=HBCIUtilsInternal.getLocMsg("EXC_MISSING_HL_PROPERTY",frontendName);
                    if (!HBCIUtilsInternal.ignoreError(passport,"client.errors.ignoreWrongJobDataErrors",msg))
                        throw new InvalidUserDataException(msg);
                    content="";
                }

                // evtl. default-wert als aktuellen wert setzen (naemlich dann,
                // wenn kein content angegeben wurde (givenContent==null), aber
                // ein default-Content definiert wurde (content.length()!=0)
                if (content.length()!=0 && givenContent==null)
                    setLowlevelParam(destination,content);
            }
        }
        
        // verify if segment can be created
        SEG seg=null;
        try {
            seg=createJobSegment();
            seg.validate();
        } catch (Exception ex) {
            throw new HBCI_Exception("*** the job segment for this task can not be created",ex);
        } finally {
            if (seg!=null) {
                SEGFactory.getInstance().unuseObject(seg);
            }
        }
    }
    
    public SEG createJobSegment()
    {
        return createJobSegment(0);
    }
    
    public SEG createJobSegment(int segnum)
    {
        SEG seg=null;
        try {
            MsgGen gen=getParentHandler().getMsgGen();
            seg=SEGFactory.getInstance().createSEG(getName(),getName(),null,0,gen.getSyntax());
            for (Enumeration e=getLowlevelParams().propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();
                String value=getLowlevelParams().getProperty(key);
                seg.propagateValue(key,value,
                                   SyntaxElement.TRY_TO_CREATE,
                                   SyntaxElement.DONT_ALLOW_OVERWRITE);
            }
            seg.propagateValue(getName()+".SegHead.seq",Integer.toString(segnum),
                               SyntaxElement.DONT_TRY_TO_CREATE,
                               SyntaxElement.ALLOW_OVERWRITE);
        } catch (Exception ex) {
            throw new HBCI_Exception("*** the job segment for this task can not be created",ex);
        }
        
        return seg;
    }
    
    public List<String> getJobParameterNames()
    {
        MsgGen gen=getParentHandler().getMsgGen();
        return gen.getGVParameterNames(name);
    }
    
    public List<String> getJobResultNames()
    {
        MsgGen gen=getParentHandler().getMsgGen();
        return gen.getGVResultNames(name);
    }
    
    public Properties getJobRestrictions()
    {
        return passports.getMainPassport().getJobRestrictions(name);
    }
    
    /** Setzen eines komplexen Job-Parameters (Kontodaten). Einige Jobs benötigten Kontodaten
        als Parameter. Diese müssten auf "normalem" Wege durch drei Aufrufe von 
        {@link #setParam(String,String)} erzeugt werden (je einer für
        die Länderkennung, die Bankleitzahl und die Kontonummer). Durch Verwendung dieser
        Methode wird dieser Weg abgekürzt. Es wird ein Kontoobjekt übergeben, für welches
        die entsprechenden drei <code>setParam(String,String)</code>-Aufrufe automatisch
        erzeugt werden.
        @param paramname die Basis der Parameter für die Kontodaten (für "<code>my.country</code>",
        "<code>my.blz</code>", "<code>my.number</code>" wäre das also "<code>my</code>")
        @param acc ein Konto-Objekt, aus welchem die zu setzenden Parameterdaten entnommen werden */
    public void setParam(String paramname,Konto acc)
    {
        setParam(paramname, null, acc);
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJob#setParam(java.lang.String, java.lang.Integer, org.kapott.hbci.structures.Konto)
     */
    public void setParam(String paramname,Integer index,Konto acc)
    {
        if (acceptsParam(paramname+".country") && acc.country!=null && acc.country.length()!=0)
            setParam(paramname+".country",index,acc.country);
        
        if (acceptsParam(paramname+".blz") && acc.blz!=null && acc.blz.length()!=0)
            setParam(paramname+".blz",index,acc.blz);
        
        if (acceptsParam(paramname+".number") && acc.number!=null && acc.number.length()!=0)
            setParam(paramname+".number",index,acc.number);
        
        if (acceptsParam(paramname+".subnumber") && acc.subnumber!=null && acc.subnumber.length()!=0)
            setParam(paramname+".subnumber",index,acc.subnumber);
        
        if (acceptsParam(paramname+".name") && acc.name!=null && acc.name.length()!=0)
            setParam(paramname+".name",index,acc.name);
        
        if (acceptsParam(paramname+".curr") && acc.curr!=null && acc.curr.length()!=0) 
        	setParam(paramname+".curr",index,acc.curr);

        if (acceptsParam(paramname+".bic") && acc.bic!=null && acc.bic.length()!=0)
            setParam(paramname+".bic",index,acc.bic);
        
        if (acceptsParam(paramname+".iban") && acc.iban!=null && acc.iban.length()!=0)
            setParam(paramname+".iban",index,acc.iban);
        
    }

    /** Setzen eines komplexen Job-Parameters (Geldbetrag). Einige Jobs benötigten Geldbeträge
        als Parameter. Diese müssten auf "normalem" Wege durch zwei Aufrufe von 
        {@link #setParam(String,String)} erzeugt werden (je einer für
        den Wert und die Währung). Durch Verwendung dieser
        Methode wird dieser Weg abgekürzt. Es wird ein Value-Objekt übergeben, für welches
        die entsprechenden zwei <code>setParam(String,String)</code>-Aufrufe automatisch
        erzeugt werden.
        @param paramname die Basis der Parameter für die Geldbetragsdaten (für "<code>btg.value</code>" und
        "<code>btg.curr</code>" wäre das also "<code>btg</code>")
        @param v ein Value-Objekt, aus welchem die zu setzenden Parameterdaten entnommen werden */
    public void setParam(String paramname, Value v)
    {
        setParam(paramname, null, v);
    }

    public void setParam(String paramname, Integer index, Value v)
    {
        if (acceptsParam(paramname+".value"))
            setParam(paramname+".value",index,HBCIUtils.bigDecimal2String(v.getBigDecimalValue()));

        String curr=v.getCurr();
        if (acceptsParam(paramname+".curr") && curr!=null && curr.length()!=0)
            setParam(paramname+".curr",index,curr);
    }

    /** Setzen eines Job-Parameters, bei dem ein Datums als Wert erwartet wird. Diese Methode
        dient als Wrapper für {@link #setParam(String,String)}, um das Datum in einen korrekt
        formatierten String umzuwandeln. Das "richtige" Datumsformat ist dabei abhängig vom
        aktuellen Locale.
        @param paramName Name des zu setzenden Job-Parameters
        @param date Datum, welches als Wert für den Job-Parameter benutzt werden soll */
    public void setParam(String paramName, Date date)
    {
        setParam(paramName, null, date);
    }

    public void setParam(String paramName, Integer index, Date date)
    {
        setParam(paramName, index, HBCIUtils.date2StringISO(date));
    }

    /** Setzen eines Job-Parameters, bei dem ein Integer-Wert Da als Wert erwartet wird. Diese Methode
        dient nur als Wrapper für {@link #setParam(String,String)}.
        @param paramName Name des zu setzenden Job-Parameters
        @param i Integer-Wert, der als Wert gesetzt werden soll */     
    public void setParam(String paramName,int i)
    {
        setParam(paramName,Integer.toString(i));
    }
    
    protected boolean acceptsParam(String hlParamName)
    {
    	return constraints.get(hlParamName)!=null;
    }

    /** <p>Setzen eines Job-Parameters. Für alle Highlevel-Jobs ist in der Package-Beschreibung zum
        Package {@link org.kapott.hbci.GV} eine Auflistung aller Jobs und deren Parameter zu finden.
        Für alle Lowlevel-Jobs kann eine Liste aller Parameter entweder mit dem Tool
        {@link org.kapott.hbci.tools.ShowLowlevelGVs} oder zur Laufzeit durch Aufruf
        der Methode {@link org.kapott.hbci.manager.HBCIHandler#getLowlevelJobParameterNames(String)} 
        ermittelt werden.</p>
        <p>Bei Verwendung dieser oder einer der anderen <code>setParam()</code>-Methoden werden zusätzlich
        einige der Job-Restriktionen (siehe {@link #getJobRestrictions()}) analysiert. Beim Verletzen einer
        der überprüften Einschränkungen wird eine Exception mit einer entsprechenden Meldung erzeugt.
        Diese Überprüfung findet allerdings nur bei Highlevel-Jobs statt.</p>
        @param paramName der Name des zu setzenden Parameters.
        @param value Wert, auf den der Parameter gesetzt werden soll */
    @Override
    public void setParam(String paramName,String value)
    {
        setParam(paramName,null,value);
    }

    /** <p>Setzen eines Job-Parameters. Für alle Highlevel-Jobs ist in der Package-Beschreibung zum
        Package {@link org.kapott.hbci.GV} eine Auflistung aller Jobs und deren Parameter zu finden.
        Für alle Lowlevel-Jobs kann eine Liste aller Parameter entweder mit dem Tool
        {@link org.kapott.hbci.tools.ShowLowlevelGVs} oder zur Laufzeit durch Aufruf
        der Methode {@link org.kapott.hbci.manager.HBCIHandler#getLowlevelJobParameterNames(String)} 
        ermittelt werden.</p>
        <p>Bei Verwendung dieser oder einer der anderen <code>setParam()</code>-Methoden werden zusätzlich
        einige der Job-Restriktionen (siehe {@link #getJobRestrictions()}) analysiert. Beim Verletzen einer
        der überprüften Einschränkungen wird eine Exception mit einer entsprechenden Meldung erzeugt.
        Diese Überprüfung findet allerdings nur bei Highlevel-Jobs statt.</p>
        @param paramName der Name des zu setzenden Parameters.
        @param index Der index oder <code>null</code>, wenn kein Index gewünscht ist
        @param value Wert, auf den der Parameter gesetzt werden soll */
    @Override
    public void setParam(String paramName,Integer index,String value)
    {
    	// wenn der Parameter einen LogFilter-Level gesetzt hat, dann den
    	// betreffenden Wert zum Logfilter hinzufügen
    	Integer logFilterLevel=logFilterLevels.get(paramName);
    	if (logFilterLevel!=null && logFilterLevel.intValue()!=0) {
    		LogFilter.getInstance().addSecretData(value,"X",logFilterLevel.intValue());
    	}

        String[][]           destinations=constraints.get(paramName);
        HBCIPassportInternal passport=getMainPassport();
        
        if (destinations==null) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_PARAM_NOTNEEDED",new String[] {paramName,getName()});
            if (!HBCIUtilsInternal.ignoreError(passport,"client.errors.ignoreWrongJobDataErrors",msg))
                throw new InvalidUserDataException(msg);
            destinations=new String[0][];
        }
        
        if (value==null || value.length()==0) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_PARAM_EMPTY",new String[] {paramName,getName()});
            if (!HBCIUtilsInternal.ignoreError(passport,"client.errors.ignoreWrongJobDataErrors",msg))
                throw new InvalidUserDataException(msg);
            value="";
        }
        
        if (index!=null && !indexedConstraints.contains(paramName)) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_PARAM_NOTINDEXED",new String[] {paramName,getName()});
            if (!HBCIUtilsInternal.ignoreError(passport,"client.errors.ignoreWrongJobDataErrors",msg))
                throw new InvalidUserDataException(msg);
        }

        for (int i=0;i<destinations.length;i++) {
            String[] valuePair=destinations[i];
            String   lowlevelname=valuePair[0];

            if (index != null && indexedConstraints.contains(paramName)) {
                lowlevelname = insertIndex(lowlevelname, index);
            }
            
            setLowlevelParam(lowlevelname,value);
        }
    }
    
    /**
     * Setzt den Offset-Parameter mit dem aktuellen Loop-Count.
     * Der Counter wird jedesmal in fillJobResult erhoeht - also mit jedem neuen Ergebnis.
     */
    public void applyOffset()
    {
        final String offset = this.getContinueOffset();
        this.setLowlevelParam(this.getName() + ".offset",(offset != null) ? offset : "");
    }
    
    /**
     * Liefert true, wenn fuer den Auftrag ein HKTAN erzeugt wurde.
     * @return true, wenn fuer den Auftrag ein HKTAN erzeugt wurde.
     */
    public boolean haveTan()
    {
        return this.haveTan;
    }
    
    /**
     * Vermerkt den Auftrag als "HKTAN erzeugt".
     */
    public void tanApplied()
    {
        this.haveTan = true;
    }
    
    /**
     * Liefert true, wenn fuer den Auftrag die VoP-Prüfung erzeugt wurde.
     * @return true, wenn fuer den Auftrag ein VoP-Prüfung erzeugt wurde.
     */
    public boolean haveVoP()
    {
        return this.haveVop;
    }
    
    /**
     * Vermerkt den Auftrag als "VoP erzeugt".
     */
    public void vopApplied()
    {
        this.haveVop = true;
    }

    /**
     * Markiert den Auftrag als zu ueberspringend.
     */
    public void skip()
    {
        this.skip = true;
        this.useResult = false;
    }
    
    /**
     * Speichert, ob das Ergebnis interpretiert werden soll.
     * @param useResult true, wenn es interpretiert werden soll.
     */
    public void setUseResult(boolean useResult)
    {
      this.useResult = useResult;
    }
    
    /**
     * Liefert true, wenn das Ergebnis interpretiert werden soll.
     * @return true, wenn das Ergebnis interpretiert werden soll.
     */
    public boolean useResult()
    {
      return useResult;
    }
    
    /**
     * Prueft, ob der Auftrag uebersprungen werden soll.
     * @return true, wenn der Auftrag uebersprungen werden soll.
     */
    public boolean skipped()
    {
        return this.skip;
    }
    
    protected void setLowlevelParam(String key,String value)
    {
        HBCIUtils.log("setting lowlevel parameter "+key+" = "+value,HBCIUtils.LOG_DEBUG);
        llParams.setProperty(key,value);
    }

    public Properties getLowlevelParams()
    {
        return llParams;
    }
    
    public String getLowlevelParam(String key)
    {
        return getLowlevelParams().getProperty(key);
    }

    public void setIdx(int idx)
    {
        this.idx=idx;
    }

    public String getName()
    {
        return name;
    }
    
    public String getSegVersion()
    {
        return this.segVersion;
    }

    /**
     * Liefert den ggf erneut auszufuehrenden Job.
     * Die Default-Implementierung liefert "this", wenn die Bank ein 3040 zurueckgemeldet hat.
     * Das kann aber auch ein anderer sein, als "this". Naemlich bei HKTAN in Prozess-Variante #2. Dort liefert es stattdessen den eigentlichen GV.
     * @return den ggf erneut auszufuehrenden Job.
     */
    public HBCIJobImpl redo()
    {
        if (!this.redoAllowed())
            return null;
        
        return (this.getContinueOffset() != null) ? this : null;
    }
    
    /**
     * Wir erlauben per Default erstmal kein Redo bei einem 3040-Code. Es sei denn, im Job ist explizit uebeschrieben.
     * Siehe https://homebanking-hilfe.de/forum/topic.php?p=150614#real150614
     * @return true, wenn redo erlaubt ist.
     */
    protected boolean redoAllowed()
    {
        return false;
    }
    
    /**
     * Gibt (sofern vorhanden) den Wiederaufsetzpunkt des letzten HBCI-Rückgabecodes 3040 zurück.
     * @return der Offset-Wert oder NULL.
     */
    private String getContinueOffset()
    {
        HBCIRetVal ret = this.getW3040(this.loopCount);
        return ret != null ? ret.params[0] : null;
    }
    
    /**
     * Liefert den Rueckgabecode 3040 (fuer "Weitere Daten folgen"), insofern vorhanden und mit einem Parameter versehen.
     * @param loop die Nummer des Durchlaufs, beginnend bei 0.
     * @return der Rueckgabewert, insofern vorhanden. Sonst NULL.
     */
    private HBCIRetVal getW3040(int loop)
    {
        final int num = jobResult.getRetNumber();

        for (int i=0;i<num;i++)
        {
            HBCIRetVal retval = jobResult.getRetVal(i);
            String[] p = retval.params;
            if (KnownReturncode.W3040.is(retval.code) && p != null && p.length > 0 && p[0] != null && p[0].length() > 0 && (--loop) == 0)
                return retval;
        }
        
        return null;
    }

    /* füllt das Objekt mit den Rückgabedaten, wenn der GV durch einen TAN Task
    gewrapped wurde und die GV-spezifischen Daten daraus übernommen werden müssen.
    Siehe dazu auch HBCIJobImpl::fillJobResult() 
    */
    public void fillJobResultFromTanJob(HBCIMsgStatus status,String header,int seg)
    {
        Properties result = status.getData();
        saveBasicValues(result, seg);
        saveReturnValues(status, seg);

        // wichtig um Parameter wie "content" zu füllen
        extractPlaintextResults(status, header, contentCounter);
        // der contentCounter wird fuer jedes antwortsegment um 1 erhoeht
        extractResults(status, header, contentCounter++);
    }
    
    /* füllt das Objekt mit den Rückgabedaten. Dazu wird zuerst eine Liste aller
       Segmente erstellt, die Rückgabedaten für diesen Task enthalten. Anschließend
       werden die HBCI-Rückgabewerte (RetSegs) im outStore gespeichert. Danach werden
       die GV-spezifischen Daten im outStore abgelegt */
    public void fillJobResult(HBCIMsgStatus status,int offset)
    {
        try {
            this.haveTan = false;
            this.skip = false;
            this.loopCount++;
            Properties result=status.getData();

            // nachsehen, welche antwortsegmente ueberhaupt
            // zu diesem task gehoeren
            
            // res-num --> segmentheader (wird für sortierung der 
            // antwort-segmente benötigt)
            Hashtable<Integer,String> keyHeaders=new Hashtable<Integer, String>();
            for (Enumeration i=result.keys();i.hasMoreElements();) {
                String key=(String)(i.nextElement());
                if (key.startsWith("GVRes")&&
                    key.endsWith(".SegHead.ref")) {
                    
                    String segref=result.getProperty(key);
                    if ((Integer.parseInt(segref))-offset==idx) {
                        // nummer des antwortsegments ermitteln
                        int resnum=0;
                        if (key.startsWith("GVRes_")) {
                            resnum=Integer.parseInt(key.substring(key.indexOf('_')+1,key.indexOf('.')));
                        }
                        
                        keyHeaders.put(
                            Integer.valueOf(resnum),
                            key.substring(0,key.length()-(".SegHead.ref").length()));
                    }
                }
            }
            
            saveBasicValues(result,idx+offset);
            saveReturnValues(status,idx+offset);
            
            // segment-header-namen der antwortsegmente in der reihenfolge des
            // eintreffens sortieren
            Object[] resnums=keyHeaders.keySet().toArray(new Object[0]);
            Arrays.sort(resnums);

            // alle antwortsegmente durchlaufen
            for (int i=0;i<resnums.length;i++) {
                // dabei reihenfolge des eintreffens beachten
                String header=keyHeaders.get(resnums[i]);
                
                extractPlaintextResults(status,header,contentCounter);
                extractResults(status,header,contentCounter++);
                // der contentCounter wird fuer jedes antwortsegment um 1 erhoeht
            }
        } catch (Exception e) {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_CANTSTORERES",getName());
            if (!HBCIUtilsInternal.ignoreError(getMainPassport(),
                                       "client.errors.ignoreJobResultStoreErrors",
                                       msg+": "+HBCIUtils.exception2String(e))) {
                throw new HBCI_Exception(msg,e);
            }
        }
    }
    
    /* wenn wenigstens ein HBCI-Rückgabewert für den aktuellen GV gefunden wurde,
       so werden im outStore zusätzlich die entsprechenden Dialog-Parameter
       gespeichert (Property @c basic.*) */
    private void saveBasicValues(Properties result,int ref)
    {
        // wenn noch keine basic-daten gespeichert sind
        if (jobResult.getDialogId()==null) {
            // Pfad des originalen MsgHead-Segmentes holen und um "orig_" ergaenzen,
            // um den Key fuer die entsprechenden Daten in das result-Property zu erhalten
            String msgheadName="orig_"+result.getProperty("1");
            
            jobResult.storeResult("basic.dialogid",result.getProperty(msgheadName+".dialogid"));
            jobResult.storeResult("basic.msgnum",result.getProperty(msgheadName+".msgnum"));
            jobResult.storeResult("basic.segnum",Integer.toString(ref));

            HBCIUtils.log("basic values for " + getName() + " set to "
                    + jobResult.getDialogId() + "/" 
                    + jobResult.getMsgNum()
                    + "/" + jobResult.getSegNum(), 
                    HBCIUtils.LOG_DEBUG);
        }
    }

    /*
     * speichert die HBCI-Rückgabewerte für diesen GV im outStore ab. Dazu
     * werden alle RetSegs durchgesehen; diejenigen, die den aktuellen GV
     * betreffen, werden im @c data Property unter dem namen @c ret_i.*
     * gespeichert. @i entspricht dabei dem @c retValCounter.
     */
    protected void saveReturnValues(HBCIMsgStatus status,int sref)
    {
        HBCIRetVal[] retVals=status.segStatus.getRetVals();
        String       segref=Integer.toString(sref);
        
        for (int i=0;i<retVals.length;i++) {
            HBCIRetVal rv=retVals[i];
            
            if (rv.segref!=null && rv.segref.equals(segref)) {
                jobResult.jobStatus.addRetVal(rv);
            }
        }
        
        /* bei Jobs, die mehrere Nachrichten benötigt haben, bewirkt das, dass nur
         * der globStatus der *letzten* ausgeführten Nachricht gespeichert wird.
         * Das ist aber auch ok, weil nach einem Fehler keine weiteren Nachrichten
         * ausgeführt werden, so dass im Fehlerfall der fehlerhafte globStatus zur
         * Verfügung steht. Im OK-Fall werden höchstens die OK-Meldungen der vorherigen
         * Nachrichten überschrieben. */
        jobResult.globStatus=status.globStatus;
    }

    /* diese Methode wird i.d.R. durch abgeleitete GV-Klassen überschrieben, um die
       Rückgabedaten in einem passenden Format abzuspeichern. Diese default-Implementation
       tut nichts */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
    }

    private void extractPlaintextResults(HBCIMsgStatus status,String header,int idx)
    {
        Properties result=status.getData();
        for (Enumeration e=result.keys();e.hasMoreElements();) {
            String key=(String)(e.nextElement());
            if (key.startsWith(header+".")) {
                jobResult.storeResult(HBCIUtilsInternal.withCounter("content",idx)+
                                      "."+
                                      key.substring(header.length()+1),result.getProperty(key));
            }
        }
    }

    public T getJobResult()
    {
        return jobResult;
    }
    
    public HBCIPassportInternal getMainPassport()
    {
        return passports.getMainPassport();
    }
    
    private void _checkAccountCRC(String frontendname,
    							  String blz,String number)
    {
        // pruefsummenberechnung nur wenn blz/kontonummer angegeben sind
        if (blz==null || number==null) {
        	return;
        }
        if (blz.length()==0 || number.length()==0) {
        	return;
        }
        
        // daten merken, die im urspruenglich verwendet wurden (um spaeter
        // zu wissen, ob sie korrigiert wurden)
        String orig_blz=blz;
        String orig_number=number;

        while (true) {
        	// daten validieren
            boolean crcok=HBCIUtils.checkAccountCRC(blz,number);

            // aktuelle daten merken
            String old_blz=blz;
            String old_number=number;

            if (!crcok) {
            	// wenn beim validieren ein fehler auftrat, nach neuen daten fragen
                StringBuffer sb=new StringBuffer(blz).append("|").append(number);
                HBCIUtilsInternal.getCallback().callback(getMainPassport(),
                                                 HBCICallback.HAVE_CRC_ERROR,
                                                 HBCIUtilsInternal.getLocMsg("CALLB_HAVE_CRC_ERROR"),
                                                 HBCICallback.TYPE_TEXT,
                                                 sb);

                int idx=sb.indexOf("|");
                blz=sb.substring(0,idx);
                number=sb.substring(idx+1);
            }
            
            if (blz.equals(old_blz) && number.equals(old_number)) {
            	// blz und kontonummer auch nach rueckfrage unveraendert, 
            	// also tatsaechlich mit diesen daten weiterarbeiten
                break;
            }
        }
            
        if (!blz.equals(orig_blz)) {
            setParam(frontendname+".KIK.blz",blz);
        } 
        if (!number.equals(orig_number)) {
            setParam(frontendname+".number",number);
        }
    }
    
    private void _checkIBANCRC(String frontendname,String iban)
    {
    	// pruefsummenberechnung nur wenn iban vorhanden ist
    	if (iban==null || iban.length()==0) {
    		return;
    	}

        // daten merken, die im urspruenglich verwendet wurden (um spaeter
        // zu wissen, ob sie korrigiert wurden)
    	String orig_iban=iban;
    	while (true) {
    		boolean crcok=HBCIUtils.checkIBANCRC(iban);

    		String old_iban=iban;

    		if (!crcok) {
    			StringBuffer sb=new StringBuffer(iban);
    			HBCIUtilsInternal.getCallback().callback(getMainPassport(),
    					HBCICallback.HAVE_IBAN_ERROR,
    					HBCIUtilsInternal.getLocMsg("CALLB_HAVE_IBAN_ERROR"),
    					HBCICallback.TYPE_TEXT,
    					sb);

    			iban=sb.toString();
    		}

    		if (iban.equals(old_iban)) {
    			// iban unveraendert 
    			break;
    		}
    	}

    	if (!iban.equals(orig_iban)) {
    		setParam(frontendname+".iban",iban);
    	}
    }

    protected void checkAccountCRC(String frontendname)
    {
        String[][] data=constraints.get(frontendname+".blz");
        if (data!=null && data.length!=0) {
        	// wenn es tatsaechlich einen frontendparamter der form acc.blz gibt,
        	// brauchen wir zunaechst den "basis-namen" ("acc")
            String paramname=data[0][0];
            String lowlevelHeader=paramname.substring(0,paramname.lastIndexOf(".KIK.blz"));

            // basierend auf dem basis-namen blz/number holen
            String blz=llParams.getProperty(lowlevelHeader+".KIK.blz");
            String number=llParams.getProperty(lowlevelHeader+".number");
            // blz/number ueberpruefen
            _checkAccountCRC(frontendname, blz,number);
        }
        
        // analoges fuer die IBAN
        String[][] data2=constraints.get(frontendname+".iban");
        if (data2!=null && data2.length!=0) {
            String paramname=data2[0][0];
            String lowlevelHeader=paramname.substring(0,paramname.lastIndexOf(".iban"));

            String iban=llParams.getProperty(lowlevelHeader+".iban");
            _checkIBANCRC(frontendname, iban);
        }
    }
    
    public void addSignaturePassport(HBCIPassport passport,String role)
    {
        HBCIUtils.log("adding additional passport to job "+getName(),
                HBCIUtils.LOG_DEBUG);
        passports.addPassport((HBCIPassportInternal)passport,role);
    }
    
    public HBCIPassportList getSignaturePassports()
    {
        return passports;
    }
    
    // die default-implementierung holt einfach aus den job-parametern
    // den genannten wert. eine bestimmte GV-klasse kann das überschreiben,
    // um "besondere Werte" (z.B. sumValues) irgendwie anders zu errechnen
    public String getChallengeParam(String path) 
    {
        String result;
        if (path.equals("SegHead.code")) {
            /* this special value is required for HHD1.3, where the segcode
             * can be part of the challenge parameters, but the segcode is
             * not a "lowlevel param", so have to handle this manually */
            result=getHBCICode();   
        } else {
            // normal lowlevel param
            String valuePath=this.getName()+"."+path;
            result=this.getLowlevelParam(valuePath);
        }
        return result;
    }
    
    /**
     * Liefert das Auftraggeber-Konto, wie es ab HKTAN5 erforderlich ist.
     * @return das Auftraggeber-Konto oder NULL, wenn keines angegeben ist.
     */
    public Konto getOrderAccount()
    {
      // Checken, ob wir das Konto unter "My.[number/iban]" haben
      String prefix = this.getName() + ".My.";
      String number = this.getLowlevelParam(prefix + "number");
      String iban   = this.getLowlevelParam(prefix + "iban");
      if ((number == null || number.length() == 0) && (iban == null || iban.length() == 0))
      {
        // OK, vielleicht unter "KTV.[number/iban]"?
        prefix = this.getName() + ".KTV.";
        number = this.getLowlevelParam(prefix + "number");
        iban   = this.getLowlevelParam(prefix + "iban");

        if ((number == null || number.length() == 0) && (iban == null || iban.length() == 0))
          return null; // definitiv kein Konto vorhanden
      }
      Konto k = new Konto();
      k.number    = number;
      k.iban      = iban;
      k.bic       = this.getLowlevelParam(prefix + "bic");
      k.subnumber = this.getLowlevelParam(prefix + "subnumber");
      k.blz       = this.getLowlevelParam(prefix + "KIK.blz");
      k.country   = this.getLowlevelParam(prefix + "KIK.country");
      return k;
    }
    
    public HBCIHandler getParentHandler()
    {
        return this.parentHandler;
    }
    
    public void addToQueue(String customerId)
    {
    	getParentHandler().addJobToDialog(customerId,this);
    }
    
    public void addToQueue()
    {
    	addToQueue(null);
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJob#getExternalId()
     */
    @Override
    public String getExternalId()
    {
        return this.externalId;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJob#setExternalId(java.lang.String)
     */
    @Override
    public void setExternalId(String id)
    {
        this.externalId = id;
    }
    
    protected boolean twoDigitValueInList(String value, String list)
    {
        boolean found=false;
        int     len=list.length();
        
        if ((len&1)!=0) {
            throw new InvalidArgumentException("list must have 2*n digits");
        }
        if (value.length()!=2) {
            throw new InvalidArgumentException("value must have 2 digits");
        }
        
        for (int i=0; i<len; i+=2) {
            String x=list.substring(i,i+2);
            if (value.equals(x)) {
                found=true;
                break;
            }
        }
        
        return found;
    }
    
    private static final Pattern INDEX_PATTERN = Pattern.compile("(\\w+\\.\\w+\\.\\w+)(\\.\\w+)?");
    private String insertIndex(String key, Integer index)
    {
        if (index != null) {
            Matcher m = INDEX_PATTERN.matcher(key);
            if (m.matches()) {
                return m.group(1) + '[' + index + ']' + (m.group(2) != null ? m.group(2) : "");
            }
        }
        return key;
    }
    
    /**
     * Durchsucht das BPD-Segment "HISPAS" nach dem Property "cannationalacc"
     * um herauszufinden, ob beim Versand eines SEPA-Auftrages die nationale Bankverbindung
     * angegeben sein darf.
     * 
     * Siehe FinTS_3.0_Messages_Geschaeftsvorfaelle_2013-05-28_final_version.pdf - Kapitel B.3.2
     * 
     * @param handler
     * @return true, wenn der BPD-Parameter von der Bank mit "J" befuellt ist und die
     * nationale Bankverbindung angegeben sein darf.
     */
    protected boolean canNationalAcc(HBCIHandler handler)
    {
        // Checken, ob das Flag im Passport durch die Anwendung hart codiert ist.
        // Dort kann die Entscheidung ueberschrieben werden, ob die nationale Kontoverbindung
        // mitgeschickt wird oder nicht.
        // Das wird voraussichtlich u.a. fuer die Postbank benoetigt, weil die in HISPAS
        // zwar mitteilt, dass die nationale Kontoverbindung NICHT angegeben werden soll.
        // Beim anschliessenden Einreichen einer SEPA-Ueberweisung beschwert sie sich aber,
        // wenn man sie nicht mitgesendet hat. Die verbieten also erst das Senden der
        // nationalen Kontoverbindung, verlangen sie anschliessend aber. Ein Fehler der
        // Bank. Siehe http://www.onlinebanking-forum.de/forum/topic.php?p=86444#real86444
        HBCIPassport passport = handler.getPassport();
        if (passport instanceof HBCIPassportInternal)
        {
            HBCIPassportInternal pi = (HBCIPassportInternal) passport;
            Object o = pi.getPersistentData("cannationalacc");
            if (o != null)
            {
                String s = o.toString();
                HBCIUtils.log("value of \"cannationalacc\" overwritten in passport, value: " + s,HBCIUtils.LOG_DEBUG);
                return s.equalsIgnoreCase("J");
            }
        }
        
        HBCIUtils.log("searching for value of \"cannationalacc\" in HISPAS",HBCIUtils.LOG_DEBUG);
        
        // Ansonsten suchen wir in HISPAS - aber nur, wenn wir die Daten schon haben
        if (handler.getSupportedLowlevelJobs().getProperty("SEPAInfo") == null)
        {
            HBCIUtils.log("no HISPAS data found",HBCIUtils.LOG_DEBUG);
            return false; // Ne, noch nicht. Dann lassen wir das erstmal weg
        }
        
        
        // SEPAInfo laden und darüber iterieren
        Properties props = handler.getLowlevelJobRestrictions("SEPAInfo");
        String value = props.getProperty("cannationalacc");
        HBCIUtils.log("cannationalacc=" + value,HBCIUtils.LOG_DEBUG);
        return value != null && value.equalsIgnoreCase("J");
    }

}
