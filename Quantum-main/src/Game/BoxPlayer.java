/*
    Auteur: Ryane Menaï
*/

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class BoxPlayer extends Box
{
    /* Variables d'états */
    private static final GameObjectState<BoxPlayer> sSpawn = new GameObjectState<>() {
        @Override public void execute(BoxPlayer p) { p.exeSpawn(); }
    };
    private static final GameObjectState<BoxPlayer> sBonk = new GameObjectState<>() {
        @Override public void execute(BoxPlayer p) { p.exeBonk(); }
    };
    private static final GameObjectState<BoxPlayer> sDie = new GameObjectState<>() {
        @Override public void execute(BoxPlayer p) { p.exeDie(); }
    };

    /* Données pour l'animation et attributs du joueur.
       Les temps sont en nombre de frames. */

    private static final Color sPlayerColor = GraphicHelper.makeColorFromHex(0xff2080).darker();

    private static final float sDimensionBias = 10;
    private static final float sBorderRadius = 40;

    private static final int sStartHealth = 3;
    private static final int sStartNumLives = 3;

    private static final int sIdleAnimationTime = 120;
    private static final float sMinIdleScale = 0.95f;
    private static final float sMaxIdleScale = 1.05f;

    private static final int sSpawnTime = 45;
    private static final float sSpawnStartScale = 0.0f;

    private static final int sDieTime = 60;
    private static final int sRespawnTime = 90;
    private static final float sDieStartRotation = 0.0f;
    private static final float sDieEndRotation = 60.0f;
    private static final float sDieRotationSpeedFactor = 6.0f;

    private static final int sBonkAdvanceTime = 5;
    private static final int sBonkBackupTime = 8;
    private static final float sBonkAdvanceDistance = Box.sBoxSize * 0.1f;

    private int mHealth;
    private int mNumLives;
    private float mAnimatedScale;
    private boolean mCanRespawn;

    /* On garde une instance pour un accès plus simple. */
    private KeyCode mInputKey;

    /* Les yeux :> */
    private PlayerEye mRightEye;
    private PlayerEye mLeftEye;

    public static int getNumLivesAtStart()
    {
        return sStartNumLives;
    }

    @Override public BoxType getBoxType()
    {
        return BoxType.Player;
    }

    @Override public boolean canMove()
    {
        return true;
    }

    /* Fonction d'initialisation */
    @Override public void onCreate()
    {
        super.onCreate();

        mCurrentMoveDirection = Direction.None;
        mNumLives = sStartNumLives;
        mCanRespawn = true;

        ((GraphicRectangle) mShape).setBorderRadius(sBorderRadius);
        mShape.setColor(sPlayerColor);

        mRightEye = GameLoop.getInstance().addObject(PlayerEye.class);
        mLeftEye = GameLoop.getInstance().addObject(PlayerEye.class);

        mRightEye.setHost(this);
        mLeftEye.setHost(this);

        mRightEye.setEyeSide(PlayerEye.EyeSide.Right);
        mLeftEye.setEyeSide(PlayerEye.EyeSide.Left);

        this.setState(sSpawn);
    }
    
    @Override protected void onEnabled()
    {
        mRightEye.setEnabled(true);
        mLeftEye.setEnabled(true);
    }

    @Override protected void onDisabled()
    {
        mRightEye.setEnabled(false);
        mLeftEye.setEnabled(false);
    }

    @Override public void onDestroy()
    {
        mRightEye.destroy();
        mLeftEye.destroy();
        
        if (GameCamera.getInstance() == null)
        {
            return;
        }

        GameCamera.getInstance().resetFocus();
    }

    @Override public void update()
    {
        mInputKey = GraphicsModule.getInstance().getKeyPressed();

        super.update();
        this.calcAnimation();
    }

    @Override public void receiveMsg(GameObject from, int msg)
    {
        if (from instanceof UndoManager)
        {
            if (msg == UndoManager.MessageType.Refresh.ordinal())
            {
                GameCamera.getInstance().focus(mParent);
            }
        }
    }

    private Direction getMoveDirFromInputKey()
    {
        if (mInputKey == null || !mInputKey.isArrowKey())
        {
            return Direction.None;
        }

        switch (mInputKey)
        {
            case KeyCode.UP: return Direction.Up;
            case KeyCode.DOWN: return Direction.Down;
            case KeyCode.RIGHT: return Direction.Right;
            case KeyCode.LEFT: return Direction.Left;
        
            default: return Direction.None;
        }
    }

    public int getHealth()
    {
        return mHealth;
    }

    public int getNumLives()
    {
        return mNumLives;
    }

    public boolean canRespawn()
    {
        return mCanRespawn;
    }

    @Override public void setDimension(float newDimension)
    {
        super.setDimension(newDimension - sDimensionBias);
    }

    private void calcAnimation()
    {
        float sinValue = ((float) Math.sin(((float) this.getTotalSteps() / (float) sIdleAnimationTime) * Math.TAU));
        float sinBetween0And1 = (sinValue + 1.0f) / 2.0f;
        mAnimatedScale = sinBetween0And1 * (sMaxIdleScale - sMinIdleScale) + sMinIdleScale;

        /* Ne met pas directement à jour le grossissement si on se trouve dans l'un des états suivant.
           Ceci notament pour éviter d'écraser l'animation qui se joue à ce moment là. */
        if (this.isState(sSpawn) || this.isState(sDie) || this.isState(sTransition))
        {
            return;
        }

        this.setScale(mParent.getChildBoxScale() * mAnimatedScale);
    }

    @Override protected boolean tryMove(Direction dir)
    {
        if (super.tryMove(dir))
        {
            this.sendMsg(MoveLogger.getInstance(), dir.ordinal());
            return true;
        }

        this.setState(sBonk);
        return false;
    }

    @Override protected void exeWait()
    {
        mCurrentMoveDirection = this.getMoveDirFromInputKey();

        if (mCurrentMoveDirection != Direction.None)
        {
            this.tryMove(mCurrentMoveDirection);
        }
    }

    @Override protected void exeTransition()
    {
        if (this.isFirstStep())
        {
            GameCamera.getInstance().focus(mParent);
        }

        this.setScale(
            Interpolation.linear(
                mStartScale,
                mAnimatedScale * mTargetScale,
                Easing.easeOutExpo((float) this.getStep() / (float) sMoveTime)));
        super.exeMove();
    }

    private void exeSpawn()
    {
        if (this.isFirstStep())
        {
            mHealth = sStartHealth;

            int[] currentCell;
            BoxRecursive spawnParent = BoxSpawn.getInstance().getParent();

            if (mParent != null)
            {
                currentCell = mParent.getCellFromPosition(this.getPosition());
                mParent.setBox(null, currentCell[0], currentCell[1]);
            }

            mParent = spawnParent;
            mParent.setBox(
                this,
                BoxSpawn.getInstance().getCellI(),
                BoxSpawn.getInstance().getCellJ());
            GameCamera.getInstance().focus(mParent);
        }

        this.setScale(
            Interpolation.linear(
                sSpawnStartScale * mParent.getChildBoxScale(),
                mAnimatedScale * mParent.getChildBoxScale(),
                Easing.easeOutExpo(this.getNormalizedStep(sSpawnTime))));

        if (this.isStep(sSpawnTime))
        {
            this.setState(sWait);
        }
    }

    private void exeBonk()
    {
        if (this.isFirstStep())
        {
            mHealth--;

            mMoveStartPosition.set(this.getPosition());
            mMoveTargetPosition = Vector2.add(
                mMoveStartPosition,
                Vector2.mul(
                    this.getMoveVectorFromEnum(mCurrentMoveDirection),
                    sBonkAdvanceDistance * mParent.getChildBoxScale()));
        }

        if (this.isLessOrEqualStep(sBonkAdvanceTime))
        {
            this.setPosition(
                Vector2.lerp(
                    mMoveStartPosition,
                    mMoveTargetPosition,
                    this.getNormalizedStep(sBonkAdvanceTime)));
        }
        else if (this.isLessOrEqualStep(sBonkAdvanceTime + sBonkBackupTime))
        {
            this.setPosition(
                Vector2.lerp(
                    mMoveTargetPosition,
                    mMoveStartPosition,
                    Easing.easeOutExpo((float) (this.getStep() - sBonkAdvanceTime) / (float) sBonkBackupTime)));
        }
        else
        {
            if (mHealth <= 0)
            {
                this.setState(sDie);
            }
            else
            {
                this.setState(sWait);
            }
        }
    }

    private void exeDie()
    {
        if (this.isFirstStep())
        {
            mNumLives--;
        }

        float normalizedStepTime = this.getNormalizedStep(sDieTime);
        float interpolationFactor = Easing.easeInCubic(Math.min(normalizedStepTime, 1.0f));
        float dieScale = Math.max(0.0f, 1.0f - interpolationFactor);

        this.setScale(dieScale * mAnimatedScale * mParent.getChildBoxScale());
        this.setRotation(
            Interpolation.linear(
                sDieStartRotation,
                sDieEndRotation,
                interpolationFactor * sDieRotationSpeedFactor));

        if (this.isStep(sRespawnTime))
        {
            if (mNumLives > 0)
            {
                this.setRotation(0.0f);
                this.setState(sSpawn);
            }
            else
            {
                mCanRespawn = false;
            }
        }
    }
}
