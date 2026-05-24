import java.util.ArrayList;
import java.util.Random;
import javafx.scene.paint.Color;

public abstract class GraphicElement extends GameObjectEx
{
    protected ArrayList<GraphicShape> mShapes;
    protected Random mRandom;

    protected static final Color[] sColorTable = {
        GraphicHelper.makeColorFromHex(0xA020F0),
        GraphicHelper.makeColorFromHex(0x00FFFF),
        GraphicHelper.makeColorFromHex(0xFF00FF),
        GraphicHelper.makeColorFromHex(0xFF2080),
        GraphicHelper.makeColorFromHex(0x7000FF),
        GraphicHelper.makeColorFromHex(0xFF5F1F),
    };

    protected static final GameObjectState<GraphicElement> sWait = new GameObjectState<GraphicElement>() {
        public void execute(GraphicElement obj) { obj.exeWait(); };
    };

    @Override public void onCreate()
    {
        mShapes = new ArrayList<>();
        mRandom = new Random();
    }

    @Override public void onDestroy()
    {
        for (GraphicShape shape : mShapes)
        {
            if (shape == null || shape == this.getGraphicShape())
            {
                continue;
            }
            
            shape.destroy();
        }
    }

    @Override public void onEnabled()
    {
        this.updateEnableState();
    }

    @Override public void onDisabled()
    {
        this.updateEnableState();
    }

    @Override public void setHidden(boolean hidden)
    {
        super.setHidden(hidden);

        for (GraphicShape shape : mShapes)
        {
            if (shape == null)
            {
                continue;
            }

            shape.setHiden(hidden);
        }
    }

    public ArrayList<GraphicShape> getShapes()
    {
        return mShapes;
    }

    protected void updateEnableState()
    {
        for (GraphicShape shape : mShapes)
        {
            if (shape == null)
            {
                continue;
            }

            shape.getGameObject().setEnabled(this.isEnabled());
        }
    }

    protected void exeWait() {}
}
