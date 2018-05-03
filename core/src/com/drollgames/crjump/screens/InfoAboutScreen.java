package com.drollgames.crjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.drollgames.crjump.CJMain;
import com.drollgames.crjump.game.Assets;
import com.drollgames.crjump.transitions.ScreenTransition;
import com.drollgames.crjump.transitions.ScreenTransitionFade;
import com.drollgames.crjump.util.AudioManager;
import com.drollgames.crjump.util.Constants;
import com.drollgames.crjump.util.GamePreferences;


/*
 * 
 * */
public class InfoAboutScreen extends AbstractGameScreen {
    private static final String TAG = "InfoAboutScreen";
    public static boolean isComingFromInfoScreen = false;

    private Stage stage;

    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;
    private PerspectiveCamera cam3d;
    private OrthographicCamera cam2d;
    private int WIDTH, HEIGHT;
    private final float scrollSpeed = 1.0f; // unit per second
    TextureRegion regBckgrndSpace1;
    private Table dialogResetLevels;

    private Table tblTopLeft;
    private Table tblBtmRight;
    private Table tblBtmleft;

    private String text =
            "\n\nDense Jump\n\n\n\n"
            + "https://cannonades.com\n\n\n\n"
            
            + "Programming and design:\n\n\n"
            + "Dumitru Hristov\n\n\n"
            
            + "Special thanks to:\n\n"
            + "Eugen Todorov\n\n"
            + "Veaceslav Grec\n\n"
            + "Eugen Garstea\n\n"

            + "Font:\n\n"
            + "http://www.1001freefonts.com/handmade_typewriter.font\n\n"
            
            + "Music:\n\n"
            + "http://www.playonloop.com/2014-music-loops/interstellar/ \n\n"
            + "http://www.playonloop.com/2014-music-loops/abyss/ \n\n"
            + "http://www.playonloop.com/2013-music-loops/club-march/ \n\n"
            + "http://www.playonloop.com/2010-music-loops/cyber-dance/ \n\n"
            
            + "Sounds:\n\n"
            + "http://www.freesound.org/people/joedeshon/sounds/119023/ \n\n"
            + "http://www.freesound.org/people/TheSubber13/sounds/239900/  \n\n"
            + "http://www.freesound.org/people/Cman634/sounds/198784/  \n\n"
            + "http://www.freesound.org/people/Splashdust/sounds/84327/ \n\n"
            + "http://www.freesound.org/people/OtisJames/sounds/215772/ \n\n"
            
            + "Icons:\n\n"
            + "http://www.flaticon.com/authors/freepik \n\n"
            + "useiconic.com from the Noun Project \n\n"
            
            ;

    public InfoAboutScreen(CJMain game) {
        super(game);
        regBckgrndSpace1 = Assets.instance.assetBackgrnds.bckgrndSpace1;
    }

    @Override
    public void render(float deltaTime) {
        // Gdx.gl.glClearColor(0x29 / 255.0f, 0x31 / 255.0f, 0x31 / 255.0f, 0xff / 255.0f);
        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(cam2d.combined);
        spriteBatch.begin();
        spriteBatch.draw(Assets.instance.assetBackgrnds.bckgrndSpace1, 0, 0);
        spriteBatch.end();

        float dt = Gdx.graphics.getDeltaTime();
        cam3d.translate(0.0f, -dt * scrollSpeed, 0.0f);
        cam3d.update(false);
        spriteBatch.setProjectionMatrix(cam3d.combined);
        spriteBatch.begin();
        bitmapFont.draw(spriteBatch, text, -cam3d.viewportWidth / 2f, -cam3d.viewportHeight, cam3d.viewportWidth, Align.center, true);
        spriteBatch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        WIDTH = width;
        HEIGHT = height;

        // define an ortho camera 10 unit wide with height depending on aspect ratio
        float camWidth = 10.0f;
        float camHeight = camWidth * (float) HEIGHT / (float) WIDTH;

        // define the perspective camera
        cam3d = new PerspectiveCamera(90.0f, camWidth, camHeight);
        cam3d.translate(0.0f, -10.0f, 3.0f);
        cam3d.lookAt(0.0f, 0.0f, 0.0f);
        cam3d.update(true);

        cam2d = new OrthographicCamera();
        cam2d.setToOrtho(false, 800, 480);
        cam2d.update();
    }

