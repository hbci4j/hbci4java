
/*  $Id: GVUebSEPA.java,v 1.1 2011/05/04 22:37:54 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.GV;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.xml.XMLCreator2;
import org.kapott.hbci.xml.XMLData;

public class GVLastSEPAOrg
    extends HBCIJobImpl
{
    private Properties sepaParams;
    
    public static String getLowlevelName()
    {
        return "LastSEPA";
    }
    
    public GVLastSEPAOrg(HBCIHandler handler,String name)
    {
        super(handler,name,new HBCIJobResultImpl());
        this.sepaParams = new Properties();
    }

    public GVLastSEPAOrg(HBCIHandler handler)
    {
        this(handler,getLowlevelName());
        
        addConstraint("dst.bic",  "My.bic",  null, LogFilter.FILTER_MOST);
        addConstraint("dst.iban", "My.iban", null, LogFilter.FILTER_IDS);

        /* TODO: take SEPA descriptor from list of supported descriptors (BPD) */
        addConstraint("_sepadescriptor", "sepadescr", "sepade.pain.008.002.02.xsd", LogFilter.FILTER_NONE);
        addConstraint("_sepapain",       "sepapain",  null,                         LogFilter.FILTER_IDS);

        /* dummy constraints to allow an application to set these values. the
         * overriden setLowlevelParam() stores these values in a special structure
         * which is later used to create the SEPA pain document. */
        addConstraint("src.bic",   "sepa.src.bic",   null, LogFilter.FILTER_MOST);
        addConstraint("src.iban",  "sepa.src.iban",  null, LogFilter.FILTER_IDS);
        addConstraint("src.name",  "sepa.src.name",  null, LogFilter.FILTER_IDS);
        addConstraint("src.MandateId",  "sepa.src.MandateId",  null, LogFilter.FILTER_IDS);
        addConstraint("src.DtOfSgntr",  "sepa.src.DtOfSgntr",  null, LogFilter.FILTER_IDS);
        addConstraint("dst.bic",   "sepa.dst.bic",   null, LogFilter.FILTER_MOST);
        addConstraint("dst.iban",  "sepa.dst.iban",  null, LogFilter.FILTER_IDS);
        addConstraint("dst.name",  "sepa.dst.name",  null, LogFilter.FILTER_IDS);
        addConstraint("dst.CdtrIdentifier",  "sepa.dst.CdtrIdentifier",  null, LogFilter.FILTER_IDS);
        
        addConstraint("btg.value", "sepa.btg.value", null, LogFilter.FILTER_NONE);
        addConstraint("btg.curr",  "sepa.btg.curr",  "EUR", LogFilter.FILTER_NONE);
        addConstraint("usage",     "sepa.usage",     null, LogFilter.FILTER_NONE);
    }
    
    
    /* This is needed to "redirect" the sepa values. They dont have to stored 
     * directly in the message, but have to go into the SEPA document which will
     * by created later (in verifyConstraints()) */
    protected void setLowlevelParam(String key, String value)
    {
        String intern=getName()+".sepa.";
        
        if (key.startsWith(intern)) {
            String realKey=key.substring(intern.length());
            this.sepaParams.setProperty(realKey, value);
            HBCIUtils.log("setting SEPA param "+realKey+" = "+value, HBCIUtils.LOG_DEBUG);
        } else {
            super.setLowlevelParam(key, value);
        }
    }
    
    
    /* This is needed for verifyConstraints(). Because verifyConstraints() tries
     * to read the lowlevel-values for each constraint, the lowlevel-values for
     * sepa.xxx would always be empty (because they do not exist in hbci messages).
     * So we read the sepa lowlevel-values from the special sepa structure instead
     * from the lowlevel params for the message */
    public String getLowlevelParam(String key)
    {
        String result;
        
        String intern=getName()+".sepa.";        
        if (key.startsWith(intern)) {
            String realKey=key.substring(intern.length());
            result=getSEPAParam(realKey);
        } else {
            result=super.getLowlevelParam(key);
        }
        
        return result;
    }

    
    protected String getSEPAMessageId()
    {
        String result=getSEPAParam("messageId");
        if (result==null) {
            Date now=new Date();
            result=now.getTime() + "-" + getMainPassport().getUserId();
            result=result.substring(0, Math.min(result.length(),35));
            setSEPAParam("messageId", result);
        }
        return result;
    }
    
    
    protected String createSEPATimestamp()
    {
        Date             now=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return format.format(now);
    }
    
    
    protected void createSEPAFromParams()
    {
        // open SEPA descriptor and create an XML-Creator using it
        /* TODO: load correct schema files depending on the SEPA descriptor set
         * above, depending on the supported SEPA descriptors (BPD) */
        InputStream f=this.getClass().getClassLoader().getResourceAsStream("pain.008.002.02.xsd");
        XMLCreator2 creator=new XMLCreator2(f);

        int size = 1;	
        /* TODO: Mehr als Eintrag
        float sumvalue = 0;
        for (int i=0; i<size ; ++i)
        	sumvalue += btg.value;
        	*/
        
        // define data to be filled into SEPA document
        XMLData xmldata=new XMLData();
        xmldata.setValue("Document/pain.008.002.02/GrpHdr/MsgId",                              getSEPAMessageId());
        xmldata.setValue("Document/pain.008.002.02/GrpHdr/CreDtTm",                            createSEPATimestamp());
        xmldata.setValue("Document/pain.008.002.02/GrpHdr/NbOfTxs",                            "1");
        // TODO: Gesamtbetrag wenn size > 1 bei mehr als einem Eintrag
        xmldata.setValue("Document/pain.008.002.02/GrpHdr/CtrlSum",                            getSEPAParam("btg.value"));
        xmldata.setValue("Document/pain.008.002.02/GrpHdr/InitgPty/Nm",                        getSEPAParam("dst.name"));

        xmldata.setValue("Document/pain.008.002.02/PmtInf/PmntInfId",              getSEPAParam("dst.CdtrIdentifier"));
        xmldata.setValue("Document/pain.008.002.02/PmtInf/PmntInfMtd",              "DD");
        // TODO: Wert auf Anzahl bei mehr als einem Eintrag
        xmldata.setValue("Document/pain.008.002.02/PmtInf/NbOfTxs",                            "1");
        // TODO: Gesamtbetrag wenn size > 1
        xmldata.setValue("Document/pain.008.002.02/PmtInf/CtrlSum",                            getSEPAParam("btg.value"));

        // Payment Type Id: Basislastschrift
        xmldata.setValue("Document/pain.008.002.02/PmtInf/PmtTpInf/SvcLvl/Cd",                 "SEPA");
        // TODO: wenn Kunde kein Verbraucher, muss hier B2B stehen, für die Business-Kunden
        xmldata.setValue("Document/pain.008.002.02/PmtInf/PmtTpInf/LclInstrm/Cd",              "CORE");
        // TODO: Nur bei einmaliger Einreichung: sonst FRST bei erster, RCUR bei allen weiteren.
        xmldata.setValue("Document/pain.008.002.02/PmtInf/PmtTpInf/SeqTp",              	   "OOFF");
        xmldata.setValue("Document/pain.008.002.02/PmtInf/ReqdColltnDt",                        "1999-01-01"); // hart kodiert

        // Angaben zum Empfänger der Lastschrift
        xmldata.setValue("Document/pain.008.002.02/PmtInf/Cdtr/Nm",                getSEPAParam("dst.name"));
        xmldata.setValue("Document/pain.008.002.02/PmtInf/CdtrAgt/FinInstnId/BIC", getSEPAParam("dst.bic"));
        xmldata.setValue("Document/pain.008.002.02/PmtInf/CdtrAcct/Id/IBAN",       getSEPAParam("dst.iban"));
        xmldata.setValue("Document/pain.008.002.02/PmtInf/ChrgBr",                "SLEV");
     // NEU Gläubiger-Identifikationsnummer: muss der Einreicher der Lastschrift angeben
        xmldata.setValue("Document/pain.008.002.02/PmtInf/CdtrSchmeId/Id/PrvtId/Othr/Id",       getSEPAParam("dst.CdtrIdentifier")); 
        xmldata.setValue("Document/pain.008.002.02/PmtInf/CdtrSchmeId/Id/PrvtId/Othr/SchmeNm/Prtry",       "SEPA");
        
        // for (int i=0; i<size ; ++i) TODO: Mehr als Eintrag
        {
        	// Angaben zum Schuldner 
	        xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/InstdAmt",           getSEPAParam("btg.value"));
	        xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/InstdAmt:Ccy",       getSEPAParam("btg.curr"));
	     // NEU Mandatsreferenznummer, wird vom Einreicher jedem Kunden zugewiesen
	        xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/DrctDbtTx/MndtRltdInf/MndtId",       
	        		getSEPAParam("src.MandateId"));
	     // NEU Datum der Einzugsermächtigung
	        xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/DrctDbtTx/MndtRltdInf/DtOfSgntr",       
	        		getSEPAParam("src.DtOfSgntr"));
	     // NEU laut [ksk] optional 
	        xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/DrctDbtTx/MndtRltdInf/AmdmntInd",       "false");
	
	        xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/Dbtr/Nm",                            
	        		getSEPAParam("src.name"));
			xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/DbtrAgt/FinInstnId/BIC",             
					getSEPAParam("src.bic"));
			xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/DbtrAcct/Id/IBAN",                   
					getSEPAParam("src.iban"));
		// TODO: bei mehreren Einträgen Unterschiedlich für alle
			xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/PmtId/EndToEndId",           getSEPAMessageId());
		// TODO: muss Richtlinien entsprechen
			xmldata.setValue("Document/pain.008.002.02/PmtInf/DrctDbtTxInf/RmtInf/Ustrd",           getSEPAParam("usage"));
        }
        
        
        /*
         * 
         * Quellen:
         * Beispiel-Datei: http://www.ebics.de/fileadmin/unsecured/anlage3/anlage3_archiv/Anlage3_V2_6_gueltigBis2013-11-03.zip
         * [ksk] https://www.ksk-koeln.de/erstellung-einer-sepa-lastschriftdatei-aus-dta-dateien.xlsx?forced=true
         * [bayernlb]  http://www.bayernlb.de/internet/media/internet_4/de_1/downloads_5/0800_financial_office_it_operations_5/4200_1/sepa_5/sepaaktuell/Newsletter_SEPA_006.pdf
        <PmtInf>
		!<PmtInfId>Payment-ID</PmtInfId>
		!<PmtMtd>DD</PmtMtd>
		!<NbOfTxs>2</NbOfTxs>
		!<CtrlSum>6655.86</CtrlSum>
		!<PmtTpInf>
		!	<SvcLvl>
		!		<Cd>SEPA</Cd>
		!	</SvcLvl>
		!	<LclInstrm>
		!		<Cd>CORE</Cd>
		!	</LclInstrm>
		!	<SeqTp>FRST</SeqTp>
		!</PmtTpInf>
		!<ReqdColltnDt>2010-12-03</ReqdColltnDt>
		!<Cdtr>
		!	<Nm>Creditor Name</Nm>
		!</Cdtr>
		!<CdtrAcct>
		!	<Id>
		!		<IBAN>DE87200500001234567890</IBAN>
		!	</Id>
		!</CdtrAcct>
		!<CdtrAgt>
		!	<FinInstnId>
		!		<BIC>BANKDEFFXXX</BIC>
		!	</FinInstnId>
		!</CdtrAgt>
		<ChrgBr>SLEV</ChrgBr>
		!<CdtrSchmeId>		<Id>
		!		<PrvtId>
		!			<Othr>
		!				<Id>DE00ZZZ00099999999</Id>
		!				<SchmeNm>
		!					<Prtry>SEPA</Prtry>
		!				</SchmeNm>
		!			</Othr>
		!		</PrvtId>
		!	</Id>
		!</CdtrSchmeId>
		<DrctDbtTxInf>
			<PmtId>
				<EndToEndId>OriginatorID1234</EndToEndId> 
				// ist optional, erstmal weggelassen [bayernlb]
			</PmtId>
		!	<InstdAmt Ccy="EUR">6543.14</InstdAmt>
		!	<DrctDbtTx>
		!		<MndtRltdInf>
		!			<MndtId>Mandate-Id</MndtId>
		!			<DtOfSgntr>2010-11-20</DtOfSgntr>
		!			! Dtls optional wenn auf false [ksk], weggelassen
		!			<AmdmntInd>true</AmdmntInd>
		!			<AmdmntInfDtls>
		!				<OrgnlCdtrSchmeId>
		!					<Nm>Original Creditor Name</Nm>
		!					<Id>
		!						<PrvtId>
		!							<Othr>
		!								<Id>AA00ZZZOriginalCreditorID</Id>
		!								<SchmeNm>
		!									<Prtry>SEPA</Prtry>
		!								</SchmeNm>
		!							</Othr>
		!						</PrvtId>
		!					</Id>
		!				</OrgnlCdtrSchmeId>
		!			</AmdmntInfDtls>
		!		</MndtRltdInf>
		!	</DrctDbtTx>
		!	<DbtrAgt>
		!		<FinInstnId>
		!			<BIC>SPUEDE2UXXX</BIC>
		!		</FinInstnId>
		!	</DbtrAgt>
		!	<Dbtr>
		!		<Nm>Debtor Name</Nm>
		!	</Dbtr>
		!	<DbtrAcct>
		!		<Id>
		!			<IBAN>DE21500500009876543210</IBAN>
		!		</Id>
		!	</DbtrAcct>
			<UltmtDbtr>
				<Nm>Ultimate Debtor Name</Nm>
			</UltmtDbtr>
			<RmtInf>
				<Ustrd>Unstructured Remittance Information</Ustrd>
			</RmtInf>
		</DrctDbtTxInf>
		<DrctDbtTxInf>
			<PmtId>
				<EndToEndId>OriginatorID1235</EndToEndId>
			</PmtId>
			<InstdAmt Ccy="EUR">112.72</InstdAmt>
			<DrctDbtTx>
				<MndtRltdInf>
					<MndtId>Other-Mandate-Id</MndtId>
					<DtOfSgntr>2010-11-20</DtOfSgntr>
					<AmdmntInd>false</AmdmntInd>
				</MndtRltdInf>
			</DrctDbtTx>
			<DbtrAgt>
				<FinInstnId>
					<BIC>SPUEDE2UXXX</BIC>
				</FinInstnId>
			</DbtrAgt>
			<Dbtr>
				<Nm>Other Debtor Name</Nm>
			</Dbtr>
			<DbtrAcct>
				<Id>
					<IBAN>DE21500500001234567897</IBAN>
				</Id>
			</DbtrAcct>
			<UltmtDbtr>
				<Nm>Ultimate Debtor Name</Nm>
			</UltmtDbtr>
			<RmtInf>
				<Ustrd>Unstructured Remittance Information</Ustrd>
			</RmtInf>
		</DrctDbtTxInf>
	</PmtInf>
			
			*/
        // create SEPA document
        ByteArrayOutputStream o=new ByteArrayOutputStream();
        creator.createXMLFromSchemaAndData(xmldata, o);
        
        // store SEPA document as parameter
        try {
            setParam("_sepapain", "B"+o.toString("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void verifyConstraints()
    {
        // creating SEPA document and storing it in _sepapain
        createSEPAFromParams();
        
        // verify all constraints
        super.verifyConstraints();
        
        // TODO: checkIBANCRC
    }
    
    protected void setSEPAParam(String name, String value)
    {
        this.sepaParams.setProperty(name, value);
    }
    
    protected String getSEPAParam(String name)
    {
        return this.sepaParams.getProperty(name);
    }
}
