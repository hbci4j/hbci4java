package org.kapott.hbci.GV.generators;


import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.datatype.XMLGregorianCalendar;

import org.kapott.hbci.GV.AbstractSEPAGV;
import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.AccountIdentification4Choice;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.ActiveOrHistoricCurrencyAndAmount;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.AmountType4Choice;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.BranchAndFinancialInstitutionIdentification6;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.CashAccount38;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.ChargeBearerType1Code;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.CreditTransferTransaction34;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.CustomerCreditTransferInitiationV09;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.DateAndDateTime2Choice;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.FinancialInstitutionIdentification18;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.GenericFinancialIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.GroupHeader85;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.PartyIdentification135;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.PaymentIdentification6;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.PaymentInstruction30;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.PaymentMethod3Code;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.PaymentTypeInformation26;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.Purpose2Choice;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.RemittanceInformation16;
import org.kapott.hbci.sepa.jaxb.pain_001_001_09.ServiceLevel8Choice;

/**
 * SEPA-Generator fuer pain.001.001.09.
 */
public class GenUebSEPA00100109 extends AbstractSEPAGenerator<Properties>
{
    /**
     * @see org.kapott.hbci.GV.generators.AbstractSEPAGenerator#getSepaVersion()
     */
    @Override
    public SepaVersion getSepaVersion()
    {
        return SepaVersion.PAIN_001_001_09;
    }

