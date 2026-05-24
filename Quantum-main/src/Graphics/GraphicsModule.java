import java.util.ArrayList;
import java.util.LinkedList;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

public class GraphicsModule 
{
    private static GraphicsModule sInstance = null;

    private static final float sRenderWidth = 1920.0f;
    private static final float sRenderHeight = 1080.0f;
    private static final Vector2 sScreenCenter = new Vector2(sRenderWidth / 2, sRenderHeight / 2);

    private static final Color sDefaultClearColor = GraphicHelper.makeColorFromHex(0x59FFEC);

    private float mSceneWidth;
    private float mSceneHeight;

    private KeyCode mKeyPressed;
    private boolean mIsMouseJustPressed;
    private boolean mIsMousePressed;
    private Vector2 mCurrentMousePosition;

    private ArrayList<LinkedList<GraphicShape>> mDrawLists;

    private Matrix3x2 mUiProjMtx;
    private Matrix3x2 mNdcToJavaFxMtx;
    private Matrix3x2 mScreenAdaptMtx;
    private Matrix3x2 mInverseFinalUiMtx;
    private Matrix3x2 mFinalUiMtx;
    private Matrix3x2 mFinalCamMtx;
    private Matrix3x2 mObjMtx;
    private Affine mJavaFxObjMtx;

    private boolean mIsKeyPressed;

    private Group mRootGroup;
    private Stage mJavaFxStage;
    private Scene mScene;
    private Canvas mCanvas;
    private GraphicsContext mJFXContext;

    private Color mClearColor;

    public void init()
    {
        if (sInstance != null)
        {
            throw new IllegalAccessError();
        }

        sInstance = this;

        mSceneWidth = 854;
        mSceneHeight = 480;

        mClearColor = sDefaultClearColor;

        mKeyPressed = null;
        mIsMouseJustPressed = false;
        mIsMousePressed = false;
        mCurrentMousePosition = new Vector2();

        mDrawLists = new ArrayList<>();
        for (int i = 0; i < 48; i++)
        {
            mDrawLists.add(new LinkedList<>());
        }

        mUiProjMtx = new Matrix3x2();
        mNdcToJavaFxMtx = new Matrix3x2();
        mScreenAdaptMtx = new Matrix3x2();
        mInverseFinalUiMtx = new Matrix3x2();
        mFinalUiMtx = new Matrix3x2();
        mFinalCamMtx = new Matrix3x2();
        mObjMtx = new Matrix3x2();
        mJavaFxObjMtx = new Affine();

        mIsKeyPressed = false;

        mJavaFxStage = ProgramInit.getInstance().getStage();
        mRootGroup = new Group();
        mScene = new Scene(mRootGroup);
        mCanvas = new Canvas(mSceneWidth, mSceneHeight);
        mJFXContext = mCanvas.getGraphicsContext2D();

        mScene.widthProperty().addListener((unused, oldValue, newValue) -> {
            mSceneWidth = newValue.floatValue();
            mCanvas.setWidth(mSceneWidth);
        });
        mScene.heightProperty().addListener((unused, oldValue, newValue) -> {
            mSceneHeight = newValue.floatValue();
            mCanvas.setHeight(mSceneHeight);
        });
        mScene.setOnKeyPressed(event -> {
            mIsKeyPressed = true;
            mKeyPressed = event.getCode();
        });

        mCanvas.setOnMouseMoved(event -> {
            mCurrentMousePosition.x = (float) event.getX();
            mCurrentMousePosition.y = (float) event.getY();

            mCurrentMousePosition = mInverseFinalUiMtx.mul(mCurrentMousePosition);
        });
        mCanvas.setOnMousePressed(event -> {
            mIsMouseJustPressed = true;
            mIsMousePressed = true;
        });
        mCanvas.setOnMouseReleased(event -> {
            mIsMousePressed = false;
        });

        mRootGroup.getChildren().add(mCanvas);

        mJavaFxStage.setScene(mScene);
        mJavaFxStage.show();
    }

