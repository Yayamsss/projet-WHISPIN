import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GraphicStars extends GameObject
{
    private static final int sStarLayer = 47;
    private static final int sStarSize = 100;

    private static final String sFontName = "Arial"; 
    private static final Font sFont = new Font(sFontName, sStarSize);

    protected static final String sUncompletedStarText = "\u2606";
    protected static final String sCompletedStarText = "\u2605";

    protected static final Color sUncompletedStarsColor = Color.RED;
    protected static final Color sCompletedStarColor = Color.YELLOW;
    protected static final Color sLockedColor = sCompletedStarColor.grayscale();

    private static final Vector2 sOffset = new Vector2(100, 23);

    protected GraphicText mStars[];
    
    @Override public void onCreate() 
    {
        mStars = new GraphicText[3];

        for (int i = 0; i < mStars.length; i++)
        {
            GameObject obj = GameLoop.getInstance().addObject(GameObject.class);
            mStars[i] = obj.setGraphicShape(GraphicText.class);
            mStars[i].init(
                sUncompletedStarText, 
                obj,
                sLockedColor, 
                sStarLayer,
                sStarSize);
            mStars[i].setFont(sFont);
        }
    }

    public GraphicText getStars(int i)
    {
        return mStars[i];
    }

    protected void setCompletion(float completionTime)
    {
        for (int i = 0; i < mStars.length; i++)
        {
            if (completionTime < (mStars.length - i) * 30.0f)
            {
                mStars[i].setText(sCompletedStarText);
                mStars[i].setColor(sCompletedStarColor);
            }
            else
            {
                mStars[i].setText(sUncompletedStarText);
            }
        }
    }

    protected void setColor(Color color)
    {
        for (int i = 0; i<3; i++)
        {
            mStars[i].setColor(color);
        }
    }

    @Override protected void receiveMsg(GameObject from, int msg) 
    {
        if (from instanceof GraphicStarPlay)
        {
            return;
        }
        
        this.setCompletion(msg);
    }

    @Override public void setPosition(Vector2 newPosition)
    {
        super.setPosition(newPosition);
        mStars[1].getGameObject().setPosition(newPosition);
        mStars[0].getGameObject().setPosition(new Vector2(
            newPosition.x - sOffset.x,
            newPosition.y + sOffset.y
        ));
        mStars[2].getGameObject().setPosition(new Vector2(
            newPosition.x + sOffset.x,
            newPosition.y + sOffset.y
        ));
    }
}
