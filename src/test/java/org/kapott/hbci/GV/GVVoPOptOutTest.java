package org.kapott.hbci.GV;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Proxy;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kapott.hbci.bpd.VoPParameter;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.dialog.DialogContext;
import org.kapott.hbci.dialog.DialogEvent;
import org.kapott.hbci.dialog.HBCIMessage;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIDialog;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.protocol.MSG;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.status.HBCIRetVal;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Value;
import org.kapott.hbci4java.callback.HBCICallbackTest;

/**
 * Testet den Verzicht auf den Namensabgleich (VoP Opt-Out, HKVOO/HIVOO/HIVOOS).
 */
public class GVVoPOptOutTest
{
  private final static String IBAN = "DE02120300000000202051";
  private final static String VOPID = "VOPID123";
  private final static String TEXT = "Sie verzichten auf den Namensabgleich.";

  private HBCICallbackTest callback = null;

  /**
   * Initialisiert HBCI4Java mit einem Callback, der die VoP-Rückfrage beantwortet.
   */
  @Before
  public void before()
  {
    this.callback = new HBCICallbackTest();
    final Properties props = new Properties();
    props.put("log.loglevel.default",Integer.toString(HBCIUtils.LOG_INFO));
    HBCIUtils.init(props,this.callback);
  }

  /**
   * Beendet HBCI4Java.
   */
  @After
  public void after()
  {
    HBCIUtils.done();
  }

  /**
   * Prüft, dass Auftrag, Antwort und BPD-Parameter in der Syntax vorhanden sind.
   */
  @Test
  public void testLowlevelSyntax()
  {
    final MsgGen gen = new HBCIKernelImpl(null,"300").getMsgGen();
    assertEquals(List.of("1"),gen.getLowlevelGVs().get("VoPOptOut"));
    assertContains(gen.getGVResultNames("VoPOptOut","1"),"vopid","infotext");
    assertContains(gen.getGVRestrictionNames("VoPOptOut","1"),"infotextformatted","segcode");
  }

  /**
   * Prüft, dass der Job ein HKVOO ohne weitere Datenelemente erzeugt.
   */
  @Test
  public void testRequestSegment()
  {
    try (HBCIHandler handler = createHandler(true,"HKCCM","HKVOO"))
    {
      final HBCIJob<?> job = handler.newJob("VoPOptOut");
      assertTrue(job instanceof GVVoPOptOut);

      final GVVoPOptOut optOut = (GVVoPOptOut) job;
      assertEquals("HKVOO",optOut.getHBCICode());
      final SEG seg = optOut.createJobSegment(3);
      seg.validate();
      assertEquals("HKVOO:3:1'",seg.toString(0));
    }
  }

  /**
   * Prüft, dass ein HIVOO mit VoP-ID und Aufklärungstext gelesen werden kann.
   * Die VoP-ID hat laut Spezifikation keine Längenbegrenzung.
   */
  @Test
  public void testParseResponse()
  {
    final String vopid = "X".repeat(2000);
    final String data = "HNHBK:1:3+000000000000+300+DIALOG+2+DIALOG:2'" +
                        "HIRMG:2:2+3060::Teilweise liegen Warnungen vor.'" +
                        "HIRMS:3:2:3+3090::Aufklärungstext beachten'" +
                        "HIVOO:4:1:3+@" + vopid.length() + "@" + vopid + "+" + TEXT + "'" +
                        "HNHBS:5:1+2'";
    final HBCIKernelImpl kernel = new HBCIKernelImpl(null,"300");
    kernel.rawNewMsg("UebSEPA");
    final MSG msg = new MSG("CustomMsgRes",data,data.length(),kernel.getMsgGen());
    final Hashtable<String,String> values = new Hashtable<>();
    msg.extractValues(values);

    assertEquals("HIVOO",values.get("CustomMsgRes.GVRes.VoPOptOutRes1.SegHead.code"));
    assertEquals(vopid,values.get("CustomMsgRes.GVRes.VoPOptOutRes1.vopid"));
    assertEquals(TEXT,values.get("CustomMsgRes.GVRes.VoPOptOutRes1.infotext"));
  }

