# README #

Nějaké postřehy k práci, překladu

# Pro překlad, spuštění #

## Uvnitř IntelliJ ##
![build.PNG](https://bitbucket.org/repo/aarxnx/images/1570686407-build.PNG)

* ** RUN buildne projekt a spustí jej** (1)
![run.PNG](https://bitbucket.org/repo/aarxnx/images/2364563639-run.PNG)

* **Build JAR** (2)
Pouze buildne projekt do složky /build/libs/ateam-pdb-0.1.jar
Spuštění je možné pomocí CMD: **java -jar ateam-pdb-0.1.jar**

## Z konzole ##
V rootu projektu je pomocí konzole možné použít **gradlew**:

 * cz.vutbr.fit.pdb.ateam.tasks - zobrazí všechny možné příkazy z gradlem (asi nepotřebné)

 * **run** - to stejné co (1)

 * **jar** - to stejné co (2)