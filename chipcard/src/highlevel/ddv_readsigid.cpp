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

#include "ddvcard.h"
#include "seccos.h"

unsigned short int DDV_readSigId()
{
    unsigned short int ret=(unsigned short int)0xFFFF;
    
    unsigned char *buffer=new unsigned char[2];
    size_t        len;
    
    if (SECCOS_readRecordBySFI(DDV_EF_SEQ,1,buffer,&len)) {
        ret=(((unsigned short int)buffer[0])<<8) | (unsigned short int)buffer[1];
    }
    
    delete buffer;
    return ret;
}
