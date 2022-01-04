package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXB;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.jaxb.pain_001_001_11.ActiveOrHistoricCurrencyAndAmount;
import org.kapott.hbci.sepa.jaxb.pain_001_001_11.CreditTransferTransaction54;
import org.kapott.hbci.sepa.jaxb.pain_001_001_11.CustomerCreditTransferInitiationV11;
import org.kapott.hbci.sepa.jaxb.pain_001_001_11.DateAndDateTime2Choice;
import org.kapott.hbci.sepa.jaxb.pain_001_001_11.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_001_11.PaymentIdentification6;
import org.kapott.hbci.sepa.jaxb.pain_001_001_11.PaymentInstruction40;
import org.kapott.hbci.sepa.jaxb.pain_001_001_11.Purpose2Choice;
import org.kapott.hbci.tools.StringUtil;


/**
 * Parser-Implementierung fuer Pain 001.001.11.
 */
public class ParsePain00100111 extends AbstractSepaParser<List<Properties>>
{
    /**
     * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
     */
    public void parse(InputStream xml, List<Properties> sepaResults)
    {
        Document doc = JAXB.unmarshal(xml, Document.class);
        CustomerCreditTransferInitiationV11 pain = doc.getCstmrCdtTrfInitn();
        
        if (pain == null)
            return;
                
        //Payment Information 
        List<PaymentInstruction40> pmtInfs = pain.getPmtInf();
        
        for (PaymentInstruction40 pmtInf : pmtInfs)
        {

            //Payment Information - Credit Transfer Transaction Information
            List<CreditTransferTransaction54> txList = pmtInf.getCdtTrfTxInf();
            
            for (CreditTransferTransaction54 tx : txList)
            {
                Properties prop = new Properties();

                put(prop,Names.PMTINFID,pmtInf.getPmtInfId());
                put(prop,Names.SRC_NAME, pain.getGrpHdr().getInitgPty().getNm());
                put(prop,Names.SRC_IBAN, pmtInf.getDbtrAcct().getId().getIBAN());
                put(prop,Names.SRC_BIC, pmtInf.getDbtrAgt().getFinInstnId().getBICFI());
                
                put(prop,Names.DST_NAME, tx.getCdtr().getNm());
                put(prop,Names.DST_IBAN, tx.getCdtrAcct().getId().getIBAN());
                
                try
                {
                    put(prop,Names.DST_BIC, tx.getCdtrAgt().getFinInstnId().getBICFI());
                }
                catch (Exception e)
                {
                    // BIC darf fehlen
                }
                
                ActiveOrHistoricCurrencyAndAmount amt = tx.getAmt().getInstdAmt();
                put(prop,Names.VALUE, SepaUtil.format(amt.getValue()));
                put(prop,Names.CURR, amt.getCcy());

                if(tx.getRmtInf() != null) {
                    final String usage = StringUtil.join(tx.getRmtInf().getUstrd(),System.getProperty("line.separator"));
                    if (usage != null)
                      put(prop,Names.USAGE,usage);
                }
                
                Purpose2Choice purp = tx.getPurp();
                if (purp != null)
                    put(prop,Names.PURPOSECODE,purp.getCd());
                
                DateAndDateTime2Choice dt = pmtInf.getReqdExctnDt();
                XMLGregorianCalendar date = dt != null ? dt.getDt() : null;
                if (date != null) {
                    put(prop,Names.DATE, SepaUtil.format(date,null));
                }
                
                PaymentIdentification6 pmtId = tx.getPmtId();
                if (pmtId != null) {
                    put(prop,Names.ENDTOENDID, pmtId.getEndToEndId());
                }
                
                sepaResults.add(prop);
            }
        }
    }
}