    public static GraphicsModule getInstance()
    {
        return sInstance;
    }

    public static float getWitdh() 
    {
        return sRenderWidth;
    }

    public static float getHeight() 
    {
        return sRenderHeight;
    }

    public static Color getDefaultClearColor()
    {
        return sDefaultClearColor;
    }

    public static Vector2 getScreenCenter()
    {
        return sScreenCenter;
    }
    
    public float getSceneWidth()
    {
        return mSceneWidth;
    }

    public float getSceneHeight()
    {
        return mSceneHeight;
    }

    public Matrix3x2 getUiProjMtx()
    {
        return mUiProjMtx;
    }

    public KeyCode getKeyPressed()
    {
        if (mIsKeyPressed)
        {
            return mKeyPressed;
        }

        return null;
    }

    public Vector2 getMousePosition()
    {
        return mCurrentMousePosition;
    }

    public LinkedList<GraphicShape> getDrawLists(int i)
    {
        return mDrawLists.get(i);
    }

    public int getNumLayers()
    {
        return mDrawLists.size();
    }

    public int getUILayer()
    {
        return this.getNumLayers() - 18;
    }

    public int getMaxGameLayer()
    {
        return this.getUILayer() - 1;
    }

    public boolean isMouseJustPressed()
    {
        return mIsMouseJustPressed;
    }

    public boolean isMousePressed()
    {
        return mIsMousePressed;
    }

    public void setClearColor(Color newClearColor)
    {
        mClearColor = newClearColor;
    }

    public void addShape(GraphicShape shape)
    {
        mDrawLists.get(shape.getLayer()).addLast(shape);
    }

    public void removeShape(GraphicShape shape)
    {
        mDrawLists.get(shape.getLayer()).remove(shape);
    }

    public Matrix3x2 getFinalCamMtx()
    {
        return mFinalCamMtx;
    }

    private void calcUiProjMtx()
    {
        mUiProjMtx.xx = 2.0f / (sRenderWidth - 0.0f);
        mUiProjMtx.xy = 0.0f;
        mUiProjMtx.yx = 0.0f;
        mUiProjMtx.yy = 2.0f / (sRenderHeight - 0.0f);
        mUiProjMtx.zx = -(sRenderWidth + 0.0f) / (sRenderWidth - 0.0f);
        mUiProjMtx.zy = -(sRenderHeight + 0.0f) / (sRenderHeight - 0.0f);
    }

    private void calcNdcToJavaFxMtx()
    {
        float halfWidth = mSceneWidth / 2.0f;
        float halfHeight = mSceneHeight / 2.0f;

        mNdcToJavaFxMtx.xx = halfWidth;
        mNdcToJavaFxMtx.yy = halfHeight;
        mNdcToJavaFxMtx.zx = halfWidth;
        mNdcToJavaFxMtx.zy = halfHeight;

        mNdcToJavaFxMtx.xy = 0.0f;
        mNdcToJavaFxMtx.yx = 0.0f;
    }

    private void calcScreenAdaptMtx()
    {
        float targetAspectRatio = sRenderHeight / sRenderWidth;
        float screenAspectRatio = mSceneHeight / mSceneWidth;

        if (screenAspectRatio > targetAspectRatio)
        {
            /*
                L'écran est dans cette configuration (portrait):
                    *--*
                    |  |
                    |  |
                    |  |
                    *--*            
            */
           mScreenAdaptMtx.xx = 1.0f;
           mScreenAdaptMtx.zx = 0.0f;
           mScreenAdaptMtx.yy = targetAspectRatio / screenAspectRatio;
           mScreenAdaptMtx.zy = (mSceneHeight / 2.0f) * (1.0f - mScreenAdaptMtx.yy);
        }
        else
        {
            /*
                L'écran est dans cette configuration (paysage): 
                    *------------------*
                    |                  |
                    |                  |
                    |                  |
                    *------------------*            
            */
           mScreenAdaptMtx.xx = screenAspectRatio / targetAspectRatio;
           mScreenAdaptMtx.zx = (mSceneWidth / 2.0f) * (1.0f - mScreenAdaptMtx.xx);
           mScreenAdaptMtx.zy = 0.0f;
           mScreenAdaptMtx.yy = 1.0f;
        }

        mScreenAdaptMtx.xy = 0.0f;
        mScreenAdaptMtx.yx = 0.0f;
    }

