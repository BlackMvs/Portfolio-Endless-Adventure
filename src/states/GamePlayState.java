package states;

import entity.enemy.BasicEnemy;
import entity.enemy.Enemy;
import entity.player.Player;
import gameExtended2D.SpriteExtended;
import gameExtended2D.TileExtended;
import gameExtended2D.TileMapExtended;
import interactables.Portal;
import main.Game;
import settings.Collision;
import settings.KeyHandler;
import settings.MapFileLoader;
import settings.Settings;
import sound.MidiMusicPlayer;
import ui.ConfigUI;
import ui.MenuButton;
import ui.ParallaxBackgroundLayer;
import ui.StaticBackground;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
//TODO java docs

/**
 * Represents the main gameplay environment where the player interacts
 * with the world, including the ability to pause.
 */
public class GamePlayState implements InterfaceGameState {

    /**
     * Enum representing the sub-states within the gameplay lifecycle.
     * Includes:
     * - LOADING: Assets and level data are loading.
     * - WAITING_TO_CONTINUE: Waiting for player confirmation after loading.
     * - PLAY: Active gameplay.
     * - PAUSE: Gameplay is suspended.
     */
    private enum SubState { LOADING, WAITING_TO_CONTINUE, PLAY, PAUSE }

    //TODO add proper comment to explain that because the way the states work the draw function and update will always run
    // so we need to create a loading screen that will want till the map and other assets are loaded
    private SubState subState = SubState.LOADING;

    //<editor-fold desc = "VARIABLES: LOADING SCREEN " >
    private int showOffLoadingTextTimer = 500;
    private int totalLoadingSteps;
    private int currentLoadingStep;
    private ArrayList<String> loadingMessages;
    private String loadingMessage;

    //not needed but just want an extra failsafe in place
    private boolean assetsLoaded;

    //</editor-fold> VARIABLES: LOADING SCREEN

    // <editor-fold desc = "VARIABLES: PAUSE SCREEN">
    private MenuButton resumeButton;
    private MenuButton quitButton;
    //</editor-fold> VARIABLES: PAUSE SCREEN

    // <editor-fold desc = "VARIABLES: CAMERA">
    private float cameraY = 0;
    @SuppressWarnings("FieldCanBeLocal")
    private final int DEAD_ZONE_HEIGHT = 175;  //how tall the dead zone is

    //</editor-fold> VARIABLES: CAMERA

    // <editor-fold desc = "VARIABLES: GAME VARIABLES">
    private float gravity;
    private boolean isGameFinished;

    //</editor-fold> VARIABLES: GAME VARIABLES

    // <editor-fold desc = "GAME RESOURCES">
    private final Game GAME;
    private final GameStateManager GAME_STATE_MANAGER;
    private KeyHandler keyHandler;
    private MidiMusicPlayer midiMusicPlayer;
    private static ArrayList<SpriteExtended> entities;
    private Player player;
    private ArrayList<TileExtended> collidedTiles;
    private TileMapExtended tileMap;
    private MapFileLoader mapFileLoader;
    private StaticBackground staticBackground;
    private ArrayList<ParallaxBackgroundLayer> parallaxBackgroundLayers;
    private ArrayList<SpriteExtended> interactables;
    private Point portalPosition; //TODO the same but for the player and consider platforms as well, and do the same for the enemies cause why not
    private ArrayList<Point> enemyPossiblePositions;
    private ArrayList<Point> enemyPositions;

    //</editor-fold> GAME RESOURCES

    // <editor-fold desc = "CONSTRUCTORS">
    /**
     * Constructs a new GamePlayState for the main gameplay environment
     *
     * @param game             The {@link Game} instance managing the application.
     * @param gameStateManager The {@link GameStateManager} responsible for
     *                         handling state transitions.
     */
    public GamePlayState(Game game, GameStateManager gameStateManager) {
        this.GAME = game;
        this.GAME_STATE_MANAGER = gameStateManager;
    }//end constructor

    //</editor-fold> CONSTRUCTORS

