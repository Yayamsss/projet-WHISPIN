/* 
    Auteur: Yanis Achab
*/

import java.util.*;
import java.io.*;

public class LevelSaver
{
    private static void collectWorlds(BoxRecursive box, ArrayList<BoxRecursive> worlds)
    {
        int size = box.getSideSize(); // CORRIGÉ: getRowSize() -> getSideSize()

        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                if (!box.isCellEmpty(i, j) && box.getChildBox(i, j).getBoxType() == Box.BoxType.Recursive)
                {
                    collectWorlds((BoxRecursive) box.getChildBox(i, j), worlds);
                }
            }
        }

        worlds.add(box);
    }

    private static char getCharFromCell(BoxRecursive world, int i, int j, ArrayList<BoxRecursive> worlds)
    {
        // Si la cellule est vide, on écrit un espace

        boolean isGoal = false;
        boolean isPlayerGoal = false;
        GameManager gm = GameManager.getInstance();

        for (int k = 0; k < gm.getNumGoals(); k++)
        {
            BoxGoal goal = gm.getGoal(k); // CORRIGÉ: GoalObject -> BoxGoal
            
            if (goal.getParent() == world // CORRIGÉ: getParentBox() -> getParent()
             && goal.getCellI() == i
             && goal.getCellJ() == j)
            {
                isGoal = true;
                isPlayerGoal = (goal.getGoalType() == BoxGoal.GoalType.Player);
                break;
            }
        }

        if (world.isCellEmpty(i, j))
        {
            if (isPlayerGoal)
            {
                return ':';
            }
            else if (isGoal)
            {
                return '.';
            }
            else
            {
                return ' ';
            }
        }

        Box box = world.getChildBox(i, j);

        switch (box.getBoxType())
        {
            case Static: return '#';
            case Regular:
            {
                if (isGoal || isPlayerGoal)
                {
                    return '*';
                }
                else
                {
                    return '$';
                }
            }
            case Player:
            {
                if (isGoal || isPlayerGoal)
                {
                    return '+';
                }
                else
                {
                    return '@';
                }
            }
            case Recursive:
            {
                int index = worlds.indexOf(box);
                char letter = (char) ('a' + index);

                if (index == -1)
                {
                    return '?';
                }
                else if (isGoal || isPlayerGoal)
                {
                    return Character.toUpperCase(letter);
                }

                return letter;
            }

            default: return ' ';
        }
    }

    public static boolean saveLevel(String filePath, BoxRecursive rootBox)
    {
        if (rootBox == null)
        {
            return false;
        }

        ArrayList<BoxRecursive> worlds = new ArrayList<>();
        collectWorlds(rootBox, worlds);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath)))
        {
            char worldName = 'A';

            for (BoxRecursive world : worlds)
            {
                int size = world.getSideSize(); // CORRIGÉ: getRowSize() -> getSideSize()
                writer.write(worldName + " " + size);
                writer.newLine();

                for (int i = 0; i < size; i++)
                {
                    for (int j = 0; j < size; j++)
                    {
                        writer.write(getCharFromCell(world, j, i, worlds));
                    }

                    writer.newLine();
                }

                worldName++;
            }
        }
        catch (IOException e)
        {
            System.err.println("Erreur lors de la sauvegarde du niveau : " + e.getMessage());
            return false;
        }

        return true;
    }
}