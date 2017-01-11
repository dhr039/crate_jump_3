package com.drollgames.crjump.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.drollgames.crjump.game.Assets;
import com.drollgames.crjump.screens.GameScreen;
import com.drollgames.crjump.util.AudioManager;
import com.drollgames.crjump.util.Constants;
import com.drollgames.crjump.util.GamePreferences;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

public class RadialSideMenuTop extends WidgetGroup {
    private static final String TAG = "RadialSideMenu";

    public float radius;
    public float arc;
    public float arcStart;
    private Button hudBtn;
    private GameScreen gameScreen;

    public RadialSideMenuTop(Skin skinUi, GameScreen _gameScreen, Button _hudBtn) {
        this.hudBtn = _hudBtn;
        this.gameScreen = _gameScreen;

        setVisible(false);

        addButtons(skinUi);

        arc = 35f;
        arcStart = -3.5f;
        // radius = Display.devicePixel(180);
        radius = 180;
        setRotation(0);
    }

    public void open() {
        setVisible(true);

        gameScreen.stopGame();
        hudBtn.setChecked(true);
        hudBtn.setDisabled(true);

        float angle = arc / getChildren().size;
        Timeline timeline = Timeline.createParallel();
        int index = 0;
        for (Actor child : getChildren()) {
            child.setWidth(Constants.GUI_BUTTON_HEIGTH);
            child.setHeight(Constants.GUI_BUTTON_HEIGTH);
            child.getColor().a = 0;
            child.setX(0);
            child.setY(0);
            float radian = (float) ((arcStart + (angle * index + angle / 2)) * (Math.PI / 180f));
            float newX = (float) (-radius * Math.cos(radian));

            /* expand down: */
            float newY = (float) (-radius * Math.sin(radian)); /* was +radius when expanding up */

            timeline.push(Tween.to(child, WidgetAccessor.POSITION, 0.2f).delay(0.01f * index++).target(newX, newY));
            timeline.push(Tween.to(child, WidgetAccessor.OPACITY, 0.2f).delay(0.01f * index++).target(1f));
            index++;
        }
        timeline.setCallback(new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                hudBtn.setDisabled(false);
            }
        });
        timeline.start(TweenSystem.manager());
    }

    public void close() {
        gameScreen.startGame();
        hudBtn.setChecked(false);
        hudBtn.setDisabled(true);

        Timeline timeline = Timeline.createParallel();
        for (Actor child : getChildren()) {
            timeline.push(Tween.to(child, WidgetAccessor.OPACITY, 0.5f).target(0, 0));
        }
        timeline.setCallback(new TweenCallback() {
            @Override
            public void onEvent(int arg0, BaseTween<?> arg1) {
                setVisible(false);
                hudBtn.setDisabled(false);
            }
        });
        timeline.start(TweenSystem.manager());
    }

    private void addButtons(final Skin _skinUi) {

        final Button btnExit = new Button(_skinUi, Constants.BTN_EXIT);
        final Button btnSoundOffOn = new Button(_skinUi, Constants.BTN_SOUND);
        final Button btnMusicOffOn = new Button(_skinUi, Constants.BTN_MUSIC);

        addActor(btnExit);
        addActor(btnSoundOffOn);
        addActor(btnMusicOffOn);

        final GamePreferences prefs = GamePreferences.instance;
        prefs.load();

        btnSoundOffOn.setChecked(!prefs.sound);
        btnSoundOffOn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.load();
                if (btnSoundOffOn.isChecked()) {
                    prefs.sound = false;
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
                    gameScreen.reloadMusic = false;
                    gameScreen.loadMusicForLevel();
                }
                prefs.save();
                Gdx.app.debug(TAG, "MUSIC: " + GamePreferences.instance.music);
                Gdx.app.debug(TAG, "SOUND: " + GamePreferences.instance.sound);
            }
        });

        btnExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                gameScreen.worldController.backPressed();
            }
        });
    }

}