    // <editor-fold desc = "INITIALISE">
//TODO modify this java doc
    /**
     * Initialise the class, e.g. set up variables, load images,
     * create animations, register event handlers.
     * <p>
     * This shows you the general principles, but you should create specific
     * methods for setting up your game that can be called again when you wish to
     * restart the game (for example you may only want to load animations once,
     * but you could reset the positions of sprites each time you restart the game).
     */
    public void init() {
        initialiseLoadingScreen();

        //Set gravity
        this.gravity = Settings.getGravity();

        //Set the key handler
        this.keyHandler = this.GAME.getKeyHandler();

        //create new instances of the variables
        entities = new ArrayList<>();
        this.collidedTiles = new ArrayList<>();

        //other variables
        this.isGameFinished = false;

        //create the loading requirements
        createLoadingRequirements();
    }//end init

    /**
     * Initializes variables used for the loading screen and ensures default loading configuration.
     */
    private void initialiseLoadingScreen(){
        //Set initial state
        this.subState = SubState.LOADING;
        //Set variables
        this.showOffLoadingTextTimer = 0;
        this.totalLoadingSteps = 0;
        this.currentLoadingStep = 0;
        this.loadingMessages = new ArrayList<>();
        this.loadingMessage = "Starting...";
        this.assetsLoaded = false;
    }//end initialiseLoadingScreen

    /**
     * Defines the ordered list of asset loading steps and corresponding messages
     * for the visual loading sequence.
     */
    private void createLoadingRequirements(){
        addLoadingRequirement("Selecting map...");              // 0
        addLoadingRequirement("Loading map tiles...");          // 1
        addLoadingRequirement("Creating player...");            // 2
        addLoadingRequirement("Creating entities...");          // 3
        addLoadingRequirement("Loading menu buttons...");       // 4
        addLoadingRequirement("Loading the background...");     // 5
        addLoadingRequirement("Loading interactable objects");  // 6
        addLoadingRequirement("Loading music...");              // 7
        addLoadingRequirement("Finished");
    }//end createLoadingRequirements

    /**
     * Executes the next stage of the loading pipeline and handles each
     * respective asset or system component.
     */
    private void performLoadingStep(){
        //Add a thread sleep to show off your loading text
        //Is it fast and recommended? No
        //Do I feel proud with the loading screen? Yes!
        //YOU SHALL SEE AND READ EVERY LOADING TEXT EVEN IF IT TAKES LONGER TO LOAD
        if (Settings.getLongLoadingScreen()){
            try {
                Thread.sleep(this.showOffLoadingTextTimer);
            }catch (Exception e){
                System.out.println("Error for the loading sleep");
            }//end try-catch
        }//end if

        //Check current stage
        this.loadingMessage = this.loadingMessages.get(this.currentLoadingStep);
        switch (this.currentLoadingStep) {
            case 0: //Selecting map
                this.mapFileLoader = new MapFileLoader("maps/mapLevels");
                break;

            case 1: //Loading map tiles
                String selectedMap = this.mapFileLoader.getRandomMapFileName();
                this.tileMap = new TileMapExtended(55, 181);
                if (this.GAME.getCurrentLevel() == 0 ){
                    tileMap.loadMap("maps", "map0.txt");
                }//end if
                else {
                    tileMap.loadMap("maps", selectedMap);
                }//end else
                break;

            case 2: //Creating player
                initialisePlayer();
                break;

            case 3: //Creating entities
                initialiseEntities();
                break;

            case 4: //Adding menu buttons
                initialisePauseMenu();
                break;

            case 5:
                //BACKGROUND
                this.staticBackground = new StaticBackground("images/ParallaxBackground/type_1/bg.png");

                //PARALLAX
                this.parallaxBackgroundLayers = new ArrayList<>();
                ParallaxBackgroundLayer parallaxBackground;
                //Layer 1
                parallaxBackground = new ParallaxBackgroundLayer("images/ParallaxBackground/type_1/1.png", 0.3f);
                this.parallaxBackgroundLayers.add(parallaxBackground);
                //Layer 2
                parallaxBackground = new ParallaxBackgroundLayer("images/ParallaxBackground/type_1/2.png", 0.6f);
                this.parallaxBackgroundLayers.add(parallaxBackground);

                break;

            case 6:
                //portal
                this.interactables = new ArrayList<>();
                this.portalPosition = findRandomGroundTilePosition(tileMap);
                if (this.GAME.getCurrentLevel() == 0){
                    spawnPortal(new Point(26, 6));
                }//end if
                break;

            case 7:
                //music
                this.midiMusicPlayer = new MidiMusicPlayer();
                break;

            case 8:
                this.assetsLoaded = true;
                break;
        }//end switch

        currentLoadingStep++; //move to the next loading

        //If finished
        if (currentLoadingStep >= totalLoadingSteps && this.assetsLoaded) {
            this.loadingMessage = "Press any key to continue...";
            subState = SubState.WAITING_TO_CONTINUE;
        }//end if
    }//end performLoadingStep

