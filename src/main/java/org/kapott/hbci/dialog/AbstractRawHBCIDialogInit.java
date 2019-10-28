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
