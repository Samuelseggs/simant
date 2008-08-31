/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vmpc.simant;

import javax.media.opengl.GL;

/**
 *
 * @author Vegard
 */
public class FoodEntity extends Entity {

    private GameGL game;
    public int coordX = 500;
    public int coordY = 500;
    public int foodAmount = 250;

    public boolean foodDecrease() {
        if (foodAmount > 0) {
            foodAmount--;
            return true;
        } else {
            return false;
        }
    }
    
    public int getFoodAmount() {
        return foodAmount;
    }
    

    public FoodEntity(GL gl, GameGL game, String ref, int x, int y) {
        super(gl, ref, x, y);

        this.game = game;
    }

    public void collidedWith(Entity other) {
        //remove some food if the ant picks up food
        }
}
