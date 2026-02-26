package entity.player;

import fx.EffectsManager;
import fx.FloatingText;
import fx.effects.PhysicalAttackFx;
import gameExtended2D.AnimationExtended;
import gameExtended2D.SpriteExtended;
import settings.CollidableEntity;
import settings.KeyHandler;
import settings.Settings;
import sound.SoundManager;

import java.awt.*;
import java.util.ArrayList;

/**
 * Represents the player character in the game world.
 * Handles movement, jumping, attacking, experience leveling,
 * animations, and interactions with the environment and other entities.
 */
public class Player extends SpriteExtended implements CollidableEntity {

    // <editor-fold desc= "INSTANCE VARIABLES">
    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<FloatingText> floatingTexts = new ArrayList<>();

    // <editor-fold desc= "Stats">
    private float movementSpeed = 0.5f;
    private float jumpStrength = 2.5f + Settings.getGravity(); // Initial upward velocity
    private float healthMax = 10;
    private float healthCurrent = this.healthMax;
    private float manaMax = 10;
    private float manaCurrent = this.manaMax;
    private float physicalDamage = 1;
    private float magicalDamage = 1;

    //levels
    private int level;
    private float expCurrent;
    private float expRequired;
    private float expBase = 10;

    //</editor-fold> Stats

    // <editor-fold desc= "Systems">
    private KeyHandler keyHandler;
    private EffectsManager effectsManager;

    //</editor-fold> Systems

    // <editor-fold desc= "Jumping">
    private long lastJumpTime = 0;
    private long jumpCooldown = 300; // in milliseconds

    public KeyHandler getKeyHandler() {
        return keyHandler;
    }

    // </editor-fold> Jumping

    // <editor-fold desc= "States">
    public enum Directions { LEFT, RIGHT }
    private Directions direction = Directions.RIGHT;
    boolean isOnPlatform = false;
    boolean isOnGround = false;
    private boolean canChangeAnimation = true;
    private boolean isJumping = false;
    private boolean isFalling = false;
    private boolean isAttacking = false;
    private boolean isDying = false;
    //</editor-fold> States

    // <editor-fold desc="Animations and Sprites">
    private String spriteSheetPath = "images/Player/Sword/2/";
    private int spriteGeneralDuration = 150; //TO-DO create different speeds, for attacks, movement and actions in general
    private int spriteMovementDuration = 150;
    private int spriteJumpDuration = 50;
    private int spriteLightAttackDuration = 100; //TODO rename this to cast
    private int spriteHeavyAttackDuration = 100;

    private AnimationExtended animationIdleRight, animationIdleLeft;
    private AnimationExtended animationWalkRight, animationWalkLeft;
    private AnimationExtended animationJumpRight, animationJumpLeft, animationFallingRight, animationFallingLeft;
    private AnimationExtended animationDamagedRight, animationDamagedLeft, animationDyingRight, animationDyingLeft;
    private AnimationExtended animationPhysicalAttackRight, animationPhysicalAttackLeft;
    private AnimationExtended animationCastingRight, animationCastingLeft;
    // </editor-fold> Animations and Sprites

    //</editor-fold> INSTANCE VARIABLES

    /**
     * Constructs a new Player object, initializing the key handler,
     * effects system, animations, and default stats.
     *
     * @param keyHandler the input handler for controlling the player
     */
    public Player(KeyHandler keyHandler){
        super(); //call the parent's class constructor
        this.keyHandler = keyHandler; //assign the keyHandler
        this.effectsManager = new EffectsManager();
        setAnimationsAndSprites();
        setStats();
    }//end constructor

