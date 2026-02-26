package states;

import entity.player.Player;
import main.Game;
import settings.KeyHandler;
import settings.Settings;
import sound.MidiMusicPlayer;
import ui.ConfigUI;
import ui.MenuButton;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the title screen or main menu of the game.
 * Displays a welcome message and handles input
 */
public class TitleState implements InterfaceGameState {

    private final Game game;
    private final GameStateManager gameStateManager;

    private List<MenuButton> buttons;
    private BufferedImage backgroundImage;
    private int backgroundX;
    private MidiMusicPlayer midiMusicPlayer;

    /**
     * Creates a new {@code TitleState} for the main menu of the game
     *
     * @param game The {@link Game} instance managing the overall application.
     * @param gameStateManager  The {@link GameStateManager} handles state transitions.
     */
    public TitleState(Game game, GameStateManager gameStateManager) {
        this.game = game;
        this.gameStateManager = gameStateManager;
    }//end constructor

    /**
     * Loads the background image for the title screen.
     * Falls back to a generated gradient background if loading fails.
     */
    private void loadAssets() {
        try {
            backgroundImage = ImageIO.read(new File("images/UI/MainMenu/background_glacial_mountains.png"));
        }//end try
        catch (IOException e) {
            System.err.println("Failed to load background image: " + e.getMessage());

            //Fallback background in case of error
            backgroundImage = new BufferedImage(Settings.getScreenWidth(), Settings.getScreenHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = backgroundImage.createGraphics();
            g2.setPaint(new GradientPaint(0, 0, Color.DARK_GRAY, Settings.getScreenWidth(), Settings.getScreenHeight(), Color.BLACK));
            g2.fillRect(0, 0, Settings.getScreenWidth(), Settings.getScreenHeight());
            g2.dispose();
        }//end catch
    }//end loadAssets

    /**
     * Sets up the UI buttons for the title screen: Start, Settings, and Quit.
     * <p>
     * Each button includes its own behavior via lambda functions.
     */
    private void setupButtons() {
        int buttonWidth = Settings.getScreenWidth() / 4;
        int buttonHeight = Settings.getScreenHeight() / 15;
        int spacing = Settings.getScreenHeight() / 30;

        int totalHeight = 3 * buttonHeight + 2 * spacing;
        int startY = (Settings.getScreenHeight() - totalHeight) / 2;
        int centerX = (Settings.getScreenWidth() - buttonWidth) / 2;

        buttons.add(new MenuButton("Start Game", centerX, startY, buttonWidth, buttonHeight, () -> {
            gameStateManager.setState(GameStateType.GAMEPLAY);
        }));//end add

        buttons.add(new MenuButton("Settings", centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight, () -> {
            gameStateManager.setState(GameStateType.SETTINGS); // Make sure this state exists
        }));//end add

        buttons.add(new MenuButton("Quit", centerX, startY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight, () -> {
            System.exit(0);
        }));//end add
    }//end setupButtons

    @Override
    public void enter() {
        System.out.println("Entering TitleState");

        //music
        this.midiMusicPlayer = new MidiMusicPlayer();
        midiMusicPlayer.playMidi("sounds/music/titleMusic.mid", true);

        //screen
        this.buttons = new ArrayList<>();
        loadAssets();
        setupButtons();
    }//end enter

    @Override
    public void exit() {
        System.out.println("Exiting TitleState");
        this.game.setPlayer(new Player(new KeyHandler()));
        this.midiMusicPlayer.close();
    }//end exit

    @Override
    public void update(long elapsed) {
        backgroundX -= 1;
        if (backgroundX <= -Settings.getScreenWidth()) {
            backgroundX = 0;
        }//end if
    }//end update

    @Override
    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        //Draw animated background (looping)
        g.drawImage(backgroundImage, backgroundX, 0, Settings.getScreenWidth(), Settings.getScreenHeight(), null);
        g.drawImage(backgroundImage, backgroundX + Settings.getScreenWidth(), 0, Settings.getScreenWidth(), Settings.getScreenHeight(), null);

        //Panel Behind Title + Buttons
        int panelWidth = Settings.getScreenWidth() / 2;
        int panelHeight = Settings.getScreenHeight() / 2;
        int panelX = (Settings.getScreenWidth() - panelWidth) / 2;
        int panelY = (Settings.getScreenHeight() - panelHeight) / 2;

        g.setColor(new Color(30, 30, 30, 100));
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        //Title
        String title = "Endless Adventure";
        g.setFont(ConfigUI.getTitleBoldScaledFont());
        FontMetrics titleFM = g.getFontMetrics();
        int titleX = Settings.getScreenWidth() / 2 - titleFM.stringWidth(title) / 2;
        int titleY = panelY + titleFM.getAscent() + 30;
        ConfigUI.drawShadowedText(g, title, titleX, titleY, ConfigUI.TEXT_COLOR_WHITE, ConfigUI.TEXT_COLOR_BLACK);

        //Reposition Buttons Dynamically
        int buttonWidth = Settings.getScreenWidth() / 4;
        int buttonHeight = Settings.getScreenHeight() / 15;
        int spacing = Settings.getScreenHeight() / 30;

        int startY = panelY + titleFM.getHeight() + 60;
        int centerX = (Settings.getScreenWidth() - buttonWidth) / 2;

        for (int i = 0; i < buttons.size(); i++) {
            MenuButton button = buttons.get(i);
            int buttonY = startY + i * (buttonHeight + spacing);
            button.getBounds().setBounds(centerX, buttonY, buttonWidth, buttonHeight);
            button.draw(g);
        }//end for loop
    }//end draw

    @Override
    public void keyPressed(KeyEvent e) {}//end keyPressed

    @Override
    public void keyReleased(KeyEvent e) {}//end keyReleased

    @Override
    public void mousePressed(MouseEvent e) {
        for (MenuButton button : buttons) {
            if (button.getBounds().contains(e.getPoint())) {
                button.getOnClick().run();
            }//end if
        }//end for loop
    }//end mousePressed

    @Override
    public void mouseReleased(MouseEvent e) {}//end mouseReleased

    @Override
    public void mouseClicked(MouseEvent e) {}//end mouseClicked

}//end class
