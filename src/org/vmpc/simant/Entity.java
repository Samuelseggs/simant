package org.vmpc.simant;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.media.opengl.GL;

/**
 * An entity represents any element that appears in the game. The
 * entity is responsible for resolving collisions and movement
 * based on a set of properties defined either by subclass or externally.
 * 
 * Note that doubles are used for positions. This may seem strange
 * given that pixels locations are integers. However, using double means
 * that an entity can move a partial pixel. It doesn't of course mean that
 * they will be display half way through a pixel but allows us not lose
 * accuracy as we move.
 * 
 * @author Kevin Glass
 */
public abstract class Entity {

    /** The current x location of this entity */
    protected double x;
    /** The current y location of this entity */
    protected double y;
    /** The sprite that represents this entity */
    protected Sprite sprite;
    /** The current speed of this entity horizontally (pixels/sec) */
    protected double dx;
    /** The current speed of this entity vertically (pixels/sec) */
    protected double dy;
    /** The angle of this entity */
    protected double angle;
    /** The speed of this entity */
    protected double speed;
    /** The rectangle used for this entity during collisions  resolution */
    private Rectangle me = new Rectangle();
    /** The rectangle used for other entities during collision resolution */
    private Rectangle him = new Rectangle();

    /**
     * Construct a entity based on a sprite image and a location.
     * 
     * @param ref The reference to the image to be displayed for this entity
     * @param x The initial x location of this entity
     * @param y The initial y location of this entity
     */
    public Entity(GL gl, String ref, int x, int y) {
        this.sprite = new Sprite(gl, ref);
        this.x = x;
        this.y = y;
    }

    /**
     * Request that this entity move itself based on a certain ammount
     * of time passing.
     * 
     * @param delta The ammount of time that has passed in milliseconds
     */
    public void move(long delta) {
        // update the location of the entity based on move speeds

        x += (delta * dx * this.speed) / 1000;
        y += (delta * dy * this.speed) / 1000;
        //System.out.println("X: " + x + "Y:" + y);
    }

    /**
     * Set the horizontal speed of this entity
     * 
     * @param dx The horizontal speed of this entity (pixels/sec)
     */
    public void setHorizontalMovement(double dx) {
        this.dx = dx;
    }

    /**
     * Set the vertical speed of this entity
     * 
     * @param dx The vertical speed of this entity (pixels/sec)
     */
    public void setVerticalMovement(double dy) {
        this.dy = dy;
    }

    /**
     * Set the angle of this entity
     * 
     * @param ang The angle of this entity in degrees
     */
    public void setAngleDegrees(double ang) {
        this.angle = Math.toRadians(ang);
        this.dy = Math.sin(Math.toRadians(ang));
        this.dx = Math.cos(Math.toRadians(ang));
    }

    //it's in degrees now sorry, got to be changed
    public void addAngle(double ang) {
        this.angle += Math.toRadians(ang);
        this.dy = Math.sin(this.angle);
        this.dx = Math.cos(this.angle);

    }

    /**
     * Set the angle of this entity
     * 
     * @param ang The angle of this entity in radians
     */
    public void setAngle(double ang) {
        this.angle = ang;
        this.dy = Math.sin(ang);
        this.dx = Math.cos(ang);
    }

    /**
     * Set the Speed of this entity
     * 
     * @param ang The speed of this entity (pixels/sec)
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Get the horizontal speed of this entity
     * 
     * @return The horizontal speed of this entity (pixels/sec)
     */
    public double getHorizontalMovement() {
        return dx;
    }

    /**
     * Get the vertical speed of this entity
     * 
     * @return The vertical speed of this entity (pixels/sec)
     */
    public double getVerticalMovement() {
        return dy;
    }

    /**
     * Get the angle of this entity
     * 
     * @return The angle of this entity
     */
    public double getAngle() {
        return this.angle;
    }

    /**
     * Get the angle of this entity in degrees
     * 
     * @return The angle of this entity
     */
    public double getAngleDegrees() {
        return Math.toDegrees(this.angle);
    }

    /**
     * Set the Speed of this entity
     * 
     * @param ang The speed of this entity (pixels/sec)
     */
    public double getSpeed(double speed) {
        return this.speed;
    }

    /**
     * Draw this entity to the graphics context provided
     * 
     * @param g The graphics context on which to draw
     */
    public void draw() {
        double theta = angle;
        double thetaX = x + sprite.getWidth() / 2;
        double thetaY = y + sprite.getHeight() / 2;
        //g.rotate(theta, thetaX, thetaY);

        sprite.draw((int) x, (int) y, angle);

        //g.rotate(-theta, thetaX, thetaY);
    }

    /**
     * Do the logic associated with this entity. This method
     * will be called periodically based on game events
     */
    public void doLogic() {
    }

    /**
     * Get the x location of this entity
     * 
     * @return The x location of this entity
     */
    public int getX() {
        return (int) x;
    }

    /**
     * Get the y location of this entity
     * 
     * @return The y location of this entity
     */
    public int getY() {
        return (int) y;
    }

    //didnt know how to call AntEntity functions :D
    public boolean getKnowFood() {
        return false;
    }

    public double getFoodX() {
        return 0;
    }

    public double getFoodY() {
        return 0;
    }

    public void foodIncrease() {
    }

    public boolean foodDecrease() {
        return true;
    }

    public int getFoodAmount() {
        return 0;
    }

    /**
     * Get the angle between the Entity and a given location
     * @param The x coordinate of the target
     * @param The y coordinate of the target
     * @return The Angle in radians
     */
    protected double calcAngle(double x, double y) {
        x -= (double) this.getX();
        y -= (double) this.getY();
        if (x == 0.0 && y == 0.0) {
            return 0;
        } else if (x != 0.0) {
            double ang = Math.atan2(y, x);
            if (ang < 0) {
                ang += Math.PI * 2;
            }

            return ang;
        } else {
            if (y > 0.0) {
                return Math.PI / 2;
            } else {
                return Math.PI * 1.5;
            }
        }

    }

    /**
     * Check if this entity collised with another.
     * 
     * @param other The other entity to check collision against
     * @return True if the entities collide with each other
     */
    public boolean collidesWith(Entity other) {
        me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
        him.setBounds((int) other.x, (int) other.y, other.sprite.getWidth(), other.sprite.getHeight());

        return me.intersects(him);
    }

    /**
     * Notification that this entity collided with another.
     * 
     * @param other The entity with which this entity collided.
     */
    public abstract void collidedWith(Entity other);
}
