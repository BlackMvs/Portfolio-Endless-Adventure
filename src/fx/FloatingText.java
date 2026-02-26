package fx;

import settings.Settings;

import java.awt.*;
import java.util.Random;

/**
 * Represents a piece of floating text that appears briefly on screen,
 * typically used to display damage numbers, healing, or level-up messages.
 * The text gradually moves upward and fades out over its lifespan.
 */
public class FloatingText {
    private final String text;
    private float x, y;
    private float velocityY;
    private int life;
    private long startTime;
    private final Color baseColor;
    private final Font font;

    /**
     * Creates a new floating text with a custom font.
     *
     * @param text the message to display
     * @param x the x-coordinate where the text appears
     * @param y the y-coordinate where the text appears
     * @param color the color of the text
     * @param font the font used to render the text
     */
    public FloatingText(String text, float x, float y, Color color, Font font) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.baseColor = color;
        this.font = font;
        this.startTime = System.currentTimeMillis();
        Random random = new Random();
        this.velocityY = -(random.nextFloat(0.4f, 1.2f));
        this.life = random.nextInt(800, 1300);
    }//end constructor

    /**
     * Creates a new floating text using the default font.
     *
     * @param text the message to display
     * @param x the x-coordinate where the text appears
     * @param y the y-coordinate where the text appears
     * @param color the color of the text
     */
    public FloatingText(String text, float x, float y, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.baseColor = color;
        this.font = new Font("Arial", Font.BOLD, Settings.getScreenHeight() / 50);
        this.startTime = System.currentTimeMillis();
        Random random = new Random();
        this.velocityY = -(random.nextFloat(0.4f, 1.2f));
        this.life = random.nextInt(800, 1300);
    }//end constructor

    /**
     * Creates a new floating text to display a numerical value (e.g., damage or healing).
     *
     * @param number the number to display
     * @param x the x-coordinate where the text appears
     * @param y the y-coordinate where the text appears
     * @param color the color of the text
     */
    public FloatingText(float number, float x, float y, Color color) {
        this.text = String.valueOf(Math.round(number));
        this.x = x;
        this.y = y;
        this.baseColor = color;
        this.font = new Font("Arial", Font.BOLD, Settings.getScreenHeight() / 50);
        this.startTime = System.currentTimeMillis();
        Random random = new Random();
        this.velocityY = -(random.nextFloat(0.4f, 1.2f));
        this.life = random.nextInt(800, 1300);
    }//end constructor

    /**
     * Checks whether the floating text has exceeded its lifespan and should be removed.
     *
     * @return true if the text is expired; false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - startTime >= life;
    }//end isExpired

    /**
     * Updates the vertical position of the floating text.
     * Typically called every frame to simulate floating movement.
     *
     * @param elapsed the time elapsed since the last update (not currently used)
     */
    public void update(long elapsed) {
        y += velocityY;
    }//end update

    /**
     * Renders the floating text on screen with a fade-out effect based on its age.
     * Also draws an outline around the text for visibility.
     *
     * @param g the graphics context
     * @param xOffset horizontal offset (e.g., for camera movement)
     * @param yOffset vertical offset (e.g., for camera movement)
     */
    public void draw(Graphics2D g, int xOffset, int yOffset) {
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1f, (float) elapsed / life);
        float alpha = 1f - progress;

        if (alpha <= 0f) return;

        int drawX = (int) (x + xOffset);
        int drawY = (int) (y + yOffset);

        Composite originalComp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setFont(font);

        //Outline effect
        g.setColor(Color.BLACK);
        g.drawString(text, drawX - 1, drawY);
        g.drawString(text, drawX + 1, drawY);
        g.drawString(text, drawX, drawY - 1);
        g.drawString(text, drawX, drawY + 1);

        //Main text
        g.setColor(baseColor);
        g.drawString(text, drawX, drawY);

        g.setComposite(originalComp);
    }//end draw

    /**
     * Convenience method to create a special floating text for "Level Up!" events.
     * Uses a larger font, yellow color, and a slower upward movement.
     *
     * @param x the x-coordinate where the text appears
     * @param y the y-coordinate where the text appears
     * @return a configured FloatingText instance representing the level-up effect
     */
    public static FloatingText createLevelUpText(float x, float y) {
        String msg = "Level Up!";
        Color color = Color.YELLOW;
        Font font = new Font("Arial", Font.BOLD, Settings.getScreenHeight() / 40); // Bigger

        FloatingText ft = new FloatingText(msg, x, y, color, font);
        ft.velocityY = -0.4f; //slower rise
        ft.life = 1600; //longer lifespan
        return ft;
    }//end createLevelUpText

}//end class
