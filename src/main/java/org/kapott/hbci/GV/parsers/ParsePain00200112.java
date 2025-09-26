package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;

import org.kapott.hbci.GV_Result.GVRVoP.VoPResultItem;
import org.kapott.hbci.GV_Result.GVRVoP.VoPStatus;
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
public class ParsePain00200112 extends AbstractParsePain002
{
  /**
   * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
   */
  public void parse(InputStream xml, List<VoPResultItem> sepaResults)
  {
    final Document doc = JAXB.unmarshal(xml, Document.class);
    final CustomerPaymentStatusReportV12 pain = doc.getCstmrPmtStsRpt();
    
    if (pain == null)
        return;

    for (OriginalPaymentInstruction40 pi:pain.getOrgnlPmtInfAndSts())
    {
      for (PaymentTransaction129 tx:pi.getTxInfAndSts())
      {
        final VoPResultItem r = new VoPResultItem();
        r.setStatus(VoPStatus.byCode(tx.getTxSts()));
        r.setName(this.getName(tx.getStsRsnInf()));
        
        // Ursprüngliche IBAN herausfinden
        final OriginalTransactionReference35 ref = tx.getOrgnlTxRef();
        final CashAccount40 account = ref != null ? ref.getCdtrAcct() : null;
        final AccountIdentification4Choice id = account != null ? account.getId() : null;
        
        if (id != null)
          r.setIban(id.getIBAN());
        
        sepaResults.add(r);
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
      sb.append(this.getNames(i.getAddtlInf()));
    }
    
    return sb.toString();
  }
  
}
