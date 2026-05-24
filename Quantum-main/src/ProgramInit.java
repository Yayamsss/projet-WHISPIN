import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ProgramInit extends Application {
    private static ProgramInit sInstance = null;
    private static final long sTargetDeltaTime = 16666666;
    private static final long sTargetDeltaTimeBias = 1000000;
    private Stage mStage;
    private AnimationTimer mAppRunner;
    private long mLastTimeStamp;

    public static ProgramInit getInstance() { return sInstance; }
    public Stage getStage() { return mStage; }

    @Override public void start(Stage stage)
    {
        if (sInstance != null)
        {
            throw new IllegalCallerException("Instanciated more than one ProgramInit class.");
        }

        sInstance = this;
        mStage = stage;
        mLastTimeStamp = 0;

        GameLoop.createInstance();
        GameLoop.getInstance().init();

        mAppRunner = new AnimationTimer() {
            @Override public void handle(long timeStamp) {
                if (GameLoop.getInstance().shouldRun())
                {
                    /* Execute run uniquement si il s'est écoulé assez de temps (on cible 60 fps) */
                    if (timeStamp - mLastTimeStamp >= sTargetDeltaTime - sTargetDeltaTimeBias)
                    {
                        GameLoop.getInstance().run();
                        mLastTimeStamp = timeStamp;
                    }
                }
                else
                {
                    System.out.println();
                    mAppRunner.stop();
                    GameLoop.getInstance().destroy();
                    GameLoop.destroyInstance();
                    Platform.exit();
                }
            }
        };

        mAppRunner.start();
    }
}
