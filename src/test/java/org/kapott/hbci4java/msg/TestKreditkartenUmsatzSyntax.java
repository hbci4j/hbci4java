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

import java.util.Hashtable;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.exceptions.ParseErrorException;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci4java.AbstractTest;

/**
 * Tests fuer die DKKKU/DIKKU-Syntaxregistrierung.
 */
public class TestKreditkartenUmsatzSyntax extends AbstractTest
{
    /**
     * Prueft Parameterreihenfolge, Ergebnisfelder und BPD-Restriktionen.
     */
    @Test
    public void testSyntax()
    {
        HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
        Assert.assertTrue(kernel.getAllLowlevelJobs().get("KreditkartenUmsatz").contains("2"));

        List<String> params = kernel.getLowlevelJobParameterNames("KreditkartenUmsatz","2");
        Assert.assertTrue(params.contains("cardnumber"));
        Assert.assertTrue(params.contains("cardsubnumber"));
        Assert.assertTrue(params.indexOf("my.number") < params.indexOf("cardnumber"));
        Assert.assertTrue(params.indexOf("cardnumber") < params.indexOf("cardsubnumber"));
        Assert.assertTrue(params.indexOf("cardsubnumber") < params.indexOf("startdate"));
        Assert.assertTrue(params.indexOf("startdate") < params.indexOf("enddate"));
        Assert.assertTrue(params.indexOf("enddate") < params.indexOf("maxentries"));
        Assert.assertTrue(params.indexOf("maxentries") < params.indexOf("offset"));

        List<String> result = kernel.getLowlevelJobResultNames("KreditkartenUmsatz","2");
        Assert.assertTrue(result.contains("transactions.bookingdate"));
        Assert.assertTrue(result.contains("transactions.value"));
        Assert.assertTrue(result.contains("transactions.detail9"));
        Assert.assertTrue(result.contains("transactions.detail10"));
        Assert.assertTrue(result.contains("transactions.detail11"));

        List<String> restrictions = kernel.getLowlevelJobRestrictionNames("KreditkartenUmsatz","2");
        Assert.assertTrue(restrictions.contains("timerange"));
        Assert.assertTrue(restrictions.contains("canmaxentries"));
        Assert.assertTrue(restrictions.contains("canrange"));
    }

    /**
     * Prueft das Wire-Format einer anonymisierten realen DIKKU-Antwort.
     */
    @Test
    public void testResponse() throws Exception
    {
        String data =
            "HNHBK:1:3+000000000000+300+1234567890+1+1234567890:1'" +
            "HIRMG:2:2+0010::Nachricht entgegengenommen.'" +
            "HIRMS:3:2:3+0020::Auftrag ausgefuehrt.'" +
            "DIKKU:4:2:3+5555000011112222++D:1234,56:EUR:20260719+++" +
            "5555000011112222:20260717:20260718::12,34:EUR:D:1,:12,34:EUR:D:" +
            "EXAMPLE SHOP:BERLIN 555500******2233::::::::J:1000000000000001:5411'" +
            "HNHBS:5:1+1'";
        data = data.replace("000000000000",String.format("%012d",data.length()));

        HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
        kernel.rawNewMsg("KreditkartenUmsatz");
        MsgGen gen = kernel.getMsgGen();
        MSG msg = new MSG("CustomMsgRes",data,data.length(),gen);

        Hashtable<String,String> values = new Hashtable<String,String>();
        msg.extractValues(values);
        Assert.assertTrue(values.containsValue("EXAMPLE SHOP"));
        Assert.assertTrue(values.containsValue("5411"));
    }

    /**
     * Prueft die DIKKU-Variante der Berliner Sparkasse mit zusaetzlichem Feld.
     */
    @Test
    public void testBerlinSparkasseResponse() throws Exception
    {
        String data =
            "HNHBK:1:3+000000000000+300+1234567890+1+1234567890:1'" +
            "HIRMG:2:2+0010::Nachricht entgegengenommen.'" +
            "HIRMS:3:2:3+0020::Auftrag ausgefuehrt.'" +
            "DIKKU:4:2:3+5555000011112222++C:0,:EUR:20260922+20260904++" +
            "5555000011112222:20260827:20260831::27,99:EUR:D:1,:27,99:EUR:D:" +
            "EXAMPLE SHOP IRELAND:::::::::J:20262430027631940001:3246:20260904+" +
            "5555000011112222:20251209:20251210::75,:CAD:D:1,608206:47,53:EUR:D:" +
            "EXAMPLE SHOP CANADA:75,00 CAD, EURO-KURS  1,608206::::::::J:" +
            "20253440012930940001:9399:20260104::inkl. 1,90% Einsatz Fremdw. EUR   0,89-+" +
            "5555000011112222:20260904:20260904::27,99:EUR:C:1,:27,99:EUR:C:" +
            "Einzug des Rechnungsbetrages:::::::::J:26247000001130310001::20260904'" +
            "HNHBS:5:1+1'";
        data = data.replace("000000000000",String.format("%012d",data.length()));

        HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
        kernel.rawNewMsg("KreditkartenUmsatz");
        MsgGen gen = kernel.getMsgGen();
        MSG msg = new MSG("CustomMsgRes",data,data.length(),gen);

        Hashtable<String,String> values = new Hashtable<String,String>();
        msg.extractValues(values);
        Assert.assertTrue(values.containsValue("EXAMPLE SHOP IRELAND"));
        Assert.assertTrue(values.containsValue("EXAMPLE SHOP CANADA"));
        Assert.assertTrue(values.containsValue("Einzug des Rechnungsbetrages"));
        Assert.assertTrue(values.containsValue("3246"));
        Assert.assertEquals(2L,values.entrySet().stream()
            .filter(e -> e.getKey().endsWith(".detail9"))
            .filter(e -> "20260904".equals(e.getValue()))
            .count());
        Assert.assertEquals(1L,values.entrySet().stream()
            .filter(e -> e.getKey().endsWith(".detail11"))
            .filter(e -> "inkl. 1,90% Einsatz Fremdw. EUR   0,89-".equals(e.getValue()))
            .count());
    }

    /**
     * Ein abweichendes DIKKU-Format darf nicht zu einem internen Indexfehler fuehren.
     */
    @Test(expected=ParseErrorException.class)
    public void testUnexpectedResponseElement() throws Exception
    {
        String data =
            "HNHBK:1:3+000000000000+300+1234567890+1+1234567890:1'" +
            "HIRMG:2:2+0010::Nachricht entgegengenommen.'" +
            "HIRMS:3:2:3+0020::Auftrag ausgefuehrt.'" +
            "DIKKU:4:2:3+5555000011112222++D:1234,56:EUR:20260719+++'" +
            "UNEXPECTED'";
        data = data.replace("000000000000",String.format("%012d",data.length()));

        HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
        kernel.rawNewMsg("KreditkartenUmsatz");
        MsgGen gen = kernel.getMsgGen();
        new MSG("CustomMsgRes",data,data.length(),gen);
    }
}
