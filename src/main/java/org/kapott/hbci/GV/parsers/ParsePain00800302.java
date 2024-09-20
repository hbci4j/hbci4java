package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import jakarta.xml.bind.JAXB;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.ActiveOrHistoricCurrencyAndAmountSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.CustomerDirectDebitInitiationV02;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.DirectDebitTransactionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PaymentInstructionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PaymentTypeInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PurposeSEPA;

/**
 * Parser-Implementierung fuer Pain 008.003.02.
 */
public class ParsePain00800302 extends AbstractSepaParser<List<Properties>>
{
    /**
     * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
     */
    public void parse(InputStream xml, List<Properties> sepaResults)
    {
        Document doc = JAXB.unmarshal(xml, Document.class);
        CustomerDirectDebitInitiationV02 pain = doc.getCstmrDrctDbtInitn();
        
        if (pain == null)
            return;
                
        List<PaymentInstructionInformationSDD> pmtInfs = pain.getPmtInf();
        
        for (PaymentInstructionInformationSDD pmtInf:pmtInfs)
        {
            List<DirectDebitTransactionInformationSDD> txList = pmtInf.getDrctDbtTxInf();
            
            for (DirectDebitTransactionInformationSDD tx : txList)
            {
                Properties prop = new Properties();

                put(prop,Names.PMTINFID,pmtInf.getPmtInfId());
                put(prop,Names.SRC_NAME, pain.getGrpHdr().getInitgPty().getNm());            
                put(prop,Names.SRC_IBAN, pmtInf.getCdtrAcct().getId().getIBAN());
                put(prop,Names.SRC_BIC, pmtInf.getCdtrAgt().getFinInstnId().getBIC());
                put(prop,Names.BATCHBOOK, pmtInf.isBtchBookg() != null ? pmtInf.isBtchBookg().toString() : null);
                
                put(prop,Names.DST_NAME, tx.getDbtr().getNm());
                put(prop,Names.DST_IBAN, tx.getDbtrAcct().getId().getIBAN());
                
                try
                {
                    put(prop,Names.DST_BIC, tx.getDbtrAgt().getFinInstnId().getBIC());
                }
                catch (Exception e)
                {
                    // BIC darf fehlen
                }
                
                ActiveOrHistoricCurrencyAndAmountSEPA amt = tx.getInstdAmt();
                put(prop,Names.VALUE, SepaUtil.format(amt.getValue()));
                put(prop,Names.CURR, amt.getCcy().value());

                if(tx.getRmtInf() != null) {
                    put(prop,Names.USAGE, tx.getRmtInf().getUstrd());
                }
                
                PurposeSEPA purp = tx.getPurp();
                if (purp != null)
                    put(prop,Names.PURPOSECODE,purp.getCd());

                XMLGregorianCalendar date = pmtInf.getReqdColltnDt();
                if (date != null)
                {
                    put(prop,Names.TARGETDATE, SepaUtil.format(date,null));
                }

                put(prop,Names.ENDTOENDID, tx.getPmtId().getEndToEndId());
                
                try
                {
                    // Auf Auftragsebene suchen
                    put(prop,Names.CREDITORID,tx.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().getId());
                }
                catch (Exception e)
                {
                    // Auf Header-Ebene suchen
                    put(prop,Names.CREDITORID,pmtInf.getCdtrSchmeId().getId().getPrvtId().getOthr().getId());
                }
                
                put(prop,Names.MANDATEID,tx.getDrctDbtTx().getMndtRltdInf().getMndtId());

                XMLGregorianCalendar mandDate = tx.getDrctDbtTx().getMndtRltdInf().getDtOfSgntr();
                if (mandDate != null)
                {
                    put(prop,Names.MANDDATEOFSIG, SepaUtil.format(mandDate,null));
                }

                final PaymentTypeInformationSDD pti = pmtInf.getPmtTpInf();
                if (pti != null)
                {
                  put(prop,Names.SEQUENCETYPE,pti.getSeqTp() != null ? pti.getSeqTp().value() : "FRST");
                  put(prop,Names.LAST_TYPE,pti.getLclInstrm() != null ? pti.getLclInstrm().getCd() : "CORE");
                }

                sepaResults.add(prop);
            }
        }
    }
}
