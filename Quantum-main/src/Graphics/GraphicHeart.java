/*
    Auteur : Ali GÜRKAN
*/

import javafx.scene.image.Image;

public class GraphicHeart extends GameObjectEx
{
    private GraphicImage mShape;
    private int mRank;

    private final static Image sTexture = GraphicHelper.loadImage("file:assets/Textures/Heart.png");

    private final static float sHeartSize = 75;
    private final static float sAmplitude = 0.06666f;
    private final static float sSpeed = 2.5f;

    private final static float sSpeedToBroke = 2;

    private final static GameObjectState<GraphicHeart> sWait = new GameObjectState<GraphicHeart>() {
        public void execute(GraphicHeart obj) { obj.exeWait(); }
    };
    private final static GameObjectState<GraphicHeart> sGrows = new GameObjectState<GraphicHeart>() {
        @Override public void execute(GraphicHeart obj) { obj.exeGrows(); }
    };
    private final static GameObjectState<GraphicHeart> sBroke = new GameObjectState<GraphicHeart>() {
        @Override public void execute(GraphicHeart obj) { obj.exeBroke(); }
    };

    @Override public void onCreate() 
    {
        mShape = this.setGraphicShape(GraphicImage.class);
        mShape.init(
            sTexture,
            sHeartSize,
            sHeartSize,
            this,
            30);
        
        setState(sGrows);
    }

    public void setRank(int rank)
    {
        mRank = rank;
    }

    public int getRank()
    {
        return mRank;
    }

    private void exeWait() 
    {
        BoxPlayer player = GameManager.getInstance().getPlayer();

        if (player == null)
        {
            return;
        }

        if (player.getNumLives() > mRank)
        {
            setState(sGrows);
        }
    }

    private void exeGrows()
    {
        this.setScale(1.0f + Math.abs(sAmplitude * (float)Math.sin(GameLoop.getInstance().getTime() * sSpeed)));

        BoxPlayer player = GameManager.getInstance().getPlayer();

        if (player == null)
        {
            return;
        }

        if (player.getNumLives() <= mRank)
        {
            setState(sBroke);
        }
    }

    private void exeBroke() 
    {
        this.setScale(Math.max(this.getScale() - sSpeedToBroke * GameLoop.getInstance().getFrameTime(), 0.0f));

        if (this.getScale() <= 0)
        {
            this.setState(sWait);
        }
    }
}
