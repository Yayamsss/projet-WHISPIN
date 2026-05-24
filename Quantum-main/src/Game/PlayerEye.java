/*
    Auteur: Ryane Menaï
*/

import javafx.scene.paint.Color;

public class PlayerEye extends GameObjectEx
{
    public enum EyeSide
    {
        Right,
        Left
    }

    private static final float sRadius = 18.5f;
    private static final int sBlinkTime = 20;
    private static final int sMinBlinkTimeInterval = 45;
    private static final int sMaxBlinkTimeInterval = 210;
    private static final Vector2 sRightEyeOffset = new Vector2(25.0f, -15.0f);
    private static final Vector2 sLeftEyeOffset = new Vector2(-25.0f, -15.0f);

    private static final GameObjectState<PlayerEye> sBlink = new GameObjectState<>() {
        @Override public void executeLate(PlayerEye p) { p.exeBlink(); }
    };
    private static final GameObjectState<PlayerEye> sWait = new GameObjectState<>() {
        @Override public void executeLate(PlayerEye p) { p.exeWait(); }
    };

    private int mTimeBeforeBlink;
    private EyeSide mSide;
    private GraphicOval mShape;
    private float mLocaleScale;

    private BoxPlayer mHost;

    public void setHost(BoxPlayer host)
    {
        mHost = host;
    }

    public void setEyeSide(EyeSide side)
    {
        mSide = side;
    }

    @Override public void onCreate()
    {
        mHost = null;
        mLocaleScale = 1.0f;

        mShape =this.setGraphicShape(GraphicOval.class);
        mShape.init(sRadius, sRadius, this, Color.BLACK, 16);
    }

    @Override public void onStart()
    {
        this.setState(sBlink);
        this.chooseNextTimeBeforeBlink();
    }

    @Override public void lateUpdate()
    {
        if (mHost == null)
        {
            return;
        }

        super.lateUpdate();
        this.followHost();
    }

    private void followHost()
    {
        Matrix3x2 hostMtx = mHost.getTransformMtx();
        Matrix3x2 localMtx = Matrix3x2.mul(
            Matrix3x2.translation(this.getOffsetFromEyeSide()),
            Matrix3x2.scale(mLocaleScale));
        
        this.setTransformMtx(Matrix3x2.mul(hostMtx, localMtx));
    }

    private void chooseNextTimeBeforeBlink()
    {
        mTimeBeforeBlink = (int)((double)(sMaxBlinkTimeInterval - sMinBlinkTimeInterval) * Math.random()) + sMinBlinkTimeInterval;
    }

    private Vector2 getOffsetFromEyeSide()
    {
        return mSide == EyeSide.Right ? sRightEyeOffset : sLeftEyeOffset;
    }
    
    private void exeBlink()
    {
        double normalizedStep = (float) this.getStep() / (float) sBlinkTime;
        double normalizedCos = (Math.cos(normalizedStep * Math.TAU) + 1.0) / 2.0;

        mLocaleScale = (float) normalizedCos;
        
        if (this.isStep(sBlinkTime))
        {
            this.setState(sWait);
            this.chooseNextTimeBeforeBlink();
        }
    }

    private void exeWait()
    {
        if (this.isStep(mTimeBeforeBlink))
        {
            this.setState(sBlink);
        }
    }
}