    /**
     * Initializes all player animations from sprite sheets, including idle, walk,
     * jump, fall, damage, death, physical attack, and casting states.
     * Sets the initial animation to idle right.
     */
    private void setAnimationsAndSprites(){
        AnimationExtended animationTemplate = new AnimationExtended();

        //IDLE
        this.animationIdleRight = animationTemplate.createAnimation(this.spriteSheetPath + "Idle.png",4,1,this.spriteGeneralDuration,0,4,false,true);
        this.animationIdleLeft = animationTemplate.createAnimation(this.spriteSheetPath + "Idle.png",4,1,this.spriteGeneralDuration,0,4,true,true);

        //WALK
        this.animationWalkRight = animationTemplate.createAnimation(this.spriteSheetPath + "Walk.png",6,1,this.spriteGeneralDuration,0,6,false,true);
        this.animationWalkLeft = animationTemplate.createAnimation(this.spriteSheetPath + "Walk.png",6,1,this.spriteGeneralDuration,0,6,true,true);

        //JUMP
        this.animationJumpRight = animationTemplate.createAnimation(this.spriteSheetPath + "Jump.png",8,1,this.spriteJumpDuration,0,8,false,false);
        this.animationJumpLeft = animationTemplate.createAnimation(this.spriteSheetPath + "Jump.png",8,1,this.spriteJumpDuration,0,8,true,false);

        //FALLING
        this.animationFallingRight = animationTemplate.createAnimation(this.spriteSheetPath + "FallAttack.png",6,1, 2L,1,2,false,false);
        this.animationFallingLeft = animationTemplate.createAnimation(this.spriteSheetPath + "FallAttack.png",6,1,2L,1,2,true,false);

        //DAMAGED
        this.animationDamagedRight = animationTemplate.createAnimation(this.spriteSheetPath + "Hurt.png",4,1,this.spriteGeneralDuration,0,4,false,false);
        this.animationDamagedLeft = animationTemplate.createAnimation(this.spriteSheetPath + "Hurt.png",4,1,this.spriteGeneralDuration,0,4,true,false);

        //DYING
        this.animationDyingRight = animationTemplate.createAnimation(this.spriteSheetPath + "Death.png",8,1,this.spriteGeneralDuration,0,8,false,false);
        this.animationDyingLeft = animationTemplate.createAnimation(this.spriteSheetPath + "Death.png",8,1,this.spriteGeneralDuration,0,8,true,false);

        //ATTACKS
        this.animationPhysicalAttackRight = animationTemplate.createAnimation(this.spriteSheetPath + "Attack2.png",6,1,this.spriteHeavyAttackDuration,0,6,false,false);
        this.animationPhysicalAttackLeft = animationTemplate.createAnimation(this.spriteSheetPath + "Attack2.png",6,1,this.spriteHeavyAttackDuration,0,6,true,false);

        //CASTING
        this.animationCastingRight = animationTemplate.createAnimation(this.spriteSheetPath + "Attack1.png",6,1,this.spriteLightAttackDuration,0,6,false,false);
        this.animationCastingLeft = animationTemplate.createAnimation(this.spriteSheetPath + "Attack1.png",6,1,this.spriteLightAttackDuration,0,6,true,false);

        //set the starting animation which is idle
        changeAnimation(this.animationIdleRight);

    }//end setAnimationsAndSprites method

    /**
     * Initializes player stats related to leveling, such as level, current experience,
     * and required experience to level up.
     */
    private void setStats(){
        //TODO add health and attack too
        this.level = 1;
        this.expCurrent = 0;
        this.expRequired = 2;
    }//end setStats

    /**
     * Changes the player's current animation if allowed. Non-looping animations are started explicitly.
     *
     * @param animation the animation to switch to
     */
    private void changeAnimation(AnimationExtended animation){
        if (canChangeAnimation){
            super.setAnimation(animation);
            //if it doesn't play if it's not looped but also doesn't play if you just start it for all
            if(!animation.getLoop()){
                super.getAnimation().start();
            }//end if
            super.playAnimation();
        }//end if
    }//end changeAnimation method

