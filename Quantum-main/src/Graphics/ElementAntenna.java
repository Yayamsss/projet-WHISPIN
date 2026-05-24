import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;

public class ElementAntenna extends ElementBuilding
{
    private GraphicLine mBar;
    private GraphicOval mOval;
    private GraphicRectangle mRectangle;

    /* Attribut pour building */
    private static final float sMinWitdh = 122;
    private static final float sMaxWitdh = 155;
    private static final float sMinHeight = 310;
    private static final float sMaxHeight = 430;
    private static final Color sColorBuilding = GraphicHelper.makeColorFromHex(0x14131f);
    private static final int sBuildingLayer = 2;

    /* Atribut pour les fenetres */
    private static final int sMinWindowCount = 3;
    private static final int sMaxWindowCount = 25;
    private static final float sWindowWitdh = 15;
    private static final float sWindowHeight = 25;
    private static final float sWindowBorderRadius = 10;

    /* Attribut pour bar */
    private static final float sLineMinHeight = 75;
    private static final float sLineMaxHeight = 100;
    private static final float sLineWitdh = 5.5f;

    /* Attribut pour oval */
    private static final float sOvalRadius = 15.5f;
    private static final float sOvalOffset = 2.5f;

    private static final GameObjectState<ElementAntenna> sBlink1 = new GameObjectState<ElementAntenna>() {
        public void execute(ElementAntenna obj) {
            obj.exeBlink1();
        }
    };
    private static final GameObjectState<ElementAntenna> sBlink2 = new GameObjectState<ElementAntenna>() {
        public void execute(ElementAntenna obj) {
            obj.exeBlink2();
        };
    };

    @Override public void onCreate()
    {
        mShapes = new ArrayList<>();
        mRandom = new Random();

        mShape = this.setGraphicShape(GraphicRectangle.class);
        mShape.init(
            mRandom.nextFloat(sMinWitdh, sMaxWitdh),
            mRandom.nextFloat(sMinHeight, sMaxHeight),
            this,
            sColorBuilding,
            sBuildingLayer,
            0);

        int numWindow = mRandom.nextInt(sMinWindowCount, sMaxWindowCount + 1);
        Color windowColor = sColorTable[mRandom.nextInt(sColorTable.length)];
        Color darkerWindowColor = windowColor.darker();

        for (int i = 0; i < numWindow; i++)
        {
            if (mRandom.nextInt(3) == 0)
            {
                mShapes.add(null); continue;
            }

            GraphicRectangle window = GraphicHelper.createRectangleObj(
                sWindowWitdh,
                sWindowHeight,
                sWindowBorderRadius,
                (i & 2) == 0 ? windowColor : darkerWindowColor,
                sBuildingLayer + 1);

            mShapes.add(window);
        }

        Color recColor = sColorTable[mRandom.nextInt(sColorTable.length)];

        if (mRandom.nextBoolean())
        {
            recColor = recColor.darker();
        }

        mRectangle = GraphicHelper.createRectangleObj(
            mShape.getWitdh() - 3,
            20,
            25.0f,
            recColor,
            sBuildingLayer + 1);
        mBar = GraphicHelper.createLineObj(
            mRandom.nextFloat(sLineMinHeight, sLineMaxHeight),
            sLineWitdh,
            135,
            sColorTable[mRandom.nextInt(sColorTable.length)],
            sBuildingLayer + 1);
        mOval = GraphicHelper.createOvalObj(
            sOvalRadius,
            sOvalRadius,
            mBar.getColor(),
            sBuildingLayer + 1);
        
        Vector2 currentPosition = this.getPosition();
        
        mRectangle.setPosition(currentPosition);
        mBar.setPosition(new Vector2(
            currentPosition.x,
            currentPosition.y - mShape.getHeight() / 2));
        mOval.setPosition(new Vector2(
            currentPosition.x,
            currentPosition.y - mBar.getHeight() - sOvalOffset));

        mShapes.add(mRectangle);
        mShapes.add(mBar);
        mShapes.add(mOval);

        this.setState(sBlink1);
    }

    @Override public void setPosition(Vector2 newPos)
    {
        super.setPosition(newPos);

        mBar.setPosition(new Vector2(
            newPos.x,
            newPos.y - mShape.getHeight() / 2));
        mOval.setPosition(new Vector2(
            newPos.x,
            mBar.getPosition().y - mBar.getHeight() - sOvalOffset));
        mRectangle.setPosition(newPos);
    }

    private void exeBlink1()
    {
        float newOpacity = mOval.getOpacity() - 0.1f;
        if (newOpacity > 0) {
            mOval.setOpacity(newOpacity);
        } else {
            mOval.setOpacity(0);
            setState(sBlink2);
        }
    }

    private void exeBlink2()
    {
        float newOpacity = mOval.getOpacity() + 0.1f;
        if (newOpacity < 1) {
            mOval.setOpacity(newOpacity);
        } else {
            mOval.setOpacity(1);
            setState(sBlink1);
        }
    }
}