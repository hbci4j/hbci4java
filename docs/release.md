---
title: Anleitung zum Release Prozess
---

Um ein Release zu erstellen, benötigt man zunächst die entsprechende Berechtigung, um ein Artifact auf Maven Central hochzuladen. Die Berechtigung für com.github.hbci4j haben zurzeit Olaf und Janning.

## Berechtigung für OSSRH

Lege in der Datei `~/.m2/settings.xml` einen zusätzlichen Eintrag für den Server `ossrh` an:

    <settings>
      <servers>
        <server>
          <id>ossrh</id>
          <username>jira-username</username>
          <password>***PASSWORD***</password>
        </server>
      </servers>
    </settings>

## GPG

Das Jar muss vor dem Upload signiert werden. Falls das Kommando nicht `gpg` ist oder man den Release Prozess automatisieren möchte, kann man die `settings.xml` um folgenden Eintrag ergänzen: 

    <settings>
      <profiles>
        <profile>
          <id>ossrh</id>
          <activation>
            <activeByDefault>true</activeByDefault>
          </activation>
          <properties>
            <gpg.executable>gpg2</gpg.executable>
            <gpg.passphrase>the_pass_phrase</gpg.passphrase>
          </properties>
        </profile>
      </profiles> 
    </settings>

Eine komplette Anleitung zum GPG Prozess findet man hier:
- http://central.sonatype.org/pages/working-with-pgp-signatures.html

## Release

Wenn alles eingerichtet ist, erstellt man wie folgt eine neue Version mit automatischem Upload nach Maven Central

    mvn release:prepare
    mvn release:perform

Was diese Schritte im Detail machen, erklären die beiden folgenden Seite:
- http://maven.apache.org/maven-release/maven-release-plugin/examples/prepare-release.html
- http://maven.apache.org/maven-release/maven-release-plugin/examples/perform-release.html
