package org.kapott.hbci4java.sepa;

import java.util.Properties;

import org.junit.Test;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;
import org.kapott.hbci4java.AbstractTestGV;

/**
 * Testet die Ausfuehrung einer SEPA-Lastschrift.
 */
public class TestGVLastSEPA extends AbstractTestGV
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
                return "LastSEPA";
            }
            
            /**
             * @see org.kapott.hbci4java.AbstractTestGV.Execution#configure(org.kapott.hbci.GV.HBCIJob, org.kapott.hbci.passport.HBCIPassport, java.util.Properties)
             */
            @Override
            public void configure(HBCIJob job, HBCIPassport passport, Properties params) {
                Konto acc = new Konto();
                acc.blz = params.getProperty("target_blz");
                acc.number = params.getProperty("target_number");
                acc.name = "Kurt Mustermann";
                acc.bic = params.getProperty("target_bic");
                acc.iban = params.getProperty("target_iban");
                int source_acc_idx = Integer.parseInt(params.getProperty("source_account_idx"));
                job.setParam("src",passport.getAccounts()[source_acc_idx]);
                job.setParam("dst",acc);
                
                String value = params.getProperty("value");
                if(value == null) value = "100";
                job.setParam("btg",new Value(Integer.parseInt(value),"EUR"));
                job.setParam("usage",params.getProperty("usage"));
                job.setParam("targetdate", params.getProperty("date"));
                job.setParam("creditorid", params.getProperty("creditorid"));
                job.setParam("mandateid", params.getProperty("mandateid"));
                job.setParam("manddateofsig", params.getProperty("date_of_sig"));
            }
            
        });
    }
}
