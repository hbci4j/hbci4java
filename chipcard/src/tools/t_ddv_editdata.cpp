
/*  $Id: t_ddv_editdata.cpp,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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
    int idx=-1;
    if (argc>=2) {
        idx=atoi(argv[1]);
    }
    
    if (!CTAPI_initCTAPI(NULL,LIBNAME,PORTNUM,CTNUM)) {
        exit(1);
    }
    
    // resetting CT
    if (!BCS_resetCT()) {
        exit(1);
    }
    
    // requesting and resetting card
    if (!BCS_requestCard(NULL,10)) {
        exit(1);
    }
    
    switch (HBCI_getCardType()) {
        case HBCI_CARD_TYPE_DDV_0:
            printf("this is a DDV-0-card\n");
            break;
        case HBCI_CARD_TYPE_DDV_1:
            printf("this is a DDV-1-card\n");
            break;
        case HBCI_CARD_TYPE_RSA:
            printf("this is a RSA-card\n");
            break;
        default:
            printf("could not determine card type\n");
            break;
    }
    
    if (idx!=-1) {
        HBCI_BankData      *data=new HBCI_BankData;
        if (DDV_readBankData(idx,data)) {
            printf("#%i: %s %s(%s): type %i:%s(%s) - %s\n",
                   data->recordnum,
                   data->shortname,
                   data->blz,
                   data->country,
                   data->commtype,
                   data->commaddr,
                   data->commaddradd,
                   data->userid);
                   
            printf("shortname: "); scanf("%20s",data->shortname);
            if (data->shortname[0]=='-')
                data->shortname[0]=0x00;
            printf("blz: "); scanf("%8s",data->blz);
            if (data->blz[0]=='-')
                strcpy((char*)data->blz,"PPPPPPPP");
            memcpy(data->country,"280",3);
            data->commtype=0x02;
            printf("commaddr: "); scanf("%28s",data->commaddr);
            if (data->commaddr[0]=='-')
                data->commaddr[0]=0x00;
            printf("userid: "); scanf("%30s",data->userid);
            if (data->userid[0]=='-')
                data->userid[0]=0x00;
            
            bool ok;
            if (BCS_FUs & BCS_HAS_FU_KEYBD) {
                ok=DDV_verifyHBCIPin(false);
            } else {
                unsigned char pin[8];
                printf("pin: ");
                scanf("%9s",pin);
                ok=DDV_verifyHBCIPin(pin);
            }
            
            if (ok) {
                if (DDV_writeBankData(idx,data)) {
                    printf("bank data updated\n");
                } else {
                    printf("error while updating bank data\n");
                }
            } else {
                printf("error while verifying PIN\n");
            }
        } else {
            printf("error reading bank data\n");
        }
    } else {
        unsigned int sigid=DDV_readSigId();
        
        printf("sigid (=%i): ",sigid);
        scanf("%10u",&sigid);
        
        bool ok;
        if (BCS_FUs & BCS_HAS_FU_KEYBD) {
            ok=DDV_verifyHBCIPin(false);
        } else {
            unsigned char pin[8];
            printf("pin: ");
            scanf("%9s",pin);
            ok=DDV_verifyHBCIPin(pin);
        }
        
        if (ok) {
            if (DDV_writeSigId(sigid)) {
                printf("sigid  updated\n");
            } else {
                printf("error while updating sigid\n");
            }
        } else {
            printf("error while verifying PIN\n");
        }
    }
    
    // ejecting card
    BCS_ejectCard(NULL,10,BCS_EJECT_KEEP,BCS_EJECT_DONT_BLINK,BCS_EJECT_DONT_BEEP);
    
    if (!CTAPI_closeCTAPI()) {
        exit(1);
    }
    
    exit(0);
}
