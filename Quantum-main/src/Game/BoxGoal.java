/*
    Auteur: Ryane Menaï
*/

import javafx.scene.image.Image;

public class BoxGoal extends BoxIntangible
{
    public enum GoalType
    {
        Box,
        Player
    }

    private static final float sDimensionDelta = 10.0f;
    private static final Image sBoxGoalTexture = GraphicHelper.loadImage("file:assets/Textures/BoxGoal.png");
    private static final Image sPlayerGoalTexture = GraphicHelper.loadImage("file:assets/Textures/PlayerGoal.png");

    private GoalType mType;

    @Override public BoxType getBoxType()
    {
        return BoxType.Goal;
    }

    @Override public void onCreate()
    {
        mShape = this.setGraphicShape(GraphicImage.class);
        ((GraphicImage) mShape).init(
            sBoxGoalTexture,
            Box.sBoxSize,
            Box.sBoxSize,
            this,
            29);

        super.onCreate();
        this.setState(sWait);

        this.setGoalType(GoalType.Box);
    }

    @Override public void setDimension(float newDimension)
    {
        super.setDimension(newDimension - sDimensionDelta);
    }

    public boolean checkGoalReached()
    {
        if (mParent.isCellEmpty(mCellI, mCellJ))
        {
            return false;
        }

        Box overlapingBox = mParent.getChildBox(mCellI, mCellJ);

        if (!overlapingBox.isState(Box.sWait))
        {
            return false;
        }

        if (mType == GoalType.Box)
        {
            return overlapingBox.isBoxOfType(BoxType.Regular) || overlapingBox.isBoxOfType(BoxType.Recursive);
        }
        else
        {
            return overlapingBox.isBoxOfType(BoxType.Player);
        }
    }

    public GoalType getGoalType()
    {
        return mType;
    }

    public void setGoalType(GoalType newType)
    {
        mType = newType;

        ((GraphicImage) mShape).setImage(mType == GoalType.Box ? sBoxGoalTexture : sPlayerGoalTexture);
    }
}
