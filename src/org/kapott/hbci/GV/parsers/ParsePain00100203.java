package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.bind.JAXB;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.CreditTransferTransactionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.PaymentInstructionInformationSCT;


public class ParsePain00100203 implements ISEPAParser {
    
    public void parse(InputStream xml, ArrayList<Properties> sepaResults) {
        
        Document doc = JAXB.unmarshal(xml, Document.class);
                
        //Payment Information 
        ArrayList<PaymentInstructionInformationSCT> pmtInfs = (ArrayList<PaymentInstructionInformationSCT>) doc.getCstmrCdtTrfInitn().getPmtInf();
        
        for(PaymentInstructionInformationSCT pmtInf : pmtInfs) {

            //Payment Information - Credit Transfer Transaction Information
            ArrayList<CreditTransferTransactionInformationSCT> cdtTrxTxInfs = (ArrayList<CreditTransferTransactionInformationSCT>) pmtInf.getCdtTrfTxInf();
            
            for(CreditTransferTransactionInformationSCT cdtTrxTxInf : cdtTrxTxInfs) {
                Properties sepaResult = new Properties();

                sepaResult.setProperty("src.name", doc.getCstmrCdtTrfInitn().getGrpHdr().getInitgPty().getNm());
                
                sepaResult.setProperty("src.iban", pmtInf.getDbtrAcct().getId().getIBAN());
                sepaResult.setProperty("src.bic", pmtInf.getDbtrAgt().getFinInstnId().getBIC());
                
                sepaResult.setProperty("dst.name", cdtTrxTxInf.getCdtr().getNm());
                sepaResult.setProperty("dst.iban", cdtTrxTxInf.getCdtrAcct().getId().getIBAN());
                sepaResult.setProperty("dst.bic", cdtTrxTxInf.getCdtrAgt().getFinInstnId().getBIC());
                
                BigDecimal value = cdtTrxTxInf.getAmt().getInstdAmt().getValue();
                sepaResult.setProperty("value", value.toString());
                sepaResult.setProperty("curr", "EUR");
                sepaResult.setProperty("usage", cdtTrxTxInf.getRmtInf().getUstrd());
                
                sepaResults.add(sepaResult);
            }
        }
    }
}
