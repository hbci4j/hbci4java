package org.kapott.hbci4java.msg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci4java.AbstractTest;

public class TestKontoauszugUebersicht extends AbstractTest
{
    @Test
    public void testLowlevelSyntax()
    {
        MsgGen generator = new HBCIKernelImpl(null, "300").getMsgGen();

        assertEquals(List.of("1", "2", "3"), generator.getLowlevelGVs().get("KontoauszugUebersicht"));
        assertContains(generator.getGVParameterNames("KontoauszugUebersicht", "1"),
            "My.number", "maxentries", "offset");
        assertContains(generator.getGVParameterNames("KontoauszugUebersicht", "2"),
            "My.iban", "My.bic", "maxentries", "offset");
        assertContains(generator.getGVResultNames("KontoauszugUebersicht", "3"), "number", "acknowledgement",
            "retrievable", "year", "date", "time", "creationtype", "documentid");
    }

    private static void assertContains(List<String> actual, String... expected)
    {
        for (String value : expected)
        {
            assertTrue("Missing lowlevel field " + value + " in " + actual, actual.contains(value));
        }
    }
}
