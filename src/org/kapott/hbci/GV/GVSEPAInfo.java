
/*  $Id: GVSEPAInfo.java,v 1.1 2011/05/04 22:37:53 willuhn Exp $

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

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.status.HBCIMsgStatus;

public class GVSEPAInfo 
	extends HBCIJobImpl 
{
    public static String getLowlevelName()
    {
        return "SEPAInfo";
    }
    
    public GVSEPAInfo(HBCIHandler handler)
    {
        super(handler,getLowlevelName(), new HBCIJobResultImpl());
    }

    public void extractResults(HBCIMsgStatus msgstatus,String header,int idx)
    {
        Properties result=msgstatus.getData();
        Properties upd=getParentHandler().getPassport().getUPD();
        
        for (int i=0;;i++) {
        	String subheader=HBCIUtilsInternal.withCounter(header+".Acc", i);
        	String cansepa=result.getProperty(subheader+".sepa");
        	if (cansepa==null) {
        		// kein weiteres konto im antwortsegment
        		break;
        	}
        	if (cansepa.equals("N")) {
        		// dieses konto kann kein sepa
        		continue;
        	}
        	
        	// sepa-konto-informationen gefunden
        	String iban=result.getProperty(subheader+".iban");
        	String bic=result.getProperty(subheader+".bic");
        	
        	// normale konto-informationen extrahieren, um dieses konto
        	// in den upd suchen zu koennen
        	String country=result.getProperty(subheader+".KIK.country");
        	String blz=result.getProperty(subheader+".KIK.blz");
        	String number=result.getProperty(subheader+".number");
        	
        	HBCIUtils.log("found BIC/IBAN = "+bic+"/"+iban+" for account "+country+"/"+blz+"/"+number,
        			HBCIUtils.LOG_DEBUG);
        	
        	// konto in den UPD suchen und UPD-Informationen aktualisieren
        	for (int j=0;;j++) {
        		String temp_header=HBCIUtilsInternal.withCounter("KInfo",j);
        		String temp_number=upd.getProperty(temp_header+".KTV.number");
        		if (temp_number==null) {
        			// kein weiteres konto in den UPD
        			break;
        		}
        		String temp_country=upd.getProperty(temp_header+".KTV.KIK.country");
        		String temp_blz=upd.getProperty(temp_header+".KTV.KIK.blz");
        		
        		if (temp_country.equals(country) && 
        				temp_blz.equals(blz) &&
        				temp_number.equals(number))
        		{
        			upd.setProperty(temp_header+".KTV.iban", iban);
        			upd.setProperty(temp_header+".KTV.bic", bic);
        		}
        	}
        }
    }
}
