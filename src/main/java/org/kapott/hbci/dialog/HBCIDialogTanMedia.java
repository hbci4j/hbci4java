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

import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUser;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;
import org.kapott.hbci.status.HBCIMsgStatus;
import org.kapott.hbci.tools.StringUtil;

/**
 * Diese Klasse enthaelt den Dialog fuer den Abruf der TAN-Medien.
 */
public class HBCIDialogTanMedia extends AbstractRawHBCIDialog
{
  private final static String GVNAME = "TANMediaList";
  
  /**
   * ct.
   */
  public HBCIDialogTanMedia()
  {
    super(KnownDialogTemplate.TANMEDIA);
  }

  /**
   * Prueft, ob der Dialog noetig ist.
   * @param ctx der Context.
   * @return true, wenn er noetig ist.
   */
  public boolean required(DialogContext ctx)
  {
    HBCIPassportInternal p = ctx.getPassport();
    final Properties upd = p.getUPD();
    if (upd == null)
      return true;
    
    return !upd.containsKey(HBCIUser.UPD_KEY_FETCH_TANMEDIA);
  }
    
  /**
   * Prueft, ob der Dialog moeglich ist.
   * @param ctx der Context.
   * @return true, wenn er moeglich ist.
   */
  public boolean supported(DialogContext ctx)
  {
    HBCIPassportInternal p = ctx != null ? ctx.getPassport() : null;
    if (p == null || !(p instanceof AbstractPinTanPassport))
      return false;
      
    return getSegmentVersion(ctx,GVNAME,null) != null;
  }
    
  /**
   * @see org.kapott.hbci.dialog.AbstractHBCIDialogInit#applyData(org.kapott.hbci.dialog.DialogContext)
   */
  @Override
  protected void applyData(DialogContext ctx)
  {
    super.applyData(ctx);

    final HBCIKernelImpl k = ctx.getKernel();
    final Integer version = getSegmentVersion(ctx,GVNAME,5);
    k.rawSet(GVNAME + version + ".mediatype","0"); // Eigentlich wollen wir nur 1 (also nur die aktiven). Aber die SPK akzeptiert das nicht
    k.rawSet(GVNAME + version + ".mediacategory","A"); // Wir wollen alle Medien-Arten
  }
  
  /**
   * @see org.kapott.hbci.dialog.AbstractRawHBCIDialog#checkResult(org.kapott.hbci.dialog.DialogContext)
   */
  @Override
  protected void checkResult(DialogContext ctx)
  {
    final HBCIPassportInternal p = ctx.getPassport();
    Properties upd = null;
    
    try
    {
      super.checkResult(ctx);

      final HBCIMsgStatus ret = ctx.getMsgStatus();
      if (!ret.isOK())
        return;
      
      final Properties result = ret.getData();
      if (result == null)
        return;

      final Integer version = getSegmentVersion(ctx,GVNAME,5);

      final StringBuilder sb = new StringBuilder();
      for (int i=0;i<100;i++)
      {
        final String header = HBCIUtilsInternal.withCounter("TANMediaListRes" + version + ".MediaInfo",i);
        
        if (result.getProperty(header + ".mediacategory") == null)
          break;

        // Nur aktive
        final String status = result.getProperty(header + ".status");
        if (!Objects.equals(status,"1"))
          continue;

        final String name = result.getProperty(header + ".medianame");
        if (!StringUtil.hasText(name))
          continue;

        if (sb.length() != 0)
          sb.append("|");

        sb.append(name);
      }
      
      final String names = sb.toString();
      if (!StringUtil.hasText(names))
        return;
      
      HBCIUtils.log("TAN-Medienbezeichnungen empfangen: " + names, HBCIUtils.LOG_INFO);
      upd = p.getUPD();
      if (upd == null)
      {
        // Fuer den Fall, dass wir das zu einem Zeitpunkt aufgerufen haben, wo wir noch gar keine UPD haben
        upd = new Properties();
        p.setUPD(upd);
      }
      upd.setProperty(HBCIUser.UPD_KEY_TANMEDIA,names);
    }
    finally
    {
      // Egal, wie das Abrufen der SEPA-Infos ausgegangen ist, wir vermerken es als erledigt,
      // damit es nicht immer wieder wiederholt wird.
      if (p != null && upd != null)
      {
        upd.setProperty(HBCIUser.UPD_KEY_FETCH_TANMEDIA,new Date().toString());
        p.saveChanges(); // Sicherstellen, dass die Aenderungen sofort gespeichert sind
      }
    }
  }
}
