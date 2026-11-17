package org.kapott.hbci.GV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.GV_Result.GVRTermLastSEPAEdit;
import org.kapott.hbci.GV_Result.GVRTermLastSEPAList;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.protocol.SyntaxElement;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.status.HBCIRetVal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet Bestand (HKDBS), Aenderung (HKDSA) und Loeschung (HKDSL) terminierter
 * SEPA-Einzellastschriften.
 */
public class GVTermLastSEPATest extends AbstractTest
{
    private static final String URN = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02";
    private static final String URN_ESCAPED = "urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.02";
    private static final String URN_08 = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.08";
    private static final String URN_08_ESCAPED = "urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.08";
    private static final String IBAN = "DE02120300000000202051";
    private static final String BIC = "BYLADEM1001";

    /**
     * Die Syntax enthaelt alle Elemente der Segmente laut Spezifikation.
     */
    @Test
    public void testLowlevelSyntax()
    {
        MsgGen generator = new HBCIKernelImpl(null, "300").getMsgGen();

        assertEquals(List.of("1", "2"), generator.getLowlevelGVs().get("TermLastSEPAList"));
        assertEquals(List.of("1"), generator.getLowlevelGVs().get("TermLastSEPAEdit"));
        assertEquals(List.of("1"), generator.getLowlevelGVs().get("TermLastSEPADel"));

        for (String version : List.of("1", "2"))
        {
            assertContains(generator.getGVParameterNames("TermLastSEPAList", version),
                "My.iban", "My.bic", "formats.format", "startdate", "enddate", "maxentries", "offset");
            assertContains(generator.getGVRestrictionNames("TermLastSEPAList", version),
                "canmaxentries", "cantimerange");
        }
        assertContains(generator.getGVResultNames("TermLastSEPAList", "1"),
            "My.iban", "sepadescr", "sepapain", "orderid", "candel", "canchange");
        assertContains(generator.getGVResultNames("TermLastSEPAList", "2"),
            "My.iban", "sepadescr", "sepapain", "orderid", "ccode", "canchange", "orderstatus");
        assertContains(generator.getGVRestrictionNames("TermLastSEPAList", "2"), "suppformats");

        assertContains(generator.getGVParameterNames("TermLastSEPAEdit", "1"),
            "My.iban", "My.bic", "sepadescr", "sepapain", "orderid");
        assertContains(generator.getGVResultNames("TermLastSEPAEdit", "1"), "orderid", "orderidold");
        assertContains(generator.getGVRestrictionNames("TermLastSEPAEdit", "1"),
            "minVorlZeitFNALRCUR", "maxVorlZeitFNALRCUR", "minVorlZeitFRSTOOFF", "maxVorlZeitFRSTOOFF");

        assertContains(generator.getGVParameterNames("TermLastSEPADel", "1"),
            "My.iban", "My.bic", "sepadescr", "sepapain", "orderid");
        assertContains(generator.getGVRestrictionNames("TermLastSEPADel", "1"), "orderdata_required");
    }

    /**
     * Der Job verwendet die hoechste Segmentversion aus den BPD und baut HKDBS laut Spezifikation.
     */
    @Test
    public void testListRequest()
    {
        try (HBCIHandler handler = createHandler())
        {
            HBCIJob<?> genericJob = handler.newJob("TermLastSEPAList");
            assertTrue(genericJob instanceof GVTermLastSEPAList);

            GVTermLastSEPAList job = (GVTermLastSEPAList) genericJob;
            assertEquals("HKDBS", job.getHBCICode());
            assertEquals("2", job.getSegVersion());
            assertTrue(job.getJobResult() instanceof GVRTermLastSEPAList);

            job.setParam("src", account());
            job.setParam("maxentries", "10");
            job.verifyConstraints();

            assertEquals("HKDBS:3:2+" + IBAN + ":" + BIC + "+" + URN_ESCAPED + "+++10'", segment(job));
        }
    }

