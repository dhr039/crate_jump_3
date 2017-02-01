package com.drollgames.crjump;

import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.pods.google.mobileads.GADInterstitial;
import org.robovm.pods.google.mobileads.GADRequest;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import java.util.Arrays;

public class IOSLauncher extends IOSApplication.Delegate implements IActivityRequestHandler {

    private static final String ADMOB_INTERSTITIAL_ID = "ca-app-pub-6925635541075684/7343688666";

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
        return new IOSApplication(new CJMain(this, null), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    @Override
    public void showIntersitial() {
//        Foundation.log(TAG + "dhr showIntersitial");
        if(adInterstitial == null) {
            loadIntersitial();
            return;
        }
        if (adInterstitial.isReady()) {
            if(viewController == null) {
                loadIntersitial();
                return;
            }
            adInterstitial.present(viewController);
            loadIntersitial();
        } else {
            this.loadIntersitial();
        }
    }

    @Override
    public void loadIntersitial() {
//        Foundation.log(TAG + "dhr loadIntersitial");
        viewController  = UIApplication.getSharedApplication().getKeyWindow().getRootViewController();
        String interstitialId = ADMOB_INTERSTITIAL_ID;
        adInterstitial = new GADInterstitial(interstitialId);

        GADRequest request = new GADRequest();
        // Display test ads on the simulator.
        request.setTestDevices(Arrays.asList(GADRequest.getSimulatorID()));

        adInterstitial.loadRequest(request);
    }

    @Override
    public void showMoreApps() {

    }

    @Override
    public void loadShareIntent() {

    }
}