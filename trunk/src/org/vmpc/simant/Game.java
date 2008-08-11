/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vmpc.simant;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author svenni
 */
public class Game extends Canvas {

    /** The stragey that allows us to use accelerate page flipping */
    private BufferStrategy strategy;
    /** True if the game is currently "running", i.e. the game loop is looping */
    private boolean gameRunning = true;
    /** The list of all the entities that exist in our game */
    private ArrayList entities = new ArrayList();
    /** The list of entities that need to be removed from the game this loop */
    private ArrayList removeList = new ArrayList();
    /** The entity representing the player */
    private Entity ant;
    /** Test **/
    private Entity pig;
    /** The speed at which the player's ant should move (pixels/sec) */
    private double moveSpeed = 300;
    /** The time at which last fired a shot */
    private long lastFire = 0;
    /** The interval between our players shot (ms) */
    private long firingInterval = 500;
    /** The number of aliens left on the screen */
    private int alienCount;
    /** The message to display which waiting for a key press */
    private String message = "";
    /** True if we're holding up game play until a key has been pressed */
    private boolean waitingForKeyPress = true;
    /** True if the left cursor key is currently pressed */
    private boolean leftPressed = false;
    /** True if the right cursor key is currently pressed */
    private boolean rightPressed = false;
    /** True if we are firing */
    private boolean firePressed = false;
    /** True if game logic needs to be applied this loop, normally as a result of a game event */
    private boolean logicRequiredThisLoop = false;
    private double test = 0;
    private int canvasWidth = 800;
    private int canvasHeight = 600;

    public Game() {
        // create a frame to contain our game

        JFrame container = new JFrame("SimAnt");

        // get hold the content of the frame and set up the resolution of the game

        JPanel panel = (JPanel) container.getContentPane();
        panel.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        panel.setLayout(null);

        // setup our canvas size and put it into the content of the frame

        setBounds(0, 0, canvasWidth, canvasHeight);
        panel.add(this);

        // Tell AWT not to bother repainting our canvas since we're

        // going to do that our self in accelerated mode

        setIgnoreRepaint(true);

        // finally make the window visible 

        container.pack();
        container.setResizable(false);
        container.setVisible(true);

        // add a listener to respond to the user closing the window. If they

        // do we'd like to exit the game

        container.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // add a key input system (defined below) to our canvas

        // so we can respond to key pressed

        //addKeyListener(new KeyInputHandler());

        // request the focus so key events come to us

        requestFocus();

        // create the buffering strategy which will allow AWT

        // to manage our accelerated graphics

        createBufferStrategy(2);
        strategy = getBufferStrategy();

        // initialise the entities in our game so there's something

        // to see at startup

        initEntities();
    }

