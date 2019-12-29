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

#include "ctapi-tools.h"
#include "seccos.h"

bool SECCOS_putData(unsigned short int tag,unsigned char dataLen,unsigned char *data)
{
    unsigned char *command=new unsigned char[5+dataLen];
    
    command[0]=SECCOS_CLA_STD;
    command[1]=SECCOS_INS_PUT_DATA;
    command[2]=(tag>>8)&0xFF;
    command[3]=(tag>>0)&0xFF;
    command[4]=dataLen;
    memcpy(command+5,data,dataLen);
    
    unsigned char      response[2];
    unsigned short int len=2;
    unsigned short int status=CTAPI_performWithCard("putData",5+dataLen,command,&len,response);
    
    delete command;
    
    if (CTAPI_isOK(status)) {
        return true;
    } else {
        return false;
    }
}
