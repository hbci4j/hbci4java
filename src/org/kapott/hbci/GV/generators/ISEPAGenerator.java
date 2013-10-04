package org.kapott.hbci.GV.generators;



import java.io.OutputStream;

import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.sepa.PainVersion;

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
	 * Liefert die PAIN-Version des Generators.
	 * Wenn die Methode implementiert wurde und eine "vollqualifizierte"
	 * Pain-Version zurueckliefert, in der sowohl URN als auch FILE gesetzt sind,
	 * dann wird die Schema-Location dem Marshaller mittels "Marshaller.JAXB_SCHEMA_LOCATION"
	 * uebergeben, was bewirkt, dass im Root-Element das Attribute
	 * "xsi:schemaLocation" gesetzt wird. Ausserdem kann dann die Schema-Validierung
	 * aktiviert werden. 
	 * @return die zu deklarierende Schema-Location oder NULL, falls nichts
	 * angegeben werden soll.
	 */
	public PainVersion getPainVersion();
	
}
