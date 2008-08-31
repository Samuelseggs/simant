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
public class HomeEntity extends Entity {

    private GameGL game;
    public int FoodAmount = 0;

    public HomeEntity(GL gl, GameGL game, String ref, int x, int y) {
        super(gl, ref, x, y);

        this.game = game;
    }

    public void foodIncrease() {
        FoodAmount++;
    }

    public int getFoodAmount() {
        return FoodAmount;
    }

    public void collidedWith(Entity other) {
        //Add some food if the ant drops some
        //but this shouldnt really be an entity,
        //and it should be done by the ant, not here
        }
}