    public void gameLoop() {
        long lastLoopTime = System.currentTimeMillis();
        double lastFrameRateTime = System.currentTimeMillis();
        double lastFrameRate = 0;
        ArrayList<Long> frameRates = new ArrayList();
        double frameRate;
        double checkFrameRateEveryMilli = 250;
        double deltaFrameRate;
        long delta;
        // keep looping round til the game ends

        while (gameRunning) {


            //1: move ants

            //2: check for collisions
            //a: edges
            //off the screen, and in at the other side? or bounce?
            //b: other ants
            //to begin with i think we should let the ants run over each other, and not collide.
            //to achive this we need all ants to have an array of bitflags with one member pr. ant.
            //then when they hit each other the flag is set and the event-fucntion is triggered for each of the two ants. 
            //when the ants nolonger are touching, the flag is again set to 0.

            // work out how long its been since the last update, this

            // will be used to calculate how far the entities should

            // move this loop

            delta = System.currentTimeMillis() - lastLoopTime;
            deltaFrameRate = System.currentTimeMillis() - lastFrameRateTime;
            lastLoopTime = System.currentTimeMillis();

            // Get hold of a graphics context for the accelerated 

            // surface and blank it out

            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            
            // Set the graphics to use a bicbic filter
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            
            // Set the background color and fill the background
            g.setColor(Color.white);
            g.fillRect(0, 0, canvasWidth, canvasHeight);

            //Add a framerate to the list of frames if the delta is not 0
            if(delta > 0) {
                frameRates.add(1000 / delta);
            }
            //Check if it is about time to calculate the average framerate
            if (deltaFrameRate > checkFrameRateEveryMilli) {
                double frameRateTotal = 0;
                // Sum all measured framerates
                for (double aFrameRate : frameRates) {
                    frameRateTotal += aFrameRate;
                }
                //Get the average framerate
                frameRate = frameRateTotal / frameRates.size();
                //Get ready for new measurements
                frameRates.clear();
                lastFrameRate = frameRate;
                lastFrameRateTime = System.currentTimeMillis();

            } else {
                frameRate = lastFrameRate;
            }
            g.setColor(Color.black);

            g.drawString("FPS: " + frameRate, 2, 20);
            g.drawString("Coordinates: (" + ant.getX() + ", " + ant.getY() + ")", 2, 40);



            // if(ant.getX() > 300) {
            //    ant.setAngle(-0.8);
            //  }
            //ant.setAngleDegrees(this.test);
           // this.test++;
            // cycle round asking each entity to move itself

            //if (!waitingForKeyPress) {
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = (Entity) entities.get(i);
                entity.move(delta);
            }
            //}

            // cycle round drawing all the entities we have in the game

            for (int i = 0; i < entities.size(); i++) {
                Entity entity = (Entity) entities.get(i);
                entity.draw(g);
            }

            // brute force collisions, compare every entity against

            // every other entity. If any of them collide notify 

            // both entities that the collision has occured
/*
            for (int p = 0; p < entities.size(); p++) {
            for (int s = p + 1; s < entities.size(); s++) {
            Entity me = (Entity) entities.get(p);
            Entity him = (Entity) entities.get(s);
            if (me.collidesWith(him)) {
            me.collidedWith(him);
            him.collidedWith(me);
            }
            }
            }
             */
            // remove any entity that has been marked for clear up

            entities.removeAll(removeList);
            removeList.clear();

            // if a game event has indicated that game logic should

            // be resolved, cycle round every entity requesting that

            // their personal logic should be considered.

            if (logicRequiredThisLoop) {
                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = (Entity) entities.get(i);
                    entity.doLogic();
                }
            }

            logicRequiredThisLoop = false;


            // if we're waiting for an "any key" press then draw the 
            // current message 
/*
            if (waitingForKeyPress) {
            g.setColor(Color.white);
            g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
            g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key")) / 2, 300);
            }
             * 
             */
            // finally, we've completed drawing so clear up the graphics
            // and flip the buffer over
            g.dispose();
            strategy.show();
            // resolve the movement of the ant. First assume the ant 
            // isn't moving. If either cursor key is pressed then
            // update the movement appropraitely
        /*
            ant.setHorizontalMovement(0);
            if ((leftPressed) && (!rightPressed)) {
            ant.setHorizontalMovement(-moveSpeed);
            } else if ((rightPressed) && (!leftPressed)) {
            ant.setHorizontalMovement(moveSpeed);
            }
            // if we're pressing fire, attempt to fire
            if (firePressed) {
            tryToFire();
            }
             */
            // finally pause for a bit. Note: this should run us at about

            // 100 fps but on windows this might vary each loop due to

            // a bad implementation of timer

            try {
                Thread.sleep(10);
            } catch (Exception e) {
                //Do nothing
            }
        }
    }

    private void initEntities() {
       
//        pig = new AntEntity(this, "ant.png", 200, 400);
//        entities.add(pig);
//        pig.setSpeed(40);
//        pig.setAngle(1.57);
        
        ant = new AntEntity(this, "antpng.png", 200, 400);
        entities.add(ant);
        ant.setSpeed(40);
        ant.setAngleDegrees(300);
         
            /*
        //adding more ants..
        for (int x=1;x<8;x++) {
            Entity entity = new AntEntity(this, "antpng.png", 100 + 40*x, 100 + 40*x);
            entities.add(entity);
            entity.setSpeed(40);
            entity.setAngleDegrees(1 + 10 *x);
        }*/
            

    }

    //get size of canvas
    public int getcanvasWidth() {
        return this.canvasWidth;
    }

    public int getcanvasHeight() {
        return this.canvasHeight;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Game g = new Game();

        // Start the main game loop, note: this method will not

        // return until the game has finished running. Hence we are

        // using the actual main thread to run the game.

        g.gameLoop();
    }
}
