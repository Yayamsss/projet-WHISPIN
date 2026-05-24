/*
    Auteur: Ryane Menaï
*/

/* Example d'objet */

import javafx.scene.paint.Color;

public class ExampleObject extends GameObject
{
    private GraphicRectangle mRectangle;
    /* Fonction appelé après l'initialisation de l'objet. */
    @Override public void onStart()
    {
        System.out.println("ExampleObject onInit.");

        mRectangle = this.setGraphicShape(GraphicRectangle.class);
        mRectangle.init(10, 10, this, Color.WHITE, GraphicsModule.getInstance().getMaxGameLayer(), 10);
    }
    /* Fonction de mise à jour, appelé à chaque frame. Permet de coder le comportement de l'objet */
    @Override public void update()
    {
        /* Tourne de 45 degrés par seconde. */
        // this.setRotation(this.getRotation() + GameLoop.getInstance().getFrameTime() * 45.0f);

        // GraphicsModule gfxModule = GameLoop.getInstance().getGraphicsModule();
// 
        // this.getPosition().x = (float)(gfxModule.getWitdh() / 2.0);
        // this.getPosition().y = (float)(gfxModule.getHeight() / 2.0);
// 
        // this.getPosition().x += (float) (Math.sin(GameLoop.getInstance().getTime()) * 300.0);
// 
        // mRectangle.setBorderRadius((float)((float) Math.sin(GameLoop.getInstance().getTime()) * 100.0));

        /* Affiche la position actuelle. */
        // System.out.println("ExampleObject update: " + GameLoop.getInstance().getFrameCount() + " Position: " + this.getPosition());

        /* Quitte l'application après 300 itterations. */
        //if (GameLoop.getInstance().getFrameCount() >= 300)
        //{
        //    GameLoop.getInstance().quit();
        //}

        this.setPosition(GameCamera.getInstance().calcScreenToWorld(GameLoop.getInstance().getGraphicsModule().getMousePosition()));
    }

    /* Autre fonction de mise à jour, appelé à chaque frame. Celle ci est toujours appelé après que tous les objets
       ont eu leurs update appelé. Permet notamment d'être sûre d'effectuer une opération après qu'un objet a été mis à jour,
       comme par exemple pour une caméra qui suit un joueur.  */
    @Override public void lateUpdate()
    {
        // System.out.println("ExampleObject lateUpdate: " + GameLoop.getInstance().getFrameCount());
    }

    /* Fonction appelé juste avant la destruction de l'objet. Permet d'effectuer des opération juste avant la destruction d'un objet.
       Comme par exemple afficher un écran de GameOver. */
    @Override public void onDestroy()
    {
        System.out.println("ExampleObject onDestroy.");
    }
}
