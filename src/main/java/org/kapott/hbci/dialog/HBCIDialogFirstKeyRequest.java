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
import org.kapott.hbci.status.HBCIMsgStatus;

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
     * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#applyData(org.kapott.hbci.dialog.DialogContext)
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
     * @see org.kapott.hbci.dialog.AbstractHBCIDialogInit#sendData(org.kapott.hbci.dialog.DialogContext)
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
