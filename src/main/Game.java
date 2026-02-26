package main;

import entity.player.Player;
import game2D.GameCore;
import settings.KeyHandler;
import settings.Settings;
import states.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Game demonstrates how we can override the GameCore class
// to create our own 'game'. We usually need to implement at
// least 'draw' and 'update' (not including any local event handling)
// to begin the process. You should also add code to the 'init'
// method that will initialise event handlers etc.

//YOU CAN LAUNCH THE GAME EITHER WITH Game.JAVA OR GameLauncher.JAVA

/**
 * The main class responsible for initializing and running the game.
 * Provides a {@link GameStateManager} to handle various game states.
 */
public class Game extends GameCore {
    private int currentLevel;
    private Player player;

    /** Manages all game states and handles state transitions. */
    private GameStateManager gameStateManager;


    private KeyHandler keyHandler;

    /**
     * Application entry point. Creates and launches a new instance of the game.
     *
     * @param args Command-line arguments (not used here).
     */
    public static void main(String[] args) {
        new Game();
    }//end main

    /**
     * Constructs a new Game instance, initializes its content,
     * and begins the primary game loop.
     */
    public Game() {
        init();
        run(Settings.getScreenFullScreen(), Settings.getScreenWidth(), Settings.getScreenHeight());
    }//end constructor

    /**
     * Initializes the game's state manager, registers individual states,
     * performs UI setup, and centers the window on the screen.
     */
    public void init() {
        this.currentLevel = 0;
        gameStateManager = new GameStateManager();

        //TODO the settings state and the shop state if time allows it

        //Registers states with the manager
        gameStateManager.addState(GameStateType.TITLE,      new TitleState(this, gameStateManager));
        gameStateManager.addState(GameStateType.SETTINGS,   new SettingsState(this, gameStateManager));
        gameStateManager.addState(GameStateType.GAMEPLAY,   new GamePlayState(this, gameStateManager));
        gameStateManager.addState(GameStateType.GAMEOVER,   new GameOverState(this, gameStateManager));

        //Starts in the TITLE state
        gameStateManager.setState(GameStateType.TITLE);

        //Set the size and frame visibility
        this.setSize(Settings.getScreenWidth(), Settings.getScreenHeight());
        this.setVisible(true);

        //Centers the window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowWidth = Settings.getScreenWidth();
        int windowHeight = Settings.getScreenHeight();
        int x = (screenSize.width - windowWidth) / 2;
        int y = (screenSize.height - windowHeight) / 2;
        setLocation(x, y);

        //Adds a key handler for input processing
        this.keyHandler = new KeyHandler();
        this.addKeyListener(this.keyHandler);

        //Adds a mouse listener. Goes to the current state's mouse methods
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gameStateManager.mousePressed(e);
            }//end mousePressed

            @Override
            public void mouseReleased(MouseEvent e) {
                gameStateManager.mouseReleased(e);
            }//end mouseReleased

            @Override
            public void mouseClicked(MouseEvent e) {
                gameStateManager.mouseClicked(e);
            }//end mouseClicked
        });//end addMouseListener

    }//end init

    /**
     * Called once per frame to update game logic. Goes to the
     * current state's update method.
     *
     * @param elapsed The time in milliseconds since the last update call.
     */
    @Override
    public void update(long elapsed) {
        gameStateManager.update(elapsed);
    }//end update

    /**
     * Called once per frame to render graphics. Goes to the
     * current state's draw method.
     *
     * @param g The {@link Graphics2D} used for drawing.
     */
    @Override
    public void draw(Graphics2D g) {
        gameStateManager.draw(g);
    }//end draw

    /**
     * Handles key press events. Goes the event to the current game state.
     *
     * @param e The {@link KeyEvent} about the key pressed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        gameStateManager.keyPressed(e);
    }//end keyPressed

    /**
     * Handles key release events. Forwards the event to the current game state.
     *
     * @param e The {@link KeyEvent} about the key released.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        gameStateManager.keyReleased(e);
    }//end keyReleased

    //GETTERS AND SETTERS

    public KeyHandler getKeyHandler() {
        return keyHandler;
    }//end getKeyHandler

    public int getCurrentLevel() {
        return currentLevel;
    }//end getCurrentLevel

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }//end setCurrentLevel

    public Player getPlayer() {
        return player;
    }//end getPlayer

    public void setPlayer(Player player) {
        this.player = player;
    }//end setPlayer

}//end class
