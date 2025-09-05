## Release erstellen

Um ein Release zu erstellen, benötigt man zunächst die entsprechende Berechtigung, um ein Artifact auf Maven Central hochzuladen. Die Berechtigung für com.github.hbci4j haben zurzeit Olaf und Janning.

## Berechtigung für Maven Central

1. Melde dich auf <https://central.sonatype.com/> an und erstelle ggf. einen Account
 Wechsle auf <https://central.sonatype.com/account> und klicke dort auf "Generate User Token", falls du noch keinen Token generiert hast
2. Erstelle/Öffne die Datei `~/.gradle/gradle.properties` in einem Editor und trage folgendes ein:

    sonatypeUsername=<der Username des User Tokens>
    sonatypePassword=<das Passwort des User Tokens>

## GPG

Die Artefakte werden vor dem Upload signiert. Stelle sicher, dass GnuPG korrekt konfiguriert ist.

Unter <https://central.sonatype.org/publish/requirements/gpg/#generating-a-key-pair> findest du hierzu weitere Informationen.

## Release

1. Lade eine aktuelle Gradle-Version von <https://gradle.org/> herunter und installiere sie.
2. Gib im Projektordner in `gradle.properties` die gewünschte Versionsnummer ein.
3. Stelle sicher, dass alle Änderungen eingecheckt und in das Repository gepusht wurden, da beim Release automatisch ein GIT-Tag erzeugt wird.
4. Mit dem folgenden Kommando wird das Release erzeugt und auf Maven Central veröffentlicht:

    $> gradle clean
    $> gradle publish
