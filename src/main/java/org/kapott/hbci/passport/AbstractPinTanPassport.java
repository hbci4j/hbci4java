/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
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

package org.kapott.hbci.passport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.kapott.hbci.GV.GVTAN2Step;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.dialog.DialogContext;
import org.kapott.hbci.dialog.DialogEvent;
import org.kapott.hbci.dialog.HBCIMessage;
import org.kapott.hbci.dialog.HBCIMessageQueue;
import org.kapott.hbci.dialog.KnownDialogTemplate;
import org.kapott.hbci.dialog.KnownReturncode;
import org.kapott.hbci.dialog.KnownTANProcess;
import org.kapott.hbci.dialog.KnownTANProcess.Variant;
import org.kapott.hbci.dialog.RawHBCIDialog;
import org.kapott.hbci.dialog.SCARequest;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.ChallengeInfo;
import org.kapott.hbci.manager.Feature;
import org.kapott.hbci.manager.HBCIDialog;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUser;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.HHDVersion;
import org.kapott.hbci.manager.HHDVersion.Type;
import org.kapott.hbci.manager.TanMethod;
import org.kapott.hbci.protocol.SEG;
import org.kapott.hbci.protocol.factory.SEGFactory;
import org.kapott.hbci.security.Crypt;
import org.kapott.hbci.security.Sig;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.status.HBCIRetVal;
import org.kapott.hbci.status.HBCIStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.tools.CryptUtils;
import org.kapott.hbci.tools.NumberUtil;
import org.kapott.hbci.tools.ParameterFinder;
import org.kapott.hbci.tools.ParameterFinder.Query;
import org.kapott.hbci.tools.StringUtil;

/**
 * Abstrakte Basis-Implementierung des PIN/TAN-Supports.
 */
public abstract class AbstractPinTanPassport extends AbstractHBCIPassport
{
    /**
     * Hier speichern wir zwischen, ob wir eine HKTAN-Anfrage in der Dialog-Initialisierung gesendet haben und wenn ja, welcher Prozess-Schritt es war
     */
    private final static String CACHE_KEY_SCA_STEP = "__sca_step__";

    /**
     * Hier speichern wir, ob wir eine SCA-Ausnahme fuer einen GV von der Bank erhalten haben
     */
    public final static String KEY_PD_SCA = "__pintan_sca___";

    /**
     * Hier speichern wir den Challenge-Text der Bank fuer die TAN-Abfrage.
     */
    public final static String KEY_PD_CHALLENGE = "__pintan_challenge___";

    /**
     * Hier speichern wir das HHDuc fuer die TAN-Abfrage.
     */
    public final static String KEY_PD_HHDUC = "__pintan_hhduc___";

    /**
     * Hier speichern wir die Auftragsreferenz fuer die TAN-Abfrage.
     */
    public final static String KEY_PD_ORDERREF = "__pintan_orderref___";

    private String certfile;
    private boolean checkCert;

    private String proxy;
    private String proxyuser;
    private String proxypass;

    private boolean verifyTANMode;
    
    // Das aktuell ausgewaehlte TAN-Verfahren
    private String tanMethod;
    
    // True, wenn das aktuelle TAN-Verfahren automatisch gewaehlt wurde
    private boolean tanMethodAutoSelected;
    
    // Die TAN-Verfahren fuer den User (aus dem 3920-Rueckmeldecode)
    private List<String> tanMethodsUser;

    // Die TAN-Verfahren mit den Parametern aus den BPD
    private Hashtable<String,Properties> tanMethodsBank;

    private String pin;

    // Die bisherige Anzahl von decoupled status refresh requests
    protected int decoupledRefreshes = 0;
    
    /**
     * ct.
     * @param initObject
     */
    public AbstractPinTanPassport(Object initObject)
    {
        super(initObject);

        this.tanMethodsBank=new Hashtable<String, Properties>();
        this.tanMethodsUser=new ArrayList<String>();
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportInternal#getPassportTypeName()
     */
    public String getPassportTypeName()
    {
        return "PinTan";
    }

    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#setBPD(java.util.Properties)
     */
    public void setBPD(Properties p)
    {
        super.setBPD(p);

        if (p!=null && p.size()!=0) {
            // hier die liste der verfügbaren sicherheitsverfahren aus den
            // bpd (HITANS) extrahieren

            tanMethodsBank.clear();
            
            // willuhn 2011-06-06 Maximal zulaessige HITANS-Segment-Version ermitteln
            // Hintergrund: Es gibt User, die nur HHD 1.3-taugliche TAN-Generatoren haben,
            // deren Banken aber auch HHD 1.4 beherrschen. In dem Fall wuerde die Bank
            // HITANS/HKTAN/HITAN in Segment-Version 5 machen, was in der Regel dazu fuehren
            // wird, dass HHD 1.4 zur Anwendung kommt. Das bewirkt, dass ein Flicker-Code
            // erzeugt wird, der vom TAN-Generator des Users gar nicht lesbar ist, da dieser
            // kein HHD 1.4 beherrscht. Mit dem folgenden Parameter kann die Maximal-Version
            // des HITANS-Segments nach oben begrenzt werden, so dass z.Bsp. HITANS5 ausgefiltert
            // wird.
            int maxAllowedVersion = Integer.parseInt(HBCIUtils.getParam("kernel.gv.HITANS.segversion.max","0"));

            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                String key=(String)e.nextElement();

                // p.getProperty("Params_x.TAN2StepParY.ParTAN2StepZ.TAN2StepParamsX_z.*")
                if (key.startsWith("Params")) {
                    String subkey=key.substring(key.indexOf('.')+1);
                    if (subkey.startsWith("TAN2StepPar")) {
                      
                        // willuhn 2011-05-13 Wir brauchen die Segment-Version, weil mittlerweile TAN-Verfahren
                        // mit identischer Sicherheitsfunktion in unterschiedlichen Segment-Versionen auftreten koennen
                        // Wenn welche mehrfach vorhanden sind, nehmen wir nur das aus der neueren Version
                        int segVersion = Integer.parseInt(subkey.substring(11,12));
                        
                        subkey=subkey.substring(subkey.indexOf('.')+1);
                        if (subkey.startsWith("ParTAN2Step") &&
                                subkey.endsWith(".secfunc"))
                        {
                            // willuhn 2011-06-06 Segment-Versionen ueberspringen, die groesser als die max. zulaessige sind
                            if (maxAllowedVersion > 0 && segVersion > maxAllowedVersion)
                            {
                              HBCIUtils.log("skipping segversion " + segVersion + ", larger than allowed version " + maxAllowedVersion, HBCIUtils.LOG_DEBUG);
                              continue;
                            }

                            String secfunc=p.getProperty(key);

                            // willuhn 2011-05-13 Checken, ob wir das Verfahren schon aus einer aktuelleren Segment-Version haben
                            Properties prev = tanMethodsBank.get(secfunc);
                            if (prev != null)
                            {
                              // Wir haben es schonmal. Mal sehen, welche Versionsnummer es hat
                              int prevVersion = Integer.parseInt(prev.getProperty("segversion"));
                              if (prevVersion > segVersion)
                              {
                                HBCIUtils.log("found another twostepmech " + secfunc + " in segversion " + segVersion + ", already have one in segversion " + prevVersion + ", ignoring segversion " + segVersion, HBCIUtils.LOG_DEBUG);
                                continue;
                              }
                            }
                            
                            Properties entry=new Properties();
                            
                            // willuhn 2011-05-13 Wir merken uns die Segment-Version in dem Zweischritt-Verfahren
                            // Daran koennen wir erkennen, ob wir ein mehrfach auftretendes
                            // Verfahren ueberschreiben koennen oder nicht.
                            entry.put("segversion",Integer.toString(segVersion));

                            String     paramHeader=key.substring(0,key.lastIndexOf('.'));
                            // Params_x.TAN2StepParY.ParTAN2StepZ.TAN2StepParamsX_z

                            // alle properties durchlaufen und alle suchen, die mit dem
                            // paramheader beginnen, und die entsprechenden werte im
                            // entry abspeichern
                            for (Enumeration e2=p.propertyNames();e2.hasMoreElements();) {
                                String key2=(String)e2.nextElement();

                                if (key2.startsWith(paramHeader+".")) {
                                    int dotPos=key2.lastIndexOf('.');

                                    entry.setProperty(
                                            key2.substring(dotPos+1),
                                            p.getProperty(key2));
                                }
                            }

                            // diesen mechanismus abspeichern
                            tanMethodsBank.put(secfunc,entry);
                        }
                    }
                }
            }
        }
    }

    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#onDialogEvent(org.kapott.hbci.dialog.DialogEvent, org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    public void onDialogEvent(DialogEvent event, DialogContext ctx)
    {
        super.onDialogEvent(event, ctx);

        if (event == DialogEvent.MSG_CREATED)
        {
            this.checkSCARequest(ctx);
        }
        else if (event == DialogEvent.MSG_SENT)
        {
            this.checkInvalidPIN(ctx);
            this.check3920(ctx);
            this.check3072(ctx);
            this.checkSCAResponse(ctx);
        }
        else if (event == DialogEvent.JOBS_CREATED)
        {
            this.patchMessagesFor2StepMethods(ctx);
        }
    }
    
