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

import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.passport.HBCIPassportInternal;

/**
 * Diese Klasse enthaelt die Dialog-Initialisierung.
 */
public class HBCIDialogInit extends AbstractRawHBCIDialogInit
{
    /**
     * ct.
     */
    public HBCIDialogInit()
    {
        super(KnownDialogTemplate.INIT);
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
        final boolean a = ctx.isAnonymous();

        k.rawSet("Idn.customerid", a ? "9999999999" : p.getCustomerId());
        k.rawSet("Idn.sysid", a ? "0" : p.getSysId());
        k.rawSet("Idn.sysStatus",a ? "0" : p.getSysStatus());
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractHBCIDialogInit#getActualTemplate(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected String getActualTemplate(DialogContext ctx)
    {
        return this.getTemplate().getName() + (ctx.isAnonymous() ? "Anon" : "");
    }
}