    /**
     * Die unterstuetzten pain messages sind eine DEG. Mehrere Descriptoren werden
     * daher mit ":" getrennt und nicht als weitere Datenelemente gesendet.
     */
    @Test
    public void testListRequestTwoFormats()
    {
        try (HBCIHandler handler = createHandler())
        {
            GVTermLastSEPAList job = (GVTermLastSEPAList) handler.newJob("TermLastSEPAList");
            job.setParam("src", account());
            job.setParam("startdate", "2026-12-01");
            job.setLowlevelParam(job.getName() + ".formats.format_2", URN_08);
            job.verifyConstraints();

            assertEquals("HKDBS:3:2+" + IBAN + ":" + BIC + "+" + URN_ESCAPED + ":" + URN_08_ESCAPED + "+20261201'", segment(job));
        }
    }

    /**
     * Meldet die Bank 3040 mit einem Aufsetzpunkt, wird der Job erneut ausgefuehrt
     * und sendet den Aufsetzpunkt mit.
     */
    @Test
    public void testListOffset()
    {
        try (HBCIHandler handler = createHandler())
        {
            GVTermLastSEPAList job = (GVTermLastSEPAList) handler.newJob("TermLastSEPAList");
            job.setParam("src", account());
            job.verifyConstraints();
            job.setIdx(3);

            HBCIMsgStatus status = new HBCIMsgStatus();
            status.segStatus.addRetVal(new HBCIRetVal("3", null, null, "3040", "Es liegen weitere Informationen vor.", new String[] {"ABC"}));
            job.fillJobResult(status, 0);

            assertTrue(job.redo() == job);
            job.applyOffset();
            assertTrue(segment(job), segment(job).endsWith("+ABC'"));
        }
    }

    /**
     * Liest eine HIDBS-Rueckmeldung in Segmentversion 1.
     */
    @Test
    public void testListResultVersion1() throws Exception
    {
        String pain = pain();
        String raw = "HIDBS:4:1:3+" + IBAN + ":" + BIC + "+" + URN_ESCAPED + "+@" + pain.length() + "@" + pain + "+ORDER-1+N+J'";
        GVRTermLastSEPAList.Entry entry = parseList("TermLastSEPAListRes1", raw);

        assertEquals("ORDER-1", entry.orderid);
        assertEquals(URN, entry.sepadescr);
        assertFalse(entry.can_delete);
        assertTrue(entry.can_change);
        assertNull(entry.ccode);
        assertNull(entry.orderstatus);
        assertPainData(entry);
    }

    /**
     * Liest eine HIDBS-Rueckmeldung in Segmentversion 2.
     */
    @Test
    public void testListResultVersion2() throws Exception
    {
        String pain = pain();
        String raw = "HIDBS:4:2:3+" + IBAN + ":" + BIC + "+" + URN_ESCAPED + "+@" + pain.length() + "@" + pain + "+ORDER-1+3+N+1'";
        GVRTermLastSEPAList.Entry entry = parseList("TermLastSEPAListRes2", raw);

        assertEquals("ORDER-1", entry.orderid);
        assertEquals("3", entry.ccode);
        assertFalse(entry.can_change);
        assertTrue(entry.can_delete);
        assertEquals("1", entry.orderstatus);
        assertPainData(entry);
    }

    /**
     * HKDSA enthaelt die vollstaendige pain.008-Nachricht und die Auftrags-ID.
     */
    @Test
    public void testEditRequest()
    {
        try (HBCIHandler handler = createHandler())
        {
            HBCIJob<?> genericJob = handler.newJob("TermLastSEPAEdit");
            assertTrue(genericJob instanceof GVTermLastSEPAEdit);

            GVTermLastSEPAEdit job = (GVTermLastSEPAEdit) genericJob;
            assertEquals("HKDSA", job.getHBCICode());
            setDebitParams(job);
            job.verifyConstraints();

            String pain = job.getLowlevelParams().getProperty("TermLastSEPAEdit1.sepapain");
            assertPainRequest(pain);
            assertEquals("HKDSA:3:1+" + IBAN + ":" + BIC + "+" + URN_ESCAPED + "+@" + (pain.length() - 1) + "@" + pain.substring(1) + "+ORDER-1'", segment(job));
        }
    }

