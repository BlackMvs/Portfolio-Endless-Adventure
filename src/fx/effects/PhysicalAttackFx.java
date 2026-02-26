package fx.effects;

import entity.player.Player;
import fx.Fx;

/**
 * Represents the visual effect triggered when the player performs a physical attack.
 * This effect is directional, based on the player's facing side,
 * and handles animation setup and damage application through the base {@link Fx} class.
 */
public class PhysicalAttackFx extends Fx {

    //TODO in the fx class, change the set and play animation to take into consideration the direction of the player
    // and to automatically load the right animation in. Set the col, row, frame etc, into the constructor of the fx class
    // so when you call super() you put the numbers there, and it reduces so much work and stuff to remember for it
    /**
     * Constructs a new PhysicalAttackFx instance.
     * Loads the appropriate directional animation based on the player's facing direction,
     * and sets it to play immediately.
     *
     * @param x              the x-position to spawn the effect
     * @param y              the y-position to spawn the effect
     * @param duration       how long the effect lasts (in milliseconds)
     * @param direction      the direction the player is facing (left or right)
     * @param player         the player who triggered the effect
     * @param physicalDamage the amount of physical damage this effect represents
     * @param magicDamage    the amount of magical damage this effect represents
     */
    public PhysicalAttackFx(float x, float y, int duration, Player.Directions direction, Player player, float physicalDamage, float magicDamage) {
        super("images/FX/PhysicalAttackFx.png", x, y, duration, player, physicalDamage, magicDamage);

        if (direction == Player.Directions.LEFT){
            setAnimationLeft(7,1,0,7,true,false);
            super.setAndPlayAnimation(super.animationLeft);
        }//end if
        else {
            setAnimationRight(7,1,0,7,false,false);
            super.setAndPlayAnimation(super.animationRight);
        }//end else
    }//end constructor

}//end class
