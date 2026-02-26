package interactables;

import entity.player.Player;
//TODO set up the class for objects like the portal and such

public interface Interactable {
    void onPlayerCollision(Player player);

}//end interface
