package org.kapott.hbci.GV.generators;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.sepa.PainVersion;
import org.kapott.hbci.structures.Value;

/**
 * Abstrakte Basis-Implementierung der SEPA-Generatoren.
 * 
 * WICHTIG: Diese Klasse sowie die Ableitungen sollten auch ohne initialisiertes HBCI-System
 * funktionieren, um das XML ohne HBCI-Handler erstellen zu koennen. Daher sollte auf die
 * Verwendung von "HBCIUtils" & Co verzichtet werden. Das ist auch der Grund, warum hier
 * das Java-Logging verwendet wird und nicht das HBCI4Java-eigene.
 */
public abstract class AbstractSEPAGenerator implements ISEPAGenerator
{
    private final static Logger LOG = Logger.getLogger(AbstractSEPAGenerator.class.getName());
    private final static Pattern INDEX_PATTERN = Pattern.compile("\\w+\\[(\\d+)\\](\\..*)?");

    /**
     * Schreibt die Bean mittels JAXB in den Strean.
     * @param e das zu schreibende JAXBElement mit der Bean.
     * @param type der Typ der Bean.
     * @param os der OutputStream, in den das XML geschrieben wird.
     * @param validate true, wenn das erzeugte XML gegen das PAIN-Schema validiert werden soll.
     * @throws Exception
     */
    protected void marshal(JAXBElement e, OutputStream os, boolean validate) throws Exception
    {
        JAXBContext jaxbContext = JAXBContext.newInstance(e.getDeclaredType());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        // Siehe https://groups.google.com/d/msg/hbci4java/RYHCai_TzHM/72Bx51B9bXUJ
        if (System.getProperty("sepa.pain.formatted","false").equalsIgnoreCase("true"))
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        PainVersion version = this.getPainVersion();
        if (version != null)
        {
            String schemaLocation = version.getSchemaLocation();
            if (schemaLocation != null)
            {
                LOG.fine("appending schemaLocation " + schemaLocation);
                marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,schemaLocation);
            }

            String file = version.getFile();
            if (file != null)
            {
                if (validate)
                {
                    Source source  = null;
                    InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);

                    if (is != null)
                    {
                        source = new StreamSource(is);
                    }
                    else
                    {
                        // Fallback auf File-Objekt
                        File f = new File(file);
                        if (f.isFile() && f.canRead())
                            source = new StreamSource(f);
                    }

                    if (source == null)
                        throw new HBCI_Exception("schema validation activated against " + file + " - but schema file could not be found");

                    LOG.fine("activating schema validation against " + file);
                    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema schema = schemaFactory.newSchema(source);
                    marshaller.setSchema(schema);
                }
            }
        }

        marshaller.marshal(e, os);
    }

    /**
     * @see org.kapott.hbci.GV.generators.ISEPAGenerator#getPainVersion()
     */
    @Override
    public PainVersion getPainVersion()
    {
        return null;
    }

    /**
     * Ermittelt den maximalen Index aller indizierten Properties. Nicht indizierte Properties
     * werden ignoriert.
     * 
     * @param properties die Properties, mit denen gearbeitet werden soll
     * @return Maximaler Index, oder {@code null}, wenn keine indizierten Properties gefunden wurden
     */
    protected Integer maxIndex(Properties properties)
    {
        Integer max = null;
        for (String key : properties.stringPropertyNames())
        {
            Matcher m = INDEX_PATTERN.matcher(key);
            if (m.matches())
            {
                int index = Integer.parseInt(m.group(1));
                if (max == null || index > max)
                {
                    max = index;
                }
            }
        }
        return max;
    }

    /**
     * Liefert die Summe der Beträge aller Transaktionen. Bei einer Einzeltransaktion wird der
     * Betrag zurückgeliefert. Mehrfachtransaktionen müssen die gleiche Währung verwenden, da
     * eine Summenbildung sonst nicht möglich ist.
     * 
     * @param sepaParams die Properties, mit denen gearbeitet werden soll
     * @param max Maximaler Index, oder {@code null} für Einzeltransaktionen
     * @return Summe aller Beträge
     */
    protected BigDecimal sumBtgValue(Properties sepaParams, Integer max) {
        if (max == null)
            return new BigDecimal(sepaParams.getProperty("btg.value"));

        BigDecimal sum = BigDecimal.ZERO;
        String curr = null;

        for (int index = 0; index <= max; index++)
        {
            sum = sum.add(new BigDecimal(sepaParams.getProperty(insertIndex("btg.value", index))));

            // Sicherstellen, dass alle Transaktionen die gleiche Währung verwenden
            String indexCurr = sepaParams.getProperty(insertIndex("btg.curr", index));
            if (curr != null)
            {
                if (!curr.equals(indexCurr)) {
                    throw new InvalidArgumentException("mixed currencies on multiple transactions");
                }
            }
            else
            {
                curr = indexCurr;
            }
        }
        return sum;
    }

    /**
     * FÃ¼gt einen Index in den Property-Key ein. Wurde kein Index angegeben, wird der Key
     * unverÃ¤ndert zurÃ¼ckgeliefert.
     * 
     * @param key Key, der mit einem Index ergÃ¤nzt werden soll
     * @param index Index oder {@code null}, wenn kein Index gesetzt werden soll
     * @return Key mit Index
     */
    protected String insertIndex(String key, Integer index)
    {
        if (index == null)
            return key;

        int pos = key.indexOf('.');
        if (pos >= 0)
        {
            return key.substring(0, pos) + '[' + index + ']' + key.substring(pos);
        }
        else
        {
            return key + '[' + index + ']';
        }
    }

    public Value sumBtgValueObject(Properties properties) {
        Integer maxIndex = maxIndex(properties);
        BigDecimal btg = sumBtgValue(properties, maxIndex);
        String curr = properties.getProperty(insertIndex("btg.curr", maxIndex == null ? null : 1));
        return new Value(btg.doubleValue(), curr);
    }
}
