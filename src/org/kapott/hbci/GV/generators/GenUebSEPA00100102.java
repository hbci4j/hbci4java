package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.xml.datatype.DatatypeFactory;

import org.kapott.hbci.sepa.PainVersion;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.AccountIdentification2;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.AmountType3;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.CashAccount8;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.ChargeBearerType2Code;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.CreditTransferTransactionInformation2;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.EuroMax9Amount;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.FinancialInstitution2;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.FinancialInstitutionIdentification4;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.GroupHeader20;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.Grouping2Code;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.Pain00100102;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PartyIdentification20;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PartyIdentification21;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PartyIdentification23;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PaymentIdentification1;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PaymentInstructionInformation4;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PaymentMethod5Code;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.PaymentTypeInformation7;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.RemittanceInformation3;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.ServiceLevel3Code;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.ServiceLevel4;


/**
 * SEPA-Generator fuer pain.001.001.02.
 */
public class GenUebSEPA00100102 extends AbstractSEPAGenerator
{
    /**
     * @see org.kapott.hbci.GV.generators.AbstractSEPAGenerator#getPainVersion()
     */
    @Override
    public PainVersion getPainVersion()
    {
        return PainVersion.PAIN_001_001_02;
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
		
		
		//Pain00100102
		doc.setPain00100102(new Pain00100102());
		
		
		doc.getPain00100102().setGrpHdr(new GroupHeader20());
				
		//Group Header
		doc.getPain00100102().getGrpHdr().setMsgId(sepaParams.getProperty("sepaid"));
		doc.getPain00100102().getGrpHdr().setCreDtTm(df.newXMLGregorianCalendar(sdtf.format(now)));
	    doc.getPain00100102().getGrpHdr().setNbOfTxs("1");
        doc.getPain00100102().getGrpHdr().setGrpg(Grouping2Code.GRPD);
		doc.getPain00100102().getGrpHdr().setInitgPty(new PartyIdentification20());
		doc.getPain00100102().getGrpHdr().getInitgPty().setNm(sepaParams.getProperty("src.name"));
		
		
		//Payment Information
		PaymentInstructionInformation4 pmtInf = new PaymentInstructionInformation4();
		doc.getPain00100102().setPmtInf(pmtInf);
		
		pmtInf.setPmtInfId(sepaParams.getProperty("sepaid")); 
		pmtInf.setPmtMtd(PaymentMethod5Code.TRF);
		
		// Payment Type Information
		pmtInf.setPmtTpInf(new PaymentTypeInformation7());
		pmtInf.getPmtTpInf().setSvcLvl(new ServiceLevel4());
		pmtInf.getPmtTpInf().getSvcLvl().setCd(ServiceLevel3Code.SEPA);
		
		String date = sepaParams.getProperty("date");
		if(date == null) date = "1999-01-01";
		pmtInf.setReqdExctnDt(df.newXMLGregorianCalendar(date));
		pmtInf.setDbtr(new PartyIdentification23());
		pmtInf.setDbtrAcct(new CashAccount8());
		pmtInf.setDbtrAgt(new FinancialInstitution2());
		
		
		//Payment Information - Debtor
		pmtInf.getDbtr().setNm(sepaParams.getProperty("src.name"));
		
		
		//Payment Information - DebtorAccount
		pmtInf.getDbtrAcct().setId(new AccountIdentification2());
		pmtInf.getDbtrAcct().getId().setIBAN(sepaParams.getProperty("src.iban"));
		
		
		//Payment Information - DebtorAgent
		pmtInf.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification4());
		pmtInf.getDbtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("src.bic"));
		
		
		//Payment Information - ChargeBearer
		pmtInf.setChrgBr(ChargeBearerType2Code.SLEV);
		
		
		//Payment Information - Credit Transfer Transaction Information
		ArrayList<CreditTransferTransactionInformation2> cdtTrxTxInfs = (ArrayList<CreditTransferTransactionInformation2>) pmtInf.getCdtTrfTxInf();
		CreditTransferTransactionInformation2 cdtTrxTxInf = new CreditTransferTransactionInformation2();
		cdtTrxTxInfs.add(cdtTrxTxInf);
		
		
		//Payment Information - Credit Transfer Transaction Information - Payment Identification
		cdtTrxTxInf.setPmtId(new PaymentIdentification1());
		cdtTrxTxInf.getPmtId().setEndToEndId(sepaParams.getProperty("endtoendid"));
		
		
		//Payment Information - Credit Transfer Transaction Information - Creditor
		cdtTrxTxInf.setCdtr(new PartyIdentification21());
		cdtTrxTxInf.getCdtr().setNm(sepaParams.getProperty("dst.name"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Account
		cdtTrxTxInf.setCdtrAcct(new CashAccount8());
		cdtTrxTxInf.getCdtrAcct().setId(new AccountIdentification2());
		cdtTrxTxInf.getCdtrAcct().getId().setIBAN(sepaParams.getProperty("dst.iban"));
		
		//Payment Information - Credit Transfer Transaction Information - Creditor Agent
		cdtTrxTxInf.setCdtrAgt(new FinancialInstitution2());
		cdtTrxTxInf.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentification4());
		cdtTrxTxInf.getCdtrAgt().getFinInstnId().setBIC(sepaParams.getProperty("dst.bic"));


		//Payment Information - Credit Transfer Transaction Information - Amount
		cdtTrxTxInf.setAmt(new AmountType3());
		cdtTrxTxInf.getAmt().setInstdAmt(new EuroMax9Amount());
		cdtTrxTxInf.getAmt().getInstdAmt().setValue(new BigDecimal(sepaParams.getProperty("btg.value")));
		
		cdtTrxTxInf.getAmt().getInstdAmt().setCcy("EUR"); 

		//Payment Information - Credit Transfer Transaction Information - Usage
		cdtTrxTxInf.setRmtInf(new RemittanceInformation3());
		cdtTrxTxInf.getRmtInf().setUstrd(sepaParams.getProperty("usage"));
		
        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
	}
}
