## WHISPIN_SOKOBAN
Projet du deuxième semestre de L2 en groupe (de 6 personnes) sur le jeu du sokoban.

## Dépendance JSON (Jackson)

La persistance JSON utilise Jackson.
Placez ces JAR dans le dossier `lib/` :

- `jackson-core`
- `jackson-databind`
- `jackson-annotations`

Le `makefile` inclut automatiquement `lib/*` au classpath.

## Sorties de persistance

Les fichiers générés par les sauvegardes de persistance sont écrits dans :

- `PERSISTANCE/solution/`

