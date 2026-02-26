package settings;

/**
 * Represents an entity in the game world that can interact with tiles via collision,
 * specifically with platforms, ground, and vertical movement (falling/jumping).
 * <p>
 * Implementing classes must define behavior for transitioning between movement states.
 */
public interface CollidableEntity {

    /**
     * Called when the entity makes contact with the ground.
     * Should stop falling and enable grounded movement logic.
     */
    void setOnGround();

    /**
     * Called when the entity is standing on a platform.
     * Should stop falling and enable platform-specific behavior.
     */
    void setOnPlatform();

    /**
     * Called when the entity begins to fall (e.g., walks off a ledge or finishes a jump).
     */
    void setFalling();

    /**
     * Called when the entity initiates a jump.
     * Should apply upward force and switch the animation/state.
     */
    void setJumping();

}//end class
