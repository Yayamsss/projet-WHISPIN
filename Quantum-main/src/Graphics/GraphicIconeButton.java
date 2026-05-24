/*
    Auteur : Ali GÜRKAN
*/

public class GraphicIconeButton extends GraphicButton 
{   
    private final static float sMinRotation = 0.0f;
    private final static float sMaxRotation = 90.0f;
    private final static int sRotationTime = 15;

    private final static GameObjectState<GraphicIconeButton> sRotation = new GameObjectState<GraphicIconeButton>() {
        public void execute(GraphicIconeButton obj) {
            obj.exeRotation();
        };
    };

    private float mMinRotation;
    private float mMaxRotation;
    private float mInterpolation;

    @Override public void onCreate()
    {
        mCallBack = null;

        this.initShape(0.0f, 0.0f);
        this.initText(sTextSize);
        this.initLines();

        mTopLine.setWitdh(0.0f);
        mBottomLine.setWitdh(0.0f);

        mMinRotation = sMinRotation;
        mMaxRotation = sMaxRotation;
        mInterpolation = 0.0f;
        
        this.setState(sRotation);
    }

    @Override public void setHidden(boolean hidden)
    {
        mShape.setHiden(hidden);
        mTextShape.setHiden(hidden);
        mTopLine.setHiden(hidden);
        mBottomLine.setHiden(hidden);
    }

    public float getMinRotation()
    {
        return mMinRotation;
    }

    public float getMaxRotation()
    {
        return mMaxRotation;
    }

    public void setMinRotation(float _minRotation)
    {
        mMinRotation = _minRotation;
    }

    public void setMaxRotation(float _maxRotation)
    {
        mMaxRotation = _maxRotation;
    }

    public void disableRotation()
    {
        this.setRotation(0.0f);
        this.setState(sWait);
    }

    private void calcRotation()
    {
        float sign = this.isMouseOnButton() ? 1.0f : -1.0f;

        mInterpolation = Math.clamp(
            mInterpolation + sign * (1.0f / sRotationTime),
            0.0f,
            1.0f);

        this.setRotation(
            Interpolation.linear(
                mMinRotation,
                mMaxRotation,
                Easing.easeInCubic(mInterpolation)));
    }

    private void exeRotation()
    {
        this.calcRotation();
        this.tryCallBack();
    }

    @Override protected void onEnabled()
    {
        this.setHidden(false);
    }

    @Override protected void onDisabled()
    {
        super.onDisabled();
        mInterpolation = 0.0f;
    }
}
