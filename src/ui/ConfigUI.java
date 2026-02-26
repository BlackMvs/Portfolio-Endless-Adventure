package ui;

import settings.Settings;

import java.awt.*;

/**
 * UIConfig is a utility class that provides dynamically scaled UI measurements
 * and styling elements such as fonts, sizes, margins, and colors, based on the
 * current screen resolution.
 */
public class ConfigUI {

    //<editor-fold desc = "SHARED UI COLORS">
    /** Default color for health bars */
    public static final Color HEALTH_BAR_COLOR = new Color(220, 70, 70);
    public static final Color MANA_BAR_COLOR = new Color(80, 130, 255);
    public static final Color BAR_BACKGROUND_COLOR = new Color(40, 40, 40);
    public static final Color BAR_BORDER_COLOR = new Color(0, 0, 0);
    public static final Color TEXT_COLOR_WHITE = new Color(240, 240, 240);
    public static final Color TEXT_COLOR_BLACK = new Color(30, 30, 30);
    public static final Color XP_BAR_COLOR = new Color(0, 255, 255);
    public static final Color GOLD_TEXT_COLOR = new Color(255, 215, 0);
    public static Color LEVEL_BADGE_COLOR = new Color(255, 215, 0);

    //</editor-fold> SHARED UI COLORS

    //<editor-fold desc= "BARS">
    /**
     * Returns a proportional width for UI bars based on the screen width.
     *
     * @return the calculated bar width
     */
    public static int getBarWidth() {
        return Settings.getScreenWidth() / 5; // 20% of screen width
    }//end getBarWidth

    /**
     * Returns a proportional height for UI bars based on the screen height.
     *
     * @return the calculated bar height
     */
    public static int getBarHeight() {
        return Settings.getScreenHeight() / 25; // About 4% of screen height
    }//end getBarHeight

    /**
     * Returns the horizontal margin to apply from the left edge of the screen.
     *
     * @return the left margin for UI elements
     */
    public static int getHorizontalMargin() {
        return Settings.getScreenWidth() / 50;
    }//end getHorizontalMargin

    /**
     * Returns the top margin for the first UI element (e.g. health bar).
     *
     * @return the vertical margin from the top
     */
    public static int getTopMargin() {
        return Settings.getScreenHeight() / 20;
    }//end getTopMargin

    /**
     * Returns the vertical position for a second bar (e.g. mana) based on the first bar's Y position.
     *
     * @param firstBarY      the Y position of the first bar
     * @param barHeight      the height of the bar
     * @param spacing        the spacing between bars
     * @return the Y position for the second bar
     */
    public static int getNextBarY(int firstBarY, int barHeight, int spacing) {
        return firstBarY + barHeight + spacing;
    }//end getNextBarY

    /**
     * @return spacing between vertically stacked bars
     */
    public static int getBarSpacing() {
        return getBarHeight() / 2;
    }//end getBarSpacing

    //use it like:
    //int arc = ConfigUI.getBarCornerRadius();
    //g.fillRoundRect(x, y, width, height, arc, arc);
    /**
     * @return the arc radius for drawing rounded corners on bars
     */
    public static int getBarCornerRadius() {
        return getBarHeight(); // Fully rounded height
    }//end getBarCornerRadius

    /**
     * Draws a stat bar (like HP or MP) with filled and background portions,
     * along with its text label.
     *
     * @param g         graphics context to draw onto
     * @param x         x-coordinate of the bar
     * @param y         y-coordinate of the bar
     * @param width     total width of the bar
     * @param height    total height of the bar
     * @param current   current stat value (e.g., current HP)
     * @param max       max stat value (e.g., max HP)
     * @param fillColor fill color of the active portion of the bar
     * @param label     label to display (e.g., "HP")
     */
    public static void drawStatBarWithText(Graphics2D g, int x, int y, int width, int height, float current, float max, Color fillColor, String label) {
        float percent = current / max;
        int currentWidth = (int)(width * percent);
        int arc = ConfigUI.getBarCornerRadius();

        //Background
        g.setColor(ConfigUI.BAR_BACKGROUND_COLOR);
        g.fillRoundRect(x, y, width, height, arc, arc);

        //Fill
        g.setColor(fillColor);
        g.fillRoundRect(x, y, currentWidth, height, arc, arc);

        //Border
        g.setColor(ConfigUI.BAR_BORDER_COLOR);
        g.drawRoundRect(x, y, width, height, arc, arc);

        //Text: HP / MP + values
        g.setFont(ConfigUI.getNormalPlainScaledFont());
        String text = label + ": " + Math.round(current) + " / " + Math.round(max);
        FontMetrics fm = g.getFontMetrics();
        int textX = x + 8;
        int textY = y + height - 6;
        ConfigUI.drawShadowedText(g, text, textX, textY, ConfigUI.TEXT_COLOR_WHITE, ConfigUI.TEXT_COLOR_BLACK);
    }//end drawStatBarWithText

