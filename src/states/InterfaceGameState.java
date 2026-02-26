package states;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Defines the common methods each game state must implement in order to handle
 * entering, exiting, updating, rendering, and processing both keyboard and mouse events.
 * <p></p><p>
 * Each class can represent a different state in the game (e.g.,
 * title screen, gameplay, pause, game over, etc.)</p>
 */
public interface InterfaceGameState {

    /**
     * Called once when this state becomes the active state.
     * <p>
     * This is typically used to initialize or reset the state's resources,
     * display any UI components, and perform other setup tasks.
     * </p>
     */
    void enter();

    /**
     * Called once when this state is about to lose focus or transition out.
     * <p>
     * This is typically used to clean up or hide any UI components, stop
     * background music, or perform other tasks.
     * </p>
     */
    void exit();

    /**
     * Updates the logic for this state.
     *
     * @param elapsed The time in milliseconds since the last update call.
     */
    void update(long elapsed);

    /**
     * Renders all visuals for this state.
     *
     * @param g The {@link Graphics2D} used for drawing operations.
     */
    void draw(Graphics2D g);

    /**
     * Called whenever a key is pressed.
     *
     * @param e The {@link KeyEvent} about which key was pressed.
     */
    void keyPressed(KeyEvent e);

    /**
     * Called whenever a key is released.
     *
     * @param e The {@link KeyEvent} about which key was released.
     */
    void keyReleased(KeyEvent e);

    /**
     * Called whenever a mouse button is pressed.
     *
     * @param e The {@link MouseEvent} about the mouse event.
     */
    void mousePressed(MouseEvent e);

    /**
     * Called whenever a mouse button is released.
     *
     * @param e The {@link MouseEvent} about the mouse event.
     */
    void mouseReleased(MouseEvent e);

    /**
     * Called whenever a mouse button is clicked (pressed and released).
     *
     * @param e The {@link MouseEvent} about the mouse event.
     */
    void mouseClicked(MouseEvent e);

}//end interface
