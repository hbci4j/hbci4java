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

import java.util.Objects;
import java.util.Properties;

import org.hbci4java.hbci.GV_Result.GVRInstUebSEPA;
import org.hbci4java.hbci.manager.HBCIHandler;
import org.hbci4java.hbci.manager.LogFilter;
import org.hbci4java.hbci.status.HBCIMsgStatus;

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
        
        // Siehe https://homebanking-hilfe.de/forum/topic.php?p=155881#real155881
        if (Objects.equals(name,getLowlevelName())) // Nur bei Einzelauftraegen ausfuehren - GVUebSEPA wird in GVMultiUebSEPA ueberschrieben - und dort wird das Flag ja user-spezifisch gefuellt
          addConstraint("batchbook", "sepa.batchbook", "0", LogFilter.FILTER_NONE);
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
