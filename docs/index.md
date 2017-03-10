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

- Die Unterstützung der neuen TAN-Verfahren (smsTAN, chipTAN - incl. Implementierung des HHD-Standards mit Flicker-Code)
- Unterstützung von PC/SC-Kartenlesern via javax.smartcardio API
- Eine aktuelle Bankenliste (mit BLZ, Server-Adresse, HBCI-Version,...)
- Unterstützung für alle aktuellen SEPA-PAIN-Versionen
- Unterstützung für SEPA-Überweisungen und -Lastschriften (jeweils Einzel- und Sammelaufträge) sowie SEPA-Daueraufträge 

Ausgangsbasis dieser Weiterentwicklung war HBCI4Java 2.5.12 mit einigen Patches von
Stefan (konkret seine SVN-Revision r227 vom 28.05.2010 - liegt im Ordner "log").
Im Ordner "log/patches" dieses Repositories hatte ich sämtliche Änderungen in Form von
diff-Dateien gepflegt, um diese auch ohne Versionsverwaltungssystem noch nachvollziehen
zu können. Im Zuge der Erweiterungen am SEPA-Code wurde das jedoch zu umfangreich. Der
Ordner wurde zwischenzeitlich gelöscht. Die Historie der Weiterentwicklung kann über
die History des GIT-Repositories nachvollzogen werden.

## Lizenz

LGPL 2.1 - GNU Lesser General Public License, version 2.1 (http://www.gnu.org/licenses/old-licenses/lgpl-2.1)

*Hinweis*
Bis 02.05.2016 unterlag HBCI4Java der GPLv2 - wurde mit https://github.com/willuhn/hbci4java/issues/36 aber auf LGPL 2.1 geändert.

## Releases

Du kannst HBCI4Java entweder selbst compilieren (siehe folgender Absatz) - oder du nimmst einfach fertige Releases. Aktuelle Versionen findest du immer im GitHub-Projekt von [Hibiscus](https://github.com/willuhn/hibiscus):

- Binaries: [hbci4java-2.5.12.jar, hbci4java*.dll, libhbci4java*.so,, libhbci4java*.jnilib](https://github.com/willuhn/hibiscus/tree/master/lib)
- Source: [hbci4java-2.5.12-src.zip](https://github.com/willuhn/hibiscus/tree/master/lib.src)

*Hinweise*

- Lass dich nicht von der Versionsnummer 2.5.12 irritieren. Es ist die aktuelle Version. Ich habe mir nur abgewöhnt, die Versionsnummer im Dateinamen zu erhöhen, weil das regelmäßig dazu führte, dass User die Datei lediglich in ihren "lib"-Ordner kopierten und dort dann ein Durcheinander aus mehreren Versionen entstand. Durch Beibehalten der Versionsnummer im Dateinamen wird die alte Version immer überschrieben.
- Immer wenn es eine Änderung im Code von [HBCI4Java](https://github.com/willuhn/hbci4java) gab, erzeuge ich auch neue JARs in [Hibiscus](https://github.com/willuhn/hibiscus/tree/master/lib)
- In der [History](https://github.com/willuhn/hibiscus/commits/master/lib/hbci4java-2.5.12.jar) siehst du auch, welche Änderungen jeweils eingeflossen sind. 

## Selbst compilieren

Du benötigst:

- Linux (unter Windows habe ich es noch nicht getestet)
- Java 6 oder höher
- Apache Ant
- GNU make und GCC zum Compilieren der JNI-Libs für die CTAPI-Kartenleser-Anbindung

Wechsle in den Ordner mit der "build.xml" und führe in einer Shell folgende Befehle aus:

    $> ant clean
    $> ant dist
  
Im Ordner "dist/jar" wird eine "hbci4java.jar" erzeugt. Im Ordner "dist/lib" findest du die JNI-Libs.
Das Build-Script "build.gradle" wird eigentlich nur für [Travis CI](https://travis-ci.org/willuhn/hbci4java)
benötigt. Releases können - wie oben beschrieben - mit Ant erzeugt werden. 

## Unit-Tests
Im Ordner "test/hbci4java" befinden sich einige JUnit-Tests. Viele davon erfordern jedoch das Vorhandensein spezieller Testumgebungen (Zugang zu Bank-Servern) bzw. vorkonfigurierte Bankzugänge. Die Tests können daher leider nicht automatisiert im Zuge der Erstellung von Deployment-Artefakten ausgeführt werden sondern nur manuell und selektiv.

 
