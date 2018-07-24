package org.kapott.hbci4java.sepa;

import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci4java.AbstractTestGV;

/**
 * Testet die Abrufen der Umsaetze per CAMT.
 */
public class TestGVKUmsAllCamt extends AbstractTestGV
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
                return "KUmsAllCamt";
            }
            
            /**
             * @see org.kapott.hbci4java.AbstractTestGV.Execution#configure(org.kapott.hbci.GV.HBCIJob, org.kapott.hbci.passport.HBCIPassport, java.util.Properties)
             */
            @Override
            public void configure(HBCIJob job, HBCIPassport passport, Properties params) {
                super.configure(job, passport, params);
                job.setParam("my.bic",params.getProperty("bic",System.getProperty("bic")));
                job.setParam("my.iban",params.getProperty("iban",System.getProperty("iban")));
                
                String start = params.getProperty("startdate",System.getProperty("startdate"));
                if (start != null)
                    job.setParam("startdate",start);
                
                String end = params.getProperty("enddate",System.getProperty("enddate"));
                if (end != null)
                    job.setParam("enddate",end);
            }
        });
    }
}