    /**
     * HIDSA liefert die neue und die alte Auftrags-ID.
     */
    @Test
    public void testEditResult()
    {
        try (HBCIHandler handler = createHandler())
        {
            GVTermLastSEPAEdit job = (GVTermLastSEPAEdit) handler.newJob("TermLastSEPAEdit");
            Properties data = new Properties();
            data.setProperty("GVRes.orderid", "ORDER-2");
            data.setProperty("GVRes.orderidold", "ORDER-1");

            HBCIMsgStatus status = new HBCIMsgStatus();
            status.setData(data);
            job.extractResults(status, "GVRes", 0);

            GVRTermLastSEPAEdit result = (GVRTermLastSEPAEdit) job.getJobResult();
            assertEquals("ORDER-2", result.getOrderId());
            assertEquals("ORDER-1", result.getOrderIdOld());
        }
    }

    /**
     * HKDSL enthaelt die pain.008-Nachricht und die Auftrags-ID.
     */
    @Test
    public void testDelRequest()
    {
        try (HBCIHandler handler = createHandler())
        {
            HBCIJob<?> genericJob = handler.newJob("TermLastSEPADel");
            assertTrue(genericJob instanceof GVTermLastSEPADel);

            GVTermLastSEPADel job = (GVTermLastSEPADel) genericJob;
            assertEquals("HKDSL", job.getHBCICode());
            setDebitParams(job);
            job.verifyConstraints();

            String pain = job.getLowlevelParams().getProperty("TermLastSEPADel1.sepapain");
            assertPainRequest(pain);
            assertEquals("HKDSL:3:1+" + IBAN + ":" + BIC + "+" + URN_ESCAPED + "+@" + (pain.length() - 1) + "@" + pain.substring(1) + "+ORDER-1'", segment(job));
        }
    }

    /**
     * Die Bestandsabfrage liefert pain.008.001.02, laut HISPAS waere pain.008.001.08
     * die neueste Version. Aenderung und Loeschung muessen das Schema aus dem Bestand
     * verwenden, wenn es als "_sepadescriptor" uebergeben wird.
     */
    @Test
    public void testEditAndDelUseListedSchema() throws Exception
    {
        try (HBCIHandler handler = createHandler(URN, URN_08))
        {
            GVTermLastSEPAList list = (GVTermLastSEPAList) handler.newJob("TermLastSEPAList");
            list.setParam("src", account());
            list.verifyConstraints();
            assertTrue(segment(list), segment(list).contains("+" + URN_ESCAPED + "'"));

            String pain = pain();
            GVRTermLastSEPAList.Entry entry = parseList(handler, "TermLastSEPAListRes2",
                "HIDBS:4:2:3+" + IBAN + ":" + BIC + "+" + URN_ESCAPED + "+@" + pain.length() + "@" + pain + "+ORDER-1'");
            assertEquals(URN, entry.sepadescr);

            for (String name : List.of("TermLastSEPAEdit", "TermLastSEPADel"))
            {
                AbstractSEPAGV job = (AbstractSEPAGV) handler.newJob(name);
                assertEquals(URN_08, job.getPainVersion().getURN());

                setDebitParams(job);
                job.setParam("_sepadescriptor", entry.sepadescr);
                job.verifyConstraints();

                String sent = job.getLowlevelParams().getProperty(job.getName() + ".sepapain");
                assertTrue(sent, sent.contains("xmlns=\"" + URN + "\""));
                assertTrue(segment(job), segment(job).contains("+" + URN_ESCAPED + "+@"));
            }
        }
    }

    private static void setDebitParams(HBCIJob<?> job)
    {
        Konto src = account();
        src.name = "Max Mustermann";
        Konto dst = new Konto();
        dst.iban = "DE89370400440532013000";
        dst.bic = "COBADEFFXXX";
        dst.name = "Erika Mustermann";

        job.setParam("src", src);
        job.setParam("dst", dst);
        job.setParam("btg", new Value("12.34", "EUR"));
        job.setParam("usage", "Beitrag Dezember");
        job.setParam("creditorid", "DE98ZZZ09999999999");
        job.setParam("mandateid", "MANDAT-1");
        job.setParam("manddateofsig", "2026-01-15");
        job.setParam("sequencetype", "RCUR");
        job.setParam("targetdate", "2026-12-01");
        job.setParam("orderid", "ORDER-1");
    }

