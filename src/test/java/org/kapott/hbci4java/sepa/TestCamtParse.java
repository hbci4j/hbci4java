/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **********************************************************************/

package org.kapott.hbci4java.sepa;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.BTag;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.tools.IOUtils;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet das Parsen von CAMT XML-Dateien - ohne HBCI-Context.
 */
public class TestCamtParse extends AbstractTest
{
    private final static DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Testet das korrekte Ermitteln der PAIN-Version aus dem XML-Dokument.
     * @throws Exception
     */
    @Test
    public void test001() throws Exception
    {
        Map<String, SepaVersion> files = new HashMap<String, SepaVersion>() {{
            put("test-camt-parse-05200102.xml", SepaVersion.CAMT_052_001_02);
        }};

        for (Entry<String, SepaVersion> entry : files.entrySet())
        {
            InputStream is = null;
            try
            {
                is = this.getStream(entry.getKey());
                SepaVersion version = SepaVersion.autodetect(is);
                Assert.assertEquals(version, entry.getValue());
            }
            finally
            {
                if (is != null)
                    is.close();
            }
        }
    }

    /**
     * Testet die Autodetection fuer eine XML-Datei ohne Namespace.
     * @throws Exception
     */
    @Test
    public void test002() throws Exception
    {
        InputStream is = null;
        try
        {
            is = this.getStream("test-camt-parse-none.xml");
            SepaVersion version = SepaVersion.autodetect(is);
            Assert.assertNull(version);
        }
        finally
        {
            if (is != null)
                is.close();
        }
    }

    /**
     * Testet das Fehlschlagen der Autodetection fuer eine XML-Datei mit falschem Namespace.
     * @throws Exception
     */
    @Test
    public void test003() throws Exception
    {
        InputStream is = null;
        try
        {
            is = this.getStream("test-camt-parse-invalid.xml");
            SepaVersion.autodetect(is);
            Assert.fail();
        }
        catch (IllegalArgumentException iae)
        {
            Assert.assertEquals(iae.getClass(), IllegalArgumentException.class);
        }
        finally
        {
            if (is != null)
                is.close();
        }
    }

