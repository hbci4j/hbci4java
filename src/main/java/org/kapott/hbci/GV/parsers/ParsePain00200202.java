package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.sepa.jaxb.pain_002_002_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_002_002_02.OriginalTransactionReferenceSEPA;
import org.kapott.hbci.sepa.jaxb.pain_002_002_02.Pain00200102;
import org.kapott.hbci.sepa.jaxb.pain_002_002_02.PaymentTransactionInformationSEPA;

import jakarta.xml.bind.JAXB;


/**
 * Parser-Implementierung fuer Pain 002.002.02.
 */
public class ParsePain00200202 extends AbstractSepaParser<List<Properties>>
{
    /**
     * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
     */
    public void parse(InputStream xml, List<Properties> sepaResults)
    {
      /*
        pain.002 parsen 
      
        Quelle: https://homebanking-hilfe.de/forum/topic.php?p=178922#real178922
      
        Zitat:
          In OrgnlPmtInfAndSts hast du wohl unter TxInfAndSts dann die verschiedenen Antworten pro Status.
          Bspw: für die Close Matches unter <TxSts>RVMC</TxSts> den neuen Namen in <StsRsnInf><AddtlInf>neuer Name</AddtlInf></StsRsnInf>
          und dahinter unter OrgnlTxRef -> Cdtr -> Pty -> Nm den alten falschen Namen und die zugehörige IBAN in CdtrACCT -> Id -> IBAN.
          
        Ausserdem in https://www.ebics.de/de/datenformate in Anlage_3_Datenformate_V3.9.pdf - Kapitel 2.2.6
      */

        Document doc = JAXB.unmarshal(xml, Document.class);
        Pain00200102 pain = doc.getPain00200102();
        
        if (pain == null)
            return;

        for (PaymentTransactionInformationSEPA pi:pain.getTxInfAndSts())
        {
          OriginalTransactionReferenceSEPA tx = pi.getOrgnlTxRef();
          final Properties prop = new Properties();
          put(prop,Names.DST_NAME, tx.getCdtr().getNm());
          put(prop,Names.DST_IBAN, tx.getCdtrAcct().getId().getIBAN());
          put(prop,Names.ENDTOENDID, pi.getOrgnlEndToEndId());
          sepaResults.add(prop);
        }
    }
}
