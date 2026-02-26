package states;

/**
 * Represents the different states the game can be in.
 * Used by the {@link GameStateManager} to determine which game logic and rendering to run.
 *
 */
public enum GameStateType {
    TITLE,
    SETTINGS,
    GAMEPLAY,
    GAMEOVER
}//end enum
