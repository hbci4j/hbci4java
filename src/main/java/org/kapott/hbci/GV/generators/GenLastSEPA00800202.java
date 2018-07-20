package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.AccountIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.ActiveOrHistoricCurrencyAndAmountSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.ActiveOrHistoricCurrencyCodeEUR;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.AmendmentInformationDetailsSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.BranchAndFinancialInstitutionIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.BranchAndFinancialInstitutionIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.CashAccountSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.CashAccountSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.ChargeBearerTypeSEPACode;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.CustomerDirectDebitInitiationV02;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.DirectDebitTransactionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.DirectDebitTransactionSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.FinancialInstitutionIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.FinancialInstitutionIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.GroupHeaderSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.IdentificationSchemeNameSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.LocalInstrumentSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.LocalInstrumentSEPACode;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.MandateRelatedInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PartyIdentificationSEPA1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PartyIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PartyIdentificationSEPA3;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PartyIdentificationSEPA5;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PartySEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PaymentIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PaymentInstructionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PaymentMethod2Code;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PaymentTypeInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PersonIdentificationSEPA2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.PurposeSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.RemittanceInformationSEPA1Choice;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.RestrictedFinancialIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.RestrictedPersonIdentificationSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.RestrictedPersonIdentificationSchemeNameSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.RestrictedSMNDACode;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.SequenceType1Code;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.ServiceLevelSEPA;
import org.kapott.hbci.sepa.jaxb.pain_008_002_02.ServiceLevelSEPACode;


/**
 * SEPA-Generator fuer pain.008.002.02.
 */
