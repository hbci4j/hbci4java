/**********************************************************************
 * $Source: /cvsroot/hibiscus/hbci4java/test/hbci4java/ddv/PCSCTest.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/11/24 21:59:37 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.sepa;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kapott.hbci.GV.generators.ISEPAGenerator;
import org.kapott.hbci.GV.generators.SEPAGeneratorFactory;
import org.kapott.hbci.sepa.PainVersion;
import org.kapott.hbci.sepa.PainVersion.Type;

/**
 * Testet das pure Generieren von Pain XML-Dateien - ohne HBCI-Context.
 */
public class TestPainGen
{
    /**
     * Initialisiert den Test.
     * @throws Exception
     */
    @BeforeClass
    public static void beforeClass() throws Exception
    {
        // Siehe AbstractSEPAGenerator#marshal - damit werden die Schema-Files auch im Unit-Test gefunden.
        System.setProperty("hbci4java.pain.debugmode","true");
    }

    /**
     * Testet das Erstellen von SEPA-Ueberweisungen.
     * @throws Exception
     */
    @Test
    public void test001() throws Exception
    {
        Properties props = new Properties();
        props.setProperty("src.bic",    "ABCDEFAA123");
        props.setProperty("src.iban",   "DE1234567890");
        props.setProperty("src.name",   "Max Mustermann");
        props.setProperty("dst.bic",    "ABCDEFAA123");
        props.setProperty("dst.iban",   "DE0987654321");
        props.setProperty("dst.name",   "SEPAstian");
        props.setProperty("btg.value",  "100.00");
        props.setProperty("btg.curr",   "EUR");
        props.setProperty("usage",      "Verwendungszweck");
        props.setProperty("sepaid",     "abcde");
        props.setProperty("endtoendid", "fghij");

        for (PainVersion version:PainVersion.getKnownVersions(Type.PAIN_001))
        {
            // Der Test schlaegt automatisch fehl, wenn die Schema-Validierung nicht klappt
            ISEPAGenerator gen = SEPAGeneratorFactory.get("UebSEPA", version);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gen.generate(props, bos, true);
        }
    }
    
    /**
     * Testet das Erstellen von SEPA-Lastschriften.
     * @throws Exception
     */
    @Test
    public void test002() throws Exception
    {
        Properties props = new Properties();
        props.setProperty("src.bic",        "ABCDEFAA123");
        props.setProperty("src.iban",       "DE1234567890");
        props.setProperty("src.name",       "Max Mustermann");
        props.setProperty("dst.bic",        "ABCDEFAA123");
        props.setProperty("dst.iban",       "DE0987654321");
        props.setProperty("dst.name",       "SEPAstian");
        props.setProperty("btg.value",      "100.00");
        props.setProperty("btg.curr",       "EUR");
        props.setProperty("usage",          "Verwendungszweck");
        props.setProperty("sepaid",         "abcde");
        props.setProperty("endtoendid",     "fghij");
        props.setProperty("creditorid",     "DE1234567890");
        props.setProperty("mandateid",      "0987654321");
        props.setProperty("manddateofsig",  "2013-11-23");
        props.setProperty("amendmandindic", "false");
        props.setProperty("sequencetype",   "FRST");

        for (PainVersion version:PainVersion.getKnownVersions(Type.PAIN_008))
        {
            // Der Test schlaegt automatisch fehl, wenn die Schema-Validierung nicht klappt
            ISEPAGenerator gen = SEPAGeneratorFactory.get("LastSEPA", version);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gen.generate(props, bos, true);
        }
    }
}
