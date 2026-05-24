public class GraphicStarPlay extends GraphicStars
{
    private GraphicPlayButton mButton;

    @Override public void onCreate() 
    {
        super.onCreate();

        for (int i = 0; i < 3; i++)
        {
            mStars[i].setScale(1.0f / 3.0f);
        }

        mButton = null;
    }

    public GraphicPlayButton getButton()
    {
        return mButton;
    }

    public void setButton(GraphicPlayButton button)
    {
        mButton = button;
    }

    @Override public void setHidden(boolean hidden)
    {
        for (int i = 0; i<3; i++)
        {
            mStars[i].setHiden(hidden);
        }
    }

    @Override public void update()
    {
        if (mButton == null)
        {
            return;
        }

        int levelId = mButton.getLevelId();
        int unlockedLevel = GameManager.getInstance().getUnlockedLevelId();
        float completionTime = SaveDataManager.getInstance().pCompletionTimes[levelId];

        setCompletion(completionTime);

        if (unlockedLevel < levelId)
        {
            setColor(sLockedColor);
        }
        else if (unlockedLevel == levelId)
        {
            setColor(sUncompletedStarsColor);
        }
        else
        {
            setColor(sCompletedStarColor);
        }
    }

    @Override public void setPosition(Vector2 newPosition)
    {
        super.setPosition(newPosition);

        mStars[1].getGameObject().setPosition(newPosition);
        mStars[0].getGameObject().setPosition(
            new Vector2(newPosition.x - 42, newPosition.y));
        mStars[2].getGameObject().setPosition(
            new Vector2(newPosition.x + 42, newPosition.y));
    }
}