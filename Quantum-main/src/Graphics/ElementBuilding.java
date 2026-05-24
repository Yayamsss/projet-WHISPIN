import javafx.scene.paint.Color;

public class ElementBuilding extends GraphicElement
{
    protected GraphicRectangle mShape;

    /* Attribut pour les batiments */
    private static final float sMinWitdh = 104;
    private static final float sMaxWitdh = 125;
    private static final float sMinHeight = 493;
    private static final float sMaxHeight = 603;
    private static final Color sBuildingColor = GraphicHelper.makeColorFromHex(0x15041C);
    private static final int sBuildingLayer = 0;

    /* Attribut pour les fenetres */
    private static final int sMinWindows = 0;
    private static final int sMaxWindows = 60;
    private static final float sWindowWitdh = 15;
    private static final float sWindowHeight = 15;
    private static final float sWindowGap = 10f;
    private static final int sApperanceChance = 3;

    @Override public void onCreate()
    {
        super.onCreate();

        mShape = this.setGraphicShape(GraphicRectangle.class);
        mShape.init(
            mRandom.nextFloat(sMinWitdh, sMaxWitdh),
            mRandom.nextFloat(sMinHeight, sMaxHeight),
            this,
            sBuildingColor,
            sBuildingLayer,
            0);

        int numWindows = mRandom.nextInt(sMinWindows, sMaxWindows + 1);

        for (int i = 0; i < numWindows; i++)
        {
            if (mRandom.nextInt(sApperanceChance) == 0)
            {
                mShapes.add(null); continue;
            }

            GraphicRectangle window = GraphicHelper.createRectangleObj(
                sWindowWitdh,
                sWindowHeight,
                0.0f,
                sColorTable[mRandom.nextInt(sColorTable.length)],
                sBuildingLayer + 1);

            mShapes.add(window);
        }

        setState(sWait);
    }

    @Override public void setPosition(Vector2 newPos)
    {
        super.setPosition(newPos);

        float left = newPos.x - mShape.getWitdh() / 2;
        float top = newPos.y - mShape.getHeight() / 2;

        int column = 0;
        int row = 0;
        float maxColumnsWidth = mShape.getWitdh() - sWindowGap;

        for (int i = 0; i < mShapes.size(); i++)
        {
            float currentX = left + sWindowGap + (column * (sWindowWitdh + sWindowGap));
            float currentY = top + sWindowGap + (row * (sWindowHeight + sWindowGap));

            if (currentX + sWindowWitdh > left + maxColumnsWidth)
            {
                column = 0;
                row++;
                currentX = left + sWindowGap;
                currentY = top + sWindowGap + (row * (sWindowHeight + sWindowGap));
            }

            column++;

            if (mShapes.get(i) == null || !mShapes.get(i).getClass().equals(GraphicRectangle.class))
            {
                continue;
            }

            GraphicRectangle window = (GraphicRectangle) mShapes.get(i);

            window.getGameObject().setPosition(new Vector2(
                currentX + window.getWitdh() / 2,
                currentY + window.getHeight() / 2));
        }
    }

    public GraphicRectangle getBuilding()
    {
        return mShape;
    }
}
