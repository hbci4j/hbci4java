package org.kapott.hbci.GV.generators;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

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
        marshaller.marshal(e, os);
    }
    
    /**
     * @see org.kapott.hbci.GV.generators.ISEPAGenerator#getSEPADescriptor()
     */
    @Override
    public String getSEPADescriptor()
    {
        return null;
    }

}
