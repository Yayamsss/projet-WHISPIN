/*
    Auteur: Ryane Menaï
*/

/* Class abstraite représentant une boîte. */

import javafx.scene.paint.Color;

public abstract class Box extends GameObjectEx
{
    /* Enum représantant un type de boîte. */
    public enum BoxType
    {
        /* Une boîte basique, remplis et qui peut être poussé par le joueur. */
        Regular,
        /* Une boîte basique, remplis et qui ne peut pas être poussé par le joueur. */
        Static,
        /* Une boîte qui contient d'autres boîtes. */
        Recursive,
        /* Le player est une boîte aussi */
        Player,
        /* Une boîte qui ne possède pas de collision. */
        Empty,
        /* Une boîte ou le joueur apparait */
        Spawn,
        /* Une boîte représentant un objectif */
        Goal,
        
        None
    }

    /* Une direction de mouvement */
    public enum Direction
    {
        Up,
        Down,
        Right,
        Left,
        None
    }

    /* Taille par défault d'une boîte */
    public static final float sBoxSize = 100;
    /* Les différentes couleurs d'une boîte */
    public static final Color[] sBoxColors = new Color[] {
        Color.rgb(0xD6, 0xB4, 0xFC),
        Color.rgb(0xE2, 0xDB, 0xBE),
        Color.rgb(0x51, 0x76, 0x64),
        Color.rgb(0xAB, 0x34, 0x28),
        Color.rgb(0x19, 0x13, 0x08)
    };

    /* Variables d'états */
    protected static final GameObjectState<Box> sWait = new GameObjectState<>() {
        @Override public void execute(Box b) { b.exeWait(); }
    };
    protected static final GameObjectState<Box> sMove = new GameObjectState<>() {
        @Override public void execute(Box b) { b.exeMove(); }
    };
    protected static final GameObjectState<Box> sTransition = new GameObjectState<>() {
        @Override public void execute(Box b) { b.exeTransition(); }
    };

    /* Le temps que prends le mouvement (en nombre d'images) */
    protected static final int sMoveTime = 24;

    /* Dans quel boîte (de type Recursive) se trouve cette boîte. */
    protected BoxRecursive mParent;

    /* La cellule courante de la boîte, mis à jours par BoxRecursive. */
    protected int mCellI;
    protected int mCellJ;

    /* Les boxes sont des carrés (logique) */
    protected GraphicShape mShape;

    /* Taille du côté */
    protected float mDimension;

    /* Position de départ du mouvement */
    protected Vector2 mMoveStartPosition;
    /* Position d'arriver du mouvement */
    protected Vector2 mMoveTargetPosition;
    /* Grossissement de départ du mouvement */
    protected float mStartScale;
    /* Grossissement d'arriver du mouvement */
    protected float mTargetScale;

    /* La direction dans laquel on se dirige */
    protected Direction mCurrentMoveDirection;

    /* Renvoie le type de boîte */
    public abstract BoxType getBoxType();
    /* Renvoie si la boîte est capable de bouger. */
    public abstract boolean canMove();

    public BoxRecursive getParent()
    {
        return mParent;
    }

    public int getCellI()
    {
        return mCellI;
    }

    public int getCellJ()
    {
        return mCellJ;
    }

    public float getDimension()
    {
        return mDimension;
    }

    /* Renvoie la dimensions mis à l'échelle. */
    public float getScaledDimension()
    {
        return this.getScale() * mDimension;
    }

    /* Renvoie un nouveau vecteur correspondant à une direction de mouvement */
    protected Vector2 getMoveVectorFromEnum(Direction dir)
    {
        switch (dir)
        {
            case Up: return new Vector2(0.0f, -1.0f);
            case Down: return new Vector2(0.0f, 1.0f);
            case Right: return new Vector2(1.0f, 0.0f);
            case Left: return new Vector2(-1.0f, 0.0f);
            case None: return new Vector2(0.0f, 0.0f);
            default: return null;
        }
    }

    /* Renvoie la direction inverse de dir */
    protected Direction getOpositeDirection(Direction dir)
    {
        switch (dir)
        {
            case Up: return Direction.Down;
            case Down: return  Direction.Up;
            case Right: return Direction.Left;
            case Left: return  Direction.Right;
            case None: return  Direction.None;
            default: return null;
        }
    }

