package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.PainVersion;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.AccountIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.ActiveOrHistoricCurrencyAndAmountSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.ActiveOrHistoricCurrencyCodeEUR;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.AmountTypeSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.BranchAndFinancialInstitutionIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.CashAccountSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.CashAccountSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.ChargeBearerTypeSEPACode;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.CreditTransferTransactionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.CustomerCreditTransferInitiationV03;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.FinancialInstitutionIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.GroupHeaderSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.PartyIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.PartyIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.PaymentIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.PaymentInstructionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.PaymentMethodSCTCode;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.PaymentTypeInformationSCT1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.PurposeSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.RemittanceInformationSEPA1Choice;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.ServiceLevelSEPA;
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.ServiceLevelSEPACode;

/**
 * SEPA-Generator fuer pain.001.002.03.
 */
public class GenUebSEPA00100203 extends AbstractSEPAGenerator
{
    /**
     * @see org.kapott.hbci.GV.generators.AbstractSEPAGenerator#getPainVersion()
     */
    @Override
    public PainVersion getPainVersion()
    {
        return PainVersion.PAIN_001_002_03;
    }

    /**
     * @see org.kapott.hbci.GV.generators.ISEPAGenerator#generate(java.util.Properties, java.io.OutputStream, boolean)
     */
    @Override
    public void generate(Properties sepaParams, OutputStream os, boolean validate) throws Exception
    {
        Integer maxIndex = SepaUtil.maxIndex(sepaParams);

        //Document
        Document doc = new Document();


        //Customer Credit Transfer Initiation
        doc.setCstmrCdtTrfInitn(new CustomerCreditTransferInitiationV03());
        doc.getCstmrCdtTrfInitn().setGrpHdr(new GroupHeaderSCT());

        final String sepaId   = sepaParams.getProperty("sepaid");
        final String pmtInfId = sepaParams.getProperty("pmtinfid");

        //Group Header
        doc.getCstmrCdtTrfInitn().getGrpHdr().setMsgId(sepaId);
        doc.getCstmrCdtTrfInitn().getGrpHdr().setCreDtTm(SepaUtil.createCalendar(null));
        doc.getCstmrCdtTrfInitn().getGrpHdr().setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        doc.getCstmrCdtTrfInitn().getGrpHdr().setInitgPty(new PartyIdentificationSEPA1());
        doc.getCstmrCdtTrfInitn().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));
        doc.getCstmrCdtTrfInitn().getGrpHdr().setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));


        //Payment Information
        ArrayList<PaymentInstructionInformationSCT> pmtInfs = (ArrayList<PaymentInstructionInformationSCT>) doc.getCstmrCdtTrfInitn().getPmtInf();
        PaymentInstructionInformationSCT pmtInf = new PaymentInstructionInformationSCT();
        pmtInfs.add(pmtInf);

        pmtInf.setPmtInfId(pmtInfId != null && pmtInfId.length() > 0 ? pmtInfId : sepaId);
        pmtInf.setPmtMtd(PaymentMethodSCTCode.TRF);
        pmtInf.setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        pmtInf.setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));

        // Payment Type Information
        pmtInf.setPmtTpInf(new PaymentTypeInformationSCT1());
        pmtInf.getPmtTpInf().setSvcLvl(new ServiceLevelSEPA());
        pmtInf.getPmtTpInf().getSvcLvl().setCd(ServiceLevelSEPACode.SEPA);

        String date = sepaParams.getProperty("date");
        if(date == null) date = SepaUtil.DATE_UNDEFINED;
        pmtInf.setReqdExctnDt(SepaUtil.createCalendar(date));

        pmtInf.setDbtr(new PartyIdentificationSEPA2());
        pmtInf.setDbtrAcct(new CashAccountSEPA1());
        pmtInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA1());


        //Payment Information - Debtor
        pmtInf.getDbtr().setNm(sepaParams.getProperty("src.name"));


        //Payment Information - DebtorAccount
        pmtInf.getDbtrAcct().setId(new AccountIdentificationSEPA());
        pmtInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty("src.iban"));


        //Payment Information - DebtorAgent
        pmtInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA1());
        pmtInf.getDbtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("src.bic"));


        //Payment Information - ChargeBearer
        pmtInf.setChrgBr(ChargeBearerTypeSEPACode.SLEV);


        //Payment Information - Credit Transfer Transaction Information
        ArrayList<CreditTransferTransactionInformationSCT> cdtTrxTxInfs = (ArrayList<CreditTransferTransactionInformationSCT>) pmtInf.getCdtTrfTxInf();
        if (maxIndex != null)
        {
            for (int tnr = 0; tnr <= maxIndex; tnr++)
            {
                cdtTrxTxInfs.add(createCreditTransferTransactionInformationSCT(sepaParams, tnr));
            }
        }
        else
        {
            cdtTrxTxInfs.add(createCreditTransferTransactionInformationSCT(sepaParams, null));
        }

        String batch = SepaUtil.getProperty(sepaParams,"batchbook",null);
        if (batch != null)
            pmtInf.setBtchBookg(batch.equals("1"));

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
    }

    private CreditTransferTransactionInformationSCT createCreditTransferTransactionInformationSCT(Properties sepaParams, Integer index)
    {
        CreditTransferTransactionInformationSCT cdtTrxTxInf = new CreditTransferTransactionInformationSCT();

        //Payment Information - Credit Transfer Transaction Information - Payment Identification
        cdtTrxTxInf.setPmtId(new PaymentIdentificationSEPA());
        cdtTrxTxInf.getPmtId().setEndToEndId(SepaUtil.getProperty(sepaParams,SepaUtil.insertIndex("endtoendid", index),AbstractSEPAGV.ENDTOEND_ID_NOTPROVIDED)); // sicherstellen, dass "NOTPROVIDED" eingetragen wird, wenn keine ID angegeben ist


        //Payment Information - Credit Transfer Transaction Information - Creditor
        cdtTrxTxInf.setCdtr(new PartyIdentificationSEPA2());
        cdtTrxTxInf.getCdtr().setNm(sepaParams.getProperty(SepaUtil.insertIndex("dst.name", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Account
        cdtTrxTxInf.setCdtrAcct(new CashAccountSEPA2());
        cdtTrxTxInf.getCdtrAcct().setId(new AccountIdentificationSEPA());
        cdtTrxTxInf.getCdtrAcct().getId().setIBAN(sepaParams.getProperty(SepaUtil.insertIndex("dst.iban", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Agent
        cdtTrxTxInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA1());
        cdtTrxTxInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA1());
        cdtTrxTxInf.getCdtrAgt().getFinInstnId().setBIC(sepaParams.getProperty(SepaUtil.insertIndex("dst.bic", index)));


        //Payment Information - Credit Transfer Transaction Information - Amount
        cdtTrxTxInf.setAmt(new AmountTypeSEPA());
        cdtTrxTxInf.getAmt().setInstdAmt(new ActiveOrHistoricCurrencyAndAmountSEPA());
        cdtTrxTxInf.getAmt().getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty(SepaUtil.insertIndex("btg.value", index))));
        cdtTrxTxInf.getAmt().getInstdAmt().setCcy(ActiveOrHistoricCurrencyCodeEUR.EUR); //FIXME: Schema sagt es gibt nur eur aber besser wäre bestimmt getSEPAParam("btg.curr")

        //Payment Information - Credit Transfer Transaction Information - Usage
        String usage = sepaParams.getProperty(SepaUtil.insertIndex("usage", index));
        if (usage != null && usage.length() > 0)
        {
            cdtTrxTxInf.setRmtInf(new RemittanceInformationSEPA1Choice());
            cdtTrxTxInf.getRmtInf().setUstrd(usage);
        }

        String purposeCode = sepaParams.getProperty(SepaUtil.insertIndex("purposecode", index));
        if (purposeCode != null && purposeCode.length() > 0)
        {
            PurposeSEPA p = new PurposeSEPA();
            p.setCd(purposeCode);
            cdtTrxTxInf.setPurp(p);
        }

        return cdtTrxTxInf;
    }

}
