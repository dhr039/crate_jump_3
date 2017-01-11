package com.drollgames.crjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.drollgames.crjump.CJMain;
import com.drollgames.crjump.game.Assets;
import com.drollgames.crjump.util.Constants;

import java.util.Locale;

public abstract class AbstractGameScreen implements Screen {

    // public DirectedGame game;
    public CJMain game;
    protected Skin skinUi;

    protected I18NBundle myBundle;

    public AbstractGameScreen(CJMain game) {
        this.game = game;
        skinUi = new Skin(Gdx.files.internal(Constants.SKIN_CRATEJUMP_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

        /* load the language bundle */
        FileHandle baseFileHandle = Gdx.files.internal("i18n/strings");
        Locale locale = Locale.getDefault();
        myBundle = I18NBundle.createBundle(baseFileHandle, locale);
    }

    public abstract void render(float deltaTime);

    public abstract void resize(int width, int height);

    public abstract void show();

    public void hide() {
        skinUi.dispose();
    }

    public abstract void pause();

    public abstract InputProcessor getInputProcessor();

    public void resume() {
        Assets.instance.init(new AssetManager());
    }

    public void dispose() {
        Assets.instance.dispose();
    }

}