    /**
     * Testet das Lesen einer CAMT-Datei.
     * @throws Exception
     */
    @Test
    public void test004() throws Exception
    {
        final String file = "test-camt-parse-05200102.xml";
        InputStream is1 = null;
        InputStream is2 = null;
        try
        {
            is1 = this.getStream(file);
            SepaVersion version = SepaVersion.autodetect(is1);
            ISEPAParser<List<BTag>> parser = SEPAParserFactory.get(version);

            is2 = this.getStream(file);
            GVRKUms ums = new GVRKUms();
            parser.parse(is2, ums.getDataPerDay());

            List<BTag> days = ums.getDataPerDayUnbooked();
            Assert.assertEquals("Anzahl Tage ungebuchte Umsaetze falsch", 0, days.size());

            days = ums.getDataPerDay();
            Assert.assertEquals("Anzahl Buchungstage falsch", 1, days.size());

            BTag day = days.get(0);
            List<UmsLine> lines = day.lines;
            Assert.assertEquals("Anzahl Buchungen falsch", 2, lines.size());

            Assert.assertTrue("Startsaldo falsch", new BigDecimal("100").compareTo(day.start.value.getBigDecimalValue()) == 0);
            Assert.assertTrue("Endsaldo falsch", new BigDecimal("110.50").compareTo(day.end.value.getBigDecimalValue()) == 0);
            Assert.assertEquals("Startdatum falsch", DF.parse("2018-07-20"), day.start.timestamp);
            Assert.assertEquals("Enddatum falsch", DF.parse("2018-07-20"), day.end.timestamp);
            Assert.assertEquals("Startsaldo Waehrung falsch", "EUR",day.start.value.getCurr());
            Assert.assertEquals("Endsaldo Waehrung falsch", "EUR",day.end.value.getCurr());

            Assert.assertNotNull("Konto fehlt",day.my);
            Assert.assertEquals("IBAN falsch","DE12345678901234567890",day.my.iban);
            Assert.assertEquals("BIC falsch","ABCDEFG1ABC",day.my.bic);
            Assert.assertEquals("Waehrung falsch","EUR",day.my.curr);
            
            Assert.assertEquals("Starttyp falsch",'F',day.starttype);
            Assert.assertEquals("Endtyp falsch",'F',day.endtype);

            {
                UmsLine l = lines.get(0);
                Assert.assertNull("additional falsch",l.additional);
                Assert.assertEquals("addkey falsch","000",l.addkey);
                Assert.assertEquals("Buchungsdatum falsch",DF.parseObject("2018-07-20"),l.bdate);
                Assert.assertNull("charge_value falsch",l.charge_value);
                Assert.assertEquals("customerref falsch","NONREF",l.customerref);
                Assert.assertEquals("gvcode falsch","152",l.gvcode);
                Assert.assertEquals("id falsch","2018-07-20-07.51.25.370057",l.id);
                Assert.assertNull("instref falsch",l.instref);
                Assert.assertTrue("Buchung nicht als CAMT-Buchung markiert",l.isCamt);
                Assert.assertTrue("Buchung nicht als SEPA-Buchung markiert",l.isSepa);
                Assert.assertFalse("Buchung ist kein Storno",l.isStorno);
                Assert.assertNull("orig_value falsch",l.orig_value);
                Assert.assertNotNull("Gegenkonto fehlt",l.other);
                Assert.assertEquals("Gegenkonto IBAN falsch","DE12345678901234567891",l.other.iban);
                Assert.assertEquals("Gegenkonto BIC falsch","ABCDEFG2ABC",l.other.bic);
                Assert.assertEquals("Gegenkonto Name falsch","Max Mustermann",l.other.name);
                Assert.assertEquals("primanota falsch","9201",l.primanota);
                Assert.assertEquals("purposecode falsch","RINP",l.purposecode);
                Assert.assertNotNull("Saldo fehlt",l.saldo);
                Assert.assertTrue("Saldo falsch", new BigDecimal("110").compareTo(l.saldo.value.getBigDecimalValue()) == 0);
                Assert.assertEquals("Saldodatum falsch", DF.parse("2018-07-20"), l.saldo.timestamp);
                Assert.assertEquals("Saldo-Waehrung falsch", "EUR",l.saldo.value.getCurr());
                Assert.assertEquals("Text falsch","DAUERAUFTRAG",l.text);
                Assert.assertNotNull("Verwendungszweck fehlt",l.usage);
                Assert.assertEquals("Anzahl Verwendungszwecke falsch",1,l.usage.size());
                Assert.assertEquals("Verwendungszweck falsch","Verwendungszweck 1",l.usage.get(0));
                Assert.assertNotNull("Betrag fehlt",l.value);
                Assert.assertTrue("Betrag falsch", new BigDecimal("10").compareTo(l.value.getBigDecimalValue()) == 0);
                Assert.assertEquals("Betrag-Waehrung falsch", "EUR",l.value.getCurr());
                Assert.assertEquals("Valuta falsch",DF.parse("2018-07-21"),l.valuta);
            }

            {
                UmsLine l = lines.get(1);
                Assert.assertNull("additional falsch",l.additional);
                Assert.assertEquals("addkey falsch","000",l.addkey);
                Assert.assertEquals("Buchungsdatum falsch",DF.parseObject("2018-07-20"),l.bdate);
                Assert.assertNull("charge_value falsch",l.charge_value);
                Assert.assertEquals("customerref falsch","NONREF",l.customerref);
                Assert.assertEquals("gvcode falsch","152",l.gvcode);
                Assert.assertEquals("id falsch","2018-07-20-07.51.28.370057",l.id);
                Assert.assertNull("instref falsch",l.instref);
                Assert.assertTrue("Buchung nicht als CAMT-Buchung markiert",l.isCamt);
                Assert.assertTrue("Buchung nicht als SEPA-Buchung markiert",l.isSepa);
                Assert.assertFalse("Buchung ist kein Storno",l.isStorno);
                Assert.assertNull("orig_value falsch",l.orig_value);
                Assert.assertNotNull("Gegenkonto fehlt",l.other);
                Assert.assertEquals("Gegenkonto IBAN falsch","DE12345678901234567892",l.other.iban);
                Assert.assertEquals("Gegenkonto BIC falsch","ABCDEFG3ABC",l.other.bic);
                Assert.assertEquals("Gegenkonto Name falsch","Bert Bezahler",l.other.name);
                Assert.assertEquals("primanota falsch","9201",l.primanota);
                Assert.assertEquals("purposecode falsch","DEPT",l.purposecode);
                Assert.assertNotNull("Saldo fehlt",l.saldo);
                Assert.assertTrue("Saldo falsch", new BigDecimal("110.50").compareTo(l.saldo.value.getBigDecimalValue()) == 0);
                Assert.assertEquals("Saldodatum falsch", DF.parse("2018-07-20"), l.saldo.timestamp);
                Assert.assertEquals("Saldo-Waehrung falsch", "EUR",l.saldo.value.getCurr());
                Assert.assertEquals("Text falsch","EINZAHLUNG",l.text);
                Assert.assertNotNull("Verwendungszweck fehlt",l.usage);
                Assert.assertEquals("Anzahl Verwendungszwecke falsch",1,l.usage.size());
                Assert.assertEquals("Verwendungszweck falsch","Verwendungszweck 2",l.usage.get(0));
                Assert.assertNotNull("Betrag fehlt",l.value);
                Assert.assertTrue("Betrag falsch", new BigDecimal("0.50").compareTo(l.value.getBigDecimalValue()) == 0);
                Assert.assertEquals("Betrag-Waehrung falsch", "EUR",l.value.getCurr());
                Assert.assertEquals("Valuta falsch",DF.parse("2018-07-22"),l.valuta);
            }
        }
        finally
        {
            if (is1 != null)
                is1.close();
            if (is2 != null)
                is2.close();
        }
    }

