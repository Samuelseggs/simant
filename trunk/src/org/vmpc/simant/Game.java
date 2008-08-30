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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JButton;
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
    private Entity home;
    /** Test **/
    private Entity food;
    private Entity food2;
    private Entity maursluker; // :D
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
    public int homeX = 50;
    public int homeY = 50;
    public ArrayList<Boolean> collisions = new ArrayList();
    long lastLoopTime;
    // True if the game has been paused by user (or event)
    private boolean gamePaused = false;

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
        // do we'd like to exit the game (and maybe do some other stuff...?)

        container.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // add a mouse listener to the game (defined further down)

        addMouseListener(new MouseInputHandler());

        // add a key input system (defined below) to our canvas
        // so we can respond to key pressed
        addKeyListener(new KeyInputHandler());

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
        double frameTimeStart;
        double frameTimeEnd;
        double frameTime = 0;
        int frameTimeCalculateEveryFrame = 5;
        int frameTimeCalculated;

        double lastFrameRateTime = System.currentTimeMillis();
        double lastFrameRate = 0;
        ArrayList<Long> frameRates = new ArrayList();
        double frameRate;
        double checkFrameRateEveryMilli = 250;
        double deltaFrameRate;
        long delta;

        lastLoopTime = System.currentTimeMillis();

        // keep looping round til the game ends
        while (gameRunning) {
            frameTimeStart = System.nanoTime();

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


            if (!gamePaused) {
                // move this loop

                delta = System.currentTimeMillis() - lastLoopTime;
                deltaFrameRate = System.currentTimeMillis() - lastFrameRateTime;
                lastLoopTime = System.currentTimeMillis();

                //NANO 10 000

                // Get hold of a graphics context for the accelerated 
                // surface and blank it out
                Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

                // Set the graphics to use a bicbic filter
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                //NANO 30 000

                // Set the background color and fill the background
                g.setColor(Color.GREEN);
                g.fillRect(0, 0, canvasWidth, canvasHeight);

                //Add a framerate to the list of frames if the delta is not 0
                if (delta > 0) {
                    frameRates.add(1000 / delta);
                }
                //NANO 50 000

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

                //NANO 50 000 ~ 100 000
                //if (!waitingForKeyPress) {
                /** Loop trough our entities **/
                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = (Entity) entities.get(i);

                    //move the entity
                    entity.move(delta);

                    //draw the entity
                    entity.draw(g);

                    //brutefoce collisions
                    for (int s = i + 1; s < entities.size(); s++) {
                        Entity me = (Entity) entities.get(i);
                        Entity him = (Entity) entities.get(s);
                        if (me.collidesWith(him)) {
                            if (!collisions.get(entities.size() * i + s)) {
                                me.collidedWith(him);
                                him.collidedWith(me);
                                collisions.set(entities.size() * i + s, true);
                            }
                        } else {
                            collisions.set(entities.size() * i + s, false);
                        }
                    //Ye, half of the array slots stay unused...I know.
                    //but I'll fix it later when i got Inet and can check how to
                    //make a "normal" array in java :=)
                    }

                    // if a game event has indicated that game logic should
                    // be resolved, cycle round every entity requesting that
                    // their personal logic should be considered.
                    if (logicRequiredThisLoop) {
                        entity.doLogic();
                    }
                }
                //NANO 12 000 000
                logicRequiredThisLoop = false;

                frameTimeEnd = System.nanoTime();
                frameTime = frameTimeEnd - frameTimeStart;

                // remove any entity that has been marked for clear up
                entities.removeAll(removeList);
                removeList.clear();

                /** Draw stats  **/
                g.setColor(Color.black);

                //NANO 15 000 000
                g.drawString("FPS: " + (int) frameRate, 2, 20);
                g.drawString("FrameTime: " + frameTime, 60, 20);
                g.drawString("Food: " + home.getFoodAmount() + " units.", 2, 40);
                g.drawString("Food left: " + food.getFoodAmount() + " units", food.getX(), food.getY() - 5);
                g.drawString("Food left: " + food2.getFoodAmount() + " units", food2.getX(), food2.getY() - 5);
                // finally, we've completed drawing so clear up the graphics
                // and flip the buffer over
                g.dispose();
                strategy.show();

            //NANO 17 ~ 20 000 000
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
            // if a game event has indicated that game logic should
            // be resolved, cycle round every entity requesting that
            // their personal logic should be considered.

//            if (logicRequiredThisLoop) {
//                for (int i = 0; i < entities.size(); i++) {
//                    Entity entity = (Entity) entities.get(i);
//                    
//                }
//            }

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
            }

            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        //NANO 18 ~ 20 000 000
        }
    }

    private class MouseInputHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent mouse) {
            System.out.println("Mouse pressed at: " + mouse.getX() + " " + mouse.getY());
            if (!gamePaused) {
            // Get the selected element and place it on the map

            }
        }
    }

    private class KeyInputHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent key) {
            if (key.getKeyCode() == KeyEvent.VK_P) {
                gamePaused = !gamePaused;
                lastLoopTime = System.currentTimeMillis();
            }
            if (!gamePaused) {

            }
        }
    }

    private void initEntities() {

//        pig = new AntEntity(this, "ant.png", 200, 400);
//        entities.add(pig);
//        pig.setSpeed(40);
//        pig.setAngle(1.57);

        home = new HomeEntity(this, "tue.png", homeX, homeY);
        entities.add(home);
        home.setSpeed(0);
        home.setAngleDegrees(0);

        food = new FoodEntity(this, "food.png", 650, 500);
        entities.add(food);
        food.setSpeed(0);
        food.setAngleDegrees(0);

        food2 = new FoodEntity(this, "food.png", 500, 200);
        entities.add(food2);
        food2.setSpeed(0);
        food2.setAngleDegrees(270);

        //adding more ants..
        for (int x = 1; x < 50; x++) {
            Entity entity = new AntEntity(this, "maur2.png", home.getX() + 20, home.getY() + 20);
            entities.add(entity);
            entity.setSpeed(40 + x);
            entity.setAngleDegrees(1 + x * 7 * Math.random());
        }
//        //nvm
        maursluker = new AntEntity(this, "maur.png", home.getX() + 20, home.getY() + 20);
        entities.add(maursluker);
        maursluker.setSpeed(100);
        maursluker.setAngleDegrees(45);
//        
//        collisions test.. will change this later
        collisions.ensureCapacity(entities.size() * entities.size());
        for (int i = 0; i < entities.size() * entities.size(); i++) {
            collisions.add(true);
        }


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
