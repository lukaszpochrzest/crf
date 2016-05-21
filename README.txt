intellij import:

Projekt gotowy do importu w IntelliJ. Dodano pliki gradle.properties i local.properties.

Build:
*Gradle
Gradle project: <Œcie¿ka do projektu>
Tasks: build

Run:
*JAR Application
Path to Jar: .../debates-crf/build/libs/debates-crf-1.0.0.jar (pe³na œcie¿ka do projektu)
Program arguments: -trainDir C:\Users ... ../../../res/debates_with_ann/ (tutaj musi byæ dok³adna œcie¿ka do pliku)
Working directory: Folder, gdzie znajduje siê nasz projekt (pe³na œcie¿ka)
Search sources using module's classpath: debates-crf


build:
$ cd debates
$ gradle build

usage:
$ cd debates/debates-crf/build/libs
$ java -jar debates-crf-1.0.0.jar -trainDir ../../../res/debates_with_ann/