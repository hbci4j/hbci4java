
/*  $Id: GVTAN2Step.java,v 1.4 2011/05/17 16:39:06 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.GV;


import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRSaldoReq;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * @author stefan.palme
 */
public class GVTAN2Step 
    extends HBCIJobImpl
{
    private GVTAN2Step  otherTAN2StepTask;
    private HBCIJobImpl origTask;
    
    public static String getLowlevelName()
    {
        return "TAN2Step";
    }
    
    public GVTAN2Step(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRSaldoReq());

        addConstraint("process","process",null, LogFilter.FILTER_NONE);
        addConstraint("orderhash","orderhash","", LogFilter.FILTER_NONE);
        addConstraint("orderref","orderref","", LogFilter.FILTER_NONE);
        addConstraint("listidx","listidx","", LogFilter.FILTER_NONE);
        addConstraint("notlasttan","notlasttan","N", LogFilter.FILTER_NONE);
        addConstraint("info","info","", LogFilter.FILTER_NONE);
        
        addConstraint("storno","storno","", LogFilter.FILTER_NONE);
        addConstraint("challengeklass","challengeklass","", LogFilter.FILTER_NONE);
        addConstraint("ChallengeKlassParam1", "ChallengeKlassParams.param","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam2", "ChallengeKlassParams.param_2","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam3", "ChallengeKlassParams.param_3","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam4", "ChallengeKlassParams.param_4","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam5", "ChallengeKlassParams.param_5","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam6", "ChallengeKlassParams.param_6","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam7", "ChallengeKlassParams.param_7","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam8", "ChallengeKlassParams.param_8","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam9", "ChallengeKlassParams.param_9","", LogFilter.FILTER_IDS);

        addConstraint("tanmedia", "tanmedia","", LogFilter.FILTER_IDS);

        addConstraint("ordersegcode", "ordersegcode","", LogFilter.FILTER_NONE);

        addConstraint("orderaccount.number","OrderAccount.number",null, LogFilter.FILTER_IDS);
        addConstraint("orderaccount.subnumber","OrderAccount.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("orderaccount.blz","OrderAccount.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("orderaccount.country","OrderAccount.KIK.country","DE", LogFilter.FILTER_NONE);

        // willuhn 2011-05-17 wird noch nicht genutzt
        // addConstraint("smsaccount.number","SMSAccount.number",null, LogFilter.FILTER_IDS);
        // addConstraint("smsaccount.subnumber","SMSAccount.subnumber","", LogFilter.FILTER_MOST);
        // addConstraint("smsaccount.blz","SMSAccount.KIK.blz",null, LogFilter.FILTER_MOST);
        // addConstraint("smsaccount.country","SMSAccount.KIK.country","DE", LogFilter.FILTER_NONE);
    }
    
    public void setParam(String paramName, String value)
    {
        if (paramName.equals("orderhash")) {
            value="B"+value;
        }
        super.setParam(paramName,value);
    }

    public void storeOtherTAN2StepTask(GVTAN2Step other)
    {
        this.otherTAN2StepTask=other;
    }
    
    public void storeOriginalTask(HBCIJobImpl task)
    {
        this.origTask=task;
    }
    
    protected void saveReturnValues(HBCIMsgStatus status, int sref) {
        super.saveReturnValues(status, sref);
        
        if (origTask!=null) {
            int orig_segnum=Integer.parseInt(origTask.getJobResult().getSegNum());
            HBCIUtils.log("storing return values in orig task (segnum="+orig_segnum+")", HBCIUtils.LOG_DEBUG);
            origTask.saveReturnValues(status,orig_segnum);
        }
    }

    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        String segcode=result.getProperty(header+".SegHead.code");
        HBCIUtils.log("found HKTAN response with segcode "+segcode,HBCIUtils.LOG_DEBUG);
        
        if (origTask!=null && new StringBuffer(origTask.getHBCICode()).replace(1,2,"I").toString().equals(segcode)) {
            // das ist für PV#2, wenn nach dem nachträglichen versenden der TAN das
            // antwortsegment des jobs aus der vorherigen Nachricht zurückommt
            HBCIUtils.log("this is a response segment for the original task - storing results in the original job",HBCIUtils.LOG_DEBUG);
            origTask.extractResults(msgstatus,header,idx);
        } else {
            HBCIUtils.log("this is a \"real\" HKTAN response - analyzing HITAN data",HBCIUtils.LOG_DEBUG);
            
            String challenge=result.getProperty(header+".challenge");
            if (challenge!=null) {
                HBCIUtils.log("found challenge '"+challenge+"' in HITAN - saving it temporarily in passport",HBCIUtils.LOG_DEBUG);
                // das ist für PV#1 (die antwort auf das einreichen des auftrags-hashs) oder 
                // für PV#2 (die antwort auf das einreichen des auftrages)
                // in jedem fall muss mit der nächsten nachricht die TAN übertragen werden
                getMainPassport().setPersistentData("pintan_challenge",challenge);
                
                // TODO: es muss hier evtl. noch überprüft werden, ob
                // der zurückgegebene auftragshashwert mit dem ursprünglich versandten
                // übereinstimmt
                // für pv#1 gilt: hitan_orderhash == sent_orderhash (from previous hktan)
                // für pv#2 gilt: hitan_orderhash == orderhash(gv from previous GV segment)
                
                // TODO: hier noch die optionale DEG ChallengeValidity bereitstellen
            }

            // willuhn 2011-05-09 extrahieren der Infos fuer die Generierung des Flickercodes
            String hhdUc = result.getProperty(header + ".challenge_hhd_uc");
            if (hhdUc != null)
            {
              // TODO willuhn 2011-05-13 das HHD_UC ist nocht NICHT das Format, wie es
              // von einem Flickercode-Widget angezeigt werden kann. Es muss noch umgewandelt
              // werden. Das ist in
              // Belegungsrichtlinien TANve1.4  mit Erratum 1-3 final version vom 2010-11-12.pdf
              // Seite 45 beschrieben. Ich warte hier aber mal noch auf einen Beispiel-HHD_UC
              // von einem User, mit dem ich testen kann, bevor ich das einbaue
              HBCIUtils.log("found HHD UC '" + hhdUc + "' in HITAN - saving it temporarily in passport",HBCIUtils.LOG_DEBUG);
              getMainPassport().setPersistentData("pintan_challenge_hhd_uc",hhdUc);
            }
            
            String orderref=result.getProperty(header+".orderref");
            if (orderref!=null) {
                // orderref ist nur für PV#2 relevant
                HBCIUtils.log("found orderref '"+orderref+"' in HITAN",HBCIUtils.LOG_DEBUG);
                if (otherTAN2StepTask!=null) {
                    // hier sind wir ganz sicher in PV#2. das hier ist die antwort auf das
                    // erste HKTAN (welches mit dem eigentlichen auftrag verschickt wird)
                    // die orderref muss im zweiten HKTAN-job gespeichert werden, weil in
                    // dieser zweiten nachricht dann die TAN mit übertragen werden muss
                    HBCIUtils.log("storing it in following HKTAN task",HBCIUtils.LOG_DEBUG);
                    otherTAN2StepTask.setParam("orderref",orderref);
                } else {
                    HBCIUtils.log("no other HKTAN task known - ignoring orderref",HBCIUtils.LOG_DEBUG);
                }
            }
        }
    }
}
