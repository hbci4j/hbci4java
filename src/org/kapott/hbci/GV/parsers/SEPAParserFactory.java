package org.kapott.hbci.GV.parsers;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.sepa.PainVersion;

/**
 * Factory zum Erzeugen von Parsern fuer das Einlesen von SEPA-XML-Daten.
 */
public class SEPAParserFactory
{
    /**
     * Gibt den passenden SEPA Parser für die angegebene PAIN-Version.
     * @param version die PAIN-Version.
     * @return ISEPAParser
     */
    public static ISEPAParser get(PainVersion version)
    {
        ISEPAParser parser = null;
        
        String className = version.getParserClass();
        try
        {
            HBCIUtils.log("trying to init SEPA parser: " + className,HBCIUtils.LOG_DEBUG);
            Class cl = Class.forName(className);
            parser = (ISEPAParser) cl.newInstance();
        }
        catch (Exception e)
        {
            String msg = "Error creating SEPA parser";
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreCreateJobErrors",msg))
                throw new HBCI_Exception(msg,e);
        }
        return parser;
    }
}
