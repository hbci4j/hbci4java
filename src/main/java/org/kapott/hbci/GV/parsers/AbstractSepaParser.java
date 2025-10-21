package org.kapott.hbci.GV.parsers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.tools.IOUtils;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Unmarshaller;




/**
 * Abstrakte Basis-Klasse der SEPA PAIN-Parser.
 * @param <T> der konkrete Typ.
 */
public abstract class AbstractSepaParser<T> implements ISEPAParser<T>
{
    /**
     * Speichert den Wert in den Properties.
     * @param props die Properties.
     * @param name das Property.
     * @param value der Wert.
     */
    void put(Properties props, Names name, String value)
    {
        // BUGZILLA 1610 - "java.util.Properties" ist von Hashtable abgeleitet und unterstuetzt keine NULL-Werte
        if (value == null)
            return;
        
        props.setProperty(name.getValue(),value);
    }
    
    /**
     * Parst das XML fehlertolerant auch dann, wenn kein Namespace im XML angegeben ist.
     * @param <R> der Typ des Root-Elements.
     * @param is der InputStream.
     * @param version die erwartete SEPA-Version.
     * @param root der Typ des Root-Elements.
     * @return das Root-Element.
     */
    protected <R> R parse(InputStream is, SepaVersion version, Class<R> root)
    {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try
      {
        IOUtils.copy(is,bos);
        final XMLInputFactory xif = XMLInputFactory.newFactory();
        final XMLStreamReader xsr = new StreamReaderDelegate(xif.createXMLStreamReader(new ByteArrayInputStream(bos.toByteArray()))) {
          /**
           * @see javax.xml.stream.util.StreamReaderDelegate#getNamespaceURI()
           */
          @Override
          public String getNamespaceURI()
          {
            String uri = super.getNamespaceURI();
            if (uri == null || uri.isEmpty())
              return version.getURN();
            return uri;
          }
        };
        
        final JAXBContext ctx = JAXBContext.newInstance(root);
        final Unmarshaller u = ctx.createUnmarshaller();
        final JAXBElement jaxb = (JAXBElement) u.unmarshal(xsr);
        return (R) jaxb.getValue();
      }
      catch (Exception e)
      {
        HBCIUtils.log(e);
        return JAXB.unmarshal(new ByteArrayInputStream(bos.toByteArray()),root);
      }
    }
}
