package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;

import org.kapott.hbci.GV_Result.GVRVoP.VoPResultItem;
import org.kapott.hbci.GV_Result.GVRVoP.VoPStatus;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.AccountIdentification4Choice;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.CashAccount38;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.CustomerPaymentStatusReportV11;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.Document;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.OriginalPaymentInstruction38;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.OriginalTransactionReference31;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.Party40Choice;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.PartyIdentification135;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.PaymentTransaction126;
import org.kapott.hbci.sepa.jaxb.pain_002_001_11.StatusReasonInformation12;

import jakarta.xml.bind.JAXB;


/**
 * Parser-Implementierung fuer Pain 002.001.11.
 */
public class ParsePain00200111 extends AbstractParsePain002
{
  /**
   * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
   */
  public void parse(InputStream xml, List<VoPResultItem> sepaResults)
  {
    final Document doc = JAXB.unmarshal(xml, Document.class);
    final CustomerPaymentStatusReportV11 pain = doc.getCstmrPmtStsRpt();
    
    if (pain == null)
        return;

    for (OriginalPaymentInstruction38 pi:pain.getOrgnlPmtInfAndSts())
    {
      for (PaymentTransaction126 tx:pi.getTxInfAndSts())
      {
        final VoPResultItem r = new VoPResultItem();
        r.setStatus(VoPStatus.byCode(tx.getTxSts()));
        r.setName(this.getName(tx.getStsRsnInf()));
        
        // Ursprüngliche IBAN herausfinden
        final OriginalTransactionReference31 ref = tx.getOrgnlTxRef();
        final CashAccount38 account = ref != null ? ref.getCdtrAcct() : null;
        final AccountIdentification4Choice id = account != null ? account.getId() : null;
        
        if (id != null)
          r.setIban(id.getIBAN());
        
        // Ursprünglichen Namen herausfinden
        final Party40Choice cdtr = ref != null ? ref.getCdtr() : null;
        final PartyIdentification135 pty = cdtr != null ? cdtr.getPty() : null;
        if (pty != null)
          r.setOriginal(pty.getNm());
        
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
