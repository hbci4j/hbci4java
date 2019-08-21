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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.kapott.hbci.manager.HBCIDialog;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;

/**
 * Diese Klasse kapselt die Context-Daten.
 */
public class DialogContext
{
    /**
     * Die initiale Dialog-ID.
     */
    private final static String DIALOGID_INITIAL = "0";
    
    /**
     * Initiale Nachrichtennumer.
     */
    private final static int MSGNUM_INITIAL = 1;
    
    private HBCIKernelImpl kernel;
    private HBCIPassportInternal passport;
    private HBCIMsgStatus msgStatus;
    private RawHBCIDialog init;
    private HBCIDialog dialog;
    private Map<String,Object> meta = new HashMap<String,Object>();
    
    private AtomicInteger msgNum = new AtomicInteger(MSGNUM_INITIAL);
    private String dialogId;
    private boolean anonymous = false;
    
    private final AtomicBoolean repeat = new AtomicBoolean(false);

    /**
     * Erzeugt einen neuen Dialog-Context.
     * @param kernel der Kernel.
     * @param passport der Passport.
     * @return der neue Context.
     */
    public static DialogContext create(HBCIKernelImpl kernel, HBCIPassportInternal passport)
    {
        DialogContext ctx = new DialogContext();
        ctx.kernel = kernel;
        ctx.passport = passport;
        return ctx;
    }

    /**
     * ct.
     */
    private DialogContext()
    {
    }
    
    /**
     * Liefert den Kernel.
     * @return der Kernel. Kann NULL sein.
     */
    public HBCIKernelImpl getKernel()
    {
        return kernel;
    }
    
    /**
     * Liefert die Dialog-Initialisierung.
     * @return die Dialog-Initialisierung.
     */
    public RawHBCIDialog getDialogInit()
    {
        return init;
    }
    
    /**
     * Speichert die Dialog-Initialisierung.
     * @param dialog die Dialog-Initialisierung.
     */
    void setDialogInit(RawHBCIDialog dialog)
    {
        this.init = dialog;
    }
    
    /**
     * Liefert den Message-Status.
     * @return der Message-Status. Kann NULL sein.
     */
    public HBCIMsgStatus getMsgStatus()
    {
        return msgStatus;
    }
    
    /**
     * Aktualisiert den Kontext mit dem aktuellen Nachrichten-Status.
     * @param msgStatus der Message-Status.
     */
    void update(HBCIMsgStatus msgStatus)
    {
        this.msgStatus = msgStatus;
        this.msgNum.incrementAndGet(); // Nachrichtennummer erhoehen
        
        // Wir uebernehmen bei der Gelegenheit gleich noch den aktuellen Status.
        if (this.msgStatus.isOK())
        {
            final Properties result = this.msgStatus.getData();
            this.dialogId = (result != null ? result.getProperty("MsgHead.dialogid",null) : null);

            if (this.dialogId != null)
                HBCIUtils.log("new dialog-id: " + this.dialogId,HBCIUtils.LOG_INFO);
        }
    }
    
    /**
     * Liefert den aktuellen Dialog.
     * @return der aktuelle Dialog. Kann NULL sein.
     */
    public HBCIDialog getDialog()
    {
        return dialog;
    }

    /**
     * Speichert den aktuellen Dialog.
     * @param dialog der aktuelle Dialog.
     */
    public void setDialog(HBCIDialog dialog)
    {
        this.dialog = dialog;
    }
    
    /**
     * Liefert den Passport.
     * @return der Passport.
     */
    public HBCIPassportInternal getPassport()
    {
        return passport;
    }
    
    /**
     * Liefert true, wenn der Dialog anonym ist.
     * @return true, wenn der Dialog anonym ist.
     */
    public boolean isAnonymous()
    {
        return this.anonymous;
    }
    
    /**
     * Speichert, ob der Dialog anonym ist.
     * @param anonymous true, wenn der Dialog anonym ist.
     */
    public void setAnonymous(boolean anonymous)
    {
        this.anonymous = anonymous;
    }
    
    /**
     * Map mit frei definierbaren Meta-Daten.
     * @return meta frei definierbare Meta-Daten.
     */
    public Map<String, Object> getMeta()
    {
        return meta;
    }
    
    /**
     * Liefert die aktuelle Dialog-ID.
     * @return die aktuelle Dialog-ID.
     */
    public String getDialogId()
    {
        return this.dialogId != null ? this.dialogId : DIALOGID_INITIAL;
    }
    
    /**
     * Speichert die aktuelle Dialog-ID.
     * @param dialogId die aktuelle Dialog-ID.
     */
    void setDialogId(String dialogId)
    {
        this.dialogId = dialogId;
    }
    
    /**
     * Liefert die aktuelle Nachrichtennummer.
     * @return die aktuelle Nachrichtennummer.
     */
    public int getMsgNum()
    {
        return this.msgNum.get();
    }
    
    /**
     * Teilt dem Dialog mit, dass er erneut ausgefuehrt werden soll.
     * @param repeat true, wenn der Dialog wiederholt werden soll.
     */
    public void setRepeat(boolean repeat)
    {
        this.repeat.set(repeat);
    }
    
    /**
     * Prueft, ob der Dialog erneut ausgefuehrt werden soll.
     * @return true, wenn der Dialog erneut ausgefuehrt werden soll.
     */
    public boolean isRepeat()
    {
        return this.repeat.get();
    }
}
