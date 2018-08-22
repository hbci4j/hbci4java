package org.kapott.hbci.GV.generators;



import java.io.OutputStream;

import org.kapott.hbci.sepa.SepaVersion;

/**
 * Basis-Interface fuer alle SEPA-Job-Generatoren.
 * @param <T> Die konkrete Struktur, aus der die Daten gelesen werden.
 */
public interface ISEPAGenerator<T>
{
    /**
     * Das verwendete Encoding. UTF-8.
     * Siehe Siehe http://www.onlinebanking-forum.de/forum/topic.php?p=107420#real107420
     */
    public final static String ENCODING = "UTF-8";
    
	/**
	 * Schreibt den Job als SEPA-XML in den Stream.
	 * @param source die zu generierenden Daten.
	 * Urspruenglich wurde hier direkt eine Instanz von "AbstractSEPAGV" uebergeben
	 * und dort job.getSEPAParam($targetname(ohne "sepa.") aufgerufen. Das hatte jedoch
	 * den Nachteil, dass fuer die Instanziierung eines "AbstractSEPAGV" (welche
	 * von "HBCIJobImpl" abgeleitet ist) ein HBCIHandler erforderlicher. Der
	 * erfordert jedoch einen initialisierten und geoeffneten Passport, was wiederrum
	 * bedeutet, dass das SEPA-XML nur innerhalb eines HBCI-Dialogs erzeugt werden
	 * kann. Schon allein zur besseren Testbarkeit sollte sich das XML jedoch auch
	 * ohne HBCI-Initialisierung erstellen lassen. Daher werden hier nur noch
	 * die Properties uebergeben aus denen sich der SEPA-Generator dann anhand
	 * der Parameternamen bedient.
	 * @param os der Stream.
	 * @param validate true, wenn das erzeugte XML gegen das PAIN-Schema validiert werden soll.
	 * @throws Exception
	 */
	public void generate(T source, OutputStream os, boolean validate) throws Exception; 
	
	/**
	 * Liefert die SEPA-Version des Generators.
	 * Wenn die Methode implementiert wurde und eine "vollqualifizierte"
	 * SEPA-Version zurueckliefert, in der sowohl URN als auch FILE gesetzt sind,
	 * dann wird die Schema-Location dem Marshaller mittels "Marshaller.JAXB_SCHEMA_LOCATION"
	 * uebergeben, was bewirkt, dass im Root-Element das Attribute
	 * "xsi:schemaLocation" gesetzt wird. Ausserdem kann dann die Schema-Validierung
	 * aktiviert werden. 
	 * @return die zu deklarierende Schema-Location oder NULL, falls nichts
	 * angegeben werden soll.
	 */
	public SepaVersion getSepaVersion();
	
}
