intellij import:

Projekt gotowy do importu w IntelliJ. Dodano pliki gradle.properties i local.properties.

Build:
*Gradle
Gradle project: <�cie�ka do projektu>
Tasks: build

Run:
*JAR Application
Path to Jar: .../debates-crf/build/libs/debates-crf-1.0.0.jar (pe�na �cie�ka do projektu)
Program arguments: -trainDir C:\Users ... ../../../res/debates_with_ann/ (tutaj musi by� dok�adna �cie�ka do pliku)
Working directory: Folder, gdzie znajduje si� nasz projekt (pe�na �cie�ka)
Search sources using module's classpath: debates-crf


build:
$ cd debates
$ gradle build

usage:
$ cd debates/debates-crf/build/libs
$ java -jar debates-crf-1.0.0.jar -trainDir ../../../res/debates_with_ann/