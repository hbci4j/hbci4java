package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import jakarta.xml.bind.JAXB;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.jaxb.pain_008_001_08.ActiveOrHistoricCurrencyAndAmount;
import org.kapott.hbci.sepa.jaxb.pain_008_001_08.CustomerDirectDebitInitiationV08;
import org.kapott.hbci.sepa.jaxb.pain_008_001_08.DirectDebitTransactionInformation23;
import org.kapott.hbci.sepa.jaxb.pain_008_001_08.Document;
import org.kapott.hbci.sepa.jaxb.pain_008_001_08.GenericPersonIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_008_001_08.PaymentInstruction29;
import org.kapott.hbci.sepa.jaxb.pain_008_001_08.PaymentTypeInformation29;
import org.kapott.hbci.sepa.jaxb.pain_008_001_08.PersonIdentificationSchemeName1Choice;
import org.kapott.hbci.sepa.jaxb.pain_008_001_08.Purpose2Choice;
import org.kapott.hbci.tools.StringUtil;

/**
 * Parser-Implementierung fuer Pain 008.001.08.
 */
public class ParsePain00800108 extends AbstractSepaParser<List<Properties>>
{
    /**
     * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
     */
    public void parse(InputStream xml, List<Properties> sepaResults)
    {
        Document doc = JAXB.unmarshal(xml, Document.class);
        CustomerDirectDebitInitiationV08 pain = doc.getCstmrDrctDbtInitn();
        
        if (pain == null)
            return;
                
        List<PaymentInstruction29> pmtInfs = pain.getPmtInf();
        
        for (PaymentInstruction29 pmtInf:pmtInfs)
        {
            List<DirectDebitTransactionInformation23> txList = pmtInf.getDrctDbtTxInf();
            
            for (DirectDebitTransactionInformation23 tx : txList)
            {
                Properties prop = new Properties();

                put(prop,Names.PMTINFID,pmtInf.getPmtInfId());
                put(prop,Names.SRC_NAME, pain.getGrpHdr().getInitgPty().getNm());            
                put(prop,Names.SRC_IBAN, pmtInf.getCdtrAcct().getId().getIBAN());
                put(prop,Names.SRC_BIC, pmtInf.getCdtrAgt().getFinInstnId().getBICFI());
                put(prop,Names.BATCHBOOK, pmtInf.isBtchBookg() != null ? pmtInf.isBtchBookg().toString() : null);
                
                put(prop,Names.DST_NAME, tx.getDbtr().getNm());
                put(prop,Names.DST_IBAN, tx.getDbtrAcct().getId().getIBAN());
                
                try
                {
                    put(prop,Names.DST_BIC, tx.getDbtrAgt().getFinInstnId().getBICFI());
                }
                catch (Exception e)
                {
                    // BIC darf fehlen
                }
                
                ActiveOrHistoricCurrencyAndAmount amt = tx.getInstdAmt();
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

                XMLGregorianCalendar date = pmtInf.getReqdColltnDt();
                if (date != null)
                {
                    put(prop,Names.TARGETDATE, SepaUtil.format(date,null));
                }

                put(prop,Names.ENDTOENDID, tx.getPmtId().getEndToEndId());
                
                try
                {
                  // Auf Auftragsebene suchen
                  put(prop,Names.CREDITORID,this.findId(tx.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr()));
                }
                catch (Exception e)
                {
                    // Auf Header-Ebene suchen
                    put(prop,Names.CREDITORID,this.findId(pmtInf.getCdtrSchmeId().getId().getPrvtId().getOthr()));
                }
                
                put(prop,Names.MANDATEID,tx.getDrctDbtTx().getMndtRltdInf().getMndtId());

                XMLGregorianCalendar mandDate = tx.getDrctDbtTx().getMndtRltdInf().getDtOfSgntr();
                if (mandDate != null)
                {
                    put(prop,Names.MANDDATEOFSIG, SepaUtil.format(mandDate,null));
                }

                final PaymentTypeInformation29 pti = pmtInf.getPmtTpInf();
                if (pti != null)
                {
                  put(prop,Names.SEQUENCETYPE,pti.getSeqTp() != null ? pti.getSeqTp().value() : "FRST");
                  put(prop,Names.LAST_TYPE,pti.getLclInstrm() != null ? pti.getLclInstrm().getCd() : "CORE");
                }

                sepaResults.add(prop);
            }
        }
    }
    
    /**
     * Liefert die erste gefundene ID aus der Liste der Personen-Identifier.
     * @param othr die Liste der Personen-Identifier.
     * @return die ID oder NULL.
     */
    private String findId(List<GenericPersonIdentification1> othr)
    {
      if (othr == null)
        return null;

      final List<String> ids = new ArrayList<String>();
 
      for (GenericPersonIdentification1 o:othr)
      {
        final String id = o.getId();
        if (id == null || id.length() == 0)
          continue;
        
        // merken
        ids.add(id);
        
        // Checken, ob der korrekte Personen-Typ angegeben ist.
        // Wenn ja, nehmen wir direkt den
        final PersonIdentificationSchemeName1Choice type = o.getSchmeNm();
        if (type != null && Objects.equals("SEPA",type.getPrtry()))
          return id;
      }

      // Wir haben keine ID gefunden, wo der Typ direkt gepasst hat.
      // Dann nehmen wir die erste gefundene ID.
      return ids.size() > 0 ? ids.get(0) : null;
    }
}
