/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2026 Franz Bettag
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

package org.kapott.hbci4java.msg;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.GV_Result.GVRKreditkartenUmsatz;

/**
 * Tests fuer die Konvertierung der proprietaeren DIKKU-Umsatzfelder.
 */
public class TestGVRKreditkartenUmsatz
{
    /**
     * Prueft Datum, Vorzeichen, Originalbetrag und Beschreibung.
     */
    @Test
    public void testTransaction()
    {
        Properties data = new Properties();
        data.setProperty("tx.bookingdate","2026-05-20");
        data.setProperty("tx.valuedate","20260519");
        data.setProperty("tx.reference","test-reference");
        data.setProperty("tx.originalvalue","12.34");
        data.setProperty("tx.originalcurrency","USD");
        data.setProperty("tx.originalcreditdebit","D");
        data.setProperty("tx.value","10,50");
        data.setProperty("tx.currency","EUR");
        data.setProperty("tx.creditdebit","D");
        data.setProperty("tx.purpose","Test purchase");
        data.setProperty("tx.location","Test city");

        GVRKreditkartenUmsatz result = new GVRKreditkartenUmsatz();
        result.addTransaction(data,"tx");

        List<UmsLine> lines = result.getFlatData();
        Assert.assertEquals(1,lines.size());
        Assert.assertEquals(0,new BigDecimal("-10.50").compareTo(lines.get(0).value.getBigDecimalValue()));
        Assert.assertEquals(0,new BigDecimal("-12.34").compareTo(lines.get(0).orig_value.getBigDecimalValue()));
        Assert.assertEquals("test-reference",lines.get(0).customerref);
        Assert.assertEquals("Test purchase / Test city",lines.get(0).usage.get(0));
        Assert.assertTrue(lines.get(0).isCamt);
    }

    /**
     * Pflichtfelder muessen vorhanden sein.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testMissingAmount()
    {
        Properties data = new Properties();
        data.setProperty("tx.bookingdate","2026-05-20");
        data.setProperty("tx.valuedate","2026-05-20");
        new GVRKreditkartenUmsatz().addTransaction(data,"tx");
    }

    /**
     * Ungueltige Kalenderdaten duerfen nicht still normalisiert werden.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidDate()
    {
        Properties data = new Properties();
        data.setProperty("tx.bookingdate","2026-02-31");
        data.setProperty("tx.valuedate","2026-02-28");
        data.setProperty("tx.value","1.00");
        data.setProperty("tx.currency","EUR");
        new GVRKreditkartenUmsatz().addTransaction(data,"tx");
    }
}