    /**
     * @see org.kapott.hbci.GV.generators.ISEPAGenerator#generate(java.lang.Object, java.io.OutputStream, boolean)
     */
    @Override
    public void generate(Properties sepaParams, OutputStream os, boolean validate) throws Exception
    {
        Integer maxIndex = SepaUtil.maxIndex(sepaParams);

        //Document
        Document doc = new Document();


        //Customer Credit Transfer Initiation
        doc.setCstmrCdtTrfInitn(new CustomerCreditTransferInitiationV09());
        doc.getCstmrCdtTrfInitn().setGrpHdr(new GroupHeader85());

        final String sepaId   = sepaParams.getProperty("sepaid");
        final String pmtInfId = sepaParams.getProperty("pmtinfid");

        //Group Header
        doc.getCstmrCdtTrfInitn().getGrpHdr().setMsgId(sepaId);
        doc.getCstmrCdtTrfInitn().getGrpHdr().setCreDtTm(SepaUtil.createCalendar(null));
        doc.getCstmrCdtTrfInitn().getGrpHdr().setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        doc.getCstmrCdtTrfInitn().getGrpHdr().setInitgPty(new PartyIdentification135());
        doc.getCstmrCdtTrfInitn().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));
        doc.getCstmrCdtTrfInitn().getGrpHdr().setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));
        

        //Payment Information
        ArrayList<PaymentInstruction30> pmtInfs = (ArrayList<PaymentInstruction30>) doc.getCstmrCdtTrfInitn().getPmtInf();
        PaymentInstruction30 pmtInf = new PaymentInstruction30();
        pmtInfs.add(pmtInf);

        pmtInf.setPmtInfId(pmtInfId != null && pmtInfId.length() > 0 ? pmtInfId : sepaId);
        pmtInf.setPmtMtd(PaymentMethod3Code.TRF);

        pmtInf.setNbOfTxs(String.valueOf(maxIndex != null ? maxIndex + 1 : 1));
        pmtInf.setCtrlSum(SepaUtil.sumBtgValue(sepaParams, maxIndex));

        pmtInf.setPmtTpInf(new PaymentTypeInformation26());
        final ServiceLevel8Choice svc = new ServiceLevel8Choice();
        svc.setCd("SEPA");
        pmtInf.getPmtTpInf().getSvcLvl().add(svc);

        String date = sepaParams.getProperty("date");
        if(date == null) date = SepaUtil.DATE_UNDEFINED;
        
        final XMLGregorianCalendar d = SepaUtil.createCalendar(date);
        final DateAndDateTime2Choice dd = new DateAndDateTime2Choice();
        dd.setDt(d);
        pmtInf.setReqdExctnDt(dd);
        pmtInf.setDbtr(new PartyIdentification135());
        pmtInf.setDbtrAcct(new CashAccount38());
        pmtInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentification6());


        //Payment Information - Debtor
        pmtInf.getDbtr().setNm(sepaParams.getProperty("src.name"));


        //Payment Information - DebtorAccount
        pmtInf.getDbtrAcct().setId(new AccountIdentification4Choice());
        pmtInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty("src.iban"));


        //Payment Information - DebtorAgent
        pmtInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification18());
        String srcBic = sepaParams.getProperty("src.bic");
        if (srcBic != null && srcBic.length() > 0) // BIC ist inzwischen optional
        {
            pmtInf.getDbtrAgt().getFinInstnId().setBICFI(srcBic);
        }
        else
        {
            pmtInf.getDbtrAgt().getFinInstnId().setOthr(new GenericFinancialIdentification1());
            pmtInf.getDbtrAgt().getFinInstnId().getOthr().setId("NOTPROVIDED");
        }


        //Payment Information - ChargeBearer
        pmtInf.setChrgBr(ChargeBearerType1Code.SLEV);


        //Payment Information - Credit Transfer Transaction Information
        ArrayList<CreditTransferTransaction34> cdtTrxTxInfs = (ArrayList<CreditTransferTransaction34>) pmtInf.getCdtTrfTxInf();
        if (maxIndex != null)
        {
            for (int tnr = 0; tnr <= maxIndex; tnr++)
            {
                cdtTrxTxInfs.add(createCreditTransferTransaction(sepaParams, tnr));
            }
        }
        else
        {
            cdtTrxTxInfs.add(createCreditTransferTransaction(sepaParams, null));
        }

        String batch = SepaUtil.getProperty(sepaParams,"batchbook",null);
        if (batch != null)
            pmtInf.setBtchBookg(batch.equals("1"));

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
    }

    private CreditTransferTransaction34 createCreditTransferTransaction(Properties sepaParams, Integer index)
    {
        CreditTransferTransaction34 cdtTrxTxInf = new CreditTransferTransaction34();


        //Payment Information - Credit Transfer Transaction Information - Payment Identification
        cdtTrxTxInf.setPmtId(new PaymentIdentification6());
        cdtTrxTxInf.getPmtId().setEndToEndId(SepaUtil.getProperty(sepaParams,SepaUtil.insertIndex("endtoendid", index),AbstractSEPAGV.ENDTOEND_ID_NOTPROVIDED)); // sicherstellen, dass "NOTPROVIDED" eingetragen wird, wenn keine ID angegeben ist


        //Payment Information - Credit Transfer Transaction Information - Creditor
        cdtTrxTxInf.setCdtr(new PartyIdentification135());
        cdtTrxTxInf.getCdtr().setNm(sepaParams.getProperty(SepaUtil.insertIndex("dst.name", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Account
        cdtTrxTxInf.setCdtrAcct(new CashAccount38());
        cdtTrxTxInf.getCdtrAcct().setId(new AccountIdentification4Choice());
        cdtTrxTxInf.getCdtrAcct().getId().setIBAN(sepaParams.getProperty(SepaUtil.insertIndex("dst.iban", index)));

        //Payment Information - Credit Transfer Transaction Information - Creditor Agent
        String dstBic = sepaParams.getProperty(SepaUtil.insertIndex("dst.bic", index));
        if (dstBic != null && dstBic.length() > 0) // BIC ist inzwischen optional
        {
            cdtTrxTxInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentification6());
            cdtTrxTxInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentification18());
            cdtTrxTxInf.getCdtrAgt().getFinInstnId().setBICFI(dstBic);
        }

        //Payment Information - Credit Transfer Transaction Information - Amount
        cdtTrxTxInf.setAmt(new AmountType4Choice());
        cdtTrxTxInf.getAmt().setInstdAmt(new ActiveOrHistoricCurrencyAndAmount());
        cdtTrxTxInf.getAmt().getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty(SepaUtil.insertIndex("btg.value", index))));

        cdtTrxTxInf.getAmt().getInstdAmt().setCcy("EUR");

        //Payment Information - Credit Transfer Transaction Information - Usage
        String usage = sepaParams.getProperty(SepaUtil.insertIndex("usage", index));
        if (usage != null && usage.length() > 0)
        {
            cdtTrxTxInf.setRmtInf(new RemittanceInformation16());
            cdtTrxTxInf.getRmtInf().getUstrd().add(usage);
        }

        String purposeCode = sepaParams.getProperty(SepaUtil.insertIndex("purposecode", index));
        if (purposeCode != null && purposeCode.length() > 0)
        {
            Purpose2Choice p = new Purpose2Choice();
            p.setCd(purposeCode);
            cdtTrxTxInf.setPurp(p);
        }

        return cdtTrxTxInf;
    }

}
