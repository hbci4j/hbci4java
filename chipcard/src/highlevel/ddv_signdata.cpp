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

#include <stdlib.h>
#include <string.h>

#include "ctapi-tools.h"
#include "ddvcard.h"
#include "seccos.h"

bool DDV_signData(unsigned char *hash,size_t *len,unsigned char *signature)
{
    // separating left and right part
    unsigned char hash_l[8],hash_r[12];
    memcpy(hash_l,hash,8);
    memcpy(hash_r,hash+8,12);
    
    // writing right part into EF_MAC
    if (!SECCOS_writeRecordBySFI(DDV_EF_MAC,1,hash_r,12)) {
        return false;
    }

    // do the rest depending on card type
    if (HBCI_cardtype==HBCI_CARD_TYPE_DDV_0) {
	// storing left part via PUT DATA
        if (!SECCOS_putData(0x0100,8,hash_l)) {
            return false;
        }
	
	// rereading right part plus signature
        unsigned char command[]={
            SECCOS_CLA_SM_PROPR,
            SECCOS_INS_READ_RECORD,
            1,
            (DDV_EF_MAC<<3)|0x04,
            0x00,
        };
        
        unsigned char      response[300];
        unsigned short int len=300;
        
        unsigned short int status=CTAPI_performWithCard("read mac (sm)",5,command,&len,response);
        if (!CTAPI_isOK(status)) {
            return false;
        }

	// storing signature
	memcpy(hash_l,response+12,8);
    } else {
	// rereading EF_MAC with signature, sending
	// left part as init-vector
        
        unsigned char command[200];
        
	command[0]=SECCOS_CLA_SM1;
	command[1]=SECCOS_INS_READ_RECORD;
	command[2]=1;
	command[3]=(DDV_EF_MAC<<3)|0x04;
	command[4]=0x11;
	command[5]=SECCOS_SM_RESP_DESCR;
	command[6]=0x0c;
	command[7]=SECCOS_SM_CRT_CC;
	command[8]=0x0a;
	command[9]=SECCOS_SM_REF_INIT_DATA;
	command[10]=0x08;
	memcpy(command+11,hash_l,8);
	command[19]=SECCOS_SM_VALUE_LE;
	command[20]=0x01;
	command[21]=0x00;
	command[22]=0x00;

        unsigned char      response[300];
        unsigned short int len=300;
        
        unsigned short int status=CTAPI_performWithCard("read mac (sm)",23,command,&len,response);
        if (!CTAPI_isOK(status)) {
            return false;
        }

	// storing signature
	memcpy(hash_l,response+16,8);
    }

    *len=8;
    memcpy(signature,hash_l,8);
    
    return true;
}
