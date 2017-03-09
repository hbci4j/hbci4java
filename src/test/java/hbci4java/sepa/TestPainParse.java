/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.sepa;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.sepa.PainVersion;

import hbci4java.AbstractTest;

/**
 * Testet das Parsen von Pain XML-Dateien - ohne HBCI-Context.
 */
public class TestPainParse extends AbstractTest
{
    /**
     * Testet das korrekte Ermitteln der PAIN-Version aus dem XML-Dokument.
     * @throws Exception
     */
    @Test
    public void test001() throws Exception
    {
        Map<String,PainVersion> files = new HashMap<String,PainVersion>()
        {{
            put("sepa/test-pain-parse-00100102.xml",PainVersion.PAIN_001_001_02);
            put("sepa/test-pain-parse-00100202.xml",PainVersion.PAIN_001_002_02);
            put("sepa/test-pain-parse-00100203.xml",PainVersion.PAIN_001_002_03);
            put("sepa/test-pain-parse-00100303.xml",PainVersion.PAIN_001_003_03);
            put("sepa/test-pain-parse-00100103.xml",PainVersion.PAIN_001_001_03);
        }};
        
        for (Entry<String,PainVersion> entry:files.entrySet())
        {
            InputStream is = null;
            try
            {
                is = this.getStream(entry.getKey());
                PainVersion version = PainVersion.autodetect(is);
                Assert.assertEquals(version,entry.getValue());
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
            is = this.getStream("sepa/test-pain-parse-none.xml");
            PainVersion version = PainVersion.autodetect(is);
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
            is = this.getStream("sepa/test-pain-parse-invalid.xml");
            PainVersion.autodetect(is);
            Assert.fail();
        }
        catch (IllegalArgumentException iae)
        {
            Assert.assertEquals(iae.getClass(),IllegalArgumentException.class); // eigentlich redundant aber egal.
        }
        finally
        {
            if (is != null)
                is.close();
        }
    }
}
