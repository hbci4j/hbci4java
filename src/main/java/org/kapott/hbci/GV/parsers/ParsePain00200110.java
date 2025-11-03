package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;

import org.kapott.hbci.GV_Result.GVRVoP.VoPResultItem;
import org.kapott.hbci.GV_Result.GVRVoP.VoPStatus;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.AccountIdentification4Choice;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.ActiveOrHistoricCurrencyAndAmount;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.CustomerPaymentStatusReportV10;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.Document;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.OriginalPaymentInstruction32;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.OriginalTransactionReference28;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.PartyIdentification135;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.PaymentTransaction105;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.StatusReasonInformation12;


/**
 * Parser-Implementierung fuer Pain 002.001.10.
 */
public class ParsePain00200110 extends AbstractParsePain002
{
  /**
   * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
   */
  public void parse(InputStream xml, List<VoPResultItem> sepaResults)
  {
    /*
      pain.002 parsen 
    
      Quelle: https://homebanking-hilfe.de/forum/topic.php?p=178922#real178922
    
      Zitat:
        In OrgnlPmtInfAndSts hast du wohl unter TxInfAndSts dann die verschiedenen Antworten pro Status.
        Bspw: für die Close Matches unter <TxSts>RVMC</TxSts> den neuen Namen in <StsRsnInf><AddtlInf>neuer Name</AddtlInf></StsRsnInf>
        und dahinter unter OrgnlTxRef -> Cdtr -> Pty -> Nm den alten falschen Namen und die zugehörige IBAN in CdtrACCT -> Id -> IBAN.
        
      Ausserdem in https://www.ebics.de/de/datenformate in Anlage_3_Datenformate_V3.9.pdf - Kapitel 2.2.6
    */

    final Document doc = this.parse(xml,SepaVersion.PAIN_002_001_10,Document.class);
    final CustomerPaymentStatusReportV10 pain = doc.getCstmrPmtStsRpt();
    
    if (pain == null)
        return;

    for (OriginalPaymentInstruction32 pi:pain.getOrgnlPmtInfAndSts())
    {
      final List<PaymentTransaction105> txList = pi.getTxInfAndSts();
      if (txList == null || txList.isEmpty())
      {
        // Die Hypovereinsbank lässt die TxInfAndSts komplett weg
        final VoPResultItem r = new VoPResultItem();
        r.setStatus(VoPStatus.byCode(pi.getPmtInfSts()));
        sepaResults.add(r);
        continue;
      }
      for (PaymentTransaction105 tx:txList)
      {
        final VoPResultItem r = new VoPResultItem();
        r.setStatus(VoPStatus.byCode(tx.getTxSts()));
        r.setName(this.getName(tx.getStsRsnInf()));
        
        final OriginalTransactionReference28 ref = tx.getOrgnlTxRef();
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
