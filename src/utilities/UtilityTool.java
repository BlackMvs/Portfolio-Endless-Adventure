package utilities;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A utility class providing helpful image processing tools.
 * <p>
 * Currently, includes image scaling methods for performance optimization during rendering.
 */
public class UtilityTool {

    /**
     * Scales a given {@link BufferedImage} to the specified width and height.
     * <p>
     * This method creates a new image buffer and draws the original image into it using
     * bilinear interpolation for smoother quality. It's intended to be used at load time
     * to avoid real-time scaling during rendering, which is computationally expensive.
     *
     * @param original The original BufferedImage to scale.
     * @param width    The desired width of the scaled image.
     * @param height   The desired height of the scaled image.
     * @return A new BufferedImage scaled to the specified dimensions.
     */
    public static BufferedImage scaleImage(BufferedImage original, int width, int height) {
        //Scale the image once at load time to avoid expensive operations during rendering
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = scaledImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();

        return scaledImage;
    }//end scaleImage

}//end class
