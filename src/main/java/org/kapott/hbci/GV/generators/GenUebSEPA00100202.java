package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.PainVersion;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.AccountIdentificationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.AmountTypeSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.BranchAndFinancialInstitutionIdentificationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.CashAccountSCT1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.CashAccountSCT2;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.ChargeBearerTypeSCTCode;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.CreditTransferTransactionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.CurrencyAndAmountSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.CurrencyCodeSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.FinancialInstitutionIdentificationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.GroupHeaderSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.Grouping1CodeSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.Pain00100102;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PartyIdentificationSCT1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PartyIdentificationSCT2;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PaymentIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PaymentInstructionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PaymentMethodSCTCode;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PaymentTypeInformationSCT1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PurposeSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.RemittanceInformationSCTChoice;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.ServiceLevelSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.ServiceLevelSCTCode;


/**
 * SEPA-Generator fuer pain.001.002.02.
 */
public class GenUebSEPA00100202 extends AbstractSEPAGenerator
{
    /**
     * @see org.kapott.hbci.GV.generators.AbstractSEPAGenerator#getPainVersion()
     */
    @Override
    public PainVersion getPainVersion()
    {
        return PainVersion.PAIN_001_002_02;
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


        //Pain00100102
        doc.setPain00100102(new Pain00100102());


        doc.getPain00100102().setGrpHdr(new GroupHeaderSCT());
        
        String batch = SepaUtil.getProperty(sepaParams,"batchbook",null);
        if (batch != null)
            doc.getPain00100102().getGrpHdr().setBtchBookg(batch.equals("1"));

        final String sepaId   = sepaParams.getProperty("sepaid");
        final String pmtInfId = sepaParams.getProperty("pmtinfid");

        //Group Header
        doc.getPain00100102().getGrpHdr().setMsgId(sepaId);
        doc.getPain00100102().getGrpHdr().setCreDtTm(SepaUtil.createCalendar(null));
        doc.getPain00100102().getGrpHdr().setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        doc.getPain00100102().getGrpHdr().setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));
        doc.getPain00100102().getGrpHdr().setGrpg(Grouping1CodeSCT.MIXD);
        doc.getPain00100102().getGrpHdr().setInitgPty(new PartyIdentificationSCT1());
        doc.getPain00100102().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));


        //Payment Information
        ArrayList<PaymentInstructionInformationSCT> pmtInfs = (ArrayList<PaymentInstructionInformationSCT>) doc.getPain00100102().getPmtInf();
        PaymentInstructionInformationSCT pmtInf = new PaymentInstructionInformationSCT();
        pmtInfs.add(pmtInf);

        pmtInf.setPmtInfId(pmtInfId != null && pmtInfId.length() > 0 ? pmtInfId : sepaId);
        pmtInf.setPmtMtd(PaymentMethodSCTCode.TRF);

        // Payment Type Information
        pmtInf.setPmtTpInf(new PaymentTypeInformationSCT1());
        pmtInf.getPmtTpInf().setSvcLvl(new ServiceLevelSCT());
        pmtInf.getPmtTpInf().getSvcLvl().setCd(ServiceLevelSCTCode.SEPA);

        String date = sepaParams.getProperty("date");
        if(date == null) date = SepaUtil.DATE_UNDEFINED;
        pmtInf.setReqdExctnDt(SepaUtil.createCalendar(date));
        pmtInf.setDbtr(new PartyIdentificationSCT2());
        pmtInf.setDbtrAcct(new CashAccountSCT1());
        pmtInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentificationSCT());


        //Payment Information - Debtor
        pmtInf.getDbtr().setNm(sepaParams.getProperty("src.name"));


        //Payment Information - DebtorAccount
        pmtInf.getDbtrAcct().setId(new AccountIdentificationSCT());
        pmtInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty("src.iban"));


        //Payment Information - DebtorAgent
        pmtInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSCT());
        pmtInf.getDbtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("src.bic"));


        //Payment Information - ChargeBearer
        pmtInf.setChrgBr(ChargeBearerTypeSCTCode.SLEV);
        
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

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
    }

    private CreditTransferTransactionInformationSCT createCreditTransferTransactionInformationSCT(Properties sepaParams, Integer index)
    {
        CreditTransferTransactionInformationSCT cdtTrxTxInf = new CreditTransferTransactionInformationSCT();

        //Payment Information - Credit Transfer Transaction Information - Payment Identification
        cdtTrxTxInf.setPmtId(new PaymentIdentification1());
        cdtTrxTxInf.getPmtId().setEndToEndId(SepaUtil.getProperty(sepaParams,SepaUtil.insertIndex("endtoendid", index),AbstractSEPAGV.ENDTOEND_ID_NOTPROVIDED)); // sicherstellen, dass "NOTPROVIDED" eingetragen wird, wenn keine ID angegeben ist


        //Payment Information - Credit Transfer Transaction Information - Creditor
        cdtTrxTxInf.setCdtr(new PartyIdentificationSCT2());
        cdtTrxTxInf.getCdtr().setNm(sepaParams.getProperty(SepaUtil.insertIndex("dst.name", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Account
        cdtTrxTxInf.setCdtrAcct(new CashAccountSCT2());
        cdtTrxTxInf.getCdtrAcct().setId(new AccountIdentificationSCT());
        cdtTrxTxInf.getCdtrAcct().getId().setIBAN(sepaParams.getProperty(SepaUtil.insertIndex("dst.iban", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Agent
        cdtTrxTxInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentificationSCT());
        cdtTrxTxInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSCT());
        cdtTrxTxInf.getCdtrAgt().getFinInstnId().setBIC(sepaParams.getProperty(SepaUtil.insertIndex("dst.bic", index)));


        //Payment Information - Credit Transfer Transaction Information - Amount
        cdtTrxTxInf.setAmt(new AmountTypeSCT());
        cdtTrxTxInf.getAmt().setInstdAmt(new CurrencyAndAmountSCT());
        cdtTrxTxInf.getAmt().getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty(SepaUtil.insertIndex("btg.value", index))));

        cdtTrxTxInf.getAmt().getInstdAmt().setCcy(CurrencyCodeSCT.EUR);

        //Payment Information - Credit Transfer Transaction Information - Usage
        String usage = sepaParams.getProperty(SepaUtil.insertIndex("usage", index));
        if (usage != null && usage.length() > 0)
        {
            cdtTrxTxInf.setRmtInf(new RemittanceInformationSCTChoice());
            cdtTrxTxInf.getRmtInf().setUstrd(usage);
        }

        String purposeCode = sepaParams.getProperty(SepaUtil.insertIndex("purposecode", index));
        if (purposeCode != null && purposeCode.length() > 0)
        {
            PurposeSCT p = new PurposeSCT();
            p.setCd(purposeCode);
            cdtTrxTxInf.setPurp(p);
        }

        return cdtTrxTxInf;
    }

}
