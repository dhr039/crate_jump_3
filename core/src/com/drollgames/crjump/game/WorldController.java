package com.drollgames.crjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.drollgames.crjump.CJMain;
import com.drollgames.crjump.model.LevelState;
import com.drollgames.crjump.objects.CrateMain;
import com.drollgames.crjump.objects.Obstacle;
import com.drollgames.crjump.objects.Rock;
import com.drollgames.crjump.screens.GameScreen;
import com.drollgames.crjump.screens.LevelsListScreen;
import com.drollgames.crjump.transitions.ScreenTransition;
import com.drollgames.crjump.transitions.ScreenTransitionFade;
import com.drollgames.crjump.util.AudioManager;
import com.drollgames.crjump.util.CameraHelper;
import com.drollgames.crjump.util.Constants;
import com.drollgames.crjump.util.GamePreferences;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.drollgames.crjump.util.Constants.BTNS_FADE_IN_OUT_DURATION;

public class WorldController extends InputAdapter {

    private static final String TAG = "WorldController";

    private static final float ZOOM = 2.2f;

    private CJMain game;
    public Level level;
    public int scoreStars;
    public CameraHelper cameraHelper;
    /* Rectangles for collision detection */
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();

    private boolean isJumping = false;
    private boolean didTouchScreenOnce = false;
    GameScreen gameScreen;
    private int levelInt;
    private boolean isInPreviewMode = false;
    private int interstitial_counter = 0;

    public WorldController(CJMain game, GameScreen gameScreen, int _level) {
        levelInt = _level;
        this.game = game;
        this.gameScreen = gameScreen;
        firstInit();
    }

    private void firstInit() {
        cameraHelper = new CameraHelper();
        cameraHelper.setZoom(ZOOM);
        initLevel(3);
    }

    private void initLevel(int _scoreStars) {
        didFallAlready = false;
        gameScreen.startGame();
        didTouchScreenOnce = false;
        scoreStars = _scoreStars;
        /* testing: always load level 0 */
        // level = new Level(Constants.LEVEL_PATH_NAME + 0 + ".png");
        level = new Level(Constants.LEVEL_PATH_NAME + levelInt + ".png");
        cameraHelper.setTarget(level.crateMain);

        AudioManager.instance.stopLongDurationApplause();

//        if((levelInt + 1) % 2 == 0) {
//            CJMain.adsRequestHandler.loadIntersitial();
//        } else if((levelInt + 1) == 39) {
//            CJMain.adsRequestHandler.loadIntersitial();
//        }

    }

    public void update(float deltaTime) {
        if (isInPreviewMode) {
            gameScreen.worldController.cameraHelper.updateForPreview(deltaTime);
            if (gameScreen.worldController.cameraHelper.position.x > level.goal.position.x) {
                stopPreview();
            }
        } else {
            handleInputGame(deltaTime);

            level.update(deltaTime);
            testCollisions();
            cameraHelper.update(deltaTime);
            if (didStartFalling()) {
                AudioManager.instance.screamFallDown();
            }
            if (isPlayerInWater()) {
                decreaseScoreAndRestart();
            }
        }
    }

    public boolean isPlayerInWater() {
        return level.crateMain.position.y < -6;
    }

    private boolean didFallAlready = false;

    private boolean didStartFalling() {

        if (didFallAlready) return false;

        if (level.crateMain.position.y < 1) {
            didFallAlready = true;
            return true;
        } else {
            return false;
        }
    }

    private void decreaseScoreAndRestart() {
        AudioManager.instance.play(Assets.instance.sounds.liveLost);
        if (scoreStars > 0) {
            scoreStars--;
        }
        initLevel(scoreStars);

        int currentTotalAttempts = GamePreferences.instance.getTotalAttempts();
        currentTotalAttempts++;
        GamePreferences.instance.saveScoresAttempts(currentTotalAttempts);

        /* show an interstitial every INTERSTITIAL_FREQUENCY+1*/
        /*commented out, do not show ads on failed passing the level*/
//        if(levelInt > Constants.INTERSTITIAL_MIN_LEVEL) {
//            interstitial_counter++;
//            if(interstitial_counter > Constants.INTERSTITIAL_FREQUENCY) {
//                interstitial_counter = 0;
//                CJMain.adsRequestHandler.showIntersitial();
//            }
//        }
    }

    private void onCollisionCrateWithObstacle(Obstacle obstcl) {
        gameScreen.stopGame();
        decreaseScoreAndRestart();
    }

