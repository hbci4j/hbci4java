package org.kapott.hbci4java.sepa;

import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;
import org.kapott.hbci4java.AbstractTestGV;


/**
 * Testet das Ausfuehren einer SEPA-Termin-Ueberweisung.
 */
public class TestGVTermUebSEPA extends AbstractTestGV
{
    
    /**
     * Testet das Ausfuehren einer SEPA-Termin-Ueberweisung.
     */
    @Test
    public void test()
    {
        this.execute(new Execution() {
            @Override
            public String getJobname() {
                return "TermUebSEPA";
            }
            
            /**
             * @see org.kapott.hbci4java.AbstractTestGV.Execution#configure(org.kapott.hbci.GV.HBCIJob, org.kapott.hbci.passport.HBCIPassport, java.util.Properties)
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
                job.setParam("date", params.getProperty("date"));
            }
        });
    }
}
