package org.kapott.hbci.GV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet den Highlevel-Job zur Synchronisierung eines TAN-Generators (HKTSY).
 */
public class GVTANGeneratorSyncTest extends AbstractTest
{
    /**
     * Ohne Kartendaten in den BPD enthaelt HKTSY nur ATC und TAN.
     */
    @Test
    public void testOhneKartendaten()
    {
        try (HBCIHandler handler = createHandler("N", "N"))
        {
            HBCIJob<?> genericJob = handler.newJob("TANGeneratorSync");
            assertTrue(genericJob instanceof GVTANGeneratorSync);

            GVTANGeneratorSync job = (GVTANGeneratorSync) genericJob;
            assertEquals("HKTSY", job.getHBCICode());

            job.setParam("atc", "12345");
            job.setParam("tan", "987654");
            job.verifyConstraints();

            Properties params = job.getLowlevelParams();
            assertEquals("12345", params.getProperty("TANGeneratorSync1.atc"));
            assertEquals("987654", params.getProperty("TANGeneratorSync1.tan"));
            assertEquals("HKTSY:3:1+12345+987654'", render(job));
        }
    }

    /**
     * Fordert die Bank keine Kartennummer an, darf sie nicht gesendet werden.
     */
    @Test(expected = InvalidUserDataException.class)
    public void testKartennummerNichtErlaubt()
    {
        try (HBCIHandler handler = createHandler("N", "N"))
        {
            handler.newJob("TANGeneratorSync").setParam("cardnumber", "1234567890");
        }
    }

    /**
     * Fordert die Bank Kartennummer und Kartenfolgenummer an, werden beide gesendet.
     */
    @Test
    public void testMitKartendaten()
    {
        try (HBCIHandler handler = createHandler("J", "J"))
        {
            GVTANGeneratorSync job = (GVTANGeneratorSync) handler.newJob("TANGeneratorSync");
            job.setParam("atc", "12345");
            job.setParam("tan", "987654");
            job.setParam("cardnumber", "1234567890");
            job.setParam("cardseqnumber", "1");
            job.verifyConstraints();

            assertEquals("HKTSY:3:1+12345+987654+1234567890+1'", render(job));
        }
    }

    /**
     * Fordert die Bank die Kartennummer an, ist sie Pflicht.
     */
    @Test(expected = InvalidUserDataException.class)
    public void testKartennummerFehlt()
    {
        try (HBCIHandler handler = createHandler("J", "N"))
        {
            HBCIJob<?> job = handler.newJob("TANGeneratorSync");
            job.setParam("atc", "12345");
            job.setParam("tan", "987654");
            ((HBCIJobImpl<?>) job).verifyConstraints();
        }
    }

    private static String render(GVTANGeneratorSync job)
    {
        SEG seg = job.createJobSegment(3);
        seg.validate();
        return seg.toString(0);
    }

    private static HBCIHandler createHandler(String needCardNumber, String needCardSeqNumber)
    {
        HBCIPassportPinTan passport = new HBCIPassportPinTan(null, 0);
        Properties bpd = new Properties();
        bpd.setProperty("Params_1.TANGeneratorSyncPar1.SegHead.code", "HITSYS");
        bpd.setProperty("Params_1.TANGeneratorSyncPar1.ParTANGeneratorSync.needcardnumber", needCardNumber);
        bpd.setProperty("Params_1.TANGeneratorSyncPar1.ParTANGeneratorSync.needcardseqnumber", needCardSeqNumber);
        passport.setBPD(bpd);
        passport.setHBCIVersion("300");
        return new HBCIHandler("300", passport, true);
    }
}
