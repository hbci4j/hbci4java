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
 * Diese Klasse enthaelt die Synchronisierung.
 */
public class HBCIDialogSync extends AbstractRawHBCIDialogInit
{
    /**
     * Legt fest, ob die System-ID oder die Signatur-ID synchronisiert werden soll.
     */
    public enum Mode
    {
        /**
         * System-ID.
         */
        SYS_ID,
        
        /**
         * Signatur-ID.
         */
        SIG_ID
    }
    
    private Mode mode = null;
    
    /**
     * ct.
     * @param mode der Modus.
     */
    public HBCIDialogSync(Mode mode)
    {
        super(KnownDialogTemplate.SYNC);
        this.mode = mode;
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

        k.rawSet("Idn.customerid", p.getCustomerId());
        
        if (this.mode == Mode.SYS_ID)
        {
            k.rawSet("Idn.sysid", "0");
            k.rawSet("Idn.sysStatus", "1");
            k.rawSet("Sync.mode", "0");
        }
        else
        {
            k.rawSet("Idn.sysid", p.getSysId());
            k.rawSet("Idn.sysStatus", p.getSysStatus());
            k.rawSet("Sync.mode", "2");
        }
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractHBCIDialogInit#sendData(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected HBCIMsgStatus sendData(DialogContext ctx)
    {
        if (this.mode == Mode.SIG_ID)
        {
            final HBCIPassportInternal p = ctx.getPassport();
            return ctx.getKernel().rawDoIt(p.hasMySigKey(),
                                           HBCIKernelImpl.CRYPTIT,
                                           p.hasMyEncKey());
        }
        
        return super.sendData(ctx);
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#checkResult(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected void checkResult(DialogContext ctx)
    {
        super.checkResult(ctx);

        final HBCIMsgStatus ret = ctx.getMsgStatus();
        if (ret.isOK())
            return;
        
        throw new ProcessException(HBCIUtilsInternal.getLocMsg((this.mode == Mode.SIG_ID) ? "EXCMSG_SYNCSIGIDFAIL" : "EXCMSG_SYNCSYSIDFAIL"),ret);
    }
}