    /**
     * Populates the world with enemy entities based on valid platform tile locations.
     * Spawning scales with the current game level.
     */
    private void initialiseEntities(){
        this.enemyPossiblePositions = new ArrayList<>();
        this.enemyPositions = new ArrayList<>(); //Debug only
        findEnemyPlatformTilePositions(this.tileMap);

        //make sure we have positions available
        if (enemyPossiblePositions.isEmpty()) {
            System.out.println("No valid enemy platform positions found.");
            return;
        }//end if

        int noOfEnemies = this.GAME.getCurrentLevel();

        //Check we never have less than 1 enemy
        //Useless as I want to quickly show enemy numbers increasing each level
        //Otherwise I would divide the noOfEnemies by like 2 or 3, for a better chance to survive which will make the
        //check below make more sense
        //TODO uncomment this once we have tutorial implemented
        if (this.GAME.getCurrentLevel() != 0 && noOfEnemies < 1) {
            noOfEnemies = 1;
        }//end if

        //check we do not ask for more enemies than we can have
        noOfEnemies = Math.min(noOfEnemies, enemyPossiblePositions.size());

        for (int i = 0; i < noOfEnemies; i++) {
            int index = (int)(Math.random() * enemyPossiblePositions.size());
            this.enemyPositions.add(enemyPossiblePositions.get(index)); //Add for debug drawing
            Point tile = enemyPossiblePositions.remove(index);

            float px = tile.x * tileMap.getTileWidth();
            float py = tile.y * tileMap.getTileHeight();

            BasicEnemy enemy = new BasicEnemy(px, py, GAME.getCurrentLevel(), this.tileMap, this.player);
            entities.add(enemy);
        }//end for loop
    }//end initialiseEntities

    /**
     * Initializes and positions the player sprite, applying keyboard input configuration.
     */
    private void initialisePlayer(){
        this.player = this.GAME.getPlayer();
        this.player.setKeyHandler(this.keyHandler);
        if(this.GAME.getCurrentLevel() == 0 ){
            int x = 2 * Settings.getTileSize();
            int y = 6 * Settings.getTileSize();
            this.player.setPosition(x, y);
        }//end if
        else {
            setRandomPlayerPosition();
        }//end else
        this.player.setVelocity(0,0);
        this.player.show();
        entities.add(this.player);
    }//end initialisePlayer

    /**
     * Configures pause menu interface buttons including resume and quit functionality.
     */
    @SuppressWarnings("CodeBlock2Expr")
    private void initialisePauseMenu() {
        int buttonWidth = Settings.getScreenWidth() / 5;
        int buttonHeight = Settings.getScreenHeight() / 15;
        int spacing = Settings.getScreenHeight() / 40;
        int centerX = (Settings.getScreenWidth() - buttonWidth) / 2;
        int startY = Settings.getScreenHeight() / 2;

        resumeButton = new MenuButton("Resume", centerX, startY, buttonWidth, buttonHeight, () -> {
            subState = SubState.PLAY;
        });//end MenuButton

        quitButton = new MenuButton("Quit", centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight, () -> {
            GAME_STATE_MANAGER.setState(GameStateType.GAMEOVER);
        });//end MenuButton
    }//end initialisePauseMenu

    //</editor-fold> INITIALISE