    /**
     * Updates the current animation state and transitions based on player conditions
     * like jumping, falling, idling, and attacking.
     *
     * @param elapsedTime the time elapsed since the last frame update
     */
    private void updateAnimation(long elapsedTime) {
        super.update(elapsedTime);

        //Reset attack state when animation finishes
        if (isAttacking && getAnimation().hasLooped()) {
            isAttacking = false;
            canChangeAnimation = true;
        }//end if

        //If animation is done and no other actions are happening, revert to idle
        if (!isJumping && !isFalling && !isAttacking && getVelocityX() == 0) {
            changeAnimation(direction == Directions.LEFT ? animationIdleLeft : animationIdleRight);
        }//end if

        //check if an animation finished looping
        if (!canChangeAnimation && !getAnimation().getLoop() && getAnimation().hasLooped()){
            canChangeAnimation = true;
        }//end if

        //jumping logic
        if (isJumping){
            if (getVelocityY() >= 0) {
                isJumping = false; //We're now falling, not jumping
                isFalling = true;
            }//end if

            if (getAnimation().hasLooped()){
                canChangeAnimation = true;
            }//end if
        }//end if

        if (isFalling){
            changeAnimation(direction == Directions.LEFT ? animationFallingLeft : animationFallingRight);
        }//end if

    }//end updateAnimation

    /**
     * Handles keyboard input for movement, jumping, attacking, and testing casting.
     * Prevents input when the player is attacking or dying.
     */
    private void keyControls() {
        //TODO test if it really is leaving the function when I am running this
        // if return isn't letting us leave the function then this is why it's not working

        if(isDying){
            return;
        }//end if

        if (isAttacking) {
            return; // Prevent movement during attack
        }//end if
        if (!canChangeAnimation){
            return;
        }//end if

        //<editor-fold desc= "LEFT & RIGHT MOVEMENT">
        if (keyHandler.leftPressed) {
            direction = Directions.LEFT;
            setVelocityX(-movementSpeed);
            shiftX(getVelocityX());
            if (!isFalling && !isJumping){
                changeAnimation(animationWalkLeft);
            }//end if
        }//end if
        else if (keyHandler.rightPressed) {
            direction = Directions.RIGHT;
            setVelocityX(movementSpeed);
            shiftX(getVelocityX());
            if (!isFalling && !isJumping){
                changeAnimation(animationWalkRight);
            }//end if
        }//end else if
        else {
            setVelocityX(0);
        }//end else

        //</editor-fold> LEFT & RIGHT MOVEMENT

        //<editor-fold desc= "JUMPING">
        if (keyHandler.upPressed && (isOnGround || isOnPlatform)) {
            long now = System.currentTimeMillis();
            if((now - lastJumpTime) > jumpCooldown){
                setY(getY() - 2);
                setJumping();
                this.lastJumpTime = now;
                if (this.direction == Directions.LEFT){
                    changeAnimation(this.animationJumpLeft);
                }//end if
                else if (this.direction == Directions.RIGHT){
                    changeAnimation(this.animationJumpRight);
                }//end else if
            }//end if
        }//end if

        //</editor-fold> JUMPING

        //<editor-fold desc= "PHYSICAL ATTACK">
        if (keyHandler.physicalAttackPressed) {
            if ((!isOnGround && !isOnPlatform) || isJumping || isFalling){
                return;
            }//end if
            isAttacking = true;
            setVelocityX(0); //stop moving
            SoundManager.playFilteredSound("slash");
            float offSet = 10f;
            if (direction == Directions.LEFT){
                changeAnimation(this.animationPhysicalAttackLeft);
                this.effectsManager.addEffect(new PhysicalAttackFx(getX()-offSet, getY(), this.spriteHeavyAttackDuration, this.direction, this, this.physicalDamage, this.magicalDamage));
            }//end if
            else if (direction == Directions.RIGHT){
                changeAnimation(this.animationPhysicalAttackRight);
                this.effectsManager.addEffect(new PhysicalAttackFx(getX()+offSet, getY(), this.spriteHeavyAttackDuration, this.direction, this, this.physicalDamage, this.magicalDamage));
            }//end else if
            canChangeAnimation = false;
        }//end if

        //</editor-fold> PHYSICAL ATTACK

        //<editor-fold desc= "Test casting Attack">
        //TODO remove it once done testing it
        if (keyHandler.testCastingPressed) {
            isAttacking = true;
            setVelocityX(0); //stop moving
            if (direction == Directions.LEFT){
                changeAnimation(this.animationCastingLeft);
            }//end if
            else if (direction == Directions.RIGHT){
                changeAnimation(this.animationCastingRight);
            }//end else if
            canChangeAnimation = false;
        }//end if

        //</editor-fold> Test casting Attack

    }//end keyControls

