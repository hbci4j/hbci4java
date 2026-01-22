package org.hbci4java.hbci.sepa;

import java.util.Properties;

import org.hbci4java.hbci.GV.HBCIJob;
import org.hbci4java.hbci.AbstractTestGV;
import org.hbci4java.hbci.passport.HBCIPassport;
import org.hbci4java.hbci.structures.Konto;
import org.hbci4java.hbci.structures.Value;
import org.junit.Test;


/**
 * Testet das Loeschen eines SEPA-Dauerauftrages.
 */
public class TestGVDauerSEPADel extends AbstractTestGV
{
    /**
     * Testet das Loeschen eines SEPA-Dauerauftrages.
     */
    @Test
    public void test()
    {
        this.execute(new Execution() {
            
            @Override
            public String getJobname() {
                return "DauerSEPADel";
            }
            
            /**
             * @see org.hbci4java.hbci.AbstractTestGV.Execution#configure(org.hbci4java.hbci.GV.HBCIJob, org.hbci4java.hbci.passport.HBCIPassport, java.util.Properties)
             */
            @Override
            public void configure(HBCIJob job, HBCIPassport passport, Properties params) {
                Konto acc = new Konto();
                acc.blz = params.getProperty("blz");
                acc.number = params.getProperty("konto");
                acc.name = params.getProperty("name");
                acc.bic = params.getProperty("bic");
                acc.iban = params.getProperty("iban");
                job.setParam("dst",acc);
                
                int idx = Integer.parseInt(params.getProperty("passport_index","0"));
                job.setParam("src",passport.getAccounts()[idx]);
                
                String value = params.getProperty("value","1");
                job.setParam("btg",new Value(Integer.parseInt(value),"EUR"));
                job.setParam("usage",params.getProperty("usage"));
                
                job.setParam("firstdate", params.getProperty("firstdate"));
                job.setParam("timeunit", params.getProperty("timeunit","M"));
                job.setParam("turnus", params.getProperty("turnus","1"));
                job.setParam("execday", params.getProperty("execday","1"));
                job.setParam("orderid", params.getProperty("orderid"));
            }
            
        });
    }
        
}
