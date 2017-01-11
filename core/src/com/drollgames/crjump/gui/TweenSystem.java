package com.drollgames.crjump.gui;

import aurelienribon.tweenengine.TweenManager;

public class TweenSystem {
    private TweenSystem() {

    }

    static TweenManager tweenManager;

    public static TweenManager manager() {
        if (tweenManager == null) {
            tweenManager = new TweenManager();
        }

        return tweenManager;
    }

    public static void setTweenManager(TweenManager tweenManager) {
        TweenSystem.tweenManager = tweenManager;
    }
}
