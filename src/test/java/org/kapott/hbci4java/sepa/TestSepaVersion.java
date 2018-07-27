/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci4java.sepa;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.GV.generators.GenLastSEPA00800101;
import org.kapott.hbci.GV.generators.GenUebSEPA00100303;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.sepa.SepaVersion.Type;

/**
 * Tests fuer den SEPA-Version Parser 
 */
public class TestSepaVersion
{
    /**
     * Testet simples Parsen einer PAIN-Version.
     * @throws Exception
     */
    @Test
    public void test001() throws Exception
    {
        SepaVersion v = SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.003.03");
        Assert.assertEquals(Type.PAIN_001,v.getType());
        Assert.assertEquals(3,v.getMajor());
        Assert.assertEquals(3,v.getMinor());
        Assert.assertEquals(GenUebSEPA00100303.class.getName(),v.getGeneratorClass("UebSEPA"));
    }
    
    /**
     * Testet simples Parsen einer PAIN-Version.
     * @throws Exception
     */
    @Test
    public void test002() throws Exception
    {
        SepaVersion v = SepaVersion.byURN("sepade.pain.008.001.01.xsd");
        Assert.assertEquals(Type.PAIN_008,v.getType());
        Assert.assertEquals(1,v.getMajor());
        Assert.assertEquals(1,v.getMinor());
        Assert.assertEquals(GenLastSEPA00800101.class.getName(),v.getGeneratorClass("LastSEPA"));
    }

    /**
     * Testet, dass ein ungueltiger PAIN-Typ nicht gelesen werden kann.
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public void test003() throws Exception
    {
        SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.005.003.03");
    }

    /**
     * Testet, dass eine ungueltige URN nicht gelesen werden kann.
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public void test004() throws Exception
    {
        SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001");
    }
    
    /**
     * Testet, dass eine ungueltige URN nicht gelesen werden kann.
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public void test005() throws Exception
    {
        SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.001");
    }

    /**
     * Testet das Ermitteln der hoechsten PAIN-Version.
     * @throws Exception
     */
    @Test
    public void test006() throws Exception
    {
        List<SepaVersion> list = new ArrayList<SepaVersion>()
        {{
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.001.01"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.002.03"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.003.03"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.001.02"));
        }};
        
        SepaVersion highest = SepaVersion.findGreatest(list);
        Assert.assertNotNull(highest);
        Assert.assertEquals(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.003.03"),highest);
    }
    
    /**
     * Testet das Ermitteln der hoechsten PAIN-Version auch dann, wenn diese neuen bizarren
     * Versionen mit drin stehen, die augenscheinlich aelter sind.
     * @throws Exception
     */
    @Test
    public void test007() throws Exception
    {
        List<SepaVersion> list = new ArrayList<SepaVersion>()
        {{
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.001.01"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.002.03"));

            // Bizzar, aber ist so. Der Test prueft, dass 001.001.03 die aktuellste Version ist. Siehe die Hinweise in PainVersion#compareTo
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.001.03"));

            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.003.03"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.001.02"));
            
        }};
        
        SepaVersion highest = SepaVersion.findGreatest(list);
        Assert.assertNotNull(highest);
        Assert.assertEquals(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.001.03"),highest);
    }
    
    /**
     * Testet das Fehlschlagen bei nicht-kompatibler PAIN-Versionen.
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public void test008() throws Exception
    {
        List<SepaVersion> list = new ArrayList<SepaVersion>()
        {{
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.002.03"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.008.001.01"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.003.03"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.001.02"));
        }};
        
        SepaVersion.findGreatest(list);
    }
    
    
    /**
     * Testet, dass die PAIN-Version von HBCI4Java unterstuetzt wird.
     * @throws Exception
     */
    @Test
    public void test009() throws Exception
    {
        SepaVersion v = SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.003.03");
        Assert.assertTrue(v.canGenerate("UebSEPA"));
    }

    /**
     * Testet, dass die PAIN-Version von HBCI4Java nicht unterstuetzt wird.
     * @throws Exception
     */
    @Test
    public void test010() throws Exception
    {
        SepaVersion v = SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:pain.001.004.03");
        Assert.assertFalse(v.canGenerate("UebSEPA"));
    }

    /**
     * Testet, dass die PAIN-Version auch dann korrekt erkannt wird, wenn sie in der alten Form angegeben ist.
     * Es gibt naemlich Banken, die in HISPAS das alte Bezeichner-Format senden (also etwa "sepade.pain.001.002.03.xsd"),
     * anschliessend aber meckern, wenn man denen beim Einreichen eines Auftrages genau dieses Format
     * uebergibt. Dort wollen die dann ploetzlich stattdessen den neuen URN haben
     * (also "urn:iso:std:iso:20022:tech:xsd:pain.001.002.03").
     * Siehe http://www.onlinebanking-forum.de/phpBB2/viewtopic.php?p=95160#95160
     * @throws Exception
     */
    @Test
    public void test011() throws Exception
    {
        SepaVersion v = SepaVersion.byURN("sepade.pain.001.002.03.xsd");
        Assert.assertEquals(SepaVersion.PAIN_001_002_03,v);
        Assert.assertEquals("urn:iso:std:iso:20022:tech:xsd:pain.001.002.03",v.getURN());
    }

    /**
     * Testet eine CAMT-Version.
     * @throws Exception
     */
    @Test
    public void test012() throws Exception
    {
        SepaVersion v = SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:camt.052.001.04");
        Assert.assertEquals(SepaVersion.CAMT_052_001_04,v);
        Assert.assertEquals("urn:iso:std:iso:20022:tech:xsd:camt.052.001.04",v.getURN());
    }

    /**
     * Testet das Ermitteln der hoechsten CAMT-Version.
     * @throws Exception
     */
    @Test
    public void test013() throws Exception
    {
        List<SepaVersion> list = new ArrayList<SepaVersion>()
        {{
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:camt.052.001.02"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:camt.052.001.05"));
            add(SepaVersion.byURN("urn:iso:std:iso:20022:tech:xsd:camt.052.001.07"));
        }};
        
        SepaVersion highest = SepaVersion.findGreatest(list);
        Assert.assertNotNull(highest);
        Assert.assertEquals(SepaVersion.CAMT_052_001_07,highest);
    }
}


