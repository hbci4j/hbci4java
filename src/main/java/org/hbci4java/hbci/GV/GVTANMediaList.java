package org.hbci4java.hbci.GV;

import java.util.Properties;

import org.hbci4java.hbci.GV_Result.GVRTANMediaList;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.HBCIUser;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.HBCIUtilsInternal;
import org.hbci4java.hbci.manager.LogFilter;
import org.hbci4java.hbci.passport.HBCIPassportInternal;
import org.hbci4java.hbci.status.HBCIMsgStatus;

public class GVTANMediaList extends HBCIJobImpl {
    
	public static String getLowlevelName()
    {
        return "TANMediaList";
    }

    public GVTANMediaList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRTANMediaList());
        addConstraint("mediatype","mediatype","0", LogFilter.FILTER_NONE);
        addConstraint("mediacategory", "mediacategory", "A", LogFilter.FILTER_NONE);
    }
    
    /**
     * @see org.hbci4java.hbci.GV.HBCIJobImpl#redoAllowed()
     */
    @Override
    protected boolean redoAllowed()
    {
        return true;
    }

    /**
     * @see org.hbci4java.hbci.GV.HBCIJobImpl#extractResults(org.hbci4java.hbci.status.HBCIMsgStatus, java.lang.String, int)
     */
    public void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        
        String s = result.getProperty(header+".tanoption");
        if(s != null) {
        	((GVRTANMediaList)jobResult).setTanOption(Integer.parseInt(s));
        }

        // Da drin speichern wir die Namen der TAN-Medien - kommt direkt in die UPD im Passport
        StringBuffer mediaNames = new StringBuffer();
        
        for (int i=0;;i++) {
            String mediaheader=HBCIUtilsInternal.withCounter(header+".MediaInfo",i);
            
            String st=result.getProperty(mediaheader+".mediacategory");
            if (st==null)
                break;
            
            GVRTANMediaList.TANMediaInfo info=new GVRTANMediaList.TANMediaInfo();
            
            info.mediaCategory = st;
            info.cardNumber = result.getProperty(mediaheader+".cardnumber");
            info.cardSeqNumber = result.getProperty(mediaheader+".cardseqnumber");
            info.mediaName = result.getProperty(mediaheader+".medianame");
            info.mobileNumber = result.getProperty(mediaheader+".mobilenumber");
            info.mobileNumberSecure = result.getProperty(mediaheader+".mobilenumber_secure");
            info.status = result.getProperty(mediaheader+".status");
            info.tanListNumber = result.getProperty(mediaheader+".tanlistnumber");
            
            st = result.getProperty(mediaheader+".freetans");
            if(st != null) info.freeTans = Integer.parseInt(st);
            
            st =  result.getProperty(mediaheader+".cardtype");
            if(st != null) info.cardType = Integer.parseInt(st);
            
            st = result.getProperty(mediaheader+".validfrom");
            if(st != null) {
            	info.validFrom = HBCIUtils.string2DateISO(st);
            }
            
            st = result.getProperty(mediaheader+".validto");
            if(st != null) {
            	info.validTo = HBCIUtils.string2DateISO(st);
            }
            
            st = result.getProperty(mediaheader+".lastuse");
            if(st != null) {
            	info.lastUse = HBCIUtils.string2DateISO(st);
            }

            st = result.getProperty(mediaheader+".activatedon");
            if(st != null) {
            	info.activatedOn = HBCIUtils.string2DateISO(st);
            }

            ((GVRTANMediaList)jobResult).add(info);
            
            // Es gibt auch noch "verfuegbar", da muss das Medium aber erst noch freigeschaltet werden
            boolean isActive    = info.status != null && info.status.equals("1");
            boolean haveName    = info.mediaName != null && info.mediaName.length() > 0;
            // boolean isMobileTan = info.mediaCategory != null && info.mediaCategory.equalsIgnoreCase("M");

            // Zu den UPD hinzufuegen
            if (isActive && haveName)
            {
                if (mediaNames.length() != 0)
                    mediaNames.append("|");
                
                mediaNames.append(info.mediaName);
            }
        }
        
        String names = mediaNames.toString();
        if (names.length() > 0)
        {
            HBCIUtils.log("TAN-Medienbezeichnungen empfangen: " + names, HBCIUtils.LOG_INFO);
            HBCIUtils.log("adding TAN media names to UPD: " + names, HBCIUtils.LOG_DEBUG);
            HBCIPassportInternal p = (HBCIPassportInternal) getParentHandler().getPassport();
            Properties upd = p.getUPD();
            if (upd == null)
            {
                // Fuer den Fall, dass wir das zu einem Zeitpunkt aufgerufen haben, wo wir noch gar keine UPD haben
                upd = new Properties();
                p.setUPD(upd);
            }
            upd.setProperty(HBCIUser.UPD_KEY_TANMEDIA,names);
        }
    }

}
