# Beschreibung

Dieses Programm kann die PDF-Kontoauszüge der ING DE einlesen und in ein maschinenlesbares Format kovertieren, welches wiederum in andere Software eingelesen werden kann. Das Programm iteriert rekursiv durch eine vorgegebene Ordnerstruktur und legt die CSV-Dateien neben den dazugehörigen PDFs ab.

Dazu müssen die Kontoauszüge in einen Ordner abgelegt werden. 

Beispiel:
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
        
Aufruf:
java -jar '.\ING-PDF2CSV.jar' "C:\Users\dummy\Transaktionen"
       
Andere Projekte sind zwar kompakter geschrieben, enthalten aber nervige Bugs. Dazu zählen:
* Es werden keine Leerzeichen bei Quell-/Zielkonten berücksichtigt
* Bei langen Kontonamen, die einen Zeilenumbruch benötigen, wird der umgebrochene Teil des Kontonamens als Beschreibung interpretiert und die tatsächliche Beschreibung verworfen
* Nur die erste Zeile von Beschreibungstexten wird eingelesen
* Sonderfälle wie Kapitalertragsteuer, Abschluss, Solidaritätszuschlag, etc. werden nicht korrekt eingelesen

Dieses Projekt versucht, die PDFs intelligenter zu bearbeiten. So wird ein Beschreibungstext so lange mit Folgezeilen erweitert, bis der Beginn einer neuen Transaktion registriert wird.

## Abhängigkeiten
Für dieses Programm muss https://tika.apache.org/ als Bibliothek vorhanden sein.
