/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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

package org.kapott.hbci.GV_Result;

/**
 * Kapselt die Antwort-Daten des VoP.
 */
public class GVRVoP extends HBCIJobResultImpl
{
  /*
    pain.002 parsen 
  
    Quelle: https://homebanking-hilfe.de/forum/topic.php?p=178922#real178922
  
    Zitat:
      In OrgnlPmtInfAndSts hast du wohl unter TxInfAndSts dann die verschiedenen Antworten pro Status.
      Bspw: für die Close Matches unter <TxSts>RVMC</TxSts> den neuen Namen in <StsRsnInf><AddtlInf>neuer Name</AddtlInf></StsRsnInf>
      und dahinter unter OrgnlTxRef -> Cdtr -> Pty -> Nm den alten falschen Namen und die zugehörige IBAN in CdtrACCT -> Id -> IBAN.
  */
}
