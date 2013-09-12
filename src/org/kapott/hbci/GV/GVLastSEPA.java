/** 
 * Geschäftsvorfall SEPA Basislastschrift. Diese ist in pain.008.003.02.xsd spezifiziert.
 * @author Jan Thielemann
 */

package org.kapott.hbci.GV;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kapott.hbci.GV.generators.ISEPAGenerator;
import org.kapott.hbci.GV.generators.SEPAGeneratorFactory;
import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.xml.XMLCreator2;
import org.kapott.hbci.xml.XMLData;

public class GVLastSEPA extends HBCIJobImpl {
	private Properties sepaParams;

	// Pain Version die genutzt werden soll. Größter gemeinsamer Nenner von Bank
	// und HBCI4Java unterstützter Version
	// Sicherheitshalber mit default Value initialisieren, wird später
	// überschrieben
	private String painToUse = "pain.008.001.01";

	public static String getLowlevelName() {
		return "LastSEPA";
	}

	public GVLastSEPA(HBCIHandler handler, String name) {
		super(handler, name, new HBCIJobResultImpl());
		this.sepaParams = new Properties();
	}

	public GVLastSEPA(HBCIHandler handler) {
		this(handler, getLowlevelName());

		// Prüfen welche Pain Version die Bank unterstzützt und diese mit den
		// von HBCI4Java unterstützten Pains vergleichen
		checkSupportedPainVersion(handler);

		//My bzw. src ist das Konto des Ausführenden. Dst ist das Konto des Belasteten.		
		addConstraint("src.bic", "My.bic", null, LogFilter.FILTER_MOST);
		addConstraint("src.iban", "My.iban", null, LogFilter.FILTER_IDS);
		addConstraint("_sepadescriptor", "sepadescr", "sepade."+ painToUse+".xsd", LogFilter.FILTER_NONE);
		addConstraint("_sepapain", "sepapain", null, LogFilter.FILTER_IDS);

		addConstraint("src.bic", "sepa.src.bic", null, LogFilter.FILTER_MOST);
		addConstraint("src.iban", "sepa.src.iban", null, LogFilter.FILTER_IDS);
		addConstraint("src.name", "sepa.src.name", null, LogFilter.FILTER_IDS);
		addConstraint("dst.bic", "sepa.dst.bic", null, LogFilter.FILTER_MOST);
		addConstraint("dst.iban", "sepa.dst.iban", null, LogFilter.FILTER_IDS);
		addConstraint("dst.name", "sepa.dst.name", null, LogFilter.FILTER_IDS);
		addConstraint("btg.value", "sepa.btg.value", null, LogFilter.FILTER_NONE);
		addConstraint("btg.curr", "sepa.btg.curr", "EUR", LogFilter.FILTER_NONE);
		addConstraint("usage", "sepa.usage", null, LogFilter.FILTER_NONE);
		
		addConstraint("sepaid", "sepa.sepaid", getSEPAMessageId(), LogFilter.FILTER_NONE);
		addConstraint("endtoendid", "sepa.endtoendid", null, LogFilter.FILTER_NONE);
		addConstraint("mandateid", "sepa.mandateid", null, LogFilter.FILTER_NONE);
		addConstraint("manddateofsig", "sepa.manddateofsig", null, LogFilter.FILTER_NONE);
		addConstraint("amendmandindic", "sepa.amendmandindic", Boolean.toString(false), LogFilter.FILTER_NONE);
	}

