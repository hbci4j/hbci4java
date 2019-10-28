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
