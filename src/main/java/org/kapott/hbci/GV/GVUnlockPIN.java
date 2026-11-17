/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2026 HBCI4Java contributors
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

import org.kapott.hbci.GV_Result.HBCIJobResultImpl;
import org.kapott.hbci.manager.HBCIHandler;

/**
 * Implementierung des Geschaeftsvorfalls zum Aufheben einer PIN-Sperre (HKPSA).
 * Das Segment enthaelt nur den Segmentkopf. PIN und TAN stehen im Signaturabschluss.
 * Der Job muss allein in einem eigenen execute() gesendet werden, da die Bank einen Dialog
 * mit gesperrter PIN nur fuer diesen Geschaeftsvorfall zulaesst (Kapitel C.2.3 und B.4.3.1).
 */
public class GVUnlockPIN extends HBCIJobImpl<HBCIJobResultImpl>
{
    /**
     * Liefert den Lowlevel-Namen.
     * @return der Lowlevel-Name.
     */
    public static String getLowlevelName()
    {
        return "UnlockPIN";
    }

    /**
     * ct.
     * @param handler der Handler.
     */
    public GVUnlockPIN(HBCIHandler handler)
    {
        super(handler, getLowlevelName(), new HBCIJobResultImpl());
    }
}
