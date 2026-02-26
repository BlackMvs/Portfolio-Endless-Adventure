package ui;

import java.awt.*;

/**
 * Represents a basic menu button with text, bounding box, and hover states.
 */
public class MenuButton {
    @SuppressWarnings("FieldMayBeFinal")
    private String text;
    @SuppressWarnings("FieldMayBeFinal")
    private Rectangle bounds;
    @SuppressWarnings("FieldMayBeFinal")
    private Runnable onClick;

    /**
     * Create a new {@link MenuButton} with specified text, position, size, and click action.
     *
     * @param text    the label to display on the button
     * @param x       the x-coordinate of the top-left corner of the button
     * @param y       the y-coordinate of the top-left corner of the button
     * @param width   the width of the button
     * @param height  the height of the button
     * @param onClick the action to perform when the button is clicked
     */
    public MenuButton(String text, int x, int y, int width, int height, Runnable onClick) {
        this.text = text;
        this.bounds = new Rectangle(x, y, width, height);
        this.onClick = onClick;
    }//end constructor

    /**
     * Draws the button on the screen using the provided {@link Graphics2D} context.
     * This includes background, border, and centered text.
     *
     * @param g the graphics to use for rendering
     */
    public void draw(Graphics2D g) {
        //Background
        g.setColor(new Color(50, 50, 50, 200));
        g.fill(bounds);

        //Border
        g.setColor(ConfigUI.BAR_BORDER_COLOR);
        g.draw(bounds);

        //Text
        g.setFont(ConfigUI.getNormalBoldScaledFont());
        g.setColor(ConfigUI.TEXT_COLOR_WHITE);

        int textWidth = g.getFontMetrics().stringWidth(text);
        int textHeight = g.getFontMetrics().getAscent();
        int textX = bounds.x + (bounds.width - textWidth) / 2;
        int textY = bounds.y + (bounds.height + textHeight) / 2 - 4;

        g.drawString(text, textX, textY);
    }//end draw

    /**
     * Checks whether the given point is within the bounds of the button.
     *
     * @param p the point to test for hover
     * @return true if the point lies within the button's area, false otherwise
     */
    public boolean isHovered(Point p) {
        return bounds.contains(p);
    }//end isHovered

    /**
     * Gets the bounding rectangle of the button.
     *
     * @return the bounding {@link Rectangle}
     */
    public Rectangle getBounds() {
        return bounds;
    }//end getBounds

    /**
     * Gets the action associated with this button.
     *
     * @return the {@link Runnable} action to execute on click
     */
    public Runnable getOnClick() {
        return onClick;
    }//end getOnClick

    /**
     * Sets the text displayed on the button.
     *
     * @param text the new button text
     */
    public void setText(String text) {
        this.text = text;
    }//end setText

}//end class