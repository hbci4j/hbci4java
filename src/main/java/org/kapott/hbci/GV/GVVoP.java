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
import org.kapott.hbci.dialog.KnownReturncode;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.tools.StringUtil;

/**
 * Der Geschaeftsvorfall für den VoP-Prüfauftrag.
 */
public class GVVoP extends HBCIJobImpl<GVRVoP>
{
    // Referenz auf den Freigabe-Task.
    private GVVoPAuth auth;

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
     * Speichert eine Referenz auf den GV mit der Freigabe.
     * @param auth der GV mit der Freigabe.
     */
    public void setAuth(GVVoPAuth auth)
    {
        this.auth = auth;
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
        // TODO VOP: Checken, ob der Fall überhaupt eintreten kann
        HBCIUtils.log("got VoP response for " + segCode + " - not for us",HBCIUtils.LOG_INFO);
        return;
      }

      // Wenn die Bank hier mit Status 3091 antwortet, verzichtet sie auf VoP Auth
      // In dem Fall müssen wir die extra Nachricht wieder entfernen
      boolean noVoP = KnownReturncode.W3091.searchReturnValue(msgstatus.segStatus.getWarnings()) != null ||
                      KnownReturncode.W3091.searchReturnValue(msgstatus.globStatus.getWarnings()) != null;
      
      if (noVoP)
      {
        HBCIUtils.log("got response code 3091 - VoP auth can be skipped",HBCIUtils.LOG_INFO);
        this.auth.skip();
        return;
      }
      
      final VoPResult result = this.parse(data,header);
      this.getJobResult().setResult(result);

      // erhaltene VoP-ID in den GV mit der Freigabe übertragen
      final String vopId = result.getVopId();
      
      //Wenn es noch keine ID gibt, müssen wir pollen, das wird durch die 3040 und redo() automatisch gemacht
      if (vopId == null || vopId.length() == 0)
      {
        // TODO VOP: Hier wird eigentlich eine Wartezeit vorgegeben, wir machen es jedoch direkt nacheinander
        this.setParam("pollingid", result.getPollingId());
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
          p.setPersistentData(AbstractPinTanPassport.KEY_VOP_RESULT,result);
          final StringBuffer sb = new StringBuffer();
          HBCIUtilsInternal.getCallback().callback(p,HBCICallback.HAVE_VOP_RESULT,result.getText(),HBCICallback.TYPE_BOOLEAN,sb);
          if (!StringUtil.toBoolean(sb.toString()))
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_VOP_CANCEL"));
        }
        finally
        {
          p.setPersistentData(AbstractPinTanPassport.KEY_VOP_RESULT,null);
        }
      }
      
      HBCIUtils.log("apply vop-id '" + vopId,HBCIUtils.LOG_INFO);
      this.auth.setParam("vopid",vopId);
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
    
    @Override
    protected boolean redoAllowed()
    {
      
      return true;
    }
}
