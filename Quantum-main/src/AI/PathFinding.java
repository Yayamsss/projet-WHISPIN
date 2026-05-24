/*
    Auteur: Abdelrahmane Issa
*/

import java.util.Queue;
import java.util.LinkedList;
import java.util.Collections;
import java.util.ArrayList;

public class PathFinding
{
    private PathFinding() {}

    public static ArrayList<int[]> findPath(BoxRecursive rootBox, int[] start, int[] end)
    {
        // Initialisation des variables
        int gridSize = rootBox.getSideSize();

        int[] dx = { 1, -1, 0, 0 };
        int[] dy = { 0, 0, 1, -1 };

        Queue<Node> queue = new LinkedList<>();
        Node startNode = new Node(start[0], start[1], null);
        queue.add(startNode);

        boolean[][] visited = new boolean[gridSize][gridSize];

        while (!queue.isEmpty())
        {
            // 1. Défiler current dans queue
            Node current = queue.poll();

            // 2. Regarder la tête (x, y) de current
            int x = current.x;
            int y = current.y;

            // 3. si on tombe sur la case end, on renvoie le chemin
            if (x == end[0] && y == end[1])
            {
                ArrayList<int[]> path = new ArrayList<>();
                Node p = current;
                while (p != null)
                {
                    path.add(new int[] { p.x, p.y });
                    p = p.pParent;
                }

                Collections.reverse(path); // On inverse le chemin
                return path;
            }

            // 4. Si hors limites
            if (x < 0 || x >= gridSize || y < 0 || y >= gridSize)
            {
                continue;
            }

            // 5. Si la case est non libre ou bien la case de départ
            boolean isStart = (x == start[0] && y == start[1]);
            if (!isStart && !rootBox.isCellEmpty(x, y))
            {
                continue;
            }

            // 6. si la case est déjà visité
            if (visited[x][y])
            {
                continue;
            }

            // 7. ajouter current dans visited
            visited[x][y] = true;

            // 8. Faire quatre copie de current (copie gratuite grâce à Node)
            for (int i = 0; i < 4; i++)
            {
                // 9. Empiler les voisins dans chaque copie
                Node next = new Node(x + dx[i], y + dy[i], current);
                // 10. Enfiler dans queue
                queue.add(next);
            }
        }
        
        return new ArrayList<int[]>(); // Aucun chemin
    }
}