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

import java.util.concurrent.atomic.AtomicInteger;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Abstrakte Basis-Klasse fuer "rohe" HBCI-Dialoge.
 */
public abstract class AbstractRawHBCIDialog implements RawHBCIDialog
{
    private KnownDialogTemplate template = null;
    private AtomicInteger executions = new AtomicInteger(0);
    
    /**
     * ct.
     * @param template das zu verwendende Template.
     */
    AbstractRawHBCIDialog(KnownDialogTemplate template)
    {
        this.template = template;
    }
    
    /**
     * @see org.kapott.hbci.dialog.RawHBCIDialog#execute(org.kapott.hbci.dialog.DialogContext)
     */
    public final HBCIMsgStatus execute(final DialogContext ctx)
    {
        HBCIMsgStatus status = null;
        
        do
        {
            if (ctx.isDialogEnd())
            {
                ctx.setDialogEnd(false);
                final HBCIDialogEnd end = new HBCIDialogEnd();
                end.execute(ctx);
            }
            // Sicherstellen, dass das Flag false ist, wenn wir starten. Kann von einem Event wieder aktiviert werden
            ctx.setRepeat(false);
            
            // Checken, ob ein Neustart noch moeglich ist:
            if (this.executions.get() > 2)
            {
                HBCIUtils.log("dialog loop detected for " + this.getTemplate() + ", id " + ctx.getDialogId() + ", message number: " + ctx.getMsgNum() + ", execution count: " + this.executions.get(),HBCIUtils.LOG_ERR);
                throw new HBCI_Exception("dialog loop detected for " + this.getTemplate());
            }

            ctx.setDialogInit(this);

            final HBCIPassportInternal p = ctx.getPassport();
            final HBCIKernelImpl k = ctx.getKernel();

            ///////////////////////////////////////////////////////
            // Erstellung der Nachricht
            p.onDialogEvent(DialogEvent.MSG_CREATE,ctx);
            final String dialog = this.getActualTemplate(ctx);
            HBCIUtils.log("creating dialog " + dialog + ", id " + ctx.getDialogId() + ", message number: " + ctx.getMsgNum() + ", execution count: " + this.executions.get(),HBCIUtils.LOG_DEBUG);
            k.rawNewMsg(dialog);
            this.applyData(ctx);
            p.onDialogEvent(DialogEvent.MSG_CREATED,ctx);
            // 
            ///////////////////////////////////////////////////////
            
            ///////////////////////////////////////////////////////
            // Versand der Nachricht
            HBCIUtils.log("sending message using dialog " + dialog + ", id " + ctx.getDialogId() + ", message number: " + ctx.getMsgNum(),HBCIUtils.LOG_DEBUG);
            status = this.sendData(ctx);
            //
            ///////////////////////////////////////////////////////

            
            ///////////////////////////////////////////////////////
            // Ergebnis-Auswertung
            ctx.update(status);
            this.executions.incrementAndGet();
            p.onDialogEvent(DialogEvent.MSG_SENT,ctx);
            this.checkResult(ctx);
            //
            ///////////////////////////////////////////////////////
        }
        while (ctx.isRepeat());

        return status;
    }
    
    /**
     * Default-Implementierung fuer den Versand. Verschluesselung und Signierung findet nur statt, wenn es kein anonymer Dialog ist.
     * @param ctx der Kontext.
     * @return die Ergebnis-Daten.
     */
    protected HBCIMsgStatus sendData(final DialogContext ctx)
    {
        final boolean a = ctx.isAnonymous();
        return ctx.getKernel().rawDoIt(!a && HBCIKernelImpl.SIGNIT,
                                       !a && HBCIKernelImpl.CRYPTIT,
                                       !a && HBCIKernelImpl.NEED_CRYPT);
    }
    
    /**
     * Befuellt die Daten fuer die Nachricht.
     * @param ctx der Kontext.
     */
    protected void applyData(final DialogContext ctx)
    {
        final HBCIKernelImpl k = ctx.getKernel();

        k.rawSet("MsgHead.dialogid",ctx.getDialogId());
        k.rawSet("MsgHead.msgnum",Integer.toString(ctx.getMsgNum()));
        k.rawSet("MsgTail.msgnum",Integer.toString(ctx.getMsgNum()));
    }
    
    /**
     * Kann implementiert werden, um das Ergebnis des Dialogs zu pruefen.
     * @param ctx der Kontext.
     */
    protected void checkResult(final DialogContext ctx)
    {
    }
    
    /**
     * @see org.kapott.hbci.dialog.RawHBCIDialog#customizeSCA(org.kapott.hbci.dialog.SCARequest)
     */
    @Override
    public void customizeSCA(SCARequest sca)
    {
    }
    
    /**
     * @see org.kapott.hbci.dialog.RawHBCIDialog#getTemplate()
     */
    @Override
    public KnownDialogTemplate getTemplate()
    {
        return this.template;
    }
    
    /**
     * @see org.kapott.hbci.dialog.RawHBCIDialog#setTemplate(org.kapott.hbci.dialog.KnownDialogTemplate)
     */
    @Override
    public void setTemplate(KnownDialogTemplate t)
    {
        this.template = t;
    }
    
    /**
     * Liefert das tatsaechlich zu verwendende Message-Template basierend auf dem Kontext.
     * @param ctx der Kontext.
     * @return template das zu verwendende Message-Template.
     */
    protected String getActualTemplate(final DialogContext ctx)
    {
        return this.getTemplate().getName();
    }
}