  /**
   * Prüft, dass das HKVPA eine VoP-ID mit mehr als 1024 Zeichen zurücksenden kann.
   */
  @Test
  public void testLongVopIdInAuth()
  {
    try (HBCIHandler handler = createHandler(true,"HKCCM","HKVOO"))
    {
      final String vopid = "X".repeat(2000);
      final GVVoPAuth auth = (GVVoPAuth) handler.newJob("VoPAuth");
      auth.setParam("vopid",vopid);
      final SEG seg = auth.createJobSegment(3);
      seg.validate();
      assertEquals("HKVPA:3:1+@2000@" + vopid + "'",seg.toString(0));
    }
  }

  /**
   * Prüft, dass Opt-Out nur bei Sammelaufträgen mit mehr als einer Transaktion zulässig ist.
   */
  @Test
  public void testIsAllowed()
  {
    try (HBCIHandler handler = createHandler(true,"HKCCM","HKVOO"))
    {
      assertFalse(GVVoPOptOut.isAllowed(createTransfer(handler,1)));
      assertTrue(GVVoPOptOut.isAllowed(createTransfer(handler,2)));
      assertFalse(GVVoPOptOut.isAllowed((HBCIJobImpl) handler.newJob("UebSEPA")));
    }
  }

  /**
   * Prüft, dass die Formatierung des Aufklärungstextes aus HIVOOS getrennt von HIVPPS gelesen wird.
   */
  @Test
  public void testOptOutInfoTextFormatted()
  {
    final Properties bpd = new Properties();
    bpd.setProperty("Params.VoPCheckPar1.ParVoPCheck.infotextformatted","N");
    bpd.setProperty("Params.VoPOptOutPar1.ParVoPOptOut.infotextformatted","J");
    final VoPParameter params = VoPParameter.parse(bpd);
    assertFalse(params.isInfoTextFormatted());
    assertTrue(params.isOptOutInfoTextFormatted());
  }

  /**
   * Prüft, dass bei gesetztem Parameter HKVOO statt HKVPP vor den Auftrag gesetzt wird.
   */
  @Test
  public void testPatchOptOut()
  {
    HBCIUtils.setParam("vop.HKCCM.optout","true");
    assertEquals("HKVOO",patch(createHandler(true,"HKCCM","HKVOO"),2));
  }

  /**
   * Prüft, dass ohne den Parameter wie bisher HKVPP gesendet wird.
   */
  @Test
  public void testPatchWithoutParameter()
  {
    assertEquals("HKVPP",patch(createHandler(true,"HKCCM","HKVOO"),2));
  }

  /**
   * Prüft, dass ein Sammelauftrag mit nur einer Transaktion trotz Parameter mit HKVPP gesendet wird.
   */
  @Test
  public void testPatchSingleTransaction()
  {
    HBCIUtils.setParam("vop.HKCCM.optout","true");
    assertEquals("HKVPP",patch(createHandler(true,"HKCCM","HKVOO"),1));
  }

  /**
   * Prüft, dass HKVPP gesendet wird, wenn HIVOOS den Auftrag nicht listet.
   */
  @Test
  public void testPatchNotInBPD()
  {
    HBCIUtils.setParam("vop.HKCCM.optout","true");
    assertEquals("HKVPP",patch(createHandler(false,"HKCCM","HKVOO"),2));
  }

  /**
   * Prüft, dass HKVPP gesendet wird, wenn HKVOO für das Konto in den UPD nicht erlaubt ist.
   */
  @Test
  public void testPatchNotAllowedForAccount()
  {
    HBCIUtils.setParam("vop.HKCCM.optout","true");
    assertEquals("HKVPP",patch(createHandler(true,"HKCCM"),2));
  }

  /**
   * Prüft, dass HKVPP gesendet wird, wenn die UPD für das Konto keine erlaubten Geschäftsvorfälle enthalten.
   */
  @Test
  public void testPatchNoAllowedGVs()
  {
    HBCIUtils.setParam("vop.HKCCM.optout","true");
    assertEquals("HKVPP",patch(createHandler(true),2));
  }