    private static void assertPainRequest(String pain)
    {
        assertTrue(pain, pain.contains("xmlns=\"" + URN + "\""));
        assertTrue(pain, pain.contains("<LclInstrm><Cd>CORE</Cd></LclInstrm><SeqTp>RCUR</SeqTp>"));
        assertTrue(pain, pain.contains("<ReqdColltnDt>2026-12-01</ReqdColltnDt>"));
        assertTrue(pain, pain.contains("<MndtId>MANDAT-1</MndtId>"));
        assertTrue(pain, pain.contains("<IBAN>DE89370400440532013000</IBAN>"));
    }

    private static void assertPainData(GVRTermLastSEPAList.Entry entry)
    {
        assertEquals(IBAN, entry.my.iban);
        assertEquals("DE89370400440532013000", entry.other.iban);
        assertEquals("Erika Mustermann", entry.other.name);
        assertEquals("12.34", entry.value.getBigDecimalValue().toPlainString());
        assertEquals("EUR", entry.value.getCurr());
        assertEquals("Beitrag Dezember", entry.usage[0]);
        assertEquals("2026-12-01", format(entry.date));
        assertEquals("CORE", entry.type);
        assertEquals("RCUR", entry.sequencetype);
        assertEquals("DE98ZZZ09999999999", entry.creditorid);
        assertEquals("MANDAT-1", entry.mandateid);
        assertEquals("2026-01-15", format(entry.manddateofsig));
    }

    /**
     * Parst eine Kreditinstitutsnachricht mit dem angegebenen Segment und uebergibt
     * die Daten an den Job.
     */
    private static GVRTermLastSEPAList.Entry parseList(String resName, String segment) throws Exception
    {
        try (HBCIHandler handler = createHandler())
        {
            return parseList(handler, resName, segment);
        }
    }

    private static GVRTermLastSEPAList.Entry parseList(HBCIHandler handler, String resName, String segment) throws Exception
    {
        String data = "HNHBK:1:3+000000000000+300+DIALOG-1+2+DIALOG-1:2'" +
                      "HIRMG:2:2+0010::Nachricht entgegengenommen.'" +
                      "HIRMS:3:2:3+0020::Auftrag ausgeführt.'" +
                      segment +
                      "HNHBS:5:1+2'";

        HBCIKernelImpl kernel = new HBCIKernelImpl(null, "300");
        MSG msg = new MSG("CustomMsgRes", data, data.length(), kernel.getMsgGen());
        Hashtable<String, String> ht = new Hashtable<String, String>();
        msg.extractValues(ht);

        Properties props = new Properties();
        props.putAll(ht);
        HBCIMsgStatus status = new HBCIMsgStatus();
        status.setData(props);

        GVTermLastSEPAList job = (GVTermLastSEPAList) handler.newJob("TermLastSEPAList");
        job.extractResults(status, "CustomMsgRes.GVRes." + resName, 0);

        GVRTermLastSEPAList.Entry[] entries = ((GVRTermLastSEPAList) job.getJobResult()).getEntries();
        assertEquals(1, entries.length);
        return entries[0];
    }

    /**
     * Erzeugt das Segment aus den Lowlevel-Parametern des Jobs, so wie es in der
     * Kundennachricht stehen wuerde.
     */
    private static String segment(HBCIJobImpl<?> job)
    {
        MsgGen gen = new HBCIKernelImpl(null, "300").getMsgGen();
        String name = job.getName();
        SEG seg = new SEG(name, name, null, 0, gen.getSyntax());
        Properties params = job.getLowlevelParams();
        for (Enumeration<?> e = params.propertyNames(); e.hasMoreElements();)
        {
            String key = (String) e.nextElement();
            String value = params.getProperty(key);
            if (value.length() != 0)
                seg.propagateValue(key, value, SyntaxElement.TRY_TO_CREATE, SyntaxElement.DONT_ALLOW_OVERWRITE);
        }
        seg.setSeq(3, SyntaxElement.ALLOW_OVERWRITE);
        seg.validate();
        return seg.toString(0);
    }

    private static Konto account()
    {
        Konto account = new Konto();
        account.iban = IBAN;
        account.bic = BIC;
        return account;
    }

