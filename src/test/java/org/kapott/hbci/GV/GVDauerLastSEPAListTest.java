package org.kapott.hbci.GV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.GV_Result.GVRDauerLastList;
import org.kapott.hbci.GV_Result.GVRDauerLastList.Dauer;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet das Auslesen des Datums der naechsten Ausfuehrung aus der Antwort einer
 * SEPA-Dauerlastschriftliste.
 */
public class GVDauerLastSEPAListTest extends AbstractTest
{
    private static final String SEPA_DESCRIPTOR="urn:iso:std:iso:20022:tech:xsd:pain.008.001.02";

    /**
     * Das von der Bank gelieferte Datum wird als naechste Ausfuehrung uebernommen.
     */
    @Test
    public void testNextDateFromBank()
    {
        Dauer entry=parse(pain("2099-12-01"));

        assertEquals("2099-12-01",format(entry.nextdate));
    }

    /**
     * "1999-01-01" ist der in SEPA-Nachrichten uebliche Platzhalter fuer "kein Datum gesetzt".
     */
    @Test
    public void testUndefinedNextDate()
    {
        Dauer entry=parse(pain("1999-01-01"));

        assertNull(entry.nextdate);
    }

    /**
     * Baut ein pain.008.001.02-Dokument mit dem angegebenen Element ReqdColltnDt.
     *
     * @param reqdColltnDt der Inhalt des Elements ReqdColltnDt
     * @return das XML-Dokument
     */
    private static String pain(String reqdColltnDt)
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
            "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.008.001.02\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xsi:schemaLocation=\"urn:iso:std:iso:20022:tech:xsd:pain.008.001.02 pain.008.001.02.xsd\">"+
            "<CstmrDrctDbtInitn><GrpHdr><MsgId>000001</MsgId><CreDtTm>2025-10-27T00:00:00.000+01:00</CreDtTm>"+
            "<NbOfTxs>1</NbOfTxs><CtrlSum>1.00</CtrlSum><InitgPty><Nm>Maxine Mustermann</Nm></InitgPty></GrpHdr>"+
            "<PmtInf><PmtInfId>NOTPROVIDED</PmtInfId><PmtMtd>DD</PmtMtd><NbOfTxs>1</NbOfTxs><CtrlSum>1.00</CtrlSum>"+
            "<PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl><LclInstrm><Cd>CORE</Cd></LclInstrm><SeqTp>FRST</SeqTp></PmtTpInf>"+
            "<ReqdColltnDt>"+reqdColltnDt+"</ReqdColltnDt>"+
            "<Cdtr><Nm>Maxine Mustermann</Nm></Cdtr><CdtrAcct><Id><IBAN>DE12345678901234567890</IBAN></Id></CdtrAcct>"+
            "<CdtrAgt><FinInstnId><BICFI>DEUTDEDB277</BICFI></FinInstnId></CdtrAgt><ChrgBr>SLEV</ChrgBr>"+
            "<DrctDbtTxInf><PmtId><EndToEndId>NOTPROVIDED</EndToEndId></PmtId>"+
            "<InstdAmt Ccy=\"EUR\">1.00</InstdAmt>"+
            "<DrctDbtTx><MndtRltdInf><MndtId>MNDT1</MndtId><DtOfSgntr>2020-01-01</DtOfSgntr>"+
            "<AmdmntInd>false</AmdmntInd></MndtRltdInf>"+
            "<CdtrSchmeId><Id><PrvtId><Othr><Id>DE98ZZZ09999999999</Id>"+
            "<SchmeNm><Prtry>SEPA</Prtry></SchmeNm></Othr></PrvtId></Id></CdtrSchmeId></DrctDbtTx>"+
            "<DbtrAgt><FinInstnId><BICFI>DEUTDEDB277</BICFI></FinInstnId></DbtrAgt>"+
            "<Dbtr><Nm>Mustermann, Max Karl</Nm></Dbtr><DbtrAcct><Id><IBAN>DE12345678901234567890</IBAN></Id></DbtrAcct>"+
            "<RmtInf><Ustrd>Grund</Ustrd></RmtInf></DrctDbtTxInf></PmtInf></CstmrDrctDbtInitn></Document>";
    }

    private static Dauer parse(String pain)
    {
        try (HBCIHandler handler=handler())
        {
            GVDauerLastSEPAList job=new GVDauerLastSEPAList(handler);

            Properties properties=new Properties();
            properties.setProperty("GVRes.sepadescr",SEPA_DESCRIPTOR);
            properties.setProperty("GVRes.sepapain",pain);
            properties.setProperty("GVRes.orderid","4711");
            properties.setProperty("GVRes.DauerDetails.firstdate","2025-11-01");
            properties.setProperty("GVRes.DauerDetails.timeunit","M");
            properties.setProperty("GVRes.DauerDetails.turnus","1");
            properties.setProperty("GVRes.DauerDetails.execday","1");

            HBCIMsgStatus status=new HBCIMsgStatus();
            status.setData(properties);
            job.extractResults(status,"GVRes",0);

            GVRDauerLastList result=(GVRDauerLastList)job.getJobResult();
            assertEquals(1,result.getEntries().length);
            return result.getEntries()[0];
        }
    }

    private static HBCIHandler handler()
    {
        HBCIPassportPinTan passport=new HBCIPassportPinTan(new Object(),0);
        Properties bpd=new Properties();
        bpd.setProperty("Params.DauerLastSEPAListPar1.SegHead.code","HICDBS");
        passport.setBPD(bpd);
        passport.setUPD(new Properties());
        passport.setHBCIVersion("300");
        return new HBCIHandler("300",passport,true);
    }

    private static String format(Date value)
    {
        return value==null?null:new SimpleDateFormat("yyyy-MM-dd").format(value);
    }
}
