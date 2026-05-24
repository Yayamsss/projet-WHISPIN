# Dockerfile — Quantum (Parabox-like game)
# Java 25 + JavaFX 23 + Maven
#
# Un Dockerfile c'est une recette : Docker lit ce fichier ligne par ligne
# et construit une "image", c'est-à-dire un mini-système Linux isolé
# avec tout ce qu'il faut pour faire tourner notre jeu.
# L'avantage c'est que ça marche pareil sur toutes les machines.
#
# Pour construire l'image (à faire une fois) :
#   docker build -t quantum .
#
# Pour lancer le jeu sur Linux :
#   xhost +local:docker      <- autorise Docker à utiliser l'écran
#   docker run -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix quantum
#
# Pour lancer sur macOS (avec XQuartz installé) :
#   xhost +localhost
#   docker run -e DISPLAY=host.docker.internal:0 quantum

# On part d'une Ubuntu 24.04 vide. C'est comme avoir un PC neuf
# avec juste Ubuntu installé dessus, rien d'autre.
FROM ubuntu:24.04

# Quand apt-get installe des trucs, il pose parfois des questions interactives
# (genre "appuyez sur Entrée"). Dans Docker personne peut répondre donc on
# désactive ça avec cette variable d'environnement.
ENV DEBIAN_FRONTEND=noninteractive

# On installe tous les paquets nécessaires en une seule commande RUN.
# C'est une bonne pratique Docker : chaque RUN crée une "couche" dans l'image,
# moins il y en a mieux c'est pour la taille finale.
#
# - wget, curl : pour télécharger des fichiers depuis internet
# - maven : pour compiler le projet Java et gérer JavaFX automatiquement
# - ca-certificates, gnupg : pour vérifier les signatures des paquets qu'on télécharge
# - libgl1, libgtk-3-0, libx11-6... : des bibliothèques graphiques dont JavaFX
#   a besoin pour pouvoir afficher une fenêtre. Sans ça JavaFX crash au démarrage
#   avec l'erreur "no suitable pipeline found".
# - fontconfig, libfreetype6 : pour que les polices du jeu s'affichent correctement
#
# Le "rm -rf /var/lib/apt/lists/*" supprime le cache apt après l'install
# pour réduire la taille de l'image (ça peut faire gagner plusieurs centaines de Mo).
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    maven \
    ca-certificates \
    gnupg \
    libgl1 \
    libgtk-3-0 \
    libglib2.0-0 \
    libxtst6 \
    libxxf86vm1 \
    libxrender1 \
    libfreetype6 \
    fontconfig \
    libx11-6 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*

# On installe Java 25 depuis le dépôt officiel Adoptium (Temurin).
# Ubuntu 24.04 ne propose pas Java 25 dans ses dépôts de base,
# donc on doit ajouter manuellement la source APT d'Adoptium.
#
# Étapes :
# 1. wget : on télécharge la clé GPG publique d'Adoptium (pour vérifier
#    que les paquets viennent bien d'eux et pas d'un autre)
# 2. gpg --dearmor : on convertit cette clé dans un format que apt comprend
# 3. echo + tee : on ajoute le dépôt Adoptium à la liste des sources APT
#    ("noble" c'est le nom de code d'Ubuntu 24.04)
# 4. apt-get update + install : on met à jour la liste et on installe Java 25
# 5. rm -rf : on nettoie le cache apt comme avant
RUN wget -q -O /tmp/adoptium.gpg \
    https://packages.adoptium.net/artifactory/api/gpg/key/public \
    && gpg --dearmor -o /usr/share/keyrings/adoptium.gpg /tmp/adoptium.gpg \
    && echo "deb [signed-by=/usr/share/keyrings/adoptium.gpg] \
    https://packages.adoptium.net/artifactory/deb noble main" \
    > /etc/apt/sources.list.d/adoptium.list \
    && apt-get update \
    && apt-get install -y temurin-25-jdk \
    && rm -rf /var/lib/apt/lists/*

# WORKDIR définit le dossier de travail dans le conteneur.
# Toutes les commandes suivantes s'exécutent depuis /app.
# Si le dossier n'existe pas Docker le crée automatiquement.
WORKDIR /app

# On copie les fichiers du projet depuis notre machine vers le conteneur.
# On copie d'abord pom.xml seul (avant les sources) : c'est une astuce Docker
# pour profiter du cache. Si on ne change que le code Java, Docker n'a pas
# besoin de re-télécharger les dépendances Maven, il réutilise la couche précédente.
COPY pom.xml .
COPY src/    ./src/
COPY assets/ ./assets/

# On demande à Maven de télécharger toutes les dépendances (JavaFX etc.)
# maintenant, pendant le build de l'image, plutôt qu'au lancement du jeu.
# Comme ça quand on fait "docker run", le jeu démarre directement sans attendre.
# Le -q c'est pour "quiet", ça réduit les logs de Maven.
RUN mvn dependency:resolve -q

# On compile le projet Java à l'intérieur du conteneur.
# Si ça rate ici, le build Docker s'arrête et on voit l'erreur.
# C'est pratique pour détecter les problèmes de compilation tôt.
RUN mvn compile -q

# DISPLAY c'est la variable qui dit à JavaFX où afficher la fenêtre.
# :0 c'est l'écran principal du système hôte (celui qui fait tourner Docker).
# Pour que ça fonctionne il faut aussi monter /tmp/.X11-unix au moment
# du "docker run" (voir les commandes en haut du fichier).
ENV DISPLAY=:0

# CMD c'est la commande lancée quand on fait "docker run quantum".
# On utilise la forme tableau ["cmd", "arg"] plutôt que la forme string
# "cmd arg" pour éviter les problèmes avec les espaces et les signaux Unix.
CMD ["mvn", "javafx:run"]