    @Override
    public void enter() {
        System.out.println("Entering GameplayState");
        System.out.println("Level: " + this.GAME.getCurrentLevel());
        init();
    }//end enter

    @Override
    public void exit() {
        System.out.println("Exiting GameplayState");
        this.GAME.setCurrentLevel(this.GAME.getCurrentLevel() + 1); //increase the level
        this.midiMusicPlayer.close();
    }//end exit

    // <editor-fold desc = "METHODS: UPDATES">
    /**
     * Update any sprites and check for collisions
     *
     * @param elapsed The elapsed time between this call and the previous call of elapsed
     */
    @Override
    public void update(long elapsed) {
        //only update anything if we are playing
        if (subState == SubState.PLAY) {
            updateGamePlayState(elapsed);
        }//end if
        else if (this.subState == SubState.LOADING){
            performLoadingStep();
        }//end else
    }//end update

    /**
     * Handles update logic specific to the active gameplay state, including
     * gravity, collision, and entity logic.
     *
     * @param elapsed The time elapsed since the last update.
     */
    private void updateGamePlayState(long elapsed) {
        Iterator<SpriteExtended> iterator = entities.iterator();

        //entities
        while (iterator.hasNext()) {
            SpriteExtended entity = iterator.next();
            entity.update(elapsed);

            //Add gravity
            float checkGravity = entity.getVelocityY() + this.gravity;
            if (checkGravity > Settings.getGravityLimit()) {
                checkGravity = this.gravity;
            }//end if
            entity.setVelocityY(checkGravity);

            Collision.collisionSpriteToTile(entity, this.tileMap, this.collidedTiles);

            if (!(entity instanceof Player)) {
                Collision.collisionSpriteToSprite(this.player, entity);

                if (entity instanceof Enemy enemy) {
                    if (enemy.checkIfDead()) {
                        System.out.println("Enemy is dead — removing from world. Check for portal");
                        iterator.remove();
                        if(entities.size() == 1){
                            if(entities.getFirst() instanceof Player){
                                this.isGameFinished = true;
                                spawnPortal(this.portalPosition);
                            }//end if
                        }//end if
                    }//end if
                }//end if
            }//end if
        }//end while loop

        //interactables
        iterator = this.interactables.iterator();
        while(iterator.hasNext()){
            SpriteExtended interactable = iterator.next();
            interactable.update(elapsed);
        }//end while loop

        //TODO once you implement the interactable change this up so all of them will use the same thing
        //check collision with interactables
        SpriteExtended interactable = Collision.checkInteractionCollision(this.player, this.interactables);
        if (interactable instanceof Portal portal) { //PORTAL
            portal.trigger(this.GAME_STATE_MANAGER);
        }//end if

        if(this.player.getIsDying()){
            this.player.setKeyHandler(new KeyHandler());
            if(this.player.getAnimation().hasLooped()){
                this.GAME_STATE_MANAGER.setState(GameStateType.GAMEOVER);
            }//end if
        }//end if

    }//end updateGamePlayerState

    //</editor-fold> METHODS: UPDATES

    // <editor-fold desc = "DRAW">
    @Override
    public void draw(Graphics2D g) {
        if (subState == SubState.PLAY){
            drawPlayState(g);
            drawPlayStateUI(g);
        }//end if
        else if (subState == SubState.PAUSE){
//            drawPlayStateUI(g);
            drawPauseStateUI(g);
        }//end else if
        else if (this.subState == SubState.LOADING || this.subState == SubState.WAITING_TO_CONTINUE){
            drawLoadingStateUI(g);
        }//end else if

    }//end draw

