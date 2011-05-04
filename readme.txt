Hinweis zu "Hibiscus-Branch" von HBCI4Java.

Das SVN von hbci4java.kapott.org ist schon seit einiger Zeit nicht mehr
oeffentlich, weil da drin wegen HBCI4Java 3 grundlegende Aenderungen
stattfinden. Fuer 2.5.12 haben sich im Laufe der Zeit aber einige Patches
angesammelt, die auf http://hbci4java.kapott.org nicht veroeffentlicht wurden.

Daher habe ich im Hibiscus CVS diese HBCI4Java-Version eingecheckt.
Ausgangsbasis ist Version 2.5.12 mit einigen Patches von Stefan
(konkret seine SVN-Revision r227 vom 28.05.2010). Hinzugekommen sind
anschliessend noch ein paar weitere Patches. Verlauf also bisher:

2.5.12 (http://hbci4java.kapott.org/hbci4java-2.5.12-src.zip)
 -> r227 (log/hbci4java-r227.tgz)
   -> "Hibiscus-Branch" (r227+log/patches/*)
   
Ich werde versuchen, fuer alle weiteren Aenderungen, die ich hier vornehme,
nummerierte Diff-Dateien in log/patches abzulegen.


Wichtig: Damit das Projekt compiliert, muss einmal das Ant-Script
"thirdparty/cryptalgs4java/build.xml" mit dem Target "compile" ausghefuehrt
werden. Es erzeugt Klassen in "thirdparty/cryptalgs4java/bin/classes", die
von HBCI4Java benoetigt werden.
