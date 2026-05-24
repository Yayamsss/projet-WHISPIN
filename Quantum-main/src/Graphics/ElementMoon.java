import javafx.scene.paint.Color;

public class ElementMoon extends GraphicElement
{
    private GraphicOval mMoon;
    private GraphicOval mOval;

    /* Attribut pour moon */
    private static final float sMoonRadius = 135;
    private static final Color sMoonColor = GraphicHelper.makeColorFromHex(0xfcc603);
    private static final int sMoonLayer = 0;
    /* Attribut pour oval */
    private static final Color sOvalColor = GraphicHelper.makeColorFromHex(0x0);
    
    @Override public void onCreate()
    {
        super.onCreate();
        mMoon = this.setGraphicShape(GraphicOval.class);
        mMoon.init(
            sMoonRadius,
            sMoonRadius,
            this,
            sMoonColor,
            sMoonLayer);
        mOval = GraphicHelper.createOvalObj(sMoonRadius, sMoonRadius, sOvalColor, sMoonLayer + 1);
        mShapes.add(mOval);

        setState(sWait);
    }

    @Override public void setPosition(Vector2 newPos)
    {
        super.setPosition(newPos);

        mOval.getGameObject().setPosition(new Vector2(
            this.getPosition().x + mMoon.getWitdh() / 4,
            this.getPosition().y - mMoon.getHeight() / 4
        ));
    }
}
