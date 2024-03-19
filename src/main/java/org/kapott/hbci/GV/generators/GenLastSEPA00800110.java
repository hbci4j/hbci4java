package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.AccountIdentification4Choice;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.ActiveOrHistoricCurrencyAndAmount;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.AmendmentInformationDetails14;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.BranchAndFinancialInstitutionIdentification6;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.CashAccount40;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.ChargeBearerType1Code;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.CustomerDirectDebitInitiationV10;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.DirectDebitTransaction11;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.DirectDebitTransactionInformation28;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.Document;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.FinancialInstitutionIdentification18;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.GenericAccountIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.GenericFinancialIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.GenericPersonIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.GroupHeader83;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.LocalInstrument2Choice;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.MandateRelatedInformation15;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.Party38Choice;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.PartyIdentification135;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.PaymentIdentification6;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.PaymentInstruction39;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.PaymentMethod2Code;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.PaymentTypeInformation29;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.PersonIdentification13;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.PersonIdentificationSchemeName1Choice;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.PostalAddress24;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.Purpose2Choice;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.RemittanceInformation21;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.SequenceType3Code;
import org.kapott.hbci.sepa.jaxb.pain_008_001_10.ServiceLevel8Choice;

/**
 * SEPA-Generator fuer pain.008.001.10.
 */
public class GenLastSEPA00800110 extends AbstractSEPAGenerator<Properties>
{
  /**
   * @see org.kapott.hbci.GV.generators.AbstractSEPAGenerator#getSepaVersion()
   */
  @Override
  public SepaVersion getSepaVersion()
  {
    return SepaVersion.PAIN_008_001_10;
  }

