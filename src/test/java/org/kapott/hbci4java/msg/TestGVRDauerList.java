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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.GV_Result.GVRDauerList.Dauer;
import org.kapott.hbci4java.AbstractTest;

/**
 * Tests fuer das Datum der naechsten Ausfuehrung und die Uhrzeit der Ausfuehrung
 * eines Dauerauftrags. Der Termin wird ausschliesslich von der Bank geliefert -
 * er wird weder berechnet noch ersetzt.
 */
public class TestGVRDauerList extends AbstractTest
{
    /**
     * Der von der Bank gelieferte Termin wird uebernommen.
     */
    @Test
    public void testNextDateFromBank()
    {
        Dauer dauer=monthly("2099-01-15");

        dauer.setNextDate("2099-12-15");

        Assert.assertEquals("2099-12-15",format(dauer.nextdate));
    }

    /**
     * "1999-01-01" ist in SEPA-Nachrichten der Platzhalter fuer "kein Datum gesetzt".
     */
    @Test
    public void testUndefinedNextDate()
    {
        Dauer dauer=monthly("2099-01-15");

        dauer.setNextDate("1999-01-01");

        Assert.assertNull(dauer.nextdate);
    }

    /**
     * Ein unlesbarer Termin wird verworfen.
     */
    @Test
    public void testUnreadableNextDate()
    {
        Dauer dauer=monthly("2099-01-15");

        dauer.setNextDate("kein Datum");

        Assert.assertNull(dauer.nextdate);
    }

    /**
     * Der Termin der naechsten Ausfuehrung kann nicht vor der ersten Ausfuehrung liegen.
     */
    @Test
    public void testNextDateBeforeFirstExecution()
    {
        Dauer dauer=monthly("2099-01-15");

        dauer.setNextDate("2098-12-15");

        Assert.assertNull(dauer.nextdate);
    }

    /**
     * Der Termin der naechsten Ausfuehrung kann nicht nach der letzten Ausfuehrung liegen.
     */
    @Test
    public void testNextDateAfterLastExecution()
    {
        Dauer dauer=monthly("2099-01-15");
        dauer.lastdate=date("2099-06-30");

        dauer.setNextDate("2099-12-15");

        Assert.assertNull(dauer.nextdate);
    }

    /**
     * Ein Termin in der Vergangenheit wird verworfen.
     */
    @Test
    public void testNextDateInThePast()
    {
        Dauer dauer=monthly("2020-01-15");

        dauer.setNextDate("2020-02-15");

        Assert.assertNull(dauer.nextdate);
    }

    /**
     * Liefert die Bank keinen Termin, bleibt das Datum unbekannt.
     */
    @Test
    public void testMissingNextDate()
    {
        Dauer dauer=monthly("2099-01-15");

        dauer.setNextDate(null);

        Assert.assertNull(dauer.nextdate);
    }

    /**
     * Die Uhrzeit der Ausfuehrung wird uebernommen.
     */
    @Test
    public void testExecTime()
    {
        Dauer dauer=monthly("2099-01-15");

        dauer.setExecTime("12:00:00");

        Assert.assertEquals("12:00:00",dauer.exectime);
    }

    /**
     * Eine unbrauchbare Uhrzeit wird verworfen.
     */
    @Test
    public void testInvalidExecTime()
    {
        Dauer dauer=monthly("2099-01-15");

        dauer.setExecTime("25:00:00");

        Assert.assertNull(dauer.exectime);
    }

    /**
     * Liefert die Bank keine Uhrzeit, bleibt sie unbekannt.
     */
    @Test
    public void testMissingExecTime()
    {
        Dauer dauer=monthly("2099-01-15");

        dauer.setExecTime(null);

        Assert.assertNull(dauer.exectime);
    }

    private static Dauer monthly(String firstdate)
    {
        Dauer dauer=new Dauer();
        dauer.firstdate=date(firstdate);
        dauer.timeunit="M";
        dauer.turnus=1;
        dauer.execday=15;
        return dauer;
    }

    private static Date date(String value)
    {
        try
        {
            return new SimpleDateFormat("yyyy-MM-dd").parse(value);
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    private static String format(Date value)
    {
        return value==null?null:new SimpleDateFormat("yyyy-MM-dd").format(value);
    }
}
