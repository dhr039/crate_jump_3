package com.drollgames.crjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.drollgames.crjump.model.JsonLevel;
import com.drollgames.crjump.model.JsonLevelList;

import java.util.ArrayList;


public class GamePreferences {
    private static final String TAG = "GamePreferences";
    private static final String PREFS_LEVELS = "levels_new";
    private static final String ATTEMPTS = "ATTEMPTS_new";
    private static final String MAX_LEVEL = "LEVEL_new";
    private static final String IS_FIRST_RUN = "IS_FIRST_RUN_new";

    public static final GamePreferences instance = new GamePreferences();

    public boolean sound;
    public boolean music;

    private boolean isFirstRun = true;

    private Preferences prefs;

    // singleton: prevent instantiation from other classes
    private GamePreferences() {
        prefs = Gdx.app.getPreferences(Constants.PREFERENCES);

        isFirstRun = prefs.getBoolean(IS_FIRST_RUN, true);
        if (isFirstRun) {
            prefs.putBoolean(IS_FIRST_RUN, false);
            saveScores(0, 0);
            initLevelsJson();
        }
    }

    public void load() {
        sound = prefs.getBoolean("sound", true);
        music = prefs.getBoolean("music", true);
    }

    public void save() {
        prefs.putBoolean("sound", sound);
        prefs.putBoolean("music", music);
        prefs.flush();
    }

    public void saveScores(int level, int attempts) {
        int previousMaxLevel = prefs.getInteger(MAX_LEVEL, -1);
        if (level > previousMaxLevel) {
            prefs.putInteger(MAX_LEVEL, level);
        }
        prefs.putInteger(ATTEMPTS, attempts);
        prefs.flush();
    }

    public void saveScoresAttempts(int attempts) {
        prefs.putInteger(ATTEMPTS, attempts);
        prefs.flush();
    }

    public int getTotalAttempts() {
        return prefs.getInteger(ATTEMPTS, 0);
    }

    public int getMaxLevel() {
        return prefs.getInteger(MAX_LEVEL, 0);
    }

    /* initialize levels from scratch */
    private JsonLevelList initLevelsJson() {

        /* set total attempts to 0 */
        saveScoresAttempts(0);

        JsonLevelList levelsList = new JsonLevelList();
        ArrayList<JsonLevel> listOfLevels = new ArrayList<JsonLevel>();
        for (int i = 0; i < Constants.NUMBER_OF_LEVELS; ++i) {
            JsonLevel level = new JsonLevel();
            level.levelNumber = i + 1;
            if (i == 0) {
                level.isUnlocked = true;
            } else {
                level.isUnlocked = false;
            }

            level.stars = 0;
            listOfLevels.add(level);
        }
        levelsList.levels = new ArrayList<JsonLevel>(listOfLevels);
        Gdx.app.log("levelsList.levels.size()", "" + levelsList.levels.size());

        Json json = new Json();

        prefs.putString(PREFS_LEVELS, json.toJson(levelsList));
        prefs.flush();

        System.out.println(json.prettyPrint(levelsList));
        return levelsList;
    }

    public JsonLevelList getJsonLevelList() {
        JsonLevelList jsonLevelsList;
        Json json = new Json();
        /*
         * exception is thrown if there is unparseable garbage in strLevels, but, if strLevels is empty fromJson returns null
         */
        try {
            jsonLevelsList = json.fromJson(JsonLevelList.class, prefs.getString(PREFS_LEVELS));
            if (jsonLevelsList == null) {
                Gdx.app.log(TAG, "jsonLevelsList is null, initializing levels from scratch");
                jsonLevelsList = initLevelsJson();
            }
        } catch (Exception e) {
            Gdx.app.log(TAG, "" + e);
            Gdx.app.log(TAG, "exception caught, initializing levels from scratch");
            jsonLevelsList = initLevelsJson();
        }

        return jsonLevelsList;
    }

    public void saveCompletedLevel(int levelNumber, int stars) {
        JsonLevelList jsonLevelsListInitial;
        Json json = new Json();
        try {
            jsonLevelsListInitial = json.fromJson(JsonLevelList.class, prefs.getString(PREFS_LEVELS));
            if (jsonLevelsListInitial == null) {
                Gdx.app.log(TAG, "error: jsonLevelsList is null, returning");
                return;
            }
        } catch (Exception e) {
            Gdx.app.log(TAG, "" + e);
            Gdx.app.log(TAG, "error: exception caught, returning");
            return;
        }

        JsonLevel levelToSave = new JsonLevel();
        levelToSave.isUnlocked = true;
        levelToSave.levelNumber = levelNumber;
        levelToSave.stars = stars;

        JsonLevelList listToSave = new JsonLevelList();

        for (JsonLevel oldLevel : jsonLevelsListInitial.levels) {
            if (oldLevel.levelNumber == levelNumber) {
                listToSave.levels.add(levelToSave);
            } else if (oldLevel.levelNumber - 1 == levelNumber) {
                /* unlock next level: */
                JsonLevel nextUnlockedLevel = new JsonLevel();
                nextUnlockedLevel.isUnlocked = true;
                nextUnlockedLevel.levelNumber = oldLevel.levelNumber;
                nextUnlockedLevel.stars = 0;
                listToSave.levels.add(nextUnlockedLevel);
            } else {
                listToSave.levels.add(oldLevel);
            }
        }

        prefs.putString(PREFS_LEVELS, json.toJson(listToSave));
        prefs.flush();
    }

    /* A testing only function */
    public void setAllLevelsUnlocked() {
        Json json = new Json();
        Gdx.app.log(TAG, "unlocking all levels");
        JsonLevelList jsonLevelsList = json.fromJson(JsonLevelList.class, prefs.getString(PREFS_LEVELS));
        for (JsonLevel l : jsonLevelsList.levels) {
            Gdx.app.log(TAG, l.toString());
            l.isUnlocked = true;
            saveCompletedLevel(l.levelNumber, l.stars);
        }
    }

    public void resetAllLevelsAndScore() {
        initLevelsJson();
        saveScoresAttempts(0);
        prefs.putInteger(MAX_LEVEL, 0);
        prefs.flush();
    }

}
