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

package org.hbci4java.hbci.GV;

import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.LogFilter;

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
     * @see org.hbci4java.hbci.GV.HBCIJobImpl#verifyConstraints()
     */
    public void verifyConstraints()
    {
        super.verifyConstraints();
        checkAccountCRC("my");
    }
}
