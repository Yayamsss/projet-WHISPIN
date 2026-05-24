import javafx.scene.paint.Color;

public class GraphicTitle extends GameObjectEx 
{
    private GraphicText mText;

    /* Attribut pour le titre */
    private static final Color sTitleColor = GraphicHelper.makeColorFromHex(0xe0c0ff);
    private static final int sTitleLayer = 47;
    private static final float sTitleSize = 80;
    private static final Vector2 sTitlePosition = new Vector2(
        GraphicsModule.getWitdh()/2,
        140
    );
    /* Attribut pour bobbing */
    private static final float sAmplitudeBobbing = 0.6666f;
    private static final float sSpeedBobbing = 2.5f;
    /* Attribut pour shaking */
    private static final float sAmplitudeShaking = 3f;
    private static final float sSpeedShaking = 90;

    private static final GameObjectState<GraphicTitle> sWait = new GameObjectState<GraphicTitle>() {
        public void execute(GraphicTitle obj) { obj.exeWait(); }
    };
    private static final GameObjectState<GraphicTitle> sBlink1 = new GameObjectState<GraphicTitle>() {
        public void execute(GraphicTitle obj) { obj.exeBlink1(); }
    };
    private static final GameObjectState<GraphicTitle> sBlink2 = new GameObjectState<GraphicTitle>() {
        public void execute(GraphicTitle obj) { obj.exeBlink2(); }
    };
    private static final GameObjectState<GraphicTitle> sBobbing = new GameObjectState<GraphicTitle>() {
        public void execute(GraphicTitle obj) { obj.exeBobbing(); } 
    };
    private static final GameObjectState<GraphicTitle> sShaking1 = new GameObjectState<GraphicTitle>() {
        public void execute(GraphicTitle obj) { obj.exeShaking1(); }
    };
    private static final GameObjectState<GraphicTitle> sShaking2 = new GameObjectState<GraphicTitle>() {
        public void execute(GraphicTitle obj) { obj.exeShaking2(); }
    };

    @Override public void onCreate()
    {
        mText = this.setGraphicShape(GraphicText.class);
        mText.init(
            "",
            this,
            sTitleColor,
            sTitleLayer,
            sTitleSize);

        this.setPosition(sTitlePosition);
        this.setState(sWait);
    }

    public GraphicText getText()
    {
        return mText;
    }

    public void setText(String _text)
    {
        mText.setText(_text);
    }

    public void startBlink()
    {
        this.setState(sBlink1);
    }

    public void startBobbing()
    {
        this.setState(sBobbing);
    }

    public void startShaking()
    {
        this.setState(sShaking1);
    }
    
    private void exeWait() {}

    private void exeBlink1()
    {
        if (isLessOrEqualStep(timeToStep(8)))
        {
            return;
        }

        float newOpacity = mText.getOpacity() - 0.3f;
        if (newOpacity > 0)
        {
            mText.setOpacity(newOpacity);
        }
        else
        {
            mText.setOpacity(0);
            setState(sBlink2);
        }
    }

    private void exeBlink2()
    {
        float newOpacity = mText.getOpacity() + 0.3f;
        if (newOpacity < 1)
        {
            mText.setOpacity(newOpacity);
        }
        else
        {
            mText.setOpacity(1);
            setState(sBlink1);
        } 
    }

    private void exeBobbing()
    {
        float newPosY = (float)(sAmplitudeBobbing * Math.sin(GameLoop.getInstance().getTime() * sSpeedBobbing));
        this.setPosition(new Vector2(
            this.getPosition().x,
            this.getPosition().y + newPosY
        ));
    }

    private void exeShaking1()
    {
       float newPosX = (float)(sAmplitudeShaking * Math.sin(GameLoop.getInstance().getTime() * sSpeedShaking));
        this.setPosition(new Vector2(
            this.getPosition().x + newPosX,
            this.getPosition().y
        )); 

        if (isLessOrEqualStep(this.timeToStep(4.5f)))
            return;

        float newOpacity = mText.getOpacity() - 0.3f;
        if (newOpacity > 0)
        {
            mText.setOpacity(newOpacity);
        }
        else
        {
            mText.setOpacity(0);
            setState(sShaking2);
        }
    }

    private void exeShaking2()
    {
        float newPosX = (float)(sAmplitudeShaking * Math.sin(GameLoop.getInstance().getTime() * sSpeedShaking));
        this.setPosition(new Vector2(
            this.getPosition().x + newPosX,
            this.getPosition().y
        )); 
        
        float newOpacity = mText.getOpacity() + 0.3f;
        if (newOpacity < 1)
        {
            mText.setOpacity(newOpacity);
        }
        else
        {
            mText.setOpacity(1);
            setState(sShaking1);
        } 
        
    }
}