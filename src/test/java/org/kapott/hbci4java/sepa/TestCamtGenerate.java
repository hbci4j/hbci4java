/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 * LGPLv2
 *
 **********************************************************************/

package org.kapott.hbci4java.sepa;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kapott.hbci.GV.generators.ISEPAGenerator;
import org.kapott.hbci.GV.generators.SEPAGeneratorFactory;
import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRKUms.BTag;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet das Erstellen von CAMT XML-Dateien - ohne HBCI-Context.
 */
public class TestCamtGenerate extends AbstractTest
{
    /**
     * Initialisiert die Tests.
     */
    @BeforeClass
    public static void init()
    {
        System.setProperty("sepa.pain.formatted","true");
    }
    
    /**
     * Testet das Erstellen einer CAMT-Datei.
     * @throws Exception
     */
    @Test
    public void test001() throws Exception
    {
        // 1. CAMT-Datei parsen
        List<BTag> list1 = this.parse(this.getStream("test-camt-parse-05200102.xml"),SepaVersion.CAMT_052_001_02);

        // 2. Geparste Datei schreiben
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ISEPAGenerator<List<BTag>> gen = SEPAGeneratorFactory.get("KUmsAllCamt",SepaVersion.CAMT_052_001_07);
        gen.generate(list1,bos,true);
        bos.close();
        
        // 3. Geschriebene Datei neu parsen und mit dem Original vergleichen
        List<BTag> list2 = this.parse(new ByteArrayInputStream(bos.toByteArray()),SepaVersion.CAMT_052_001_07);
        
        Assert.assertTrue("Datei leer",list2.size() > 0);
        Assert.assertEquals("Anzahl der Tage falsch",list1.size(),list2.size());
        
        for (int i=0;i<list1.size();++i)
        {
            BTag tag1 = list1.get(i);
            BTag tag2 = list2.get(i);
            
            Assert.assertEquals("Startsaldo Datum falsch",tag1.start.timestamp,tag2.start.timestamp);
            Assert.assertEquals("Startsaldo Waehrung falsch",tag1.start.value.getCurr(),tag2.start.value.getCurr());
            Assert.assertEquals("Startsaldo Betrag falsch",tag1.start.value.getBigDecimalValue(),tag2.start.value.getBigDecimalValue());
            
            Assert.assertEquals("Endsaldo Datum falsch",tag1.end.timestamp,tag2.end.timestamp);
            Assert.assertEquals("Endsaldo Waehrung falsch",tag1.end.value.getCurr(),tag2.end.value.getCurr());
            Assert.assertEquals("Endsaldo Betrag falsch",tag1.end.value.getBigDecimalValue(),tag2.end.value.getBigDecimalValue());
            
            Assert.assertEquals("Konto IBAN falsch",tag1.my.iban,tag2.my.iban);
            Assert.assertEquals("Konto BIC falsch",tag1.my.bic,tag2.my.bic);
            Assert.assertEquals("Konto Name falsch",tag1.my.name,tag2.my.name);
            
            Assert.assertTrue("Keine Buchungen vorhanden",tag2.lines.size() > 0);
            Assert.assertEquals("Anzahl Buchungen falsch",tag1.lines.size(),tag2.lines.size());
            
            for (int k=0;k<tag1.lines.size();++k)
            {
                UmsLine line1 = tag1.lines.get(k);
                UmsLine line2 = tag2.lines.get(k);
                
                Assert.assertEquals("Gegekonto IBAN falsch",line1.other.iban,line2.other.iban);
                Assert.assertEquals("Gegekonto BIC falsch",line1.other.bic,line2.other.bic);
                Assert.assertEquals("Gegekonto Name falsch",line1.other.name,line2.other.name);
                
                Assert.assertEquals("Saldo Datum falsch",line1.saldo.timestamp,line2.saldo.timestamp);
                Assert.assertEquals("Saldo Waehrung falsch",line1.saldo.value.getCurr(),line2.saldo.value.getCurr());
                Assert.assertEquals("Saldo Betrag falsch",line1.saldo.value.getBigDecimalValue(),line2.saldo.value.getBigDecimalValue());

                Assert.assertEquals("Datum falsch",line1.bdate,line2.bdate);
                Assert.assertEquals("Valuta falsch",line1.valuta,line2.valuta);
                
                Assert.assertEquals("Waehrung falsch",line1.value.getBigDecimalValue(),line2.value.getBigDecimalValue());
                Assert.assertEquals("Betrag falsch",line1.value.getCurr(),line2.value.getCurr());
                
                Assert.assertTrue("Verwendungszweck fehlt",line2.usage.size() > 0);
                Assert.assertEquals("Anzahl Verwendungszwecke falsch",line1.usage.size(),line2.usage.size());
                
                for (int m=0;m<line1.usage.size();++m)
                {
                    Assert.assertEquals("Verwendungszweck falsch",line1.usage.get(m),line2.usage.get(m));
                }
                
                Assert.assertEquals("Addkey falsch",line1.addkey,line2.addkey);
                Assert.assertEquals("Customerref falsch",line1.customerref,line2.customerref);
                Assert.assertEquals("GV-Code falsch",line1.gvcode,line2.gvcode);
                Assert.assertEquals("ID falsch",line1.id,line2.id);
                Assert.assertEquals("Instref falsch",line1.instref,line2.instref);
                Assert.assertEquals("isCamt falsch",line1.isCamt,line2.isCamt);
                Assert.assertEquals("isSepa falsch",line1.isSepa,line2.isSepa);
                Assert.assertEquals("isStorno falsch",line1.isStorno,line2.isStorno);
                Assert.assertEquals("Primanota falsch",line1.primanota,line2.primanota);
                Assert.assertEquals("Purposecode falsch",line1.purposecode,line2.purposecode);
            }
        }
    }
    
    /**
     * Parst eine CAMT-Datei.
     * @param is der Stream mit den Daten.
     * Die Funktion kuemmert sich selbst um das Schliessen des Stream.
     * @param version die SEPA-Version.
     * @return die geparsten Daten.
     * @throws Exception
     */
    private List<BTag> parse(InputStream is, SepaVersion version) throws Exception
    {
        final List<BTag> tage = new ArrayList<BTag>();
        
        try
        {
            ISEPAParser<List<BTag>> parser = SEPAParserFactory.get(version);
            parser.parse(is,tage);
        }
        finally
        {
            if (is != null)
                is.close();
        }
        
        return tage;
    }
}
