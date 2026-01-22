package org.hbci4java.hbci.sepa;

import java.util.Properties;

import org.hbci4java.hbci.GV.HBCIJob;
import org.hbci4java.hbci.AbstractTestGV;
import org.hbci4java.hbci.passport.HBCIPassport;
import org.junit.Test;

/**
 * Testet das Abrufen der SEPA-Dauerauftraege.
 */
public class TestGVDauerSEPAList extends AbstractTestGV
{
    /**
     * Testet das Ausfuehren einer SEPA-Lastschrift.
     */
    @Test
    public void test()
    {
        this.execute(new Execution() {
            
            @Override
            public String getJobname() {
                return "DauerSEPAList";
            }
            
            /**
             * @see org.hbci4java.hbci.AbstractTestGV.Execution#configure(org.hbci4java.hbci.GV.HBCIJob, org.hbci4java.hbci.passport.HBCIPassport, java.util.Properties)
             */
            @Override
            public void configure(HBCIJob job, HBCIPassport passport, Properties params) {
                super.configure(job, passport, params);
                job.setParam("my.bic",params.getProperty("bic",System.getProperty("bic")));
                job.setParam("my.iban",params.getProperty("iban",System.getProperty("iban")));
            }
        });
    }
}
