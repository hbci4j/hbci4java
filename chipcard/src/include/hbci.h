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

#ifndef _HBCI_H
#define _HBCI_H

#define HBCI_CARD_TYPE_UNKNOWN ((unsigned short int)0)
#define HBCI_CARD_TYPE_DDV_0   ((unsigned short int)1)
#define HBCI_CARD_TYPE_DDV_1   ((unsigned short int)2)
#define HBCI_CARD_TYPE_RSA     ((unsigned short int)3)

typedef struct HBCI_BankData {
    unsigned char recordnum;
    unsigned char shortname[22];
    unsigned char blz[10];
    unsigned char commtype;
    unsigned char commaddr[30];
    unsigned char commaddradd[4];
    unsigned char country[5];
    unsigned char userid[32];
} HBCI_BankData;

typedef struct HBCI_KeyInfo {
    unsigned char keynum;
    unsigned char keyversion;
    unsigned char alg;
    unsigned char keylen;
} HBCI_KeyInfo;

extern unsigned short int HBCI_cardtype;

unsigned short int HBCI_getCardType();

#endif
