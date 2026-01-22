package org.hbci4java.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;

import org.hbci4java.hbci.GV_Result.GVRVoP.VoPResultItem;
import org.hbci4java.hbci.GV_Result.GVRVoP.VoPStatus;
import org.hbci4java.hbci.sepa.SepaVersion;
import org.hbci4java.hbci.sepa.jaxb.pain_002_001_11.AccountIdentification4Choice;
import org.hbci4java.hbci.sepa.jaxb.pain_002_001_11.ActiveOrHistoricCurrencyAndAmount;
import org.hbci4java.hbci.sepa.jaxb.pain_002_001_11.CustomerPaymentStatusReportV11;
import org.hbci4java.hbci.sepa.jaxb.pain_002_001_11.Document;
import org.hbci4java.hbci.sepa.jaxb.pain_002_001_11.OriginalPaymentInstruction38;
import org.hbci4java.hbci.sepa.jaxb.pain_002_001_11.OriginalTransactionReference31;
import org.hbci4java.hbci.sepa.jaxb.pain_002_001_11.PartyIdentification135;
import org.hbci4java.hbci.sepa.jaxb.pain_002_001_11.PaymentTransaction126;
import org.hbci4java.hbci.sepa.jaxb.pain_002_001_11.StatusReasonInformation12;


/**
 * Parser-Implementierung fuer Pain 002.001.11.
 */
public class ParsePain00200111 extends AbstractParsePain002
{
  /**
   * @see org.hbci4java.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
   */
  public void parse(InputStream xml, List<VoPResultItem> sepaResults)
  {
    final Document doc = this.parse(xml,SepaVersion.PAIN_002_001_11,Document.class);
    final CustomerPaymentStatusReportV11 pain = doc.getCstmrPmtStsRpt();
    
    if (pain == null)
        return;

    for (OriginalPaymentInstruction38 pi:pain.getOrgnlPmtInfAndSts())
    {
      final List<PaymentTransaction126> txList = pi.getTxInfAndSts();
      if (txList == null || txList.isEmpty())
      {
        // Die Hypovereinsbank lässt die TxInfAndSts komplett weg
        final VoPResultItem r = new VoPResultItem();
        r.setStatus(VoPStatus.byCode(pi.getPmtInfSts()));
        sepaResults.add(r);
        continue;
      }

      for (PaymentTransaction126 tx:txList)
      {
        final VoPResultItem r = new VoPResultItem();
        r.setStatus(VoPStatus.byCode(tx.getTxSts()));
        r.setName(this.getName(tx.getStsRsnInf()));
        
        final OriginalTransactionReference31 ref = tx.getOrgnlTxRef();
        if (ref != null)
        {
          final AccountIdentification4Choice id       = ref.getCdtrAcct() != null ? ref.getCdtrAcct().getId() : null;
          final List<String> usage                    = ref.getRmtInf() != null   ? ref.getRmtInf().getUstrd() : null;
          final ActiveOrHistoricCurrencyAndAmount amt = ref.getAmt() != null      ? ref.getAmt().getInstdAmt() : null;
          final PartyIdentification135 pty            = ref.getCdtr() != null     ? ref.getCdtr().getPty() : null;
          
          if (id != null)
            r.setIban(id.getIBAN());
          
          if (usage != null)
            r.setUsage(this.toString(usage,false));
          
          if (amt != null)
            r.setAmount(amt.getValue());
          
          if (pty != null)
            r.setOriginal(pty.getNm());
        }
        
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
      sb.append(this.toString(i.getAddtlInf(),true));
    }
    
    return sb.toString();
  }
  
}
