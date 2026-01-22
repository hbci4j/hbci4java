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

import org.hbci4java.hbci.exceptions.ProcessException;
import org.hbci4java.hbci.manager.HBCIKernelImpl;
import org.hbci4java.hbci.manager.HBCIUtilsInternal;
import org.hbci4java.hbci.passport.HBCIPassportInternal;
import org.hbci4java.hbci.status.HBCIMsgStatus;

/**
 * Diese Klasse enthaelt die Message zum Sperren von Schluesseln.
 */
public class HBCIDialogLockKeys extends AbstractRawHBCIDialog
{
    /**
     * ct.
     */
    public HBCIDialogLockKeys()
    {
        super(KnownDialogTemplate.LOCKKEYS);
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
        
        k.rawSet("KeyLock.KeyName.KIK.country",p.getCountry());
        k.rawSet("KeyLock.KeyName.KIK.blz",p.getBLZ());
        k.rawSet("KeyLock.KeyName.userid",p.getMySigKeyName());
        k.rawSet("KeyLock.KeyName.keynum",p.getMySigKeyNum());
        k.rawSet("KeyLock.KeyName.keyversion",p.getMySigKeyVersion());
        k.rawSet("KeyLock.SecProfile.method", p.getProfileMethod());
        k.rawSet("KeyLock.SecProfile.version", p.getProfileVersion());
        k.rawSet("KeyLock.locktype","999");
    }
    
    /**
     * @see org.hbci4java.hbci.dialog.AbstractRawHBCIDialog#sendData(org.hbci4java.hbci.dialog.DialogContext)
     */
    @Override
    protected HBCIMsgStatus sendData(DialogContext ctx)
    {
        return ctx.getKernel().rawDoIt(HBCIKernelImpl.SIGNIT,
                                       HBCIKernelImpl.CRYPTIT,
                                       HBCIKernelImpl.DONT_NEED_CRYPT);
    }
    
    /**
     * @see org.hbci4java.hbci.dialog.AbstractRawHBCIDialog#checkResult(org.hbci4java.hbci.dialog.DialogContext)
     */
    @Override
    protected void checkResult(DialogContext ctx)
    {
        super.checkResult(ctx);

        final HBCIMsgStatus ret = ctx.getMsgStatus();
        if (!ret.isOK())
            throw new ProcessException(HBCIUtilsInternal.getLocMsg("EXCMSG_LOCKFAILED"),ret);
    }
}
