/*Auteur : AIT MEDDOUR Sami*/

/*
    Enumère les outils disponibles dans l'éditeur de niveau.
    Chaque outil est associé à un caractère fixe utilisé dans la grille :
    '#' mur, '$' caisse, '@' joueur, '.' objectif, ' ' gomme (efface la case).
*/
public enum Tool
{
    WALL('#'),
    BOX('$'),
    PLAYER('@'),
    GOAL('.'),
    ERASE(' ');

    private final char mChar; // caractère associé à l'outil, utilisé directement dans la grille

    Tool(char c) { mChar = c; }

    public char getChar() { return mChar; }
}