    private static String pain()
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<Document xmlns=\"" + URN + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<CstmrDrctDbtInitn><GrpHdr><MsgId>MSG-1</MsgId><CreDtTm>2026-11-17T10:00:00</CreDtTm>" +
            "<NbOfTxs>1</NbOfTxs><CtrlSum>12.34</CtrlSum><InitgPty><Nm>Max Mustermann</Nm></InitgPty></GrpHdr>" +
            "<PmtInf><PmtInfId>PMT-1</PmtInfId><PmtMtd>DD</PmtMtd><NbOfTxs>1</NbOfTxs><CtrlSum>12.34</CtrlSum>" +
            "<PmtTpInf><SvcLvl><Cd>SEPA</Cd></SvcLvl><LclInstrm><Cd>CORE</Cd></LclInstrm><SeqTp>RCUR</SeqTp></PmtTpInf>" +
            "<ReqdColltnDt>2026-12-01</ReqdColltnDt>" +
            "<Cdtr><Nm>Max Mustermann</Nm></Cdtr><CdtrAcct><Id><IBAN>" + IBAN + "</IBAN></Id></CdtrAcct>" +
            "<CdtrAgt><FinInstnId><BIC>" + BIC + "</BIC></FinInstnId></CdtrAgt><ChrgBr>SLEV</ChrgBr>" +
            "<DrctDbtTxInf><PmtId><EndToEndId>NOTPROVIDED</EndToEndId></PmtId>" +
            "<InstdAmt Ccy=\"EUR\">12.34</InstdAmt>" +
            "<DrctDbtTx><MndtRltdInf><MndtId>MANDAT-1</MndtId><DtOfSgntr>2026-01-15</DtOfSgntr></MndtRltdInf>" +
            "<CdtrSchmeId><Id><PrvtId><Othr><Id>DE98ZZZ09999999999</Id>" +
            "<SchmeNm><Prtry>SEPA</Prtry></SchmeNm></Othr></PrvtId></Id></CdtrSchmeId></DrctDbtTx>" +
            "<DbtrAgt><FinInstnId><BIC>COBADEFFXXX</BIC></FinInstnId></DbtrAgt>" +
            "<Dbtr><Nm>Erika Mustermann</Nm></Dbtr><DbtrAcct><Id><IBAN>DE89370400440532013000</IBAN></Id></DbtrAcct>" +
            "<RmtInf><Ustrd>Beitrag Dezember</Ustrd></RmtInf></DrctDbtTxInf></PmtInf></CstmrDrctDbtInitn></Document>";
    }

    private static HBCIHandler createHandler()
    {
        return createHandler(URN);
    }

    /**
     * @param hispasFormats die laut HISPAS unterstuetzten pain-Formate.
     * HIDBSS Version 2 unterstuetzt immer nur pain.008.001.02.
     */
    private static HBCIHandler createHandler(String... hispasFormats)
    {
        HBCIPassportPinTan passport = new HBCIPassportPinTan(new Object(), 0);
        Properties bpd = new Properties();
        bpd.setProperty("Params.SEPAInfoPar1.SegHead.code", "HISPAS");
        for (int i = 0; i < hispasFormats.length; i++)
            bpd.setProperty("Params.SEPAInfoPar1.ParSEPAInfo.suppformats" + (i == 0 ? "" : "_" + (i + 1)), hispasFormats[i]);
        bpd.setProperty("Params.TermLastSEPAListPar1.SegHead.code", "HIDBSS");
        bpd.setProperty("Params_1.TermLastSEPAListPar2.SegHead.code", "HIDBSS");
        bpd.setProperty("Params_1.TermLastSEPAListPar2.ParTermLastSEPAList.suppformats", URN);
        bpd.setProperty("Params.TermLastSEPAEditPar1.SegHead.code", "HIDSAS");
        bpd.setProperty("Params.TermLastSEPADelPar1.SegHead.code", "HIDSLS");
        passport.setBPD(bpd);
        passport.setUPD(new Properties());
        passport.setHBCIVersion("300");
        return new HBCIHandler("300", passport, true);
    }

    private static String format(Date value)
    {
        return value == null ? null : new SimpleDateFormat("yyyy-MM-dd").format(value);
    }

    private static void assertContains(List<String> actual, String... expected)
    {
        for (String value : expected)
        {
            assertTrue("Missing lowlevel field " + value + " in " + actual, actual.contains(value));
        }
    }
}
