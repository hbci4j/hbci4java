/**********************************************************************
 *
 * Copyright (c) 2026 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.passport;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * Hilfsklasse zum Codieren/Decodieren der User-Signatur.
 */
public class UserSig
{
  private final static String ENCODING = Comm.ENCODING;
      
  /**
   * Erzeugt die Signatur.
   * @param pin die PIN.
   * @param tan die TAN.
   * @return die Signatur.
   */
  public static byte[] encode(String pin, String tan)
  {
    try
    {
      if (pin == null)
        pin = "";
      
      if (tan == null)
        tan = "";
      
      final ByteArrayOutputStream bos = new ByteArrayOutputStream();
      bos.write(pin.length());
      bos.write(pin.getBytes(ENCODING));
      bos.write(tan.getBytes(ENCODING));
      
      return bos.toByteArray();
    }
    catch (Exception e)
    {
      HBCIUtils.log(e);
      throw new HBCI_Exception("unable to encode pin/tan",e);
    }
  }
  
  /**
   * Decodiert die Signatur.
   * Die Funktion sichert zu, dass das zurückgelieferte Array immer 2 Elemente besitzt. Ggf. sind es Leerstrings.
   * @param sig die Signatur.
   * @return PIN und TAN.
   */
  public static String[] decode(byte[] sig)
  {
    final int siglen = sig != null ? sig.length : 0;
    
    if (sig == null || siglen < 1)
      throw new HBCI_Exception("pin/tan missing or too short - sig length: " + siglen);
    
    try
    {
      final int pinlen = sig[0];
      if (sig.length < (pinlen+1))
        throw new HBCI_Exception("pin length invalid - sig length: " + siglen + ", pin length: " + pinlen);
      
      final String pin = new String(Arrays.copyOfRange(sig,1,pinlen+1),ENCODING);
      final String tan = new String(Arrays.copyOfRange(sig,pinlen+1,siglen),ENCODING);
      return new String[]{pin,tan};
    }
    catch (Exception e)
    {
      HBCIUtils.log(e);
      throw new HBCI_Exception("unable to decode pin/tan",e);
    }
  }

}
