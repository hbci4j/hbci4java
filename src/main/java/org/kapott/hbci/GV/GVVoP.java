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

package org.kapott.hbci.GV;


import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.GV.parsers.ISEPAParser;
import org.kapott.hbci.GV.parsers.SEPAParserFactory;
import org.kapott.hbci.GV_Result.GVRVoP;
import org.kapott.hbci.GV_Result.GVRVoP.VoPResult;
import org.kapott.hbci.GV_Result.GVRVoP.VoPStatus;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.sepa.SepaVersion;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.tools.StringUtil;

/**
 * Die Geschaeftsvorfall-Implementierung fuer VoP.
 */
public class GVVoP extends HBCIJobImpl<GVRVoP>
{
    private HBCIJobImpl task;
    private HBCIJobImpl redo;

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
     * Speichert eine Referenz auf den eigentlichen Geschaeftsvorfall.
     * @param task
     */
    public void setTask(HBCIJobImpl task)
    {
        this.task = task;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#saveReturnValues(org.kapott.hbci.status.HBCIMsgStatus, int)
     */
    protected void saveReturnValues(HBCIMsgStatus status, int sref)
    {
        super.saveReturnValues(status, sref);
        
        // Rueckgabecode an den eigentlichen Auftrag weiterreichen
        if (this.task != null)
        {
            int orig_segnum=Integer.parseInt(task.getJobResult().getSegNum());
            HBCIUtils.log("storing return values in orig task (segnum="+orig_segnum+")", HBCIUtils.LOG_DEBUG);
            task.saveReturnValues(status,orig_segnum);
        }
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#redo()
     */
    @Override
    public HBCIJobImpl redo()
    {
      // TODO: redo noch klären
      return this.redo;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#haveTan()
     */
    @Override
    public boolean haveTan()
    {
        // VoP kann nie ein HKTAN benoetigen - die wird nur fuer den eigentlichen Auftrag gebraucht.
        return true;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
      final Properties data = msgstatus.getData();
      final String segCode = data.getProperty(header + ".SegHead.code"); // HIVPP oder das HI** des GV
      HBCIUtils.log("found HKVPP response with segcode " + segCode,HBCIUtils.LOG_DEBUG);
      
      final GVRVoP vop = this.getJobResult();

      // Aufkärungstext bei Abweichung
      vop.setText(data.getProperty(header + ".infotext"));

      // vopid kann leer sein bei Teillieferungen.
      // TODO: Die unterstützen wir im ersten Schritt noch nicht.
      final String vopid     = data.getProperty(header + ".vopid");
      final String pollingid = data.getProperty(header + ".pollingid");
      
      // Wir kriegen entweder ein XML vom Typ pain.002.001.*, wenn es ein Sammelauftrag war
      final String desc = data.getProperty(header + ".reportdesc"); // Ich nehme an, das ist die Schema-Kennung
      final String xml  = data.getProperty(header + ".report");
      
      if (StringUtil.hasText(desc) && StringUtil.hasText(xml))
      {
        HBCIUtils.log("got VoP multiple result [desc: " + desc + "]",HBCIUtils.LOG_INFO);

        try
        {
          final SepaVersion version = SepaVersion.choose(null,xml);
          ISEPAParser<List<VoPResult>> parser = SEPAParserFactory.get(version);
          
          HBCIUtils.log("  parsing pain.002 data: " + xml,HBCIUtils.LOG_DEBUG2);
          parser.parse(new ByteArrayInputStream(xml.getBytes(Comm.ENCODING)),vop.getResults());
          HBCIUtils.log("  parsed pain data, entries: " + vop.getResults().size(),HBCIUtils.LOG_DEBUG);
        }
        catch (Exception e)
        {
          HBCIUtils.log("  unable to parse pain.002 data: " + e.getMessage(),HBCIUtils.LOG_ERR);
          throw new HBCI_Exception("Error while parsing pain VoP document",e);
        }
      }
      else
      {
        HBCIUtils.log("got VoP single result",HBCIUtils.LOG_INFO);
        
        // oder alternativ das Prüfergebnis einer Einzelprüfung
        final String iban   = data.getProperty(header + ".result.iban"); // Die IBAN des Empfängers
        final String name   = data.getProperty(header + ".result.differentname"); // der korrigierte Name des Empfängers
        final String result = data.getProperty(header + ".result.result"); // Status-Code
        final String reason = data.getProperty(header + ".result.reason"); // Falls Status "Not Applicable" ist: Ein Hinweis-Text
        // final String addon  = data.getProperty(header + ".result.ibanaddon"); // Hier kann ggf. eine Unterkontonummer stehen. Keine Ahnung, wofür ich das verwenden soll
        // final String other  = data.getProperty(header + ".result.otheridentifier"); // "Anderes Identifikationsmerkmal" - keine Ahnung, wofür die Bank das verwenden könnte
        
        final VoPResult r = new VoPResult();
        r.setIban(iban);
        r.setName(name);
        r.setStatus(VoPStatus.byCode(result));
        r.setText(reason);
        vop.getResults().add(r);
      }
    }
}
