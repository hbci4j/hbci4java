package org.kapott.hbci.GV.generators;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.sepa.PainVersion;

/**
 * Abstrakte Basis-Implementierung der SEPA-Generatoren.
 */
public abstract class AbstractSEPAGenerator implements ISEPAGenerator
{
    /**
     * Schreibt die Bean mittels JAXB in den Strean.
     * @param e das zu schreibende JAXBElement mit der Bean.
     * @param type der Typ der Bean.
     * @param os
     * @throws Exception
     */
    protected void marshal(JAXBElement e, OutputStream os) throws Exception
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
                HBCIUtils.log("appending schemaLocation " + schemaLocation,HBCIUtils.LOG_DEBUG);
                marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,schemaLocation);
            }
            
            String file = version.getFile();
            if (file != null)
            {
                boolean validate = HBCIUtils.getParam("sepa.schema.validation","0").equals("1");
                if (validate)
                {
                    InputStream is = this.getClass().getResourceAsStream(file);
                    if (is != null)
                    {
                        HBCIUtils.log("activating schema validation " + schemaLocation,HBCIUtils.LOG_DEBUG);
                        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                        Schema schema = schemaFactory.newSchema(new StreamSource(is));
                        marshaller.setSchema(schema);
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
