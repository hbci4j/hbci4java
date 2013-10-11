package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.xml.datatype.DatatypeFactory;

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
		doc.getPain00800101().getGrpHdr().setMsgId(sepaParams.getProperty("sepaid"));
		doc.getPain00800101().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
		doc.getPain00800101().getGrpHdr().setNbOfTxs("1");
        doc.getPain00800101().getGrpHdr().setCtrlSum(new BigDecimal(sepaParams.getProperty("btg.value")));
        doc.getPain00800101().getGrpHdr().setGrpg(Grouping2Code.GRPD);

		doc.getPain00800101().getGrpHdr().setInitgPty(new PartyIdentification20());
		doc.getPain00800101().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));
		
		//Payment Information 
		PaymentInstructionInformation5 pmtInf = new PaymentInstructionInformation5();
		doc.getPain00800101().setPmtInf(pmtInf);
				
		pmtInf.setPmtInfId(sepaParams.getProperty("sepaid")); 
		pmtInf.setPmtMtd(PaymentMethod2Code.DD);
		
		pmtInf.setReqdColltnDt(df.newXMLGregorianCalendar("1999-01-01"));
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
		DirectDebitTransactionInformation2 drctDbtTxInf = new DirectDebitTransactionInformation2();
		drctDbtTxInfs.add(drctDbtTxInf);
		
		
		drctDbtTxInf.setDrctDbtTx(new DirectDebitTransaction4());
		drctDbtTxInf.getDrctDbtTx().setCdtrSchmeId(new PartyIdentification11()); 
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().setId(new PartyPrivate1());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().setPrvtId(new PersonIdentification4());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().setOthrId(new RestrictedIdentification2());
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setId(sepaParams.getProperty("creditorid"));
		drctDbtTxInf.getDrctDbtTx().getCdtrSchmeId().getId().getPrvtId().getOthrId().setIdTp("SEPA");

				
		drctDbtTxInf.getDrctDbtTx().setMndtRltdInf(new MandateRelatedInformation4());
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setMndtId(sepaParams.getProperty("mandateid"));
		drctDbtTxInf.getDrctDbtTx().getMndtRltdInf().setDtOfSgntr(df.newXMLGregorianCalendar(sepaParams.getProperty("manddateofsig"))); //FIXME: Wird das datum richtig geparst?

        boolean amend = Boolean.valueOf(sepaParams.getProperty("amendmandindic"));
		
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
		drctDbtTxInf.getPmtId().setEndToEndId(sepaParams.getProperty("endtoendid"));
		
		
		//Payment Information - Credit Transfer Transaction Information - Debitor
		drctDbtTxInf.setDbtr(new PartyIdentification23());
		drctDbtTxInf.getDbtr().setNm(sepaParams.getProperty("dst.name"));
		
		//Payment Information - Credit Transfer Transaction Information - Debitor Account
		drctDbtTxInf.setDbtrAcct(new CashAccount8());
		drctDbtTxInf.getDbtrAcct().setId(new AccountIdentification2());
		drctDbtTxInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty("dst.iban"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Agent
		drctDbtTxInf.setDbtrAgt(new FinancialInstitution2());
		drctDbtTxInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification4());
		drctDbtTxInf.getDbtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("dst.bic"));


		//Payment Information - Credit Transfer Transaction Information - Amount
		drctDbtTxInf.setInstdAmt(new EuroMax9Amount());
		drctDbtTxInf.getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty("btg.value")));
		
		drctDbtTxInf.getInstdAmt().setCcy(sepaParams.getProperty("btg.curr")); 
		

		//Payment Information - Credit Transfer Transaction Information - Usage
		drctDbtTxInf.setRmtInf(new RemittanceInformation3());
		drctDbtTxInf.getRmtInf().setUstrd(sepaParams.getProperty("usage"));

        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
	}
}
