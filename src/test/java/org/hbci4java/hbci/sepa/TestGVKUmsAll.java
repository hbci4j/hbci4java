package org.hbci4java.hbci.sepa;

import java.util.Properties;

import org.hbci4java.hbci.GV.HBCIJob;
import org.hbci4java.hbci.AbstractTestGV;
import org.hbci4java.hbci.passport.HBCIPassport;
import org.junit.Test;

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
             * @see org.hbci4java.hbci.AbstractTestGV.Execution#configure(org.hbci4java.hbci.GV.HBCIJob, org.hbci4java.hbci.passport.HBCIPassport, java.util.Properties)
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
