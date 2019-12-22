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

bool SECCOS_modifyPin(unsigned char pwdnum,unsigned char pwdtype,size_t oldlen,unsigned char *oldpin,size_t newlen,unsigned char *newpin)
{
    unsigned char *command=new unsigned char[5+oldlen+newlen];
    
    command[0]=SECCOS_CLA_STD;
    command[1]=SECCOS_INS_MODIFY;
    command[2]=0x00;
    command[3]=pwdtype|pwdnum;
    command[4]=oldlen+newlen;
    
    memcpy(command+5,        oldpin, oldlen);
    memcpy(command+5+oldlen, newpin, newlen);
    
    unsigned char      *response=new unsigned char[2];
    unsigned short int len=2;
    
    unsigned short int status=CTAPI_performWithCard("modify",5+oldlen+newlen,command,&len,response);
    
    delete command;
    delete response;
    return CTAPI_isOK(status);
}
