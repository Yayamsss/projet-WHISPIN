/*Auteur : AIT MEDDOUR Sami*/

/*
    Gère la logique de l'éditeur de niveau : sélection d'outil, modification de la grille,
    vérification de jouabilité, sauvegarde et chargement depuis un fichier ASCII.
    Utilisée par EditorView pour répondre aux interactions de l'utilisateur.
*/
import java.io.*;

public class LevelEditor
{
    private Level mLevel;         // grille en cours d'édition
    private char mSelectedTool;   // outil actif : '#', '$', '@', '.', ou ' ' pour la gomme

    // Crée un éditeur avec une grille vide de taille sideSize, outil par défaut : mur
    public LevelEditor(int sideSize)
    {
        mLevel = new Level(sideSize);
        mSelectedTool = Tool.WALL.getChar();
    }

    // Retourne la grille en cours d'édition
    public Level getLevel()
    {
        return mLevel;
    }

    // Change l'outil actif
    public void setSelectedTool(char tool)
    {
        mSelectedTool = tool;
    }

    // Retourne le caractère de l'outil actuellement sélectionné
    public char getSelectedTool()
    {
        return mSelectedTool;
    }

    // Applique l'outil actif sur la case (i, j) : place un élément ou efface selon l'outil
    public void applyTool(int i, int j)
    {
        if (!mLevel.isValidCell(i, j)) return;

        if (mSelectedTool == Tool.ERASE.getChar())
            mLevel.clearCase(i, j);
        else
            mLevel.setCase(i, j, mSelectedTool);
    }

    // Efface la case (i, j) sans tenir compte de l'outil sélectionné
    public void eraseCell(int i, int j)
    {
        mLevel.clearCase(i, j);
    }

    // Recrée une grille vide de taille newSideSize, l'ancienne grille est perdue
    public void resize(int newSideSize)
    {
        mLevel = new Level(newSideSize);
    }

    // Retourne true si le niveau est jouable (un seul joueur et au moins un objectif)
    public boolean canBePlayed()
    {
        return getPlayabilityError() == null;
    }

    // Retourne null si le niveau est jouable, sinon un message d'erreur explicite
    public String getPlayabilityError()
    {
        int playerCount = countChar('@') + countChar('+');
        int goalCount   = countChar('.') + countChar('*') + countChar('+');

        if (playerCount == 0) return "Place un joueur avant de jouer.";
        if (playerCount > 1)  return "Il ne faut qu'un seul joueur.";
        if (goalCount == 0)   return "Place au moins un objectif.";

        return null;
    }

    // Compte le nombre de cases contenant le caractère target dans la grille
    private int countChar(char target)
    {
        int count = 0;
        int size  = mLevel.getSideSize();

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (mLevel.getCase(i, j) == target) count++;

        return count;
    }

    // Sauvegarde la grille dans un fichier ASCII, format : "A <taille>" puis les lignes de la grille
    public boolean save(String filePath)
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath)))
        {
            int size = mLevel.getSideSize();
            writer.write("A " + size);
            writer.newLine();

            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < size; j++)
                    writer.write(mLevel.getCase(i, j));
                writer.newLine();
            }

            return true;
        }
        catch (IOException e)
        {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            return false;
        }
    }

    // Charge la première grille trouvée dans le fichier et reconstruit le niveau
    public boolean load(String filePath)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
        {
            String line;

            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");
                if (parts.length >= 2)
                {
                    int size;
                    try { size = Integer.parseInt(parts[1]); }
                    catch (NumberFormatException e) { continue; }

                    mLevel = new Level(size);

                    for (int i = 0; i < size; i++)
                    {
                        String gridLine = reader.readLine();
                        if (gridLine == null) gridLine = "";

                        for (int j = 0; j < size; j++)
                        {
                            // Si la ligne est plus courte que la grille, on complète avec des espaces
                            char c = (j < gridLine.length()) ? gridLine.charAt(j) : ' ';
                            mLevel.setCase(i, j, c);
                        }
                    }

                    return true; // on ne lit que la première world du fichier
                }
            }
            return false;
        }
        catch (IOException e)
        {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            return false;
        }
    }
}
