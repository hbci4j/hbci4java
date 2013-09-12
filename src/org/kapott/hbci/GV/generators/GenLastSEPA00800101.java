package org.kapott.hbci.GV.generators;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;

import org.kapott.hbci.GV.GVLastSEPA;
import org.kapott.hbci.GV.HBCIJob;
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
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.PersonIdentification4;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.RemittanceInformation3;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.RestrictedIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_008_001_01.RestrictedIdentification2;


public class GenLastSEPA00800101 implements ISEPAGenerator{

	@Override
	public void generate(HBCIJob job, ByteArrayOutputStream os)
			throws Exception {
		
		
		generate((GVLastSEPA)job, os);
		
	}
	public void generate(GVLastSEPA job, ByteArrayOutputStream os) throws Exception {
		
		//Formatter um Dates ins gewünschte ISODateTime Format zu bringen.
		Date now=new Date();
		SimpleDateFormat sdtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		DatatypeFactory df = DatatypeFactory.newInstance();
		
		
		//Document
		Document doc = new Document();
		
		
		//Customer Credit Transfer Initiation
		doc.setPain00800101(new Pain00800101());
		doc.getPain00800101().setGrpHdr(new GroupHeader20());
		
		
		//Group Header
		doc.getPain00800101().getGrpHdr().setMsgId(job.getSEPAParam("sepaid"));
		doc.getPain00800101().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
		doc.getPain00800101().getGrpHdr().setNbOfTxs("1");
		doc.getPain00800101().getGrpHdr().setInitgPty(new PartyIdentification20());
		doc.getPain00800101().getGrpHdr().getInitgPty().setNm(job.getSEPAParam("src.name"));
		
		
		//Payment Information 
		PaymentInstructionInformation5 pmtInf = new PaymentInstructionInformation5();
		doc.getPain00800101().setPmtInf(pmtInf);
				
		pmtInf.setPmtInfId(job.getSEPAParam("sepaid")); 
		pmtInf.setPmtMtd(PaymentMethod2Code.DD);
		
		pmtInf.setReqdColltnDt(df.newXMLGregorianCalendar("1999-01-01"));
		pmtInf.setCdtr(new PartyIdentification22());
		pmtInf.setCdtrAcct(new CashAccount8());
		pmtInf.setCdtrAgt(new FinancialInstitution2());
				
		//Payment Information
		pmtInf.getCdtr().setNm(job.getSEPAParam("src.name"));
				
		//Payment Information
		pmtInf.getCdtrAcct().setId(new AccountIdentification2());
		pmtInf.getCdtrAcct().getId().setIBAN(job.getSEPAParam("src.iban"));
				
		//Payment Information
		pmtInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentification4());
		pmtInf.getCdtrAgt().getFinInstnId().setBIC(job.getSEPAParam("src.bic"));
		
		
		//Payment Information - ChargeBearer
		pmtInf.setChrgBr(ChargeBearerType2Code.SLEV);
		
		
		//Payment Information - Credit Transfer Transaction Information
		ArrayList<DirectDebitTransactionInformation2> drctDbtTxInfs = (ArrayList<DirectDebitTransactionInformation2>) pmtInf.getDrctDbtTxInf();
		DirectDebitTransactionInformation2 drctDbtTxInf = new DirectDebitTransactionInformation2();
		drctDbtTxInfs.add(drctDbtTxInf);
		
		
		
		
		//FIXME: SEPA Mandant
		drctDbtTxInf.setDrctDbtTx(new DirectDebitTransaction4());
		drctDbtTxInf.getDrctDbtTx().setCdtrSchmeId(new PartyIdentification11()); 
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().setId(new PartyPrivate1());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().setPrvtId(new PersonIdentification4());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().setOthrId(new RestrictedIdentification2());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setId(job.getSEPAParam("src.iban"));
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setIdTp("SEPA");

				
		drctDbtTxInf.getDrctDbtTx().setMndtRltdInf(new MandateRelatedInformation4());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setMndtId(job.getSEPAParam("mandateid"));
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setDtOfSgntr(df.newXMLGregorianCalendar(job.getSEPAParam("manddateofsig"))); //FIXME: Wird das datum richtig geparst?
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInd(Boolean.valueOf(job.getSEPAParam("amendmandindic")));
		//FIXME: Ich glaube die AmdmntInfDtls wird nur gebraucht wenn AmdmntInd true ist. Wenn ja muss das hier in ne if
//		if(Boolean.valueOf(job.getSEPAParam("amendmandindic")) == true){
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setAmdmntInfDtls(new AmendmentInformationDetails4());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().setOrgnlDbtrAgt(new FinancialInstitution3());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification5());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().setPrtryId(new RestrictedIdentification1());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().getAmdmntInfDtls().getOrgnlDbtrAgt().getFinInstnId().getPrtryId().setId("SMNDA");
//		}
		
		
		
		
		
		//Payment Information - Credit Transfer Transaction Information - Payment Identification
		drctDbtTxInf.setPmtId(new PaymentIdentification1());
		drctDbtTxInf.getPmtId().setEndToEndId(job.getSEPAParam("endtoendid"));
		
		
		//Payment Information - Credit Transfer Transaction Information - Debitor
		drctDbtTxInf.setDbtr(new PartyIdentification23());
		drctDbtTxInf.getDbtr().setNm(job.getSEPAParam("dst.name"));
		
		//Payment Information - Credit Transfer Transaction Information - Debitor Account
		drctDbtTxInf.setDbtrAcct(new CashAccount8());
		drctDbtTxInf.getDbtrAcct().setId(new AccountIdentification2());
		drctDbtTxInf.getDbtrAcct().getId().setIBAN(job.getSEPAParam("dst.iban"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Agent
		drctDbtTxInf.setDbtrAgt(new FinancialInstitution2());
		drctDbtTxInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification4());
		drctDbtTxInf.getDbtrAgt().getFinInstnId().setBIC(job.getSEPAParam("dst.bic"));


		//Payment Information - Credit Transfer Transaction Information - Amount
		drctDbtTxInf.setInstdAmt(new EuroMax9Amount());
		drctDbtTxInf.getInstdAmt().setValue(new BigDecimal(job.getSEPAParam("btg.value")));
		
		//FIXME: Laut schema ist das ein String, daher nehm ich hier einfach mal btg.curr was ja ein Constraint der GVLastSEPA ist
		drctDbtTxInf.getInstdAmt().setCcy(job.getSEPAParam("btg.curr")); 
		

		//Payment Information - Credit Transfer Transaction Information - Usage
		//FIXME: momentan nur unstrukturierter Verwendungszweck! Vielleicht gibt es einen Parameter dafür? Dann kann man per If entscheiden
		drctDbtTxInf.setRmtInf(new RemittanceInformation3());
		drctDbtTxInf.getRmtInf().setUstrd(job.getSEPAParam("usage"));


		writeDocToOutputStream(doc, os);
	}

	private void writeDocToOutputStream(Document doc, ByteArrayOutputStream os) throws Exception{
		//Fertiges Dokument mittels JAXB marshallen (XML in den ByteArrayOutputStream schreiben)
		ObjectFactory of = new ObjectFactory();		
		JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(of.createDocument(doc), os);
	}
}
