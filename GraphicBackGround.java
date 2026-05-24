import java.util.ArrayList;
import javafx.scene.paint.Color;

public class GraphicBackGround 
{
    private static final Color sDefaultColor = GraphicHelper.makeColorFromHex(0x030108);

    private ArrayList<GameObject> mObjectGroup;
    private Color mBackGroundColor;

    public GraphicBackGround() 
    {
        mObjectGroup = new ArrayList<>();
        mBackGroundColor = sDefaultColor;
    }

    public void destroy()
    {
        for (GameObject obj : mObjectGroup)
        {
            obj.destroy();
        }

        mObjectGroup.clear();
        mBackGroundColor = sDefaultColor;
    }

    public ArrayList<GameObject> getObjectGroup() 
    {
        return mObjectGroup;
    }

    public Color getColor()
    {
        return mBackGroundColor;
    }

    public void setHidden(boolean hidden)
    {
        for (GameObject obj : mObjectGroup)
        {
            obj.setHidden(hidden);
        }
    }

    public void setColor(Color color)
    {
        mBackGroundColor = color;
    }

    public void addObject(GameObject obj)
    {
        if (mObjectGroup.contains(obj))
        {
            return;
        }

        mObjectGroup.add(obj);
    }

    public void addObjects(ArrayList<GameObject> objects)
    {
        for (GameObject obj : objects)
        {
            mObjectGroup.add(obj);
        }
    }
}
