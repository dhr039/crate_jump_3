package com.drollgames.crjump.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.drollgames.crjump.game.Assets;

public class Clouds extends AbstractGameObject {

    private static final float ROTATION_SPEED_FACTOR = .2f;
    private static final float CLOUDS_BASE_HEIGHT = 5.0f;

    private float length;

    private Array<Animation> regClouds;
    private Array<Cloud> clouds;
    private static final float FRAME_DURATION = 1.0f / 30.0f;

    private class Cloud extends AbstractGameObject {

        public Animation animation;
        public float animationTime;

        public Cloud() {
            animationTime = 0.0f;
        }

        public void setRegion(Animation animation) {
            this.animation = animation;
        }

        @Override
        public void render(SpriteBatch batch) {
            /*
             * TextureRegion reg = regCloud; batch.draw(reg.getTexture(), position.x + origin.x, position.y + origin.y, origin.x, origin.y, dimension.x,
             * dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);
             */
            animationTime += ROTATION_SPEED_FACTOR * Gdx.graphics.getDeltaTime();
            TextureRegion frame = (TextureRegion) animation.getKeyFrame(animationTime);
            batch.draw(frame.getTexture(), position.x + origin.x, position.y + origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, frame.getRegionX(), frame.getRegionY(), frame.getRegionWidth(), frame.getRegionHeight(), false,
                    false);
        }
    }

    public Clouds(float length) {
        this.length = length;
        init();
    }

    private void init() {
        dimension.set(1.5f, 1.5f);

        regClouds = new Array<Animation>();
        regClouds.add(new Animation(FRAME_DURATION, Assets.instance.assetBackgrnds.arrAs1Sprites, Animation.PlayMode.LOOP_REVERSED));
        regClouds.add(new Animation(FRAME_DURATION, Assets.instance.assetBackgrnds.arrAs2Sprites, Animation.PlayMode.LOOP_REVERSED));
        regClouds.add(new Animation(FRAME_DURATION, Assets.instance.assetBackgrnds.arrAs3Sprites, Animation.PlayMode.LOOP_REVERSED));

        int distFac = 10;
        int numClouds = (int) (length / distFac);
        clouds = new Array<Cloud>(2 * numClouds);
        for (int i = 0; i < numClouds; i++) {
            Cloud cloud = spawnCloud();
            cloud.position.x = i * distFac;
            clouds.add(cloud);
        }
    }

    private Cloud spawnCloud() {
        Cloud cloud = new Cloud();
        cloud.dimension.set(dimension);
        // select random cloud image
        cloud.setRegion(regClouds.random());
        // position
        Vector2 pos = new Vector2();
        pos.x = length + 10; // position after end of level
        pos.y = CLOUDS_BASE_HEIGHT;
        // pos.y += MathUtils.random(0.0f, 0.7f) * (MathUtils.randomBoolean() ? 1 : -1); // random additional position
        pos.y += MathUtils.random(0.0f, 5f) * (MathUtils.randomBoolean() ? 1 : -1);
        cloud.position.set(pos);
        // speed
        Vector2 speed = new Vector2();
        speed.x += 0.5f; // base speed
        speed.x += MathUtils.random(0.0f, 3f); // random additional speed
        cloud.terminalVelocity.set(speed);
        speed.x *= -1; // move left
        cloud.velocity.set(speed);
        return cloud;
    }

    @Override
    public void update(float deltaTime) {
        for (int i = clouds.size - 1; i >= 0; i--) {
            Cloud cloud = clouds.get(i);
            cloud.update(deltaTime);
            if (cloud.position.x < -10) {
                // cloud moved outside of world.
                // destroy and spawn new cloud at end of level.
                clouds.removeIndex(i);
                clouds.add(spawnCloud());
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        for (Cloud cloud : clouds)
            cloud.render(batch);
    }

}
