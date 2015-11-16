# CaruselulLiterelor
Repository-ul pentru proiectul #1 (Caruselul Literelor) de la Management-ul Proiectelor Software

<b>Echipa:</b>

<b>Technical Writer</b>
* Dragomir Dragos, 341C5

<b>Developers:</b>
* Ionel Laura, 342C5
* Gruia Iulia, 341C5
* Volmer Mihai, 341C1

<b>Testers</b>
* Popescu Teodor, 341C5
* Andreea Bruma, 341C5



Caruselul Literelor a fost conceput ca o aplicatie Android mobile

<b>Cerinta:</b>

http://elf.cs.pub.ro/mps/wiki/proiect/proiect-1

<b>Tehnologii folosite:</b>
* Java
* Android

<b>Scopul aplicatiei:</b>

Aplicatia isi propune implementarea unui joc interactiv in care utilizatorul trebuie sa gaseasca cat mai multe cuvinte, continand doar literele aflate pe ecran sub forma unor zaruri, intr-un interval dat de timp.

<b>Scorul se va calcula astfel: </b>
- daca utilizatorul a fosrmat cuvinte ce cuprind litere rare( X, Z, H, J), iar cuvantul este mai lung de 6 litere punctajul se va alcatui dupa formula numarulLitereCuvant + 3*numarulLitereRare
- daca utilizatorul a fosrmat cuvinte ce cuprind litere rare( X, Z, H, J), iar cuvantul este mai scurt de 6 litere punctajul se va alcatui dupa formula numarulLitereCuvant + numarulLitereRare
- altfel punctajul se va calcula ca fiind numarulLitereCuvant



<b>Pasi configurare mediu de dezvoltare (IntelliJ IDEA):</b>
--clone de pe Git--

1. Check out from Version Control -> GitHub -> Git Repository URL -> Clone -> Create project from existing sources 
-> Next -> Next-> Next -> Next -> Next -> Select SDK (+) and JDK 1.8 -> Next -> Finish

2. Project Structure -> Project -> Project SDK: 1.8 , Project Language Level: 8-Lambdas, type annotations -> Modules -> select android app -> Souces -> Language Level: 8-Lambdas, type annotations - > Dependencies -> Project SDK: 1.8 -> Ok

3. File -> Settings -> Build, Exception, Deployment -> Compiler -> Java Compiler -> Target bytecode version: select 1.8

4. in project source find pom.xml -> right click -> Add as Maven Project

5. AVD Manager -> Create Virtual Device 

6. Edit Configuration -> + button -> Android Application -> Model: select your model -> Package : Deploy Default APK, Activity: Louch, Emulator: select your emulator




<b>Modulele aplicatiei:</b>

1. LetterGraphicSetup.java - foloseste un xml(letters.xml) pentru desenarea in interfara grafica a celor 9 zaruri ale jocului
2. Generator.java - clasa in care se genereaza fetele celor 9 zaruri ale jocului
                  - metoda getLetters() genereaza fete noi pentru fiecare zar si le intoarce sub forma de lista
3. WordUtils.java - clasa contine metode ce implementeaza validarea si cautarea cuvantului
4. EnterWordButton.java - clasa mapata intr-un xml(httpget.xml) pentru implementarea grafica a butonului
5. Pop.java - clasa mapata intr-un xml(popwindow.xml) pentru rewprezentarea grafica a ferestrei FinishGame
6. MainActivity.java - clasa principala care asigura atat partea de view cat si cea de control
7. WordSearchThread.java - clasa ce cuprinde metode ce implementeaza cautarea cuvantului in dictionar
                         - trimite off-main-thread un query catre dexonline.ro si, in functie de raspuns, afiseaza rezultatul pe ecran

