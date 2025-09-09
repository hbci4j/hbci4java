package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.util.List;

import org.kapott.hbci.GV_Result.GVRVoP.VoPResult;
import org.kapott.hbci.GV_Result.GVRVoP.VoPStatus;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.AccountIdentification4Choice;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.CashAccount38;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.CustomerPaymentStatusReportV10;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.Document;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.OriginalPaymentInstruction32;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.OriginalTransactionReference28;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.PaymentTransaction105;
import org.kapott.hbci.sepa.jaxb.pain_002_001_10.StatusReasonInformation12;

import jakarta.xml.bind.JAXB;


/**
 * Parser-Implementierung fuer Pain 002.001.10.
 */
public class ParsePain00200110 extends AbstractSepaParser<List<VoPResult>>
{
  /**
   * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
   */
  public void parse(InputStream xml, List<VoPResult> sepaResults)
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

    final Document doc = JAXB.unmarshal(xml, Document.class);
    final CustomerPaymentStatusReportV10 pain = doc.getCstmrPmtStsRpt();
    
    if (pain == null)
        return;

    for (OriginalPaymentInstruction32 pi:pain.getOrgnlPmtInfAndSts())
    {
      for (PaymentTransaction105 tx:pi.getTxInfAndSts())
      {
        final VoPResult r = new VoPResult();
        r.setStatus(VoPStatus.byCode(tx.getTxSts()));
        r.setName(this.getName(tx.getStsRsnInf()));
        
        // Ursprüngliche IBAN herausfinden
        final OriginalTransactionReference28 ref = tx.getOrgnlTxRef();
        final CashAccount38 account = ref != null ? ref.getCdtrAcct() : null;
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
