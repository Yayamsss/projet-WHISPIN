/*
    Auteur: Ryane Menaï
    Le monde de départ et la box dans lequel le joueur peut rentrer sont des boxRecursive.
*/

import java.util.ArrayList;
import java.util.LinkedList;
import javafx.scene.paint.Color;

public class BoxRecursive extends Box
{
    public static final int sMaxDepthLevel = 5;
    private static final Color sRootColor = GraphicHelper.makeColorFromHex(0x080118);
    private static final float sBaseBrightenFactor = 1.80f;
    
    /* La liste des boîtes que contient cette boîte. */
    private ArrayList<BoxEmpty> mGrid;
    private ArrayList<Box> mChildBoxes;
    private ArrayList<LinkedList<BoxIntangible>> mIntangibleChilds;
    /* Combien de boîte une ligne contient. La boîte étant toujours un carré, c'est aussi
    le nombre de boîte qu'une colonne contient. */
    private int mSideSize;
    private int mNumCells;
    /* Le grossissement (scale) d'une boîte enfant. */
    private float mChildBoxScaledDimension;
    private float mChildBoxScale;
    private int mId;

    @Override public void onCreate()
    {
        super.onCreate();

        mGrid = null;
        mChildBoxes = null;
        mIntangibleChilds = null;

        mSideSize = 0;
        mNumCells = 0;

        mChildBoxScaledDimension = 0.0f;
        mChildBoxScale = 0;

        mId = '\0';
        mShape.setColor(sRootColor);
        mShape.setLayer(0);
        mShape.setOpacity(0.40f);
        ((GraphicRectangle) mShape).setBorderRadius(15.0f);
    }

    @Override public void onStart()
    {
        super.onStart();
        this.updateColorFromParent();
    }

    @Override public void receiveMsg(GameObject from, int msg)
    {
        if (from instanceof UndoManager)
        {
            this.setScale(mParent.getChildBoxScale());
            this.updatePositionFromCell();
        }
    }

    @Override protected void onEnabled()
    {
        this.updateAllChildEnableState();
    }

    @Override protected void onDisabled()
    {
        this.updateAllChildEnableState();
    }

    @Override public void onDestroy()
    {
        for (int i = 0; i < mNumCells; i++)
        {
            mGrid.get(i).destroy();

            Box currentBox = mChildBoxes.get(i);

            if (currentBox == null || currentBox.isBoxOfType(BoxType.Player))
            {
                continue;
            }

            currentBox.destroy();
        }

        for (int i = 0; i < mNumCells; i++)
        {
            for (BoxIntangible currentBox : mIntangibleChilds.get(i))
            {
                if (currentBox == null)
                {
                    continue;
                }

                currentBox.destroy();
            }

            mIntangibleChilds.get(i).clear();
        }

        mGrid.clear();
        mChildBoxes.clear();
        mIntangibleChilds.clear();
    }

    @Override public BoxType getBoxType()
    {
        return BoxType.Recursive;
    }

    @Override public boolean canMove()
    {
        return true;
    }

    public int getId()
    {
        return mId;
    }

    public int getSideSize()
    {
        return mSideSize;
    }

    public int getNumCells()
    {
        return mNumCells;
    }

    public float getChildBoxScaledDimension()
    {
        return mChildBoxScaledDimension;
    }

    public float getChildBoxScale()
    {
        return mChildBoxScale;
    }

    /* Renvoie la boîte se trouvant à la cellule (i, j) */
    public Box getChildBox(int i, int j)
    {
        if (! this.isInBound(i, j))
        {
            throw new IllegalArgumentException("i et j sont hors limites.");
        }

        return mChildBoxes.get(i * mSideSize + j);
    }

    public BoxIntangible[] getChildIntangibleBoxes(int i, int j)
    {
        if (!this.isInBound(i, j))
        {
            throw new IllegalArgumentException("i et j sont hors limites.");
        }

        int index = this.getIndexFromCell(i, j);
        LinkedList<BoxIntangible> boxesList = mIntangibleChilds.get(index);
        BoxIntangible[] boxesArray = new BoxIntangible[boxesList.size()];

        for (int k = 0; k < boxesList.size(); k++)
        {
            boxesArray[k] = boxesList.get(k);
        }

        return boxesArray;
    }

    /* Convertie (i, j) en indice pour mChildBoxes. */
    private int getIndexFromCell(int i, int j)
    {
        if (! this.isInBound(i, j))
        {
            throw new IllegalArgumentException("i et j sont hors limites.");
        }

        return i * mSideSize + j;
    }

    public int[] getCellFromPosition(Vector2 pos)
    {
        Vector2 localPosition = Vector2.sub(pos, this.caclTopLeft());

        int i = Math.round(localPosition.x / mChildBoxScaledDimension);
        int j = Math.round(localPosition.y / mChildBoxScaledDimension);

        return new int[] { i, j };
    }

