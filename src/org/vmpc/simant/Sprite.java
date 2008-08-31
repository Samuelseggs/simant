package org.vmpc.simant;

import java.awt.Image;
import java.io.IOException;
import javax.media.opengl.GL;

/**
 * A sprite to be displayed on the screen. Note that a sprite
 * contains no state information, i.e. its just the image and 
 * not the location. This allows us to use a single sprite in
 * lots of different places without having to store multiple 
 * copies of the image.
 * 
 * @author Kevin Glass
 */
public class Sprite {

    /** The texture that stores the image for this sprite */
    private Texture texture;
    private TextureLoader textureLoader;
    /** The window that this sprite can be drawn in */
    private GL gl;
    /** The width in pixels of this sprite */
    private int width;
    /** The height in pixels of this sprite */
    private int height;

    /**
     * Create a new sprite from a specified image.
     * 
     * @param window The window in which the sprite will be displayed
     * @param ref A reference to the image on which this sprite should be based
     */
    public Sprite(GL gl, String ref) {
        try {
            this.gl = gl;
            this.textureLoader = new TextureLoader(gl);
            texture = textureLoader.getTexture(ref);

            width = texture.getImageWidth();
            height = texture.getImageHeight();
        } catch (IOException e) {
            // a tad abrupt, but our purposes if you can't find a 
            // sprite's image you might as well give up.
            System.err.println("Unable to load texture: " + ref);
            System.exit(0);
        }
    }

    /**
     * Get the width of this sprite in pixels
     * 
     * @return The width of this sprite in pixels
     */
    public int getWidth() {
        return texture.getImageWidth();
    }

    /**
     * Get the height of this sprite in pixels
     * 
     * @return The height of this sprite in pixels
     */
    public int getHeight() {
        return texture.getImageHeight();
    }

    /**
     * Draw the sprite at the specified location
     * 
     * @param x The x location at which to draw this sprite
     * @param y The y location at which to draw this sprite
     */
    public void draw(int x, int y) {
        // store the current model matrix
        gl.glPushMatrix();

        // bind to the appropriate texture for this sprite
        texture.bind(gl);
        // translate to the right location and prepare to draw
        gl.glTranslatef(x, y, 0);
        gl.glColor3f(1, 1, 1);

        // draw a quad textured to match the sprite
        gl.glBegin(GL.GL_QUADS);
        {
            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(0, 0);
            gl.glTexCoord2f(0, texture.getHeight());
            gl.glVertex2f(0, height);
            gl.glTexCoord2f(texture.getWidth(), texture.getHeight());
            gl.glVertex2f(width, height);
            gl.glTexCoord2f(texture.getWidth(), 0);
            gl.glVertex2f(width, 0);
        }
        gl.glEnd();

        // restore the model view matrix to prevent contamination
        gl.glPopMatrix();
    }
}
