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
#include "seccos.h"

bool SECCOS_internalAuthenticate(unsigned char keynum,unsigned char keytype,
                                 size_t dataLen,unsigned char *data,
                                 size_t *encLen,unsigned char *enc)
{
    unsigned char command[200];
    
    command[0]=SECCOS_CLA_STD;
    command[1]=SECCOS_INS_INT_AUTH;
    command[2]=0x00;
    command[3]=keytype | keynum;
    command[4]=dataLen;
    memcpy(command+5,data,dataLen);
    command[5+dataLen]=0x00;
    
    unsigned short int len=300;
    unsigned char      *response=new unsigned char[len];
    
    unsigned short int status=CTAPI_performWithCard("internalAuthenticate",5+dataLen+1,command,&len,response);
    
    if (CTAPI_isOK(status)) {
        *encLen=len-2;
        memcpy(enc,response,*encLen);
        delete response;
        return true;
    } else {
        delete response;
        return false;
    }
}
