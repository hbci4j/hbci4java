package org.kapott.hbci.GV.generators;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.sepa.SepaVersion;

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
    private final static Logger LOG            = Logger.getLogger(AbstractSEPAGenerator.class.getName());

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
        
        // Wir verwenden hier hart UTF-8. Siehe http://www.onlinebanking-forum.de/forum/topic.php?p=107420#real107420
        marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);

        // Siehe https://groups.google.com/d/msg/hbci4java/RYHCai_TzHM/72Bx51B9bXUJ
        if (System.getProperty("sepa.pain.formatted","false").equalsIgnoreCase("true"))
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        SepaVersion version = this.getPainVersion();
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
    public SepaVersion getPainVersion()
    {
        return null;
    }
}
