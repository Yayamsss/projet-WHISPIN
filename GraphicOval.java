/*
    Auteur : Ali GÜRKAN
*/

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphicOval extends GraphicShape
{
    public void init(float _width, float _height, GameObject _obj, Color _color, int _layer) {
        super.baseInit(_width, _height, _obj, _color, _layer);
    }

    @Override public void drawObject(GraphicsContext gc) 
    {
        gc.setFill(mColor);
        gc.fillOval(
            (double) - (getWitdh()/2),
            (double) - (getHeight()/2),
            (double) getWitdh(),
            (double) getHeight()
        );
    }
}