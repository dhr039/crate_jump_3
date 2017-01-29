package com.drollgames.crjump;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.drollgames.crjump.game.Assets;
import com.drollgames.crjump.gui.WidgetAccessor;
import com.drollgames.crjump.screens.DirectedGame;
import com.drollgames.crjump.screens.FirstMenuScreen;
import com.drollgames.crjump.util.Constants;
import com.drollgames.crjump.util.GamePreferences;

import aurelienribon.tweenengine.Tween;

/* DirectedGame implements ApplicationListener */
public class CJMain extends DirectedGame {
    private static final String TAG = "CJMain";

    private FirstMenuScreen firstScreen;

    public static IActivityRequestHandler adsRequestHandler = null;
    public ActionResolver actionResolver;


    public CJMain(IActivityRequestHandler handler, ActionResolver actionResolver) {

        this.actionResolver = actionResolver;

        if (handler == null) {
            adsRequestHandler = new IActivityRequestHandler() {
                @Override
                public void showIntersitial() {
                    Gdx.app.log(TAG, "handler was null, showIntersitial() will do nothing");
                }

                @Override
                public void loadIntersitial() {

                }

                @Override
                public void showMoreApps() {
                    Gdx.app.log(TAG, "handler was null, showMoreApps() will do nothing");
                }

                @Override
                public void loadShareIntent() {
                    Gdx.app.log(TAG, "handler was null, loadShareIntent() will do nothing");
                }
            };
        } else {
            adsRequestHandler = handler;
        }
    }

    @Override
    public void create() {

        if (Constants.ENABLE_LOGS) {
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        } else {
            Gdx.app.setLogLevel(Application.LOG_NONE);
        }

        GamePreferences.instance.load();

        Tween.registerAccessor(Actor.class, new WidgetAccessor());

        // Load assets
        Assets.instance.init(new AssetManager());

        firstScreen = new FirstMenuScreen(this);
        setScreen(firstScreen, null);

    }

}
