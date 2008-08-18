/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vmpc.simant;

/**
 * The entity that represents the players ship
 * 
 * @author Kevin Glass
 */
public class AntEntity extends Entity {

    /** The game in which the ship exists */
    private Game game;
    public boolean iKnowFood = false;
    public boolean carryFood = false;
    public double foodX = 0;
    public double foodY = 0;

    /**
     * Create a new entity to represent the players ship
     *  
     * @param game The game in which the ship is being created
     * @param ref The reference to the sprite to show for the ship
     * @param x The initial x location of the player's ship
     * @param y The initial y location of the player's ship
     */
    public AntEntity(Game game, String ref, int x, int y) {
        super(ref, x, y);

        this.game = game;
    }

    /**
     * Request that the ship move itself based on an elapsed ammount of
     * time
     * 
     * @param delta The time that has elapsed since last move (ms)
     */
    public void move(long delta) {
        // if we're moving left and have reached the left hand side
        // of the screen, jump to the right hand side
        if ((dx < 0) && (x < 0 - sprite.getWidth())) {
            x = game.getcanvasWidth();
            return;
        }

        // if we're moving right and have reached the right hand side
        // of the screen, jump to the left hand side
        if ((dx > 0) && (x > game.getcanvasWidth()/* pluss halve størrelsen*/)) {
            x = 0 - sprite.getWidth();
            return;
        }
        // if we're moving up and have reached the top
        // of the screen, jump to the bottom
        if ((dy < 0) && (y < 0 - sprite.getHeight())) {
            y = game.getcanvasHeight();
            return;
        }

        // if we're moving down and have reached the bottom
        // of the screen, jump to the top
        if ((dy > 0) && (y > game.getcanvasHeight()/* pluss halve størrelsen*/)) {
            y = 0 - sprite.getHeight();
            return;
        }


        super.move(delta);
    }

    public boolean getKnowFood() {
        return this.iKnowFood;
    }

    public double getFoodX() {
        return this.foodX;
    }

    public double getFoodY() {
        return this.foodY;
    }

    /**
     * Notification that the player's ship has collided with something
     * 
     * @param other The entity with which the ship has collided
     */
    public void collidedWith(Entity other) {
        if (other instanceof AntEntity) { //replace content with eventtriggers

            if (!this.iKnowFood) {
                if (other.getKnowFood()) {
                    this.foodX = other.getFoodX();
                    this.foodY = other.getFoodY();
                   // this.iKnowFood = true; //removed because ants tend to lie.. and lies spread like fire in dry grass troughout the ant community :'<
                    this.setAngle(this.calcAngle((double) this.foodX, (double) this.foodY));
                } else {
                    this.addAngle(100);
                }
            }
        } else if (other instanceof HomeEntity) {
            if (this.carryFood) { //auto (not set by the player)

                other.foodIncrease();
                this.carryFood = false;
            }
            //user code:
            if (this.iKnowFood) {
                this.setAngle(this.calcAngle((double) this.foodX, (double) this.foodY));
            }

        } else if (other instanceof FoodEntity) {
            if (!this.carryFood) { //auto (not in the event)

                if (other.foodDecrease()) {
                    this.carryFood = true;
                }
            }
            //usercode //function got the argument boolean FoodLeft...or smth
            if (this.carryFood) {
                this.iKnowFood = true;
                this.foodX = other.getX();
                this.foodY = other.getY();
                this.setAngle(this.calcAngle((double) game.homeX, (double) game.homeY));
            } else {
                this.addAngle(100);
                this.iKnowFood = false;
            }
        }
        //other user commands:
        //getDistanceBetweenCoords();
        //some standard variables like "iKnowFood"
        //+ "free brainspace" of 16 bit or smth :p
        //so users can define their own variables.

           //yet longer shortterm memory, atleast the ability to compare two food coordinates?


        // if its an alien, notify the game that the player
        // is dead
                /*
        if (other instanceof AlienEntity) {
        game.notifyDeath();
        }
         * */
        }
    }
