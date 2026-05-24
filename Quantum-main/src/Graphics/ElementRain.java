public class ElementRain extends GraphicElement
{
    private GraphicLine mRain;
    private float mSpeed;
    private float mWaitTime;

    private static final float sHeight = 101;
    private static final float sWitdh = 1.5f;
    private static final int sLayer = 3;
    private static final float sOpacity = 0.8f;
    private static final float sMinSpeed = 25f;
    private static final float sMaxSpeed = 35f;
    private static final float sMaxWaitTime = 0.10f;

    private static final float sOffsetY = -100;

    private static final GameObjectState<ElementRain> sRainDrop = new GameObjectState<ElementRain>() {
        public void execute(ElementRain obj) { obj.exeRainDrop(); }
    };

    @Override public void onCreate() 
    {
        super.onCreate();
        this.setPosition(new Vector2(
            mRandom.nextFloat(0, GraphicsModule.getWitdh()),
            sOffsetY));

        mSpeed = mRandom.nextFloat(sMinSpeed, sMaxSpeed);
        mWaitTime = mRandom.nextFloat(sMaxWaitTime);

        mRain = this.setGraphicShape(GraphicLine.class);
        mRain.init(
            this, 
            sHeight, 
            sWitdh, 
            48, 
            sColorTable[mRandom.nextInt(sColorTable.length)], 
            sLayer);
        mRain.setOpacity(sOpacity);
        mShapes.add(mRain);

        setState(sWait);
    }
    
    @Override protected void exeWait()
    {
        if (isLessOrEqualStep(timeToStep(mWaitTime)))
        {
            return;
        }
        else
        {
            setState(sRainDrop);
        }
    }

    private void exeRainDrop()
    {
        Vector2 currentPos = this.getPosition();
        float newPos = currentPos.y + mSpeed;

        if (newPos <= GraphicsModule.getHeight())
        {
            this.setPosition(new Vector2(currentPos.x, newPos));
        }
        else
        {
            this.setPosition(new Vector2(
                mRandom.nextFloat(0,GraphicsModule.getWitdh()),
                sOffsetY));

            mRain.setColor(sColorTable[mRandom.nextInt(sColorTable.length)]);

            mSpeed = mRandom.nextFloat(sMinSpeed, sMaxSpeed);
            mWaitTime = mRandom.nextFloat(sMaxWaitTime);

            setState(sWait);
        }
    }
}
