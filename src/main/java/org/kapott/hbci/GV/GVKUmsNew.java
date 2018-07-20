
/*  $Id: GVKUmsNew.java,v 1.1 2011/05/04 22:37:52 willuhn Exp $

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

import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.LogFilter;

/**
 * Implementierung des Geschaeftsvorfalls zum Abruf von neuen Umsaetzen (HKKAN).
 */
public final class GVKUmsNew extends GVKUmsAll
{
  /**
   * @return der Lowlevelname.
   */
    public static String getLowlevelName()
    {
        return "KUmsNew";
    }
    
    /**
     * ct.
     * @param handler
     */
    public GVKUmsNew(HBCIHandler handler)
    {
        super(handler,getLowlevelName());

        boolean sepa = false;
        try
        {
          // Siehe auch GVKontoauszug/HKEKA. Die einzige Aenderung war die Umstellung
          // der Bankverbindungsart von ktv auf kti (wegen IBAN-Support).
          // Bei HKKAN ist das ab Segment-Version 7 der Fall.
          sepa = Integer.parseInt(this.getSegVersion()) >= 7; 
        }
        catch (Exception e)
        {
          HBCIUtils.log(e);
        }
        
        // Dennoch kann es sein, dass die nationale Bankverbindung auch bei der
        // SEPA-Variante noch mitgeschickt wird, wenn die Bank das zulaesst.
        // (Es scheint auch Banken zu geben, die das in dem Fall nicht nur
        // zulassen sondern erwarten).
        boolean nat = this.canNationalAcc(handler);

        if (sepa)
        {
          addConstraint("my.bic",  "KTV.bic",  null, LogFilter.FILTER_MOST);
          addConstraint("my.iban", "KTV.iban", null, LogFilter.FILTER_IDS);
        }

        if (nat || !sepa)
        {
          addConstraint("my.country","KTV.KIK.country","DE", LogFilter.FILTER_NONE);
          addConstraint("my.blz","KTV.KIK.blz",null, LogFilter.FILTER_MOST);
          addConstraint("my.number","KTV.number",null, LogFilter.FILTER_IDS);
          addConstraint("my.subnumber","KTV.subnumber","", LogFilter.FILTER_MOST);
        }

        addConstraint("my.curr","curr","EUR", LogFilter.FILTER_NONE);
        addConstraint("maxentries","maxentries","", LogFilter.FILTER_NONE);
        addConstraint("dummyall","allaccounts","N", LogFilter.FILTER_NONE);
    }

    
    /**
     * @see org.kapott.hbci.GV.HBCIJobImpl#verifyConstraints()
     */
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
