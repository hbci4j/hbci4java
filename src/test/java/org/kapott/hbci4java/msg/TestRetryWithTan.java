package org.kapott.hbci4java.msg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRSaldoReq;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.passport.HBCIPassportPinTanMemory;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.status.HBCIRetVal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci4java.callback.HBCICallbackTest;
import org.kapott.hbci4java.comm.CommDummy;

/**
 * Testet, dass ein Auftrag, fuer den laut HIPINS keine TAN erforderlich ist, nach 9075
 * genau einmal mit HKTAN wiederholt wird.
 * Siehe https://github.com/hbci4j/hbci4java/issues/155
 */
public class TestRetryWithTan
{
  private static final String BPD =
      "HNHBK:1:3+000000000000+300+0+1+0:1'" +
      "HIRMG:2:2+0020::Auftrag ausgefuehrt.'" +
      "HIBPA:3:3+1+280:12345678+Testbank+0+1+300+9999'" +
      "HIPINS:4:1:3+1+1+0+5:50:6:::HKTAN:N:HKSAL:N:HKWPD:N:HKKAZ:N:HKCCS:N:HKVPP:N:HKVPA:N'" +
      "HITANS:5:7:3+1+1+0+N:N:0:921:2:PushTan1-Push:Decoupled::PushTan1-Push:::PushTan1-Push:999:N:1:N:0:2:N:J:00:2:N:9:60:5:5:N:J'" +
      "HISALS:6:6:3+1+1+0'" +
      "HIWPDS:7:6:3+1+1+0+N:N:N'" +
      "HIKAZS:8:6:3+1+1+0+90:N:N'" +
      "HICCSS:9:1:3+1+1+0'" +
      "HISPAS:10:1:3+1+1+0+J:N:J:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.09'" +
      "HIVPPS:11:1:3+1+0+0+1000:N:V:J:N:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.002.001.10:HKCCS'" +
      "HNHBS:12:1+1'";

  private static final String DIALOG_INIT = "HIRMG:2:2+0020::Dialoginitialisierung erfolgreich.+3076::Keine starke Authentifizierung erforderlich.'" +
                                            "HIRMS:3:2:5+3076::Keine starke Authentifizierung erforderlich.'" +
                                            "HITAN:4:7:5+4++noref+nochallenge'";
  private static final String DIALOG_END  = "HIRMG:2:2+0100::Dialog beendet.'";

  private final List<String> messages = new ArrayList<String>();
  private HBCIPassportPinTan passport = null;
  private int maxGVSegsPerMsg = 1;
  private HBCIJob saldo = null;
  private HBCIJob depot = null;
  private HBCIJob umsatz = null;

  /**
   * Bank meldet 9075 zu HKSAL. HKSAL wird in einer neuen Nachricht mit HKTAN wiederholt, HKWPD nicht.
   * Bei PIN/TAN steht jeder Auftrag in einer eigenen Nachricht.
   * @throws Exception
   */
  @Test
  public void testRetryWithTan() throws Exception
  {
    this.execute("SaldoReq,WPDepotList",
        response(2,"HIRMG:2:2+9050::Nachricht teilweise fehlerhaft.'" +
                   "HIRMS:3:2:3+9075::Starke Authentifizierung erforderlich.'"),
        response(3,"HIRMG:2:2+0010::Nachricht entgegengenommen.'" +
                   "HIRMS:3:2:3+0020::Auftrag ausgefuehrt.'"),
        response(4,"HIRMG:2:2+3060::Teilweise liegen Warnungen/Hinweise vor.'" +
                   "HIRMS:3:2:3+0020::Auftrag ausgefuehrt.'" +
                   "HIRMS:4:2:4+3076::Keine starke Authentifizierung erforderlich.'" +
                   "HISAL:5:6:3+1234567890::280:12345678+Girokonto+EUR+C:100,:EUR:20261117'" +
                   "HITAN:6:7:4+4++noref+nochallenge'"),
        response(5,DIALOG_END));

    Assert.assertEquals(this.messages.toString(),5,this.messages.size());
    assertJobs(1,"HKSAL");
    assertJobs(2,"HKWPD");
    assertJobs(3,"HKSAL","HKTAN");
    Assert.assertTrue(this.messages.get(3),this.messages.get(3).contains("+4+HKSAL+"));
    Assert.assertEquals(2,count("HKSAL"));
    Assert.assertEquals(1,count("HKWPD"));

    Assert.assertTrue(this.saldo.getJobResult().getJobStatus().toString(),this.saldo.getJobResult().isOK());
    Assert.assertTrue(this.depot.getJobResult().isOK());
    Assert.assertTrue(codes(this.saldo).isEmpty());
    GVRSaldoReq.Info[] entries = ((GVRSaldoReq) this.saldo.getJobResult()).getEntries();
    Assert.assertEquals(1,entries.length);
    Assert.assertEquals(0,new BigDecimal("100").compareTo(entries[0].ready.value.getBigDecimalValue()));
  }

