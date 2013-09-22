package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.datatype.DatatypeFactory;

import org.kapott.hbci.GV.AbstractSEPAGV;
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
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.Pain00100102;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PartyIdentificationSCT1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PartyIdentificationSCT2;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PaymentIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PaymentInstructionInformationSCT;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.PaymentMethodSCTCode;
import org.kapott.hbci.sepa.jaxb.pain_001_002_02.RemittanceInformationSCTChoice;


/**
 * SEPA-Generator fuer pain.001.002.02.
 */
public class GenUebSEPA00100202 extends AbstractSEPAGenerator
{
	/**
	 * @see org.kapott.hbci.GV.generators.ISEPAGenerator#generate(org.kapott.hbci.GV.AbstractSEPAGV, java.io.OutputStream)
	 */
	public void generate(AbstractSEPAGV job, OutputStream os) throws Exception
	{
		//Formatter um Dates ins gewünschte ISODateTime Format zu bringen.
		Date now=new Date();
		SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	    DatatypeFactory df = DatatypeFactory.newInstance();
		
		
		//Document
		Document doc = new Document();
		
		
		//Pain00100102
		doc.setPain00100102(new Pain00100102());
		
		
		doc.getPain00100102().setGrpHdr(new GroupHeaderSCT());
				
		//Group Header
		doc.getPain00100102().getGrpHdr().setMsgId(job.getSEPAParam("sepaid"));
		doc.getPain00100102().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
	    doc.getPain00100102().getGrpHdr().setNbOfTxs("1");
		doc.getPain00100102().getGrpHdr().setInitgPty(new PartyIdentificationSCT1());
		doc.getPain00100102().getGrpHdr().getInitgPty().setNm(job.getSEPAParam("src.name"));
		
		
		//Payment Information 
		ArrayList<PaymentInstructionInformationSCT> pmtInfs = (ArrayList<PaymentInstructionInformationSCT>) doc.getPain00100102().getPmtInf();
		PaymentInstructionInformationSCT pmtInf = new PaymentInstructionInformationSCT();
		pmtInfs.add(pmtInf);
		
		//FIXME: Wo kommt die ID her und wie muss sie aussehen?
		pmtInf.setPmtInfId(job.getSEPAParam("sepaid")); 
		pmtInf.setPmtMtd(PaymentMethodSCTCode.TRF);
		
		pmtInf.setReqdExctnDt(df.newXMLGregorianCalendar("1999-01-01"));
		pmtInf.setDbtr(new PartyIdentificationSCT2());
		pmtInf.setDbtrAcct(new CashAccountSCT1());
		pmtInf.setDbtrAgt(new BranchAndFinancialInstitutionIdentificationSCT());
		
		
		//Payment Information - Debtor
		pmtInf.getDbtr().setNm(job.getSEPAParam("src.name"));
		
		
		//Payment Information - DebtorAccount
		pmtInf.getDbtrAcct().setId(new AccountIdentificationSCT());
		pmtInf.getDbtrAcct().getId().setIBAN(job.getSEPAParam("src.iban"));
		
		
		//Payment Information - DebtorAgent
		pmtInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSCT());
		pmtInf.getDbtrAgt().getFinInstnId().setBIC(job.getSEPAParam("src.bic"));
		
		
		//Payment Information - ChargeBearer
		pmtInf.setChrgBr(ChargeBearerTypeSCTCode.SLEV);
		
		
		//Payment Information - Credit Transfer Transaction Information
		ArrayList<CreditTransferTransactionInformationSCT> cdtTrxTxInfs = (ArrayList<CreditTransferTransactionInformationSCT>) pmtInf.getCdtTrfTxInf();
		CreditTransferTransactionInformationSCT cdtTrxTxInf = new CreditTransferTransactionInformationSCT();
		cdtTrxTxInfs.add(cdtTrxTxInf);
		
		
		//Payment Information - Credit Transfer Transaction Information - Payment Identification
		cdtTrxTxInf.setPmtId(new PaymentIdentification1());
		cdtTrxTxInf.getPmtId().setEndToEndId(job.getSEPAParam("endtoendid"));
		
		
		//Payment Information - Credit Transfer Transaction Information - Creditor
		cdtTrxTxInf.setCdtr(new PartyIdentificationSCT2());
		cdtTrxTxInf.getCdtr().setNm(job.getSEPAParam("dst.name"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Account
		cdtTrxTxInf.setCdtrAcct(new CashAccountSCT2());
		cdtTrxTxInf.getCdtrAcct().setId(new AccountIdentificationSCT());
		cdtTrxTxInf.getCdtrAcct().getId().setIBAN(job.getSEPAParam("dst.iban"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Agent
		cdtTrxTxInf.setCdtrAgt(new BranchAndFinancialInstitutionIdentificationSCT());
		cdtTrxTxInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentificationSCT());
		cdtTrxTxInf.getCdtrAgt().getFinInstnId().setBIC(job.getSEPAParam("dst.bic"));


		//Payment Information - Credit Transfer Transaction Information - Amount
		cdtTrxTxInf.setAmt(new AmountTypeSCT());
		cdtTrxTxInf.getAmt().setInstdAmt(new CurrencyAndAmountSCT());
		cdtTrxTxInf.getAmt().getInstdAmt().setValue(new BigDecimal(job.getSEPAParam("btg.value")));
		
		//FIXME: Schema sagt es gibt nur "eur" aber besser wäre bestimmt trotzdem getSEPAParam("btg.curr") oder?
		cdtTrxTxInf.getAmt().getInstdAmt().setCcy(CurrencyCodeSCT.EUR); 
		
		

		//Payment Information - Credit Transfer Transaction Information - Usage
		//FIXME: momentan nur unstrukturierter Verwendungszweck! Vielleicht gibt es einen Parameter dafür? Dann kann man per If entscheiden
		cdtTrxTxInf.setRmtInf(new RemittanceInformationSCTChoice());
		cdtTrxTxInf.getRmtInf().setUstrd(job.getSEPAParam("usage"));

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc),os);
	}
}
