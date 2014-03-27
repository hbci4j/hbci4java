/**
 * 
 */
package hbci4java.rsa;

import hbci4java.AbstractTest;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassportRSA;
import org.kapott.hbci.structures.Konto;

/**
 * @author axel
 *
 */
public class HBCIPassportRSATest extends AbstractTest {
    
    private static File dir = null;
    
    @BeforeClass
    public static void beforeCardClass() throws Exception {
        String tmpDir = System.getProperty("java.io.tmpdir", "/tmp");
        dir = new File(tmpDir, "rsajava");
        dir.mkdirs();
    }
    
    @AfterClass
    public static void afterCardClass() throws Exception {
        dir = null;
        // TODO: Verzeichnis und Inhalt muesste mal noch geloescht werden.
    }
    
    private HBCIPassportRSA passport = null;
    
    @Before
    public void beforeCard() throws Exception {
        HBCIUtils.setParam("client.passport.RSA.path", dir.getAbsolutePath() + "/");
        HBCIUtils.setParam("client.passport.RSA.entryidx", "1");
        this.passport = (HBCIPassportRSA) AbstractHBCIPassport.getInstance("RSA");
    }
    
    @After
    public void afterCard() throws Exception {
//        if (this.passport != null)
//            this.passport.saveChanges();
        if (this.passport != null)
            this.passport.close();
    }
    
    @Test
    public void testReadCardData() throws Exception {
        System.out.println("card id: " + passport.getCardId());
        System.out.println("user id: " + passport.getUserId());
        System.out.println("cust id: " + passport.getCustomerId());
        System.out.println("sys id : " + passport.getSysId());
        System.out.println("blz    : " + passport.getBLZ());
        System.out.println("host   : " + passport.getHost());
        System.out.println("prfMeth: " + passport.getProfileMethod());
        System.out.println("prfVers: " + passport.getProfileVersion());
        System.out.println("sigId  : " + passport.getSigId());
        System.out.println("instSig: " + passport.hasInstSigKey());
        System.out.println("instEnc: " + passport.hasInstEncKey());
        System.out.println("mySig  : " + passport.hasMySigKey());
        System.out.println("myEnc  : " + passport.hasMyEncKey());
        
        Konto[] accounts = passport.getAccounts();
        if (accounts != null) {
            for (Konto account : accounts) {
                System.out.println("Account:");
                System.out.println(account.name);
                System.out.println(account.name2);
                System.out.println(account.blz);
                System.out.println(account.number);
                System.out.println(account.subnumber);
                System.out.println(account.bic);
                System.out.println(account.iban);
            }
        }
    }
    
//    @Test
//    public void testCiphering() throws Exception {
//        byte[] data = "Hallo Welt hier ist Axel".getBytes("UTF-8");
//        
//        byte[][] encrypted = passport.encrypt(data);
//        
//        byte[] decrypted = passport.decrypt(encrypted[0], encrypted[1]);
//        
//        System.out.println(new String(decrypted, "UTF-8"));
//    }
    
//    @Test
//    public void testFetchSaldo() throws Exception {
//        HBCIHandler handler = new HBCIHandler("220", passport);
//        HBCIJob job = handler.newJob("SaldoReq");
//        
//        // wir nehmen wir die Saldo-Abfrage einfach das erste verfuegbare Konto
//        //job.setParam("my", passport.getAccounts()[0]);
//        job.addToQueue();
//        handler.execute();
//        HBCIJobResult result = job.getJobResult();
//        System.out.println(result);
//    }
    
}
