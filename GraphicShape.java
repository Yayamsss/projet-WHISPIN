/*
    Auteur : Ali GURKAN
*/

import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

public abstract class GraphicShape
{
    protected float mWidth;
    protected float mHeight;
    protected GameObject mObject;
    protected Color mColor;
    protected int mLayer;
    protected boolean mIsHiden;
    
    protected void baseInit(float _width, float _height, GameObject _obj, Color _color, int _layer)
    {
        mWidth = _width;
        mHeight = _height;
        mObject = _obj;
        mColor = _color;
        mLayer = _layer;
        mIsHiden = false;
        
        GraphicsModule.getInstance().addShape(this);
    }

    public void setWitdh(float _width)
    {
        mWidth = _width;
    }

    public void setHeight(float _height)
    {
        mHeight = _height;
    }

    public void setColor(Color _colorShape) 
    {
        mColor = _colorShape;
    }

    public void setOpacity(float opacity)
    {
        mColor = GraphicHelper.makeColorWithOpacity(mColor, opacity);
    }

    public void setLayer(int _layer)
    {
        GraphicsModule gfxModule = GraphicsModule.getInstance();

        if (_layer < 0 || _layer >= gfxModule.getNumLayers()) 
            throw new IndexOutOfBoundsException("Layer " + _layer + " est hors limites.");
    
        gfxModule.removeShape(this);
        mLayer = _layer;
        gfxModule.addShape(this);
    }

    public void setHiden(boolean b)
    {
        mIsHiden = b;
    }

    public final float getWitdh()
    {
        return mWidth;
    }

    public final float getHeight() 
    {   
        return mHeight;
    }

    public final Vector2 getPosition()
    {
        return mObject.getPosition();
    }

    public final float getRotation()
    {
        return mObject.getRotation();
    }

    public final float getScale()
    {
        return mObject.getScale();
    }

    public final GameObject getGameObject()
    {
        return mObject;
    }

    public final Color getColor()
    {
        return mColor;
    }

    public final float getOpacity()
    {
        return (float) mColor.getOpacity();
    }

    public final int getLayer()
    {
        return mLayer;
    }

    public final boolean isHiden()
    {
        return mIsHiden;
    }

    public final void setPosition(Vector2 newPosition)
    {
        mObject.setPosition(newPosition);
    }

    public final void setRotation(float newRotation)
    {
        mObject.setRotation(newRotation);
    }

    public final void setScale(float newScale)
    {
        mObject.setScale(newScale);
    }

    public final void destroy()
    {
        GameLoop.getInstance().getGraphicsModule().removeShape(this);
    }

    public abstract void drawObject(GraphicsContext gc);
}
