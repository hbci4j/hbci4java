package org.kapott.hbci.GV;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.GV.generators.ISEPAGenerator;
import org.kapott.hbci.GV.generators.SEPAGeneratorFactory;
import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.SepaVersion.Type;

/** 
 * Abstrakte Basis-Klasse fuer JAXB-basierte SEPA-Jobs.
 */
public abstract class AbstractSEPAGV extends HBCIJobImpl
{
    /**
     * Token, der als End-to-End ID Platzhalter verwendet wird, wenn keine angegeben wurde.
     * In pain.001.001.02 wurde dieser Token noch explizit erwaehnt. Inzwischen nicht mehr.
     * Nach Ruecksprache mit Holger vom onlinebanking-forum.de weiss ich aber, dass VRNetworld
     * den auch verwendet und er von Banken als solcher erkannt wird.
     */
    public final static String ENDTOEND_ID_NOTPROVIDED = "NOTPROVIDED";
    
    protected final Properties sepaParams = new Properties();
    private SepaVersion pain         = null;
    private ISEPAGenerator generator = null;

    /**
     * Liefert die Default-PAIN-Version, das verwendet werden soll,
     * wenn von der Bank keine geliefert wurden.
     * @return Default-Pain-Version.
     */
    protected abstract SepaVersion getDefaultPainVersion();
    
    /**
     * Liefert den PAIN-Type.
     * @return der PAIN-Type.
     */
    protected abstract Type getPainType();
    
    /**
     * ct.
     * @param handler
     * @param name
     */
    public AbstractSEPAGV(HBCIHandler handler, String name)
    {
        super(handler, name, new HBCIJobResultImpl());
        this.pain = this.determinePainVersion(handler,name);
    }
    
    /**
     * ct.
     * @param handler
     * @param name
     * @param jobResult
     */
    public AbstractSEPAGV(HBCIHandler handler, String name, HBCIJobResultImpl jobResult)
    {
        super(handler, name, jobResult);
        this.pain = this.determinePainVersion(handler,name);
    }
    /**
     * Diese Methode schaut in den BPD nach den unterstützen pain Versionen
     * (bei LastSEPA pain.008.xxx.xx) und vergleicht diese mit den von HBCI4Java
     * unterstützen pain Versionen. Der größte gemeinsamme Nenner wird
     * zurueckgeliefert.
     * @param handler
     * @param gvName der Geschaeftsvorfall fuer den in den BPD nach dem PAIN-Versionen
     * gesucht werden soll.
     * @return die ermittelte PAIN-Version.
     */
    private SepaVersion determinePainVersion(HBCIHandler handler, String gvName)
    {
        // Schritt 1: Wir holen uns die globale maximale PAIN-Version
        SepaVersion globalVersion = this.determinePainVersionInternal(handler,GVSEPAInfo.getLowlevelName());
        
        // Schritt 2: Die des Geschaeftsvorfalls - fuer den Fall, dass die Bank
        // dort weitere Einschraenkungen hinterlegt hat
        SepaVersion jobVersion = this.determinePainVersionInternal(handler,gvName);
        
        // Wir haben gar keine PAIN-Version gefunden
        if (globalVersion == null && jobVersion == null)
        {
            SepaVersion def = this.getDefaultPainVersion();
            HBCIUtils.log("unable to determine matching pain version, using default: " + def,HBCIUtils.LOG_WARN);
            return def;
        }
        
        // Wenn wir keine GV-spezifische haben, dann nehmen wir die globale
        if (jobVersion == null)
        {
            HBCIUtils.log("have no job-specific pain version, using global pain version: " + globalVersion,HBCIUtils.LOG_DEBUG);
            return globalVersion;
        }
        
        // Ansonsten hat die vom Job Vorrang:
        HBCIUtils.log("using job-specific pain version: " + jobVersion,HBCIUtils.LOG_DEBUG);
        return jobVersion;
    }
    
