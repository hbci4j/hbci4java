
/*  $Id: frontend.cpp,v 1.1 2011/05/04 22:38:00 willuhn Exp $

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

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <jni.h>

/*#include "frontend.h"*/
#include "bcs.h"
#include "ctapi-tools.h"
#include "ddvcard.h"
#include "hbci.h"
#include "seccos.h"

#ifdef __cplusplus
extern "C" {
#endif 

JNIEnv *javaEnv;

void error(JNIEnv *env,const char *msg)
{
    char temp[1024];
    jclass cls=env->FindClass("org/kapott/hbci/exceptions/CTException");
    sprintf(temp,"%s - ret=%i response=%02x %02x (%s)",msg,CTAPI_error.ret,
            (CTAPI_error.status>>8)&0xFF,
            (CTAPI_error.status>>0)&0xFF,
            CTAPI_getStatusString(CTAPI_error.status));
    env->ThrowNew(cls,temp);
}

// -----------------------------------------------------

void checkForException()
{
    if (javaEnv->ExceptionOccurred()) {
        javaEnv->ExceptionDescribe();
        javaEnv->ExceptionClear();
    }
}

// -----------------------------------------------------

void javaLog(const char *msg)
{
    jclass cls=javaEnv->FindClass("org/kapott/hbci/manager/HBCIUtils");
    jmethodID mid=javaEnv->GetStaticMethodID(cls,"log","(Ljava/lang/String;I)V");
    
    jstring jmsg=javaEnv->NewStringUTF(msg);
    
    jfieldID fid=javaEnv->GetStaticFieldID(cls,"LOG_DEBUG2","I");
    jint jlevel=javaEnv->GetStaticIntField(cls,fid);
    
    javaEnv->CallStaticVoidMethod(cls,mid,jmsg,jlevel);
}

// -----------------------------------------------------

bool initCTAPI(JNIEnv *env,jobject obj)
{
    jclass cls=env->GetObjectClass(obj);
    jmethodID mid=env->GetMethodID(cls,"getLibName","()Ljava/lang/String;");
    jstring jname=(jstring)(env->CallObjectMethod(obj,mid));
    checkForException();
    const char *libname=env->GetStringUTFChars(jname,NULL);
    
    // comport nummer holen
    mid=env->GetMethodID(cls,"getComPort","()I");
    unsigned short int comport=(unsigned short int)(env->CallIntMethod(obj,mid));
    checkForException();
    
    // ctnumber holen
    mid=env->GetMethodID(cls,"getCTNumber","()I");
    unsigned short int ctnumber=(unsigned short int)(env->CallIntMethod(obj,mid));
    checkForException();

    bool ret=CTAPI_initCTAPI(&javaLog,libname,comport,ctnumber);
    
    if (!ret) {
        char temp[1024];
        sprintf(temp,"can not load ctapi lib %s",libname);
        error(env,temp);
    }
        
    env->ReleaseStringUTFChars(jname,libname);
    return ret;
}

// -----------------------------------------------------

JNIEXPORT void JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_initCT
  (JNIEnv *env, jobject obj)
{
    javaEnv=env;
    
    // basic initialization
    if (!initCTAPI(env,obj))
        return;
    
    if (!BCS_resetCT()) {
        error(env,"error while resetting card terminal");
        return;
    }
    
    if (!BCS_requestCard(NULL,60)) {
        error(env,"error while waiting for chipcard");
        return;
    }
    
    if (!BCS_resetCard()) {
        error(env,"error while resetting chipcard");
        return;
    }

    // check type of card
    HBCI_getCardType();
    if (HBCI_cardtype==HBCI_CARD_TYPE_UNKNOWN) {
        error(env,"unknown card type");
        return;
    } else if (HBCI_cardtype==HBCI_CARD_TYPE_RSA) {
        error(env,"this seems to be a RSA card, which are not supported until now");
        return;
    }

    // read card-id
    unsigned char buffer[300];
    size_t        size;
    if (!SECCOS_readRecordBySFI(DDV_EF_ID,1,buffer,&size)) {
        error(env,"error while reading card serial number (EF_ID)");
        return;
    }

    // storing CID in calling object
    buffer[size]=0x00;
    jchar *resdata=new jchar[size];
    for (unsigned int i=0;i<size;i++)
        resdata[i]=buffer[i];
    jstring st=env->NewString(resdata,size);
    jclass cls=env->GetObjectClass(obj);
    jmethodID mid=env->GetMethodID(cls,"setCID","(Ljava/lang/String;)V");
    env->CallVoidMethod(obj,mid,st);
    checkForException();
    delete resdata;

    // storing card id in object
    jchar *cardid=new jchar[16];
    for (int i=0;i<8;i++) {
        cardid[(i<<1)]  =((buffer[i+1]>>4)&0x0F) +0x30;
        cardid[(i<<1)+1]=((buffer[i+1])   &0x0F) +0x30;
    }
    st=env->NewString(cardid,16);
    cls=env->GetObjectClass(obj);
    mid=env->GetMethodID(cls,"setCardId","(Ljava/lang/String;)V");
    env->CallVoidMethod(obj,mid,st);
    checkForException();
    delete cardid;
}

// -----------------------------------------------------

JNIEXPORT void JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_ctReadBankData
  (JNIEnv *env, jobject obj)
{
    javaEnv=env;
    
    jclass cls=env->GetObjectClass(obj);
    jmethodID mid=env->GetMethodID(cls,"getEntryIdx","()I");
    long entryidx=(long)env->CallIntMethod(obj,mid);
    checkForException();
    
    // reading institute data from card
    HBCI_BankData      *data=new HBCI_BankData;
    if (!DDV_readBankData(entryidx,data)) {
        error(env,"error while reading institute data from chipcard");
        return;
    }
    
    // storing institute data in object
    jstring st=env->NewStringUTF((char*)data->country);
    cls=env->FindClass("org/kapott/hbci/datatypes/SyntaxCtr");
    mid=env->GetStaticMethodID(cls,"getName","(Ljava/lang/String;)Ljava/lang/String;");
    jstring jname=(jstring)env->CallStaticObjectMethod(cls,mid,st);
    checkForException();

    cls=env->GetObjectClass(obj);
    mid=env->GetMethodID(cls,"setCountry","(Ljava/lang/String;)V");
    env->CallVoidMethod(obj,mid,jname);
    checkForException();
    
    st=env->NewStringUTF((char*)data->blz);
    mid=env->GetMethodID(cls,"setBLZ","(Ljava/lang/String;)V");
    env->CallVoidMethod(obj,mid,st);
    checkForException();
    
    st=env->NewStringUTF((char*)data->commaddr);
    mid=env->GetMethodID(cls,"setHost","(Ljava/lang/String;)V");
    env->CallVoidMethod(obj,mid,st);
    checkForException();
    
    st=env->NewStringUTF((char*)data->userid);
    mid=env->GetMethodID(cls,"setUserId","(Ljava/lang/String;)V");
    env->CallVoidMethod(obj,mid,st);
    checkForException();
    
    delete data;
}

// -----------------------------------------------------

JNIEXPORT void JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_ctReadKeyData
  (JNIEnv *env, jobject obj)
{
    javaEnv=env;
    
    // read signature id
    unsigned short int sigid=DDV_readSigId();
    if (sigid==(unsigned short int)0xFFFF) {
        error(env,"error while reading signature id");
        return;
    }
    
    // saving signature id
    jlong sigid_current=(jlong)sigid;
    jclass cls=env->FindClass("java/lang/Long");
    jmethodID mid=env->GetMethodID(cls,"<init>","(J)V");
    jobject jsigid=env->NewObject(cls,mid,sigid_current);
    checkForException();
    cls=env->GetObjectClass(obj);
    mid=env->GetMethodID(cls,"setSigId","(Ljava/lang/Long;)V");
    env->CallVoidMethod(obj,mid,jsigid);
    checkForException();

    // reading key data from chipcard
    HBCI_KeyInfo **keydata=new HBCI_KeyInfo*[2];
    size_t       size;
    if (!DDV_readKeyData(keydata,&size) || size!=2) {
        error(env,"error while reading key information from chipcard");
        return;
    }
    
    // creating hbcikey objects for current passport
    cls=env->GetObjectClass(obj);
    mid=env->GetMethodID(cls,"getCountry","()Ljava/lang/String;");
    jstring jcountry=(jstring)env->CallObjectMethod(obj,mid);
    checkForException();
    
    mid=env->GetMethodID(cls,"getBLZ","()Ljava/lang/String;");
    jstring jblz=(jstring)env->CallObjectMethod(obj,mid);
    checkForException();

    mid=env->GetMethodID(cls,"getUserId","()Ljava/lang/String;");
    jstring juserid=(jstring)env->CallObjectMethod(obj,mid);
    checkForException();

    char keynum[5];
    char keyversion[5];
    
    sprintf(keynum,"%i",keydata[0]->keynum);
    sprintf(keyversion,"%i",keydata[0]->keyversion);
    cls=env->FindClass("org/kapott/hbci/manager/HBCIKey");
    mid=env->GetMethodID(cls,"<init>","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/security/Key;)V");
    jobject key=env->NewObject(cls,mid,
                               jcountry,
                               jblz,
                               juserid,
                               env->NewStringUTF(keynum),
                               env->NewStringUTF(keyversion),
                               (jobject)(NULL));
    checkForException();
    cls=env->GetObjectClass(obj);
    mid=env->GetMethodID(cls,"setInstSigKey","(Lorg/kapott/hbci/manager/HBCIKey;)V");
    env->CallVoidMethod(obj,mid,key);
    checkForException();

    sprintf(keynum,"%i",keydata[1]->keynum);
    sprintf(keyversion,"%i",keydata[1]->keyversion);
    cls=env->FindClass("org/kapott/hbci/manager/HBCIKey");
    mid=env->GetMethodID(cls,"<init>","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/security/Key;)V");
    key=env->NewObject(cls,mid,
                       jcountry,
                       jblz,
                       juserid,
                       env->NewStringUTF(keynum),
                       env->NewStringUTF(keyversion),
                       (jobject)(NULL));
    checkForException();
    cls=env->GetObjectClass(obj);
    mid=env->GetMethodID(cls,"setInstEncKey","(Lorg/kapott/hbci/manager/HBCIKey;)V");
    env->CallVoidMethod(obj,mid,key);
    checkForException();
    
    delete keydata[0];
    delete keydata[1];
    delete keydata;
}

// -----------------------------------------------------

JNIEXPORT void JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_ctEnterPIN
  (JNIEnv *env, jobject obj)
{
    javaEnv=env;
    
    jclass cls=env->GetObjectClass(obj);
    jmethodID mid=env->GetMethodID(cls,"getUseSoftPin","()I");
    long useSoftPin=(long)(env->CallIntMethod(obj,mid));
    checkForException();

    mid=env->GetMethodID(cls,"getUseBio","()I");
    long usebio=(long)(env->CallIntMethod(obj,mid));
    checkForException();
    
    if (useSoftPin!=0 && useSoftPin!=1) {
        CTAPI_log("auto detecting chipcard keypad availability");  
        unsigned short int fus=BCS_requestFunctionalUnits();
        useSoftPin=(fus&BCS_HAS_FU_KEYBD)?0:1;
        
        char temp[100];
        sprintf(temp,"using softpin: %s",(useSoftPin==0)?"no":"yes");
        CTAPI_log(temp);
    }
    
    if (usebio!=0 && usebio!=1) {
        CTAPI_log("auto detecting chipcard biometrics availability");  
        unsigned short int fus=BCS_requestFunctionalUnits();
        usebio=(fus&BCS_HAS_FU_BIO_FINGER)?1:0;
        
        char temp[100];
        sprintf(temp,"using bio: %s",(usebio==0)?"no":"yes");
        CTAPI_log(temp);
    }
    
    if (useSoftPin==0) {
        if (!DDV_verifyHBCIPin(usebio==1)) {
            error(env,"error while entering PIN");
        }
    } else {
        cls=env->GetObjectClass(obj);
        mid=env->GetMethodID(cls,"getSoftPin","()[B");
        jbyteArray ba=(jbyteArray)(env->CallObjectMethod(obj,mid));
        checkForException();
        
        int size=env->GetArrayLength(ba);
        unsigned char softpin[13];
        env->GetByteArrayRegion(ba,(jsize)(0),(jsize)(size),(jbyte*)(softpin));
        softpin[size]=0x00;
        
        if (!DDV_verifyHBCIPin(softpin)) {
            error(env,"error while verifying PIN");
        }
    }
}

// -----------------------------------------------------

JNIEXPORT void JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_ctSaveBankData
  (JNIEnv *env, jobject obj)
{
    javaEnv=env;
    
    jclass cls=env->GetObjectClass(obj);
    jmethodID mid=env->GetMethodID(cls,"getEntryIdx","()I");
    long entryidx=(long)env->CallIntMethod(obj,mid);
    checkForException();
    
    HBCI_BankData *entry=new HBCI_BankData;
    if (!DDV_readBankData(entryidx,entry)) {
        error(env,"error while reading bank data from card");
        return;
    }
    
    cls=env->GetObjectClass(obj);
    mid=env->GetMethodID(cls,"getCountry","()Ljava/lang/String;");
    jstring jcountryname=(jstring)env->CallObjectMethod(obj,mid);
    checkForException();
    
    cls=env->FindClass("org/kapott/hbci/datatypes/SyntaxCtr");
    mid=env->GetStaticMethodID(cls,"getCode","(Ljava/lang/String;)Ljava/lang/String;");
    jstring jcountrycode=(jstring)env->CallStaticObjectMethod(cls,mid,jcountryname);
    checkForException();
    
    const char *st=env->GetStringUTFChars(jcountrycode,NULL);
    strcpy((char*)entry->country,st);
    env->ReleaseStringUTFChars(jcountrycode,st);
    
    cls=env->GetObjectClass(obj);
    mid=env->GetMethodID(cls,"getBLZ","()Ljava/lang/String;");
    jstring jblz=(jstring)env->CallObjectMethod(obj,mid);
    checkForException();
    
    st=env->GetStringUTFChars(jblz,NULL);
    strcpy((char*)entry->blz,st);
    env->ReleaseStringUTFChars(jblz,st);
    
    mid=env->GetMethodID(cls,"getHost","()Ljava/lang/String;");
    jstring jhost=(jstring)env->CallObjectMethod(obj,mid);
    checkForException();
    
    st=env->GetStringUTFChars(jhost,NULL);
    strcpy((char*)entry->commaddr,st);
    env->ReleaseStringUTFChars(jhost,st);
    
    mid=env->GetMethodID(cls,"getUserId","()Ljava/lang/String;");
    jstring juserid=(jstring)env->CallObjectMethod(obj,mid);
    checkForException();
    
    st=env->GetStringUTFChars(juserid,NULL);
    strcpy((char*)entry->userid,st);
    env->ReleaseStringUTFChars(juserid,st);
    
    if (!DDV_writeBankData(entryidx,entry)) {
        error(env,"error while storing bank data on card");
    }

    delete entry;
}

// -----------------------------------------------------

JNIEXPORT void JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_ctSaveSigId
  (JNIEnv *env, jobject obj)
{
    javaEnv=env;
    
    // getting current sigid from object
    jclass cls=env->GetObjectClass(obj);
    jmethodID mid=env->GetMethodID(cls,"getSigId","()Ljava/lang/Long;");
    jobject sigid_o=env->CallObjectMethod(obj,mid);
    checkForException();
    
    cls=env->GetObjectClass(sigid_o);
    mid=env->GetMethodID(cls,"longValue","()J");
    long sigid=(long)env->CallLongMethod(sigid_o,mid);
    checkForException();
    
    if (!DDV_writeSigId((unsigned short int)sigid)) {
        error(env,"error while saving new sigid to chipcard");
    }
}

// -----------------------------------------------------

JNIEXPORT jbyteArray JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_ctSign
  (JNIEnv *env, jobject, jbyteArray data)
{
    javaEnv=env;
    
    // storing data to be signed into local array
    jbyte *jhash=env->GetByteArrayElements(data,NULL);
    jsize len=env->GetArrayLength(data);
    unsigned char *hash=new unsigned char[len];
    for (int i=0;i<len;i++)
        hash[i]=jhash[i];

    unsigned char signature[8];
    size_t        siglen;
    if (!DDV_signData(hash,&siglen,signature)) {
        error(env,"error while signing data");
        return NULL;
    }
    
    // creating return value
    jbyteArray ba=env->NewByteArray((jsize)(8));
    env->SetByteArrayRegion(ba,(jsize)(0),(jsize)(8),(jbyte*)(signature));

    delete hash;
    return ba;
}

// -----------------------------------------------------

JNIEXPORT jobjectArray JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_ctEncrypt
  (JNIEnv *env, jobject obj)
{
    javaEnv=env;
    
    jbyte *plainkey=new jbyte[16];
    jbyte *enckey=new jbyte[16];

    // getting keynum for encryption key
    jclass cls=env->GetObjectClass(obj);
    jmethodID mid=env->GetMethodID(cls,"getInstEncKeyNum","()Ljava/lang/String;");
    jstring jst=(jstring)(env->CallObjectMethod(obj,mid));
    checkForException();
    
    const char *st=env->GetStringUTFChars(jst,NULL);
    unsigned char keynum=(unsigned char)atoi(st);
    env->ReleaseStringUTFChars(jst,st);

    if (!DDV_getEncryptionKeys(keynum,(unsigned char*)plainkey,(unsigned char*)enckey)) {
        error(env,"error while getting keys for encryption");
        return NULL;
    }
    
    // storing plain key and encrypted key in arrays
    jbyteArray plaina=env->NewByteArray((jsize)(16));
    env->SetByteArrayRegion(plaina,(jsize)(0),(jsize)(16),plainkey);
    jbyteArray enca=env->NewByteArray((jsize)(16));
    env->SetByteArrayRegion(enca,(jsize)(0),(jsize)(16),enckey);

    // creating array of arrays to return keys
    jobjectArray reta=env->NewObjectArray((jsize)(2),env->GetObjectClass(plaina),(jobject)(NULL));
    env->SetObjectArrayElement(reta,(jsize)(0),plaina);
    env->SetObjectArrayElement(reta,(jsize)(1),enca);
    
    delete plainkey;
    delete enckey;

    return reta;
}

// -----------------------------------------------------

JNIEXPORT jbyteArray JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_ctDecrypt
  (JNIEnv *env, jobject obj, jbyteArray enckeya)
{
    javaEnv=env;
    
    // getting keynum of encryption key
    jclass cls=env->GetObjectClass(obj);
    jmethodID mid=env->GetMethodID(cls,"getInstEncKeyNum","()Ljava/lang/String;");
    jstring jst=(jstring)(env->CallObjectMethod(obj,mid));
    checkForException();
    
    const char *st=env->GetStringUTFChars(jst,NULL);
    unsigned char keynum=(unsigned char)atoi(st);
    env->ReleaseStringUTFChars(jst,st);

    unsigned char *enckey=(unsigned char*)(env->GetByteArrayElements(enckeya,NULL));
    unsigned char *plainkey=new unsigned char[16];

    if (!DDV_decryptKey(keynum,enckey,plainkey)) {
        return NULL;
    }

    jbyteArray plaina=env->NewByteArray((jsize)(16));
    env->SetByteArrayRegion(plaina,(jsize)(0),(jsize)(16),(jbyte*)(plainkey));
    delete plainkey;
    
    return plaina;
}

// -----------------------------------------------------

JNIEXPORT void JNICALL Java_org_kapott_hbci_passport_HBCIPassportDDV_closeCT
  (JNIEnv *env, jobject)
{
    javaEnv=env;
    
    BCS_ejectCard(NULL,1,BCS_EJECT_KEEP,BCS_EJECT_DONT_BLINK,BCS_EJECT_DONT_BEEP);
    BCS_resetCT();
    CTAPI_closeCTAPI();
}

#ifdef __cplusplus
}
#endif
