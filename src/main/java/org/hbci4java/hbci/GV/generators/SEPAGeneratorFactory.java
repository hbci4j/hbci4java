package org.hbci4java.hbci.GV.generators;

import java.util.logging.Logger;

import org.hbci4java.hbci.GV.AbstractSEPAGV;
import org.hbci4java.hbci.GV.HBCIJob;
import org.hbci4java.hbci.exceptions.InvalidUserDataException;
import org.hbci4java.hbci.sepa.SepaVersion;

/**
 * Factory zum Ermitteln des passenden Pain-Generators fuer den angegebenen Job.
 * 
 * WICHTIG: Diese Klasse sowie die Ableitungen sollten auch ohne initialisiertes HBCI-System
 * funktionieren, um das XML ohne HBCI-Handler erstellen zu koennen. Daher sollte auf die
 * Verwendung von "HBCIUtils" verzichtet werden. Das ist auch der Grund, warum hier
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
	public static ISEPAGenerator get(HBCIJob job, SepaVersion version) throws ClassNotFoundException, InstantiationException, IllegalAccessException
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
    public static ISEPAGenerator get(String jobname, SepaVersion version) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        if (!version.canGenerate(jobname))
            throw new InvalidUserDataException("SEPA version is not supported: " + version);

        String className = version.getGeneratorClass(jobname);
        LOG.fine("trying to init SEPA creator: " + className);
        Class cl = Class.forName(className);
        return (ISEPAGenerator) cl.newInstance();
    }
	
}
