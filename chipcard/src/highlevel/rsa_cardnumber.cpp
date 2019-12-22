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

#include <string.h>

#include "seccos.h"

unsigned char* RSA_getCardNumber(void)
{
    size_t         len;
    unsigned char* buffer=new unsigned char[300];
    unsigned char* cardid=NULL;
    
    SECCOS_selectRoot(SECCOS_SELECT_RET_NOTHING);
    
    // EF_GDO lesen
    SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, SECCOS_EF_GDO);
    if (SECCOS_readBinary(&len, buffer)) {
        // seriennummer der karte extrahieren
        cardid=new unsigned char[6];
        memcpy(cardid, buffer+6, 5);
        cardid[5]=0;
    }
    
    return cardid;
}