    public Vector2 getPositionFromCell(int i, int j)
    {
        if (!this.isInBound(i, j))
        {
            throw new IllegalArgumentException("i et j sont hors limites.");
        }

        Vector2 topLeft = this.caclTopLeft();
        return new Vector2(
            topLeft.x + i * mChildBoxScaledDimension,
            topLeft.y + j * mChildBoxScaledDimension);
    }

    private Vector2 caclTopLeft()
    {
        float halfDimension = this.getScaledDimension() / 2.0f;
        float halfChildDimension = mChildBoxScaledDimension / 2.0f;

        return new Vector2(
            this.getPosition().x - halfDimension + halfChildDimension,
            this.getPosition().y - halfDimension + halfChildDimension);
    }

    private void calcAllScaleData()
    {
        mChildBoxScaledDimension = this.getScaledDimension() / mSideSize;
        mChildBoxScale = mChildBoxScaledDimension / sBoxSize;
    }

    /* Retourne vrai si la cellule (i, j) se trouve dans les limites de cette boîte. */
    public boolean isInBound(int i, int j)
    {
        return i >= 0 && i < mSideSize && j >= 0 && j < mSideSize;
    }

    public boolean isCellEmpty(int i, int j)
    {
        if (! this.isInBound(i, j))
        {
            throw new IllegalArgumentException("i et j sont hors limites.");
        }

        return mChildBoxes.get(this.getIndexFromCell(i, j)) == null;
    }

    public boolean isPositionEmpty(Vector2 pos)
    {
        int[] cell = this.getCellFromPosition(pos);

        return this.isInBound(cell[0], cell[1]) && this.isCellEmpty(cell[0], cell[1]);
    }

    public void setId(int newId)
    {
        mId = newId;
    }

    /* Met à jour la taille d'une ligne. Ceci à pour effet d'ecraser les données déjà existante.
    Cette fonction ne devrait être appelé qu'au moment de la création de la boîte. */
    public void setSideSize(int newSideSize)
    {
        if (newSideSize < 1)
        {
            throw new IllegalArgumentException("newSideSize ne peut être inférieur à un.");
        }

        if (mSideSize != 0)
        {
            // Permet de détruire toutes les boîtes si il y en avait déjà
            this.onDestroy();
        }

        mSideSize = newSideSize;
        mNumCells = newSideSize * newSideSize;
        mChildBoxes = new ArrayList<Box>(mNumCells);
        mGrid = new ArrayList<BoxEmpty>(mNumCells);
        mIntangibleChilds = new ArrayList<LinkedList<BoxIntangible>>(mNumCells);

        if (this.isRootBox())
        {
            this.setScale(newSideSize);
        }
        else
        {
            this.calcAllScaleData();
        }

        for (int i = 0; i < mSideSize; i++)
        {
            for (int j = 0; j < mSideSize; j++)
            {
                BoxEmpty emptyBox = GameLoop.getInstance().addObject(BoxEmpty.class);

                mGrid.add(emptyBox);
                emptyBox.mCellI = i;
                emptyBox.mCellJ = j;
                emptyBox.mParent = this;
                emptyBox.getGraphicShape().setLayer(mShape.getLayer() + 1);
                emptyBox.updatePositionFromCell();
                emptyBox.setScale(mChildBoxScale);

                mChildBoxes.add(null);
                mIntangibleChilds.add(new LinkedList<BoxIntangible>());
            }
        }
    }

    /* Met à jour la cellule (i, j) pour contenir newBox. */
    public void setBox(Box newBox, int i, int j)
    {
        this.setBoxNoTransformUpdate(newBox, i, j);

        if (newBox != null)
        {
            newBox.setScale(mChildBoxScale);
            newBox.updatePositionFromCell();
        }
    }

    public void removeBox(int i, int j)
    {
        Box boxToRemove = this.getChildBox(i, j);
        
        if (boxToRemove == null)
        {
            return;
        }

        mChildBoxes.set(this.getIndexFromCell(i, j), null);
        boxToRemove.destroy();
    }

    public void setBoxNoTransformUpdate(Box newBox, int i, int j)
    {
        if (! this.isInBound(i, j))
        {
            throw new IllegalArgumentException("i et j sont hors limites.");
        }

        int index = this.getIndexFromCell(i, j);

        if (mChildBoxes.get(index) != null)
        {
            mChildBoxes.get(index).mParent = null;
        }

        if (newBox != null)
        {
            newBox.mCellI = i;
            newBox.mCellJ = j;
            newBox.mParent = this;
            newBox.getGraphicShape().setLayer(mShape.getLayer() + 1);
        }

        mChildBoxes.set(index, newBox);
    }

