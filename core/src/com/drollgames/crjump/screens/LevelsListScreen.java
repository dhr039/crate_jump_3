package com.drollgames.crjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.drollgames.crjump.CJMain;
import com.drollgames.crjump.game.Assets;
import com.drollgames.crjump.model.JsonLevel;
import com.drollgames.crjump.model.JsonLevelList;
import com.drollgames.crjump.transitions.ScreenTransition;
import com.drollgames.crjump.transitions.ScreenTransitionFade;
import com.drollgames.crjump.util.AudioManager;
import com.drollgames.crjump.util.Constants;
import com.drollgames.crjump.util.GamePreferences;

/*
 * http://nexsoftware.net/wp/2013/05/09/libgdx-making-a-paged-level-selection-screen/
 */
public class LevelsListScreen extends AbstractGameScreen {

    private Stage stage;
    private CustomInputAdapter controller;
    private PagedScrollPane scroll;

    public static boolean isComingFromLevelsList = false;

    public LevelsListScreen(CJMain game) {
        super(game);
        controller = new CustomInputAdapter();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        isComingFromLevelsList = true;
        if (FirstMenuScreen.isComingFromFirstMenuScreen) {
            /* let the music play as it was */
            FirstMenuScreen.isComingFromFirstMenuScreen = false;
        } else {
            /* coming from the Game screen, have to load the other type of music */
            AudioManager.instance.play(Assets.instance.music.abyss_short_0);
        }
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
        stage = new Stage();

        Stack stack = new Stack();
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);

        Image image = new Image(Assets.instance.assetBackgrnds.bckgrndMetal);
        stack.add(image);

        stack.add(buildLevelButtonsTable());
        stack.add(buildTableBtnExit());
        stage.addActor(stack);
        stage.setViewport(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        // stage.setViewport(new ScreenViewport());
    }

