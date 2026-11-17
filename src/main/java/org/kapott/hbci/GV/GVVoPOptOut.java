/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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

package org.kapott.hbci.GV;

import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRVoP;
import org.kapott.hbci.GV_Result.GVRVoP.VoPResult;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.dialog.DialogContext;
import org.kapott.hbci.dialog.HBCIMessage;
import org.kapott.hbci.dialog.HBCIMessageQueue;
import org.kapott.hbci.dialog.KnownReturncode;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.tools.StringUtil;

/**
 * Der Geschaeftsvorfall für den Verzicht auf den Namensabgleich (VoP Opt-Out, HKVOO).
 * Er wird anstelle des HKVPP zusammen mit einem Sammelauftrag gesendet. Die Bank
 * antwortet mit einer VoP-ID und einem Aufklärungstext (HIVOO). Bestätigt der User
 * den Text, wird das HKVPA gesendet: bei PIN/TAN zusammen mit dem HKTAN zur Freigabe,
 * nach RM 3945 oder bei Kryptografie zusammen mit dem erneut eingereichten Auftrag.
 */
public class GVVoPOptOut extends HBCIJobImpl<GVRVoP>
{
  /**
   * Referenz auf den Auftrag, für den wir auf die VoP verzichten.
   */
  private HBCIJobImpl task = null;

  /**
   * Der Dialog-Context.
   */
  private DialogContext ctx = null;

  /**
   * Die Segment-Kennung.
   */
  private final static String CODE = "HKVOO";

  /**
   * Liefert den Lowlevel-Namen.
   * @return der Lowlevel-Name.
   */
  public static String getLowlevelName()
  {
    return "VoPOptOut";
  }

  /**
   * ct.
   * @param handler
   */
  public GVVoPOptOut(HBCIHandler handler)
  {
    super(handler,getLowlevelName(),new GVRVoP());
  }

  /**
   * Prüft, ob für den Auftrag auf den Namensabgleich verzichtet werden darf.
   * Opt-Out ist nur für Sammelaufträge mit mehr als einer Transaktion zulässig.
   * Ein Sammelauftrag mit nur einem Auftrag muss immer mit HKVPP eingereicht werden.
   * Außerdem muss HKVOO in den UPD für das Auftraggeberkonto erlaubt sein.
   * Siehe FinTS_3.0_Messages_Geschaeftsvorfaelle_VOP_1.02_2026_10_19_FV.pdf, Kapitel C.10.7.2.
   * @param task der Auftrag.
   * @return true, wenn Opt-Out für den Auftrag zulässig ist.
   */
  public static boolean isAllowed(HBCIJobImpl task)
  {
    if (!(task instanceof AbstractSEPAGV))
      return false;

    final Properties sepaParams = ((AbstractSEPAGV) task).sepaParams;
    final Integer maxIndex = SepaUtil.maxIndex(sepaParams);
    if (maxIndex == null || maxIndex.intValue() < 1)
    {
      HBCIUtils.log("VoP opt-out not allowed for orders with only one transaction",HBCIUtils.LOG_INFO);
      return false;
    }

    // Die Freigabe erfolgt pro Konto über die "erlaubten Geschäftsvorfälle" im HIUPD.
    // Finden wir das Konto nicht in den UPD oder fehlt die Liste, verzichten wir auf Opt-Out.
    final Konto account = findAccount(task);
    final boolean allowed = account != null && account.allowedGVs != null && account.allowedGVs.contains(CODE);
    if (!allowed)
      HBCIUtils.log("VoP opt-out not allowed for the order account in UPD",HBCIUtils.LOG_INFO);
    return allowed;
  }

  /**
   * Sucht das Auftraggeberkonto in den UPD.
   * @param task der Auftrag.
   * @return das Konto aus den UPD oder NULL, wenn es nicht gefunden wurde.
   */
  private static Konto findAccount(HBCIJobImpl task)
  {
    final Konto k = task.getOrderAccount();
    if (k == null)
      return null;

    final String iban = normalize(k.iban);
    final String number = HBCIUtilsInternal.stripLeadingZeroes(k.number);
    final String subnumber = HBCIUtilsInternal.stripLeadingZeroes(k.subnumber);

    for (Konto a:task.getMainPassport().getAccounts())
    {
      if (iban != null && iban.equals(normalize(a.iban)))
        return a;

      if (number != null && number.length() > 0 && number.equals(HBCIUtilsInternal.stripLeadingZeroes(a.number)) &&
          (subnumber == null || subnumber.length() == 0 || subnumber.equals(HBCIUtilsInternal.stripLeadingZeroes(a.subnumber))))
        return a;
    }
    return null;
  }

  /**
   * Entfernt Leerzeichen aus der IBAN und wandelt sie in Großbuchstaben.
   * @param iban die IBAN.
   * @return die normalisierte IBAN oder NULL, wenn keine angegeben ist.
   */
  private static String normalize(String iban)
  {
    if (iban == null || iban.isBlank())
      return null;
    return iban.replace(" ","").toUpperCase();
  }