  /**
   * Bank meldet in derselben Nachricht 9075 zu HKSAL und 0020 zu HKWPD. Nur HKSAL wird wiederholt.
   * Damit beide Auftraege in einer Nachricht stehen, hebt der Test die Grenze von einem Auftrag pro Nachricht auf.
   * @throws Exception
   */
  @Test
  public void testRetryOnlyRejectedTask() throws Exception
  {
    this.maxGVSegsPerMsg = 0;
    this.execute("SaldoReq,WPDepotList",
        response(2,"HIRMG:2:2+9050::Nachricht teilweise fehlerhaft.'" +
                   "HIRMS:3:2:3+9075::Starke Authentifizierung erforderlich.'" +
                   "HIRMS:4:2:4+0020::Auftrag ausgefuehrt.'"),
        response(3,"HIRMG:2:2+3060::Teilweise liegen Warnungen/Hinweise vor.'" +
                   "HIRMS:3:2:3+0020::Auftrag ausgefuehrt.'" +
                   "HIRMS:4:2:4+3076::Keine starke Authentifizierung erforderlich.'" +
                   "HITAN:5:7:4+4++noref+nochallenge'"),
        response(4,DIALOG_END));

    Assert.assertEquals(this.messages.toString(),4,this.messages.size());
    assertJobs(1,"HKSAL","HKWPD");
    assertJobs(2,"HKSAL","HKTAN");
    Assert.assertEquals(1,count("HKWPD"));
    Assert.assertTrue(this.saldo.getJobResult().isOK());
    // Der Auftrag selbst ist OK. Die 9050 der Nachricht steht weiterhin im globalen Status.
    Assert.assertTrue(this.depot.getJobResult().getJobStatus().isOK());
  }

  /**
   * Bank meldet auch mit HKTAN wieder 9075. Der Auftrag wird nicht noch einmal gesendet.
   * @throws Exception
   */
  @Test
  public void testRetryOnlyOnce() throws Exception
  {
    this.execute("SaldoReq",
        response(2,"HIRMG:2:2+9050::Nachricht teilweise fehlerhaft.'" +
                   "HIRMS:3:2:3+9075::Starke Authentifizierung erforderlich.'"),
        response(3,"HIRMG:2:2+9050::Nachricht teilweise fehlerhaft.'" +
                   "HIRMS:3:2:3+9075::Starke Authentifizierung erforderlich.'" +
                   "HIRMS:4:2:4+3076::Keine starke Authentifizierung erforderlich.'" +
                   "HITAN:5:7:4+4++noref+nochallenge'"),
        response(4,DIALOG_END));

    Assert.assertEquals(this.messages.toString(),4,this.messages.size());
    assertJobs(1,"HKSAL");
    assertJobs(2,"HKSAL","HKTAN");
    Assert.assertFalse(this.saldo.getJobResult().isOK());
    Assert.assertEquals(List.of("9075"),codes(this.saldo));
  }

  /**
   * Bank meldet 9075 zusammen mit 9800. Der Dialog ist beendet, der Auftrag wird nicht wiederholt.
   * @throws Exception
   */
  @Test
  public void testNoRetryAfterDialogEnd() throws Exception
  {
    this.execute("SaldoReq",
        response(2,"HIRMG:2:2+9050::Nachricht teilweise fehlerhaft.+9800::Dialog abgebrochen.'" +
                   "HIRMS:3:2:3+9075::Starke Authentifizierung erforderlich.'"),
        response(3,DIALOG_END));

    Assert.assertEquals(this.messages.toString(),3,this.messages.size());
    assertJobs(1,"HKSAL");
    Assert.assertEquals(1,count("HKSAL"));
    Assert.assertFalse(this.saldo.getJobResult().isOK());
    Assert.assertEquals(List.of("9075"),codes(this.saldo));
  }

