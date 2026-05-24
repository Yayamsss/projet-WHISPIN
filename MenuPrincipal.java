import javafx.scene.paint.Color;
import javafx.scene.image.Image;

public class MenuPrincipal extends GameObjectEx
{
    private GraphicBackGround mBackGround;
    private GraphicButton mBtnNiveau;
    private GraphicButton mBtnRegles;
    private GraphicButton mBtnSauvegarde;
    private GraphicButton mBtnParametres;
    private GraphicButton mBtnQuitter;

    private static final float sButtonWidth = 400;
    private static final float sButtonHeight = 60;
    private static final float sButtonSpacing = 80;
    private static final float sStartY = 150;

    @Override
    public void onCreate()
    {
        this.initBackground();
        this.initButtons();
    }

    @Override
    public void onEnabled()
    {
        this.setHidden(false);
    }

    @Override
    public void onDisabled()
    {
        this.setHidden(true);
    }

    @Override
    public void onDestroy()
    {
        if (mBackGround != null)
        {
            mBackGround.destroy();
        }
    }

    public GraphicBackGround getBackGround()
    {
        return mBackGround;
    }

    private void initBackground()
    {
        mBackGround = new GraphicBackGround();
        
        // Charger l'image de fond
        Image bgImage = GraphicHelper.loadImage("fond_principale_ecran-frame0.png");
        GraphicImage bgGraphic = new GraphicImage();
        bgGraphic.init(bgImage, 1920, 1080, this, 0);
        
        mBackGround.addObject(this);
    }

    private void initButtons()
    {
        float centerX = GraphicsModule.getWitdh() / 2;
        float startY = sStartY;

        // Bouton Niveau
        mBtnNiveau = GraphicHelper.createButton("Niveaux", centerX, startY, sButtonWidth, sButtonHeight);
        mBtnNiveau.setCallBack(() -> {
            System.out.println("Niveaux clique");
        });
        mBackGround.addObject(mBtnNiveau);

        // Bouton Règles du jeu
        mBtnRegles = GraphicHelper.createButton("Règles du jeu", centerX, startY + sButtonSpacing, sButtonWidth, sButtonHeight);
        mBtnRegles.setCallBack(() -> {
            System.out.println("Regles du jeu clique");
        });
        mBackGround.addObject(mBtnRegles);

        // Bouton Sauvegarde
        mBtnSauvegarde = GraphicHelper.createButton("Sauvegarde", centerX, startY + sButtonSpacing * 2, sButtonWidth, sButtonHeight);
        mBtnSauvegarde.setCallBack(() -> {
            System.out.println("Sauvegarde clique");
        });
        mBackGround.addObject(mBtnSauvegarde);

        // Bouton Paramètres
        mBtnParametres = GraphicHelper.createButton("Paramètres", centerX, startY + sButtonSpacing * 3, sButtonWidth, sButtonHeight);
        mBtnParametres.setCallBack(() -> {
            System.out.println("Parametres clique");
        });
        mBackGround.addObject(mBtnParametres);

        // Bouton Quitter
        mBtnQuitter = GraphicHelper.createButton("Quitter", centerX, startY + sButtonSpacing * 4, sButtonWidth, sButtonHeight);
        mBtnQuitter.setCallBack(() -> {
            System.out.println("Quitter clique");
            GameLoop.getInstance().quit();
        });
        mBackGround.addObject(mBtnQuitter);
    }
}
