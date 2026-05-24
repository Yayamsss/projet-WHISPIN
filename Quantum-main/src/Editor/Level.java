/* Auteur : AIT MEDDOUR Sami*/

/*Représente un niveau de Sokoban sous forme de grille carrée de caractères.
    Utilisée dans l'éditeur de niveau pour stocker et manipuler le contenu de la grille.
    Chaque case contient un symbole : '#' mur, '$' caisse, '@' joueur, '.' objectif, ' ' vide.
    Fournit les opérations de base pour lire, modifier et exporter la grille*/

public class Level
{
    private int mSideSize;    // taille d'un côté de la grille (grille carrée)
    private char[][] mGrid;   // contenu de chaque cellule ('#', '$', '@', '.', ' ')

    // Crée une grille carrée de taille sideSize x sideSize, toutes les cases initialisées à vide
    public Level(int sideSize)
    {
        mSideSize = sideSize;
        mGrid = new char[sideSize][sideSize];
        for (int i = 0; i < sideSize; i++)
            for (int j = 0; j < sideSize; j++)
                mGrid[i][j] = ' ';
    }

    // Retourne la taille d'un côté de la grille
    public int getSideSize()
    {
        return mSideSize;
    }

    // Retourne le caractère de la case (i, j), ou ' ' si hors grille
    public char getCase(int i, int j)
    {
        if (!isValidCell(i, j)) return ' ';
        return mGrid[i][j];
    }

    // Modifie le contenu de la case (i, j) si elle existe
    public void setCase(int i, int j, char type)
    {
        if (isValidCell(i, j))
            mGrid[i][j] = type;
    }

    // Remet la case (i, j) à vide
    public void clearCase(int i, int j)
    {
        if (isValidCell(i, j))
            mGrid[i][j] = ' ';
    }

    // Vérifie que (i, j) est bien dans les limites de la grille
    public boolean isValidCell(int i, int j)
    {
        return i >= 0 && i < mSideSize && j >= 0 && j < mSideSize;
    }

    // Convertit la grille en chaîne de caractères, une ligne par rangée, pour la sauvegarde ou le debug
    public String toAscii()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mSideSize; i++)
        {
            for (int j = 0; j < mSideSize; j++)
                sb.append(mGrid[i][j]);
            sb.append('\n');
        }
        return sb.toString();
    }
}
