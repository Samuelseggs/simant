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
     public void reverse(long delta) {
        x -= (delta * dx * this.speed) / 1000;
        y -= (delta * dy * this.speed) / 1000;
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
        this.anglefix();
        this.dy = Math.sin(this.angle);
        this.dx = Math.cos(this.angle);
    }

    //it's in degrees now sorry, got to be changed
    public void addAngleDegrees(double ang) {
        this.angle += Math.toRadians(ang);
        this.anglefix();
        this.dy = Math.sin(this.angle);
        this.dx = Math.cos(this.angle);

    }
//it's in degrees now sorry, got to be changed
    public void addAngle(double ang) {
        this.angle += ang;
        this.anglefix();
        this.dy = Math.sin(this.angle);
        this.dx = Math.cos(this.angle);

    }
    public void anglefix() {
            while (this.angle < 0) {
            this.angle += 2* Math.PI;
            }
            while (this.angle > 2*Math.PI) {
                this.angle -= 2* Math.PI;
            }
    }
          

    /**
     * Set the angle of this entity
     * 
     * @param ang The angle of this entity in radians
     */
    public void setAngle(double ang) {
        this.angle = ang;
        this.anglefix();
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
    public boolean getRound() {
        return true; //warning :O! TEST :p
    }
    public void recalculateTargetAngle() {
    }
    public double getTargetAngle() {
        return angle;
    }
    
    public void setGuilty(boolean guilt) {
    }

    /**
     * Check if this entity collised with another.
     * 
     * @param other The other entity to check collision against
     * @return True if the entities collide with each other
     */
    public boolean collidesWith(Entity other) {
        
        
    //introducing circle shaped hitboxes.
    if (true) {
        //Even for squares this method should be slightly faster, as they most probably wont intersect anyway.
        double yDist;
        double height;
        double width;
        double xDist;
        
        //if we are in the wrong height for a collision (distance longer than rad+rad)
        if (( (height=((double)sprite.getHeight()/2)+((double)other.sprite.getHeight()/2)) < (yDist = (y - other.y))) || (height < yDist*-1)) {
            return false;         
        }
        //or too far to the side :)
        else if (((width=((double)sprite.getWidth()/2)+((double)other.sprite.getWidth()/2)) < (xDist = (x - other.x))) || (width < xDist*-1)) {
            return false;
        } 
        //else if both got square hitboxes we're actually colliding :o|
        else if (!getRound() && !other.getRound()) {        
             return true;
        }
        //but not necessarily if both got round ones
        else if (getRound() && other.getRound()) {
             //if the distance betweeen the two objects is smaller than the two radiuses combined.
             if (Math.pow(x-other.x,2)+Math.pow(x-other.x,2) < Math.pow((sprite.getWidth()/2)+(other.sprite.getWidth()/2),2) ) //Here i assume that half the sprite width is the radius and the wanted hitbox size.
                return true;
             else return false;          
         }
        //or if only one got round ones :)
        else {
            //moving the imagined square to one corner of the circle, to simplify it for us
            if (yDist < 0)
                yDist*= -1;
            if (xDist < 0)
                xDist*= -1;
            // if it crosses the middle of our circle, it hits.
            if (getRound()) {
                if ((yDist-=other.sprite.getHeight()/2) <= 0)
                    return true;
                else if ((xDist-=other.sprite.getWidth()/2) <= 0)
                    return true;
            } else {
                if ((yDist-=sprite.getHeight()/2) <= 0)
                    return true;
                else if ((xDist-=sprite.getWidth()/2) <= 0)
                    return true;
            }
            //and the very last check.       //PS: We need to do the two above checks first, or we might get a false positive.
            if (Math.pow(x-other.x,2)+Math.pow(x-other.x,2) < Math.pow((sprite.getWidth()/2)+(other.sprite.getWidth()/2),2) )
                return true;
            else return false;
        }
    }
        
        
        
        
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
