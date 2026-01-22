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

package org.hbci4java.hbci.sepa;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import org.hbci4java.hbci.GV.generators.ISEPAGenerator;
import org.hbci4java.hbci.GV.generators.SEPAGeneratorFactory;
import org.hbci4java.hbci.sepa.SepaVersion;
import org.hbci4java.hbci.sepa.SepaVersion.Type;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testet das pure Generieren von SEPA XML-Dateien - ohne HBCI-Context.
 */
public class TestSepaGen
{
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

        for (SepaVersion version:SepaVersion.getKnownVersions(Type.PAIN_001))
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
        props.setProperty("targetdate",     "2013-11-30");
        props.setProperty("type",           "CORE");

        for (SepaVersion version:SepaVersion.getKnownVersions(Type.PAIN_008))
        {
            // Der Test schlaegt automatisch fehl, wenn die Schema-Validierung nicht klappt
            ISEPAGenerator gen = SEPAGeneratorFactory.get("LastSEPA", version);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gen.generate(props, bos, true);
        }
    }

    /**
     * Testet das Erstellen von SEPA-Multi-Ueberweisungen.
     * @throws Exception
     */
    @Test
    public void test003() throws Exception
    {
        Properties props = new Properties();
        props.setProperty("src.bic",    "ABCDEFAA123");
        props.setProperty("src.iban",   "DE1234567890");
        props.setProperty("src.name",   "Max Mustermann");
        props.setProperty("sepaid",     "abcde");

        props.setProperty("dst[0].bic",    "ABCDEFAA123");
        props.setProperty("dst[0].iban",   "DE0987654321");
        props.setProperty("dst[0].name",   "SEPAstian");
        props.setProperty("btg[0].value",  "100.00");
        props.setProperty("btg[0].curr",   "EUR");
        props.setProperty("usage[0]",      "Verwendungszweck");
        props.setProperty("endtoendid[0]", "fghij");

        props.setProperty("dst[1].bic",    "ABCDEFBB456");
        props.setProperty("dst[1].iban",   "DE5432109876");
        props.setProperty("dst[1].name",   "BICole");
        props.setProperty("btg[1].value",  "150.00");
        props.setProperty("btg[1].curr",   "EUR");
        props.setProperty("usage[1]",      "Verwendungszweck 2");
        props.setProperty("endtoendid[1]", "fghij");

        for (SepaVersion version:SepaVersion.getKnownVersions(Type.PAIN_001))
        {
            // Der Test schlaegt automatisch fehl, wenn die Schema-Validierung nicht klappt
            ISEPAGenerator gen = SEPAGeneratorFactory.get("UebSEPA", version);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gen.generate(props, bos, true);
        }
    }

    /**
     * Testet das Erstellen von SEPA-Multi-Lastschriften.
     * @throws Exception
     */
    @Test
    public void test004() throws Exception
    {
        Properties props = new Properties();
        props.setProperty("src.bic",        "ABCDEFAA123");
        props.setProperty("src.iban",       "DE1234567890");
        props.setProperty("src.name",       "Max Mustermann");
        props.setProperty("sepaid",         "abcde");
        props.setProperty("sequencetype",   "FRST");
        props.setProperty("targetdate",     "2013-11-30");
        props.setProperty("type",           "CORE");

        props.setProperty("dst[0].bic",        "ABCDEFAA123");
        props.setProperty("dst[0].iban",       "DE0987654321");
        props.setProperty("dst[0].name",       "SEPAstian");
        props.setProperty("btg[0].value",      "100.00");
        props.setProperty("btg[0].curr",       "EUR");
        props.setProperty("mandateid[0]",      "0987654321");
        props.setProperty("manddateofsig[0]",  "2013-11-23");
        props.setProperty("usage[0]",          "Verwendungszweck");
        props.setProperty("amendmandindic[0]", "false");
        props.setProperty("endtoendid[0]",     "fghij");
        props.setProperty("creditorid[0]",     "DE1234567890");

        props.setProperty("dst[1].bic",        "ABCDEFBB456");
        props.setProperty("dst[1].iban",       "DE5432109876");
        props.setProperty("dst[1].name",       "BICole");
        props.setProperty("btg[1].value",      "150.00");
        props.setProperty("btg[1].curr",       "EUR");
        props.setProperty("mandateid[1]",      "5432109876");
        props.setProperty("manddateofsig[1]",  "2013-11-23");
        props.setProperty("usage[1]",          "Verwendungszweck 2");
        props.setProperty("amendmandindic[1]", "false");
        props.setProperty("endtoendid[1]",     "fghij");
        props.setProperty("creditorid[1]",     "DE1234567890");

        for (SepaVersion version:SepaVersion.getKnownVersions(Type.PAIN_008))
        {
            // Der Test schlaegt automatisch fehl, wenn die Schema-Validierung nicht klappt
            ISEPAGenerator gen = SEPAGeneratorFactory.get("LastSEPA", version);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gen.generate(props, bos, true);
        }
    }

    /**
     * Testet das Mappen des SEPA-Type "B2B" auf die passende Enum.
     * Siehe https://www.willuhn.de/bugzilla/show_bug.cgi?id=1458
     * @throws Exception
     */
    @Test
    public void test005() throws Exception
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
        props.setProperty("targetdate",     "2013-11-30");
        props.setProperty("type",           "B2B");

        for (SepaVersion version:SepaVersion.getKnownVersions(Type.PAIN_008))
        {
            // Der Test schlaegt automatisch fehl, wenn die Schema-Validierung nicht klappt
            ISEPAGenerator gen = SEPAGeneratorFactory.get("LastSEPA", version);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gen.generate(props, bos, true);
        }
    }

    /**
     * Testet die korrekte Codierung von Umlauten im erzeugten PAIN-Dokument.
     * @throws Exception
     */
    @Test
    public void test006() throws Exception
    {
        String umlaute = "üöäÜÖÄ";
        Properties props = new Properties();
        props.setProperty("src.bic",    "ABCDEFAA123");
        props.setProperty("src.iban",   "DE1234567890");
        props.setProperty("src.name",   umlaute);
        props.setProperty("dst.bic",    "ABCDEFAA123");
        props.setProperty("dst.iban",   "DE0987654321");
        props.setProperty("dst.name",   "SEPAstian");
        props.setProperty("btg.value",  "100.00");
        props.setProperty("btg.curr",   "EUR");
        props.setProperty("usage",      "Verwendungszweck");
        props.setProperty("sepaid",     "abcde");
        props.setProperty("endtoendid", "fghij");

        for (SepaVersion version:SepaVersion.getKnownVersions(Type.PAIN_001))
        {
            ISEPAGenerator gen = SEPAGeneratorFactory.get("UebSEPA", version);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            gen.generate(props, bos, true);
            String xml = bos.toString(ISEPAGenerator.ENCODING);
            Assert.assertTrue(xml.contains(umlaute));
        }
    }

}
