package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.PainVersion;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.AccountIdentification2;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.AmendmentInformationDetails4;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.CashAccount8;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.ChargeBearerType2Code;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.DirectDebitTransaction4;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.DirectDebitTransactionInformation2;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.Document;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.EuroMax9Amount;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.FinancialInstitution2;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.FinancialInstitution3;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.FinancialInstitutionIdentification4;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.FinancialInstitutionIdentification5;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.GroupHeader20;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.Grouping2Code;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.MandateRelatedInformation4;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.Pain00800101;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PartyIdentification11;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PartyIdentification20;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PartyIdentification22;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PartyIdentification23;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PartyPrivate1;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PaymentIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PaymentInstructionInformation5;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PaymentMethod2Code;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PaymentTypeInformation8;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PersonIdentification4;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.RemittanceInformation3;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.RestrictedIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.RestrictedIdentification2;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.SequenceType1Code;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.ServiceLevel3Code;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.ServiceLevel4;


/**
 * SEPA-Generator fuer das Schema pain.008.001.01.
 */
public class GenLastSEPA00800101 extends AbstractSEPAGenerator
{
    /**
     * @see org.kapott.hbci.GV.generators.AbstractSEPAGenerator#getPainVersion()
     */
    @Override
    public PainVersion getPainVersion()
    {
        return PainVersion.PAIN_008_001_01;
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
        doc.setPain00800101(new Pain00800101());
        doc.getPain00800101().setGrpHdr(new GroupHeader20());

        final String sepaId   = sepaParams.getProperty("sepaid");
        final String pmtInfId = sepaParams.getProperty("pmtinfid");

        //Group Header
        doc.getPain00800101().getGrpHdr().setMsgId(sepaId);
        doc.getPain00800101().getGrpHdr().setCreDtTm(SepaUtil.createCalendar(null));
        doc.getPain00800101().getGrpHdr().setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        doc.getPain00800101().getGrpHdr().setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));
        doc.getPain00800101().getGrpHdr().setGrpg(Grouping2Code.GRPD);

        doc.getPain00800101().getGrpHdr().setInitgPty(new PartyIdentification20());
        doc.getPain00800101().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));

        //Payment Information
        PaymentInstructionInformation5 pmtInf = new PaymentInstructionInformation5();
        doc.getPain00800101().setPmtInf(pmtInf);

        pmtInf.setPmtInfId(pmtInfId != null && pmtInfId.length() > 0 ? pmtInfId : sepaId);
        pmtInf.setPmtMtd(PaymentMethod2Code.DD);

        pmtInf.setReqdColltnDt(SepaUtil.createCalendar(sepaParams.getProperty("targetdate")));
        pmtInf.setCdtr(new PartyIdentification22());
        pmtInf.setCdtrAcct(new CashAccount8());
        pmtInf.setCdtrAgt(new FinancialInstitution2());

        //Payment Information
        pmtInf.getCdtr().setNm(sepaParams.getProperty("src.name"));

        //Payment Information
        pmtInf.getCdtrAcct().setId(new AccountIdentification2());
        pmtInf.getCdtrAcct().getId().setIBAN(sepaParams.getProperty("src.iban"));

        //Payment Information
        pmtInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentification4());
        pmtInf.getCdtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("src.bic"));


        //Payment Information - ChargeBearer
        pmtInf.setChrgBr(ChargeBearerType2Code.SLEV);

        pmtInf.setPmtTpInf(new PaymentTypeInformation8());
        pmtInf.getPmtTpInf().setSvcLvl(new ServiceLevel4());
        pmtInf.getPmtTpInf().getSvcLvl().setCd(ServiceLevel3Code.SEPA);
        pmtInf.getPmtTpInf().setSeqTp(SequenceType1Code.fromValue(sepaParams.getProperty("sequencetype")));

        //Payment Information - Credit Transfer Transaction Information
        ArrayList<DirectDebitTransactionInformation2> drctDbtTxInfs = (ArrayList<DirectDebitTransactionInformation2>) pmtInf.getDrctDbtTxInf();
        if (maxIndex != null)
        {
            for (int tnr = 0; tnr <= maxIndex; tnr++)
            {
                drctDbtTxInfs.add(createDirectDebitTransactionInformation2(sepaParams, tnr));
            }
        }
        else
        {
            drctDbtTxInfs.add(createDirectDebitTransactionInformation2(sepaParams, null));
        }

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
    }

    private DirectDebitTransactionInformation2 createDirectDebitTransactionInformation2(Properties sepaParams, Integer index) throws Exception
    {
        DirectDebitTransactionInformation2 drctDbtTxInf = new DirectDebitTransactionInformation2();

        drctDbtTxInf.setDrctDbtTx(new DirectDebitTransaction4());
        drctDbtTxInf.getDrctDbtTx().setCdtrSchmeId(new PartyIdentification11());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().setId(new PartyPrivate1());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().setPrvtId(new PersonIdentification4());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().setOthrId(new RestrictedIdentification2());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setId(sepaParams.getProperty(SepaUtil.insertIndex("creditorid", index)));
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setIdTp("SEPA");


        drctDbtTxInf.getDrctDbtTx().setMndtRltdInf(new MandateRelatedInformation4());
        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setMndtId(sepaParams.getProperty(SepaUtil.insertIndex("mandateid", index)));
        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setDtOfSgntr(SepaUtil.createCalendar(sepaParams.getProperty(SepaUtil.insertIndex("manddateofsig", index))));

        boolean amend = Boolean.valueOf(sepaParams.getProperty(SepaUtil.insertIndex("amendmandindic", index)));

        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInd(amend);

        if (amend)
        {
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInfDtls(new AmendmentInformationDetails4());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().setOrgnlDbtrAgt(new FinancialInstitution3());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification5());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().setPrtryId(new RestrictedIdentification1());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().getPrtryId().setId("SMNDA");
        }

        //Payment Information - Credit Transfer Transaction Information - Payment Identification
        drctDbtTxInf.setPmtId(new PaymentIdentification1());
        drctDbtTxInf.getPmtId().setEndToEndId(SepaUtil.getProperty(sepaParams,SepaUtil.insertIndex("endtoendid", index),AbstractSEPAGV.ENDTOEND_ID_NOTPROVIDED)); // sicherstellen, dass "NOTPROVIDED" eingetragen wird, wenn keine ID angegeben ist


        //Payment Information - Credit Transfer Transaction Information - Debitor
        drctDbtTxInf.setDbtr(new PartyIdentification23());
        drctDbtTxInf.getDbtr().setNm(sepaParams.getProperty(SepaUtil.insertIndex("dst.name", index)));

        //Payment Information - Credit Transfer Transaction Information - Debitor Account
        drctDbtTxInf.setDbtrAcct(new CashAccount8());
        drctDbtTxInf.getDbtrAcct().setId(new AccountIdentification2());
        drctDbtTxInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty(SepaUtil.insertIndex("dst.iban", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Agent
        drctDbtTxInf.setDbtrAgt(new FinancialInstitution2());
        drctDbtTxInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification4());
        drctDbtTxInf.getDbtrAgt().getFinInstnId().setBIC(sepaParams.getProperty(SepaUtil.insertIndex("dst.bic", index)));


        //Payment Information - Credit Transfer Transaction Information - Amount
        drctDbtTxInf.setInstdAmt(new EuroMax9Amount());
        drctDbtTxInf.getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty(SepaUtil.insertIndex("btg.value", index))));

        drctDbtTxInf.getInstdAmt().setCcy(sepaParams.getProperty(SepaUtil.insertIndex("btg.curr", index)));

        //Payment Information - Credit Transfer Transaction Information - Usage
        String usage = sepaParams.getProperty(SepaUtil.insertIndex("usage", index));
        if (usage != null && usage.length() > 0)
        {
            drctDbtTxInf.setRmtInf(new RemittanceInformation3());
            drctDbtTxInf.getRmtInf().setUstrd(usage);
        }

        return drctDbtTxInf;
    }

}
