import javafx.scene.paint.Color;

public class GraphicTimer extends GameObjectEx
{
    private GraphicText mTime;
    private GraphicRectangle mRectangle;

    /* Attribut pour le timer*/
    private static final String sFontPath = "file:assets/Fonts/Digital.ttf";
    private static final Color sNumColor = GraphicHelper.makeColorFromHex(0xe0c0ff);
    private static final float sNumSize = 50;
    private static final Vector2 sPosition = new Vector2(
        GraphicsModule.getWitdh() / 1.152f,
        GraphicsModule.getHeight() / 4.5f
    );
    private static final Vector2 sOffset = new Vector2(0, 15);
    /* Attribut pour le rectangle */
    private static final float sBackgroundWitdh = 325;
    private static final float sBackgroundHeight = 100;
    private static final Color sBackgroundColor = GraphicHelper.makeColorWithOpacity(Color.BLACK, 0.5f);
    private static final float sBackgroundBorderRadius = 5;
    private static final int sBackgroundLayer = 30;

    private static final GameObjectState<GraphicTimer> sWait = new GameObjectState<GraphicTimer>() {
        public void execute(GraphicTimer obj) { obj.exeWait(); }
    };
    private static final GameObjectState<GraphicTimer> sTime = new GameObjectState<GraphicTimer>() {
        public void execute(GraphicTimer obj) { obj.exeTime(); }
    };

    @Override public void onCreate()
    {
        this.setPosition(sPosition);

        mRectangle = new GraphicRectangle();
        mRectangle.init(
            sBackgroundWitdh, 
            sBackgroundHeight, 
            this, 
            sBackgroundColor, 
            sBackgroundLayer, 
            sBackgroundBorderRadius
        );

        mTime = new GraphicText();
        mTime.init(
            "00 : 00,000",
            this, 
            sNumColor,
            sBackgroundLayer + 1, 
            sNumSize
        );
        mTime.setFont(sFontPath);
        mTime.setOffset(sOffset.x, sOffset.y);

        setState(sWait);
    }


    public GraphicText getTime()
    {
        return mTime;
    }

    public GraphicRectangle getRectancle()
    {
        return mRectangle;
    }

    public void pauseTimer()
    {
        setState(sWait);
    }

    public void startTimer()
    {
        setState(sTime);
    }

    public void setHideMode(boolean hidden)
    {
        mTime.setHiden(hidden);
        mRectangle.setHiden(hidden);
    }

    private void exeWait() {}

    private void exeTime() 
    {
        mTime.setText(this.toString());
    }

    @Override public String toString() 
    {
        float time = GameManager.getInstance().getCompletionTime();
        int wholeTime = (int) GameManager.getInstance().getCompletionTime();
        int min = wholeTime / 60;
        int sec = wholeTime % 60;
        int milli = (int) ((time - (float) wholeTime) * 1000.0f);

        return String.format("%02d:%02d,%03d", min, sec, milli);
    }
}