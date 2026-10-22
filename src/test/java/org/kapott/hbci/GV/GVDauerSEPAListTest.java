package org.kapott.hbci.GV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.GV_Result.GVRDauerList;
import org.kapott.hbci.GV_Result.GVRDauerList.Dauer;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet das Auslesen des Datums der naechsten Ausfuehrung und der Uhrzeit der
 * Ausfuehrung aus der Antwort einer SEPA-Dauerauftragsliste.
 */
public class GVDauerSEPAListTest extends AbstractTest
{
    private static final String SEPA_DESCRIPTOR="urn:iso:std:iso:20022:tech:xsd:pain.001.001.09";

    /**
     * Das von der Bank gelieferte Datum wird als naechste Ausfuehrung uebernommen.
     */
    @Test
    public void testNextDateFromBank()
    {
        Dauer entry=parse(pain("<Dt>2099-12-01</Dt>"));

        assertEquals("2099-12-01",format(entry.nextdate));
    }

    /**
     * Manche Banken liefern das Datum als Zeitstempel statt als Datum.
     */
    @Test
    public void testNextDateFromTimestamp()
    {
        Dauer entry=parse(pain("<DtTm>2099-12-01T00:00:00</DtTm>"));

        assertEquals("2099-12-01",format(entry.nextdate));
    }

    /**
     * "1999-01-01" ist der in SEPA-Nachrichten uebliche Platzhalter fuer "kein Datum gesetzt".
     */
    @Test
    public void testUndefinedNextDate()
    {
        Dauer entry=parse(pain("<Dt>1999-01-01</Dt>"));

        assertNull(entry.nextdate);
    }

    /**
     * Die Uhrzeit der Ausfuehrung wird aus dem Segment uebernommen.
     */
    @Test
    public void testExecTime()
    {
        Dauer entry=parse(pain("<Dt>2099-12-01</Dt>"));

        assertEquals("12:00:00",entry.exectime);
    }

    /**
     * Baut ein pain.001.001.09-Dokument mit dem angegebenen Element ReqdExctnDt.
     *
     * @param reqdExctnDt der Inhalt des Elements ReqdExctnDt
     * @return das XML-Dokument
     */
    private static String pain(String reqdExctnDt)
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
            "<Document xmlns=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.09\" "+
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
            "xsi:schemaLocation=\"urn:iso:std:iso:20022:tech:xsd:pain.001.001.09 pain.001.001.09.xsd\">"+
            "<CstmrCdtTrfInitn><GrpHdr><MsgId>000001</MsgId><CreDtTm>2025-10-27T00:00:00.000+01:00</CreDtTm>"+
            "<NbOfTxs>1</NbOfTxs><CtrlSum>1.00</CtrlSum><InitgPty><Nm>Maxine Mustermann</Nm></InitgPty></GrpHdr>"+
            "<PmtInf><PmtInfId>NOTPROVIDED</PmtInfId><PmtMtd>TRF</PmtMtd><NbOfTxs>1</NbOfTxs><CtrlSum>1.00</CtrlSum>"+
            "<ReqdExctnDt>"+reqdExctnDt+"</ReqdExctnDt>"+
            "<Dbtr><Nm>Maxine Mustermann</Nm></Dbtr><DbtrAcct><Id><IBAN>DE12345678901234567890</IBAN></Id></DbtrAcct>"+
            "<DbtrAgt><FinInstnId><BICFI>DEUTDEDB277</BICFI></FinInstnId></DbtrAgt><ChrgBr>SLEV</ChrgBr>"+
            "<CdtTrfTxInf><PmtId><EndToEndId>NOTPROVIDED</EndToEndId></PmtId>"+
            "<Amt><InstdAmt Ccy=\"EUR\">1.00</InstdAmt></Amt>"+
            "<CdtrAgt><FinInstnId><BICFI>DEUTDEDB277</BICFI></FinInstnId></CdtrAgt>"+
            "<Cdtr><Nm>Mustermann, Max Karl</Nm></Cdtr><CdtrAcct><Id><IBAN>DE12345678901234567890</IBAN></Id></CdtrAcct>"+
            "<RmtInf><Ustrd>Uebertrag</Ustrd></RmtInf></CdtTrfTxInf></PmtInf></CstmrCdtTrfInitn></Document>";
    }

    private static Dauer parse(String pain)
    {
        try (HBCIHandler handler=handler())
        {
            GVDauerSEPAList job=new GVDauerSEPAList(handler);

            Properties properties=new Properties();
            properties.setProperty("GVRes.sepadescr",SEPA_DESCRIPTOR);
            properties.setProperty("GVRes.sepapain",pain);
            properties.setProperty("GVRes.orderid","4711");
            properties.setProperty("GVRes.DauerDetails.firstdate","2025-11-01");
            properties.setProperty("GVRes.DauerDetails.timeunit","M");
            properties.setProperty("GVRes.DauerDetails.turnus","1");
            properties.setProperty("GVRes.DauerDetails.execday","1");
            properties.setProperty("GVRes.DauerDetails.exectime","12:00:00");

            HBCIMsgStatus status=new HBCIMsgStatus();
            status.setData(properties);
            job.extractResults(status,"GVRes",0);

            GVRDauerList result=(GVRDauerList)job.getJobResult();
            assertEquals(1,result.getEntries().length);
            return result.getEntries()[0];
        }
    }

    private static HBCIHandler handler()
    {
        HBCIPassportPinTan passport=new HBCIPassportPinTan(new Object(),0);
        Properties bpd=new Properties();
        bpd.setProperty("Params.DauerSEPAListPar2.SegHead.code","HICDBS");
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
