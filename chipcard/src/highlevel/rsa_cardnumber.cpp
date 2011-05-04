
/*  $Id: rsa_cardnumber.cpp,v 1.1 2011/05/04 22:37:59 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2007  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

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
