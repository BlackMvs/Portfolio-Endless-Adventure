package ui;

import settings.Settings;
import utilities.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents static background image for the game screen.
 * <p>
 * The image is scaled to the screen dimensions once during initialization
 * and drawn each frame without movement or parallax effects.
 */
public class StaticBackground {
    private final BufferedImage scaledImage;

    /**
     * Loads and scales a static background image from the specified path.
     *
     * @param imagePath The path to the background image file.
     */
    public StaticBackground(String imagePath) {
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load background image from: " + imagePath, e);
        }//end try-catch

        //Scale the image to screen size
        this.scaledImage = UtilityTool.scaleImage(originalImage, Settings.getScreenWidth(), Settings.getScreenHeight());
    }//end constructor

    /**
     * Draws the static background image in the top-left corner of the screen.
     *
     * @param g The {@link Graphics2D} context used to draw the image.
     */
    public void draw(Graphics2D g) {
        g.drawImage(scaledImage, 0, 0, null);
    }//end draw

}//end class
