/* 
    Auteur: Yanis Achab
*/

import java.util.*;
import java.io.*;

public class LevelLoader
{
    private static class BoxLocation
    {
        char pParentWorldName;
        int pI;
        int pJ;

        public BoxLocation(char parentName, int _i, int _j)
        {
            pParentWorldName = parentName;
            pI = _i;
            pJ = _j;
        }
    }

    private static int sRootSize = 0;
    private static Character sRootName = null;
    private static HashMap<Character, BoxRecursive> sWorlds = null;
    private static HashMap<Character, String[]> sAsciiWorlds = null;
    private static ArrayList<BoxLocation> sPendingGoals = null;
    private static BoxLocation sPendingPlayerGoal = null;
    private static BoxLocation sPendingSpawn = null;

    private static boolean isInteger(String str)
    {
        if (str == null || str.isEmpty())
        {
            return false;
        }
        
        for (int i = 0; i < str.length(); i++)
        {
            if (!Character.isDigit(str.charAt(i)))
            {
                return false;
            }
        }

        return true;
    }
    
    private static void readFile(String filePath) throws IOException, Exception
    {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null)
        {
            line = line.trim();

            if (line.isEmpty())
            {
                continue;
            }

            String[] elements = line.split(" ");

            if (elements.length >= 2 && isInteger(elements[1]))
            {
                char worldName = elements[0].charAt(0);
                int worldSize = Integer.parseInt(elements[1]);
                String[] gridLines = new String[worldSize];

                BoxRecursive newWorld = GameLoop.getInstance().addObject(BoxRecursive.class);

                newWorld.setSideSize(worldSize);
                sWorlds.put(Character.valueOf(worldName), newWorld);

                for (int i = 0; i < worldSize; i++)
                {
                    String gridLine = reader.readLine();
                    gridLines[i] = (gridLine != null) ? gridLine : "";
                }

                sAsciiWorlds.put(worldName, gridLines);
            }
        }

        reader.close();

        // Identification de la RootBox : le monde référencé par personne
        Set<Character> rootCandidates = new HashSet<>(sAsciiWorlds.keySet());
        for (String[] gridLines : sAsciiWorlds.values())
        {
            for (String gridLine : gridLines)
            {
                for (int k = 0; k < gridLine.length(); k++)
                {
                    char c = gridLine.charAt(k);
                    if (Character.isLetter(c))
                    {
                        rootCandidates.remove(Character.toUpperCase(c));
                    }
                }
            }
        }

        if (rootCandidates.size() != 1)
        {
            throw new Exception("Impossible de déterminer la rootBox.");
        }

        // On détruit la rootbox pour la reconstruire après avec BoxManager
        sRootName = rootCandidates.iterator().next();
        sRootSize = sWorlds.get(sRootName).getSideSize();
        sWorlds.get(sRootName).destroy();
        sWorlds.put(sRootName, null);
    }

    /*
        Construit récursivement le contenu d'une BoxRecursive
        exactement comme initDebug — le parent est déjà positionné
        et dimensionné quand on remplit ses enfants.
    */
    private static void buildWorld(BoxRecursive world, Character worldName)
    {
        char worldNameChar = worldName.charValue();
        String[] gridLines = sAsciiWorlds.get(worldName);
        int size = world.getSideSize();

        for (int i = 0; i < size; i++)
        {
            String gridLine = (i < gridLines.length) ? gridLines[i] : "";

            for (int j = 0; j < size; j++)
            {
                char cell = (j < gridLine.length()) ? gridLine.charAt(j) : ' ';

                switch (cell)
                {
                    case ' ': break;
                    case '#':
                    {
                        world.addBox(BoxStatic.class, j, i);
                    }
                    break;

                    case '$':
                    {
                        world.addBox(BoxRegular.class, j, i);
                    }
                    break;

                    case '*':
                    {
                        world.addBox(BoxRegular.class, j, i);
                        sPendingGoals.add(new BoxLocation(worldNameChar, j, i));
                    }
                    break;

                    case '.':
                    {
                        sPendingGoals.add(new BoxLocation(worldNameChar, j, i));
                    }
                    break;

                    case '@':
                    {
                        sPendingSpawn = new BoxLocation(worldNameChar, j, i);
                    }
                    break;

                    case '+':
                    {
                        sPendingSpawn = new BoxLocation(worldNameChar, j, i);
                        sPendingGoals.add(new BoxLocation(worldNameChar, j, i));
                    }
                    break;

                    case ':':
                    {
                        sPendingPlayerGoal = new BoxLocation(worldNameChar, j, i);
                    }
                    break;

                    default:
                    {
                        if (Character.isLetter(cell))
                        {
                            Character childName = Character.toUpperCase(cell);

                            if (sWorlds.containsKey(childName))
                            {
                                BoxRecursive childWorld = sWorlds.get(childName);
                                world.setBox(childWorld, j, i);
                                
                                buildWorld(childWorld, childName);
                            }
                        }
                        else
                        {
                            throw new IllegalStateException("Caractère illégal.");
                        }
                    }
                    break;
                }
            }
        }
    }

    public static boolean initLevel(String filePath, BoxManager manager)
    {
        sWorlds = new HashMap<>();
        sAsciiWorlds = new HashMap<>();
        sPendingGoals = new ArrayList<>(); 
        BoxRecursive rootBox;

        try
        {
            readFile(filePath);

            manager.init();
        
            rootBox = manager.getRootBox();
            rootBox.setSideSize(sRootSize);

            sWorlds.put(sRootName, rootBox);
            buildWorld(rootBox, sRootName);

            // On ajoute les goals et spawns en derniers
            for (BoxLocation goalLocation : sPendingGoals)
            {
                GameManager.getInstance().addNewGoal(
                    sWorlds.get(goalLocation.pParentWorldName),
                    goalLocation.pI,
                    goalLocation.pJ);
            }
            
            if(sPendingPlayerGoal != null)
            {
                GameManager.getInstance().addNewGoal(sWorlds.get(sPendingPlayerGoal.pParentWorldName),
                sPendingPlayerGoal.pI,
                sPendingPlayerGoal.pJ);

                BoxGoal playerGoal = GameManager.getInstance().getGoal(
                    GameManager.getInstance().getNumGoals() - 1);
                playerGoal.setGoalType(BoxGoal.GoalType.Player);
            }

            if (sPendingSpawn != null)
            {
                GameLoop.getInstance().addObject(BoxSpawn.class);
                BoxSpawn.getInstance().setCell(
                    sWorlds.get(sPendingSpawn.pParentWorldName),
                    sPendingSpawn.pI,
                    sPendingSpawn.pJ);
            }
            else
            {
                throw new Exception("Aucun point d'apparition trouvé.");
            }
        }
        catch (Exception e)
        {
            System.out.println("[LevelLoader] Erreur lors de la lecture du fichier: " + e.getMessage());

            Set<Character> worldNames = sWorlds.keySet();
            for (Character name : worldNames)
            {
                sWorlds.get(name).destroy();
            }
            
            clean();
            return false;
        }

        clean();
        return true;
    }

    private static void clean()
    {
        sWorlds = null;
        sAsciiWorlds = null;
        sPendingGoals = null;
        sPendingPlayerGoal = null;
        sPendingSpawn = null;
        sRootName = null;
    }
}