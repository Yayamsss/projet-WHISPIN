/*
    Auteur: Abderrachid BELLOUM
*/

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class AutoSolver extends GameObject
{
    private boolean mActive;
    private PlayerPath mCurrentPath;
    private int mGoalIndex;

    @Override public void onCreate()
    {
        mActive = false;
        mCurrentPath = null;
        mGoalIndex = 0;
    }

    @Override public void onDestroy()
    {
        stopSolving();
    }

    @Override public void onEnabled()
    {
        if (mCurrentPath != null) mCurrentPath.setHidden(false);
    }

    @Override public void onDisabled()
    {
        if (mCurrentPath != null) mCurrentPath.setHidden(true);
    }

    public boolean isActive()
    {
        return mActive;
    }

    public void startSolving()
    {
        clearPath();
        mGoalIndex = 0;
        mActive = true;
    }

    public void stopSolving()
    {
        mActive = false;
        clearPath();
    }

    @Override public void update()
    {
        if (!mActive)
        {
            return;
        }

        GameManager gm = GameManager.getInstance();
        BoxPlayer player = gm.getPlayer();

        if (!gm.isGameRuning())
        {
            return;
        }

        if (mCurrentPath != null)
        {
            if (mCurrentPath.isActive())
            {
                return;
            }

            mCurrentPath = null;
        }

        while (mGoalIndex < gm.getNumGoals() && gm.getGoal(mGoalIndex).checkGoalReached())
        {
            mGoalIndex++;
        }

        if (mGoalIndex >= gm.getNumGoals())
        {
            mActive = false;
            return;
        }

        BoxGoal currentGoal = gm.getGoal(mGoalIndex);
        ArrayList<int[]> path = findGlobalPath(player, currentGoal);

        if (path == null || path.size() <= 1)
        {
            System.out.println("AutoSolver: aucun chemin trouve pour le goal " + mGoalIndex);
            mActive = false;
            return;
        }

        mCurrentPath = GameLoop.getInstance().addObject(PlayerPath.class);
        mCurrentPath.init(player, path);
    }

    private ArrayList<int[]> findGlobalPath(BoxPlayer player, BoxGoal goal)
    {
        if (player.getParent() != goal.getParent())
        {
            return findPathToTargetWorld(player, goal.getParent());
        }

        if (goal.getGoalType() == BoxGoal.GoalType.Player)
        {
            ArrayList<int[]> path = PathFinding.findPath(
                player.getParent(),
                new int[] { player.getCellI(), player.getCellJ() },
                new int[] { goal.getCellI(), goal.getCellJ() }
            );

            if (path != null && path.size() > 1)
            {
                mGoalIndex++;
            }

            return path;
        }

        ArrayList<int[]> path = solveBoxGoal(player, goal);
        if (path != null && path.size() > 1)
        {
            mGoalIndex++;
        }

        return path;
    }

    private ArrayList<int[]> findPathToTargetWorld(BoxPlayer player, BoxRecursive targetWorld)
    {
        BoxRecursive currentWorld = player.getParent();
        int[] playerCell = new int[] { player.getCellI(), player.getCellJ() };

        if (worldContains(currentWorld, targetWorld))
        {
            BoxRecursive childToReach = getFirstWorldOnPath(currentWorld, targetWorld);
            if (childToReach == null)
            {
                return null;
            }

            return PathFinding.findPath(
                currentWorld,
                playerCell,
                new int[] { childToReach.getCellI(), childToReach.getCellJ() }
            );
        }

        return findPathToExitCurrentWorld(currentWorld, playerCell, targetWorld);
    }

    private ArrayList<int[]> findPathToExitCurrentWorld(BoxRecursive currentWorld, int[] playerCell, BoxRecursive targetWorld)
    {
        if (currentWorld == null || currentWorld.getParent() == null)
        {
            return null;
        }

        BoxRecursive parentWorld = currentWorld.getParent();
        int[] nextUsefulCell = getUsefulCellInParent(parentWorld, targetWorld);

        ArrayList<int[]> bestPath = null;
        int bestScore = Integer.MAX_VALUE;

        for (int side = 0; side < 4; side++)
        {
            for (int k = 0; k < currentWorld.getSideSize(); k++)
            {
                int[] exitCell = getExitCell(currentWorld, side, k);
                int[] parentCell = getParentArrivalCell(currentWorld, side);

                if (exitCell == null || parentCell == null)
                {
                    continue;
                }

                if (!parentWorld.isInBound(parentCell[0], parentCell[1]))
                {
                    continue;
                }

                ArrayList<int[]> path = PathFinding.findPath(currentWorld, playerCell, exitCell);
                if (path == null || path.size() <= 1)
                {
                    continue;
                }

                int score = path.size();
                if (nextUsefulCell != null)
                {
                    score += distance(parentCell, nextUsefulCell) * 4;
                }

                if (score < bestScore)
                {
                    bestScore = score;
                    bestPath = path;
                }
            }
        }

        return bestPath;
    }

    private int[] getExitCell(BoxRecursive world, int side, int index)
    {
        switch (side)
        {
            case 0: return new int[] { -1, index };
            case 1: return new int[] { world.getSideSize(), index };
            case 2: return new int[] { index, -1 };
            case 3: return new int[] { index, world.getSideSize() };
            default: return null;
        }
    }

    private int[] getParentArrivalCell(BoxRecursive world, int side)
    {
        switch (side)
        {
            case 0: return new int[] { world.getCellI() - 1, world.getCellJ() };
            case 1: return new int[] { world.getCellI() + 1, world.getCellJ() };
            case 2: return new int[] { world.getCellI(), world.getCellJ() - 1 };
            case 3: return new int[] { world.getCellI(), world.getCellJ() + 1 };
            default: return null;
        }
    }

    private int[] getUsefulCellInParent(BoxRecursive parentWorld, BoxRecursive targetWorld)
    {
        if (parentWorld == null)
        {
            return null;
        }

        if (parentWorld == targetWorld)
        {
            return new int[] { targetWorld.getSideSize() / 2, targetWorld.getSideSize() / 2 };
        }

        BoxRecursive nextWorld = getFirstWorldOnPath(parentWorld, targetWorld);
        if (nextWorld == null)
        {
            return null;
        }

        return new int[] { nextWorld.getCellI(), nextWorld.getCellJ() };
    }

    private BoxRecursive getFirstWorldOnPath(BoxRecursive startWorld, BoxRecursive targetWorld)
    {
        for (int i = 0; i < startWorld.getSideSize(); i++)
        {
            for (int j = 0; j < startWorld.getSideSize(); j++)
            {
                if (startWorld.isCellEmpty(i, j))
                {
                    continue;
                }

                Box child = startWorld.getChildBox(i, j);
                if (!child.isBoxOfType(Box.BoxType.Recursive))
                {
                    continue;
                }

                BoxRecursive childWorld = (BoxRecursive) child;
                if (childWorld == targetWorld || worldContains(childWorld, targetWorld))
                {
                    return childWorld;
                }
            }
        }

        return null;
    }

    private boolean worldContains(BoxRecursive startWorld, BoxRecursive targetWorld)
    {
        if (startWorld == targetWorld)
        {
            return true;
        }

        for (int i = 0; i < startWorld.getSideSize(); i++)
        {
            for (int j = 0; j < startWorld.getSideSize(); j++)
            {
                if (startWorld.isCellEmpty(i, j))
                {
                    continue;
                }

                Box child = startWorld.getChildBox(i, j);
                if (!child.isBoxOfType(Box.BoxType.Recursive))
                {
                    continue;
                }

                if (worldContains((BoxRecursive) child, targetWorld))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private ArrayList<int[]> solveBoxGoal(BoxPlayer player, BoxGoal goal)
    {
        BoxRecursive world = goal.getParent();
        int size = world.getSideSize();

        boolean[][] walls = new boolean[size][size];
        HashSet<Integer> boxSet = new HashSet<>();

        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                if (world.isCellEmpty(i, j))
                {
                    continue;
                }

                Box child = world.getChildBox(i, j);
                if (child == player)
                {
                    continue;
                }

                if (child.isBoxOfType(Box.BoxType.Regular))
                {
                    boxSet.add(cellToInt(i, j, size));
                }
                else
                {
                    walls[i][j] = true;
                }
            }
        }

        int goalPos = cellToInt(goal.getCellI(), goal.getCellJ(), size);
        if (boxSet.contains(goalPos))
        {
            ArrayList<int[]> path = new ArrayList<>();
            path.add(new int[] { player.getCellI(), player.getCellJ() });
            return path;
        }

        SolverNode start = new SolverNode(player.getCellI(), player.getCellJ(), boxSet, null);
        Queue<SolverNode> queue = new LinkedList<>();
        HashMap<String, Boolean> visited = new HashMap<>();

        queue.add(start);
        visited.put(start.makeKey(), true);

        int[][] dirs = {
            { 1, 0 },
            { -1, 0 },
            { 0, 1 },
            { 0, -1 }
        };

        while (!queue.isEmpty())
        {
            SolverNode current = queue.poll();

            if (current.mBoxes.contains(goalPos))
            {
                return rebuildPath(current);
            }

            for (int d = 0; d < 4; d++)
            {
                int nextI = current.mPlayerI + dirs[d][0];
                int nextJ = current.mPlayerJ + dirs[d][1];

                if (!inBound(nextI, nextJ, size))
                {
                    continue;
                }

                if (walls[nextI][nextJ])
                {
                    continue;
                }

                int nextPos = cellToInt(nextI, nextJ, size);

                if (!current.mBoxes.contains(nextPos))
                {
                    SolverNode next = new SolverNode(nextI, nextJ, current.mBoxes, current);
                    addNode(queue, visited, next);
                    continue;
                }

                int pushI = nextI + dirs[d][0];
                int pushJ = nextJ + dirs[d][1];

                if (!inBound(pushI, pushJ, size))
                {
                    continue;
                }

                if (walls[pushI][pushJ])
                {
                    continue;
                }

                int pushPos = cellToInt(pushI, pushJ, size);
                if (current.mBoxes.contains(pushPos))
                {
                    continue;
                }

                HashSet<Integer> newBoxes = new HashSet<>(current.mBoxes);
                newBoxes.remove(nextPos);
                newBoxes.add(pushPos);

                SolverNode next = new SolverNode(nextI, nextJ, newBoxes, current);
                addNode(queue, visited, next);
            }
        }

        return null;
    }

    private void addNode(Queue<SolverNode> queue, HashMap<String, Boolean> visited, SolverNode node)
    {
        String key = node.makeKey();
        if (visited.containsKey(key))
        {
            return;
        }

        visited.put(key, true);
        queue.add(node);
    }

    private ArrayList<int[]> rebuildPath(SolverNode node)
    {
        ArrayList<int[]> path = new ArrayList<>();
        SolverNode current = node;

        while (current != null)
        {
            path.add(new int[] { current.mPlayerI, current.mPlayerJ });
            current = current.mParent;
        }

        Collections.reverse(path);
        return path;
    }

    private int distance(int[] a, int[] b)
    {
        return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
    }

    private boolean inBound(int i, int j, int size)
    {
        return i >= 0 && i < size && j >= 0 && j < size;
    }

    private int cellToInt(int i, int j, int size)
    {
        return i * size + j;
    }

    private void clearPath()
    {
        if (mCurrentPath != null)
        {
            mCurrentPath.cancel();
            mCurrentPath = null;
        }
    }

    private static class SolverNode
    {
        int mPlayerI;
        int mPlayerJ;
        HashSet<Integer> mBoxes;
        SolverNode mParent;

        SolverNode(int playerI, int playerJ, HashSet<Integer> boxes, SolverNode parent)
        {
            mPlayerI = playerI;
            mPlayerJ = playerJ;
            mBoxes = new HashSet<>(boxes);
            mParent = parent;
        }

        String makeKey()
        {
            ArrayList<Integer> sorted = new ArrayList<>(mBoxes);
            Collections.sort(sorted);
            return mPlayerI + ":" + mPlayerJ + ":" + sorted.toString();
        }
    }
}
