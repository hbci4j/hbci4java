package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXB;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.CreditTransferTransactionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.CurrencyAndAmountSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.Pain00100102;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PaymentIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PaymentInstructionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PurposeSCT;


/**
 * Parser-Implementierung fuer Pain 001.002.02.
 */
public class ParsePain00100202 extends AbstractSepaParser<List<Properties>>
{
    /**
     * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
     */
    public void parse(InputStream xml, List<Properties> sepaResults)
    {
        Document doc = JAXB.unmarshal(xml, Document.class);
                
        //Payment Information 
        Pain00100102 pain = doc.getPain00100102();

        if (pain == null)
            return;

        List<PaymentInstructionInformationSCT> pmtInfs = pain.getPmtInf();
        
        for (PaymentInstructionInformationSCT pmtInf : pmtInfs)
        {
            //Payment Information - Credit Transfer Transaction Information
            List<CreditTransferTransactionInformationSCT> txList = pmtInf.getCdtTrfTxInf();
            
            for (CreditTransferTransactionInformationSCT tx : txList)
            {
                Properties prop = new Properties();
                
                put(prop,Names.PMTINFID,pmtInf.getPmtInfId());
                put(prop,Names.SRC_NAME, pain.getGrpHdr().getInitgPty().getNm());
                put(prop,Names.SRC_IBAN, pmtInf.getDbtrAcct().getId().getIBAN());
                put(prop,Names.SRC_BIC, pmtInf.getDbtrAgt().getFinInstnId().getBIC());
                
                put(prop,Names.DST_NAME, tx.getCdtr().getNm());
                put(prop,Names.DST_IBAN, tx.getCdtrAcct().getId().getIBAN());
                put(prop,Names.DST_BIC, tx.getCdtrAgt().getFinInstnId().getBIC());
                
                CurrencyAndAmountSCT amt = tx.getAmt().getInstdAmt();
                put(prop,Names.VALUE, SepaUtil.format(amt.getValue()));
                put(prop,Names.CURR, amt.getCcy().value());

                if(tx.getRmtInf() != null) {
                    put(prop,Names.USAGE, tx.getRmtInf().getUstrd());
                }
                
                PurposeSCT purp = tx.getPurp();
                if (purp != null)
                    put(prop,Names.PURPOSECODE,purp.getCd());
                
                XMLGregorianCalendar date = pmtInf.getReqdExctnDt();
                if (date != null) {
                    put(prop,Names.DATE, SepaUtil.format(date,null));
                }

                PaymentIdentification1 pmtId = tx.getPmtId();
                if (pmtId != null) {
                    put(prop,Names.ENDTOENDID, pmtId.getEndToEndId());
                }

                sepaResults.add(prop);
            }
        }
    }
}
