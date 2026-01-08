## Vorab

Dies ist die offizielle Quelle von HBCI4Java, welches u.a.
in [Hibiscus](http://www.willuhn.de/products/hibiscus) zum Einsatz kommt.

## Versionshinweis

- Version 4.x von HBCI4Java verwendet Jakarta EE (jakarta.*)
- Version 3.x von HBCI4Java verwendet Java EE (javax.*) - veraltet

## Kontakt

<https://github.com/hbci4j/hbci4java/discussions>

## Entstehung

Das Projekt entstand 2010 als Fork von hbci4java.kapott.org (Seite nicht mehr verfügbar), da dessen
Weiterentwicklung eingestellt wurde.

Seither wurden umfangreiche neue Features hinzugefügt wie etwa:

- Unterstützung für PSD2 (SCA), welche seit September 2019 für FinTS verpflichtend ist
- Abruf von Umsätzen im CAMT-Format (HKCAZ)
- Unterstützung für chipTAN USB
- Abruf des elektronischen Kontoauszuges (HKEKA und HKEKP)
- SEPA-Überweisungen und -Lastschriften (jeweils Einzel- und Sammelaufträge) sowie
  SEPA-Daueraufträge
- Support für alle aktuellen SEPA-PAIN-Versionen
- Unterstützung von PC/SC-Kartenlesern via javax.smartcardio API
- Eine aktuelle Bankenliste (mit BLZ, Server-Adresse, HBCI-Version,...)
- Unterstützung der neuen TAN-Verfahren (smsTAN, photoTAN, chipTAN - incl. Implementierung
  des HHD-Standards mit Flicker-Code)
- RAH10-Schlüsseldateien
- PushTAN Decoupled (Direktfreigabe per App)
- Verification of Payee (VoP)

## Lizenz

LGPL 2.1 - GNU Lesser General Public License, version
2.1 <http://www.gnu.org/licenses/old-licenses/lgpl-2.1>

*Hinweis*
Bis 02.05.2016 unterlag HBCI4Java der GPLv2 - wurde
mit <https://github.com/willuhn/hbci4java/issues/36> aber auf LGPL 2.1 geändert.

## Download

Du kannst die aktuellste Version von HBCI4Java in Maven Central finden:

<https://central.sonatype.com/artifact/com.github.hbci4j/hbci4j-core/versions>

### Maven

```
<dependency>
   <groupId>com.github.hbci4j</groupId>
   <artifactId>hbci4j-core</artifactId>
</dependency>
```

### Gradle

```
dependencies {
  compile 'com.github.hbci4j:hbci4j-core:+'
}
```

## Selbst compilieren

Du benötigst:

- GIT <https://git-scm.com/>
- Java SDK 17 oder höher <https://adoptium.net/>

Öffne ein Terminal-Fenster und checke den Quellcode per GIT aus:

    git clone https://github.com/hbci4j/hbci4java.git

Wechsle in den Ordner "hbci4java":

    cd hbci4java

Erzeuge die JAR-Datei per:

    ./gradlew assemble

Im Ordner "build/libs" wird die Datei "hbci4j-core-${version}.jar" erzeugt. Zusätzlich
findet sich im Ordner "build/distributions" die Datei "hbci4j-core-${version}.zip", welche
auch die nötigen Abhängigkeiten enthält.

| **Hinweis** : Unter Windows nutze bitte `./gradlew.bat` statt `./gradlew` |
|---------------------------------------------------------------------------|

## In Eclipse einrichten

Führe im Ordner `hbci4java` die folgenden beiden Befehle aus:

    ./gradlew jaxb
    ./gradlew eclipse

Hierbei wird der Java-Code für die PAIN/CAMT XML-Dateien im Ordner "build/generated/jaxb"
und die Eclipse Projekt-Konfiguration generiert.

- Starte Eclipse.
- Wähle in der View "GIT Repositories" oben den Button "Add an existing local GIT
  repository to this view".
- Klicke mit der rechten Maustaste auf das importierte Repository und wähle "Import
  Projects..." (wenn das nicht funktioniert, öffne den "Project Explorer" und wähle dort
  im Kontextmenü "Import..." und anschließend unter "General" die Option "Existing
  Projects into Workspace". Wähle anschließend den Ordner "hbci4java" aus).
- Folge den Anweisungen des Assistenten.

## Unit-Tests

Im Ordner "src/main/test/" befinden sich einige JUnit-Tests. Einige davon erfordern jedoch
das Vorhandensein spezieller Testumgebungen (Vorhandensein von Bankzugängen oder
Chipkartenleser). Diese Tests werden im Zuge der Erstellung von Deployment-Artefakten nur
dann ausgeführt, wenn die entsprechenden System-Properties "test.online=true" und "
test.chipcard=true" aktiv sind. Die Tests zur Ausführung von HBCI-Geschäftsvorfällen
benötigen jedoch weitere Daten (Empfängerkonto, Betrag, Verwendungszweck, usw.). Wenn du
diese Tests ausführen möchtest, schaue dir den Quellcode der entsprechenden Tests an.

Du kannst die Tests starten per:

    ./gradlew test

## Beispiel-Code

Unter <https://github.com/hbci4j/hbci4java/blob/master/src/main/java/org/kapott/hbci/examples/UmsatzAbrufPinTan.java>
findest du Beispiel-Code zum Abrufen des Saldos und der Umsätze eines Kontos per
PIN/TAN-Verfahren.

## Veröffentlichung auf Maven Central

Um die aktuelle Version auf Maven Central zu veröffentlichen, benötigt man

- Central Portal Account mit Zugriff auf den Namespace `com.github.hbci4j`
- Einen GPG Key

1. Erstelle ein **Token** im Central Portal Account
   https://central.sonatype.com/usertoken

2. In der `~/.gradle/gradle.properties` trägt man dann sein gerade erstelltes Token für
   das Maven Central ein. **Wichtig**: `mavenCentralUsername` und `mavenCentralPassword` bestehen aus dem Token, 
   welches über das CentralPortal erstellt werden muss, und sind nicht Benutzername und Passwort des Central Portal Account. 
   Siehe Punkt 2 
   ```
   # ~/.gradle/gradle.properties !!NICHT INS REPO!!
   mavenCentralUsername=yourTokenUsername
   mavenCentralPassword=yourTokenPassword
   
   signing.keyId=ABCDEFGH
   signing.password=the_password_of_the_key
   signing.secretKeyRingFile=/home/YOUR_USERNAME/.gnupg/secring.gpg
   ```

3. Man konfiguriert dort auch seinen GPG Signing Key.

    - Erstellen des Keys mit `gpg --gen-key`
    - Upload des Keys: `gpg --keyserver keyserver.ubuntu.com --send-keys 
   LONG_ID_OF_THE_KEY`
    - Erstellen der `secring.gpg`: `gpg --keyring secring.gpg --export-secret-keys > ~/.
   gnupg/secring.gpg`
    - Eintragen der Short-ID, also die letzten acht Zeichen als `signing.keyId`

4. Dann noch die aktuelle Version in `hbci4java/gradle.properties` manuell festgelegen.

5. Anschließend kann man mit gradle die Veröffentlichung starten:
   ```
   ./gradlew clean build publishToMavenCentral
   ```
   Es dauert ein paar Minuten, bis die neue Version auf Maven Central verfügbar ist:
   https://repo1.maven.org/maven2/com/github/hbci4j/hbci4j-core/

   Wenn bereits die Version getagged ist und man nur das Paket veröffentlichen will, kann man wie folgt vorgehen:

   ```
   ./gradlew -x createTag clean build publishToMavenCentral
   ```

Bei der nächsten Version natürlich nur noch Schritte 4 und 5 nötig. 

Beim Publishing wird auch gleich der aktuelle Stand in Github getaggt.
