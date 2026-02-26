package settings;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Handles player keyboard input using Java's {@link KeyListener}.
 * <p>
 * Tracks key states for movement, combat, debug toggles, and testing inputs.
 * <p>
 * Intended to be passed to player or input-aware objects for real-time interaction.
 */
public class KeyHandler implements KeyListener {
    //arrow keys
    public boolean upPressed, leftPressed, downPressed, rightPressed;

    //TODO testCastingPressed is just for testing, remove when finished
    public boolean physicalAttackPressed, testCastingPressed;
    public boolean hurt, die;
    //debug
    public boolean debugMode;

    public boolean noButtonPressed(){
        return !upPressed && !leftPressed && !downPressed && !rightPressed && !physicalAttackPressed && !testCastingPressed;
    }//end noButtonPressed method

    @Override
    public void keyTyped(KeyEvent e) {
    }//end keyTyped

    //TODO change this to use a use case instead, as it is more tidy
    @Override
    public void keyPressed(KeyEvent e)
    {
        int code = e.getKeyCode(); //return the number of the key that has been pressed

        //CHECK WHAT KEYS HAVE BEEN PRESSED
        if(code == KeyEvent.VK_UP) //UP ARROW KEY
        {
            this.upPressed = true;
        }//end if
        if(code == KeyEvent.VK_LEFT) //LEFT ARROW KEY
        {
            this.leftPressed = true;
        }//end if
        if(code == KeyEvent.VK_DOWN) //DOWN ARROW KEY
        {
            this.downPressed = true;
        }//end if
        if(code == KeyEvent.VK_RIGHT) //RIGHT ARROW KEY
        {
            this.rightPressed = true;
        }//end if
        if(code == KeyEvent.VK_Z) {
            this.physicalAttackPressed = true;
        }//end if
        if(code == KeyEvent.VK_X) {
            this.testCastingPressed = true;
        }//end if
        if(code == KeyEvent.VK_NUMPAD1){
            this.hurt = true;
        }
        if(code == KeyEvent.VK_NUMPAD2){
            this.die = true;
        }
        if(code == KeyEvent.VK_F11){
            if (!debugMode) {
                Settings.setDebugMode(!Settings.getDebugMode());
                debugMode = true;
                System.out.println("Debug: " + Settings.getDebugMode());
            }//end if
        }//end if
    }//end keyPressed

    @Override
    public void keyReleased(KeyEvent e)
    {
        int code = e.getKeyCode(); //return the number of the key that has been pressed

        //CHECK WHAT KEYS HAVE BEEN PRESSED
        if(code == KeyEvent.VK_UP) //UP ARROW KEY
        {
            this.upPressed = false;
        }//end if
        if(code == KeyEvent.VK_LEFT) //LEFT ARROW KEY
        {
            this.leftPressed = false;
        }//end if
        if(code == KeyEvent.VK_DOWN) //DOWN ARROW KEY
        {
            this.downPressed = false;
        }//end if
        if(code == KeyEvent.VK_RIGHT) //RIGHT ARROW KEY
        {
            this.rightPressed = false;
        }//end if
        if(code == KeyEvent.VK_Z)
        {
            this.physicalAttackPressed = false;
        }//end if
        if(code == KeyEvent.VK_X)
        {
            this.testCastingPressed = false;
        }//end if
        if(code == KeyEvent.VK_NUMPAD1){
            this.hurt = false;
        }
        if(code == KeyEvent.VK_NUMPAD2){
            this.die = false;
        }
        if(code == KeyEvent.VK_F11){
            this.debugMode = false;
        }//end if
    }//end keyReleased

}//end class
