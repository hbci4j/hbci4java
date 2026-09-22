package org.kapott.hbci4java.msg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci4java.AbstractTest;

public class TestInstUebSEPAStatus extends AbstractTest
{
  @Test
  public void testLowlevelSyntax()
  {
    MsgGen generator = new HBCIKernelImpl(null, "300").getMsgGen();

    assertEquals(List.of("1"), generator.getLowlevelGVs().get("InstUebSEPAStatus"));
    assertContains(generator.getGVParameterNames("InstUebSEPAStatus", "1"),
        "My.iban", "My.bic", "formats.format", "orderid");
    assertContains(generator.getGVResultNames("InstUebSEPAStatus", "1"),
        "My.iban", "sepadescr", "sepapain", "orderid", "ccode", "orderstatus");
    assertContains(generator.getGVRestrictionNames("InstUebSEPAStatus", "1"),
        "minwait", "suppformats");
  }

  private static void assertContains(List<String> actual, String... expected)
  {
    for (String value : expected)
    {
      assertTrue("Missing lowlevel field " + value + " in " + actual, actual.contains(value));
    }
  }
}
