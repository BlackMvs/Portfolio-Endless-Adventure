package settings;
//TODO java docs

/**
 * Holds various configuration settings
 * for the application, including screen dimensions, tile sizes, and scaling factors.
 * All values are defined as static fields to be accessed globally.
 */
public class Settings {

    /**
     * Enum to represent common screen resolutions supported by the game
     */
    public enum Resolution {

        //1280x720 resolution
        HD_720(1280, 720),

        //1920x1080 resolution
        HD_1080(1920, 1080),

        //1024x768 resolution
        XGA(1024, 768),

        //1280x1024 resolution
        SXGA(1280, 1024);

        //Width and height in pixels for resolution
        public final int width;
        public final int height;

        /**
         * Constructor to initialize width and height for each resolution
         * @param width The width that the resolution has
         * @param height The height that the resolution has
         */
        Resolution(int width, int height) {
            this.width = width;
            this.height = height;
        }//end constructor

    }//end enum Resolution

    //Set a default resolution
    private static Resolution screenResolution = Resolution.XGA;

    private static boolean screenFullscreen = false;
    private static int defaultTileSize = 32;
    private static int tileScale = 2;
    private static int tileSize = getDefaultTileSize() * tileScale;
    private static int windowRow = screenResolution.width / tileSize;
    private static int windowCol = screenResolution.height / tileSize;
    private static int screenWidth = windowRow * tileSize;
    private static int screenHeight = windowCol * tileSize;

    private static boolean debugMode = false;
    private static boolean longLoadingScreen = false;
    private static boolean isPlayerInvincible = false;
    private static float gravity = 0.3f;
    private static final float GRAVITY_MAX = 0.3f; //gravity cannot exceed this amount
    private static final float GRAVITY_LIMIT = 1.5f; //gravity when falling

    //Sound
    private static float SOUND_EFFECT_VOLUME = 1f;

    private static float MUSIC_VOLUME = 1f;

    //GETTERS AND SETTERS

    public static boolean getScreenFullScreen(){
        return screenFullscreen;
    }//end getScreenFullScreen

    public static void setScreenFullscreen(boolean screenFullscreen){
        Settings.screenFullscreen = screenFullscreen;
    }//end setScreenFullscreen

    public static int getTileSize() {
        return tileSize;
    }//end getTileSize

    public static int getScreenWidth() {
        return screenWidth;
    }//end getScreenWidth

    public static int getScreenHeight() {
        return screenHeight;
    }//end getScreenHeight

    public static int getTileScale() {
        return tileScale;
    }//end getTileScale

    public static int getDefaultTileSize() {
        return defaultTileSize;
    }//end getDefaultTileSize

    public static boolean getDebugMode() {
        return debugMode;
    }//end isDebugMode

    public static void setDebugMode(boolean debugMode) {
        Settings.debugMode = debugMode;
    }//end setDebugMode

    public static float getGravity() {
        return gravity;
    }//end getGravity

    public static void setGravity(float gravity) {
        Settings.gravity = gravity;
    }//end setGravity

    public static float getGravityMax() {
        return GRAVITY_MAX;
    }

    public static float getGravityLimit() {
        return GRAVITY_LIMIT;
    }//end getGravityLimit

    public static boolean getIsPlayerInvincible() {
        return isPlayerInvincible;
    }

    public static void setIsPlayerInvincible(boolean isPlayerInvincible) {
        Settings.isPlayerInvincible = isPlayerInvincible;
    }//end setIsPlayerInvincible

    public static boolean getLongLoadingScreen() {
        return longLoadingScreen;
    }//end getLongLoadingScreen

    public static void setLongLoadingScreen(boolean longLoadingScreen) {
        Settings.longLoadingScreen = longLoadingScreen;
    }//end setLongLoadingScreen

    /** Sound effect volume: 0.0 (mute) to 1.0 (max) */
    public static float getSoundEffectVolume() {
        return SOUND_EFFECT_VOLUME;
    }//end getSoundEffectVolume

    public static void setSoundEffectVolume(float soundEffectVolume) {
        SOUND_EFFECT_VOLUME = soundEffectVolume;
    }//end setSoundEffectVolume

    /** Music volume: 0.0 (mute) to 1.0 (max) */
    public static float getMusicVolume() {
        return MUSIC_VOLUME;
    }//end getMusicVolume

    public static void setMusicVolume(float musicVolume) {
        MUSIC_VOLUME = musicVolume;
    }//end setMusicVolume

    /**
     * Sets the screen resolution to one of the predefined values in the {@link Resolution} enum.
     * Also recalculates the number of rows and columns in the game window
     * based on the current tile size and updates the screen dimensions accordingly.
     * <p>
     * Call this method before the game starts to configure the resolution.
     * </p>
     *
     * @param res The desired screen resolution from the {@link Resolution} enum.
     */
    public static void setScreenResolution(Resolution res) {
        screenResolution = res;

        //Recalculate number of tiles that can fit in the selected resolution
        windowRow = res.width / tileSize;
        windowCol = res.height / tileSize;

        //Recalculate actual screen size based on how many full tiles can fit
        screenWidth = windowRow * tileSize;
        screenHeight = windowCol * tileSize;
    }//end setScreenResolution


}//end class
