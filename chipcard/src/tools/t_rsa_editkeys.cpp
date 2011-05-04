
/*  $Id: t_rsa_editkeys.cpp,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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

#include <stdio.h>
#include <string.h>

#include "ctapi-tools.h"
#include "bcs.h"
#include "seccos.h"
#include "rsacard.h"
#include "hbci.h"
#include "tools.h"

unsigned char* cardid;

void ctapi_log(const char *msg)
{
    // printf("%s\n",msg);
}

bool initialize(void)
{
    // kartentyp verifizieren
    unsigned short cardtype=HBCI_getCardType();
    if (cardtype!=HBCI_CARD_TYPE_RSA) {
        fprintf(stderr, "this is not an RSA card\n");
        return false;
    }
    
    // auslesen der kartennummer
    cardid=RSA_getCardNumber();
    char* st=bytes2hex(5,cardid);
    printf("RSA_getCardNumber(): %s\n",st);
    delete[] st;
    
    // selektieren von a600
    SECCOS_selectRoot(SECCOS_SELECT_RET_NOTHING);
    if (!SECCOS_selectDF(SECCOS_SELECT_RET_NOTHING, RSA_DF_BANKING)) {
        fprintf(stderr, "could not select RSA-DF\n");
        return false;
    }
    
    // PIN eingeben
    if (SECCOS_isPinInitialized(RSA_PIN_CH, SECCOS_PWD_TYPE_DF)) {
        printf("CH-PIN status ok - initial cardholder PIN has already been set\n");
    } else {
        printf("initial cardholder PIN has not been set!\n");
        printf("new CH PIN: ");
        unsigned char pin[9];
        scanf("%8s",pin);
        
        // TODO: hier auch CT-keypad unterstützen
        if (RSA_modifyPin(RSA_PIN_CH,5,cardid,strlen((const char*)pin),pin)) {
            printf("modifying CH pin successful\n");
        } else {
            fprintf(stderr,"error while modifying CH pin\n");
            return false;
        }
    }
    
    // CH PIN verifizieren
    // TODO: hier optional CT-keypad bzw. bio benutzen
    printf("current CH PIN: ");
    unsigned char pin_input[9];
    scanf("%8s",pin_input);
    
    if (RSA_verifyPin(RSA_PIN_CH, strlen((const char*)pin_input), pin_input)) {
        printf("cardholder PIN ok.\n");
    } else {
        fprintf(stderr,"error while verifying cardholder pin!\n");
        return false;
    }
    
    // optional EG-PIN initialisieren
    if (SECCOS_isPinInitialized(RSA_PIN_EG, SECCOS_PWD_TYPE_DF)) {
        printf("EG-PIN status ok - initial E/G PIN has already been set\n");
    } else {
        printf("initial E/G PIN has not been set!\n");
        
        if (RSA_modifyPin(RSA_PIN_EG,5,cardid,5,cardid)) {
            printf("modifying E/G pin successful\n");
        } else {
            fprintf(stderr,"error while modifying E/G pin\n");
            return false;
        }
    }
    
    // EG PIN verifizieren
    // TODO: hier optional eingabe der e/g-pin ermöglichen
    if (RSA_verifyPin(RSA_PIN_EG, 5, cardid)) {
        printf("E/G PIN ok.\n");
    } else {
        fprintf(stderr, "error while verifying E/G pin!\n");
        return false;
    }
    
    return true;
}

void modifyPIN(void)
{
    printf("old CH PIN: ");
    unsigned char oldpin[9];
    scanf("%8s",oldpin);
    
    printf("new CH PIN: ");
    unsigned char newpin[9];
    scanf("%8s",newpin);
    
    // TODO: hier auch CT-keypad unterstützen
    if (RSA_modifyPin(RSA_PIN_CH,
                      strlen((const char*)oldpin),oldpin,
                      strlen((const char*)newpin),newpin)) 
    {
        printf("modifying CH pin successful\n");
    } else {
        fprintf(stderr,"error while modifying CH pin\n");
    }
}

void listAllEntries(void)
{
    unsigned char buffer[1024];
    size_t        len;
    
    // EF_KEYLOG lesen 
    if (!SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_KEY_LOG)) {
        fprintf(stderr, "error while selecting EG_KEYLOG\n");
        return;
    }
    
    // KEY_LOG lesen
    if (!SECCOS_readBinary(&len,buffer,1)) {
        fprintf(stderr, "error while reading KEY_LOG\n");
        return;
    }
    
    // anzahl der verwendeten und max. anzahl einträge lesen
    unsigned char nof_entries=(buffer[0]>>4)&0x07;
    printf("used nof entries: %i\n", nof_entries);
    
    // durch alle bank-einträge durchgehen
    for (int entry_idx=0;entry_idx<nof_entries;entry_idx++) {
        // ef_bnk lesen und ausgeben
        if (!SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_BNK)) {
            fprintf(stderr, "error while selecting EF_BNK\n");
            return;
        }
        
        if (!SECCOS_readRecord(entry_idx+1, buffer, &len)) {
            fprintf(stderr, "error while reading EF_BNK\n");
            return;
        }
        
        // country, blz, userid, commtype, commaddr, userid, sysid
        unsigned char blz[31];
        unsigned char userid[31];
        unsigned char commaddr[29];
        
        memcpy(blz,      buffer+3, 30);   blz[30]=0;
        memcpy(userid,   buffer+33, 30);  userid[30]=0;
        memcpy(commaddr, buffer+64, 28);  commaddr[28]=0;
        
        printf("entry #%i: blz=%s userid=%s commaddr=%s\n", 
               entry_idx+1,
               trim(blz,8),
               trim(userid,30),
               trim(commaddr,28));
    }
}

void printKeyInfo(unsigned char* buffer)
{
    char  status_st[128];
    switch (buffer[0]) {
        case 0x00: strcpy(status_st,"INI-Brief fehlt"); break;
        case 0x01: strcpy(status_st,"Schlüsselnummer oder -version fehlen"); break;
        case 0x02: strcpy(status_st,"Neuer Schlüssel im Puffer erzeugt"); break;
        case 0x07: strcpy(status_st,"Privater und öffentlicher Schlüssel neu geschrieben"); break;
        case 0x08: strcpy(status_st,"Inaktiv, kann neu beschrieben werden"); break;
        case 0x0a: strcpy(status_st,"Inaktiv, Schlüssel mit Zertifikat verwenden"); break;
        case 0x10: strcpy(status_st,"Aktiv"); break;
        default:   strcpy(status_st,"(unbekannt)"); break;
    }
    
    char* st=bytes2hex(1,buffer);
    printf("    status-byte: %s (%s)\n",st,status_st);
    delete[] st;
    
    printf("    schlüssel-typ: %s\n",buffer[1]==0x53?"Signierschlüssel":(buffer[1]==0x56?"Chiffrierschlüssel":"unbekannt"));
    
    char  st2[4];
    memcpy(st2,buffer+2,3);
    st2[3]=0;
    printf("    schlüsselnummer: %s\n",st2);
    
    memcpy(st2,buffer+5,3);
    st2[3]=0;
    printf("    schlüsselversion: %s\n",st2);
}

bool printKeyData(unsigned char kid, unsigned char nof_entries)
{
    unsigned char buffer[1024];
    size_t        len;
    
    int  idx=0;
    bool found=false;
    while (idx<nof_entries && !found) {
        if (!SECCOS_readBinary(&len,buffer,1+(121*idx),1)) {
            fprintf(stderr, "error while reading IPF\n");
            return false;
        }
        if (buffer[0]==kid) {
            found=true;
        } else {
            idx++;
        }
    }
    
    if (!found) {
        fprintf(stderr, "no info for key %02x in IPF\n", kid);
        return false;
    }
    
    if (!SECCOS_readBinary(&len,buffer,1+(121*idx),121)) {
        fprintf(stderr, "error while reading IPF\n");
        return false;
    }
    
    char* st;
    
    st=bytes2hex(1,buffer);
    printf("    KID: %s\n",st);
    delete[] st;
    
    st=bytes2hex(1,buffer+6);
    printf("    Algo-Byte: %s\n",st);
    delete[] st;
    printf("      %s\n", (buffer[6]&0x08)?"LSB->MSB":"MSB->LSB");
    printf("      DSA: %i\n", (buffer[6]&2)?1:0);
    printf("      RSA: %i\n", (buffer[6]&1)?1:0);
    
    st=bytes2hex(1,buffer+7);
    printf("    AKD-Byte: %s\n",st);
    delete[] st;
    printf("      Signature:              %i\n", (buffer[7]&16)?1:0);
    printf("      Verify:                 %i\n", (buffer[7]&8)?1:0);
    printf("      Read Public Key:        %i\n", (buffer[7]&4)?1:0);
    printf("      Decipher/Encipher:      %i\n", (buffer[7]&2)?1:0);
    printf("      Internal/External auth: %i\n", (buffer[7]&1)?1:0);
    
    unsigned char lenModulus=buffer[11+3];
    unsigned char lenExponent=buffer[11+5];
    unsigned char lenEmpty=buffer[11+7];
    
    st=bytes2hex(lenModulus,buffer+20);
    printf("    Modulus (%i bytes): %s\n", lenModulus, st);
    delete[] st;

    st=bytes2hex(lenExponent,buffer+20+lenModulus);
    printf("    Exponent (%i bytes): %s\n", lenExponent, st);
    delete[] st;

    st=bytes2hex(lenEmpty,buffer+20+lenModulus+lenExponent);
    printf("    Empty (%i bytes): %s\n", lenEmpty, st);
    delete[] st;
    
    return true;
}

void printEntryDetails(unsigned char num)
{
    unsigned char buffer[1024];
    size_t        len;
    
    printf("data for account #%i\n", num);
    
    // ef_bnk lesen und ausgeben
    if (!SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_BNK)) {
        fprintf(stderr, "error while selecting EF_BNK\n");
        return;
    }
    
    if (!SECCOS_readRecord(num, buffer, &len)) {
        fprintf(stderr, "error while reading EF_BNK\n");
        return;
    }
    
    // country, blz, userid, commtype, commaddr, userid, sysid
    char country[4];
    char blz[31];
    char userid[31];
    char commtype;
    char commaddr[29];
    char commaddradd[3];
    char userid_inst[31];
    char sysid[31];
    
    memcpy(country,     buffer+0, 3);    country[3]=0;
    memcpy(blz,         buffer+3, 30);   blz[30]=0;
    memcpy(userid,      buffer+33, 30);  userid[30]=0;
    memcpy(&commtype,   buffer+63, 1);
    memcpy(commaddr,    buffer+64, 28);  commaddr[28]=0;
    memcpy(commaddradd, buffer+92, 2);   commaddradd[2]=0;
    memcpy(userid_inst, buffer+94, 30);  userid_inst[30]=0;
    memcpy(sysid,       buffer+124, 30); sysid[30]=0;
    
    printf("bankdata:\n");
    printf("  country:     %s\n",country);
    printf("  blz:         %s\n",blz);
    printf("  userid:      %s\n",userid);
    printf("  commtype:    %hhi\n",commtype);
    printf("  commaddr:    %s\n",commaddr);
    printf("  commaddradd: %s\n",commaddradd);
    printf("  userid_inst: %s\n",userid_inst);
    printf("  sysid:       %s\n",sysid);
    
    // kunden-ids lesen
    if (!SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_KD_ID)) {
        fprintf(stderr, "error while selecting EF_KD_ID\n");
        return;
    }
    
    if (!SECCOS_readBinary(&len,buffer,1)) {
        fprintf(stderr, "error while reading EF_KD_ID\n");
        return;
    }
    
    // Anzahl der Kunden-ID-Einträge ermitteln
    unsigned char nof_entries=(buffer[0]>>4)&0x0F;
    
    // Anzahl der Einträge pro Bankverbindung ermitteln
    SECCOS_readBinary(&len,buffer,1,nof_entries);
    unsigned char skip_entries=0;
    unsigned char nof_entries2=0;
    
    // Offset für Beginn der Einträge für die aktuelle
    // Bankverbindung ermitteln
    for (int i=0;i<nof_entries;i++) {
        if (((buffer[i]>>4)&0x0F) == (num)) {
            nof_entries2=buffer[i]&0x0F;
            break;
        } else {
            skip_entries+=buffer[i]&0x0F;
        }
    }
    printf("  Anzahl Kunden-IDs für diese Bankverbindung: %i\n",nof_entries2);
    
    // Kunden-IDs anzeigen
    for (int i=0;i<nof_entries2;i++) {
        SECCOS_readBinary(&len,buffer,1+nof_entries+(30*skip_entries)+(30*i),30);
        buffer[30]=0x00;
        printf("    Kunden-ID #%i: %s\n",i+1,buffer);
    }

    // EF_KEYLOG lesen und schlüssel-infos anzeigen
    if (!SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_KEY_LOG)) {
        fprintf(stderr,"error while selecting EF_KEY_LOG\n");
        return;
    }
    
    if (!SECCOS_readBinary(&len,buffer,1+(32*(num-1)),32)) {
        fprintf(stderr,"error while reading EF_KEY_LOG\n");
        return;
    }
    
    printf("keyinfo:\n");
    printf("  public user enc key:\n");
    printKeyInfo(buffer);

    printf("  public user sig key:\n");
    printKeyInfo(buffer+8);
    
    printf("  public inst enc key:\n");
    printKeyInfo(buffer+16);
    
    printf("  public inst sig key:\n");
    printKeyInfo(buffer+24);
    
    // schlüssel-infos aus IPF lesen
    if (!SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_IPF)) {
        fprintf(stderr, "error while selecting EF_IPF\n");
    }
    
    if (!SECCOS_readBinary(&len,buffer,1)) {
        fprintf(stderr, "error while reading EF_IPF\n");
    }
    nof_entries=buffer[0];
    
    printf("keydata:\n");
    printf("  public user enc key:\n");
    if (!printKeyData(0x86+num-1, nof_entries)) {
        return;
    }

    printf("  public user sig key:\n");
    if (!printKeyData(0x81+num-1, nof_entries)) {
        return;
    }
    
    printf("  public inst enc key:\n");
    if (!printKeyData(0x96+num-1, nof_entries)) {
        return;
    }
    
    printf("  public inst sig key:\n");
    if (!printKeyData(0x91+num-1, nof_entries)) {
        return;
    }

    // EF_SEQ lesen und wert anzeigen
    if (!SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_SEQ)) {
        fprintf(stderr,"error while selecting EF_SEQ\n");
        return;
    }
    
    if (!SECCOS_readRecord(num,buffer,&len)) {
        fprintf(stderr,"error while reading EF_SEQ\n");
        return;
    }
    
    unsigned long seq=(((unsigned long)buffer[0])<<24) |
                      (((unsigned long)buffer[1])<<16) |
                      (((unsigned long)buffer[2])<<8) |
                      (((unsigned long)buffer[3])<<0);
    printf("aktueller Sequenzzähler: %li\n",seq);
}

/* Kommandos:
     - newpin: Neue CH-PIN für Karte setzen
     - list: Listet alle Bank-Einträge auf
     - details #idx: Zeigt Bank-Daten und Schlüsseldetails zu dem gewählten
                     Eintrag (Bank-Daten, Schlüssel, SEQ)
     - edit #idx: Bearbeiten von Bank-Daten #idx
     - instkeys #idx: Bankschlüssel für Eintrag #idx bearbeiten
     - userkeys #idx: Schlüssel des Nutzers bearbeiten
 */
