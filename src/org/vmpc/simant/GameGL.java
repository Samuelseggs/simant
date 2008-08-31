package org.vmpc.simant;

import java.awt.*;
import java.awt.event.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.*;
import com.sun.opengl.util.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

/**
 * JOGLGearsDemo.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GameGL implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

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
    static private int canvasWidth = 800;
    static private int canvasHeight = 600;
    public int homeX = 50;
    public int homeY = 50;
    public boolean collArray[];
    long lastLoopTime = 0;
    // True if the game has been paused by user (or event)
    private boolean gamePaused = false;
    // Variables from gameloop
    double frameTimeStart;
    double frameTimeEnd;
    double frameTime = 0;
    int frameTimeCalculateEveryFrame = 5;
    int frameTimeCalculated;
    double lastFrameRateTime = System.nanoTime();
    double lastFrameRate = 0;
    ArrayList<Long> frameRates = new ArrayList();
    double frameRate;
    double checkFrameRateEveryMilli = 2500;
    double deltaFrameRate;
    long delta;
    //GL vars
    /** The OpenGL content, we use this to access all the OpenGL commands */
    private GL gl;
    private GLCanvas canvas;
    TextureLoader textureLoader;
    static Frame frame;

    public static void main(String[] args) {

        frame = new Frame("SimAnt");
        GLCanvas canvas = new GLCanvas();

        canvas.addGLEventListener(new GameGL());
        frame.add(canvas);
        frame.setSize(canvasWidth, canvasHeight);
        final Animator animator = new Animator(canvas);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        frame.setVisible(true);
        animator.start();
    }
    private float view_rotx = 20.0f,  view_roty = 30.0f,  view_rotz = 0.0f;
    private int gear1,  gear2,  gear3;
    private float angle = 0.0f;
    private int prevMouseX,  prevMouseY;
    private boolean mouseRButtonDown = false;

    private void initEntities(GL gl) {
        home = new HomeEntity(gl, this, "tue.png", homeX, homeY);
        entities.add(home);
        home.setSpeed(0);
        home.setAngleDegrees(0);

        food = new FoodEntity(gl, this, "food.png", 650, 500);
        entities.add(food);
        food.setSpeed(0);
        food.setAngleDegrees(0);

        food2 = new FoodEntity(gl, this, "food.png", 500, 200);
        entities.add(food2);
        food2.setSpeed(0);
        food2.setAngleDegrees(270);

        //adding more ants..
        for (int x = 1; x < 50; x++) {
            Entity entity = new AntEntity(gl, this, "maur2.png", home.getX() + 20, home.getY() + 20);
            entities.add(entity);
            entity.setSpeed(40 + x);
            entity.setAngleDegrees(1 + x * 7 * Math.random());
        }
//        //nvm
        maursluker = new AntEntity(gl, this, "maur.png", home.getX() + 20, home.getY() + 20);
        entities.add(maursluker);
        maursluker.setSpeed(100);
        maursluker.setAngleDegrees(45);

        //This array makes sure we dont trigger events more than once when we have a collision
        collArray = new boolean[(int) (0.5 * (entities.size() * entities.size() + entities.size()))];
        for (int a = 0; a < (int) 0.5 * (entities.size() * entities.size() + entities.size()); a++) {
            collArray[a] = true;
        }
    }

    public void init(GLAutoDrawable drawable) {


        // get hold of the GL content

        gl = drawable.getGL();
        // enable textures since we're going to use these for our sprites
        gl.glEnable(GL.GL_TEXTURE_2D);

        // set the background colour of the display to black
        gl.glClearColor(0, 0, 0, 0);
        // set the area being rendered
        gl.glViewport(0, 0, canvasWidth, canvasHeight);
        // disable the OpenGL depth test since we're rendering 2D graphics
        gl.glDisable(GL.GL_DEPTH_TEST);

        initEntities(gl);
        textureLoader = new TextureLoader(gl);
    }
    //get size of canvas
    public int getcanvasWidth() {
        return this.canvasWidth;
    }

    public int getcanvasHeight() {
        return this.canvasHeight;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        System.err.println("Reshape called");
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));


        // at reshape we're going to tell OPENGL that we'd like to 
        // treat the screen on a pixel by pixel basis by telling
        // it to use Orthographic projection.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glOrtho(0, width, height, 0, -1, 1);
    }
    //Probably the game loop
    public void display(GLAutoDrawable drawable) {
        if(lastLoopTime == 0) {
            lastLoopTime = System.nanoTime(); //Looptime has not yet been set, so we need to init it.
        }
        // clear the screen and setup for rendering
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        frameTimeStart = System.nanoTime();

        if (!gamePaused) {
            // move this loop
            delta =  (System.nanoTime() - lastLoopTime) / 1000000; //Delta is supposed to be in milliseconds
           // System.out.println("Delta: " + delta);
            deltaFrameRate = (System.nanoTime() - lastFrameRateTime) / 1000000;
           // System.out.println("Delta: " + deltaFrameRate);
            lastLoopTime = System.nanoTime();

            //Add a framerate to the list of frames if the delta is not 0
            if (delta > 0) {
                frameRates.add(1000 / delta); //Convert to seconds
            }
            //Get the average framerate
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
                lastFrameRateTime = System.nanoTime();


                frame.setTitle("SimAnt (FPS: " + (int) frameRate + " FrameTime: " + frameTime + ")");
            } else {
                frameRate = lastFrameRate;
            }


            //if (!waitingForKeyPress) {
            /** Loop trough our entities **/
            int collisionSlot = 0;
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = (Entity) entities.get(i);

                //NANO 50 000 ~ 100 000
                entity.move(delta);

                //move the entity
                entity.draw();

                //brutefoce collisions
                for (int s = i + 1; s < entities.size(); s++) {
                    Entity me = (Entity) entities.get(i);
                    Entity him = (Entity) entities.get(s);
                    if (me.collidesWith(him)) {
                        if (!collArray[collisionSlot]) {
                            me.collidedWith(him);
                            him.collidedWith(me);
                            collArray[collisionSlot] = true;
                        }
                    } else {
                        collArray[collisionSlot] = false;
                    }
                    collisionSlot++;
                }

                // if a game event has indicated that game logic should
                // be resolved, cycle round every entity requesting that
                // their personal logic should be considered.
                if (logicRequiredThisLoop) {
                    entity.doLogic();
                }
            }

            logicRequiredThisLoop = false;

            frameTimeEnd = System.nanoTime();
            frameTime = frameTimeEnd - frameTimeStart;

            // remove any entity that has been marked for clear up
            entities.removeAll(removeList);
            removeList.clear();
        /** Draw stats  **
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
        strategy.show();*/
        // if a game event has indicated that game logic should
        // be resolved, cycle round every entity requesting that
//            if (logicRequiredThisLoop) {
//                for (int i = 0; i < entities.size(); i++) {
//                    Entity entity = (Entity) entities.get(i);
//                    
//                }
//            }
        }


        lastLoopTime = System.nanoTime();

        // flush the graphics commands to the card
        gl.glFlush();
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(GameGL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    // Methods required for the implementation of MouseListener
    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        prevMouseX = e.getX();
        prevMouseY = e.getY();
        if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
            mouseRButtonDown = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
            mouseRButtonDown = false;
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    // Methods required for the implementation of MouseMotionListener
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        Dimension size = e.getComponent().getSize();

        float thetaY = 360.0f * ((float) (x - prevMouseX) / (float) size.width);
        float thetaX = 360.0f * ((float) (prevMouseY - y) / (float) size.height);

        prevMouseX = x;
        prevMouseY = y;

        view_rotx += thetaX;
        view_roty += thetaY;
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void keyTyped(KeyEvent arg0) {
    }

    public void keyPressed(KeyEvent arg0) {
    }

    public void keyReleased(KeyEvent arg0) {
    }
}