    /**
     * Draws the main game visuals during active gameplay.
     *
     * @param g The {@link Graphics2D} used for drawing operations.
     */
    public void drawPlayState(Graphics2D g){
        //Draw background image
        this.staticBackground.draw(g);

        //Parallax background
        for (ParallaxBackgroundLayer layer : this.parallaxBackgroundLayers) {
            layer.draw(g, player.getX());
        }//end for loop

        //Camera
        float playerY = player.getY();
        float screenCenterY = cameraY + Settings.getScreenHeight() / 2f;

        //DEAD ZONE bounds
        float deadZoneTop = screenCenterY - DEAD_ZONE_HEIGHT / 2f;
        float deadZoneBottom = screenCenterY + DEAD_ZONE_HEIGHT / 2f;


        //If player leaves vertical dead zone, move camera
        if (playerY < deadZoneTop) {
            cameraY -= (deadZoneTop - playerY);
        }//end if
        else if (playerY > deadZoneBottom) {
            cameraY += (playerY - deadZoneBottom);
        }//end else if

        //Final offsets for drawing
        int xo = (int)(Settings.getScreenWidth()/2 - this.player.getX());
        int yo = (int)(-cameraY);


        //Apply offsets to entity.player
        this.player.setOffsets(xo, yo);

        //Apply offsets to tile map and draw  it
        this.tileMap.draw(g,xo,yo);

        //Draw all the entities being displayed to the screen
        for (SpriteExtended entity: entities){
            entity.setOffsets(xo, yo);
            entity.draw(g);
        }//end for loop

        //interactables
        for (SpriteExtended interactable: this.interactables){
            interactable.setOffsets(xo, yo);
            interactable.draw(g);
        }//end for loop

        this.player.drawEffects(g, xo, yo);

        if (Settings.getDebugMode()) {
            // When in debug mode, you could draw borders around objects
            // and write messages to the screen with useful information.
            // Try to avoid printing to the console since it will produce
            // a lot of output and slow down your game.
            g.setColor(Color.DARK_GRAY);
            this.tileMap.drawBorder(g, xo, yo, Color.black);

            g.setColor(Color.RED);
            this.player.drawBoundingBox(g);
            this.player.drawBoundingCircle(g);

            for (SpriteExtended entity: entities){
                entity.drawBoundingBox(g);
                entity.drawBoundingCircle(g);
            }//end for loop

            Collision.drawCollidedTiles(g, this.tileMap, xo, yo, this.collidedTiles);

            //enemy spawn points
            g.setColor(Color.BLUE);
            for (Point p : enemyPositions) {
                int px = (p.x * tileMap.getTileWidth()) + xo;
                int py = (p.y * tileMap.getTileHeight()) + yo;
                g.drawRect(px, py, tileMap.getTileWidth(), tileMap.getTileHeight());
            }//end for loop

            //Show fps
            int fps = (int) this.GAME.getFPS();
            String msg = String.format("FPS: %d", fps);
            g.setColor(Color.GREEN);
            g.setFont(ConfigUI.getNormalPlainScaledFont());
            g.drawString(msg, this.GAME.getWidth() - 100, 40);
        }//end if

        if (GAME.getCurrentLevel() == 0) {
            g.setFont(ConfigUI.getNormalBoldScaledFont());
            g.setColor(Color.WHITE);

            //Message 1
            String msg1 = "Use arrow keys to move";
            int x1 = 2 * tileMap.getTileWidth(); // tile 3
            int y1 = 5 * tileMap.getTileHeight(); // tile 5
            g.drawString(msg1, x1 + xo, y1 + yo);

            //Message 2
            String msg2 = "Press Z to attack";
            int x2 = 10 * tileMap.getTileWidth();
            int y2 = 5 * tileMap.getTileHeight();
            g.drawString(msg2, x2 + xo, y2 + yo);

            //Message 3
            String msg3 = "Portals will show up when all enemies are dead";
            int x3 = 15 * tileMap.getTileWidth();
            int y3 = 5 * tileMap.getTileHeight();
            g.drawString(msg3, x3 + xo, y3 + yo);

            //Message 4
            String msg4 = "Enter portal to move to the next level";
            int x4 = 24 * tileMap.getTileWidth();
            int y4 = 5 * tileMap.getTileHeight();
            g.drawString(msg4, x4 + xo, y4 + yo);
        }//end if

    }//end drawPlayState

