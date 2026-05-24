/*
    Auteur : Ali GÜRKAN
*/

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphicRectangle extends GraphicShape {
    protected float mBorderRadius;

    public void init(float _width, float _height, GameObject _obj, Color _color, int _layer, float _borderRadius) {
        super.baseInit(_width, _height, _obj, _color, _layer);
        mBorderRadius = _borderRadius;
    }

    public void setBorderRadius(float _borderRadius)
    {
        mBorderRadius = _borderRadius;
    }

    public float getBorderRadius()
    {
        return mBorderRadius;
    }

    @Override public void drawObject(GraphicsContext gc) 
    {
        gc.setFill(mColor);
        gc.fillRoundRect(
            (double) -0.5f * this.getWitdh(),
            (double) -0.5f * this.getHeight(),
            (double) this.getWitdh(),
            (double) this.getHeight(), 
            (double) mBorderRadius,
            (double) mBorderRadius
        );
    }
}
