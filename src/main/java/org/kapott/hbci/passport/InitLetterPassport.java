/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.passport;

import org.kapott.cryptalgs.SignatureParamSpec;

/**
 * Basis-Interface fuer Passports mit INI-Brief.
 */
public interface InitLetterPassport
{
  /**
   * Liefert die Signatur-Spec.
   * @return die Signatur-Spec.
   */
  public SignatureParamSpec getSignatureParamSpec();

}


