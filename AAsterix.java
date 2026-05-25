import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Pathfinding A* pour le clic-deplacement.
 */
public final class AAsterix {
    private static final int[] D_ROW = {-1, 1, 0, 0};
    private static final int[] D_COL = {0, 0, -1, 1};

    public List<Pair> aStarSearch(int[][] grid, int rows, int cols, Pair src, Pair dest) {
        if (!isValid(rows, cols, src) || !isValid(rows, cols, dest)) {
            return null;
        }
        if (src.equals(dest)) {
            return new ArrayList<>();
        }

        boolean[][] closedList = new boolean[rows][cols];
        Cell[][] cellDetails = new Cell[rows][cols];

        int si = src.getFirst();
        int sj = src.getSecond();
        cellDetails[si][sj] = new Cell();
        cellDetails[si][sj].f = 0.0;
        cellDetails[si][sj].g = 0.0;
        cellDetails[si][sj].h = 0.0;
        cellDetails[si][sj].parent = new Pair(si, sj);

        PriorityQueue<Details> openList = new PriorityQueue<>((a, b) -> Double.compare(a.getValue(), b.getValue()));
        openList.add(new Details(0.0, si, sj));

        while (!openList.isEmpty()) {
            Details current = openList.poll();
            int i = current.getI();
            int j = current.getJ();
            closedList[i][j] = true;

            for (int k = 0; k < 4; k++) {
                int ni = i + D_ROW[k];
                int nj = j + D_COL[k];
                Pair neighbour = new Pair(ni, nj);

                if (!isValid(rows, cols, neighbour)) {
                    continue;
                }

                if (cellDetails[ni][nj] == null) {
                    cellDetails[ni][nj] = new Cell();
                }

                if (neighbour.equals(dest)) {
                    cellDetails[ni][nj].parent = new Pair(i, j);
                    return tracePath(cellDetails, src, dest);
                }

                if (closedList[ni][nj] || !isUnBlocked(grid, rows, cols, neighbour)) {
                    continue;
                }

                double gNew = cellDetails[i][j].g + 1.0;
                double hNew = calculateHValue(neighbour, dest);
                double fNew = gNew + hNew;

                if (cellDetails[ni][nj].f < 0 || cellDetails[ni][nj].f > fNew) {
                    openList.add(new Details(fNew, ni, nj));
                    cellDetails[ni][nj].f = fNew;
                    cellDetails[ni][nj].g = gNew;
                    cellDetails[ni][nj].h = hNew;
                    cellDetails[ni][nj].parent = new Pair(i, j);
                }
            }
        }

        return null;
    }

    private static boolean isValid(int rows, int cols, Pair point) {
        return rows > 0
            && cols > 0
            && point.getFirst() >= 0
            && point.getFirst() < rows
            && point.getSecond() >= 0
            && point.getSecond() < cols;
    }

    private static boolean isUnBlocked(int[][] grid, int rows, int cols, Pair point) {
        return isValid(rows, cols, point) && grid[point.getFirst()][point.getSecond()] == 1;
    }

    private static double calculateHValue(Pair src, Pair dest) {
        return Math.abs(src.getFirst() - dest.getFirst()) + Math.abs(src.getSecond() - dest.getSecond());
    }

    private static List<Pair> tracePath(Cell[][] cellDetails, Pair src, Pair dest) {
        Stack<Pair> stack = new Stack<>();
        int row = dest.getFirst();
        int col = dest.getSecond();

        while (!(row == src.getFirst() && col == src.getSecond())) {
            stack.push(new Pair(row, col));
            Pair parent = cellDetails[row][col].parent;
            row = parent.getFirst();
            col = parent.getSecond();
        }

        List<Pair> path = new ArrayList<>();
        while (!stack.isEmpty()) {
            path.add(stack.pop());
        }
        return path;
    }
}
