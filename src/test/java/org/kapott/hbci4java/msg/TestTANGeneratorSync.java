package org.kapott.hbci4java.msg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.List;

import org.junit.Test;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci4java.AbstractTest;

/**
 * Testet die Syntax von HKTSY und HITSYS.
 */
public class TestTANGeneratorSync extends AbstractTest
{
  /**
   * Prueft Segmentversion, Auftragsfelder und BPD-Restriktionen.
   */
  @Test
  public void testLowlevelSyntax()
  {
    MsgGen generator = new HBCIKernelImpl(null, "300").getMsgGen();

    assertEquals(List.of("1"), generator.getLowlevelGVs().get("TANGeneratorSync"));
    assertEquals(List.of("atc", "tan", "cardnumber", "cardseqnumber"),
        generator.getGVParameterNames("TANGeneratorSync", "1"));
    assertEquals(List.of("needcardnumber", "needcardseqnumber"),
        generator.getGVRestrictionNames("TANGeneratorSync", "1"));
  }

  /**
   * Prueft, dass HITSYS in den BPD erkannt wird.
   */
  @Test
  public void testParseBPD() throws Exception
  {
    String data =
        "HNHBK:1:3+000000000000+300+0+1+0:1'" +
        "HIRMG:2:2+0020::Auftrag ausgefuehrt.'" +
        "HIBPA:3:3+1+280:12030000+Testbank+1+1+300'" +
        "HITSYS:4:1:3+1+1+0+J:N'" +
        "HNHBS:5:1+1'";
    data = data.replace("000000000000", String.format("%012d", data.length()));

    HBCIKernelImpl kernel = new HBCIKernelImpl(null, "300");
    kernel.rawNewMsg("Synch");
    MSG msg = new MSG("SynchRes", data, data.length(), kernel.getMsgGen());

    Hashtable<String, String> values = new Hashtable<String, String>();
    msg.extractValues(values);

    String prefix = "SynchRes.BPD.Params.TANGeneratorSyncPar1.";
    assertEquals("HITSYS", values.get(prefix + "SegHead.code"));
    assertEquals("1", values.get(prefix + "maxnum"));
    assertEquals("1", values.get(prefix + "minsigs"));
    assertEquals("0", values.get(prefix + "secclass"));
    assertEquals("J", values.get(prefix + "ParTANGeneratorSync.needcardnumber"));
    assertEquals("N", values.get(prefix + "ParTANGeneratorSync.needcardseqnumber"));
  }
}