    public void addIntangibleBox(BoxIntangible newBox, int i, int j)
    {
        if (! this.isInBound(i, j))
        {
            throw new IllegalArgumentException("i et j sont hors limites.");
        }
        else if (newBox == null)
        {
            throw new IllegalArgumentException("newBox est null.");
        }

        mIntangibleChilds.get(this.getIndexFromCell(i, j)).add(newBox);

        newBox.mCellI = i;
        newBox.mCellJ = j;
        newBox.mParent = this;
        newBox.updatePositionFromCell();
        newBox.setScale(mChildBoxScale);
        newBox.getGraphicShape().setLayer(mShape.getLayer() + 1);
    }

    public void removeIntangibleBox(BoxIntangible boxToRemove)
    {
        if (boxToRemove.getParent() != this)
        {
            throw new IllegalArgumentException("boxToRemove ne fait pas parti de cette BoxRecursive.");
        }

        int[] cell = this.getCellFromPosition(boxToRemove.getPosition());

        boxToRemove.mParent = null;
        mIntangibleChilds.get(this.getIndexFromCell(cell[0], cell[1])).remove(boxToRemove);
        boxToRemove.destroy();
    }

    public void removeIntangibleBox(int i, int j)
    {
        this.removeIntangibleBox(i, j, 0);
    }

    public void removeIntangibleBox(int i, int j, int n)
    {
        if (!this.isInBound(i, j))
        {
            throw new IllegalArgumentException("i et j sont hors limites.");
        }

        int index = this.getIndexFromCell(i, j);

        if (mIntangibleChilds.get(index).size() <= n)
        {
            return;
        }
        
        BoxIntangible boxToRemove = mIntangibleChilds.get(index).get(n);
        mIntangibleChilds.get(index).remove(n);

        boxToRemove.destroy();
    }

    protected void updateBoxCell(Box b, int i, int j)
    {
        if (!this.isInBound(i, j))
        {
            throw new IllegalArgumentException("i et j sont hors limites.");
        }
        else if (!this.isCellEmpty(i, j))
        {
            throw new IllegalArgumentException("Impossible d'update la cellule d'une boîte vers une cellule non vide.");
        }

        if (b.mParent != this)
        {
            throw new IllegalCallerException("Box n'est pas un enfant.");
        }

        mChildBoxes.set(this.getIndexFromCell(b.mCellI, b.mCellJ), null);
        mChildBoxes.set(this.getIndexFromCell(i, j), b);

        b.mCellI = i;
        b.mCellJ = j;
    }

    public <B extends Box> void addBox(Class<B> boxClass, int i, int j)
    {
        if (boxClass.isAssignableFrom(BoxIntangible.class))
        {
            this.addIntangibleBox(((BoxIntangible) GameLoop.getInstance().addObject(boxClass)), i, j);
        }
        else
        {
            this.setBox(GameLoop.getInstance().addObject(boxClass), i, j);
        }
    }

    public void swapBoxes(int i0, int j0, int i1, int j1)
    {
        if (!this.isInBound(i0, j0) || !this.isInBound(i1, j1))
        {
            throw new IllegalArgumentException("(i0, j0) ou (i1, j1) est hors limites.");
        }

        Box box0 = this.getChildBox(i0, j0);
        Box box1 = this.getChildBox(i1, j1);

        mChildBoxes.set(this.getIndexFromCell(i0, j0), box1);
        mChildBoxes.set(this.getIndexFromCell(i1, j1), box0);

        box0.startMoveNoCellUpdate(Direction.None, i1, j1);
        box1.startMoveNoCellUpdate(Direction.None, i0, j0);
    }

    private void updateAllChildEnableState()
    {
        for (int i = 0; i < mNumCells; i++)
        {
            mGrid.get(i).setEnabled(this.isEnabled());

            Box currentBox = mChildBoxes.get(i);

            if (currentBox == null)
            {
                continue;
            }

            currentBox.setEnabled(this.isEnabled());
        }

        for (int i = 0; i < mNumCells; i++)
        {
            for (BoxIntangible currentBox : mIntangibleChilds.get(i))
            {
                if (currentBox == null)
                {
                    continue;
                }

                currentBox.setEnabled(this.isEnabled());
            }
        }
    }