  /**
   * Prüft den Normalfall bei PIN/TAN: HKVPA geht zusammen mit dem HKTAN#2, der Auftrag wird nicht erneut gesendet.
   */
  @Test
  public void testConfirmWithTan()
  {
    this.callback.put(HBCICallback.HAVE_VOP_RESULT,"true");
    try (HBCIHandler handler = createHandler(true,"HKCCM","HKVOO"))
    {
      final HBCIJobImpl task = createTransfer(handler,2);
      final DialogContext ctx = createContext(handler,task);
      final GVTAN2Step hktan2 = (GVTAN2Step) handler.newJob("TAN2Step");
      hktan2.setTask(task);
      final HBCIMessage tanMsg = new HBCIMessage();
      tanMsg.append(hktan2);
      ctx.getDialog().getMessageQueue().append(tanMsg);

      final GVVoPOptOut job = createOptOut(handler,task,ctx);
      job.extractResults(createResponse(false),"GVRes",0);

      assertEquals(VOPID,job.getJobResult().getResult().getVopId());
      assertEquals(TEXT,job.getJobResult().getResult().getText());
      assertTrue(job.getJobResult().getResult().getItems().isEmpty());

      final List<HBCIMessage> messages = ctx.getDialog().getMessageQueue().getMessages();
      assertEquals(2,messages.size());
      assertEquals(1,messages.get(0).getTasks().size());
      assertSame(task,messages.get(0).getTasks().get(0));

      final List<HBCIJobImpl> tasks = messages.get(1).getTasks();
      assertEquals(2,tasks.size());
      assertAuth(tasks.get(0));
      assertSame(hktan2,tasks.get(1));
    }
  }

  /**
   * Prüft, dass nach RM 3945 HKVPA zusammen mit dem Auftrag erneut eingereicht wird.
   */
  @Test
  public void testConfirmAfter3945()
  {
    this.callback.put(HBCICallback.HAVE_VOP_RESULT,"true");
    try (HBCIHandler handler = createHandler(true,"HKCCM","HKVOO"))
    {
      final HBCIJobImpl task = createTransfer(handler,2);
      final DialogContext ctx = createContext(handler,task);
      final GVVoPOptOut job = createOptOut(handler,task,ctx);

      job.extractResults(createResponse(true),"GVRes",0);

      final List<HBCIJobImpl> tasks = ctx.getDialog().getMessageQueue().getMessages().get(0).getTasks();
      assertEquals(2,tasks.size());
      assertAuth(tasks.get(0));
      assertSame(task,tasks.get(1));
    }
  }

  /**
   * Prüft, dass bei PIN/TAN ohne HKTAN#2 das HKVPA allein gesendet wird.
   */
  @Test
  public void testConfirmWithoutTan()
  {
    this.callback.put(HBCICallback.HAVE_VOP_RESULT,"true");
    try (HBCIHandler handler = createHandler(true,"HKCCM","HKVOO"))
    {
      final HBCIJobImpl task = createTransfer(handler,2);
      final DialogContext ctx = createContext(handler,task);
      final GVVoPOptOut job = createOptOut(handler,task,ctx);

      job.extractResults(createResponse(false),"GVRes",0);

      final List<HBCIJobImpl> tasks = ctx.getDialog().getMessageQueue().getMessages().get(0).getTasks();
      assertEquals(1,tasks.size());
      assertAuth(tasks.get(0));
    }
  }

  /**
   * Prüft, wann der Auftrag erneut eingereicht werden muss: immer bei Kryptografie, bei PIN/TAN nur nach 3945.
   */
  @Test
  public void testNeedResubmit()
  {
    try (HBCIHandler handler = createHandler(true,"HKCCM","HKVOO"))
    {
      final HBCIPassportInternal pintan = (HBCIPassportInternal) handler.getPassport();
      assertFalse(GVVoPOptOut.needResubmit(pintan,createResponse(false)));
      assertTrue(GVVoPOptOut.needResubmit(pintan,createResponse(true)));

      final HBCIPassportInternal crypto = (HBCIPassportInternal) Proxy.newProxyInstance(getClass().getClassLoader(),
          new Class<?>[] {HBCIPassportInternal.class},(proxy,method,args) -> null);
      assertTrue(GVVoPOptOut.needResubmit(crypto,createResponse(false)));
    }
  }

