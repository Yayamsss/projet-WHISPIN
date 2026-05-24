/*
    Auteur: Ryane Menaï
*/

public class GameCamera extends GameObjectEx
{
    private static GameCamera sInstance = null;

    private static final GameObjectState<GameCamera> sWait = new GameObjectState<>() {
        @Override public void execute(GameCamera c) { c.exeWait(); }
    };
    private static final GameObjectState<GameCamera> sFocus = new GameObjectState<>() {
        @Override public void execute(GameCamera c) { c.exeFocus(); }
    };
    private static final GameObjectState<GameCamera> sMove = new GameObjectState<>() {
        @Override public void execute(GameCamera c) { c.exeMove(); }
    };

    private static final int sFocusTime = 40;
    private static final int sMoveTime = 50;

    private float mRight;
    private float mLeft;
    private float mBottom;
    private float mTop;
    private float mAspectRatio;

    private float[] mStartFrustum;
    private float[] mTargetFrustum;

    private Vector2 mStartPosition;
    private Vector2 mTartgetPosition;

    private Matrix3x2 mViewMtx;
    private Matrix3x2 mProjectionMtx;
    private Matrix3x2 mViewProjectionMtx;

    public static GameCamera getInstance()
    {
        return sInstance;
    }

    public float getRight()
    {
        return mRight;
    }

    public float getLeft()
    {
        return mLeft;
    }

    public float getBottom()
    {
        return mBottom;
    }

    public float getTop()
    {
        return mTop;
    }

    public float getAspectRatio()
    {
        return mAspectRatio;
    }

    public void focus(Box boxToFocus)
    {
        Vector2 boxPosition = boxToFocus.getPosition();
        float horizontal = boxToFocus.getScaledDimension() + (boxToFocus.isRootBox() ? 0.0f : 10.0f * boxToFocus.getScale());
        float vertical = horizontal * mAspectRatio;

        mTargetFrustum[0] = boxPosition.x + horizontal;
        mTargetFrustum[1] = boxPosition.x - horizontal;
        mTargetFrustum[2] = boxPosition.y + vertical;
        mTargetFrustum[3] = boxPosition.y - vertical;

        mStartFrustum[0] = mRight;
        mStartFrustum[1] = mLeft;
        mStartFrustum[2] = mBottom;
        mStartFrustum[3] = mTop;

        if (this.isState(sMove))
        {
            this.setPosition(mTartgetPosition);
        }

        this.setState(sFocus);
    }

    public void focus(Vector2 position, float factor)
    {
        if (factor < 0.000001f)
        {
            throw new IllegalArgumentException("Le facteur zoom doit être supérieur à 0.");
        }

        factor = 1.0f / factor;

        mStartFrustum[0] = mRight;
        mStartFrustum[1] = mLeft;
        mStartFrustum[2] = mBottom;
        mStartFrustum[3] = mTop;

        float halfWidth = ((mRight - mLeft) / 2.0f) * factor;
        float halfHeight = ((mBottom - mTop) / 2.0f) * factor;

        mTargetFrustum[0] = position.x + halfWidth;
        mTargetFrustum[1] = position.x - halfWidth;
        mTargetFrustum[2] = position.y + halfHeight;
        mTargetFrustum[3] = position.y - halfHeight;

        if (this.isState(sMove))
        {
            this.setPosition(mTartgetPosition);
        }

        this.setState(sFocus);
    }

    public void move(Vector2 targetPosition)
    {
        mStartPosition.set(this.getPosition());
        mTartgetPosition.set(targetPosition);

        if (this.isState(sFocus))
        {
            this.setFrustum(
                mTargetFrustum[0],
                mTargetFrustum[1],
                mTargetFrustum[2],
                mTargetFrustum[3]);
        }

        this.setState(sMove);
    }

