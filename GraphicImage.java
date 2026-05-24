/*
    Auteur : Ali GÜRKAN
*/

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GraphicImage extends GraphicShape
{
    private Image mImage;

    public void init(Image _image, float _width, float _height, GameObject _obj, int layer)
    {
        super.baseInit(_width, _height, _obj, mColor, layer);
        mImage = _image;
    }

    public void setImage(Image _image)
    {
        mImage = _image;
    }

    public Image getImage()
    {
        return mImage;
    }

    @Override public void drawObject(GraphicsContext gc) {
        gc.drawImage(
            mImage,
            (double) -1/2*this.getWitdh(),
            (double) -1/2*this.getHeight(),
            (double) this.getWitdh(),
            (double) this.getHeight()
        );

    }
}
