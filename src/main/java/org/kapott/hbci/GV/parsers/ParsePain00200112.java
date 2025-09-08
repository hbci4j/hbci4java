package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.sepa.jaxb.pain_002_001_12.AccountIdentification4Choice;
import org.kapott.hbci.sepa.jaxb.pain_002_001_12.CashAccount40;
import org.kapott.hbci.sepa.jaxb.pain_002_001_12.CustomerPaymentStatusReportV12;
import org.kapott.hbci.sepa.jaxb.pain_002_001_12.Document;
import org.kapott.hbci.sepa.jaxb.pain_002_001_12.OriginalPaymentInstruction40;
import org.kapott.hbci.sepa.jaxb.pain_002_001_12.OriginalTransactionReference35;
import org.kapott.hbci.sepa.jaxb.pain_002_001_12.PaymentTransaction129;
import org.kapott.hbci.sepa.jaxb.pain_002_001_12.StatusReasonInformation12;

import jakarta.xml.bind.JAXB;


/**
 * Parser-Implementierung fuer Pain 002.001.12.
 */
public class ParsePain00200112 extends AbstractSepaParser<List<Properties>>
{
  /**
   * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
   */
  public void parse(InputStream xml, List<Properties> sepaResults)
  {
    final Document doc = JAXB.unmarshal(xml, Document.class);
    final CustomerPaymentStatusReportV12 pain = doc.getCstmrPmtStsRpt();
    
    if (pain == null)
        return;

    for (OriginalPaymentInstruction40 pi:pain.getOrgnlPmtInfAndSts())
    {
      for (PaymentTransaction129 tx:pi.getTxInfAndSts())
      {
        final Properties prop = new Properties();
        put(prop,Names.VOP_STATUS, tx.getTxSts());
        put(prop,Names.DST_NAME, this.getName(tx.getStsRsnInf()));
        
        // Ursprüngliche IBAN herausfinden
        final OriginalTransactionReference35 ref = tx.getOrgnlTxRef();
        final CashAccount40 account = ref != null ? ref.getCdtrAcct() : null;
        final AccountIdentification4Choice id = account != null ? account.getId() : null;
        
        if (id != null)
          put(prop,Names.DST_IBAN, id.getIBAN());
        
        sepaResults.add(prop);
      }
    }
  }
  
  /**
   * Liefert den korrigierten Namen.
   * @param infos die Infos.
   * @return der korrigerte Name oder einen Leerstring, wenn keiner ermittelt werden konnte.
   */
  private String getName(List<StatusReasonInformation12> infos)
  {
    final StringBuilder sb = new StringBuilder();
    if (infos == null || infos.isEmpty())
      return sb.toString();
    
    for (StatusReasonInformation12 i:infos)
    {
      final List<String> names = i.getAddtlInf();
      if (names == null || names.isEmpty())
        continue;
      
      for (String s:names)
      {
        if (s == null || s.isBlank())
          continue;
        
        if (s.startsWith("“") || s.startsWith("\""))
          s = s.substring(1);
        
        sb.append(s);
      }
    }
    
    return sb.toString();
  }
  
}
