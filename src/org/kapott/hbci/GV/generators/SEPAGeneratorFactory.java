package org.kapott.hbci.GV.generators;

import java.lang.reflect.Constructor;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;

/**
 * Factory zum Ermitteln des passenden Pain-Generators fuer den angegebenen Job.
 */
public class SEPAGeneratorFactory
{
	/**
	 * Gibt den passenden SEPA Generator für ein gegebenes Schema. Das Schema
	 * muss dabei derzeit die Form "pain.001.001.02" oder "00100102" haben um
	 * erfoglreich geparst zu werden.
	 * @param job der zu erzeugende Job.
	 * @param schema
	 * @return ISEPAGenerator
	 */
	public static ISEPAGenerator get(HBCIJob job, String schema)
	{
		//Schmenamen parsen und entsprechenden Generator für Job/Schema Kombination laden
		String pschema = parseScheme(schema);
		ISEPAGenerator ret=null;

		String jobname   = ((HBCIJobImpl)job).getJobName(); // "getJobName()" ist ohne Versionsnummer, "getName()" ist mit Versionsnummer
		String className = "org.kapott.hbci.GV.generators.Gen" + jobname + pschema;

        try {
            HBCIUtils.log("trying to load SEPA creator class: " + className,HBCIUtils.LOG_INFO);
            Class cl = Class.forName(className);
            Constructor cons=cl.getConstructor();
            ret=(ISEPAGenerator)cons.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            throw new InvalidUserDataException("*** there is no ISEPAGenerator class named " + className +". Maybe the pain version is not supported");
        }
        catch (Exception e)
        {
            String msg=HBCIUtilsInternal.getLocMsg("EXCMSG_GENERATOR_CREATE_ERR",job.getName()); //TODO: Msg anlegen
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCreateJobErrors",msg))
            	throw new HBCI_Exception(msg,e);
        }
        return ret;
	}


	/**
	 * Parst ein Schma in einen für diese Factory brauchbaren String.
	 * @param schema
	 * @return Schema als String der Form "00100102"
	 */
	private static String parseScheme(String schema) {

		//Schema der Form 00100102
		if(schema != null && schema.length() > 0 && schema.matches("[0-9]+"))
			return schema;


		//Schema der Form pain.001.001.02
		String ret = "";
		for(String s : schema.split("\\.")){
			if(s.length() > 0 && s.matches("[0-9]+"))
				ret = ret + s;
		}
		return ret;
	}

}
