/*
    Auteur: Ryane Menaï
*/

import javafx.scene.image.Image;

public class BoxStatic extends Box
{
    private static final Image sBoxTexture = new Image("file:assets/Textures/BoxStatic.png");

    @Override public BoxType getBoxType()
    {
        return BoxType.Static;
    }

    @Override public boolean canMove()
    {
        return false;
    }

    @Override public void onCreate()
    {
        mShape = this.setGraphicShape(GraphicImage.class);
        super.onCreate();
        this.getGraphicShapeAs(GraphicImage.class).init(
            sBoxTexture,
            mDimension,
            mDimension,
            this,
            0);
    }
}