    private void calcFinalUiMtx()
    {
        mFinalUiMtx.set(mScreenAdaptMtx);
        mFinalUiMtx.mul(mNdcToJavaFxMtx);
        mFinalUiMtx.mul(mUiProjMtx);
        
        mInverseFinalUiMtx.set(mFinalUiMtx);
        mInverseFinalUiMtx.inverse();
    }

    private void calcFinalCamMtx()
    {
        mFinalCamMtx.set(mScreenAdaptMtx);
        mFinalCamMtx.mul(mNdcToJavaFxMtx);
        mFinalCamMtx.mul(GameCamera.getInstance().calcViewProjectionMtx());
    }

    private void clearScene(Color color) 
    {
        mJFXContext.setTransform(mFinalUiMtx.toAffine());
        mJFXContext.setFill(color);
        mJFXContext.fillRect(0, 0, sRenderWidth, sRenderHeight);
    }

    private void clearScreenBorders(Color color)
    {
        mJFXContext.setTransform(Matrix3x2.sIdentity.toAffine());
        mJFXContext.setFill(color);

        if (mScreenAdaptMtx.xx == 1.0f)
        {
            /* Mode portrait: */
            mJFXContext.fillRect(0, 0, mSceneWidth, mScreenAdaptMtx.zy);
            mJFXContext.fillRect(0, mSceneHeight - mScreenAdaptMtx.zy, mSceneWidth, mScreenAdaptMtx.zy);
        }
        else
        {
            /* Mode paysage */
            mJFXContext.fillRect(0, 0, mScreenAdaptMtx.zx, mSceneHeight);
            mJFXContext.fillRect(mSceneWidth - mScreenAdaptMtx.zx, 0, mScreenAdaptMtx.zx, mSceneHeight);
        }
    }
    
    public void render()
    {
        int numDrawCall = 0;

        if (this.getKeyPressed() == KeyCode.F11)
        {
            mJavaFxStage.setFullScreen(!mJavaFxStage.isFullScreen());
        }

        this.calcUiProjMtx();
        this.calcNdcToJavaFxMtx();
        this.calcScreenAdaptMtx();
        this.calcFinalUiMtx();
        this.calcFinalCamMtx();
        
        this.clearScene(mClearColor);

        // System.out.printf("det(UiMtx) = %f, det(camMtx) = %f, det(ndcToJavaFxMtx) = %f", uiMtx.det(), camMtx.det(), ndcToJavaFxMtx.det());

        for (int i = 0; i < getNumLayers(); i++)
        {
            Matrix3x2 projMtx = i < getUILayer() ? mFinalCamMtx : mFinalUiMtx;

            for (GraphicShape shape : mDrawLists.get(i))
            {
                if (!shape.isHiden())
                {
                    mObjMtx.set(projMtx);
                    mObjMtx.mul(shape.getGameObject().getTransformMtx());

                    mObjMtx.toAffine(mJavaFxObjMtx);
                    mJFXContext.setTransform(mJavaFxObjMtx);

                    shape.drawObject(mJFXContext);

                    numDrawCall++;
                }
            }
        }

        System.out.print("NumDrawCalls = " + numDrawCall + " ");
        this.clearScreenBorders(mClearColor);

        mIsKeyPressed = false;
        mIsMouseJustPressed = false;
    }
}