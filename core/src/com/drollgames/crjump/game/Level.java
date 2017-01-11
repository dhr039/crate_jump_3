package com.drollgames.crjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.drollgames.crjump.objects.AbstractGameObject;
import com.drollgames.crjump.objects.Clouds;
import com.drollgames.crjump.objects.CrateMain;
import com.drollgames.crjump.objects.Goal;
import com.drollgames.crjump.objects.Obstacle;
import com.drollgames.crjump.objects.Rock;

public class Level {

    public static final String TAG = Level.class.getName();

    public enum BLOCK_TYPE {
        EMPTY(0, 0, 0), // black
        GOAL(255, 0, 0), // red
        ROCK(0, 255, 0), // green
        PLAYER_SPAWNPOINT(255, 255, 255), // white
        ITEM_OBSTACLE(255, 0, 255); // purple

        private int color;

        private BLOCK_TYPE(int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        public boolean sameColor(int color) {
            return this.color == color;
        }

        public int getColor() {
            return color;
        }
    }

    public CrateMain crateMain;
    public Array<Rock> rocks;
    public Array<Obstacle> obstacles;
    public Clouds asteroids;
    public Goal goal;

    public Level(String filename) {
        init(filename);
    }

    private void init(String filename) {
        crateMain = null;
        rocks = new Array<Rock>();
        obstacles = new Array<Obstacle>();

        /* load image file that represents the level data */
        Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
        // Gdx.app.log("", "pixmap.getWidth(): " + pixmap.getWidth()); == 120
        // Gdx.app.log("", "pixmap.getHeight(): " + pixmap.getHeight()); == 7
        /* scan pixels from top-left to bottom-right */
        int lastPixel = -1;
        for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
            for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
                AbstractGameObject obj = null;
                /* height grows from bottom to top */
                float baseHeight = pixmap.getHeight() - pixelY;
                // get color of current pixel as 32-bit RGBA value
                int currentPixel = pixmap.getPixel(pixelX, pixelY);
                // find matching color value to identify block type at (x,y)
                // point and create the corresponding game object if there is
                // a match

                // empty space
                if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
                    // do nothing
                } else if (BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
                    if (lastPixel != currentPixel) {
                        obj = new Rock();
                        obj.position.set(pixelX, baseHeight * obj.dimension.y);
                        rocks.add((Rock) obj);
                    } else {
                        rocks.get(rocks.size - 1).increaseLength(1);
                    }
                } else if (BLOCK_TYPE.GOAL.sameColor(currentPixel)) {
                    obj = new Goal();
                    obj.position.set(pixelX, baseHeight);
                    goal = (Goal) obj;
                } else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
                    obj = new CrateMain();
                    obj.position.set(pixelX, baseHeight * obj.dimension.y);
                    crateMain = (CrateMain) obj;
                } else if (BLOCK_TYPE.ITEM_OBSTACLE.sameColor(currentPixel)) {
                    obj = new Obstacle();
                    obj.position.set(pixelX, baseHeight * obj.dimension.y);
                    obstacles.add((Obstacle) obj);
                }

                // unknown object/pixel color
                else {
                    // red color channel
                    int r = 0xff & (currentPixel >>> 24);
                    // green color channel
                    int g = 0xff & (currentPixel >>> 16);
                    // blue color channel
                    int b = 0xff & (currentPixel >>> 8);
                    // alpha channel
                    int a = 0xff & currentPixel;
                    Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" + pixelY + ">: r<" + r + "> g<" + g + "> b<" + b + "> a<" + a + ">");
                }
                lastPixel = currentPixel;
            }
        }

        // decoration
        asteroids = new Clouds(pixmap.getWidth());

        // free memory
        pixmap.dispose();
    }

    public void update(float deltaTime) {
        crateMain.update(deltaTime);
        // Rocks
        for (Rock rock : rocks) {
            rock.update(deltaTime);
        }
        // Clouds
        asteroids.update(deltaTime);
        for (Obstacle o : obstacles) {
            o.update(deltaTime);
        }
    }

    /* draw order is from first to last: */
    public void render(SpriteBatch batch) {

        asteroids.render(batch);

        for (Rock rock : rocks) {
            rock.render(batch);
        }

        crateMain.render(batch);

        for (Obstacle o : obstacles) {
            o.render(batch);
        }

    }

}
