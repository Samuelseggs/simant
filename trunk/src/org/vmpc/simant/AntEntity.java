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
	
	/**
	 * Create a new entity to represent the players ship
	 *  
	 * @param game The game in which the ship is being created
	 * @param ref The reference to the sprite to show for the ship
	 * @param x The initial x location of the player's ship
	 * @param y The initial y location of the player's ship
	 */
	public AntEntity(Game game,String ref,int x,int y) {
		super(ref,x,y);
		
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
		if ((dy < 0) && (y < 0/* minus halve størrelsen*/)) {
                    y = game.getcanvasHeight();
                    return;
		}
                
		// if we're moving down and have reached the bottom
		// of the screen, jump to the top
		if ((dy > 0) && (y > game.getcanvasHeight()/* pluss halve størrelsen*/)) {
                    y = 0;
                    return;
		}
                
		
		super.move(delta);
	}
	
	/**
	 * Notification that the player's ship has collided with something
	 * 
	 * @param other The entity with which the ship has collided
	 */
	public void collidedWith(Entity other) {
		// if its an alien, notify the game that the player

		// is dead
                /*
		if (other instanceof AlienEntity) {
			game.notifyDeath();
		}
                 * */
	}
}
