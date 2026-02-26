package gameExtended2D;

import game2D.Tile;
import settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents a tile in the extended tile map system with support for scaled images,
 * tile types (e.g., ground, platform, empty), and coded tile identifiers.
 * <p>
 * Extends the base {@link game2D.Tile} class by adding visual and gameplay metadata.
 */
public class TileExtended extends Tile {

    /**
     * The type of tile, used for gameplay logic like collision and platform detection.
     */
    public enum TileType {
        GROUND, PLATFORM, EMPTY
    }//end enum
    private TileType type;
    private Image image;
    private String code;

    /**
     * Constructs a TileExtended at the given position with an identifying code.
     * Defaults the tile type to {@link TileType#EMPTY}.
     *
     * @param code a unique string identifier or map symbol
     * @param x    the x-coordinate of the tile in the map grid
     * @param y    the y-coordinate of the tile in the map grid
     */
    public TileExtended(String code, int x, int y) {
        super(' ', x, y);
        this.code = code;
        this.type = TileType.EMPTY; //Default type
    }//end constructor

    /**
     * Sets the tile's image using a file path and scales it based on the original dimensions
     * and the global tile scale defined in {@link Settings}.
     *
     * @param imagePath      the path to the image file
     * @param originalWidth  the original width (in pixels) of the image
     * @param originalHeight the original height (in pixels) of the image
     */
    public void setImage(String imagePath, int originalWidth, int originalHeight) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image originalImage = icon.getImage();
        int widthSize = originalWidth * Settings.getTileScale();
        int heightSize = originalWidth * Settings.getTileScale();
        this.image = scaleImage(originalImage, widthSize, heightSize);
    }//end setImage

    /**
     * Assigns a transparent image
     *
     * @param image the image to assign
     */
    public void setImage(Image image) {
        this.image = new ImageIcon(image).getImage();
    }//end setImage

    /**
     * Scales a given image to the specified width and height using bilinear interpolation.
     *
     * @param img    the image to scale
     * @param width  the desired width in pixels
     * @param height the desired height in pixels
     * @return the scaled {@link Image}
     */
    private Image scaleImage(Image img, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
    }//end scaleImage method

    //GETTERS AND SETTERS

    public Image getImage() {
        return image;
    }//end getImage

    public String getCode() {
        return code;
    }//end getCode

    public void setType(TileType type) {
        this.type = type;
    }//end setType

    public TileType getType() {
        return type;
    }//end getType

}//end class
