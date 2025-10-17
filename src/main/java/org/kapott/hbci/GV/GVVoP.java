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


import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRVoP;
import org.kapott.hbci.GV_Result.GVRVoP.VoPResult;
import org.kapott.hbci.GV_Result.GVRVoP.VoPResultItem;
import org.kapott.hbci.GV_Result.GVRVoP.VoPStatus;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.dialog.DialogContext;
import org.kapott.hbci.dialog.HBCIMessage;
import org.kapott.hbci.dialog.HBCIMessageQueue;
import org.kapott.hbci.dialog.KnownReturncode;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.tools.StringUtil;

/**
 * Der Geschaeftsvorfall für den VoP-Prüfauftrag.
 */
public class GVVoP extends HBCIJobImpl<GVRVoP>
{
  /**
   * Referenz auf den Auftrag, für den wir die VoP machen.
   */
  private HBCIJobImpl task = null;
  
  /**
   * Der Dialog-Context.
   */
  private DialogContext ctx = null;

    /**
     * Liefert den Lowlevel-Namen.
     * @return der Lowlevel-Name.
     */
    public static String getLowlevelName()
    {
        return "VoPCheck";
    }

    /**
     * ct.
     * @param handler
     */
    public GVVoP(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRVoP());
        
        addConstraint("suppreports.descriptor","suppreports.descriptor","",LogFilter.FILTER_NONE);
        addConstraint("pollingid","pollingid",null,LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries",null,LogFilter.FILTER_NONE);
        addConstraint("offset","offset",null,LogFilter.FILTER_NONE);
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
     * @see org.kapott.hbci.GV.HBCIJobImpl#skipBPDCheck()
     */
    @Override
    protected boolean skipBPDCheck()
    {
      return true;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#setParam(java.lang.String, java.lang.String)
     */
    public void setParam(String paramName, String value)
    {
        if (paramName.equals("pollingid"))
            value="B"+value;
        super.setParam(paramName,value);
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
      final Properties data = msgstatus.getData();
      
      final String segCode = data.getProperty(header+".SegHead.code"); // HITAN oder HIVPP
      if (!StringUtil.toInsCode(this.getHBCICode()).equals(segCode)) // Das ist nicht unser Response
      {
        // TODO: Das kann möglicherweise entfernt werden
        HBCIUtils.log("got VoP response for " + segCode + " - not for us",HBCIUtils.LOG_INFO);
        return;
      }

      // Wenn die Bank hier mit Status 3091 antwortet, verzichtet sie auf VoP Auth
      // In dem Fall können wir uns das VopAuth schenken und wir sind schon fertig
      boolean noVoP = KnownReturncode.W3091.searchReturnValue(msgstatus.segStatus.getWarnings()) != null ||
                      KnownReturncode.W3091.searchReturnValue(msgstatus.globStatus.getWarnings()) != null;
      
      if (noVoP)
      {
        HBCIUtils.log("got response code 3091 - VoP auth can be skipped",HBCIUtils.LOG_INFO);
        return;
      }
      
      final VoPResult result = this.parse(data,header);
      this.getJobResult().setResult(result);

      // erhaltene VoP-ID in den GV mit der Freigabe übertragen
      final String vopId = result.getVopId();
      
      // Wenn es noch keine ID gibt, müssen wir pollen, das wird durch die 3040 und redo() automatisch gemacht
      if (vopId == null || vopId.length() == 0)
      {
        // Wir müssen hier eigentlich die zu wartende Zeit aus den BPD holen. Da das aber ohnehin nur ein
        // paar Sekunden sein können, warten wir einfach pauschal 2 Sekunden
        try
        {
          HBCIUtils.log("have no vop-id, polling required, waiting 2 seconds before retry",HBCIUtils.LOG_INFO);
          Thread.sleep(2000L);
        }
        catch (InterruptedException e) {}
        
        HBCIUtils.log("send vop polling message",HBCIUtils.LOG_INFO);
        this.setParam("pollingid", result.getPollingId());
        // Task als einzelne Polling-Nachricht direkt als nächstes ausführen - noch vor allen anderen Nachrichten
        final HBCIMessageQueue queue = this.ctx.getDialog().getMessageQueue();
        final HBCIMessage msg = new HBCIMessage();
        msg.append(this);
        queue.prepend(msg);

        return;
      }
      
      final boolean needCallback = result.getItems().stream().filter(r -> !Objects.equals(r.getStatus(),VoPStatus.MATCH)).count() > 0;
      if (needCallback)
      {
        HBCIUtils.log("VoP callback needed",HBCIUtils.LOG_INFO);
        final HBCIPassportInternal p = this.getMainPassport();
        try
        {
          // VOP-Result im Passport speichern und User fragen, ob der Vorgang fortgesetzt werden kann
          p.setPersistentData(AbstractHBCIPassport.KEY_VOP_RESULT,result);
          final StringBuffer sb = new StringBuffer();
          HBCIUtilsInternal.getCallback().callback(p,HBCICallback.HAVE_VOP_RESULT,result.getText(),HBCICallback.TYPE_BOOLEAN,sb);
          if (!StringUtil.toBoolean(sb.toString()))
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_VOP_CANCEL"));
        }
        finally
        {
          p.setPersistentData(AbstractHBCIPassport.KEY_VOP_RESULT,null);
        }
      }
      
      final GVVoPAuth auth = (GVVoPAuth) this.getParentHandler().newJob("VoPAuth"); // Die VoP Freigabe
      auth.setTask(task); // Referenz auf den Auftrag speichern
      auth.setParam("vopid",vopId);
      
      // VOP-Freigabe in die Queue - zusammen mit nochmal dem Auftrag
      // Wir suchen hier die Message mit dem HKTAN und fügen es dort mit ein
      HBCIUtils.log("adding new vop-auth message to queue [vop-id: " + vopId + "]",HBCIUtils.LOG_INFO);
      final HBCIMessageQueue queue = this.ctx.getDialog().getMessageQueue();
      final HBCIMessage msg = new HBCIMessage();
      queue.prepend(msg);
      
      // Wir müssen den Auftrag zusammen mit dem HKVPA NICHT nochmal mitsenden bei PIN/TAN und Match
      // Laut FinTS_3.0_Messages_Geschaeftsvorfaelle_VOP_1.01_2025_06_27_FV.pdf Seite 14:
      // "Falls das Ergebnis der VOP-Prüfung Match ist, *KANN* im PIN/TAN Verfahren ggf. auf die Einreichung
      // des HKVPA seitens des Kreditinstituts verzichtet werden. Dies wird dem Kundenprodukt durch den 
      // Rückmeldungscode 3091 angezeigt. In diesem Fall ist lediglich die Challenge im HITAN durch einen HKTAN zu beantworten.
      // Das heisst: Wir dürfen den eigentlichen Auftrag nochmal mit schicken und müssten nicht den Aufwand betreiben, ihn
      // nur in diesem einen Fall wegzulassen.
      msg.append(task);
      
      msg.append(auth);
      task.vopApplied();
    }
    
    /**
     * Extrahiert das VoP-Result aus den Daten.
     * @param data die Daten.
     * @param header der Header-Prefix.
     * @return die VoP-Daten.
     */
    private VoPResult parse(Properties data, String header)
    {
      final VoPResult result = new VoPResult();

      result.setVopId(data.getProperty(header + ".vopid")); // vopid kann leer sein bei Teillieferungen.
      result.setPollingId(data.getProperty(header + ".pollingid"));
      result.setText(data.getProperty(header + ".infotext"));

      // Wir kriegen entweder ein XML vom Typ pain.002.001.*, wenn es ein Sammelauftrag war
      final String desc = data.getProperty(header + ".reportdesc"); // Ich nehme an, das ist die Schema-Kennung
      final String xml  = data.getProperty(header + ".report");
      if (StringUtil.hasText(desc) && StringUtil.hasText(xml))
      {
        HBCIUtils.log("got VoP multiple result [desc: " + desc + "]",HBCIUtils.LOG_INFO);

        try
        {
          final SepaVersion version = SepaVersion.choose(null,xml);
          ISEPAParser<List<VoPResultItem>> parser = SEPAParserFactory.get(version);
          
          HBCIUtils.log("parsing pain.002 data: " + xml,HBCIUtils.LOG_DEBUG2);
          parser.parse(new ByteArrayInputStream(xml.getBytes(Comm.ENCODING)),result.getItems());
          HBCIUtils.log("parsed pain data, entries: " + result.getItems().size(),HBCIUtils.LOG_DEBUG);
        }
        catch (Exception e)
        {
          HBCIUtils.log("unable to parse pain.002 data: " + e.getMessage(),HBCIUtils.LOG_ERR);
          throw new HBCI_Exception("Error while parsing pain VoP document",e);
        }
      }
      else
      {
        HBCIUtils.log("got VoP single result",HBCIUtils.LOG_INFO);
        
        // oder alternativ das Prüfergebnis einer Einzelprüfung
        final VoPResultItem r = new VoPResultItem();
        r.setIban(data.getProperty(header + ".result.iban")); // Die IBAN des Empfängers
        r.setName(data.getProperty(header + ".result.differentname")); // der korrigierte Name des Empfängers
        r.setStatus(VoPStatus.byCode(data.getProperty(header + ".result.result"))); // der Status-Code
        r.setText(data.getProperty(header + ".result.reason")); // Falls Status "Not Applicable" ist: Ein Hinweis-Text
        result.getItems().add(r);
      }
      
      return result;
    }
}
