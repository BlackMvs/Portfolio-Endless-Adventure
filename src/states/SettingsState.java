package states;

import main.Game;
import settings.Settings;
import ui.ConfigUI;
import ui.MenuButton;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * A settings menu to adjust options that can be changed during gameplay.
 * Includes toggles for debug mode, gravity, and fast loading screens.
 */
public class SettingsState implements InterfaceGameState {

    private final Game GAME;
    private final GameStateManager GAME_STATE_MANAGER;

    private ArrayList<MenuButton> buttons;
    private MenuButton backButton;
    private MenuButton gravityDecreaseButton;
    private MenuButton gravityIncreaseButton;

    private boolean debugEnabled;
    private boolean longLoadingEnabled;
    private float gravityValue;
    private boolean playerInvincibleEnabled;

    /**
     * Constructs the settings menu state.
     *
     * @param game the game instance
     * @param gsm  the game state manager
     */
    public SettingsState(Game game, GameStateManager gsm) {
        this.GAME = game;
        this.GAME_STATE_MANAGER = gsm;
    }//end constructor

    @Override
    public void enter() {
        this.buttons = new ArrayList<>();
        this.debugEnabled = Settings.getDebugMode();
        this.longLoadingEnabled = Settings.getLongLoadingScreen();
        this.gravityValue = Settings.getGravity();
        this.playerInvincibleEnabled = Settings.getIsPlayerInvincible();
        setupButtons();
        updateButtonLabels();
    }//end enter

    /**
     * Creates and positions the settings menu buttons.
     * Handles toggles and dynamic button content updates.
     */
    private void setupButtons() {
        buttons.clear();

        int buttonWidth = ConfigUI.getBarWidth();
        int buttonHeight = ConfigUI.getBarHeight() * 2;
        int spacing = buttonHeight / 2;

        int startY = Settings.getScreenHeight() / 3;
        int centerX = (Settings.getScreenWidth() - buttonWidth) / 2;

        buttons.add(new MenuButton("Debug Mode: " + (debugEnabled ? "ON" : "OFF"), centerX, startY, buttonWidth, buttonHeight, () -> {
            debugEnabled = !debugEnabled;
            Settings.setDebugMode(debugEnabled);
            updateButtonLabels();
        }));

        buttons.add(new MenuButton("Fast Loading: " + (!longLoadingEnabled ? "ON" : "OFF"), centerX, startY + (buttonHeight + spacing), buttonWidth, buttonHeight, () -> {
            longLoadingEnabled = !longLoadingEnabled;
            Settings.setLongLoadingScreen(longLoadingEnabled);
            updateButtonLabels();
        }));

        // Gravity display button
        buttons.add(new MenuButton("Gravity: " + gravityValue, centerX, startY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight, () -> {}));

        // Gravity adjustment buttons
        int sideButtonWidth = buttonHeight;
        int sideButtonHeight = buttonHeight;
        int gravityY = startY + 2 * (buttonHeight + spacing);

        float step = 0.05f;
        float min = 0.05f;
        float max = Settings.getGravityMax(); // Uses your global gravity limit setting

        gravityDecreaseButton = new MenuButton("-", centerX - sideButtonWidth - 10, gravityY, sideButtonWidth, sideButtonHeight, () -> {
            gravityValue = Math.max(min, gravityValue - step);
            Settings.setGravity(gravityValue);
            updateButtonLabels();
        });

        gravityIncreaseButton = new MenuButton("+", centerX + buttonWidth + 10, gravityY, sideButtonWidth, sideButtonHeight, () -> {
            gravityValue = Math.min(max, gravityValue + step);
            Settings.setGravity(gravityValue);
            updateButtonLabels();
        });

        this.buttons.add(new MenuButton("Invincible: " + this.playerInvincibleEnabled, centerX, startY + 3 * (buttonHeight + spacing), buttonWidth, buttonHeight, () -> {
            this.playerInvincibleEnabled = !playerInvincibleEnabled;
            Settings.setIsPlayerInvincible(this.playerInvincibleEnabled);
            updateButtonLabels();
        }));

        backButton = new MenuButton("Back", centerX, startY + 4 * (buttonHeight + spacing), buttonWidth, buttonHeight, () -> {
            GAME_STATE_MANAGER.setState(GameStateType.TITLE);
        });
    }//end setupButtons

    /**
     * Updates the text displayed on the setting buttons to reflect the current state of each setting.
     */
    private void updateButtonLabels() {
        buttons.get(0).setText("Debug Mode: " + (debugEnabled ? "ON" : "OFF"));
        buttons.get(1).setText("Fast Loading: " + (!longLoadingEnabled ? "ON" : "OFF"));
        buttons.get(2).setText(String.format("Gravity: %.2f", gravityValue));
        buttons.get(3).setText("Invincible: " + this.playerInvincibleEnabled);
    }//end updateButtonLabels

    @Override
    public void exit() {
        //nothing
    }//end exit

    @Override
    public void update(long elapsed) {
        //none
    }//end update

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Settings.getScreenWidth(), Settings.getScreenHeight());

        g.setColor(Color.WHITE);
        g.setFont(ConfigUI.getTitleBoldScaledFont());
        String title = "Settings";
        g.drawString(title, ConfigUI.middleStringX(g, title), Settings.getScreenHeight() / 6);

        for (MenuButton button : buttons) {
            button.draw(g);
        }//end for loop

        gravityDecreaseButton.draw(g);
        gravityIncreaseButton.draw(g);

        backButton.draw(g);
    }//end draw

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            GAME_STATE_MANAGER.setState(GameStateType.TITLE);
        }//end if
    }//end keyPressed

    @Override
    public void keyReleased(KeyEvent e) {
        //nada
    }//end keyReleased

    @Override
    public void mousePressed(MouseEvent e) {
        Point click = e.getPoint();
        for (MenuButton button : buttons) {
            if (button.isHovered(click)) {
                button.getOnClick().run();
                return;
            }//end if
        }//end for loop
        if (gravityDecreaseButton.isHovered(click)) {
            gravityDecreaseButton.getOnClick().run();
        }//end if
        if (gravityIncreaseButton.isHovered(click)) {
            gravityIncreaseButton.getOnClick().run();
        }//end if
        if (backButton.isHovered(click)) {
            backButton.getOnClick().run();
        }//end if
    }//end mousePressed

    @Override
    public void mouseReleased(MouseEvent e) {
        //nothing
    }//end mouseReleased

    @Override
    public void mouseClicked(MouseEvent e) {
        //nada
    }//end mouseClicked

}//end class