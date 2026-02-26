package settings;

import game2D.Sprite;
import gameExtended2D.SpriteExtended;
import gameExtended2D.TileExtended;
import gameExtended2D.TileExtended.TileType;
import gameExtended2D.TileMapExtended;

import java.awt.*;
import java.util.ArrayList;

/**
 * Utility class for handling all sprite and tile-based collision logic.
 * <p>
 * Supports:
 * <ul>
 *   <li>Tile collisions (X and Y axis)</li>
 *   <li>Platform and ground interaction</li>
 *   <li>Bounding box and circular collision checks</li>
 *   <li>Precise collision detection and response between sprites</li>
 *   <li>Interaction-based collisions (e.g., portals)</li>
 * </ul>
 */
public class Collision {

    /**
     * Check and handles collisions with a tile map for the
     * given sprite 's'
     *
     * @param sprite The Sprite to check collisions for
     * @param tileMap The tile map to check
     * @param collidedTiles An array used for debug that will show the tiles that are colliding with the sprite/map
     */
    public static void collisionSpriteToTile(SpriteExtended sprite, TileMapExtended tileMap, ArrayList<TileExtended> collidedTiles) {
        //clear previous collided tiles
        collidedTiles.clear();

        //sprite size and position
        float spritePositionX = sprite.getX();
        float spritePositionY = sprite.getY();
        float spriteWidth = sprite.getWidth();
        float spriteHeight = sprite.getHeight();

        //tile size
        float tileWidth = tileMap.getTileWidth();
        float tileHeight = tileMap.getTileHeight();

        //-----------------X AXIS COLLISION-----------------------
        if (sprite.getVelocityX() != 0) {
            //where the sprite will be next frame
            float futureX = spritePositionX + sprite.getVelocityX();

            //use the midpoint of the sprite (avoids false positives at top/bottom edges)
            float midY = spritePositionY + spriteHeight / 2;

            //determine the tile the sprite is about to move into (left or right)
            int tileX;
            if (sprite.getVelocityX() > 0) { //Moving right
                //check the tile on the right edge
                tileX = (int)((futureX + spriteWidth) / tileWidth);
            }//end if
            else { //Moving left
                //check the tile on the left edge
                tileX = (int)(futureX / tileWidth);
            }//end else

            int tileY = (int)(midY / tileHeight); //does not change (y-axis one)


            //get the tile
            TileExtended tile = tileMap.getTile(tileX, tileY);

            //check if collision
            if (tile != null && tile.getType() != TileType.EMPTY) {
                pushSpriteX(sprite, tile, tileWidth);
                collidedTiles.add(tile);
            }//end if
        }//end if

        //-----------------Y AXIS COLLISION-----------------------
        if (sprite.getVelocityY() != 0) {
            //TODO might need to remove
            boolean isFalling = false;

            //predict vertical position next frame
            float futureY = spritePositionY + sprite.getVelocityY();

            //Use near-left and near-right edges of the sprite for ceiling/floor checks
            float leftX = spritePositionX + 2;             //Slightly inside left edge
            float rightX = spritePositionX + spriteWidth - 2;       //Slightly inside right edge

            //determine tile row for top or bottom edge depending on movement
            int tileY;
            if (sprite.getVelocityY() > 0) { //is falling
                //check bottom edge
                isFalling = true;
                tileY = (int)((futureY + spriteHeight) / tileHeight);
            }//end if
            else { //is jumping
                //check top edge
                tileY = (int)(futureY / tileHeight);
            }//end else


            //which tiles we're about to collide with horizontally
            int xtileLeft = (int) (leftX / tileWidth);
            int xtileRight = (int) (rightX / tileWidth);

            //get tiles at top or bottom corners
            TileExtended tileLeft = tileMap.getTile(xtileLeft, tileY);
            TileExtended tileRight = tileMap.getTile(xtileRight, tileY);

            //if there's a solid tile at either corner, resolve the vertical collision
            if (tileLeft != null && tileLeft.getType() != TileExtended.TileType.EMPTY) {
                pushSpriteY(sprite, tileLeft, tileHeight);
                collidedTiles.add(tileLeft);

                //Check if is a colidable entity
                collidableEntitySetStates(sprite, tileLeft);
            }//end if
            else if (tileRight != null && tileRight.getType() != TileExtended.TileType.EMPTY) {
                pushSpriteY(sprite, tileRight, tileHeight);
                collidedTiles.add(tileRight);

                //Check if is a colidable entity
                collidableEntitySetStates(sprite, tileRight);
            }//end else if
            else if (tileRight != null || tileLeft != null){
                if (tileRight.getType() == TileExtended.TileType.EMPTY && tileLeft.getType() == TileExtended.TileType.EMPTY){
                    if (isFalling){
                        collidableEntitySetStates(sprite, tileRight);
                    }//end if
                }//end if
            }//end else if

        }//end if
    }//end collisionSpriteToTile

