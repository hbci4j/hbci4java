/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/hbci4java/ddv/PCSCTest.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/11/24 21:59:37 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.sepa;

import hbci4java.AbstractTest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.Document;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.GroupHeaderSDD;
import org.kapott.hbci.sepa.jaxb.pain_008_003_02.PartyIdentificationSEPA1;
import org.kapott.hbci.xml.XMLCreator2;
import org.kapott.hbci.xml.XMLData;

/**
 * Testet das Generieren von Pain XML-Dateien.
 */
public class TestPainGenLastSEPA extends AbstractTest
{
  /**
   * Erstellt eine Pain-Nachricht mit dem alten XMLCreator und mit JAXB und vergleicht das Ergebnis.
   * @throws Exception
   */
  @Test
  public void test001() throws Exception
  {
    String sNew = this.viaJAXB();
    System.out.println(sNew);
  }

  private String viaJAXB() throws Exception
  {
    
	Document doc = new Document();
	
	
	setPaymentInstructionInformationSSD(doc);
       

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = schemaFactory.newSchema(new File("src/pain.001.001.02.xsd"));

    JAXBContext jaxbContext = JAXBContext.newInstance(Document.class);
    Marshaller marshaller = jaxbContext.createMarshaller();
//    marshaller.setSchema(schema);
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.marshal(doc,bos);

    return bos.toString("UTF-8");
  }

  	private void setPaymentInstructionInformationSSD(Document doc) {
  		
	}
	
	private void setGroupHeaderSSD(Document doc) throws DatatypeConfigurationException, ParseException {
		GroupHeaderSDD header = new GroupHeaderSDD();
		
		header.setMsgId("Message-ID");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	    DatatypeFactory df = DatatypeFactory.newInstance();
		GregorianCalendar cal = new GregorianCalendar();
	    cal.setTime(format.parse("2008-05-11T09:30:47.000Z"));
	    header.setCreDtTm(df.newXMLGregorianCalendar(cal));
			    
		header.setNbOfTxs("1");
		
		PartyIdentificationSEPA1 initgPty = new PartyIdentificationSEPA1();
		initgPty.setNm("Initiator Name");
		header.setInitgPty(initgPty);
		
		
	}

private String viaXMLCreator2() throws Exception
  {
    FileInputStream f = new FileInputStream("src/pain.001.001.02.xsd");
    XMLCreator2 creator = new XMLCreator2(f);

    XMLData xmldata=new XMLData();
    xmldata.setValue("Document/pain.001.001.02/GrpHdr/MsgId",                              "Message-ID-4711");
    xmldata.setValue("Document/pain.001.001.02/GrpHdr/CreDtTm",                            "2008-05-11T09:30:47.000Z");
    xmldata.setValue("Document/pain.001.001.02/GrpHdr/NbOfTxs",                            "1");
    xmldata.setValue("Document/pain.001.001.02/GrpHdr/InitgPty/Nm",                        "Name 1");

    xmldata.setValue("Document/pain.001.001.02/PmtInf/Dbtr/Nm",                            "Name 2");
    xmldata.setValue("Document/pain.001.001.02/PmtInf/DbtrAgt/FinInstnId/BIC",             "MY-BIC");
    xmldata.setValue("Document/pain.001.001.02/PmtInf/DbtrAcct/Id/IBAN",                   "MY-IBAN");

    xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/Cdtr/Nm",                "Der Empfaenger");
    xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/CdtrAgt/FinInstnId/BIC", "OTHER-BIC");
    xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/CdtrAcct/Id/IBAN",       "OTHER-IBAN");

    xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/Amt/InstdAmt",           "1.23");
    xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/Amt/InstdAmt:Ccy",       "EUR");
    xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/RmtInf/Ustrd",           "Verwendungszweck");
    xmldata.setValue("Document/pain.001.001.02/PmtInf/CdtTrfTxInf/PmtId/EndToEndId",       "NOTPROVIDED");

    xmldata.setValue("Document/pain.001.001.02/PmtInf/ReqdExctnDt",                        "1999-01-01"); // hart kodiert

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    creator.createXMLFromSchemaAndData(xmldata, bos);
    return bos.toString("UTF-8");
  }
}
