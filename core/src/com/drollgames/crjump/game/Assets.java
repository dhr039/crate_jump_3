package com.drollgames.crjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.drollgames.crjump.util.Constants;

public class Assets implements Disposable, AssetErrorListener {

    public static final String TAG = Assets.class.getName();

    public static final Assets instance = new Assets();

    private AssetManager assetManager;

    public AssetCrateMain crateMain;
    public AssetRock rock;
    public AssetObstacle obstacle;
    public AssetBackgrnds assetBackgrnds;
    public AssetObstacleNoProblem obstacleNoProblem;
    public AssetLevelDecoration levelDecoration;

    public AssetSounds sounds;
    public AssetMusic music;

    // singleton: prevent instantiation from other classes
    private Assets() {}

    public class AssetObstacle {
        public final AtlasRegion obstacle;

        public AssetObstacle(TextureAtlas atlas) {
            obstacle = atlas.findRegion("alien_obstacle");
        }
    }

    public class AssetBackgrnds {
        public final AtlasRegion bckgrndMetal;
        public final AtlasRegion bckgrndSpace1;

        public Array<Sprite> arrAs1Sprites;
        public Array<Sprite> arrAs2Sprites;
        public Array<Sprite> arrAs3Sprites;

        public AssetBackgrnds(TextureAtlas atlas) {
            bckgrndMetal = atlas.findRegion("metalbkgrnd");
            bckgrndSpace1 = atlas.findRegion("space_one");
            arrAs1Sprites = atlas.createSprites("asone");
            arrAs2Sprites = atlas.createSprites("dthree");
            arrAs3Sprites = atlas.createSprites("bfour");
        }
    }

    public class AssetObstacleNoProblem {
        public final AtlasRegion obstacleNoProblem;

        public AssetObstacleNoProblem(TextureAtlas atlas) {
            obstacleNoProblem = atlas.findRegion("obstacle_no_problem");
        }
    }

    public class AssetCrateMain {
        public final AtlasRegion head;

        public AssetCrateMain(TextureAtlas atlas) {
            head = atlas.findRegion("crate");
        }
    }

    public class AssetRock {
        public final AtlasRegion edge;
        public final AtlasRegion middle;

        public AssetRock(TextureAtlas atlas) {
            edge = atlas.findRegion("rock_edge");
            middle = atlas.findRegion("rock_middle");
        }
    }

    public class AssetLevelDecoration {
        public final AtlasRegion cloud01;
        public final AtlasRegion cloud02;
        public final AtlasRegion cloud03;
        public final AtlasRegion mountainLeft;
        public final AtlasRegion mountainRight;
        public final AtlasRegion scrollingFront;
        public final AtlasRegion scrollingFrontB;
        public final AtlasRegion goal;

        public AssetLevelDecoration(TextureAtlas atlas) {
            cloud01 = atlas.findRegion("cloud01");
            cloud02 = atlas.findRegion("cloud02");
            cloud03 = atlas.findRegion("cloud03");

            mountainLeft = atlas.findRegion("chunk");
            mountainRight = atlas.findRegion("chunk_b");
            scrollingFront = atlas.findRegion("scrolling_front");
            scrollingFrontB = atlas.findRegion("scrolling_front_b");

            goal = atlas.findRegion("goal");
        }
    }
    
    public class AssetMusic {
        public final String applause;
        public final String interstellar_short_1;
        public final String club_march_2;
        public final String cyber_dance_short_3;
        public final String abyss_short_0;
        public final String scream;

        public AssetMusic() {
            applause = "sounds/polite_applause.wav";
            interstellar_short_1 = "music/interstellar_short.wav";
            club_march_2 = "music/club_march_short.wav";
            cyber_dance_short_3 = "music/cyber_dance_short.wav";
            abyss_short_0 = "music/abyss_short.wav";
            scream = "music/scream.wav";

//            applause = am.get("sounds/polite_applause.wav", Music.class);
//            interstellar_short_1 = am.get("music/interstellar_short.wav", Music.class);
//            club_march_2 = am.get("music/club_march_short.wav", Music.class);
//            cyber_dance_short_3 = am.get("music/cyber_dance_short.wav", Music.class);
//            abyss_short_0 = am.get("music/abyss_short.wav", Music.class);
//            scream = am.get("music/scream.ogg", Music.class);
        }
    }

    public class AssetSounds {
        public final Sound jump;
        public final Sound liveLost;
        public final Sound click;

        public AssetSounds(AssetManager am) {
            jump = am.get("sounds/jump_sound_or_power_up.mp3", Sound.class);
            liveLost = am.get("sounds/live_lost.wav", Sound.class);
            click = am.get("sounds/215772__otisjames__click.wav", Sound.class);
        }
    }

    public void init(AssetManager assetManager) {
        this.assetManager = assetManager;
        
        /* set asset manager error handler */
        assetManager.setErrorListener(this);
        
        /* load texture atlas */
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
        
        /* load sounds */
        assetManager.load("sounds/jump_sound_or_power_up.mp3", Sound.class);
        assetManager.load("sounds/215772__otisjames__click.wav", Sound.class);
        assetManager.load("sounds/live_lost.wav", Sound.class);

//        /* load music */
//        assetManager.load("sounds/polite_applause.wav", Music.class);
//        assetManager.load("music/interstellar_short.wav", Music.class);
//        assetManager.load("music/club_march_short.wav", Music.class);
//        assetManager.load("music/cyber_dance_short.wav", Music.class);
//        assetManager.load("music/abyss_short.wav", Music.class);
//        assetManager.load("music/scream.ogg", Music.class);

        assetManager.finishLoading();

        for (String a : assetManager.getAssetNames()) {
            Gdx.app.debug(TAG, "asset: " + a);
        }

        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

        // enable texture filtering for pixel smoothing
        for (Texture t : atlas.getTextures()) {
            t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }

        // create game resource objects
        crateMain = new AssetCrateMain(atlas);
        rock = new AssetRock(atlas);
        levelDecoration = new AssetLevelDecoration(atlas);
        obstacle = new AssetObstacle(atlas);
        assetBackgrnds = new AssetBackgrnds(atlas);
        obstacleNoProblem = new AssetObstacleNoProblem(atlas);
        music = new AssetMusic();
        sounds = new AssetSounds(assetManager);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(TAG, "Couldn't load asset '" + asset + "'", (Exception) throwable);
    }

}