    //</editor-fold> BARS

    /**
     * Draws text with a shadow effect by rendering a darker outline behind it.
     *
     * @param g           the graphics context
     * @param text        the string to draw
     * @param x           x-coordinate of the text
     * @param y           y-coordinate of the text
     * @param textColor   the color of the main text
     * @param shadowColor the color of the shadow
     */
    public static void drawShadowedText(Graphics2D g, String text, int x, int y, Color textColor, Color shadowColor) {
        g.setColor(shadowColor);
        g.drawString(text, x + 1, y + 1);
        g.setColor(textColor);
        g.drawString(text, x, y);
    }//end drawShadowedText


    //<editor-fold desc = "FONTS">
    /**
     * Returns a scaled font based on the screen height to ensure readability across resolutions.
     * Used for normal text, in plain style
     *
     * @return a Font object scaled to the screen height
     */
    public static Font getNormalPlainScaledFont() {
        int fontSize = Settings.getScreenHeight() / 40;
        return new Font("Arial", Font.PLAIN, fontSize);
    }//end getNormalScaledFont

    /**
     * Returns a scaled font based on the screen height to ensure readability across resolutions.
     * Used for normal text, in bold style
     *
     * @return a Font object scaled to the screen height
     */
    public static Font getNormalBoldScaledFont() {
        int fontSize = Settings.getScreenHeight() / 40;
        return new Font("Arial", Font.BOLD, fontSize);
    }//end getNormalBoldScaledFont

    /**
     * Returns a scaled font based on the screen height to ensure readability across resolutions.
     * Used for Title text, in bold style
     *
     * @return a Font object scaled to the screen height
     */
    public static Font getTitleBoldScaledFont() {
        int fontSize = Settings.getScreenHeight() / 20;
        return new Font("Arial", Font.BOLD, fontSize);
    }//end getTitleBoldScaledFont

    /**
     * Returns a scaled font based on the screen height to ensure readability across resolutions.
     * Used for Massive text (such a paused) , in bold style
     *
     * @return a Font object scaled to the screen height
     */
    public static Font getMassiveBoldScaledFont() {
        int fontSize = Settings.getScreenHeight() / 5;
        return new Font("Arial", Font.BOLD, fontSize);
    }//end getTitleBoldScaledFont

    //</editor-fold> FONTS

    //<editor-fold desc = "DISPLAY UTILITY">
    /**
     * Gets the middle x-axis location for drawing a String perfectly to the middle of the screen
     *
     * @param g The {@link Graphics2D} used
     * @param text The text that will be used to calcualte
     * @return The location where the String should be drawn to be displayed in the middle
     */
    public static int middleStringX(Graphics2D g, String text){
        return Settings.getScreenWidth()/2 - ((int) g.getFontMetrics().getStringBounds(text, g).getWidth()/2);
    }//end middleStringX

    /**
     * Calculates the horizontal center position to draw a string based on its width.
     *
     * @param g    the graphics context
     * @param text the text to be centered
     * @return the X coordinate to center the text
     */
    public static int middleStringY(Graphics2D g, String text) {
        return Settings.getScreenHeight()/2 - ((int) g.getFontMetrics().getStringBounds(text, g).getHeight()/2);
    }//end middleStringY
    //</editor-fold>

}//end class