    /**
     * Resolves horizontal (X-axis) collisions between a sprite and a solid tile.
     * Pushes the sprite to the appropriate side of the tile and stops horizontal velocity.
     *
     * @param sprite     the sprite being moved
     * @param tile       the tile that the sprite collided with
     * @param tileWidth  the width of the tile
     */
    private static void pushSpriteX(SpriteExtended sprite, TileExtended tile, float tileWidth){
        float spritePositionX = sprite.getX(); //sprite's current X
        float spriteWidth = sprite.getWidth(); //sprite width
        float tilePositionX = tile.getXC(); //tile X

        if (sprite.getVelocityX() > 0) {
            //Moving right and hitting tile from the left
            if (spritePositionX + spriteWidth > tilePositionX) {
                //Snap sprite to the left edge of the tile
                sprite.setX(tilePositionX - spriteWidth);
                sprite.setVelocityX(0); //Stop horizontal movement
            }//end if
        } //end if
        else if (sprite.getVelocityX() < 0) {
            //Moving left and hitting tile from the right
            if (spritePositionX < tilePositionX + tileWidth) {
                //Snap sprite to the right edge of the tile
                sprite.setX(tilePositionX + tileWidth);
                sprite.setVelocityX(0); //Stop horizontal movement
            }//end if
        }//end else if
    }//end pushSpriteX


    /**
     * Resolves vertical (Y-axis) collisions between a sprite and a solid tile.
     * Adjusts the sprite's Y position and zeroes out Y velocity.
     *
     * @param sprite      the sprite being moved
     * @param tile        the tile that the sprite collided with
     * @param tileHeight  the height of the tile
     */
    private static void pushSpriteY(SpriteExtended sprite, TileExtended tile, float tileHeight){
        float spritePositionY = sprite.getY(); //sprite Y
        float spriteHeight = sprite.getHeight(); //sprite height
        float tilePositionY = tile.getYC();// tile Y

        if (sprite.getVelocityY() > 0) {
            //Falling downward onto the tile
            if (spritePositionY + spriteHeight > tilePositionY) {
                //Snap sprite on top of the tile
                sprite.setY(tilePositionY - spriteHeight);
                sprite.setVelocityY(0); //Stop falling
            }//end if
        }//end if
        else if (sprite.getVelocityY() < 0) {
            //Jumping upward into the tile (ceiling hit)
            if (spritePositionY < tilePositionY + tileHeight) {
                //Snap sprite to bottom of the ceiling tile
                sprite.setY(tilePositionY + tileHeight);
                sprite.setVelocityY(0); //Stop jumping
            }//end if
        }//end else if
    }//end pushSpriteY

    /**
     * Assigns the correct grounded/platform/falling state to a {@link CollidableEntity}
     * based on the type of tile it's colliding with.
     *
     * @param sprite the sprite implementing CollidableEntity
     * @param tile   the tile the sprite is interacting with
     */
    private static void collidableEntitySetStates(SpriteExtended sprite, TileExtended tile){
        //Check if is a colidable entity
        if (sprite instanceof CollidableEntity){
            if (tile.getType() == TileType.PLATFORM){
                ((CollidableEntity) sprite).setOnPlatform();
            }//end if
            else if (tile.getType() == TileType.GROUND) {
                ((CollidableEntity) sprite).setOnGround();
            }//end else if
            else if (tile.getType() == TileType.EMPTY) {
                ((CollidableEntity) sprite).setFalling();
            }//end else if
        }//end if
    }//end

    /**
     * Draws rectangles around tiles currently colliding with the sprite.
     * Used primarily for debugging visual collisions.
     *
     * @param g              the graphics context
     * @param map            the tile map
     * @param xOffset        the x offset for camera or viewport
     * @param yOffset        the y offset for camera or viewport
     * @param collidedTiles  the list of tiles currently in collision
     */
    public static void drawCollidedTiles(Graphics2D g, TileMapExtended map, int xOffset, int yOffset, ArrayList<TileExtended> collidedTiles) {
        if (!collidedTiles.isEmpty()) {
            int tileWidth = map.getTileWidth();
            int tileHeight = map.getTileHeight();

            g.setColor(Color.blue);
            for (TileExtended t : collidedTiles) {
                g.drawRect(t.getXC()+xOffset, t.getYC()+yOffset, tileWidth, tileHeight);
            }//end for
        }//end if
    }//end drawCollidedTiles

    /**
     * Performs a basic axis-aligned bounding box collision check between two sprites.
     *
     * @param a the first sprite
     * @param b the second sprite
     * @return true if the bounding boxes intersect; false otherwise
     */
    private static boolean boundingBoxCollisionSpriteToSprite(Sprite a, Sprite b) {
        return a.getX() < b.getX() + b.getWidth() &&
                a.getX() + a.getWidth() > b.getX() &&
                a.getY() < b.getY() + b.getHeight() &&
                a.getY() + a.getHeight() > b.getY();
    }//end boundingBoxCollision

