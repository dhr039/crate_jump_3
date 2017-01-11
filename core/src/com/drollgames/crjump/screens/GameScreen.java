package com.drollgames.crjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.drollgames.crjump.CJMain;
import com.drollgames.crjump.game.Assets;
import com.drollgames.crjump.game.WorldController;
import com.drollgames.crjump.game.WorldRenderer;
import com.drollgames.crjump.gui.RadialSideMenuTop;
import com.drollgames.crjump.gui.TweenSystem;
import com.drollgames.crjump.model.LevelState;
import com.drollgames.crjump.util.AudioManager;
import com.drollgames.crjump.util.Constants;
import com.drollgames.crjump.util.GamePreferences;

public class GameScreen extends AbstractGameScreen {
    private static final String TAG = "GameScreen";
    private static final float BTN_ENDLEVEL_SIZE = 80f;

    public WorldController worldController;
    public WorldRenderer worldRenderer;
    public LevelState levelState;
    public Table tableEndLevel;
    public static Button btnMenuRadialNotFinal;
    public Stage stage;
    private int level;
    public boolean reloadMusic;

    public static boolean isSettingBtnPressed = false;

    private int leaderboardBtnClickedAmount;

    public Table tableTopRight;
    public Table tableBottomLeft;

    public GameScreen(final CJMain game, final int _level, boolean doReload) {
        super(game);
        level = _level;
        startGame();
        btnMenuRadialNotFinal = new Button(skinUi, Constants.BTN_SETTINGS);

        reloadMusic = doReload;

        isSettingBtnPressed = false;

        leaderboardBtnClickedAmount = 0;
    }

    @Override
    public void render(float deltaTime) {
        TweenSystem.manager().update(deltaTime);
        switch (levelState) {
            case RUNNING:
                worldController.update(deltaTime);
                break;
            case STOPPED:
                break;/* Do not update */
            default:
                break;
        }

        worldRenderer.render();
        stage.act(deltaTime);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        worldRenderer.resize(width, height);
    }

    @Override
    public void hide() {
        super.hide();
        stage.dispose();
        worldRenderer.dispose();
    }

    @Override
    public void show() {
        loadMusicForLevel();
        stage = new Stage();
        GamePreferences.instance.load();
        worldController = new WorldController(game, GameScreen.this, level);
        worldRenderer = new WorldRenderer(worldController);
        rebuildHUDStage();
        Gdx.input.setCatchBackKey(true);
    }

    public void loadMusicForLevel() {
        if (!reloadMusic) {
            if (level <= 7) {
                AudioManager.instance.play(Assets.instance.music.interstellar_short_1);
            } else if (level <= 12) {
                AudioManager.instance.play(Assets.instance.music.club_march_2);
            } else if (level <= 18) {
                AudioManager.instance.play(Assets.instance.music.cyber_dance_short_3);
            } else if (level <= 24) {
                AudioManager.instance.play(Assets.instance.music.interstellar_short_1);
            } else if (level <= 32) {
                AudioManager.instance.play(Assets.instance.music.club_march_2);
            } else if (level <= 40) {
                AudioManager.instance.play(Assets.instance.music.cyber_dance_short_3);
            } else {
                AudioManager.instance.play(Assets.instance.music.interstellar_short_1);
            }
        } else {
            Gdx.app.debug(TAG, "will not (re)load music");
        }
    }

    /* A good place to save the game state. */
    @Override
    public void pause() {
        stopGame();
    }

    @Override
    public void resume() {
        super.resume();
        startGame();
    }

    @Override
    public InputProcessor getInputProcessor() {
        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(stage);
        im.addProcessor(worldController);
        return im;
    }

    public void startGame() {
        levelState = LevelState.RUNNING;
    }

    public void stopGame() {
        levelState = LevelState.STOPPED;
    }

