package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.xml.datatype.DatatypeFactory;

import org.kapott.hbci.sepa.PainVersion;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.AccountIdentificationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.AmendmentInformationDetailsSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.BranchAndFinancialInstitutionIdentificationSDD1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.BranchAndFinancialInstitutionIdentificationSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.CashAccountSDD1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.CashAccountSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.ChargeBearerTypeSDDCode;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.CurrencyAndAmountSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.CurrencyCodeSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.DirectDebitTransactionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.DirectDebitTransactionSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.Document;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.FinancialInstitutionIdentificationSDD1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.FinancialInstitutionIdentificationSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.GenericIdentificationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.GroupHeaderSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.Grouping1CodeSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.LocalInstrumentCodeSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.LocalInstrumentSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.MandateRelatedInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.Pain00800101;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartyIdentificationSDD1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartyIdentificationSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartyIdentificationSDD3;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartyIdentificationSDD4;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PartySDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PaymentIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PaymentInstructionInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PaymentMethod2Code;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PaymentTypeInformationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.PersonIdentificationSDD2;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.RemittanceInformationSDDChoice;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.RestrictedIdentificationSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.RestrictedSEPACode;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.RestrictedSMNDACode;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.SequenceType1Code;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.ServiceLevelSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_002_01.ServiceLevelSDDCode;


/**
 * SEPA-Geneator fuer pain.008.002.01.
 */
public class GenLastSEPA00800201 extends AbstractSEPAGenerator
{
    /**
     * @see org.kapott.hbci.GV.generators.AbstractSEPAGenerator#getPainVersion()
     */
    @Override
    public PainVersion getPainVersion()
    {
        return PainVersion.PAIN_008_002_01;
    }