  /**
   * @see org.kapott.hbci.GV.generators.ISEPAGenerator#generate(java.lang.Object, java.io.OutputStream, boolean)
   */
  @Override
  public void generate(Properties sepaParams, OutputStream os, boolean validate) throws Exception
  {
    Integer maxIndex = SepaUtil.maxIndex(sepaParams);

    // Document
    Document doc = new Document();

    // Customer Credit Transfer Initiation
    doc.setCstmrDrctDbtInitn(new CustomerDirectDebitInitiationV10());
    doc.getCstmrDrctDbtInitn().setGrpHdr(new GroupHeader83());

    final String sepaId = sepaParams.getProperty("sepaid");
    final String pmtInfId = sepaParams.getProperty("pmtinfid");

    // Group Header
    doc.getCstmrDrctDbtInitn().getGrpHdr().setMsgId(sepaId);
    doc.getCstmrDrctDbtInitn().getGrpHdr().setCreDtTm(SepaUtil.createCalendar(null));
    doc.getCstmrDrctDbtInitn().getGrpHdr().setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
    doc.getCstmrDrctDbtInitn().getGrpHdr().setInitgPty(new PartyIdentification135());
    doc.getCstmrDrctDbtInitn().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));
    doc.getCstmrDrctDbtInitn().getGrpHdr().setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));

    // Payment Information
    ArrayList<PaymentInstruction39> pmtInfs = (ArrayList<PaymentInstruction39>) doc.getCstmrDrctDbtInitn().getPmtInf();
    PaymentInstruction39 pmtInf = new PaymentInstruction39();
    pmtInfs.add(pmtInf);

    pmtInf.setPmtInfId(pmtInfId != null && pmtInfId.length() > 0 ? pmtInfId : sepaId);
    pmtInf.setPmtMtd(PaymentMethod2Code.DD);

    pmtInf.setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
    pmtInf.setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));

    pmtInf.setReqdColltnDt(SepaUtil.createCalendar(sepaParams.getProperty("targetdate")));
    pmtInf.setCdtr(new PartyIdentification135());
    pmtInf.setCdtrAcct(new CashAccount40());
    pmtInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentification6());

    // Payment Information
    pmtInf.getCdtr().setNm(sepaParams.getProperty("src.name"));

    // Payment Information
    pmtInf.getCdtrAcct().setId(new AccountIdentification4Choice());
    pmtInf.getCdtrAcct().getId().setIBAN(sepaParams.getProperty("src.iban"));

    // Payment Information
    pmtInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentification18());
    String srcBic = sepaParams.getProperty("src.bic");
    if (srcBic != null && srcBic.length() > 0) // BIC ist inzwischen optional
    {
      pmtInf.getCdtrAgt().getFinInstnId().setBICFI(srcBic);
    } else
    {
      pmtInf.getCdtrAgt().getFinInstnId().setOthr(new GenericFinancialIdentification1());
      pmtInf.getCdtrAgt().getFinInstnId().getOthr().setId("NOTPROVIDED");
    }

    // Payment Information - ChargeBearer
    pmtInf.setChrgBr(ChargeBearerType1Code.SLEV);

    pmtInf.setPmtTpInf(new PaymentTypeInformation29());
    
    final ServiceLevel8Choice svc = new ServiceLevel8Choice();
    svc.setCd("SEPA");
    pmtInf.getPmtTpInf().getSvcLvl().add(svc);
    pmtInf.getPmtTpInf().setLclInstrm(new LocalInstrument2Choice());
    pmtInf.getPmtTpInf().getLclInstrm().setCd(sepaParams.getProperty("type"));
    pmtInf.getPmtTpInf().setSeqTp(SequenceType3Code.fromValue(sepaParams.getProperty("sequencetype")));

    // Payment Information - Credit Transfer Transaction Information
    ArrayList<DirectDebitTransactionInformation28> drctDbtTxInfs = (ArrayList<DirectDebitTransactionInformation28>) pmtInf.getDrctDbtTxInf();
    if (maxIndex != null)
    {
      for (int tnr = 0; tnr <= maxIndex; tnr++)
      {
        drctDbtTxInfs.add(createDirectDebitTransactionInformation(sepaParams, tnr));
      }
    } else
    {
      drctDbtTxInfs.add(createDirectDebitTransactionInformation(sepaParams, null));
    }

    String batch = SepaUtil.getProperty(sepaParams, "batchbook", null);
    if (batch != null)
      pmtInf.setBtchBookg(batch.equals("1"));

    ObjectFactory of = new ObjectFactory();
    this.marshal(of.createDocument(doc), os, validate);
  }

  private DirectDebitTransactionInformation28 createDirectDebitTransactionInformation(Properties sepaParams, Integer index) throws Exception
  {
    DirectDebitTransactionInformation28 drctDbtTxInf = new DirectDebitTransactionInformation28();
    drctDbtTxInf.setDrctDbtTx(new DirectDebitTransaction11());
    drctDbtTxInf.getDrctDbtTx().setCdtrSchmeId(new PartyIdentification135());
    drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().setId(new Party38Choice());
    drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().setPrvtId(new PersonIdentification13());
    
    final GenericPersonIdentification1 creditor = new GenericPersonIdentification1();
    creditor.setId(sepaParams.getProperty(SepaUtil.insertIndex("creditorid", index)));
    final PersonIdentificationSchemeName1Choice schema = new PersonIdentificationSchemeName1Choice();
    schema.setPrtry("SEPA");
    creditor.setSchmeNm(schema);
    drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().add(creditor);

    drctDbtTxInf.getDrctDbtTx().setMndtRltdInf(new MandateRelatedInformation15());
    drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setMndtId(sepaParams.getProperty(SepaUtil.insertIndex("mandateid", index)));
    drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setDtOfSgntr(SepaUtil.createCalendar(sepaParams.getProperty(SepaUtil.insertIndex("manddateofsig", index))));

    boolean amend = Boolean.valueOf(sepaParams.getProperty(SepaUtil.insertIndex("amendmandindic", index)));
    drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInd(amend);
    if (amend)
    {
      drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInfDtls(new AmendmentInformationDetails14());
      drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().setOrgnlDbtrAcct(new CashAccount40());
      drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAcct().setId(new AccountIdentification4Choice());
      
      final GenericAccountIdentification1 smnda = new GenericAccountIdentification1();
      smnda.setId("SMNDA");
      drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAcct().getId().setOthr(smnda);
    }

    // Payment Information - Credit Transfer Transaction Information - Payment Identification
    drctDbtTxInf.setPmtId(new PaymentIdentification6());
    
     // sicherstellen, dass "NOTPROVIDED" eingetragen wird, wenn keine ID angegeben ist
    drctDbtTxInf.getPmtId().setEndToEndId(SepaUtil.getProperty(sepaParams, SepaUtil.insertIndex("endtoendid", index), AbstractSEPAGV.ENDTOEND_ID_NOTPROVIDED)); 

    // Payment Information - Credit Transfer Transaction Information - Creditor
    drctDbtTxInf.setDbtr(new PartyIdentification135());
    drctDbtTxInf.getDbtr().setNm(sepaParams.getProperty(SepaUtil.insertIndex("dst.name", index)));

    // Payment Information - Credit Transfer Transaction Information - Creditor Account
    drctDbtTxInf.setDbtrAcct(new CashAccount40());
    drctDbtTxInf.getDbtrAcct().setId(new AccountIdentification4Choice());
    drctDbtTxInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty(SepaUtil.insertIndex("dst.iban", index)));

    // Payment Information - Credit Transfer Transaction Information - Creditor Agent
    drctDbtTxInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentification6());
    drctDbtTxInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification18());

    String bic = sepaParams.getProperty(SepaUtil.insertIndex("dst.bic", index));
    if (bic != null && bic.length() > 0)
    {
      drctDbtTxInf.getDbtrAgt().getFinInstnId().setBICFI(bic);
    }
    else
    {
      final GenericFinancialIdentification1 fi = new GenericFinancialIdentification1();
      fi.setId("NOTPROVIDED");
      drctDbtTxInf.getDbtrAgt().getFinInstnId().setOthr(fi);
    }

    // Payment Information - notwendig bei Sepa Lastschriften in Drittstaaten (CH, UK?)
    String property = sepaParams.getProperty(SepaUtil.insertIndex("dst.addr.country", index));
    if (property != null && property.length() > 0)
    {
      drctDbtTxInf.getDbtr().setPstlAdr(new PostalAddress24());
      // Country Code, zb DE, CH etc. [A-Z]{2,2}
      drctDbtTxInf.getDbtr().getPstlAdr().setCtry(sepaParams.getProperty(SepaUtil.insertIndex("dst.addr.country", index)));
      // max 2 Zeilen mit Text min 1, max 70 Zeichen
      for (int i = 1; i <= 2; i++)
      {
        String addressLine = sepaParams.getProperty(SepaUtil.insertIndex("dst.addr.line" + i, index));
        if (addressLine != null && addressLine.length() > 0)
        {
          drctDbtTxInf.getDbtr().getPstlAdr().getAdrLine().add(addressLine);
        }
      }
    }

    // Payment Information - Credit Transfer Transaction Information - Amount
    drctDbtTxInf.setInstdAmt(new ActiveOrHistoricCurrencyAndAmount());
    drctDbtTxInf.getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty(SepaUtil.insertIndex("btg.value", index))));

    String currencyCode = sepaParams.getProperty(SepaUtil.insertIndex("btg.curr", index));
    if(currencyCode != null && currencyCode.length() > 0) {
      drctDbtTxInf.getInstdAmt().setCcy(currencyCode);
    } else {
      drctDbtTxInf.getInstdAmt().setCcy("EUR");
    }

    // Payment Information - Credit Transfer Transaction Information - Usage
    String usage = sepaParams.getProperty(SepaUtil.insertIndex("usage", index));
    if (usage != null && usage.length() > 0)
    {
      drctDbtTxInf.setRmtInf(new RemittanceInformation21());
      drctDbtTxInf.getRmtInf().getUstrd().add(usage);
    }

    String purposeCode = sepaParams.getProperty(SepaUtil.insertIndex("purposecode", index));
    if (purposeCode != null && purposeCode.length() > 0)
    {
      Purpose2Choice p = new Purpose2Choice();
      p.setCd(purposeCode);
      drctDbtTxInf.setPurp(p);
    }

    return drctDbtTxInf;
  }

}
