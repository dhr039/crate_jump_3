package com.drollgames.crjump.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.drollgames.crjump.game.Assets;
import com.drollgames.crjump.util.AudioManager;

public class CrateMain extends AbstractGameObject {

    public static final String TAG = "CrateMain";
    
    private static final float TERM_VELOCITY_GROUNDED_X = 9;
    private static final float TERM_VELOCITY_GROUNDED_Y = 9; 
    private static final float TERM_VELOCITY_JUMPING_X = 10; 
    private static final float TERM_VELOCITY_JUMPING_Y = 12;
    private static final float JUMP_ROTATION_FACTOR = 100;

    public enum JUMP_STATE {
        GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
    }

    private TextureRegion regHead;

    public JUMP_STATE jumpState;

    public ParticleEffect dustParticles = new ParticleEffect();

    public CrateMain() {
        init();
    }

    public void init() {
        dimension.set(1.0f, 1.0f);

        regHead = Assets.instance.crateMain.head;

        // Center image on game object
        origin.set(dimension.x / 2, dimension.y / 2);

        // Bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);

        // Set physics values
        terminalVelocity.set(TERM_VELOCITY_GROUNDED_X, TERM_VELOCITY_GROUNDED_Y);
        friction.set(112.0f, 0.0f);
        acceleration.set(0.0f, -25.0f);

        // Jump state
        jumpState = JUMP_STATE.FALLING;

        // Particles
        dustParticles.load(Gdx.files.internal("particles/dust.pfx"), Gdx.files.internal("particles"));
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        dustParticles.update(deltaTime);
    }
    
    public void setJumping(boolean jumpKeyPressed) {
        switch (jumpState) {
            case GROUNDED:
                /*if you still hold pressed the jump, it will not be grounded*/
                if (jumpKeyPressed) {
                    AudioManager.instance.play(Assets.instance.sounds.jump, 4);
                    jumpState = JUMP_STATE.JUMP_RISING;
                }
                break;
            case JUMP_RISING: // Rising in the air
//                if (!jumpKeyPressed) {
                    jumpState = JUMP_STATE.JUMP_FALLING;
//                }
                break;
            case FALLING:
            case JUMP_FALLING:
                break;
        }
    }

    @Override
    protected void updateMotionY(float deltaTime) {
        
        switch (jumpState) {
            case GROUNDED:
                setJumpRotation(deltaTime, false);
                jumpState = JUMP_STATE.FALLING;
                if (velocity.x != 0) {
                    dustParticles.setPosition(position.x + dimension.x / 2, position.y);
                    dustParticles.start();
                }
                break;
            case JUMP_RISING:
                setJumpRotation(deltaTime, true);
                velocity.y = terminalVelocity.y;
                break;
            case FALLING:
                break;
            case JUMP_FALLING:
                setJumpRotation(deltaTime, true);
                break;
        }
        if (jumpState != JUMP_STATE.GROUNDED) {
            dustParticles.allowCompletion();
            super.updateMotionY(deltaTime);
        }
    }
    
    
    private void setJumpRotation(float deltaTime, boolean isJumping) {
        if(isJumping) {
            rotation -= (JUMP_ROTATION_FACTOR * deltaTime);
            terminalVelocity.set(TERM_VELOCITY_JUMPING_X, TERM_VELOCITY_JUMPING_Y);
        } else {
            if(rotation < 0 && rotation > -135) {
                rotation = -90;
            } else if(rotation <= -135 && rotation > -225) {
                rotation = -180;
            } else if(rotation <= -225 && rotation > -315) {
                rotation = -270;
            } else {
                rotation = 0;
            }
            terminalVelocity.set(TERM_VELOCITY_GROUNDED_X, TERM_VELOCITY_GROUNDED_Y);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion reg = null;
        
        dustParticles.draw(batch);

        // Draw image
        reg = regHead;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
                reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), false, false);

        // Reset color to white
        batch.setColor(1, 1, 1, 1);
    }

}
