package org.kapott.hbci.GV;

import java.util.Properties;
import org.kapott.hbci.GV_Result.GVRTANMediaList;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.status.HBCIMsgStatus;

public class GVTANMediaList extends HBCIJobImpl {
    
	public static String getLowlevelName()
    {
        return "TANMediaList";
    }

    public GVTANMediaList(HBCIHandler handler)
    {
        super(handler,getLowlevelName(),new GVRTANMediaList());
        addConstraint("mediatype","mediatype","1", LogFilter.FILTER_NONE);
        addConstraint("mediacategory", "mediacategory", "A", LogFilter.FILTER_NONE);
    }
    
    public void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        
        String s = result.getProperty(header+".tanoption");
        if(s != null) {
        	((GVRTANMediaList)jobResult).setTanOption(Integer.parseInt(s));
        }
        
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
        }
    }

}
