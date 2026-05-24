/*
    Auteur: Ryane Menaï
*/

import java.util.ArrayList;
import javafx.scene.input.KeyCode;

public class GameManager extends GameObject
{
    public enum MessageType
    {
        PlayerWin,
        PlayerDefeat
    }

    private static GameManager sInstance = null;

    private static final String sLevelPathFormat = "assets/Levels/Level%d.lvl";
    private static final String sTestLevelPath = "assets/Levels/LevelTest.lvl";
    private static final String sCustomLevelPath = "assets/Save/CustomLevel.lvl";
    private static final String sSavedProgressPath = "assets/Save/LevelSave.lvl";

    private static final int sNumLevels = 12;
    private static final int sCustomLevelId = -1;
    private static final int sDebugLevelId = -2;

    private SaveDataManager mSaveManager;

    private int mCurrentLevelId;
    private int mUnlockedLevelId;
    private float mCompletionTime;
    private boolean mIsGamePaused;
    private boolean mShouldGameEnd;
    private boolean mDidPlayerWin;
    private GridClickHandler mClickHandler;
    private UndoManager mUndoManager;
    private MoveLogger mMoveLogger;
    private AutoSolver mAi;
    private BoxManager mBoxManager;
    private BoxPlayer mPlayer;
    private ArrayList<BoxGoal> mGoalList;

    public static GameManager getInstance()
    {
        return sInstance;
    }

    public static int getNumLevels()
    {
        return sNumLevels;
    }

    public static int getCustomLevelId()
    {
        return sCustomLevelId;
    }

    public static int getDebugLevelId()
    {
        return sDebugLevelId;
    }

    public static String getCustomLevelPath()
    {
        return sCustomLevelPath;
    }
    
    @Override public void onCreate()
    {
        if (sInstance != null)
        {
            this.destroy();
        }
        else
        {
            sInstance = this;
            
            mCurrentLevelId = 0;
            mCompletionTime = 0.0f;

            mShouldGameEnd = false;
            mIsGamePaused = false;
            mDidPlayerWin = false;

            mBoxManager = null;
            mPlayer = null;
            mUndoManager = null;
            mMoveLogger = null;
            mAi = null;
            mGoalList = new ArrayList<>();

            SaveDataManager.createInstance();
            mSaveManager = SaveDataManager.getInstance();

            if (mSaveManager.load())
            {
                mUnlockedLevelId = mSaveManager.pUnlockedLevelId;
            }
            else
            {
                mUnlockedLevelId = 0;
            }
        }
    }

    @Override public void onDestroy()
    {
        if (sInstance == this)
        {
            SaveDataManager.destroyInstance();
        }
    }

    @Override public void update()
    {
        if (this.isGameRuning() && this.getCompletionTime() > 0.5f)
        {
            GraphicsModule gfxInstance = GraphicsModule.getInstance();

            if (gfxInstance.getKeyPressed() == KeyCode.I)
            {
                if (mAi.isActive())
                {
                    mAi.stopSolving();
                }
                else
                {
                    mAi.startSolving();
                }
            }
            else if (gfxInstance.getKeyPressed() == KeyCode.U)
            {
                this.sendMsg(mUndoManager, UndoManager.MessageType.Undo.ordinal());
            }
            else if (gfxInstance.getKeyPressed() == KeyCode.R)
            {
                this.restartGame();
            }
        }
    }

    @Override public void lateUpdate()
    {
        if (!this.isGameRuning())
        {
            return;
        }

        mUndoManager.commitPendingMove();
        mCompletionTime += GameLoop.getInstance().getFrameTime();

        if (!mPlayer.canRespawn())
        {
            mDidPlayerWin = false;

            this.sendGameEndMsg();
            this.forceEndGame();

            return;
        }

        for (BoxGoal goal : mGoalList)
        {
            if (!goal.checkGoalReached())
            {
                return;
            }
        }

        mDidPlayerWin = true;

        if (mCurrentLevelId == mUnlockedLevelId)
        {
            mUnlockedLevelId++;
        }

        this.sendGameEndMsg();
        this.forceEndGame();

        mSaveManager.pUnlockedLevelId = mUnlockedLevelId;

        if (!this.isCustomLevel() && !this.isDebugLevel())
        {
            mSaveManager.pCompletionTimes[mCurrentLevelId] = Math.min(
                mSaveManager.pCompletionTimes[mCurrentLevelId], mCompletionTime);
        }

        if (this.isSavedLevel())
        {
            mSaveManager.pSavedLevelId = 0;
            mSaveManager.pSavedLevelCompletionTime = Float.MAX_VALUE;
        }

        mSaveManager.save();
    }

    public boolean isGameRuning()
    {
        return (mPlayer != null) && (mBoxManager != null) && !mShouldGameEnd && !mIsGamePaused;
    }

    public boolean isGamePaused()
    {
        return mPlayer != null && mBoxManager != null && !mShouldGameEnd && mIsGamePaused;
    }

    public boolean isCustomLevel()
    {
        return mCurrentLevelId == sCustomLevelId;
    }

    public boolean isDebugLevel()
    {
        return mCurrentLevelId == -2;
    }

    public boolean isSavedLevel()
    {
        return mCurrentLevelId == mSaveManager.pSavedLevelId
            && mSaveManager.pSavedLevelCompletionTime != Float.MAX_VALUE;
    }

    public int getCurrentLevelId()
    {
        return mCurrentLevelId;
    }

    public int getUnlockedLevelId()
    {
        return mUnlockedLevelId;
    }

    public float getCompletionTime()
    {
        return mCompletionTime;
    }

    public BoxPlayer getPlayer()
    {
        return mPlayer;
    }

    public BoxManager getBoxManager()
    {
        return mBoxManager;
    }

