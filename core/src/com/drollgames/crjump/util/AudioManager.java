package com.drollgames.crjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.drollgames.crjump.game.Assets;

public class AudioManager {

    private Music playingMusic;

    // singleton: prevent instantiation from other classes
    public static final AudioManager instance = new AudioManager();

    private AudioManager() {}

    public void play(Sound sound) {
        play(sound, 1);
    }

    public void play(Sound sound, float volume) {
        play(sound, volume, 1);
    }

    public void play(Sound sound, float volume, float pitch) {
        play(sound, volume, pitch, 0);
    }

    public void play(Sound sound, float volume, float pitch, float pan) {
        if (!GamePreferences.instance.sound) return;
        sound.play(volume, pitch, pan);
    }

    public void play(Music music) {

        /* stop any previously started music */
        stopMusic();

        playingMusic = music;
        if (GamePreferences.instance.music) {
            playingMusic.setLooping(true);
            try {
                playingMusic.play();
            } catch (Exception e) {
                Gdx.app.debug("", "play music EXCEPTION caught: ");
                e.printStackTrace();
            }

        }
    }

    public void play(String musicLocation) {

        /* stop any previously started music */
        stopMusic();
        playingMusic = Gdx.audio.newMusic(Gdx.files.internal(musicLocation));

        if (GamePreferences.instance.music) {
            playingMusic.setLooping(true);
            try {
                playingMusic.play();
            } catch (Exception e) {
                Gdx.app.debug("", "play music EXCEPTION caught: ");
                e.printStackTrace();
            }
        }
    }


    public void stopMusic() {
        if (playingMusic != null) {
            playingMusic.stop();
            playingMusic = null;
        }
    }

    public void screamFallDown() {
        Music scream = Gdx.audio.newMusic(Gdx.files.internal(Assets.instance.music.scream));
        if (GamePreferences.instance.sound) {
            scream.setLooping(false);
            scream.play();
        }
    }

    private Music applauseLongDuration;

    public void playLongDurationApplause() {
        applauseLongDuration = Gdx.audio.newMusic(Gdx.files.internal(Assets.instance.music.applause));
        if (GamePreferences.instance.sound) {
            applauseLongDuration.setLooping(false);
            applauseLongDuration.play();
        }
    }

    public void stopLongDurationApplause() {
        if (applauseLongDuration != null) {
            applauseLongDuration.stop();
        }
    }

}