    /* Revoie vrai si la boîte est du même genre que type */
    public boolean isBoxOfType(BoxType type)
    {
        return this.getBoxType() == type;
    }

    /* Renvoie vrai si cette boîte est celle qui est au sommet de l'arbre */
    public boolean isRootBox()
    {
        return mParent == null;
    }

    /* Renvoie la profondeur actuelle de la boîte par rapport à la boîte racine */
    public int calcDepth()
    {
        int depth = 0;
        Box currentBox = this;

        while (!currentBox.isRootBox())
        {
            depth++;
            currentBox = currentBox.getParent();
        }

        return depth;
    }

    public void setDimension(float newDimension)
    {
        mDimension = newDimension;

        if (mShape != null)
        {
            mShape.setWitdh(newDimension);
            mShape.setHeight(newDimension);
        }
    }

    @Override public void onCreate()
    {
        mCellI = 0;
        mCellJ = 0;
        mParent = null;

        mMoveStartPosition = new Vector2();
        mMoveTargetPosition = new Vector2();

        this.setDimension(sBoxSize);
        this.setState(sWait);

        if (mShape == null)
        {
            mShape = this.setGraphicShape(GraphicRectangle.class);
            ((GraphicRectangle) mShape).init(
                mDimension,
                mDimension,
                this,
                sBoxColors[0],
                0,
                0);
        }
    }

    @Override public void onStart()
    {
        if (mParent == null)
        {
            return;
        }

        mShape.setLayer(mParent.getGraphicShape().getLayer() + 1);
    }

    /* Met à jours la position de la boîte par rapport à sa cellule */
    protected void updatePositionFromCell()
    {
        if (this.isRootBox())
        {
            throw new IllegalCallerException("Impossible d'appeler setPositionFromCell pour la root box.");
        }
        
        this.setPosition(mParent.getPositionFromCell(mCellI, mCellJ));
    }

