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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.manager.HBCIKernelImpl;
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

        List<String> restrictions = kernel.getLowlevelJobRestrictionNames("KreditkartenUmsatz","2");
        Assert.assertTrue(restrictions.contains("timerange"));
        Assert.assertTrue(restrictions.contains("canmaxentries"));
        Assert.assertTrue(restrictions.contains("canrange"));
    }
}
