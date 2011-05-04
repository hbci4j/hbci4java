#!/bin/sh

# Verwendet HBCIBatch für das Abholen von Kontostand und
# Kontoumsatzdaten. Die beiden folgenden Zeilen müssen 
# angepasst werden und geben zum einen den CLASSPATH zu
# HBCI4Java resp. den Pfad, in dem die Batch-Dateien gespeichert
# sind, an.

HBCI4JAVA=/home/kleiner/projects/hbci4java-stable/classes
DATAPATH=/home/kleiner/projects/hbci4java-stable/demo/HBCIBatch


# Beim Aufruf von HBCIBatch werden 4 bzw. 5 Dateinamen als Argumente übergeben.
# Die angegebenen Dateien enthalten entweder Informationen für HBCIBatch bzw.
# werden als Ausgabedateien für die Ergebnisse der Ausführung von HBCIBatch
# verwendet.
#
# Für die Dateien mit Angaben für HBCIBatch (die ersten drei Dateinamen) 
# befinden sich Beispieldateien im selben Verzeichnis wie dieses Shell-Script. 
# Die jeweiligen Dateien enthalten zusätzliche Beschreibungen zu ihrer 
# Verwendung und ihrer Syntax):
# 
#   - hbci4java.properties enthält die HBCI4Java-Kernel-Parameter.
# 
#   - answers.properties enthält Antwort-Daten, die für die Behandlung von
#     Callbacks benötigt werden.
# 
#   - jobs.batch enthält die Definitionen der auszuführenden GVs
# 
#   - In die Datei results.dat werden die Ausgabedaten der ausgeführten Jobs
#     geschrieben. Eine Beschreibung des Formats dieser Datei ist in 
#     results.dat.txt zu finden
# 
#   - Logausgaben von HBCI4Java werden in die Datei jobs.log geschrieben. Die
#     Angabe dieses Dateinamens ist optional - wird er weggelassen, werden die
#     Logausgaben auf STDOUT ausgegeben.

java \
    -Xmx128M \
    -cp "$HBCI4JAVA" \
    org.kapott.hbci.tools.HBCIBatch \
      "$DATAPATH/hbci4java.properties" \
      "$DATAPATH/answers.properties" \
      "$DATAPATH/jobs.batch" \
      "$DATAPATH/results.dat" \
      "$DATAPATH/jobs.log"
