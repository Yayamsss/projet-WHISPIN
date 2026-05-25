#!/bin/bash
# Script de compilation et d'exécution des tests unitaires

CLASSPATH="lib/*:bin"

echo "=== Compilation des sources ==="
mkdir -p bin

# Compiler toutes les classes principales (sans JavaFX)
javac -cp "lib/*" -d bin \
    Case.java CaseBoite.java CaseBoiteCible.java CaseBoiteMonde.java \
    CaseBoiteMondeSurCible.java CaseCible.java CaseMur.java \
    CasePersonnage.java CasePersonnageCible.java CaseVide.java \
    Cell.java Direction.java Position.java Pair.java \
    ConvertisseurCases.java ChargeurNiveau.java LogiqueSokoban.java \
    ServicePersistance.java 2>&1

if [ $? -ne 0 ]; then
    echo "Erreur de compilation des sources principales"
    exit 1
fi

echo "=== Compilation des tests ==="
# Compiler les tests
javac -cp "$CLASSPATH" -d bin \
    src/test/java/sokoban/modeles/CaseTest.java \
    src/test/java/sokoban/modeles/ConvertisseurCasesTest.java \
    src/test/java/sokoban/modeles/LogiqueSokobanTest.java \
    src/test/java/sokoban/modeles/ServicePersistanceTest.java 2>&1

if [ $? -ne 0 ]; then
    echo "Erreur de compilation des tests"
    exit 1
fi

echo "=== Exécution des tests ==="
# Exécuter les tests avec JUnit Platform Console Launcher
java -cp "$CLASSPATH:bin" org.junit.platform.console.ConsoleLauncher \
    --select-package sokoban.modeles \
    --details tree

echo "=== Tests terminés ==="