  /**
   * Bank liefert Seite 1 der Umsaetze mit 3040 und meldet zu Seite 2 9075. Seite 2 wird mit dem
   * Aufsetzpunkt aus der 3040 und mit HKTAN wiederholt. Keine Seite geht verloren oder kommt doppelt.
   * @throws Exception
   */
  @Test
  public void testRetryWithOffset() throws Exception
  {
    this.execute("KUmsAll",
        response(2,"HIRMG:2:2+3060::Teilweise liegen Warnungen/Hinweise vor.'" +
                   "HIRMS:3:2:3+3040::Es liegen weitere Informationen vor.:OFFSET1'" +
                   "HIKAZ:4:6:3+" + bin(mt940(1,"Test 1")) + "'"),
        response(3,"HIRMG:2:2+9050::Nachricht teilweise fehlerhaft.'" +
                   "HIRMS:3:2:3+9075::Starke Authentifizierung erforderlich.'"),
        response(4,"HIRMG:2:2+3060::Teilweise liegen Warnungen/Hinweise vor.'" +
                   "HIRMS:3:2:3+0020::Auftrag ausgefuehrt.'" +
                   "HIRMS:4:2:4+3076::Keine starke Authentifizierung erforderlich.'" +
                   "HIKAZ:5:6:3+" + bin(mt940(2,"Test 2")) + "'" +
                   "HITAN:6:7:4+4++noref+nochallenge'"),
        response(5,DIALOG_END));

    Assert.assertEquals(this.messages.toString(),5,this.messages.size());
    assertJobs(1,"HKKAZ");
    Assert.assertFalse(this.messages.get(1).contains("OFFSET1"));
    assertJobs(2,"HKKAZ");
    Assert.assertTrue(this.messages.get(2).contains("OFFSET1"));
    assertJobs(3,"HKKAZ","HKTAN");
    Assert.assertTrue(this.messages.get(3).contains("OFFSET1"));
    Assert.assertTrue(this.messages.get(3).contains("+4+HKKAZ+"));

    Assert.assertTrue(this.umsatz.getJobResult().isOK());
    final List<String> usages = ((GVRKUms) this.umsatz.getJobResult()).getFlatData().stream().map(l -> String.join("",l.usage)).collect(Collectors.toList());
    Assert.assertEquals(List.of("Test 1","Test 2"),usages);
  }

  /**
   * Bei der Wiederholung verlangt die Bank eine echte Freigabe (decoupled). Das HKTAN wird mit 0030 und
   * Auftragsreferenz beantwortet, der Saldo kommt mit der Antwort auf das zweite HKTAN.
   * @throws Exception
   */
  @Test
  public void testRetryWithSCA() throws Exception
  {
    this.execute("SaldoReq",
        response(2,"HIRMG:2:2+9050::Nachricht teilweise fehlerhaft.'" +
                   "HIRMS:3:2:3+9075::Starke Authentifizierung erforderlich.'"),
        response(3,"HIRMG:2:2+3060::Teilweise liegen Warnungen/Hinweise vor.'" +
                   "HIRMS:3:2:4+0030::Auftrag empfangen - Sicherheitsfreigabe erforderlich.'" +
                   "HITAN:4:7:4+4++ORDERREF1+Bitte in der App freigeben.'"),
        response(4,"HIRMG:2:2+0010::Nachricht entgegengenommen.'" +
                   "HIRMS:3:2:3+0020::Auftrag ausgefuehrt.'" +
                   "HISAL:4:6:3+1234567890::280:12345678+Girokonto+EUR+C:100,:EUR:20261117'" +
                   "HITAN:5:7:3+S++ORDERREF1'"),
        response(5,DIALOG_END));

    Assert.assertEquals(this.messages.toString(),5,this.messages.size());
    assertJobs(1,"HKSAL");
    assertJobs(2,"HKSAL","HKTAN");
    assertJobs(3,"HKTAN");
    Assert.assertTrue(this.messages.get(3),this.messages.get(3).contains("+S++++ORDERREF1+"));

    Assert.assertTrue(this.saldo.getJobResult().getJobStatus().toString(),this.saldo.getJobResult().isOK());
    Assert.assertNotNull(this.saldo.getJobResult().getSegNum());
    final GVRSaldoReq.Info[] entries = ((GVRSaldoReq) this.saldo.getJobResult()).getEntries();
    Assert.assertEquals(1,entries.length);
    Assert.assertEquals(0,new BigDecimal("100").compareTo(entries[0].ready.value.getBigDecimalValue()));
  }

