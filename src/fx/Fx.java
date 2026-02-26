package fx;

import entity.enemy.Enemy;
import entity.player.Player;
import fx.effects.PhysicalAttackFx;
import gameExtended2D.AnimationExtended;
import gameExtended2D.SpriteExtended;
import settings.Collision;
import states.GamePlayState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a visual and damaging effect (FX) in the game.
 * This class handles animation playback, collision detection with enemies,
 * damage application, and automatic expiration after a duration.
 * <p>
 * Intended to be extended by specific effect implementations like {@link PhysicalAttackFx}.
 */
public class Fx extends SpriteExtended {
    //TODO change from player to an entity class. Will require making an entity class from where the player and enemy extends from
    // this was the enemy can use the same fx class
    protected Player owner;
    protected String spriteSheetPath;
    AnimationExtended currentAnimation;
    protected AnimationExtended animationLeft;
    protected AnimationExtended animationRight;

    protected boolean singleTarget = false; //if it can attack one or more enemies
    private int duration;
    private int timer; //TODO remove later?
    private boolean active;
    private boolean hasHit = false;
    protected float physicalDamage = 0f;
    protected float magicDamage = 0f;
    private Set<Enemy> hitEnemies = new HashSet<>();

    /**
     * Constructs a new Fx instance with a specified sprite sheet, position, duration,
     * owning player, and damage values.
     *
     * @param spriteSheetPath the path to the effect's sprite sheet
     * @param x               the x-coordinate to spawn the effect
     * @param y               the y-coordinate to spawn the effect
     * @param duration        the duration of the effect in milliseconds
     * @param owner           the player that triggered this effect
     * @param physicalDamage  the amount of physical damage this effect will deal
     * @param magicDamage     the amount of magical damage this effect will deal
     */
    public Fx(String spriteSheetPath, float x, float y, int duration, Player owner, float physicalDamage, float magicDamage) {
        this.spriteSheetPath = spriteSheetPath;
        setPosition(x,y);
        this.duration = duration;
        this.timer = 0;
        this.active = true;
        this.owner = owner;
        this.physicalDamage = physicalDamage;
        this.magicDamage = magicDamage;
    }//end constructor

    /**
     * Creates the left-facing animation for this effect.
     *
     * @param col        number of columns in the sprite sheet
     * @param row        number of rows in the sprite sheet
     * @param framStart  index of the starting frame
     * @param noOfFrames total number of frames in the animation
     * @param flipped    whether the image should be flipped horizontally
     * @param loop       whether the animation should loop
     */
    protected void setAnimationLeft(int col, int row, int framStart, int noOfFrames, boolean flipped, boolean loop){
        this.animationLeft = new AnimationExtended().createAnimation(this.spriteSheetPath, col, row, duration, framStart, noOfFrames, flipped, loop);
    }//end setAnimationLeft

    /**
     * Creates the right-facing animation for this effect.
     *
     * @param col        number of columns in the sprite sheet
     * @param row        number of rows in the sprite sheet
     * @param framStart  index of the starting frame
     * @param noOfFrames total number of frames in the animation
     * @param flipped    whether the image should be flipped horizontally
     * @param loop       whether the animation should loop
     */
    protected void setAnimationRight(int col, int row, int framStart, int noOfFrames, boolean flipped, boolean loop){
        this.animationRight = new AnimationExtended().createAnimation(this.spriteSheetPath, col, row, duration, framStart, noOfFrames, flipped, loop);
    }//end setAnimationRight

    /**
     * Sets and starts playing the specified animation.
     * If the animation is non-looping, it will start from the beginning.
     *
     * @param animation the animation to set and play
     */
    protected void setAndPlayAnimation(AnimationExtended animation){
        this.currentAnimation = animation;
        super.setAnimation(animation);
        //if it doesn't play if it's not looped but also doesn't play if you just start it for all
        if(!animation.getLoop()){
            super.getAnimation().start();
        }//end if
        super.playAnimation();
    }//end changeAnimation method

    /**
     * Checks whether the animation has finished playing and deactivates the effect if it has.
     */
    private void fxFinished(){
        if(this.currentAnimation.hasLooped()){
            this.active = false;
        }//end if
    }//end fxFinished

    /**
     * Checks for collisions with multiple enemies in the scene.
     * Applies damage to each enemy once and tracks them to avoid repeat hits.
     *
     * @param entities the list of entities to check against (expected to include enemies)
     */
    protected void checkCollisionWithEnemies(ArrayList<SpriteExtended> entities) {
        for (SpriteExtended entity : entities) {
            if (!(entity instanceof Enemy enemy)) continue;

            if (!hitEnemies.contains(enemy) && Collision.preciseCollisionSpriteToSprite(this, enemy)) {
                enemy.takeDamage(physicalDamage, magicDamage);
                System.out.println("Damage given");
                hitEnemies.add(enemy);
            }//end if
        }//end for loop
    }//end checkCollisionWithEnemies

    /**
     * Checks for a collision with a single enemy.
     * Once a collision is detected, applies damage, marks the effect as hit, and hides it.
     *
     * @param entities the list of entities to check against (expected to include enemies)
     */
    protected void checkCollisionWithEnemy(ArrayList<SpriteExtended> entities) {
        if (hasHit) return;

        for (SpriteExtended entity : entities) {
            if (!(entity instanceof Enemy enemy)) continue;

            if (Collision.preciseCollisionSpriteToSprite(this, entity)) {
                enemy.takeDamage(physicalDamage, magicDamage);
                hasHit = true; //Prevent future collisions
                this.hide();
                break;
            }//end if
        }//end for loop
    }//end checkCollisionWithEnemy

    /**
     * Updates the effect's animation and checks for collisions with enemies,
     * depending on whether it's a single-target or multi-target effect.
     *
     * @param elapsedTime the time elapsed since the last frame update
     */
    public void update(long elapsedTime) {
        super.update(elapsedTime);
        fxFinished();
        if (!isActive()) return;
        if(this.singleTarget){
            checkCollisionWithEnemy(GamePlayState.getEntities());
        }//end if
        else {
            checkCollisionWithEnemies(GamePlayState.getEntities());
        }//end else
    }//end update

    /**
     * Returns whether the effect is still active (i.e., not expired or finished).
     *
     * @return true if the effect is still active, false otherwise
     */
    public boolean isActive() {
        return active;
    }//end isActive

}//end class
