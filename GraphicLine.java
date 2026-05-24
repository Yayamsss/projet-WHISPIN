/*
    Auteur : Ali GÜRKAN
*/

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GraphicLine extends GraphicShape
{
    public void init(GameObject _start, float _lineHeight, float _lineWitdh, float _degrees, Color _color, int _layer) 
    {
        super.baseInit(_lineWitdh, _lineHeight, _start, _color, _layer);
        mObject.setRotation(_degrees);
    }

    @Override public void drawObject(GraphicsContext gc) 
    {
        double rad = Math.toRadians(this.getGameObject().getRotation());
        
        double finishX = (mHeight * Math.cos(rad));
        double finishY = (mHeight * Math.sin(rad));

        gc.setStroke(mColor);
        gc.setLineWidth(mWidth);        
        gc.strokeLine(0, 0, finishX, finishY);
    }
}
