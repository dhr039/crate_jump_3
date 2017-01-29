package com.drollgames.crjump;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.pods.google.mobileads.GADInterstitial;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.drollgames.crjump.CJMain;

public class IOSLauncher extends IOSApplication.Delegate implements IActivityRequestHandler {

    private static final String ADMOB_INTERSTITIAL_ID = "ca-app-pub-6925635541075684/6327090666";

    private static final String TAG = "DHRlogs ";
    UIViewController viewController;
    GADInterstitial adInterstitial;

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = false;
        config.useAccelerometer = false;
        config.useCompass = false;
        return new IOSApplication(new CJMain(null, null), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    public void showIntersitial() {

    }

    @Override
    public void loadIntersitial() {

    }

    @Override
    public void showMoreApps() {

    }

    @Override
    public void loadShareIntent() {

    }
}