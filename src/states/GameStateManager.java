package states;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the different states of the game, allowing to add, remove,
 * and switch between them. It uses update, render, and input calls
 * to whichever state is currently active.
 */
public class GameStateManager {

    /** Stores each state by its unique enum key. */
    private final Map<GameStateType, InterfaceGameState> states = new HashMap<>();

    /** The state that is currently active. */
    private InterfaceGameState currentState;

    /**
     * Adds a new state to the manager with the specified key.
     *
     * @param type  The enum key identifying this state.
     * @param state The instance of the state to be managed.
     */
    public void addState(GameStateType type, InterfaceGameState state) {
        states.put(type, state);
    }//end addState

    /**
     * Switches the current state to the state associated with
     * the given enum key.
     *
     * @param type The enum key of the state to activate.
     */
    public void setState(GameStateType type) {
        //Exit the old state if present
        if (currentState != null) {
            currentState.exit();
        }//end if

        //Activate the new state
        currentState = states.get(type);
        if (currentState != null) {
            currentState.enter();
        }//end if
    }//end setState

    /**
     * Retrieves the state that is currently active.
     *
     * @return The currently active state, or {@code null} if none is set.
     */
    public InterfaceGameState getCurrentState() {
        return currentState;
    }//end getCurrentState

    /**
     * Updates the active state's logic.
     *
     * @param elapsed The time in milliseconds since the last update.
     */
    public void update(long elapsed) {
        if (currentState != null) {
            currentState.update(elapsed);
        }//end if
    }//end update

    /**
     * Draws the active state's visuals.
     *
     * @param g The {@link Graphics2D} used for drawing.
     */
    public void draw(Graphics2D g) {
        if (currentState != null) {
            currentState.draw(g);
        }//end if
    }//end draw

    /**
     * Handles the event of a key being pressed.
     *
     * @param e The {@link KeyEvent} for the key pressed.
     */
    public void keyPressed(KeyEvent e) {
        if (currentState != null) {
            currentState.keyPressed(e);
        }//end if
    }//end keyPressed

    /**
     * Handles the event of a key being released.
     *
     * @param e The {@link KeyEvent} for the key released.
     */
    public void keyReleased(KeyEvent e) {
        if (currentState != null) {
            currentState.keyReleased(e);
        }//end if
    }//end keyReleased

    /**
     * Handles a mouse button being pressed.
     *
     * @param e The {@link MouseEvent} for the mouse pressed.
     */
    public void mousePressed(MouseEvent e) {
        if (currentState != null) {
            currentState.mousePressed(e);
        }//end if
    }//end mousePressed

    /**
     * Handles a mouse button being released.
     *
     * @param e The {@link MouseEvent} for the mouse released.
     */
    public void mouseReleased(MouseEvent e) {
        if (currentState != null) {
            currentState.mouseReleased(e);
        }//end if
    }//end mouseReleased

    /**
     * Handles a mouse button being clicked (pressed and released).
     *
     * @param e The {@link MouseEvent} for the mouse click.
     */
    public void mouseClicked(MouseEvent e) {
        if (currentState != null) {
            currentState.mouseClicked(e);
        }//end if
    }//end mouseClicked

}//end class