    @Override
    public void show() {
        InfoAboutScreen.isComingFromInfoScreen = true;

        stage = new Stage();
        stage.addActor(generateMyStack());
        stage.setViewport(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));

        spriteBatch = new SpriteBatch();
        bitmapFont = new BitmapFont();
        bitmapFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        bitmapFont.setUseIntegerPositions(false);
        bitmapFont.getData().setScale(.05f);
        bitmapFont.setColor(Color.WHITE);

        Gdx.input.setCatchBackKey(true);
    }

    private Stack generateMyStack() {
        Stack stack = new Stack();
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        tblBtmRight = buildBtnExitTable();
        stack.add(tblBtmRight);
        tblTopLeft = buildResetLevelsTable();
        stack.add(tblTopLeft);
        tblBtmleft = buildBottomLeft();
        stack.add(tblBtmleft);
        dialogResetLevels = buildTableDialogResetLevels();
        stack.add(dialogResetLevels);

        showDialogResetLevels(false);

        return stack;
    }

    @Override
    public void hide() {
        super.hide();
        stage.dispose();
    }

    @Override
    public void pause() {}

    private Table buildBtnExitTable() {
        Button btnExit = new Button(skinUi, "btn_cancel");
        btnExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                goToFirstMenuScreen();
            }
        });

        Table table = new Table();
        table.top().left();
        table.add(btnExit).width(Constants.BTN_EXIT_SIZE).height(Constants.BTN_EXIT_SIZE).pad(15);

        return table;
    }

    private Table buildBottomLeft() {

        Table table = new Table();
        table.bottom().left();

        Stack myStack = new Stack();

        final Button btn_leaderboard_40 = new Button(skinUi, "btn_leaderboard_40");
        final Button btn_leaderboard_24 = new Button(skinUi, "btn_leaderboard_24");

        btn_leaderboard_24.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.actionResolver != null) {
                    if (game.actionResolver.getSignedInGPGS()) {
                        game.actionResolver.getLeaderboard24();
                    } else {
                        game.actionResolver.loginGPGS();
                    }
                }
            }
        });

        final GamePreferences prefs = GamePreferences.instance;
        prefs.load();

        btn_leaderboard_40.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.actionResolver != null) {
                    if (game.actionResolver.getSignedInGPGS()) {
                        game.actionResolver.getLeaderboard40();
                    } else {
                        game.actionResolver.loginGPGS();
                    }
                }
            }
        });

        table.add(btn_leaderboard_24).width(Constants.GUI_BUTTON_SIZE2).height(Constants.GUI_BUTTON_SIZE2);
        table.row();
        table.add(btn_leaderboard_40).width(Constants.GUI_BUTTON_SIZE2).height(Constants.GUI_BUTTON_SIZE2);
        table.add(myStack).width(Constants.GUI_BUTTON_HEIGTH - 10).height(Constants.GUI_BUTTON_HEIGTH - 10).pad(15);

        return table;
    }

    private void goToFirstMenuScreen() {
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        game.setScreen(new FirstMenuScreen(game), transition);
    }

    @Override
    public void resume() {}


    Label lblLevelValue;
    Label lblAttemptsValue;
    private Table buildResetLevelsTable() {
        Table table = new Table();
        table.top().right();

        final Button btn_reload_levels = new Button(skinUi, Constants.BTN_RELOAD_LEVELS);
        btn_reload_levels.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showDialogResetLevels(true);
            }

        });
        table.add(btn_reload_levels).width(Constants.GUI_BUTTON_HEIGTH - 10).height(Constants.GUI_BUTTON_HEIGTH - 10).pad(10);

        Label.LabelStyle labelStyle = skinUi.get("smaller_label", Label.LabelStyle.class);
        Label.LabelStyle labelStyleGreen = skinUi.get("smaller_label_green", Label.LabelStyle.class);

        String strLevelTitle = myBundle.format("level");
        String strLevel = strLevelTitle + ": ";
        Label lblLevel = new Label(strLevel, skinUi);
        lblLevel.setStyle(labelStyleGreen);
        lblLevel.pack();
        table.add(lblLevel).width(lblLevel.getWidth()).padLeft(20);
        lblLevelValue = new Label(Integer.toString(GamePreferences.instance.getMaxLevel()), skinUi);
        lblLevelValue.pack();
        lblLevelValue.setStyle(labelStyle);
        table.add(lblLevelValue).width(lblLevelValue.getWidth()).padLeft(5);

        String strAttemptsTitle = myBundle.format("attempts");
        String strAttempts = strAttemptsTitle + ": ";
        Label lblAttempts = new Label(strAttempts, skinUi);

        lblAttempts.setStyle(labelStyleGreen);
        lblAttempts.pack();
        table.add(lblAttempts).width(lblAttempts.getWidth()).padLeft(20);
        lblAttemptsValue = new Label(Integer.toString(GamePreferences.instance.getTotalAttempts()), skinUi);
        lblAttemptsValue.pack();
        lblAttemptsValue.setStyle(labelStyle);
        table.add(lblAttemptsValue).width(lblLevelValue.getWidth()).padLeft(5).padRight(80);

        table.row();

        return table;
    }

    private void showDialogResetLevels(boolean doShow) {
        if (doShow) {
            dialogResetLevels.setVisible(true);
            tblTopLeft.setVisible(false);
            tblBtmRight.setVisible(false);
            tblBtmleft.setVisible(false);
        } else {
            dialogResetLevels.setVisible(false);
            tblTopLeft.setVisible(true);
            tblBtmRight.setVisible(true);
            tblBtmleft.setVisible(true);
        }
    }

    private static final float BTN_ENDLEVEL_SIZE = 80f;

    private Table buildTableDialogResetLevels() {
        Table tableOuterContainer = new Table();
        tableOuterContainer.top();
        tableOuterContainer.setBackground(skinUi.getDrawable("endlevel_dialog_background"));


        Table tableTextContainer = new Table();

        String strGreat = myBundle.get("reset_everything");
        Label labelGreat = new Label(strGreat, skinUi);
        labelGreat.setAlignment(Align.center);

        tableTextContainer.add(labelGreat).width(BTN_ENDLEVEL_SIZE * 4);


        Table tableBtnsContainer = new Table();

        Button btnLevelEndExit = new Button(skinUi, "btn_cancel");
        tableBtnsContainer.add(btnLevelEndExit).width(BTN_ENDLEVEL_SIZE).height(BTN_ENDLEVEL_SIZE).padRight(40f);
        btnLevelEndExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                showDialogResetLevels(false);
            }
        });

        Button btnNextLevel = new Button(skinUi, "bnt_ok");
        tableBtnsContainer.add(btnNextLevel).width(BTN_ENDLEVEL_SIZE + 10).height(BTN_ENDLEVEL_SIZE).padLeft(40f);
        btnNextLevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                GamePreferences.instance.resetAllLevelsAndScore();

                lblLevelValue.setText("0");
                lblAttemptsValue.setText("0");

                // createScreen();
                showDialogResetLevels(false);
            }
        });

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


    private CustomInputAdapter customInputAdapter = new CustomInputAdapter();
    private class CustomInputAdapter extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
                goToFirstMenuScreen();
            }
            return false;
        }
    }
    @Override
    public InputProcessor getInputProcessor() {
        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(stage);
        im.addProcessor(customInputAdapter);
        return im;
    }

}