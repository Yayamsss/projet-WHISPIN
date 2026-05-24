/*
    Auteur : Ali GÜRKAN
*/

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GraphicText extends GraphicShape 
{
    private static final String sDefaultFontPath = "file:assets/Fonts/Orbitron.ttf";
    private static final Vector2 sDefaultOffset = new Vector2(0, 7);

    private String mText;
    private Font mFont;
    private float mSize;
    private TextAlignment mTextAlignment;
    private Vector2 mOffset;

    public static Vector2 getDefaultOffset()
    {
        return sDefaultOffset;
    }

    public void init(String _text, GameObject _obj, Color _color, int _layer, float _size)
    {
        super.baseInit(0, 0, _obj, _color, _layer);
        mText = _text;

        mSize = _size;
        mFont = Font.loadFont(sDefaultFontPath, mSize);
        mTextAlignment = TextAlignment.CENTER;

        mOffset = new Vector2(sDefaultOffset);
    }

    public void setText(String _text)
    {
        mText = _text;
    }

    public void setFont(Font _font)
    {
        mFont = _font;
        mSize = (float) _font.getSize();
    }

    public void setFont(String _pathFont)
    {
        mFont = Font.loadFont(_pathFont, mSize);
    }

    public void setTextAlignment(TextAlignment _alignment)
    {
        mTextAlignment = _alignment;
    }

    public void setOffset(float x, float y)
    {
        mOffset.x = x;
        mOffset.y = y;
    }

    public String getText()
    {
        return mText;
    }

    public Font getFont()
    {
        return mFont;
    }
    
    public TextAlignment getTextAlignment()
    {
        return mTextAlignment;
    } 

    public float getSize()
    {
        return mSize;
    }

    public Vector2 getOffset()
    {
        return mOffset;
    }

    @Override public void drawObject(GraphicsContext gc)
    {
        gc.setFill(mColor);
        gc.setFont(mFont);
        gc.setTextAlign(mTextAlignment);

        gc.fillText(
            mText, 
            (double) mOffset.x,
            (double) mOffset.y);
    }
}
