
/*  $Id: GVTAN2Step.java,v 1.6 2011/05/27 10:28:38 willuhn Exp $

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
import org.kapott.hbci.dialog.KnownReturncode;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * @author stefan.palme
 */
public class GVTAN2Step extends HBCIJobImpl
{
    // Wenn der Wert NULL ist, ist "this" also das zweite HKTAN 
    private GVTAN2Step step2;
    
    // Wenn der Wert NULL ist, ist "this" also das erste HKTAN, da die Task-Referenz nur im zweiten HKTAN enthalten ist.
    private HBCIJobImpl task;
    private boolean hdaveSCA = false;
    
    public static String getLowlevelName()
    {
        return "TAN2Step";
    }
    
    /**
     * ct.
     * @param handler
     */
    public GVTAN2Step(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRSaldoReq());

        int version = 5;
        try
        {
          version = Integer.parseInt(this.getSegVersion());
        }
        catch (Exception e)
        {
          HBCIUtils.log(e);
        }

        addConstraint("process","process",null, LogFilter.FILTER_NONE);
        addConstraint("ordersegcode", "ordersegcode","", LogFilter.FILTER_NONE);
        addConstraint("orderaccount.bic","OrderAccount.bic",null, LogFilter.FILTER_MOST);
        addConstraint("orderaccount.iban","OrderAccount.iban",null, LogFilter.FILTER_IDS);
        addConstraint("orderaccount.number","OrderAccount.number",null, LogFilter.FILTER_IDS);
        addConstraint("orderaccount.subnumber","OrderAccount.subnumber","", LogFilter.FILTER_MOST);
        addConstraint("orderaccount.blz","OrderAccount.KIK.blz",null, LogFilter.FILTER_MOST);
        addConstraint("orderaccount.country","OrderAccount.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("orderhash","orderhash","", LogFilter.FILTER_NONE);
        addConstraint("orderref","orderref","", LogFilter.FILTER_NONE);
        
        if (version < 6)
            addConstraint("listidx","listidx","", LogFilter.FILTER_NONE);
        
        addConstraint("notlasttan","notlasttan","N", LogFilter.FILTER_NONE);
        
        if (version <= 1) // Gabs nur in HKTAN 1
            addConstraint("info","info","", LogFilter.FILTER_NONE);

        addConstraint("storno","storno","", LogFilter.FILTER_NONE);
        // willuhn 2011-05-17 wird noch nicht genutzt
        // addConstraint("smsaccount.number","SMSAccount.number",null, LogFilter.FILTER_IDS);
        // addConstraint("smsaccount.subnumber","SMSAccount.subnumber","", LogFilter.FILTER_MOST);
        // addConstraint("smsaccount.blz","SMSAccount.KIK.blz",null, LogFilter.FILTER_MOST);
        // addConstraint("smsaccount.country","SMSAccount.KIK.country","DE", LogFilter.FILTER_NONE);
        addConstraint("challengeklass","challengeklass","", LogFilter.FILTER_NONE);
        addConstraint("ChallengeKlassParam1", "ChallengeKlassParams.param1","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam2", "ChallengeKlassParams.param2","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam3", "ChallengeKlassParams.param3","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam4", "ChallengeKlassParams.param4","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam5", "ChallengeKlassParams.param5","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam6", "ChallengeKlassParams.param6","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam7", "ChallengeKlassParams.param7","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam8", "ChallengeKlassParams.param8","", LogFilter.FILTER_IDS);
        addConstraint("ChallengeKlassParam9", "ChallengeKlassParams.param9","", LogFilter.FILTER_IDS);

        addConstraint("tanmedia", "tanmedia","", LogFilter.FILTER_IDS);
        
        addConstraint("HHDUCAnswer", "HHDUCAnswer.atc","", LogFilter.FILTER_IDS);
        addConstraint("HHDUCAnswer", "HHDUCAnswer.appcrypto_ac","", LogFilter.FILTER_IDS);
        addConstraint("HHDUCAnswer", "HHDUCAnswer.ef_id_data","", LogFilter.FILTER_IDS);
        addConstraint("HHDUCAnswer", "HHDUCAnswer.cvr","", LogFilter.FILTER_IDS);
        addConstraint("HHDUCAnswer", "HHDUCAnswer.versioninfo","", LogFilter.FILTER_IDS);

    }
    
    public void setParam(String paramName, String value)
    {
        if (paramName.equals("orderhash")) {
            value="B"+value;
        }
        super.setParam(paramName,value);
    }

    /**
     * Speichert die Referenz auf das zweite HKTAN im ersten HKTAN.
     * Wird fuer Prozess-Variante 2 benoetigt.
     * @param step2 die Referenz auf den ersten HKTAN.
     */
    public void setStep2(GVTAN2Step step2)
    {
        this.step2 = step2;
    }
    
    /**
     * Speichert eine Referenz auf den eigentlichen Geschaeftsvorfall.
     * @param task
     */
    public void setTask(HBCIJobImpl task)
    {
        this.task = task;
    }
    
