#!/bin/bash
# Script d'installation — Quantum (Parabox-like game)
# Testé sur : Kali Linux, Ubuntu, Debian, macOS
# Prérequis : Java 25+, Maven (installé auto si absent)
#
# Ce script vérifie que l'environnement est bon avant de compiler.
# Il évite à l'utilisateur de se taper les erreurs Maven à la main.

# "set -e" fait planter le script dès qu'une commande échoue.
# Sans ça le script continuerait même en cas d'erreur, ce qui
# pourrait cacher des problèmes et mener à des comportements bizarres.
set -e

# Codes couleur ANSI pour afficher des messages colorés dans le terminal.
# \033[0;31m = rouge, \033[0;32m = vert, etc.
# NC = "No Color", on l'utilise pour remettre la couleur par défaut après.
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; NC='\033[0m'

# Fonctions utilitaires pour afficher des messages formatés.
# "echo -e" active l'interprétation des séquences d'échappement (\033...).
info()    { echo -e "${BLUE}[INFO]${NC}  $1"; }
success() { echo -e "${GREEN}[OK]${NC}    $1"; }
warning() { echo -e "${YELLOW}[WARN]${NC}  $1"; }
error()   { echo -e "${RED}[ERREUR]${NC} $1"; exit 1; }  # exit 1 = erreur, on arrête tout

echo ""
echo "============================================"
echo "   Installation de Quantum"
echo "   Java 25 + JavaFX 23 + Maven"
echo "============================================"
echo ""

# ── 1. Vérification Java ──────────────────────────────────────────────────────
info "Vérification de Java..."

# "command -v java" cherche si java est dans le PATH.
# Le "&> /dev/null" redirige la sortie vers nulle part (on veut juste savoir
# si la commande existe, pas afficher son chemin).
if ! command -v java &> /dev/null; then
    error "Java n'est pas installé.\n\
  → Kali/Ubuntu/Debian :\n\
      sudo apt install wget apt-transport-https\n\
      wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public \\\n\
        | sudo gpg --dearmor -o /usr/share/keyrings/adoptium.gpg\n\
      echo \"deb [signed-by=/usr/share/keyrings/adoptium.gpg] \\\n\
        https://packages.adoptium.net/artifactory/deb \$(lsb_release -cs) main\" \\\n\
        | sudo tee /etc/apt/sources.list.d/adoptium.list\n\
      sudo apt update && sudo apt install temurin-25-jdk\n\
  → Autres : https://adoptium.net/temurin/releases/?version=25"
fi

# On extrait juste le numéro de version majeure de Java.
# "java -version" affiche sur stderr (pas stdout), d'où le 2>&1.
# grep -oP '(?<=version ")[0-9]+' : expression régulière qui capture
# le nombre juste après 'version "' dans la sortie.
# head -1 : on prend juste la première ligne au cas où il y en a plusieurs.
JAVA_VER=$(java -version 2>&1 | grep -oP '(?<=version ")[0-9]+' | head -1)
info "Java détecté : version $JAVA_VER"

# On vérifie que la version est bien >= 25.
# "-z" teste si la variable est vide (cas où grep n'a rien trouvé).
# "-lt 25" = "less than 25".
if [ -z "$JAVA_VER" ] || [ "$JAVA_VER" -lt 25 ]; then
    error "Java $JAVA_VER détecté, mais Java 25+ est requis.\n\
  Le code utilise les 'flexible constructors' (fonctionnalité Java 25+).\n\
  → Installer Java 25 : https://adoptium.net/temurin/releases/?version=25\n\
  → Sur Kali/Ubuntu/Debian : sudo apt install temurin-25-jdk"
fi

success "Java $JAVA_VER — OK"

# ── 2. Vérification / installation Maven ─────────────────────────────────────
info "Vérification de Maven..."

# Si Maven n'est pas installé, on essaie de l'installer automatiquement
# selon le gestionnaire de paquets disponible sur la machine.
if ! command -v mvn &> /dev/null; then
    warning "Maven non trouvé. Tentative d'installation automatique..."

    # On teste quel gestionnaire de paquets est disponible.
    # apt-get → Debian/Ubuntu/Kali, brew → macOS, pacman → Arch Linux
    if command -v apt-get &> /dev/null; then
        sudo apt-get install -y maven
    elif command -v brew &> /dev/null; then
        brew install maven
    elif command -v pacman &> /dev/null; then
        sudo pacman -S --noconfirm maven
    else
        # Aucun gestionnaire connu, on abandonne et on donne le lien
        error "Impossible d'installer Maven automatiquement.\n  → https://maven.apache.org/download.cgi"
    fi
fi

# On affiche la version de Maven installée pour confirmer que ça marche.
# "mvn -version 2>&1 | head -1" donne quelque chose comme :
# "Apache Maven 3.9.6 (bc0240f3c744dd6b6ec2920b3cd08dcc295161ae)"
success "Maven $(mvn -version 2>&1 | head -1) — OK"

# ── 3. Vérification du pom.xml ───────────────────────────────────────────────
# On vérifie qu'on est bien dans le bon dossier (celui qui contient pom.xml).
# Si l'utilisateur lance le script depuis n'importe où ça ne marchera pas.
if [ ! -f "pom.xml" ]; then
    error "pom.xml introuvable. Lancez ce script depuis la racine du projet Quantum."
fi

# ── 4. Compilation ────────────────────────────────────────────────────────────
info "Compilation (téléchargement de JavaFX 23 au 1er lancement, ~50 Mo)..."
echo ""

# "mvn compile" compile tout le code Java du projet.
# -U : force Maven à retenter les téléchargements même si une tentative
#      a échoué avant (Maven met en cache les échecs, ce qui peut bloquer).
# -q : mode silencieux, n'affiche que les erreurs.
if mvn compile -U -q; then
    success "Compilation réussie !"
else
    error "Échec de la compilation. Relancez avec : mvn compile -U pour voir le détail."
fi

echo ""
echo "============================================"
success "Installation terminée !"
echo ""
echo "  Lancer le jeu :"
echo -e "  ${GREEN}mvn javafx:run${NC}"
echo ""
echo "  Créer un JAR portable :"
echo -e "  ${GREEN}mvn package${NC}  →  target/quantum-full.jar"
echo ""
echo "  Lancer avec Docker :"
echo -e "  ${GREEN}xhost +local:docker${NC}"
echo -e "  ${GREEN}docker build -t quantum .${NC}"
echo -e "  ${GREEN}docker run -e DISPLAY=\$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix quantum${NC}"
echo "============================================"
echo ""
