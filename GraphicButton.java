/*
    Auteur : Ali GÜRKAN
*/
import javafx.scene.paint.Color;

public class GraphicButton extends GameObjectEx
{
    protected GraphicRectangle mShape;
    protected GraphicText mTextShape;
    protected GraphicLine mTopLine;
    protected GraphicLine mBottomLine;
    private GraphicText mLeftBracket;
    private GraphicText mRightBracket;

    protected ButtonCallBack mCallBack;

    protected static final int sClickEnableWaitTime = 15;

    /* Attribut pour rectangle */
    private static final float sWitdh = 380;
    private static final float sHeight = 50;
    protected static final int sLayer = 45;

    protected static final float sMinOpacity = 0.6f;
    protected static final float sMaxOpacity = 0.8f;

    protected static final Color sBaseColor = GraphicHelper.makeColorWithOpacity(Color.BLACK, sMinOpacity);
    protected static final Color sHoverColor = GraphicHelper.makeColorFromHex(0xA020F0);

    /* Attribut pour Text */
    protected static final float sTextSize = 20;
    protected static final Color sTextColor = GraphicHelper.makeColorFromHex(0xe0c0ff);

    /* Attribut pour les lignes */
    protected static final Color sLineColor = GraphicHelper.makeColorFromHex(0xA020F0);
    protected static final float sLineWidth = 1.5f;

    /* Attribut pour les chevrons */
    protected static final Color sBracketColor = Color.ORANGE;
    protected static final float sBracketSize = 20.0f;
    protected static final float sBracketOffsetPercentage = (2.0f / 3.0f) / 2.0f;

    protected final static GameObjectState<GraphicButton> sWait = new GameObjectState<GraphicButton>() {
        public void execute(GraphicButton obj) { obj.exeWait(); }
    };

    public static float getMinOpacity()
    {
        return sMinOpacity;
    }

    public static float getMaxOpacity()
    {
        return sMaxOpacity;
    }

    @Override public void onCreate()
    {
        mCallBack = null;

        this.initShape(sWitdh, sHeight);
        this.initText(sTextSize);
        this.initLines();
        this.initBrackets();

        this.setState(sWait);
    }

    @Override public void onDestroy()
    {
        mTextShape.destroy();

        if (mLeftBracket != null)
        {
            mLeftBracket.destroy();
        }
        
        if (mRightBracket != null)
        {
            mRightBracket.destroy();
        }

        mTopLine.getGameObject().destroy();
        mBottomLine.getGameObject().destroy();
    }

    @Override protected void onEnabled()
    {
        this.setHidden(false);
        this.setState(sWait);
    }

    @Override protected void onDisabled() 
    {
        this.setHidden(true);
        mShape.setColor(sBaseColor);
    }

    @Override public void setPosition(Vector2 newPosition)
    {
        super.setPosition(newPosition);

        float halfWidth = mShape.getWitdh() / 2;
        float halfHeight = mShape.getHeight() / 2;

        mTopLine.getGameObject().setPosition(new Vector2(
            newPosition.x - halfWidth,
            newPosition.y - halfHeight));
        mBottomLine.getGameObject().setPosition(new Vector2(
            newPosition.x - halfWidth,
            newPosition.y + halfHeight));
    }

    @Override public void setHidden(boolean hidden)
    {
        mShape.setHiden(hidden);
        mTextShape.setHiden(hidden);
        mTopLine.setHiden(hidden);
        mBottomLine.setHiden(hidden);

        if (mLeftBracket != null) mLeftBracket.setHiden(hidden);
        if (mRightBracket != null) mRightBracket.setHiden(hidden);
    }

    public void setText(String _text)
    {
        mTextShape.setText(_text);
    }

    public void setOpacity(float opacity)
    {
        mShape.setOpacity(opacity);
        mTextShape.setOpacity(opacity);
        mTopLine.setOpacity(opacity);
        mBottomLine.setOpacity(opacity);

        if (mLeftBracket != null) mLeftBracket.setOpacity(opacity);
        if (mRightBracket != null) mRightBracket.setOpacity(opacity);
    }

    public void setCallBack(ButtonCallBack _callBack)
    {
        mCallBack = _callBack;
    }

