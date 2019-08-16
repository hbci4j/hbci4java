/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.dialog;

import org.kapott.hbci.exceptions.ProcessException;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;

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
     * @see org.kapott.hbci.dialog.AbstractHBCIDialogInit#applyData(org.kapott.hbci.dialog.DialogContext)
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
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#sendData(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected HBCIMsgStatus sendData(DialogContext ctx)
    {
        return ctx.getKernel().rawDoIt(HBCIKernelImpl.SIGNIT,
                                       HBCIKernelImpl.CRYPTIT,
                                       HBCIKernelImpl.DONT_NEED_CRYPT);
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#checkResult(org.kapott.hbci.dialog.DialogContext)
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
