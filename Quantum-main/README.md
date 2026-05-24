# Quantum — Parabox-like Game

Projet de programmation — Licence 2ème année  
Université Paris 13 — Année 2025-2026

---

## Description

Quantum est une implémentation du jeu Patrick's Parabox, lui-même inspiré du célèbre jeu de puzzle Sokoban.  
Le joueur doit pousser des boîtes sur des cibles. La particularité du jeu est la récursivité : les boîtes peuvent contenir d'autres mondes, et le joueur peut entrer et sortir des boîtes pour résoudre les puzzles.

---

## Membres du groupe et rôles

| Membre      | Rôle                       |
|-------------|----------------------------|
| Ryane       | Modèle                     |
| Aleksandar  | Coordinateur               |
| Ali         | Interface Graphique        |
| Yanis       | Persistance                |
| Arthur      | Installation & Déploiement |
| Abdel       | Chemins                    |
| Sami        | Édition de plateau         |
| Abde        | Résolution Automatique     |

---

## Prérequis

| Outil      | Version requise | Lien                                                               |
|------------|----------------|--------------------------------------------------------------------|
| Java (JDK) | **25+**        | [adoptium.net](https://adoptium.net/temurin/releases/?version=25) |
| Maven      | 3.8+           | [maven.apache.org](https://maven.apache.org/download.cgi)         |

> **Pourquoi Java 25 ?** Le code source utilise les *flexible constructors*, une fonctionnalité introduite en Java 25.  
> **JavaFX** est téléchargé **automatiquement** par Maven — inutile de l'installer manuellement.

---

## Installation et lancement

### Linux (Kali / Ubuntu / Debian)

#### 1. Installer Java 25

```bash
sudo apt install wget apt-transport-https
wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public \
  | sudo gpg --dearmor -o /usr/share/keyrings/adoptium.gpg
echo "deb [signed-by=/usr/share/keyrings/adoptium.gpg] \
  https://packages.adoptium.net/artifactory/deb $(lsb_release -cs) main" \
  | sudo tee /etc/apt/sources.list.d/adoptium.list
sudo apt update
sudo apt install temurin-25-jdk
```

#### 2. Installer Maven

```bash
sudo apt install maven
```

#### 3. Lancer le script d'installation

```bash
chmod +x install.sh
./install.sh
```

#### 4. Lancer le jeu

```bash
mvn javafx:run
```

---

### Windows

1. Télécharger et installer **Java 25** depuis [adoptium.net](https://adoptium.net/temurin/releases/?version=25)
2. Télécharger et installer **Maven** depuis [maven.apache.org](https://maven.apache.org/download.cgi) et l'ajouter au PATH
3. Dans un terminal PowerShell, dans le dossier du projet :

```powershell
mvn javafx:run
```

---

### macOS

```bash
# Installer Java 25 et Maven via Homebrew
brew install --cask temurin@25
brew install maven

# Lancer le jeu
mvn javafx:run
```

---

## Lancement via Docker

Docker permet de lancer le jeu sans installer Java ni Maven sur sa machine.

### Prérequis Docker

```bash
# 1. Installer Docker
sudo apt install docker.io

# 2. Ajouter l'utilisateur au groupe docker (redémarrer le terminal après)
sudo usermod -aG docker $USER

# 3. Autoriser Docker à accéder à l'écran (X11)
xhost +local:docker
```

### Build et lancement

```bash
# Construire l'image (à faire une seule fois)
docker build -t quantum .

# Lancer le jeu
docker run -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix quantum
```

> **macOS** : Installer [XQuartz](https://www.xquartz.org/), puis remplacer la dernière commande par :
> ```bash
> docker run -e DISPLAY=host.docker.internal:0 quantum
> ```

---

## Autres commandes utiles

```bash
# Forcer le re-téléchargement si le cache Maven est corrompu
mvn javafx:run -U

# Compiler sans lancer
mvn compile

# Créer un JAR portable avec toutes les dépendances
mvn package
# → Produit : target/quantum-full.jar

# Nettoyer les fichiers compilés
mvn clean
```

---

## Problèmes fréquents

| Erreur                           | Cause                              | Solution                                  |
|----------------------------------|------------------------------------|-------------------------------------------|
| `flexible constructors error`    | Java < 25 utilisé                  | Installer Java 25 (voir étape 1)          |
| `transfer failed` (Maven)        | Cache Maven corrompu               | Relancer avec `mvn javafx:run -U`         |
| `install.sh: command not found`  | Droits d'exécution manquants       | Faire `chmod +x install.sh`               |
| `no suitable pipeline` (JavaFX)  | Problème de rendu graphique        | Ajouter `-Dprism.order=sw`                |
| `DISPLAY not set`                | Pas de bureau graphique accessible | Faire `export DISPLAY=:0` avant de lancer |

---

## Structure du projet

```
Quantum/
├── src/
│   ├── Program.java          
│   ├── ProgramInit.java      
│   ├── AI/                   
│   │   ├── AutoSolver.java
│   │   ├── Node.java
│   │   └── PathFinding.java
│   ├── Data/                 
│   │   ├── LevelLoader.java
│   │   ├── LevelSaver.java
│   │   ├── MoveLogger.java
│   │   ├── SaveDataManager.java
│   │   └── UndoManager.java
│   ├── Editor/               
│   │   ├── EditorView.java
│   │   ├── Level.java
│   │   ├── LevelEditor.java
│   │   └── Tool.java
│   ├── Game/                
│   │   ├── Box.java
│   │   ├── BoxRecursive.java
│   │   ├── BoxPlayer.java
│   │   ├── BoxManager.java
│   │   ├── GameLoop.java
│   │   ├── GameManager.java
│   │   ├── PlayerEye.java
│   │   └── ...
│   ├── Graphics/            
│   │   ├── MenuMain.java
│   │   ├── MenuGame.java
│   │   ├── MenuLevel.java
│   │   ├── MenuWin.java
│   │   ├── MenuLoose.java
│   │   ├── GraphicBackGround.java
│   │   ├── GraphicTimer.java
│   │   ├── GraphicStars.java
│   │   └── ...
│   ├── Math/                 
│   │   ├── Vector2.java
│   │   ├── Matrix3x2.java
│   │   └── Easing.java
│   └── Path/                 
│       ├── GridClickHandler.java
│       └── PlayerPath.java
├── assets/
│   ├── Levels/              
│   │   ├── Level0.lvl
│   │   ├── Level1.lvl
│   │   └── Level2.lvl
│   ├── Save/                 
│   │   ├── CustomLevel.lvl
│   │   └── MoveLogs.txt
│   ├── Fonts/               
│   │   └── Orbitron.ttf
│   └── Textures/             
│       ├── BoxRegular.png
│       └── ...
├── .gitignore
├── Dockerfile
├── install.sh
├── Makefile
├── pom.xml
└── README.md
```

---

## Format des niveaux

Les niveaux sont représentés en ASCII :

| Caractère        | Signification             |
|------------------|---------------------------|
| ` ` (espace)     | Case vide                 |
| `#`              | Mur                       |
| `$`              | Boîte                     |
| `@`              | Personnage                |
| `.`              | Cible                     |
| `*`              | Boîte sur une cible       |
| `+`              | Personnage sur une cible  |
| `:`              | Cible pour joueur         |
| lettre minuscule | Boîte-monde               |
| lettre majuscule | Boîte-monde sur une cible |

---

## Contrôles

| Touche      | Action                                       |
|-------------|----------------------------------------------|
| ↑ ↓ ← →     | Déplacer le personnage                       |
| U           | Annuler le dernier mouvement                 |
| Clic souris | Déplacement automatique vers la case cliquée |
| I           | Afficher un indice (résolution automatique)  |
| R           | Relancer le niveau  |