    private void testCollisions() {
        r1.set(level.crateMain.position.x, level.crateMain.position.y, level.crateMain.bounds.width, level.crateMain.bounds.height);

        for (Rock rock : level.rocks) {
            r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionCrateMainWithRock(rock);
            // IMPORTANT: must do all collisions for valid edge testing on rocks.
        }

        for (Obstacle obstcl : level.obstacles) {
            r2.set(obstcl.position.x, obstcl.position.y, obstcl.bounds.width, obstcl.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionCrateWithObstacle(obstcl);
        }

        r2.set(level.goal.bounds);
        r2.x += level.goal.position.x;
        r2.y += level.goal.position.y;
        if (r1.overlaps(r2)) onCollisionCrateWithGoal();

    }

    private void onCollisionCrateWithGoal() {
        gameScreen.tableEndLevel.setVisible(true);
        GameScreen.btnMenuRadialNotFinal.setDisabled(true);
        gameScreen.levelState = LevelState.STOPPED;

        GamePreferences.instance.saveCompletedLevel(levelInt, scoreStars);

        if(levelInt > Constants.INTERSTITIAL_MIN_LEVEL) {
            if(levelInt % 2 == 0) {
                CJMain.adsRequestHandler.showIntersitial();
            } else if(levelInt == 39) {
                CJMain.adsRequestHandler.showIntersitial();
            }
        }

//        CJMain.adsRequestHandler.showIntersitial();

        AudioManager.instance.playLongDurationApplause();

        int attempts = GamePreferences.instance.getTotalAttempts() + 1;
        Gdx.app.log(TAG, "level finished, saving maxLevel : " + levelInt + " and attempts: " + attempts);
        GamePreferences.instance.saveScores(levelInt, attempts);

        /* submit scores to Google Play if level 24 or 40 */
        if(game.actionResolver != null) {
            if (game.actionResolver.getSignedInGPGS()) {
                if (levelInt == 24) {
                    Gdx.app.log(TAG, "submitting scores 24: " + attempts);
                    game.actionResolver.submitScoreLevel24(attempts);
                } else if (levelInt == 40) {
                    Gdx.app.log(TAG, "submitting scores 40: " + attempts);
                    game.actionResolver.submitScoreLevel40(attempts);
                }

            } else {
                Gdx.app.log(TAG, "not logged in to Goole Play, will not send high scores");
            }
        }


    }

    private void onCollisionCrateMainWithRock(Rock rock) {
        CrateMain crateMain = level.crateMain;

        float heightDifference = Math.abs(crateMain.position.y - (rock.position.y + rock.bounds.height));
        if (heightDifference > 0.25f) {
            boolean hitLeftEdge = crateMain.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if (hitLeftEdge) {
                crateMain.position.x = rock.position.x + rock.bounds.width;
            } else {
                crateMain.position.x = rock.position.x - crateMain.bounds.width;
                // crateMain.position.x = rock.position.x - (crateMain.bounds.width + 0.20f);
            }
            return;
        }

        switch (crateMain.jumpState) {
            case GROUNDED:
                break;
            case FALLING:
            case JUMP_FALLING:
                crateMain.position.y = rock.position.y + crateMain.bounds.height;
                crateMain.jumpState = com.drollgames.crjump.objects.CrateMain.JUMP_STATE.GROUNDED;
                break;
            case JUMP_RISING:
                crateMain.position.y = rock.position.y + crateMain.bounds.height + crateMain.origin.y;
                break;
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
            backPressed();
        }
        return false;
    }

    /*
     * called in update()
     */
    private void handleInputGame(float deltaTime) {
        if (cameraHelper.hasTarget(level.crateMain)) {

            /* execute auto-forward movement */
            if (didTouchScreenOnce) {
                level.crateMain.velocity.x = level.crateMain.terminalVelocity.x;
            }

            level.crateMain.setJumping(isJumping);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (GameScreen.isSettingBtnPressed) {
            GameScreen.isSettingBtnPressed = false;
            /* simulate click: */
            GameScreen.btnMenuRadialNotFinal.toggle();
            return super.touchDown(screenX, screenY, pointer, button);
        }

        switch (gameScreen.levelState) {
            case STOPPED:
                return super.touchDown(screenX, screenY, pointer, button);
            case RUNNING:
                break;
        }

        if (!didTouchScreenOnce) {
            didTouchScreenOnce = true;
            /* https://github.com/libgdx/libgdx/wiki/Scene2d#actions */
            gameScreen.tableBottomLeft.addAction(Actions.sequence(Actions.fadeOut(BTNS_FADE_IN_OUT_DURATION), run(new Runnable() {

                @Override
                public void run() {
                    gameScreen.tableBottomLeft.setVisible(false);
                }
            })));
        } else {
            isJumping = true;
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isJumping = false;
        return super.touchUp(screenX, screenY, pointer, button);
    }

    public void backPressed() {
        AudioManager.instance.stopLongDurationApplause();
        // switch to menu screen
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        game.setScreen(new LevelsListScreen(game), transition);
    }

    public void startPreview() {
        isInPreviewMode = true;
        gameScreen.btnMenuRadialNotFinal.setDisabled(true);
        gameScreen.tableTopRight.addAction(Actions.sequence(Actions.fadeOut(BTNS_FADE_IN_OUT_DURATION), run(new Runnable() {

            @Override
            public void run() {
                gameScreen.tableTopRight.setVisible(false);
            }
        })));
        gameScreen.tableBottomLeft.addAction(Actions.sequence(Actions.fadeOut(BTNS_FADE_IN_OUT_DURATION), run(new Runnable() {

            @Override
            public void run() {
                gameScreen.tableBottomLeft.setVisible(false);
            }
        })));
    }

    public void stopPreview() {
        isInPreviewMode = false;
        initLevel(scoreStars);
        gameScreen.btnMenuRadialNotFinal.setDisabled(false);
        gameScreen.tableTopRight.addAction(Actions.sequence(Actions.fadeIn(BTNS_FADE_IN_OUT_DURATION), run(new Runnable() {

            @Override
            public void run() {
                gameScreen.tableTopRight.setVisible(true);
            }
        })));
        gameScreen.tableBottomLeft.addAction(Actions.sequence(Actions.fadeIn(BTNS_FADE_IN_OUT_DURATION), run(new Runnable() {

            @Override
            public void run() {
                gameScreen.tableBottomLeft.setVisible(true);
            }
        })));
    }

}
