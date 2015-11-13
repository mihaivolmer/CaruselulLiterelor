# CaruselulLiterelor
Repository-ul pentru proiectul #1 (Caruselul Literelor) de la Management-ul Proiectelor Software

Echipa:

Technical Writer
* Dragomir Dragos, 341C5

Developers:
* Ionel Laura, 342C5
* Gruia Iulia, 341C5
* Volmer Mihai, 341C1

Testers
* Popescu Teodor, 341C5
* Andreea Bruma, 341C5



Caruselul Literelor a fost conceput ca o aplicatie Android mobile.

Pasi configurare mediu de dezvoltare (IntelliJ IDEA):
--clone de pe Git--

1. Check out from Version Control -> GitHub -> Git Repository URL -> Clone -> Create project from existing sources 
-> Next -> Next-> Next -> Next -> Next -> Select SDK (+) and JDK 1.8 -> Next -> Finish

2. Project Structure -> Project -> Project SDK: 1.8 , Project Language Level: 8-Lambdas, type annotations -> Modules -> select android app -> Souces -> Language Level: 8-Lambdas, type annotations - > Dependencies -> Project SDK: 1.8 -> Ok

3. File -> Settings -> Build, Exception, Deployment -> Compiler -> Java Compiler -> Target bytecode version: select 1.8

4. in project source find pom.xml -> right click -> Add as Maven Project

5. AVD Manager -> Create Virtual Device 

6. Edit Configuration -> + button -> Android Application -> Model: select your model -> Package : Deploy Default APK, Activity: Louch, Emulator: select your emulator