    /* Essaie de faire bouger la boîte dans la direction dir, fait potentiellement bouger
       sa voisine, et fait potentiellement changer de parent. Renvoie vrai si le déplacement
       à eu lieu, faux sinon. */
    protected boolean tryMove(Direction dir)
    {
        if (this.isRootBox())
        {
            throw new IllegalCallerException("Impossible d'appeler tryMove pour la root box.");
        }
        else if (!this.canMove())
        {
            return false;
        }

        Vector2 moveVector = Vector2.mul(this.getMoveVectorFromEnum(dir), mParent.getChildBoxScaledDimension());
        Vector2 nextPosition = Vector2.add(this.getPosition(), moveVector);
        int[] targetCell = mParent.getCellFromPosition(nextPosition);
        Box neighbour =
            mParent.isInBound(targetCell[0], targetCell[1])
          ? mParent.getChildBox(targetCell[0], targetCell[1])
          : null;
        BoxRecursive parentOfParent = neighbour == null ? null : neighbour.getParent();

        /* Si on est reste dans les limites */
        if (mParent.isInBound(targetCell[0], targetCell[1]))
        {
            /* Empêche les boîtes autres que Player d'aller sur la même cellule que
               le point de départ. */
            if (!this.isBoxOfType(BoxType.Player))
            {
                BoxSpawn spwan = BoxSpawn.getInstance();

                if (spwan.getParent() == this.mParent
                 && spwan.getCellI() == targetCell[0]
                 && spwan.getCellJ() == targetCell[1])
                {
                    return false;
                }
            }

            if (neighbour == null || neighbour.tryMove(dir))
            {
                /* On bouge si il n'y a pas de voisin, ou si le voisin à réussi à bouger. */
                this.startMove(dir, targetCell[0], targetCell[1]);
                return true;
            }
            else
            {
                if (!neighbour.isBoxOfType(BoxType.Recursive))
                {
                    return false;
                }

                /* Sinon, si le voisin est une boîte recusive, on essaye de rentrer dedans. */
                return this.tryEnterTransition(neighbour, dir);
            }
        }
        else
        {
            if (mParent.isRootBox())
            {
                return false;
            }

            /* Sinon, on essaye de sortir du parent courrant. */
            
            parentOfParent = mParent.mParent;
            targetCell = parentOfParent.getCellFromPosition(nextPosition);

            if (!parentOfParent.isInBound(targetCell[0], targetCell[1]))
            {
                return false;
            }

            if (parentOfParent.isCellEmpty(targetCell[0], targetCell[1])
             || parentOfParent.getChildBox(targetCell[0], targetCell[1]).tryMove(dir))
            {
                this.doEscapeTransition(parentOfParent, dir, targetCell[0], targetCell[1]);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    /* Démare la séquence de mouvement vers la cellule (cellI, cellJ) */
    protected void startMove(Direction dir, int cellI, int cellJ)
    {
        this.sendMsg(UndoManager.getInstance(), UndoManager.MessageType.SaveMove.ordinal());
        this.startMoveNoCellUpdate(dir, cellI, cellJ);
        mParent.updateBoxCell(this, cellI, cellJ);
    }

    /* Démare la séquence de mouvement vers la cellule (cellI, cellJ) sans mettre
       à jour la cellule courante */
    protected void startMoveNoCellUpdate(Direction dir, int cellI, int cellJ)
    {
        mMoveStartPosition.set(this.getPosition());
        mMoveTargetPosition.set(mParent.getPositionFromCell(cellI, cellJ));
        mCurrentMoveDirection = dir;

        this.setState(sMove);
    }

    /* Démare la transition quand on sort du parent */
    protected void doEscapeTransition(BoxRecursive newParent, Direction dir, int cellI, int cellJ)
    {
        this.sendMsg(UndoManager.getInstance(), UndoManager.MessageType.SaveMove.ordinal());

        mMoveStartPosition.set(this.getPosition());
        mMoveTargetPosition.set(newParent.getPositionFromCell(cellI, cellJ));
        mCurrentMoveDirection = dir;

        mStartScale = this.getScale();
        mTargetScale = newParent.getChildBoxScale();
        
        /* On s'efface de l'ancient parent. */
        mParent.setBoxNoTransformUpdate(null, mCellI, mCellJ);
        /* Puis on s'incruste dans le nouveau parent. */
        newParent.setBoxNoTransformUpdate(this, cellI, cellJ);

        this.setState(sTransition);
    }

    /* Démare la transition quand on entre dans un parent (si possible).
       Renvoie vrai quand la transition réussi, faux sinon */
    protected boolean tryEnterTransition(Box targetBox, Direction dir)
    {
        if (!targetBox.isBoxOfType(BoxType.Recursive))
        {
            return false;
        }

        BoxRecursive newParent = (BoxRecursive) targetBox;
        /* Essaie de trouver une cellule d'entrée. */
        int[] targetCell = newParent.tryMoveBoxAtEdge(this.getOpositeDirection(dir));

        if (targetCell == null)
        {
            return false;
        }

        this.sendMsg(UndoManager.getInstance(), UndoManager.MessageType.SaveMove.ordinal());
        
        mMoveStartPosition.set(this.getPosition());
        mMoveTargetPosition = newParent.getPositionFromCell(targetCell[0], targetCell[1]);
        mCurrentMoveDirection = dir;

        mStartScale = this.getScale();
        mTargetScale = newParent.getChildBoxScale();

        /* Même schéma que dans doEscapeTransition */
        mParent.setBoxNoTransformUpdate(null, mCellI, mCellJ);
        newParent.setBoxNoTransformUpdate(this, targetCell[0], targetCell[1]);

        this.setState(sTransition);
        return true;
    }

    protected void exeWait()
    {

    }

    protected void exeMove()
    {
        this.setPosition(
            Vector2.lerp(
                mMoveStartPosition,
                mMoveTargetPosition,
                Easing.easeOutExpo(this.getNormalizedStep(sMoveTime))));
        
        if (this.isStep(sMoveTime))
        {
            this.setState(sWait);
        }
    }

    protected void exeTransition()
    {
        this.setScale(
            Interpolation.linear(
                mStartScale,
                mTargetScale,
                Easing.easeOutQuart((float) this.getStep() / (float) sMoveTime)));
        this.exeMove();
    }
}
