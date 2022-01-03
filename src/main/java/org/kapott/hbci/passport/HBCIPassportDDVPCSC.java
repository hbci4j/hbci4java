/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
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

package org.kapott.hbci.passport;

import java.io.File;

import org.kapott.hbci.callback.HBCICallback.Reason;
import org.kapott.hbci.callback.HBCICallback.ResponseType;
import org.kapott.hbci.datatypes.SyntaxCtr;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIKey;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.storage.PassportData;
import org.kapott.hbci.passport.storage.PassportStorage;
import org.kapott.hbci.smartcardio.DDVBankData;
import org.kapott.hbci.smartcardio.DDVCardService;
import org.kapott.hbci.smartcardio.DDVKeyData;
import org.kapott.hbci.smartcardio.SmartCardService;

/**
 * Implementierung eines DDV-Passports, welcher intern die neue Chipkarten-API
 * "javax.smartcardio" von Java 6 verwendet. Die Implementierung basiert auf
 * dem OCF-Code von HBCI4Java 2.5.8.
 */
public class HBCIPassportDDVPCSC extends HBCIPassportDDV
{
    private DDVCardService cardService;
    
    /**
     * ct.
     * @param init
     * @param dummy
     */
    public HBCIPassportDDVPCSC(Object init, int dummy)
    {
      super(init,dummy);
    }

    /**
     * ct.
     * @param init
     */
    public HBCIPassportDDVPCSC(Object init)
    {
        this(init,0);
      
        this.setUseBio(Integer.parseInt(HBCIUtils.getParam(getParamHeader()+".usebio","-1")));
        this.setUseSoftPin(Integer.parseInt(HBCIUtils.getParam(getParamHeader()+".softpin","-1")));
        this.setSoftPin(new byte[0]);
        this.setPINEntered(false);
        this.setEntryIdx(Integer.parseInt(HBCIUtils.getParam(getParamHeader()+".entryidx","1")));
        this.setPort(new Integer(3000));
        this.setFilterType("None");

        try
        {
          HBCIUtilsInternal.getCallback().callback(this,Reason.NEED_CHIPCARD,HBCIUtilsInternal.getLocMsg("CALLB_NEED_CHIPCARD"),ResponseType.NONE,null);
          HBCIUtils.log("initializing javax.smartcardio",HBCIUtils.LOG_DEBUG);
          this.initCT();
          HBCIUtilsInternal.getCallback().callback(this,Reason.HAVE_CHIPCARD,"",ResponseType.NONE,null);
          
          this.ctReadBankData();
          
          // Wenn wir neue Daten haben, speichern wir die gleich auf der Karte
          if (this.askForMissingData(true,true,true,false,false,true,false))
            this.saveBankData();

          // Schluesseldaten von der Karte laden.
          this.ctReadKeyData();
          
          // Lokale Passport-Datei laden, wenn sie existiert
          final String path = HBCIUtils.getParam(getParamHeader()+".path","./");
          this.setFileName(HBCIUtilsInternal.withCounter(path + "pcsc" + getCardId(),getEntryIdx()-1));
          File file = new File(this.getFileName());
          if (file.exists() && file.isFile() && file.canRead())
          {
              PassportData data = PassportStorage.load(this,new File(this.getFileName()));
              this.setBPD(data.bpd);
              this.setUPD(data.upd);
              this.setHBCIVersion(data.hbciVersion);
          }
        }
        catch (Exception e)
        {
            // Verbindung zum Kartenleser nur im Fehlerfall schliessen
            try
            {
                closeCT();
            }
            catch (Exception e2)
            {
                HBCIUtils.log(e2);
            }
            
            if (e instanceof HBCI_Exception)
                throw (HBCI_Exception) e;
                
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CTERR"),e);
        }
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#initCT()
     */
    protected void initCT()
    {
      this.cardService = SmartCardService.createInstance(DDVCardService.class,HBCIUtils.getParam(getParamHeader() + ".pcsc.name", null));
      this.setCID(this.cardService.getCID());
      this.setCardId(this.cardService.getCardId());
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#ctReadBankData()
     */
    protected void ctReadBankData()
    {
      int idx = this.getEntryIdx()-1;
      DDVBankData bankData = this.cardService.readBankData(idx);
      
      this.setBLZ(bankData.blz);
      this.setCountry(SyntaxCtr.getName(bankData.country));
      this.setHost(bankData.commaddr);
      this.setUserId(bankData.userid);
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#ctReadKeyData()
     */
    protected void ctReadKeyData()
    {
      this.setSigId(new Long(cardService.readSigId()));
      
      // readKeyData
      DDVKeyData[] keyData=cardService.readKeyData();
      
      this.setInstSigKey(new HBCIKey(
          getCountry(), getBLZ(), getUserId(), 
          Integer.toString(keyData[0].num), Integer.toString(keyData[0].version), 
          null));
      
      this.setInstEncKey(new HBCIKey(
          getCountry(), getBLZ(), getUserId(), 
          Integer.toString(keyData[1].num), Integer.toString(keyData[1].version),
          null));
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#ctEnterPIN()
     */
    protected void ctEnterPIN()
    {
      if (getUseSoftPin()==1)
        this.cardService.verifySoftPIN(1, this.getSoftPin());
      else
        this.cardService.verifyHardPIN(1);
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#ctSaveBankData()
     */
    protected void ctSaveBankData()
    {
      int idx = this.getEntryIdx()-1;
      DDVBankData bankData;
      
      bankData=cardService.readBankData(idx);
      bankData.country=SyntaxCtr.getCode(this.getCountry());
      bankData.blz=this.getBLZ();
      bankData.commaddr=this.getHost();
      bankData.userid=this.getUserId();
      cardService.writeBankData(idx,bankData);
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#ctSaveSigId()
     */
    protected void ctSaveSigId()
    {
      cardService.writeSigId(getSigId().intValue());
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#ctSign(byte[])
     */
    protected byte[] ctSign(byte[] data)
    {
      return cardService.sign(data);
    }

    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#ctEncrypt()
     */
    protected byte[][] ctEncrypt()
    {
      return cardService.getEncryptionKeys(Integer.parseInt(getInstEncKeyNum()));
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#ctDecrypt(byte[])
     */
    protected byte[] ctDecrypt(byte[] cryptedKey)
    {
      return cardService.decrypt(Integer.parseInt(getInstEncKeyNum()),cryptedKey);
    }
    
    /**
     * @see org.kapott.hbci.passport.HBCIPassportDDV#closeCT()
     */
    protected void closeCT()
    {
      try
      {
        if (this.cardService != null)
          this.cardService.close();
      }
      finally
      {
        this.cardService = null;
      }
    }
}
