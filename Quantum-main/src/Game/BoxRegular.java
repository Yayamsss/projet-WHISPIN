/*
    Auteur: Ryane Menaï
*/

import javafx.scene.image.Image;

public class BoxRegular extends Box
{
    private static final Image sBoxTexture = GraphicHelper.loadImage("file:assets/Textures/BoxRegular.png");

    @Override public BoxType getBoxType()
    {
        return BoxType.Regular;
    }

    @Override public boolean canMove()
    {
        return true;
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