    /**
     * @see org.kapott.hbci.GV.generators.ISEPAGenerator#generate(java.util.Properties, java.io.OutputStream, boolean)
     */
    @Override
    public void generate(Properties sepaParams, OutputStream os, boolean validate) throws Exception
    {
        //Formatter um Dates ins gewünschte ISODateTime Format zu bringen.
        Date now=new Date();
        SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DatatypeFactory df = DatatypeFactory.newInstance();


        //Document
        Document doc = new Document();


        //Customer Credit Transfer Initiation
        doc.setPain00800101(new Pain00800101());
        doc.getPain00800101().setGrpHdr(new GroupHeaderSDD());


        //Group Header
        doc.getPain00800101().getGrpHdr().setMsgId(sepaParams.getProperty("sepaid"));
        doc.getPain00800101().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
        doc.getPain00800101().getGrpHdr().setNbOfTxs("1");
        doc.getPain00800101().getGrpHdr().setCtrlSum(new BigDecimal(sepaParams.getProperty("btg.value")));
        doc.getPain00800101().getGrpHdr().setGrpg(Grouping1CodeSDD.MIXD);
        doc.getPain00800101().getGrpHdr().setInitgPty(new PartyIdentificationSDD1());
        doc.getPain00800101().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));


        //Payment Information
        ArrayList<PaymentInstructionInformationSDD> pmtInfs = (ArrayList<PaymentInstructionInformationSDD>) doc.getPain00800101().getPmtInf();
        PaymentInstructionInformationSDD pmtInf = new PaymentInstructionInformationSDD();
        pmtInfs.add(pmtInf);

        pmtInf.setPmtInfId(sepaParams.getProperty("sepaid"));
        pmtInf.setPmtMtd(PaymentMethod2Code.DD);

        pmtInf.setReqdColltnDt(df.newXMLGregorianCalendar(sepaParams.getProperty("targetdate")));
        pmtInf.setCdtr(new PartyIdentificationSDD2());
        pmtInf.setCdtrAcct(new CashAccountSDD1());
        pmtInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentificationSDD1());

        //Payment Information
        pmtInf.getCdtr().setNm(sepaParams.getProperty("src.name"));

        //Payment Information
        pmtInf.getCdtrAcct().setId(new AccountIdentificationSDD());
        pmtInf.getCdtrAcct().getId().setIBAN(sepaParams.getProperty("src.iban"));

        //Payment Information
        pmtInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSDD1());
        pmtInf.getCdtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("src.bic"));


        //Payment Information - ChargeBearer
        pmtInf.setChrgBr(ChargeBearerTypeSDDCode.SLEV);

        pmtInf.setPmtTpInf(new PaymentTypeInformationSDD());
        pmtInf.getPmtTpInf().setSeqTp(SequenceType1Code.fromValue(sepaParams.getProperty("sequencetype")));
        pmtInf.getPmtTpInf().setSvcLvl(new ServiceLevelSDD());
        pmtInf.getPmtTpInf().getSvcLvl().setCd(ServiceLevelSDDCode.SEPA);
        pmtInf.getPmtTpInf().setLclInstrm(new LocalInstrumentSDD());
        pmtInf.getPmtTpInf().getLclInstrm().setCd(LocalInstrumentCodeSDD.valueOf(sepaParams.getProperty("type")));

        //Payment Information - Credit Transfer Transaction Information
        ArrayList<DirectDebitTransactionInformationSDD> drctDbtTxInfs = (ArrayList<DirectDebitTransactionInformationSDD>) pmtInf.getDrctDbtTxInf();
        DirectDebitTransactionInformationSDD drctDbtTxInf = new DirectDebitTransactionInformationSDD();
        drctDbtTxInfs.add(drctDbtTxInf);


        drctDbtTxInf.setDrctDbtTx(new DirectDebitTransactionSDD());
        drctDbtTxInf.getDrctDbtTx().setCdtrSchmeId(new PartyIdentificationSDD4());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().setId(new PartySDD());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().setPrvtId(new PersonIdentificationSDD2());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().setOthrId(new GenericIdentificationSDD());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setId(sepaParams.getProperty("creditorid"));
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setIdTp(RestrictedSEPACode.SEPA);


        drctDbtTxInf.getDrctDbtTx().setMndtRltdInf(new MandateRelatedInformationSDD());
        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setMndtId(sepaParams.getProperty("mandateid"));
        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setDtOfSgntr(df.newXMLGregorianCalendar(sepaParams.getProperty("manddateofsig")));

        boolean amend = Boolean.valueOf(sepaParams.getProperty("amendmandindic"));

        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInd(amend);

        if (amend)
        {
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInfDtls(new AmendmentInformationDetailsSDD());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().setOrgnlDbtrAgt(new BranchAndFinancialInstitutionIdentificationSDD2());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSDD2());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().setPrtryId(new RestrictedIdentificationSDD());
            drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().getPrtryId().setId(RestrictedSMNDACode.SMNDA);
        }


        //Payment Information - Credit Transfer Transaction Information - Payment Identification
        drctDbtTxInf.setPmtId(new PaymentIdentification1());
        drctDbtTxInf.getPmtId().setEndToEndId(sepaParams.getProperty("endtoendid"));


        //Payment Information - Credit Transfer Transaction Information - Creditor
        drctDbtTxInf.setDbtr(new PartyIdentificationSDD3());
        drctDbtTxInf.getDbtr().setNm(sepaParams.getProperty("dst.name"));

        //Payment Information - Credit Transfer Transaction Information - Creditor Account
        drctDbtTxInf.setDbtrAcct(new CashAccountSDD2());
        drctDbtTxInf.getDbtrAcct().setId(new AccountIdentificationSDD());
        drctDbtTxInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty("dst.iban"));

        //Payment Information - Credit Transfer Transaction Information - Creditor Agent
        drctDbtTxInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentificationSDD1());
        drctDbtTxInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSDD1());
        drctDbtTxInf.getDbtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("dst.bic"));


        //Payment Information - Credit Transfer Transaction Information - Amount
        drctDbtTxInf.setInstdAmt(new CurrencyAndAmountSDD());
        drctDbtTxInf.getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty("btg.value")));

        drctDbtTxInf.getInstdAmt().setCcy(CurrencyCodeSDD.EUR);

        //Payment Information - Credit Transfer Transaction Information - Usage
        drctDbtTxInf.setRmtInf(new RemittanceInformationSDDChoice());
        drctDbtTxInf.getRmtInf().setUstrd(sepaParams.getProperty("usage"));

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
    }
}
