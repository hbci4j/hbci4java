## Download

```
<dependency>
   <groupId>com.github.hbci4j</groupId>
   <artifactId>hbci4j</artifactId>
   <version>3.0.7</version>
</dependency>
```

## Vorab

Dies ist ein aktuell gepflegter Fork von [HBCI4Java](http://hbci4java.kapott.org/),
welcher u.a. in [Hibiscus](http://www.willuhn.de/products/hibiscus) und
[Pecunia-Banking](http://www.pecuniabanking.de/) zum Einsatz kommt.

## Kontakt

Unter https://groups.google.com/forum/?hl=de#!forum/hbci4java findet ihr die
zugehörige Mailingliste.

## Entstehung

Das SVN von hbci4java.kapott.org ist schon seit einiger Zeit nicht mehr
öffentlich. Für die letzte veröffentlichte Version 2.5.12 hatten sich im Laufe der Zeit viele Patches
angesammelt, die in diesem Porjekt alle enthalten sind. Inzwischen enthält diese Fork aber auch umfangreiche
Weiterentwicklungen wie etwa 

- Die Unterstützung der neuen TAN-Verfahren (smsTAN, chipTAN - incl. Implementierung des HHD-Standards mit Flicker-Code)
- Unterstützung von PC/SC-Kartenlesern via javax.smartcardio API
- Eine aktuelle Bankenliste (mit BLZ, Server-Adresse, HBCI-Version,...)
- Unterstützung für alle aktuellen SEPA-PAIN-Versionen
- Unterstützung für SEPA-Überweisungen und -Lastschriften (jeweils Einzel- und Sammelaufträge) sowie SEPA-Daueraufträge 

## Lizenz

LGPL 2.1 - GNU Lesser General Public License, version 2.1 (http://www.gnu.org/licenses/old-licenses/lgpl-2.1)

## Releases

Aktuelle Releases werden auf Maven Central veröffentlicht. 

## Unit-Tests
Im Ordner "src/main/test/hbci4java" befinden sich einige JUnit-Tests. Viele davon erfordern jedoch das Vorhandensein spezieller Testumgebungen (Zugang zu Bank-Servern) bzw. vorkonfigurierte Bankzugänge. Die Tests können daher leider nicht automatisiert im Zuge der Erstellung von Deployment-Artefakten ausgeführt werden sondern nur manuell und selektiv.