int main(int argc, char** argv)
{
    if (argc < 4) {
        fprintf(stderr, "usage: t_rsa_editkeys <CTAPI_LIB> <PORTNUM> <COMMAND> [<ARGS>]\n");
        exit(1);
    }
    
    if (!CTAPI_initCTAPI(ctapi_log,argv[1],atoi(argv[2]),0)) {
        fprintf(stderr,"error while initializing CTAPI lib\n");
        exit(1);
    }
    
    // resetting CT
    if (!BCS_resetCT()) {
        exit(1);
    }
    
    // requesting and reset card
    if (!BCS_requestCard("Karte rein",10)) {
        exit(1);
    }
    
    // TODO: hier gehts los
    if (initialize()) {
        char* command=argv[3];
        
        if (!strcmp(command,"newpin")) {
            modifyPIN();
        } else if (!strcmp(command,"list")) {
            listAllEntries();
        } else if (!strcmp(command,"details")) {
            if (argc < 5) {
                fprintf(stderr, "usage: t_rsa_editkeys <CTAPI_LIB> <PORTNUM> details #idx\n");
            } else {
                unsigned char num = atoi(argv[4]);
                printEntryDetails(num);
            }
        // TODO: edit
        // TODO: instkeys
        // TODO: userkeys
        } else {
            fprintf(stderr, "unknown command: %s\n", command);
        }
    }
    
    // ejecting card
    BCS_ejectCard("Karte raus",10,BCS_EJECT_KEEP,BCS_EJECT_DONT_BLINK,BCS_EJECT_DONT_BEEP);
    
    if (!CTAPI_closeCTAPI()) {
        exit(1);
    }
    
    exit(0);
}