    /**
     * Testet das Lesen einer Ruecklastschrift.
     * @throws Exception
     */
    @Test
    public void test005() throws Exception
    {
        final String file = "test-camt-ruecklastschrift.xml";
        InputStream is1 = null;
        InputStream is2 = null;
        try
        {
            is1 = this.getStream(file);
            SepaVersion version = SepaVersion.autodetect(is1);
            ISEPAParser<List<BTag>> parser = SEPAParserFactory.get(version);

            is2 = this.getStream(file);
            GVRKUms ums = new GVRKUms();
            parser.parse(is2, ums.getDataPerDay());

            List<BTag> days = ums.getDataPerDay();
            Assert.assertEquals("Anzahl Buchungstage falsch", 1, days.size());

            BTag day = days.get(0);
            List<UmsLine> lines = day.lines;


            {
                UmsLine l = lines.get(0);
                Assert.assertEquals("Gegenkonto IBAN falsch","DES1234567890",l.other.iban);
                Assert.assertEquals("Gegenkonto BIC falsch","TESTS1234",l.other.bic);
                Assert.assertEquals("Gegenkonto Name falsch","Sven Schuldner",l.other.name);
                Assert.assertNotNull("orig_value ist null", l.orig_value);
                Assert.assertEquals("orig_value ist falsch", 0, new BigDecimal("50").compareTo(l.orig_value.getBigDecimalValue()));
                Assert.assertEquals("Grund für Rückbuchung falsch", "RUECKLASTSCHRIFT Sonstige Gruende", l.additional);
            }
        }
        finally
        {
            if (is1 != null)
                is1.close();
            if (is2 != null)
                is2.close();
        }
    }
    
