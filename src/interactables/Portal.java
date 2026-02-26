package interactables;

import entity.player.Player;
import gameExtended2D.AnimationExtended;
import gameExtended2D.SpriteExtended;
import settings.Settings;
import states.GameStateManager;
import states.GameStateType;

import java.awt.*;

/**
 * Represents a portal in the game world that can be interacted with by the player.
 * <p>
 * The portal is animated and can trigger state transitions (e.g., loading a level).
 * Implements the {@link Interactable} interface and extends {@link SpriteExtended} for animation support.
 */

public class Portal extends SpriteExtended implements Interactable {

    private AnimationExtended animation;

    /**
     * Constructs a new Portal at the given position.
     * Initializes its animation and sets the position in the world.
     *
     * @param x the x-coordinate of the portal
     * @param y the y-coordinate of the portal
     */
    public Portal(float x, float y) {
        this.setPosition(x, y);
        setUpAnimations();
    }//end constructor

    /**
     * Sets up the portal's looping animation using a sprite sheet.
     * Assigns and starts the animation immediately.
     */
    private void setUpAnimations() {
        this.animation = new AnimationExtended().createAnimation("images/Interactables/Portal/Dimensional_Portal.png", 3, 2, 150, 0, 6, false, true);
        setAnimation(animation);
        playAnimation();
    }//end setUpAnimations

    /**
     * Triggers the portal's interaction effect by switching the game state.
     *
     * @param gameStateManager the game state manager responsible for changing states
     */
    public void trigger(GameStateManager gameStateManager){
        gameStateManager.setState(GameStateType.GAMEPLAY);
    }//end trigger

    /**
     * Updates the portal's animation logic each frame.
     *
     * @param elapsedTime the time elapsed since the last frame (in milliseconds)
     * @param player      the player interacting with or near the portal //todo remove the player
     */
    public void update(long elapsedTime, Player player) {
        super.update(elapsedTime); //update the animation
    }//end update

    @Override
    public void draw(Graphics2D g) {
        if (!isVisible()) return;
        drawTransformed(g);

        if (Settings.getDebugMode()) {
            drawBoundingBox(g);
            drawBoundingCircle(g);
        }//end if
    }//end draw

    //TODO implement this version from the interface
    @Override
    public void onPlayerCollision(Player player) {

    }
}//end class
