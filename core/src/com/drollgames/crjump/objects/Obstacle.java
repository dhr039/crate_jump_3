package com.drollgames.crjump.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.drollgames.crjump.game.Assets;

public class Obstacle extends AbstractGameObject {

    private TextureRegion regObstacle;

    public Obstacle() {
        init();
    }

    private void init() {
        dimension.set(1.0f, 1.0f);

        regObstacle = Assets.instance.obstacle.obstacle;

        // Set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
    }

    public void render(SpriteBatch batch) {

        TextureRegion reg = null;

        reg = regObstacle;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
    }

}
