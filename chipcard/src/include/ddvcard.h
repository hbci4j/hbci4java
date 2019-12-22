/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2007 Stefan Palme
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

#ifndef _DDVCARD_H
#define _DDVCARD_H

#include <stdlib.h>
#include "hbci.h"

// sfis
#define DDV_EF_ID  ((unsigned char)0x19)
#define DDV_EF_BNK ((unsigned char)0x1A)
#define DDV_EF_MAC ((unsigned char)0x1B)
#define DDV_EF_SEQ ((unsigned char)0x1C)

bool               DDV_readBankData(unsigned char idx,HBCI_BankData *data);
bool               DDV_writeBankData(unsigned char recordnum,HBCI_BankData *data);
bool               DDV_verifyHBCIPin(bool usebio);
bool               DDV_verifyHBCIPin(unsigned char *pin);
unsigned short int DDV_readSigId();
bool               DDV_writeSigId(unsigned short int sigid);
bool               DDV_readKeyData(HBCI_KeyInfo **data,size_t *dataLen);
bool               DDV_signData(unsigned char *hash,size_t *len,unsigned char *signature);
bool               DDV_getEncryptionKeys(unsigned char keynum,unsigned char *plainkey,unsigned char *enckey);
bool               DDV_decryptKey(unsigned char keynum,unsigned char *enckey,unsigned char *plainkey);

#endif
