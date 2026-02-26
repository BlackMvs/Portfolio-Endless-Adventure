package gameExtended2D;

import game2D.Animation;
import settings.Settings;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * An extended version of the base {@link Animation} class that adds features like:
 * - Horizontal flipping
 * - Image scaling based on game settings
 * - Simplified creation from sprite sheets
 * <p>
 * Used to streamline animation setup for game objects like players, enemies, and effects.
 */
public class AnimationExtended extends Animation {

    //instance variables
    private String spriteSheetPath;
    private int spriteSheetCol;
    private int spriteSheetRow;
    private int spriteSheetFrameDuration;

    // <editor-fold desc="Constructors">

    /**
     * Constructs a default AnimationExtended with looping disabled.
     */
    public AnimationExtended(){
        super();
    }//end constructor

    /**
     * Constructs an AnimationExtended with the option to loop.
     *
     * @param loop whether the animation should loop
     */
    public AnimationExtended(boolean loop) {
        super();
        super.setLoop(loop);
    }//end constructor

    //TODO double check it and remove it later, obsolete now
    /**
     * Constructs an AnimationExtended with sprite sheet data.
     * Sets up internal values used for creation or reuse.
     *
     * @param spriteSheetPath          the path to the sprite sheet image
     * @param spriteSheetCol           the number of columns in the sprite sheet
     * @param spriteSheetRow           the number of rows in the sprite sheet
     * @param spriteSheetFrameDuration the duration of each frame in milliseconds
     * @param loop                     whether the animation should loop
     */
    public AnimationExtended(String spriteSheetPath, int spriteSheetCol, int spriteSheetRow, int spriteSheetFrameDuration, boolean loop){
        super();
        super.setLoop(loop);
        this.spriteSheetPath = spriteSheetPath;
        this.spriteSheetCol = spriteSheetCol;
        this.spriteSheetRow = spriteSheetRow;
        this.spriteSheetFrameDuration = spriteSheetFrameDuration;
    }//end constructor
    // </editor-fold>

    // <editor-fold desc="Flip animation">

    /**
     * Flips a {@link BufferedImage} horizontally.
     *
     * @param original the image to flip
     * @return the horizontally flipped image
     */
    private BufferedImage flipImageHorizontally(BufferedImage original) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-original.getWidth(), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(original, null);
    }//end flipImageHorizontally method

    /**
     * Flips all frames of an animation horizontally in-place.
     *
     * @param animation the animation whose frames should be flipped
     */
    private void flipAnimationHorizontally(AnimationExtended animation){
        for(int i = 0; i < animation.getFrames().size(); i++){
            BufferedImage bufferedImage = (BufferedImage) animation.getFrameImage(i);
            animation.setFrameImage(i, flipImageHorizontally(bufferedImage));
        }//end for loop
    }//end flipAnimationHorizontally method

    /**
     * Scales a given {@link Image} to the specified dimensions using bilinear interpolation.
     *
     * @param img    the image to scale
     * @param width  the target width
     * @param height the target height
     * @return the scaled {@link BufferedImage}
     */
    private Image scaleImage(Image img, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
    }//end scaleImage method

    /**
     * Scales all frames of an animation based on the global tile scale defined in {@link Settings}.
     *
     * @param animation the animation whose frames should be scaled
     */
    private void scaleAnimation(AnimationExtended animation){
        for(int i = 0; i < animation.getFrames().size(); i++){
            BufferedImage bufferedImage = (BufferedImage) animation.getFrameImage(i);
            int widthSize = bufferedImage.getWidth() * Settings.getTileScale();
            int heightSize = bufferedImage.getHeight() * Settings.getTileScale();
            animation.setFrameImage(i, scaleImage(bufferedImage, widthSize, heightSize));
        }//end for loop
    }//end scaleAnimation

    /**
     * Replaces a frame's image at the specified index while preserving its end time.
     *
     * @param i     the index of the frame to update
     * @param image the new image to assign to the frame
     */
    public void setFrameImage(int i, Image image){
        AnimFrame frame = new AnimFrame(image, super.getFrames().get(i).endTime);
        super.getFrames().set(i, frame);
    }//end setFrameImage
    // </editor-fold>

    /**
     * Creates a new AnimationExtended from a sprite sheet.
     * Supports frame flipping and auto-scaling.
     *
     * @param spriteSheetPath          the path to the sprite sheet
     * @param spriteSheetCol           the number of columns in the sprite sheet
     * @param spriteSheetRow           the number of rows in the sprite sheet
     * @param spriteSheetFrameDuration the duration of each frame in milliseconds
     * @param frameStartNo             the starting frame index
     * @param numFrames                the number of frames to use
     * @param flipped                  whether the frames should be flipped horizontally
     * @param loop                     whether the animation should loop
     * @return the created and configured AnimationExtended
     */
    public AnimationExtended createAnimation(String spriteSheetPath, int spriteSheetCol, int spriteSheetRow, long spriteSheetFrameDuration, int frameStartNo, int numFrames, boolean flipped, boolean loop){
        AnimationExtended animation = new AnimationExtended(loop);
        animation.loadAnimationSeries(spriteSheetPath, spriteSheetCol, spriteSheetRow, spriteSheetFrameDuration, frameStartNo, numFrames);
        if(flipped){
            flipAnimationHorizontally(animation);
        }//end if
        scaleAnimation(animation); //scale the animation
        return animation;
    }//end createAnimation method

    /**
     * Returns the list of animation frames in this animation.
     *
     * @return a list of {@link AnimFrame} objects
     */
    public ArrayList<AnimFrame> getFrames() {
        return super.getFrames();
    }//end getFrames method

}//end class

