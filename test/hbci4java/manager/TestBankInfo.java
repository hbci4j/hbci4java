package hbci4java.manager;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIVersion;

import hbci4java.AbstractTest;

/**
 * Testet das Parsen der Bank-Informationen.
 */
public class TestBankInfo extends AbstractTest {

    /**
     * Testet das Lesen der Bank-Informationen fuer eine einzelne Bank.
     */
    @Test
    public void test001() {
        BankInfo info = HBCIUtils.getBankInfo("86050200");
        Assert.assertNotNull(info);
        Assert.assertEquals("BLZ falsch","86050200",info.getBlz());
        Assert.assertEquals("BIC falsch","SOLADES1GRM",info.getBic());
        Assert.assertEquals("Ort falsch","Grimma",info.getLocation());
        Assert.assertEquals("Name falsch","Sparkasse Muldental",info.getName());
        Assert.assertEquals("PIN/TAN-URL falsch","https://banking-sn5.s-fints-pt-sn.de/fints30",info.getPinTanAddress());
        Assert.assertEquals("PIN/TAN-Version falsch",HBCIVersion.HBCI_300,info.getPinTanVersion());
        Assert.assertEquals("RDH-Adresse falsch","i052.s-fints-sn.de",info.getRdhAddress());
        Assert.assertEquals("RDH-Version falsch",HBCIVersion.HBCI_220,info.getRdhVersion());
    }
    
    /**
     * Testet die Suche anhand der BLZ.
     */
    @Test
    public void test002() {
        Collection<BankInfo> list = HBCIUtils.searchBankInfo("86050");
        Assert.assertNotNull(list);
        Assert.assertTrue("Nicht genug Treffer",list.size() >= 2);
        
        boolean found = false;
        for (BankInfo info:list)
        {
            if (info.getBlz().equals("86050200"))
            {
                found = true;
                break;
            }
        }
        Assert.assertTrue("Bank nicht gefunden",found);
    }
    
    /**
     * Testet die Suche anhand der BIC.
     */
    @Test
    public void test003() {
        Collection<BankInfo> list = HBCIUtils.searchBankInfo("SOLADES");
        Assert.assertNotNull(list);
        Assert.assertTrue("Nicht genug Treffer",list.size() >= 100);
        
        boolean found = false;
        for (BankInfo info:list)
        {
            if (info.getBic().equals("SOLADES1GRM"))
            {
                found = true;
                break;
            }
        }
        Assert.assertTrue("Bank nicht gefunden",found);
    }

    /**
     * Testet die Suche anhand des Orts.
     */
    @Test
    public void test004() {
        Collection<BankInfo> list = HBCIUtils.searchBankInfo("Grim");
        Assert.assertNotNull(list);
        Assert.assertTrue("Nicht genug Treffer",list.size() >= 2);
        
        boolean found = false;
        for (BankInfo info:list)
        {
            if (info.getLocation().equals("Grimma"))
            {
                found = true;
                break;
            }
        }
        Assert.assertTrue("Bank nicht gefunden",found);
    }

    /**
     * Testet die Suche anhand des Namens.
     */
    @Test
    public void test005() {
        Collection<BankInfo> list = HBCIUtils.searchBankInfo("Muldent");
        Assert.assertNotNull(list);
        Assert.assertTrue("Nicht genug Treffer",list.size() >= 2);
        
        boolean found = false;
        for (BankInfo info:list)
        {
            if (info.getName().equals("Sparkasse Muldental"))
            {
                found = true;
                break;
            }
        }
        Assert.assertTrue("Bank nicht gefunden",found);
    }
    
    /**
     * Testet die leere Ergebnis-Menge bei zu kurzem Suchbegriff.
     */
    @Test
    public void test006() {
        Collection<BankInfo> list = HBCIUtils.searchBankInfo("12");
        Assert.assertNotNull(list);
        Assert.assertEquals("Falsche Treffer-Anzahl",0,list.size());
    }
    
    /**
     * Testet die leere Ergebnis-Menge bei zu kurzem Suchbegriff.
     */
    @Test
    public void test007() {
        Collection<BankInfo> list = HBCIUtils.searchBankInfo(null);
        Assert.assertNotNull(list);
        Assert.assertEquals("Falsche Treffer-Anzahl",0,list.size());
    }
}
