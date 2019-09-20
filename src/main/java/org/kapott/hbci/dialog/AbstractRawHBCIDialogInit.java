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
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportInternal;

/**
 * Abstrakte Basis-Klasse fuer "rohe" HBCI-Init-Dialoge.
 */
public abstract class AbstractRawHBCIDialogInit extends AbstractRawHBCIDialog
{
    /**
     * ct.
     * @param template das Template.
     */
    AbstractRawHBCIDialogInit(KnownDialogTemplate template)
    {
        super(template);
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#applyData(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected void applyData(final DialogContext ctx)
    {
        super.applyData(ctx);
        
        final HBCIPassportInternal p = ctx.getPassport();
        final HBCIKernelImpl k = ctx.getKernel();

        k.rawSet("Idn.KIK.blz", p.getBLZ());
        k.rawSet("Idn.KIK.country", p.getCountry());
        k.rawSet("ProcPrep.BPD", p.getBPDVersion());
        k.rawSet("ProcPrep.UPD", p.getUPDVersion());
        k.rawSet("ProcPrep.lang",p.getLang());
        k.rawSet("ProcPrep.prodName",HBCIUtils.getParam("client.product.name",HBCIUtils.PRODUCT_ID));
        k.rawSet("ProcPrep.prodVersion",HBCIUtils.getParam("client.product.version","3"));
    }
}
