package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.xml.datatype.DatatypeFactory;

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
import org.kapott.hbci.sepa.jaxb.pain_001_002_03.RemittanceInformationSEPA1Choice;

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
		//Formatter um Dates ins gewünschte ISODateTime Format zu bringen.
		Date now=new Date();
		SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	    DatatypeFactory df = DatatypeFactory.newInstance();
	    	    
		//Document
		Document doc = new Document();
		
		
		//Customer Credit Transfer Initiation
		doc.setCstmrCdtTrfInitn(new CustomerCreditTransferInitiationV03());
		doc.getCstmrCdtTrfInitn().setGrpHdr(new GroupHeaderSCT());
		
		
		//Group Header
		doc.getCstmrCdtTrfInitn().getGrpHdr().setMsgId(sepaParams.getProperty("sepaid"));
		doc.getCstmrCdtTrfInitn().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
	    doc.getCstmrCdtTrfInitn().getGrpHdr().setNbOfTxs("1");
		doc.getCstmrCdtTrfInitn().getGrpHdr().setInitgPty(new PartyIdentificationSEPA1());
		doc.getCstmrCdtTrfInitn().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));
		
		
		//Payment Information 
		ArrayList<PaymentInstructionInformationSCT> pmtInfs = (ArrayList<PaymentInstructionInformationSCT>) doc.getCstmrCdtTrfInitn().getPmtInf();
		PaymentInstructionInformationSCT pmtInf = new PaymentInstructionInformationSCT();
		pmtInfs.add(pmtInf);
		
		pmtInf.setPmtInfId(sepaParams.getProperty("sepaid")); 
		pmtInf.setPmtMtd(PaymentMethodSCTCode.TRF);
		
		pmtInf.setReqdExctnDt(df.newXMLGregorianCalendar("1999-01-01")); 
		
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
		CreditTransferTransactionInformationSCT cdtTrxTxInf = new CreditTransferTransactionInformationSCT();
		cdtTrxTxInfs.add(cdtTrxTxInf);
		
		
		//Payment Information - Credit Transfer Transaction Information - Payment Identification
		cdtTrxTxInf.setPmtId(new PaymentIdentificationSEPA());
		cdtTrxTxInf.getPmtId().setEndToEndId(sepaParams.getProperty("endtoendid"));
		
		
		//Payment Information - Credit Transfer Transaction Information - Creditor
		cdtTrxTxInf.setCdtr(new PartyIdentificationSEPA2());
		cdtTrxTxInf.getCdtr().setNm(sepaParams.getProperty("dst.name"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Account
		cdtTrxTxInf.setCdtrAcct(new CashAccountSEPA2());
		cdtTrxTxInf.getCdtrAcct().setId(new AccountIdentificationSEPA());
		cdtTrxTxInf.getCdtrAcct().getId().setIBAN(sepaParams.getProperty("dst.iban"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Agent
		cdtTrxTxInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentificationSEPA1());
		cdtTrxTxInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSEPA1());
		cdtTrxTxInf.getCdtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("dst.bic"));


		//Payment Information - Credit Transfer Transaction Information - Amount
		cdtTrxTxInf.setAmt(new AmountTypeSEPA());
		cdtTrxTxInf.getAmt().setInstdAmt(new ActiveOrHistoricCurrencyAndAmountSEPA());
		cdtTrxTxInf.getAmt().getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty("btg.value")));
		cdtTrxTxInf.getAmt().getInstdAmt().setCcy(ActiveOrHistoricCurrencyCodeEUR.EUR); //FIXME: Schema sagt es gibt nur eur aber besser wäre bestimmt getSEPAParam("btg.curr")
		

		//Payment Information - Credit Transfer Transaction Information - Usage
		cdtTrxTxInf.setRmtInf(new RemittanceInformationSEPA1Choice());
		
		cdtTrxTxInf.getRmtInf().setUstrd(sepaParams.getProperty("usage"));

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
	}
}
