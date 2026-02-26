package gameExtended2D;

import game2D.Animation;
import game2D.Sprite;

/**
 * An extended version of the base {@link Sprite} class that integrates {@link AnimationExtended}.
 * <p>
 * Provides utilities for rendering with positional offsets and accessing draw coordinates.
 * This class is intended to be the base for all animated entities in the game (e.g., player, enemies, FX).
 */
public class SpriteExtended extends Sprite {
    //TODO add a method that will set the sprite location based on the tile numbers instead of the pixels
    /**
     * The extended animation object used by this sprite for advanced features
     * like flipping, scaling, and streamlined animation creation.
     */
    @SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
    private AnimationExtended animationExtended;

    /**
     * Constructs a new SpriteExtended with an empty {@link AnimationExtended}
     * and assigns it as the default animation.
     */
    public SpriteExtended() {
        super(new Animation());
        this.animationExtended = new AnimationExtended();
        super.setAnimation(this.animationExtended);
    }//end constructor

    public float getDrawX() {
        return getX() + getOffsetX();
    }//end getDrawX

    public float getDrawY() {
        return getY() + getOffsetY();
    }//end getDrawY

    /**
     * Returns the horizontal offset applied to the sprite's position.
     *
     * @return the x-axis offset
     */
    public int getOffsetX() {
        return xoff;
    }//end getOffsetX

    /**
     * Returns the vertical offset applied to the sprite's position.
     *
     * @return the y-axis offset
     */
    public int getOffsetY() {
        return yoff;
    }//end getOffsetY

}//end class