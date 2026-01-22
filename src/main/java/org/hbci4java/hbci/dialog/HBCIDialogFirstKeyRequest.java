/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2019 Olaf Willuhn
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

package org.hbci4java.hbci.dialog;

import org.hbci4java.hbci.manager.HBCIKernelImpl;
import org.hbci4java.hbci.passport.HBCIPassportInternal;
import org.hbci4java.hbci.status.HBCIMsgStatus;

/**
 * Diese Klasse enthaelt den Request fuer die Abfrage der Bankensignatur.
 */
public class HBCIDialogFirstKeyRequest extends AbstractRawHBCIDialogInit
{
    /**
     * ct.
     */
    public HBCIDialogFirstKeyRequest()
    {
        super(KnownDialogTemplate.FIRSTKEYREQUEST);
    }
    
    /**
     * @see org.hbci4java.hbci.dialog.AbstractRawHBCIDialog#applyData(org.hbci4java.hbci.dialog.DialogContext)
     */
    @Override
    protected void applyData(DialogContext ctx)
    {
        super.applyData(ctx);
        
        final HBCIPassportInternal p = ctx.getPassport();
        final HBCIKernelImpl k = ctx.getKernel();

        final String country = p.getCountry();
        final String blz     = p.getBLZ();
        
        k.rawSet("KeyReq.SecProfile.method",p.getProfileMethod());
        k.rawSet("KeyReq.SecProfile.version",p.getProfileVersion());
        k.rawSet("KeyReq.KeyName.keytype", "V");
        k.rawSet("KeyReq.KeyName.KIK.blz", blz);
        k.rawSet("KeyReq.KeyName.KIK.country", country);
        k.rawSet("KeyReq_2.SecProfile.method",p.getProfileMethod());
        k.rawSet("KeyReq_2.SecProfile.version",p.getProfileVersion());
        k.rawSet("KeyReq_2.KeyName.keytype", "S");
        k.rawSet("KeyReq_2.KeyName.KIK.blz", blz);
        k.rawSet("KeyReq_2.KeyName.KIK.country", country);
    }
    
    /**
     * @see org.hbci4java.hbci.dialog.AbstractRawHBCIDialog#sendData(org.hbci4java.hbci.dialog.DialogContext)
     */
    @Override
    protected HBCIMsgStatus sendData(DialogContext ctx)
    {
        // Generell ohne Signatur und ohne Verschluesselung
        return ctx.getKernel().rawDoIt(HBCIKernelImpl.DONT_SIGNIT,
                                       HBCIKernelImpl.DONT_CRYPTIT,
                                       HBCIKernelImpl.DONT_NEED_CRYPT);
    }
}
