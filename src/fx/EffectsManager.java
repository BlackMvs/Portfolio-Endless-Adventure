package fx;

import settings.Settings;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Manages all active visual effects ({@link Fx}) in the game,
 * including adding, updating, and rendering them.
 * Also provides debug rendering for bounding visuals when debug mode is enabled.
 */
public class EffectsManager {
    private List<Fx> effects;

    /**
     * Constructs a new EffectsManager and initializes the internal list of effects.
     */
    public EffectsManager() {
        //create an array that will store all the effects
        effects = new ArrayList<>();
    }//end constructor

    /**
     * Adds a new {@link Fx} visual effect to the manager to be updated and rendered.
     *
     * @param fx the effect to add
     */
    public void addEffect(Fx fx) {
        effects.add(fx);
    }//end addEffect

    /**
     * Updates all active effects managed by this instance.
     * Inactive effects are removed automatically.
     *
     * @param elapsedTime the time elapsed since the last update in milliseconds
     */
    public void updateEffects(long elapsedTime) {
        //Create an iterator to loop through the list of effects
        //Using an iterator as it will throw a ConcurrentModificationException since we are modifying the list during iteration
        Iterator<Fx> iterator = effects.iterator();

        while (iterator.hasNext()) { //Loop through each effect
            Fx effect = iterator.next(); //Get the next effect

            effect.update(elapsedTime); //Call the update method of the effect

            if (!effect.isActive()) { //Check if the effect is no longer active
                iterator.remove(); //Remove the effect from the list safely
            }//end if
        }//end while
    }//end updateEffects

    /**
     * Draws all active effects on the screen.
     * If debug mode is enabled, also draws bounding boxes and circles for each effect.
     *
     * @param g the graphics context to draw on
     */
    public void draw(Graphics2D g){
        for (Fx effect: this.effects){
            effect.draw(g);
            if (Settings.getDebugMode()){
                effect.drawBoundingBox(g);
                effect.drawBoundingCircle(g);
            }//end if
        }//end for loop
    }//end draw

    /**
     * Draws all active effects on the screen using a positional offset.
     * This is useful when the camera or viewport is shifted.
     * If debug mode is enabled, also draws bounding boxes and circles for each effect.
     *
     * @param g the graphics context to draw on
     * @param x the horizontal offset to apply to all effects
     * @param y the vertical offset to apply to all effects
     */
    public void drawWithOffSets(Graphics2D g, int x, int y){
        for (Fx effect: this.effects){
            effect.setOffsets(x,y);
            effect.draw(g);
            if (Settings.getDebugMode()){
                effect.drawBoundingBox(g);
                effect.drawBoundingCircle(g);
            }//end if
        }//end for loop
    }//end draw

}//end class
