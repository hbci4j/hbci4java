package org.kapott.hbci.GV.generators;

import java.lang.reflect.Constructor;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtilsInternal;

public class SEPAGeneratorFactory {
	
	
	/**
	 * Gibt den passenden SEPA Generator für ein gegebenes Schema. Das Schema
	 * muss dabei derzeit die Form "pain.001.001.02" oder "00100102" haben um
	 * erfoglreich geparst zu werden.
	 * @param schema
	 * @return ISEPAGenerator
	 */
	public static ISEPAGenerator get(HBCIJob job, String schema){
		
		
		//Schmenamen parsen und entsprechenden Generator für Job/Schema Kombination laden
		String pschema = parseScheme(schema);
		ISEPAGenerator ret=null;
		
		//FIXME: Der Jobname ist entspricht dem LowLevel Namen zusammen mit einer Versionsnummer
        String      className="org.kapott.hbci.GV.generators.Gen"+job.getName()+pschema;
        String jobname  = ((HBCIJobImpl)job).getJobName();

        try {
            Class cl=Class.forName("org.kapott.hbci.GV.generators.Gen"+jobname + pschema);
            Constructor cons=cl.getConstructor();
            ret=(ISEPAGenerator)cons.newInstance();
        } catch (ClassNotFoundException e) {
            throw new InvalidUserDataException("*** there is no highlevel job named "+job.getName()+" - need class "+className);
        } catch (Exception e) {
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
