
/*  $Id: t_hbci_cardinfo.cpp,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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
    
    // resetting CT
    if (!BCS_resetCT()) {
        exit(1);
    }
    
    // request manufacturer info
    char* manufacturer=BCS_requestCTManufacturer();
    printf("manufacturer: %s\n", manufacturer);
    delete[] manufacturer;
    
    // request icc status
    size_t         number;
    BCS_ICCStatus* icc_status; 
    icc_status=BCS_requestICCStatus(&number);
    for (unsigned int i=0;i<number;i++) {
        printf("ICC status #%i: present=%i, connected=%i\n", 
               i+1, 
               icc_status[i].cardpresent, 
               icc_status[i].connected);
    }
    delete[] icc_status;
    
    // request functional units
    unsigned short fus=BCS_requestFunctionalUnits();
    printf("has display: %i\n", (fus&BCS_HAS_FU_DISPLAY)!=0);
    printf("has keybd: %i\n", (fus&BCS_HAS_FU_KEYBD)!=0);
    
    // requesting and reset card
    if (!BCS_requestCard("Karte rein",10)) {
        exit(1);
    }
    
    // ejecting card
    BCS_ejectCard("Karte raus",10,BCS_EJECT_KEEP,BCS_EJECT_DONT_BLINK,BCS_EJECT_DONT_BEEP);
    
    if (!CTAPI_closeCTAPI()) {
        exit(1);
    }
    
    exit(0);
}