    protected void saveReturnValues(HBCIMsgStatus status, int sref)
    {
        super.saveReturnValues(status, sref);
        
        if (this.task != null)
        {
            int orig_segnum=Integer.parseInt(task.getJobResult().getSegNum());
            HBCIUtils.log("storing return values in orig task (segnum="+orig_segnum+")", HBCIUtils.LOG_DEBUG);
            task.saveReturnValues(status,orig_segnum);
        }
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#needsContinue(int)
     */
    @Override
    public boolean needsContinue(int loop)
    {
        return super.needsContinue(loop);
    }

    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result = msgstatus.getData();
        String segcode = result.getProperty(header+".SegHead.code");
        
        final boolean isStep2 = this.task != null && this.step2 == null;
                        
        ///////////////////////////////////////////////////////////////////////
        // Das ist das Ergebnis des zweiten HKTAN bei Prozess-Variante 2. Das ignorieren wir
        if (isStep2 && "HITAN".equals(segcode))
            return;
        //
        ///////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////
        // Die Rueckmeldung des eigentlichen Geschaeftsvorfalls an den Task durchreichen
        // Kommt nur bei Prozess-Variante 2 zum Einsatz, weil dort im zweiten Schritt ja nur noch das HKTAN
        // gesendet wird und die Instituts-Antwort des GV quasi ohne direkten Request davor kommt
        if (task != null && new StringBuffer(task.getHBCICode()).replace(1,2,"I").toString().equals(segcode))
        {
            // das ist für PV#2, wenn nach dem nachträglichen versenden der TAN das
            // antwortsegment des jobs aus der vorherigen Nachricht zurückommt
            HBCIUtils.log("this is a response segment for the original task - storing results in the original job",HBCIUtils.LOG_DEBUG);
            this.task.extractResults(msgstatus,header,idx);
            
            // Hier koennen wir auch aufhoeren. Wenn wir das Ergebnis haben, ist der TAN-Prozess zu Ende
            return;
        }
        //
        ///////////////////////////////////////////////////////////////////////

        final HBCIPassportInternal p = this.getMainPassport();
        
        
        ///////////////////////////////////////////////////////////////////////
        // SCA-Ausnahme checken. Wenn wir in der Auswertung des ersten HKTAN sind, pruefen wir, ob die Bank einen 3076 geschickt
        // hat. Wenn das der Fall ist, koennen wir das zweite HKTAN weglassen und muessen auch beim User keine TAN erfragen
        // "task == null" ist in zwei Situationen der Fall: PV#1 oder HKTAN1 von PV2. Genau die Stelle, wo wir die SCA-Ausnahme
        // pruefen wollen.
        boolean haveSCA = false;
        if (this.task == null && KnownReturncode.W3076.searchReturnValue(msgstatus.segStatus.getWarnings()) != null)
        {
            HBCIUtils.log("found status code 3076, no SCA required",HBCIUtils.LOG_INFO);
            p.setPersistentData(AbstractPinTanPassport.KEY_PD_SCA,"true");
            haveSCA = true;
        }
        //
        ///////////////////////////////////////////////////////////////////////

        HBCIUtils.log("found HKTAN response with segcode " + segcode,HBCIUtils.LOG_DEBUG);
        
        // Wir haben eine SCA-Ausnahme erhalten. Wir koennen uns die Auswertung des HITAN schenken
        if (haveSCA)
            return;
        
        // Daten fuer die TAN-Abfrage einsammeln
        
        /////////////////////////////////////////////////////////////////
        // Prozess-Variante 1:
        final String challenge = result.getProperty(header+".challenge");
        if (challenge != null)
        {
            HBCIUtils.log("found challenge '" + challenge + "' in HITAN - saving it temporarily in passport",HBCIUtils.LOG_DEBUG);
            // das ist für PV#1 (die antwort auf das einreichen des auftrags-hashs) oder 
            // für PV#2 (die antwort auf das einreichen des auftrages)
            // in jedem fall muss mit der nächsten nachricht die TAN übertragen werden
            p.setPersistentData(AbstractPinTanPassport.KEY_PD_CHALLENGE,challenge);
        }
        //
        /////////////////////////////////////////////////////////////////

        // External-ID des originalen Jobs durchreichen
        p.setPersistentData("externalid",this.getExternalId());

        // Challenge HHDuc aus dem Reponse holen und im Passport zwischenspeichern
        String hhdUc = result.getProperty(header + ".challenge_hhd_uc");
        if (hhdUc != null)
        {
          HBCIUtils.log("found Challenge HHDuc '" + hhdUc + "' in HITAN - saving it temporarily in passport",HBCIUtils.LOG_DEBUG);
          getMainPassport().setPersistentData(AbstractPinTanPassport.KEY_PD_HHDUC,hhdUc);
        }
        
        // Die Auftragsreferenz aus dem ersten HITAN bei Prozessvariante 2. Die muessen wir bei dem HKTAN#2 mitschicken, damit die Bank
        // weiss, auf welchen Auftrag sich die TAN bezieht
        final String orderref = result.getProperty(header+".orderref");
        if (step2 != null && orderref != null)
        {
            HBCIUtils.log("found orderref '" + orderref + "' in HITAN",HBCIUtils.LOG_DEBUG);
            step2.setParam("orderref",orderref);
        }
    }
}
