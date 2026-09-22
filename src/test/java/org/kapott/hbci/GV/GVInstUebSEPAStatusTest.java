package org.kapott.hbci.GV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.GV_Result.GVRInstUebSEPAStatus;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci4java.AbstractTest;

public class GVInstUebSEPAStatusTest extends AbstractTest
{
    private static final String FORMAT = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.09";

    @Test
    public void testHighlevelJob()
    {
        try (HBCIHandler handler = createHandler())
        {
            HBCIJob<?> genericJob = handler.newJob("InstUebSEPAStatus");
            assertTrue(genericJob instanceof GVInstUebSEPAStatus);

            GVInstUebSEPAStatus job = (GVInstUebSEPAStatus) genericJob;
            assertEquals("HKIPS", job.getHBCICode());
            assertTrue(job.getJobResult() instanceof GVRInstUebSEPAStatus);

            Konto account = new Konto();
            account.iban = "DE89370400440532013000";
            account.bic = "COBADEFFXXX";
            job.setParam("src", account);
            job.setParam("format", FORMAT);
            job.setParam("orderid", "ORDER-123");
            job.verifyConstraints();

            Properties params = job.getLowlevelParams();
            assertEquals(account.iban, params.getProperty("InstUebSEPAStatus1.My.iban"));
            assertEquals(account.bic, params.getProperty("InstUebSEPAStatus1.My.bic"));
            assertEquals(FORMAT, params.getProperty("InstUebSEPAStatus1.formats.format"));
            assertEquals("ORDER-123", params.getProperty("InstUebSEPAStatus1.orderid"));
        }
    }

    @Test
    public void testResultExtraction()
    {
        try (HBCIHandler handler = createHandler())
        {
            GVInstUebSEPAStatus job = (GVInstUebSEPAStatus) handler.newJob("InstUebSEPAStatus");
            Properties data = new Properties();
            data.setProperty("GVRes.orderid", "ORDER-123");
            data.setProperty("GVRes.orderstatus", "7");
            data.setProperty("GVRes.ccode", "2");

            HBCIMsgStatus status = new HBCIMsgStatus();
            status.setData(data);
            job.extractResults(status, "GVRes", 0);

            GVRInstUebSEPAStatus result = job.getJobResult();
            assertEquals("ORDER-123", result.getOrderId());
            assertEquals("7", result.getOrderStatus());
            assertEquals("2", result.getCancellationCode());
        }
    }

    private static HBCIHandler createHandler()
    {
        HBCIPassportPinTan passport = new HBCIPassportPinTan(null, 0);
        Properties bpd = new Properties();
        bpd.setProperty("Params.InstUebSEPAStatusPar1.SegHead.code", "HIIPSS");
        passport.setBPD(bpd);
        passport.setHBCIVersion("300");
        return new HBCIHandler("300", passport, true);
    }
}
