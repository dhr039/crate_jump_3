package com.drollgames.crjump.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.drollgames.crjump.CJMain;
import com.drollgames.crjump.game.Assets;
import com.drollgames.crjump.gui.TweenSystem;
import com.drollgames.crjump.transitions.ScreenTransition;
import com.drollgames.crjump.transitions.ScreenTransitionFade;
import com.drollgames.crjump.util.AudioManager;
import com.drollgames.crjump.util.Constants;
import com.drollgames.crjump.util.GamePreferences;

public class FirstMenuScreen extends AbstractGameScreen {
    private static final String TAG = "FirstMenuScreen";

    private Stage stage;

    public static boolean isComingFromFirstMenuScreen = false;

    public FirstMenuScreen(CJMain game) {
        super(game);

        // /* testing only, unlock all levels*/
        // GamePreferences.instance.setAllLevelsUnlocked();
    }

    @Override
    public void render(float deltaTime) {
        // Color c = Colors.BACKGROUND_COLOR;
        // Gdx.gl.glClearColor(c.r, c.g, c.b, c.a);
        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(deltaTime);

        stage.draw();
        TweenSystem.manager().update(deltaTime);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {

        if (InfoAboutScreen.isComingFromInfoScreen) {
            InfoAboutScreen.isComingFromInfoScreen = false;
        } else {
            if (LevelsListScreen.isComingFromLevelsList) {
                /* nothing */
            } else {
                AudioManager.instance.play(Assets.instance.music.abyss_short_0);
            }
        }

        stage = new Stage();
        stage.setViewport(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        createScreen();
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void hide() {
        super.hide();
        stage.dispose();
    }

    @Override
    public void pause() {}


    private void createScreen() {

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);

        Image image = new Image(Assets.instance.assetBackgrnds.bckgrndMetal);
        stack.add(image);

        stack.add(buildTableCenter());
        stack.add(buildTopRight());
//        stack.add(buildTableBtnExit());
        stack.add(buildBtnMore());
    }

    private Table buildTableCenter() {
        Table table = new Table();

        TextField textField = new TextField("Dense Jump", skinUi);
        table.add(textField).padBottom(5f).width(314f).padTop(100);
        table.row();

        Button btnMenuPlay = new Button(skinUi, "btn_menu_play");
        table.add(btnMenuPlay).width(200).height(200).center();
        btnMenuPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                onPlayClicked();
            }
        });
        // btnMenuPlay.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.6f)));

        return table;
    }

    private Table buildBtnMore() {

        Button btn_more = new Button(skinUi, "btn_more");
        btn_more.setChecked(true);
        btn_more.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CJMain.adsRequestHandler.showMoreApps();
            }

        });

        Table table = new Table();
        table.bottom().right();
        table.add(btn_more).width(Constants.GUI_BUTTON_HEIGTH - 10).height(Constants.GUI_BUTTON_HEIGTH - 10).pad(15);

        return table;
    }

//    private Table buildTableBtnExit() {
//        Table table = new Table();
//        table.top().left();
//
//        Button btnExit = new Button(skinUi, "btn_cancel");
//        btnExit.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                AudioManager.instance.play(Assets.instance.sounds.click);
//                onExitClicked();
//            }
//        });
//
//        table.add(btnExit).width(Constants.BTN_EXIT_SIZE).height(Constants.BTN_EXIT_SIZE).pad(15);
//        return table;
//    }

    private Table buildTopRight() {
        Table table = new Table();
        table.top().right();

        final Button btnSoundOffOn = new Button(skinUi, Constants.BTN_SOUND);
        final Button btnMusicOffOn = new Button(skinUi, Constants.BTN_MUSIC);
        final GamePreferences prefs = GamePreferences.instance;
        prefs.load();

        btnSoundOffOn.setChecked(!prefs.sound);
        btnSoundOffOn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.load();
                if (btnSoundOffOn.isChecked()) {
                    prefs.sound = false;
                    // AudioManager.instance.stopLongDurationApplause();
                } else {
                    prefs.sound = true;
                }
                prefs.save();
                AudioManager.instance.play(Assets.instance.sounds.click);
            }
        });

        btnMusicOffOn.setChecked(!prefs.music);
        btnMusicOffOn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                prefs.load();
                if (btnMusicOffOn.isChecked()) {
                    prefs.music = false;
                    AudioManager.instance.stopMusic();
                } else {
                    prefs.music = true;
                    AudioManager.instance.play(Assets.instance.music.abyss_short_0);
                }
                prefs.save();
            }
        });

        final Button btn_info = new Button(skinUi, "btn_info");
        btn_info.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                ScreenTransition transition = ScreenTransitionFade.init(0.75f);
                FirstMenuScreen.this.game.setScreen(new InfoAboutScreen(FirstMenuScreen.this.game), transition);
            }
        });


        table.add(btnMusicOffOn).width(50).height(50).pad(20);
        table.add(btnSoundOffOn).width(50).height(50).pad(20);
        table.add(btn_info).width(Constants.GUI_BUTTON_SIZE2).height(Constants.GUI_BUTTON_SIZE2);

        return table;
    }

    private void onPlayClicked() {
        AudioManager.instance.play(Assets.instance.sounds.click);
        isComingFromFirstMenuScreen = true;
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        game.setScreen(new LevelsListScreen(game), transition);
    }

    private void onExitClicked() {
        AudioManager.instance.play(Assets.instance.sounds.click);
        AudioManager.instance.stopMusic();
        Gdx.app.exit();
    }

    @Override
    public InputProcessor getInputProcessor() {
        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(stage);
        im.addProcessor(customInputAdapter);
        return im;
    }

    private CustomInputAdapter customInputAdapter = new CustomInputAdapter();

    class CustomInputAdapter extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
                // if (dialogResetLevels.isVisible()) {
                // showDialogResetLevels(false);
                // } else {
                onExitClicked();
                // }
            }
            return false;
        }
    }

    @Override
    public void resume() {

    }

}