  /**
   * Auftraege mit VoP und das HKVPP werden nach 9075 nicht wiederholt.
   * Den Auftrag sendet der VoP-Ablauf zusammen mit dem HKVPA ohnehin erneut.
   * @throws Exception
   */
  @Test
  public void testNoRetryWithVoP() throws Exception
  {
    try (HBCIHandler handler = new HBCIHandler("300",this.passport,true))
    {
      final HBCIMsgStatus status = new HBCIMsgStatus();
      status.segStatus.addRetVal(new HBCIRetVal("3",null,null,"9075","Starke Authentifizierung erforderlich.",new String[0]));

      final HBCIJobImpl transfer = (HBCIJobImpl) handler.newJob("UebSEPA");
      Assert.assertTrue(this.passport.shouldRetryWithTan(transfer,status,3));

      transfer.vopApplied();
      Assert.assertFalse(this.passport.shouldRetryWithTan(transfer,status,3));

      final HBCIJobImpl vop = (HBCIJobImpl) handler.newJob("VoP");
      Assert.assertEquals("HKVPP",vop.getHBCICode());
      Assert.assertFalse(this.passport.shouldRetryWithTan(vop,status,3));
    }
  }

  /**
   * Fuehrt die Auftraege gegen die vorgegebenen Antworten aus.
   * @param jobs die Namen der Auftraege, kommagetrennt.
   * @param responses die Antworten der Bank nach der Dialog-Initialisierung.
   * @throws Exception
   */
  private void execute(String jobs, String... responses) throws Exception
  {
    final CommDummy comm = (CommDummy) this.passport.getComm();
    comm.addResponse(response(1,DIALOG_INIT));
    for (String r:responses)
      comm.addResponse(r);

    final Konto k = new Konto("DE","12345678","1234567890");
    k.iban = "DE02120300000000202051";
    k.bic = "BYLADEM1001";
    k.curr = "EUR";

    try (HBCIHandler handler = new HBCIHandler("300",this.passport,true))
    {
      for (String name:jobs.split(","))
      {
        final HBCIJob job = handler.newJob(name);
        job.setParam("my",k);
        job.addToQueue();
        if (name.equals("SaldoReq"))
          this.saldo = job;
        else if (name.equals("WPDepotList"))
          this.depot = job;
        else
          this.umsatz = job;
      }
      handler.execute();
    }
  }

  /**
   * Prueft die Auftragssegmente in der Nachricht.
   * @param idx der Index der gesendeten Nachricht.
   * @param codes die erwarteten Segmentkennungen.
   */
  private void assertJobs(int idx, String... codes)
  {
    final String msg = this.messages.get(idx);
    for (String code:new String[]{"HKSAL","HKWPD","HKKAZ","HKTAN"})
    {
      Assert.assertEquals(code + " in " + msg,List.of(codes).contains(code),msg.contains(code + ":"));
    }
  }

  /**
   * Zaehlt die gesendeten Nachrichten mit dem Segment.
   * @param code die Segmentkennung.
   * @return die Anzahl der Nachrichten.
   */
  private int count(String code)
  {
    return (int) this.messages.stream().filter(m -> m.contains(code + ":")).count();
  }

  /**
   * Liefert die Rueckmeldecodes des Auftrags.
   * @param job der Auftrag.
   * @return die Rueckmeldecodes.
   */
  private static List<String> codes(HBCIJob job)
  {
    return List.of(job.getJobResult().getJobStatus().getErrors()).stream().map((HBCIRetVal r) -> r.code).collect(Collectors.toList());
  }

