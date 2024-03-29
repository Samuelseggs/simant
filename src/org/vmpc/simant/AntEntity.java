/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vmpc.simant;

import javax.media.opengl.GL;

/**
 * The entity that represents the players ship
 * 
 * @author Kevin Glass
 */
public class AntEntity extends Entity {

    /** The game in which the ship exists */
    private GameGL game;
    public boolean iKnowFood = false;
    public boolean carryFood = false;
    public double foodX = 0;
    public double foodY = 0;
    public double targetangle = 0;
    public boolean headingForTarget = false;
    public boolean guilty = false;
    public boolean angleDir = true;

    /**
     * Create a new entity to represent the players ship
     *  
     * @param game The game in which the ship is being created
     * @param ref The reference to the sprite to show for the ship
     * @param x The initial x location of the player's ship
     * @param y The initial y location of the player's ship
     */
    public AntEntity(GL gl, GameGL game, String ref, int x, int y) {
        super(gl, ref, x, y);

        this.game = game;
    }

    /**
     * Request that the ship move itself based on an elapsed ammount of
     * time
     * 
     * @param delta The time that has elapsed since last move (ms)
     */
    public void move(long delta) {  
        
        //moved this to the beginning instead of end.
        super.move(delta);
        
        
        // if we're moving left and have reached the left hand side
        // of the screen, jump to the right hand side
        if ((dx < 0) && (x < 0 - sprite.getWidth()/2)) {
            x = game.getcanvasWidth()+ sprite.getWidth()/2;
            return;
        }

        // if we're moving right and have reached the right hand side
        // of the screen, jump to the left hand side
        if ((dx > 0) && (x > game.getcanvasWidth()+ sprite.getWidth()/2)) {
            x = 0 - sprite.getWidth()/2;
            return;
        }
        // if we're moving up and have reached the top
        // of the screen, jump to the bottom
        if ((dy < 0) && (y < 0 - sprite.getHeight()/2)) {
            y = game.getcanvasHeight()+ sprite.getWidth()/2;
            return;
        }

        // if we're moving down and have reached the bottom
        // of the screen, jump to the top
        if ((dy > 0) && (y > game.getcanvasHeight()+ sprite.getWidth()/2)) {
            y = 0 - sprite.getHeight()/2;
            return;
        }
    }
    public void reverse(long delta) {
        super.reverse(delta);
         // if we're moving left and have reached the left hand side
        // of the screen, jump to the right hand side
        if ((dx*-1 < 0) && (x < 0 - sprite.getWidth()/2)) {
            x = game.getcanvasWidth()+ sprite.getWidth()/2;
            return;
        }

        // if we're moving right and have reached the right hand side
        // of the screen, jump to the left hand side
        if ((dx*-1 > 0) && (x > game.getcanvasWidth()+ sprite.getWidth()/2)) {
            x = 0 - sprite.getWidth()/2;
            return;
        }
        // if we're moving up and have reached the top
        // of the screen, jump to the bottom
        if ((dy*-1 < 0) && (y < 0 - sprite.getHeight()/2)) {
            y = game.getcanvasHeight()+ sprite.getWidth()/2;
            return;
        }

        // if we're moving down and have reached the bottom
        // of the screen, jump to the top
        if ((dy*-1 > 0) && (y > game.getcanvasHeight()+ sprite.getWidth()/2)) {
            y = 0 - sprite.getHeight()/2;
            return;
        }
        
    }
    public double getTargetAngle() {
//        if (headingForTarget)
            return targetangle;
//        else
//            return Math.random()* 2.123;
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
    public boolean getRound() {
       return true; //i want round hitboxes for ants (test)
    }
    
    public void recalculateTargetAngle(boolean change) {
        if (change) {
            if (this.headingForTarget) {
                if (!carryFood)
                    targetangle = this.calcAngle((double) this.foodX, (double) this.foodY);
                else
                    targetangle = this.calcAngle((double) game.homeX, (double) game.homeY);
           } //else 
             //   this.targetangle=angle;
            

            if ((this.getTargetAngle()+Math.PI/8 - this.getAngle()) > Math.PI || ((this.getTargetAngle()+Math.PI/8 - this.getAngle()) < 0 && (this.getTargetAngle()+Math.PI/8 - this.getAngle() > Math.PI * -1)))
                this.angleDir=true;
            else
                this.angleDir=false;
        }
        if (this.angleDir)
            this.addAngle(game.delta * -0.002);
        else
            this.addAngle(game.delta * 0.002);
    }
    
    public void setGuilty(boolean guilt) {
        guilty=guilt;
    }
    public void setTargetAngle() {
        this.targetangle=super.angle;
    }
    /**
     * Notification that the player's ship has collided with something
     * 
     * @param other The entity with which the ship has collided
     */
    public void collidedWith(Entity other) {
        
        if (other instanceof AntEntity) { //replace content with eventtriggers
            if (!this.headingForTarget) {
                if (!this.iKnowFood) {
                    if (other.getKnowFood()) {
                        this.foodX = other.getFoodX();
                        this.foodY = other.getFoodY();
                       // this.iKnowFood = true; //removed because ants tend to lie.. and lies spread like fire in dry grass troughout the ant community :'<
                        this.headingForTarget=true;
                        if (game.stepOn)
                            this.setAngle(this.calcAngle((double) this.foodX, (double) this.foodY));
                        else {
                            this.targetangle = this.calcAngle((double) this.foodX, (double) this.foodY);
                        }
                    } else {
                        if (game.stepOn)
                            this.addAngleDegrees(other.getAngleDegrees()-this.getAngleDegrees()+40);
                    }
                }
            }
           if (!game.stepOn) {
               double diff =((this.calcAngle( other.x, other.y)) - this.angle);
               if (diff < 0)
                   diff+=2*Math.PI;
                  
                if (diff > Math.PI) //turn away from the oponent.
                    this.angleDir=false;
                else 
                 this.angleDir=true;
               double viewang = other.angle-this.angle;
               if (viewang < 0)
                   viewang+=2*Math.PI;
               if (viewang < Math.PI/2 || viewang > 2*Math.PI) { //but if we're looking in the same dir, one has to turn against the other.
                   //if mydiff < otherdiff.. turn against him.. else turn away.
//                  diff-=Math.PI;
//                  double otherdiff= ((other.calcAngle( this.x, this.y)) - other.angle);
//                  if (otherdiff < 0)
//                      otherdiff +=2*Math.PI;
//                  otherdiff-=Math.PI;
//                  if (otherdiff<0)
//                      otherdiff*=-1;
//                   if (diff<0)
//                       diff*=-1;
//                  if (diff<otherdiff)
//                      this.angleDir= !this.angleDir;
                   this.angleDir=true;
               }
           }
        } else if (other instanceof HomeEntity) {
            if (this.carryFood) { //auto (not set by the player)

                other.foodIncrease();
                this.carryFood = false;
            }
            //user code:
            if (this.iKnowFood) {
                if (game.stepOn)
                    this.setAngle(this.calcAngle((double) this.foodX, (double) this.foodY));
                else {
                    this.targetangle = this.calcAngle((double) this.foodX, (double) this.foodY);
                }
            }
         //  if (!game.stepOn) {
                   //this.addAngle(-0.5);
       //    }
        } else if (other instanceof FoodEntity) {
            if (!this.carryFood) { //auto (not in the event)

                if (other.foodDecrease()) {
                    this.carryFood = true;
                }
            }
            //usercode
            if (this.carryFood) {
                this.iKnowFood = true;
                this.headingForTarget = true;
                this.foodX = other.getX();
                this.foodY = other.getY();
                if (game.stepOn)
                    this.setAngle(this.calcAngle((double) game.homeX, (double) game.homeY));
                 else {
                    this.targetangle = this.calcAngle((double) game.homeX, (double) game.homeY);
                }
            } else {
                if (game.stepOn)
                    this.addAngleDegrees(100);
                this.iKnowFood = false;
                this.headingForTarget = false;
            }
            //if (!game.stepOn) {
          //     this.addAngle(-0.5);
        //   }
        }
        
        //destination boolean variable? 
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
