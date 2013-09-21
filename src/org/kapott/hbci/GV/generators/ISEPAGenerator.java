package org.kapott.hbci.GV.generators;



import java.io.OutputStream;

import org.kapott.hbci.GV.AbstractSEPAGV;

/**
 * Basis-Interface fuer alle SEPA-Job-Generatoren.
 */
public interface ISEPAGenerator
{
	/**
	 * Schreibt den Job als SEPA-XML in den Stream.
	 * @param job der Job.
	 * @param os der Stream.
	 * @throws Exception
	 */
	public void generate(AbstractSEPAGV job, OutputStream os) throws Exception; 
	
	/**
	 * Liefert den zu verwendenden SEPA-Descriptor fuer die HBCI-Nachricht.
	 * @return der zu verwendende SEPA-Descriptor fuer die HBCI-Nachricht.
	 */
	public String getSEPADescriptor();
}