    /* -------------------- BUILD UI ----------------------------- */
    private void rebuildHUDStage() {
        Table tableTopLeft = buildTableTopLeft();
        tableTopRight = buildTableTopRigth();
        tableBottomLeft = buildBottomLeft();

        tableEndLevel = buildTableEndLevelCenter();
        /* hide the end level dialog while playing: */
        tableEndLevel.setVisible(false);

        /* tableTop.debug(); */
        stage.clear();
        tableTopLeft.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        tableTopRight.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stage.addActor(tableTopLeft);
        stage.addActor(tableTopRight);
        stage.addActor(tableEndLevel);
        stage.addActor(tableBottomLeft);
        stage.setViewport(new FitViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
    }

    private Table buildTableTopLeft() {
        Table table = new Table();
        table.top().left();
        table.add(new ActorScoreStars()).padTop(20);
        table.row();
        return table;
    }

    private Table buildBottomLeft() {
        final Button btn_share = new Button(skinUi, "btn_eye");
        btn_share.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                worldController.startPreview();
            }

        });

        Table table = new Table();
        table.bottom().left();

        table.add(btn_share).width(Constants.GUI_BUTTON_HEIGTH + 20).height(Constants.GUI_BUTTON_HEIGTH - 10).pad(10);

        return table;
    }



    private Table buildTableTopRigth() {
        Table table = new Table();
        table.top().right();
        LabelStyle labelStyle = skinUi.get("level_indicator", LabelStyle.class);
        String strLevel = myBundle.format("level");
        Label label = new Label(strLevel + " " + level + " ", skinUi);
        label.setStyle(labelStyle);
        label.setFontScale(0.43f);
        table.add(label);


        final RadialSideMenuTop radialSideMenu = new RadialSideMenuTop(skinUi, this, btnMenuRadialNotFinal);
        table.add(btnMenuRadialNotFinal).width(Constants.GUI_BUTTON_SIZE2).height(Constants.GUI_BUTTON_SIZE2);
        btnMenuRadialNotFinal.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                /* sometimes Stackoverflow here */
                try {
                    stage.addActor(radialSideMenu);
                } catch (Exception e) {
                    Gdx.app.error(TAG, "+++++++++++ ERROR +++++++++++");
                    e.printStackTrace();
                }
                radialSideMenu.setX(btnMenuRadialNotFinal.getX() + 20f);
                radialSideMenu.setY(btnMenuRadialNotFinal.getY());
                switch (levelState) {
                    case RUNNING:
                        radialSideMenu.open();
                        isSettingBtnPressed = true;
                        break;
                    case STOPPED:
                        radialSideMenu.close();
                        isSettingBtnPressed = false;
                        break;
                }
            }
        });

        table.row();
        return table;
    }

    private Table buildTableEndLevelCenter() {
        Table tableOuterContainer = new Table();
        tableOuterContainer.top();
        tableOuterContainer.setBackground(skinUi.getDrawable("endlevel_dialog_background"));

        Table tableTextContainer = new Table();

        String strGreat = myBundle.get("great");
        Label labelGreat = new Label(strGreat, skinUi);
        labelGreat.setAlignment(Align.center);

        tableTextContainer.add(labelGreat).width(BTN_ENDLEVEL_SIZE * 4);


        Table tableBtnsContainer = new Table();

        Button btnLevelEndExit = new Button(skinUi, "btn_cancel");
        tableBtnsContainer.add(btnLevelEndExit).width(Constants.END_LEVEL_BTN_SIZE).height(Constants.END_LEVEL_BTN_SIZE);
        btnLevelEndExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                worldController.backPressed();
            }
        });

        Button btnReloadLevel = new Button(skinUi, "btn_restart_level");
        tableBtnsContainer.add(btnReloadLevel).width(Constants.END_LEVEL_BTN_SIZE).height(Constants.END_LEVEL_BTN_SIZE);
        btnReloadLevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                game.setScreen(new GameScreen(game, level, true));
            }
        });

        if (level < 40) {
            Button btnNextLevel = new Button(skinUi, "btn_next_level");
            tableBtnsContainer.add(btnNextLevel).width(Constants.END_LEVEL_BTN_SIZE + 10).height(Constants.END_LEVEL_BTN_SIZE + 10);
            btnNextLevel.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    AudioManager.instance.play(Assets.instance.sounds.click);

                    if (level != 7 && level != 12 && level != 18) {
                        level++;
                        game.setScreen(new GameScreen(game, level, true));
                    } else {
                        level++;
                        game.setScreen(new GameScreen(game, level, false));
                    }
                }
            });
        }

        tableOuterContainer.add(tableTextContainer.padBottom(30));
        tableOuterContainer.row();
        tableOuterContainer.add(tableBtnsContainer.padTop(30));
        tableOuterContainer.center().center();

        tableOuterContainer.setSize(Constants.END_LEVEL_DIALOG_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        float x = (Constants.VIEWPORT_GUI_WIDTH / 2) - (Constants.END_LEVEL_DIALOG_WIDTH / 2);
        float y = 0;
        tableOuterContainer.setX(x);
        tableOuterContainer.setY(y);

        return tableOuterContainer;
    }

    private class ActorScoreStars extends Actor {
        private TextureRegion regionStarFilled;
        private TextureRegion regionStarEmpty;
        private int starWidth = 30;
        private int startHeight = 30;
        private int spaceBetweenStars = 10;


        public ActorScoreStars() {
            regionStarFilled = skinUi.getRegion("star_filled");
            regionStarEmpty = skinUi.getRegion("star_empty");
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

            switch (worldController.scoreStars) {
                case 3:
                    batch.draw(regionStarFilled, getX(), getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    batch.draw(regionStarFilled, getX() + starWidth + spaceBetweenStars, getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    batch.draw(regionStarFilled, getX() + 2 * starWidth + 2 * spaceBetweenStars, getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    break;
                case 2:
                    batch.draw(regionStarFilled, getX(), getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    batch.draw(regionStarFilled, getX() + starWidth + spaceBetweenStars, getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    batch.draw(regionStarEmpty, getX() + 2 * starWidth + 2 * spaceBetweenStars, getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    break;
                case 1:
                    batch.draw(regionStarFilled, getX(), getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    batch.draw(regionStarEmpty, getX() + starWidth + spaceBetweenStars, getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    batch.draw(regionStarEmpty, getX() + 2 * starWidth + 2 * spaceBetweenStars, getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    break;
                default:
                    batch.draw(regionStarEmpty, getX(), getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    batch.draw(regionStarEmpty, getX() + starWidth + spaceBetweenStars, getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    batch.draw(regionStarEmpty, getX() + 2 * starWidth + 2 * spaceBetweenStars, getY() - startHeight, getOriginX(), getOriginY(), starWidth, startHeight, getScaleX(), getScaleY(), getRotation());
                    break;
            }
        }
    }

}
