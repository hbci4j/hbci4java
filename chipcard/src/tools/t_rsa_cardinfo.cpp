
/*  $Id: t_rsa_cardinfo.cpp,v 1.1 2011/05/04 22:37:58 willuhn Exp $

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
#include "hbci.h"
#include "rsacard.h"
#include "tools.h"

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
    printf("    schlüsselnummer: %s\n",st2);
}

void printKeyData(unsigned int bufferLen,unsigned char* buffer,unsigned char kid)
{
    unsigned int posi=0;
    bool         found=false;
    
    while (posi<bufferLen && !found) {
        if (buffer[posi]==kid) {
            found=true;
            break;
        }
        posi+=121;
    }
    
    if (found) {
        char* st;
        
        st=bytes2hex(1,buffer+posi);
        printf("    KID: %s\n",st);
        delete[] st;
        
        st=bytes2hex(1,buffer+posi+6);
        printf("    Algo-Byte: %s\n",st);
        delete[] st;
        printf("      %s\n", (buffer[posi+6]&0x08)?"LSB->MSB":"MSB->LSB");
        printf("      DSA: %i\n", (buffer[posi+6]&2)?1:0);
        printf("      RSA: %i\n", (buffer[posi+6]&1)?1:0);
        
        st=bytes2hex(1,buffer+posi+7);
        printf("    AKD-Byte: %s\n",st);
        delete[] st;
        printf("      Signature:              %i\n", (buffer[posi+7]&16)?1:0);
        printf("      Verify:                 %i\n", (buffer[posi+7]&8)?1:0);
        printf("      Read Public Key:        %i\n", (buffer[posi+7]&4)?1:0);
        printf("      Decipher/Encipher:      %i\n", (buffer[posi+7]&2)?1:0);
        printf("      Internal/External auth: %i\n", (buffer[posi+7]&1)?1:0);
        
        unsigned char lenModulus=buffer[posi+11+3];
        unsigned char lenExponent=buffer[posi+11+5];
        unsigned char lenEmpty=buffer[posi+11+7];
        
        st=bytes2hex(lenModulus,buffer+posi+20);
        printf("    Modulus (%i bytes): %s\n", lenModulus, st);
        delete[] st;

        st=bytes2hex(lenExponent,buffer+posi+20+lenModulus);
        printf("    Exponent (%i bytes): %s\n", lenExponent, st);
        delete[] st;

        st=bytes2hex(lenEmpty,buffer+posi+20+lenModulus+lenExponent);
        printf("    Empty (%i bytes): %s\n", lenEmpty, st);
        delete[] st;
    } else {
        printf("    (nicht gefunden)\n");
    }
}

int main(int argc,char **argv)
{
    if (argc!=3) {
        printf("usage: t_rsa_cardinfo <CTAPI_LIB> <PORTNUM>\n");
        exit(1);
    }
    
    if (!CTAPI_initCTAPI(NULL,argv[1],atoi(argv[2]),0)) {
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
    
    // detect cardtype
    CTAPI_ERROR    save_error=CTAPI_error;
    char           nof_hisBytes;
    unsigned short cardtype=HBCI_getCardType();
    
    switch (cardtype) {
        case HBCI_CARD_TYPE_DDV_0:
            printf("this is a DDV-0-card\n");
            break;
        case HBCI_CARD_TYPE_DDV_1:
            printf("this is a DDV-1-card\n");
            break;
        case HBCI_CARD_TYPE_RSA:
            printf("this is a RSA-card\n");
            
            nof_hisBytes=save_error.response[1]&0x0F;
            if (!strncmp((char*)save_error.response+save_error.resLen-3-nof_hisBytes,"SPK23",5)) {
                printf("  OS is SPK 2.3 (STARCOS)\n");
            } else {
                printf("  unknown OS\n");
            }
            break;
        default:
            printf("could not determine card type\n");
            break;
    }
    
    // this is only for rsa cards
    if (cardtype==HBCI_CARD_TYPE_RSA) {
        size_t         len;
        unsigned char* buffer=new unsigned char[4096];
        
        unsigned char* cardid=RSA_getCardNumber();
        char*          st=bytes2hex(5,cardid);
        printf("RSA_getCardNumber(): %s\n",st);
        delete[] st;
        
        // DF_BANKING selektieren
        SECCOS_selectRoot(SECCOS_SELECT_RET_NOTHING);
        if (SECCOS_selectDF(SECCOS_SELECT_RET_NOTHING, RSA_DF_BANKING)) {
            printf("selected DF_BANKING\n");

            // optional CH-PIN initialisieren
            if (SECCOS_isPinInitialized(RSA_PIN_CH, SECCOS_PWD_TYPE_DF)) {
                printf("initial cardholder PIN has already been set\n");
            } else {
                printf("initial cardholder PIN has not been set!\n");
                printf("new CH PIN: ");
                unsigned char pin[9];
                scanf("%8s",pin);
                
                // TODO: hier auch CT-keypad unterstützen
                if (RSA_modifyPin(RSA_PIN_CH,5,cardid,strlen((const char*)pin),pin)) {
                    printf("modifying CH pin successful\n");
                } else {
                    printf("error while modifying CH pin\n");
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
                printf("error while verifying cardholder pin!\n");
            }
            
            // optional EG-PIN initialisieren
            if (SECCOS_isPinInitialized(RSA_PIN_EG, SECCOS_PWD_TYPE_DF)) {
                printf("initial E/G PIN has already been set\n");
            } else {
                printf("initial E/G PIN has not been set!\n");
                
                if (RSA_modifyPin(RSA_PIN_EG,5,cardid,5,cardid)) {
                    printf("modifying E/G pin successful\n");
                } else {
                    printf("error while modifying E/G pin\n");
                }
            }
            
            // EG PIN verifizieren
            // TODO: hier optional eingabe der e/g-pin ermöglichen
            if (RSA_verifyPin(RSA_PIN_EG, 5, cardid)) {
                printf("E/G PIN ok.\n");
            } else {
                printf("error while verifying E/G pin!\n");
            }
            
            
            // ICC_INFO selektieren
            SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_ICC_INFO);
            if (CTAPI_isOK(CTAPI_error.status)) {
                // ICC_INFO lesen
                if (SECCOS_readBinary(&len,buffer)) {
                    char version[4];
                    memcpy(version, buffer, 3);
                    version[3]=0x00;
                    printf("Unterstützte HBCI-Version: %s\n",version);
                    
                    printf("Unterstützte Signatur-Algorithmen:\n");
                    printf("  Incoming: %s, %s\n", buffer[3]&0x10?"ISO ohne":"", buffer[3]&0x20?"ISO mit":"");
                    printf("  Outgoing: %s, %s\n", buffer[3]&0x01?"ISO ohne":"", buffer[3]&0x02?"ISO mit":"");
                    
                    printf("Status PIN_EG: %s\n", buffer[4]==0x00?"initial value":(buffer[4]==0x01?"individal":"individual, public"));
                } else {
                    printf("error while reading ICC_STATUS\n");
                }
            } else {
                printf("error while selecting ICC_INFO\n");
            }
            

            // EF_KEYLOG lesen 
            unsigned char nof_entries=0;
            SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_KEY_LOG);
            if (CTAPI_isOK(CTAPI_error.status)) {
                // KEY_LOG lesen
                if (SECCOS_readBinary(&len,buffer,1)) {
                    // anzahl der verwendeten und max. anzahl einträge lesen
                    nof_entries=(buffer[0]>>4)&0x07;
                    printf("max  nof inst. entries: %i\n", buffer[0]&0x07);
                    printf("used nof inst. entries: %i\n", nof_entries);
                    
                    // buffer-status anzeigen
                    printf("sig-buffer used: %i\n",  buffer[0]&0x80?1:0);
                    printf("enc-buffer used: %i\n",  buffer[0]&0x08?1:0);
                } else {
                    printf("error while reading KEY_LOG\n");
                }
            } else {
                printf("error while selecting KEY_LOG\n");
            }
            
            // für jede bankverbindung:
            int entry_idx;
            for (entry_idx=0;entry_idx<nof_entries;entry_idx++) {
                printf("data for account #%i\n", (entry_idx+1));
                
                // ef_bnk lesen und ausgeben
                if (SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_BNK)) {
                    if (SECCOS_readRecord(entry_idx+1, buffer, &len)) {
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
                        
                        printf("  country:     %s\n",country);
                        printf("  blz:         %s\n",blz);
                        printf("  userid:      %s\n",userid);
                        printf("  commtype:    %hhi\n",commtype);
                        printf("  commaddr:    %s\n",commaddr);
                        printf("  commaddradd: %s\n",commaddradd);
                        printf("  userid_inst: %s\n",userid_inst);
                        printf("  sysid:       %s\n",sysid);
                    } else {
                        printf("  error while reading EF_BNK\n");
                    }
                } else {
                    printf("  error while selecting EF_BNK\n");
                }
                
                // kunden-ids lesen
                if (SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_KD_ID)) {
                    if (SECCOS_readBinary(&len,buffer,1)) {
                        // Anzahl der Kunden-ID-Einträge ermitteln
                        unsigned char nof_entries=(buffer[0]>>4)&0x0F;
                        printf("  Anzahl Kunden-IDs gesamt: %i\n", nof_entries);
                        printf("  Anzahl Kunden-IDs max.:   %i\n", (buffer[0]>>0)&0x0F);
                        
                        // Anzahl der Einträge pro Bankverbindung ermitteln
                        SECCOS_readBinary(&len,buffer,1,nof_entries);
                        unsigned char nof_entries2=0;
                        unsigned char skip_entries=0;
                        
                        // Offset für Beginn der Einträge für die aktuelle
                        // Bankverbindung ermitteln
                        for (int i=0;i<nof_entries;i++) {
                            if (((buffer[i]>>4)&0x0F) == (entry_idx+1)) {
                                nof_entries2=buffer[i]&0x0F;
                                break;
                            } else {
                                skip_entries+=buffer[i]&0x0F;
                            }
                        }
                        printf("  Anzahl Kunden-IDs für diese Bankverbindung: %i (to skip:%i)\n",nof_entries2,skip_entries);
                        
                        // Kunden-IDs anzeigen
                        for (int i=0;i<nof_entries2;i++) {
                            SECCOS_readBinary(&len,buffer,1+nof_entries+(30*skip_entries)+(30*i),30);
                            buffer[30]=0x00;
                            printf("    Kunden-ID #%i: %s\n",i+1,buffer);
                        }
                    } else {
                        printf("  error while reading EF_KD_ID\n");
                    }
                } else {
                    printf("  error while selecting EF_KD_ID\n");
                }
                

                // EF_KEYLOG lesen und schlüssel-infos anzeigen
                if (SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_KEY_LOG)) {
                    if (SECCOS_readBinary(&len,buffer,1+(32*entry_idx),32)) {
                        printf("  public user enc key:\n");
                        printKeyInfo(buffer);

                        printf("  public user sig key:\n");
                        printKeyInfo(buffer+8);
                        
                        printf("  public inst enc key:\n");
                        printKeyInfo(buffer+16);
                        
                        printf("  public inst sig key:\n");
                        printKeyInfo(buffer+24);
                    } else {
                        printf("  error while reading EF_KEY_LOG\n");
                    }
                } else {
                    printf("  error while selecting EF_KEY_LOG\n"); 
                }
                
                
                // schlüssel-infos aus IPF lesen
                if (SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_IPF)) {
                    if (SECCOS_readBinary(&len,buffer,1)) {
                        unsigned int nof_entries=buffer[0];
                        printf("  Anzahl der Schlüssel im IPF: %i\n", nof_entries);
                        
                        unsigned int bufferLen=nof_entries*121;
                        unsigned int readPosi=1;
                        unsigned int writePosi=0;
                        unsigned int restSize=bufferLen;
                        while (restSize>0) {
                            SECCOS_readBinary(&len,buffer+writePosi,readPosi,121);
                            readPosi+=121;
                            writePosi+=121;
                            restSize-=121;
                        }
                        
                        printf("  public user enc key:\n");
                        printKeyData(bufferLen,buffer,0x86+entry_idx);
    
                        printf("  public user sig key:\n");
                        printKeyData(bufferLen,buffer,0x81+entry_idx);
                        
                        printf("  public inst enc key:\n");
                        printKeyData(bufferLen,buffer,0x96+entry_idx);
                        
                        printf("  public inst sig key:\n");
                        printKeyData(bufferLen,buffer,0x91+entry_idx);
                    } else {
                        printf("  error while reading EF_IPF\n");
                    }
                } else {
                    printf("  error while selecting EF_IPF\n");
                }
    
    
                // EF_SEQ lesen und wert anzeigen
                if (SECCOS_selectSubFile(SECCOS_SELECT_RET_NOTHING, RSA_EF_SEQ)) {
                    if (SECCOS_readRecord(entry_idx+1,buffer,&len)) {
                        unsigned long seq=(((unsigned long)buffer[0])<<24) |
                                          (((unsigned long)buffer[1])<<16) |
                                          (((unsigned long)buffer[2])<<8) |
                                          (((unsigned long)buffer[3])<<0);
                        printf("  aktueller Sequenzzähler: %li\n",seq);
                    } else {
                        printf("  error while reading EF_SEQ\n");
                    }
                } else {
                    printf("  error while selecting EF_SEQ\n");
                }
            }
        } else {
            printf("error while selecting DF_BANKING\n");
        }

        delete[] cardid;
        delete[] buffer;
    }
    
    // ejecting card
    BCS_ejectCard("Karte raus",10,BCS_EJECT_KEEP,BCS_EJECT_DONT_BLINK,BCS_EJECT_DONT_BEEP);
    
    if (!CTAPI_closeCTAPI()) {
        exit(1);
    }
    
    exit(0);
}
