/*
    Auteur: Abdelrahmane Issa
*/

import java.util.ArrayList;
import javafx.scene.image.Image;

public class PlayerPath extends GameObject
{
    private static final Image sPathLineTexture = GraphicHelper.loadImage("file:assets/Textures/PathLine.png");
    private static final int sPathLineLayer = 29;

    private BoxPlayer mPlayer;
    private ArrayList<int[]> mPath;
    private GameObject[] mPathLines;
    private int mIndex;
    private boolean mActive;

    @Override public void onCreate()
    {
        mPlayer = null;
        mPath = null;
        mPathLines = null;
        mIndex = 0;
        mActive = false;
    }

    @Override public void onDestroy()
    {
        mPlayer = null;
        mPath = null;

        this.destroyPathLines();
    }

    @Override public void update()
    {
        // Vérifications
        if (!mActive || mPlayer == null || mPath == null) return;

        if (mPlayer.getParent() == null)
        {
            mActive = false;
            this.destroy();
            return;
        }

        // Attendre que le joueur soit immobile avant de donner la prochaine direction
        if (!mPlayer.isState(Box.sWait)) return;

        // Si on a atteint la fin du chemin on quitte
        if (mIndex >= mPath.size())
        {
            this.cancel();
            return;
        } 

        // Cellule actuelle du joueur et de la prochaine position
        int[] currentCell = new int[]{ mPlayer.getCellI(), mPlayer.getCellJ() };
        int[] targetCell = mPath.get(mIndex);
        
        // Si le joueur est déjà sur la prochaine case cible, avancer dans le chemin
        if (currentCell[0] == targetCell[0] && currentCell[1] == targetCell[1])
        {
            mPathLines[mIndex].setHidden(true);
            mIndex ++;
            
            if (mIndex >= mPath.size())
            {
                this.cancel();
                return;
            }
            targetCell = mPath.get(mIndex);
        }

        // Calculer la direction et demander le mouvement
        int di = (targetCell[0] - currentCell[0]);
        int dj = (targetCell[1] - currentCell[1]);
        Box.Direction dir = getDirectionFromDelta(di, dj);

        // Si le joueur est bloqué on quitte
        if (dir != Box.Direction.None)
        {
            if (!mPlayer.tryMove(dir))
            { 
                this.cancel();
                return;
            }
        }
    }

    @Override public void setHidden(boolean hidden)
    {
        if (mPathLines == null)
        {
            return;
        }

        for (int i = 0; i < mPathLines.length; i++)
        {
            if (i >= mIndex)
            {
                mPathLines[i].setHidden(hidden);
            }
        }
    }

    public ArrayList<int[]> getPath()
    {
        return mPath;
    }

    public void init(BoxPlayer player, ArrayList<int[]> path)
    {
        mPlayer = player;
        mPath = path;
        mIndex = 0;
        mActive = (path != null && path.size() > 1);

        this.buildPathLines();
    }
    
    public boolean isActive()
    {
        return mActive;
    }
    
    public void cancel()
    {
        mActive = false;
        this.destroy();
    }
    
    private Box.Direction getDirectionFromDelta(int di, int dj)
    {
        if (di == 1  && dj == 0) return Box.Direction.Right;
        if (di == -1 && dj == 0) return Box.Direction.Left;
        if (di == 0  && dj == 1) return Box.Direction.Down;
        if (di == 0  && dj == -1) return Box.Direction.Up;
        return Box.Direction.None;
    }

    private float getRotationFromDelta(int di, int dj)
    {
        if (di == 1  && dj == 0) return 90.0f;
        if (di == -1 && dj == 0) return -90.0f;
        if (di == 0  && dj == 1) return 180.0f;
        if (di == 0  && dj == -1) return 0.0f;
        return 0.0f;
    }

    private void buildPathLines()
    {
        if (mPath == null)
        {
            return;
        }

        this.destroyPathLines();
        mPathLines = new GameObject[mPath.size()];

        BoxRecursive parentBox = mPlayer.getParent();
        float currentRotation = 0.0f;

        for (int i = 0; i < mPath.size(); i++)
        {
            mPathLines[i] = GraphicHelper.createImageObj(
                Box.sBoxSize - 30,
                Box.sBoxSize - 30,
                sPathLineTexture,
                sPathLineLayer).getGameObject();
            
            int cellI = mPath.get(i)[0];
            int cellJ = mPath.get(i)[1];

            if (i < mPath.size() - 1)
            {
                int nextCellI = mPath.get(i + 1)[0];
                int nextCellJ = mPath.get(i + 1)[1];
                
                currentRotation = this.getRotationFromDelta(
                    nextCellI - cellI,
                    nextCellJ - cellJ);
            }
            
            mPathLines[i].setPosition(parentBox.getPositionFromCell(cellI, cellJ));
            mPathLines[i].setScale(parentBox.getChildBoxScale());
            mPathLines[i].setRotation(currentRotation);
        }
    }

    private void destroyPathLines()
    {
        if (mPathLines == null)
        {
            return;
        }

        for (int i = 0; i < mPathLines.length; i++)
        {
            mPathLines[i].destroy();
        }

        mPathLines = null;
    }
}
