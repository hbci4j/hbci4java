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


import java.util.Properties;

import org.kapott.hbci.GV_Result.GVRSaldoReq;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.dialog.KnownReturncode;
import org.kapott.hbci.dialog.KnownTANProcess;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.Feature;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.tools.StringUtil;

/**
 * @author stefan.palme
 */
public class GVTAN2Step extends HBCIJobImpl
{
    // Wenn der Wert NULL ist, ist "this" also das zweite HKTAN 
    private GVTAN2Step step2;
    
    private KnownTANProcess process = null;
    
    // Wenn der Wert NULL ist, ist "this" also das erste HKTAN, da die Task-Referenz nur im zweiten HKTAN enthalten ist.
    private HBCIJobImpl task;
    
    private HBCIJobImpl redo;

    public static String getLowlevelName()
    {
        return "TAN2Step";
    }
    
    /**
     * Speichert den Prozess-Schritt des HKTAN.
     * @param p der Prozess-Schritt.
     */
    public void setProcess(KnownTANProcess p)
    {
        this.process = p;
        this.setParam("process",p.getCode());
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
        
        addConstraint("notlasttan","notlasttan","", LogFilter.FILTER_NONE);
        
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
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#setParam(java.lang.String, java.lang.String)
     */
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
     * @see org.kapott.hbci.GV.HBCIJobImpl#redo()
     */
    @Override
    public HBCIJobImpl redo()
    {
        // Falls der redo job 'this' ist, wird ein redo für das Decoupled Verfahren durchgeführt, welcher jeweils
        // nur ein mal pro 3956 status wiederholt werden soll. Um unendliche Wiederholungen zu vermeiden setzen wir
        // also redo=null.
        if (this.redo == this)
        {
            HBCIJobImpl redo = this.redo;
            this.redo = null;
            return redo;
        }
        return this.redo;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#haveTan()
     */
    @Override
    public boolean haveTan()
    {
        // Das HKTAN selbst kann nie ein HKTAN benoetigen.
        // Das ist hier nur zur Sicherheit. Eigentlich sollte HIPINS niemals fuer HKTAN=J liefern.
        return true;
    }
    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#extractResults(org.kapott.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    protected void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        final Properties result = msgstatus.getData();
        final String segCode = result.getProperty(header+".SegHead.code"); // HITAN oder das HI** des GV
        HBCIUtils.log("found HKTAN response with segcode " + segCode,HBCIUtils.LOG_DEBUG);

        ///////////////////////////////////////////////////////////////////////
        // Die folgenden Sonderbehandlungen sind nur bei Prozess-Variante 2 in Schritt 2 noetig,
        // weil wir dort ein Response auf einen GV erhalten, wir selbst aber gar nicht der GV sind sondern das HKTAN Step2
        if ((this.process == KnownTANProcess.PROCESS2_STEP2 || this.process == KnownTANProcess.PROCESS2_STEPS) && this.task != null)
        {
            if (StringUtil.toInsCode(this.getHBCICode()).equals(segCode)) {
                if (KnownReturncode.W3040.searchReturnValue(msgstatus.segStatus.getWarnings()) != null && this.task.redoAllowed()) {
                    // Pruefen, ob die Bank eventuell ein 3040 gesendet hat - sie also noch weitere Daten braucht.
                    // Das 3040 bezieht sich dann aber nicht auf unser HKTAN sondern auf den eigentlichen GV
                    // In dem Fall muessen wir dem eigentlichen Task mitteilen, dass er erneut ausgefuehrt werden soll.
                    HBCIUtils.log("found status code 3040, need to repeat task " + this.task.getHBCICode(),HBCIUtils.LOG_DEBUG);
                    HBCIUtils.log("Weitere Daten folgen",HBCIUtils.LOG_INFO);
                    this.redo = this.task;
                } else if (((AbstractPinTanPassport) getMainPassport()).shouldPerformDecoupledRefresh(msgstatus.segStatus)) {
                    HBCIUtils.log("Decoupled refresh required for task " + this.getHBCICode() + ". Redoing task",HBCIUtils.LOG_DEBUG);
                    this.redo = this;
                } else {
                    this.redo = null;
                }
            }

            // Das ist das Response auf den eigentlichen GV - an den Task durchreichen
            // Muessen wir extra pruefen, weil das hier auch das HITAN sein koennte. Das schauen wir aber nicht an
            if (StringUtil.toInsCode(this.task.getHBCICode()).equals(segCode))
            {
                HBCIUtils.log("this is a response segment for the original task (" + this.task.getName() + ") - storing results in the original job",HBCIUtils.LOG_DEBUG);
                this.task.fillJobResultFromTanJob(msgstatus, header, idx);
            }

            // Wir haben hier nichts weiter zu tun
            return;
        }
        //
        ///////////////////////////////////////////////////////////////////////

        this.redo = null;
        final HBCIPassportInternal p = this.getMainPassport();
        
        ///////////////////////////////////////////////////////////////////////
        // SCA-Ausnahme checken. Wenn wir in der Auswertung des ersten HKTAN sind, pruefen wir, ob die Bank einen 3076 geschickt
        // hat. Wenn das der Fall ist, koennen wir das zweite HKTAN weglassen und muessen auch beim User keine TAN erfragen
        if ((this.process == KnownTANProcess.PROCESS1 || this.process == KnownTANProcess.PROCESS2_STEP1) && (KnownReturncode.W3076.searchReturnValue(msgstatus.segStatus.getWarnings()) != null || KnownReturncode.W3076.searchReturnValue(msgstatus.globStatus.getWarnings()) != null))
        {
            HBCIUtils.log("found status code 3076, no SCA required",HBCIUtils.LOG_DEBUG);
            p.setPersistentData(AbstractPinTanPassport.KEY_PD_SCA,"true"); // Bewirkt, dass die TAN-Abfrage nicht erscheint
            if (this.step2 != null)
            {
                // Bewirkt, dass das zweite HKTAN bei Prozess-Variante 2 nicht mehr gesendet wird
                this.step2.skip();
            }
            return;
        }
        //
        ///////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////
        // Daten fuer die TAN-Abfrage einsammeln
        
        final String challenge = result.getProperty(header+".challenge");
        if (challenge != null)
        {
            HBCIUtils.log("found challenge '" + challenge + "' in HITAN - saving it temporarily in passport",HBCIUtils.LOG_DEBUG);
            p.setPersistentData(AbstractPinTanPassport.KEY_PD_CHALLENGE,challenge);
        }
        
        // External-ID des originalen Jobs durchreichen
        p.setPersistentData("externalid",this.getExternalId());

        // Challenge HHDuc aus dem Reponse holen und im Passport zwischenspeichern
        String hhdUc = result.getProperty(header + ".challenge_hhd_uc");
        if (hhdUc != null)
        {
          HBCIUtils.log("found Challenge HHDuc '" + hhdUc + "' in HITAN - saving it temporarily in passport",HBCIUtils.LOG_DEBUG);
          p.setPersistentData(AbstractPinTanPassport.KEY_PD_HHDUC,hhdUc);
        }
        
        // Die Auftragsreferenz aus dem ersten HITAN bei Prozessvariante 2. Die muessen wir bei dem HKTAN#2 mitschicken, damit die Bank
        // weiss, auf welchen Auftrag sich die TAN bezieht
        final String orderref = result.getProperty(header+".orderref");
        if (step2 != null && orderref != null)
        {
            HBCIUtils.log("found orderref '" + orderref + "' in HITAN",HBCIUtils.LOG_DEBUG);
            step2.setParam("orderref",orderref);
        }
        //
        ///////////////////////////////////////////////////////////////////////
    }
}
