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

#include "seccos.h"

bool DDV_decryptKey(unsigned char keynum,unsigned char *enckey,unsigned char *plainkey)
{
    for (int part=0;part<2;part++) {
        size_t dataLen;
        if (!SECCOS_internalAuthenticate(keynum,SECCOS_KEY_TYPE_DF,  
                                         8,enckey+(8*part),
                                         &dataLen,plainkey+(8*part))) {
            return false;
        }
    }
    
    return true;
}
