package org.kapott.hbci.GV.generators;

import java.util.logging.Logger;

import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.sepa.PainVersion;

/**
 * Factory zum Ermitteln des passenden Pain-Generators fuer den angegebenen Job.
 * 
 * WICHTIG: Diese Klasse sowie die Ableitungen sollten auch ohne initialisiertes HBCI-System
 * funktionieren, um das XML ohne HBCI-Handler erstellen zu koennen. Daher sollte auf die
 * Verwendung von "HBCIUtils" & Co verzichtet werden. Das ist auch der Grund, warum hier
 * das Java-Logging verwendet wird und nicht das HBCI4Java-eigene.
 */
public class SEPAGeneratorFactory
{
    private final static Logger LOG = Logger.getLogger(SEPAGeneratorFactory.class.getName());
    
	/**
	 * Gibt den passenden SEPA Generator für die angegebene PAIN-Version.
	 * @param job der zu erzeugende Job.
	 * @param version die PAIN-Version.
	 * @return ISEPAGenerator
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static ISEPAGenerator get(HBCIJob job, PainVersion version) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
        String jobname = ((AbstractSEPAGV)job).getPainJobName(); // referenzierter pain-Geschäftsvorfall
        return get(jobname,version);
	}
	
    /**
     * Gibt den passenden SEPA Generator für die angegebene PAIN-Version.
     * @param jobname der Job-Name. Z.Bsp. "UebSEPA".
     * @param version die PAIN-Version.
     * @return ISEPAGenerator
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public static ISEPAGenerator get(String jobname, PainVersion version) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        if (!version.isSupported(jobname))
            throw new InvalidUserDataException("PAIN version is not supported: " + version);

        String className = version.getGeneratorClass(jobname);
        LOG.fine("trying to init SEPA creator: " + className);
        Class cl = Class.forName(className);
        return (ISEPAGenerator) cl.newInstance();
    }
	
}
