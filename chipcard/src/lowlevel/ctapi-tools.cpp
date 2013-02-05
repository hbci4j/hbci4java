
/*  $Id: ctapi-tools.cpp,v 1.1 2011/05/04 22:37:44 willuhn Exp $

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

#include <string.h>
#include <stdio.h>

#include "ctapi-tools.h"
#include "porting.h"

static initfunc_t         initfunc;
static datafunc_t         datafunc;
static closefunc_t        closefunc;

apihandle_t        handle;
unsigned short int ctnum;

CTAPI_ERROR     CTAPI_error;
CTAPI_logfunc_t CTAPI_logfunc;

void CTAPI_log(const char *msg)
{
    if (CTAPI_logfunc==NULL) {
        printf("%s\n",msg);
    } else {
        CTAPI_logfunc(msg);
    }
}

unsigned short int extractStatus(unsigned short int len,unsigned char *response)
{
    return (((unsigned short int)response[len-2])<<8) + (((unsigned short int)response[len-1])&(unsigned char)0xFF);
}

char* CTAPI_getStatusString(unsigned short int status)
{
    CTAPI_MapInt2String *codes=(CTAPI_MapInt2String*)CTAPI_statusMsgs;
    
    while (codes->msg!=NULL) {
        if (codes->code==status) {
            char *ret=new char[strlen(codes->msg)+1];
            strcpy(ret,codes->msg);
            return ret;
        }
        codes++;
    }
    
    char* ret=new char[5];
    sprintf(ret,"%04X",status);
    return ret; 
}

char* CTAPI_getErrorString(char status)
{
    CTAPI_MapChar2String *codes=(CTAPI_MapChar2String*)CTAPI_errorMsgs;
    
    while (codes->msg!=NULL) {
        if (codes->code==status) {
            char *ret=new char[strlen(codes->msg)+1];
            strcpy(ret,codes->msg);
            return ret;
        }
        codes++;
    }
    
    char* ret=new char[5];
    sprintf(ret,"%i",status);
    return ret; 
}

bool CTAPI_isOK(unsigned short int status)
{
    return ((status&0xFF00)==0x9000) ||
           ((status&0xFF00)==0x6100);
}

#define MIN_LOCAL_RESPONSE_BUFFER_SIZE 4096
static unsigned short int perform(unsigned char _dad,const char *name,
             unsigned short int lenIn,unsigned char *command,
             unsigned short int *lenOut,unsigned char *response)
{
    unsigned char sad=CTAPI_SAD;
    unsigned char dad=_dad;
    
    char logmsg[1024];
    char temp[20];
    static unsigned char *response_local = NULL;
    static unsigned short int lenOut_local, lenOut_return;
     
    if (response_local==NULL) {
      lenOut_local = MIN_LOCAL_RESPONSE_BUFFER_SIZE;
      response_local = (unsigned char *)malloc( lenOut_local * sizeof(unsigned char) );
      if (response_local==NULL) {
        CTAPI_log("Alloc of local response buffer failed. Out of memory. Aborting!");
        return 0;
      }
    }
    if (lenOut_local<(*lenOut)) {
      free( response_local );
      lenOut_local = *lenOut;
      response_local = (unsigned char *)malloc( lenOut_local * sizeof(unsigned char) );
      if (response_local==NULL) {
        CTAPI_log("Realloc of local response buffer failed. Out of memory. Aborting!");
        return 0;
      }
    }
    lenOut_return = lenOut_local;  
      
    sprintf(logmsg,"%s apdu:",name);
    for (int i=0;i<lenIn;i++) {
        sprintf(temp," %02X",command[i]);
        strcat(logmsg,temp);
    }
    CTAPI_log(logmsg);
    
    memcpy(CTAPI_error.request,command,lenIn);
    CTAPI_error.reqLen=lenIn;

    char err;
    int  retries=3;
    while (retries--) { 
        err=(*datafunc)(ctnum,&dad,&sad,lenIn,command,&lenOut_return,response_local);
        CTAPI_error.ret=err;
        
        if (!err)
            break;
        
        sprintf(logmsg,"%s: %i (%s)",name,err,CTAPI_getErrorString(err));
        CTAPI_log(logmsg);
    }

    if (lenOut_return < (*lenOut)) {
      *lenOut = lenOut_return;
    }
    memcpy(response,response_local, *lenOut);
    if (err!=0) {
        CTAPI_log("aborting");
        return 0;
    }
    
    sprintf(logmsg,"%s response:",name);
    for (int i=0;i<*lenOut;i++) {
        sprintf(temp," %02X",response[i]);
        strcat(logmsg,temp);
    }
    CTAPI_log(logmsg);
    
    memcpy(CTAPI_error.response,response,*lenOut);
    CTAPI_error.resLen=*lenOut;

    unsigned short int status=extractStatus(*lenOut,response);
    CTAPI_error.status=status;

    char *msg=CTAPI_getStatusString(status);
    sprintf(logmsg,"%s: %s",name,msg);
    CTAPI_log(logmsg);
    
    delete msg;
    return status;
}

unsigned short int CTAPI_performWithCT(const char *name,unsigned short int lenIn,unsigned char *command,unsigned short int *lenOut,unsigned char *response)
{
    return perform(CTAPI_DAD_CT,name,lenIn,command,lenOut,response);
}

unsigned short int CTAPI_performWithCard(const char *name,unsigned short int lenIn,unsigned char *command,unsigned short int *lenOut,unsigned char *response)
{
    return perform(CTAPI_DAD_CARD,name,lenIn,command,lenOut,response);
}

bool CTAPI_initCTAPI(CTAPI_logfunc_t _logfunc,const char *libname,unsigned short int portnum,unsigned short int _ctnum)
{
    CTAPI_logfunc=_logfunc;
    ctnum=_ctnum;
    
    char logmsg[300];
    
    // loading ctapi library
    handle=DLOPEN(libname);
    if (handle==NULL) {
        sprintf(logmsg,"dlopen: %s",DLERROR());
        CTAPI_log(logmsg);
        return false;
    }

    initfunc=(initfunc_t)(DLSYM(handle,"CT_init"));
    if (initfunc==0) {
        sprintf(logmsg,"dlsym CT_init: %s",DLERROR());
        CTAPI_log(logmsg);
        return false;
    }
    
    datafunc=(datafunc_t)(DLSYM(handle,"CT_data"));
    if (datafunc==NULL) {
        sprintf(logmsg,"dlsym CT_data: %s",DLERROR());
        CTAPI_log(logmsg);
        return false;
    }
    
    closefunc=(closefunc_t)(DLSYM(handle,"CT_close"));
    if (closefunc==NULL) {
        sprintf(logmsg,"dlsym CT_close %s",DLERROR());
        CTAPI_log(logmsg);
        return false;
    }
    
    CTAPI_log("loading lib ok");
    
    // initializing CTAPI lib
    signed char err=(*initfunc)(ctnum,portnum);
    if (err!=0) {
        sprintf(logmsg,"CT_init: %i (%s)",err,CTAPI_getErrorString(err));
        CTAPI_log(logmsg);
        return false;
    }
    
    CTAPI_log("initializing CTAPI ok");
    return true;
}

bool CTAPI_closeCTAPI()
{
    char logmsg[300];
    
    // closing CTAPI lib
    signed char err=(*closefunc)(ctnum);
    if (err!=0) {
        sprintf(logmsg,"CT_close: %i (%s)",err,CTAPI_getErrorString(err));
        CTAPI_log(logmsg);
        return false;
    }
    
    CTAPI_log("closing CTAPI ok");

    // unloading CTAPI library
    if (DLCLOSE(handle)!=0) {
        sprintf(logmsg,"dlclose: %s",DLERROR());
        CTAPI_log(logmsg);
        return false;
    }
    
    return true;
}