public class GenLastSEPA00800202 extends AbstractSEPAGenerator
{
    /**
     * @see org.kapott.hbci.GV.generators.AbstractSEPAGenerator#getPainVersion()
     */
    @Override
    public SepaVersion getPainVersion()
    {
        return SepaVersion.PAIN_008_002_02;
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
        doc.setCstmrDrctDbtInitn(new CustomerDirectDebitInitiationV02());
        doc.getCstmrDrctDbtInitn().setGrpHdr(new GroupHeaderSDD());

        final String sepaId   = sepaParams.getProperty("sepaid");
        final String pmtInfId = sepaParams.getProperty("pmtinfid");

        //Group Header
        doc.getCstmrDrctDbtInitn().getGrpHdr().setMsgId(sepaId);
        doc.getCstmrDrctDbtInitn().getGrpHdr().setCreDtTm(SepaUtil.createCalendar(null));
        doc.getCstmrDrctDbtInitn().getGrpHdr().setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        doc.getCstmrDrctDbtInitn().getGrpHdr().setInitgPty(new PartyIdentificationSEPA1());
        doc.getCstmrDrctDbtInitn().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));
        doc.getCstmrDrctDbtInitn().getGrpHdr().setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));


        //Payment Information
        ArrayList<PaymentInstructionInformationSDD> pmtInfs = (ArrayList<PaymentInstructionInformationSDD>) doc.getCstmrDrctDbtInitn().getPmtInf();
        PaymentInstructionInformationSDD pmtInf = new PaymentInstructionInformationSDD();
        pmtInfs.add(pmtInf);

        pmtInf.setPmtInfId(pmtInfId != null && pmtInfId.length() > 0 ? pmtInfId : sepaId);
        pmtInf.setPmtMtd(PaymentMethod2Code.DD);
        pmtInf.setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        pmtInf.setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));

        pmtInf.setReqdColltnDt(SepaUtil.createCalendar(sepaParams.getProperty("targetdate")));
        pmtInf.setCdtr(new PartyIdentificationSEPA5());
        pmtInf.setCdtrAcct(new CashAccountSEPA1());
        pmtInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA1());

        //Payment Information
        pmtInf.getCdtr().setNm(sepaParams.getProperty("src.name"));

        //Payment Information
        pmtInf.getCdtrAcct().setId(new AccountIdentificationSEPA());
        pmtInf.getCdtrAcct().getId().setIBAN(sepaParams.getProperty("src.iban"));

        //Payment Information
        pmtInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA1());
        pmtInf.getCdtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("src.bic"));


        //Payment Information - ChargeBearer
        pmtInf.setChrgBr(ChargeBearerTypeSEPACode.SLEV);

        pmtInf.setPmtTpInf(new PaymentTypeInformationSDD());
        pmtInf.getPmtTpInf().setSeqTp(SequenceType1Code.fromValue(sepaParams.getProperty("sequencetype")));

        pmtInf.getPmtTpInf().setSvcLvl(new ServiceLevelSEPA());
        pmtInf.getPmtTpInf().getSvcLvl().setCd(ServiceLevelSEPACode.SEPA);
        pmtInf.getPmtTpInf().setLclInstrm(new LocalInstrumentSEPA());
        
        String type = sepaParams.getProperty("type");
        try
        {
            pmtInf.getPmtTpInf().getLclInstrm().setCd(LocalInstrumentSEPACode.fromValue(type));
        }
        catch (IllegalArgumentException e)
        {
            throw new HBCI_Exception("Lastschrift-Art " + type + " wird in der SEPA-Version 008.002.02 Ihrer Bank noch nicht unterstützt",e);
        }

        //Payment Information - Credit Transfer Transaction Information
        ArrayList<DirectDebitTransactionInformationSDD> drctDbtTxInfs = (ArrayList<DirectDebitTransactionInformationSDD>) pmtInf.getDrctDbtTxInf();
        if (maxIndex != null)
        {
            for (int tnr = 0; tnr <= maxIndex; tnr++)
            {
                drctDbtTxInfs.add(createDirectDebitTransactionInformationSDD(sepaParams, tnr));
            }
        }
        else
        {
            drctDbtTxInfs.add(createDirectDebitTransactionInformationSDD(sepaParams, null));
        }

        String batch = SepaUtil.getProperty(sepaParams,"batchbook",null);
        if (batch != null)
            pmtInf.setBtchBookg(batch.equals("1"));

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
    }

    private DirectDebitTransactionInformationSDD createDirectDebitTransactionInformationSDD(Properties sepaParams, Integer index) throws Exception
    {
        DirectDebitTransactionInformationSDD drctDbtTxInf = new DirectDebitTransactionInformationSDD();

        drctDbtTxInf.setDrctDbtTx(new DirectDebitTransactionSDD());
        drctDbtTxInf.getDrctDbtTx().setCdtrSchmeId(new PartyIdentificationSEPA3());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().setId(new PartySEPA2());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().setPrvtId(new PersonIdentificationSEPA2());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().setOthr(new RestrictedPersonIdentificationSEPA());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().setId(sepaParams.getProperty(SepaUtil.insertIndex("creditorid", index)));
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().setSchmeNm(new RestrictedPersonIdentificationSchemeNameSEPA());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().getSchmeNm().setPrtry(IdentificationSchemeNameSEPA.SEPA);
        drctDbtTxInf.getDrctDbtTx().setMndtRltdInf(new MandateRelatedInformationSDD());
        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setMndtId(sepaParams.getProperty(SepaUtil.insertIndex("mandateid", index)));
        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setDtOfSgntr(SepaUtil.createCalendar(sepaParams.getProperty(SepaUtil.insertIndex("manddateofsig", index))));

        boolean amend = Boolean.valueOf(sepaParams.getProperty(SepaUtil.insertIndex("amendmandindic", index)));

        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInd(amend);

        if (amend)
        {
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInfDtls(new AmendmentInformationDetailsSDD());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().setOrgnlDbtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA2());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA2());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().setOthr(new RestrictedFinancialIdentificationSEPA());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().getOthr().setId(RestrictedSMNDACode.SMNDA);
        }

        //Payment Information - Credit Transfer Transaction Information - Payment Identification
        drctDbtTxInf.setPmtId(new PaymentIdentificationSEPA());
        drctDbtTxInf.getPmtId().setEndToEndId(SepaUtil.getProperty(sepaParams,SepaUtil.insertIndex("endtoendid", index),AbstractSEPAGV.ENDTOEND_ID_NOTPROVIDED)); // sicherstellen, dass "NOTPROVIDED" eingetragen wird, wenn keine ID angegeben ist


        //Payment Information - Credit Transfer Transaction Information - Creditor
        drctDbtTxInf.setDbtr(new PartyIdentificationSEPA2());
        drctDbtTxInf.getDbtr().setNm(sepaParams.getProperty(SepaUtil.insertIndex("dst.name", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Account
        drctDbtTxInf.setDbtrAcct(new CashAccountSEPA2());
        drctDbtTxInf.getDbtrAcct().setId(new AccountIdentificationSEPA());
        drctDbtTxInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty(SepaUtil.insertIndex("dst.iban", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Agent
        drctDbtTxInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA1());
        drctDbtTxInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA1());
        drctDbtTxInf.getDbtrAgt().getFinInstnId().setBIC(sepaParams.getProperty(SepaUtil.insertIndex("dst.bic", index)));


        //Payment Information - Credit Transfer Transaction Information - Amount
        drctDbtTxInf.setInstdAmt(new ActiveOrHistoricCurrencyAndAmountSEPA());
        drctDbtTxInf.getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty(SepaUtil.insertIndex("btg.value", index))));

        drctDbtTxInf.getInstdAmt().setCcy(ActiveOrHistoricCurrencyCodeEUR.EUR);

        //Payment Information - Credit Transfer Transaction Information - Usage
        String usage = sepaParams.getProperty(SepaUtil.insertIndex("usage", index));
        if (usage != null && usage.length() > 0)
        {
            drctDbtTxInf.setRmtInf(new RemittanceInformationSEPA1Choice());
            drctDbtTxInf.getRmtInf().setUstrd(usage);
        }

        String purposeCode = sepaParams.getProperty(SepaUtil.insertIndex("purposecode", index));
        if (purposeCode != null && purposeCode.length() > 0)
        {
            PurposeSEPA p = new PurposeSEPA();
            p.setCd(purposeCode);
            drctDbtTxInf.setPurp(p);
        }

        return drctDbtTxInf;
    }

}