	/**
	 * Diese Methode schaut in den BPD nach den unterstzützen pain Versionen
	 * (bei LastSEPA pain.008.xxx.xx) und vergleicht diese mit den von HBCI4Java
	 * unterstützen pain Versionen. Der größte gemeinsamme Nenner wird
	 * schließlich in this.painToUse gespeichert.
	 * 
	 * @param handler
	 */
	private void checkSupportedPainVersion(HBCIHandler handler) {
		// Erst prüfen ob die SEPAInfo überhaupt vorhanden ist
		if (handler.getSupportedLowlevelJobs().getProperty("SEPAInfo") != null) {
			// Regex für die pain Version
			Pattern pattern = Pattern
					.compile("pain\\.008\\.(\\d\\d\\d\\.\\d\\d)");

			// Liste zum speichern aller gefundenen pain Versionen
			ArrayList<String[]> validPains = new ArrayList<String[]>();

			// SEPAInfo laden und darüber iterieren
			Properties props = handler.getLowlevelJobRestrictions("SEPAInfo");
			Enumeration<?> e = props.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String val = props.getProperty(key);

				// pain Version suchen
				Matcher m = pattern.matcher(val);
				while (m.find()) {
					// Prüfen ob die gefundene pain Version von HBCI4Java
					// unterstützt wird. Dazu einfach prüfen ob das pain Schema
					// vorhanden ist
					String rawpain = m.group(1);
					URL u = GVUebSEPA.class.getClassLoader().getResource(
							"pain.008." + rawpain + ".xsd");
					if (u != null) {
						validPains.add(rawpain.split("\\."));
					}
				}
			}

			int maxMajorVersion = 0;
			int maxMinorVersion = 0;
			for (String[] pain : validPains) {
				int maj = Integer.parseInt(pain[0]);
				maxMajorVersion = maj > maxMajorVersion ? maj : maxMajorVersion;
			}
			for (String[] pain : validPains) {
				int maj = Integer.parseInt(pain[0]);
				int min = Integer.parseInt(pain[1]);
				if (maj == maxMajorVersion) {
					maxMinorVersion = min > maxMinorVersion ? min
							: maxMinorVersion;
				}
			}
			for (String[] pain : validPains) {
				int maj = Integer.parseInt(pain[0]);
				int min = Integer.parseInt(pain[1]);
				if (maxMajorVersion == maj && maxMinorVersion == min)
					painToUse = "pain.008." + pain[0] + "." + pain[1];
			}
		}
	}

	/*
	 * This is needed to "redirect" the sepa values. They dont have to stored
	 * directly in the message, but have to go into the SEPA document which will
	 * by created later (in verifyConstraints())
	 */
	protected void setLowlevelParam(String key, String value) {
		String intern = getName() + ".sepa.";

		if (key.startsWith(intern)) {
			String realKey = key.substring(intern.length());
			this.sepaParams.setProperty(realKey, value);
			HBCIUtils.log("setting SEPA param " + realKey + " = " + value,
					HBCIUtils.LOG_DEBUG);
		} else {
			super.setLowlevelParam(key, value);
		}
	}

	/*
	 * This is needed for verifyConstraints(). Because verifyConstraints() tries
	 * to read the lowlevel-values for each constraint, the lowlevel-values for
	 * sepa.xxx would always be empty (because they do not exist in hbci
	 * messages). So we read the sepa lowlevel-values from the special sepa
	 * structure instead from the lowlevel params for the message
	 */
	public String getLowlevelParam(String key) {
		String result;

		String intern = getName() + ".sepa.";
		if (key.startsWith(intern)) {
			String realKey = key.substring(intern.length());
			result = getSEPAParam(realKey);
		} else {
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
	public String getSEPAMessageId() {
		String result = getSEPAParam("messageId");
		if (result == null) {
			Date now = new Date();
			result = now.getTime() + "-" + getMainPassport().getUserId();
			result = result.substring(0, Math.min(result.length(), 35));
			setSEPAParam("messageId", result);
		}
		return result;
	}

	/**
	 * Erstellt einen Timestamp im ISODateTime Forma.
	 * 
	 * @discuss Diese methode wäre bestimmt auch gut in der SEPAGeneratorFactory
	 *          oder den einzelnen Generator Klassen nützlich
	 * @return Aktuelles Datum als ISODateTime String
	 */
	public String createSEPATimestamp() {
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return format.format(now);
	}

	/**
	 * Erstellt die XML für diesen Job und schreibt diese in den _sepapain Parameter des Jobs
	 */
	protected void createSEPAFromParams() {

		//Hier wird die XML rein geschrieben
		ByteArrayOutputStream o = new ByteArrayOutputStream();

		//Passenden SEPA Generator zur verwendeten pain Version laden
		ISEPAGenerator gen = SEPAGeneratorFactory.get(this, painToUse);
		
		//Die XML in den baos schreiben, ggf fehler behandeln
		try {
			gen.generate(this, o);
		} catch (Exception e) {
			throw new HBCI_Exception(
					"*** the _sepapain segment for this job can not be created",
					e);
		}

		//Prüfen ob die XML erfolgreich generiert wurde
		if (o.size() == 0)
			throw new HBCI_Exception(
					"*** the _sepapain segment for this job can not be created");

//		//Testausgabe der XML
//		try {
//			System.out.println("-------------" + o.toString("ISO-8859-1"));
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		// store SEPA document as parameter
		try {
			setParam("_sepapain", "B" + o.toString("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Bei SEPA Geschäftsvorfällen müssen wir verifyConstraints überschreiben um die SEPA XML zu generieren
	 */
	public void verifyConstraints() {
		// creating SEPA document and storing it in _sepapain
		createSEPAFromParams();

		// verify all constraints
		super.verifyConstraints();

		// TODO: checkIBANCRC
	}

	protected void setSEPAParam(String name, String value) {
		this.sepaParams.setProperty(name, value);
	}

	/**
	 * Liest den Parameter zu einem gegeben Key aus dem speziellen SEPA
	 * Parametern aus
	 * 
	 * @param Key
	 * @return Value
	 */
	public String getSEPAParam(String name) {
		return this.sepaParams.getProperty(name);
	}
}