    /**
     * Prueft, ob es Anzeichen fuer eine falsche PIN gibt.
     * Wenn ja, geben wir per Callback Bescheid.
     * @param ctx der Kontext.
     */
    private void checkInvalidPIN(DialogContext ctx)
    {
        // Falsche PIN kann es bei einem anonymen Dialog nicht geben
        if (ctx.isAnonymous())
            return;

        HBCIMsgStatus status = ctx.getMsgStatus();
        if (status == null)
            return;
        
        if (status.isOK())
            return;
        
        HBCIRetVal ret = status.getInvalidPINCode();
        
        if (ret == null)
            return;

        HBCIUtils.log("PIN-Fehler erkannt, Meldung der Bank: " + ret.code + ": " + ret.text, HBCIUtils.LOG_INFO);
        this.clearPIN();
        
        // Aufrufer informieren, dass falsche PIN eingegeben wurde (um evtl. PIN aus Puffer zu löschen, etc.) 
        HBCIUtilsInternal.getCallback().callback(this,HBCICallback.WRONG_PIN,"*** invalid PIN entered",HBCICallback.TYPE_TEXT,new StringBuffer());
    }
    
    /**
     * Prueft, ob im Response der Code 3920 enthalten ist.
     * Dort liefert die Bank neue Zweischritt-Verfahren.
     * @param ctx der Kontext.
     */
    private void check3920(DialogContext ctx)
    {
        // In einem anonymen Dialog koennen keine 3920 enthalten sein, da die User-spezifisch sind
        if (ctx.isAnonymous())
            return;
        
        HBCIMsgStatus status = ctx.getMsgStatus();
        if (status == null)
            return;

        ////////////////////////////////////////////////////
        // TAN-Verfahren ermitteln und uebernehmen
        final List<HBCIRetVal> recvList = KnownReturncode.W3920.searchReturnValues(status);
        
        if (recvList == null || recvList.isEmpty())
            return;

        HBCIUtils.log("autosecfunc: found " + recvList.size() + " 3920s in response, detect allowed twostep secmechs", HBCIUtils.LOG_DEBUG);

        final List<String> oldList = new ArrayList<String>(this.tanMethodsUser);
        final Set<String> newSet = new HashSet<String>(); // Damit doppelte nicht doppelt in der Liste landen
        for (HBCIRetVal r:recvList)
        {
          if (r.params != null)
            newSet.addAll(Arrays.asList(r.params));
        }
        final List<String> newList = new ArrayList<String>(newSet);

        if (newList.size() > 0 && !newList.equals(oldList))
        {
            this.tanMethodsUser.clear();
            this.tanMethodsUser.addAll(newList);
            HBCIUtils.log("autosecfunc: found 3920 in response - updated list of allowed twostepmechs - old: " + oldList + ", new: " + this.tanMethodsUser, HBCIUtils.LOG_DEBUG);
        }
        //
        ////////////////////////////////////////////////////
        
        if (this.isAnonymous())
            return;
        
        ////////////////////////////////////////////////////
        // Dialog neu starten, wenn das Verfahren sich geaendert hat
        // aktuelle secmech merken und neue auswählen (basierend auf evtl. gerade neu empfangenen informationen (3920s))
        final String oldMethod = this.tanMethod;
        final String newMethod = this.getCurrentTANMethod(true);
        
        if (Objects.equals(oldMethod,newMethod))
            return;

        // Wenn es eine Synchronisierung ist, lassen wir das Repeat weg.
        // Die Postbank kommt nicht damit klar, wenn man eine neue Synchronisierung mit dem anderen TAN-Verfahren direkt hinterher sendet
        if (ctx.getDialogInit().getTemplate() == KnownDialogTemplate.SYNC)
            return;

        // wenn sich das ausgewählte secmech geändert hat, müssen wir
        // einen dialog-restart fordern, weil während eines dialoges
        // das secmech nicht gewechselt werden darf
        HBCIUtils.log("autosecfunc: after this dialog-init we had to change selected pintan method from " + oldMethod + " to " + newMethod + ", so a restart of this dialog is needed", HBCIUtils.LOG_DEBUG);
        HBCIUtils.log("Derzeitiges TAN-Verfahren aktualisiert, starte Dialog neu", HBCIUtils.LOG_INFO);
        
        ctx.setRepeat(true);
        //
        ////////////////////////////////////////////////////

    }