    public BoxGoal getGoal(int i)
    {
        return mGoalList.get(i);
    }

    public int getNumGoals()
    {
        return mGoalList.size();
    }

    public boolean shouldGameEnd()
    {
        return mShouldGameEnd;
    }

    public boolean didPlayerWin()
    {
        return mDidPlayerWin;
    }

    public void setNextLevel(int levelId)
    {
        if (this.isGameRuning() || this.isGamePaused())
        {
            throw new IllegalAccessError("Impossible d'appeler setNextLevel alors que le jeu est en cours.");
        }
        else if (levelId >= sNumLevels || levelId < sDebugLevelId)
        {
            throw new IndexOutOfBoundsException("levelId est hors limite.");
        }
        else if (levelId > mUnlockedLevelId)
        {
            throw new IllegalArgumentException("levelId est supérieur à mUnlockedLevelId.");
        }

        mCurrentLevelId = levelId;
    }

    public boolean addNewGoal(BoxRecursive parentBox, int i, int j)
    {
        if (parentBox.isInBound(i, j))
        {
            BoxGoal newGoal = GameLoop.getInstance().addObject(BoxGoal.class);

            newGoal.setCell(parentBox, i, j);
            mGoalList.add(newGoal);
        }

        return false;
    }

    public void startNewGame()
    {
        mIsGamePaused = false;
        mShouldGameEnd = false;
        mCompletionTime = 0.0f;

        mUndoManager = GameLoop.getInstance().addObject(UndoManager.class);
        mAi = GameLoop.getInstance().addObject(AutoSolver.class);
        mMoveLogger = GameLoop.getInstance().addObject(MoveLogger.class);
        mPlayer = GameLoop.getInstance().addObject(BoxPlayer.class);
        mBoxManager = GameLoop.getInstance().addObject(BoxManager.class);
        mClickHandler = GameLoop.getInstance().addObject(GridClickHandler.class);

        String levelPath;

        if (mCurrentLevelId == mSaveManager.pSavedLevelId
         && mSaveManager.pSavedLevelCompletionTime != Float.MAX_VALUE)
        {
            levelPath = sSavedProgressPath;
            mCompletionTime = mSaveManager.pSavedLevelCompletionTime;
        }
        else if (this.isCustomLevel())
        {
            levelPath = sCustomLevelPath;
        }
        else if (this.isDebugLevel())
        {
            levelPath = sTestLevelPath;
        }
        else
        {
            levelPath = String.format(sLevelPathFormat, mCurrentLevelId);
        }
        
        if (!LevelLoader.initLevel(levelPath, mBoxManager))
        {
            this.forceEndGame();
        }
    }

    public void pauseGame()
    {
        mPlayer.setEnabled(false);
        mBoxManager.getRootBox().setEnabled(false);
        mAi.setEnabled(false);
        mUndoManager.setEnabled(false);
        mMoveLogger.setEnabled(false);
        mClickHandler.setEnabled(false);

        mIsGamePaused = true;
    }

    public void resumeGame()
    {
        if (!this.isGamePaused())
        {
            return;
        }

        mIsGamePaused = false;

        mClickHandler.setEnabled(true);
        mMoveLogger.setEnabled(true);
        mUndoManager.setEnabled(true);
        mAi.setEnabled(true);
        mPlayer.setEnabled(true);
        mBoxManager.getRootBox().setEnabled(true);
    }

    public void restartGame()
    {
        this.forceEndGame();
        this.startNewGame();
    }

    public void forceEndGame()
    {
        if (!this.isGamePaused() && !this.isGameRuning())
        {
            return;
        }

        mShouldGameEnd = true;

        mPlayer.destroy();
        mBoxManager.destroy();
        mUndoManager.destroy();
        mMoveLogger.destroy();
        mClickHandler.destroy();
        mAi.destroy();
        mGoalList.clear();

        mAi = null;
        mUndoManager = null;
        mMoveLogger = null;
        mPlayer = null;
        mBoxManager = null;
        mClickHandler = null;
    }

    public void saveLevelProgress()
    {
        if (!this.isGamePaused() && !this.isGameRuning())
        {
            return;
        }

        mSaveManager.pSavedLevelCompletionTime = mCompletionTime;
        mSaveManager.pSavedLevelId = mCurrentLevelId;
        mSaveManager.save();
        LevelSaver.saveLevel(sSavedProgressPath, mBoxManager.getRootBox());
    }

    public void restoreLevelProgress()
    {
        if (this.isGamePaused() || this.isGameRuning())
        {
            return;
        }

        if (mSaveManager.pSavedLevelId < sCustomLevelId
         || mSaveManager.pSavedLevelId > mUnlockedLevelId
         || mSaveManager.pSavedLevelCompletionTime == Float.MAX_VALUE)
        {
            // Aucune partie à restorer
            return;
        }

        this.setNextLevel(mSaveManager.pSavedLevelId);
        this.startNewGame();
    }

    public void resetSave()
    {
        mSaveManager.reset();

        mUnlockedLevelId = mSaveManager.pUnlockedLevelId;
    }

    public void unlockAllLevels()
    {
        mUnlockedLevelId = sNumLevels;
        mSaveManager.pUnlockedLevelId = sNumLevels;

        mSaveManager.save();
    }

    private void sendGameEndMsg()
    {
        MenuEndGame menu = GameLoop.getInstance().tryFindObject(MenuEndGame.class);

        if (menu == null)
        {
            throw new IllegalStateException("MenuEndGame n'existe pas lors de l'envoie du message de fin de partie.");
        }

        this.sendMsg(menu, mDidPlayerWin ? MessageType.PlayerWin.ordinal() : MessageType.PlayerDefeat.ordinal());
    }
}