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