    public void resetFocus()
    {
        mStartFrustum[0] = mRight;
        mStartFrustum[1] = mLeft;
        mStartFrustum[2] = mBottom;
        mStartFrustum[3] = mTop;

        mTargetFrustum[0] = 1920.0f;
        mTargetFrustum[1] = 0.0f;
        mTargetFrustum[2] = 1080.0f;
        mTargetFrustum[3] = 0.0f;

        if (this.isState(sMove))
        {
            this.setPosition(mTartgetPosition);
        }

        this.setState(sFocus);
    }

    private void setFrustum(float right, float left, float top, float bottom)
    {
        mRight = right;
        mLeft = left;
        mBottom = top;
        mTop = bottom;
    }

    @Override public void onCreate()
    {
        if (sInstance == null)
        {
            sInstance = this;
            
            mStartFrustum = new float[4];
            mTargetFrustum = new float[4];
            mViewMtx = new Matrix3x2();
            mProjectionMtx = new Matrix3x2();
            mViewProjectionMtx = new Matrix3x2();
            mAspectRatio = 1080.0f / 1920.0f;

            mStartPosition = new Vector2();
            mTartgetPosition = new Vector2();

            this.setState(sWait);
            this.setFrustum(1920.0f, 0.0f, 1080.0f, 0.0f);
        }
        else
        {
            this.destroy();
        }
    }

    @Override public void onDestroy()
    {
        if (sInstance == this)
        {
            sInstance = null;
        }
    }

    private void exeWait()
    {

    }

    private void exeFocus()
    {
        float normalizedStepTime = this.getNormalizedStep(sFocusTime);
        float interpolationFactor = Easing.easeOutQuart(normalizedStepTime);

        this.setFrustum(
            Interpolation.linear(mStartFrustum[0], mTargetFrustum[0], interpolationFactor),
            Interpolation.linear(mStartFrustum[1], mTargetFrustum[1], interpolationFactor),
            Interpolation.linear(mStartFrustum[2], mTargetFrustum[2], interpolationFactor),
            Interpolation.linear(mStartFrustum[3], mTargetFrustum[3], interpolationFactor));
        
        if (this.isStep(sFocusTime))
        {
            this.setState(sWait);
        }
    }

    private void exeMove()
    {
        float normalizedStepTime = this.getNormalizedStep(sMoveTime);
        float interpolationFactor = Easing.easeOutQuart(normalizedStepTime);

        this.setPosition(Vector2.lerp(mStartPosition, mTartgetPosition, interpolationFactor));

        if (this.isStep(sMoveTime))
        {
            this.setState(sWait);
        }
    }

    public Vector2 calcScreenToWorld(Vector2 screenPosition)
    {
        Vector2 worldPosition;
        Vector2 viewPosition;
        Vector2 ndcPosition;
        Matrix3x2 uiProjMtx = GraphicsModule.getInstance().getUiProjMtx();
        Matrix3x2 invereCamProjMtx = new Matrix3x2();

        ndcPosition = uiProjMtx.mul(screenPosition);

        invereCamProjMtx.set(mProjectionMtx);
        invereCamProjMtx.inverse();

        viewPosition = invereCamProjMtx.mul(ndcPosition);
        worldPosition = this.getTransformMtx().mul(viewPosition);

        return worldPosition;
    }

    public Matrix3x2 calcViewMtx()
    {
        mViewMtx.set(this.getTransformMtx());
        mViewMtx.inverse();

        return mViewMtx;
    }

    public Matrix3x2 calcProjectionMtx()
    {
        /* Projection orthographique */
        mProjectionMtx.xx = 2.0f / (mRight - mLeft);
        mProjectionMtx.yy = 2.0f / (mBottom - mTop);
        mProjectionMtx.zx = -(mRight + mLeft) / (mRight - mLeft);
        mProjectionMtx.zy = -(mBottom + mTop) / (mBottom - mTop);
        
        mProjectionMtx.xy = 0.0f;
        mProjectionMtx.yx = 0.0f;

        return mProjectionMtx;
    }

    public Matrix3x2 calcViewProjectionMtx()
    {
        mViewProjectionMtx.set(this.calcProjectionMtx());
        mViewProjectionMtx.mul(this.calcViewMtx());

        return mViewProjectionMtx;
    }
}