    /**
     * Diese Methode schaut in den BPD nach den unterstützen pain Versionen
     * (bei LastSEPA pain.008.xxx.xx) und vergleicht diese mit den von HBCI4Java
     * unterstützen pain Versionen. Der größte gemeinsamme Nenner wird
     * zurueckgeliefert.
     * @param handler
     * @param gvName der Geschaeftsvorfall fuer den in den BPD nach dem PAIN-Versionen
     * gesucht werden soll.
     * @return die ermittelte PAIN-Version oder NULL wenn keine ermittelt werden konnte.
     */
    private SepaVersion determinePainVersionInternal(HBCIHandler handler, final String gvName)
    {
        HBCIUtils.log("searching for supported pain versions for GV " + gvName,HBCIUtils.LOG_DEBUG);
        
        if (handler.getSupportedLowlevelJobs().getProperty(gvName) == null)
        {
            HBCIUtils.log("don't have any BPD for GV " + gvName,HBCIUtils.LOG_DEBUG);
            return null;
        }

        List<SepaVersion> found = new ArrayList<SepaVersion>();
    
        // GV-Restrictions laden und darüber iterieren
        Properties props = handler.getLowlevelJobRestrictions(gvName);
        Enumeration e = props.propertyNames();
        while (e.hasMoreElements())
        {
            String key = (String) e.nextElement();
    
            // Die Keys, welche die Schema-Versionen enthalten, heissen alle "suppformats*"
            if (!key.startsWith("suppformats"))
                continue;
    
            String urn = props.getProperty(key);
            try
            {
                SepaVersion version = SepaVersion.byURN(urn);
                if (version.getType() == this.getPainType())
                {
                    if (!version.isSupported(this.getPainJobName()))
                    {
                        HBCIUtils.log("  unsupported " + version,HBCIUtils.LOG_DEBUG);
                        continue;
                    }
                    
                    // Frueher wurde hier noch geschaut, ob die PAIN-Version per
                    // PainVersion.getKnownVersions bekannt ist. In dem Fall wurde
                    // stattdessen unsere verwendet, damit beim Senden des Auftrages
                    // der korrekte URN verwendet wird. Das ist inzwischen nicht mehr
                    // noetig, da das "PainVersion.byURN" (siehe oben) ohnehin bereits
                    // macht - wenn wir die PAIN-Version kennen, nehmen wir gleich die
                    // eigene Instanz. Siehe auch
                    // TestSepaVersion#test011 bzw. http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=95160#95160
                    HBCIUtils.log("  found " + version,HBCIUtils.LOG_DEBUG);
                    found.add(version);
                }
            }
            catch (Exception ex)
            {
                HBCIUtils.log("ignoring invalid pain version " + urn,HBCIUtils.LOG_WARN);
                HBCIUtils.log(ex,HBCIUtils.LOG_DEBUG);
            }
        }
        
        return SepaVersion.findGreatest(found);
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#setLowlevelParam(java.lang.String, java.lang.String)
     * This is needed to "redirect" the sepa values. They dont have to stored
     * directly in the message, but have to go into the SEPA document which will
     * by created later (in verifyConstraints())
     */
    protected void setLowlevelParam(String key, String value)
    {
    	String intern = getName() + ".sepa.";
    
    	if (key.startsWith(intern))
    	{
    	    String realKey = key.substring(intern.length());
    	    this.sepaParams.setProperty(realKey, value);
    	    HBCIUtils.log("setting SEPA param " + realKey + " = " + value, HBCIUtils.LOG_DEBUG);
    	}
    	else
    	{
    	    super.setLowlevelParam(key, value);
    	}
    }

    /**
     * This is needed for verifyConstraints(). Because verifyConstraints() tries
     * to read the lowlevel-values for each constraint, the lowlevel-values for
     * sepa.xxx would always be empty (because they do not exist in hbci
     * messages). So we read the sepa lowlevel-values from the special sepa
     * structure instead from the lowlevel params for the message
     * @param key
     * @return the lowlevel param.
     */
    public String getLowlevelParam(String key) {
    	String result;
    
    	String intern = getName() + ".sepa.";
    	if (key.startsWith(intern)) {
    	    String realKey = key.substring(intern.length());
    	    result = getSEPAParam(realKey);
    	}
    	else
    	{
    	    result = super.getLowlevelParam(key);
    	}
    
    	return result;
    }
    
    /**
     * Gibt die SEPA Message ID als String zurück. Existiert noch keine wird sie
     * aus Datum und User ID erstellt.
     * 
     * @return SEPA Message ID
     */
    public String getSEPAMessageId()
    {
    	String result = getSEPAParam("messageId");
    	if (result == null)
    	{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSS");
    	    result = format.format(new Date());
    	    result = result.substring(0, Math.min(result.length(), 35));
    	    setSEPAParam("messageId", result);
    	}
    	return result;
    }

    /**
     * Liefert den passenden SEPA-Generator.
     * @return der SEPA-Generator.
     */
    protected final ISEPAGenerator getSEPAGenerator()
    {
        if (this.generator == null)
        {
            try
            {
                this.generator = SEPAGeneratorFactory.get(this, this.getPainVersion());
            }
            catch (Exception e)
            {
                String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_JOB_CREATE_ERR",this.getPainJobName());
                if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCreateJobErrors",msg))
                    throw new HBCI_Exception(msg,e);
            }
            
        }
        return this.generator;
    }
    
    /**
     * Liefert den zu verwendenden PAIN-Version fuer die HBCI-Nachricht.
     * @return der zu verwendende PAIN-Version fuer die HBCI-Nachricht.
     */
    protected SepaVersion getPainVersion()
    {
        return this.pain;
    }

    /**
     * Erstellt die XML für diesen Job und schreibt diese in den _sepapain
     * Parameter des Jobs
     */
    protected void createSEPAFromParams()
    {
    	// Hier wird die XML rein geschrieben
    	ByteArrayOutputStream o = new ByteArrayOutputStream();
    
    	// Passenden SEPA Generator zur verwendeten pain Version laden
    	ISEPAGenerator gen = this.getSEPAGenerator();
    
    	// Die XML in den baos schreiben, ggf fehler behandeln
    	try
    	{
            boolean validate = HBCIUtils.getParam("sepa.schema.validation","0").equals("1");
            HBCIUtils.log("schema validation enabled: " + validate,HBCIUtils.LOG_DEBUG);
    	    gen.generate(this.sepaParams, o, validate);
    	}
    	catch (HBCI_Exception he)
    	{
    	    throw he;
    	}
    	catch (Exception e)
    	{
    	    throw new HBCI_Exception("*** the _sepapain segment for this job can not be created",e);
    	}
    
    	// Prüfen ob die XML erfolgreich generiert wurde
    	if (o.size() == 0)
    	    throw new HBCI_Exception("*** the _sepapain segment for this job can not be created");
    
    	try {
            String xml = o.toString(Comm.ENCODING);
            HBCIUtils.log("generated XML:\n" + xml,HBCIUtils.LOG_DEBUG);
    	    setParam("_sepapain", "B" + xml);
    	}
    	catch (UnsupportedEncodingException e)
    	{
    	    throw new RuntimeException(e);
    	}
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#addConstraint(java.lang.String, java.lang.String, java.lang.String, int, boolean)
     * Ueberschrieben, um die Default-Werte der SEPA-Parameter vorher rauszufischen und in "this.sepaParams" zu
     * speichern. Die brauchen wir "createSEPAFromParams" beim Erstellen des XML - sie wuerden dort sonst aber
     * fehlen, weil Default-Werte eigentlich erst in "verifyConstraints" uebernommen werden.
     */
    @Override
    protected void addConstraint(String frontendName, String destinationName, String defValue, int logFilterLevel, boolean indexed)
    {
        super.addConstraint(frontendName, destinationName, defValue, logFilterLevel, indexed);
        
        if (destinationName.startsWith("sepa.") && defValue != null)
        {
            this.sepaParams.put(frontendName,defValue);
        }
    }


    /**
     * Bei SEPA Geschäftsvorfällen müssen wir verifyConstraints überschreiben um
     * die SEPA XML zu generieren
     */
    public void verifyConstraints()
    {
        // creating SEPA document and storing it in _sepapain
        if(this.acceptsParam("_sepapain")) {
            createSEPAFromParams();            
        }
        
    	super.verifyConstraints();
    }

    protected void setSEPAParam(String name, String value) {
        this.sepaParams.setProperty(name, value);
    }

    /**
     * Liest den Parameter zu einem gegeben Key aus dem speziellen SEPA
     * Parametern aus
     * 
     * @param name
     * @return Value
     */
    public String getSEPAParam(String name) {
        return this.sepaParams.getProperty(name);
    }
    
    /**
     * Referenzierter pain-Jobname. Bei vielen Geschäftsvorfällen
     * (z.B. Daueraufträgen) wird die pain der Einzeltransaktion verwendet.
     * @return Value
     */
    public String getPainJobName() {
        return this.getJobName();
    }
}
