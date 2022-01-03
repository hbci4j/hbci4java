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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kapott.hbci.callback.HBCICallback.Status;
import org.kapott.hbci.exceptions.ProcessException;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Diese Klasse enthaelt das Dialog-Ende.
 */
public class HBCIDialogEnd extends AbstractRawHBCIDialog
{
    /**
     * Parametrisierende Flags fuer das Dialog-Ende.
     */
    public enum Flag
    {
        /**
         * Dialogende fuer Sync-SigID.
         */
        SIG_ID,
        
        /**
         * Fehler werden nicht geworfen sondern nur geloggt.
         * Muss nur explizit angegeben werden, wenn es kein anonymer Dialog ist oder das Werfen per "client.errors.ignoreDialogEndErrors" abgeschaltet ist.
         */
        ACCEPT_ERROR,
        
    }
    
    private List<Flag> flags = new ArrayList<Flag>();
    
    /**
     * ct.
     * @param flags optionale Flags.
     */
    public HBCIDialogEnd(Flag... flags)
    {
        super(KnownDialogTemplate.END);
        if (flags != null)
            this.flags.addAll(Arrays.asList(flags));
    }

    /**
     * @see org.kapott.hbci.dialog.AbstractHBCIDialogInit#applyData(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected void applyData(DialogContext ctx)
    {
        super.applyData(ctx);

        final HBCIKernelImpl k = ctx.getKernel();
        k.rawSet("DialogEndS.dialogid",ctx.getDialogId());
    }
    
    /**
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#sendData(org.kapott.hbci.dialog.DialogContext)
     */
    @Override
    protected HBCIMsgStatus sendData(DialogContext ctx)
    {
        if (this.flags.contains(Flag.SIG_ID))
        {
            final HBCIPassportInternal p = ctx.getPassport();
            return ctx.getKernel().rawDoIt(p.hasMySigKey(),HBCIKernelImpl.CRYPTIT,p.hasMyEncKey());
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
        
        HBCIUtilsInternal.getCallback().status(ctx.getPassport(),Status.DIALOG_END_DONE,ret);

        if (ret.isOK())
            return;

        final String msg = HBCIUtilsInternal.getLocMsg("ERR_INST_ENDFAILED");

        // Checken, ob es ein anonymer Dialog war. Der kann fehlschlagen. Daher tolerieren wir hier auch fehlgeschlagene Dialog-Enden
        // Ausserdem ignorieren wir den Fehler, wenn es explizit so konfiguriert ist oder per Flag angegeben ist.
        final boolean ignore = ctx.isAnonymous() ||
                               this.flags.contains(Flag.ACCEPT_ERROR) ||
                               HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreDialogEndErrors",msg+": "+ret.getErrorString());

        // Loggen
        HBCIUtils.log("dialog end failed: "+ret.getErrorString(),ignore ? HBCIUtils.LOG_WARN : HBCIUtils.LOG_ERR);

        ProcessException e = new ProcessException(msg,ret);
        ret.addException(e);

        // Fehler werfen, wenn er nicht ignoriert werden darf
        if (!ignore)
            throw e;
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
