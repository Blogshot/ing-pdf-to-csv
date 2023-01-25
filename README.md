# English
### Description

This sofware is able to ingest account statements fron ING DE and convert them to a machine readable CSV format that can be imported into other software, e.g. [FireFly 3](https://github.com/firefly-iii/data-importer). 

It recurses through a given directory structure and creates CSV files alongside the according PDFs.

You will need to place all your PDFs into a single folder. Subfolders are supported. See below for an example of a valid directory structure.

Example:
```
...\Transactions
    |-- 2019 
        | -- 1.pdf
        | -- 2.pdf
        | -- 3.pdf   
    |-- 2020
        | -- 1.pdf
        | -- 2.pdf
        | -- 3.pdf   
    |-- 2021
        | -- 1.pdf
        | -- 2.pdf
        | -- 3.pdf
``` 

Other projects are more compact but cointain some bugs that lead me to develop something myself:
* Spaces in source/target accounts are not considered
* When an account name needs a line wrap, the second line gets interpreted as the description text while the actual description is ignored
* Only the first line of the description text is considered
* Edge cases like some forms of tax, closing statements, etc. are not ingested correctly

This project avoids these issues and aims to be more intelligent when analyzing the statements.

### Dependencies
https://tika.apache.org/ needs to be provided.

# Deutsch
### Beschreibung

Dieses Programm kann die PDF-Kontoauszüge der ING DE einlesen und in ein maschinenlesbares Format konvertieren, welches wiederum in andere Software eingelesen werden kann. Das Programm iteriert rekursiv durch eine vorgegebene Ordnerstruktur und legt die CSV-Dateien neben den dazugehörigen PDFs ab.

Dazu müssen die Kontoauszüge in einem Ordner abgelegt werden. Unterordner werden unterstützt. Siehe unten für eine Beispiel-Ordnerstruktur.

Beispiel:
```
...\Transaktionen
    |-- 2019 
        | -- 1.pdf
        | -- 2.pdf
        | -- 3.pdf   
    |-- 2020
        | -- 1.pdf
        | -- 2.pdf
        | -- 3.pdf   
    |-- 2021
        | -- 1.pdf
        | -- 2.pdf
        | -- 3.pdf
``` 

Andere Projekte sind zwar kompakter geschrieben, enthalten aber nervige Bugs. Dazu zählen:
* Es werden keine Leerzeichen bei Quell-/Zielkonten berücksichtigt
* Bei langen Kontonamen, die einen Zeilenumbruch benötigen, wird der umgebrochene Teil des Kontonamens als Beschreibung interpretiert und die tatsächliche Beschreibung verworfen
* Nur die erste Zeile von Beschreibungstexten wird eingelesen
* Sonderfälle wie Kapitalertragsteuer, Abschluss, Solidaritätszuschlag, etc. werden nicht korrekt eingelesen

Dieses Projekt vermeidet diese Fehler und bearbeitet die Transaktionen in den PDFs intelligenter. 

### Abhängigkeiten
Für dieses Programm muss https://tika.apache.org/ als Bibliothek vorhanden sein.
