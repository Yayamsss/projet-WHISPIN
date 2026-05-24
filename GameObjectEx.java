/*
    Auteur: Ryane Menaï
*/

@SuppressWarnings({ "unchecked", "rawtypes" })
public class GameObjectEx extends GameObject
{
    private GameObjectState mCurrentState;
    private int mCurrentStep;
    private int mTotalSteps;

    protected GameObjectEx()
    {
        mCurrentState = null;
        mCurrentStep = 0;
        mTotalSteps = 0;

        super();
    }

    @Override public void update()
    {
        mCurrentStep++;
        mTotalSteps++;

        if (mCurrentState == null)
        {
            return;
        }

        mCurrentState.execute(this);
    }

    @Override public void lateUpdate()
    {
        if (mCurrentState == null)
        {
            return;
        }

        mCurrentState.executeLate(this);
    }

    public int getStep()
    {
        return mCurrentStep;
    }

    public int getTotalSteps()
    {
        return mTotalSteps;
    }

    public float getNormalizedStep(int numTargetSteps)
    {
        return (float) mCurrentStep / (float) numTargetSteps;
    }

    public boolean isState(GameObjectState state)
    {
        return mCurrentState == state;
    }

    /* Permet de changer d'état */
    public void setState(GameObjectState newState)
    {
        mCurrentState = newState;
        mCurrentStep = 0;
    }

    /* Renvoie vrai si on est dans la première ittération de l'état courant */
    public boolean isFirstStep()
    {
        return mCurrentStep == 1;
    }

    /* Renvoie vrai si l'ittération courante est la même que step */
    public boolean isStep(int step)
    {
        return mCurrentStep == step;
    }

    public boolean isAboveStep(int step)
    {
        return mCurrentStep > step;
    }

    public boolean isAboveOrEqualStep(int step)
    {
        return mCurrentStep >= step;
    }

    public boolean isLessStep(int step)
    {
        return mCurrentStep < step;
    }

    public boolean isLessOrEqualStep(int step)
    {
        return mCurrentStep <= step;
    }

    public int timeToStep(float time)
    {
        float numStep = time / GameLoop.getInstance().getFrameTime();
        int wholePart = (int) numStep;
        float fractionalPart = numStep - wholePart;

        if (fractionalPart >= 0.5f)
        {
            return wholePart + 1;
        }
        else
        {
            return wholePart;
        }
    }

    public float stepToTime(int step)
    {
        return step * GameLoop.getInstance().getFrameTime();
    }
}