    /**
     * Draws the user interface elements specific to active gameplay.
     *
     * @param g The {@link Graphics2D} used for drawing operations.
     */
    @SuppressWarnings("UnnecessaryLocalVariable") //They are kept for readability
    public void drawPlayStateUI(Graphics2D g){
        //LAYOUT CONFIG
        int spacing = ConfigUI.getBarSpacing();
        int levelBoxWidth = Settings.getScreenWidth() / 9;
        int levelBoxHeight = Settings.getScreenHeight() / 9;

        int barWidth = ConfigUI.getBarWidth() + 50;
        int barHeight = ConfigUI.getBarHeight();

        int x = ConfigUI.getHorizontalMargin();
        int y = ConfigUI.getTopMargin();

        //LEVEL BOX
        int boxX = x;
        int boxY = y;

        g.setColor(Color.BLACK);
        g.fillRoundRect(boxX, boxY, levelBoxWidth, levelBoxHeight, 12, 12);
        g.setColor(ConfigUI.LEVEL_BADGE_COLOR);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(boxX, boxY, levelBoxWidth, levelBoxHeight, 12, 12);

        Font levelFont = ConfigUI.getNormalBoldScaledFont();
        g.setFont(levelFont);
        FontMetrics fm = g.getFontMetrics(levelFont);

        String levelText = "Lv. " + player.getLevel();
        int lvTextX = boxX + (levelBoxWidth - fm.stringWidth(levelText)) / 2;
        int lvTextY = boxY + fm.getAscent() +11;
        g.setColor(Color.WHITE);
        g.drawString(levelText, lvTextX, lvTextY);

        //EXP % under level
        float xpPercent = player.getExpCurrent() / player.getExpRequired();
        int xpPercentage = Math.round(xpPercent * 100);
        String expText = xpPercentage + "%";

        Font expFont = ConfigUI.getNormalPlainScaledFont();
        g.setFont(expFont);
        FontMetrics expFm = g.getFontMetrics(expFont);
        int expTextX = boxX + (levelBoxWidth - expFm.stringWidth(expText)) / 2;
        int expTextY = lvTextY + expFm.getHeight()+10;
        g.drawString(expText, expTextX, expTextY);

        //BARS (to the right of level box)
        int barStartX = boxX + levelBoxWidth + spacing;
        int barY = boxY;

        ConfigUI.drawStatBarWithText(g, barStartX, barY, barWidth, barHeight,
                player.getHealthCurrent(), player.getHealthMax(),
                ConfigUI.HEALTH_BAR_COLOR, "HP");

        int manaY = ConfigUI.getNextBarY(barY, barHeight, spacing);
        ConfigUI.drawStatBarWithText(g, barStartX, manaY, barWidth, barHeight,
                player.getManaCurrent(), player.getManaMax(),
                ConfigUI.MANA_BAR_COLOR, "MP");

        //WIN STATE TEXT
        if (this.isGameFinished) {
            int fontSize = Settings.getScreenHeight() / 10;
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            g.setColor(Color.GREEN);
            String text = "FIND THE PORTAL!";
            g.drawString(text, ConfigUI.middleStringX(g, text), Settings.getScreenHeight() / 3);
        }//end if

    }//end drawPlayStateUI

    /**
     * Draws the UI displayed when the game is paused.
     *
     * @param g The {@link Graphics2D} used for drawing operations.
     */
    public void drawPauseStateUI(Graphics2D g) {
        //Dim screen
        Composite original = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Settings.getScreenWidth(), Settings.getScreenHeight());
        g.setComposite(original);

        //Pause title
        String pausedText = "PAUSED";
        g.setFont(ConfigUI.getMassiveBoldScaledFont());
        int titleX = ConfigUI.middleStringX(g, pausedText);
        int titleY = Settings.getScreenHeight() / 4;
        ConfigUI.drawShadowedText(g, pausedText, titleX, titleY, ConfigUI.TEXT_COLOR_WHITE, ConfigUI.TEXT_COLOR_BLACK);

        //Button layout
        int buttonWidth = Settings.getScreenWidth() / 5;
        int buttonHeight = Settings.getScreenHeight() / 14;
        int spacing = Settings.getScreenHeight() / 40;
        int totalHeight = buttonHeight * 2 + spacing;

        int boxWidth = buttonWidth + 40;
        int boxHeight = totalHeight + 40;
        int boxX = (Settings.getScreenWidth() - boxWidth) / 2;
        int boxY = titleY + 40;

