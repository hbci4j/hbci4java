
/*  $Id: t_ddv_readdata.cpp,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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
    
    HBCI_BankData **data=new HBCI_BankData*[5];
    bool abort=false;
    
    for (int idx=1;idx<=5;idx++) {
        data[idx-1]=new HBCI_BankData;
        if (!DDV_readBankData(idx,data[idx-1])) {
            printf("error reading bank data\n");
            abort=true;
        }
    }
    
    if (!abort) {
        for (int idx=1;idx<=5;idx++) {
            HBCI_BankData *current=data[idx-1];
            
            printf("#%i: %s %s(%s): type %i:%s(%s) - %s\n",
                   idx,
                   current->shortname,
                   current->blz,
                   current->country,
                   current->commtype,
                   current->commaddr,
                   current->commaddradd,
                   current->userid);
        }
    }
    
    unsigned short int sigid=DDV_readSigId();
    printf("signature id is %i\n",sigid);
    
    HBCI_KeyInfo **keyinfo=new HBCI_KeyInfo*[2];
    size_t       len;
    
    if (DDV_readKeyData(keyinfo,&len)) {
        for (unsigned int i=0;i<len;i++) {
            HBCI_KeyInfo *entry=keyinfo[i];
            printf("keynum/-version:%i/%i len:%i alg:%i\n",
                   entry->keynum,entry->keyversion,entry->keylen,entry->alg);
        }
    } else {
        printf("can not read key data\n");
    }
    
    // ejecting card
    BCS_ejectCard(NULL,10,BCS_EJECT_KEEP,BCS_EJECT_DONT_BLINK,BCS_EJECT_DONT_BEEP);
    
    if (!CTAPI_closeCTAPI()) {
        exit(1);
    }
    
    exit(0);
}
