
/*  $Id: HBCICallbackNative.java,v 1.1 2011/05/04 22:37:52 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

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

package org.kapott.hbci.callback;

import java.util.Date;

import org.kapott.hbci.passport.HBCIPassport;

/** <p>Callback-Klasse für native-Applikationen, die <em>HBCI4Java</em>
    durch Aufrufe des JNI benutzen. Diese Klasse wird in Java-Anwendungen
    selbst nicht benötigt, sondern stellt nur eine Schnittstelle dar, um die
    Callbacks via JNI zu realisieren.</p><p>
    Diese Klasse überschreibt die drei Methoden <code>log</code>, <code>callback</code> 
    und <code>status</code>. In dieser Klasse werden die jeweiligen Methoden-Aufrufe
    nicht direkt behandelt, sondern es wird jeweils eine native Methode mit 
    der gleichen Signatur aufgerufen, nur dass sich der Methodenname durch ein
    vorangestelltes Prefix "<code>native</code>" unterscheidet.</p><p>
    Eine Anwendung muss vor dem Initialisieren des HBCI-Kernels diese nativen
    Methoden via JNI registrieren. Folgender Programmcode kann als Vorlage für
    die Verwendung von <em>HBCI4Java</em>-Callbacks aus einer C++-Anwendung heraus dienen:
<pre>
#include "jni.h"

void myOwnLog(JNIEnv *env,jobject obj,jstring jmsg,jint level,jobject date,jobject trace)
{
    const char *msg=env->GetStringUTFChars(jmsg,NULL);
    printf("log: %s\n",msg);
    env->ReleaseStringUTFChars(jmsg,msg);
}

void myOwnCallback(JNIEnv *env,jobject obj,jobject passport,jint reason,jstring msg,jint datatype,jobject retData)
{
    switch ((int)reason) {
        // ...
    }
}

void myOwnStatus(JNIEnv *env,jobject obj,jobject passport,jint statusTag,jarray o)
{
    printf("status-callback\n");
}

int main(int argc,char **argv)
{
    JavaVM *jvm;
    JNIEnv *env;
    JavaVMInitArgs vm_args;
    JavaVMOption options[1];
    JNINativeMethod methods[3];
    
    // initialize JVM 
    options[0].optionString="-Djava.class.path=/home/kleiner/projects/hbci2/classes";
    vm_args.version=JNI_VERSION_1_4;
    vm_args.options=options;
    vm_args.nOptions=1;
    printf("create: %i\n",JNI_CreateJavaVM(&jvm,(void**)&env,&vm_args));
    
    // build array for registering native callback-methods
    methods[0].name="nativeLog";
    methods[0].signature="(Ljava/lang/String;ILjava/util/Date;Ljava/lang/StackTraceElement;)V";
    methods[0].fnPtr=myOwnLog;
    methods[1].name="nativeCallback";
    methods[1].signature="(Lorg/kapott/hbci/passport/HBCIPassport;ILjava/lang/String;ILjava/lang/StringBuffer;)V";
    methods[1].fnPtr=myOwnCallback;
    methods[2].name="nativeStatus";
    methods[2].signature="(Lorg/kapott/hbci/passport/HBCIPassport;I[Ljava/lang/Object;)V";
    methods[2].fnPtr=myOwnStatus;
    
    // get class HBCICallbackNative
    jclass callbacknative=env->FindClass("org/kapott/hbci/callback/HBCICallbackNative");
    printf("callbacknative: %p\n",callbacknative);
    
    // register native methods
    printf("register: %i\n",env->RegisterNatives(callbacknative,methods,3));
    
    // get constructor for HBCICallbackNative
    jmethodID callbacknative_init=env->GetMethodID(callbacknative,"<init>","()V");
    printf("callbacknative_init: %p\n",callbacknative_init);
    
    // create new HBCICallbackNative-object
    jobject callback=env->NewObject(callbacknative,callbacknative_init);
    printf("callback: %p\n",callback);
    
    // get class HBCIUtils
    jclass utils=env->FindClass("org/kapott/hbci/manager/HBCIUtils");
    printf("utils: %p\n",utils);
    
    // get method HBCIUtils.setParam()
    jmethodID utils_setparam=env->GetStaticMethodID(utils,"setParam","(Ljava/lang/String;Ljava/lang/String;)V");
    printf("utils_setparam: %p\n",utils_setparam);

    // set loglevel to DEBUG
    jstring name=env->NewStringUTF("log.loglevel.default");
    jstring value=env->NewStringUTF("4");
    env->CallStaticVoidMethod(utils,utils_setparam,name,value);
    
    // get method HBCIUtils.init()
    jmethodID utils_init=env->GetStaticMethodID(utils,"init","(Ljava/lang/ClassLoader;Ljava/lang/String;Lorg/kapott/hbci/callback/HBCICallback;)V");
    printf("utils_init: %p\n",utils_init);
    
    // call HBCIUtils.init()
    env->CallStaticVoidMethod(utils,utils_init,NULL,NULL,callback);
    
    // ...
    // do HBCI stuff here
    // ...
    
    jvm->DestroyJavaVM();
}
</pre> */
public final class HBCICallbackNative
    extends AbstractHBCICallback
{
    /** Externe, von der Anwendung zu implementierende Methode. Es muss von der
     * nativen Anwendung eine Methode mit dieser Signatur erzeugt und via JNI
     * registriert werden. */
    public native void nativeLog(String msg,int level,Date date,StackTraceElement trace);

    /** Externe, von der Anwendung zu implementierende Methode. Es muss von der
     * nativen Anwendung eine Methode mit dieser Signatur erzeugt und via JNI
     * registriert werden. */
    public native void nativeCallback(HBCIPassport passport,int reason,String msg,int datatype,StringBuffer retData);

    /** Externe, von der Anwendung zu implementierende Methode. Es muss von der
     * nativen Anwendung eine Methode mit dieser Signatur erzeugt und via JNI
     * registriert werden. */
    public native void nativeStatus(HBCIPassport passport,int statusTag,Object[] o);

    /** Ruft {@link #nativeLog(String, int, Date, StackTraceElement)} auf. */
    public synchronized void log(String msg,int level,Date date,StackTraceElement trace)
    {
        nativeLog(msg,level,date,trace);
    }

    /** Ruft {@link #nativeCallback(HBCIPassport, int, String, int, StringBuffer)} auf. */
    public void callback(HBCIPassport passport,int reason,final String msg,int datatype,StringBuffer retData)
    {
        nativeCallback(passport,reason,msg,datatype,retData);
    }
    
    /** Ruft {@link #nativeStatus(HBCIPassport, int, Object[])} auf. */
    public synchronized void status(HBCIPassport passport,int statusTag,Object[] o)
    {
        nativeStatus(passport,statusTag,o);
    }
}
