# WHISPIN - Sokoban Recursif

Projet Java/JavaFX realise dans le cadre du projet de L2 (S2), inspire de Patrick's Parabox.

Le jeu combine:

- un Sokoban classique (deplacement, poussee de boites, cibles, victoire),
- un mode recursif avec mondes imbriques,
- une interface graphique JavaFX,
- une persistance JSON des parties,
- des tests automatises de non-regression.

## Fonctionnalites principales

- Menu principal avec scenes dediees:
	- choix de niveau,
	- regles,
	- sauvegarde/chargement,
	- choix du personnage.
- Deplacement clavier:
	- fleches,
	- ZQSD / WASD,
	- `Ctrl+Z` pour annuler,
	- `Ctrl+S` pour sauvegarder.
- Deplacement automatique par clic (plus court chemin sans pousser de boite).
- Gestion des niveaux recursifs (entree/sortie de mondes, pile de recursion).
- Export/rejeu de solutions au format Sokobano (RLE simple).
- Sauvegarde et chargement en JSON.

## Prerequis

- Linux (cible principale du rendu).
- JDK (Java 17+ recommande).
- JavaFX installe localement (OpenJFX).
- JAR Jackson dans `lib/`:
	- `jackson-core`
	- `jackson-databind`
	- `jackson-annotations`

Le `makefile` ajoute automatiquement `lib/*` au classpath pour Jackson.
Pour JavaFX, il utilise une installation systeme detectee (ou les variables `JAVAFX_LIB` / `JAVAFX_RUN_LIB`).

## Arborescence utile

- `src/main/java/ui/`: interface JavaFX (scenes, menu, animation, selection de niveaux).
- `src/main/java/logique/`: moteur, chargement de niveaux, deplacement automatique, A*.
- `src/main/java/modele/`: structures du plateau et objets de domaine (`Case*`, direction, position).
- `src/main/java/rendu/`: rendu graphique des cases et du plateau.
- `src/main/java/persistance/`: sauvegarde/chargement JSON.
- `src/test/java/`: tests executables via `make test`.
- `docs/`: documentation du projet (dont le sujet `projet (1).pdf`).
- `niveau/`: fichiers de niveaux.
- `sauvegardes/`: sauvegardes JSON generees par le jeu.

## Format des niveaux

Le projet accepte:

- un format simple Sokoban (grille ASCII),
- un format recursif multi-mondes:
	- en-tete `X n` (identifiant de monde + taille),
	- suivi de `n` lignes de `n` caracteres,
	- plusieurs mondes concatenees dans le meme fichier.

Les boites-mondes utilisent des lettres (minuscule/majuscule sur cible).

## Persistance

- Dossier de sortie: `sauvegardes/`
- Format: JSON
- Type de sauvegardes:
	- automatiques (nom horodate),
	- personnalisees (nom saisi),
	- listing trie des sauvegardes existantes.

Le JSON conserve notamment:

- le nom du niveau,
- le plateau,
- la sequence Sokobano (`solution_sokobano`).

## Commandes

Depuis la racine du projet:

```bash
make build
```

Compile tout le code Java dans `bin/`.

```bash
make run
```

Lance l'application JavaFX.

```bash
make test
```

Lance les suites:

- `TestsMoteur`
- `TestsPersistance`
- `TestsNiveaux`

```bash
make clean
```

Supprime `bin/` et les jars generes.

## Controles en jeu

- Deplacement: fleches, `ZQSD`, `WASD`
- Annuler: `Ctrl+Z`
- Sauvegarder: `Ctrl+S`
- Retour menu/partie: `Echap`
- Clic souris sur case accessible: deplacement automatique

## Notes techniques

- Les sauvegardes automatiques sont utilisees pour limiter les pertes de progression pendant la partie.
- Le `makefile` detecte JavaFX automatiquement:
	- detection d'un chemin systeme connu,
	- surcharge possible via `JAVAFX_LIB` (build/test) et `JAVAFX_RUN_LIB` (run).

## Etat des tests

La commande `make test` doit terminer avec:

- `[OK] TestsMoteur`
- `[OK] TestsPersistance`
- `[OK] TestsNiveaux`

Si JavaFX n'est pas detecte automatiquement, definir explicitement:

```bash
make JAVAFX_LIB=/chemin/vers/javafx/lib build
```

Et pour forcer un chemin different au lancement:

```bash
make JAVAFX_RUN_LIB=/chemin/vers/javafx/lib run
```

