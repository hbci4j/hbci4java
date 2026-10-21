package org.kapott.hbci.GV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet Saldo- und Umsatzabruf fuer Konten ohne IBAN/BIC (z.B. Kreditkartenkonten).
 * Erlaubt die Bank in HISPAS die nationale Kontoverbindung, sind IBAN und BIC optional.
 */
public class GVKontoOhneIbanTest extends AbstractTest
{
    @Test
    public void testSaldoOhneIban()
    {
        this.testOhneIban("SaldoReq", "Saldo", "HISALS", "7");
    }

    @Test
    public void testUmsaetzeOhneIban()
    {
        this.testOhneIban("KUmsAll", "KUmsZeit", "HIKAZS", "7");
    }

    @Test
    public void testUmsaetzeCamtOhneIban()
    {
        this.testOhneIban("KUmsAllCamt", "KUmsZeitCamt", "HICAZS", "1");
    }

    @Test
    public void testSaldoOhneIbanOhneNationaleKontoverbindung()
    {
        this.testOhneIbanFehler("SaldoReq", "Saldo", "HISALS", "7");
    }

    @Test
    public void testUmsaetzeOhneIbanOhneNationaleKontoverbindung()
    {
        this.testOhneIbanFehler("KUmsAll", "KUmsZeit", "HIKAZS", "7");
    }

    @Test
    public void testUmsaetzeCamtOhneIbanOhneNationaleKontoverbindung()
    {
        this.testOhneIbanFehler("KUmsAllCamt", "KUmsZeitCamt", "HICAZS", "1");
    }

    private void testOhneIban(String jobname, String lowlevel, String code, String version)
    {
        try (HBCIHandler handler = createHandler(lowlevel, code, version, "J"))
        {
            HBCIJob<?> job = handler.newJob(jobname);
            job.setParam("my", createKonto());
            ((HBCIJobImpl<?>) job).verifyConstraints();

            Properties params = job.getLowlevelParams();
            String prefix = lowlevel + version + ".KTV.";
            assertEquals("4930000000000000", params.getProperty(prefix + "number"));
            assertEquals("TOKEN", params.getProperty(prefix + "subnumber"));
            assertEquals("12030000", params.getProperty(prefix + "KIK.blz"));
            assertNull(params.getProperty(prefix + "iban"));
            assertNull(params.getProperty(prefix + "bic"));
        }
    }

    private void testOhneIbanFehler(String jobname, String lowlevel, String code, String version)
    {
        try (HBCIHandler handler = createHandler(lowlevel, code, version, "N"))
        {
            HBCIJob<?> job = handler.newJob(jobname);
            job.setParam("my", createKonto());
            ((HBCIJobImpl<?>) job).verifyConstraints();
            fail("IBAN/BIC muessen Pflicht bleiben, wenn die nationale Kontoverbindung nicht erlaubt ist");
        }
        catch (InvalidUserDataException e)
        {
            // erwartet
        }
    }

    private static Konto createKonto()
    {
        Konto k = new Konto();
        k.country = "DE";
        k.blz = "12030000";
        k.number = "4930000000000000";
        k.subnumber = "TOKEN";
        return k;
    }

    private static HBCIHandler createHandler(String lowlevel, String code, String version, String nat)
    {
        HBCIPassportPinTan passport = new HBCIPassportPinTan(null, 0);
        Properties bpd = new Properties();
        bpd.setProperty("Params_1." + lowlevel + "Par" + version + ".SegHead.code", code);
        bpd.setProperty("Params_2.SEPAInfoPar1.SegHead.code", "HISPAS");
        bpd.setProperty("Params_2.SEPAInfoPar1.ParSEPAInfo.cannationalacc", nat);
        passport.setBPD(bpd);
        passport.setHBCIVersion("300");
        return new HBCIHandler("300", passport, true);
    }
}
