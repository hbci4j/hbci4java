
/*  $Id: t_hbci_cardtype.cpp,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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
#include <stdlib.h>
#include <string.h>

#include "ctapi-tools.h"
#include "bcs.h"
#include "ddvcard.h"

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
    
    // requesting and reset card
    if (!BCS_requestCard(NULL,10)) {
        exit(1);
    }
    CTAPI_ERROR save_error=CTAPI_error;
    
    char nof_hisBytes;
    switch (HBCI_getCardType()) {
        case HBCI_CARD_TYPE_DDV_0:
            printf("this is a DDV-0-card\n");
            break;
        case HBCI_CARD_TYPE_DDV_1:
            printf("this is a DDV-1-card\n");
            break;
        case HBCI_CARD_TYPE_RSA:
            printf("this is a RSA-card\n");
            
            nof_hisBytes=save_error.response[1]&0x0F;
            if (!strncmp((char*)save_error.response+save_error.resLen-3-nof_hisBytes,"SPK23",5)) {
                printf("  OS is SPK 2.3 (STARCOS)\n");
            } else {
                printf("  unknown OS\n");
            }
            break;
        default:
            printf("could not determine card type\n");
            break;
    }
    
    // ejecting card
    BCS_ejectCard(NULL,10,BCS_EJECT_KEEP,BCS_EJECT_DONT_BLINK,BCS_EJECT_DONT_BEEP);
    
    if (!CTAPI_closeCTAPI()) {
        exit(1);
    }
    
    exit(0);
}
