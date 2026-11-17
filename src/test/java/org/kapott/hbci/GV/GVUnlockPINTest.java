package org.kapott.hbci.GV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci4java.AbstractTest;

/**
 * Tests fuer den Geschaeftsvorfall "PIN-Sperre aufheben" (HKPSA/HIPSAS).
 */
public class GVUnlockPINTest extends AbstractTest
{
    /**
     * Prueft, dass der Highlevel-Job angelegt wird und das Segment HKPSA nur aus dem Segmentkopf besteht.
     */
    @Test
    public void testHighlevelJob()
    {
        try (HBCIHandler handler = createHandler())
        {
            HBCIJob<?> genericJob = handler.newJob("UnlockPIN");
            assertTrue(genericJob instanceof GVUnlockPIN);

            GVUnlockPIN job = (GVUnlockPIN) genericJob;
            assertEquals("HKPSA", job.getHBCICode());
            assertTrue(job.getJobResult() instanceof HBCIJobResultImpl);

            job.verifyConstraints();
            // Ausser der Request-Markierung gibt es keine Parameter
            Properties params = job.getLowlevelParams();
            assertEquals(1, params.size());
            assertEquals("requested", params.getProperty("UnlockPIN1"));

            SEG seg = job.createJobSegment(3);
            seg.validate();
            assertEquals("HKPSA:3:1'", seg.toString(0));
        }
    }

    /**
     * Prueft die Registrierung der Lowlevel-Syntax.
     */
    @Test
    public void testLowlevelSyntax()
    {
        MsgGen generator = new HBCIKernelImpl(null, "300").getMsgGen();

        assertEquals(List.of("1"), generator.getLowlevelGVs().get("UnlockPIN"));
        assertTrue(generator.getGVParameterNames("UnlockPIN", "1").isEmpty());
    }

    /**
     * Prueft, dass das Parametersegment HIPSAS in den BPD erkannt wird.
     */
    @Test
    public void testParams() throws Exception
    {
        String data =
            "HNHBK:1:3+000000000000+300+0+1+0:1'" +
            "HIRMG:2:2+0020::Auftrag ausgefuehrt.'" +
            "HIBPA:3:3+1+280:12030000+Testbank+1+1+300'" +
            "HIPSAS:4:1:3+1+1+0'" +
            "HNHBS:5:1+1'";
        data = data.replace("000000000000", String.format("%012d", data.length()));

        HBCIKernelImpl kernel = new HBCIKernelImpl(null, "300");
        kernel.rawNewMsg("DialogInitAnon");
        MSG msg = new MSG("DialogInitAnonRes", data, data.length(), kernel.getMsgGen());

        Hashtable<String, String> values = new Hashtable<String, String>();
        msg.extractValues(values);

        String prefix = "DialogInitAnonRes.BPD.Params.UnlockPINPar1.";
        assertEquals("HIPSAS", values.get(prefix + "SegHead.code"));
        assertEquals("1", values.get(prefix + "SegHead.version"));
        assertEquals("1", values.get(prefix + "maxnum"));
        assertEquals("1", values.get(prefix + "minsigs"));
        assertEquals("0", values.get(prefix + "secclass"));
    }

    private static HBCIHandler createHandler()
    {
        HBCIPassportPinTan passport = new HBCIPassportPinTan(null, 0);
        Properties bpd = new Properties();
        bpd.setProperty("Params.UnlockPINPar1.SegHead.code", "HIPSAS");
        passport.setBPD(bpd);
        passport.setHBCIVersion("300");
        return new HBCIHandler("300", passport, true);
    }
}