    /**
     * Prueft, ob im Response der Code 3072 enthalten ist.
     * Dort liefert die Bank ggf. aktualisierte Zugangsdaten.
     * @param ctx der Kontext.
     */
    private void check3072(DialogContext ctx)
    {
        // Neue Zugangsdaten kann es anonym nicht geben.
        if (ctx.isAnonymous())
            return;

        HBCIMsgStatus status = ctx.getMsgStatus();
        if (status == null)
            return;

        HBCIRetVal[] seg = status.segStatus != null ? status.segStatus.getWarnings() : null;
        if (seg == null)
            return;
        
        final HBCIRetVal ret = KnownReturncode.W3072.searchReturnValue(seg);
        if (ret == null)
            return;
        
        String newCustomerId = "";
        String newUserId = "";
        int l2=ret.params.length;
        if(l2>0) {
            newUserId = ret.params[0];
            newCustomerId = ret.params[0];
        }
        if(l2>1) {
            newCustomerId = ret.params[1];
        }
        if(l2>0) {
            HBCIUtils.log("autosecfunc: found 3072 in response - change user id", HBCIUtils.LOG_DEBUG);
            // Aufrufer informieren, dass UserID und CustomerID geändert wurde
            StringBuffer retData=new StringBuffer();
            retData.append(newUserId+"|"+newCustomerId);
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.USERID_CHANGED,"*** User ID changed",HBCICallback.TYPE_TEXT,retData);
        }
    }

    /**
     * Prueft, ob die Dialog-Initialisierung um ein HKTAN erweitert werden muss.
     * @param ctx der Kontext.
     */
    private void checkSCARequest(DialogContext ctx)
    {
        HBCIUtils.log("check SCA request",HBCIUtils.LOG_DEBUG);

        final SCARequest sca = this.getSCARequest(ctx);
        if (sca == null)
        {
          HBCIUtils.log("no SCA request for this context, skipping check",HBCIUtils.LOG_DEBUG);
          return;
        }
        
        Integer step = (Integer) ctx.getMeta().get(CACHE_KEY_SCA_STEP);

        // Wir haben noch kein HKTAN gesendet. Dann senden wir jetzt Schritt 1
        if (step == null)
        {
          step = 1;
          ctx.getMeta().put(CACHE_KEY_SCA_STEP,step);
        }

        final Variant variant = sca.getVariant();
        KnownTANProcess tp = KnownTANProcess.get(variant,step.intValue());

        if (tp == KnownTANProcess.PROCESS2_STEP2)
        {
          // Checken, ob wir Decoupled verwenden. In dem Fall
          // TAN-Prozess von "2" auf "S" ändern
          final Properties secmechInfo = this.getCurrentSecMechInfo();
          final HHDVersion hhd = HHDVersion.find(secmechInfo);
          final String segversion = secmechInfo != null ? secmechInfo.getProperty("segversion") : null;
          HBCIUtils.log("detected HHD version: " + hhd,HBCIUtils.LOG_DEBUG);

          if (hhd != null && hhd.getType() == Type.DECOUPLED)
          {
            Integer i = null;
            try
            {
              i = Integer.parseInt(segversion);
            }
            catch (Exception e) {}
            if (i != null && i.intValue() >= 7)
            {
              HBCIUtils.log("switching TAN process from " + tp + " to " + KnownTANProcess.PROCESS2_STEPS,HBCIUtils.LOG_DEBUG);
              tp = KnownTANProcess.PROCESS2_STEPS;
            }
          }
        }
        
        final int version = sca.getVersion();
        
        // wir fuegen die Daten des HKTAN ein
        final HBCIKernelImpl k = ctx.getKernel();
        final String prefix = "TAN2Step" + version;
        k.rawSet(prefix,"requested"); // forcieren, dass das Segment mit gesendet wird - auch wenn es eigentlich optional ist
        k.rawSet(prefix + ".process",tp.getCode());


        String segcode = sca.getTanReference();
        if (Feature.PINTAN_SEGCODE_STRICT.isEnabled())
        {
          if (step == 2)
            segcode = "";
        }
        HBCIUtils.log("creating HKTAN for SCA [process : " + tp + ", order code: " + segcode + ", step: " + step + "]",HBCIUtils.LOG_DEBUG);
        
        k.rawSet(prefix + ".ordersegcode",segcode);
        k.rawSet(prefix + ".OrderAccount.bic","");
        k.rawSet(prefix + ".OrderAccount.iban","");
        k.rawSet(prefix + ".OrderAccount.number","");
        k.rawSet(prefix + ".OrderAccount.subnumber","");
        k.rawSet(prefix + ".OrderAccount.KIK.blz","");
        k.rawSet(prefix + ".OrderAccount.KIK.country","");
        k.rawSet(prefix + ".orderhash",(variant == Variant.V2) ? "" : ("B00000000"));
        k.rawSet(prefix + ".orderref",(step == 2) ? (String) this.getPersistentData(KEY_PD_ORDERREF) : "");
        k.rawSet(prefix + ".notlasttan",(tp == KnownTANProcess.PROCESS1 || step == 2) ? "N" : ""); // Darf nur bei TAN-Prozess 1, 2 und S belegt sein
        k.rawSet(prefix + ".challengeklass",(variant == Variant.V2) ? "" : "99");
        k.rawSet(prefix + ".tanmedia",sca.getTanMedia());
    }
    
    /**
     * Erzeugt einen passenden SCA-Request fuer die Dialog-Initialisierung.
     * @param ctx der Context.
     * @return der SCA-Request oder NULL, wenn keiner noetig ist.
     */
    private SCARequest getSCARequest(DialogContext ctx)
    {
        HBCIUtils.log("create new SCA request",HBCIUtils.LOG_DEBUG);
        
        final RawHBCIDialog init = ctx.getDialogInit();
        if (init == null)
        {
          HBCIUtils.log("have no dialog init, skip SCA request creation",HBCIUtils.LOG_DEBUG);
          return null;
        }

        // Checken, ob es ein Dialog, in dem eine SCA gemacht werden soll
        if (!KnownDialogTemplate.LIST_SEND_SCA.contains(init.getTemplate()))
        {
          HBCIUtils.log("dialog (" + init.getTemplate() + ") not in list of SCA dialogs, skip SCA request creation",HBCIUtils.LOG_DEBUG);
          return null;
        }

        if (Feature.PINTAN_INIT_SKIPONESTEPSCA.isEnabled())
        {
            // Wenn wir ein Einschritt-TAN-Verfahren haben und es die autorisierte Initialisierung ist,
            // dann senden wir das Init ohne HKTAN. Im anonymen Init haben wir ja schon per HKTAN mitgeteilt, dass
            // wir SCA koennen. Jetzt gehts uns nur darum, die TAN-Verfahren per 3920 zu kriegen
            // Ist nach Abstimmung mit einem HBCI-Server-Experten so legitim und wird von allen so gemacht:
            // Bei Dialog-Init Verfahren 999 nehmen und ohne HKTAN senden
            // Laut https://homebanking-hilfe.de/forum/topic.php?p=149751#real149751 akzeptiert die DKB kein Sync mit 999
            //
            //  Dialog                                   HKTAN weglassen?
            //  ---------------------------------------------------------
            //  DialogInitAnon                           nein
            //  DialogInit mit Einschritt-TAN            ja
            //  DialogInit mit Zweischritt-TAN           nein
            //  Sync                                     nein
            //  Sync mit aktiviertem Init-Flip           ja
          
            final KnownDialogTemplate tpl = init.getTemplate();
            final String currentTanMethod = this.getCurrentTANMethod(false);
            if (!ctx.isAnonymous() && Objects.equals(TanMethod.ONESTEP.getId(),currentTanMethod) && 
                (tpl == KnownDialogTemplate.INIT || (Feature.INIT_FLIP_USER_INST.isEnabled() && tpl == KnownDialogTemplate.SYNC)))
            {
                HBCIUtils.log("skipping HKTAN for dialog init [anon: " + ctx.isAnonymous() + ", current tan method: " + currentTanMethod + ", tpl: " + tpl + "]",HBCIUtils.LOG_DEBUG);
                return null;
            }
        }

        // HKTAN-Version und Prozessvariante ermitteln - kann NULL sein
        final int segversionDefault = 6;
        final Properties secmechInfo = this.getCurrentSecMechInfo();

        final int hktanVersion = secmechInfo != null ? NumberUtil.parseInt(secmechInfo.getProperty("segversion"),segversionDefault) : segversionDefault;
        
        // Erst ab HKTAN 6 noetig. Die Bank unterstuetzt es scheinbar noch nicht
        // Siehe B.4.3.1 - Wenn die Bank HITAN < 6 geschickt hat, dann kann sie keine SCA
        if (hktanVersion < 6)
        {
          HBCIUtils.log("HKTAN version < 6, skip SCA request creation",HBCIUtils.LOG_DEBUG);
          return null;
        }

        final SCARequest r = init.createSCARequest(secmechInfo,hktanVersion);
        if (r == null)
        {
          HBCIUtils.log("have no SCA request, skip SCA request creation",HBCIUtils.LOG_DEBUG);
          return null;
        }
        
        if (r.getTanReference() == null)
        {
            // Beim Bezug auf das Segment schicken wir per Default "HKIDN". Gemaess Kapitel B.4.3.1 muss das Bezugssegment aber
            // bei PIN/TAN-Management-Geschaeftsvorfaellen mit dem GV des jeweiligen Geschaeftsvorfalls belegt werden.
            // Daher muessen wir im Payload schauen, ob ein entsprechender Geschaeftsvorfall enthalten ist.
            // Wird muessen nur nach HKPAE, HKTAB schauen - das sind die einzigen beiden, die wir unterstuetzen
            String segcode = "HKIDN";
            HBCIDialog payload = ctx.getDialog();
            if (payload != null)
            {
                final HBCIMessageQueue queue = payload.getMessageQueue();
                for (String code:Arrays.asList("HKPAE","HKTAB")) // Das sind GVChangePIN und GVTANMediaList
                {
                    if (queue.findTask(code) != null)
                    {
                        segcode = code;
                        break;
                    }
                }
            }
            r.setTanReference(segcode);
        }
        if (r.getTanMedia() == null)
            r.setTanMedia(this.getTanMedia(hktanVersion));
        
        return r;
    }

    /**
     * Prueft das Response auf Vorhandensein eines HITAN bzw Code.
     * Hinweis: Wir haben das ganze HKTAN-Handling derzeit leider doppelt. Einmal fuer die Dialog-Initialisierung und einmal fuer
     * die Nachrichten mit den eigentlichen Geschaeftsvorfaellen (in patchMessagesFor2StepMethods). Wenn auch HBCIDialog#doJobs irgendwann
     * auf die neuen RawHBCIDialoge umgestellt ist, kann eigentlich patchMessagesFor2StepMethods entfallen.
     * @param ctx der Kontext.
     */
    private void checkSCAResponse(DialogContext ctx)
    {
        HBCIUtils.log("check SCA response",HBCIUtils.LOG_DEBUG);
        
        final RawHBCIDialog init = ctx.getDialogInit();
        if (init == null)
        {
          HBCIUtils.log("no init dialog, skip SCA response analysis",HBCIUtils.LOG_DEBUG);
          return;
        }
        
        // Checken, ob es ein Dialog, in dem eine SCA gemacht werden soll
        if (!KnownDialogTemplate.LIST_SEND_SCA.contains(init.getTemplate()))
        {
          HBCIUtils.log("dialog (" + init.getTemplate() + ") not in list of SCA dialogs, skip SCA response analysis",HBCIUtils.LOG_DEBUG);
          return;
        }

        // Wenn wir noch in der anonymen Dialog-Initialisierung sind, interessiert uns das nicht.
        if (ctx.isAnonymous() || this.isAnonymous())
        {
            HBCIUtils.log("anonymous dialog, skip SCA response analysis",HBCIUtils.LOG_DEBUG);
            ctx.getMeta().remove(CACHE_KEY_SCA_STEP);
            return;
        }

        Integer scaStep = (Integer) ctx.getMeta().get(CACHE_KEY_SCA_STEP);
        
        // Wenn wir keinen SCA-Request gesendet haben, brauchen wir auch nicht nach dem Response suchen
        if (scaStep == null)
        {
          HBCIUtils.log("no sca request sent, skip SCA response analysis",HBCIUtils.LOG_DEBUG);
          return;
        }

        // Ohne Status brauchen wir es gar nicht erst versuchen
        final HBCIMsgStatus status = ctx.getMsgStatus();
        if (status == null)
        {
          HBCIUtils.log("no message status received, skip SCA response analysis",HBCIUtils.LOG_DEBUG);
          return;
        }

        // Bank hat uns eine Ausnahme erteilt - wir brauchen keine TAN
        if (status.segStatus != null && (KnownReturncode.W3076.searchReturnValue(status.segStatus.getWarnings()) != null || KnownReturncode.W3076.searchReturnValue(status.globStatus.getWarnings()) != null))
        {
            HBCIUtils.log("found status code 3076, no SCA required",HBCIUtils.LOG_DEBUG);
            ctx.getMeta().remove(CACHE_KEY_SCA_STEP);
            return;
        }
        
        // Schritt 1: Wir haben eine HKTAN-Anfrage gesendet. Mal schauen, ob die Bank tatsaechlich eine TAN will
        if (scaStep.intValue() == 1)
        {
            HBCIUtils.log("HKTAN step 1 for SCA sent, checking for HITAN response [step: " + scaStep + "]",HBCIUtils.LOG_DEBUG);

            Properties props = ParameterFinder.find(status.getData(),"TAN2StepRes*.");
            if (props == null || props.size() == 0)
            {
              HBCIUtils.log("no hitan reponse data found",HBCIUtils.LOG_DEBUG);
              return; // Wir haben kein HITAN
            }

            // HITAN erhalten - Daten uebernehmen
            HBCIUtils.log("SCA HITAN response found, triggering TAN request",HBCIUtils.LOG_DEBUG);
            final String challenge = props.getProperty("challenge");
            if (challenge != null && challenge.length() > 0)
                this.setPersistentData(KEY_PD_CHALLENGE,challenge);
            
            final String hhdUc = props.getProperty("challenge_hhd_uc");
            if (hhdUc != null && hhdUc.length() > 0)
                this.setPersistentData(KEY_PD_HHDUC,hhdUc);
            
            final String orderref = props.getProperty("orderref");
            if (orderref != null && orderref.length() > 0)
                this.setPersistentData(KEY_PD_ORDERREF,orderref);

            /////////////////////////////////////////////////////
            // Dialog-Init wiederholen, um den zweiten HKTAN-Schritt durchzufuehren
            // OK, wir senden jetzt das finale HKTAN. Die Message darf nichts anderes enthalten. Daher aendern wir das Template.
            ctx.getMeta().put(CACHE_KEY_SCA_STEP,2);
            ctx.getDialogInit().setTemplate(KnownDialogTemplate.INIT_SCA);
            ctx.setRepeat(true);
            //
            /////////////////////////////////////////////////////
            
            return;
        }
        
        if (scaStep.intValue() == 2)
        {
            if (this.shouldPerformDecoupledRefresh(status.segStatus)) {
                HBCIUtils.log("Decoupled refresh required for dialog initialization. Repeating dialog",HBCIUtils.LOG_DEBUG);
                ctx.getMeta().put(CACHE_KEY_SCA_STEP,2);
                ctx.getDialogInit().setTemplate(KnownDialogTemplate.INIT_SCA);
                ctx.setRepeat(true);
                return;
            }
            ctx.getMeta().remove(CACHE_KEY_SCA_STEP); // Geschafft
            HBCIUtils.log("HKTAN step 2 for SCA sent, checking for HITAN response [step: " + scaStep + "]",HBCIUtils.LOG_DEBUG);
            Properties props = ParameterFinder.find(status.getData(),"TAN2StepRes*.");
            if (props.size() > 0)
                HBCIUtils.log("final SCA HITAN response found",HBCIUtils.LOG_DEBUG);
        }
    }

    /**
     * Beim Decoupled Verfahren kann die Bank ein 3956 senden, wenn der Nutzer den Prozess noch nicht bestätigt hat.
     * In diesem Fall muss die Nachricht wiederholt werden, um erneut zu prüfen, ob die Bestätigung erfolgt ist.
     * Wir benachrichtigen die Applikation mit einem entsprechenden Callback und warten eine mögliche Mindestzeit.
     * Ein refresh kann entweder durch die Dialoginitialisierung in {@link #checkSCAResponse},
     * oder von konkreten Geschäftsvorfällen in {@link GVTAN2Step#extractResults(HBCIMsgStatus, String, int)}
     * ausgelöst werden.
     * @param segStatus Der segStatus, wo ein möglicher 3956 response code zu finden ist.
     * @return true, wenn ein 3956 code gefunden wurde, und ein refresh durchgeführt werden soll.
     */
    public boolean shouldPerformDecoupledRefresh(HBCIStatus segStatus) {
        if (segStatus == null || (KnownReturncode.W3956.searchReturnValue(segStatus.getWarnings()) == null)) {
            return false;
        }
        if (!Feature.PINTAN_DECOUPLED_REFRESH.isEnabled()) {
            HBCIUtils.log("found status code 3956, but PINTAN_DECOUPLED_REFRESH is disabled, so no refresh will be performed", HBCIUtils.LOG_DEBUG);
            return false;
        }

        HBCIUtils.log("found status code 3956, calling decoupled callback", HBCIUtils.LOG_DEBUG);
        if (this.getDecoupledMaxRefreshes() != null && this.decoupledRefreshes >= this.getDecoupledMaxRefreshes()) {
            throw new HBCI_Exception("*** the maximum number of decoupled refreshes has been reached.");
        }
        Integer timeBeforeDecoupledRefresh = this.decoupledRefreshes == 0
            ? this.getMinimumTimeBeforeFirstDecoupledRefresh()
            : this.getMinimumTimeBeforeNextDecoupledRefresh();
        long callbackDurationMs = System.currentTimeMillis();
        HBCIUtilsInternal.getCallback().callback(
            this,
            HBCICallback.NEED_PT_DECOUPLED_RETRY,
            "*** decoupled SCA still required",
            HBCICallback.TYPE_TEXT,
            new StringBuffer(String.valueOf(timeBeforeDecoupledRefresh != null ? timeBeforeDecoupledRefresh : 0)));
        callbackDurationMs = System.currentTimeMillis() - callbackDurationMs;
        if (timeBeforeDecoupledRefresh != null && callbackDurationMs < timeBeforeDecoupledRefresh * 1000) {
            long sleepMs = timeBeforeDecoupledRefresh * 1000 - callbackDurationMs;
            HBCIUtils.log(String.format(
                "The pause before the next decoupled request was too short. Sleeping for %dms to reach the required delay.", sleepMs
            ), HBCIUtils.LOG_INFO);
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                throw new HBCI_Exception("*** Decoupled refresh sleep was interrupted.");
            }
        }
        this.decoupledRefreshes++;
        return true;
    }
    
    /**
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#getCommInstance()
     */
    public Comm getCommInstance()
    {
        return Comm.getInstance("PinTan",this);
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassport#isSupported()
     */
    public boolean isSupported()
    {
      final Properties bpd = this.getBPD();
      if (bpd == null)
        return true;

      // Wir triggern hier nur einmal die Auswahl des TAN-Verfahrens
      this.getCurrentTANMethod(true);
      return true;
    }
    
    /**
     * Liefert true, wenn das TAN-Einschritt-Verfahren unterstuetzt wird.
     * @return true, wenn das TAN-Einschritt-Verfahren unterstuetzt wird.
     */
    private boolean isOneStepAllowed()
    {
      final Properties bpd = this.getBPD();
      if (bpd == null)
        return true;
      
      return ParameterFinder.findAll(bpd,ParameterFinder.Query.BPD_PINTAN_CAN1STEP).containsValue("J");
    }

    /**
     * Für das Decoupled Verfahren, liefert die minimale Zeit vor dem ersten refresh.
     * @return Die minimale Zeit vor dem ersten Decoupled refresh.
     */
    public Integer getMinimumTimeBeforeFirstDecoupledRefresh() {
        final Properties bpd = this.getBPD();
        if (bpd == null)
            return null;
        return HBCIUtilsInternal.getIntegerProperty(bpd, Query.BPD_DECOUPLED_TIME_BEFORE_FIRST_STATUS_REQUEST, false);
    }

    /**
     * Für das Decoupled Verfahren, liefert die minimale Zeit vor weiteren refreshes.
     * @return Die minimale Zeit vor weiteren Decoupled refreshes.
     */
    public Integer getMinimumTimeBeforeNextDecoupledRefresh() {
        final Properties bpd = this.getBPD();
        if (bpd == null)
            return null;
        return HBCIUtilsInternal.getIntegerProperty(bpd, Query.BPD_DECOUPLED_TIME_BEFORE_NEXT_STATUS_REQUEST, false);
    }

    /**
     * Für das Decoupled Verfahren, liefert die maximale Anzahl von refreshes.
     * @return Die maximale Anzahl von Decoupled refreshes.
     */
    public Integer getDecoupledMaxRefreshes() {
        final Properties bpd = this.getBPD();
        if (bpd == null)
            return null;
        return HBCIUtilsInternal.getIntegerProperty(bpd, Query.BPD_DECOUPLED_MAX_STATUS_REQUESTS, true);
    }
    
    /** Kann vor <code>new HBCIHandler()</code> aufgerufen werden, um zu
     * erzwingen, dass die Liste der unterstützten PIN/TAN-Sicherheitsverfahren
     * neu vom Server abgeholt wird und evtl. neu vom Nutzer abgefragt wird. */
    public void resetSecMechs()
    {
        this.tanMethodsUser=new ArrayList<String>();
        this.tanMethod=null;
        this.tanMethodAutoSelected=false;
    }
    
    /**
     * Legt das aktuelle TAN-Verfahren fest.
     * @param method das aktuelle TAN-Verfahren.
     */
    public void setCurrentTANMethod(String method)
    {
        this.tanMethod=method;
    }

    /**
     * Liefert das aktuelle TAN-Verfahren.
     * @param recheck true, wenn die gespeicherte Auswahl auf Aktualitaet und Verfuegbarkeit geprueft werden soll.
     * Die Funktion kann in dem Fall einen Callback ausloesen, wenn mehrere Optionen zur Wahl stehen.
     * @return das TAN-Verfahren.
     */
    public String getCurrentTANMethod(boolean recheck)
    {
        // Wir haben ein aktuelles TAN-Verfahren und eine Neupruefung ist nicht noetig
        if (this.tanMethod != null && !recheck)
            return this.tanMethod;

        boolean auto = Feature.PINTAN_INIT_AUTOMETHOD.isEnabled();
        HBCIUtils.log("(re)checking selected pintan method using " + (auto ? "auto-determine" : "ask") + " strategy", HBCIUtils.LOG_DEBUG);

        if (auto)
            return this.determineTanMethod();
        
        return this.askForTanMethod();
    }

    /**
     * Liefert das aktuelle TAN-Verfahren.
     * Hierbei versucht HBCI4Java das Verfahren erst automatisch zu ermitteln, bevor es den User fragt.
     * @return das TAN-Verfahren.
     */
    private String determineTanMethod()
    {
        // Wenn der User noch keine TAN-Verfahren hat, bleibt und als Option nur 999 - also Einschritt-Verfahren, um an den 3920
        // mit den zulaessigen Verfahren zu kommen. Wir pruefen hier gar nicht erst per "isOneStepAllowed", ob die Bank ein
        // Einschritt-Verfahren anbietet, weil wir gar keine andere Option haben
        // Update 2019-11-02: Geht bei der Postbank leider nicht. Die erlauben kein Einschritt-Vefahren und wollen daher
        // tatsaechlich bereits beim Abruf der verfuegbaren TAN-Verfahren ein Zweischritt-Verfahren haben. Siehe 
        // https://homebanking-hilfe.de/forum/topic.php?p=151725#real151725
        if (this.tanMethodsUser.size() == 0 && this.isOneStepAllowed())
            return TanMethod.ONESTEP.getId();
        
        /////////////////////////////////////////
        // Die Liste der verfuegbaren Optionen ermitteln
        final List<TanMethod> userList = new ArrayList<TanMethod>();
        final List<TanMethod> bankList = new ArrayList<TanMethod>();

        String[] secfuncs= this.tanMethodsBank.keySet().toArray(new String[this.tanMethodsBank.size()]);
        Arrays.sort(secfuncs);
        for (String secfunc:secfuncs)
        {
            final Properties entry = this.tanMethodsBank.get(secfunc);
            final TanMethod m = new TanMethod(secfunc,entry.getProperty("name"));
            if (this.tanMethodsUser.contains(secfunc))
                userList.add(m);
            bankList.add(m);
        }
        //
        /////////////////////////////////////////

        HBCIUtils.log("tan methods of institute: " + bankList, HBCIUtils.LOG_DEBUG);
        HBCIUtils.log("tan methods for user: " + userList, HBCIUtils.LOG_DEBUG);

        /////////////////////////////////////////
        // Auswahl treffen
        
        if (userList.size() == 0)
        {
          if (this.isOneStepAllowed())
          {
            final TanMethod m = TanMethod.ONESTEP;
            HBCIUtils.log("no tan method available for user, using: " + m,HBCIUtils.LOG_DEBUG);
            // Wir speichern das TAN-Verfahren nicht, das kann unmoeglich das finale Verfahren sein.
            return m.getId();
          }
          else
          {
            // Sonderfall HBCI4Java Testserver. Der liefert gar keine TAN-Verfahren
            if (bankList.size() == 0)
            {
              final TanMethod m = TanMethod.ONESTEP;
              HBCIUtils.log("no tan method available for bank, using: " + m,HBCIUtils.LOG_DEBUG);
              // Wir speichern das TAN-Verfahren nicht, das kann unmoeglich das finale Verfahren sein.
              return m.getId();
            }
            
            // Das ist sicher die Postbank. Wir haben noch kein Verfahren per 3920 erhalten, die Bank erlaubt
            // aber nicht, diese per Einschritt-Verfahren abzurufen. Also muessen wir den User bitten, die Auswahl
            // aus der in den BPD verfuegbaren Verfahren zu treffen. Auch wenn diese Liste dann Eintraege enthaelt,
            // die fuer den User u.U. gar nicht verfuegbar sind.
            HBCIUtils.log("have no methods for user and institute doesn't allow one step method - asking user. available methods on institute: " + bankList,HBCIUtils.LOG_DEBUG);
            this.setCurrentTANMethod(this.chooseTANMethod(bankList));
            HBCIUtils.log("selected pintan method by user: " + tanMethod, HBCIUtils.LOG_INFO);
            return this.tanMethod;
          }
        }
        
        if (userList.size() == 1)
        {
            final TanMethod m = userList.get(0);
            HBCIUtils.log("only one tan method available for user: " + m,HBCIUtils.LOG_DEBUG);
            this.setCurrentTANMethod(m.getId()); // Ueberschreibt bei der Gelegenheit gleich die letzte Version
            return this.tanMethod;
        }
        
        // Wir haben mehrere zur Auswahl. Checken, ob wir schon eins hatten. Und wenn ja, ob es
        // immer noch verfuegbar ist. Wenn ja, liefern wir einfach das zurueck. Wenn es nicht
        // mehr verfuegbar ist, muessen wir den User neu fragen.
        boolean reuse = this.tanMethod != null && userList.stream().anyMatch(m -> Objects.equals(m.getId(),this.tanMethod));
        if (reuse)
            return this.tanMethod;
        
        // User fragen
        HBCIUtils.log("asking user what tan method to use. available methods: " + userList,HBCIUtils.LOG_DEBUG);
        this.setCurrentTANMethod(this.chooseTANMethod(userList));
        HBCIUtils.log("selected pintan method by user: " + tanMethod, HBCIUtils.LOG_INFO);
        return this.tanMethod;
    }

    /**
     * Liefert das aktuelle TAN-Verfahren.
     * Fragt hierbei im Zweifelsfall eher den User anstatt es selbst herauszufinden.
     * @return das TAN-Verfahren.
     */
    private String askForTanMethod()
    {
        /////////////////////////////////////////
        // Die Liste der verfuegbaren Optionen ermitteln
        final List<TanMethod> options = new ArrayList<TanMethod>();
        final List<TanMethod> fallback = new ArrayList<TanMethod>();

        // Einschritt-Verfahren optional hinzufuegen
        if (this.isOneStepAllowed())
        {
            TanMethod m = TanMethod.ONESTEP;
            // Nur hinzufuegen, wenn wir entweder gar keine erlaubten haben oder es in der Liste der erlaubten drin ist
            if (this.tanMethodsUser.size() == 0 || this.tanMethodsUser.contains(m.getId()))
                options.add(m);
        }
        
        // Die Zweischritt-Verfahren hinzufuegen
        String[] secfuncs= this.tanMethodsBank.keySet().toArray(new String[this.tanMethodsBank.size()]);
        Arrays.sort(secfuncs);
        for (String secfunc:secfuncs)
        {
            final Properties entry = this.tanMethodsBank.get(secfunc);
            final TanMethod m = new TanMethod(secfunc,entry.getProperty("name"));
            if (this.tanMethodsUser.size() == 0 || this.tanMethodsUser.contains(secfunc))
            {
                options.add(m);
            }
            fallback.add(m);
        }
        //
        /////////////////////////////////////////

        
        /////////////////////////////////////////
        // 0 Optionen verfuegbar
        
        // Wir haben keine Optionen gefunden
        if (options.size() == 0)
        {
            // wir lassen das hier mal noch auf true stehen, weil das bestimmt noch nicht final war. Schliesslich basierte die
            // Auswahl des Verfahrens nicht auf den fuer den User freigeschalteten Verfahren sondern nur den allgemein von der
            // Bank unterstuetzten
            this.tanMethodAutoSelected = true;
            
            HBCIUtils.log("autosecfunc: no information about allowed pintan methods available", HBCIUtils.LOG_INFO);
            // Wir haben keine TAN-Verfahren, die fuer den User per 3920 zugelassen sind.
            // Wir schauen mal, ob die Bank wenigstens welche in HIPINS gemeldet hat. Wenn ja, dann soll der
            // User eins von dort auswaehlen. Ob das dann aber ein erlaubtes ist, wissen wir nicht.
            if (fallback.size() > 0)
            {
                HBCIUtils.log("autosecfunc: have some pintan methods in HIPINS, asking user, what to use from: " + fallback, HBCIUtils.LOG_INFO);
                final String selected = this.chooseTANMethod(fallback);
                this.setCurrentTANMethod(selected);
                HBCIUtils.log("autosecfunc: manually selected pintan method from HIPINS " + tanMethod, HBCIUtils.LOG_DEBUG);
            }
            else
            {
                TanMethod m = TanMethod.ONESTEP;
                HBCIUtils.log("autosecfunc: absolutly no information about allowed pintan methods available, fallback to " + m, HBCIUtils.LOG_WARN);
                this.setCurrentTANMethod(m.getId());
            }
            
            return this.tanMethod;
        }
        //
        /////////////////////////////////////////
        
        
        /////////////////////////////////////////
        // 1 Option verfuegbar
        if (options.size() == 1)
        {
            final TanMethod m = options.get(0);
            
            HBCIUtils.log("autosecfunc: there is only one pintan method supported - choosing this automatically: " + m,HBCIUtils.LOG_DEBUG);
            
            if (this.tanMethod != null && !this.tanMethod.equals(m.getId()))
                HBCIUtils.log("autosecfunc: auto-selected method differs from current: " + this.tanMethod, HBCIUtils.LOG_DEBUG);
            
            this.setCurrentTANMethod(m.getId());
            this.tanMethodAutoSelected = true;
            
            return this.tanMethod;
        }
        //
        /////////////////////////////////////////


        /////////////////////////////////////////
        // Mehrere Optionen verfuegbar

        // Checken, was gerade eingestellt ist.
        if (this.tanMethod != null)
        {
            boolean found = false;
            for (TanMethod m:options)
            {
                found |= this.tanMethod.equals(m.getId());
                if (found)
                    break;
            }
            
            if (!found)
            {
                HBCIUtils.log("autosecfunc: currently selected pintan method ("+this.tanMethod+") not in list of supported methods  " + options + " - resetting current selection", HBCIUtils.LOG_DEBUG);
                this.tanMethod = null;
            }
        }
        //
        /////////////////////////////////////////

        // Wenn wir jetzt immer noch ein Verfahren haben und dieses nicht automatisch gewaehlt wurde, dann
        // duerfen wir es verwenden.
        if (this.tanMethod != null && !this.tanMethodAutoSelected)
            return this.tanMethod;
        
        // User fragen - aber nur, wenn wir was zum Auswahlen haben
        if (options != null && options.size() > 0)
        {
            HBCIUtils.log("autosecfunc: asking user what tan method to use. available methods: " + options,HBCIUtils.LOG_DEBUG);
            final String selected = this.chooseTANMethod(options);
              
            this.setCurrentTANMethod(selected);
            this.tanMethodAutoSelected = false;
            HBCIUtils.log("autosecfunc: manually selected pintan method "+tanMethod, HBCIUtils.LOG_DEBUG);
        }
        return tanMethod;
    }
    
    /**
     * Fuehrt den Callback zur Auswahl des TAN-Verfahrens durch.
     * @param options die verfuegbaren Optionen.
     * @return das gewaehlte TAN-Verfahren.
     */
    private String chooseTANMethod(List<TanMethod> options)
    {
        final StringBuffer retData = new StringBuffer();
        for (TanMethod entry:options)
        {
            if (retData.length()!=0)
                retData.append("|");
            
            retData.append(entry.getId()).append(":").append(entry.getName());
        }
        
        HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_PT_SECMECH,"*** Select a pintan method from the list",HBCICallback.TYPE_TEXT,retData);
        
        // Pruefen, ob das gewaehlte Verfahren einem aus der Liste entspricht
        final String selected = retData.toString();
        
        for (TanMethod entry:options)
        {
            if  (selected.equals(entry.getId()))
                return selected;
        }
        
        throw new InvalidUserDataException("*** selected pintan method not supported: " + selected);
    }
    
    public Properties getCurrentSecMechInfo()
    {
        return tanMethodsBank.get(getCurrentTANMethod(false));
    }
    
    public Hashtable<String, Properties> getTwostepMechanisms()
    {
    	return tanMethodsBank;
    }

    public String getProfileMethod()
    {
        return "PIN";
    }
    
    public String getProfileVersion()
    {
        return getCurrentTANMethod(false).equals(TanMethod.ONESTEP.getId())?"1":"2";
    }

    public boolean needUserKeys()
    {
        return false;
    }
    
    public boolean needInstKeys()
    {
        // TODO: das abhängig vom thema "bankensignatur für HKTAN" machen
        return false;
    }
    
    public boolean needUserSig()
    {
        return true;
    }
    
    public String getSysStatus()
    {
        return "1";
    }

    public boolean hasInstSigKey()
    {
        // TODO: hier müsste es eigentlich zwei antworten geben: eine für
        // das PIN/TAN-verfahren an sich (immer true) und eine für
        // evtl. bankensignatur-schlüssel für HITAN
        return true;
    }
    
    public boolean hasInstEncKey()
    {
        return true;
    }
    
    public boolean hasMySigKey()
    {
        return true;
    }
    
    public boolean hasMyEncKey()
    {
        return true;
    }
    
    public HBCIKey getInstSigKey()
    {
        // TODO: hier müsste es eigentlich zwei antworten geben: eine für
        // das PIN/TAN-verfahren an sich (immer null) und eine für
        // evtl. bankensignatur-schlüssel für HITAN
        return null;
    }
    
    public HBCIKey getInstEncKey()
    {
        return null;
    }
    
    public String getInstSigKeyName()
    {
        // TODO: evtl. zwei antworten: pin/tan und bankensignatur für HITAN
        return getUserId();
    }

    public String getInstSigKeyNum()
    {
        // TODO: evtl. zwei antworten: pin/tan und bankensignatur für HITAN
        return "0";
    }

    public String getInstSigKeyVersion()
    {
        // TODO: evtl. zwei antworten: pin/tan und bankensignatur für HITAN
        return "0";
    }

    public String getInstEncKeyName()
    {
        return getUserId();
    }

    public String getInstEncKeyNum()
    {
        return "0";
    }

    public String getInstEncKeyVersion()
    {
        return "0";
    }

    public String getMySigKeyName()
    {
        return getUserId();
    }

    public String getMySigKeyNum()
    {
        return "0";
    }

    public String getMySigKeyVersion()
    {
        return "0";
    }

    public String getMyEncKeyName()
    {
        return getUserId();
    }

    public String getMyEncKeyNum()
    {
        return "0";
    }

    public String getMyEncKeyVersion()
    {
        return "0";
    }
    
    public HBCIKey getMyPublicDigKey()
    {
        return null;
    }

    public HBCIKey getMyPrivateDigKey()
    {
        return null;
    }

    public HBCIKey getMyPublicSigKey()
    {
        return null;
    }

    public HBCIKey getMyPrivateSigKey()
    {
        return null;
    }

    public HBCIKey getMyPublicEncKey()
    {
        return null;
    }

    public HBCIKey getMyPrivateEncKey()
    {
        return null;
    }

    public String getCryptMode()
    {
        // dummy-wert
        return Crypt.ENCMODE_CBC;
    }

    public String getCryptAlg()
    {
        // dummy-wert
        return Crypt.ENCALG_2K3DES;
    }

    public String getCryptKeyType()
    {
        // dummy-wert
        return Crypt.ENC_KEYTYPE_DDV;
    }

    public String getSigFunction()
    {
        return getCurrentTANMethod(false);
    }

    public String getCryptFunction()
    {
        return Crypt.SECFUNC_ENC_PLAIN;
    }

    public String getSigAlg()
    {
        // dummy-wert
        return Sig.SIGALG_RSA;
    }

    public String getSigMode()
    {
        // dummy-wert
        return Sig.SIGMODE_ISO9796_1;
    }

    public String getHashAlg()
    {
        // dummy-wert
        return Sig.HASHALG_RIPEMD160;
    }
    
    public void setInstSigKey(HBCIKey key)
    {
    }

    public void setInstEncKey(HBCIKey key)
    {
        // TODO: implementieren für bankensignatur bei HITAN
    }

    public void setMyPublicDigKey(HBCIKey key)
    {
    }

    public void setMyPrivateDigKey(HBCIKey key)
    {
    }

    public void setMyPublicSigKey(HBCIKey key)
    {
    }

    public void setMyPrivateSigKey(HBCIKey key)
    {
    }

    public void setMyPublicEncKey(HBCIKey key)
    {
    }

    public void setMyPrivateEncKey(HBCIKey key)
    {
    }
    
    public void incSigId()
    {
        // for PinTan we always use the same sigid
    }

    protected String collectSegCodes(String msg)
    {
        StringBuffer ret=new StringBuffer();
        int          len=msg.length();
        int          posi=0;
        
        while (true) {
            int endPosi=msg.indexOf(':',posi);
            if (endPosi==-1) {
                break;
            }
            
            String segcode=msg.substring(posi,endPosi);
            if (ret.length()!=0) {
                ret.append("|");
            }
            ret.append(segcode);
            
            while (posi<len && msg.charAt(posi)!='\'') {
                posi=HBCIUtilsInternal.getPosiOfNextDelimiter(msg,posi+1);
            }
            if (posi>=len) {
                break;
            }
            posi++;
        }
        
        return ret.toString();
    }

    /**
     * Liefert "J" oder "N" aus den BPD des Geschaeftsvorfalls, ob fuer diesen eine TAN erforderlich ist.
     * @param code der GV-Code.
     * @return "J" oder "N". Oder "A", wenn es ein Admin-Segment ist, jedoch keine TAN noetig ist.
     */
    public String getPinTanInfo(String code)
    {
        String     ret="";
        Properties bpd = getBPD();
        
        if (bpd == null)
            return ret;
        
        boolean isGV = false;
        final String paramCode = StringUtil.toParameterCode(code);
        
        for (Enumeration e=bpd.propertyNames();e.hasMoreElements();) {
            String key=(String)e.nextElement();

            if (key.startsWith("Params")&&
                    key.substring(key.indexOf(".")+1).startsWith("PinTanPar") &&
                    key.indexOf(".ParPinTan.PinTanGV")!=-1 &&
                    key.endsWith(".segcode")) 
            {
                String code2=bpd.getProperty(key);
                if (code.equals(code2)) {
                    key=key.substring(0,key.length()-("segcode").length())+"needtan";
                    ret=bpd.getProperty(key);
                    break;
                }
            } else if (key.startsWith("Params")&&
                       key.endsWith(".SegHead.code")) {

                String code2=bpd.getProperty(key);
                if (paramCode.equals(code2)) {
                    isGV=true;
                }
            }
        }

        // wenn das kein GV ist, dann ist es ein Admin-Segment
        if (ret.length()==0&&!isGV) {
            if (verifyTANMode && code.equals("HKIDN")) {
                // im TAN-verify-mode wird bei der dialog-initialisierung
                // eine TAN mit versandt; die Dialog-Initialisierung erkennt
                // man am HKIDN-segment
                ret="J";
                deactivateTANVerifyMode();
            } else {
                ret="A";
            }
        }
        
        return ret;
    }

    public void deactivateTANVerifyMode()
    {
        this.verifyTANMode=false;
    }

    public void activateTANVerifyMode()
    {
        this.verifyTANMode=true;
    }

    public void setCertFile(String filename)
    {
        this.certfile=filename;
    }
    
    public String getCertFile()
    {
        return certfile;
    }
    
    protected void setCheckCert(boolean doCheck)
    {
        this.checkCert=doCheck;
    }
    
    public boolean getCheckCert()
    {
        return checkCert;
    }

    public String getProxy() 
    {
        return proxy;
    }

    public void setProxy(String proxy) 
    {
        this.proxy = proxy;
    }

    public String getProxyPass() 
    {
        return proxypass;
    }

    public String getProxyUser() 
    {
        return proxyuser;
    }

    public void setProxyPass(String proxypass) 
    {
        this.proxypass = proxypass;
    }

    public void setProxyUser(String proxyuser) 
    {
        this.proxyuser = proxyuser;
    }
    
    /**
     * Liefert den Code fuer den Hash-Modus, mit dem bei der HKTAN-Prozessvariante 1 das Auftragssegment gehasht werden soll.
     * @return der Order-Hashmode oder NULL, wenn er nicht ermittelbar ist.
     * @throws HBCI_Exception wenn ein ungueltiger Wert fuer den Hash-Mode in den BPD angegeben ist.
     */
    private String getOrderHashMode()
    {
        final Properties bpd = this.getBPD();
        if (bpd == null)
            return null;

        // Wir muessen auch bei der richtigen Segment-Version schauen
        final Properties props = this.getCurrentSecMechInfo();
        final String segVersion = props.getProperty("segversion");
        
        final String s = ParameterFinder.getValue(bpd,Query.BPD_PINTAN_ORDERHASHMODE.withParameters((segVersion != null ? segVersion : "")),null);
        
        if ("1".equals(s))
            return CryptUtils.HASH_ALG_RIPE_MD160;
        if ("2".equals(s))
            return CryptUtils.HASH_ALG_SHA1;
                    
        throw new HBCI_Exception("unknown orderhash mode " + s);
    }
    
    /**
     * Patcht die TAN-Abfrage bei Bedarf in die Nachricht.
     * Hinweis: Wir haben das ganze HKTAN-Handling derzeit leider doppelt. Einmal fuer die Dialog-Initialisierung (checkSCAResponse) und einmal fuer
     * die Nachrichten mit den eigentlichen Geschaeftsvorfaellen (in patchMessagesFor2StepMethods). Wenn auch HBCIDialog#doJobs irgendwann
     * auf die neuen RawHBCIDialoge umgestellt ist, kann eigentlich patchMessagesFor2StepMethods entfallen.
     * @param dialog der Dialog.
     * @param ret der aktuelle Dialog-Status.
     */
    private void patchMessagesFor2StepMethods(DialogContext ctx)
    {
        final HBCIDialog dialog = ctx.getDialog();
        if (dialog == null)
            return;
        
        final HBCIMessageQueue queue = dialog.getMessageQueue();
        if (queue == null)
            return;
        
        // Einschritt-Verfahren - kein HKTAN erforderlich
        final String tanMethod = this.getCurrentTANMethod(false);
        if (tanMethod.equals(TanMethod.ONESTEP.getId()))
            return;

        // wenn es sich um das pintan-verfahren im zweischritt-modus handelt,
        // müssen evtl. zusätzliche nachrichten bzw. segmente eingeführt werden
        HBCIUtils.log("patching message for twostep method",HBCIUtils.LOG_DEBUG);
        
        final HBCIHandler handler    = (HBCIHandler) this.getParentHandlerData();
        final Properties secmechInfo = this.getCurrentSecMechInfo();
        final String segversion      = secmechInfo.getProperty("segversion");
        final String process         = secmechInfo.getProperty("process");

        for (HBCIMessage message:queue.getMessages())
        {
            for (HBCIJobImpl task:message.getTasks())
            {
                // Damit wir keine doppelten erzeugen
                if (task.haveTan())
                    continue;
                
                final String segcode = task.getHBCICode();
                
                // Braucht der Job eine TAN?
                if (!this.getPinTanInfo(segcode).equals("J"))
                {
                    HBCIUtils.log("found task that does not require HKTAN: " + segcode + " - adding it to current msg",HBCIUtils.LOG_DEBUG);
                    continue;
                }
    
                // OK, Task braucht vermutlich eine TAN - es handelt sich um einen tan-pflichtigen task
                // Ob letztlich tatsaechlich beim User eine TAN-Abfrage ankommt, haengt davon ab, ob die Bank ggf. eine 3076 SCA-Ausnahme sendet
                HBCIUtils.log("found task that probably requires HKTAN: " + segcode + " - have to patch message queue",HBCIUtils.LOG_DEBUG);
                
                final GVTAN2Step hktan = (GVTAN2Step) handler.newJob("TAN2Step");
                hktan.setParam("ordersegcode",task.getHBCICode()); // Seit HKTAN auch bei HKTAN#6 Pflicht
                hktan.setExternalId(task.getExternalId()); // externe ID durchreichen
                hktan.setSegVersion(segversion); // muessen wir explizit setzen, damit wir das HKTAN in der gleichen Version schicken, in der das HITANS kam.
                task.tanApplied();
                
                final String tanMedia = this.getTanMedia(Integer.parseInt(hktan.getSegVersion()));
                if (tanMedia != null && tanMedia.length() > 0) // tanmedia nur setzen, wenn vorhanden Sonst meckert HBCIJobIml
                    hktan.setParam("tanmedia",tanMedia);
                
                
                ////////////////////////////////////////////////////////////////////////////
                // Prozess-Variante 1:
                // 1. Nur HKTAN mit dem Hash des Auftragssegments einreichen, dann per HITAN die TAN generieren
                // 2. Auftrag + TAN (HNSHA) einreichen
                if (process.equals("1"))
                {
                    HBCIUtils.log("process variant 1: adding new message with HKTAN(p=1,hash=...) before current message",HBCIUtils.LOG_DEBUG);
                    hktan.setProcess(KnownTANProcess.PROCESS1);
                    hktan.setParam("notlasttan","N");
                    
                    // willuhn 2011-05-16
                    // Siehe FinTS_3.0_Security_Sicherheitsverfahren_PINTAN_Rel_20101027_final_version.pdf, Seite 58
                    int hktanVersion = Integer.parseInt(hktan.getSegVersion());
                    if (hktanVersion >= 5)
                    {
                      // Zitat aus HITANS5: Diese Funktion ermöglicht das Sicherstellen einer gültigen Kontoverbindung
                      // z. B. für die Abrechnung von SMS-Kosten bereits vor Erzeugen und Versenden einer
                      // (ggf. kostenpflichtigen!) TAN.
                      //  0: Auftraggeberkonto darf nicht angegeben werden
                      //  2: Auftraggeberkonto muss angegeben werden, wenn im Geschäftsvorfall enthalten
                      String noa = secmechInfo.getProperty("needorderaccount","");
                      HBCIUtils.log("needorderaccount=" + noa,HBCIUtils.LOG_DEBUG);
                      if (noa.equals("2"))
                      {
                        Konto k = task.getOrderAccount();
                        if (k != null)
                        {
                            HBCIUtils.log("applying orderaccount to HKTAN for " + task.getHBCICode(),HBCIUtils.LOG_DEBUG);
                            hktan.setParam("orderaccount",k);
                        }
                        else
                        {
                            HBCIUtils.log("orderaccount needed, but not found in " + task.getHBCICode(),HBCIUtils.LOG_WARN);
                        }
                      }
                    }
    
                    // Challenge-Klasse, wenn erforderlich
                    if (secmechInfo.getProperty("needchallengeklass","N").equals("J"))
                    {
                        ChallengeInfo cinfo = ChallengeInfo.getInstance();
                        cinfo.applyParams(task,hktan,secmechInfo);
                    }
    
                    // orderhash ermitteln
                    SEG seg = null;
                    try
                    {
                        seg = task.createJobSegment(3); // FIXME: hartcodierte Segment-Nummer. Zu dem Zeitpunkt wissen wir sie noch nicht.
                        seg.validate();
                        final String segdata = seg.toString(0);
                        HBCIUtils.log("calculating hash for jobsegment: " + segdata,HBCIUtils.LOG_DEBUG2);
                        hktan.setParam("orderhash",CryptUtils.hash(segdata,this.getOrderHashMode()));
                    }
                    finally
                    {
                        SEGFactory.getInstance().unuseObject(seg);
                    }
    
                    // HKTAN in einer neuen Nachricht *vor* dem eigentlichen Auftrag einreihen
                    HBCIMessage newMsg = queue.insertBefore(message);
                    newMsg.append(hktan);
                }
                //
                ////////////////////////////////////////////////////////////////////////////
                
                ////////////////////////////////////////////////////////////////////////////
                // Prozess-Variante 2:
                // 1. Auftrag + HKTAN einreichen, dann per HITAN die TAN generieren
                // 2. HKTAN mit Referenz zum Auftrag und TAN(HNSHA) einreichen
                else
                {
                    HBCIUtils.log("process variant 2: adding new task HKTAN(p=4) to current message",HBCIUtils.LOG_DEBUG);
                    hktan.setProcess(KnownTANProcess.PROCESS2_STEP1);
    
                    // das HKTAN direkt dahinter - in der selben Nachricht
                    message.append(hktan);
                    
                    // Neue Nachricht fuer das zweite HKTAN
                    HBCIUtils.log("process variant 2: creating new msg with HKTAN(p=2,orderref=DELAYED)",HBCIUtils.LOG_DEBUG);

                    // Decoupled Verfahren - Sonderbehandlung bei TAN-Prozess 2
                    KnownTANProcess proc = KnownTANProcess.PROCESS2_STEP2;
                    final HHDVersion hhd = HHDVersion.find(secmechInfo);
                    HBCIUtils.log("detected HHD version: " + hhd,HBCIUtils.LOG_DEBUG);
                    if (hhd.getType() == Type.DECOUPLED)
                    {
                      Integer i = null;
                      try
                      {
                        i = Integer.parseInt(segversion);
                      }
                      catch (Exception e) {}
                      if (i != null && i.intValue() >= 7)
                      {
                        HBCIUtils.log("using decoupled hktan for step 2",HBCIUtils.LOG_DEBUG);
                        proc = KnownTANProcess.PROCESS2_STEPS;
                      }
                    }

                    // HKTAN-job für das Einreichen der TAN erzeugen
                    final GVTAN2Step hktan2 = (GVTAN2Step) handler.newJob("TAN2Step");
                    hktan2.setProcess(proc);
                    hktan2.setExternalId(task.getExternalId()); // externe ID auch an HKTAN2 durchreichen
                    hktan2.setSegVersion(segversion);
                    hktan2.setParam("notlasttan","N");
    
                    // in dem zweiten HKTAN-Job eine referenz auf den originalen job
                    // speichern, damit die antwortdaten für den job, die als antwortdaten
                    // für hktan2 ankommen, dem richtigen job zugeordnet werden können
                    HBCIUtils.log("storing reference to original job in new HKTAN segment",HBCIUtils.LOG_DEBUG);
                    hktan2.setTask(task);
    
                    // in dem ersten HKTAN-job eine referenz auf den zweiten speichern,
                    // damit der erste die auftragsreferenz später im zweiten speichern kann
                    hktan.setStep2(hktan2);
    
                    // Dahinter eine neue Nachricht mit dem einzelnen HKTAN#2
                    HBCIUtils.log("adding new message with HKTAN(p=2) after current one",HBCIUtils.LOG_DEBUG);
                    HBCIMessage newMsg = queue.insertAfter(message);
                    newMsg.append(hktan2);
                }
            }
        }
    }
    
    /**
     * Uebernimmt das Rueckfragen der TAN-Medien-Bezeichung bei Bedarf.
     * @param segVersion die HKTAN-Versionsnummer.
     * @return das ausgewaehlte TAN-Medium oder einen Leerstring, wenn keines verfuegbar war oder keines noetig ist (bei HKTAN < 3).
     */
    private String getTanMedia(int segVersion)
    {
        // Gibts erst ab hhd1.3, siehe
        // FinTS_3.0_Security_Sicherheitsverfahren_PINTAN_Rel_20101027_final_version.pdf, Kapitel B.4.3.1.1.1
        HBCIUtils.log("HKTAN version: " + segVersion,HBCIUtils.LOG_DEBUG);
        if (segVersion < 3)
            return "";

        final Properties  secmechInfo = this.getCurrentSecMechInfo();

        // Brauchen wir ein TAN-Medium?
        final String needed = secmechInfo != null ? secmechInfo.getProperty("needtanmedia","") : "";
        HBCIUtils.log("needtanmedia: " + needed,HBCIUtils.LOG_DEBUG);
    
        final boolean tn = Objects.equals(needed,"2");
        if (tn)
        {
            HBCIUtils.log("we have to add the tan media",HBCIUtils.LOG_DEBUG);
    
            final StringBuffer retData = new StringBuffer();
            
            // Namen der TAN-Medien als Auswahl anbieten, falls vorhanden
            final Properties upd = this.getUPD();
            if (upd != null)
                retData.append(upd.getProperty(HBCIUser.UPD_KEY_TANMEDIA,""));
            
            HBCIUtilsInternal.getCallback().callback(this,HBCICallback.NEED_PT_TANMEDIA,"*** Enter the name of your TAN media",HBCICallback.TYPE_TEXT,retData);
            final String result = retData.toString();
            if (StringUtil.hasText(result))
                return result;
        }
        
        // Seit HKTAN 6: Wenn die Angabe eines TAN-Mediennamens laut BPD erforderlich ist, wir aber gar keinen Namen haben,
        // dann "noref" eintragen.
        return tn ? "noref" : "";
    }
    
    public void setPIN(String pin)
    {
        this.pin=pin;
    }
    
    public String getPIN()
    {
        return this.pin;
    }
    
    public void clearPIN()
    {
        setPIN(null);
    }
    
    public List<String> getAllowedTwostepMechanisms() 
    {
        return this.tanMethodsUser;
    }
    
    public void setAllowedTwostepMechanisms(List<String> l)
    {
        this.tanMethodsUser=l;
    }
    
    public int getMaxGVSegsPerMsg()
    {
        return 1;
    }
    
    /**
     * Ueberschrieben, um das "https://" am Anfang automatisch abzuschneiden.
     * Das sorgte schon fuer so viele unnoetige Fehler.
     * @see org.kapott.hbci.passport.AbstractHBCIPassport#getHost()
     */
    @Override
    public String getHost()
    {
      String host = super.getHost();
      if (host == null || host.length() == 0 || !host.startsWith("https://"))
        return host;
      
      return host.replace("https://","");
    }
}