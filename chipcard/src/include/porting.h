
/*  $Id: porting.h,v 1.1 2011/05/04 22:37:55 willuhn Exp $

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

#ifndef PORTING_H
#define PORTING_H

#ifdef __WIN32
#include <windows.h>
#endif

#ifdef __UNIX
#include <unistd.h>
#include <dlfcn.h>
#endif

#ifdef __WIN32
typedef HINSTANCE apihandle_t;
typedef char (FAR WINAPI *initfunc_t)(unsigned short int,unsigned short int);
typedef char (FAR WINAPI *datafunc_t)(unsigned short,unsigned char*,unsigned char*,unsigned short,unsigned char*,unsigned short*,unsigned char*);
typedef char (FAR WINAPI *closefunc_t)(unsigned short);
#define DLOPEN(x) LoadLibrary((LPSTR)x)
#define DLSYM(handle,name) GetProcAddress(handle,name)
#define DLCLOSE(handle) ((handle!=NULL)?0:1)
#define DLERROR() ("")
#endif

#ifdef __UNIX
typedef void* apihandle_t;
typedef char (*initfunc_t)(unsigned short int,unsigned short int);
typedef char (*datafunc_t)(unsigned short,unsigned char*,unsigned char*,unsigned short,unsigned char*,unsigned short*,unsigned char*);
typedef char (*closefunc_t)(unsigned short);
#define DLOPEN(x) dlopen(x,RTLD_NOW)
#define DLSYM(handle,name) dlsym(handle,name)
#define DLCLOSE(handle) dlclose(handle)
#define DLERROR() dlerror()
#endif

#endif
