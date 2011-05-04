
/*  $Id: t_test.cpp,v 1.1 2011/05/04 22:37:57 willuhn Exp $

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

#include <stdio.h>

#include "bcs.h"
#include "ctapi-tools.h"
#include "seccos.h"
#include "test.h"

int main(int argc,char **argv)
{
    if (!CTAPI_initCTAPI(NULL,LIBNAME,PORTNUM,CTNUM)) {
        exit(1);
    }

    BCS_resetCT();
    BCS_requestCard("Karte rein", 20);

    // select DF notepad
    unsigned char aid[]={0xD2, 0x76, 0x00, 0x00, 0x25, 0x4E, 0x50, 0x01, 0x00};
    SECCOS_selectFileByName(SECCOS_SELECT_RET_FCP, 9, aid);

    // select EF notepad
    SECCOS_selectSubFile(SECCOS_SELECT_RET_FCP, 0xA611);

    SECCOS_selectSubFile(SECCOS_SELECT_RET_FCP, 0x0030);
    size_t         s;
    unsigned char* buffer=new unsigned char[300];
    for (int i=1; i<=5; i++) {
        SECCOS_readRecord(i, buffer, &s);
    }

    // TODO: authentication

    // read bank data records
    /*
    size_t         s;
    unsigned char* buffer=new unsigned char[300];
    for (int i=1; i<16; i++) {
        SECCOS_readRecord(i, buffer, &s);
    }
    */

    BCS_ejectCard("Karte raus", 1, false, false, false);
    
    if (!CTAPI_closeCTAPI()) {
        exit(1);
    }
    
    exit(0);
}
