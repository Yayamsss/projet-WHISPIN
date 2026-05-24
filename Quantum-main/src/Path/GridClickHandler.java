/*
    Auteur: Abdelrahmane Issa
*/

import java.util.ArrayList;

public class GridClickHandler extends GameObject
{
    private PlayerPath mCurrentPath = null;
    private int[] lastTarget = null;

    @Override public void onCreate()
    {
        mCurrentPath = null;
        lastTarget = null;
    }

    @Override public void onDestroy()
    {
        if (mCurrentPath != null) mCurrentPath.destroy();
    }

    @Override public void update()
    {
        // Initialisation des variables
        GraphicsModule gfxModule = GameLoop.getInstance().getGraphicsModule();
        if (!gfxModule.isMouseJustPressed())
        {
            return;
        }

        GameCamera cam = GameCamera.getInstance();
        if (cam == null)
        {
            return;
        }

        BoxPlayer player = GameManager.getInstance().getPlayer();
        if (player == null || player.getParent() == null)
        {
            return;
        }

        BoxRecursive parentBox = player.getParent(); 
        // Obtenir la position de la souris
        Vector2 mousePos = cam.calcScreenToWorld(gfxModule.getMousePosition());

        // Convertir la position de la souris en Cellule du monde
        int[] targetCell = parentBox.getCellFromPosition(mousePos);
        int[] playerCell = new int[]{ player.getCellI(), player.getCellJ() };

        if (!parentBox.isInBound(targetCell[0], targetCell[1])) return; // Clic hors limite
        if (!parentBox.isCellEmpty(targetCell[0], targetCell[1])) return; // Clic sur une boite          

        if (mCurrentPath != null && mCurrentPath.isActive())
        {
            // Si la case cible est la même, on ne recréer pas de chemin
            if (lastTarget != null && targetCell[0] == lastTarget[0] &&
                targetCell[1] == lastTarget[1])
            {
                return;
            }

            // Si la cible est différente, on recrée un chemin
            mCurrentPath.cancel();
            mCurrentPath = null;
        }

        // On sauvegarde la dernière cible
        lastTarget = new int[]{ targetCell[0], targetCell[1] };

        ArrayList<int[]> path = PathFinding.findPath(parentBox, playerCell, targetCell); // Création du chemin

        // Si chemin trouvé on fait bouger le joueur
        if (path!=null && path.size() > 1)
        {
            mCurrentPath = GameLoop.getInstance().addObject(PlayerPath.class);
            mCurrentPath.init(player, path);
        }
    }
}