    /**
     * Performs a circular collision check between two sprites using their center points and radius.
     *
     * @param a the first sprite
     * @param b the second sprite
     * @return true if the circle bounds of both sprites intersect; false otherwise
     */
    private static boolean boundingCircleCollisionSpriteToSprite(Sprite a, Sprite b) {
        //Get the center point of sprite A
        float centerAx = a.getX() + a.getWidth() / 2f;
        float centerAy = a.getY() + a.getHeight() / 2f;

        //Get the center point of sprite B
        float centerBx = b.getX() + b.getWidth() / 2f;
        float centerBy = b.getY() + b.getHeight() / 2f;

        //Calculate the distance between the two centers on the X and Y axis
        float deltaX = centerAx - centerBx;
        float deltaY = centerAy - centerBy;

        //Calculate the squared distance (avoids costly square root)
        float distanceSquared = deltaX * deltaX + deltaY * deltaY;

        //Get the combined radius of both sprites
        float combinedRadius = a.getRadius() + b.getRadius();

        //Compare squared distance with squared radius sum
        //If distance between centers is less than combined radii, they are colliding
        return distanceSquared < (combinedRadius * combinedRadius);
    }//end boundingCircleCollision


    /**
     * Combines bounding box and circle collision detection for a more accurate check.
     *
     * @param a the first sprite
     * @param b the second sprite
     * @return true if both AABB and circle collisions occur
     */
    public static boolean preciseCollisionSpriteToSprite(Sprite a, Sprite b) {
        return boundingBoxCollisionSpriteToSprite(a, b) && boundingCircleCollisionSpriteToSprite(a, b);
    }//end preciseCollision

    /**
     * Resolves a collision between two sprites with position correction.
     * Handles vertical landings (bouncing), nudging, and horizontal pushback.
     *
     * @param a the active/moving sprite
     * @param b the static or collided-into sprite
     */
    public static void collisionSpriteToSprite(Sprite a, Sprite b) {
        if (!preciseCollisionSpriteToSprite(a, b)) return;

        //Positions and dimensions
        float ax = a.getX();
        float ay = a.getY();
        float aw = a.getWidth();
        float ah = a.getHeight();

        float bx = b.getX();
        float by = b.getY();
        float bw = b.getWidth();
        float bh = b.getHeight();

        //Center differences
        float dx = (ax + aw / 2f) - (bx + bw / 2f);
        float dy = (ay + ah / 2f) - (by + bh / 2f);

        //Amount of overlap in each direction
        float overlapX = (aw / 2f + bw / 2f) - Math.abs(dx);
        float overlapY = (ah / 2f + bh / 2f) - Math.abs(dy);

        final float OVERLAP_THRESHOLD = 0.5f; //Small buffer to break ties

        if (overlapX > 0 && overlapY > 0) {
            boolean isVertical = !(Math.abs(overlapX - overlapY) > OVERLAP_THRESHOLD) || overlapY < overlapX;

            if (isVertical) {
                if (dy < 0) {
                    //Sprite 'a' landed on top of sprite 'b'
                    float aBottom = ay + ah;
                    if (aBottom > by) {
                        //Snap a on top of b
                        a.setY(by - ah);

                        //Apply bounce
                        a.setVelocityY(-1.2f); //Soft upward push

                        //Apply gentle nudge to edge for natural fall
                        float nudge = 1.2f;
                        float vx = a.getVelocityX();
                        if (vx != 0) {
                            //Continue in movement direction
                            a.setX(ax + (vx > 0 ? nudge : -nudge));
                        }//end if
                        else {
                            //If standing still, nudge randomly
                            a.setX(ax + (Math.random() > 0.5 ? nudge : -nudge));
                        }//end else
                    }//end if
                }//end if
                else {
                    //Sprite 'b' is on top of sprite 'a'
                    float bBottom = by + bh;
                    if (bBottom > ay) {
                        b.setY(ay - bh);          //Snap b on top of a
                        b.setVelocityY(1.5f);     //Force it to fall
                        b.setX(bx + (dx < 0 ? 1.2f : -1.2f)); //Nudge off
                    }//end if
                }//end else
            }//end if
            else {
                //Horizontal collision
                if (dx > 0) {
                    a.setX(ax + overlapX); //Push right
                }//end if
                else {
                    a.setX(ax - overlapX); //Push left
                }//end else
                a.setVelocityX(0); //Stop horizontal movement
            }//end else
        }//end if

    }//end collisionSpriteToSprite

    /**
     * Checks for interaction-type collision (e.g. portals, etc.)
     * between a player sprite and a list of interactables.
     *
     * @param player The player or sprite that is interacting.
     * @param interactables A list of interactable sprites to check collision against.
     * @return The first interactable collided with, or null if none.
     */
    public static SpriteExtended checkInteractionCollision(SpriteExtended player, java.util.List<SpriteExtended> interactables) {
        for (SpriteExtended interactable : interactables) {
            if (preciseCollisionSpriteToSprite(player, interactable)) {
                return interactable;
            }//end if
        }//end for
        return null;
    }//end checkInteractionCollision

}//end class
