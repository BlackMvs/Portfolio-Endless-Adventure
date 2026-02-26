package ui;

import settings.Settings;
import utilities.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents a single layer in a parallax scrolling background system.
 * <p>
 * Each layer scrolls horizontally at a different speed based on its parallax speed,
 * creating a depth illusion. The image is scaled once on initialization and drawn
 * as a seamless loop during gameplay.
 */
public class ParallaxBackgroundLayer {
    private final BufferedImage scaledImage;
    private final float parallaxSpeedX;
    private final int screenWidth;

    /**
     * Constructs a new parallax background layer with a specified image and scroll speed.
     *
     * @param imagePath     The path to the image file for this layer.
     * @param speedX        The horizontal parallax speed (0 = static, 1 = same as camera).
     */
    public ParallaxBackgroundLayer(String imagePath, float speedX) {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load background image from: " + imagePath, e);
        }//end try-catch

        this.parallaxSpeedX = speedX;
        this.screenWidth = Settings.getScreenWidth();

        //Scale once and reuse to avoid runtime scaling
        this.scaledImage = UtilityTool.scaleImage(originalImage, screenWidth, Settings.getScreenHeight());
    }//end constructor

    /**
     * Draws the parallax background layer based on the current camera position.
     * <p>
     * Ensures seamless looping by drawing the image twice when necessary.
     *
     * @param g         The graphics context used to draw.
     * @param cameraX   The X position of the camera (or player) in the world.
     */
    public void draw(Graphics2D g, float cameraX) {
        float positionX = -(cameraX * parallaxSpeedX) % screenWidth;

        if (positionX > 0){
            positionX -= screenWidth;
        }//end if

        g.drawImage(scaledImage, (int) positionX, 0, null);
        g.drawImage(scaledImage, (int) (positionX + screenWidth), 0, null);
    }//end draw

}//end class
