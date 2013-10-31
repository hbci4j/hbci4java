package hbci4java.sepa;

import static org.junit.Assert.*;
import org.junit.Test;
import hbci4java.AbstractTest;
import hbci4java.AbstractTestGV;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.HBCIJobResult;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;



public class TestGVLastSEPA2 extends AbstractTestGV {

    @Test
    public void test() {
        System.out.println("---------Erstelle Job");
        HBCIJob job =  handler.newJob("LastSEPA");
        
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
        
        System.out.println("---------Für Job zur Queue");
        job.addToQueue();

        
        HBCIExecStatus ret = handler.execute();
        HBCIJobResult res = job.getJobResult();
        System.out.println("----------Result: "+res.toString());
        
        Assert.assertEquals("Job Result ist nicht OK!", true, res.isOK() && ret.isOK());
    }

    protected String getJobname()
    {
        return "LastSEPA";
    }

}
