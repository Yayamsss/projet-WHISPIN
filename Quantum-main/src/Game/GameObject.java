/*
    Auteur: Ryane Menaï
*/

public class GameObject
{
    private Vector2 mPosition;
    private float mRotation;
    private float mScale;
    private boolean mIsEnabled;
    private boolean mIsQueuedForDestroy;
    private GraphicShape mGraphicsObj;

    public GameObject()
    {
        this.init();
        this.onCreate();
    }

    public final Vector2 getPosition()
    {
        return mPosition;
    }

    public final float getRotation()
    {
        return mRotation;
    }

    public final float getScale()
    {
        return mScale;
    }

    public final Matrix3x2 getTransformMtx()
    {
        return Matrix3x2.transform(mPosition.x, mPosition.y, mRotation, mScale);
    }

    public final GraphicShape getGraphicShape()
    {
        return mGraphicsObj;
    }

    public final <S extends GraphicShape> S getGraphicShapeAs(Class<S> shapeClass)
    {
        if (mGraphicsObj == null)
        {
            return null;
        }

        if (shapeClass.isInstance(mGraphicsObj))
        {
            return shapeClass.cast(mGraphicsObj);
        }
        
        return null;
    }

    public void setHidden(boolean hidden)
    {
        if (mGraphicsObj == null)
        {
            throw new IllegalAccessError("Impossible d'appeler setHidden alors que mShape est null.");
        }

        mGraphicsObj.setHiden(hidden);
    }

    public void setPosition(Vector2 newPosition)
    {
        mPosition.set(newPosition);
    }

    public void setRotation(float newRotation)
    {
        mRotation = newRotation;
    }

    public void setScale(float newScale)
    {
        if (newScale < 0.0f)
        {
            throw new IllegalArgumentException("newScale can't be negative.");
        }

        mScale = newScale;
    }

    public final void setTransformMtx(Matrix3x2 transform)
    {
        mPosition.x = transform.zx;
        mPosition.y = transform.zy;

        float scaleX = (float) Math.sqrt(transform.xx * transform.xx + transform.xy * transform.xy);
        float scaleY = (float) Math.sqrt(transform.yx * transform.yx + transform.yy * transform.yy);

        if (Math.abs(scaleX - scaleY) > 0.001f)
        {
            throw new IllegalArgumentException("X and Y scaling is not the same in new transform matrix");
        }
        else if (scaleX < 0.0f || scaleY < 0.0f)
        {
            throw new IllegalArgumentException("Negative scaling in new transform matrix");
        }

        mScale = scaleX;
        mRotation = scaleX > 0.001f ? (float) Math.atan2(transform.xy / scaleX, transform.xx / scaleX) : 0.0f;
    }

    @SuppressWarnings("unchecked")
    public final <S extends GraphicShape> S setGraphicShape(Class<S> shapeClass)
    {
        if (mGraphicsObj != null)
        {
            mGraphicsObj.destroy();
        }

        try
        {
            mGraphicsObj = shapeClass.getConstructor().newInstance();
        }
        catch (Exception e)
        {
            System.out.println("Exception in GameObject.setGraphicShape");
        }

        return (S) mGraphicsObj;
    }

    public final boolean isEnabled()
    {
        return mIsEnabled;
    }

    public final boolean isQueuedForDestroy()
    {
        return mIsQueuedForDestroy;
    }

    public final void setEnabled(boolean enabled)
    {
        if (enabled == mIsEnabled)
        {
            return;
        }

        mIsEnabled = enabled;
        if (enabled)
            this.onEnabled();
        else
            this.onDisabled();
    }

    public final void sendMsg(GameObject to, int msg)
    {
        to.receiveMsg(this, msg);
    }

    public final void followObject(Vector2 localPos, float localRotation, float localScale, GameObject obj)
    {
        Matrix3x2 objMtx = obj.getTransformMtx();
        Matrix3x2 localMtx = Matrix3x2.transform(localPos.x, localPos.y, localRotation, localScale);

        this.setTransformMtx(Matrix3x2.mul(objMtx, localMtx));
    }

    /* Fonction à override dans les classes enfants.
       Lorsqu'un objet fait obj.sendMsg(votreObjet, msg);
       la fonction receiveMsg est appelé et permet ainsi
       de traiter le message. */
    protected void receiveMsg(GameObject from, int msg) {}

    private final void init()
    {
        mPosition = new Vector2();
        mRotation = 0.0f;
        mScale = 1.0f;
        mIsEnabled = true;
        mIsQueuedForDestroy = false;
        mGraphicsObj = null;
    }

    public final void destroy()
    {
        if (mIsQueuedForDestroy)
        {
            /* Ne pas afficher l'avertissement lors de la destruction de GameLoop */
            if (GameLoop.getInstance().shouldRun())
            {
                System.out.println("[WARNING] L'objet \"" + this.getClass().getName() + "\" a déjà été détruit.");
            }
            
            return;
        }

        if (mGraphicsObj != null)
        {
            mGraphicsObj.destroy();
        }

        mIsQueuedForDestroy = true;
        GameLoop.getInstance().removeObject(this);
        this.onDestroy();
    }

    /* Fonction à override dans les classes enfants,
       ATTENTION: A ne pas appeler vous même ! Elles sont appelés
       par GameLoop à des moments précis ! */
    /* A la création de l'objet (dans le constructeur) */
    public void onCreate() {}
    /* Avant le premier appel à update. */
    public void onStart() {}
    /* Quand l'objet est activé */
    protected void onEnabled() {}
    /* Quand l'objet est desactivé */
    protected void onDisabled() {}
    /* Après que l'objet est été rendu à l'écran. */
    public void onRender() {}
    /* Juste avant la destruction de l'objet. */
    public void onDestroy() {}

    /* Appeler chaque frame */
    public void update() {}
    /* Aussi appeler chaque frame, mais après
       que la fonction update ai été appelé pour tous les
       objets. */
    public void lateUpdate() {}
}