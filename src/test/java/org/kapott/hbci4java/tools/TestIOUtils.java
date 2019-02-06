/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci4java.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.tools.IOUtils;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet Funktionen in IOUtils.
 */
public class TestIOUtils extends AbstractTest
{
    /**
     * Testet die automatische Konvertierung von unsicheren Dateinamen.
     * @throws Exception
     */
    @Test
    public void testSafeFilename() throws Exception
    {
        Map<String,String> tests = new HashMap<String,String>();
        tests.put("foobar.txt","foobar.txt");
        tests.put("123456789012345678901234567890","1234567890123456789012345");
        tests.put("abc&(%$-.txt","abc-.txt");
        
        for (Entry<String,String> e:tests.entrySet())
        {
            File f = new File(IOUtils.safeFilename(e.getKey()));
            Assert.assertEquals("Dateiname falsch",e.getValue(),f.getName());
        }
    }
}


