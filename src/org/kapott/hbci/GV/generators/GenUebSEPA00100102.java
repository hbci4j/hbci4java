package org.kapott.hbci.GV.generators;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.AccountIdentification2;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.AmountType3;
import org.kapott.hbci.sepa.jaxb.pain_001_001_02.CashAccount8;
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

public class GenUebSEPA00100102 implements ISEPAGenerator{

	@Override
	public void generate(HBCIJob job, OutputStream os) {
		// TODO Auto-generated method stub

	}

	 private String viaJAXB() throws Exception
	  {
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	    DatatypeFactory df = DatatypeFactory.newInstance();

//	    Document doc = new Document();
	    ObjectFactory factory = new ObjectFactory();
	    Document doc = factory.createDocument();
	    Pain00100102 pain = new Pain00100102();
	    GroupHeader20 grpHdr = new GroupHeader20();
	    PartyIdentification20 initgPty = new PartyIdentification20();
	    PaymentInstructionInformation4 pmtInf = new PaymentInstructionInformation4();
	    PartyIdentification23 dbtr = new PartyIdentification23();
	    FinancialInstitution2 dbtrAgt = new FinancialInstitution2();
	    FinancialInstitutionIdentification4 finInstnId = new FinancialInstitutionIdentification4();
	    CashAccount8 dbtrAcct = new CashAccount8();
	    AccountIdentification2 id = new AccountIdentification2();
	    CreditTransferTransactionInformation2 cdtTrfTxInf = new CreditTransferTransactionInformation2();
	    PartyIdentification21 cdtr = new PartyIdentification21();
	    FinancialInstitution2 cdtrAgt = new FinancialInstitution2();
	    FinancialInstitutionIdentification4 finInstnId2 = new FinancialInstitutionIdentification4();
	    CashAccount8 cdtrAcct = new CashAccount8();
	    AccountIdentification2 id2 = new AccountIdentification2();
	    AmountType3 amt = new AmountType3();
	    EuroMax9Amount instdAmt = new EuroMax9Amount();
	    RemittanceInformation3 rmtInf = new RemittanceInformation3();
	    PaymentIdentification1 pmtId = new PaymentIdentification1();
	    PaymentTypeInformation7 pmtTpInf = new PaymentTypeInformation7();
	    ServiceLevel4 svcLvl = new ServiceLevel4();
	    svcLvl.setCd(ServiceLevel3Code.SEPA);
	    pmtTpInf.setSvcLvl(svcLvl);

	    doc.setPain00100102(pain);
	    pain.setGrpHdr(grpHdr);
	    pain.setPmtInf(pmtInf);
	    grpHdr.setInitgPty(initgPty);
	    pmtInf.setDbtr(dbtr);
	    pmtInf.setDbtrAgt(dbtrAgt);
	    pmtInf.setDbtrAcct(dbtrAcct);
	    pmtInf.getCdtTrfTxInf().add(cdtTrfTxInf);

	    dbtrAgt.setFinInstnId(finInstnId);
	    dbtrAcct.setId(id);
	    cdtTrfTxInf.setCdtr(cdtr);
	    cdtTrfTxInf.setCdtrAgt(cdtrAgt);
	    cdtrAgt.setFinInstnId(finInstnId2);
	    cdtTrfTxInf.setCdtrAcct(cdtrAcct);
	    cdtrAcct.setId(id2);
	    cdtTrfTxInf.setAmt(amt);
	    amt.setInstdAmt(instdAmt);
	    cdtTrfTxInf.setRmtInf(rmtInf);
	    cdtTrfTxInf.setPmtId(pmtId);

	    grpHdr.setMsgId("Message-ID-4711");

	    GregorianCalendar cal = new GregorianCalendar();
	    cal.setTime(format.parse("2008-05-11T09:30:47.000Z"));
	    grpHdr.setCreDtTm(df.newXMLGregorianCalendar(cal));
	    grpHdr.setGrpg(Grouping2Code.GRPD);

	    grpHdr.setNbOfTxs("1");
	    initgPty.setNm("Name 1");
	    dbtr.setNm("Name 2");
	    finInstnId.setBIC("MY-BIC");
	    id.setIBAN("MY-IBAN");

	    cdtr.setNm("Der Empfaenger");
	    finInstnId2.setBIC("OTHER-BIC");
	    id2.setIBAN("OTHER-IBAN");
	    instdAmt.setValue(new BigDecimal("1.23"));
	    instdAmt.setCcy("EUR");
	    rmtInf.setUstrd("Verwendungszweck");
	    pmtId.setEndToEndId("NOTPROVIDED");

	    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
	    cal.setTime(format2.parse("1999-01-01"));
	    pmtInf.setPmtMtd(PaymentMethod5Code.TRF);
	    pmtInf.setPmtTpInf(pmtTpInf);
	    XMLGregorianCalendar cal2 = df.newXMLGregorianCalendar(cal);
	    pmtInf.setReqdExctnDt(cal2);

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	    Schema schema = schemaFactory.newSchema(new File("src/pain.001.001.02.xsd"));

	    JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
	    Marshaller marshaller = jaxbContext.createMarshaller();
//	    marshaller.setSchema(schema);
	    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    marshaller.marshal(factory.createDocument(doc) ,bos);

	    return bos.toString("UTF-8");
	  }
}
