package com.drollgames.crjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class WorldRenderer implements Disposable {
    private static final float VIEWPORT_WIDTH = 6.6f;
    private static final float VIEWPORT_HEIGHT = 4.0f;

    private OrthographicCamera camera;
    private OrthographicCamera cam2d;
    private SpriteBatch batch;
    private WorldController worldController;
    TextureAtlas.AtlasRegion texture;

    private Viewport viewport;

    public WorldRenderer(WorldController worldController) {
        this.worldController = worldController;
        init();
    }

    private void init() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.position.set(0, 0, 0);
        camera.update();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);

        cam2d = new OrthographicCamera();
        cam2d.setToOrtho(false, 800, 480);
        cam2d.update();

        texture = Assets.instance.assetBackgrnds.bckgrndSpace1;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void render() {
        if (viewport instanceof ScalingViewport) {

            // This shows how to set the viewport to the whole screen and draw within the black bars.
            ScalingViewport scalingViewport = (ScalingViewport) viewport;
            int screenWidth = Gdx.graphics.getWidth();
            int screenHeight = Gdx.graphics.getHeight();
            Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
            batch.getProjectionMatrix().idt().setToOrtho2D(0, 0, screenWidth, screenHeight);
            batch.getTransformMatrix().idt();
            batch.begin();
            float leftGutterWidth = scalingViewport.getLeftGutterWidth();
            if (leftGutterWidth > 0) {
                // batch.draw(texture, 0, 0, leftGutterWidth, screenHeight);
                // batch.draw(texture, scalingViewport.getRightGutterX(), 0, scalingViewport.getRightGutterWidth(), screenHeight);

                batch.draw(texture, 0, 0, screenWidth, screenHeight);
                // batch.draw(texture, scalingViewport.getRightGutterX(), 0, scalingViewport.getRightGutterWidth(), screenHeight);
            }
            float bottomGutterHeight = scalingViewport.getBottomGutterHeight();
            if (bottomGutterHeight > 0) {
                // batch.draw(texture, 0, 0, screenWidth, bottomGutterHeight);
                // batch.draw(texture, 0, scalingViewport.getTopGutterY(), screenWidth, scalingViewport.getTopGutterHeight());

                batch.draw(texture, 0, 0, screenWidth, screenHeight);
                // batch.draw(texture, 0, scalingViewport.getTopGutterY(), screenWidth, scalingViewport.getTopGutterHeight());
            }
            batch.end();
            viewport.update(screenWidth, screenHeight, true); // Restore viewport.
        }

        batch.setProjectionMatrix(cam2d.combined);
        batch.begin();
        batch.draw(Assets.instance.assetBackgrnds.bckgrndSpace1, 0, 0);
        batch.end();

        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldController.level.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

}
