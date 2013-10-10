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

import org.kapott.hbci.sepa.PainVersion;

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
                    InputStream is = this.getClass().getResourceAsStream(file);
                    
                    if (is != null)
                    {
                        source = new StreamSource(is);
                    }
                    else
                    {
                        // Fallback auf File-Objekt
                        
                        // Der Pfad-Prafix ist eigentlich nur fuer die Unit-Tests.
                        // Im normalen Betrieb ist der nicht gesetzt. Siehe "TestPainGen".
                        String path = System.getProperty("hbci4java.pain.path",null);
                        File f = path != null ? new File(path,file) : new File(file);
                        if (f.isFile() && f.canRead())
                            source = new StreamSource(f);
                    }
                    
                    if (source != null)
                    {
                        LOG.fine("activating schema validation against " + file);
                        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                        Schema schema = schemaFactory.newSchema(source);
                        marshaller.setSchema(schema);
                    }
                    else
                    {
                        LOG.warning("schema validation activated against " + file + " - but schema file could not be found");
                    }
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
}
