/*
    Auteur : Ali GÜRKAN
*/

import java.util.Random;
import javafx.scene.paint.Color;

public class ElementStar extends GameObjectEx
{
    private GraphicOval mOval;
    private Random mRandomGenerator;
    private int mMaxStep;
    private Vector2 mSpawnRangeX;
    private Vector2 mSpawnRangeY;

    private final static float sMaxRadius = 10;
    private final static int sMaxStep = 500;
    private final static float sOpacity = 0.02f;

    private final static Vector2 sDefaultSpawnRangeX = new Vector2(
        0, GraphicsModule.getWitdh()
    );
    private final static Vector2 sDefaultSpawnRangeY = new Vector2(
        0, GraphicsModule.getHeight() / 3
    );

    private final static GameObjectState<ElementStar> sAppear = new GameObjectState<ElementStar>() {
        public void execute(ElementStar obj) { obj.exeAppear(); };
    };
    private final static GameObjectState<ElementStar> sWait = new GameObjectState<ElementStar>() {
        public void execute(ElementStar obj) { obj.exeWait(); };
    };
    private final static GameObjectState<ElementStar> sDisapear = new GameObjectState<ElementStar>() {
        public void execute(ElementStar obj) { obj.exeDisappear(); };
    };

    @Override public void onCreate() 
    {
        mRandomGenerator = new Random();

        mSpawnRangeX = sDefaultSpawnRangeX;
        mSpawnRangeY = sDefaultSpawnRangeY;

        mMaxStep = mRandomGenerator.nextInt(sMaxStep + 1);
        
        float radius = mRandomGenerator.nextFloat(sMaxRadius);
        mOval = this.setGraphicShape(GraphicOval.class);
        mOval.init(
            radius,
            radius,
            this,
            mRandomGenerator.nextBoolean() ? Color.GREY: Color.WHITE,
            0);
        mOval.setOpacity(0);

        this.setNewRandomPosition();
        this.setState(sAppear);
    }

    public GraphicOval getOval()
    {
        return mOval;
    }

    public void setSpawnRangeX(Vector2 vec)
    {
        mSpawnRangeX = vec;
        setNewRandomPosition();
    }

    public void setSpawnRangeY(Vector2 vec)
    {
        mSpawnRangeY = vec;
        setNewRandomPosition();
    }

    public void setNewRandomPosition()
    {
        this.setPosition(new Vector2(
            mRandomGenerator.nextFloat(mSpawnRangeX.x, mSpawnRangeX.y),
            mRandomGenerator.nextFloat(mSpawnRangeY.x, mSpawnRangeY.y)
        ));
    }

    private void exeAppear()
    {
        mOval.setOpacity(Math.clamp(mOval.getOpacity() + sOpacity, 0.0f, 1.0f));
        
        if (mOval.getOpacity() >= 1.0f)
        {
            setState(sWait);
        }
    }

    private void exeWait()
    {
        if (isAboveStep(mMaxStep))
        {
            setState(sDisapear);
        }
    }

    private void exeDisappear()
    {
        mOval.setOpacity(Math.clamp(mOval.getOpacity() - sOpacity, 0.0f, 1.0f));

        if (mOval.getOpacity() <= 0.0f)
        {
            setState(sAppear);
        }
    }
}
