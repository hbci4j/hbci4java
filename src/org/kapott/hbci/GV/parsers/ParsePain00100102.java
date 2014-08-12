package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXB;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.CreditTransferTransactionInformation2;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.EuroMax9Amount;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.Pain00100102;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PaymentIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PaymentInstructionInformation4;


/**
 * Parser-Implementierung fuer Pain 001.001.02.
 */
public class ParsePain00100102 extends AbstractSepaParser
{
    
    /**
     * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.util.List)
     */
    public void parse(InputStream xml, List<Properties> sepaResults)
    {
        
        Document doc = JAXB.unmarshal(xml, Document.class);
        Pain00100102 pain = doc.getPain00100102();
        
        if (pain == null)
            return;

        PaymentInstructionInformation4 pmtInf = pain.getPmtInf();
        
        //Payment Information - Credit Transfer Transaction Information
        List<CreditTransferTransactionInformation2> txList = pmtInf.getCdtTrfTxInf();
            
        for (CreditTransferTransactionInformation2 tx : txList)
        {
            Properties prop = new Properties();
            
            put(prop,Names.PMTINFID,pmtInf.getPmtInfId());
            put(prop,Names.SRC_NAME,pain.getGrpHdr().getInitgPty().getNm());
            put(prop,Names.SRC_IBAN, pmtInf.getDbtrAcct().getId().getIBAN());
            put(prop,Names.SRC_BIC, pmtInf.getDbtrAgt().getFinInstnId().getBIC());
            
            put(prop,Names.DST_NAME, tx.getCdtr().getNm());
            put(prop,Names.DST_IBAN, tx.getCdtrAcct().getId().getIBAN());
            put(prop,Names.DST_BIC, tx.getCdtrAgt().getFinInstnId().getBIC());
            
            EuroMax9Amount amt = tx.getAmt().getInstdAmt();
            put(prop,Names.VALUE, SepaUtil.format(amt.getValue()));
            put(prop,Names.CURR, amt.getCcy());

            if(tx.getRmtInf() != null) {
                put(prop,Names.USAGE, tx.getRmtInf().getUstrd());
            }
            
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
