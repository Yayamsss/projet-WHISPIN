import javafx.scene.paint.Color;

public class ElementCar extends GraphicElement
{
    private GraphicRectangle mCar;
    private GraphicRectangle mBody;
    private GraphicRectangle mFrontWindow;
    private GraphicRectangle mRearWindow;
    private GraphicLine mLine;

    private Direction mDirection;
    
    /* Attribut pour la voiture */
    private static final float sCarWidth = 77;
    private static final float sCarHeight = 34;
    private static final float sCarBorderRadius = 25;
    private static final int sCarLayer = 0;

    /* Attribut pour la vitre avant */
    private static final float sFrontWindowWitdh = 22;
    private static final float sFrontWindowHeight = 14;
    private static final float  sFrontWindowBordeRadius = 8;

    /* Attribut pour la vitre arrière */
    private static final float sRearWindowWitdh = 14;
    private static final float sRearWindowHeight = 11;
    private static final float  sRearWindowBorderRadius = 7;

    /* Attribut pour la ligne */
    private static final float sLineHeight = 52;
    private static final float sLineWitdh = 3;

    /* Attribut pour les états (pour la vitesse de la gova) */
    private static final float sCarSpeed = 22;
    private static final float sOffsetLimit = 100;

    private static final GameObjectState<ElementCar> sMoove = new GameObjectState<ElementCar>() {
        public void execute(ElementCar obj) { obj.exeMoove(); }
    };

    public enum Direction
    {
        toLeft,
        toRight
    }

    @Override public void onCreate() 
    {
        super.onCreate();

        Color windowColor = sColorTable[mRandom.nextInt(sColorTable.length)];
        
        mCar = this.setGraphicShape(GraphicRectangle.class);
        mCar.init(
            sCarWidth, 
            sCarHeight, 
            this, 
            sColorTable[mRandom.nextInt(sColorTable.length)], 
            sCarLayer, 
            sCarBorderRadius);
        mBody = GraphicHelper.createRectangleObj(
            sCarWidth,
            sCarHeight / 2,
            sCarBorderRadius,
            mCar.getColor().darker(),
            sCarLayer + 1);
        mFrontWindow = GraphicHelper.createRectangleObj(
            sFrontWindowWitdh,
            sFrontWindowHeight,
            sFrontWindowBordeRadius,
            windowColor,
            sCarLayer + 1);
        mRearWindow = GraphicHelper.createRectangleObj(
            sRearWindowWitdh,
            sRearWindowHeight,
            sRearWindowBorderRadius,
            windowColor,
            sCarLayer + 1);
        mLine = GraphicHelper.createLineObj(
            sLineHeight,
            sLineWitdh,
            0.0f,
            windowColor,
            sCarLayer);
        
        mShapes.add(mCar);
        mShapes.add(mBody);
        mShapes.add(mFrontWindow);
        mShapes.add(mRearWindow);
        mShapes.add(mLine);

        mDirection = null;
        setState(sWait);
    }

    public GraphicRectangle getCar()
    {
        return mCar;
    }

    public GraphicRectangle getBody()
    {
        return mBody;
    }

    public GraphicRectangle getFrontWindow()
    {
        return mFrontWindow;
    }

    public GraphicRectangle getRearWindow()
    {
        return mRearWindow;
    }

    public GraphicLine getLine()
    {
        return mLine;
    }

    public void setDirection(Direction direction)
    {
        mDirection = direction;
    }

    @Override protected void exeWait() 
    {
        if (mDirection == null)
        {
            return;
        }

        setState(sMoove);
    }

    private void exeMoove()
    {
        if (isFirstStep())
        {
            setNewRandomColor();
            calcNewPos(mDirection == Direction.toLeft ? Direction.toRight : Direction.toLeft);
        }

        boolean isFinished = false;
        if (mDirection == Direction.toRight)
        {
            isFinished = exeMooveToRight();
        }
        else
        {
            isFinished = exeMooveToLeft();
        }

        if (isFinished)
        {
            setState(sWait);
        }
    }

    private boolean exeMooveToRight()
    {
        float newPosX = this.getPosition().x + sCarSpeed;
        if (newPosX - mCar.getWitdh() / 2 < GraphicsModule.getWitdh() + sOffsetLimit)
        {
            calcPosToRight(new Vector2(newPosX, this.getPosition().y));
            return false;
        }
        return true;
    }

    private boolean exeMooveToLeft()
    {
        float newPosX = this.getPosition().x - sCarSpeed;
        if (-sOffsetLimit < newPosX + mCar.getWitdh() / 2)
        {
            calcPosToLeft(new Vector2(newPosX, this.getPosition().y));
            return false;
        }
        return true;
    }

    private void calcNewPos(Direction direction)
    {
        mDirection = direction;
        if (direction == Direction.toLeft)
        {
            calcPosToLeft(new Vector2(
                GraphicsModule.getWitdh() + sOffsetLimit,
                mRandom.nextFloat(
                    800,
                    GraphicsModule.getHeight() - 25
                )
            ));
        }
        else
        {
            calcPosToRight(new Vector2(
                -sOffsetLimit,
                mRandom.nextFloat(
                    800,
                    GraphicsModule.getHeight() - 25
                )
            ));
        }
    }

    private void calcPosToLeft(Vector2 newPos)
    {
        this.setPosition(newPos);

        float quarterWidth = mCar.getWitdh() / 4;
        float quarterHeight = mCar.getHeight() / 4;

        mBody.getGameObject().setPosition(new Vector2(
            newPos.x,
            newPos.y + quarterHeight));
        mFrontWindow.getGameObject().setPosition(new Vector2(
            newPos.x - quarterWidth,
            newPos.y - quarterHeight));
        mRearWindow.getGameObject().setPosition(new Vector2(
            newPos.x + quarterWidth,
            newPos.y - quarterHeight ));
        mLine.getGameObject().setPosition(new Vector2(
            newPos.x - mCar.getWitdh() / 2 - mLine.getHeight(),
            newPos.y));
    }

    private void calcPosToRight(Vector2 newPos)
    {
        this.setPosition(newPos);

        float quarterWidth = mCar.getWitdh() / 4;
        float quarterHeight = mCar.getHeight() / 4;

        mBody.getGameObject().setPosition(new Vector2(
            newPos.x,
            newPos.y + quarterHeight));
        mFrontWindow.getGameObject().setPosition(new Vector2(
            newPos.x + quarterWidth,
            newPos.y - quarterHeight));
        mRearWindow.getGameObject().setPosition(new Vector2(
            newPos.x - quarterWidth,
            newPos.y - quarterHeight));
        mLine.getGameObject().setPosition(new Vector2(
            newPos.x + mCar.getWitdh() / 2,
            newPos.y));
    }

    private void setNewRandomColor()
    {
        Color newColor = sColorTable[mRandom.nextInt(sColorTable.length)];
        Color newWindowColor = newColor;

        mCar.setColor(newColor);
        mBody.setColor(newColor.darker());
        
        do
        {
            newWindowColor = sColorTable[mRandom.nextInt(sColorTable.length)];    
        } while (newWindowColor == newColor);
        
        mFrontWindow.setColor(newColor);
        mRearWindow.setColor(newColor);
        mLine.setColor(newColor);
    }
}
