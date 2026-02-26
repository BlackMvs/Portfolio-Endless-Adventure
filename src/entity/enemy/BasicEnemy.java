package entity.enemy;

import entity.player.Player;
import gameExtended2D.AnimationExtended;
import gameExtended2D.TileMapExtended;

/**
 * A basic type of enemy that appears in the game.
 * <p>
 * This enemy has standard animations for idle, walking, attacking, and dying.
 * It interacts with the player and the environment based on the game's tile map and settings.
 */
public class BasicEnemy extends Enemy {

    /**
     * Constructs a new BasicEnemy instance with the specified position, world level,
     * tile map, and player reference.
     *
     * @param x          the x-coordinate position of the enemy
     * @param y          the y-coordinate position of the enemy
     * @param worldLevel the level of the game world this enemy belongs to
     * @param tileMap    the tile map the enemy uses for movement and collision
     * @param player     the player this enemy may interact or collide with
     */
    public BasicEnemy(float x, float y, int worldLevel, TileMapExtended tileMap, Player player) {
        super(x, y, worldLevel, player, tileMap);
    }//end constructor

    @Override
    protected void setUpAnimations() {
        AnimationExtended animTemplate = new AnimationExtended();
        String path = "images/Enemy/1/";

        //IDLE
        this.idleRight = animTemplate.createAnimation(path + "Idle.png", 4, 1, 150, 0, 4, false, true);
        this.idleLeft = animTemplate.createAnimation(path + "Idle.png", 4, 1, 150, 0, 4, true, true);

        //WALK
        this.walkRight = animTemplate.createAnimation(path + "Walk.png", 6, 1, 120, 0, 6, false, true);
        this.walkLeft = animTemplate.createAnimation(path + "Walk.png", 6, 1, 120, 0, 6, true, true);

        //DYING
        this.dyingRight = animTemplate.createAnimation(path + "Death.png", 8, 1, 50, 0, 8, false, false);
        this.dyingLeft = animTemplate.createAnimation(path + "Death.png", 8, 1, 50, 0, 8, true, false);

        //ATTACK
        this.attackRight = animTemplate.createAnimation(path + "Attack.png", 6, 1, 100, 0, 6, false, false);
        this.attackLeft  = animTemplate.createAnimation(path + "Attack.png", 6, 1, 100, 0, 6, true, false);

        //Start with idle
        setAnimation(this.idleLeft);
        playAnimation();
    }//end setupAnimations

}//end class
