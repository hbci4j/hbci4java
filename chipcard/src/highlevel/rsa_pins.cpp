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

#include "seccos.h"

bool RSA_modifyPin(unsigned char pinnum,unsigned char oldlen,unsigned char* oldpin,unsigned char newlen,unsigned char* newpin)
{
    unsigned char _oldpin[8];
    memset(_oldpin,0x20,8);
    memcpy(_oldpin,oldpin,oldlen);
    
    unsigned char _newpin[8];
    memset(_newpin,0x20,8);
    memcpy(_newpin,newpin,newlen);
    
    return SECCOS_modifyPin(pinnum, SECCOS_PWD_TYPE_DF, 8, _oldpin, 8, _newpin);
}

bool RSA_verifyPin(unsigned char pinnum,unsigned char pinlen,unsigned char* pin)
{
    unsigned char _pin[8];
    memset(_pin,0x20,8);
    memcpy(_pin,pin,pinlen);
    
    return SECCOS_verifyPin(pinnum, SECCOS_PWD_TYPE_DF, SECCOS_PIN_CODING_T50, 8, _pin);
}