    public void setWidth(float width)
    {
        mShape.setWitdh(width);
        mTextShape.setWitdh(width);
        mTopLine.setHeight(width); /* La longeur en réalité */
        mBottomLine.setHeight(width);

        this.setPosition(this.getPosition());
        this.updateBracketsOffsets();
    }

    public void setHeight(float height)
    {
        mShape.setHeight(height);
        mTextShape.setHeight(height);

        this.setPosition(this.getPosition());
    }

    public String getText()
    {
        return mTextShape.getText();
    }

    public GraphicText getGraphicText()
    {
        return mTextShape;
    }

    public GraphicRectangle getShape()
    {
        return mShape;
    }

    public GraphicLine getTopLine()
    {
        return mTopLine;
    }

    public GraphicLine getBottomLine()
    {
        return mBottomLine;
    }

    protected void initShape(float width, float height)
    {
        mShape = this.setGraphicShape(GraphicRectangle.class);
        mShape.init(width, height, this, sBaseColor, sLayer, 0.0f);
        mShape.setOpacity(sMinOpacity);
    }

    protected void initText(float textSize)
    {
        mTextShape = new GraphicText();
        mTextShape.init("", this, sTextColor, sLayer + 1, textSize);
    }

    protected void initLines()
    {
        mTopLine = GraphicHelper.createLineObj(sWitdh, sLineWidth, 0.0f, sLineColor, sLayer + 1);
        mBottomLine = GraphicHelper.createLineObj(sWitdh, sLineWidth, 0.0f, sLineColor, sLayer + 1);
    }

    protected void initBrackets()
    {
        mLeftBracket = new GraphicText();
        mRightBracket = new GraphicText();

        mLeftBracket.init(
            ">", 
            this, 
            sBracketColor,
            sLayer + 1, 
            sBracketSize);
        mRightBracket.init(
            "<",
            this,
            sBracketColor,
            sLayer + 1,
            sBracketSize);
        
        this.updateBracketsOffsets();
    }

    protected void updateBracketsOffsets()
    {
        float offsetX = mShape.getWitdh() * sBracketOffsetPercentage;
        float offsetY =  GraphicText.getDefaultOffset().y;
        
        if (mLeftBracket != null) mLeftBracket.setOffset(-offsetX, offsetY);
        if (mRightBracket != null) mRightBracket.setOffset(offsetX, offsetY);
    }

    protected boolean checkClick()
    {
        return GraphicsModule.getInstance().isMouseJustPressed() && this.isMouseOnButton();
    } 

    protected boolean isMouseOnButton()
    {
        Vector2 mousePosition = GraphicsModule.getInstance().getMousePosition();
        Vector2 topLeft = this.caclTopLeft();
        Vector2 bottomRight = this.calcBottomRight();

        return
            (mousePosition.x > topLeft.x && mousePosition.x < bottomRight.x)
         && (mousePosition.y > topLeft.y && mousePosition.y < bottomRight.y);
    }

    protected void tryCallBack()
    {
        if (this.checkClick())
        {
            mCallBack.function();
        }
    }

    protected void exeWait()
    {
        boolean isMouseOnBoutton = this.isMouseOnButton();

        if (mRightBracket != null) mRightBracket.setHiden(!isMouseOnBoutton);
        if (mLeftBracket != null) mLeftBracket.setHiden(!isMouseOnBoutton);

        if (this.isLessStep(sClickEnableWaitTime))
        {
            return;
        }

        mShape.setColor(isMouseOnBoutton ? sHoverColor : sBaseColor);
        this.tryCallBack();
    }

    private Vector2 caclTopLeft()
    {
        Vector2 center = this.getPosition();
        Vector2 topLeft = new Vector2(
            center.x - (mShape.getWitdh() / 2),
            center.y - (mShape.getHeight() / 2));

        return topLeft;
    }

    private Vector2 calcBottomRight()
    {
        Vector2 center = this.getPosition();
        Vector2 bottomRight = new Vector2(
            center.x + (mShape.getWitdh() / 2),
            center.y + (mShape.getHeight() / 2));

        return bottomRight;
    }
}
