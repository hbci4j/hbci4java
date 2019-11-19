
/*  $Id: GVInstUebSEPA.java,v 1.0 2019/11/17 12:34:56 styppo Exp $

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

import org.kapott.hbci.GV_Result.GVRInstUebSEPA;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.status.HBCIMsgStatus;

import java.util.Properties;

/**
 * Job-Implementierung fuer SEPA-Instant Ueberweisungen.
 */
public class GVInstUebSEPA extends GVUebSEPA {
    /**
     * Liefert den Lowlevel-Namen des Jobs.
     * @return der Lowlevel-Namen des Jobs.
     */
    public static String getLowlevelName() {
        return "InstUebSEPA";
    }

    /**
     * @see AbstractSEPAGV#getPainJobName()
     */
    @Override
    public String getPainJobName() {
        return "UebSEPA";
    }

    /**
     * ct.
     * @param handler
     */
    public GVInstUebSEPA(HBCIHandler handler) {
        this(handler, getLowlevelName());
    }

    /**
     * ct.
     * @param handler
     * @param name
     */
    public GVInstUebSEPA(HBCIHandler handler, String name) {
        super(handler, name, new GVRInstUebSEPA());
    }

    @Override
    protected void extractResults(HBCIMsgStatus msgstatus, String header, int idx) {
        final Properties data = msgstatus.getData();
        final GVRInstUebSEPA result = (GVRInstUebSEPA) jobResult;
        result.setOrderId(data.getProperty(header + ".orderid"));
        result.setOrderStatus(data.getProperty(header + ".orderstatus"));
        result.setCancellationCode(data.getProperty(header + ".ccode"));
    }
}
