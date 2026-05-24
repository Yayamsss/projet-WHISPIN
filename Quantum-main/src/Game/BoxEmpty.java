import javafx.scene.image.Image;

class BoxEmpty extends Box
{
    private static final Image sTexture = GraphicHelper.loadImage("file:assets/Textures/BoxEmpty.png");

    @Override public BoxType getBoxType()
    {
        return BoxType.Empty;
    }

    @Override public boolean canMove()
    {
        return false;
    }

    @Override public void onCreate()
    {
        mShape = this.setGraphicShape(GraphicImage.class);
        super.onCreate();

        ((GraphicImage) mShape).init(
            sTexture,
            this.getDimension(),
            this.getDimension(),
            this,
            0);
    }
}