    private void updateColorFromParent()
    {
        if (this.isRootBox())
        {
            return;
        }

        Color parentColor = mParent.getGraphicShape().getColor();
        float brightFactor = sBaseBrightenFactor * (1.0f / (float) this.calcDepth());
        mShape.setColor(Color.rgb(
            (int) (parentColor.getRed() * brightFactor * 255.0f),
            (int) (parentColor.getGreen() * brightFactor * 255.0f),
            (int) (parentColor.getBlue() * brightFactor * 255.0f)));
        
        for (int i = 0; i < mNumCells; i++)
        {
            if (mChildBoxes.get(i) == null)
            {
                continue;
            }

            if (mChildBoxes.get(i).isBoxOfType(BoxType.Recursive))
            {
                ((BoxRecursive) mChildBoxes.get(i)).updateColorFromParent();
            }
        }
    }

    private void updateLayerFromParent()
    {
        if (this.isRootBox())
        {
            return;
        }

        mShape.setLayer(mParent.getGraphicShape().getLayer() + 1);
        int chidLayer = mShape.getLayer() + 1;

        for (int i = 0; i < mNumCells; i++)
        {
            mGrid.get(i).getGraphicShape().setLayer(chidLayer);

            for (BoxIntangible currentBox : mIntangibleChilds.get(i))
            {
                currentBox.getGraphicShape().setLayer(chidLayer);
            }

            if (mChildBoxes.get(i) == null)
            {
                continue;
            }
            else if (mChildBoxes.get(i).isBoxOfType(BoxType.Recursive))
            {
                ((BoxRecursive) mChildBoxes.get(i)).updateLayerFromParent();
            }
            else
            {
                mChildBoxes.get(i).getGraphicShape().setLayer(chidLayer);
            }
        }
    }

    private void updateAllChildPosition()
    {
        if (mGrid == null || mGrid.isEmpty())
        {
            return;
        }

        for (int i = 0; i < mSideSize; i++)
        {
            for (int j = 0; j < mSideSize; j++)
            {
                int index = this.getIndexFromCell(i, j);

                mGrid.get(index).updatePositionFromCell();

                if (mChildBoxes.get(index) != null)
                {
                    mChildBoxes.get(index).updatePositionFromCell();
                }

                for (BoxIntangible currentBox : mIntangibleChilds.get(index))
                {
                    currentBox.updatePositionFromCell();
                }
            }
        }
    }

    private void updateAllChildScale()
    {
        float currentChildBoxScaledDim = this.getScaledDimension() / mSideSize;
        float currentChildBoxScale = currentChildBoxScaledDim / sBoxSize;

        if (mGrid == null || mGrid.isEmpty())
        {
            return;
        }

        for (int i = 0; i < mSideSize; i++)
        {
            for (int j = 0; j < mSideSize; j++)
            {
                int index = this.getIndexFromCell(i, j);

                mGrid.get(index).setScale(currentChildBoxScale);

                if (mChildBoxes.get(index) != null)
                {
                    mChildBoxes.get(index).setScale(currentChildBoxScale);
                }

                for (BoxIntangible currentBox : mIntangibleChilds.get(index))
                {
                    currentBox.setScale(currentChildBoxScale);
                }
            }
        }
    }

    protected int[] tryMoveBoxAtEdge(Direction dir)
    {
        int rowIncrValue;
        int columnIncrValue;

        int row;
        int column;

        switch (dir) {
            case Up:
            {
                row = 0;
                column = 0;
                rowIncrValue = 0;
                columnIncrValue = 1;
            } break;
            case Down:
            {
                row = mSideSize - 1;
                column = 0;
                rowIncrValue = 0;
                columnIncrValue = 1;
            }
            break;
            case Right:
            {
                row = 0;
                column = mSideSize - 1;
                rowIncrValue = 1;
                columnIncrValue = 0;
            }
            break;
            case Left:
            {
                row = 0;
                column = 0;
                rowIncrValue = 1;
                columnIncrValue = 0;
            }
            break;
            default: return null;
        }

        for (int i = 0; i < mSideSize; i++)
        {
            if (this.isCellEmpty(column, row) || this.getChildBox(column, row).tryMove(this.getOpositeDirection(dir)))
            {
                return new int[] { column, row };
            }

            column += columnIncrValue;
            row += rowIncrValue;
        }

        return null;
    }

    @Override protected void exeMove()
    {
        super.exeMove();
        this.updateAllChildPosition();
    }

    @Override protected void exeTransition()
    {
        if (this.isFirstStep())
        {
            this.updateColorFromParent();
            this.updateLayerFromParent();

            // mChildBoxScaledDimension = (mDimension * mTargetScale) / mSideSize;
            // mChildBoxScale = mChildBoxScaledDimension / sBoxSize;
        }

        super.exeTransition();
    }

    @Override public void setPosition(Vector2 newPosition)
    {
        super.setPosition(newPosition);
        this.updateAllChildPosition();
    }

    @Override public void setScale(float newScale)
    {
        super.setScale(newScale);
        this.calcAllScaleData();
        this.updateAllChildScale();
    }
}
