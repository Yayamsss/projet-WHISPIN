package src.main;

import src.entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    // check the collison when the direction is up/down/left/right
    // get the world coordonate of the player from top,down,left,right
    // get the col and row of the screen where the player is
    // check the collision between the player the th 2 blocks close to it relatively
    // to the direction
    public void checkTile(Entity entity) {
    //TO DO
    }
}
