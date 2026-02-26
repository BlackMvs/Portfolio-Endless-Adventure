package states;

import main.Game;
import sound.MidiMusicPlayer;
import ui.ConfigUI;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Represents the Game Over state displayed after the player has lost.
 */
public class GameOverState implements InterfaceGameState {

    private final Game GAME;
    private final GameStateManager GAME_STATE_MANAGER;
    private MidiMusicPlayer midiMusicPlayer;

    private static final long INPUT_DELAY_MS = 2000; // 2 seconds delay
    private long timeSinceEnter = 0;
    private boolean inputAllowed = false;

    /**
     * Constructs a new {@link GameOverState}.
     *
     * @param game             The {@link Game} instance managing the application.
     * @param gameStateManager The {@link GameStateManager} responsible for
     *                         handling state transitions.
     */
    public GameOverState(Game game, GameStateManager gameStateManager) {
        this.GAME = game;
        this.GAME_STATE_MANAGER = gameStateManager;
    }//end constructor

    @Override
    public void enter() {
        System.out.println("Entering GameOverState");
        //reset timer on enter
        timeSinceEnter = 0;
        inputAllowed = false;
        //music
        this.midiMusicPlayer = new MidiMusicPlayer();
        this.midiMusicPlayer.playMidi("sounds/music/gameoverMusic.mid", true);
    }//end enter

    @Override
    public void exit() {
        System.out.println("Exiting GameOverState");
        this.GAME.setCurrentLevel(1); //reset the level to 1, not 0 since there's no point in always doing the tutorial (level 0 is tutorial)
        this.midiMusicPlayer.close();
    }//end exit

    @Override
    public void update(long elapsed) {
        if (!inputAllowed) {
            timeSinceEnter += elapsed;
            if (timeSinceEnter >= INPUT_DELAY_MS) {
                inputAllowed = true;
            }//end if
        }//end if
    }//end update

    @Override
    public void draw(Graphics2D g) {
        //Dimmed Background Overlay
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GAME.getWidth(), GAME.getHeight());

        Composite original = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, GAME.getWidth(), GAME.getHeight());
        g.setComposite(original);

        //Panel Dimensions
        int panelWidth = GAME.getWidth() / 2;
        int panelHeight = GAME.getHeight() / 4;
        int panelX = (GAME.getWidth() - panelWidth) / 2;
        int panelY = (GAME.getHeight() - panelHeight) / 2;

        //Panel Background
        g.setColor(new Color(30, 30, 30, 200));
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        //GAME OVER Title
        String title = "GAME OVER";
        g.setFont(ConfigUI.getTitleBoldScaledFont());
        FontMetrics titleFM = g.getFontMetrics();
        int titleX = (GAME.getWidth() - titleFM.stringWidth(title)) / 2;
        int titleY = panelY + titleFM.getAscent() + 40;
        ConfigUI.drawShadowedText(g, title, titleX, titleY, Color.RED, Color.BLACK);

        //Instruction Text
        if(inputAllowed){
            String instruction = "Press any key to return to title";
            g.setFont(ConfigUI.getNormalPlainScaledFont());
            FontMetrics instFM = g.getFontMetrics();
            int instX = (GAME.getWidth() - instFM.stringWidth(instruction)) / 2;
            int instY = titleY + instFM.getHeight() + 20;
            ConfigUI.drawShadowedText(g, instruction, instX, instY, ConfigUI.TEXT_COLOR_WHITE, ConfigUI.TEXT_COLOR_BLACK);
        }//end if
    }//end draw


    @Override
    public void keyPressed(KeyEvent e) {
        if (inputAllowed){
            GAME_STATE_MANAGER.setState(GameStateType.TITLE);
        }
    }//end keyPressed

    @Override
    public void keyReleased(KeyEvent e) {

    }//end keyReleased

    @Override
    public void mousePressed(MouseEvent e) {
        if(inputAllowed){
            GAME_STATE_MANAGER.setState(GameStateType.TITLE);
        }
    }//end mousePressed

    @Override
    public void mouseReleased(MouseEvent e) {

    }//end mouseReleased

    @Override
    public void mouseClicked(MouseEvent e) {

    }//end mouseClicked

}//end class