    @Override
    public void render(float deltaTime) {
        /*
         * Color c = Colors.BACKGROUND_COLOR; Gdx.gl.glClearColor(c.r, c.b, c.g, c.a); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
         */

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public InputProcessor getInputProcessor() {
        InputMultiplexer im = new InputMultiplexer();
        im.addProcessor(stage);
        im.addProcessor(controller);
        return im;
    }

    private Table buildLevelButtonsTable() {
        Table containerTable = new Table();
        containerTable.setFillParent(true);
        scroll = new PagedScrollPane();
        scroll.setSmoothScrolling(true);
        scroll.setFlingTime(1.7f);
        scroll.setPageSpacing(25);

        JsonLevelList jsonLevelsList = GamePreferences.instance.getJsonLevelList();
        double numberOfPages = Math.floor(Constants.NUMBER_OF_LEVELS / 8);
        int levelNumberLabel = 1;
        int totalLevelsAdded = 0;
        int numberOfLatestPassedLevel = 1;

        for (int l = 0; l < numberOfPages; l++) {

            Table levels = new Table().pad(50);
            levels.defaults().pad(20, 40, 20, 40);
            for (int y = 0; y < 2; y++) {
                if (totalLevelsAdded > Constants.NUMBER_OF_LEVELS - 1) {
                    break;
                }
                levels.row();
                for (int x = 0; x < 4; x++) {
                    if (totalLevelsAdded > Constants.NUMBER_OF_LEVELS - 1) {
                        break;
                    }
                    JsonLevel level = jsonLevelsList.levels.get(totalLevelsAdded++);
                    if (level.isUnlocked) {
                        numberOfLatestPassedLevel = level.levelNumber - 1;
                    }
                    levels.add(getLevelTable(levelNumberLabel++, level.isUnlocked, level.stars));
                }
            }
            scroll.addPage(levels);
        }

        // /*--- hardcoded: add 16 new levels ---*/
        // jsonLevelsList = GamePreferences.instance.getJsonLevelList2();
        // totalLevelsAdded = 0;
        // for(int i=0; i<2; ++i) {
        // Table levels = new Table().pad(50);
        // levels.defaults().pad(20, 40, 20, 40);
        // for (int y = 0; y < 2; y++) {
        // levels.row();
        // for (int x = 0; x < 4; x++) {
        // JsonLevel level = jsonLevelsList.levels.get(totalLevelsAdded++);
        //// JsonLevel level = new JsonLevel();
        // if (level.isUnlocked) {
        // numberOfLatestPassedLevel = level.levelNumber - 1;
        // }
        // levels.add(getLevelTable(levelNumberLabel++, level.isUnlocked, level.stars));
        // }
        // }
        // scroll.addPage(levels);
        // }
        // /*----------------------------------------*/

        containerTable.add(scroll).expand().fill();

        /* which page are we displaying? */
        if (numberOfLatestPassedLevel > 7 && numberOfLatestPassedLevel < 16) {
            scrollToPage2();
        } else if (numberOfLatestPassedLevel > 15 && numberOfLatestPassedLevel < 24) {
            scrollToPage3();
        } else if (numberOfLatestPassedLevel > 23 && numberOfLatestPassedLevel < 32) {
            scrollToPage4();
        } else if (numberOfLatestPassedLevel > 31) {
            scrollToLastPage();
        } else {
            /* nothing, remain on the first page */
        }

        return containerTable;
    }

    private void scrollToLastPage() {
        scroll.validate();
        scroll.setScrollPercentX(1);
    }

    private void scrollToPage2() {
        scroll.validate();
        scroll.setScrollPercentX(0.2f);
        scroll.fling(0.3f, 2, 0);
    }

    private void scrollToPage3() {
        scroll.validate();
        scroll.setScrollPercentX(0.4f);
        scroll.fling(0.3f, 2, 0);
    }

    private void scrollToPage4() {
        scroll.validate();
        scroll.setScrollPercentX(0.6f);
        scroll.fling(0.3f, 2, 0);
    }

    private Table buildTableBtnExit() {
        Table table = new Table();
        table.top().left();

        Button btnExit = new Button(skinUi, "btn_cancel");
        btnExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.click);
                goToFirstMenuScreen();
            }
        });

        table.add(btnExit).width(Constants.BTN_EXIT_SIZE).height(Constants.BTN_EXIT_SIZE).pad(15);
        return table;
    }

    /**
     * Creates a button and stars below it to represent the level
     */
    private Table getLevelTable(int levelNumber, boolean isLevelUnlocked, int stars) {
        Table resultTable = new Table();
        TextButton button = new TextButton(Integer.toString(levelNumber), skinUi);
        button.padBottom(20);

        TextButton.TextButtonStyle btnStyle;
        if (isLevelUnlocked) {
            btnStyle = skinUi.get("btn_level_passed", TextButton.TextButtonStyle.class);
            button.addListener(levelClickListener);
        } else {
            btnStyle = skinUi.get("btn_level_not_passed", TextButton.TextButtonStyle.class);
        }
        button.setStyle(btnStyle);

        Table starTable = new Table();
        starTable.defaults().pad(5);
        for (int star = 0; star < 3; star++) {
            if (stars > star) {
                starTable.add(new Image(skinUi.getDrawable("star_filled"))).width(20).height(20);
            } else {
                starTable.add(new Image(skinUi.getDrawable("star_empty"))).width(20).height(20);
            }
        }
        button.setName("" + Integer.toString(levelNumber));
        resultTable.add(button).width(Constants.GUI_BUTTON_HEIGTH).height(Constants.GUI_BUTTON_HEIGTH);
        resultTable.row();
        resultTable.add(starTable).height(30);
        return resultTable;
    }

    private ClickListener levelClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            AudioManager.instance.play(Assets.instance.sounds.click);
            int level = Integer.parseInt(event.getListenerActor().getName());
            ScreenTransition transition = ScreenTransitionFade.init(0.75f);
            game.setScreen(new GameScreen(game, level, false), transition);
        }
    };

    private class CustomInputAdapter extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
                goToFirstMenuScreen();
            }
            return false;
        }
    }

    private void goToFirstMenuScreen() {
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        game.setScreen(new FirstMenuScreen(game), transition);
    }

    @Override
    public void resume() {}

}
