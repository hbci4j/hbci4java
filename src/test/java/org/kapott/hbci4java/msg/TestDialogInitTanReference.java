package org.kapott.hbci4java.msg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.passport.HBCIPassportPinTanMemory;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci4java.callback.HBCICallbackTest;
import org.kapott.hbci4java.comm.CommDummy;

/**
 * Testet, dass das HKTAN der Dialog-Initialisierung bei PIN/TAN-Management-Geschaeftsvorfaellen
 * deren Segmentkennung traegt (Kapitel B.4.3.1 und B.4.3.1.4).
 */
@RunWith(Parameterized.class)
public class TestDialogInitTanReference
{
  private static final String BPD =
      "HNHBK:1:3+000000000000+300+0+1+0:1'" +
      "HIRMG:2:2+0020::Auftrag ausgefuehrt.'" +
      "HIBPA:3:3+1+280:12345678+Testbank+0+1+300+9999'" +
      "HIPINS:4:1:3+1+1+0+5:50:6:::HKTAN:N:HKTSY:N:HKTAB:N:HKPAE:N'" +
      "HITANS:5:7:3+1+1+0+N:N:0:921:2:PushTan1-Push:Decoupled::PushTan1-Push:::PushTan1-Push:999:N:1:N:0:2:N:J:00:2:N:9:60:5:5:N:J'" +
      "HITSYS:6:1:3+1+1+0+N:N'" +
      "HITABS:7:4:3+1+1+0'" +
      "HIPAES:8:1:3+1+1+0'" +
      "HNHBS:9:1+1'";

  private final String job;
  private final String segcode;
  private final List<String> messages = new ArrayList<String>();
  private HBCIPassportPinTan passport = null;

  /**
   * Liefert die Geschaeftsvorfaelle und die erwartete Segmentkennung im HKTAN.
   * @return die Testparameter.
   */
  @Parameters(name = "{0}")
  public static Collection<Object[]> data()
  {
    return Arrays.asList(new Object[][] {
      {"TANGeneratorSync", "HKTSY"},
      {"TANMediaList", "HKTAB"},
      {"ChangePIN", "HKPAE"},
    });
  }

  /**
   * ct.
   * @param job der Geschaeftsvorfall.
   * @param segcode die erwartete Segmentkennung im HKTAN.
   */
  public TestDialogInitTanReference(String job, String segcode)
  {
    this.job = job;
    this.segcode = segcode;
  }

  /**
   * Prueft die Segmentkennung im HKTAN der Dialog-Initialisierung.
   * @throws Exception
   */
  @Test
  public void test() throws Exception
  {
    try (HBCIHandler handler = new HBCIHandler("300", this.passport, true))
    {
      HBCIJob j = handler.newJob(this.job);
      if (this.job.equals("TANGeneratorSync"))
      {
        j.setParam("atc", "12345");
        j.setParam("tan", "987654");
      }
      else if (this.job.equals("ChangePIN"))
      {
        j.setParam("newpin", "54321");
      }
      j.addToQueue();
      handler.execute();
    }
    catch (Exception e)
    {
      // Es gibt keine Antwort der Bank. Uns interessiert nur die erste Nachricht.
    }

    Assert.assertFalse("no message sent", this.messages.isEmpty());
    String init = this.messages.get(0);
    Assert.assertTrue(init, init.contains("HKIDN:"));
    Assert.assertTrue(init, init.contains("HKTAN:") && init.contains("+4+" + this.segcode + "+"));
  }

  /**
   * Erzeugt das Passport-Objekt mit BPD, UPD und Kundensystem-ID.
   * @throws Exception
   */
  @Before
  public void before() throws Exception
  {
    final HBCICallbackTest callback = new HBCICallbackTest();
    callback.put(HBCICallback.NEED_BLZ, "12345678");
    callback.put(HBCICallback.NEED_COUNTRY, "DE");
    callback.put(HBCICallback.NEED_HOST, "https://fints-demobank.local/fints/test123");
    callback.put(HBCICallback.NEED_PORT, "443");
    callback.put(HBCICallback.NEED_FILTER, "Base64");
    callback.put(HBCICallback.NEED_USERID, "1234567890");
    callback.put(HBCICallback.NEED_CUSTOMERID, "1234567890");
    callback.put(HBCICallback.NEED_PT_PIN, "12345");
    callback.put(HBCICallback.NEED_CONNECTION, "");
    callback.put(HBCICallback.CLOSE_CONNECTION, "");
    callback.put(HBCICallback.NEED_PT_SECMECH, "921");
    callback.put(HBCICallback.NEED_PT_TANMEDIA, "foo");

    final Properties props = new Properties();
    props.put("log.loglevel.default", Integer.toString(HBCIUtils.LOG_INFO));
    HBCIUtils.init(props, callback);

    this.passport = new HBCIPassportPinTanMemory(null) {
      private CommDummy comm = null;

      @Override
      public Comm getCommInstance()
      {
        if (this.comm == null)
          this.comm = new CommDummy(this);
        return this.comm;
      }
    };
    this.passport.setHBCIVersion("300");
    this.passport.setBPD(parseBPD());
    this.passport.setUPD(new Properties());
    this.passport.setSysId("1234567890ABCDEF");
    this.passport.setCurrentTANMethod("921");

    ((CommDummy) this.passport.getComm()).onPing(s -> this.messages.add(s));
  }

  /**
   * Schliesst das Passport-Objekt.
   * @throws Exception
   */
  @After
  public void after() throws Exception
  {
    try
    {
      if (this.passport != null)
        this.passport.close();
    }
    finally
    {
      HBCIUtils.done();
    }
  }

  private static Properties parseBPD()
  {
    String data = BPD.replace("000000000000", String.format("%012d", BPD.length()));
    HBCIKernelImpl kernel = new HBCIKernelImpl(null, "300");
    kernel.rawNewMsg("Synch");
    MSG msg = new MSG("SynchRes", data, data.length(), kernel.getMsgGen());
    Hashtable<String, String> values = new Hashtable<String, String>();
    msg.extractValues(values);

    Properties bpd = new Properties();
    for (Map.Entry<String, String> e : values.entrySet())
    {
      if (e.getKey().startsWith("SynchRes.BPD."))
        bpd.setProperty(e.getKey().substring("SynchRes.BPD.".length()), e.getValue());
    }
    return bpd;
  }
}
