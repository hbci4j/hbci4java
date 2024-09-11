/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2024 Olaf Willuhn
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

package org.kapott.hbci4java.passport.pintan;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.HBCIPassportPinTan;
import org.kapott.hbci.passport.HBCIPassportPinTanMemory;
import org.kapott.hbci4java.callback.HBCICallbackTest;
import org.kapott.hbci4java.comm.CommDummy;

/**
 * Testet die Verwendung eines PIN/TAN-Passports.
 */
public class TestPassportPinTan
{
  private HBCIPassportPinTan passport = null;
  
  /**
   * Testet die Synchronisierung.
   * @throws Exception
   */
  @Test
  public void testSync() throws Exception
  {
    try (final HBCIHandler handler = new HBCIHandler("300",passport))
    {
      handler.sync(false);
    }
  }
  
  /**
   * Erzeugt das Passport-Objekt.
   * @throws Exception
   */
  @Before
  public void before() throws Exception
  {
    final HBCICallbackTest callback = new HBCICallbackTest();
    callback.put(HBCICallback.NEED_BLZ,"12345678");
    callback.put(HBCICallback.NEED_COUNTRY,"DE");
    callback.put(HBCICallback.NEED_HOST,"https://fi nts-demo bank.local/fints/test123");
    callback.put(HBCICallback.NEED_PORT,"443");
    callback.put(HBCICallback.NEED_FILTER,"Base64");
    callback.put(HBCICallback.NEED_USERID,"1234567890");
    callback.put(HBCICallback.NEED_CUSTOMERID,"1234567890");
    callback.put(HBCICallback.NEED_PT_PIN,"12345");
    callback.put(HBCICallback.NEED_CONNECTION,"");
    callback.put(HBCICallback.CLOSE_CONNECTION,"");
    callback.put(HBCICallback.NEED_PT_SECMECH,"921");
    callback.put(HBCICallback.NEED_PT_TANMEDIA,"foo");

    final Properties props = new Properties();
    props.put("log.loglevel.default",Integer.toString(HBCIUtils.LOG_INFO));
    
    HBCIUtils.init(props,callback);
    this.passport = new HBCIPassportPinTanMemory(null) {
      
      private CommDummy comm = null;
      
      /**
       * @see org.kapott.hbci.passport.AbstractPinTanPassport#getCommInstance()
       */
      @Override
      public Comm getCommInstance()
      {
        if (this.comm != null)
          return this.comm;
        
        // Hier sollte jetzt die URL korrigiert worden sein
        Assert.assertEquals("fints-demobank.local/fints/test123",this.getHost());
        
        this.comm = new CommDummy(this);
        return this.comm;
      }
    };
    
    final CommDummy comm = (CommDummy) this.passport.getComm();
    
    comm.addResponse("HNHBK:1:3+000000003547+300+3895648141472000VbiAvxlruyluWF+1+3895648141472000VbiAvxlruyluWF:1'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142903+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@3273@HIRMG:2:2+0020::Dialoginitialisierung erfolgreich.+3075::Starke Authentifizierung ab dem 2019-09-14 erforderlich.:2019-09-14+3060::Teilweise liegen Warnungen/Hinweise vor.'HIRMS:3:2:1+3099::Herzlich willkommen, Sie nutzen den FinTS Service, vielen Dank'HIRMS:4:2:3+0020::Information fehlerfrei entgegengenommen.'HIRMS:5:2:4+1040::BPD nicht mehr aktuell. Aktuelle Version folgt.+3920::Meldung unterstützter Ein- und Zwei-Schritt-Verfahren:920:921+0940::Letzte Anmeldung am 12.06.2024 - 10?:39?:16:12.06.2024 - 10?:39?:16'HIRMS:6:2:5+0020::Auftrag ausgeführt.'HIBPA:7:3:4+54+280:12345678+Testbank+0+1+300+9999'HIKOM:8:4:4+280:12345678+1+3:https?://fints-demobank.local/fints/test123::MIM:1'HIPINS:9:1:4+1+1+0+5:50:6:::HKCDN:J:HKDBS:N:HKDSA:J:HKCCM:J:HKBSA:J:HKBME:J:HKCSE:J:HKDSL:J:HKKAZ:N:HKCUM:J:HKCDL:J:HKCME:J:HKWPD:N:HKCDE:J:HKBML:J:HKDML:J:HKDSE:J:HKCSB:N:HKBSE:J:HKCSL:J:HKBBS:N:HKCML:J:HKIPZ:J:HKTAN:N:HKCUB:N:HKCCS:J:HKSAL:N:HKCDB:N:HKCMB:N:HKBSL:J:HKSPA:N:HKTAB:N:HKCSA:J:HKDMB:N:HKPRO:N:HKDME:J:DKPAE:J:HKPAE:J:HKBMB:N:HKIPS:N'DIPINS:10:1:4+1+1+HKCDN:J:HKDBS:N:HKDSA:J:HKCCM:J:HKBSA:J:HKBME:J:HKCSE:J:HKDSL:J:HKKAZ:N:HKCUM:J:HKCDL:J:HKCME:J:HKWPD:N:HKCDE:J:HKBML:J:HKDML:J:HKDSE:J:HKCSB:N:HKBSE:J:HKCSL:J:HKBBS:N:HKCML:J:HKIPZ:J:HKTAN:N:HKCUB:N:HKCCS:J:HKSAL:N:HKCDB:N:HKCMB:N:HKBSL:J:HKSPA:N:HKTAB:N:HKCSA:J:HKDMB:N:HKPRO:N:HKDME:J:DKPAE:J:HKBMB:N:HKIPS:N'HIPAES:11:1:4+1+1+0'DIPAES:12:1:4+1+1'HITANS:13:6:4+1+1+0+N:N:0:920:2:PushTan1:PushTan1::PushTan1:6:2:PushTan1:999:N:1:N:0:2:N:J:00:2:N:9'HITANS:14:7:4+1+1+0+N:N:0:921:2:PushTan1-Push:Decoupled::PushTan1-Push:::PushTan1-Push:999:N:1:N:0:2:N:J:00:2:N:9:60:5:5:N:J'HITABS:15:2:4+1+1+0'HITABS:16:4:4+1+1+0'HIPROS:17:3:4+1+1'HISPAS:18:1:4+1+1+0+J:N:J:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.003.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.09:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.02:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.08'HIKAZS:19:5:4+1+1+90:N:N'HIKAZS:20:6:4+1+1+0+90:N:N'HISALS:21:5:4+1+1'HISALS:22:6:4+1+1+0'HIWPDS:23:6:4+10+1+0+N:N:N'HICUBS:24:1:4+1+1+0+N'HICUMS:25:1:4+1+1+1+ACCT;CASH:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.09'HICCSS:26:1:4+1+1+0'HICSES:27:1:4+1+1+0+0:90'HICSBS:28:1:4+1+1+1+N:N'HICSAS:29:1:4+1+1+1+0:90'HICSLS:30:1:4+1+1+1+N'HICDES:31:1:4+1+1+1+1:1:360:0102030612:010203040506070809101112131415161718192021222324252627282930'HICDBS:32:1:4+1+1+1+N'HICDNS:33:1:4+1+1+1+9:1:360:J:J:J:J:J:J:J:J:J:0102030612:010203040506070809101112131415161718192021222324252627282930'HICDLS:34:1:4+1+1+1+0:0:N:N'HICCMS:35:1:4+1+1+0+500:J:J'HICMES:36:1:4+1+1+0+1:90:500:J:J'HICMBS:37:1:4+1+1+1+N:J'HICMLS:38:1:4+1+1+1'HIDSES:39:1:4+1+1+1+1:90:1:90'HIDBSS:40:1:4+1+1+1+N:N'HIDSAS:41:1:4+1+1+1+1:90:1:90'HIDSLS:42:1:4+1+1+1+J'HIBSES:43:1:4+20+1+1+1:90:1:90'HIBBSS:44:1:4+20+1+1+N:N'HIBSAS:45:1:4+20+1+1+1:90:1:90'HIBSLS:46:1:4+20+1+1+J'HIDMES:47:1:4+1+1+0+1:90:1:90:500:J:J'HIDMBS:48:1:4+1+1+1+N:J'HIDMLS:49:1:4+1+1+1'HIBMES:50:1:4+1+1+0+1:90:1:90:50:J:J'HIBMBS:51:1:4+1+1+1+N:J'HIBMLS:52:1:4+1+1+1'HIIPZS:53:1:4+990+1+1+:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03'HIIPSS:54:1:4+990+1+0+5'HISYN:55:4:5+3895648143636000SX1DL9ZRA5M9O8''HNHBS:56:1+1'");
    comm.addResponse("HNHBK:1:3+000000000349+300+3895648141472000VbiAvxlruyluWF+2+3895648141472000VbiAvxlruyluWF:2'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142917+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@78@HIRMG:2:2+0010::Nachricht entgegengenommen.'HIRMS:3:2:3+0100::Dialog beendet.''HNHBS:4:1+2'");
    comm.addResponse("HNHBK:1:3+000000003475+300+3895648157886000PfrjPCB1ePtMkq+1+3895648157886000PfrjPCB1ePtMkq:1'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142919+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@3201@HIRMG:2:2+0020::Dialoginitialisierung erfolgreich.+3076::Keine starke Authentifizierung erforderlich.+3060::Teilweise liegen Warnungen/Hinweise vor.'HIRMS:3:2:1+3099::Herzlich willkommen, Sie nutzen den FinTS Service, vielen Dank'HIRMS:4:2:3+0020::Information fehlerfrei entgegengenommen.'HIRMS:5:2:4+1040::BPD nicht mehr aktuell. Aktuelle Version folgt.+3920::Meldung unterstützter Ein- und Zwei-Schritt-Verfahren:920:921+0940::Letzte Anmeldung am 12.06.2024 - 10?:39?:16:12.06.2024 - 10?:39?:16'HIBPA:6:3:4+54+280:12345678+Testbank+0+1+300+9999'HIKOM:7:4:4+280:12345678+1+3:https?://fints-demobank.local/fints/test123::MIM:1'HIPINS:8:1:4+1+1+0+5:50:6:::HKSAL:N:HKCML:J:HKBMB:N:HKCMB:N:HKCSL:J:HKCDB:N:HKDSA:J:HKCUM:J:HKTAB:N:HKCUB:N:HKBME:J:HKBBS:N:HKCME:J:HKKAZ:N:HKDME:J:HKDMB:N:HKDBS:N:HKWPD:N:HKCSE:J:HKCCM:J:DKPAE:J:HKBSA:J:HKDML:J:HKCDL:J:HKBSE:J:HKDSE:J:HKIPZ:J:HKCCS:J:HKPRO:N:HKCSA:J:HKPAE:J:HKTAN:N:HKCDN:J:HKBML:J:HKDSL:J:HKSPA:N:HKBSL:J:HKCSB:N:HKIPS:N:HKCDE:J'DIPINS:9:1:4+1+1+HKSAL:N:HKCML:J:HKBMB:N:HKCMB:N:HKCSL:J:HKCDB:N:HKDSA:J:HKCUM:J:HKTAB:N:HKCUB:N:HKBME:J:HKBBS:N:HKCME:J:HKKAZ:N:HKDME:J:HKDMB:N:HKDBS:N:HKWPD:N:HKCSE:J:HKCCM:J:DKPAE:J:HKBSA:J:HKDML:J:HKCDL:J:HKBSE:J:HKDSE:J:HKIPZ:J:HKCCS:J:HKPRO:N:HKCSA:J:HKTAN:N:HKCDN:J:HKBML:J:HKDSL:J:HKSPA:N:HKBSL:J:HKCSB:N:HKIPS:N:HKCDE:J'HIPAES:10:1:4+1+1+0'DIPAES:11:1:4+1+1'HITANS:12:6:4+1+1+0+N:N:0:920:2:PushTan1:PushTan1::PushTan1:6:2:PushTan1:999:N:1:N:0:2:N:J:00:2:N:9'HITANS:13:7:4+1+1+0+N:N:0:921:2:PushTan1-Push:Decoupled::PushTan1-Push:::PushTan1-Push:999:N:1:N:0:2:N:J:00:2:N:9:60:5:5:N:J'HITABS:14:2:4+1+1+0'HITABS:15:4:4+1+1+0'HIPROS:16:3:4+1+1'HISPAS:17:1:4+1+1+0+J:N:J:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.003.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.09:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.02:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.08'HIKAZS:18:5:4+1+1+90:N:N'HIKAZS:19:6:4+1+1+0+90:N:N'HISALS:20:5:4+1+1'HISALS:21:6:4+1+1+0'HIWPDS:22:6:4+10+1+0+N:N:N'HICUBS:23:1:4+1+1+0+N'HICUMS:24:1:4+1+1+1+ACCT;CASH:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.09'HICCSS:25:1:4+1+1+0'HICSES:26:1:4+1+1+0+0:90'HICSBS:27:1:4+1+1+1+N:N'HICSAS:28:1:4+1+1+1+0:90'HICSLS:29:1:4+1+1+1+N'HICDES:30:1:4+1+1+1+1:1:360:0102030612:010203040506070809101112131415161718192021222324252627282930'HICDBS:31:1:4+1+1+1+N'HICDNS:32:1:4+1+1+1+9:1:360:J:J:J:J:J:J:J:J:J:0102030612:010203040506070809101112131415161718192021222324252627282930'HICDLS:33:1:4+1+1+1+0:0:N:N'HICCMS:34:1:4+1+1+0+500:J:J'HICMES:35:1:4+1+1+0+1:90:500:J:J'HICMBS:36:1:4+1+1+1+N:J'HICMLS:37:1:4+1+1+1'HIDSES:38:1:4+1+1+1+1:90:1:90'HIDBSS:39:1:4+1+1+1+N:N'HIDSAS:40:1:4+1+1+1+1:90:1:90'HIDSLS:41:1:4+1+1+1+J'HIBSES:42:1:4+20+1+1+1:90:1:90'HIBBSS:43:1:4+20+1+1+N:N'HIBSAS:44:1:4+20+1+1+1:90:1:90'HIBSLS:45:1:4+20+1+1+J'HIDMES:46:1:4+1+1+0+1:90:1:90:500:J:J'HIDMBS:47:1:4+1+1+1+N:J'HIDMLS:48:1:4+1+1+1'HIBMES:49:1:4+1+1+0+1:90:1:90:50:J:J'HIBMBS:50:1:4+1+1+1+N:J'HIBMLS:51:1:4+1+1+1'HIIPZS:52:1:4+990+1+1+:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03'HIIPSS:53:1:4+990+1+0+5'HITAN:54:7:5+4++noref+nochallenge''HNHBS:55:1+1'");
    comm.addResponse("HNHBK:1:3+000000000398+300+3895648157886000PfrjPCB1ePtMkq+2+3895648157886000PfrjPCB1ePtMkq:2'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142919+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@126@HIRMG:2:2+0010::Nachricht entgegengenommen.'HIRMS:3:2:3+0020::Auftrag ausgeführt.'HITAB:4:4:3+0+G:1:::::::::::PushTanMuster12''HNHBS:5:1+2'");
    comm.addResponse("HNHBK:1:3+000000000349+300+3895648157886000PfrjPCB1ePtMkq+3+3895648157886000PfrjPCB1ePtMkq:3'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142920+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@78@HIRMG:2:2+0010::Nachricht entgegengenommen.'HIRMS:3:2:3+0100::Dialog beendet.''HNHBS:4:1+3'");
    comm.addResponse("HNHBK:1:3+000000004374+300+3895648160439000VBOBN6j56L1rtO+1+3895648160439000VBOBN6j56L1rtO:1'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142921+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@4100@HIRMG:2:2+0020::Dialoginitialisierung erfolgreich.+3076::Keine starke Authentifizierung erforderlich.+3060::Teilweise liegen Warnungen/Hinweise vor.'HIRMS:3:2:1+3099::Herzlich willkommen, Sie nutzen den FinTS Service, vielen Dank'HIRMS:4:2:3+0020::Information fehlerfrei entgegengenommen.'HIRMS:5:2:4+1040::BPD nicht mehr aktuell. Aktuelle Version folgt.+3920::Meldung unterstützter Ein- und Zwei-Schritt-Verfahren:920:921+0940::Letzte Anmeldung am 12.06.2024 - 10?:39?:16:12.06.2024 - 10?:39?:16+1050::UPD nicht mehr aktuell. Aktuelle Version folgt.'HIBPA:6:3:4+54+280:12345678+Testbank+0+1+300+9999'HIKOM:7:4:4+280:12345678+1+3:https?://fints-demobank.local/fints/test123::MIM:1'HIPINS:8:1:4+1+1+0+5:50:6:::HKTAB:N:HKCSA:J:HKCUM:J:HKDBS:N:HKPAE:J:HKCDN:J:HKSPA:N:HKCDB:N:HKBSA:J:HKCUB:N:DKPAE:J:HKCDE:J:HKCCS:J:HKDSL:J:HKTAN:N:HKBME:J:HKIPZ:J:HKCDL:J:HKCME:J:HKCMB:N:HKBMB:N:HKCSE:J:HKBML:J:HKCSB:N:HKCML:J:HKDSA:J:HKWPD:N:HKDMB:N:HKCCM:J:HKKAZ:N:HKDME:J:HKDSE:J:HKIPS:N:HKDML:J:HKSAL:N:HKBSE:J:HKBBS:N:HKCSL:J:HKPRO:N:HKBSL:J'DIPINS:9:1:4+1+1+HKTAB:N:HKCSA:J:HKCUM:J:HKDBS:N:HKCDN:J:HKSPA:N:HKCDB:N:HKBSA:J:HKCUB:N:DKPAE:J:HKCDE:J:HKCCS:J:HKDSL:J:HKTAN:N:HKBME:J:HKIPZ:J:HKCDL:J:HKCME:J:HKCMB:N:HKBMB:N:HKCSE:J:HKBML:J:HKCSB:N:HKCML:J:HKDSA:J:HKWPD:N:HKDMB:N:HKCCM:J:HKKAZ:N:HKDME:J:HKDSE:J:HKIPS:N:HKDML:J:HKSAL:N:HKBSE:J:HKBBS:N:HKCSL:J:HKPRO:N:HKBSL:J'HIPAES:10:1:4+1+1+0'DIPAES:11:1:4+1+1'HITANS:12:6:4+1+1+0+N:N:0:920:2:PushTan1:PushTan1::PushTan1:6:2:PushTan1:999:N:1:N:0:2:N:J:00:2:N:9'HITANS:13:7:4+1+1+0+N:N:0:921:2:PushTan1-Push:Decoupled::PushTan1-Push:::PushTan1-Push:999:N:1:N:0:2:N:J:00:2:N:9:60:5:5:N:J'HITABS:14:2:4+1+1+0'HITABS:15:4:4+1+1+0'HIPROS:16:3:4+1+1'HISPAS:17:1:4+1+1+0+J:N:J:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.003.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.09:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.02:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.08'HIKAZS:18:5:4+1+1+90:N:N'HIKAZS:19:6:4+1+1+0+90:N:N'HISALS:20:5:4+1+1'HISALS:21:6:4+1+1+0'HIWPDS:22:6:4+10+1+0+N:N:N'HICUBS:23:1:4+1+1+0+N'HICUMS:24:1:4+1+1+1+ACCT;CASH:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.09'HICCSS:25:1:4+1+1+0'HICSES:26:1:4+1+1+0+0:90'HICSBS:27:1:4+1+1+1+N:N'HICSAS:28:1:4+1+1+1+0:90'HICSLS:29:1:4+1+1+1+N'HICDES:30:1:4+1+1+1+1:1:360:0102030612:010203040506070809101112131415161718192021222324252627282930'HICDBS:31:1:4+1+1+1+N'HICDNS:32:1:4+1+1+1+9:1:360:J:J:J:J:J:J:J:J:J:0102030612:010203040506070809101112131415161718192021222324252627282930'HICDLS:33:1:4+1+1+1+0:0:N:N'HICCMS:34:1:4+1+1+0+500:J:J'HICMES:35:1:4+1+1+0+1:90:500:J:J'HICMBS:36:1:4+1+1+1+N:J'HICMLS:37:1:4+1+1+1'HIDSES:38:1:4+1+1+1+1:90:1:90'HIDBSS:39:1:4+1+1+1+N:N'HIDSAS:40:1:4+1+1+1+1:90:1:90'HIDSLS:41:1:4+1+1+1+J'HIBSES:42:1:4+20+1+1+1:90:1:90'HIBBSS:43:1:4+20+1+1+N:N'HIBSAS:44:1:4+20+1+1+1:90:1:90'HIBSLS:45:1:4+20+1+1+J'HIDMES:46:1:4+1+1+0+1:90:1:90:500:J:J'HIDMBS:47:1:4+1+1+1+N:J'HIDMLS:48:1:4+1+1+1'HIBMES:49:1:4+1+1+0+1:90:1:90:50:J:J'HIBMBS:50:1:4+1+1+1+N:J'HIBMLS:51:1:4+1+1+1'HIIPZS:52:1:4+990+1+1+:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03'HIIPSS:53:1:4+990+1+0+5'HIUPA:54:4:4+1234567890123?@1234567890123+1+0+Peter anb Paleo'HIUPD:55:6:4+0161523660:EUR:280:21574124+DE08215741240161523660+1234567890123?@1234567890123+10+EUR+Peter anb Paleo++Rendite Plus SparCard++HKTAN:1+DKPAE:1+HKPAE:1+HKPRO:1+HKTAB:1+HKSPA:1+HKCUM:1+HKKAZ:1+HKCUB:1+HKSAL:1'HIUPD:56:6:4+0161523600:EUR:280:21574124+DE76215741240161523600+1234567890123?@1234567890123+1+EUR+Peter anb Paleo++Testbank Giro plus++HKTAN:1+DKPAE:1+HKPAE:1+HKPRO:1+HKTAB:1+HKSPA:1+HKDSC:1+HKCDN:1+HKCSE:1+HKCUB:1+HKDME:1+HKIPZ:1+HKBSL:1+HKCDL:1+HKCME:1+HKBSA:1+HKCCM:1+HKBSE:1+HKKAZ:1+HKCSB:1+HKBML:1+HKDSA:1+HKCCS:1+HKCDE:1+HKDSL:1+HKCMB:1+HKDMB:1+HKBMB:1+HKBME:1+HKBBS:1+HKSAL:1+HKIPS:1+HKDML:1+HKCSA:1+HKCSL:1+HKCDB:1+HKDBS:1+HKCUM:1+HKCML:1+HKDMC:1+HKDSE:1'HIUPD:57:6:4+++1234567890123?@1234567890123+++X++++HKTAN:1+DKPAE:1+HKPAE:1+HKPRO:1+HKTAB:1+HKSPA:1'HITAN:58:7:5+4++noref+nochallenge''HNHBS:59:1+1'");
    comm.addResponse("HNHBK:1:3+000000000349+300+3895648160439000VBOBN6j56L1rtO+2+3895648160439000VBOBN6j56L1rtO:2'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142922+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@78@HIRMG:2:2+0010::Nachricht entgegengenommen.'HIRMS:3:2:3+0100::Dialog beendet.''HNHBS:4:1+2'");
    comm.addResponse("HNHBK:1:3+000000004374+300+3895648162526000md3YEKjbmmS6Ri+1+3895648162526000md3YEKjbmmS6Ri:1'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142923+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@4100@HIRMG:2:2+0020::Dialoginitialisierung erfolgreich.+3076::Keine starke Authentifizierung erforderlich.+3060::Teilweise liegen Warnungen/Hinweise vor.'HIRMS:3:2:1+3099::Herzlich willkommen, Sie nutzen den FinTS Service, vielen Dank'HIRMS:4:2:3+0020::Information fehlerfrei entgegengenommen.'HIRMS:5:2:4+1040::BPD nicht mehr aktuell. Aktuelle Version folgt.+3920::Meldung unterstützter Ein- und Zwei-Schritt-Verfahren:920:921+0940::Letzte Anmeldung am 12.06.2024 - 14?:29?:22:12.06.2024 - 14?:29?:22+1050::UPD nicht mehr aktuell. Aktuelle Version folgt.'HIBPA:6:3:4+54+280:12345678+Testbank+0+1+300+9999'HIKOM:7:4:4+280:12345678+1+3:https?://fints-demobank.local/fints/test123::MIM:1'HIPINS:8:1:4+1+1+0+5:50:6:::HKCSA:J:HKCDL:J:HKDSE:J:HKDSL:J:HKTAN:N:HKBSA:J:DKPAE:J:HKCSB:N:HKCDB:N:HKBBS:N:HKCSL:J:HKCSE:J:HKTAB:N:HKCML:J:HKBME:J:HKBMB:N:HKDME:J:HKBSE:J:HKCCM:J:HKSAL:N:HKSPA:N:HKCME:J:HKDMB:N:HKCUB:N:HKDSA:J:HKKAZ:N:HKCCS:J:HKPAE:J:HKWPD:N:HKCDE:J:HKDBS:N:HKDML:J:HKCUM:J:HKIPS:N:HKIPZ:J:HKCMB:N:HKBSL:J:HKCDN:J:HKBML:J:HKPRO:N'DIPINS:9:1:4+1+1+HKCSA:J:HKCDL:J:HKDSE:J:HKDSL:J:HKTAN:N:HKBSA:J:DKPAE:J:HKCSB:N:HKCDB:N:HKBBS:N:HKCSL:J:HKCSE:J:HKTAB:N:HKCML:J:HKBME:J:HKBMB:N:HKDME:J:HKBSE:J:HKCCM:J:HKSAL:N:HKSPA:N:HKCME:J:HKDMB:N:HKCUB:N:HKDSA:J:HKKAZ:N:HKCCS:J:HKWPD:N:HKCDE:J:HKDBS:N:HKDML:J:HKCUM:J:HKIPS:N:HKIPZ:J:HKCMB:N:HKBSL:J:HKCDN:J:HKBML:J:HKPRO:N'HIPAES:10:1:4+1+1+0'DIPAES:11:1:4+1+1'HITANS:12:6:4+1+1+0+N:N:0:920:2:PushTan1:PushTan1::PushTan1:6:2:PushTan1:999:N:1:N:0:2:N:J:00:2:N:9'HITANS:13:7:4+1+1+0+N:N:0:921:2:PushTan1-Push:Decoupled::PushTan1-Push:::PushTan1-Push:999:N:1:N:0:2:N:J:00:2:N:9:60:5:5:N:J'HITABS:14:2:4+1+1+0'HITABS:15:4:4+1+1+0'HIPROS:16:3:4+1+1'HISPAS:17:1:4+1+1+0+J:N:J:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.003.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.09:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.02:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.008.001.08'HIKAZS:18:5:4+1+1+90:N:N'HIKAZS:19:6:4+1+1+0+90:N:N'HISALS:20:5:4+1+1'HISALS:21:6:4+1+1+0'HIWPDS:22:6:4+10+1+0+N:N:N'HICUBS:23:1:4+1+1+0+N'HICUMS:24:1:4+1+1+1+ACCT;CASH:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.09'HICCSS:25:1:4+1+1+0'HICSES:26:1:4+1+1+0+0:90'HICSBS:27:1:4+1+1+1+N:N'HICSAS:28:1:4+1+1+1+0:90'HICSLS:29:1:4+1+1+1+N'HICDES:30:1:4+1+1+1+1:1:360:0102030612:010203040506070809101112131415161718192021222324252627282930'HICDBS:31:1:4+1+1+1+N'HICDNS:32:1:4+1+1+1+9:1:360:J:J:J:J:J:J:J:J:J:0102030612:010203040506070809101112131415161718192021222324252627282930'HICDLS:33:1:4+1+1+1+0:0:N:N'HICCMS:34:1:4+1+1+0+500:J:J'HICMES:35:1:4+1+1+0+1:90:500:J:J'HICMBS:36:1:4+1+1+1+N:J'HICMLS:37:1:4+1+1+1'HIDSES:38:1:4+1+1+1+1:90:1:90'HIDBSS:39:1:4+1+1+1+N:N'HIDSAS:40:1:4+1+1+1+1:90:1:90'HIDSLS:41:1:4+1+1+1+J'HIBSES:42:1:4+20+1+1+1:90:1:90'HIBBSS:43:1:4+20+1+1+N:N'HIBSAS:44:1:4+20+1+1+1:90:1:90'HIBSLS:45:1:4+20+1+1+J'HIDMES:46:1:4+1+1+0+1:90:1:90:500:J:J'HIDMBS:47:1:4+1+1+1+N:J'HIDMLS:48:1:4+1+1+1'HIBMES:49:1:4+1+1+0+1:90:1:90:50:J:J'HIBMBS:50:1:4+1+1+1+N:J'HIBMLS:51:1:4+1+1+1'HIIPZS:52:1:4+990+1+1+:urn?:iso?:std?:iso?:20022?:tech?:xsd?:pain.001.001.03'HIIPSS:53:1:4+990+1+0+5'HIUPA:54:4:4+1234567890123?@1234567890123+2+0+Peter anb Paleo'HIUPD:55:6:4+0161523660:EUR:280:21574124+DE08215741240161523660+1234567890123?@1234567890123+10+EUR+Peter anb Paleo++Rendite Plus SparCard++HKTAN:1+DKPAE:1+HKPAE:1+HKPRO:1+HKTAB:1+HKSPA:1+HKCUM:1+HKKAZ:1+HKCUB:1+HKSAL:1'HIUPD:56:6:4+0161523600:EUR:280:21574124+DE76215741240161523600+1234567890123?@1234567890123+1+EUR+Peter anb Paleo++Testbank Giro plus++HKTAN:1+DKPAE:1+HKPAE:1+HKPRO:1+HKTAB:1+HKSPA:1+HKDSC:1+HKCDN:1+HKCSE:1+HKCUB:1+HKDME:1+HKIPZ:1+HKBSL:1+HKCDL:1+HKCME:1+HKBSA:1+HKCCM:1+HKBSE:1+HKKAZ:1+HKCSB:1+HKBML:1+HKDSA:1+HKCCS:1+HKCDE:1+HKDSL:1+HKCMB:1+HKDMB:1+HKBMB:1+HKBME:1+HKBBS:1+HKSAL:1+HKIPS:1+HKDML:1+HKCSA:1+HKCSL:1+HKCDB:1+HKDBS:1+HKCUM:1+HKCML:1+HKDMC:1+HKDSE:1'HIUPD:57:6:4+++1234567890123?@1234567890123+++X++++HKTAN:1+DKPAE:1+HKPAE:1+HKPRO:1+HKTAB:1+HKSPA:1'HITAN:58:7:5+4++noref+nochallenge''HNHBS:59:1+1'");
    comm.addResponse("HNHBK:1:3+000000000496+300+3895648162526000md3YEKjbmmS6Ri+2+3895648162526000md3YEKjbmmS6Ri:2'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142924+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@224@HIRMG:2:2+0010::Nachricht entgegengenommen.'HIRMS:3:2:3+0020::Auftrag ausgeführt.'HISPA:4:1:3+J:DE08215741240161523660:DEUTDEDBPB2:0161523660:EUR:280:21574124+J:DE76215741240161523600:DEUTDEDBPB2:0161523600:EUR:280:21574124''HNHBS:5:1+2'");
    comm.addResponse("HNHBK:1:3+000000000349+300+3895648162526000md3YEKjbmmS6Ri+3+3895648162526000md3YEKjbmmS6Ri:3'HNVSK:998:3+PIN:1+998+1+2::3895648143636000SX1DL9ZRA5M9O8+1:20240612:142924+2:2:13:@8@        :5:1+280:12345678:1234567890123?@1234567890123:V:0:0+0'HNVSD:999:1+@78@HIRMG:2:2+0010::Nachricht entgegengenommen.'HIRMS:3:2:3+0100::Dialog beendet.''HNHBS:4:1+3'");
  }
  
  /**
   * Schliesst das Passport-Objekt.
   * @throws Exception
   */
  @After
  public void after() throws Exception
  {
    try
    {
      if (this.passport != null)
        this.passport.close();
    }
    finally
    {
      HBCIUtils.done();
    }
  }
}