    @Override
    public void setOnGround() {
        if(isFalling){
            this.canChangeAnimation = true;
            if(!this.isJumping ){
                SoundManager.playFilteredSound("landing");
            }
        }//end if
        this.isOnGround = true;
        this.isOnPlatform = false; //If on the ground, cannot be on a platform
        this.isFalling = false;
    }//end setOnGround

    @Override
    public void setOnPlatform() {
        if(isFalling){
            this.canChangeAnimation = true;
            if(!this.isJumping){
                SoundManager.playFilteredSound("landing");
            }//end if
        }//end if
        this.isOnPlatform = true;
        this.isOnGround = false; //If on a platform, cannot be on the ground
        this.isFalling = false;

    }//end setOnPlatform

    @Override
    public void setFalling() {
        this.isFalling = true;
        this.isOnGround = false;
        this.isOnPlatform = false;
    }//end setFalling

    @Override
    public void setJumping() {
        if (!isJumping && !isAttacking){
            SoundManager.playSound("jump");
            this.isJumping = true;
            this.isOnGround = false;
            this.isOnPlatform = false;
            this.isFalling = false;
            this.canChangeAnimation = false;

            //Only apply jump force ONCE
            if (getVelocityY() >= 0) {
                setVelocityY(-jumpStrength);
            }//end if

            //DEBUG
            if(Settings.getDebugMode()){
                System.out.println("Player is JUMPING");
            }//end if
        }//end if
    }//end setJumping

    /**
     * Called every frame to update animation, key input, active effects,
     * and floating text elements.
     *
     * @param elapsedTime the time elapsed since the last update
     */
    public void update(long elapsedTime){
        // Update animation
        updateAnimation(elapsedTime);
        //update the keys
        keyControls();
        //update fx
        this.effectsManager.updateEffects(elapsedTime);

        //floating text
        floatingTexts.removeIf(FloatingText::isExpired);
        for (FloatingText ft : floatingTexts) {
            ft.update(elapsedTime);
        }//end for loop
    }//end update method

    /**
     * Draws all visual effects (such as attacks) and floating texts associated with the player.
     *
     * @param g the graphics context
     * @param x the horizontal screen offset
     * @param y the vertical screen offset
     */
    public void drawEffects(Graphics2D g, int x, int y){
        this.effectsManager.drawWithOffSets(g, x, y);

        for (FloatingText ft : floatingTexts) {
            ft.draw(g, x, y);
        }//end for loop
    }//end drawEffects

