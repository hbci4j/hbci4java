package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;

import org.kapott.hbci.GV_Result.GVRVoP.VoPResultItem;
import org.kapott.hbci.GV_Result.GVRVoP.VoPStatus;
import org.kapott.hbci.sepa.jaxb.pain_002_001_14.AccountIdentification4Choice;
import org.kapott.hbci.sepa.jaxb.pain_002_001_14.CashAccount40;
import org.kapott.hbci.sepa.jaxb.pain_002_001_14.CustomerPaymentStatusReportV14;
import org.kapott.hbci.sepa.jaxb.pain_002_001_14.Document;
import org.kapott.hbci.sepa.jaxb.pain_002_001_14.OriginalPaymentInstruction51;
import org.kapott.hbci.sepa.jaxb.pain_002_001_14.OriginalTransactionReference42;
import org.kapott.hbci.sepa.jaxb.pain_002_001_14.PaymentTransaction160;
import org.kapott.hbci.sepa.jaxb.pain_002_001_14.StatusReasonInformation14;

import jakarta.xml.bind.JAXB;


/**
 * Parser-Implementierung fuer Pain 002.001.14.
 */
public class ParsePain00200114 extends AbstractParsePain002
{
  /**
   * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
   */
  public void parse(InputStream xml, List<VoPResultItem> sepaResults)
  {
    final Document doc = JAXB.unmarshal(xml, Document.class);
    final CustomerPaymentStatusReportV14 pain = doc.getCstmrPmtStsRpt();
    
    if (pain == null)
        return;

    for (OriginalPaymentInstruction51 pi:pain.getOrgnlPmtInfAndSts())
    {
      for (PaymentTransaction160 tx:pi.getTxInfAndSts())
      {
        final VoPResultItem r = new VoPResultItem();
        r.setStatus(VoPStatus.byCode(tx.getTxSts()));
        r.setName(this.getName(tx.getStsRsnInf()));
        
        // Ursprüngliche IBAN herausfinden
        final OriginalTransactionReference42 ref = tx.getOrgnlTxRef();
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
  private String getName(List<StatusReasonInformation14> infos)
  {
    final StringBuilder sb = new StringBuilder();
    if (infos == null || infos.isEmpty())
      return sb.toString();
    
    for (StatusReasonInformation14 i:infos)
    {
      sb.append(this.getNames(i.getAddtlInf()));
    }
    
    return sb.toString();
  }
  
}
