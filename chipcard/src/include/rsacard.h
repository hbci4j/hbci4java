
/*  $Id: rsacard.h,v 1.1 2011/05/04 22:37:55 willuhn Exp $

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

#ifndef RSACARD_H_
#define RSACARD_H_

// file ids
#define RSA_EF_SEQ        ((unsigned short int)0xa601)
#define RSA_EF_KEY_LOG    ((unsigned short int)0xa602)
#define RSA_EF_BNK        ((unsigned short int)0xa603)
#define RSA_EF_KD_ID      ((unsigned short int)0xa604)
#define RSA_EF_ICC_INFO   ((unsigned short int)0xa606)
#define RSA_EF_IPF        ((unsigned short int)0xb300)

#define RSA_DF_BANKING ((unsigned short)0xa600)

#define RSA_PIN_CH ((unsigned char)0x10)
#define RSA_PIN_EG ((unsigned char)0x11)

// zurückgegebenes byte-array ist immer 5 bytes lang
unsigned char*  RSA_getCardNumber();

bool RSA_modifyPin(unsigned char pinnum,unsigned char oldlen,unsigned char* oldpin,unsigned char newlen,unsigned char* newpin);
bool RSA_verifyPin(unsigned char pinnum,unsigned char pinlen,unsigned char* pin);

#endif /*RSACARD_H_*/