    /**
     * Applies physical and magical damage to the player, shows floating damage text,
     * plays hurt animation, and handles player death.
     *
     * @param physicalDamage the amount of physical damage taken
     * @param magicDamage    the amount of magical damage taken
     */
    public void takeDamage(float physicalDamage, float magicDamage){
        float totalDamage = 0;

        //TODO play hit sound
        //Ensure damage values aren't negative
        if (physicalDamage > 0) {
            totalDamage += physicalDamage;
        }//end if

        if (magicDamage > 0) {
            totalDamage += magicDamage;
        }//end if

        //If we are invincible, set damage to 0, since you know
        if(Settings.getIsPlayerInvincible()){
            totalDamage = 0;
        }//end if

        //Apply damage to current health
        this.healthCurrent -= totalDamage;

        //Show floating number
        addFloatingNumber(totalDamage, Color.RED);

        //Play hurt animation
        if (direction == Directions.LEFT) {
            changeAnimation(this.animationDamagedLeft);
        } else {
            changeAnimation(this.animationDamagedRight);
        }//end if else

        //Check if the player has died
        if (this.healthCurrent <= 0) {
            this.healthCurrent = 0; //Clamp to zero
            if (this.direction == Directions.LEFT){
                this.changeAnimation(this.animationDyingLeft);
            }
            else{
                this.changeAnimation(this.animationDyingRight);
            }
            this.canChangeAnimation = false;
            this.isDying = true;
        }//end if
    }//end takeDamage

    /**
     * Adds experience points to the player and handles level-up logic.
     * Displays floating text and plays level-up sound.
     *
     * @param amount the amount of experience to add
     */
    public void addExp(float amount){
        addFloatingText(amount+"exp", Color.CYAN);
        this.expCurrent += amount;
        while (this.expCurrent >= this.expRequired){
            floatingTexts.add(FloatingText.createLevelUpText(getX() + getWidth() / 2f, getY() - 40));
            SoundManager.playSound("levelUp");
            this.level++; //increase the level
            this.expCurrent -= this.expRequired; //take away the experience needed to level up
            this.expRequired = this.level * this.expBase;
        }//end while loop
    }//end addExp

    /**
     * Adds a floating text message above the player's head.
     *
     * @param msg   the message to display
     * @param color the color of the text
     */
    public void addFloatingText(String msg, Color color) {
        floatingTexts.add(new FloatingText(msg, getX() + getWidth() / 2f, getY() - 10, color));
    }//end addFloatingText

    /**
     * Adds a floating number (such as damage or healing) above the player.
     *
     * @param number the value to display
     * @param color  the color of the number
     */
    public void addFloatingNumber(float number, Color color) {
        floatingTexts.add(new FloatingText(number, getX() + getWidth() / 2f, getY() - 10, color));
    }//end addFloatingNumber

    //GETTERS AND SETTERS

    public float getHealthMax() {
        return healthMax;
    }//end getHealthMax

    public void setHealthMax(float healthMax) {
        this.healthMax = healthMax;
    }//end setHealthMax

    public float getHealthCurrent() {
        return healthCurrent;
    }//end getHealthCurrent

    public void setHealthCurrent(float healthCurrent) {
        this.healthCurrent = healthCurrent;
    }//end setHealthCurrent

    public void setKeyHandler(KeyHandler keyHandler) {
        this.keyHandler = keyHandler;
    }//end setKeyHandler

    public int getLevel() {
        return level;
    }//end getLevel

    public void setLevel(int level) {
        this.level = level;
    }//end setLevel

    public float getExpCurrent() {
        return expCurrent;
    }//end getExpCurrent

    public void setExpCurrent(float expCurrent) {
        this.expCurrent = expCurrent;
    }//end setExpCurrent

    public float getExpRequired() {
        return expRequired;
    }//end getExpRequired

    public void setExpRequired(float expRequired) {
        this.expRequired = expRequired;
    }//end setExpRequired

    public float getManaMax() {
        return manaMax;
    }//end getManaMax

    public void setManaMax(float manaMax) {
        this.manaMax = manaMax;
    }//end setManaMax

    public float getManaCurrent() {
        return manaCurrent;
    }//end getManaCurrent

    public void setManaCurrent(float manaCurrent) {
        this.manaCurrent = manaCurrent;
    }//end setManaCurrent

    public boolean getIsDying() {
        return isDying;
    }//end getIsDying

    public void setDying(boolean dying) {
        isDying = dying;
    }//end setDying

}//end class
