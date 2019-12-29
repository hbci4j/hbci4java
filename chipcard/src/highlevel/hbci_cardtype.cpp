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

#include "hbci.h"
#include "seccos.h"

unsigned short int HBCI_cardtype;

unsigned short int HBCI_getCardType()
{
    unsigned char aid[][9]={
        {0xd2,0x76,0x00,0x00,0x25,0x48,0x42,0x01,0x00},
        {0xd2,0x76,0x00,0x00,0x25,0x48,0x42,0x02,0x00},
        {0xd2,0x76,0x00,0x00,0x74,0x48,0x42,0x01,0x10},
    };
    
    int num;
    for (num=0;num<3;num++) {
        SECCOS_selectRoot(SECCOS_SELECT_RET_NOTHING);
        void* ret=SECCOS_selectFileByName(SECCOS_SELECT_RET_NOTHING,9,aid[num]);
        if (ret!=NULL) {
            break;
        }
    }

    switch (num) {
        case 0:
            HBCI_cardtype=HBCI_CARD_TYPE_DDV_0; break;
        case 1:
            HBCI_cardtype=HBCI_CARD_TYPE_DDV_1; break;
        case 2:
            HBCI_cardtype=HBCI_CARD_TYPE_RSA; break;
        default:
            HBCI_cardtype=HBCI_CARD_TYPE_UNKNOWN; break;
    }
    
    return HBCI_cardtype;
}
