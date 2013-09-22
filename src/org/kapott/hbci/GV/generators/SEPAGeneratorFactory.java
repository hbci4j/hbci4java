package org.kapott.hbci.GV.generators;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.sepa.PainVersion;

/**
 * Factory zum Ermitteln des passenden Pain-Generators fuer den angegebenen Job.
 */
public class SEPAGeneratorFactory
{
	/**
	 * Gibt den passenden SEPA Generator für die angegebene PAIN-Version.
	 * @param job der zu erzeugende Job.
	 * @param version die PAIN-Version.
	 * @return ISEPAGenerator
	 */
	public static ISEPAGenerator get(HBCIJob job, PainVersion version)
	{
		String jobname = ((HBCIJobImpl)job).getJobName(); // "getJobName()" ist ohne Versionsnummer, "getName()" ist mit Versionsnummer
		
		if (!version.isSupported(jobname))
            throw new InvalidUserDataException("PAIN version is not supported: " + version);

        ISEPAGenerator gen = null;
		String className   = version.getGeneratorClass(jobname);
        try
        {
            HBCIUtils.log("trying to init SEPA creator: " + className,HBCIUtils.LOG_DEBUG);
            Class cl = Class.forName(className);
            gen = (ISEPAGenerator) cl.newInstance();
        }
        catch (Exception e)
        {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_JOB_CREATE_ERR",jobname);
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCreateJobErrors",msg))
                throw new HBCI_Exception(msg,e);
        }
        
        return gen;
	}
}
