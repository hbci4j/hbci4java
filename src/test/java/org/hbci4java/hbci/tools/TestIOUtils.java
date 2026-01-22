/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2019 Olaf Willuhn
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

package org.hbci4java.hbci.tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hbci4java.hbci.AbstractTest;
import org.hbci4java.hbci.tools.IOUtils;
import org.junit.Assert;
import org.junit.Test;

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