  /**
   * Erzeugt Umsaetze im MT940-Format mit einer Buchung.
   * @param betrag der Betrag der Buchung.
   * @param usage der Verwendungszweck.
   * @return die Umsaetze.
   */
  private static String mt940(int betrag, String usage)
  {
    return "\r\n:20:STARTUMS\r\n:25:12345678/1234567890\r\n:28C:" + betrag + "\r\n" +
           ":60F:C261116EUR100,00\r\n" +
           ":61:2611171117CR" + betrag + ",00NTRFNONREF\r\n" +
           ":86:166?00GUTSCHRIFT?20" + usage + "\r\n" +
           ":62F:C261117EUR" + (100 + betrag) + ",00\r\n-\r\n";
  }

  /**
   * Kodiert die Daten als binaeres Datenelement.
   * @param data die Daten.
   * @return das binaere Datenelement.
   */
  private static String bin(String data)
  {
    return "@" + data.length() + "@" + data;
  }

  /**
   * Erzeugt eine Antwortnachricht der Bank mit den Segmenten im verschluesselten Teil.
   * @param msgnum die Nachrichtennummer.
   * @param segments die Segmente ab Segmentnummer 2.
   * @return die Antwortnachricht.
   */
  private static String response(int msgnum, String segments)
  {
    final int last = segments.split("'").length + 1;
    final String data = "HNVSK:998:3+PIN:1+998+1+2::1234567890ABCDEF+1:20261117:120000+2:2:13:@8@        :5:1+280:12345678:1234567890:V:0:0+0'" +
                        "HNVSD:999:1+@" + segments.length() + "@" + segments + "'" +
                        "HNHBS:" + (last + 1) + ":1+" + msgnum + "'";
    final String tail = "+300+DIALOG1+" + msgnum + "+DIALOG1:" + msgnum + "'";
    final int length = "HNHBK:1:3+".length() + 12 + tail.length() + data.length();
    return "HNHBK:1:3+" + String.format("%012d",length) + tail + data;
  }

  /**
   * Erzeugt das Passport-Objekt mit BPD und Kundensystem-ID.
   * @throws Exception
   */
  @Before
  public void before() throws Exception
  {
    final HBCICallbackTest callback = new HBCICallbackTest();
    callback.put(HBCICallback.NEED_BLZ,"12345678");
    callback.put(HBCICallback.NEED_COUNTRY,"DE");
    callback.put(HBCICallback.NEED_HOST,"https://fints-demobank.local/fints/test123");
    callback.put(HBCICallback.NEED_PORT,"443");
    callback.put(HBCICallback.NEED_FILTER,"Base64");
    callback.put(HBCICallback.NEED_USERID,"1234567890");
    callback.put(HBCICallback.NEED_CUSTOMERID,"1234567890");
    callback.put(HBCICallback.NEED_PT_PIN,"12345");
    callback.put(HBCICallback.NEED_CONNECTION,"");
    callback.put(HBCICallback.CLOSE_CONNECTION,"");
    callback.put(HBCICallback.NEED_PT_SECMECH,"921");
    callback.put(HBCICallback.NEED_PT_TANMEDIA,"foo");
    callback.put(HBCICallback.NEED_PT_DECOUPLED,"");

    final Properties props = new Properties();
    props.put("log.loglevel.default",Integer.toString(HBCIUtils.LOG_INFO));
    HBCIUtils.init(props,callback);

    this.passport = new HBCIPassportPinTanMemory(null) {
      private CommDummy comm = null;

      @Override
      public Comm getCommInstance()
      {
        if (this.comm == null)
          this.comm = new CommDummy(this);
        return this.comm;
      }

      @Override
      public int getMaxGVSegsPerMsg()
      {
        return maxGVSegsPerMsg;
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
    String data = BPD.replace("000000000000",String.format("%012d",BPD.length()));
    HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
    kernel.rawNewMsg("Synch");
    MSG msg = new MSG("SynchRes",data,data.length(),kernel.getMsgGen());
    Hashtable<String,String> values = new Hashtable<String,String>();
    msg.extractValues(values);

    Properties bpd = new Properties();
    for (Map.Entry<String,String> e:values.entrySet())
    {
      if (e.getKey().startsWith("SynchRes.BPD."))
        bpd.setProperty(e.getKey().substring("SynchRes.BPD.".length()),e.getValue());
    }
    return bpd;
  }
}