  /**
   * Prüft, dass der Auftrag abgebrochen wird, wenn der User den Aufklärungstext nicht bestätigt.
   */
  @Test
  public void testDecline()
  {
    this.callback.put(HBCICallback.HAVE_VOP_RESULT,"false");
    try (HBCIHandler handler = createHandler(true,"HKCCM","HKVOO"))
    {
      final HBCIJobImpl task = createTransfer(handler,2);
      final DialogContext ctx = createContext(handler,task);
      final GVVoPOptOut job = createOptOut(handler,task,ctx);

      try
      {
        job.extractResults(createResponse(false),"GVRes",0);
        fail("expected HBCI_Exception");
      }
      catch (HBCI_Exception e)
      {
        // erwartet
      }

      assertEquals(1,ctx.getDialog().getMessageQueue().getMessages().size());
      assertEquals(1,ctx.getDialog().getMessageQueue().getMessages().get(0).getTasks().size());
      assertNull(((HBCIPassportPinTan) handler.getPassport()).getPersistentData(AbstractHBCIPassport.KEY_VOP_RESULT));
    }
  }

  /**
   * Führt das VoP-Patching für einen Sammelauftrag aus.
   * @param handler der Handler.
   * @param count die Anzahl der Transaktionen.
   * @return die Segment-Kennung des Jobs, der vor den Auftrag gesetzt wurde.
   */
  private static String patch(HBCIHandler handler, int count)
  {
    try (handler)
    {
      final HBCIJobImpl task = createTransfer(handler,count);
      final DialogContext ctx = createContext(handler,task);

      ((HBCIPassportPinTan) handler.getPassport()).onDialogEvent(DialogEvent.JOBS_CREATED,ctx);

      final List<HBCIJobImpl> tasks = ctx.getDialog().getMessageQueue().getMessages().get(0).getTasks();
      assertEquals(2,tasks.size());
      assertSame(task,tasks.get(1));
      return tasks.get(0).getHBCICode();
    }
  }

  /**
   * Prüft, dass der Job ein HKVPA mit der VoP-ID aus dem HIVOO ist.
   * @param job der Job.
   */
  private static void assertAuth(HBCIJobImpl job)
  {
    assertTrue(job instanceof GVVoPAuth);
    assertEquals("B" + VOPID,job.getLowlevelParams().getProperty("VoPAuth1.vopid"));
  }

  /**
   * Erzeugt einen SEPA-Sammelauftrag mit der angegebenen Anzahl Transaktionen.
   * @param handler der Handler.
   * @param count die Anzahl der Transaktionen.
   * @return der Auftrag.
   */
  private static HBCIJobImpl createTransfer(HBCIHandler handler, int count)
  {
    final HBCIJobImpl job = (HBCIJobImpl) handler.newJob("MultiUebSEPA");
    final Konto src = new Konto();
    src.iban = IBAN;
    src.bic = "BYLADEM1001";
    job.setParam("src",src);
    for (int i=0;i<count;i++)
    {
      final Konto dst = new Konto();
      dst.iban = IBAN;
      dst.bic = "BYLADEM1001";
      dst.name = "Empfaenger " + i;
      job.setParam("dst",i,dst);
      job.setParam("btg",i,new Value("1.00","EUR"));
      job.setParam("usage",i,"Test " + i);
    }
    return job;
  }