    /**
     * Testet das Lesen einer CAMT-Datei 052.001.08 mit einer Lastschrift.
     * @throws Exception
     */
    @Test
    public void test006() throws Exception
    {
        final String file = "test-camt-parse-05200108.xml";
        InputStream is1 = null;
        InputStream is2 = null;
        try
        {
            is1 = this.getStream(file);
            SepaVersion version = SepaVersion.autodetect(is1);
            ISEPAParser<List<BTag>> parser = SEPAParserFactory.get(version);

            is2 = this.getStream(file);
            GVRKUms ums = new GVRKUms();
            parser.parse(is2, ums.getDataPerDay());

            List<BTag> days = ums.getDataPerDayUnbooked();
            Assert.assertEquals("Anzahl Tage ungebuchte Umsaetze falsch", 0, days.size());

            days = ums.getDataPerDay();
            Assert.assertEquals("Anzahl Buchungstage falsch", 1, days.size());

            BTag day = days.get(0);
            List<UmsLine> lines = day.lines;
            Assert.assertEquals("Anzahl Buchungen falsch", 1, lines.size());

            Assert.assertTrue("Startsaldo falsch", new BigDecimal("100").compareTo(day.start.value.getBigDecimalValue()) == 0);
            Assert.assertTrue("Endsaldo falsch", new BigDecimal("66").compareTo(day.end.value.getBigDecimalValue()) == 0);
            Assert.assertEquals("Startdatum falsch", DF.parse("2023-11-08"), day.start.timestamp);
            Assert.assertEquals("Enddatum falsch", DF.parse("2023-11-10"), day.end.timestamp);
            Assert.assertEquals("Startsaldo Waehrung falsch", "EUR",day.start.value.getCurr());
            Assert.assertEquals("Endsaldo Waehrung falsch", "EUR",day.end.value.getCurr());

            Assert.assertNotNull("Konto fehlt",day.my);
            Assert.assertEquals("IBAN falsch","DE12345678901234567890",day.my.iban);
            Assert.assertEquals("BIC falsch","ABCDEFG1ABC",day.my.bic);
            Assert.assertEquals("Waehrung falsch","EUR",day.my.curr);
            
            Assert.assertEquals("Starttyp falsch",'F',day.starttype);
            Assert.assertEquals("Endtyp falsch",'F',day.endtype);

            {
                UmsLine l = lines.get(0);
                Assert.assertNull("additional falsch",l.additional);
                Assert.assertEquals("addkey falsch","992",l.addkey);
                Assert.assertEquals("Buchungsdatum falsch",DF.parseObject("2023-11-10"),l.bdate);
                Assert.assertNull("charge_value falsch",l.charge_value);
                Assert.assertEquals("customerref falsch","2023-11-10-00.06.42.329883",l.customerref);
                Assert.assertEquals("gvcode falsch","105",l.gvcode);
                Assert.assertEquals("id falsch","2023-11-10-00.06.42.329883",l.id);
                Assert.assertNull("instref falsch",l.instref);
                Assert.assertTrue("Buchung nicht als CAMT-Buchung markiert",l.isCamt);
                Assert.assertTrue("Buchung nicht als SEPA-Buchung markiert",l.isSepa);
                Assert.assertFalse("Buchung ist kein Storno",l.isStorno);
                Assert.assertNull("orig_value falsch",l.orig_value);
                Assert.assertNotNull("Gegenkonto fehlt",l.other);
                Assert.assertEquals("Gegenkonto IBAN falsch","DE12345678901234567892",l.other.iban);
                Assert.assertEquals("Gegenkonto BIC falsch","ABCDEFG1CBA",l.other.bic);
                Assert.assertEquals("Gegenkonto Name falsch","Beispiel AG",l.other.name);
                Assert.assertEquals("Gegenkonto GläubigerID falsch", "DE46ZZZ00000012345", l.other.creditorid);
                Assert.assertEquals("primanota falsch","9200",l.primanota);
                Assert.assertNull("purposecode falsch",l.purposecode);
                Assert.assertNotNull("Saldo fehlt",l.saldo);
                Assert.assertTrue("Saldo falsch", new BigDecimal("66").compareTo(l.saldo.value.getBigDecimalValue()) == 0);
                Assert.assertEquals("Saldodatum falsch", DF.parse("2023-11-10"), l.saldo.timestamp);
                Assert.assertEquals("Saldo-Waehrung falsch", "EUR",l.saldo.value.getCurr());
                Assert.assertEquals("Text falsch","FOLGELASTSCHRIFT",l.text);
                Assert.assertNotNull("Verwendungszweck fehlt",l.usage);
                Assert.assertEquals("Anzahl Verwendungszwecke falsch",1,l.usage.size());
                Assert.assertEquals("Verwendungszweck falsch","Verwendungszweck",l.usage.get(0));
                Assert.assertNotNull("Betrag fehlt",l.value);
                Assert.assertTrue("Betrag falsch", new BigDecimal("-34").compareTo(l.value.getBigDecimalValue()) == 0);
                Assert.assertEquals("Betrag-Waehrung falsch", "EUR",l.value.getCurr());
                Assert.assertEquals("Valuta falsch",DF.parse("2023-11-10"),l.valuta);
            }
        }
        finally
        {
            if (is1 != null)
                is1.close();
            if (is2 != null)
                is2.close();
        }
    }

    /**
     * Testet das Parsen mit einem fehlenden Saldo-Datum.
     * @throws Exception
     */
    @Test
    public void test007() throws Exception
    {
      InputStream is = null;
      try
      {
        final ISEPAParser<List<BTag>> parser = SEPAParserFactory.get(SepaVersion.CAMT_052_001_08);
        is = this.getStream("test-camt-parse-5200108-missing-date.xml");
        GVRKUms ums = new GVRKUms();
        parser.parse(is, ums.getDataPerDay());
      }
      finally
      {
        IOUtils.close(is);
      }
    }    

    /**
     * Testet das Parsen mit einem ungültigen Saldo (zu viele Nachkommastellen).
     * @throws Exception
     */
    @Test
    public void test008() throws Exception
    {
      InputStream is = null;
      try
      {
        final ISEPAParser<List<BTag>> parser = SEPAParserFactory.get(SepaVersion.CAMT_052_001_08);
        is = this.getStream("test-camt-parse-5200108-invalid-saldo.xml");
        GVRKUms ums = new GVRKUms();
        parser.parse(is, ums.getDataPerDay());
      }
      finally
      {
        IOUtils.close(is);
      }
    }    
}
