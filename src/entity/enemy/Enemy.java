package entity.enemy;

import entity.player.Player;
import fx.FloatingText;
import game2D.Animation;
import gameExtended2D.AnimationExtended;
import gameExtended2D.SpriteExtended;
import gameExtended2D.TileExtended;
import gameExtended2D.TileMapExtended;
import settings.Collision;
import settings.Settings;

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents an abstract enemy entity in the game world.
 * <p>
 * This class defines core behavior and properties for any enemy,
 * including animations, health, damage values, attack logic,
 * detection and movement behavior, and interaction with the player.
 * <p>
 * Concrete enemy types (like {@link BasicEnemy}) must extend this class
 * and provide their own implementation for {@link #setUpAnimations()}.
 */
public abstract class Enemy extends SpriteExtended {
    //Stats
    private float movementSpeed = 0.05f;
    private float healthCurrent;
    private float healthMax;
    private float physicalDamage;
    private float magicDamage;
    private int goldDropped;
    private float exp;

    //Animation
    protected AnimationExtended idleRight, idleLeft, walkRight, walkLeft, dyingRight, dyingLeft;
    protected AnimationExtended attackLeft, attackRight;

    //States
    protected enum EnemyState { PATROL, CHASE, ATTACK }
    protected EnemyState currentState = EnemyState.PATROL;
    protected enum Direction { LEFT, RIGHT }
    protected Direction direction;
    protected boolean isAttacking = false;
    protected boolean isDying = false;

    //Attacking
    protected long lastAttackTime = 0;
    protected long attackCooldown = 1000; //milliseconds between attacks

    //Resources
    protected TileMapExtended tileMap;
    protected Player player;
    private ArrayList<FloatingText> floatingTexts = new ArrayList<>();

    /**
     * Constructs an Enemy instance with the specified position,
     * world level, associated player, and tile map.
     * <p>
     * This constructor also initializes the enemy's stats (health, damage, gold, and experience)
     * based on the world level, sets the initial position and direction,
     * and calls the {@link #setUpAnimations()} method.
     *
     * @param x          the x-coordinate of the enemy's spawn location
     * @param y          the y-coordinate of the enemy's spawn location
     * @param worldLevel the level of the world (used to scale stats)
     * @param player     the player object this enemy will interact with
     * @param tileMap    the tile map for collision and movement logic
     */
    public Enemy(float x, float y, int worldLevel, Player player, TileMapExtended tileMap) {
        this.tileMap = tileMap;
        this.player = player;
        this.healthMax = calculateFloatLevelModifier(worldLevel, 3f);
        this.healthCurrent = healthMax; //start with full health
        this.physicalDamage = calculateFloatLevelModifier(worldLevel, 0.1f);
        this.magicDamage = calculateFloatLevelModifier(worldLevel, 0.1f);
        this.goldDropped = calculateIntLevelModifier(worldLevel, 0.5f);
        this.exp = calculateIntLevelModifier(worldLevel, 0.2f);
        setUpAnimations();
        setPosition(x, y);
        randomDirection();
    }//end constructor

    /**
     * Calculates a stat value scaled by the level and modifier.
     * Returns at least 1.
     *
     * @param level    the world level
     * @param modifier the stat modifier
     * @return the scaled integer value
     */
    private int calculateIntLevelModifier(int level, float modifier){
        int total = (int) Math.floor((float) level * modifier);
        return Math.max(total, 1);
    }//end calculateLevelModifier

    /**
     * Calculates a stat value scaled by the level and modifier.
     * Returns at least 1.0f.
     *
     * @param level    the world level
     * @param modifier the stat modifier
     * @return the scaled float value
     */
    private float calculateFloatLevelModifier(int level, float modifier){
        float total = level * modifier;
        if (total < 1){
            return 1;
        }//end if
        return total;
    }//end calculateLevelModifier

    /**
     * Chooses a random initial direction (left or right) for the enemy.
     */
    private void randomDirection(){
        // 50/50 chance to start facing left or right
        if (Math.random() < 0.5) {
            this.direction = Direction.LEFT;
        } else {
            this.direction = Enemy.Direction.RIGHT;
        }
    }//end randomDirection

    /**
     * Updates the enemy's state and animation based on the elapsed time and player position.
     * Handles state transitions (patrol, chase, attack), applies movement logic,
     * and updates floating text.
     *
     * @param elapsedTime the time elapsed since the last update (in milliseconds)
     */
    public void update(long elapsedTime) {
        if (isDying) {
            getAnimation().update(elapsedTime);
            return;
        }//end if
        checkBackstab();

        //TODO search all the enhanced switch statements and change them
        switch (currentState) {
            case PATROL -> {
                //Look for the player
                if (isPlayerInDetectionZone()) {
                    currentState = EnemyState.CHASE;
                    if(Settings.getDebugMode()){
                        System.out.println("Enemy spotted the player!");
                    }//end if
                }//end if

                // Movement
                patrolLogic();
                if (direction == Direction.LEFT) {
                    setVelocityX(-getMovementSpeed());
                    setAnimation(walkLeft);
                }//end if
                else {
                    setVelocityX(getMovementSpeed());
                    setAnimation(walkRight);
                }//end else
            }//end PATROL

            case CHASE -> {
                if (isAttacking) {
                    getAnimation().start();
                    isAttacking = false;
                }//end if
                chasePlayer();

                //Check for attack range
                if (isPlayerWithinAttackRange()) {
                    currentState = EnemyState.ATTACK;
                }//end if

                //Lost sight of player?
                if (!isPlayerInDetectionZone()) {
                    currentState = EnemyState.PATROL;
                    if(Settings.getDebugMode()){
                        System.out.println("Enemy lost the player...");
                    }//end if
                }//end if
            }//end CHASE

            case ATTACK -> {
                //Recheck range — if player escaped, chase again
                if (!isPlayerWithinAttackRange()) {
                    currentState = EnemyState.CHASE;
                    isAttacking = false;
                    getAnimation().start();
                    return;
                }//end if

                setVelocityX(0); //Stand still

                long now = System.currentTimeMillis();
                boolean readyToAttack = now - lastAttackTime >= attackCooldown;

                if (readyToAttack) {
                    if (!isAttacking) {
                        //Start slowed attack animation
                        setAnimation(direction == Direction.LEFT ? attackLeft : attackRight);
                        getAnimation().start();
                        isAttacking = true;
                    }//end if

                    getAnimation().update(elapsedTime);
                    if (getAnimation().hasLooped()) {
                        tryAttackPlayer();
                        isAttacking = false;
                        getAnimation().start();
                    }//end if
                }//end if
                else {
                    //Show idle animation while waiting for cooldown
                    setAnimation(direction == Direction.LEFT ? idleLeft : idleRight);
                    playAnimation(); //Make sure idle anim plays continuously
                    isAttacking = false;
                }//end else
            }//end ATTACK
        }//end switch
        super.update(elapsedTime);

        //floating text
        floatingTexts.removeIf(FloatingText::isExpired);
        for (FloatingText ft : floatingTexts) {
            ft.update(elapsedTime);
        }//end for loop

    }//end update

    /**
     * Checks whether the player is within the enemy's attack range.
     *
     * @return true if the player is within attack range, false otherwise
     */
    private boolean isPlayerWithinAttackRange() {
        if (player == null) return false;

        //Buffer size (how much larger the range box is)
        float buffer = 10f;

        Rectangle attackRangeBox = new Rectangle(
                (int) (getX() - buffer), (int) (getY() - buffer),
                (int) (getWidth() + buffer * 2), (int) (getHeight() + buffer * 2));

        Rectangle playerBox = new Rectangle(
                (int) player.getX(), (int) player.getY(),
                player.getWidth(), player.getHeight());

        return attackRangeBox.intersects(playerBox);
    }//end isPlayerWithinAttackRange

    /**
     * Handles patrol movement and direction change if a ledge or wall is detected.
     */
    private void patrolLogic() {
        float futureX = getX() + (direction == Direction.LEFT ? -2 : getWidth() + 2);
        float belowY = getY() + getHeight() + 1;
        float midY = getY() + getHeight() / 2f;

        int tileX = (int)(futureX / tileMap.getTileWidth());
        int tileBelowY = (int)(belowY / tileMap.getTileHeight());
        int tileWallY = (int)(midY / tileMap.getTileHeight());

        TileExtended tileBelow = tileMap.getTile(tileX, tileBelowY);
        TileExtended tileAhead = tileMap.getTile(tileX, tileWallY);

        boolean ledge = tileBelow == null || tileBelow.getType() == TileExtended.TileType.EMPTY;
        boolean wall = tileAhead != null && tileAhead.getType() != TileExtended.TileType.EMPTY;

        if (ledge || wall) {
            reverseDirection();
        }//end if
    }//end patrolLogic

    /**
     * Attempts to damage the player if within range and cooldown is ready.
     * Applies physical and magic damage.
     */
    public void tryAttackPlayer() {
        if (player == null || isDying) return;

        long now = System.currentTimeMillis();
        if (now - lastAttackTime >= attackCooldown) {
            player.takeDamage(getPhysicalDamage(), getMagicDamage());
            lastAttackTime = now;
            isAttacking = true;
        }//end if
    }//end tryAttackPlayer

    /**
     * Reverses the enemy's current walking direction.
     */
    private void reverseDirection() {
        direction = (direction == Direction.LEFT) ? Direction.RIGHT : Direction.LEFT;
    }//end reverseDirection

    /**
     * Checks if the player is within the enemy's detection zone.
     *
     * @return true if the player is within the detection area, false otherwise
     */
    private boolean isPlayerInDetectionZone() {
        if (player == null) return false;

        //Detection box parameters
        float detectionWidth = 400;
        float detectionHeight = getHeight() * 1.5f;

        float visionX = direction == Direction.LEFT ? getX() - detectionWidth : getX() + getWidth();
        float visionY = getY();

        Rectangle detectionBox = new Rectangle(
                (int) visionX, (int) visionY,
                (int) detectionWidth, (int) detectionHeight);

        Rectangle playerBox = new Rectangle(
                (int) player.getX(), (int) player.getY(),
                player.getWidth(), player.getHeight());

        return detectionBox.intersects(playerBox);
    }//end isPlayerInDetectionZone

    /**
     * Moves the enemy toward the player with a slight speed boost
     * and updates the animation accordingly.
     */
    private void chasePlayer() {
        if (player == null) return;

        if (player.getX() < getX()) {
            direction = Direction.LEFT;
            setVelocityX(-getMovementSpeed() * 5f); //Slight speed boost for chasing
            setAnimation(walkLeft);
        }//end if
        else {
            direction = Direction.RIGHT;
            setVelocityX(getMovementSpeed() * 5f);
            setAnimation(walkRight);
        }//end else
    }//end chasePlayer

    /**
     * Checks if the player has touched the enemy from behind.
     * If so, the enemy turns around and starts chasing.
     */
    private void checkBackstab() {
        if (player == null) return;

        boolean isTouching = Collision.preciseCollisionSpriteToSprite(this, player);

        if (!isTouching) return;

        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float enemyCenterX = getX() + getWidth() / 2f;

        boolean behind = (direction == Direction.LEFT && playerCenterX > enemyCenterX) ||
                (direction == Direction.RIGHT && playerCenterX < enemyCenterX);

        if (behind) {
            direction = (direction == Direction.LEFT) ? Direction.RIGHT : Direction.LEFT;
            currentState = EnemyState.CHASE;
        }//end if
    }//end checkBackstab

    /**
     * Reduces the enemy's health based on incoming physical and magic damage,
     * accounting for armor (not implemented yet).
     * Also displays floating damage text and triggers death if health reaches 0.
     *
     * @param physicalDmg the physical damage to apply
     * @param magicDmg    the magic damage to apply
     */
    public void takeDamage(float physicalDmg, float magicDmg) {
        //TODO this only makes sense if the player will have a penetrate armor stats
        // if time allows to implement the stat, uncomment them and initialise them
        //apply armor efficiency
//        float physicalArmor = this.physicalArmor * this.armorEfficiency;
//        float magicArmor = this.magicArmor * this.armorEfficiency;
        float physicalArmor = 0;
        float magicArmor = 0;

        //calculate the damage taken
        float reducedPhysicalDmg = Math.max(0, physicalDmg - physicalArmor);
        float reducedMagicDmg = Math.max(0, magicDmg - magicArmor);
        this.healthCurrent -= (reducedPhysicalDmg + reducedMagicDmg);
        addFloatingText((reducedPhysicalDmg + reducedMagicDmg), Color.white);

        //if health reached 0, die
        if (this.healthCurrent <= 0 && !isDying) {
            die();
        }//end if
    }//end takeDamage

    /**
     * Triggers the death state for the enemy, stopping movement and playing the death animation.
     * Awards experience to the player.
     */
    public void die() {
        isDying = true;
        setVelocity(0, 0);
        setAnimation(direction == Direction.LEFT ? dyingLeft : dyingRight);
        getAnimation().start();
        this.player.addExp(this.exp);
        this.floatingTexts.clear();
    }//end die

    /**
     * Checks whether the enemy has completed its death animation.
     *
     * @return true if the death animation has completed, false otherwise
     */
    public boolean checkIfDead(){
        return isDying && getAnimation().hasLooped();
    }//end checkIfDead

    /**
     * Draws the enemy sprite, its health bar, and any active floating texts.
     * Also draws the detection box if debug mode is enabled.
     *
     * @param g the graphics context to draw on
     */
    public void draw(Graphics2D g) {
        if (!isVisible()) return;

        drawTransformed(g); //Draw the enemy normally

        //Draw health bar above enemy
        if (!isDying && healthMax > 0) {
            int barWidth = 40;
            int barHeight = 6;
            int x = (int)(getX() + getWidth() / 2 - barWidth / 2 + getOffsetX());
            int y = (int)(getY() - 10 + getOffsetY());

            float healthRatio = Math.max(0, healthCurrent / healthMax);

            //Background bar
            g.setColor(Color.DARK_GRAY);
            g.fillRect(x, y, barWidth, barHeight);

            //Health amount
            g.setColor(Color.RED);
            g.fillRect(x, y, (int)(barWidth * healthRatio), barHeight);

            //Add border
            g.setColor(Color.BLACK);
            g.drawRect(x, y, barWidth, barHeight);
        }//end if

        //Floating text
        for (FloatingText ft : floatingTexts) {
            ft.draw(g, getOffsetX(), getOffsetY());
        }//end for loop

        //Debug mode
        if (Settings.getDebugMode()) {
            float detectionWidth = 400;
            float detectionHeight = getHeight() * 1.5f;

            float boxX = (direction == Direction.LEFT) ? getX() - detectionWidth : getX() + getWidth();

            float boxY = getY();

            g.setColor(Color.YELLOW);
            g.drawRect(
                    (int) (boxX + getOffsetX()), (int) (boxY + getOffsetY()),
                    (int) detectionWidth, (int) detectionHeight);
        }//end if
    }//end draw

    /**
     * Adds a floating damage text above the enemy.
     *
     * @param number the damage number to display
     * @param color  the color of the floating text
     */
    public void addFloatingText(float number, Color color) {
        if (!this.isDying) {
            floatingTexts.add(new FloatingText(number, getX() + getWidth() / 2f, getY() - 10, color));
        }//end if
    }//end addFloatingText

    /**
     * Set up the animations that the player will use
     * <p></p>
     * !!!You will require an idle, walk, dying and attack animation
     * <p></p>
     * !!!Always have {@link gameExtended2D.SpriteExtended#setAnimation(Animation)}
     * and {@link gameExtended2D.SpriteExtended#playAnimation()} at the end of the method.
     * <p></p>
     */
    protected abstract void setUpAnimations();

    // <editor-fold desc= "GETTERS AND SETTERS">
    public float getHealthCurrent() {
        return healthCurrent;
    }//end getHealthCurrent

    public void setHealthCurrent(float healthCurrent) {
        this.healthCurrent = healthCurrent;
    }//end setHealthCurrent

    public float getHealthMax() {
        return healthMax;
    }//end getHealthMax

    public void setHealthMax(float healthMax) {
        this.healthMax = healthMax;
    }//end setHealthMax

    public float getPhysicalDamage() {
        return physicalDamage;
    }//end getPhysicalDamage

    public void setPhysicalDamage(float physicalDamage) {
        this.physicalDamage = physicalDamage;
    }//end setPhysicalDamage

    public float getMagicDamage() {
        return magicDamage;
    }//end getMagicDamage

    public void setMagicDamage(float magicDamage) {
        this.magicDamage = magicDamage;
    }//end setMagicDamage

    public int getGoldDropped() {
        return goldDropped;
    }//end getGoldDropped

    public void setGoldDropped(int goldDropped) {
        this.goldDropped = goldDropped;
    }//end setGoldDropped

    public float getExp() {
        return exp;
    }//end getExp

    public void setExp(int exp) {
        this.exp = exp;
    }//end setExp

    public float getMovementSpeed() {
        return movementSpeed;
    }//end getMovementSpeed

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }//end setMovementSpeed


    //</editor-fold> GETTERS AND SETTERS

}//end class