        //Panel background
        g.setColor(new Color(30, 30, 30, 200));
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        //Buttons
        int buttonX = boxX + 20;
        int buttonY = boxY + 20;

        if (resumeButton != null) {
            resumeButton.getBounds().setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
            resumeButton.draw(g);
        }//end if

        if (quitButton != null) {
            quitButton.getBounds().setBounds(buttonX, buttonY + buttonHeight + spacing, buttonWidth, buttonHeight);
            quitButton.draw(g);
        }//end if

        //Help text at bottom
        String helpText = "Press ESC to resume";
        g.setFont(ConfigUI.getNormalPlainScaledFont());
        int helpX = ConfigUI.middleStringX(g, helpText);
        int helpY = Settings.getScreenHeight() - 50;
        ConfigUI.drawShadowedText(g, helpText, helpX, helpY, ConfigUI.TEXT_COLOR_WHITE, ConfigUI.TEXT_COLOR_BLACK);
    }//end drawPauseStateUI

    /**
     * Renders the loading screen and progress bar during asset initialization.
     *
     * @param g The graphics context.
     */
    public void drawLoadingStateUI(Graphics2D g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Settings.getScreenWidth(), Settings.getScreenHeight());

        g.setFont(ConfigUI.getNormalBoldScaledFont());
        g.setColor(Color.WHITE);
        int textY = Settings.getScreenHeight() / 2 - 30;

        g.drawString(loadingMessage, ConfigUI.middleStringX(g, loadingMessage), textY);

        //Draw loading bar
        int barWidth = Settings.getScreenWidth() / 3;
        int barHeight = 20;
        int barX = (Settings.getScreenWidth() - barWidth) / 2;
        int barY = Settings.getScreenHeight() / 2;

        int progressWidth = (int)(((float) currentLoadingStep / totalLoadingSteps) * barWidth);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, progressWidth, barHeight);
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
    }//end drawLoadingUI
    //</editor-fold> DRAW

    /**
     * Finds a random empty tile above a ground tile to use for spawning entities such as the portal.
     *
     * @param tileMap The tile map to search.
     * @return A {@link Point} representing a valid tile position.
     */
    private Point findRandomGroundTilePosition(TileMapExtended tileMap) {
        int mapWidth = tileMap.getMapWidth();
        int mapHeight = tileMap.getMapHeight();

        int minXPadding = 4;
        int maxXPadding = 4;

        ArrayList<Point> validGroundPositions = new ArrayList<>();

        //Loop through each row from top to second-last
        for (int y = 0; y < mapHeight - 1; y++) {
            for (int x = minXPadding; x < mapWidth - maxXPadding; x++) {
                TileExtended current = tileMap.getTile(x, y);
                TileExtended below = tileMap.getTile(x, y + 1);

                if (current == null || below == null) continue;

                //Look for empty space above solid ground
                if (current.getType() == TileExtended.TileType.EMPTY &&
                        below.getType() == TileExtended.TileType.GROUND) {

                    validGroundPositions.add(new Point(x, y));

                    if (Settings.getDebugMode()) {
                        System.out.println("Valid spawn at tile (" + x + "," + y + ")");
                    }//end if
                }//end if
            }//end for loop
        }//end for loop

        if (validGroundPositions.isEmpty()) {
            System.out.println("No valid ground tiles found!");
            return null;
        }//end if

        // Pick a random point from the valid list
        Point chosen = validGroundPositions.get((int)(Math.random() * validGroundPositions.size()));
        System.out.println("Chosen portal position: " + chosen.x + "," + chosen.y);
        return chosen;
    }//end findRandomGroundTilePosition

    /**
     * Collects all platform tiles where enemies can validly spawn.
     *
     * @param tileMap The tile map to inspect for spawnable tiles.
     */
    private void findEnemyPlatformTilePositions(TileMapExtended tileMap) {
        int mapWidth = tileMap.getMapWidth();
        int mapHeight = tileMap.getMapHeight();

        int minXPadding = 4;
        int maxXPadding = 4;

        //Loop through each row from top to second-last
        for (int y = 0; y < mapHeight - 1; y++) {
            for (int x = minXPadding; x < mapWidth - maxXPadding; x++) {
                TileExtended current = tileMap.getTile(x, y);
                TileExtended below = tileMap.getTile(x, y + 1);

                if (current == null || below == null) continue;

                //Look for empty space above solid ground
                if (current.getType() == TileExtended.TileType.EMPTY &&
                        below.getType() == TileExtended.TileType.PLATFORM) {

                    this.enemyPossiblePositions.add(new Point(x, y));

                    if (Settings.getDebugMode()) {
                        System.out.println("Valid spawn at tile (" + x + "," + y + ")");
                    }//end if
                }//end if
            }//end for loop
        }//end for loop

        //Debug
        if (this.enemyPossiblePositions.isEmpty()) {
            System.out.println("No valid ground tiles found!");
        }//end if
        System.out.println("Number of positions: " + this.enemyPossiblePositions.size());

    }//end findRandomGroundTilePosition

    /**
     * Spawns a portal at the specified map location.
     *
     * @param portalPosition Tile coordinates to place the portal.
     */
    private void spawnPortal(Point portalPosition){
        if (portalPosition != null) {
            float tileSize = tileMap.getTileWidth();
            float px = portalPosition.x * tileSize;
            float py = portalPosition.y * tileMap.getTileHeight();

            Portal portal = new Portal(px, py);
            interactables.add(portal);
        }//end if
        else {
            System.out.println("No valid position found for portal spawn.");
        }//end else
    }//end spawnPortal

    /**
     * Places the player at a valid location found by findRandomGroundTilePosition.
     */
    private void setRandomPlayerPosition(){
        Point playerPosition = findRandomGroundTilePosition(this.tileMap);
        if (playerPosition != null) {
            float tileSize = this.tileMap.getTileWidth();
            float px = playerPosition.x * tileSize;
            float py = playerPosition.y * this.tileMap.getTileHeight();

            this.player.setPosition(px, py);
        }//end if
        else {
            System.out.println("No valid position found for portal spawn.");
        }//end else
    }//end setRandomPlayerPosition

    //<editor-fold desc = "METHODS: LOADING SCREEN" >
    //for everything that we need to load into the game add this function
    private void addLoadingRequirement(String text){
        this.totalLoadingSteps += 1;
        this.loadingMessages.add(text);
    }//end addLoadingRequirement

    //</editor-fold> METHODS: LOADING SCREEN

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        //ESC toggles between PLAY and PAUSE
        if (code == KeyEvent.VK_ESCAPE) {
            if (subState == SubState.PLAY) {
                subState = SubState.PAUSE;
            }//end if
            else if (subState == SubState.PAUSE) {
                subState = SubState.PLAY;
            }//end if else
        }//end if

        //take any input for loading screen
        if (subState == SubState.WAITING_TO_CONTINUE) {
            this.midiMusicPlayer.playMidi("sounds/music/gameplayMusic.mid", true);
            subState = SubState.PLAY;
        }//end if
    }//end keyPressed

    @Override
    public void keyReleased(KeyEvent e) {

    }//end keyReleased

    @Override
    public void mousePressed(MouseEvent e) {
        //take any input for loading screen
        if (subState == SubState.WAITING_TO_CONTINUE) {
            this.midiMusicPlayer.playMidi("sounds/music/gameplayMusic.mid", true);
            subState = SubState.PLAY;
            return;
        }//end if

        if (subState == SubState.PAUSE) {
            Point click = e.getPoint();
            if (resumeButton != null && resumeButton.isHovered(click)) {
                resumeButton.getOnClick().run();
            }//end if
            else if (quitButton != null && quitButton.isHovered(click)) {
                quitButton.getOnClick().run();
            }//end else if
        }//end if
    }//end mousePressed

    @Override
    public void mouseReleased(MouseEvent e) {

    }//end mouseReleased

    @Override
    public void mouseClicked(MouseEvent e) {

    }//end mouseClicked

    //TODO remove the static and implement this better
    public static ArrayList<SpriteExtended> getEntities(){
        return entities;
    }//end getEntities

}//end class