  /**
   * Prüft, ob der Auftrag zusammen mit dem HKVPA erneut eingereicht werden muss.
   * Bei PIN/TAN entfällt das (Kapitel C.10.7, Opt-Out), außer die Bank meldet 3945 zum HKTAN
   * (Kapitel E.8.1, Punkt 5 und Abbildung 5). Bei Kryptografie und ggf. Secoder ist es immer Pflicht.
   * @param passport der Passport.
   * @param status der Status der Nachricht mit dem HIVOO.
   * @return true, wenn der Auftrag erneut eingereicht werden muss.
   */
  static boolean needResubmit(HBCIPassportInternal passport, HBCIMsgStatus status)
  {
    if (!(passport instanceof AbstractPinTanPassport))
      return true;
    return !KnownReturncode.W3945.searchReturnValues(status).isEmpty();
  }

  /**
   * Speichert eine Referenz auf den eigentlichen Geschaeftsvorfall.
   * @param task
   */
  public void setTask(HBCIJobImpl task)
  {
    this.task = task;
  }

  /**
   * Speichert den Dialog-Context.
   * @param ctx der Dialog-Context.
   */
  public void setDialogContext(DialogContext ctx)
  {
    this.ctx = ctx;
  }

  /**
   * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
   */
  protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
  {
    final Properties data = msgstatus.getData();

    final String segCode = data.getProperty(header+".SegHead.code");
    if (!StringUtil.toInsCode(this.getHBCICode()).equals(segCode)) // Das ist nicht unser Response
    {
      HBCIUtils.log("got VoP opt-out response for " + segCode + " - not for us",HBCIUtils.LOG_INFO);
      return;
    }

    final VoPResult result = new VoPResult();
    result.setVopId(data.getProperty(header + ".vopid"));
    result.setText(data.getProperty(header + ".infotext"));
    this.getJobResult().setResult(result);

    final String vopId = result.getVopId();
    if (vopId == null || vopId.length() == 0)
    {
      HBCIUtils.log("Got VoP opt-out response without vop-id",HBCIUtils.LOG_ERR);
      throw new HBCI_Exception("Got VoP opt-out response without vop-id");
    }

    // Der User muss den Aufklärungstext immer bestätigen - es gibt hier keine Prüfergebnisse
    HBCIUtils.log("VoP opt-out callback needed",HBCIUtils.LOG_INFO);
    final HBCIPassportInternal p = this.getMainPassport();
    try
    {
      p.setPersistentData(AbstractHBCIPassport.KEY_VOP_RESULT,result);
      p.setPersistentData(AbstractHBCIPassport.KEY_EXTERNAL_ID,task != null ? task.getExternalId() : null);
      final StringBuffer sb = new StringBuffer();
      HBCIUtilsInternal.getCallback().callback(p,HBCICallback.HAVE_VOP_RESULT,result.getText(),HBCICallback.TYPE_BOOLEAN,sb);
      if (!StringUtil.toBoolean(sb.toString()))
        throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_VOP_OPTOUT_CANCEL"));
    }
    finally
    {
      p.setPersistentData(AbstractHBCIPassport.KEY_VOP_RESULT,null);
      p.setPersistentData(AbstractHBCIPassport.KEY_EXTERNAL_ID,null);
    }

    final GVVoPAuth auth = (GVVoPAuth) this.getParentHandler().newJob("VoPAuth"); // Die VoP Freigabe
    auth.setTask(task); // Referenz auf den Auftrag speichern
    auth.setParam("vopid",vopId);

    final HBCIMessageQueue queue = this.ctx.getDialog().getMessageQueue();

    // Nach 3945 oder bei Kryptografie: HKVPA zusammen mit dem Auftrag erneut einreichen.
    // Das neue HKTAN erzeugt dann wie beim Close-Match der Passport.
    if (needResubmit(p,msgstatus))
    {
      HBCIUtils.log("adding new vop-auth message with order to queue after opt-out [vop-id: " + vopId + "]",HBCIUtils.LOG_INFO);
      final HBCIMessage msg = new HBCIMessage();
      queue.prepend(msg);
      msg.append(auth);
      msg.append(task);
      return;
    }

    // PIN/TAN ohne 3945: Der Auftrag wird nicht erneut eingereicht. Das HKVPA geht
    // zusammen mit dem HKTAN#2, mit dem die Challenge beantwortet wird (analog Abbildung 3).
    for (HBCIMessage msg:queue.getMessages())
    {
      final HBCIJobImpl t = msg.findTask("HKTAN");
      if (t instanceof GVTAN2Step && ((GVTAN2Step)t).belongToTask(task))
      {
        HBCIUtils.log("adding vop-auth to HKTAN message after opt-out [vop-id: " + vopId + "]",HBCIUtils.LOG_INFO);
        msg.prepend(t,auth);
        return;
      }
    }

    // Kein HKTAN#2 vorhanden (z.B. Auftrag ohne TAN). Dann HKVPA allein senden.
    HBCIUtils.log("adding new vop-auth message without order to queue after opt-out [vop-id: " + vopId + "]",HBCIUtils.LOG_INFO);
    final HBCIMessage msg = new HBCIMessage();
    queue.prepend(msg);
    msg.append(auth);
  }
}