  /**
   * Erzeugt einen Dialog-Context mit dem Auftrag in der ersten Nachricht.
   * @param handler der Handler.
   * @param task der Auftrag.
   * @return der Dialog-Context.
   */
  private static DialogContext createContext(HBCIHandler handler, HBCIJobImpl task)
  {
    final HBCIDialog dialog = new HBCIDialog(handler);
    dialog.getMessageQueue().getLast().append(task);
    final DialogContext ctx = DialogContext.create((HBCIKernelImpl) handler.getKernel(),(HBCIPassportPinTan) handler.getPassport());
    ctx.setDialog(dialog);
    return ctx;
  }

  /**
   * Erzeugt den Opt-Out-Job für den Auftrag.
   * @param handler der Handler.
   * @param task der Auftrag.
   * @param ctx der Dialog-Context.
   * @return der Job.
   */
  private static GVVoPOptOut createOptOut(HBCIHandler handler, HBCIJobImpl task, DialogContext ctx)
  {
    final GVVoPOptOut job = (GVVoPOptOut) handler.newJob("VoPOptOut");
    job.setTask(task);
    job.setDialogContext(ctx);
    task.vopApplied();
    return job;
  }

  /**
   * Erzeugt die Antwortdaten eines HIVOO.
   * @param with3945 true, wenn die Bank 3945 zum HKTAN meldet.
   * @return die Antwortdaten.
   */
  private static HBCIMsgStatus createResponse(boolean with3945)
  {
    final Properties data = new Properties();
    data.setProperty("GVRes.SegHead.code","HIVOO");
    data.setProperty("GVRes.vopid",VOPID);
    data.setProperty("GVRes.infotext",TEXT);
    final HBCIMsgStatus status = new HBCIMsgStatus();
    status.setData(data);
    status.segStatus.addRetVal(new HBCIRetVal("4",null,null,"3090","Aufklärungstext beachten",new String[0]));
    if (with3945)
      status.segStatus.addRetVal(new HBCIRetVal("5",null,null,"3945","Freigabe kann nicht erteilt werden",new String[0]));
    return status;
  }

  /**
   * Erzeugt einen Handler mit BPD für HKCCM, HKVPP, HKTAN und optional HKVOO sowie UPD für das Konto.
   * @param listed true, wenn HIVOOS den HKCCM listet.
   * @param allowedGVs die im HIUPD erlaubten Geschäftsvorfälle des Kontos.
   * @return der Handler.
   */
  private static HBCIHandler createHandler(boolean listed, String... allowedGVs)
  {
    final HBCIPassportPinTan passport = new HBCIPassportPinTan(null,0);
    final Properties bpd = new Properties();
    bpd.setProperty("Params.SammelUebSEPAPar1.SegHead.code","HICCMS");
    bpd.setProperty("Params.UebSEPAPar1.SegHead.code","HICCSS");
    bpd.setProperty("Params.TAN2StepPar6.SegHead.code","HITANS");
    bpd.setProperty("Params.VoPCheckPar1.SegHead.code","HIVPPS");
    bpd.setProperty("Params.VoPCheckPar1.ParVoPCheck.segcode","HKCCM");
    bpd.setProperty("Params.VoPOptOutPar1.SegHead.code","HIVOOS");
    bpd.setProperty("Params.VoPOptOutPar1.ParVoPOptOut.infotextformatted","N");
    bpd.setProperty("Params.VoPOptOutPar1.ParVoPOptOut.segcode",listed ? "HKCCM" : "HKCME");
    passport.setBPD(bpd);

    final Properties upd = new Properties();
    upd.setProperty("KInfo.KTV.number","202051");
    upd.setProperty("KInfo.iban",IBAN);
    for (int i=0;i<allowedGVs.length;i++)
    {
      upd.setProperty((i == 0 ? "KInfo.AllowedGV" : "KInfo.AllowedGV_" + (i+1)) + ".code",allowedGVs[i]);
    }
    passport.setUPD(upd);
    passport.setHBCIVersion("300");
    return new HBCIHandler("300",passport,true);
  }

  private static void assertContains(List<String> actual, String... expected)
  {
    for (String value:expected)
    {
      assertTrue("Missing lowlevel field " + value + " in " + actual,actual.contains(value));
    }
  }
}
