# [![Build Status](https://travis-ci.org/willuhn/hbci4java.svg?branch=master)](https://travis-ci.org/willuhn/hbci4java) HBCI4Java

## Vorab

Dies ist ein aktuell gepflegter Fork von [HBCI4Java](http://hbci4java.kapott.org/),
welcher u.a. in [Hibiscus](http://www.willuhn.de/products/hibiscus) und
[Pecunia-Banking](http://www.pecuniabanking.de/) zum Einsatz kommt.

## Kontakt

Unter https://groups.google.com/forum/?hl=de#!forum/hbci4java findet ihr die
zugehörige Mailingliste.

## Entstehung

Das SVN von hbci4java.kapott.org ist schon seit einiger Zeit nicht mehr
öffentlich, weil da drin wegen HBCI4Java 3 grundlegende Änderungen
stattfinden (wobei ich nicht weiss, ob Stefan wirklich noch daran arbeitet)
Für die letzte veröffentlichte Version 2.5.12 haben sich im Laufe der Zeit aber viele Patches
angesammelt, die auf http://hbci4java.kapott.org nicht veröffentlicht wurden.

Inzwischen enthält diese Fork hier nicht mehr nur Patches sondern auch umfangreiche
Weiterentwicklungen wie etwa 

- die Unterstützung der neuen TAN-Verfahren (smsTAN, chipTAN - incl. Implementierung des HHD-Standards mit Flicker-Code)
- Unterstützung von PC/SC-Kartenlesern via javax.smartcardio API
- eine aktuelle Bankenliste (mit BLZ, Server-Adresse, HBCI-Version,...)
- Unterstützung für alle aktuellen SEPA-PAIN-Versionen bei SEPA-Überweisungen
- erste Unterstützung für SEPA-Lastschriften und SEPA-Daueraufträge 

Ausgangsbasis dieser Weiterentwicklung war HBCI4Java 2.5.12 mit einigen Patches von
Stefan (konkret seine SVN-Revision r227 vom 28.05.2010 - liegt im Ordner "log").
Im Ordner "log/patches" dieses Repositories hatte ich sämtliche Änderungen in Form von
diff-Dateien gepflegt, um diese auch ohne Versionsverwaltungssystem noch nachvollziehen
zu können. Im Zuge der Erweiterungen am SEPA-Code wurde das jedoch zu umfangreich. Der
Ordner wurde zwischenzeitlich gelöscht. Die Historie der Weiterentwicklung kann über
die History des GIT-Repositories nachvollzogen werden.

