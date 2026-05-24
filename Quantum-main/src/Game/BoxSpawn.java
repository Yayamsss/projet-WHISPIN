/*
    Auteur: Ryane Menaï
*/

import javafx.scene.paint.Color;

public class BoxSpawn extends BoxIntangible
{
    private static BoxSpawn sInstance = null;

    private static final float sRadius = 15.0f;
    private static final Color sColor = GraphicHelper.makeColorFromHex(0xff2080).darker();

    public static BoxSpawn getInstance()
    {
        return sInstance;
    }

    @Override public BoxType getBoxType()
    {
        return BoxType.Spawn;
    }

    @Override public void onCreate()
    {
        if (sInstance == null)
        {
            sInstance = this;
        }
        else
        {
            this.destroy();
        }
        
        super.onCreate();
        mShape.setColor(sColor);
        mShape.setHeight(sRadius);
        mShape.setWitdh(sRadius);
        ((GraphicRectangle) mShape).setBorderRadius(sRadius);
    }

    @Override public void onDestroy()
    {
        if (this == sInstance)
        {
            sInstance = null;
        }
    }
}
