package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.xml.datatype.DatatypeFactory;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.sepa.PainVersion;
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
    public PainVersion getPainVersion()
    {
        return PainVersion.PAIN_008_002_02;
    }

    /**
     * @see org.kapott.hbci.GV.generators.ISEPAGenerator#generate(java.util.Properties, java.io.OutputStream, boolean)
     */
    @Override
    public void generate(Properties sepaParams, OutputStream os, boolean validate) throws Exception
    {
        Integer maxIndex = maxIndex(sepaParams);

        //Formatter um Dates ins gewünschte ISODateTime Format zu bringen.
        Date now=new Date();
        SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DatatypeFactory df = DatatypeFactory.newInstance();


        //Document
        Document doc = new Document();


        //Customer Credit Transfer Initiation
        doc.setCstmrDrctDbtInitn(new CustomerDirectDebitInitiationV02());
        doc.getCstmrDrctDbtInitn().setGrpHdr(new GroupHeaderSDD());


        //Group Header
        doc.getCstmrDrctDbtInitn().getGrpHdr().setMsgId(sepaParams.getProperty("sepaid"));
        doc.getCstmrDrctDbtInitn().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
        doc.getCstmrDrctDbtInitn().getGrpHdr().setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        doc.getCstmrDrctDbtInitn().getGrpHdr().setInitgPty(new PartyIdentificationSEPA1());
        doc.getCstmrDrctDbtInitn().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));


        //Payment Information
        ArrayList<PaymentInstructionInformationSDD> pmtInfs = (ArrayList<PaymentInstructionInformationSDD>) doc.getCstmrDrctDbtInitn().getPmtInf();
        PaymentInstructionInformationSDD pmtInf = new PaymentInstructionInformationSDD();
        pmtInfs.add(pmtInf);

        pmtInf.setPmtInfId(sepaParams.getProperty("sepaid"));
        pmtInf.setPmtMtd(PaymentMethod2Code.DD);
        pmtInf.setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        pmtInf.setCtrlSum(sumBtgValue(sepaParams, maxIndex));

        pmtInf.setReqdColltnDt(df.newXMLGregorianCalendar(sepaParams.getProperty("targetdate")));
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
                drctDbtTxInfs.add(createDirectDebitTransactionInformationSDD(sepaParams, tnr, df));
            }
        }
        else
        {
            drctDbtTxInfs.add(createDirectDebitTransactionInformationSDD(sepaParams, null, df));
        }

        if ( "0".equals(sepaParams.getProperty("batchbook")))
            pmtInf.setBtchBookg(false);

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
    }

    private DirectDebitTransactionInformationSDD createDirectDebitTransactionInformationSDD(Properties sepaParams, Integer index, DatatypeFactory df)
    {
        DirectDebitTransactionInformationSDD drctDbtTxInf = new DirectDebitTransactionInformationSDD();

        drctDbtTxInf.setDrctDbtTx(new DirectDebitTransactionSDD());
        drctDbtTxInf.getDrctDbtTx().setCdtrSchmeId(new PartyIdentificationSEPA3());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().setId(new PartySEPA2());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().setPrvtId(new PersonIdentificationSEPA2());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().setOthr(new RestrictedPersonIdentificationSEPA());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().setId(sepaParams.getProperty(insertIndex("creditorid", index)));
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().setSchmeNm(new RestrictedPersonIdentificationSchemeNameSEPA());
        drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthr().getSchmeNm().setPrtry(IdentificationSchemeNameSEPA.SEPA);
        drctDbtTxInf.getDrctDbtTx().setMndtRltdInf(new MandateRelatedInformationSDD());
        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setMndtId(sepaParams.getProperty(insertIndex("mandateid", index)));
        drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setDtOfSgntr(df.newXMLGregorianCalendar(sepaParams.getProperty(insertIndex("manddateofsig", index))));

        boolean amend = Boolean.valueOf(sepaParams.getProperty(insertIndex("amendmandindic", index)));

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
        drctDbtTxInf.getPmtId().setEndToEndId(sepaParams.getProperty(insertIndex("endtoendid", index)));


        //Payment Information - Credit Transfer Transaction Information - Creditor
        drctDbtTxInf.setDbtr(new PartyIdentificationSEPA2());
        drctDbtTxInf.getDbtr().setNm(sepaParams.getProperty(insertIndex("dst.name", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Account
        drctDbtTxInf.setDbtrAcct(new CashAccountSEPA2());
        drctDbtTxInf.getDbtrAcct().setId(new AccountIdentificationSEPA());
        drctDbtTxInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty(insertIndex("dst.iban", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Agent
        drctDbtTxInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA1());
        drctDbtTxInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA1());
        drctDbtTxInf.getDbtrAgt().getFinInstnId().setBIC(sepaParams.getProperty(insertIndex("dst.bic", index)));


        //Payment Information - Credit Transfer Transaction Information - Amount
        drctDbtTxInf.setInstdAmt(new ActiveOrHistoricCurrencyAndAmountSEPA());
        drctDbtTxInf.getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty(insertIndex("btg.value", index))));

        drctDbtTxInf.getInstdAmt().setCcy(ActiveOrHistoricCurrencyCodeEUR.EUR);


        //Payment Information - Credit Transfer Transaction Information - Usage
        drctDbtTxInf.setRmtInf(new RemittanceInformationSEPA1Choice());
        drctDbtTxInf.getRmtInf().setUstrd(sepaParams.getProperty(insertIndex("usage", index)));

        return drctDbtTxInf;
    }

}
