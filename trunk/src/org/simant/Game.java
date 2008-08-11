/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.simant;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

        // keep looping round til the game ends

        while (gameRunning) {
            
            //Move ant
            ant.setHorizontalMovement(20);
            
            // work out how long its been since the last update, this

            // will be used to calculate how far the entities should

            // move this loop

            long delta = System.currentTimeMillis() - lastLoopTime;

            lastLoopTime = System.currentTimeMillis();

            // Get hold of a graphics context for the accelerated 

            // surface and blank it out

            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, canvasWidth, canvasHeight);

            if (delta > 0) {
                g.setColor(Color.black);

                g.drawString("FPS: " + 1000 / delta, 2, 20);
            }

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
        ant = new AntEntity(this, "ant.jpg", 100, 100);
        entities.add(ant);

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
