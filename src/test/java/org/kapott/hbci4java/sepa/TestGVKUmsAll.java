package org.kapott.hbci4java.sepa;

import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci4java.AbstractTestGV;

/**
 * Testet die Abrufen der Umsaetze.
 */
public class TestGVKUmsAll extends AbstractTestGV
{
    /**
     * Testet das Abrufen der Umsaetze.
     */
    @Test
    public void test()
    {
        this.execute(new Execution() {
            
            @Override
            public String getJobname() {
                return "KUmsAll";
            }
            
            /**
             * @see org.kapott.hbci4java.AbstractTestGV.Execution#configure(org.kapott.hbci.GV.HBCIJob, org.kapott.hbci.passport.HBCIPassport, java.util.Properties)
             */
            @Override
            public void configure(HBCIJob job, HBCIPassport passport, Properties params) {
                super.configure(job, passport, params);
                job.setParam("my.blz",params.getProperty("blz",System.getProperty("blz")));
                job.setParam("my.number",params.getProperty("number",System.getProperty("number")));

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
