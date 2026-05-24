/*
    Auteur: Ryane Menaï
*/

import java.util.LinkedList;

/*
    Synopsys:
        Cette class gère le control flow de l'application. C'est un singleton,
        c'est à dire qu'une seule instance de cette class peut exister à un moment donné.
*/
public class GameLoop
{
    /* Instance de la classe. */
    private static GameLoop sInstance = null;

    static int objCount = 1500;

    /* Le nombre d'image géneré depuis le démarage de l'application. */
    private int mFrameCount;
    /* Le nombre d'objets actifs. */
    private int mObjectCount;
    /* Défini si l'application doit continuer son execution. */
    private boolean mShouldRun;

    /* La liste globale de tous les objets actifs */
    private LinkedList<GameObject> mObjectList;
    /* La liste des objets créer qui ne sont pas encore initialisé à initialiser */
    private LinkedList<GameObject> mInitList;
    /* La liste des objets qui sont à détruire. */
    private LinkedList<GameObject> mDestroyList;

    private GraphicsModule mGfxModule;

    private GameLoop() {}

    public static GameLoop createInstance()
    {
        if (sInstance == null)
        {
            sInstance = new GameLoop();
        }

        return sInstance;
    }

    public static void destroyInstance()
    {
        sInstance = null;
    }

    public static GameLoop getInstance()
    {
        return sInstance;
    }

    public GraphicsModule getGraphicsModule()
    {
        return mGfxModule;
    }

    public int getFrameCount()
    {
        return mFrameCount;
    }

    public int getObjectCount()
    {
        return mObjectCount;
    }

    public int getFrameRate()
    {
        return 60;
    }

    public float getFrameTime()
    {
        return (1.0f / 60.0f);
    }

    public float getTime()
    {
        return getFrameTime() * getFrameCount();
    }

    /**
        Ajoute l'objet newObj à l'application.
        Utilisation: MyObject myObj = GameLoop.getInstance().addObject(new MyObject);

        @param newOjb L'objet à ajouter
        @returns L'objet qui a été ajouter
    **/
    public <T extends GameObject> T addObject(Class<T> objClass)
    {
        T newObj;

        try
        {
            newObj = objClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            System.out.println("Erreur lors de la creation de " + objClass.getName() + ". Erreur: " + e.getCause());
            return null;
        }
        
        mInitList.add(newObj);
        return newObj;
    }

    /**
        Retire objToRemove de l'application

        @param objToRemove L'objet à retirer.
    **/
    public <T extends GameObject> void removeObject(T objToRemove)
    {
        if (mDestroyList.contains(objToRemove))
        {
            throw new IllegalArgumentException("L'objet " + objToRemove.getClass().getName() + " est déjà en queue pour être détruit.");
        }
        else if (! mObjectList.contains(objToRemove) && ! mInitList.contains(objToRemove))
        {
            throw new IllegalArgumentException("L'objet " + objToRemove.getClass().getName() + " n'existe pas.");
        }

        mDestroyList.add(objToRemove);
    }

    @SuppressWarnings("unchecked")
    public <T extends GameObject> T tryFindObject(Class<T> objClass)
    {
        for (GameObject obj : mObjectList)
        {
            if (objClass.isInstance(obj))
            {
                return (T) obj;
            }
        }

        return null;
    }

    /*
        Fonction d'initialisation. Initialise les différents système de l'application.
    */
    public void init()
    {
        System.out.println("GameLoop init.");

        mFrameCount = 0;
        mObjectCount = 0;
        mShouldRun = true;
        mObjectList = new LinkedList<GameObject>();
        mInitList = new LinkedList<GameObject>();
        mDestroyList = new LinkedList<GameObject>();
        mGfxModule = new GraphicsModule();

        mGfxModule.init();

        /* Init various game systems here */

        this.addObject(GameCamera.class);
        this.addObject(GameManager.class);
        this.addObject(MenuManager.class);
    }

    /*
        Fonction de destruction. Detruit les différents système de l'application et retire
        tous les objets créer.
    */
    public void destroy()
    {
        this.removeAllObjects();
    }

    /*
        Fonction de mise à jour de l'application. Les opérations sont effectuées dans cet ordre:
            - Initialise tous les objets qui sont à ajouter et les ajoutes à la liste globale des objets.
            - Appelle update puis lateUpdate sur tous les objets actifs.
            - Detruit tous les objets qui sont à détruire.
            - Fait le rendu JavaFX
            - Attends si nécessaire que 16.66666ms soit passé.
    */
    public void run()
    {
        System.out.print("NumObj " + mObjectCount + "                  \r");

        this.updateInitList();
        this.updateObjectList();
        this.updateDestroyList();

        mGfxModule.render();
        mFrameCount++;
    }

    /*
        Fonction de mise à jour de la liste des objets à initialiser.
    */
    private void updateInitList()
    {
        while (! mInitList.isEmpty())
        {
            GameObject obj = mInitList.getFirst();
            mInitList.remove();

            mObjectCount++;

            if (!obj.isQueuedForDestroy())
            {
                obj.onStart();
            }
            
            mObjectList.add(obj);
        }
    }

    /*
        Fonction de mise à jour de la liste des objets actifs.
    */
    private void updateObjectList()
    {
        /* Updates all objects */
        for (GameObject obj : mObjectList)
        {
            if (obj.isEnabled())
            {
                obj.update();
            }
        }

        for (GameObject obj : mObjectList)
        {
            if (obj.isEnabled())
            {
                obj.lateUpdate();
            }
        }
    }

    /*
        Fonction de mise à jour de la liste des objets à détruire.
    */
    private void updateDestroyList()
    {
        while (! mDestroyList.isEmpty())
        {
            GameObject obj = mDestroyList.getFirst();

            mDestroyList.remove(obj);
            mObjectList.remove(obj);
            mObjectCount--;
        }
    }

    /*
        Permet de détruire tous les objets de l'application
    */
    private void removeAllObjects()
    {
        this.updateInitList();
        for (GameObject obj : mObjectList)
        {
            if (obj.isQueuedForDestroy())
            {
                continue;
            }

            obj.destroy();
        }

        this.updateDestroyList();
    }

    public boolean shouldRun()
    {
        return mShouldRun;
    }

    /*
        Fonction permetant de quiter l'application.
    */
    public void quit()
    {
        System.out.println("GameLoop.quit()");
        mShouldRun = false;
    }
}