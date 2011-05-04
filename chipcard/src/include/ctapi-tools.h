
/*  $Id: ctapi-tools.h,v 1.1 2011/05/04 22:37:54 willuhn Exp $

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

#ifndef _CTAPI_TOOLS_H
#define _CTAPI_TOOLS_H

#include <stdlib.h>

// sad and dad
#define CTAPI_SAD      ((unsigned char)0x02)
#define CTAPI_DAD_CT   ((unsigned char)0x01)
#define CTAPI_DAD_CARD ((unsigned char)0x00)

// CTAPI error codes
#define CTAPI_ERR_OK      ((char)0) 
#define CTAPI_ERR_INVALID ((char)-1) 
#define CTAPI_ERR_CT      ((char)-8) 
#define CTAPI_ERR_TRANS   ((char)-10) 
#define CTAPI_ERR_MEMORY  ((char)-11) 
#define CTAPI_ERR_HOST    ((char)-127) 
#define CTAPI_ERR_HTSI    ((char)-128) 

typedef void (*CTAPI_logfunc_t)(const char *msg);

typedef struct CTAPI_ERROR {
    unsigned char      request[300];
    size_t             reqLen;
    unsigned char      response[300];
    size_t             resLen;
    char               ret;
    unsigned short int status;
} CTAPI_ERROR;

// error codes
typedef struct CTAPI_MapInt2String {
    unsigned short int code;
    const char*        msg;
} CTAPI_MapInt2String;

// error codes
typedef struct CTAPI_MapChar2String {
    char        code;
    const char* msg;
} CTAPI_MapChar2String;

const CTAPI_MapInt2String CTAPI_statusMsgs[]=
{
    {0x6200,"timeout"},
    {0x6201,"card already present"},
    {0x6281,"part of returned data may be corrupted"},
    {0x6282,"end of file reached before Le bytes read"},
    {0x6283,"selected file invalidated"},
    {0x6284,"FCI format incorrect"},
    {0x6381,"file filled up by last write"},
    {0x63C0,"use of internal retry routine (0)"},
    {0x63C1,"use of internal retry routine (1)"},
    {0x63C2,"use of internal retry routine (2)"},
    {0x63C3,"use of internal retry routine (3)"},
    {0x63C4,"use of internal retry routine (4)"},
    {0x63C5,"use of internal retry routine (5)"},
    {0x63C6,"use of internal retry routine (6)"},
    {0x63C7,"use of internal retry routine (7)"},
    {0x63C8,"use of internal retry routine (8)"},
    {0x63C9,"use of internal retry routine (9)"},
    {0x63CA,"use of internal retry routine (10)"},
    {0x63CB,"use of internal retry routine (11)"},
    {0x63CC,"use of internal retry routine (12)"},
    {0x63CD,"use of internal retry routine (13)"},
    {0x63CE,"use of internal retry routine (14)"},
    {0x63CF,"use of internal retry routine (15)"},
    {0x6400,"command not successful"},
    {0x6401,"aborted by cancel key"},
    {0x64A1,"no card present"},
    {0x64A2,"card not activated"},
    {0x6581,"memory failure"},
    {0x6700,"wrong length"},
    {0x6881,"logical channel not supported"},
    {0x6882,"secure messaging not supported"},
    {0x6883,"final command expected"},
    {0x6900,"command not allowed"},
    {0x6981,"command incompatible with file structure"},
    {0x6982,"security status not satisfied"},
    {0x6983,"authentication method blocked"},
    {0x6984,"referenced data invalidated"},
    {0x6985,"conditions of use not satisfied"},
    {0x6986,"command not allowed (no EF selected)"},
    {0x6987,"expected SM data objects missing"},
    {0x6988,"SM data objects inconsistent"},
    {0x6A00,"wrong parameters p1,p2"},
    {0x6A80,"incorrect parameters in data field"},
    {0x6A81,"function not supported"},
    {0x6A82,"file not found"},
    {0x6A83,"record not found"},
    {0x6A84,"not enough memory space"},
    {0x6A85,"Lc inconsistent with TLV structure"},
    {0x6A86,"incorrect parameters p1-p2"},
    {0x6A87,"Lc inconsistent with p1-p2"},
    {0x6A88,"referenced data not found"},
    {0x6B00,"wrong parameters (offset outside transparent EF)"},
    {0x6C00,"wrong length in Le"},
    {0x6D00,"wrong instruction"},
    {0x6E00,"class not supported"},
    {0x6F00,"communication with ICC not possible"},
    {0x9000,"success"},
    {0x9001,"success"},
    {0x0000,NULL},
};

const CTAPI_MapChar2String CTAPI_errorMsgs[]=
{
    {0,"success"},
    {-1,"invalid parameter or value"},
    {-8,"CT error"},
    {-10,"transmission error"},
    {-11,"memory error"},
    {-127,"aborted"},
    {-128,"HTSI error"},
    {0,NULL},
};

void               CTAPI_log(const char *msg);
bool               CTAPI_initCTAPI(CTAPI_logfunc_t _logfunc,const char *libname,unsigned short int portnum,unsigned short int ctnum);
unsigned short int CTAPI_performWithCT(const char *name,unsigned short int lenIn,unsigned char *command,unsigned short int *lenOut,unsigned char *response);
unsigned short int CTAPI_performWithCard(const char *name,unsigned short int lenIn,unsigned char *command,unsigned short int *lenOut,unsigned char *response);

char*              CTAPI_getErrorString(char status);
char*              CTAPI_getStatusString(unsigned short int status);
bool               CTAPI_isOK(unsigned short int status);

bool               CTAPI_closeCTAPI();

extern CTAPI_ERROR     CTAPI_error;
extern CTAPI_logfunc_t CTAPI_logfunc;

#endif
