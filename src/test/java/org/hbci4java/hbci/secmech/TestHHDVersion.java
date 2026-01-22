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

package org.hbci4java.hbci.secmech;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.hbci4java.hbci.AbstractTest;
import org.hbci4java.hbci.manager.HHDVersion;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests zum korrekten Parsen der HHD-Versionen.
 */
public class TestHHDVersion extends AbstractTest
{
    private final static List<TestData> testdata = new ArrayList<TestData>();
    
    /**
     * Initialisiert die Testdaten.
     */
    @BeforeClass
    public static void initClass()
    {
        // Daten stammen von der Sparkasse
        // {segversion=5, id=MS1.0.0, secfunc=907, name=photoTAN-Verfahren}
        // {segversion=5, id=CR#5 - 1.4, secfunc=904, name=chipTAN comfort, zkamethod_name=HHDOPT1, zkamethod_version=1.4}
        // {segversion=5, id=CR#6 - 1.4, secfunc=905, name=chipTAN comfort manuell, zkamethod_name=HHD, zkamethod_version=1.4}
        // {segversion=3, id=HHD1.3.0, secfunc=910, name=chipTAN manuell}
        // {segversion=3, id=HHD1.3.0OPT, secfunc=911, name=chipTAN optisch}
        // {segversion=3, id=HHD1.3.0USB, secfunc=912, name=chipTAN-USB}
        // {segversion=3, id=HHD1.3.0QR, secfunc=913, name=chipTAN-QR}
        {
            TestData t = new TestData(HHDVersion.MS_1);
            t.props.put("id","MS1.0.0");
            t.props.put("segversion","5");
            testdata.add(t);
        }
        {
            TestData t = new TestData(HHDVersion.HHD_1_4);
            t.props.put("id","CR#5 - 1.4");
            t.props.put("segversion","5");
            t.props.put("zkamethod_version","1.4");
            testdata.add(t);
        }
        {
            TestData t = new TestData(HHDVersion.HHD_1_4);
            t.props.put("id","CR#6 - 1.4");
            t.props.put("segversion","5");
            t.props.put("zkamethod_version","1.4");
            testdata.add(t);
        }
        {
            TestData t = new TestData(HHDVersion.HHD_1_3);
            t.props.put("id","HHD1.3.0");
            t.props.put("segversion","3");
            testdata.add(t);
        }
        {
            TestData t = new TestData(HHDVersion.HHD_1_3);
            t.props.put("id","HHD1.3.0OPT");
            t.props.put("segversion","3");
            testdata.add(t);
        }
        {
            TestData t = new TestData(HHDVersion.HHD_1_3);
            t.props.put("id","HHD1.3.0USB");
            t.props.put("segversion","3");
            testdata.add(t);
        }
        {
            TestData t = new TestData(HHDVersion.QR_1_3);
            t.props.put("id","HHD1.3.0QR");
            t.props.put("segversion","3");
            testdata.add(t);
        }
        
        // So sollte QR-Code eigentlich aussehen
        {
            TestData t = new TestData(HHDVersion.QR_1_4);
            t.props.put("id","Q1S");
            t.props.put("segversion","5");
            testdata.add(t);
        }

        // smsTAN bei der HASPA - wurde versehentlich als QR-TAN erkannt.
        // https://homebanking-hilfe.de/forum/topic.php?t=22515&page=2
        // {zkamethod_name=mobileTAN, segversion=4, zkamethod_version=1.3.2, id=mTAN, name=smsTAN}
        {
            TestData t = new TestData(HHDVersion.HHD_1_3);
            t.props.put("id","mTAN");
            t.props.put("name","smsTAN");
            t.props.put("zkamethod_version","1.3.2");
            t.props.put("segversion","4");
            testdata.add(t);
        }

        // Konstruiertes Beispiel fuer smsTAN
        {
            TestData t = new TestData(HHDVersion.HHD_1_3);
            t.props.put("id","mTAN");
            t.props.put("zkamethod_version","1.3");
            testdata.add(t);
        }

        {
            TestData t = new TestData(HHDVersion.DECOUPLED);
            t.props.put("zkamethod_name","Decoupled");
            testdata.add(t);
        }
        {
          TestData t = new TestData(HHDVersion.DECOUPLED);
          t.props.put("zkamethod_name","DecoupledPush");
          testdata.add(t);
      }

    }
    
    
    /**
     * Testet die korrekte Erkennung der HHD-Versionen.
     * @throws Exception
     */
    @Test
    public void test001() throws Exception
    {
        for (TestData t:testdata)
        {
            HHDVersion actual = HHDVersion.find(t.props);
            Assert.assertEquals("HHD-Version falsch erkannt",t.version,actual);
        }
    }
    
    /**
     * Enthaelt ein Test-Set.
     */
    private static class TestData
    {
        private HHDVersion version = null;
        private Properties props = new Properties();
        
        /**
         * ct.
         * @param version die Version.
         */
        private TestData(HHDVersion version)
        {
            this.version = version;
        }
    }
}


